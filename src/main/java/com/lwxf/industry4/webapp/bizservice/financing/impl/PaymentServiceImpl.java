package com.lwxf.industry4.webapp.bizservice.financing.impl;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


import com.lwxf.industry4.webapp.common.enums.financing.PaymentStatus;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentDto;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentDtoForApp;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.*;
import org.springframework.stereotype.Component;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.lwxf.industry4.webapp.bizservice.base.BaseServiceImpl;
import com.lwxf.industry4.webapp.domain.dao.financing.PaymentDao;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;


/**
 * 功能：
 * 
 * @author：panchenxiao(Mister_pan@126.com)
 * @created：2018-12-18 20:13:58
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Service("paymentService")
public class PaymentServiceImpl extends BaseServiceImpl<Payment, String, PaymentDao> implements PaymentService {


	@Resource
	@Override	public void setDao( PaymentDao paymentDao) {
		this.dao = paymentDao;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<PaymentDto> selectByFilter(PaginatedFilter paginatedFilter) {
		//
		return this.dao.selectByFilter(paginatedFilter) ;
	}

	@Override
	public List<PaymentDto> findByOrderId(String orderId) {
		return this.dao.findByOrderId(orderId);
	}

	@Override
	public PaymentDto findByPaymentId(String paymentId) {
		return this.dao.findByPaymentId(paymentId);
	}
	@Override
	public PaymentDto findByPId(String paymentId) {
		return this.dao.findByPId(paymentId);
	}

	@Override
	public List<Payment> findByCompanyIdAndStatusAndType(MapContext params) {
		return this.dao.findByCompanyIdAndStatusAndType(params);
	}

	@Override
	public PaginatedList<Payment> findByCompanyIdAndStatusAndType(PaginatedFilter paginatedFilter) {
		return this.dao.findByCompanyIdAndStatusAndType(paginatedFilter);
	}

	@Override
	public int updateStatusByOrderIdAndFund(String orderId, Integer funds, Integer status) {
		return this.dao.updateStatusByOrderIdAndFund(orderId,funds,status);
	}

	@Override
	public PaginatedList<PaymentDto> findListByPaginateFilter(PaginatedFilter paginatedFilter) {
		return this.dao.findListByPaginateFilter(paginatedFilter);
	}

	@Override
	public List<Payment> findByOrderIdAndStatus(String orderId, Integer status) {
		MapContext params = MapContext.newOne();
		params.put("orderId",orderId);
		params.put("status",status);
		return this.dao.findByOrderIdAndStatus(params);
	}

	@Override
	public PaginatedList<PaymentDtoForApp> selectByFilterForApp(PaginatedFilter paginatedFilter) {
		return this.dao.selectByFilterForApp(paginatedFilter);
	}


	@Override
	public PaymentDto findByOrderIdAndTypeAndFundsAndStatus(MapContext orderIdAndType) {
		return this.dao.findByOrderIdAndTypeAndFundsAndStatus(orderIdAndType);
	}

	@Override
	public PaginatedList<PaymentDtoForApp> selectPaymentByCompanyIdForApp(PaginatedFilter paginatedFilter) {
		return this.dao.selectPaymentByCompanyIdForApp(paginatedFilter);
	}

	@Override
	public FinanceCountDto selectFinanceCountForApp() {
		return this.dao.selectFinanceCountForApp();
	}

	@Override
	public List<VerifyPaymentDto> selectVerifyPaymentList() {
		return this.dao.selectVerifyPaymentList();
	}

	@Override
	public VerifyOrderPriceDto selectVerifyOrderPriceInfo(String paymentId) {
		return this.dao.selectVerifyOrderPriceInfo(paymentId);
	}

	@Override
	public VerifyDesignPriceDto selectVerifyDesignPriceInfo(String paymentId) {
		return this.dao.selectVerifyDesignPriceInfo(paymentId);
	}

	@Override
	public int verifyOrderPrice(MapContext map) {
		return this.dao.verifyOrderPrice(map);
	}

	@Override
	public int verifyDesignPrice(MapContext map) {
		return this.dao.verifyDesignPrice(map);
	}

	@Override
	public Map<String, String> countCompanyFinance() {
		return this.dao.countCompanyFinance();
	}

	@Override
	public PaginatedList<CompanyFinanceListDto> findCompanyFinanceList(PaginatedFilter paginatedFilter) {
		return this.dao.findCompanyFinanceList(paginatedFilter);
	}

	@Override
	public CompanyFinanceInfoDto getCompanyFinanceInfoByPaymentId(String paymentId) {
		return this.dao.getCompanyFinanceInfoByPaymentId(paymentId);
	}
	@Override
	public List<Map> findByOrderIdAndType(MapContext orderIdAndType) {
		return this.dao.findByOrderIdAndType(orderIdAndType);
	}

	@Override
	public BigDecimal findByTypeAndCreated(Integer type, String beginTime, String endTime, String day) {
		MapContext params = MapContext.newOne();
		params.put("type", type);
		params.put("beginTime",beginTime );
		params.put("endTime",endTime );
		params.put("created", day);
		params.put("status", PaymentStatus.PAID.getValue());
		return this.dao.findByTypeAndCreated(params);
	}
}