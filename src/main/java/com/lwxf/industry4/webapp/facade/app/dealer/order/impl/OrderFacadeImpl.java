package com.lwxf.industry4.webapp.facade.app.dealer.order.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.customer.CompanyCustomerService;
import com.lwxf.industry4.webapp.bizservice.customorder.*;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountLogService;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockItemService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageService;
import com.lwxf.industry4.webapp.common.component.BaseFileUploadComponent;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.aftersale.AftersaleStatus;
import com.lwxf.industry4.webapp.common.enums.company.*;
import com.lwxf.industry4.webapp.common.enums.customorder.*;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentStatus;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentWay;
import com.lwxf.industry4.webapp.common.enums.order.*;
import com.lwxf.industry4.webapp.common.enums.product.ProductCategoryId;
import com.lwxf.industry4.webapp.common.enums.product.ProductCategoryType;
import com.lwxf.industry4.webapp.common.enums.storage.FinishedStockStatus;
import com.lwxf.industry4.webapp.common.enums.storage.FinishedStockWay;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.AddressUtils;
import com.lwxf.industry4.webapp.common.utils.UserUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.aftersale.AftersaleDto;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.customorder.*;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockItemDto;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.customorder.*;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccount;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccountLog;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStock;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStockItem;
import com.lwxf.industry4.webapp.domain.entity.warehouse.Storage;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderFacade;
import com.lwxf.mybatis.utils.MapContext;

