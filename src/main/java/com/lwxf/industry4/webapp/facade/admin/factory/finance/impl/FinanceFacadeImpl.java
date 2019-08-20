package com.lwxf.industry4.webapp.facade.admin.factory.finance.impl;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.*;

import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderLogService;
import com.lwxf.industry4.webapp.bizservice.customorder.OrderProductService;
import com.lwxf.industry4.webapp.common.enums.order.*;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderProductDto;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApply;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderLog;
import com.lwxf.industry4.webapp.domain.entity.customorder.OrderProduct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.customorder.ProduceOrderService;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountService;
import com.lwxf.industry4.webapp.bizservice.financing.BankAccountService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentFilesService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.procurement.PurchaseService;
import com.lwxf.industry4.webapp.common.aop.syslog.OperationMoudule;
import com.lwxf.industry4.webapp.common.aop.syslog.OperationType;
import com.lwxf.industry4.webapp.common.aop.syslog.SysOperationLog;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.aftersale.AftersaleStatus;
import com.lwxf.industry4.webapp.common.enums.company.*;
import com.lwxf.industry4.webapp.common.enums.customorder.CoordinationState;
import com.lwxf.industry4.webapp.common.enums.customorder.ProduceOrderState;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentStatus;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentWay;
import com.lwxf.industry4.webapp.common.enums.payment.PaymentResourceType;
import com.lwxf.industry4.webapp.common.exceptions.BankBalanceLowException;
import com.lwxf.industry4.webapp.common.exceptions.BankNotFoundException;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.customorder.ProduceOrderDto;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.PaymentPrintTableDto;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.customorder.ProduceOrder;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccount;
import com.lwxf.industry4.webapp.domain.entity.financing.BankAccount;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentFiles;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.finance.FinanceFacade;
import com.lwxf.mybatis.utils.MapContext;

