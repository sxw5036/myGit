package com.lwxf.industry4.webapp.facade.admin.factory.dealer.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.*;

import com.lwxf.industry4.webapp.domain.dto.dispatch.DispatchBillDto;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBill;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.collections.map.HashedMap;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyFilesService;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyService;
import com.lwxf.industry4.webapp.bizservice.common.UploadFilesService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.customer.CompanyCustomerService;
import com.lwxf.industry4.webapp.bizservice.customorder.*;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountLogService;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillItemService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillPlanItemService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.system.BasecodeService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockItemService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageService;
import com.lwxf.industry4.webapp.common.aop.syslog.OperationMoudule;
import com.lwxf.industry4.webapp.common.aop.syslog.OperationType;
import com.lwxf.industry4.webapp.common.aop.syslog.SysOperationLog;
import com.lwxf.industry4.webapp.common.component.BaseFileUploadComponent;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.aftersale.AftersaleStatus;
import com.lwxf.industry4.webapp.common.enums.company.*;
import com.lwxf.industry4.webapp.common.enums.customorder.*;
import com.lwxf.industry4.webapp.common.enums.financing.*;
import com.lwxf.industry4.webapp.common.enums.order.*;
import com.lwxf.industry4.webapp.common.enums.payment.PaymentResourceType;
import com.lwxf.industry4.webapp.common.enums.product.ProductCategoryKey;
import com.lwxf.industry4.webapp.common.enums.storage.FinishedStockStatus;
import com.lwxf.industry4.webapp.common.enums.storage.FinishedStockWay;
import com.lwxf.industry4.webapp.common.enums.user.UserState;
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
import com.lwxf.industry4.webapp.domain.dto.customorder.*;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.OrderPrintTableDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockItemDto;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApply;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApplyFiles;
import com.lwxf.industry4.webapp.domain.entity.common.UploadFiles;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.customer.CompanyCustomer;
import com.lwxf.industry4.webapp.domain.entity.customorder.*;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.product.Product;
import com.lwxf.industry4.webapp.domain.entity.system.Basecode;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStock;
import com.lwxf.industry4.webapp.domain.entity.warehouse.Storage;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.dealer.OrderFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;

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
	@Resource(name = "storageService")
	private StorageService storageService;
	@Resource(name = "finishedStockService")
	private FinishedStockService finishedStockService;
	@Resource(name = "finishedStockItemService")
	private FinishedStockItemService finishedStockItemService;
	@Resource(name = "uploadFilesService")
	private UploadFilesService uploadFilesService;
	@Resource(name = "dispatchBillService")
	private DispatchBillService dispatchBillService;
	@Resource(name = "dispatchBillItemService")
	private DispatchBillItemService dispatchBillItemService;
	@Resource(name = "dispatchBillPlanItemService")
	private DispatchBillPlanItemService dispatchBillPlanItemService;
	@Resource(name = "aftersaleApplyFilesService")
	private AftersaleApplyFilesService aftersaleApplyFilesService;
	@Resource(name = "orderOfferService")
	private OrderOfferService orderOfferService;
	@Resource(name = "offerItemService")
	private OfferItemService offerItemService;
	@Resource(name = "basecodeService")
	private BasecodeService basecodeService;
	@Resource(name = "companyCustomerService")
	private CompanyCustomerService companyCustomerService;
	@Resource(name = "customOrderLogService")
	private CustomOrderLogService customOrderLogService;


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
		Role manager = this.roleService.selectByKey(DealerEmployeeRole.MANAGER.getValue(), WebUtils.getCurrBranchId());
		//店主
		Role shopkeeper = this.roleService.selectByKey(DealerEmployeeRole.SHOPKEEPER.getValue(), WebUtils.getCurrBranchId());
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
	@SysOperationLog(detail = "新建订单",operationType = OperationType.INSERT,operationMoudule = OperationMoudule.CUSTOM_ORDER)
	public RequestResult addOrder(CustomOrder customOrder, String uid, String cid) {
		Integer type = customOrder.getType();
		boolean contains = OrderType.contains(type);
		if (!contains) {
			MapContext result = new MapContext();
			result.put("type", ErrorCodes.VALIDATE_ILLEGAL_ARGUMENT);
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
	@SysOperationLog(detail = "修改订单信息",operationType = OperationType.UPDATE,operationMoudule = OperationMoudule.CUSTOM_ORDER)
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
	@SysOperationLog(detail = "删除订单",operationType = OperationType.DELETE,operationMoudule = OperationMoudule.CUSTOM_ORDER)
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
	 *
	 * @param params
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult commitOrderDemand(MapContext params) {
		String id = params.getTypedValue("id", String.class);

		CustomOrder customOrder = this.customOrderService.findById(id);
		if (null == customOrder) {
			return ResultFactory.generateResNotFoundResult();
		}

		Integer status = customOrder.getStatus();
		switch (status) {
			case 0:
				status = 1;
				break;
		}

		params.put("status", status);
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
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		paginatedFilter.setFilters(mapContext);
		List<Map<String, String>> sorts = new ArrayList<Map<String, String>>();
		Map<String, String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED, "desc");
		sorts.add(created);
		paginatedFilter.setSorts(sorts);
		return ResultFactory.generateRequestResult(this.customOrderService.findFinishedOrderList(paginatedFilter));
	}

	@Override
	public RequestResult findOrderList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		mapContext.put("funds",PaymentFunds.ORDER_FEE_CHARGE.getValue());
		paginatedFilter.setFilters(mapContext);
		Map<String, String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED, "desc");
		List sort = new ArrayList();
		sort.add(created);
		paginatedFilter.setSorts(sort);
		PaginatedList<CustomOrderDto> listByPaginateFilter = this.customOrderService.findListByPaginateFilter(paginatedFilter);
		if(listByPaginateFilter.getRows().size()>0) {
			for (CustomOrderDto customOrderDto:listByPaginateFilter.getRows()){
				String orderId=customOrderDto.getId();
				List<OrderProductDto> productDtos=this.orderProductService.findListByOrderId(orderId);
				customOrderDto.setOrderProductDtoList(productDtos);
			}
		}
		return ResultFactory.generateRequestResult(listByPaginateFilter);
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "修改设计方案",operationType = OperationType.UPDATE,operationMoudule = OperationMoudule.DESIGN)
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
		customOrderInfoDto.setOrderOffer(this.orderOfferService.findByOrerId(id));
		if (customOrderInfoDto.getOrderOffer() != null) {
			customOrderInfoDto.setOfferItems(this.offerItemService.findByOfferId(customOrderInfoDto.getOrderOffer().getId()));
		}
		return ResultFactory.generateRequestResult(customOrderInfoDto);
	}

	@Override
	public RequestResult findOrderInfoNew(String id) {
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
		List<OrderProductDto> productsList = this.orderProductService.findListByOrderId(id);
		for(OrderProductDto dto : productsList){
			List<ProduceOrderDto> produceOrderList= this.produceOrderService.findProduceOrderByProductId(dto.getId());
			for(ProduceOrderDto po : produceOrderList){
				po.setProduceFlowDtos(produceFlowService.findListByProduceOrderId(po.getId()));
			}
			dto.setProduceOrderList(produceOrderList);
		}
		customOrderInfoDto.setDispatchBill(dispatchBillService.findDispatchInfoForOrder(id));
		customOrderInfoDto.setOrderProducts(productsList);
		customOrderInfoDto.setProduceOrderDtos(this.produceOrderService.findListByOrderId(id));
		customOrderInfoDto.setOrderOffer(this.orderOfferService.findByOrerId(id));
		if (customOrderInfoDto.getOrderOffer() != null) {
			customOrderInfoDto.setOfferItems(this.offerItemService.findByOfferId(customOrderInfoDto.getOrderOffer().getId()));
		}
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
	@SysOperationLog(detail = "新增设计方案",operationType = OperationType.INSERT,operationMoudule = OperationMoudule.DESIGN)
	public RequestResult addOrderDesign(String id, CustomOrderDesign customOrderDesign) {
		//判断订单是否存在
		CustomOrder customOrder = this.customOrderService.findById(id);
		if (customOrder == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//判断产品是否存在
		OrderProduct orderProduct = this.orderProductService.findById(customOrderDesign.getOrderProductId());
		if (orderProduct == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		this.customOrderDesignService.add(customOrderDesign);
		return ResultFactory.generateRequestResult(this.customOrderDesignService.findOneByDesignId(customOrderDesign.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "修改设计方案",operationType = OperationType.UPDATE,operationMoudule = OperationMoudule.DESIGN)
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
	@SysOperationLog(detail = "删除设计方案",operationType = OperationType.DELETE,operationMoudule = OperationMoudule.DESIGN)
	public RequestResult deleteDesign(String id, String designId) {
		//判断设计记录是否存在
		CustomOrderDesign customOrderDesign = this.customOrderDesignService.findById(designId);
		if (customOrderDesign == null || !customOrderDesign.getCustomOrderId().equals(id)) {
			return ResultFactory.generateSuccessResult();
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
			customOrderFiles.setFullPath(AppBeanInjector.configuration.getDomainUrl() + uploadInfo.getRelativePath());
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
	@SysOperationLog(detail = "新增订单",operationType = OperationType.INSERT,operationMoudule = OperationMoudule.CUSTOM_ORDER)
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
		//判断跟单员(厂家) 和接单员(经销商) 是否真实存在
		User merchandiser = this.userService.findById(customOrderInfoDto.getCustomOrder().getMerchandiser());
		if (merchandiser == null || !merchandiser.getState().equals(UserState.ENABLED.getValue())) {
			return ResultFactory.generateResNotFoundResult();
		}
//		User saleman = this.userService.findById(customOrderInfoDto.getCustomOrder().getSalesman());
//		if(saleman==null||!saleman.getState().equals(UserState.ENABLED.getValue())){
//			return ResultFactory.generateResNotFoundResult();
//		}
		//判断客户是否存在
		MapContext filter = new MapContext();
		filter.put(WebConstant.KEY_ENTITY_NAME,customOrderInfoDto.getCompanyCustomer().getName());
		filter.put(WebConstant.KEY_ENTITY_COMPANY_ID,WebUtils.getCurrCompanyId());
		List<CompanyCustomer> customerByMap = this.companyCustomerService.findCustomerByMap(filter);
		if(customerByMap==null||customerByMap.size()==0){
			CompanyCustomer companyCustomer = new CompanyCustomer();
			companyCustomer.setName(customOrderInfoDto.getCompanyCustomer().getName());
			companyCustomer.setCompanyId(company.getId());
			companyCustomer.setCreated(DateUtil.getSystemDate());
			companyCustomer.setCreator(WebUtils.getCurrUserId());
			companyCustomer.setStatus(CompanyCustomerStatus.ORDER.getValue());
			this.companyCustomerService.add(companyCustomer);
			customOrder.setCustomerId(companyCustomer.getId());
		}else{
			customOrder.setCustomerId(customerByMap.get(0).getId());
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
		if (payment != null) {
			payment.setCustomOrderId(customOrder.getId());
			this.paymentService.add(payment);
		}

		//生产拆单
		List<ProduceOrderDto> produceOrderDtos = customOrderInfoDto.getProduceOrderDtos();
		if (produceOrderDtos != null && produceOrderDtos.size() > 0) {
			//是否外协
			boolean isCoordination = true;
			for (ProduceOrderDto produceOrderDto : produceOrderDtos) {
				produceOrderDto.setCustomOrderNo(customOrder.getNo());
				produceOrderDto.setCustomOrderId(customOrder.getId());
				produceOrderDto.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PURCHASE_REQUEST));
				produceOrderDto.setCreated(DateUtil.getSystemDate());
				produceOrderDto.setCreator(WebUtils.getCurrUserId());
				produceOrderDto.setPay(ProduceOrderPay.NOT_PAY.getValue());
				//如果是五金
				if (produceOrderDto.getType().equals(ProduceOrderType.HARDWARE.getValue())) {
					produceOrderDto.setState(ProduceOrderState.COMPLETE.getValue());
					produceOrderDto.setWay(ProduceOrderWay.SELF_PRODUCED.getValue());
				} else if (produceOrderDto.getType().equals(ProduceOrderType.CABINET_BODY.getValue())) {//如果是柜体
					produceOrderDto.setState(ProduceOrderState.NOT_YET_BEGUN.getValue());
					produceOrderDto.setWay(ProduceOrderWay.SELF_PRODUCED.getValue());
				} else {//门板
					if (produceOrderDto.getWay().equals(ProduceOrderWay.SELF_PRODUCED.getValue())) {//如果是自产
						produceOrderDto.setState(ProduceOrderState.NOT_YET_BEGUN.getValue());
					} else if (produceOrderDto.getWay().equals(ProduceOrderWay.SPECIAL.getValue())) {//如果是特供实木
						produceOrderDto.setState(ProduceOrderState.COMPLETE.getValue());
					} else {//如果是外协
						produceOrderDto.setState(ProduceOrderState.NOT_YET_BEGUN.getValue());
					}
				}
				RequestResult requestResult = produceOrderDto.validateFields();
				if (requestResult != null) {
					return requestResult;
				}
				//判断是否是外协生产单 如果是 就修改订单需要外协
				if (isCoordination) {
					if (produceOrderDto.getWay().equals(ProduceOrderWay.COORDINATION.getValue())) {
						isCoordination = false;
					}
				}
				this.produceOrderService.add(produceOrderDto);
			}
			if (!isCoordination) {
				MapContext updateOrder = new MapContext();
				updateOrder.put(WebConstant.KEY_ENTITY_ID, customOrder.getId());
				updateOrder.put("coordination", CustomOrderCoordination.NEED_COORDINATION.getValue());
				this.customOrderService.updateByMapContext(updateOrder);
			}
		}

		//订单产品
		List<OrderProductDto> orderProducts = customOrderInfoDto.getOrderProducts();
		if (orderProducts != null && orderProducts.size() > 0) {
			boolean updateOrderNo = true;
			for (OrderProductDto orderProductDto : orderProducts) {
				orderProductDto.setCustomOrderId(customOrder.getId());
				this.orderProductService.add(orderProductDto);
				if (orderProductDto.getFileIds().size() != 0) {
					MapContext updateFile = new MapContext();
					updateFile.put(WebConstant.KEY_ENTITY_ID, orderProductDto.getFileIds());
					updateFile.put("belongId", orderProductDto.getId());
					updateFile.put(WebConstant.KEY_ENTITY_STATUS, CustomOrderFilesStatus.FORMAL.getValue());
					updateFile.put("customOrderId", customOrder.getId());
					this.customOrderFilesService.updateByIds(updateFile);
				}
				//判断产品的 门板颜色 和 门型 和 柜体颜色 是否存在 不存在 则新增
				if(orderProductDto.getType()==0||orderProductDto.getType()==1) {
					Basecode doorColor = this.basecodeService.findByTypeAndValue("orderProductDoorColor", orderProductDto.getDoorColor());
					if (doorColor == null) {
						List<Basecode> basecodeList = this.basecodeService.findByType("orderProductDoorColor");
						Basecode basecode = new Basecode();
						basecode.setValue(orderProductDto.getDoorColor());
						basecode.setType("orderProductDoorColor");
						basecode.setOrderNum(basecodeList.size());
						basecode.setName("订单产品门板颜色");
						basecode.setDelFlag(0);
						basecode.setCode(basecodeList.size() + "");
						this.basecodeService.add(basecode);
					}
					Basecode door = this.basecodeService.findByTypeAndValue("orderProductDoor", orderProductDto.getDoor());
					if (door == null) {
						List<Basecode> basecodeList = this.basecodeService.findByType("orderProductDoor");
						Basecode basecode = new Basecode();
						basecode.setValue(orderProductDto.getDoor());
						basecode.setType("orderProductDoor");
						basecode.setOrderNum(basecodeList.size());
						basecode.setName("订单产品门型");
						basecode.setDelFlag(0);
						basecode.setCode(basecodeList.size() + "");
						this.basecodeService.add(basecode);
					}
					Basecode bodyColor = this.basecodeService.findByTypeAndValue("orderProductBodyColor", orderProductDto.getBodyColor());
					if (bodyColor == null) {
						List<Basecode> basecodeList = this.basecodeService.findByType("orderProductBodyColor");
						Basecode basecode = new Basecode();
						basecode.setValue(orderProductDto.getBodyColor());
						basecode.setType("orderProductBodyColor");
						basecode.setOrderNum(basecodeList.size());
						basecode.setName("订单产品柜体颜色");
						basecode.setDelFlag(0);
						basecode.setCode(basecodeList.size() + "");
						this.basecodeService.add(basecode);
					}
				}
			}
			//修改订单的最终报价
			MapContext mapContext = new MapContext();
			mapContext.put(WebConstant.KEY_ENTITY_ID, customOrder.getId());
			BigDecimal price = this.orderProductService.findCountPriceByOrderId(customOrder.getId());
			mapContext.put("factoryFinalPrice", price);
			mapContext.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.TO_PAID.getValue());
			this.customOrderService.updateByMapContext(mapContext);
		}
		//支付记录信息生成
		payment = new Payment();
		String userId=WebUtils.getCurrUserId();
		String userName=AppBeanInjector.userService.findByUserId(userId).getName();
		payment.setHolder(userName);
		payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
		payment.setAmount(customOrder.getFactoryFinalPrice());
		payment.setCompanyId(customOrder.getCompanyId());
		payment.setStatus(PaymentStatus.PENDING_PAYMENT.getValue());
		payment.setCreated(DateUtil.getSystemDate());
		payment.setCreator(WebUtils.getCurrUserId());
		payment.setFunds(PaymentFunds.ORDER_FEE_CHARGE.getValue());
		payment.setWay(PaymentWay.DEALER_ACCOUNT.getValue());
		payment.setType(PaymentTypeNew.CHARGEBACK.getValue());
		//payment.setPayee("4j1u3r1efshq");
		payment.setCustomOrderId(customOrder.getId());
		payment.setBranchId(WebUtils.getCurrUser().getBranchId());
		payment.setResourceType(PaymentResourceType.ORDER.getValue());
		this.paymentService.add(payment);
		//记录操作日志
		CustomOrderLog log = new CustomOrderLog();
		log.setCreated(new Date());
		log.setCreator(WebUtils.getCurrUserId());
		log.setName("创建订单");
		log.setStage(OrderStage.CREATE.getValue());
		log.setContent("订单创建，订单号："+customOrder.getNo());
		log.setCustomOrderId(customOrder.getId());
		customOrderLogService.add(log);
		return ResultFactory.generateRequestResult(this.customOrderService.findByOrderId(customOrder.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "修改订单",operationType = OperationType.UPDATE,operationMoudule = OperationMoudule.CUSTOM_ORDER)
	public RequestResult factoryUpdateOrder(String id, MapContext mapContext) {
		//判断订单是否存在
		CustomOrder customOrder = this.customOrderService.findById(id);
		if (customOrder == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		if (customOrder.getStatus().equals(OrderStatus.TO_QUOTED.getValue()) && mapContext.containsKey("factoryFinalPrice")) {
			mapContext.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.TO_PAID.getValue());
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID, id);
		this.customOrderService.updateByMapContext(mapContext);
		BigDecimal price = mapContext.getTypedValue("factoryFinalPrice", BigDecimal.class);
		if (price != null) {
			if (customOrder.getStatus() < OrderStatus.TO_PRODUCED.getValue()) {
				MapContext filter = new MapContext();
				filter.put("orderId", id);
				filter.put("funds", PaymentFunds.ORDER_FEE_CHARGE.getValue());
				PaymentDto paymentDto = this.paymentService.findByOrderIdAndFunds(filter);
				MapContext updatePayment = new MapContext();
				updatePayment.put(WebConstant.KEY_ENTITY_ID, paymentDto.getId());
				updatePayment.put("amount", price);
				this.paymentService.updateByMapContext(updatePayment);
			}
		}
		return ResultFactory.generateRequestResult(this.customOrderService.findByOrderId(id));
	}


	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addProduceOrder(String id, ProduceOrderDto produceOrder, List<String> fileIds) {
		CustomOrder customOrder = this.customOrderService.findById(id);
		if (customOrder == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		produceOrder.setCustomOrderNo(customOrder.getNo());
		produceOrder.setCustomOrderId(customOrder.getId());
		String orderNo = customOrder.getNo();
		Basecode b = basecodeService.findByTypeAndCode("produceType",produceOrder.getType().toString());
		OrderProductDto prod = orderProductService.findOneById(produceOrder.getOrderProductId());
		orderNo = orderNo+"-"+prod.getTypeName()+"-"+b.getValue();
		produceOrder.setNo(orderNo);
		produceOrder.setCreated(DateUtil.getSystemDate());
		produceOrder.setCreator(WebUtils.getCurrUserId());
		produceOrder.setBranchId(WebUtils.getCurrBranchId());
		produceOrder.setResourceType(PaymentResourceType.ORDER.getValue());
		MapContext orderMapContext = new MapContext();
		//如果是五金
		if (produceOrder.getType().equals(ProduceOrderType.HARDWARE.getValue())) {
			produceOrder.setState(ProduceOrderState.COMPLETE.getValue());
			produceOrder.setWay(ProduceOrderWay.SELF_PRODUCED.getValue());
			//判断订单状态是否已付款
			if (customOrder.getStatus() > OrderStatus.TO_PAID.getValue()) {
				produceOrder.setPay(ProduceOrderPay.PAY.getValue());
			} else {
				produceOrder.setPay(ProduceOrderPay.NOT_PAY.getValue());
			}
			//判断是否存在自产生产单 存在则以自产生产单为主
			List<ProduceOrder> ways = this.produceOrderService.findListByOrderIdAndTypesAndWays(id, null, Arrays.asList(ProduceOrderWay.SELF_PRODUCED.getValue()));
			if (ways == null || ways.size() == 0) {
				if (customOrder.getStatus() == OrderStatus.PRODUCTION.getValue() || customOrder.getStatus().equals(OrderStatus.TO_PRODUCED.getValue())) {
					orderMapContext.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.TO_PACKAGED.getValue());
				}
			}
		} else if (produceOrder.getType().equals(ProduceOrderType.CABINET_BODY.getValue())) {//如果是柜体
			produceOrder.setState(ProduceOrderState.NOT_YET_BEGUN.getValue());
			produceOrder.setWay(ProduceOrderWay.SELF_PRODUCED.getValue());
			//判断订单状态是否已付款
			if (customOrder.getStatus() > OrderStatus.TO_PAID.getValue()) {
				produceOrder.setPay(ProduceOrderPay.PAY.getValue());
			} else {
				produceOrder.setPay(ProduceOrderPay.NOT_PAY.getValue());
			}
		} else {//门板
			if (produceOrder.getWay().equals(ProduceOrderWay.SELF_PRODUCED.getValue())) {//如果是自产
				produceOrder.setState(ProduceOrderState.NOT_YET_BEGUN.getValue());
				//判断订单状态是否已付款
				if (customOrder.getStatus() > OrderStatus.TO_PAID.getValue()) {
					produceOrder.setPay(ProduceOrderPay.PAY.getValue());
				} else {
					produceOrder.setPay(ProduceOrderPay.NOT_PAY.getValue());
				}
				produceOrder.setPermit(ProduceOrderPermit.NOT_ALLOW.getValue());
			} else if (produceOrder.getWay().equals(ProduceOrderWay.SPECIAL.getValue())) {//如果是特供实木
				produceOrder.setState(ProduceOrderState.COMPLETE.getValue());
				//判断订单状态是否已付款
				if (customOrder.getStatus() > OrderStatus.TO_PAID.getValue()) {
					produceOrder.setPay(ProduceOrderPay.PAY.getValue());
				} else {
					produceOrder.setPay(ProduceOrderPay.NOT_PAY.getValue());
				}
				//判断是否存在自产生产单 存在则以自产生产单为主
				List<ProduceOrder> ways = this.produceOrderService.findListByOrderIdAndTypesAndWays(id, null, Arrays.asList(ProduceOrderWay.SELF_PRODUCED.getValue()));
				if (ways == null || ways.size() == 0) {
					if (customOrder.getStatus() == OrderStatus.PRODUCTION.getValue() || customOrder.getStatus().equals(OrderStatus.TO_PRODUCED.getValue())) {
						orderMapContext.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.TO_PACKAGED.getValue());
					}
				}
			}
		}
		RequestResult result = produceOrder.validateFields();
		if(result!=null){
			return result;
		}
		this.produceOrderService.add(produceOrder);
		if (orderMapContext.size() > 0) {
			orderMapContext.put(WebConstant.KEY_ENTITY_ID, id);
			this.customOrderService.updateByMapContext(orderMapContext);
		}
		//修改图片资源
		if (fileIds.size() != 0) {
			MapContext mapContext = new MapContext();
			mapContext.put(WebConstant.KEY_ENTITY_ID, fileIds);
			mapContext.put("belongId", produceOrder.getId());
			mapContext.put(WebConstant.KEY_ENTITY_STATUS, CustomOrderFilesStatus.FORMAL.getValue());
			this.customOrderFilesService.updateByIds(mapContext);
		}
		//记录操作日志
		CustomOrderLog log = new CustomOrderLog();
		log.setCreated(new Date());
		log.setCreator(WebUtils.getCurrUserId());
		log.setName("订单生产中");
		log.setStage(OrderStage.PRODUCE.getValue());
		log.setContent("订单号："+customOrder.getNo()+" 中的产品:"+OrderProductType.getByValue(produceOrder.getType()).getName()+"开始生产");
		log.setCustomOrderId(customOrder.getId());
		customOrderLogService.add(log);
		return ResultFactory.generateRequestResult(this.produceOrderService.findOneById(produceOrder.getId()));
	}

	/**
	 * 创建外协单
	 * @param id
	 * @param produceOrder
	 * @param fileIds
	 * @return
	 */
	@Override
	@SysOperationLog(detail = "新建外协订单",operationType = OperationType.INSERT,operationMoudule = OperationMoudule.CORPORATE_ORDER)
	@Transactional(value = "transactionManager")
	public RequestResult addCorporateProduceOrder(String id, ProduceOrderDto produceOrder, List<String> fileIds) {
		CustomOrder customOrder = this.customOrderService.findById(id);
		if (customOrder == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		produceOrder.setCustomOrderNo(customOrder.getNo());
		produceOrder.setCustomOrderId(customOrder.getId());
		produceOrder.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PURCHASE_REQUEST));
		produceOrder.setCreated(DateUtil.getSystemDate());
		produceOrder.setCreator(WebUtils.getCurrUserId());
		produceOrder.setBranchId(WebUtils.getCurrBranchId());
		produceOrder.setResourceType(PaymentResourceType.ORDER.getValue());
		MapContext orderMapContext = new MapContext();
		produceOrder.setState(ProduceOrderState.NOT_YET_BEGUN.getValue());
		produceOrder.setPay(ProduceOrderPay.NOT_PAY.getValue());
		RequestResult result = produceOrder.validateFields();
		if(result!=null){
			return result;
		}
		this.produceOrderService.add(produceOrder);
		orderMapContext.put("coordination", CustomOrderCoordination.NEED_COORDINATION.getValue());
		if (orderMapContext.size() > 0) {
			orderMapContext.put(WebConstant.KEY_ENTITY_ID, id);
			this.customOrderService.updateByMapContext(orderMapContext);
		}
		//修改图片资源
		if (fileIds.size() != 0) {
			MapContext mapContext = new MapContext();
			mapContext.put(WebConstant.KEY_ENTITY_ID, fileIds);
			mapContext.put("belongId", produceOrder.getId());
			mapContext.put(WebConstant.KEY_ENTITY_STATUS, CustomOrderFilesStatus.FORMAL.getValue());
			this.customOrderFilesService.updateByIds(mapContext);
		}
		//记录操作日志
		CustomOrderLog log = new CustomOrderLog();
		log.setCreated(new Date());
		log.setCreator(WebUtils.getCurrUserId());
		log.setName("外协订单生产中");
		log.setStage(OrderStage.PRODUCE.getValue());
		log.setContent("订单号："+customOrder.getNo()+" 中的外协产品:"+OrderProductType.getByValue(produceOrder.getType()).getName()+"已下单");
		log.setCustomOrderId(customOrder.getId());
		customOrderLogService.add(log);
		return ResultFactory.generateRequestResult(this.produceOrderService.findOneById(produceOrder.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addOrderProduct(String id, OrderProductDto orderProduct) {
		//判断订单是否存在
		CustomOrder customOrder = this.customOrderService.findById(id);
		if (customOrder == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//获取订单编号头一位 判断是否是数字
		int num = customOrder.getNo().charAt(0);
		if (num > 47 && num < 58) {//是的话表示当前订单使用的编号为临时编号需要修改
			MapContext updateOrder = new MapContext();
			updateOrder.put(WebConstant.KEY_ENTITY_ID, id);
			if (orderProduct.getType().equals(OrderProductType.WARDROBE.getValue())) {
				updateOrder.put(WebConstant.STRING_NO, "B" + customOrder.getNo());
			} else if (orderProduct.getType().equals(OrderProductType.SAMPLE_PIECE.getValue())) {
				updateOrder.put(WebConstant.STRING_NO, "Y" + customOrder.getNo());
			} else {
				updateOrder.put(WebConstant.STRING_NO, "J" + customOrder.getNo());
			}
			this.customOrderService.updateByMapContext(updateOrder);
		}
		this.orderProductService.add(orderProduct);
		if (orderProduct.getFileIds().size() != 0) {
			MapContext updateFile = new MapContext();
			updateFile.put(WebConstant.KEY_ENTITY_ID, orderProduct.getFileIds());
			updateFile.put("belongId", orderProduct.getId());
			updateFile.put(WebConstant.KEY_ENTITY_STATUS, CustomOrderFilesStatus.FORMAL.getValue());
			this.customOrderFilesService.updateByIds(updateFile);
		}
		//判断是否存在订单报价信息
		OrderOffer byOrerId = this.orderOfferService.findByOrerId(id);
		if (byOrerId == null) {
			//获取订单产品总金额
			BigDecimal countPrice = this.orderProductService.findCountPriceByOrderId(id);
			//修改订单金额
			MapContext updateOrder = new MapContext();
			updateOrder.put(WebConstant.KEY_ENTITY_ID, id);
			updateOrder.put("factoryFinalPrice", countPrice);
			this.customOrderService.updateByMapContext(updateOrder);
			//判断订单是未付款
			if (customOrder.getStatus() < OrderStatus.TO_PRODUCED.getValue()) {
				MapContext filter = new MapContext();
				filter.put("orderId", id);
				filter.put("funds", PaymentFunds.ORDER_FEE_CHARGE.getValue());
				PaymentDto paymentDto = this.paymentService.findByOrderIdAndFunds(filter);
				MapContext updatePayment = new MapContext();
				updatePayment.put(WebConstant.KEY_ENTITY_ID, paymentDto.getId());
				updatePayment.put("amount", countPrice);
				this.paymentService.updateByMapContext(updatePayment);
			}
		}
		//判断产品的 门板颜色 和 门型 和 柜体颜色 是否存在 不存在 则新增
		Basecode doorColor = this.basecodeService.findByTypeAndValue("orderProductDoorColor", orderProduct.getDoorColor());
		if (doorColor == null) {
			List<Basecode> basecodeList = this.basecodeService.findByType("orderProductDoorColor");
			Basecode basecode = new Basecode();
			basecode.setValue(orderProduct.getDoorColor());
			basecode.setType("orderProductDoorColor");
			basecode.setOrderNum(basecodeList.size());
			basecode.setName("订单产品门板颜色");
			basecode.setDelFlag(0);
			basecode.setCode(basecodeList.size() + "");
			this.basecodeService.add(basecode);
		}
		Basecode door = this.basecodeService.findByTypeAndValue("orderProductDoor", orderProduct.getDoor());
		if (door == null) {
			List<Basecode> basecodeList = this.basecodeService.findByType("orderProductDoor");
			Basecode basecode = new Basecode();
			basecode.setValue(orderProduct.getDoor());
			basecode.setType("orderProductDoor");
			basecode.setOrderNum(basecodeList.size());
			basecode.setName("订单产品门型");
			basecode.setDelFlag(0);
			basecode.setCode(basecodeList.size() + "");
			this.basecodeService.add(basecode);
		}
		Basecode bodyColor = this.basecodeService.findByTypeAndValue("orderProductBodyColor", orderProduct.getBodyColor());
		if (bodyColor == null) {
			List<Basecode> basecodeList = this.basecodeService.findByType("orderProductBodyColor");
			Basecode basecode = new Basecode();
			basecode.setValue(orderProduct.getBodyColor());
			basecode.setType("orderProductBodyColor");
			basecode.setOrderNum(basecodeList.size());
			basecode.setName("订单产品柜体颜色");
			basecode.setDelFlag(0);
			basecode.setCode(basecodeList.size() + "");
			this.basecodeService.add(basecode);
		}
		return ResultFactory.generateRequestResult(this.orderProductService.findOneById(orderProduct.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateOrderProduct(String id, String pId, MapContext mapContext) {
		//判断订单产品 以及订单是否存下
		OrderProduct orderProduct = this.orderProductService.findById(pId);
		if (orderProduct == null || !orderProduct.getCustomOrderId().equals(id)) {
			return ResultFactory.generateResNotFoundResult();
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID, pId);
		this.orderProductService.updateByMapContext(mapContext);
		BigDecimal amount = mapContext.getTypedValue("amount", BigDecimal.class);
		if (amount != null) {
			//判断是否存在订单报价信息
			OrderOffer byOrerId = this.orderOfferService.findByOrerId(id);
			if (byOrerId == null) {
				//获取订单产品总金额
				BigDecimal countPrice = this.orderProductService.findCountPriceByOrderId(id);
				//修改订单金额
				MapContext updateOrder = new MapContext();
				updateOrder.put(WebConstant.KEY_ENTITY_ID, id);
				updateOrder.put("factoryFinalPrice", countPrice);
				this.customOrderService.updateByMapContext(updateOrder);
				//判断订单是未付款
				CustomOrder customOrder = this.customOrderService.findById(id);
				if (customOrder.getStatus() < OrderStatus.TO_PRODUCED.getValue()) {
					MapContext filter = new MapContext();
					filter.put("orderId", id);
					filter.put("funds", PaymentFunds.ORDER_FEE_CHARGE.getValue());
					PaymentDto paymentDto = this.paymentService.findByOrderIdAndFunds(filter);
					MapContext updatePayment = new MapContext();
					updatePayment.put(WebConstant.KEY_ENTITY_ID, paymentDto.getId());
					updatePayment.put("amount", countPrice);
					this.paymentService.updateByMapContext(updatePayment);
				}
			}
		}
		return ResultFactory.generateRequestResult(this.orderProductService.findOneById(pId));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteOrderProduct(String id, String pId) {
		//判断订单产品 以及订单是否存
		OrderProduct orderProduct = this.orderProductService.findById(pId);
		if (orderProduct == null || !orderProduct.getCustomOrderId().equals(id)) {
			return ResultFactory.generateSuccessResult();
		}
		CustomOrder customOrderServiceById = this.customOrderService.findById(id);
		//如果订单已付款 则不允许删除
		if (customOrderServiceById.getStatus() > OrderStatus.TO_PAID.getValue()) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		//删除产品下的生产单
		this.produceOrderService.deleteByProductId(pId);
		this.orderProductService.deleteById(pId);
		//判断是否存在订单报价信息
		OrderOffer byOrerId = this.orderOfferService.findByOrerId(id);
		if (byOrerId == null) {
			//获取订单产品总金额
			BigDecimal countPrice = this.orderProductService.findCountPriceByOrderId(id);
			//修改订单金额
			MapContext updateOrder = new MapContext();
			updateOrder.put(WebConstant.KEY_ENTITY_ID, id);
			updateOrder.put("factoryFinalPrice", countPrice);
			this.customOrderService.updateByMapContext(updateOrder);
			//判断订单是未付款
			CustomOrder customOrder = this.customOrderService.findById(id);
			if (customOrder.getStatus() < OrderStatus.TO_PRODUCED.getValue()) {
				MapContext filter = new MapContext();
				filter.put("orderId", id);
				filter.put("funds", PaymentFunds.ORDER_FEE_CHARGE.getValue());
				PaymentDto paymentDto = this.paymentService.findByOrderIdAndFunds(filter);
				MapContext updatePayment = new MapContext();
				updatePayment.put(WebConstant.KEY_ENTITY_ID, paymentDto.getId());
				updatePayment.put("amount", countPrice);
				this.paymentService.updateByMapContext(updatePayment);
			}
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "修改生产单",operationType = OperationType.UPDATE,operationMoudule = OperationMoudule.PRODUCE)
	public RequestResult updateProduceOrder(String id, String pId, MapContext mapContext) {
		//判断生产拆单是否存在 订单是否存在
		ProduceOrder produceOrder = this.produceOrderService.findById(pId);
		if (produceOrder == null || !produceOrder.getCustomOrderId().equals(id)) {
			return ResultFactory.generateResNotFoundResult();
		}
		//如果是外协单  则判断是否修改报价
		if (produceOrder.getWay().equals(ProduceOrderWay.COORDINATION.getValue())) {
			BigDecimal amount = mapContext.getTypedValue("amount", BigDecimal.class);
//			if (amount != null) {
//				mapContext.put(WebConstant.KEY_ENTITY_STATE, CoordinationState.TO_PAY.getValue());
//			}
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID, pId);
		this.produceOrderService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.produceOrderService.findOneById(pId));
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "删除生产单",operationType = OperationType.DELETE,operationMoudule = OperationMoudule.PRODUCE)
	public RequestResult deleteProduceOrder(String id, String pId) {
		//判断生产拆单是否存在 订单是否存在
		ProduceOrder produceOrder = this.produceOrderService.findById(pId);
		if (produceOrder == null || !produceOrder.getCustomOrderId().equals(id)) {
			return ResultFactory.generateSuccessResult();
		}
		//删除生产单下的生产流程
		this.produceFlowService.deleteByOrderId(id);
		//删除 生产单
		this.produceOrderService.deleteById(pId);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findProducesList(MapContext mapContext, Integer pageNum, Integer pageSize, List<Map<String, String>> sorts) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		mapContext.put("funds", PaymentFunds.ORDER_FEE_CHARGE.getValue());
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		paginatedFilter.setFilters(mapContext);
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		Map<String, String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED, "desc");
		sorts.add(created);
		Map<String, String> id = new HashMap<String, String>();
		id.put(WebConstant.KEY_ENTITY_ID, "desc");
		sorts.add(id);
		paginatedFilter.setSorts(sorts);
		return ResultFactory.generateRequestResult(this.produceOrderService.findListByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addProduceFlow(String id, ProduceFlow produceFlow) {
		//判断生产单状态是否存在 是否是生产中
		ProduceOrder produceOrder = this.produceOrderService.findById(id);
		if (produceOrder == null || !produceOrder.getState().equals(ProduceOrderState.IN_PRODUCTION.getValue())) {
			return ResultFactory.generateResNotFoundResult();
		}
		//判断该节点是否已存在
		ProduceFlow existFlow = this.produceFlowService.findOneByProduceIdAndNode(id, produceFlow.getNode());
		if (existFlow != null) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		this.produceFlowService.add(produceFlow);
		return ResultFactory.generateRequestResult(this.produceFlowService.findOneById(produceFlow.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "新建包裹",operationType = OperationType.INSERT,operationMoudule = OperationMoudule.FINISHEDSTOCKITEM)
	public RequestResult addOrderPack(String id, List<FinishedStockItemDto> finishedStockItemDtoList,int type) {
		//判断资源是否存在
		CustomOrder order = null;
		AftersaleApply aftersaleApply = null;
		if(type==PaymentResourceType.ORDER.getValue()){
			order = this.customOrderService.findById(id);
			if (order == null) {
				return ResultFactory.generateResNotFoundResult();
			}
		}else if(type==PaymentResourceType.AFTERSALEAPPLY.getValue()){
			aftersaleApply = this.aftersaleApplyService.findById(id);
			if (aftersaleApply == null) {
				return ResultFactory.generateResNotFoundResult();
			}
		}else{
			return ResultFactory.generateResNotFoundResult();
		}
		//判断订单产品是否存在
		OrderProduct byId = this.orderProductService.findById(finishedStockItemDtoList.get(0).getOrderProductId());
		if (byId == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//判断是否存在与包裹类型相符的生产单
//		Integer itemType = finishedStockItemDtoList.get(0).getType();
//		if(itemType.equals(FinishedStockItemType.CABINET.getValue())){
//			if(this.produceOrderService.findListByOrderIdAndTypes(id,Arrays.asList(ProduceOrderType.CABINET_BODY.getValue())).size()==0){
//				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_THIS_TYPE_OF_PRODUCE_ORDER_NOT_EXIST_10092,AppBeanInjector.i18nUtil.getMessage("BIZ_THIS_TYPE_OF_PRODUCE_ORDER_NOT_EXIST_10092"));
//			}
//		}else if(itemType.equals(FinishedStockItemType.DOOR_HOMEGROWN.getValue())||finishedStockItemDtoList.get(0).getType().equals(FinishedStockItemType.DOOR_OUTSOURCING.getValue())||finishedStockItemDtoList.get(0).getType().equals(FinishedStockItemType.SPECIAL_SUPPLY.getValue())){
//			if(this.produceOrderService.findListByOrderIdAndTypes(id,Arrays.asList(ProduceOrderType.DOOR_PLANK.getValue())).size()==0){
//				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_THIS_TYPE_OF_PRODUCE_ORDER_NOT_EXIST_10092,AppBeanInjector.i18nUtil.getMessage("BIZ_THIS_TYPE_OF_PRODUCE_ORDER_NOT_EXIST_10092"));
//			}
//		}else if(itemType.equals(FinishedStockItemType.HARDWARE.getValue())){
//			if(this.produceOrderService.findListByOrderIdAndTypes(id,Arrays.asList(ProduceOrderType.HARDWARE.getValue())).size()==0){
//				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_THIS_TYPE_OF_PRODUCE_ORDER_NOT_EXIST_10092,AppBeanInjector.i18nUtil.getMessage("BIZ_THIS_TYPE_OF_PRODUCE_ORDER_NOT_EXIST_10092"));
//			}
//		}else{
//			return ResultFactory.generateResNotFoundResult();
//		}
		FinishedStock oldFinishedStock = this.finishedStockService.findByOrderId(id);
		String finishedStockId;
		//判断成品库单是否存在
		if (oldFinishedStock == null) {
			FinishedStock finishedStock = new FinishedStock();
			finishedStock.setCreated(DateUtil.getSystemDate());
			finishedStock.setCreator(WebUtils.getCurrUserId());
			finishedStock.setOrderId(id);
			finishedStock.setPackages(finishedStockItemDtoList.size() + 1);
			if(type==PaymentResourceType.ORDER.getValue()){
				finishedStock.setOrderNo(order.getNo());
			}else {
				finishedStock.setOrderNo(aftersaleApply.getNo());
			}
			finishedStock.setStatus(FinishedStockStatus.UNSHIPPED.getValue());
			//查询成品仓
			Storage storage = this.storageService.findOneByProductCategoryKey(ProductCategoryKey.finished.getId(), WebUtils.getCurrBranchId());
			finishedStock.setStorageId(storage.getId());
			finishedStock.setWay(FinishedStockWay.MANUAL.getValue());
			finishedStock.setBranchId(WebUtils.getCurrBranchId());
			finishedStock.setResourceType(type);
			RequestResult result = finishedStock.validateFields();
			if (result != null) {
				return result;
			}
			this.finishedStockService.add(finishedStock);
			finishedStockId = finishedStock.getId();
		} else {
			finishedStockId = oldFinishedStock.getId();
			MapContext mapContext = new MapContext();
			mapContext.put(WebConstant.KEY_ENTITY_ID, finishedStockId);
			//查询已经存在包裹数
			List<FinishedStockItemDto> listByFinishedStockId = this.finishedStockItemService.findListByFinishedStockId(finishedStockId);
			//如果已存在的包裹数 等于 成品库中 的包装数 则 代表着 已全部入库 再次入库时 存入真实包数
			if (listByFinishedStockId.size() == oldFinishedStock.getPackages()) {
				mapContext.put("packages", listByFinishedStockId.size() + finishedStockItemDtoList.size());
			} else {
				mapContext.put("packages", listByFinishedStockId.size() + finishedStockItemDtoList.size() + 1);
			}
			this.finishedStockService.updateByMapContext(mapContext);
		}
		for (FinishedStockItemDto finishedStockItem : finishedStockItemDtoList) {
			//判断包裹编号是否重复
			if (this.finishedStockItemService.findListByBarcodes(new HashSet(Arrays.asList(finishedStockItem.getBarcode()))).size() != 0) {
				Map res = new HashMap<String, String>();
				res.put("barcodes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
				return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, res);
			}
			finishedStockItem.setFinishedStockId(finishedStockId);
			this.finishedStockItemService.add(finishedStockItem);
			//修改图片资源
			if (finishedStockItem.getFileIds().size() != 0) {
				MapContext fileUpdate = new MapContext();
				fileUpdate.put(WebConstant.KEY_ENTITY_ID, finishedStockItem.getFileIds());
				fileUpdate.put("belongId", finishedStockItem.getId());
				fileUpdate.put(WebConstant.KEY_ENTITY_STATUS, CustomOrderFilesStatus.FORMAL.getValue());
				this.customOrderFilesService.updateByIds(fileUpdate);
			}
		}
		//修改订单状态 如果订单存在
		if (order!=null&&order.getStatus().equals(OrderStatus.TO_PACKAGED.getValue())) {
			MapContext orderUpdate = MapContext.newOne();
			orderUpdate.put(WebConstant.KEY_ENTITY_ID, order.getId());
			orderUpdate.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.TO_SHIPPED.getValue());
			this.customOrderService.updateByMapContext(orderUpdate);
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult orderPacked(String id) {
		//判断订单是否存在
		CustomOrder order = this.customOrderService.findById(id);
		if (order == null) {
			return ResultFactory.generateResNotFoundResult();
		}
//		//判断是否存在未完成生产单
//		List<ProduceOrder> incompleteListByOrderId = this.produceOrderService.findIncompleteListByOrderId(id,Arrays.asList(ProduceOrderWay.SELF_PRODUCED.getValue(),ProduceOrderWay.COORDINATION.getValue()));
//		if (incompleteListByOrderId.size()!=0){
//			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
//		}
		//判断 成品库单 是否存在
		FinishedStock finishedStock = this.finishedStockService.findByOrderId(id);
		if (finishedStock == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//判断是否已确认打包完成
		List<FinishedStockItemDto> listByFinishedStockId = this.finishedStockItemService.findListByFinishedStockId(finishedStock.getId());
		if (finishedStock.getPackages() == listByFinishedStockId.size()) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID, finishedStock.getId());
		mapContext.put("packages", listByFinishedStockId.size());
		this.finishedStockService.updateByMapContext(mapContext);
		return ResultFactory.generateSuccessResult();
	}


	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateProducesOrderPlanTime(List ids, Date planTime) {
		//安排生产时间即开始生产 进入生产中
		this.produceOrderService.updatePlanTimeByIds(planTime, ids);
		//订单进入生产中
		List<String> orderId = this.produceOrderService.findListOrderIdByPId(ids);
		for (String id : orderId) {
			CustomOrder customOrderServiceById = this.customOrderService.findById(id);
			//订单存在 则代表 为订单 生产单 不存在则代表为 售后单生产单
			if(customOrderServiceById!=null){
				if (customOrderServiceById.getStatus().equals(OrderStatus.TO_PRODUCED.getValue())) {
					MapContext updateOrder = new MapContext();
					updateOrder.put(WebConstant.KEY_ENTITY_ID, id);
					updateOrder.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.PRODUCTION.getValue());
					this.customOrderService.updateByMapContext(updateOrder);
				}
			}
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult endCoordination(String id) {
		//判断生产单是否存在
		ProduceOrder produceOrder = this.produceOrderService.findById(id);
		// 生产单 是否存在 是否是生产中
		if (produceOrder == null || !produceOrder.getState().equals(ProduceOrderState.IN_PRODUCTION.getValue())) {
			return ResultFactory.generateResNotFoundResult();
		}
		//修改生产单状态为 已完成
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID, id);
		mapContext.put(WebConstant.KEY_ENTITY_STATE, ProduceOrderState.COMPLETE.getValue());
		mapContext.put("completionTime", DateUtil.getSystemDate());
		this.produceOrderService.updateByMapContext(mapContext);
		//判断是否存在自产生产单 存在则以自产生产单为主
		List<ProduceOrder> ways = this.produceOrderService.findListByOrderIdAndTypesAndWays(produceOrder.getCustomOrderId(), null, Arrays.asList(ProduceOrderWay.SELF_PRODUCED.getValue()));
		if(ways==null||ways.size()==0){
			//如果订单状态为 生产中 则修改为待打包
			CustomOrder customOrder = this.customOrderService.findById(produceOrder.getCustomOrderId());
			if (customOrder.getStatus().equals(OrderStatus.PRODUCTION.getValue())) {
				MapContext updateOrder = new MapContext();
				updateOrder.put(WebConstant.KEY_ENTITY_ID, customOrder.getId());
				updateOrder.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.TO_PACKAGED.getValue());
				this.customOrderService.updateByMapContext(updateOrder);
			}
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findAllDesign(MapContext mapContext, Integer pageNum, Integer pageSize) {
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		paginatedFilter.setFilters(mapContext);
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		return ResultFactory.generateRequestResult(this.customOrderDesignService.findListByFilter(paginatedFilter));
	}

	@Override
	public RequestResult findOrderPackagesNo(String id, String type, Integer count,int resType) {
		//判断订单是否存在
		CustomOrder order = null;
		AftersaleApply aftersaleApply = null;
		String no;
		if (resType==PaymentResourceType.ORDER.getValue()){
			order = this.customOrderService.findById(id);
			if (order == null) {
				return ResultFactory.generateResNotFoundResult();
			}
			no = order.getNo();
		}else{
			aftersaleApply = this.aftersaleApplyService.findById(id);
			if(aftersaleApply==null){
				return ResultFactory.generateResNotFoundResult();
			}
			no = aftersaleApply.getNo();
		}
		List noOrder = new ArrayList();
		String typeString="finishedStockItemType";
		Basecode basecode=this.basecodeService.findByTypeAndCode(typeString,type);
		type=basecode.getValue()+count.toString();
		for (int i = 1; i <= count; i++) {//包裹编号以订单编号+部件类型编码+包裹总数+第几包（例：J20190506-03-A5-01）
			noOrder.add(no+"-"+type+"-"+i);
		}
		return ResultFactory.generateRequestResult(noOrder);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult uploadOrderFiles(String id, Integer type, String resId, List<MultipartFile> multipartFileList) {
		int s; //判断资源赋值类型
		//判断订单以及资源是否存在
		switch (CustomOrderFilesType.getByValue(type)) {
			case DEMAND:
				CustomOrderDemand customOrderDemand = this.customOrderDemandService.findById(resId);
				if (customOrderDemand == null || !customOrderDemand.getCustomOrderId().equals(id)) {
					return ResultFactory.generateResNotFoundResult();
				}
				s = 0;
				break;
			case ORDER_PRODUCT:
				if (!resId.equals("productId")) {
					OrderProduct byIdOrderProduct = this.orderProductService.findById(resId);
					if (byIdOrderProduct == null || !byIdOrderProduct.getCustomOrderId().equals(id)) {
						return ResultFactory.generateResNotFoundResult();
					}
					s = 1;
				} else {
					s = 2;
				}
				break;
			case PRODUCE_ORDER:
				if (!resId.equals("produceId")) {
					ProduceOrder orderServiceById = this.produceOrderService.findById(resId);
					if (orderServiceById == null || !orderServiceById.getCustomOrderId().equals(id)) {
						return ResultFactory.generateResNotFoundResult();
					}
					s = 1;
				} else {
					s = 2;
				}
				break;
			default:
				return ResultFactory.generateResNotFoundResult();
		}
		List imgList = new ArrayList();
		for (MultipartFile multipartFile : multipartFileList) {
			CustomOrderFiles customOrderFiles = new CustomOrderFiles();
			if (s == 0) {
				customOrderFiles.setBelongId(resId);
				customOrderFiles.setStatus(CustomOrderFilesStatus.FORMAL.getValue());
			} else if (s == 1) {
				customOrderFiles.setBelongId(resId);
				customOrderFiles.setStatus(CustomOrderFilesStatus.FORMAL.getValue());
			} else {
				customOrderFiles.setBelongId(null);
				customOrderFiles.setStatus(CustomOrderFilesStatus.TEMP.getValue());
			}
			if (id.equals("null")) {
				customOrderFiles.setCustomOrderId(null);
			} else {
				customOrderFiles.setCustomOrderId(id);
			}
			customOrderFiles.setCategory(CustomOrderFilesCategory.ACCESSORY.getValue());
			customOrderFiles.setType(type);
			customOrderFiles.setCreated(DateUtil.getSystemDate());
			customOrderFiles.setCreator(WebUtils.getCurrUserId());
			UploadInfo uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, multipartFile, UploadResourceType.CUSTOM_ORDER, id, resId);
			customOrderFiles.setPath(uploadInfo.getRelativePath());
			customOrderFiles.setFullPath(AppBeanInjector.configuration.getDomainUrl() + uploadInfo.getRelativePath());
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
		if (byId == null || !byId.getCustomOrderId().equals(id)) {
			return ResultFactory.generateSuccessResult();
		}
		//清除本地文件
		AppBeanInjector.baseFileUploadComponent.deleteFileByDir(AppBeanInjector.configuration.getUploadFileRootDir().concat(byId.getPath()));
		//清除数据库文件
		this.customOrderFilesService.deleteById(fileId);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "删除订单",operationType = OperationType.DELETE,operationMoudule = OperationMoudule.CUSTOM_ORDER)
	public RequestResult deleteOrderById(String orderId) {
		return this.deleteOrder(orderId);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult producePermit(String orderId, String produceId) {
		ProduceOrder byId = this.produceOrderService.findById(produceId);
		if (byId == null || !byId.getCustomOrderId().equals(orderId) || byId.getPermit() == ProduceOrderPermit.ALLOW.getValue()) {
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID, produceId);
		mapContext.put("permit", ProduceOrderPermit.ALLOW.getValue());
		this.produceOrderService.updateByMapContext(mapContext);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult produceComplete(String orderId, String produceId) {
		ProduceOrder byId = this.produceOrderService.findById(produceId);
		if (byId == null || !byId.getCustomOrderId().equals(orderId) || byId.getState() != ProduceOrderState.IN_PRODUCTION.getValue()) {
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID, produceId);
		if (byId.getWay().equals(ProduceOrderWay.COORDINATION.getValue())) {
			mapContext.put(WebConstant.KEY_ENTITY_STATE, CoordinationState.COMPLETE.getValue());
		} else {
			mapContext.put(WebConstant.KEY_ENTITY_STATE, ProduceOrderState.COMPLETE.getValue());
		}
		this.produceOrderService.updateByMapContext(mapContext);
		//判断是否存在未完成的自产生产单
		List<ProduceOrder> produceOrders = this.produceOrderService.findIncompleteListByOrderId(orderId, Arrays.asList(ProduceOrderWay.SELF_PRODUCED.getValue()));
		if (produceOrders.size() == 0) {
			MapContext updateOrder = new MapContext();
			updateOrder.put(WebConstant.KEY_ENTITY_ID, orderId);
			updateOrder.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.TO_PACKAGED.getValue());
			this.customOrderService.updateByMapContext(updateOrder);
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult submitDesignfee(String orderId, MapContext mapContext) {
		//判断订单是否存在
		CustomOrder customOrderServiceById = this.customOrderService.findById(orderId);
		if (customOrderServiceById == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//修改设计费
		mapContext.put(WebConstant.KEY_ENTITY_ID, orderId);
		this.customOrderService.updateByMapContext(mapContext);
		//如果订单不存在设计费的支付记录 则新建 存在则 修改支付记录
		MapContext filter = new MapContext();
		filter.put("orderId", orderId);
		filter.put("funds", PaymentFunds.DESIGN_FEE_CHARGE.getValue());
		PaymentDto paymentDto = this.paymentService.findByOrderIdAndFunds(filter);
		if (paymentDto == null) {
			//生成待支付的设计费支付记录
			Payment pricePayment = new Payment();
			pricePayment.setCompanyId(customOrderServiceById.getCompanyId());
			pricePayment.setStatus(PaymentStatus.PENDING_PAYMENT.getValue());
			pricePayment.setBranchId(WebUtils.getCurrBranchId());
			pricePayment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
			pricePayment.setCreator(WebUtils.getCurrUserId());
			pricePayment.setCreated(DateUtil.getSystemDate());
			pricePayment.setCustomOrderId(orderId);
			pricePayment.setWay(PaymentWay.BANK.getValue());
			pricePayment.setType(PaymentTypeNew.CHARGEBACK.getValue());
			pricePayment.setFunds(PaymentFunds.DESIGN_FEE_CHARGE.getValue());
			pricePayment.setAmount(mapContext.getTypedValue("designFee", BigDecimal.class));
			pricePayment.setHolder("红田集团");
			pricePayment.setPayee("4j1u3r1efshq");
			pricePayment.setResourceType(PaymentResourceType.ORDER.getValue());
			RequestResult result = pricePayment.validateFields();
			if (result != null) {
				return result;
			}
			this.paymentService.add(pricePayment);
		} else {
			//如果支付记录未付款 则修改支付记录金额字段
			if (paymentDto.getStatus().equals(PaymentStatus.PENDING_PAYMENT.getValue())) {
				MapContext updatePayment = new MapContext();
				updatePayment.put("amount", mapContext.getTypedValue("designFee", BigDecimal.class));
				updatePayment.put(WebConstant.KEY_ENTITY_ID, paymentDto.getId());
				this.paymentService.updateByMapContext(updatePayment);
			}
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addCustomOrderDemand(String orderId, CustomOrderDemand customOrderDemand) {
		//判断订单是否存在
		CustomOrder customOrderServiceById = this.customOrderService.findById(orderId);
		if (customOrderServiceById == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//判断产品是否存在
		OrderProduct orderProduct = this.orderProductService.findById(customOrderDemand.getOrderProductId());
		if (orderProduct == null || !orderProduct.getCustomOrderId().equals(orderId)) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_REPETITION_ALLOWED_10094, AppBeanInjector.i18nUtil.getMessage("BIZ_NO_REPETITION_ALLOWED_10094"));
		}
		//判断该产品是否已经存在需求单
		CustomOrderDemand oldOrderDemand = this.customOrderDemandService.findByProductId(orderProduct.getId());
		if (oldOrderDemand != null) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		this.customOrderDemandService.add(customOrderDemand);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateCustomOrderDemand(String orderId, String demandId, MapContext mapContext) {
		CustomOrderDemand byId = this.customOrderDemandService.findById(demandId);
		if (byId == null || !byId.getCustomOrderId().equals(orderId)) {
			return ResultFactory.generateResNotFoundResult();
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID, demandId);
		this.customOrderDemandService.updateByMapContext(mapContext);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteCustomOrderDemand(String orderId, String demandId) {
		CustomOrderDemand byId = this.customOrderDemandService.findById(demandId);
		if (byId == null || !byId.getCustomOrderId().equals(orderId)) {
			return ResultFactory.generateSuccessResult();
		}
		this.customOrderDemandService.deleteById(demandId);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult findProduceOrderById(String id, String produceId) {
		ProduceOrderDto oneById = this.produceOrderService.findOneById(produceId);
		if (oneById == null || !oneById.getCustomOrderId().equals(id)) {
			return ResultFactory.generateResNotFoundResult();
		}
		oneById.setProduceFlowDtos(this.produceFlowService.findListByProduceOrderId(produceId));
		oneById.setOrderProductDto(this.orderProductService.findOneById(oneById.getOrderProductId()));
		return ResultFactory.generateRequestResult(oneById);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult findProductInfo(String id, String pId) {
		OrderProductDto oneById = this.orderProductService.findOneById(id);
		if (oneById == null || !oneById.getCustomOrderId().equals(id)) {
			return ResultFactory.generateResNotFoundResult();
		}
		return ResultFactory.generateRequestResult(oneById);
	}

	/**
	 * 订单打印信息查询
	 *
	 * @param orderId
	 * @return
	 */
	@Override
	public RequestResult findOrderPrintTable(String orderId) {
		String branchId = WebUtils.getCurrBranchId();
		MapContext mapContext = MapContext.newOne();
		mapContext.put("orderId", orderId);
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID, branchId);
		OrderPrintTableDto orderPrintTableDto = this.customOrderService.findOrderPrintTable(mapContext);
		List<OrderProductDto> orderProductDtos = this.orderProductService.findListByOrderId(orderId);
		if (orderProductDtos.size() > 1) {
			orderPrintTableDto.setOrderProductTypeName("全屋");
		} else {
			orderPrintTableDto.setOrderProductTypeName("橱柜");
		}
		List<PaymentDto> paymentDtos = this.paymentService.findByOrderId(orderId);
		orderPrintTableDto.setOrderProductDtos(orderProductDtos);
		orderPrintTableDto.setPaymentDtos(paymentDtos);
		return ResultFactory.generateRequestResult(orderPrintTableDto);
	}

	/**
	 * 订单产品打印信息
	 *
	 * @param orderProductId
	 * @return
	 */
	@Override
	public RequestResult findProductPrintTable(String orderProductId) {
		//产品信息
		OrderProductDto orderProductDto = this.orderProductService.findOneById(orderProductId);
		if (orderProductDto == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//设计单信息
		String orderId = orderProductDto.getCustomOrderId();
		CustomOrderDesignDto customOrderDesignDto = this.customOrderDesignService.findOneByProductId(orderProductId);
		//订单信息
		MapContext mapContext = MapContext.newOne();
		mapContext.put("orderId", orderId);
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		OrderPrintTableDto orderPrintTableDto = this.customOrderService.findOrderPrintTable(mapContext);

		List<OrderProductDto> orderProductDtos = new ArrayList<>();
		orderProductDtos.add(orderProductDto);
		orderPrintTableDto.setOrderProductDtos(orderProductDtos);
		orderPrintTableDto.setCustomOrderDesignDto(customOrderDesignDto);
		if (orderProductDto.getType() == 0) {
			orderPrintTableDto.setOrderProductTypeName("橱柜");
		} else {
			orderPrintTableDto.setOrderProductTypeName("全屋");
		}
		return ResultFactory.generateRequestResult(orderPrintTableDto);
	}


	/**
	 * 产品需求打印信息
	 *
	 * @param demandId
	 * @return
	 */
	@Override
	public RequestResult findDemandPrintTable(String demandId) {
		//需求信息
		CustomOrderDemandDto customOrderDemandDto = this.customOrderDemandService.selectByDemandId(demandId);
		//订单信息
		String orderId = customOrderDemandDto.getCustomOrderId();
		MapContext mapContext = MapContext.newOne();
		mapContext.put("orderId", orderId);
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		OrderPrintTableDto orderPrintTableDto = this.customOrderService.findOrderPrintTable(mapContext);
		//产品信息
		OrderProductDto orderProductDto = this.orderProductService.findOneById(customOrderDemandDto.getOrderProductId());
		if (orderProductDto.getType() == 0) {
			orderPrintTableDto.setOrderProductTypeName("橱柜");
		} else {
			orderPrintTableDto.setOrderProductTypeName("全屋");
		}
		List<OrderProductDto> orderProductDtos = new ArrayList<>();
		orderProductDtos.add(orderProductDto);
		orderPrintTableDto.setOrderProductDtos(orderProductDtos);
		orderPrintTableDto.setCustomOrderDemandDto(customOrderDemandDto);
		return ResultFactory.generateRequestResult(orderPrintTableDto);
	}

	/**
	 * 设计信息打印
	 *
	 * @param designId
	 * @return
	 */
	@Override
	public RequestResult findDesignPrintTable(String designId) {
		//设计信息
		CustomOrderDesignDto oneByDesignId = this.customOrderDesignService.findOneByDesignId(designId);
		if(oneByDesignId==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//产品信息
		OrderProductDto orderProductDto = this.orderProductService.findOneById(oneByDesignId.getOrderProductId());
		//订单信息
		String orderId = oneByDesignId.getCustomOrderId();
		MapContext mapContext = MapContext.newOne();
		mapContext.put("orderId", orderId);
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		OrderPrintTableDto orderPrintTableDto = this.customOrderService.findOrderPrintTable(mapContext);
		if (orderProductDto.getType() == 0) {
			orderPrintTableDto.setOrderProductTypeName("橱柜");
		} else {
			orderPrintTableDto.setOrderProductTypeName("全屋");
		}
		orderPrintTableDto.setCustomOrderDesignDto(oneByDesignId);
		return ResultFactory.generateRequestResult(orderPrintTableDto);
	}

	/**
	 * 生产单打印信息
	 *
	 * @return
	 */
	@Override
	public RequestResult findProducePrintTable(String produceId) {

		//生产单信息
		ProduceOrderDto produceOrderDto = this.produceOrderService.findOneById(produceId);
		if (produceOrderDto == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		String orderId = produceOrderDto.getCustomOrderId();
		//源订单信息
		MapContext mapContext = MapContext.newOne();
		mapContext.put("orderId", orderId);
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		OrderPrintTableDto orderPrintTableDto = this.customOrderService.findOrderPrintTable(mapContext);
		//产品信息
		String productId = produceOrderDto.getOrderProductId();
		OrderProductDto orderProductDto = this.orderProductService.findOneById(productId);
		if (orderProductDto.getType() == 0) {
			orderPrintTableDto.setOrderProductTypeName("橱柜");
		} else {
			orderPrintTableDto.setOrderProductTypeName("全屋");
		}
		//生产流程信息
		List<ProduceFlowDto> produceFlowDtos = this.produceFlowService.findListByProduceOrderId(produceId);
		List<OrderProductDto> orderProductDtos = new ArrayList<>();
		orderProductDtos.add(orderProductDto);
		orderPrintTableDto.setOrderProductDtos(orderProductDtos);
		orderPrintTableDto.setProduceOrderDto(produceOrderDto);
		orderPrintTableDto.setProduceFlowDtos(produceFlowDtos);
		return ResultFactory.generateRequestResult(orderPrintTableDto);
	}

	@Override
	public RequestResult findDesignOverview() {
		MapContext result = new MapContext();
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(999999999);
		pagination.setPageNum(1);
		paginatedFilter.setPagination(pagination);
		//今日新增设计任务数
		MapContext filter1 = new MapContext();
		filter1.put("createdNow", 1);
		paginatedFilter.setFilters(filter1);
		PaginatedList<CustomOrderDesign> designPaginatedList = this.customOrderDesignService.selectByFilter(paginatedFilter);
		result.put("addDesignCount", designPaginatedList.getRows().size());
		//今日延期设计任务数
		result.put("delayDesignCount", 0);
		//今日完成设计任务数
		MapContext filter3 = new MapContext();
		filter3.put("endTimeNow", 1);
		paginatedFilter.setFilters(filter3);
		PaginatedList<CustomOrderDesign> designPaginatedList2 = this.customOrderDesignService.selectByFilter(paginatedFilter);
		result.put("completeDesignCount", designPaginatedList2.getRows().size());
		return ResultFactory.generateRequestResult(result);
	}

	@Override
	public RequestResult findCustomOrderOverview() {
		MapContext result = new MapContext();
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(1);
		pagination.setPageSize(999999);
		paginatedFilter.setPagination(pagination);
		//今日新增订单数
		MapContext filter1 = new MapContext();
		filter1.put("createdNow", "1");
		filter1.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		paginatedFilter.setFilters(filter1);
		PaginatedList<CustomOrder> customOrderPaginatedList = this.customOrderService.selectByFilter(paginatedFilter);
		result.put("addOrderCount", customOrderPaginatedList.getRows().size());
		//待付款订单数
		MapContext filter2 = new MapContext();
		filter2.put(WebConstant.KEY_ENTITY_STATUS, OrderStatus.TO_PAID.getValue());
		filter2.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		paginatedFilter.setFilters(filter2);
		PaginatedList<CustomOrder> customOrderPaginatedList1 = this.customOrderService.selectByFilter(paginatedFilter);
		result.put("toPaidCount", customOrderPaginatedList1.getRows().size());
		//延期订单数
		Integer delayOrderCount=this.customOrderService.findOverdueOrderCount(WebUtils.getCurrBranchId());
		result.put("delayOrderCount", delayOrderCount);
		//完成订单数
		MapContext m=MapContext.newOne();
		m.put("status",OrderStatus.SHIPPED.getValue());
		m.put("branchId",WebUtils.getCurrBranchId());
		Integer endOrderCount=this.customOrderService.findOrderCountByStatus(m);
		result.put("completeOrderCount", endOrderCount);
		return ResultFactory.generateRequestResult(result);
	}

	/**
	 * 成品单打印信息
	 *
	 * @param id
	 * @return
	 */
	@Override
	public RequestResult findOrderProductPrintTable(String id) {
		//源订单信息
		MapContext mapContext = MapContext.newOne();
		mapContext.put("orderId", id);
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID, WebUtils.getCurrBranchId());
		OrderPrintTableDto orderPrintTableDto = this.customOrderService.findOrderPrintTable(mapContext);
		if(orderPrintTableDto==null){
			orderPrintTableDto = this.aftersaleApplyService.findOrderPrintTable(mapContext);
		}
		//产品信息
		List<OrderProductDto> orderProductDtos = this.orderProductService.findProductsByOrderId(id);
		orderPrintTableDto.setOrderProductDtos(orderProductDtos);

		return ResultFactory.generateRequestResult(orderPrintTableDto);
	}

	private RequestResult deleteOrder(String orderId) {
		//判断订单是否存在
		CustomOrder customOrder = this.customOrderService.findById(orderId);
		if (customOrder == null) {
			return ResultFactory.generateSuccessResult();
		}
		//删除订单下的相关资源文件
		List<CustomOrderFiles> customOrderFiles = this.customOrderFilesService.selectByOrderId(orderId);
		this.customOrderFilesService.deleteByOrderId(orderId);
		List<UploadFiles> uploadFilesList = this.uploadFilesService.findByResourceId(orderId);
		this.uploadFilesService.deleteByResourceId(orderId);
		//删除订单下的设计记录
		this.customOrderDesignService.deleteByOrderId(orderId);
		//删除订单下的需求
		this.customOrderDemandService.deleteByOrderId(orderId);
		//删除订单下的订单产品
		this.orderProductService.deleteByOrderId(orderId);
		//删除订单下的生产流程明细
		this.produceFlowService.deleteByOrderId(orderId);
		//删除订单下的生产拆单
		this.produceOrderService.deleteByOrderId(orderId);
		//删除订单下的 发货条目信息
		this.dispatchBillItemService.deleteByOrderId(orderId);
		//删除订单下的 发货单 (发货单 和订单无关联 暂时不做 后果为 发货单下 包裹数量为 0)
		//删除订单下的包裹的配送计划
		this.dispatchBillPlanItemService.deleteByOrderId(orderId);
		//删除订单下包裹
		this.finishedStockItemService.deleteByOrderId(orderId);
		//删除订单下的成品库单
		this.finishedStockService.deleteByOrderId(orderId);
		//删除售后单资源文件
		List<AftersaleApplyFiles> aftersaleApplyFiles = this.aftersaleApplyFilesService.findListByOrderId(orderId);
		this.aftersaleApplyFilesService.deleteByOrderId(orderId);
		//删除售后单
		List<AftersaleApply> aftersaleApplies = this.aftersaleApplyService.findAftersaleListByOrderId(orderId);
		for (AftersaleApply aftersaleApply : aftersaleApplies) {
			if (!LwxfStringUtils.isBlank(aftersaleApply.getResultOrderId())) {
				this.deleteOrder(aftersaleApply.getResultOrderId());
			}
			this.aftersaleApplyService.deleteById(aftersaleApply.getId());
		}
		//删除订单报价修改记录
		this.orderAccountLogService.deleteByOrderId(orderId);
		//删除订单支付记录
		this.paymentService.deleteByOrderId(orderId);
		//删除订单关联的售后订单资源
		List<AftersaleApplyFiles> aftersaleApplyFilesList = this.aftersaleApplyFilesService.findListByResultOrderId(orderId);
		this.aftersaleApplyFilesService.deleteByResultOrderId(orderId);
		//删除售后单
		this.aftersaleApplyService.deleteByResultOrderId(orderId);
		//删除订单
		this.customOrderService.deleteById(orderId);
		for (CustomOrderFiles customOrderFile : customOrderFiles) {
			//清除本地文件
			AppBeanInjector.baseFileUploadComponent.deleteFileByDir(AppBeanInjector.configuration.getUploadFileRootDir().concat(customOrderFile.getPath()));
		}
		for (UploadFiles uploadFiles : uploadFilesList) {
			//清除本地文件
			AppBeanInjector.baseFileUploadComponent.deleteFileByDir(AppBeanInjector.configuration.getUploadFileRootDir().concat(uploadFiles.getPath()));
		}
		for (AftersaleApplyFiles aftersaleApplyFile : aftersaleApplyFiles) {
			//清除本地文件
			AppBeanInjector.baseFileUploadComponent.deleteFileByDir(AppBeanInjector.configuration.getUploadFileRootDir().concat(aftersaleApplyFile.getPath()));
		}
		for (AftersaleApplyFiles aftersaleApplyFile : aftersaleApplyFilesList) {
			//清除本地文件
			AppBeanInjector.baseFileUploadComponent.deleteFileByDir(AppBeanInjector.configuration.getUploadFileRootDir().concat(aftersaleApplyFile.getPath()));
		}
		return ResultFactory.generateSuccessResult();
	}

	public Integer getMoneyCount(MapContext mapContext, Integer low, Integer high) {
		mapContext.put("low", low);
		mapContext.put("high", high);
		Integer result = this.customOrderService.findOrderMoneyCount(mapContext);
		return result;
	}
}