import org.apache.commons.collections.map.HashedMap;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.*;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/6 16:34
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("orderFacade")
public class OrderFacadeImpl extends BaseFacadeImpl implements OrderFacade {
	@Resource(name = "customOrderService")
	private CustomOrderService customOrderService;
	@Resource(name = "customOrderDemandService")
	private CustomOrderDemandService customOrderDemandService;
	@Resource(name = "customOrderDesignService")
	private CustomOrderDesignService customOrderDesignService;//设计
	@Resource(name = "customOrderFilesService")
	private CustomOrderFilesService customOrderFilesService;
	@Resource(name = "baseFileUploadComponent")
	private BaseFileUploadComponent baseFileUploadComponent;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "companyEmployeeService")
	private CompanyEmployeeService companyEmployeeService;
	@Resource(name = "roleService")
	private RoleService roleService;
	@Resource(name = "dealerAccountService")
	private DealerAccountService dealerAccountService;
	@Resource(name = "dealerAccountLogService")
	private DealerAccountLogService dealerAccountLogService;
	@Resource(name = "companyService")
	private CompanyService companyService;
	@Resource(name = "aftersaleApplyService")
	private AftersaleApplyService aftersaleApplyService;
	@Resource(name = "paymentService")
	private PaymentService paymentService;
	@Resource(name = "orderAccountLogService")
	private OrderAccountLogService orderAccountLogService;
	@Resource(name = "produceOrderService")
	private ProduceOrderService produceOrderService;
	@Resource(name = "orderProductService")
	private OrderProductService orderProductService;
	@Resource(name = "produceFlowService")
	private ProduceFlowService produceFlowService;
	@Resource(name="storageService")
	private StorageService storageService;
	@Resource(name = "finishedStockService")
	private FinishedStockService finishedStockService;
	@Resource(name = "finishedStockItemService")
	private FinishedStockItemService finishedStockItemService;


	/**
	 * 订单列表
	 *
	 * @param pageNum
	 * @param pageSize
	 * @param params
	 * @return
	 */
	@Override
	public RequestResult selectByCondition(Integer pageNum, Integer pageSize, MapContext params, String uid, String cid) {
		CompanyEmployee companyEmployee = this.companyEmployeeService.findOneByCompanyIdAndUserId(cid, uid);
		if (null == companyEmployee) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		PaginatedList<CustomOrderDto> customOrderPaginatedList;
		PaginatedFilter filter = PaginatedFilter.newOne();
		Pagination pagination = Pagination.newOne();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		filter.setPagination(pagination);
		filter.setFilters(params);
		//判断是不是店主或者经理，true-查询店铺下的所有订单，fales-查询店铺下业务员的订单
		String roleId = companyEmployee.getRoleId();
		//经理
		Role manager = this.roleService.selectByKey(DealerEmployeeRole.MANAGER.getValue());
		//店主
		Role shopkeeper = this.roleService.selectByKey(DealerEmployeeRole.SHOPKEEPER.getValue());
		if (roleId.equals(manager.getId()) || roleId.equals(shopkeeper.getId())) {
			customOrderPaginatedList = this.customOrderService.selectByCondition(filter);
		} else {
			params.put("salesman", uid);
			filter.setFilters(params);
			customOrderPaginatedList = this.customOrderService.selectByCondition(filter);
		}
		for (int i = 0; i < customOrderPaginatedList.getRows().size(); i++) {
			String city = customOrderPaginatedList.getRows().get(i).getCityName();//地区地址
			String address1 = customOrderPaginatedList.getRows().get(i).getAddress();//详细地址
			String address = AddressUtils.getAddress(city, address1);
			customOrderPaginatedList.getRows().get(i).setAddress(address);
			customOrderPaginatedList.getRows().get(i).setCityName(null);
			customOrderPaginatedList.getRows().get(i).setCityAreaId(null);
		}
		return ResultFactory.generateRequestResult(customOrderPaginatedList);
	}

	/**
	 * 订单详情
	 *
	 * @param id
	 * @return
	 */
	@Override
	public RequestResult selectByOrderId(String id) {
		//订单信息
		CustomOrderDto customOrderDto = this.customOrderService.findByOrderId(id);
		if (null == customOrderDto) {
			return ResultFactory.generateRequestResult(customOrderDto);
		}
		String city = customOrderDto.getCityName();
		String address1 = customOrderDto.getAddress();//详细地址
		String address = AddressUtils.getAddress(city, address1);
		customOrderDto.setAddress(address);
		customOrderDto.setCityName(null);
		List<CustomOrderFiles> filesList = this.customOrderFilesService.selectByOrderIdAndType(id, 2, null);
		List<String> filesPath = new ArrayList<>();
		for (CustomOrderFiles customOrderFile : filesList) {
			String path = customOrderFile.getPath();
			filesPath.add(path);
		}
		MapContext data = MapContext.newOne();
		data.put("customOrder", customOrderDto);
		data.put("filesPath", filesPath);
		return ResultFactory.generateRequestResult(data);
	}

	/**
	 * 添加订单
	 *
	 * @param customOrder
	 * @param
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addOrder(CustomOrder customOrder, String uid, String cid) {
		Integer type = customOrder.getType();
		boolean contains = OrderType.contains(type);
		if (!contains) {
			MapContext result = new MapContext();
			result.put("type",ErrorCodes.VALIDATE_ILLEGAL_ARGUMENT);
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, result);
		}
		customOrder.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.ORDER_NO));
		customOrder.setCompanyId(cid);
		customOrder.setCreated(DateUtil.getSystemDate());
		customOrder.setCreator(uid);
		customOrder.setStatus(0);
		customOrder.setAmount(new BigDecimal(0));
		customOrder.setImprest(new BigDecimal(0));
		customOrder.setRetainage(new BigDecimal(0));
		customOrder.setEarnest(0);
		customOrder.setDesignFee(0);
		customOrder.setFactoryPrice(new BigDecimal(0));
		customOrder.setMarketPrice(new BigDecimal(0));
		customOrder.setDiscounts(new BigDecimal(0));
		customOrder.setFactoryDiscounts(new BigDecimal(0));
		customOrder.setFactoryFinalPrice(new BigDecimal(0));
		customOrder.setConfirmFprice(false);
		customOrder.setConfirmCprice(false);
		RequestResult result = customOrder.validateFields();
		if (null != result) {
			return result;
		}
		this.customOrderService.add(customOrder);
//        CustomOrderLog customOrderLog = new CustomOrderLog();
//        customOrderLog.setContent("顾客XXX,添加订单了,订单编号为："+customOrder.getNo());
//        customOrderLog.setCreated(DateUtil.getSystemDate());
//        customOrderLog.setCreator(uid);
//        customOrderLog.setCustomOrderId(customOrder.getId());
//        customOrderLog.setName("创建订单");
//        //customOrderLog.setNotes();
//        customOrderLog.setStage(CustomOrderLogStage.Marketing.getValue());
//        //customOrderLog.setPath();
//        this.customOrderLogService.add(customOrderLog);
		MapContext map = MapContext.newOne();
		map.put("id", customOrder.getId());
		return ResultFactory.generateRequestResult(map);

	}

	/**
	 * 修改订单
	 *
	 * @param updateMap
	 * @param request
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateOrder(MapContext updateMap, HttpServletRequest request) {
		this.customOrderService.updateByMapContext(updateMap);
		return ResultFactory.generateSuccessResult();
	}

	/**
	 * 删除订单
	 *
	 * @param orderId
	 * @param request
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteByOrderId(String orderId, HttpServletRequest request) {

		List<CustomOrderFiles> filesList = this.customOrderFilesService.selectByOrderId(orderId);
		Map<String, UploadResourceType> files = new HashedMap();
		for (CustomOrderFiles file : filesList) {
			files.put(file.getPath(), UploadResourceType.CUSTOM_ORDER);
		}
		this.customOrderFilesService.deleteByOrderId(orderId);
		baseFileUploadComponent.deleteFiles(files, 0);
		this.customOrderDesignService.deleteByOrderId(orderId);
		this.customOrderDemandService.deleteByOrderId(orderId);
		this.customOrderService.deleteById(orderId);

		return ResultFactory.generateSuccessResult();
	}


	/**
	 * 修改订单的状态,将订单的状态改为1
	 * 提交需求。修改订单的状态。
	 * @param params
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult commitOrderDemand(MapContext params) {
		String id = params.getTypedValue("id", String.class);

		CustomOrder customOrder = this.customOrderService.findById(id);
		if (null==customOrder){
			return ResultFactory.generateResNotFoundResult();
		}

		Integer status = customOrder.getStatus();
		switch (status){
			case 0:status = 1;break;
		}

		params.put("status",status);
		this.customOrderService.updateByMapContext(params);
		MapContext data = MapContext.newOne();
		return ResultFactory.generateRequestResult(data);
	}

	@Override
	public RequestResult findFinishedOrderList(List<Integer> statusList, Integer pageNum, Integer pageSize, boolean finishedOrder) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_ENTITY_STATUS, statusList);
		mapContext.put("finishedOrder", finishedOrder);
		paginatedFilter.setFilters(mapContext);
		List<Map<String, String>> sorts = new ArrayList<Map<String, String>>();
		Map<String, String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED, "desc");
		sorts.add(created);
		paginatedFilter.setSorts(sorts);
		return ResultFactory.generateRequestResult(this.customOrderService.findFinishedOrderList(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateOrderStatus(String id, Integer status, MapContext mapContext) {
		//判断订单是否存在
		CustomOrder customOrder = this.customOrderService.findById(id);
		if (customOrder == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext updateOrder = new MapContext();
		MapContext updateDealerAccount = new MapContext();
		DealerAccount dealerAccount;
		DealerAccountLog dealerAccountLog;
		User user;
		CustomOrderProcess customOrderProcess;
		Payment payment;
		//根据现订单不同状态进行修改操作
		switch (OrderStatus.getByValue(customOrder.getStatus())) {
//			case TO_ADD_DESIGNFEE://设计费待评估
//				if (!OrderStatus.TO_BE_CONFIRMED_DESIGNFEE.getValue().equals(status)) {
//					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
//				}
//				break;
			case TO_ADD_DESIGNFEE://设计费待确认
				//暂时工厂端支持替经销商确认设计费
				if (!OrderStatus.TO_AUDIT_DESIGN.getValue().equals(status)) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}
				payment = new Payment();
				payment.setHolder("红田集团");
				payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
				payment.setAmount(new BigDecimal(customOrder.getDesignFee()));
				payment.setCompanyId(customOrder.getCompanyId());
				payment.setStatus(PaymentStatus.PENDING_PAYMENT.getValue());
				payment.setCreated(DateUtil.getSystemDate());
				payment.setCreator(WebUtils.getCurrUserId());
				payment.setFunds(PaymentFunds.DESIGN_FEE_CHARGE.getValue());
				payment.setWay(PaymentWay.BANK.getValue());
				payment.setType(PaymentType.B_TO_F_WITHHOLD.getValue());
				payment.setPayee("4j1u3r1efshq");
				payment.setCustomOrderId(customOrder.getId());
				this.paymentService.add(payment);
				break;
			//return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
			case TO_AUDIT_DESIGN://设计费待审核
				if (!OrderStatus.TO_DESIGN.getValue().equals(status)) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}
				//获取经销商财务账户
				dealerAccount = this.dealerAccountService.findByCompanIdAndNatureAndType(customOrder.getCompanyId(), DealerAccountNature.PUBLIC.getValue(), DealerAccountType.FREE_ACCOUNT.getValue());
				if (dealerAccount == null) {
					return ResultFactory.generateResNotFoundResult();
				}
				//判断余额是否充足
				if (dealerAccount.getBalance().compareTo(new BigDecimal(customOrder.getDesignFee())) == -1) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_CREDIT_IS_LOW_10075, AppBeanInjector.i18nUtil.getMessage("BIZ_CREDIT_IS_LOW_10075"));
				}
				//扣款
				updateDealerAccount.put(WebConstant.KEY_ENTITY_ID, dealerAccount.getId());
				updateDealerAccount.put("balance", dealerAccount.getBalance().subtract(new BigDecimal(customOrder.getDesignFee())));
				this.dealerAccountService.updateByMapContext(updateDealerAccount);
				//生成扣款记录
				dealerAccountLog = new DealerAccountLog();
				dealerAccountLog.setDealerAccountId(dealerAccount.getId());
				dealerAccountLog.setContent("订单设计扣款");
				dealerAccountLog.setType(3);
				dealerAccountLog.setCreated(DateUtil.getSystemDate());
				dealerAccountLog.setCreator(WebUtils.getCurrUserId());
				dealerAccountLog.setAmount(new BigDecimal(customOrder.getDesignFee()));
				dealerAccountLog.setDisburse(true);
				dealerAccountLog.setCompanyId(dealerAccount.getCompanyId());
				dealerAccountLog.setResourceId(customOrder.getId());
				this.dealerAccountLogService.add(dealerAccountLog);
				//修改支付记录信息
				this.paymentService.updateStatusByOrderIdAndFund(id, PaymentFunds.DESIGN_FEE_CHARGE.getValue(), PaymentStatus.PAID.getValue());
				//设计费扣除完成后 生成货款的支付记录
//				Payment pricePayment = new Payment();
//				pricePayment.setHolder("红田集团");
//				pricePayment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
//				pricePayment.setAmount(new BigDecimal(customOrder.getDesignFee()));
//				pricePayment.setCompanyId(customOrder.getCompanyId());
//				pricePayment.setStatus(PaymentStatus.PENDING_PAYMENT.getValue());
//				pricePayment.setCreated(DateUtil.getSystemDate());
//				pricePayment.setCreator(WebUtils.getCurrUserId());
//				pricePayment.setWay(PaymentWay.BANK.getValue());
//				pricePayment.setType(PaymentType.B_TO_F_WITHHOLD.getValue());
//				pricePayment.setPayee("4j1u3r1efshq");
//				pricePayment.setCustomOrderId(customOrder.getId());
//				pricePayment.setFunds(PaymentFunds.CARGO.getValue());
//				this.paymentService.add(pricePayment);
				break;
			case TO_DESIGN://待设计
				if (!OrderStatus.DESIGNING.getValue().equals(status)) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}
				//判断订单是否存在设计师 不存在则不允许
				if (customOrder.getDesigner() == null) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_UNASSIGNED_DESIGNER_10078, AppBeanInjector.i18nUtil.getMessage("BIZ_UNASSIGNED_DESIGNER_10078"));
				}
				break;
			case DESIGNING://设计中
				if (!OrderStatus.TO_SUBMIT.getValue().equals(status)) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}
				//工厂价
				BigDecimal factoryPrice = mapContext.getTypedValue("factoryPrice", BigDecimal.class);
				updateOrder.put("factoryPrice", factoryPrice);
				break;
			case TO_SUBMIT://设计待确认(设计确认后的状态，将提交给工厂)
				if(!OrderStatus.FACTORY_CONFIRMED_FPROCE.getValue().equals(status)){
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}
				//修改设计单状态 为 已确认
				String designId = mapContext.getTypedValue("designId",String.class);
				if(this.customOrderDesignService.findById(designId)==null){
					return ResultFactory.generateResNotFoundResult();
				}
				MapContext updateDesign = new MapContext();
				updateDesign.put(WebConstant.KEY_ENTITY_ID,designId);
				updateDesign.put(WebConstant.KEY_ENTITY_STATUS,OrderDesignStatus.CONFIRMED.getValue());
				this.customOrderDesignService.updateByMapContext(updateDesign);
				break;
			case FACTORY_CONFIRMED_FPROCE://出厂价待确认
				if (!OrderStatus.TO_AUDIT.getValue().equals(status)) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}
				payment = new Payment();
				payment.setHolder("红田集团");
				payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
				payment.setAmount(customOrder.getFactoryFinalPrice());
				payment.setCompanyId(customOrder.getCompanyId());
				payment.setStatus(PaymentStatus.PENDING_PAYMENT.getValue());
				payment.setCreated(DateUtil.getSystemDate());
				payment.setCreator(WebUtils.getCurrUserId());
				payment.setFunds(PaymentFunds.ORDER_FEE_CHARGE.getValue());
				payment.setWay(PaymentWay.BANK.getValue());
				payment.setType(PaymentType.B_TO_F_WITHHOLD.getValue());
				payment.setPayee("4j1u3r1efshq");
				payment.setCustomOrderId(customOrder.getId());
				this.paymentService.add(payment);
				break;
//			case DEALER_CONFIRMED_FPRICE://经销商待确认出厂价 (废弃)
//				// 暂时工厂端支持替经销商确认设计费
//				if (!OrderStatus.TO_AUDIT.getValue().equals(status)) {
//					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
//				}
//				payment = new Payment();
//				payment.setHolder("红田集团");
//				payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
//				payment.setAmount(customOrder.getFactoryFinalPrice());
//				payment.setCompanyId(customOrder.getCompanyId());
//				payment.setStatus(PaymentStatus.PENDING_PAYMENT.getValue());
//				payment.setCreated(DateUtil.getSystemDate());
//				payment.setCreator(WebUtils.getCurrUserId());
//				payment.setFunds(PaymentFunds.CARGO.getValue());
//				payment.setWay(PaymentWay.BANK.getValue());
//				payment.setType(PaymentType.B_TO_F_WITHHOLD.getValue());
//				payment.setPayee("4j1u3r1efshq");
//				payment.setCustomOrderId(customOrder.getId());
//				this.paymentService.add(payment);
//				break;
			//return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
			case TO_AUDIT://货款支付审核
				if (!OrderStatus.TO_PRODUCTION.getValue().equals(status)) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}

				//获取经销商财务账户
				dealerAccount = this.dealerAccountService.findByCompanIdAndNatureAndType(customOrder.getCompanyId(), DealerAccountNature.PUBLIC.getValue(), DealerAccountType.FREE_ACCOUNT.getValue());
				if (dealerAccount == null) {
					return ResultFactory.generateResNotFoundResult();
				}
				//判断余额是否充足
				if (dealerAccount.getBalance().compareTo(customOrder.getFactoryFinalPrice()) == -1) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_CREDIT_IS_LOW_10075, AppBeanInjector.i18nUtil.getMessage("BIZ_CREDIT_IS_LOW_10075"));
				}
				//扣款
				updateDealerAccount.put(WebConstant.KEY_ENTITY_ID, dealerAccount.getId());
				updateDealerAccount.put("balance", dealerAccount.getBalance().subtract(customOrder.getFactoryFinalPrice()));
				this.dealerAccountService.updateByMapContext(updateDealerAccount);
				//用户信息
				user = this.userService.findById(customOrder.getCustomerId());
				//生成扣款记录
				dealerAccountLog = new DealerAccountLog();
				dealerAccountLog.setDealerAccountId(dealerAccount.getId());
				dealerAccountLog.setContent("订单扣款");
				dealerAccountLog.setType(2);
				dealerAccountLog.setCreated(DateUtil.getSystemDate());
				dealerAccountLog.setCreator(WebUtils.getCurrUserId());
				dealerAccountLog.setAmount(customOrder.getFactoryFinalPrice());
				dealerAccountLog.setDisburse(true);
				dealerAccountLog.setCompanyId(dealerAccount.getCompanyId());
				dealerAccountLog.setResourceId(customOrder.getId());
				this.dealerAccountLogService.add(dealerAccountLog);
				//修改支付记录信息

				this.paymentService.updateStatusByOrderIdAndFund(id,PaymentFunds.ORDER_FEE_CHARGE.getValue(),PaymentStatus.PAID.getValue());

				//如果订单类型是补产单 则正常进入待生产 因此不处理
				//如果订单类型是补发单 则进入待入库状态
				if (customOrder.getType().equals(OrderType.REPLACEMENT.getValue())) {
					status = OrderStatus.TO_WAREHOUSE.getValue();
				}

				//订单货款扣款完成后 修改自产的生产为已付款
				this.produceOrderService.updatePayByOrderIdAndWays(id,Arrays.asList(ProduceOrderWay.SELF_PRODUCED.getValue(),ProduceOrderWay.SPECIAL.getValue()));
				updateOrder.put("documentaryTime",DateUtil.getSystemDate());
				updateOrder.put("documentaryNotes","已下车间");
				break;
			case TO_PRODUCTION://待生产
				if (!OrderStatus.IN_PRODUCTION.getValue().equals(status)) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}
				//创建订单生产信息表
//                customOrderProcess = new CustomOrderProcess();
//                customOrderProcess.setBeginTime(DateUtil.getSystemDate());
//                customOrderProcess.setOperator(WebUtils.getCurrUserId());
//                customOrderProcess.setOrderId(id);
//                customOrderProcess.setType(CustomOrderProcessType.SPARE_PARTS.getValue());
//                customOrderProcess.setStatus(CustomOrderProcessStatus.HAVE_IN_HAND.getValue());
//                this.customOrderProcessService.add(customOrderProcess);
				break;
			case IN_PRODUCTION://生产中
				if (!OrderStatus.TO_WAREHOUSE.getValue().equals(status)) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}
				//创建订单生产信息表
//                customOrderProcess = new CustomOrderProcess();
//                customOrderProcess.setBeginTime(DateUtil.getSystemDate());
//                customOrderProcess.setOperator(WebUtils.getCurrUserId());
//                customOrderProcess.setOrderId(id);
//                customOrderProcess.setType(CustomOrderProcessType.MACHINING.getValue());
//                customOrderProcess.setStatus(CustomOrderProcessStatus.HAVE_IN_HAND.getValue());
//                this.customOrderProcessService.add(customOrderProcess);
				//修改上一步订单信息表信息
//                this.customOrderProcessService.updateStatusByOrderIdAndType(id,CustomOrderProcessType.SPARE_PARTS.getValue());
				break;
			case TO_WAREHOUSE://待入库
				if (!OrderStatus.TO_OUT_STOCK.getValue().equals(status)) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}
				break;
			case TO_OUT_STOCK://待出库
				if (!OrderStatus.TO_DISPATCH.getValue().equals(status)) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}
				break;
			case TO_DISPATCH://待配送
				if (!OrderStatus.DISPATCHING.getValue().equals(status)) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}
				break;
			case DISPATCHING://配送中
				if (!OrderStatus.RECEIVED.getValue().equals(status)) {
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
				}
				break;
			default:
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077, AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
		}
		//修改订单状态`
		updateOrder.put(WebConstant.KEY_ENTITY_ID, id);
		updateOrder.put(WebConstant.KEY_ENTITY_STATUS, status);
		this.customOrderService.updateByMapContext(updateOrder);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findOrderList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		paginatedFilter.setFilters(mapContext);
		Map<String, String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED, "desc");
		List sort = new ArrayList();
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.customOrderService.findListByPaginateFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateOrderDesigner(String userId, String id) {
		//该用户是否存在并且是否是厂家的员工以及该角色是否是设计师
		if (!UserUtil.hasRoleByKey(userId, FactoryEmployeeRole.DESIGNER.getValue())) {
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID, id);
		mapContext.put("designer", userId);
		this.customOrderService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.customOrderService.findByOrderId(id));
	}

	@Override
	public RequestResult findOrderInfo(String id) {
		//判断订单是否存在
		CustomOrderDto customOrderDto = this.customOrderService.findByOrderId(id);
		if (customOrderDto == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		CustomOrderInfoDto customOrderInfoDto = new CustomOrderInfoDto();
		customOrderInfoDto.setCustomOrder(customOrderDto);
		customOrderInfoDto.setOrderDemand(this.customOrderDemandService.findListByOrderId(id));
		customOrderInfoDto.setOrderDesign(this.customOrderDesignService.findListByOrderId(id));
		customOrderInfoDto.setOrderContractFiles(this.customOrderFilesService.selectByOrderIdAndType(id, CustomOrderFilesType.CONTRACT.getValue(), null));
		customOrderInfoDto.setOrderAccountLogList(this.orderAccountLogService.findByOrderId(id));
		customOrderInfoDto.setOrderProducts(this.orderProductService.findListByOrderId(id));
		customOrderInfoDto.setProduceOrderDtos(this.produceOrderService.findListByOrderId(id));
		return ResultFactory.generateRequestResult(customOrderInfoDto);
	}

	@Override
	public RequestResult findOrderDemand(String id, String demandId) {
		CustomOrderDemandDto customOrderDemandDto = this.customOrderDemandService.selectByDemandId(demandId);
		if (customOrderDemandDto == null || !customOrderDemandDto.getCustomOrderId().equals(id)) {
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext mapContext = MapContext.newOne();
		mapContext.put("customOrderDemand", customOrderDemandDto);
		mapContext.put("demandFile", this.customOrderFilesService.selectByOrderIdAndType(id, 0, demandId));
		return ResultFactory.generateRequestResult(mapContext);
	}

	@Override
	public RequestResult findOrderDesignId(String id, String designId) {
		CustomOrderDesignDto orderDesignDto = this.customOrderDesignService.findOneByDesignId(designId);
		if (orderDesignDto == null || !orderDesignDto.getCustomOrderId().equals(id)) {
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext mapContext = MapContext.newOne();
		mapContext.put("orderDesignDto", orderDesignDto);
		mapContext.put("designFile", this.customOrderFilesService.selectByOrderIdAndType(id, null, designId));
		return ResultFactory.generateRequestResult(mapContext);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addOrderDesign(String id, CustomOrderDesign customOrderDesign) {
		//判断订单是否存在 状态是否是设计中
		CustomOrder customOrder = this.customOrderService.findById(id);
		if (customOrder == null || !customOrder.getStatus().equals(OrderStatus.DESIGNING.getValue())) {
			return ResultFactory.generateResNotFoundResult();
		}
		this.customOrderDesignService.add(customOrderDesign);
		return ResultFactory.generateRequestResult(this.customOrderDesignService.findOneByDesignId(customOrderDesign.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateDesign(String id, String designId, MapContext mapContext) {
		//判断设计记录是否存在
		CustomOrderDesign customOrderDesign = this.customOrderDesignService.findById(designId);
		if (customOrderDesign == null || !customOrderDesign.getCustomOrderId().equals(id)) {
			return ResultFactory.generateResNotFoundResult();
		}
		//todo:判断修改操作是否是修改状态 如果存在 待确认或者已确认的设计记录则不允许操作
		mapContext.put(WebConstant.KEY_ENTITY_ID, designId);
		this.customOrderDesignService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.customOrderDesignService.findOneByDesignId(designId));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteDesign(String id, String designId) {
		//判断设计记录是否存在
		CustomOrderDesign customOrderDesign = this.customOrderDesignService.findById(designId);
		if (customOrderDesign == null || !customOrderDesign.getCustomOrderId().equals(id)) {
			return ResultFactory.generateSuccessResult();
		}
		//如果设计单的状态是已确定或者待确认则不允许删除
		if (customOrderDesign.getStatus().equals(OrderDesignStatus.CONFIRMED.getValue()) || customOrderDesign.getStatus().equals(OrderDesignStatus.UNCONFIRMED.getValue())) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_DELETION_AFTER_SUBMISSION_10079, AppBeanInjector.i18nUtil.getMessage("BIZ_NO_DELETION_AFTER_SUBMISSION_10079"));
		}
		this.customOrderDesignService.deleteById(designId);
		//删除相关资源
		this.customOrderFilesService.deleteByBelongId(designId, id);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult uploadFile(String id, String designId, List<MultipartFile> multipartFileList, Integer category) {
		//判断设计记录是否存在
		CustomOrderDesign customOrderDesign = this.customOrderDesignService.findById(designId);
		if (customOrderDesign == null || !customOrderDesign.getCustomOrderId().equals(id)) {
			return ResultFactory.generateResNotFoundResult();
		}
		CustomOrderFiles customOrderFiles = new CustomOrderFiles();
		customOrderFiles.setBelongId(designId);
		customOrderFiles.setCustomOrderId(id);
		customOrderFiles.setCategory(category);
		customOrderFiles.setStatus(CustomOrderFilesStatus.FORMAL.getValue());
		customOrderFiles.setType(CustomOrderFilesType.DESIGN.getValue());
		customOrderFiles.setCreated(DateUtil.getSystemDate());
		customOrderFiles.setCreator(WebUtils.getCurrUserId());
		List imgList = new ArrayList();
		for (MultipartFile multipartFile : multipartFileList) {
			UploadInfo uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, multipartFile, UploadResourceType.CUSTOM_ORDER, id, designId);
			customOrderFiles.setPath(uploadInfo.getRelativePath());
			customOrderFiles.setFullPath(uploadInfo.getRealPath());
			customOrderFiles.setName(uploadInfo.getFileName());
			customOrderFiles.setMime(uploadInfo.getFileMimeType().getRealType());
			customOrderFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
			this.customOrderFilesService.add(customOrderFiles);
			imgList.add(customOrderFiles);
		}
		return ResultFactory.generateRequestResult(imgList);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteFile(String id, String designId, String fileId) {
		//判断图片是否存在
		CustomOrderFiles customOrderFiles = this.customOrderFilesService.findById(fileId);
		if (customOrderFiles == null || !customOrderFiles.getBelongId().equals(designId) || !customOrderFiles.getCustomOrderId().equals(id)) {
			return ResultFactory.generateSuccessResult();
		}
		//删除资源
		AppBeanInjector.baseFileUploadComponent.deleteFileByDir(AppBeanInjector.configuration.getUploadFileRootDir().concat(customOrderFiles.getPath()));
		this.customOrderFilesService.deleteById(fileId);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult factoryAddOrder(CustomOrderInfoDto customOrderInfoDto, String aftersaleId, Payment payment) {
		CustomOrder customOrder = customOrderInfoDto.getCustomOrder();
		//如果存在父级订单 则判断是否存在
		if (customOrder.getParentId() != null) {
			if (this.customOrderService.findById(customOrder.getParentId()) == null) {
				return ResultFactory.generateResNotFoundResult();
			}
		}
		Company company = this.companyService.findById(customOrder.getCompanyId());
		//判断经销商是否存在 状态是否是正常
		if (company == null || !company.getStatus().equals(CompanyStatus.NORMAL.getValue())) {
			return ResultFactory.generateResNotFoundResult();
		}
		this.customOrderService.add(customOrder);
		//如果售后单ID不为空 则售后单补充订单ID
		if (aftersaleId != null) {
			//判断售后单是否存在
			AftersaleDto aftersaleDto = this.aftersaleApplyService.findOneById(aftersaleId);
			if (aftersaleDto == null) {
				return ResultFactory.generateResNotFoundResult();
			}
			MapContext mapContext = new MapContext();
			mapContext.put(WebConstant.KEY_ENTITY_ID, aftersaleId);
			mapContext.put("resultOrderId", customOrder.getId());
			mapContext.put(WebConstant.KEY_ENTITY_STATUS, AftersaleStatus.COMPLETED.getValue());
			this.aftersaleApplyService.updateByMapContext(mapContext);
		}
		//如果支付记录不为空 则生成支付记录
		if(payment!=null){
			payment.setCustomOrderId(customOrder.getId());
			this.paymentService.add(payment);
		}

		//生产拆单
		List<ProduceOrderDto> produceOrderDtos = customOrderInfoDto.getProduceOrderDtos();
		if(produceOrderDtos!=null&&produceOrderDtos.size()>0){
			//是否外协
			boolean isCoordination = true;
			for(ProduceOrderDto produceOrderDto :produceOrderDtos){
				produceOrderDto.setCustomOrderNo(customOrder.getNo());
				produceOrderDto.setCustomOrderId(customOrder.getId());
				produceOrderDto.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PURCHASE_REQUEST));
				produceOrderDto.setCreated(DateUtil.getSystemDate());
				produceOrderDto.setCreator(WebUtils.getCurrUserId());
				produceOrderDto.setPay(ProduceOrderPay.NOT_PAY.getValue());
				produceOrderDto.setState(ProduceOrderState.NOT_YET_BEGUN.getValue());
				RequestResult requestResult = produceOrderDto.validateFields();
				if(requestResult!=null){
					return requestResult;
				}
				//判断是否是外协生产单 如果是 就修改订单需要外协
				if(isCoordination){
					if(produceOrderDto.getWay().equals(ProduceOrderWay.COORDINATION.getValue())){
						isCoordination=false;
					}
				}
				this.produceOrderService.add(produceOrderDto);
			}
			if(!isCoordination){
				MapContext updateOrder = new MapContext();
				updateOrder.put(WebConstant.KEY_ENTITY_ID,customOrder.getId());
				updateOrder.put("coordination",CustomOrderCoordination.NEED_COORDINATION.getValue());
				this.customOrderService.updateByMapContext(updateOrder);
			}
		}

		//订单产品
		List<OrderProductDto> orderProducts = customOrderInfoDto.getOrderProducts();
		if(orderProducts!=null&&orderProducts.size()>0){
			for(OrderProductDto orderProductDto :orderProducts){
				orderProductDto.setCustomOrderId(customOrder.getId());
				orderProductDto.setCreated(DateUtil.getSystemDate());
				orderProductDto.setCreator(WebUtils.getCurrUserId());
				RequestResult requestResult = orderProductDto.validateFields();
				if(requestResult!=null){
					return requestResult;
				}
				this.orderProductService.add(orderProductDto);
			}
		}
		return ResultFactory.generateRequestResult(this.customOrderService.findByOrderId(customOrder.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult factoryUpdateOrder(String id, MapContext mapContext) {
		//判断订单是否存在
		CustomOrder customOrder = this.customOrderService.findById(id);
		if (customOrder == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID, id);
		this.customOrderService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.customOrderService.findByOrderId(id));
	}

	@Override
	public RequestResult findAmountInfo(String orderId) {
		//判断订单是否存在
		CustomOrder order = this.customOrderService.findById(orderId);
		if (order == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//获取经销商财务账户
		DealerAccount dealerAccount = this.dealerAccountService.findByCompanIdAndNatureAndType(order.getCompanyId(), DealerAccountNature.PUBLIC.getValue(), DealerAccountType.FREE_ACCOUNT.getValue());
		MapContext result = new MapContext();
		result.put("order", order);
		result.put("dealerAccount", dealerAccount);
		return ResultFactory.generateRequestResult(result);
	}

	/**
	 * 订单统计 （待优化）
	 * @param companyId
	 * @param request
	 * @param mapContext
	 * @return
	 */
	@Override
	public RequestResult customerOrderCount(String companyId, HttpServletRequest request, MapContext mapContext) {
		String uid = request.getHeader("X-UID");
		String cid = request.getHeader("X-CID");
		User user=this.userService.findByUserId(uid);
		if(user==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断登陆人职务
		Role role = this.roleService.findRoleByCidAndUid(uid, cid);
		if (role == null) {
			return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_NO_PERMISSION_10003, AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
		}
		//店员只能查看自己负责的订单
		if (role.getKey().equals(DealerEmployeeRole.CLERK.getValue())) {
			mapContext.put("salesMan", uid);
		}
		Map<String, Object> result = new HashMap<>();
		//订单状态统计
		Integer newOrder = OrderStatus.NEW_ORDER.getValue();//待处理0
		Integer inProduction = OrderStatus.IN_PRODUCTION.getValue();//生产中11
		Integer dispatching = OrderStatus.DISPATCHING.getValue();//配送中15
		Integer toSubmit = OrderStatus.TO_SUBMIT.getValue();//设计待确认6
		Integer received = OrderStatus.RECEIVED.getValue();//已完成16
		List<Integer> statusList = new ArrayList();
		statusList.add(newOrder);
		statusList.add(inProduction);
		statusList.add(dispatching);
		statusList.add(toSubmit);
		statusList.add(received);
		Integer status;
		for (int i = 0; i < statusList.size(); i++) {
			status = statusList.get(i);
			mapContext.put("orderStatus", status);
			Integer count = this.customOrderService.findOrderCountByStatus(mapContext);
			switch (status) {
				case 0:
					result.put("newOrder", count);
					break;
				case 6:
					result.put("toSubmit", count);
					break;
				case 11:
					result.put("inProduction", count);
					break;
				case 15:
					result.put("dispatching", count);
					break;
				case 16:
					result.put("received", count);
					break;
			}
		}
		mapContext.remove("orderStatus");
        //业务员业绩统计
		List<Map<String,Object>> numList=new ArrayList<>();
		List<Map<String,Object>> moneyList=new ArrayList<>();
		String saleName;
		Integer orderNum;
		BigDecimal orderMoney;
		if(role.getKey().equals(DealerEmployeeRole.CLERK.getValue())){//判断是否为员工
			Map<String,Object> orderNumMap=new HashMap<>();
			Map<String,Object> orderMoneyMap=new HashMap<>();
			saleName=user.getName();
			//判断员工是否有订单
			List<CustomOrder> customOrder = this.customOrderService.findByCidAndSalemanId(mapContext);
			if (customOrder.isEmpty()) {
				orderNumMap.put("saleName", saleName);
				orderNumMap.put("orderNum", 0);
				numList.add(orderNumMap);
				orderMoneyMap.put("saleName", saleName);
				orderMoneyMap.put("orderMoney", 0.00);
				moneyList.add(orderMoneyMap);
			}else {
				OrderCountDto orderCountDto = this.customOrderService.findOrderNumByUidAndCid(mapContext);
				orderNum = orderCountDto.getOrderNum();
				orderMoney = orderCountDto.getOrderMoney();
				orderNumMap.put("saleName", saleName);
				orderNumMap.put("orderNum", orderNum);
				numList.add(orderNumMap);
				orderMoneyMap.put("saleName", saleName);
				orderMoneyMap.put("orderMoney", orderMoney);
				moneyList.add(orderMoneyMap);
			}
		}
		else {
			//判断是否查询单个业务员
			if(mapContext.containsKey("saleMan")){
				if(mapContext.getTypedValue("saleMan",String.class)!=null||!mapContext.getTypedValue("saleMan",String.class).equals("")) {
					Map<String, Object> orderNumMap = new HashMap<>();
					Map<String, Object> orderMoneyMap = new HashMap<>();
					String saleManId = mapContext.getTypedValue("saleMan", String.class);
					User u = this.userService.findByUserId(saleManId);
					if (u == null) {
						return ResultFactory.generateResNotFoundResult();
					}
					saleName = u.getName();
					//判断员工是否有订单
					List<CustomOrder> customOrder = this.customOrderService.findByCidAndSalemanId(mapContext);
					if (customOrder.isEmpty()) {
						orderNumMap.put("saleName", saleName);
						orderNumMap.put("orderNum", 0);
						numList.add(orderNumMap);
						orderMoneyMap.put("saleName", saleName);
						orderMoneyMap.put("orderMoney", 0.00);
						moneyList.add(orderMoneyMap);
					}else {
						OrderCountDto orderCountDto = this.customOrderService.findOrderNumByUidAndCid(mapContext);
						orderNum = orderCountDto.getOrderNum();
						orderMoney = orderCountDto.getOrderMoney();
						orderNumMap.put("saleName", saleName);
						orderNumMap.put("orderNum", orderNum);
						numList.add(orderNumMap);
						orderMoneyMap.put("saleName", saleName);
						orderMoneyMap.put("orderMoney", orderMoney);
						moneyList.add(orderMoneyMap);
					}
				}
			}else {
				Integer employeeStatus = EmployeeStatus.NORMAL.getValue();
				mapContext.put("employeeStatus", employeeStatus);
				List<CompanyEmployee> employeeList = this.companyEmployeeService.findEmployeeListByCidAndStatus(mapContext);
				if (employeeList.isEmpty()) {
					Map<String, Object> orderNumMap = new HashMap<>();
					Map<String, Object> orderMoneyMap = new HashMap<>();
					saleName = "";
					orderNum = 0;
					orderMoney = new BigDecimal(0);
					orderNumMap.put("saleName", saleName);
					orderNumMap.put("orderNum", orderNum);
					numList.add(orderNumMap);
					orderMoneyMap.put("saleName", saleName);
					orderMoneyMap.put("orderMoney", orderMoney);
					moneyList.add(orderMoneyMap);
				} else {
					MapContext mapContext1 = MapContext.newOne();
					mapContext1.put("companyId", cid);
					//遍历员工列表
					for (CompanyEmployee companyEmployee : employeeList) {
						Map<String, Object> orderNumMap = new HashMap<>();
						Map<String, Object> orderMoneyMap = new HashMap<>();
						String saleManId = companyEmployee.getUserId();
						mapContext1.put("salesman",saleManId);
						if(mapContext.containsKey("selectTime")){
							mapContext1.put("selectTime",mapContext.getTypedValue("selectTime",String.class));
						}
						mapContext1.put("saleMan", saleManId);
						String saleManName = this.userService.findByUserId(saleManId).getName();
						//判断员工是否有订单
						List<CustomOrder> customOrder = this.customOrderService.findByCidAndSalemanId(mapContext);
						if (customOrder.isEmpty()) {
							orderNumMap.put("saleName", saleManName);
							orderNumMap.put("orderNum", 0);
							numList.add(orderNumMap);
							orderMoneyMap.put("saleName", saleManName);
							orderMoneyMap.put("orderMoney", 0.00);
							moneyList.add(orderMoneyMap);
						} else {
							OrderCountDto orderCountDto = this.customOrderService.findOrderNumByUidAndCid(mapContext1);
							orderNum = orderCountDto.getOrderNum();
							orderMoney = orderCountDto.getOrderMoney();
							orderNumMap.put("saleName", saleManName);
							orderNumMap.put("orderNum", orderNum);
							numList.add(orderNumMap);
							orderMoneyMap.put("saleName", saleManName);
							orderMoneyMap.put("orderMoney", orderMoney);
							moneyList.add(orderMoneyMap);
						}
					}
				}
			}
		}
		result.put("orderNumMap", numList);
		result.put("orderMoneyMap", moneyList);
		//订单金额统计
		result.put("oneGroup",getMoneyCount(mapContext,0,2000));//0-2000
		result.put("twoGroup",getMoneyCount(mapContext,2000,5000));//2000-5000
		result.put("threeGroup",getMoneyCount(mapContext,5000,10000));
		result.put("fourGroup",getMoneyCount(mapContext,10000,null));
		//支付统计
		Integer  finished=0;
		Integer noFinished=0;
			List<CustomOrder> customOrderList=this.customOrderService.findByCidAndSalemanId(mapContext);
			if(customOrderList.isEmpty()){
				finished=0;
				noFinished=0;
			}else {
				for(CustomOrder customOrder:customOrderList){
					String orderId=customOrder.getId();
					BigDecimal orderAmount=customOrder.getAmount();
					BigDecimal payAmount=this.customOrderService.findpayAmountByOrderId(orderId);
					if(payAmount==null){
						noFinished=noFinished+1;
					}else {
						int a = payAmount.compareTo(orderAmount);
						if (-1 == a) {
							noFinished = noFinished + 1;

						} else {
							finished = finished + 1;
						}
					}
				}
			}

		result.put("finished",finished);
		result.put("noFinished",noFinished);
		//每日订单增涨曲线
		List<DateNum> list=this.customOrderService.findOrderNumByCreatedAndCid(mapContext);
		result.put("dateNum",list);
		return ResultFactory.generateRequestResult(result);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addProduceOrder(String id, ProduceOrderDto produceOrder,List<String> fileIds) {
		CustomOrder customOrder = this.customOrderService.findById(id);
		if(customOrder==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//订单状态只有是 待生产 才可以新建生产订单
		if(customOrder.getStatus()>=OrderStatus.IN_PRODUCTION.getValue()){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
		}
		//判断是否存在订单产品 不存在则不允许新建生产单
		if(this.orderProductService.findListByOrderId(id).size()==0){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_PRODUCT_DOES_NOT_EXIST_10089,AppBeanInjector.i18nUtil.getMessage("BIZ_PRODUCT_DOES_NOT_EXIST_10089"));
		}

		produceOrder.setCustomOrderNo(customOrder.getNo());
		produceOrder.setCustomOrderId(customOrder.getId());
		produceOrder.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PURCHASE_REQUEST));
		produceOrder.setCreated(DateUtil.getSystemDate());
		produceOrder.setCreator(WebUtils.getCurrUserId());
		//如果是五金
		if(produceOrder.getType().equals(ProduceOrderType.HARDWARE.getValue())){
			produceOrder.setState(ProduceOrderState.COMPLETE.getValue());
			produceOrder.setWay(ProduceOrderWay.SELF_PRODUCED.getValue());
			//判断订单状态是否已付款
			if(customOrder.getStatus()>OrderStatus.TO_AUDIT.getValue()){
				produceOrder.setPay(ProduceOrderPay.PAY.getValue());
			}else{
				produceOrder.setPay(ProduceOrderPay.NOT_PAY.getValue());
			}
		}else if(produceOrder.getType().equals(ProduceOrderType.CABINET_BODY.getValue())){//如果是柜体
			produceOrder.setState(ProduceOrderState.NOT_YET_BEGUN.getValue());
			produceOrder.setWay(ProduceOrderWay.SELF_PRODUCED.getValue());
			//判断订单状态是否已付款
			if(customOrder.getStatus()>OrderStatus.TO_AUDIT.getValue()){
				produceOrder.setPay(ProduceOrderPay.PAY.getValue());
			}else{
				produceOrder.setPay(ProduceOrderPay.NOT_PAY.getValue());
			}
		}else{//门板
			if(produceOrder.getWay().equals(ProduceOrderWay.SELF_PRODUCED.getValue())){//如果是自产
				produceOrder.setState(ProduceOrderState.NOT_YET_BEGUN.getValue());
				//判断订单状态是否已付款
				if(customOrder.getStatus()>OrderStatus.TO_AUDIT.getValue()){
					produceOrder.setPay(ProduceOrderPay.PAY.getValue());
				}else{
					produceOrder.setPay(ProduceOrderPay.NOT_PAY.getValue());
				}
			}else if(produceOrder.getWay().equals(ProduceOrderWay.SPECIAL.getValue())){//如果是特供实木
				produceOrder.setState(ProduceOrderState.COMPLETE.getValue());
				//判断订单状态是否已付款
				if(customOrder.getStatus()>OrderStatus.TO_AUDIT.getValue()){
					produceOrder.setPay(ProduceOrderPay.PAY.getValue());
				}else{
					produceOrder.setPay(ProduceOrderPay.NOT_PAY.getValue());
				}
			}else{//如果是外协
				produceOrder.setState(ProduceOrderState.NOT_YET_BEGUN.getValue());
				produceOrder.setPay(ProduceOrderPay.NOT_PAY.getValue());
			}
		}
		RequestResult result = produceOrder.validateFields();
		if(result!=null){
			return result;
		}
		this.produceOrderService.add(produceOrder);
		//判断生产拆单是否是 外协
		if(produceOrder.getWay().equals(ProduceOrderWay.COORDINATION.getValue())){
			//判断订单是否是已经是外协
			if(customOrder.getCoordination()==CustomOrderCoordination.UNWANTED_COORDINATION.getValue()){
				MapContext updateOrder = new MapContext();
				updateOrder.put(WebConstant.KEY_ENTITY_ID,customOrder.getId());
				updateOrder.put("coordination",CustomOrderCoordination.NEED_COORDINATION.getValue());
				this.customOrderService.updateByMapContext(updateOrder);
			}
		}
		//修改图片资源
		if(fileIds.size()!=0){
			MapContext mapContext = new MapContext();
			mapContext.put(WebConstant.KEY_ENTITY_ID,fileIds);
			mapContext.put("belongId",produceOrder.getId());
			mapContext.put(WebConstant.KEY_ENTITY_STATUS,CustomOrderFilesStatus.FORMAL.getValue());
			this.customOrderFilesService.updateByIds(mapContext);
		}
		return ResultFactory.generateRequestResult(this.produceOrderService.findOneById(produceOrder.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addOrderProduct(String id, OrderProduct orderProduct) {
		//判断订单是否存在
		CustomOrder customOrder = this.customOrderService.findById(id);
		if(customOrder==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//获取订单编号头一位 判断是否是数字
		int num = customOrder.getNo().charAt(0);
		if(num>47&&num<58){//是的话表示当前订单使用的编号为临时编号需要修改
			MapContext updateOrder = new MapContext();
			updateOrder.put(WebConstant.KEY_ENTITY_ID,id);
			if(orderProduct.getType().equals(OrderProductType.WARDROBE.getValue())){
				updateOrder.put(WebConstant.STRING_NO,"B"+customOrder.getNo());
			}else if(orderProduct.getType().equals(OrderProductType.SAMPLE_PIECE.getValue())){
				updateOrder.put(WebConstant.STRING_NO,"Y"+customOrder.getNo());
			}else{
				updateOrder.put(WebConstant.STRING_NO,"J"+customOrder.getNo());
			}
			this.customOrderService.updateByMapContext(updateOrder);
		}
		//订单状态只有是 未开始生产的 才可以新建生产订单
		if(customOrder.getStatus()>=OrderStatus.IN_PRODUCTION.getValue()){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
		}
		this.orderProductService.add(orderProduct);
		return ResultFactory.generateRequestResult(this.orderProductService.findOneById(orderProduct.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateOrderProduct(String id, String pId, MapContext mapContext) {
		//判断订单产品 以及订单是否存下
		OrderProduct orderProduct = this.orderProductService.findById(pId);
		if(orderProduct==null||!orderProduct.getCustomOrderId().equals(id)){
			return ResultFactory.generateResNotFoundResult();
		}
		CustomOrder customOrder = this.customOrderService.findById(id);
		//判断订单状态是否是 待生产
		if(customOrder.getStatus()>=OrderStatus.IN_PRODUCTION.getValue()){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID,pId);
		this.orderProductService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.orderProductService.findOneById(pId));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteOrderProduct(String id, String pId) {
		//判断订单产品 以及订单是否存下
		OrderProduct orderProduct = this.orderProductService.findById(pId);
		if(orderProduct==null||!orderProduct.getCustomOrderId().equals(id)){
			return ResultFactory.generateSuccessResult();
		}
		CustomOrder customOrder = this.customOrderService.findById(id);
		//判断订单状态是否是 待生产
		if(customOrder.getStatus()>=OrderStatus.IN_PRODUCTION.getValue()){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
		}
		this.orderProductService.deleteById(pId);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateProduceOrder(String id, String pId, MapContext mapContext) {
		//判断生产拆单是否存在 订单是否存在
		ProduceOrder produceOrder = this.produceOrderService.findById(pId);
		if(produceOrder==null||!produceOrder.getCustomOrderId().equals(id)){
			return ResultFactory.generateResNotFoundResult();
		}
		CustomOrder customOrder = this.customOrderService.findById(id);
		//判断订单状态是否是 待生产
		if(!customOrder.getStatus().equals(OrderStatus.TO_PRODUCTION.getValue())){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID,pId);
		this.produceOrderService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.produceOrderService.findOneById(pId));
	}

	@Override
	public RequestResult deleteProduceOrder(String id, String pId) {
		//判断生产拆单是否存在 订单是否存在
		ProduceOrder produceOrder = this.produceOrderService.findById(pId);
		if(produceOrder==null||!produceOrder.getCustomOrderId().equals(id)){
			return ResultFactory.generateSuccessResult();
		}
		CustomOrder customOrder = this.customOrderService.findById(id);
		//判断订单状态是否是 待生产
		if(!customOrder.getStatus().equals(OrderStatus.TO_PRODUCTION.getValue())){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
		}
		this.produceOrderService.deleteById(pId);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findProducesList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		mapContext.put("funds",PaymentFunds.ORDER_FEE_CHARGE.getValue());
		paginatedFilter.setFilters(mapContext);
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		List<Map<String,String>> sorts = new ArrayList<Map<String, String>>();
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sorts.add(created);
		paginatedFilter.setSorts(sorts);
		return ResultFactory.generateRequestResult(this.produceOrderService.findListByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult producesPlansListStart(List<String> ids) {
		List<ProduceOrder> produceOrderList = this.produceOrderService.findListByIds(ids);
		if(produceOrderList.size()==0){
			return ResultFactory.generateResNotFoundResult();
		}
		//所选择的生产单属于哪些订单
		Set orderIds = new HashSet<String>();
		for(ProduceOrder produceOrder:produceOrderList){
			//判断普通生产单是否存在 生产中 或者 生产完成的 或者 没有生产计划的
			if(!produceOrder.getWay().equals(ProduceOrderWay.COORDINATION.getValue())&&(!produceOrder.getState().equals(ProduceOrderState.NOT_YET_BEGUN.getValue())||produceOrder.getPlannedTime()==null)){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
			}
			//如果是外协的话 判断是否存在 生产中 或者 生产完成的
			if(produceOrder.getWay().equals(ProduceOrderWay.COORDINATION.getValue())&&(!produceOrder.getState().equals(ProduceOrderState.NOT_YET_BEGUN.getValue()))){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
			}
			//获取生产的订单ID
			orderIds.add(produceOrder.getCustomOrderId());
		}
		List<CustomOrder> customOrders = this.customOrderService.findByIds(orderIds);
		//需要修改订单状态的 订单Id集合
		List startOrderIds = new ArrayList();
		for(CustomOrder customOrder:customOrders){
			if(customOrder.getStatus().equals(OrderStatus.TO_PRODUCTION.getValue())){
				startOrderIds.add(customOrder.getId());
			}
		}
		//批量修改订单状态
		if(startOrderIds.size()>0){
			this.customOrderService.updateOrderStatusByIds(startOrderIds,OrderStatus.IN_PRODUCTION.getValue());
		}
		this.produceOrderService.updateStateByIds(ids,ProduceOrderState.IN_PRODUCTION.getValue());
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addProduceFlow(String id, ProduceFlow produceFlow) {
		//判断生产单状态是否存在 是否是生产中
		ProduceOrder produceOrder = this.produceOrderService.findById(id);
		if(produceOrder==null||!produceOrder.getState().equals(ProduceOrderState.IN_PRODUCTION.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		//包装节点 单独调用另一接口
		if(produceFlow.getNode().equals(ProduceFlowNode.PACKING.getValue())){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		//判断该节点是否已存在
		ProduceFlow existFlow = this.produceFlowService.findOneByProduceIdAndNode(id,produceFlow.getNode());
		if (existFlow!=null){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		this.produceFlowService.add(produceFlow);
		return ResultFactory.generateRequestResult(this.produceFlowService.findOneById(produceFlow.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult produceFlowPacking(String id, ProduceFlow produceFlow) {
		//判断生产单是否存在
		ProduceOrder produceOrder = this.produceOrderService.findById(id);
		// 生产单 是否存在 是否是生产中
		if(produceOrder==null||!produceOrder.getState().equals(ProduceOrderState.IN_PRODUCTION.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		this.produceFlowService.add(produceFlow);
		//修改生产单状态为 已完成
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		mapContext.put(WebConstant.KEY_ENTITY_STATE,ProduceOrderState.COMPLETE.getValue());
		mapContext.put("completionTime",DateUtil.getSystemDate());
		this.produceOrderService.updateByMapContext(mapContext);
		//如果订单状态为 生产中 则修改为待入库
		CustomOrder customOrder = this.customOrderService.findById(produceOrder.getCustomOrderId());
		if (customOrder.getStatus().equals(OrderStatus.IN_PRODUCTION.getValue())){
			MapContext updateOrder = new MapContext();
			updateOrder.put(WebConstant.KEY_ENTITY_ID,customOrder.getId());
			updateOrder.put(WebConstant.KEY_ENTITY_STATUS,OrderStatus.TO_WAREHOUSE.getValue());
			this.customOrderService.updateByMapContext(updateOrder);
		}
		return ResultFactory.generateRequestResult(this.produceFlowService.findOneById(produceFlow.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addOrderPack(String id, List<FinishedStockItemDto> finishedStockItemDtoList) {
		//判断订单是否存在
		CustomOrder order = this.customOrderService.findById(id);
		if(order==null){
			return ResultFactory.generateResNotFoundResult();
		}
		FinishedStock oldFinishedStock = this.finishedStockService.findByOrderId(id);
		String finishedStockId;
		//判断成品库单是否存在
		if(oldFinishedStock==null){
			FinishedStock finishedStock = new FinishedStock();
			finishedStock.setCreated(DateUtil.getSystemDate());
			finishedStock.setCreator(WebUtils.getCurrUserId());
			finishedStock.setOrderId(id);
			finishedStock.setPackages(finishedStockItemDtoList.size()+1);
			finishedStock.setOrderNo(order.getNo());
			finishedStock.setStatus(FinishedStockStatus.UNSHIPPED.getValue());
			//查询成品仓
			Storage storage = this.storageService.findOneByProductCategoryId(ProductCategoryId.finished.getId());
			finishedStock.setStorageId(storage.getId());
			finishedStock.setWay(FinishedStockWay.MANUAL.getValue());
			RequestResult result = finishedStock.validateFields();
			if(result!=null){
				return result;
			}
			this.finishedStockService.add(finishedStock);
			finishedStockId = finishedStock.getId();
		}else{
			finishedStockId=oldFinishedStock.getId();
			//查询已经存在包裹数
			List<FinishedStockItemDto> listByFinishedStockId = this.finishedStockItemService.findListByFinishedStockId(finishedStockId);
			//如果已存在的包裹数 等于 成品库中 的包装数 则 代表着 已全部入库 不允许再次入库
			if(listByFinishedStockId.size()==oldFinishedStock.getPackages()){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
			}
			MapContext mapContext = new MapContext();
			mapContext.put(WebConstant.KEY_ENTITY_ID,finishedStockId);
			mapContext.put("packages",listByFinishedStockId.size()+finishedStockItemDtoList.size()+1);
			this.finishedStockService.updateByMapContext(mapContext);
		}
		for(FinishedStockItemDto finishedStockItem:finishedStockItemDtoList){
			finishedStockItem.setFinishedStockId(finishedStockId);
			finishedStockItem.setShipped(false);
			finishedStockItem.setIn(false);
			RequestResult requestResult = finishedStockItem.validateFields();
			if(requestResult!=null){
				return requestResult;
			}
			//判断包裹编号是否重复
			if(this.finishedStockItemService.findListByBarcodes(new HashSet(Arrays.asList(finishedStockItem.getBarcode()))).size()!=0){
				Map res = new HashMap<String,String>();
				res.put("barcodes",AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
				return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,res);
			}
			this.finishedStockItemService.add(finishedStockItem);
			//修改图片资源
			if(finishedStockItem.getFileIds().size()!=0){
				MapContext mapContext = new MapContext();
				mapContext.put(WebConstant.KEY_ENTITY_ID,finishedStockItem.getFileIds());
				mapContext.put("belongId",finishedStockItem.getId());
				mapContext.put(WebConstant.KEY_ENTITY_STATUS,CustomOrderFilesStatus.FORMAL.getValue());
				this.customOrderFilesService.updateByIds(mapContext);
			}
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult orderPacked(String id) {
		//判断订单是否存在
		CustomOrder order = this.customOrderService.findById(id);
		if(order==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断是否存在未完成生产单
		List<ProduceOrder> incompleteListByOrderId = this.produceOrderService.findIncompleteListByOrderId(id);
		if (incompleteListByOrderId.size()!=0){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		//判断 成品库单 是否存在
		FinishedStock finishedStock = this.finishedStockService.findByOrderId(id);
		if (finishedStock==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断是否已确认打包完成
		List<FinishedStockItemDto> listByFinishedStockId = this.finishedStockItemService.findListByFinishedStockId(finishedStock.getId());
		if(finishedStock.getPackages()==listByFinishedStockId.size()){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID,finishedStock.getId());
		mapContext.put("packages",listByFinishedStockId.size());
		this.finishedStockService.updateByMapContext(mapContext);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findPacksOrderList(Integer pageNum, Integer pageSize, MapContext mapContext) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		mapContext.put(WebConstant.KEY_ENTITY_STATUS,OrderStatus.TO_PRODUCTION.getValue());
		mapContext.put(WebConstant.KEY_ENTITY_FUNDS,PaymentFunds.ORDER_FEE_CHARGE.getValue());
		mapContext.put("payStatus",PaymentStatus.PAID.getValue());
		paginatedFilter.setFilters(mapContext);
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		List<Map<String,String>> sort = new ArrayList<Map<String, String>>();
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		Map<String,String> id = new HashMap<String, String>();
		id.put(WebConstant.KEY_ENTITY_ID,"desc");
		sort.add(created);
		sort.add(id);
		paginatedFilter.setSorts(sort);
		paginatedFilter.setPagination(pagination);
		return ResultFactory.generateRequestResult(this.customOrderService.findPacksOrderList(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateProducesOrderPlanTime(List ids,Date planTime) {
		this.produceOrderService.updatePlanTimeByIds(planTime,ids);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult endCoordination(String id) {
		//判断生产单是否存在
		ProduceOrder produceOrder = this.produceOrderService.findById(id);
		// 生产单 是否存在 是否是生产中
		if(produceOrder==null||!produceOrder.getState().equals(ProduceOrderState.IN_PRODUCTION.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		//修改生产单状态为 已完成
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		mapContext.put(WebConstant.KEY_ENTITY_STATE,ProduceOrderState.COMPLETE.getValue());
		mapContext.put("completionTime",DateUtil.getSystemDate());
		this.produceOrderService.updateByMapContext(mapContext);
		//如果订单状态为 生产中 则修改为待入库
		CustomOrder customOrder = this.customOrderService.findById(produceOrder.getCustomOrderId());
		if (customOrder.getStatus().equals(OrderStatus.IN_PRODUCTION.getValue())){
			MapContext updateOrder = new MapContext();
			updateOrder.put(WebConstant.KEY_ENTITY_ID,customOrder.getId());
			updateOrder.put(WebConstant.KEY_ENTITY_STATUS,OrderStatus.TO_WAREHOUSE.getValue());
			this.customOrderService.updateByMapContext(updateOrder);
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findAllDesign(MapContext mapContext,Integer pageNum,Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		paginatedFilter.setFilters(mapContext);
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		return ResultFactory.generateRequestResult(this.customOrderDesignService.findListByFilter(paginatedFilter));
	}

	@Override
	public RequestResult findOrderPackagesNo(String id) {
		//判断订单是否存在
		CustomOrder order = this.customOrderService.findById(id);
		if(order==null){
			return ResultFactory.generateResNotFoundResult();
		}
		return ResultFactory.generateRequestResult(AppBeanInjector.uniquneCodeGenerator.getPackageNextNo(order.getNo()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult uploadOrderFiles(String id, Integer type, String resId, List<MultipartFile> multipartFileList) {
		CustomOrderFiles customOrderFiles = new CustomOrderFiles();
		//判断订单以及资源是否存在
		switch (CustomOrderFilesType.getByValue(type)){
			case ORDER_PRODUCT:
				OrderProduct byIdOrderProduct = this.orderProductService.findById(resId);
				if(byIdOrderProduct==null||!byIdOrderProduct.getCustomOrderId().equals(id)){
					return ResultFactory.generateResNotFoundResult();
				}
				customOrderFiles.setBelongId(resId);
				customOrderFiles.setStatus(CustomOrderFilesStatus.FORMAL.getValue());
				break;
			case PRODUCE_ORDER:
				if(!resId.equals("produceId")){
					ProduceOrder orderServiceById = this.produceOrderService.findById(resId);
					if(orderServiceById==null||!orderServiceById.getCustomOrderId().equals(id)){
						return ResultFactory.generateResNotFoundResult();
					}
					customOrderFiles.setBelongId(resId);
					customOrderFiles.setStatus(CustomOrderFilesStatus.FORMAL.getValue());
				}else{
					customOrderFiles.setBelongId(null);
					customOrderFiles.setStatus(CustomOrderFilesStatus.TEMP.getValue());
				}
				break;
			default:
				return ResultFactory.generateResNotFoundResult();
		}
		customOrderFiles.setCustomOrderId(id);
		customOrderFiles.setCategory(CustomOrderFilesCategory.ACCESSORY.getValue());
		customOrderFiles.setType(type);
		customOrderFiles.setCreated(DateUtil.getSystemDate());
		customOrderFiles.setCreator(WebUtils.getCurrUserId());
		List imgList = new ArrayList();
		for (MultipartFile multipartFile : multipartFileList) {
			UploadInfo uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, multipartFile, UploadResourceType.CUSTOM_ORDER, id, resId);
			customOrderFiles.setPath(uploadInfo.getRelativePath());
			customOrderFiles.setFullPath(uploadInfo.getRealPath());
			customOrderFiles.setName(uploadInfo.getFileName());
			customOrderFiles.setMime(uploadInfo.getFileMimeType().getRealType());
			customOrderFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
			this.customOrderFilesService.add(customOrderFiles);
			imgList.add(customOrderFiles);
		}
		return ResultFactory.generateRequestResult(imgList);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteCustomOrderFile(String id, String fileId) {
		//判断资源文件 以及订单是否存在
		CustomOrderFiles byId = this.customOrderFilesService.findById(fileId);
		if(byId==null||!byId.getCustomOrderId().equals(id)){
			return ResultFactory.generateSuccessResult();
		}
		//清除本地文件
		AppBeanInjector.baseFileUploadComponent.deleteFileByDir(AppBeanInjector.configuration.getUploadFileRootDir().concat(byId.getPath()));
		//清除数据库文件
		this.customOrderFilesService.deleteById(fileId);
		return ResultFactory.generateSuccessResult();
	}

	public Integer getMoneyCount(MapContext mapContext,Integer low,Integer high){
		mapContext.put("low",low);
		mapContext.put("high",high);
		Integer result=this.customOrderService.findOrderMoneyCount(mapContext);
		return result;
	}
}

