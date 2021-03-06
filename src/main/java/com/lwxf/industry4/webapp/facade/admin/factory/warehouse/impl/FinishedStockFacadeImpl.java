package com.lwxf.industry4.webapp.facade.admin.factory.warehouse.impl;

import javax.annotation.Resource;

import java.util.*;

import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderLogService;
import com.lwxf.industry4.webapp.common.enums.order.OrderStage;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderLog;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderFilesService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillPlanItemService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillPlanService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockItemService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderFilesCategory;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderFilesStatus;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderFilesType;
import com.lwxf.industry4.webapp.common.enums.storage.DispatchBillPlanItemStatus;
import com.lwxf.industry4.webapp.common.enums.storage.DispatchBillPlanStatus;
import com.lwxf.industry4.webapp.common.enums.storage.FinishedStockStatus;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.common.utils.excel.BaseExportExcelUtil;
import com.lwxf.industry4.webapp.common.utils.excel.ExcelParam;
import com.lwxf.industry4.webapp.domain.dto.dispatch.DispatchBillDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.DispatchBillPlanDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockItemDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderFiles;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillPlanItem;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStock;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStockItem;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.warehouse.FinishedStockFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/24/024 13:47
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("finishedStockFacade")
public class FinishedStockFacadeImpl extends BaseFacadeImpl implements FinishedStockFacade {
	@Resource(name = "finishedStockService")
	private FinishedStockService finishedStockService;
	@Resource(name="customOrderService")
	private CustomOrderService customOrderService;
	@Resource(name = "storageService")
	private StorageService storageService;
	@Resource(name = "finishedStockItemService")
	private FinishedStockItemService finishedStockItemService;
	@Resource(name = "dispatchBillService")
	private DispatchBillService dispatchBillService;
	@Resource(name = "dispatchBillPlanService")
	private DispatchBillPlanService dispatchBillPlanService;
	@Resource(name = "dispatchBillPlanItemService")
	private DispatchBillPlanItemService dispatchBillPlanItemService;
	@Resource(name = "customOrderFilesService")
	private CustomOrderFilesService customOrderFilesService;
	@Resource(name = "customOrderLogService")
	private CustomOrderLogService customOrderLogService;

