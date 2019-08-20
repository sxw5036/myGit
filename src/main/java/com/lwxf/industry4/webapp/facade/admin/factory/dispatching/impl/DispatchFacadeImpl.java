package com.lwxf.industry4.webapp.facade.admin.factory.dispatching.impl;

import javax.annotation.Resource;

import java.util.*;

import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderLogService;
import com.lwxf.industry4.webapp.common.enums.order.OrderStage;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderLog;
import org.elasticsearch.cluster.ClusterState;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.common.UploadFilesService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillItemService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillPlanItemService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillService;
import com.lwxf.industry4.webapp.bizservice.system.LogisticsCompanyService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockItemService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockService;
import com.lwxf.industry4.webapp.common.aop.syslog.OperationMoudule;
import com.lwxf.industry4.webapp.common.aop.syslog.OperationType;
import com.lwxf.industry4.webapp.common.aop.syslog.SysOperationLog;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.dispatch.DispatchBillItemStatus;
import com.lwxf.industry4.webapp.common.enums.dispatch.DispatchBillStatus;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.enums.storage.DispatchBillPlanItemStatus;
import com.lwxf.industry4.webapp.common.enums.storage.FinishedStockItemType;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.dispatch.DispatchBillDto;
import com.lwxf.industry4.webapp.domain.dto.dispatch.DispatchBillItemDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.DispatchPrintTableDto;
import com.lwxf.industry4.webapp.domain.entity.common.UploadFiles;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBill;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStock;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStockItem;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.dispatching.DispatchFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/25/025 13:57
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("dispatchFacade")
public class DispatchFacadeImpl extends BaseFacadeImpl implements DispatchFacade {

	@Resource(name = "dispatchBillService")
	private DispatchBillService dispatchBillService;
	@Resource(name = "customOrderService")
	private CustomOrderService customOrderService;
	@Resource(name = "finishedStockService")
	private FinishedStockService finishedStockService;
	@Resource(name = "logisticsCompanyService")
	private LogisticsCompanyService logisticsCompanyService;
	@Resource(name = "finishedStockItemService")
	private FinishedStockItemService finishedStockItemService;
	@Resource(name = "dispatchBillItemService")
	private DispatchBillItemService dispatchBillItemService;
	@Resource(name = "dispatchBillPlanItemService")
	private DispatchBillPlanItemService dispatchBillPlanItemService;
	@Resource(name = "uploadFilesService")
	private UploadFilesService uploadFilesService;
	@Resource(name = "customOrderLogService")
	private CustomOrderLogService customOrderLogService;

