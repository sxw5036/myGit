package com.lwxf.industry4.webapp.facade.admin.factory.finance.impl;

import javax.annotation.Resource;

import java.util.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.customorder.ProduceOrderService;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.procurement.PurchaseService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.company.CompanyStatus;
import com.lwxf.industry4.webapp.common.enums.company.CompanyType;
import com.lwxf.industry4.webapp.common.enums.company.DealerAccountNature;
import com.lwxf.industry4.webapp.common.enums.company.ProduceOrderPay;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentStatus;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentWay;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.customorder.ProduceOrder;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccount;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.finance.FinanceFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2019/1/9/009 10:03
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("financeFacade")
public class FinanceFacadeImpl extends BaseFacadeImpl implements FinanceFacade {
	@Resource(name = "customOrderService")
	private CustomOrderService customOrderService;
	@Resource(name = "purchaseService")
	private PurchaseService purchaseService;
	@Resource(name = "companyService")
	private CompanyService companyService;
	@Resource(name = "paymentService")
	private PaymentService paymentService;
	@Resource(name = "dealerAccountService")
	private DealerAccountService dealerAccountService;
	@Resource(name = "aftersaleApplyService")
	private AftersaleApplyService aftersaleApplyService;
	@Resource(name = "produceOrderService")
	private ProduceOrderService produceOrderService;

