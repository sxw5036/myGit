package com.lwxf.industry4.webapp.facade.admin.factory.order.impl;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.customorder.OrderAccountLogService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.customorder.AccountLogClassIfcation;
import com.lwxf.industry4.webapp.common.enums.order.OrderAccountLogType;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.customorder.OrderAccountLog;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.order.OrderAccountLogFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2019/3/5/005 15:26
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("orderAccountLogFacade")
public class OrderAccountLogFacadeImpl extends BaseFacadeImpl implements OrderAccountLogFacade {

	@Resource(name = "orderAccountLogService")
	private OrderAccountLogService orderAccountLogService;
	@Resource(name = "customOrderService")
	private CustomOrderService customOrderService;

	@Override
	public RequestResult findOrderList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		paginatedFilter.setFilters(mapContext);
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		List<Map<String,String>> sorts = new ArrayList<Map<String, String>>();
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sorts.add(created);
		paginatedFilter.setSorts(sorts);
		return ResultFactory.generateRequestResult(this.customOrderService.findListByPaginateFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateOrderAccount(String id,Integer type,OrderAccountLog orderAccountLog) {
		//判断订单是否存在
		CustomOrder customOrder = this.customOrderService.findById(id);
		if(customOrder==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//根据订单状态进行不同判断
		Integer status = customOrder.getStatus();
		MapContext updateOrder = new MapContext();
		//如果是修改设计费 则判断订单状态是否是 设计费待评估 或者 设计费待确认
		if(type==OrderAccountLogType.DESIGN.getValue()){
			if(!status.equals(OrderStatus.TO_ADD_DESIGNFEE.getValue())&&!status.equals(OrderStatus.TO_BE_CONFIRMED_DESIGNFEE.getValue())){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
			}
			updateOrder.put("designFee",orderAccountLog.getPresentPrice());
		}else{
			//如果是修改最终货款 则判断订单状态是否是 出厂价待确认 或者 经销商待确认出厂价
			if(!status.equals(OrderStatus.FACTORY_CONFIRMED_FPROCE.getValue())&&!status.equals(OrderStatus.DEALER_CONFIRMED_FPRICE.getValue())){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
			}
			updateOrder.put("factoryFinalPrice",orderAccountLog.getPresentPrice());
		}
		updateOrder.put(WebConstant.KEY_ENTITY_ID,id);
		//生成报价修改日志信息
		orderAccountLog.setClassification(AccountLogClassIfcation.F_B.getValue());
		orderAccountLog.setCreated(DateUtil.getSystemDate());
		orderAccountLog.setCreator(WebUtils.getCurrUserId());
		orderAccountLog.setOrderId(id);
		if(type==OrderAccountLogType.DESIGN.getValue()){
			orderAccountLog.setOriginalPrice(new BigDecimal(customOrder.getDesignFee()));
			orderAccountLog.setType(type);
		}else{
			orderAccountLog.setOriginalPrice(customOrder.getAmount());
			orderAccountLog.setType(type);
		}
		orderAccountLog.setPreferentialPrice(orderAccountLog.getOriginalPrice().subtract(orderAccountLog.getPresentPrice()));
		//判断是否价格长度过长 价格是否为负
		RequestResult result = orderAccountLog.validateFields();
		if(result!=null){
			return result;
		}
		//修改订单信息
		this.customOrderService.updateByMapContext(updateOrder);
		//生成报价修改记录
		this.orderAccountLogService.add(orderAccountLog);
		return ResultFactory.generateRequestResult(this.customOrderService.findByOrderId(id));
	}
}
