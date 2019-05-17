package com.lwxf.industry4.webapp.domain.dao.dispatch.impl;


import java.util.List;
import java.util.Map;

import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Repository;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDaoImpl;
import com.lwxf.industry4.webapp.domain.dao.dispatch.DispatchBillDao;
import com.lwxf.industry4.webapp.domain.dto.dispatch.DispatchBillDto;
import com.lwxf.industry4.webapp.domain.dto.dispatch.DispatchBillItemDto;
import com.lwxf.industry4.webapp.domain.dto.dispatch.DispatchBillPlanItemDto;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBill;


/**
 * 功能：
 * 
 * @author：panchenxiao(Mister_pan@126.com)
 * @created：2018-12-20 15:10:29
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Repository("dispatchBillDao")
public class DispatchBillDaoImpl extends BaseDaoImpl<DispatchBill, String> implements DispatchBillDao {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<DispatchBillDto> selectByFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilter");
		//
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<DispatchBillDto> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public List<DispatchBillDto> findDispatchsByOrderId(String orderId) {
		String sqlId = this.getNamedSqlId("findDispatchsByOrderId");
		return this.getSqlSession().selectList(sqlId,orderId);
	}

	@Override
	public List<DispatchBillItemDto> findListByDispatchId(String id) {
		String sqlId = this.getNamedSqlId("findListByDispatchId");
		return this.getSqlSession().selectList(sqlId,id);
	}

	@Override
	public DispatchBill findOneByNo(String no) {
		String sqlId = this.getNamedSqlId("findOneByNo");
		return this.getSqlSession().selectOne(sqlId,no);
	}

	@Override
	public List<DispatchBillItemDto> findItemListById(String id) {
		String sqlId = this.getNamedSqlId("findItemListById");
		return this.getSqlSession().selectList(sqlId,id);
	}

	@Override
	public DispatchBillDto findDispatchsBillById(String dispatchId) {
		String sqlId = this.getNamedSqlId("findDispatchsBillById");
		return this.getSqlSession().selectOne(sqlId,dispatchId);
	}

	@Override
	public int findYSHItemCount(String orderId) {
		String sqlId = this.getNamedSqlId("findYSHItemCount");
		return this.getSqlSession().selectOne(sqlId,orderId);
	}

	@Override
	public String findTimeByOrderId(String orderId) {
		String sqlId = this.getNamedSqlId("findTimeByOrderId");
		return this.getSqlSession().selectOne(sqlId,orderId);
	}

	@Override
	public Integer findTodayCount() {
		String sqlId = this.getNamedSqlId("findTodayCount");
		return this.getSqlSession().selectOne(sqlId);
	}


	@Override
	public Integer findThisMonthCount() {
		String sqlId = this.getNamedSqlId("findThisMonthCount");
		return this.getSqlSession().selectOne(sqlId);
	}

	@Override
	public PaginatedList<DispatchBillPlanItemDto> findDispathcBillList(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("findDispathcBillList");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<DispatchBillPlanItemDto> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public Integer findTobeShipped() {
		String sqlId = this.getNamedSqlId("findTobeShipped");
		return this.getSqlSession().selectOne(sqlId);
	}

	@Override
	public List<DispatchBill> findDispatchListByOrderId(String orderId) {
		String sqlId = this.getNamedSqlId("findDispatchListByOrderId");
		return this.getSqlSession().selectList(sqlId,orderId);
	}

	@Override
	public List<Map> findFactoryDispatchsByOrderId(String orderId) {
		String sqlId = this.getNamedSqlId("findFactoryDispatchsByOrderId");
		return this.getSqlSession().selectList(sqlId,orderId);
	}

	@Override
	public List<Map> findDispatchList(String resultOrderId) {
		String sqlId = this.getNamedSqlId("findDispatchList");
		return this.getSqlSession().selectList(sqlId,resultOrderId);
	}

	@Override
	public Integer findNumByCreated(MapContext params) {
		String sqlId = this.getNamedSqlId("findNumByCreated");
		return this.getSqlSession().selectOne(sqlId,params);
	}
}