package com.lwxf.industry4.webapp.facade.app.factory.factoryDispatchBill.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.industry4.webapp.bizservice.common.UploadFilesService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillItemService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillPlanItemService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillPlanService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillService;
import com.lwxf.industry4.webapp.bizservice.system.LogisticsCompanyService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockItemService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDto;
import com.lwxf.industry4.webapp.domain.dto.dispatch.DispatchBillPlanItemDto;
import com.lwxf.industry4.webapp.domain.entity.common.UploadFiles;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBill;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillItem;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillPlan;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillPlanItem;
import com.lwxf.industry4.webapp.domain.entity.system.LogisticsCompany;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryDispatchBill.FactoryDispatchBillFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.DateUtil;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：发货管理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/8 0008 14:41
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factoryDispatchBillFacade")
public class FactoryDispatchBillFacadeImpl extends BaseFacadeImpl implements FactoryDispatchBillFacade {
	@Resource(name = "dispatchBillService")
	private DispatchBillService dispatchBillService;
	@Resource(name = "dispatchBillItemService")
	private DispatchBillItemService dispatchBillItemService;
	@Resource(name = "customOrderService")
	private CustomOrderService customOrderService;
	@Resource(name = "finishedStockService")
	private FinishedStockService finishedStockService;
	@Resource(name = "finishedStockItemService")
	private FinishedStockItemService finishedStockItemService;
	@Resource(name = "uploadFilesService")
	private UploadFilesService uploadFilesService;
	@Resource(name = "dispatchBillPlanItemService")
	private DispatchBillPlanItemService dispatchBillPlanItemService;
	@Resource(name = "dispatchBillPlanService")
	private DispatchBillPlanService dispatchBillPlanService;
	@Resource(name = "logisticsCompanyService")
	private LogisticsCompanyService logisticsCompanyService;

	/**
	 * 发货单查询
	 * 待发货 今日 本月
	 *
	 * @param companyId
	 * @return
	 */
	@Override
	public RequestResult findDispatchBillCount(String companyId) {
		MapContext result = MapContext.newOne();
		Integer today = this.dispatchBillService.findTodayCount();
		Integer tobeShipped = this.dispatchBillService.findTobeShipped();
		Integer thisMonth = this.dispatchBillService.findThisMonthCount();
		result.put("today", today);
		result.put("tobeShipped", tobeShipped);
		result.put("thisMonth", thisMonth);
		return ResultFactory.generateRequestResult(result);
	}