import static com.lwxf.industry4.webapp.facade.AppBeanInjector.i18nUtil;

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
	@Resource(name = "paymentFilesService")
	private PaymentFilesService paymentFilesService;
	@Resource(name = "bankAccountService")
	private BankAccountService bankAccountService;
	@Resource(name = "customOrderLogService")
	private CustomOrderLogService customOrderLogService;
	@Resource(name = "orderProductService")
	private OrderProductService orderProductService;

	@Override
	public RequestResult findOrderFinanceList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		paginatedFilter.setFilters(mapContext);
		List<Map<String, String>> sort = new ArrayList<Map<String, String>>();
		Map<String, String> status = new HashMap<String, String>();
		status.put(WebConstant.KEY_ENTITY_STATUS, "asc");
		sort.add(status);
		Map<String, String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED, "desc");
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
		List<Map<String, String>> sort = new ArrayList<Map<String, String>>();
		Map<String, String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED, "desc");
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
		List<Map<String, String>> sort = new ArrayList<Map<String, String>>();
		Map<String, String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED, "desc");
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.paymentService.selectByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateFinancePayStatus(String id, Integer type) {
		//判断支付记录是否存在
		Payment payment = this.paymentService.findById(id);
		if (payment == null && payment.getStatus().equals(PaymentStatus.PAID.getValue())) {
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_ENTITY_ID, id);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS, 1);
		this.paymentService.updateByMapContext(mapContext);
		MapContext updateDealerAccount = new MapContext();
		//根据type不同资金如不同的库
		DealerAccount dealerAccount = this.dealerAccountService.findByCompanIdAndNatureAndType(payment.getCompanyId(), DealerAccountNature.PUBLIC.getValue(), type);
		updateDealerAccount.put(WebConstant.KEY_ENTITY_ID, dealerAccount.getId());
		if (payment.getType().equals(PaymentType.B_TO_F.getValue())) {//是否充值操作
			if (dealerAccount == null) {
				return ResultFactory.generateResNotFoundResult();
			}
			updateDealerAccount.put("balance", dealerAccount.getBalance().add(payment.getAmount()));
		} else if (payment.getType().equals(PaymentType.F_TO_B.getValue())) {//是否是体现操作
			if (dealerAccount.getBalance().compareTo(payment.getAmount()) != -1) {//判断余额是否充足
				updateDealerAccount.put("balance", dealerAccount.getBalance().subtract(payment.getAmount()));
			} else {
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_CREDIT_IS_LOW_10075, AppBeanInjector.i18nUtil.getMessage("BIZ_CREDIT_IS_LOW_10075"));
			}
		} else {
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
		mapContext.put("type", Arrays.asList(CompanyType.SUPPLIER.getValue()));
		paginatedFilter.setFilters(mapContext);
		List<Map<String, String>> sort = new ArrayList<Map<String, String>>();
		Map<String, String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED, "desc");
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.companyService.selectByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateSupplier(String id, Integer status) {
		//判断供应商是否存在
		Company company = this.companyService.findById(id);
		if (company == null || !company.getType().equals(CompanyType.SUPPLIER.getValue())) {
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext update = new MapContext();
		update.put(WebConstant.KEY_ENTITY_ID, id);
		update.put(WebConstant.KEY_ENTITY_STATUS, status);
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
		List<Map<String, String>> sort = new ArrayList<Map<String, String>>();
		Map<String, String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED, "desc");
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
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		paginatedFilter.setFilters(mapContext);
		List<Map<String, String>> sort = new ArrayList<Map<String, String>>();
		Map<String, String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED, "desc");
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.paymentService.findListByPaginateFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateDealerPay(String id) {
		Payment payment = this.paymentService.findById(id);
		if (payment == null || !payment.getFunds().equals(PaymentFunds.FRANCHISE_FEE.getValue())) {
			return ResultFactory.generateResNotFoundResult();
		}
		//修改支付记录状态为 已收款
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID, id);
		mapContext.put(WebConstant.KEY_ENTITY_STATUS, PaymentStatus.PAID.getValue());
		mapContext.put("auditor", WebUtils.getCurrUserId());
		mapContext.put("audited", DateUtil.getSystemDate());
		mapContext.put("payTime", DateUtil.getSystemDate());
		this.paymentService.updateByMapContext(mapContext);
		//修改公司状态为待激活
		MapContext updateDealer = new MapContext();
		updateDealer.put(WebConstant.KEY_ENTITY_ID, payment.getCompanyId());
		updateDealer.put(WebConstant.KEY_ENTITY_STATUS, CompanyStatus.TO_BE_ENABLED.getValue());
		this.companyService.updateByMapContext(updateDealer);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "外协单审核",operationType = OperationType.VERIFY,operationMoudule = OperationMoudule.FINANCE)
	public RequestResult updateCoordinationPay(String id,MapContext map) {
		//判断外协单是否存在 是否已付款
		ProduceOrder produceOrder = this.produceOrderService.findById(id);
		if (produceOrder == null || produceOrder.getPay().equals(ProduceOrderPay.PAY.getValue())) {
			return ResultFactory.generateResNotFoundResult();
		}
		String bankId = map.getTypedValue("bank", String.class);
		BigDecimal amount = map.getTypedValue("amount", BigDecimal.class);
		//判断余额是否充足
		BankAccount bankAccount = this.bankAccountService.findById(bankId);
		if (bankAccount == null) {
			throw new BankNotFoundException();
		} else {
			if(bankAccount.getAmount().compareTo(amount)==-1){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_BANK_NOT_FOUND_10091,AppBeanInjector.i18nUtil.getMessage("BIZ_BANK_NOT_FOUND_10091"));
			}
			MapContext updateBank = MapContext.newOne();
			updateBank.put("id", bankId);
			updateBank.put("amount", bankAccount.getAmount().subtract(amount));
			bankAccountService.updateByMapContext(updateBank);
		}

		//修改外协单为已付款 且生产中
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID, id);
		mapContext.put("pay", ProduceOrderPay.PAY.getValue());
		mapContext.put(WebConstant.KEY_ENTITY_STATE, ProduceOrderState.IN_PRODUCTION.getValue());
		this.produceOrderService.updateByMapContext(mapContext);

		Payment payment = new Payment();
		payment.setHolder(produceOrder.getCoordinationName() == null ? "" : produceOrder.getCoordinationName());
		payment.setHolderAccount(produceOrder.getCoordinationAccount());
		payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
		payment.setAmount(produceOrder.getAmount());
		payment.setCompanyId(WebUtils.getCurrCompanyId());
		payment.setStatus(PaymentStatus.PAID.getValue());
		payment.setCreated(DateUtil.getSystemDate());
		payment.setCreator(WebUtils.getCurrUserId());
		payment.setFunds(PaymentFunds.COORDINATION.getValue());
		payment.setWay(map.getTypedValue("way",Integer.class));
		payment.setType(PaymentType.F_TO_B.getValue());
		payment.setPayee("4j1u3r1efshq");
		payment.setCustomOrderId(produceOrder.getCustomOrderId());
		payment.setNotes("外协单编号:" + produceOrder.getNo());
		payment.setAudited(map.getTypedValue("payTime",Date.class));
		payment.setAuditor(map.getTypedValue("auditor",String.class));
		payment.setPayTime(map.getTypedValue("payTime",Date.class));
		payment.setPayAmount(amount);
		this.paymentService.add(payment);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "订单审核",operationType = OperationType.VERIFY,operationMoudule = OperationMoudule.FINANCE)
	public RequestResult updateCustomOrdersPay(String id, MapContext map) {
		Payment payment = this.paymentService.findById(id);
		if (map == null) {
			return ResultFactory.generateResNotFoundResult();
		} else {
			if (map.get("amountDeduction") == null) {
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_PAYMENT_PAY_AMOUNT_NOT_NULL_10093, i18nUtil.getMessage("BIZ_AUDITED_PAY_AMOUNT_NOTNULL_10085"));
			}
		}
		if (payment == null || payment.getStatus().equals(PaymentStatus.PAID.getValue())) {
			return ResultFactory.generateResNotFoundResult();
		}
		//抵扣金额
		BigDecimal amountDeduction = map.getTypedValue("amountDeduction", BigDecimal.class);
		DealerAccount dealerAccount;
		//判断经销商账户余额 是否充足
		if (payment.getFunds().equals(PaymentFunds.ORDER_FEE_CHARGE.getValue())) {
			dealerAccount = this.dealerAccountService.findByCompanIdAndNatureAndType(payment.getCompanyId(), DealerAccountNature.PUBLIC.getValue(), DealerAccountType.FREE_ACCOUNT.getValue());
		} else if (payment.getFunds().equals(PaymentFunds.DESIGN_FEE_CHARGE.getValue())) {
			dealerAccount = this.dealerAccountService.findByCompanIdAndNatureAndType(payment.getCompanyId(), DealerAccountNature.PUBLIC.getValue(), DealerAccountType.FREEZE_ACCOUNT.getValue());
		} else {
			return ResultFactory.generateResNotFoundResult();
		}
		if (dealerAccount.getBalance().compareTo(amountDeduction) == -1) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_CREDIT_IS_LOW_10075, AppBeanInjector.i18nUtil.getMessage("BIZ_CREDIT_IS_LOW_10075"));
		}
		String customOrderId = payment.getCustomOrderId();
		//如果是货款扣款 则修改订单状态
		if (payment.getFunds().equals(PaymentFunds.ORDER_FEE_CHARGE.getValue())) {
			MapContext updateOrder = new MapContext();
			updateOrder.put(WebConstant.KEY_ENTITY_ID, customOrderId);
			//判断支付记录是 订单 还是 售后单 根据资源不同 修改不同数据
			if (payment.getResourceType() == PaymentResourceType.ORDER.getValue()) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(DateUtil.getSystemDate()); //需要将date数据转移到Calender对象中操作
				calendar.add(calendar.DATE, 15);//把日期往后增加15天.正数往后推,负数往前移动
				updateOrder.put("estimatedDeliveryDate", calendar.getTime());
				//判断产品不包含需要生产的产品，则直接状态更新为待发货
				List<OrderProductDto> listProd = orderProductService.findListByOrderId(customOrderId);
				int prodCount=0;
				//如果是柜体或者衣柜则订单状态改为待生产，否则为待发货
				if(listProd!=null && listProd.size()>0){
					for(OrderProduct prod : listProd){
						if(prod.getType()== OrderProductType.CUPBOARD.getValue()){
							prodCount++;
						}
						if(prod.getType()== OrderProductType.WARDROBE.getValue()){
							prodCount++;
						}
					}
				}
				if(prodCount>0){
					updateOrder.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.TO_PRODUCED.getValue());
				}else{
					updateOrder.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.TO_PACKAGED.getValue());
				}
				//更新订单编号
				CustomOrder order = customOrderService.findById(customOrderId);
				String orderNo = order.getNo();
				String lastStr = order.getNo().substring(10,orderNo.length());
				String dateFormat = "yyyyMMdd";
				String currDate = DateUtil.dateTimeToString(new Date(),dateFormat);
				String newOrderNo="";
				if(order.getType()== OrderType.SUPPLEMENTORDER.getValue()){
					newOrderNo="补HT"+currDate+lastStr;
				}else{
					newOrderNo="HT"+currDate+lastStr;
				}
				updateOrder.put("no",newOrderNo);
				this.customOrderService.updateByMapContext(updateOrder);
			} else {
				updateOrder.put(WebConstant.KEY_ENTITY_STATUS, AftersaleStatus.TO_BE_DISPATCH.getValue());
				this.aftersaleApplyService.updateByMapContext(updateOrder);
			}
		}
		//现付金额支付记录
