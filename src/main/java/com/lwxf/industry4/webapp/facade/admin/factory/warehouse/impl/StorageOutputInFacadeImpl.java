package com.lwxf.industry4.webapp.facade.admin.factory.warehouse.impl;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.product.ProductService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StockService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageOutputInItemService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageOutputInService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.storage.StorageOutputInStatus;
import com.lwxf.industry4.webapp.common.enums.storage.StorageOutputInType;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.warehouse.StorageOutputInDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.StorageOutputInItemDto;
import com.lwxf.industry4.webapp.domain.entity.warehouse.Stock;
import com.lwxf.industry4.webapp.domain.entity.warehouse.StorageOutputIn;
import com.lwxf.industry4.webapp.domain.entity.warehouse.StorageOutputInItem;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.warehouse.StorageOutputInFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/20/020 10:26
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("storageOutputInFacade")
public class StorageOutputInFacadeImpl extends BaseFacadeImpl implements StorageOutputInFacade {
	@Resource(name = "storageOutputInService")
	private StorageOutputInService storageOutputInService;
	@Resource(name = "storageService")
	private StorageService storageService;
	@Resource(name = "productService")
	private ProductService productService;
	@Resource(name = "stockService")
	private StockService stockService;
	@Resource(name = "companyService")
	private CompanyService companyService;
	@Resource(name = "storageOutputInItemService")
	private StorageOutputInItemService storageOutputInItemService;
	@Override
	public RequestResult findStorageOutPutInList(MapContext mapContext,Integer pageNum,Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		paginatedFilter.setFilters(mapContext);
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		List<Map<String,String>> sorts = new ArrayList<Map<String,String>>();
		Map<String,String> created = new HashMap<String,String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sorts.add(created);
		paginatedFilter.setSorts(sorts);
		return ResultFactory.generateRequestResult(this.storageOutputInService.findListByPaginateFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addStorageInput(StorageOutputInDto storageOutputInDto) {
		//判断编号是否重复
		if(this.storageOutputInService.findOneByNo(storageOutputInDto.getNo())!=null){
			Map result = new HashMap<String,String>();
			result.put(WebConstant.STRING_NO,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,result);
		}
		//判断仓库是否存在
		if(!this.storageService.isExist(storageOutputInDto.getStorageId())){
			return ResultFactory.generateResNotFoundResult();
		}
		this.storageOutputInService.add(storageOutputInDto);
		//判断产品列表
		for(StorageOutputInItemDto storageOutputInItemDto :storageOutputInDto.getStorageOutputInItemList()){
			//判断产品是否存在
			if(!this.productService.isExist(storageOutputInItemDto.getProductId())){
				return ResultFactory.generateResNotFoundResult();
			}
			storageOutputInItemDto.setOutputInId(storageOutputInDto.getId());
			this.storageOutputInItemService.add(storageOutputInItemDto);
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateStorageOutputInStatus(String id) {
		StorageOutputIn output = this.storageOutputInService.findById(id);
		//判断该入库单是否存在
		if(output==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断订单状态是否是未确定
		if(output.getStatus().equals(StorageOutputInStatus.CONFIRM.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext mapContext = MapContext.newOne();
		mapContext.put("id",id);
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		paginatedFilter.setFilters(mapContext);
		PaginatedList<StorageOutputInDto> paginateFilter = this.storageOutputInService.findListByPaginateFilter(paginatedFilter);
		StorageOutputInDto storageOutputInDto = paginateFilter.getRows().get(0);
		//判断出入库单是入库还是出库
		if(output.getType().equals(StorageOutputInType.OUT_STOCK.getValue())){//出库
			//出库单下的商品库存修改
			for(StorageOutputInItemDto storageOutputInItemDto :storageOutputInDto.getStorageOutputInItemList()){
				//仓库下产品出库
				Stock stock = this.stockService.findOneByStorageIdAndProductId(storageOutputInDto.getStorageId(), storageOutputInItemDto.getProductId());
				//判断当库存为0  并且产品预出库量等于 本次出库的出库量 则删除仓库下的该条数据
				if(stock.getQuantity()==0||stock.getPreOutput()-storageOutputInItemDto.getQuantity()==0){
					this.stockService.deleteById(stock.getId());
				}else{
					MapContext updateStock = new MapContext();
					updateStock.put(WebConstant.KEY_ENTITY_ID,stock.getId());
					updateStock.put("preOutput",stock.getPreOutput()-storageOutputInItemDto.getQuantity());
					this.stockService.updateByMapContext(updateStock);
				}
			}
		}else{//入库
			//出入单下的商品库存录入
			for(StorageOutputInItemDto storageOutputInItemDto :storageOutputInDto.getStorageOutputInItemList()){
				//判断仓库下是否已存在该产品
				Stock stock = this.stockService.findOneByStorageIdAndProductId(storageOutputInDto.getStorageId(), storageOutputInItemDto.getProductId());
				if(stock!=null){
					//判断库存中存的位置和出入库单条目中的位置是否一致
					if(!stock.getTier().equals(storageOutputInItemDto.getTier())||!stock.getShelf().equals(storageOutputInItemDto.getShelf())||!stock.getColumn().equals(storageOutputInItemDto.getColumn())){
						return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
					}
					//设定价格
					storageOutputInItemDto.setPrice((stock.getPrice().multiply(new BigDecimal(stock.getQuantity())))
							.add(storageOutputInItemDto.getPrice().multiply(new BigDecimal(storageOutputInItemDto.getQuantity())))
							.divide((new BigDecimal(stock.getQuantity())).add(new BigDecimal(storageOutputInItemDto.getQuantity())),2,BigDecimal.ROUND_UP));

					//修改相对应仓库下的产品数量
					MapContext update = MapContext.newOne();
					update.put(WebConstant.KEY_ENTITY_ID,stock.getId());
					update.put("quantity",stock.getQuantity()+storageOutputInItemDto.getQuantity());
					update.put("price",storageOutputInItemDto.getPrice());
					this.stockService.updateByMapContext(update);
				}else{
					//仓库下生成产品库存
					stock = new Stock();
					stock.setPrice(storageOutputInItemDto.getPrice());
					stock.setStorageId(storageOutputInDto.getStorageId());
					stock.setOperator(WebUtils.getCurrUserId());
					stock.setOperateTime(DateUtil.getSystemDate());
					stock.setColumn(storageOutputInItemDto.getColumn());
					stock.setShelf(storageOutputInItemDto.getShelf());
					stock.setTier(storageOutputInItemDto.getTier());
					stock.setProductId(storageOutputInItemDto.getProductId());
					stock.setPreOutput(0);
					stock.setQuantity(storageOutputInItemDto.getQuantity());
					this.stockService.add(stock);
				}
			}
		}
		//修改订单状态为已确定
		mapContext.put(WebConstant.KEY_ENTITY_STATUS,StorageOutputInStatus.CONFIRM.getValue());
		this.storageOutputInService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(StorageOutputInStatus.CONFIRM.getValue());
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addStorageOutput(StorageOutputInDto storageOutputInDto) {
		//判断编号是否重复
		if(this.storageOutputInService.findOneByNo(storageOutputInDto.getNo())!=null){
			Map result = new HashMap<String,String>();
			result.put(WebConstant.STRING_NO,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,result);
		}
		//判断仓库是否存在
		if(!this.storageService.isExist(storageOutputInDto.getStorageId())){
			return ResultFactory.generateResNotFoundResult();
		}
		this.storageOutputInService.add(storageOutputInDto);
		//判断产品列表
		for(StorageOutputInItemDto storageOutputInItemDto :storageOutputInDto.getStorageOutputInItemList()){
			//判断库存是否存在
			Stock stock = this.stockService.findOneByStorageIdAndProductId(storageOutputInDto.getStorageId(), storageOutputInItemDto.getProductId());
			if(stock==null){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_LACK_OF_STOCK_10063,LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_LACK_OF_STOCK_10063"),0));
			}else if(stock.getQuantity()<storageOutputInItemDto.getQuantity()){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_LACK_OF_STOCK_10063,LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_LACK_OF_STOCK_10063"),stock.getQuantity()));
			}
			storageOutputInItemDto.setOutputInId(storageOutputInDto.getId());
			//修改库存表预出库及库存数据
			MapContext mapContext = MapContext.newOne();
			mapContext.put(WebConstant.KEY_ENTITY_ID,stock.getId());
			mapContext.put("quantity",stock.getQuantity()-storageOutputInItemDto.getQuantity());
			mapContext.put("preOutput",stock.getPreOutput()+storageOutputInItemDto.getQuantity());
			this.stockService.updateByMapContext(mapContext);
			storageOutputInItemDto.setColumn(stock.getColumn());
			storageOutputInItemDto.setShelf(stock.getShelf());
			storageOutputInItemDto.setTier(stock.getTier());
			this.storageOutputInItemService.add(storageOutputInItemDto);
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateStorageOutputItem(String id, String itemId, MapContext mapContext) {
		//判断出入库条目单是否存在
		StorageOutputInItem outputInItem = this.storageOutputInItemService.findById(itemId);
		if(outputInItem==null||!outputInItem.getOutputInId().equals(id)){
			return ResultFactory.generateResNotFoundResult();
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID,itemId);
		this.storageOutputInItemService.updateByMapContext(mapContext);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult cancelStorageOutput(String id) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		paginatedFilter.setFilters(mapContext);
		List<StorageOutputInDto> outputInDtos = this.storageOutputInService.findListByPaginateFilter(paginatedFilter).getRows();
		if(outputInDtos==null||outputInDtos.size()==0||outputInDtos.get(0).getStatus().equals(StorageOutputInStatus.CANCEL.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext updateOutputIn = new MapContext();
		updateOutputIn.put(WebConstant.KEY_ENTITY_ID,id);
		updateOutputIn.put(WebConstant.KEY_ENTITY_STATUS,StorageOutputInStatus.CANCEL.getValue());
		this.storageOutputInService.updateByMapContext(updateOutputIn);
		StorageOutputInDto storageOutputInDto = outputInDtos.get(0);
		if(storageOutputInDto.getType().equals(StorageOutputInType.OUT_STOCK.getValue())){//出库
			for(StorageOutputInItemDto storageOutputInItemDto :storageOutputInDto.getStorageOutputInItemList()){
				MapContext updateStock = new MapContext();
				Stock stock = this.stockService.findOneByStorageIdAndProductId(storageOutputInDto.getStorageId(), storageOutputInItemDto.getProductId());
				updateStock.put(WebConstant.KEY_ENTITY_ID,stock.getId());
				updateStock.put("preOutput",stock.getPreOutput()-storageOutputInItemDto.getQuantity());
				updateStock.put("quantity",stock.getQuantity()+storageOutputInItemDto.getQuantity());
				this.stockService.updateByMapContext(updateStock);
			}
		}
		return ResultFactory.generateRequestResult(StorageOutputInStatus.CANCEL.getValue());
	}

}