	@Override
	public RequestResult findOrderFinanceList(MapContext mapContext,Integer pageNum,Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		paginatedFilter.setFilters(mapContext);
		List<Map<String,String>> sort = new ArrayList<Map<String, String>>();
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.paymentService.findListByPaginateFilter(paginatedFilter));
	}

	@Override
	public RequestResult findPurchaseList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS, 0);
		paginatedFilter.setFilters(mapContext);
		List<Map<String,String>> sort = new ArrayList<Map<String, String>>();
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.purchaseService.selectByFilter(paginatedFilter));
	}

	@Override
	public RequestResult findDealerPayList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS, 0);
		paginatedFilter.setFilters(mapContext);
		List<Map<String,String>> sort = new ArrayList<Map<String, String>>();
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.paymentService.selectByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateFinancePayStatus(String id, Integer type) {
		//判断支付记录是否存在
		Payment payment = this.paymentService.findById(id);
		if(payment==null&&payment.getStatus().equals(PaymentStatus.PAID.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS,1);
		this.paymentService.updateByMapContext(mapContext);
		MapContext updateDealerAccount = new MapContext();
		//根据type不同资金如不同的库
		DealerAccount dealerAccount = this.dealerAccountService.findByCompanIdAndNatureAndType(payment.getCompanyId(), DealerAccountNature.PUBLIC.getValue(), type);
		updateDealerAccount.put(WebConstant.KEY_ENTITY_ID,dealerAccount.getId());
		if(payment.getType().equals(PaymentType.B_TO_F.getValue())) {//是否充值操作
			if(dealerAccount==null){
				return ResultFactory.generateResNotFoundResult();
			}
			updateDealerAccount.put("balance",dealerAccount.getBalance().add(payment.getAmount()));
		}else if(payment.getType().equals(PaymentType.F_TO_B.getValue())){//是否是体现操作
			if(dealerAccount.getBalance().compareTo(payment.getAmount())!=-1){//判断余额是否充足
				updateDealerAccount.put("balance",dealerAccount.getBalance().subtract(payment.getAmount()));
			}else{
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_CREDIT_IS_LOW_10075,AppBeanInjector.i18nUtil.getMessage("BIZ_CREDIT_IS_LOW_10075"));
			}
		}else{
			return ResultFactory.generateResNotFoundResult();
		}
		this.dealerAccountService.updateByMapContext(updateDealerAccount);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findSupplierList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS, 0);
		mapContext.put("type",Arrays.asList(CompanyType.SUPPLIER.getValue()));
		paginatedFilter.setFilters(mapContext);
		List<Map<String,String>> sort = new ArrayList<Map<String, String>>();
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.companyService.selectByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateSupplier(String id, Integer status) {
		//判断供应商是否存在
		Company company = this.companyService.findById(id);
		if(company==null||!company.getType().equals(CompanyType.SUPPLIER.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext update = new MapContext();
		update.put(WebConstant.KEY_ENTITY_ID,id);
		update.put(WebConstant.KEY_ENTITY_STATUS,status);
		this.companyService.updateByMapContext(update);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findAftersaleList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS, 1);
		paginatedFilter.setFilters(mapContext);
		List<Map<String,String>> sort = new ArrayList<Map<String, String>>();
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.aftersaleApplyService.selectByFilter(paginatedFilter));
	}

	@Override
	public RequestResult findDealerList(MapContext mapContext, Integer pageNum, Integer pageSize) {
//		PaginatedFilter paginatedFilter = new PaginatedFilter();
//		Pagination pagination = new Pagination();
//		pagination.setPageNum(pageNum);
//		pagination.setPageSize(pageSize);
//		paginatedFilter.setPagination(pagination);
//		mapContext.put(WebConstant.KEY_ENTITY_STATUS, 0);
//		mapContext.put("type",Arrays.asList(CompanyType.MANUFACTURERS.getValue(),CompanyType.DIRECT_SHOP.getValue(),CompanyType.SHOP_IN_STORE.getValue(),CompanyType.EXCLUSIVE_SHOP.getValue(),CompanyType.FRANCHISED_STORE.getValue(),CompanyType.LOOSE_ORDER.getValue()));
//		paginatedFilter.setFilters(mapContext);
//		List<Map<String,String>> sort = new ArrayList<Map<String, String>>();
//		Map<String,String> created = new HashMap<String, String>();
//		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
//		sort.add(created);
//		paginatedFilter.setSorts(sort);
//		return ResultFactory.generateRequestResult(this.companyService.selectByFilter(paginatedFilter));
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		mapContext.put("fundsList", Arrays.asList(PaymentFunds.FRANCHISE_FEE.getValue()));
		paginatedFilter.setFilters(mapContext);
		List<Map<String,String>> sort = new ArrayList<Map<String, String>>();
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.paymentService.findListByPaginateFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateDealerPay(String id) {
		Payment payment = this.paymentService.findById(id);
		if(payment==null||!payment.getFunds().equals(PaymentFunds.FRANCHISE_FEE.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		//修改支付记录状态为 已收款
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS,PaymentStatus.PAID.getValue());
		this.paymentService.updateByMapContext(mapContext);
		//修改公司状态为待激活
		MapContext updateDealer = new MapContext();
		updateDealer.put(WebConstant.KEY_ENTITY_ID,payment.getCompanyId());
		updateDealer.put(WebConstant.KEY_ENTITY_STATUS,CompanyStatus.TO_BE_ENABLED.getValue());
		this.companyService.updateByMapContext(updateDealer);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateCoordinationPay(String id) {
		//判断外协单是否存在 是否已付款
		ProduceOrder produceOrder = this.produceOrderService.findById(id);
		if(produceOrder==null||produceOrder.getPay().equals(ProduceOrderPay.PAY.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		//修改外协单为完成
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		mapContext.put("pay",ProduceOrderPay.PAY.getValue());
		this.produceOrderService.updateByMapContext(mapContext);

		Payment payment = new Payment();
		payment.setHolder(produceOrder.getCoordinationName());
		payment.setHolderAccount(produceOrder.getCoordinationAccount());
		payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
		payment.setAmount(produceOrder.getAmount());
		payment.setCompanyId(WebUtils.getCurrCompanyId());
		payment.setStatus(PaymentStatus.PAID.getValue());
		payment.setCreated(DateUtil.getSystemDate());
		payment.setCreator(WebUtils.getCurrUserId());
		payment.setFunds(PaymentFunds.COORDINATION.getValue());
		payment.setWay(PaymentWay.BANK.getValue());
		payment.setType(PaymentType.F_TO_COORDINATION.getValue());
		payment.setPayee("4j1u3r1efshq");
		payment.setCustomOrderId(produceOrder.getCustomOrderId());
		this.paymentService.add(payment);
		return ResultFactory.generateSuccessResult();
	}
}