//		Payment cashPayment = new Payment();

		//修改支付记录状态  已支付
		MapContext updatePayment = new MapContext();
		updatePayment.put(WebConstant.KEY_ENTITY_ID, id);
		updatePayment.put(WebConstant.KEY_ENTITY_STATUS, PaymentStatus.PAID.getValue());
		updatePayment.put("auditor", map.get("auditor"));
		updatePayment.put("audited", DateUtil.getSystemDate());
		if (map.get("notes") != null && !map.get("notes").equals("")) {
			updatePayment.put("notes", map.get("notes"));
		}
		if (map.get("payTime") != null && !map.get("payTime").equals("")) {
			updatePayment.put("payTime", map.get("payTime"));
//			cashPayment.setPayTime(map.getTypedValue("payTime", Date.class));
		}
		if (map.get("holder") != null && !map.get("holder").equals("")) {
			updatePayment.put("holder", map.get("holder"));
//			cashPayment.setHolder(map.getTypedValue("holder", String.class));
		}
		//现付金额
	//	BigDecimal cashDeduction = map.getTypedValue("cashDeduction", BigDecimal.class);
		updatePayment.put("payAmount", map.get("amountDeduction"));
		updatePayment.put("way", PaymentWay.DEALER_ACCOUNT.getValue());
		this.paymentService.updateByMapContext(updatePayment);
