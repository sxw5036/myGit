package com.lwxf.industry4.webapp.facade.admin.factory.procurement.impl;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.procurement.PurchaseProductService;
import com.lwxf.industry4.webapp.bizservice.procurement.PurchaseRequestService;
import com.lwxf.industry4.webapp.bizservice.procurement.PurchaseService;
import com.lwxf.industry4.webapp.bizservice.product.ProductService;
import com.lwxf.industry4.webapp.bizservice.supplier.SupplierProductService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StockService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageOutputInItemService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageOutputInService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.company.CompanyStatus;
import com.lwxf.industry4.webapp.common.enums.procurment.PurchaseProductStatus;
import com.lwxf.industry4.webapp.common.enums.procurment.PurchaseStatus;
import com.lwxf.industry4.webapp.common.enums.user.UserState;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.procurement.PurchaseDto;
import com.lwxf.industry4.webapp.domain.dto.procurement.PurchaseProductDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.StorageOutputInDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.StorageOutputInItemDto;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.procurement.Purchase;
import com.lwxf.industry4.webapp.domain.entity.procurement.PurchaseProduct;
import com.lwxf.industry4.webapp.domain.entity.product.Product;
import com.lwxf.industry4.webapp.domain.entity.supplier.SupplierProduct;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.procurement.PurchaseRequest;
import com.lwxf.industry4.webapp.domain.entity.warehouse.Stock;
import com.lwxf.industry4.webapp.domain.entity.warehouse.Storage;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.procurement.PurchaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/17/017 11:16
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("purchaseFacade")
public class PurchaseFacadeImpl extends BaseFacadeImpl implements PurchaseFacade {
	@Resource(name = "purchaseRequestService")
	private PurchaseRequestService purchaseRequestService;
	@Resource(name = "storageService")
	private StorageService storageService;
	@Resource(name = "productService")
	private ProductService productService;
	@Resource(name = "purchaseService")
	private PurchaseService purchaseService;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "companyService")
	private CompanyService companyService;
	@Resource(name = "purchaseProductService")
	private PurchaseProductService purchaseProductService;
	@Resource(name = "supplierProductService")
	private SupplierProductService supplierProductService;
	@Resource(name = "stockService")
	private StockService stockService;
	@Resource(name = "storageOutputInService")
	private StorageOutputInService storageOutputInService;
	@Resource(name = "storageOutputInItemService")
	private StorageOutputInItemService storageOutputInItemService;

	@Override
	public RequestResult findRequestList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		//企业id
		String branchId=WebUtils.getCurrBranchId();
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID,branchId);
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		paginatedFilter.setFilters(mapContext);
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		List sort = new ArrayList();
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.purchaseRequestService.selectByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addRequest(PurchaseRequest purchaseRequest) {
		//判断仓库是否存在
		Storage storage = this.storageService.findById(purchaseRequest.getStorageId());
		if(storage==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断产品是否存在 是否符合仓库分类
		Product product = this.productService.findById(purchaseRequest.getProductId());
		if(product==null||!product.getProductCategoryId().equals(storage.getProductCategoryId())){
			return ResultFactory.generateResNotFoundResult();
		}
		this.purchaseRequestService.add(purchaseRequest);
		return ResultFactory.generateRequestResult(this.purchaseRequestService.findOneById(purchaseRequest.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateStatus(String id, Integer status) {
		//判断采购申请单是否存在
		if(!this.purchaseRequestService.isExist(id)){
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS,status);
		this.purchaseRequestService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.purchaseRequestService.findOneById(id));
	}

	@Override
	public RequestResult findPurchaseList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		//企业id
		String branchId=WebUtils.getCurrBranchId();
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID,branchId);
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		paginatedFilter.setFilters(mapContext);
		List<Map<String,String>> sorts = new ArrayList<Map<String, String>>();
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sorts.add(created);
		paginatedFilter.setSorts(sorts);
		return ResultFactory.generateRequestResult(this.purchaseService.selectByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updatePurchase(String id, MapContext mapContext) {
		Boolean paid = mapContext.getTypedValue("paid", Boolean.class);
		if(paid==null){
			//如果采购单状态时未审核 则允许修改
			Purchase purchase = this.purchaseService.findById(id);
			if(!purchase.getStatus().equals(PurchaseStatus.PENDING_APPROVAL.getValue())){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
			}
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		this.purchaseService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.purchaseService.findOneById(id));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addPurchase(PurchaseDto purchaseDto) {
		//判断仓库是否存在
		if (!this.storageService.isExist(purchaseDto.getStorageId())){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断采购人是否存在 状态是否正常
		User user = this.userService.findById(purchaseDto.getBuyer());
		if(user==null||user.getState().equals(UserState.DISABLED.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断供应商是否存在 状态是否正常
		Company company = this.companyService.findById(purchaseDto.getSupplierId());
		if(company==null||!company.getStatus().equals(CompanyStatus.NORMAL.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		//添加采购单
		this.purchaseService.add(purchaseDto);
		//添加采购单下的产品表
		List<PurchaseProductDto> purchaseProductDtoList = purchaseDto.getPurchaseProductDtoList();
		for(PurchaseProductDto purchaseProductDto:purchaseProductDtoList){
			SupplierProduct supplierProduct = this.supplierProductService.findById(purchaseProductDto.getId());
			//判断采购单下产品的供应商是否是采购单中的供应商
			if(supplierProduct==null||!supplierProduct.getSupplierId().equals(purchaseDto.getSupplierId())){
				return ResultFactory.generateResNotFoundResult();
			}
			purchaseProductDto.setStatus(0);
			purchaseProductDto.setPurchaseId(purchaseDto.getId());
			purchaseProductDto.setSupplierProductId(purchaseProductDto.getId());
			this.purchaseProductService.add(purchaseProductDto);
		}
		return ResultFactory.generateRequestResult(this.purchaseService.findOneById(purchaseDto.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updatePurchaseStatus(String id, Integer status) {
		//判断采购单是否存在
		Purchase purchase = this.purchaseService.findById(id);
		if(purchase==null){
			return ResultFactory.generateResNotFoundResult();
		}
//		switch (status){
//			case 4:;
//			case 5:;
//			case 6:
//				List<PurchaseProduct> purchaseProducts = this.purchaseProductService.findListByStatusAndPurchaseId(Arrays.asList(0), id);
//				if(purchaseProducts.size()!=0){
//					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
//				}
//				break;
//		}
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS,status);
		this.purchaseService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.purchaseService.findOneById(id));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addPurcahseProduct(String id, List<PurchaseProduct> purchaseProductList) {
		//判断采购单是否存在 以及状态是否为待审批
		Purchase purchase = this.purchaseService.findById(id);
		if(purchase==null||!purchase.getStatus().equals(PurchaseStatus.PENDING_APPROVAL.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		BigDecimal totalMoney=purchase.getTotalMoney();
		for(PurchaseProduct purchaseProduct:purchaseProductList){
			//判断 供应商产品是否存在 供应商 状态是否正常
			SupplierProduct supplierProduct = this.supplierProductService.findById(purchaseProduct.getSupplierProductId());
			if(supplierProduct==null||!this.companyService.findById(supplierProduct.getSupplierId()).getStatus().equals(CompanyStatus.NORMAL.getValue())){
				return ResultFactory.generateResNotFoundResult();
			}
			purchaseProduct.setStatus(0);
			//新增采购产品单
			this.purchaseProductService.add(purchaseProduct);
			totalMoney.add(purchaseProduct.getPrice().multiply(new BigDecimal(purchaseProduct.getQuantity())));
		}
		//修改采购单金额
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		mapContext.put("totalMoney",totalMoney);
		this.purchaseService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.purchaseService.findOneById(id));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updatePurchaseProduct(String id, MapContext mapContext,String purchaseProductId) {
		//判断采购单是否存在
		Purchase purchase = this.purchaseService.findById(id);
		if(purchase==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断状态是否是待审批
		if(!purchase.getStatus().equals(PurchaseStatus.PENDING_APPROVAL.getValue())){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_AUDITED_NOT_OPERATE_10083,AppBeanInjector.i18nUtil.getMessage("BIZ_AUDITED_NOT_OPERATE_10083"));
		}
		//判断产品单是否存在
		PurchaseProduct purchaseProduct = this.purchaseProductService.findById(purchaseProductId);
		if(purchaseProduct==null){
			return ResultFactory.generateResNotFoundResult();
		}
		BigDecimal price = mapContext.getTypedValue("price", BigDecimal.class);
		Integer quantity = mapContext.getTypedValue("quantity", Integer.class);
		MapContext updatePurchase = new MapContext();
		updatePurchase.put(WebConstant.KEY_ENTITY_ID,id);
		updatePurchase.put("totalMoney",purchase.getTotalMoney().subtract(purchaseProduct.getPrice().multiply(new BigDecimal(purchaseProduct.getQuantity()))).add(price.multiply(new BigDecimal(quantity))));
		this.purchaseService.updateByMapContext(updatePurchase);
		mapContext.put(WebConstant.KEY_ENTITY_ID,Arrays.asList(purchaseProductId));
		this.purchaseProductService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.purchaseService.findOneById(id));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deletePurchaseProduct(String id, String purchaseProductId) {
		//判断采购单是否存在
		Purchase purchase = this.purchaseService.findById(id);
		if(purchase==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断状态是否是待审批
		if(!purchase.getStatus().equals(PurchaseStatus.PENDING_APPROVAL.getValue())){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_AUDITED_NOT_OPERATE_10083,AppBeanInjector.i18nUtil.getMessage("BIZ_AUDITED_NOT_OPERATE_10083"));
		}
		//判断产品单是否存在
		PurchaseProduct purchaseProduct = this.purchaseProductService.findById(purchaseProductId);
		if(purchaseProduct==null){
			return ResultFactory.generateResNotFoundResult();
		}
		this.purchaseProductService.deleteById(purchaseProductId);
		MapContext updatePurchase = new MapContext();
		updatePurchase.put(WebConstant.KEY_ENTITY_ID,id);
		updatePurchase.put("totalMoney",purchase.getTotalMoney().subtract(purchaseProduct.getPrice().multiply(new BigDecimal(purchaseProduct.getQuantity()))));
		this.purchaseService.updateByMapContext(updatePurchase);
		return ResultFactory.generateRequestResult(this.purchaseService.findOneById(id));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updatePurcahseProductStatus(String id, String purchaseProductId, Integer status) {
		//判断采购单是否存在
		Purchase purchase = this.purchaseService.findById(id);
		if(purchase==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断产品单是否存在
		PurchaseProduct purchaseProduct = this.purchaseProductService.findById(purchaseProductId);
		if(purchaseProduct==null){
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID, Arrays.asList(purchaseProductId));
		mapContext.put(WebConstant.KEY_ENTITY_STATUS,status);
		this.purchaseProductService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.purchaseService.findOneById(id));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addStorageInput(String id, StorageOutputInDto storageOutputInDto) {
		//判断采购单是否为null 以及状态是否为审核通过
		Purchase purchase = this.purchaseService.findById(id);
		if(purchase==null){
			return ResultFactory.generateResNotFoundResult();
		}
		if(!purchase.getStatus().equals(PurchaseStatus.QUALITY_INSPECTION_PASS.getValue())&&!purchase.getStatus().equals(PurchaseStatus.PARTIAL_QUALIFICATION.getValue())&&!purchase.getStatus().equals(PurchaseStatus.PARTIAL_WAREHOUSING.getValue())){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		//新增入库单
		this.storageOutputInService.add(storageOutputInDto);
		for(StorageOutputInItemDto storageOutputInItemDto: storageOutputInDto.getStorageOutputInItemList()){
			storageOutputInItemDto.setOutputInId(storageOutputInDto.getId());
			//由于供应商产品不再关联product表，这里需要修改成productid
			//storageOutputInItemDto.setProductId(this.supplierProductService.findById(storageOutputInItemDto.getProductId()).getId());
			RequestResult result = storageOutputInItemDto.validateFields();
			if(result!=null){
				return result;
			}
			this.storageOutputInItemService.add(storageOutputInItemDto);
			//库存表数据修改
			Stock stock = this.stockService.findOneByStorageIdAndProductId(purchase.getStorageId(), storageOutputInItemDto.getProductId());
			if(stock==null){
				stock = new Stock();
				stock.setStorageId(purchase.getStorageId());
				stock.setPrice(storageOutputInItemDto.getPrice());
				stock.setOperator(WebUtils.getCurrUserId());
				stock.setOperateTime(DateUtil.getSystemDate());
				stock.setPreOutput(0);
				stock.setQuantity(storageOutputInItemDto.getQuantity());
				stock.setProductId(storageOutputInItemDto.getProductId());
				stock.setTier(storageOutputInItemDto.getTier());
				stock.setShelf(storageOutputInItemDto.getShelf());
				stock.setColumn(storageOutputInItemDto.getColumn());
				this.stockService.add(stock);
			}else{
				if(!stock.getColumn().equals(storageOutputInItemDto.getColumn())||!stock.getShelf().equals(storageOutputInItemDto.getShelf())||!stock.getTier().equals(storageOutputInItemDto.getTier())){
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_INVENTORY_LOCATION_ERROR_10084,AppBeanInjector.i18nUtil.getMessage("BIZ_INVENTORY_LOCATION_ERROR_10084"));
				}
				MapContext updateStock = new MapContext();
				updateStock.put(WebConstant.KEY_ENTITY_ID,stock.getId());
				updateStock.put("quantity",storageOutputInItemDto.getQuantity()+stock.getQuantity());
				updateStock.put("price",(stock.getPrice().multiply(new BigDecimal(stock.getQuantity())))
						.add(storageOutputInItemDto.getPrice().multiply(new BigDecimal(storageOutputInItemDto.getQuantity())))
						.divide((new BigDecimal(stock.getQuantity())).add(new BigDecimal(storageOutputInItemDto.getQuantity())),2,BigDecimal.ROUND_UP));
				this.stockService.updateByMapContext(updateStock);
			}
		}
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_ENTITY_ID,storageOutputInDto.getPurchaseProductIds());
		mapContext.put(WebConstant.KEY_ENTITY_STATUS,PurchaseProductStatus.WAREHOUSING.getValue());
		this.purchaseProductService.updateByMapContext(mapContext);
		//修改采购单状态
		this.purchaseService.updateStatusById(id);
		return ResultFactory.generateRequestResult(this.purchaseService.findOneById(id));
	}

}
