package com.lwxf.industry4.webapp.facade.admin.factory.dispatching.impl;

import javax.annotation.Resource;

import java.util.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillItemService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillPlanItemService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillService;
import com.lwxf.industry4.webapp.bizservice.system.LogisticsCompanyService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockItemService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.dispatch.DispatchBillItemStatus;
import com.lwxf.industry4.webapp.common.enums.dispatch.DispatchBillStatus;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.enums.storage.DispatchBillPlanItemStatus;
import com.lwxf.industry4.webapp.common.enums.storage.FinishedStockStatus;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.dispatch.DispatchBillDto;
import com.lwxf.industry4.webapp.domain.dto.dispatch.DispatchBillItemDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBill;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillItem;
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

	@Override
	public RequestResult findDispatchList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
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
	public RequestResult addDispatch(DispatchBillDto dispatchBillDto) {
		//判断物流公司是否存在
		if (dispatchBillDto.getLogisticsCompanyId() == null || !this.logisticsCompanyService.isExist(dispatchBillDto.getLogisticsCompanyId())) {
			return ResultFactory.generateResNotFoundResult();
		}

		//物流单分组
		Map<String,List<DispatchBillItemDto>> dispatchs = new HashMap<String,List<DispatchBillItemDto>>();
		//订单编号集合 orderId 为 key
		Map<String,String> orderNo = new HashMap<String,String>();
		//成品库单集合 orderId 为 key
		Map<String,String> finishedId = new HashMap<String,String>();
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
			String orderId = finishedStock.getOrderId();
			//判断此包裹的订单ID 是否存在于 集合中 存在 则对集合 追加
			if(dispatchs.containsKey(orderId)){
				dispatchs.get(orderId).add(dispatchBillItemDto);
			}else{//不存在则新建
				List<DispatchBillItemDto> itemIds = new ArrayList<DispatchBillItemDto>();
				itemIds.add(dispatchBillItemDto);
				dispatchs.put(orderId,itemIds);
				orderNo.put(orderId,finishedStock.getOrderNo());
				finishedId.put(orderId,finishedStock.getId());
			}
		}
		//循环发货单集合
		for(Map.Entry<String,List<DispatchBillItemDto>> entry:dispatchs.entrySet()){
			//设计订单编号 发货单编号 等基础信息
			dispatchBillDto.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.DISPATCH_NO));
			dispatchBillDto.setOrderId(entry.getKey());
			dispatchBillDto.setOrderNo(orderNo.get(entry.getKey()));
			dispatchBillDto.setFinishedStockId(finishedId.get(entry.getKey()));
			this.dispatchBillService.add(dispatchBillDto);
			//循环发货条目 生成条目数据
			for(DispatchBillItemDto dispatchBillItemDto:entry.getValue()){
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
			//某一发货单发货完毕后 修改订单及 成品库单状态
			CustomOrder order = this.customOrderService.findById(entry.getKey());
			FinishedStock finishedStock = this.finishedStockService.findByOrderId(entry.getKey());
			//全部发货则判断发货条目总数是否等于成品库包数
			List<DispatchBillItem> dispatchBillItems = this.dispatchBillItemService.findListByOrderId(entry.getKey());
			if (dispatchBillItems.size() == finishedStock.getPackages()) {
				//全部配送修改订单状态为配送中
				MapContext updateOrder = new MapContext();
				updateOrder.put(WebConstant.KEY_ENTITY_ID, entry.getKey());
				updateOrder.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.DISPATCHING.getValue());
				this.customOrderService.updateByMapContext(updateOrder);
				//全部配送修改配送单状态为全部配送
				MapContext updateFinishedStock = new MapContext();
				updateFinishedStock.put(WebConstant.KEY_ENTITY_ID, finishedStock.getId());
				updateFinishedStock.put(WebConstant.KEY_ENTITY_STATUS, FinishedStockStatus.ALL_SHIPMENT.getValue());
				this.finishedStockService.updateByMapContext(updateFinishedStock);
			} else {
				//存在配送单为待配送,则判断订单状态是否为待配送 是则修改
				if (order.getStatus().equals(OrderStatus.TO_DISPATCH.getValue())) {
					MapContext updateOrder = new MapContext();
					updateOrder.put(WebConstant.KEY_ENTITY_ID, entry.getKey());
					updateOrder.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.DISPATCHING.getValue());
					this.customOrderService.updateByMapContext(updateOrder);
				}
				//存在配送单为待配送,则判断成品库单状态是否是待配送 是则修改
				if (finishedStock.getStatus().equals(FinishedStockStatus.UNSHIPPED.getValue())) {
					MapContext updateFinishedStock = new MapContext();
					updateFinishedStock.put(WebConstant.KEY_ENTITY_ID, finishedStock.getId());
					updateFinishedStock.put(WebConstant.KEY_ENTITY_STATUS, FinishedStockStatus.PARTIAL_SHIPMENT.getValue());
					this.finishedStockService.updateByMapContext(updateFinishedStock);
				}
			}
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
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

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deliverById(MapContext mapContext, String id) {
		DispatchBill dispatchBill = this.dispatchBillService.findById(id);
		//判断发货单是否存在, 以及状态是否是待取件
		if (dispatchBill == null || !dispatchBill.getStatus().equals(DispatchBillStatus.WAITING_FOR_PIECES.getValue())) {
			return ResultFactory.generateResNotFoundResult();
		}
		String logisticsCompanyId = mapContext.getTypedValue("logisticsCompanyId", String.class);
		//判断物流公司是否存在
		if (logisticsCompanyId == null || !this.logisticsCompanyService.isExist(logisticsCompanyId)) {
			return ResultFactory.generateResNotFoundResult();
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID, id);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS, DispatchBillStatus.TRANSPORT.getValue());//配送单状态
		mapContext.put("actualDate", DateUtil.getSystemDate());//配送日期
		this.dispatchBillService.updateByMapContext(mapContext);


		String orderId = this.finishedStockService.findById(dispatchBill.getFinishedStockId()).getOrderId();
		CustomOrder order = this.customOrderService.findById(orderId);
		FinishedStock finishedStock = this.finishedStockService.findById(dispatchBill.getFinishedStockId());