	@Override
	public RequestResult findDispatchList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID,WebUtils.getCurrBranchId());
		paginatedFilter.setFilters(mapContext);
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		List<Map<String, String>> sorts = new ArrayList<Map<String, String>>();
		Map<String, String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED, "desc");
		sorts.add(created);
		paginatedFilter.setSorts(sorts);
		return ResultFactory.generateRequestResult(this.dispatchBillService.selectByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "新建发货单",operationType = OperationType.INSERT,operationMoudule = OperationMoudule.DISPATCH)
	public RequestResult addDispatch(DispatchBillDto dispatchBillDto) {
		//判断物流公司是否存在
		if (dispatchBillDto.getLogisticsCompanyId() == null || !this.logisticsCompanyService.isExist(dispatchBillDto.getLogisticsCompanyId())) {
			return ResultFactory.generateResNotFoundResult();
		}
		//包裹的订单ID集合
		Set<String> dispatchs = new HashSet<String>();
		//包裹中包含的订单数量
		List<String> listOrderIds = new ArrayList<>();
		//循环进行分组操作
		for(DispatchBillItemDto dispatchBillItemDto : dispatchBillDto.getDispatchBillItemDtoList()){
			//通过包裹查询 包裹所属的成品库条目
			FinishedStockItem byId = this.finishedStockItemService.findById(dispatchBillItemDto.getFinishedStockItemId());
			//如果包裹不存在 或者 包裹 未创建配送计划 或者 包裹 已经发货 则不允许发货
			if (byId == null || byId.getShipped().equals(false) || byId.getDelivered() != null) {
				return ResultFactory.generateResNotFoundResult();
			}
			//通过包裹查询成品库单 以获取订单ID NO
			FinishedStock finishedStock = this.finishedStockService.findById(byId.getFinishedStockId());
			if(!listOrderIds.contains(finishedStock.getOrderId())){
				listOrderIds.add(finishedStock.getOrderId());
			}
			//判断此包裹的订单ID 是否存在于 集合中 存在 则对集合 追加
			dispatchs.add(finishedStock.getOrderId());
		}
		//赋值发货单编号
		dispatchBillDto.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.DISPATCH_NO));
		this.dispatchBillService.add(dispatchBillDto);
		//循环发货条目 生成条目数据
		for(DispatchBillItemDto dispatchBillItemDto:dispatchBillDto.getDispatchBillItemDtoList()){
			dispatchBillItemDto.setDispatchBillId(dispatchBillDto.getId());
			dispatchBillItemDto.setStatus(DispatchBillItemStatus.ARRIVAL.getValue());
			this.dispatchBillItemService.add(dispatchBillItemDto);
			//修改成品库单下的条目状态
			MapContext mapContext = MapContext.newOne();
			mapContext.put(WebConstant.KEY_ENTITY_ID, dispatchBillItemDto.getFinishedStockItemId());
			mapContext.put("deliverer", WebUtils.getCurrUserId());//出库人
			mapContext.put("delivered", DateUtil.getSystemDate());//出库时间
			this.finishedStockItemService.updateByMapContext(mapContext);
			//修改配送计划条目发货状态
			MapContext updatePlanItem = new MapContext();
			updatePlanItem.put("finishedStockItemId",dispatchBillItemDto.getFinishedStockItemId());
			updatePlanItem.put(WebConstant.KEY_ENTITY_STATUS,DispatchBillPlanItemStatus.SHIPPED.getValue());
			this.dispatchBillPlanItemService.updateStatusByFinishedItemId(updatePlanItem);
		}
		//循环发货单集合
		for(String orderId:dispatchs){
			//修改订单状态为已配送
			MapContext updateOrder = new MapContext();
			updateOrder.put(WebConstant.KEY_ENTITY_ID, orderId);
			updateOrder.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.SHIPPED.getValue());
			this.customOrderService.updateByMapContext(updateOrder);
		}
		//如果存在使用的图片资源 则修改资源为正式资源
		if(dispatchBillDto.getFileIds()!=null&&dispatchBillDto.getFileIds().size()!=0){
			MapContext mapContext = new MapContext();
			mapContext.put(WebConstant.KEY_ENTITY_ID,dispatchBillDto.getFileIds());
			mapContext.put("resourceId",dispatchBillDto.getId());
			mapContext.put(WebConstant.KEY_ENTITY_STATUS,UploadType.FORMAL.getValue());
			this.uploadFilesService.updateByIds(mapContext);
		}
		//记录处理日志
		if(listOrderIds.size()>0){
			for(String id : listOrderIds){
				//更新订单实际交付时间
				CustomOrder order = customOrderService.findByOrderId(id);
				if(order!=null){
					order.setDeliveryDate(new Date());
					MapContext map = MapContext.newOne();
					map.put("deliveryDate",new Date());
					map.put("id",id);
					customOrderService.updateByMapContext(map);
				}
				CustomOrderLog log = new CustomOrderLog();
				log.setCreated(new Date());
				log.setCreator(WebUtils.getCurrUserId());
				log.setName("当前订单已发货");
				log.setStage(OrderStage.DELIVERY.getValue());
				log.setContent("当前订单已发货");
				log.setCustomOrderId(id);
				customOrderLogService.add(log);
			}
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "删除发货单",operationType = OperationType.DELETE,operationMoudule = OperationMoudule.DISPATCH)
	public RequestResult deleteById(String id) {
		DispatchBill dispatchBill = this.dispatchBillService.findById(id);
		//判断发货单是否存在
		if (dispatchBill == null) {
			return ResultFactory.generateSuccessResult();
		}
		//判断发货单是否是运输中
		if (dispatchBill.getStatus() != DispatchBillStatus.WAITING_FOR_PIECES.getValue()) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_DELIVERED_NOT_OPERATE_10082, AppBeanInjector.i18nUtil.getMessage("BIZ_DELIVERED_NOT_OPERATE_10082"));
		}
		this.dispatchBillService.deleteDispatchBillAndItemById(id);
		return ResultFactory.generateSuccessResult();
	}

