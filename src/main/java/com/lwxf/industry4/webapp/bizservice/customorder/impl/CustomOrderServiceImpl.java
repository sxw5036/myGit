package com.lwxf.industry4.webapp.bizservice.customorder.impl;


import com.lwxf.industry4.webapp.bizservice.base.BaseServiceImpl;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dao.customorder.CustomOrderDao;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.customorder.*;
import com.lwxf.industry4.webapp.domain.dto.dealer.DealerOrderRankDto;
import com.lwxf.industry4.webapp.domain.dto.dealer.WxDealerInfoDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.OfferPrintTableDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.OrderPrintTableDto;
import com.lwxf.industry4.webapp.domain.dto.statement.WxFactoryStatementDto;
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
	public List<CustomOrderDto> findByCompanyIdAndStatus(MapContext params) {
		return this.dao.findByCompanyIdAndStatus(params);
	}

	@Override
	public PaginatedList<Map> findSpecimenOrderList(PaginatedFilter paginatedFilter) {
		return this.dao.findSpecimenOrderList(paginatedFilter);
	}




	@Override
	public List<MapContext> findOrderListByCidAndUid(String dealerId, String userId) {
		return this.dao.findOrderListByCidAndUid(userId,dealerId);
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
	public List<CustomOrder> findByCustomerIdAndCid(String uId, String branchId) {
		return this.dao.findByCustomerIdAndCid(uId,branchId);
	}

	@Override
	public List<CustomOrder> findByCustomerIdAndCidAndStatus(String uId, String branchId, Integer status) {
		return this.dao.findByCustomerIdAndCidAndStatus(uId,branchId,status);
	}

	@Override
	public CustomOrder findByUidAndBranchId(MapContext mapContext) {
		return this.dao.findByUidAndBranchId(mapContext);
	}

	@Override
	public CustomOrder findByCidAndBranchId(String dealerId, String branchId) {
		return this.dao.findByCidAndBranchId(dealerId,branchId);
	}

	@Override
	public Integer findTodayOrderCount(MapContext param1) {
		return this.dao.findTodayOrderCount(param1);
	}

	@Override
	public Integer findTodayInvalidOrder(MapContext param2) {
		return this.dao.findTodayInvalidOrder(param2);
	}

	@Override
	public Integer findTodayEffectiveOrder(MapContext param2) {
		return this.dao.findTodayEffectiveOrder(param2);
	}

	@Override
	public PaginatedList<WxCustomOrderDto> findWxOrderList(PaginatedFilter paginatedFilter) {
		return this.dao.findWxOrderList(paginatedFilter);
	}

	@Override
	public WxCustomerOrderInfoDto findWxOrderByorderId(String orderId) {
		return this.dao.findWxOrderByorderId(orderId);
	}

	@Override
	public WxFactoryStatementDto statementWxFactory(String branchId) {
		return this.dao.statementWxFactory(branchId);
	}

	@Override
	public WxDealerInfoDto selectDealerInfo(String companyId) {
		return this.dao.selectDealerInfo(companyId);
	}

	@Override
	public OrderPrintTableDto findOrderPrintTable(MapContext mapContext) {
		return this.dao.findOrderPrintTable(mapContext);
	}

	@Override
	public OfferPrintTableDto findOfferPrintTableInfo(String id) {
		return this.dao.findOfferPrintTableInfo(id);
	}

	@Override
	public Integer findOverdueOrderCount(String currBranchId) {
		return this.dao.findOverdueOrderCount(currBranchId);
	}


}