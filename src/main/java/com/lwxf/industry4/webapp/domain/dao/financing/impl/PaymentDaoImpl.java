package com.lwxf.industry4.webapp.domain.dao.financing.impl;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentDto;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentDtoForApp;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDaoImpl;
import com.lwxf.industry4.webapp.domain.dao.financing.PaymentDao;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;


/**
 * 功能：
 * 
 * @author：panchenxiao(Mister_pan@126.com)
 * @created：2018-12-18 20:13:58
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Repository("paymentDao")
public class PaymentDaoImpl extends BaseDaoImpl<Payment, String> implements PaymentDao {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<PaymentDto> selectByFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilter");
		//
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<PaymentDto> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public List<PaymentDto> findByOrderId(String orderId) {
		String sqlId = this.getNamedSqlId("findByOrderId");
		return this.getSqlSession().selectList(sqlId,orderId);
	}

	@Override
	public PaymentDto findByPaymentId(String paymentId) {
		String sqlId = this.getNamedSqlId("findByPaymentId");
		return this.getSqlSession().selectOne(sqlId,paymentId);
	}
	@Override
	public PaymentDto findByPId(String paymentId) {
		String sqlId = this.getNamedSqlId("findByPId");
		return this.getSqlSession().selectOne(sqlId,paymentId);
	}

	@Override
	public List<Payment> findByCompanyIdAndStatusAndType(MapContext params) {
		String sqlId = this.getNamedSqlId("findByCompanyIdAndStatusAndType");
		return this.getSqlSession().selectList(sqlId,params);
	}

	@Override
	public PaginatedList<Payment> findByCompanyIdAndStatusAndType(PaginatedFilter paginatedFilter){
		String sqlId = this.getNamedSqlId("findByCompanyIdAndStatusAndType");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<Payment> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}


	@Override
	public List<Payment> findByOrderIdAndStatus(MapContext params) {
		String sqlId = this.getNamedSqlId("findByOrderIdAndStatus");
		return this.getSqlSession().selectList(sqlId,params);
	}

	@Override
	public int updateStatusByOrderIdAndFund(String orderId, Integer funds, Integer status) {
		String sqlId = this.getNamedSqlId("updateStatusByOrderIdAndFund");
		MapContext mapContext = new MapContext();
		mapContext.put("orderId",orderId);
		mapContext.put("funds",funds);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS,status);
		mapContext.put("auditor",WebUtils.getCurrUserId());
		mapContext.put("audited",DateUtil.getSystemDate());
		mapContext.put("payTime",DateUtil.getSystemDate());
		return this.getSqlSession().update(sqlId,mapContext);
	}

	@Override
	public PaginatedList<PaymentDto> findListByPaginateFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("findListByPaginateFilter");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<PaymentDto> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public PaymentDto findByOrderIdAndTypeAndFundsAndStatus(MapContext orderIdAndType) {
		String sqlId = this.getNamedSqlId("findByOrderIdAndTypeAndFundsAndStatus");
		return this.getSqlSession().selectOne(sqlId,orderIdAndType);
	}

	@Override
	public PaginatedList<PaymentDtoForApp> selectByFilterForApp(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilterForApp");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<PaymentDtoForApp> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public PaginatedList<PaymentDtoForApp> selectPaymentByCompanyIdForApp(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectPaymentByCompanyIdForApp");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<PaymentDtoForApp> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public FinanceCountDto selectFinanceCountForApp() {
		String sqlId = this.getNamedSqlId("selectFinanceCountForApp");
		return this.getSqlSession().selectOne(sqlId);
	}

	@Override
	public List<VerifyPaymentDto> selectVerifyPaymentList() {
		String sqlId = this.getNamedSqlId("selectVerifyPaymentList");
		return this.getSqlSession().selectList(sqlId);
	}

	@Override
	public VerifyOrderPriceDto selectVerifyOrderPriceInfo(String paymentId) {
		String sqlId = this.getNamedSqlId("selectVerifyOrderPriceInfo");
		return this.getSqlSession().selectOne(sqlId,paymentId);
	}


	@Override
	public VerifyDesignPriceDto selectVerifyDesignPriceInfo(String paymentId) {
		String sqlId = this.getNamedSqlId("selectVerifyDesignPriceInfo");
		return this.getSqlSession().selectOne(sqlId,paymentId);
	}

	@Override
	public int verifyOrderPrice(MapContext map) {
		String sqlId = this.getNamedSqlId("verifyOrderPrice");
		return this.getSqlSession().update(sqlId,map);
	}

	@Override
	public int verifyDesignPrice(MapContext map) {
		String sqlId = this.getNamedSqlId("verifyDesignPrice");
		return this.getSqlSession().update(sqlId,map);
	}

	@Override
	public Map<String, String> countCompanyFinance() {
		String sqlId = this.getNamedSqlId("countCompanyFinance");
		return this.getSqlSession().selectOne(sqlId);
	}

	@Override
	public PaginatedList<CompanyFinanceListDto> findCompanyFinanceList(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectCompanyFinanceList");
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<CompanyFinanceListDto> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public CompanyFinanceInfoDto getCompanyFinanceInfoByPaymentId(String paymentId) {
		String sqlId = this.getNamedSqlId("selectCompanyFinanceInfoByPaymentId");
		return this.getSqlSession().selectOne(sqlId,paymentId);
	}

	@Override
	public List<Map> findByOrderIdAndType(MapContext orderIdAndType) {
		String sqlId = this.getNamedSqlId("findByOrderIdAndType");
		return this.getSqlSession().selectList(sqlId,orderIdAndType);
	}

	@Override
	public BigDecimal findByTypeAndCreated(MapContext params) {
		String sqlId = this.getNamedSqlId("findByTypeAndCreated");
		return this.getSqlSession().selectOne(sqlId,params);
	}
}