//	@Override
//	@Transactional(value = "transactionManager")
//	public RequestResult deliverById(MapContext mapContext, String id) {
//		DispatchBill dispatchBill = this.dispatchBillService.findById(id);
//		//判断发货单是否存在, 以及状态是否是待取件
//		if (dispatchBill == null || !dispatchBill.getStatus().equals(DispatchBillStatus.WAITING_FOR_PIECES.getValue())) {
//			return ResultFactory.generateResNotFoundResult();
//		}
//		String logisticsCompanyId = mapContext.getTypedValue("logisticsCompanyId", String.class);
//		//判断物流公司是否存在
//		if (logisticsCompanyId == null || !this.logisticsCompanyService.isExist(logisticsCompanyId)) {
//			return ResultFactory.generateResNotFoundResult();
//		}
//		mapContext.put(WebConstant.KEY_ENTITY_ID, id);
//		mapContext.put(WebConstant.KEY_ENTITY_STATUS, DispatchBillStatus.TRANSPORT.getValue());//配送单状态
//		mapContext.put("actualDate", DateUtil.getSystemDate());//配送日期
//		this.dispatchBillService.updateByMapContext(mapContext);
//
//
//		String orderId = this.finishedStockService.findById(dispatchBill.getFinishedStockId()).getOrderId();
//		CustomOrder order = this.customOrderService.findById(orderId);
//		FinishedStock finishedStock = this.finishedStockService.findById(dispatchBill.getFinishedStockId());
//		if(order.getStatus().equals(OrderStatus.TO_DISPATCH.getValue())){//如果订单的状态为待配送则修改为配送中
//			MapContext updateOrder = new MapContext();
//			updateOrder.put(WebConstant.KEY_ENTITY_ID,orderId);
//			updateOrder.put(WebConstant.KEY_ENTITY_STATUS,OrderStatus.DISPATCHING.getValue());
//			this.customOrderService.updateByMapContext(mapContext);
//		}
//
//
//		//全部发货则判断发货条目总数是否等于成品库包数
//		List<DispatchBillItem> dispatchBillItems = this.dispatchBillItemService.findListByOrderId(orderId);
//		if (dispatchBillItems.size() == finishedStock.getPackages()) {
//			//全部配送修改订单状态为已配送
//			MapContext updateOrder = new MapContext();
//			updateOrder.put(WebConstant.KEY_ENTITY_ID, orderId);
//			updateOrder.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.DISPATCHING.getValue());
//			this.customOrderService.updateByMapContext(updateOrder);
//			//全部配送修改配送单状态为全部配送
//			MapContext updateFinishedStock = new MapContext();
//			updateFinishedStock.put(WebConstant.KEY_ENTITY_ID, finishedStock.getId());
//			updateFinishedStock.put(WebConstant.KEY_ENTITY_STATUS, FinishedStockStatus.ALL_SHIPMENT.getValue());
//			this.finishedStockService.updateByMapContext(updateFinishedStock);
//		} else {
//			//存在配送单为待配送,则判断订单状态是否为待配送 是则修改
//			if (order.getStatus().equals(OrderStatus.TO_DISPATCH.getValue())) {
//				MapContext updateOrder = new MapContext();
//				updateOrder.put(WebConstant.KEY_ENTITY_ID, orderId);
//				updateOrder.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.DISPATCHING.getValue());
//				this.customOrderService.updateByMapContext(updateOrder);
//			}
//			//存在配送单为待配送,则判断成品库单状态是否是待配送 是则修改
//			if (finishedStock.getStatus().equals(FinishedStockStatus.UNSHIPPED.getValue())) {
//				MapContext updateFinishedStock = new MapContext();
//				updateFinishedStock.put(WebConstant.KEY_ENTITY_ID, finishedStock.getId());
//				updateFinishedStock.put(WebConstant.KEY_ENTITY_STATUS, FinishedStockStatus.PARTIAL_SHIPMENT.getValue());
//				this.finishedStockService.updateByMapContext(updateFinishedStock);
//			}
//		}
//		return ResultFactory.generateRequestResult(this.dispatchBillService.findById(id));
//	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult uploadFiles(String id, List<MultipartFile> multipartFileList) {
		boolean isId = false;
		if(!id.equals("dispatchId")){//如果Id已存在,则判断资源是否存在
			DispatchBill dispatchBill = this.dispatchBillService.findById(id);
			//判断发货单是否存在
			if (dispatchBill == null) {
				return ResultFactory.generateResNotFoundResult();
			}
			isId = true;
		}
		List imgList = new ArrayList();
		for(MultipartFile multipartFile:multipartFileList){
			UploadFiles uploadFiles = new UploadFiles();
			if(isId){
				uploadFiles.setResourceId(id);
			}
			uploadFiles.setCreated(DateUtil.getSystemDate());
			uploadFiles.setCompanyId(WebUtils.getCurrCompanyId());
			uploadFiles.setCreator(WebUtils.getCurrUserId());
			UploadInfo uploadInfo=AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.TEMP, multipartFile, UploadResourceType.DISPATCH_BILL, id);
			uploadFiles.setName(uploadInfo.getFileName());
			uploadFiles.setPath(uploadInfo.getRelativePath());
			uploadFiles.setFullPath(WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
			uploadFiles.setMime(uploadInfo.getFileMimeType().getRealType());
			uploadFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
			uploadFiles.setStatus(UploadType.TEMP.getValue());
			uploadFiles.setResourceType(UploadResourceType.DISPATCH_BILL.getType());
			this.uploadFilesService.add(uploadFiles);
			imgList.add(uploadFiles);
		}
		return ResultFactory.generateRequestResult(imgList);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateDispatch(MapContext mapContext, String id) {
		mapContext.put("id",id);
		return ResultFactory.generateRequestResult(dispatchBillService.updateByMapContext(mapContext));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteFile(String fileId) {
		//判断资源是否存在
		UploadFiles byId = this.uploadFilesService.findById(fileId);
		if(byId==null){
			return ResultFactory.generateSuccessResult();
		}
		this.uploadFilesService.deleteById(fileId);
		//清除本地文件
		AppBeanInjector.baseFileUploadComponent.deleteFileByDir(byId.getFullPath());
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findDispatchPrintInfo(String id) {
		DispatchPrintTableDto dispatchPrintTableDto = this.dispatchBillService.findDispatchPrintInfo(id);
		dispatchPrintTableDto.setBodyDispatchBillItems(this.dispatchBillItemService.findListByDIdAndTypes(id,Arrays.asList(FinishedStockItemType.CABINET.getValue())));
		dispatchPrintTableDto.setDoorDispatchBillItems(this.dispatchBillItemService.findListByDIdAndTypes(id,Arrays.asList(FinishedStockItemType.DOOR_HOMEGROWN.getValue(),FinishedStockItemType.DOOR_OUTSOURCING.getValue(),FinishedStockItemType.SPECIAL_SUPPLY.getValue())));
		dispatchPrintTableDto.setHardwareDispatchBillItems(this.dispatchBillItemService.findListByDIdAndTypes(id,Arrays.asList(FinishedStockItemType.HARDWARE.getValue())));
		return ResultFactory.generateRequestResult(dispatchPrintTableDto);
	}

	@Override
	public RequestResult findDispatchBillCount(String branchId) {
		MapContext mapContext=this.dispatchBillPlanItemService.findCountByBranchId(branchId);
		List result=new ArrayList();
		int a=0;
		for(int i=0;i<3;i++){
			Map map = new HashMap();
			switch (a) {
				case 	0:
					map.put("name", "今日计划发货");
					map.put("value",mapContext.getTypedValue("plann",Integer.class));
					a=a+1;
					break;
				case 1:
					map.put("name", "今日发货完成");
					map.put("value",mapContext.getTypedValue("orver",Integer.class));
					a=a+1;
					break;
				case 2:
					map.put("name", "今日发货笔数");
					map.put("value",mapContext.getTypedValue("num",Integer.class));
					a=a+1;
					break;
			}
			result.add(map);
		}
		return ResultFactory.generateRequestResult(result);
	}
}
