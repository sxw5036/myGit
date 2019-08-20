package com.lwxf.industry4.webapp.domain.dao.customorder;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.customorder.*;
import com.lwxf.industry4.webapp.domain.dto.dealer.DealerOrderRankDto;
import com.lwxf.industry4.webapp.domain.dto.dealer.WxDealerInfoDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.OfferPrintTableDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.OrderPrintTableDto;
import com.lwxf.industry4.webapp.domain.dto.statement.WxFactoryStatementDto;
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

	List<MapContext> findOrderListByCidAndUid(String userId, String dealerId);

	Map findFAppBaseInfoByOrderId(MapContext params);

	List<CustomOrder> findByIds(Set orderIds);

	int updateOrderStatusByIds(List startOrderIds, Integer status);

	PaginatedList<CustomOrderDto> findPacksOrderList(PaginatedFilter paginatedFilter);

	List<CustomOrder> findByCustomerIdAndCid(String uId, String branchId);

	List<CustomOrder> findByCustomerIdAndCidAndStatus(String uId, String branchId, Integer status);

	CustomOrder findByUidAndBranchId(MapContext mapContext);

	CustomOrder findByCidAndBranchId(String dealerId, String branchId);

	Integer findTodayOrderCount(MapContext param1);

	Integer findTodayInvalidOrder(MapContext param2);

	Integer findTodayEffectiveOrder(MapContext param2);

	PaginatedList<WxCustomOrderDto> findWxOrderList(PaginatedFilter paginatedFilter);

	WxCustomerOrderInfoDto findWxOrderByorderId(String orderId);

	//微信工厂端报表
	WxFactoryStatementDto statementWxFactory(String branchId);
	//微信经销商端首页
	WxDealerInfoDto selectDealerInfo(String companyId);


	OrderPrintTableDto findOrderPrintTable(MapContext mapContext);

	OfferPrintTableDto findOfferPrintTableInfo(String id);

	Integer findOverdueOrderCount(String branchId);
}