	@Override
	public RequestResult findFinishedDto(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID,WebUtils.getCurrBranchId());
		paginatedFilter.setFilters(mapContext);
		List<Map<String,String>> sorts=  new ArrayList<Map<String,String>>();
//		Map<String,String> orderId = new HashMap<String,String>();
//		orderId.put("orderId","desc");
//		sorts.add(orderId);
		Map<String,String> created = new HashMap<String,String>();
		created.put("created","desc");
		sorts.add(created);
		Map<String,String> id = new HashMap<String,String>();
		id.put("id","desc");
		sorts.add(id);
		paginatedFilter.setSorts(sorts);
		return ResultFactory.generateRequestResult(this.finishedStockItemService.findListByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addFinishedStock(FinishedStockDto finishedStockDto) {
		//判断订单是否存在
		CustomOrder customOrder = this.customOrderService.findById(finishedStockDto.getOrderId());
		if(customOrder==null||!customOrder.getNo().equals(finishedStockDto.getOrderNo())){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断订单是否已存在成品库单 如果已存在则不允许新建
		FinishedStock finishedStock = this.finishedStockService.findByOrderId(finishedStockDto.getOrderId());
		if(finishedStock!=null){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		//判断仓库是否存在
		if(!this.storageService.isExist(finishedStockDto.getStorageId())){
			return ResultFactory.generateResNotFoundResult();
		}

		//验证包装编号不允许重复
		Set set = new HashSet<String>();
		for(FinishedStockItemDto finishedStockItemDto:finishedStockDto.getFinishedStockItemDtos()){
			set.add(finishedStockItemDto.getBarcode());
		}
		if(set.size()!=finishedStockDto.getFinishedStockItemDtos().size()||this.finishedStockItemService.findListByBarcodes(set).size()!=0){
			Map res = new HashMap<String,String>();
			res.put("barcode",AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,res);
		}
		this.finishedStockService.add(finishedStockDto);
		for(FinishedStockItemDto finishedStockItemDto:finishedStockDto.getFinishedStockItemDtos()){
			finishedStockItemDto.setFinishedStockId(finishedStockDto.getId());
			this.finishedStockItemService.add(finishedStockItemDto);
		}
		//记录操作日志
		CustomOrderLog log = new CustomOrderLog();
		log.setCreated(new Date());
		log.setCreator(WebUtils.getCurrUserId());
		log.setName("订单打包中");
		log.setStage(OrderStage.PACKAGING.getValue());
		log.setContent("订单号："+customOrder.getNo()+" 中的产品正在打包中");
		log.setCustomOrderId(customOrder.getId());
		customOrderLogService.add(log);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addFinishedItem(FinishedStockItem finishedStockItem, String id,String storageId) {
		FinishedStock finishedStock = this.finishedStockService.findById(id);
		//判断成品库单是否存在以及是否是对应仓库下的
		if(finishedStock==null||!finishedStock.getStorageId().equals(storageId)){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断成品库单包数是否达到预期值 达到则不允许新增
		List<FinishedStockItemDto> listByFinishedStockId = this.finishedStockService.findListByFinishedStockId(id);
		if(listByFinishedStockId.size()==finishedStock.getPackages()){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_RESOURCES_LIMIT_10076,AppBeanInjector.i18nUtil.getMessage("BIZ_RESOURCES_LIMIT_10076"));
		}
		//判断编号是否重复
		if(this.finishedStockItemService.findListByBarcodes(new HashSet(Arrays.asList(finishedStockItem.getBarcode()))).size()!=0){
			Map res = new HashMap<String,String>();
			res.put("barcodes",AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,res);
		}
		this.finishedStockItemService.add(finishedStockItem);
		return ResultFactory.generateRequestResult(this.finishedStockItemService.findOneById(finishedStockItem.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateFinishedStock(String id, MapContext mapContext,String storageId) {
		//判断成品单是否存在
		FinishedStock finishedStock = this.finishedStockService.findById(id);
		if(finishedStock==null||!finishedStock.getStorageId().equals(storageId)){
			return ResultFactory.generateResNotFoundResult();
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		this.finishedStockService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.finishedStockService.findOneById(id));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteFinishedStockById(String id,String storageId) {
		FinishedStock finishedStock = this.finishedStockService.findById(id);
		//判断成品库单是否存在
		if(finishedStock==null||!finishedStock.getStorageId().equals(storageId)){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断成品库单是否已发货
		if(!finishedStock.getStatus().equals(FinishedStockStatus.UNSHIPPED.getValue())){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_DELIVERED_NOT_OPERATE_10082,AppBeanInjector.i18nUtil.getMessage("BIZ_DELIVERED_NOT_OPERATE_10082"));
		}
		//判断成品库下的包裹 是否已有创建配送计划的 有 则不允许删除
		List<DispatchBillDto> dispatchsByOrderId = this.dispatchBillService.findDispatchsByOrderId(finishedStock.getOrderId());
		if(dispatchsByOrderId.size()!=0){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_DELIVERED_NOT_OPERATE_10082,AppBeanInjector.i18nUtil.getMessage("BIZ_DELIVERED_NOT_OPERATE_10082"));
		}
		this.finishedStockItemService.deleteByFinishedStockId(id);
		this.finishedStockService.deleteById(id);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateItemById(String itemId,MapContext mapContext) {
		FinishedStockItem finishedStockItem = this.finishedStockItemService.findById(itemId);
		//判断item是否存在
		if(finishedStockItem==null){
			return ResultFactory.generateResNotFoundResult();
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID,itemId);
		this.finishedStockItemService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.finishedStockItemService.findOneById(itemId));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteByItemId(String id, String itemId,String storageId) {
		//判断成品库单是否存在
		FinishedStock finishedStock = this.finishedStockService.findById(id);
		if(finishedStock==null||!finishedStock.getStorageId().equals(storageId)){
			return ResultFactory.generateResNotFoundResult();
		}
		FinishedStockItem finishedStockItem = this.finishedStockItemService.findById(itemId);
		//判断item是否存在
		if(finishedStockItem==null){
			return ResultFactory.generateSuccessResult();
		}
		//判断是否已发货
		if(finishedStockItem.getDelivered()!=null){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_DELIVERED_NOT_OPERATE_10082,AppBeanInjector.i18nUtil.getMessage("BIZ_DELIVERED_NOT_OPERATE_10082"));
		}
		this.finishedStockItemService.deleteById(itemId);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult itemWarehousing(String itemId,MapContext mapContext) {
		FinishedStockItem finishedStockItem = this.finishedStockItemService.findById(itemId);
		//判断item是否存在
		if(finishedStockItem==null){
			return ResultFactory.generateResNotFoundResult();
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID,itemId);
		mapContext.put("ins",true);
		this.finishedStockItemService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.finishedStockItemService.findOneById(itemId));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addDispatchPlan(DispatchBillPlanDto dispatchBillPlanDto) {
		dispatchBillPlanDto.setCreated(DateUtil.getSystemDate());
		dispatchBillPlanDto.setCreator(WebUtils.getCurrUserId());
		dispatchBillPlanDto.setNum(dispatchBillPlanDto.getDispatchBillPlanItems().size());
		dispatchBillPlanDto.setStatus(DispatchBillPlanStatus.INCOMPLETE.getValue());
		this.dispatchBillPlanService.add(dispatchBillPlanDto);
		List itemIds = new ArrayList();
		Set ids = new HashSet();
		for(DispatchBillPlanItem dispatchBillPlanItem:dispatchBillPlanDto.getDispatchBillPlanItems()){
			dispatchBillPlanItem.setStatus(DispatchBillPlanItemStatus.UNSHIPPED.getValue());
			dispatchBillPlanItem.setDispatchBillPlanId(dispatchBillPlanDto.getId());
			//判断包裹是否存在 以及包裹是否已入库 并且 未发货 未创建配送计划
			FinishedStockItem finishedStockItem = this.finishedStockItemService.findById(dispatchBillPlanItem.getFinishedStockItemId());
			if(finishedStockItem==null||finishedStockItem.getIn().equals(false)||finishedStockItem.getShipped().equals(true)||finishedStockItem.getDelivered()!=null){
				return ResultFactory.generateResNotFoundResult();
			}
			itemIds.add(dispatchBillPlanItem.getFinishedStockItemId());
			ids.add(dispatchBillPlanItem.getFinishedStockItemId());
		}
		if(itemIds.size()!=ids.size()){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		for(DispatchBillPlanItem dispatchBillPlanItem:dispatchBillPlanDto.getDispatchBillPlanItems()){
			this.dispatchBillPlanItemService.add(dispatchBillPlanItem);
		}
		//修改包裹状态为已创建配送计划
		this.finishedStockItemService.updateShippedByIds(itemIds);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult uploadPackageFiles(String id, String itemId, List<MultipartFile> multipartFileList) {
		CustomOrderFiles customOrderFiles = new CustomOrderFiles();
		if(itemId.equals("itemId")){//如果是创建包裹时上传文件
			customOrderFiles.setStatus(CustomOrderFilesStatus.TEMP.getValue());
		}else{
			customOrderFiles.setStatus(CustomOrderFilesStatus.FORMAL.getValue());
			customOrderFiles.setBelongId(itemId);
		}
		customOrderFiles.setCustomOrderId(id);
		customOrderFiles.setCategory(CustomOrderFilesCategory.ACCESSORY.getValue());
		customOrderFiles.setType(CustomOrderFilesType.ORDER_PACKAGE.getValue());
		customOrderFiles.setCreated(DateUtil.getSystemDate());
		customOrderFiles.setCreator(WebUtils.getCurrUserId());
		List imgList = new ArrayList();
		for (MultipartFile multipartFile : multipartFileList) {
			UploadInfo uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, multipartFile, UploadResourceType.CUSTOM_ORDER, id, itemId);
			customOrderFiles.setPath(uploadInfo.getRelativePath());
			customOrderFiles.setFullPath(WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
			customOrderFiles.setName(uploadInfo.getFileName());
			customOrderFiles.setMime(uploadInfo.getFileMimeType().getRealType());
			customOrderFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
			this.customOrderFilesService.add(customOrderFiles);
			imgList.add(customOrderFiles);
		}
		return ResultFactory.generateRequestResult(imgList);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deletePackageFiles(String id, String itemId, String fileId) {
		//判断资源文件是否存在
		CustomOrderFiles byId = this.customOrderFilesService.findById(fileId);
		if(byId==null||!byId.getCustomOrderId().equals(id)){
			return ResultFactory.generateSuccessResult();
		}
		//删除数据库记录
		this.customOrderFilesService.deleteById(fileId);
		//删除本地文件
		AppBeanInjector.baseFileUploadComponent.deleteFileByDir(byId.getFullPath());
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult writeExcel(PaginatedFilter paginatedFilter, ExcelParam excelParam) {
		List<Map<String,String>> sorts=  new ArrayList<Map<String,String>>();
		Map<String,String> created = new HashMap<String,String>();
		created.put("orderId","desc");
		sorts.add(created);
		paginatedFilter.setSorts(sorts);

		new BaseExportExcelUtil().download("第一页",this.finishedStockItemService.findListMapByFilter(paginatedFilter).getRows(),excelParam);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findFinishedStockNos(String order,MapContext mapContext) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(1);
		pagination.setPageSize(100);
		paginatedFilter.setPagination(pagination);
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID,WebUtils.getCurrBranchId());
		paginatedFilter.setFilters(mapContext);
		List<Map<String,String>> sorts=  new ArrayList<Map<String,String>>();
		Map<String,String> created = new HashMap<String, String>();
		if(order!=null){
			if(order.equals("orderCreated")){
				created.put("orderCreated","asc");
			}else if(order.equals("consigneeName")){
				created.put("consigneeName","desc");
			}
		}else {
			created.put("finishedCreated","desc");
		}
		sorts.add(created);
		paginatedFilter.setSorts(sorts);
		PaginatedList<MapContext> result=this.finishedStockItemService.findFinishedStockNos(paginatedFilter);
		return ResultFactory.generateRequestResult(result.getRows());
	}

	@Override
	public RequestResult findFinishedStockCount(String branchId) {
		MapContext mapContext=this.finishedStockService.findCountByBranchId(branchId);
		List result=new ArrayList();
		int a=0;
		for(int i=0;i<3;i++){
			Map map = new HashMap();
			switch (a) {
				case 	0:
					map.put("name", "今日包装数");
					map.put("value",mapContext.getTypedValue("baoZhuang",Integer.class));
					a=a+1;
					break;
				case 1:
					map.put("name", "今日包裹订单数");
					map.put("value",mapContext.getTypedValue("orderNum",Integer.class));
					a=a+1;
					break;
				case 2:
					map.put("name", "今日入库数量");
					map.put("value",mapContext.getTypedValue("ruKu",Integer.class));
					a=a+1;
					break;
			}
			result.add(map);
		}
		return ResultFactory.generateRequestResult(result);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addDispatchPlanByOrder(DispatchBillPlanDto dispatchBillPlanDto) {
		List<String> orderIds = dispatchBillPlanDto.getOrderIds();
		//查询这些订单下的包裹
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(1);
		pagination.setPageNum(999999999);
		paginatedFilter.setPagination(pagination);
		MapContext mapContext = new MapContext();
		List<List<FinishedStockItemDto>> lists = new ArrayList<List<FinishedStockItemDto>>();
		int pages = 0;
		for(String orderId:orderIds){
			mapContext.put("orderId",orderId);
			paginatedFilter.setFilters(mapContext);
			PaginatedList<FinishedStockItemDto> listByFilter = this.finishedStockItemService.findListByFilter(paginatedFilter);
			lists.add(listByFilter.getRows());
			pages+=listByFilter.getRows().size();
		}
		if(pages!=0){
			dispatchBillPlanDto.setBranchId(WebUtils.getCurrBranchId());
			dispatchBillPlanDto.setCreated(DateUtil.getSystemDate());
			dispatchBillPlanDto.setCreator(WebUtils.getCurrUserId());
			dispatchBillPlanDto.setNum(pages);
			dispatchBillPlanDto.setStatus(DispatchBillPlanStatus.INCOMPLETE.getValue());
			this.dispatchBillPlanService.add(dispatchBillPlanDto);
			List itemIds = new ArrayList();
			Set ids = new HashSet();
			for(List<FinishedStockItemDto> finishedStockItemDtoList:lists){
				for(FinishedStockItemDto finishedStockItemDto :finishedStockItemDtoList){
					DispatchBillPlanItem dispatchBillPlanItem = new DispatchBillPlanItem();
					dispatchBillPlanItem.setStatus(DispatchBillPlanItemStatus.UNSHIPPED.getValue());
					dispatchBillPlanItem.setDispatchBillPlanId(dispatchBillPlanDto.getId());
					dispatchBillPlanItem.setFinishedStockItemId(finishedStockItemDto.getId());
					dispatchBillPlanDto.getDispatchBillPlanItems().add(dispatchBillPlanItem);
					//判断包裹是否存在 以及包裹是否已入库 并且 未发货 未创建配送计划
					FinishedStockItem finishedStockItem = this.finishedStockItemService.findById(dispatchBillPlanItem.getFinishedStockItemId());
					if(finishedStockItem==null||finishedStockItem.getIn().equals(false)||finishedStockItem.getShipped().equals(true)||finishedStockItem.getDelivered()!=null){
						return ResultFactory.generateResNotFoundResult();
					}
					itemIds.add(dispatchBillPlanItem.getFinishedStockItemId());
					ids.add(dispatchBillPlanItem.getFinishedStockItemId());
				}
			}
			if(itemIds.size()!=ids.size()){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
			}
			for(DispatchBillPlanItem dispatchBillPlanItem:dispatchBillPlanDto.getDispatchBillPlanItems()){
				this.dispatchBillPlanItemService.add(dispatchBillPlanItem);
			}
			//修改包裹状态为已创建配送计划
			this.finishedStockItemService.updateShippedByIds(itemIds);
		}
		return ResultFactory.generateSuccessResult();
	}

}