//		//经销商充值
//		Payment daqian = new Payment();
//		daqian.setResourceType(payment.getResourceType());
//		daqian.setPayee(payment.getPayee());
//		daqian.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
//		daqian.setFunds(PaymentFunds.FREE_FUNDS.getValue());
//		daqian.setAmount(cashDeduction);
//		daqian.setType(PaymentType.B_TO_F.getValue());
//		daqian.setCustomOrderId(payment.getCustomOrderId());
//		daqian.setBranchId(payment.getBranchId());
//		daqian.setCreated(DateUtil.getSystemDate());
//		daqian.setCreator(WebUtils.getCurrUserId());
//		daqian.setStatus(PaymentStatus.PAID.getValue());
//		daqian.setCompanyId(payment.getCompanyId());
//		daqian.setName(payment.getName());
//		daqian.setAuditor(map.getTypedValue("auditor", String.class));
//		daqian.setAudited(DateUtil.getSystemDate());
//		daqian.setPayAmount(cashDeduction);
//		daqian.setHolderAccount(payment.getHolderAccount());
//		String userId=WebUtils.getCurrUserId();
//		String userName=AppBeanInjector.userService.findByUserId(userId).getName();
//		daqian.setHolder(userName);
//		Integer way = map.getTypedValue("way", Integer.class);
//		daqian.setWay(way);
//		this.paymentService.add(daqian);
		//经销商账户扣除金额
		MapContext updateDealerAccount = new MapContext();
		updateDealerAccount.put(WebConstant.KEY_ENTITY_ID, dealerAccount.getId());
		updateDealerAccount.put("balance", dealerAccount.getBalance().subtract((amountDeduction)));
		this.dealerAccountService.updateByMapContext(updateDealerAccount);

