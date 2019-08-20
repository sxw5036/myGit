package com.lwxf.industry4.webapp.domain.dao.customorder.impl;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.lwxf.industry4.webapp.domain.dto.dealer.WxDealerInfoDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.OfferPrintTableDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.OrderPrintTableDto;
import com.lwxf.industry4.webapp.domain.dto.statement.WxFactoryStatementDto;
import org.springframework.stereotype.Repository;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;


import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDaoImpl;
import com.lwxf.industry4.webapp.domain.dao.customorder.CustomOrderDao;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.customorder.*;
import com.lwxf.industry4.webapp.domain.dto.dealer.DealerOrderRankDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Repository;

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
@Repository("customOrderDao")
public class CustomOrderDaoImpl extends BaseDaoImpl<CustomOrder, String> implements CustomOrderDao {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<CustomOrder> selectByFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilter");
		//
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<CustomOrder> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}
	@Override
	public PaginatedList<CustomOrderDto> selectByCondition(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByCondition");
		//
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<CustomOrderDto> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public CustomOrderDto findByOrderId(String orderId) {
		String sqlId = this.getNamedSqlId("findByOrderId");
		return this.getSqlSession().selectOne(sqlId,orderId);
	}

	@Override
	public PaginatedList<CustomOrderDto> findFinishedOrderList(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("findFinishedOrderList");
		//
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<CustomOrderDto> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public PaginatedList<OrderQuoteDto> findOrderQuoteMessage(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("findOrderQuoteMessage");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<OrderQuoteDto> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);

	}

	@Override
	public PaginatedList<CustomOrderDto> findListByPaginateFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("findListByPaginateFilter");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<CustomOrderDto> pageList =  (PageList)this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}
	@Override
	public PaginatedList<CustomOrderDto> findByCompanyIdAndStatus(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("findByCompanyIdAndStatus");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<CustomOrderDto> pageList =  (PageList)this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}


	@Override
	public List<CustomOrderDto> findByCompanyIdAndStatus(MapContext params) {
		String sqlId = this.getNamedSqlId("findByCompanyIdAndStatus");
		return this.getSqlSession().selectList(sqlId,params);
	}


	@Override
	public PaginatedList<Map> findSpecimenOrderList(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("findSpecimenOrderList");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<Map> pageList =  (PageList)this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}


	@Override
	public Integer findOrderCountByStatus(MapContext mapContext) {
		String sqlId = this.getNamedSqlId("findOrderCountByStatus");
		return this.getSqlSession().selectOne(sqlId,mapContext);
	}

	@Override
	public OrderCountDto findOrderNumByUidAndCid(MapContext mapContext) {
		String sqlId=this.getNamedSqlId("findOrderNumByUidAndCid");
		return this.getSqlSession().selectOne(sqlId,mapContext);
	}

	@Override
	public List<CustomOrder> findByCidAndSalemanId(MapContext mapContext) {
		String sqlId=this.getNamedSqlId("findByCidAndSalemanId");
		return this.getSqlSession().selectList(sqlId,mapContext);
	}

	@Override
	public Integer findOrderMoneyCount(MapContext mapContext) {
		String sqlId=this.getNamedSqlId("findOrderMoneyCount");
		return this.getSqlSession().selectOne(sqlId,mapContext);
	}

	@Override
	public BigDecimal findpayAmountByOrderId(String orderId) {
		String sqlId=this.getNamedSqlId("findpayAmountByOrderId");
		return this.getSqlSession().selectOne(sqlId,orderId);
	}

	@Override
	public List<DateNum> findOrderNumByCreatedAndCid(MapContext mapContext) {
		String sqlId=this.getNamedSqlId("findOrderNumByCreatedAndCid");
		return this.getSqlSession().selectList(sqlId,mapContext);
	}
	@Override
	public List<MapContext> findOrderListByCidAndUid(String userId, String dealerId) {
		String sqlId=this.getNamedSqlId("findOrderListByCidAndUid");
		MapContext mapContext=MapContext.newOne();
		mapContext.put("userId",userId);
		mapContext.put("companyId",dealerId);
		return this.getSqlSession().selectList(sqlId,mapContext);
	}


	@Override
	public Map findFAppBaseInfoByOrderId(MapContext params) {
		String sqlId = this.getNamedSqlId("findFAppBaseInfoByOrderId");
		return this.getSqlSession().selectOne(sqlId,params);
	}

	@Override
	public List<CustomOrder> findByIds(Set orderIds) {
		String sqlId = this.getNamedSqlId("findByIds");
		return this.getSqlSession().selectList(sqlId,orderIds);
	}

	@Override
	public int updateOrderStatusByIds(List startOrderIds, Integer status) {
		String sqlId = this.getNamedSqlId("updateOrderStatusByIds");
		MapContext mapContext = new MapContext();
		mapContext.put("ids",startOrderIds);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS,status);
		mapContext.put("documentaryTime",DateUtil.getSystemDate());
		return this.getSqlSession().update(sqlId,mapContext);
	}

	@Override
	public PaginatedList<CustomOrderDto> findPacksOrderList(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("findPacksOrderList");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<CustomOrderDto> pageList =  (PageList)this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public List<CustomOrder> findByCustomerIdAndCid(String uId, String branchId) {
		MapContext mapContext=MapContext.newOne();
		mapContext.put("uId",uId);
		mapContext.put("branchId",branchId);
		String sqlId=this.getNamedSqlId("findByCustomerIdAndCid");
		return this.getSqlSession().selectList(sqlId,mapContext);
	}

	@Override
	public List<CustomOrder> findByCustomerIdAndCidAndStatus(String uId, String branchId, Integer status) {
		MapContext mapContext=MapContext.newOne();
		mapContext.put("uId",uId);
		mapContext.put("branchId",branchId);
		mapContext.put("status",status);
		String sqlId=this.getNamedSqlId("findByCustomerIdAndCidAndStatus");
		return this.getSqlSession().selectList(sqlId,mapContext);
	}

	@Override
	public CustomOrder findByUidAndBranchId(MapContext mapContext) {
		String sqlId=this.getNamedSqlId("findByUidAndBranchId");
		return this.getSqlSession().selectOne(sqlId,mapContext);
	}

	@Override
	public CustomOrder findByCidAndBranchId(String dealerId, String branchId) {
		MapContext mapContext=MapContext.newOne();
		mapContext.put("cId",dealerId);
		mapContext.put("branchId",branchId);
		String sqlId=this.getNamedSqlId("findByCidAndBranchId");
		return this.getSqlSession().selectOne(sqlId,mapContext);
	}

	@Override
	public Integer findTodayOrderCount(MapContext param1) {

		String sqlId=this.getNamedSqlId("findTodayOrderCount");
		return this.getSqlSession().selectOne(sqlId,param1);
	}

	@Override
	public Integer findTodayInvalidOrder(MapContext param2) {
		String sqlId=this.getNamedSqlId("findTodayInvalidOrder");
		return this.getSqlSession().selectOne(sqlId,param2);
	}

	@Override
	public Integer findTodayEffectiveOrder(MapContext param2) {
		String sqlId=this.getNamedSqlId("findTodayEffectiveOrder");
		return this.getSqlSession().selectOne(sqlId,param2);
	}

	@Override
	public PaginatedList<WxCustomOrderDto> findWxOrderList(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("findWxOrderList");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<WxCustomOrderDto> pageList =  (PageList)this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public WxCustomerOrderInfoDto findWxOrderByorderId(String orderId) {
		String sqlId=this.getNamedSqlId("findWxOrderByorderId");
		return this.getSqlSession().selectOne(sqlId,orderId);
	}

	@Override
	public WxFactoryStatementDto statementWxFactory(String branchId) {
		String sqlId=this.getNamedSqlId("statementWxFactory");
		return this.getSqlSession().selectOne(sqlId,branchId);
	}

	@Override
	public WxDealerInfoDto selectDealerInfo(String companyId) {
		String sqlId=this.getNamedSqlId("selectDealerInfo");
		return this.getSqlSession().selectOne(sqlId,companyId);
	}

	@Override
	public OrderPrintTableDto findOrderPrintTable(MapContext mapContext) {
		String sqlId=this.getNamedSqlId("findOrderPrintTable");
		return this.getSqlSession().selectOne(sqlId,mapContext);
	}

	@Override
	public OfferPrintTableDto findOfferPrintTableInfo(String id) {
		String sqlId = this.getNamedSqlId("findOfferPrintTableInfo");
		return this.getSqlSession().selectOne(sqlId,id);
	}

	@Override
	public Integer findOverdueOrderCount(String branchId) {
		String sqlId = this.getNamedSqlId("findOverdueOrderCount");
		return this.getSqlSession().selectOne(sqlId,branchId);
	}


}