package com.lwxf.industry4.webapp.bizservice.customorder.impl;


import com.lwxf.industry4.webapp.bizservice.base.BaseServiceImpl;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dao.customorder.CustomOrderDao;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderCountDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderQuoteDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 功能：
 * 
 * @author：panchenxiao(Mister_pan@126.com)
 * @created：2018-12-04 17:48:28
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Service("customOrderService")
public class CustomOrderServiceImpl extends BaseServiceImpl<CustomOrder, String, CustomOrderDao> implements CustomOrderService {


	@Resource

	@Override	public void setDao( CustomOrderDao customOrderDao) {
		this.dao = customOrderDao;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<CustomOrder> selectByFilter(PaginatedFilter paginatedFilter) {
		//
		return this.dao.selectByFilter(paginatedFilter) ;
	}

	@Override
	public PaginatedList<CustomOrderDto> selectByCondition(PaginatedFilter paginatedFilter) {
		return this.dao.selectByCondition(paginatedFilter);
	}

	@Override
	public CustomOrderDto findByOrderId(String orderId) {
		return this.dao.findByOrderId(orderId);
	}

	@Override
	public PaginatedList<CustomOrderDto> findFinishedOrderList(PaginatedFilter paginatedFilter) {
		return this.dao.findFinishedOrderList(paginatedFilter);
	}

	@Override
	public PaginatedList<OrderQuoteDto> findOrderQuoteMessage(PaginatedFilter paginatedFilter) {
		return this.dao.findOrderQuoteMessage(paginatedFilter);
	}

	@Override
	public PaginatedList<CustomOrderDto> findListByPaginateFilter(PaginatedFilter paginatedFilter) {
		return this.dao.findListByPaginateFilter(paginatedFilter);
	}
	@Override
	public PaginatedList<CustomOrderDto> findByCompanyIdAndStatus(PaginatedFilter paginatedFilter) {
		return this.dao.findByCompanyIdAndStatus(paginatedFilter);
	}

	@Override
	public Integer findOrderCountByStatus(MapContext mapContext) {
		return this.dao.findOrderCountByStatus(mapContext);
	}

	@Override
	public OrderCountDto findOrderNumByUidAndCid(MapContext mapContext) {
		return this.dao.findOrderNumByUidAndCid(mapContext);
	}

	@Override
	public List<CustomOrder> findByCidAndSalemanId(MapContext mapContext) {

		return this.dao.findByCidAndSalemanId(mapContext);
	}

	@Override
	public Integer findOrderMoneyCount(MapContext mapContext) {
		return this.dao.findOrderMoneyCount(mapContext);
	}

	@Override
	public BigDecimal findpayAmountByOrderId(String orderId) {
		return this.dao.findpayAmountByOrderId(orderId);
	}

	@Override
	public List<DateNum> findOrderNumByCreatedAndCid(MapContext mapContext) {
		return this.dao.findOrderNumByCreatedAndCid(mapContext);
	}

	@Override
	public List<CustomOrder> findOrderNumByCustomIdAndCid(MapContext mapContext) {
		return this.dao.findOrderNumByCustomIdAndCid(mapContext);
	}


	@Override
	public List<CustomOrderDto> findByCompanyIdAndStatus(MapContext params) {
		return this.dao.findByCompanyIdAndStatus(params);
	}

	@Override
	public PaginatedList<Map> findSpecimenOrderList(PaginatedFilter paginatedFilter) {
		return this.dao.findSpecimenOrderList(paginatedFilter);
	}


	@Override
	public PaginatedList<Map> findByCreated(PaginatedFilter filter) {
		return this.dao.findByCreated(filter);
	}

	@Override
	public int findOrderNumByCreated(String created) {
		return this.dao.findOrderNumByCreated(created);
	}

	@Override
	public Map findOrderInfoByOrderId(String orderId) {
		return this.dao.findOrderInfoByOrderId(orderId);
	}

	@Override
	public int findPaidOrderNumByCreated(String created) {
		return this.dao.findPaidOrderNumByCreated(created);
	}

	@Override
	public int findPaidOrderNumByTime(String beginTime,String endTime,String created) {
		return this.dao.findPaidOrderNumByTime(beginTime,endTime,created);
	}

	@Override
	public int findUnpaidOrderNumByTime(String beginTime,String endTime,String created) {
		return this.dao.findUnpaidOrderNumByTime(beginTime,endTime,created);
	}

	@Override
	public int findDesignNumByCreated(String created) {
		return this.dao.findDesignNumByCreated(created);
	}

	@Override
	public PaginatedList<Map> findByConditions(PaginatedFilter paginatedFilter) {
		return this.dao.findByConditions(paginatedFilter);
	}

	@Override
	public Map findFactoryOrderInfoById(String orderId) {
		return this.dao.findFactoryOrderInfoById(orderId);
	}

	@Override
	public int findIsDesignNumByTime(MapContext params) {
		return this.dao.findIsDesignNumByTime(params);
	}


	@Override
	public List<CustomOrder> findOrderListByStatusList(List<Integer> statusList) {
		return this.dao.findOrderListByStatusList(statusList);
	}

	@Override
	public List<MapContext> findCustomerOrderInfo(String userId, String companyId) {
		return this.dao.findCustomerOrderInfo(userId,companyId);
	}

	@Override
	public List<MapContext> findOrderListByCidAndUid(String dealerId, String userId) {
		return this.dao.findOrderListByCidAndUid(userId,dealerId);
	}

	@Override
	public PaginatedList<Map> findFProcessOrderList(PaginatedFilter paginatedFilter) {
		return this.dao.findFProcessOrderList(paginatedFilter);
	}

	@Override
	public Map findFAppBaseInfoByOrderId(MapContext params) {
		return this.dao.findFAppBaseInfoByOrderId(params);
	}

	@Override
	public List<CustomOrder> findByIds(Set orderIds) {
		return this.dao.findByIds(orderIds);
	}

	@Override
	public int updateOrderStatusByIds(List startOrderIds, Integer status) {
		return this.dao.updateOrderStatusByIds(startOrderIds,status);
	}

	@Override
	public PaginatedList<CustomOrderDto> findPacksOrderList(PaginatedFilter paginatedFilter) {
		return this.dao.findPacksOrderList(paginatedFilter);
	}

	@Override
	public Map findShipmentsInfoByOrderId(String orderId) {
		return this.dao.findShipmentsInfoByOrderId(orderId);
	}




	@Override
	public PaginatedList<Map> findPaidOrderListByTime(PaginatedFilter filter) {
		return this.dao.findPaidOrderListByTime(filter);
	}

	@Override
	public PaginatedList<Map> findUnpaidOrderListByTime(PaginatedFilter filter) {
		return this.dao.findUnpaidOrderListByTime(filter);
	}

	@Override
	public PaginatedList<Map> findIsDesignListByTime(PaginatedFilter filter) {
		return this.dao.findIsDesignListByTime(filter);
	}

	@Override
	public List<MapContext> findByCid(String dealerId) {
		return this.dao.findByCid(dealerId);
	}

	@Override
	public Integer getAllByCreated(String beginTime, String endTime, String todayDate) {
		return this.dao.getAllByCreated(beginTime,endTime,todayDate);
	}

	@Override
	public List<CustomOrder> getAllOrderByCreated(String beginTime, String endTime, String todayDate) {
		return this.dao.getAllOrderByCreated(beginTime,endTime,todayDate);
	}

	@Override
	public BigDecimal findPaidOrderAmountByTime(String beginTime, String endTime, String day) {
		return this.dao.findPaidOrderAmountByTime(beginTime,endTime,day);
	}
}