//		cashPayment.setResourceType(payment.getResourceType());
//		cashPayment.setPayee(payment.getPayee());
//		cashPayment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
//		cashPayment.setFunds(payment.getFunds());
//		cashPayment.setAmount(cashDeduction);
//		cashPayment.setType(PaymentType.B_TO_F.getValue());
//		cashPayment.setCustomOrderId(payment.getCustomOrderId());
//		cashPayment.setBranchId(payment.getBranchId());
//		cashPayment.setCreated(DateUtil.getSystemDate());
//		cashPayment.setCreator(WebUtils.getCurrUserId());
//		cashPayment.setStatus(PaymentStatus.PAID.getValue());
//		cashPayment.setCompanyId(payment.getCompanyId());
//		cashPayment.setName(payment.getName());
//		cashPayment.setAuditor(map.getTypedValue("auditor", String.class));
//		cashPayment.setAudited(DateUtil.getSystemDate());
//		cashPayment.setPayAmount(cashDeduction);
//		cashPayment.setHolderAccount(payment.getHolderAccount());
//		cashPayment.setHolder("红田集团");
//		if (cashDeduction.compareTo(BigDecimal.ZERO) == 1) {
////			Integer way = map.getTypedValue("way", Integer.class);
////			cashPayment.setWay(way);
//			String bank = map.getTypedValue("bank", String.class);
////			cashPayment.setBank(bank);
////			this.paymentService.add(cashPayment);
//			//银行账户 余额更新
//			BankAccount bankAccount = this.bankAccountService.findById(bank);
//			if (bankAccount == null) {
//				throw new BankNotFoundException();
//			} else {
//				MapContext updateBank = MapContext.newOne();
//				updateBank.put("id", bank);
//				updateBank.put("amount", cashDeduction.add(bankAccount.getAmount()));
//				bankAccountService.updateByMapContext(updateBank);
//			}
//		}
		//记录操作日志
		//正常订单操作日志，售后单不保存操作日志
		if(payment.getResourceType()==0) {
			CustomOrder order = customOrderService.findByOrderId(customOrderId);
			if(order!=null){
				CustomOrderLog log = new CustomOrderLog();
				log.setCreated(new Date());
				log.setCreator(WebUtils.getCurrUserId());
				log.setName("订单财务审核");
				log.setStage(OrderStage.PAYMENT_VERIFY.getValue());
				log.setContent("订单号：" + order.getNo()+"已由"+WebUtils.getCurrUser().getName()+"审核扣款");
				log.setCustomOrderId(order.getId());
				customOrderLogService.add(log);
			}
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findOrderFinanceInfo(String paymentId) {
		PaymentDto paymentDto = this.paymentService.findOrderFinanceInfo(paymentId);
		paymentDto.setPaymentFilesList(this.paymentFilesService.findByPaymentId(paymentId));
		return ResultFactory.generateRequestResult(paymentDto);
	}

	@Override
	public RequestResult findPaymentInfo(String id) {
		//判断支付记录是否存在
		PaymentPrintTableDto byPaymentId = this.paymentService.findPrintTableById(id);
		return ResultFactory.generateRequestResult(byPaymentId);
	}

	@Override
	public RequestResult findFinanceOverview() {
		MapContext mapContext = new MapContext();
		//今日收入
		mapContext.put("incomeAmount", this.paymentService.findTodayAmountByType(PaymentType.B_TO_F.getValue()));
		//今日支出
		mapContext.put("expenditureAmount", this.paymentService.findTodayAmountByType(PaymentType.F_TO_B.getValue()));
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(1);
		pagination.setPageSize(99999);
		paginatedFilter.setPagination(pagination);
		//今日收入笔数
		MapContext filter1 = new MapContext();
		filter1.put("type", PaymentType.B_TO_F.getValue());
		filter1.put("payTimeNow", 1);
		paginatedFilter.setFilters(filter1);
		mapContext.put("incomeCount", this.paymentService.findListByPaginateFilter(paginatedFilter).getRows().size());
		//今日支出笔数
		MapContext filter2 = new MapContext();
		filter2.put("type", PaymentType.F_TO_B.getValue());
		filter2.put("payTimeNow", 1);
		paginatedFilter.setFilters(filter2);
		mapContext.put("expenditureCount", this.paymentService.findListByPaginateFilter(paginatedFilter).getRows().size());
		return ResultFactory.generateRequestResult(mapContext);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult uploadFiles(String id, List<MultipartFile> multipartFileList) {
		//添加图片到本地
		List<MapContext> listUrls = new ArrayList<>();
		if (multipartFileList != null && multipartFileList.size() > 0) {
			for (MultipartFile multipartFile : multipartFileList) {
				UploadInfo uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, multipartFile, UploadResourceType.PAYMENT, id);
				//添加图片到数据库
				PaymentFiles paymentFiles = new PaymentFiles();
				paymentFiles.setCreated(DateUtil.getSystemDate());
				paymentFiles.setFullPath(WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
				paymentFiles.setMime(uploadInfo.getFileMimeType().getRealType());
				paymentFiles.setName(uploadInfo.getFileName());
				paymentFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
				paymentFiles.setPath(uploadInfo.getRelativePath());
				paymentFiles.setStatus(0);
				paymentFiles.setCreator(WebUtils.getCurrUserId());
				paymentFiles.setPayment(id);
				this.paymentFilesService.add(paymentFiles);
				MapContext map = MapContext.newOne();
				map.put("fullPath", paymentFiles.getFullPath());
				map.put("imgRelPath", uploadInfo.getRelativePath());
				map.put("id", paymentFiles.getId());
				listUrls.add(map);
			}
		}
		return ResultFactory.generateRequestResult(listUrls);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteFile(String fileId) {
		//删除图片资源
		PaymentFiles paymentFiles = this.paymentFilesService.findById(fileId);
		if (paymentFiles == null) {
			return ResultFactory.generateSuccessResult();
		}
		//删除数据库图片资源
		this.paymentFilesService.deleteByPaymentId(fileId);
		//清楚本地文件
		AppBeanInjector.baseFileUploadComponent.deleteFileByDir(paymentFiles.getFullPath());
		return ResultFactory.generateSuccessResult();
	}
}
