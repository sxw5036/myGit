package com.lwxf.industry4.webapp.domain.dao.customorder.impl;


import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Repository;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDemandDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderProductDto;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDaoImpl;
import com.lwxf.industry4.webapp.domain.dao.customorder.OrderProductDao;
import com.lwxf.industry4.webapp.domain.entity.customorder.OrderProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 功能：
 * 
 * @author：F_baisi(F_baisi@126.com)
 * @created：2019-04-11 17:38:26
 * @version：2019 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Repository("orderProductDao")
public class OrderProductDaoImpl extends BaseDaoImpl<OrderProduct, String> implements OrderProductDao {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<OrderProduct> selectByFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilter");
		//
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<OrderProduct> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public List<Map> findByOrderId(String orderId) {
		String sqlId = this.getNamedSqlId("findByOrderId");
		return this.getSqlSession().selectList(sqlId,orderId);
	}

	@Override
	public OrderProductDto findOneById(String id) {
		String sqlId = this.getNamedSqlId("findOneById");
		return this.getSqlSession().selectOne(sqlId,id);
	}

	@Override
	public List<OrderProductDto> findListByOrderId(String id) {
		String sqlId = this.getNamedSqlId("findListByOrderId");
		return this.getSqlSession().selectList(sqlId,id);
	}

	@Override
	public BigDecimal findCountPriceByOrderId(String id) {
		String sqlId = this.getNamedSqlId("findCountPriceByOrderId");
		return this.getSqlSession().selectOne(sqlId,id);
	}

	@Override
	public BigDecimal findCountPriceByCreatedAndStatus(MapContext params) {
		String sqlId = this.getNamedSqlId("findCountPriceByCreatedAndStatus");
		return this.getSqlSession().selectOne(sqlId,params);
	}

	@Override
	public Integer findCountNumByCreatedAndStatus(MapContext params) {
		String sqlId = this.getNamedSqlId("findCountNumByCreatedAndStatus");
		return this.getSqlSession().selectOne(sqlId,params);
	}

	@Override
	public int deleteByOrderId(String orderId) {
		String sqlId = this.getNamedSqlId("deleteByOrderId");
		return this.getSqlSession().delete(sqlId,orderId);
	}

	@Override
	public List<OrderProductDto> findProductsByOrderId(String id) {
		String sqlId = this.getNamedSqlId("findProductsByOrderId");
		return this.getSqlSession().selectList(sqlId,id);
	}

	@Override
	public List<OrderProductDto> findListByAftersaleId(String id) {
		String sqlId = this.getNamedSqlId("findListByAftersaleId");
		return this.getSqlSession().selectList(sqlId,id);
	}


	@Override
	public Integer findCountNumByCreatedAndType(MapContext params) {
		String sqlId = this.getNamedSqlId("findCountNumByCreatedAndType");
		return this.getSqlSession().selectOne(sqlId,params);
	}




}