	/**
	 * 按条件查询发货列表
	 * 经销商名称 客户名称 地区名称 订单编号 发货状态 起止时间 发货状态
	 * 按创建时间排序
	 *
	 * @param pageNum
	 * @param pageSize
	 * @param mapContext
	 * @return
	 */
	@Override
	public RequestResult findDispathcBillList(Integer pageNum, Integer pageSize, MapContext mapContext) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		paginatedFilter.setFilters(mapContext);
		Map dataSort = new HashMap();
		List sorts = new ArrayList();
		if (mapContext.containsKey("order")) {
			if (mapContext.containsKey("sort")) {
				String sort = mapContext.getTypedValue("sort", String.class);
				if (mapContext.getTypedValue("order", String.class).equals("byCreated")) {
					dataSort.put("created", sort);
					sorts.add(dataSort);
					paginatedFilter.setSorts(sorts);
				}
			}
		}
		if(mapContext.containsKey("topParam")){
			String topParam=mapContext.getTypedValue("topParam",String.class);
			if(topParam.equals("1")){
				mapContext.put("status",0);
			}else if(topParam.equals("2")){
				mapContext.put("today",topParam);
			}
			else if(topParam.equals("3")){
				mapContext.put("thisMonth",topParam);
			}
		}
		PaginatedList<DispatchBillPlanItemDto> dispathcBillList = this.dispatchBillService.findDispathcBillList(paginatedFilter);
		MapContext result = MapContext.newOne();
		result.put("dispathcBillList", dispathcBillList.getRows());
		return ResultFactory.generateRequestResult(result, dispathcBillList.getPagination());
	}

	@Override
	public RequestResult findDispatchBillInfo(String dispatchBillId, String dispatchBillItemId) {
		MapContext mapContext = MapContext.newOne();
		DispatchBill dispatchBill = this.dispatchBillService.findById(dispatchBillId);
		if (dispatchBill == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//包裹信息
		Map finishedItem = this.finishedStockItemService.findByDispatchBillItemId(dispatchBillItemId);
		mapContext.put("finishedItem", finishedItem);
		//所属发货单的包裹信息
		List<String> typeList = new ArrayList<>();//品名
		List<MapContext> dispatchBillItems = this.dispatchBillItemService.findByDispatchBillId(dispatchBillId);
		//包裹数
		Integer baoguoSize = 0;
		for (int i = 0; i < dispatchBillItems.size(); i++) {
			MapContext map = dispatchBillItems.get(i);
			Integer type = map.getTypedValue("type", Integer.class);
			switch (type) {
				case 0:
					typeList.add("柜体");
					break;
				case 1:
					typeList.add("门板-自产");
					break;
				case 2:
					typeList.add("门板-外协");
					break;
				case 3:
					typeList.add("特供实木");
					break;
				case 4:
					typeList.add("五金");
					break;
			}
			Integer count = map.getTypedValue("count", Integer.class);
			baoguoSize = baoguoSize + count;

		}
		mapContext.put("typeList", typeList);
		mapContext.put("baoguoSize", baoguoSize);
		MapContext dispatchBillMessage = this.dispatchBillItemService.findLogisticsByDispatchId(dispatchBillId);
		String cityName = dispatchBillMessage.getTypedValue("mergerName", String.class);
		String mergerName="";
		if(cityName!=null){
			int a = cityName.indexOf(",");
			 mergerName = cityName.substring(a + 1);
		}
		dispatchBillMessage.put("mergerName", mergerName);
		//附件信息
		Integer type = UploadResourceType.DISPATCH_BILL.getType();
		List<UploadFiles> uploadFilesList = this.uploadFilesService.findListByBelongIdAndResourceIdAndResourceType(null, dispatchBillId, type);
		for(UploadFiles uploadFiles:uploadFilesList){
			String fullPath= WebUtils.getDomainUrl()+uploadFiles.getPath();
			uploadFiles.setFullPath(fullPath);
		}
		mapContext.put("dispatchBillMessage", dispatchBillMessage);
		mapContext.put("dispatchBillItems", dispatchBillItems);
		mapContext.put("uploadFilesList", uploadFilesList);
		return ResultFactory.generateRequestResult(mapContext);
	}

	//展示发货基础信息
	@Override
	public RequestResult dispatchBillRedis(String finishedStockItemIds, String orderId) {
		MapContext result = MapContext.newOne();
		String[] finishedItemIdlist = finishedStockItemIds.split(",");
		Integer size = finishedItemIdlist.length;
		List list = new ArrayList();
		for (int i = 0; i < size; i++) {
			String finishedStockitemId = finishedItemIdlist[i];
			MapContext finishedStockItemDto = this.finishedStockItemService.findByFinishedStockitemId(finishedStockitemId);
			finishedStockItemDto.put("finishedStockitemId",finishedStockitemId);
			list.add(finishedStockItemDto);
		}
		CustomOrderDto order = this.customOrderService.findByOrderId(orderId);
		String cityName = order.getCityName();
		if (cityName != null) {
			int a = cityName.indexOf(",");
			cityName = cityName.substring(a + 1);
		}
		String address = order.getAddress();
		String consigneeName = order.getConsigneeName();
		String consigneeTel = order.getConsigneeTel();
		result.put("size", size);
		result.put("list", list);
		if (order.getCityAreaId() == null) {
			result.put("cityAreaId", "");
		} else {
			result.put("cityAreaId", order.getCityAreaId());
		}
		if (cityName == null) {
			result.put("cityName", "");
		} else {
			result.put("cityName", cityName);
		}
		if (address == null) {
			result.put("address", "");
		} else {
			result.put("address", address);
		}
		if (consigneeTel == null) {
			result.put("consigneeTel", "");
		} else {
			result.put("consigneeTel", consigneeTel);
		}
		if (consigneeName == null) {
			result.put("consigneeName", "");
		} else {
			result.put("consigneeName", consigneeName);
		}
		result.put("orderId",orderId);
		result.put("orderNo",order.getNo());
		return ResultFactory.generateRequestResult(result);
	}

	/**
	 * 创建发货单
	 *
	 * @param mapContext
	 * @param request
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addDispatchBill(MapContext mapContext, HttpServletRequest request) {
		String inputer=mapContext.getTypedValue("inputer",String.class);
		//String uid = request.getHeader("X-UID");
		User user = AppBeanInjector.userService.findByUserId(inputer);
		String orderId = (mapContext.getTypedValue("orderId", String.class));
		String mobile = user.getMobile();
		DispatchBill dispatchBill = new DispatchBill();
		dispatchBill.setOrderId(orderId);
		dispatchBill.setOrderNo(mapContext.getTypedValue("orderNo", String.class));
		dispatchBill.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.DISPATCH_NO));
		dispatchBill.setPlanDate(DateUtil.now());
		if(mapContext.containsKey("actualDate")){
			try {
				String actualDate = mapContext.getTypedValue("actualDate", String.class);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				dispatchBill.setActualDate(dateFormat.parse(actualDate));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			dispatchBill.setActualDate(DateUtil.now());
		}
		CustomOrder customOrder = this.customOrderService.findByOrderId(orderId);
		if (customOrder == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//收货人和电话
		String consignee=customOrder.getConsigneeName();
		dispatchBill.setConsignee(consignee);
        String consigneeTel=customOrder.getConsigneeTel();
		dispatchBill.setConsigneeTel(consigneeTel);
		dispatchBill.setCityAreaId(mapContext.getTypedValue("cityAreaId", String.class));
		dispatchBill.setAddress(mapContext.getTypedValue("address", String.class));
		dispatchBill.setLogisticsCompanyId(mapContext.getTypedValue("logisticsCompanyId", String.class));
		dispatchBill.setLogisticsNo(mapContext.getTypedValue("logisticsNo", String.class));
		dispatchBill.setStatus(1);
		dispatchBill.setCreated(DateUtil.now());
		dispatchBill.setCreator(inputer);
		dispatchBill.setNotes(mapContext.getTypedValue("notes", String.class));
		dispatchBill.setDeliverer(inputer);
		dispatchBill.setDelivererTel(mobile);
		this.dispatchBillService.add(dispatchBill);
		//包裹id
		String finishedStockItemId = mapContext.getTypedValue("finishedStockItemIds", String.class);
		String[] finishedStockItemIds = finishedStockItemId.split(",");
		//修改发货计划明细中包裹的状态
		for (String finishedItemId : finishedStockItemIds) {
			DispatchBillPlanItem dispatchBillPlanItem = this.dispatchBillPlanItemService.findByfinishedStockItemId(finishedItemId);
			String id = dispatchBillPlanItem.getId();
			Integer status = 1;
			MapContext map1 = MapContext.newOne();
			map1.put("id", id);
			map1.put("status", status);
			this.dispatchBillPlanItemService.updateByMapContext(map1);
			//添加发货明细
			DispatchBillItem dispatchBillItem = new DispatchBillItem();
			dispatchBillItem.setDispatchBillId(dispatchBill.getId());
			dispatchBillItem.setFinishedStockItemId(finishedItemId);
			dispatchBillItem.setStatus(0);
			this.dispatchBillItemService.add(dispatchBillItem);
			//补充包裹明细里的发货人 发货时间数据
			MapContext map2=MapContext.newOne();
			map2.put("id",finishedItemId);
			map2.put("deliverer",inputer);
			map2.put("delivered", com.lwxf.commons.utils.DateUtil.getSystemDate());
			this.finishedStockItemService.updateByMapContext(map2);
		}
		//判断发货计划中的包裹是否已经发完
		String dispatchBillPlanId = mapContext.getTypedValue("dispatchBillPlanId", String.class);
		String[] dispatchBillPlanIds = dispatchBillPlanId.split(",");
		for(int i=0;i<dispatchBillPlanIds.length;i++) {
			String dispatchPlanId=dispatchBillPlanIds[i];
			DispatchBillPlan dispatchBillPlan = this.dispatchBillPlanService.findById(dispatchPlanId);
			//计划发货数
			Integer num = dispatchBillPlan.getNum();
			//已发货数
			Integer status1 = 1;
			List<DispatchBillPlanItem> dispatchBillPlanItems = this.dispatchBillPlanItemService.findBydbpIdAndStatus(dispatchPlanId, status1);
			Integer count = dispatchBillPlanItems.size();
			//修改发货计划的状态
			if (num == count) {
				MapContext mapContext1 = MapContext.newOne();
				mapContext1.put("id", dispatchBillPlanId);
				mapContext1.put("status", 1);
				this.dispatchBillPlanService.updateByMapContext(mapContext1);
			}
		}
		return ResultFactory.generateRequestResult(dispatchBill);
	}

	/**
	 * 物流公司列表
	 *
	 * @return
	 */
	@Override
	public RequestResult findLogisticsList() {
		List<LogisticsCompany> LogisticsCompany = this.logisticsCompanyService.findAllNormal();
		return ResultFactory.generateRequestResult(LogisticsCompany);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addFiles(String dispatchBillId, List<MultipartFile> multipartFiles, HttpServletRequest request) {
		String uid = request.getHeader("X-UID");
		String cid = request.getHeader("X-CID");
		List<Map<String, Object>> list = new ArrayList<>();
		UploadFiles UploadFiles = new UploadFiles();
		UploadInfo uploadInfo;
		for (MultipartFile mul : multipartFiles) {
			uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, mul, UploadResourceType.DISPATCH_BILL, dispatchBillId);
			UploadFiles.setCompanyId(cid);
			UploadFiles.setResourceId(dispatchBillId);
			UploadFiles.setBelongId(dispatchBillId);
			UploadFiles.setName(uploadInfo.getFileName());
			UploadFiles.setCreator(uid);
			UploadFiles.setCreated(com.lwxf.commons.utils.DateUtil.getSystemDate());
			UploadFiles.setPath(uploadInfo.getRelativePath());
			UploadFiles.setFullPath(WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
			UploadFiles.setMime(uploadInfo.getFileMimeType().getRealType());
			UploadFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
			UploadFiles.setStatus(UploadType.FORMAL.getValue());
			UploadFiles.setResourceType(UploadResourceType.DISPATCH_BILL.getType());
			this.uploadFilesService.add(UploadFiles);
			MapContext mapContext = MapContext.newOne();
			String imagePath=WebUtils.getDomainUrl() + uploadInfo.getRelativePath();
			mapContext.put("imagePath", imagePath);
			list.add(mapContext);
		}
		return ResultFactory.generateRequestResult(list);
	}


}
