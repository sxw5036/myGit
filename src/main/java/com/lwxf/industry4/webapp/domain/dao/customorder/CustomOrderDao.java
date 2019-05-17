package com.lwxf.industry4.webapp.domain.dao.customorder;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderCountDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderQuoteDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.mybatis.utils.MapContext;

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
@IBatisSqlTarget
public interface CustomOrderDao extends BaseDao<CustomOrder, String> {

	PaginatedList<CustomOrder> selectByFilter(PaginatedFilter paginatedFilter);
	PaginatedList<CustomOrderDto> selectByCondition(PaginatedFilter paginatedFilter);
	CustomOrderDto findByOrderId (String orderId);

	PaginatedList<CustomOrderDto> findFinishedOrderList(PaginatedFilter paginatedFilter);


	PaginatedList<OrderQuoteDto> findOrderQuoteMessage(PaginatedFilter paginatedFilter);

	PaginatedList<CustomOrderDto> findListByPaginateFilter(PaginatedFilter paginatedFilter);

	PaginatedList<CustomOrderDto> findByCompanyIdAndStatus(PaginatedFilter paginatedFilter);

	List<CustomOrderDto>  findByCompanyIdAndStatus(MapContext params);

	PaginatedList<Map>  findSpecimenOrderList(PaginatedFilter paginatedFilter);


	Integer findOrderCountByStatus(MapContext mapContext);

	OrderCountDto findOrderNumByUidAndCid(MapContext mapContext);

	List<CustomOrder> findByCidAndSalemanId(MapContext mapContext);

	Integer findOrderMoneyCount(MapContext mapContext);

	BigDecimal findpayAmountByOrderId(String orderId);

	List<DateNum> findOrderNumByCreatedAndCid(MapContext mapContext);

	List<CustomOrder> findOrderNumByCustomIdAndCid(MapContext mapContext);

	PaginatedList<Map> findByCreated(PaginatedFilter filter);

	int findOrderNumByCreated(String created);

	Map findOrderInfoByOrderId(String orderId);

    int findPaidOrderNumByCreated(String created);

	int findPaidOrderNumByTime(String beginTime,String endTime,String created);

	int findUnpaidOrderNumByTime(String beginTime,String endTime,String created);

	int findDesignNumByCreated(String created);

	PaginatedList<Map> findByConditions(PaginatedFilter paginatedFilter);

    Map findFactoryOrderInfoById(String orderId);

    int findIsDesignNumByTime(MapContext params);

	List<CustomOrder> findOrderListByStatusList(List<Integer> statusList);

	List<MapContext> findCustomerOrderInfo(String userId, String companyId);

	List<MapContext> findOrderListByCidAndUid(String userId, String dealerId);

    PaginatedList<Map> findFProcessOrderList(PaginatedFilter paginatedFilter);

	Map findFAppBaseInfoByOrderId(MapContext params);

	List<CustomOrder> findByIds(Set orderIds);

	int updateOrderStatusByIds(List startOrderIds, Integer status);

	PaginatedList<CustomOrderDto> findPacksOrderList(PaginatedFilter paginatedFilter);

	Map findShipmentsInfoByOrderId(String orderId);


    PaginatedList<Map> findPaidOrderListByTime(PaginatedFilter filter);

	PaginatedList<Map> findUnpaidOrderListByTime(PaginatedFilter filter);

	PaginatedList<Map> findIsDesignListByTime(PaginatedFilter filter);

	List<MapContext> findByCid(String dealerId);

    Integer getAllByCreated(String beginTime, String endTime, String todayDate);

	List<CustomOrder> getAllOrderByCreated (String beginTime, String endTime, String todayDate);

	BigDecimal findPaidOrderAmountByTime(String beginTime, String endTime, String day);

}