//		if(order.getStatus().equals(OrderStatus.TO_DISPATCH.getValue())){//如果订单的状态为待配送则修改为配送中
//			MapContext updateOrder = new MapContext();
//			updateOrder.put(WebConstant.KEY_ENTITY_ID,orderId);
//			updateOrder.put(WebConstant.KEY_ENTITY_STATUS,OrderStatus.DISPATCHING.getValue());
//			this.customOrderService.updateByMapContext(mapContext);
//		}


		//全部发货则判断发货条目总数是否等于成品库包数
		List<DispatchBillItem> dispatchBillItems = this.dispatchBillItemService.findListByOrderId(orderId);
		if (dispatchBillItems.size() == finishedStock.getPackages()) {
			//全部配送修改订单状态为已配送
			MapContext updateOrder = new MapContext();
			updateOrder.put(WebConstant.KEY_ENTITY_ID, orderId);
			updateOrder.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.DISPATCHING.getValue());
			this.customOrderService.updateByMapContext(updateOrder);
			//全部配送修改配送单状态为全部配送
			MapContext updateFinishedStock = new MapContext();
			updateFinishedStock.put(WebConstant.KEY_ENTITY_ID, finishedStock.getId());
			updateFinishedStock.put(WebConstant.KEY_ENTITY_STATUS, FinishedStockStatus.ALL_SHIPMENT.getValue());
			this.finishedStockService.updateByMapContext(updateFinishedStock);
		} else {
			//存在配送单为待配送,则判断订单状态是否为待配送 是则修改
			if (order.getStatus().equals(OrderStatus.TO_DISPATCH.getValue())) {
				MapContext updateOrder = new MapContext();
				updateOrder.put(WebConstant.KEY_ENTITY_ID, orderId);
				updateOrder.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.DISPATCHING.getValue());
				this.customOrderService.updateByMapContext(updateOrder);
			}
			//存在配送单为待配送,则判断成品库单状态是否是待配送 是则修改
			if (finishedStock.getStatus().equals(FinishedStockStatus.UNSHIPPED.getValue())) {
				MapContext updateFinishedStock = new MapContext();
				updateFinishedStock.put(WebConstant.KEY_ENTITY_ID, finishedStock.getId());
				updateFinishedStock.put(WebConstant.KEY_ENTITY_STATUS, FinishedStockStatus.PARTIAL_SHIPMENT.getValue());
				this.finishedStockService.updateByMapContext(updateFinishedStock);
			}
		}
		return ResultFactory.generateRequestResult(this.dispatchBillService.findById(id));
	}
}
