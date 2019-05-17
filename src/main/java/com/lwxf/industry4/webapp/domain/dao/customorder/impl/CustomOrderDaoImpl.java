package com.lwxf.industry4.webapp.domain.dao.customorder.impl;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderCountDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderQuoteDto;
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
	public List<CustomOrder> findOrderNumByCustomIdAndCid(MapContext mapContext) {
		String sqlId=this.getNamedSqlId("findOrderNumByCustomIdAndCid");
		return this.getSqlSession().selectList(sqlId,mapContext);
	}


	@Override
	public PaginatedList<Map> findByCreated(PaginatedFilter filter) {
		String sqlId = this.getNamedSqlId("findByCreated");
		PageBounds pageBounds = this.toPageBounds(filter.getPagination(), filter.getSorts());
		PageList<Map> pageList =  (PageList)this.getSqlSession().selectList(sqlId, filter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public int findOrderNumByCreated(String created) {
		String sqlId = this.getNamedSqlId("findOrderNumByCreated");
		return this.getSqlSession().selectOne(sqlId,created);
	}

	@Override
	public Map findOrderInfoByOrderId(String orderId) {
		String sqlId = this.getNamedSqlId("findOrderInfoByOrderId");
		return this.getSqlSession().selectOne(sqlId,orderId);
	}

	@Override
	public int findPaidOrderNumByCreated(String created) {
		String sqlId = this.getNamedSqlId("findPaidOrderNumByCreated");
		return this.getSqlSession().selectOne(sqlId,created);
	}

	@Override
	public int findPaidOrderNumByTime(String beginTime,String endTime,String created) {
		MapContext params = MapContext.newOne();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		params.put("created", created);
		String sqlId = this.getNamedSqlId("findPaidOrderNumByTime");
		return this.getSqlSession().selectOne(sqlId,params);
	}

	@Override
	public int findUnpaidOrderNumByTime(String beginTime,String endTime,String created) {
		MapContext params = MapContext.newOne();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		params.put("created", created);
		String sqlId = this.getNamedSqlId("findUnpaidOrderNumByTime");
		return this.getSqlSession().selectOne(sqlId,params);
	}

	@Override
	public int findDesignNumByCreated(String created) {
		String sqlId = this.getNamedSqlId("findDesignNumByCreated");
		return this.getSqlSession().selectOne(sqlId,created);
	}

	@Override
	public PaginatedList<Map> findByConditions(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("findByConditions");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<Map> pageList =  (PageList)this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public Map findFactoryOrderInfoById(String orderId) {
		String sqlId = this.getNamedSqlId("findFactoryOrderInfoById");
		return this.getSqlSession().selectOne(sqlId,orderId);
	}

	@Override
	public int findIsDesignNumByTime(MapContext params) {
		String sqlId = this.getNamedSqlId("findIsDesignNumByTime");
		return this.getSqlSession().selectOne(sqlId,params);
	}



	@Override
	public List<CustomOrder> findOrderListByStatusList(List<Integer> statusList) {
		String sqlId=this.getNamedSqlId("findOrderListByStatusList");
		return this.getSqlSession().selectList(sqlId,statusList);
	}

	@Override
	public List<MapContext> findCustomerOrderInfo(String userId, String companyId) {
		String sqlId=this.getNamedSqlId("findCustomerOrderInfo");
		MapContext mapContext=MapContext.newOne();
		mapContext.put("customerId",userId);
		mapContext.put("companyId",companyId);
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
	public PaginatedList<Map> findFProcessOrderList(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("findFProcessOrderList");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<Map> pageList =  (PageList)this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
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
	public Map findShipmentsInfoByOrderId(String orderId) {
		String sqlId = this.getNamedSqlId("findShipmentsInfoByOrderId");
		return this.getSqlSession().selectOne(sqlId,orderId);
	}

	@Override
	public PaginatedList<Map> findPaidOrderListByTime(PaginatedFilter filter) {
		String sqlId = this.getNamedSqlId("findPaidOrderListByTime");
		PageBounds pageBounds = this.toPageBounds(filter.getPagination(), filter.getSorts());
		PageList<Map> pageList =  (PageList)this.getSqlSession().selectList(sqlId, filter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public PaginatedList<Map> findUnpaidOrderListByTime(PaginatedFilter filter) {
		String sqlId = this.getNamedSqlId("findUnpaidOrderListByTime");
		PageBounds pageBounds = this.toPageBounds(filter.getPagination(), filter.getSorts());
		PageList<Map> pageList =  (PageList)this.getSqlSession().selectList(sqlId, filter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public PaginatedList<Map> findIsDesignListByTime(PaginatedFilter filter) {
		String sqlId = this.getNamedSqlId("findIsDesignListByTime");
		PageBounds pageBounds = this.toPageBounds(filter.getPagination(), filter.getSorts());
		PageList<Map> pageList =  (PageList)this.getSqlSession().selectList(sqlId, filter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public List<MapContext> findByCid(String dealerId) {
		String sqlId = this.getNamedSqlId("findByCid");
		return this.getSqlSession().selectList(sqlId,dealerId);
	}

	@Override
	public Integer getAllByCreated(String beginTime, String endTime, String todayDate) {
		MapContext params = MapContext.newOne();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		params.put("created", todayDate);
		String sqlId = this.getNamedSqlId("getAllByCreated");
		return this.getSqlSession().selectOne(sqlId,params);
	}

	@Override
	public List<CustomOrder> getAllOrderByCreated(String beginTime, String endTime, String todayDate) {
		MapContext params = MapContext.newOne();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		params.put("created", todayDate);
		String sqlId = this.getNamedSqlId("getAllOrderByCreated");
		return this.getSqlSession().selectList(sqlId,params);
	}

	@Override
	public BigDecimal findPaidOrderAmountByTime(String beginTime, String endTime, String day) {
		MapContext params = MapContext.newOne();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		params.put("created", day);
		String sqlId = this.getNamedSqlId("findPaidOrderAmountByTime");
		return this.getSqlSession().selectOne(sqlId,params);
	}
}