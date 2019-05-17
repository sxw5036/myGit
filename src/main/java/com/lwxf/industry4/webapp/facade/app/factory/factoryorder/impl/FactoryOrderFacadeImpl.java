package com.lwxf.industry4.webapp.facade.app.factory.factoryorder.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyService;
import com.lwxf.industry4.webapp.bizservice.assemble.DispatchListService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.customer.CompanyCustomerService;
import com.lwxf.industry4.webapp.bizservice.customorder.*;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountLogService;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentFilesService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockItemService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.company.DealerAccountNature;
import com.lwxf.industry4.webapp.common.enums.company.DealerAccountType;
import com.lwxf.industry4.webapp.common.enums.company.EmployeeStatus;
import com.lwxf.industry4.webapp.common.enums.company.FactoryEmployeeRole;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderCoordination;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderFilesType;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderIsDesign;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentStatus;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentWay;
import com.lwxf.industry4.webapp.common.enums.order.*;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.AddressUtils;
import com.lwxf.industry4.webapp.common.utils.FactoryOrderUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.common.utils.excel.POIUtil;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentDto;
import com.lwxf.industry4.webapp.domain.dto.user.UserAreaDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockItemDto;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderDemand;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderFiles;
import com.lwxf.industry4.webapp.domain.entity.customorder.OrderProduct;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccount;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccountLog;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentFiles;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryorder.FactoryOrderFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/28 11:33
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factoryOrderFacade")
public class FactoryOrderFacadeImpl extends BaseFacadeImpl implements FactoryOrderFacade {

    @Resource(name = "customOrderService")
    private CustomOrderService customOrderService;
    @Resource(name = "customOrderDemandService")
    private CustomOrderDemandService customOrderDemandService;//需求
    @Resource(name = "customOrderDesignService")
    private CustomOrderDesignService customOrderDesignService;//设计
    @Resource(name = "orderAccountLogService")
    private OrderAccountLogService orderAccountLogService;//报价
    @Resource(name = "customOrderProcessService")
    private CustomOrderProcessService orderProcessService;//生产
    @Resource(name = "dispatchBillService")
    private DispatchBillService dispatchBillService;//配送
    @Resource(name = "dispatchListService")
    private DispatchListService dispatchListService;//安装
    @Resource(name = "companyService")
    private CompanyService companyService;
    @Resource(name = "companyEmployeeService")
    private CompanyEmployeeService companyEmployeeService;
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "paymentService")
    private PaymentService paymentService;
    @Resource(name = "finishedStockItemService")
    private FinishedStockItemService finishedStockItemService;
    @Resource(name = "aftersaleApplyService")
    private AftersaleApplyService aftersaleApplyService;
    @Resource(name = "orderProductService")
    private OrderProductService orderProductService;
    @Resource(name = "customOrderFilesService")
    private CustomOrderFilesService customOrderFilesService;
    @Resource(name = "produceOrderService")
    private ProduceOrderService produceOrderService;
    @Resource(name = "produceFlowService")
    private ProduceFlowService produceFlowService;
    @Resource(name = "paymentFilesService")
    private PaymentFilesService paymentFilesService;
    @Resource(name = "roleService")
    private RoleService roleService;
    @Resource(name = "companyCustomerService")
    private CompanyCustomerService companyCustomerService;
    @Resource(name = "dealerAccountService")
    private DealerAccountService dealerAccountService;
    @Resource(name = "dealerAccountLogService")
    private DealerAccountLogService dealerAccountLogService;

    /**
     * 查询订单的首页信息(无用了)
     *
     * @return
     */
    @Override
    public RequestResult findFactoryOrder() {
        //上月的订单数量
        Date precedMonthFirstDay = DateUtil.getPrecedMonthFirstDay();
        String lastMonth = DateUtil.dateTimeToString(precedMonthFirstDay, "yyyy-MM");
        Integer lastMonthOrderNum = this.customOrderService.findOrderNumByCreated(lastMonth);
        //当月的订单数量
        Date systemDate = DateUtil.getSystemDate();
        String thisMonth = DateUtil.dateTimeToString(systemDate, "yyyy-MM");
        int thisMonthOrderNum = this.customOrderService.findOrderNumByCreated(thisMonth);
        //当天的订单数量
        String today = DateUtil.dateTimeToString(systemDate, "yyyy-MM-dd");
        int todayOrderNum = this.customOrderService.findOrderNumByCreated(today);

        MapContext result = MapContext.newOne();
        result.put("lastMonthOrderNum", lastMonthOrderNum);
        result.put("thisMonthOrderNum", thisMonthOrderNum);
        result.put("todayOrderNum", todayOrderNum);

        PaginatedFilter filter = PaginatedFilter.newOne();
        Pagination pagination = Pagination.newOne();
        pagination.setPageNum(1);
        pagination.setPageSize(1000);
        filter.setPagination(pagination);
        MapContext params = MapContext.newOne();
        params.put("created", today);
        filter.setFilters(params);
        PaginatedList<Map> mapPaginatedList = this.customOrderService.findByCreated(filter);
        List<Map> rows = mapPaginatedList.getRows();
        List<Map> orderList = FactoryOrderUtil.getOrderList(rows);
        result.put("todayOrderList", orderList);
        List<Map> allstatus = OrderStatus.getAll();
        result.put("allstatus", allstatus);
        return ResultFactory.generateRequestResult(result);
    }

    /**
     * \
     * 根据创建时间去查询订单的列表(无用了)
     *
     * @param pagination
     * @param type
     * @param status
     * @return
     */
    @Override
    public RequestResult findFactoryOrderByCreated(Pagination pagination, Integer type, Integer status) {
        PaginatedFilter filter = new PaginatedFilter();
        filter.setPagination(pagination);
        MapContext params = MapContext.newOne();
        params.put("status", status);
        Date systemDate = DateUtil.getSystemDate();
        List<Map> rows = new ArrayList<>();
        //当天
        if (type == 1) {
            String today = DateUtil.dateTimeToString(systemDate, "yyyy-MM-dd");
            params.put("created", today);
            filter.setFilters(params);
            PaginatedList<Map> todayOrders = this.customOrderService.findByCreated(filter);
            rows = todayOrders.getRows();
        }
        //当月
        if (type == 2) {
            String thisMonth = DateUtil.dateTimeToString(systemDate, "yyyy-MM");
            params.put("created", thisMonth);
            filter.setFilters(params);
            PaginatedList<Map> thisMonthOrders = this.customOrderService.findByCreated(filter);
            rows = thisMonthOrders.getRows();

        }

        //上月
        if (type == 3) {
            Date precedMonthFirstDay = DateUtil.getPrecedMonthFirstDay();
            String lastMonth = DateUtil.dateTimeToString(precedMonthFirstDay, "yyyy-MM");
            params.put("created", lastMonth);
            filter.setFilters(params);
            PaginatedList<Map> lastMonthOrders = this.customOrderService.findByCreated(filter);
            rows = lastMonthOrders.getRows();

        }
        List<Map> orderList = FactoryOrderUtil.getOrderList(rows);
        List<Map> allstatus = OrderStatus.getAll();
        MapContext result = MapContext.newOne();
        result.put("rows", orderList);
        result.put("allstatus", allstatus);
        return ResultFactory.generateRequestResult(result);
    }


    /**
     * 查询订单的详情(无用了)
     *
     * @param orderId
     * @return
     */
    @Override
    public RequestResult findOrderInfoByOrderId(String orderId) {
        Map orderInfo = this.customOrderService.findOrderInfoByOrderId(orderId);
        if (orderInfo != null) {
            String merger_name = (String) orderInfo.get("merger_name");
            String address = (String) orderInfo.get("address");
            String address1 = AddressUtils.getAddress(merger_name, address);
            orderInfo.put("address", address1);
            orderInfo.remove("merger_name");
        }

        List<CustomOrderDemand> demandList = this.customOrderDemandService.findByorderId(orderId);
        String demandName = "";
        if (demandList != null && demandList.size() > 0) {
            for (CustomOrderDemand customOrderDemand : demandList) {
                String name = customOrderDemand.getName();
                if (LwxfStringUtils.isNotBlank(name)) {
                    demandName = demandName + ";" + name;
                }
            }
            int i = demandName.indexOf(";");
            demandName = demandName.substring(i + 1);
        }
        orderInfo.put("demandName", demandName);
        Date created = (Date) orderInfo.get("created");

        MapContext flowPath = MapContext.newOne();
        //订单创建时间
        flowPath.put("name1", "订单生成");
        flowPath.put("orderCreated", created);
        //设计时间
        String designTime = this.customOrderDesignService.findTimeByOrderId(orderId);
        flowPath.put("name2", "设计");
        if (LwxfStringUtils.isNotBlank(designTime)) {
            flowPath.put("designTime", designTime);
        } else {
            flowPath.put("designTime", "还未开始");
        }
        //报价时间
        String quoteTime = this.orderAccountLogService.findTimeByOrderId(orderId);
        flowPath.put("name3", "报价");
        if (LwxfStringUtils.isNotBlank(quoteTime)) {
            flowPath.put("quoteTime", quoteTime);
        } else {
            flowPath.put("quoteTime", "还未开始");
        }
        //生产时间
        String processTime = this.orderProcessService.findTimeByOrderId(orderId);
        flowPath.put("name4", "生产");

        if (LwxfStringUtils.isNotBlank(processTime)) {
            flowPath.put("processTime", processTime);
        } else {
            flowPath.put("processTime", "还未开始");
        }
        //发货时间
        String shipmentTime = this.dispatchBillService.findTimeByOrderId(orderId);
        flowPath.put("name5", "发货");
        if (LwxfStringUtils.isNotBlank(shipmentTime)) {
            flowPath.put("shipmentTime", shipmentTime);
        } else {
            flowPath.put("shipmentTime", "还未开始");
        }
        //安装时间
        String installTime = this.dispatchListService.findTimeByOrderId(orderId);
        flowPath.put("name6", "安装");
        if (LwxfStringUtils.isNotBlank(installTime)) {
            flowPath.put("installTime", installTime);
        } else {
            flowPath.put("installTime", "还未开始");
        }

        MapContext result = MapContext.newOne();
        result.put("flowPath", flowPath);
        result.put("orderInfo", orderInfo);
        return ResultFactory.generateRequestResult(result);
    }


    /**
     * 根据条件查询订单的列表(无用了)
     *
     * @param pagination
     * @param mapContext
     * @return
     */
    @Override
    public RequestResult findFactoryOrderByCondition(Pagination pagination, MapContext mapContext) {
        PaginatedFilter filter = new PaginatedFilter();
        filter.setPagination(pagination);
        filter.setFilters(mapContext);
        PaginatedList<Map> ordersMap = this.customOrderService.findByCreated(filter);
        List<Map> rows = ordersMap.getRows();
        List<Map> orderList = FactoryOrderUtil.getOrderList(rows);
        List<Map> allstatus = OrderStatus.getAll();
        MapContext result = MapContext.newOne();
        result.put("rows", orderList);
        result.put("allstatus", allstatus);
        return ResultFactory.generateRequestResult(result);
    }

    /**
     * 根据订单的状态去查询订单的列表(无用了)
     *
     * @param pagination
     * @param params
     * @return
     */
    @Override
    public RequestResult findOrderListByStatus(Pagination pagination, MapContext params) {
        PaginatedFilter filter = new PaginatedFilter();
        filter.setFilters(params);
        filter.setPagination(pagination);
        PaginatedList<Map> mapList = this.customOrderService.findByCreated(filter);
        List<Map> rows = mapList.getRows();
        List<Map> orderList = FactoryOrderUtil.getOrderList(rows);
        List<Map> allstatus = OrderStatus.getAll();
        MapContext result = MapContext.newOne();
        result.put("rows", orderList);
        result.put("allstatus", allstatus);
        return ResultFactory.generateRequestResult(result);
    }

    /**
     * 查询订单的首页
     *
     * @return
     */
    @Override
    public RequestResult findOrderHomePage() {
        MapContext result = MapContext.newOne();
        Date systemDate = DateUtil.getSystemDate();
        String thisMonth = DateUtil.dateTimeToString(systemDate, "yyyy-MM");
        String thisYear = DateUtil.dateTimeToString(systemDate, "yyyy");
        String today = DateUtil.dateTimeToString(systemDate, "yyyy-MM-dd");
        String beginTime = null;
        String endTime = null;
        //查询当月的有效订单和无效订单
        int thisMonthPaidOrderNum = this.customOrderService.findPaidOrderNumByTime(beginTime,endTime,thisMonth);
        int thisMonthUnpaidOrderNum = this.customOrderService.findUnpaidOrderNumByTime(beginTime,endTime,thisMonth);

        //查询当年的有效订单和无效订单
        int thisYearPaidOrderNum = this.customOrderService.findPaidOrderNumByTime(beginTime,endTime,thisYear);
        int thisYearUnpaidOrderNum = this.customOrderService.findUnpaidOrderNumByTime(beginTime,endTime,thisYear);

        //今日散单（需设计）和今日正常订单
        MapContext timeAndIsDesign = MapContext.newOne();
        timeAndIsDesign.put("created", today);
        timeAndIsDesign.put("isDesign", 1);
        int unNeedDesignNum = this.customOrderService.findIsDesignNumByTime(timeAndIsDesign);//不需设计（正常）
        timeAndIsDesign.put("isDesign", 0);
        int needDesignNum = this.customOrderService.findIsDesignNumByTime(timeAndIsDesign);//需要设计（散单）
        MapContext count = MapContext.newOne();
        count.put("thisMonthPaidOrderNum", thisMonthPaidOrderNum);//当月有效订单
        count.put("thisMonthUnpaidOrderNum", thisMonthUnpaidOrderNum);//当月无效订单
        count.put("thisYearPaidOrderNum", thisYearPaidOrderNum);//当年有效订单
        count.put("thisYearUnpaidOrderNum", thisYearUnpaidOrderNum);//当年无效订单
        count.put("needDesignNum", needDesignNum);//今日接单需要设计的订单
        count.put("unNeedDesignNum", unNeedDesignNum);//今日接单不需要设计的订单
        //查询当日的订单列表
        Pagination pagination = Pagination.newOne();
        pagination.setPageNum(1);
        pagination.setPageSize(1000);
        PaginatedFilter filter = PaginatedFilter.newOne();
        filter.setPagination(pagination);
        MapContext params = MapContext.newOne();
        params.put("created", today);
        filter.setFilters(params);
        PaginatedList<Map> mapPaginatedList = this.customOrderService.findByConditions(filter);
        List<Map> rows = mapPaginatedList.getRows();
        //订单状态
        List<String> allStatus = Arrays.asList("有效订单", "无效订单");
        //订单类型
        List<String> allType = Arrays.asList("自产", "外协");
        //产品类型
        List<String> productType = Arrays.asList("橱柜（J）", "衣柜（B）", "成品家具（J）", "电器（J）", "五金（J）","样块（Y）");
        //查询设计师
        Role role = this.roleService.findRoleByKey(FactoryEmployeeRole.DESIGNER.getValue());
        MapContext RIdAndCId = MapContext.newOne();
        RIdAndCId.put("roleId", role.getId());
        RIdAndCId.put("companyId", WebUtils.getCurrCompanyId());
        List<Map> designers = this.companyEmployeeService.findEmployeeNameByIdByRoleId(RIdAndCId);
        //业务经理
        MapContext cidAndStatus = MapContext.newOne();
        cidAndStatus.put(WebConstant.KEY_ENTITY_COMPANY_ID, WebUtils.getCurrCompanyId());
        cidAndStatus.put("status", EmployeeStatus.NORMAL.getValue());
        List<Map> empName = this.companyEmployeeService.findListCidAndStatus(cidAndStatus);
        result.put("productType", productType);//产品类型
        result.put("allType", allType);//订单类型
        //result.put("shengchanchejian", shengchanchejian);//生产车间(目前不存在)

        result.put("designers", designers);//设计师
        result.put("empName", empName);//业务经理和接单员
        result.put("allstatus", allStatus);//订单状态
        result.put("todayOrderList", rows);//今日订单列表
        result.put("count", count);//统计的数
        return ResultFactory.generateRequestResult(result);
    }

    /**
     * 新建订单
     *
     * @return
     */
    @Override
    public RequestResult findCreatedOrder() {
        List<Map> allType = OrderProductType.getAll();
        List<Map> allSeries = OrderProductSeries.getAll();
        MapContext result = MapContext.newOne();
        result.put("allType", allType);
        result.put("allSeries", allSeries);
        return ResultFactory.generateRequestResult(result);
    }


    /**
     * 添加订单的信息
     *
     * @param params
     * @return
     */
    @Override
    public RequestResult addOrderInfo(MapContext params) {
        String orderType = (String)params.get("orderType");//订单类型
        String produceType = (String)params.get("produceType");//生产类型
        String deliveryDate = (String)params.get("deliveryDate");//交付时间
        String companyId = (String)params.get("companyId");//交付时间
        String companysalesmanId = (String)params.get("companysalesmanId");//交付时间
        String customerId = (String)params.get("customerId");//交付时间
        List<OrderProduct> productInfo = (List<OrderProduct>)params.get("productInfo");//交付时间
        return null;
    }


    /**
     * 根据经销商的名称或顾客的名称 查询经销商的公司信息
     *
     * @param name
     * @return
     */
    @Override
    public RequestResult findCompnayByCNameAndBName(String name, String cityName) {
        MapContext mapContext = MapContext.newOne();
        mapContext.put("name", name);
        mapContext.put("cityName", cityName);
        List<Map> CityNameAndName = this.companyService.findByCityNameAndName(mapContext);
        for (Map map:CityNameAndName){
            Object companyId = map.get("id");
            MapContext params = MapContext.newOne();
            params.put("companyId",companyId);
            params.put("employeeStatus", EmployeeStatus.NORMAL.getValue());
            List<MapContext> companyEmployees = this.companyEmployeeService.findCompanyEmployees(params);
            map.put("companyEmployees", companyEmployees);
        }
        return ResultFactory.generateRequestResult(CityNameAndName);
    }

    @Override
    public RequestResult findCompanyCustomer(String companyId) {
        List<Map> companyCustomer = this.companyCustomerService.findCompanyCustomer(companyId);
        return ResultFactory.generateRequestResult(companyCustomer);
    }

    /**
     * 根据条件查询订单的列表
     *
     * @param pagination
     * @param mapContext
     * @return
     */
    @Override
    public RequestResult findOrderListByCondition(Pagination pagination, MapContext mapContext, String countType) {
        PaginatedList<Map> orderLists = new PaginatedList<>();
        PaginatedFilter filter = new PaginatedFilter();
        filter.setPagination(pagination);

        if (countType != null) {
            Date systemDate = DateUtil.getSystemDate();
            String thisYear = DateUtil.dateTimeToString(systemDate, "yyyy");
            String thisMonth = DateUtil.dateTimeToString(systemDate, "yyyy-MM");
            String thisToday = DateUtil.dateTimeToString(systemDate, "yyyy-MM-dd");
            MapContext params = new MapContext();
            switch (countType) {
                case "1":
                    params.put("created", thisMonth);
                    filter.setFilters(params);
                    orderLists = this.customOrderService.findPaidOrderListByTime(filter);//本月有效
                    break;
                case "2":
                    params.put("created", thisMonth);
                    filter.setFilters(params);
                    orderLists = this.customOrderService.findUnpaidOrderListByTime(filter);//本月无效
                    break;
                case "3":
                    params.put("created", thisYear);
                    filter.setFilters(params);
                    orderLists = this.customOrderService.findPaidOrderListByTime(filter);//本年有效
                    break;
                case "4":
                    params.put("created", thisYear);
                    filter.setFilters(params);
                    orderLists = this.customOrderService.findUnpaidOrderListByTime(filter);//本年无效
                    break;
                case "5":
                    params.put("created", thisToday);
                    params.put("isDesign", CustomOrderIsDesign.UNWANTED_DESIGN.getValue());
                    filter.setFilters(params);
                    orderLists = this.customOrderService.findIsDesignListByTime(filter);//当天不需要设计
                    break;
                case "6":
                    params.put("created", thisToday);
                    params.put("isDesign", CustomOrderIsDesign.NEED_DESIGN.getValue());
                    filter.setFilters(params);
                    orderLists = this.customOrderService.findIsDesignListByTime(filter);//当天需要设计
                    break;
            }
        } else {
            filter.setFilters(mapContext);
            orderLists = this.customOrderService.findByConditions(filter);
        }
        List<Map> rows = orderLists.getRows();
        MapContext result = MapContext.newOne();
        result.put("rows", rows);
        result.put("countNum", orderLists.getPagination().getTotalCount());
        return ResultFactory.generateRequestResult(result);
    }

    @Override
    public RequestResult findOrderInfos(String orderId) {
        List<MapContext> result = new ArrayList<>();
        //查询订单的信息
        Map factoryOrderInfo = this.customOrderService.findFactoryOrderInfoById(orderId);
        Date documentary_time = (Date) factoryOrderInfo.get("documentary_time");
        Date deliverDate = (Date) factoryOrderInfo.get("estimated_delivery_date");
        String customerId = (String) factoryOrderInfo.get("customer_id");
        String designer = (String) factoryOrderInfo.get("designer");
        String merchandiser = (String) factoryOrderInfo.get("merchandiser");
        String timeConsuming = (String) factoryOrderInfo.get("time_consuming");
        Date systemDate = DateUtil.getSystemDate();
        //查询财务是否收款开始
        MapContext orderIdAndType = MapContext.newOne();
        orderIdAndType.put("orderId", orderId);
        orderIdAndType.put("type", PaymentType.B_TO_F_WITHHOLD.getValue());
        orderIdAndType.put("funds", PaymentFunds.ORDER_FEE_CHARGE.getValue());
        PaymentDto orderPayment = this.paymentService.findByOrderIdAndTypeAndFundsAndStatus(orderIdAndType);
        String isPay = "未审核";
        String isValid = "无效";
        if (orderPayment != null && orderPayment.getStatus() == PaymentStatus.PAID.getValue()) {
            isPay = "已审核";
            isValid = "有效";
        }
        factoryOrderInfo.put("isPay", isPay);
        factoryOrderInfo.put("isValid", isValid);
        //查询财务是否收款结束
        //查询已消耗开始
        String residueTime = "";
        if (timeConsuming != null) {
            residueTime = timeConsuming;
            factoryOrderInfo.put("residueTime", residueTime);
            factoryOrderInfo.put("overdue", "已完成");
        } else {
            if (documentary_time != null) {//下车间时间为空，已耗时和状态都为空
                long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
                long nh = 1000 * 60 * 60;//一小时的毫秒数
                long nm = 1000 * 60;//一分钟的毫秒数
                long ns = 1000;//一秒钟的毫秒数
                //下车间时间
                long documentaryTime = documentary_time.getTime();
                //系统时间
                long systemDateTime = systemDate.getTime();
                //系统时间-下车间的时间 = 已耗时的时间
                long diff = (systemDateTime - documentaryTime);

                if (diff > 0) {//如果已耗时时间小于0，已耗时为空
                    long day = diff / nd;//计算已耗时多少天
                    long hour = diff % nd / nh;//计算差多少小时
                    long min = diff % nd % nh / nm;//计算差多少分钟
                    long sec = diff % nd % nh % nm / ns;//计算差多少秒
                    residueTime = day + "天" + hour + "小时" + min + "分钟" + sec + "秒";
                    factoryOrderInfo.put("residueTime", residueTime);
                    if (deliverDate != null) {//交货时间为空，状态为空
                        //交货时间
                        long deliverDateTime = deliverDate.getTime();
                        //交货时间-下车间的时间 = 交货周期有多长时间
                        long totalDays = (deliverDateTime - documentaryTime);
                        long ghour = totalDays / nh;//计算一共有多少小时
                        long xhour = diff / nh;//计算消耗了多少小时
                        if (ghour < xhour) {
                            factoryOrderInfo.put("overdue", "超期");
                        } else if ((ghour - xhour) >= 0 && (ghour - xhour) <= 24) {
                            factoryOrderInfo.put("overdue", "临期");
                        } else if ((ghour - xhour) > 24) {
                            factoryOrderInfo.put("overdue", "正常");
                        }

                    } else {
                        factoryOrderInfo.put("overdue", "");
                    }
                } else {
                    factoryOrderInfo.put("residueTime", "");
                    factoryOrderInfo.put("overdue", "超期");
                }
            } else {
                factoryOrderInfo.put("residueTime", residueTime);
                factoryOrderInfo.put("overdue", "");
            }

        }
        //查询已消耗结束
        List<Object> factoryOrderInfos = new ArrayList<>();
        factoryOrderInfos.add(factoryOrderInfo);
        MapContext orderMap = MapContext.newOne();
        orderMap.put("title", "订单基础信息");
        orderMap.put("status", "1");
        orderMap.put("A", factoryOrderInfos);
        result.add(orderMap);//订单信息

        //查询经销商信息
        String companyId = (String) factoryOrderInfo.get("company_id");
        Map dealerInfo = this.companyService.findBCompanyInfoById(companyId);
        if (dealerInfo != null && dealerInfo.size() > 0) {
            String salesman = (String) factoryOrderInfo.get("salesman");
            User user = this.userService.findById(salesman);
            String salesmanName = "";
            if (user != null) {
                salesmanName = user.getName();//订单的负责人（家居顾问）
            }
            List<String> cids = new ArrayList<>();
            cids.add(companyId);
            List<CompanyEmployee> companyEmployees = this.companyEmployeeService.selectShopkeeperByCompanyIds(cids);
            String shopKeeperName = "";//店主的姓名
            String shopKeeperMobile = "";//店主的电话
            if (companyEmployees != null && companyEmployees.size() == 1) {
                //店主的用户id
                String shopKeeperId = companyEmployees.get(0).getUserId();
                User shopKeeperUserInfo = this.userService.findById(shopKeeperId);
                if (shopKeeperUserInfo != null) {
                    shopKeeperName = shopKeeperUserInfo.getName();
                    shopKeeperMobile = shopKeeperUserInfo.getMobile();
                }
            }
            dealerInfo.put("shopKeeperName", shopKeeperName);
            dealerInfo.put("shopKeeperMobile", shopKeeperMobile);
            dealerInfo.put("salesmanName", salesmanName);
            String merger_name = (String) dealerInfo.get("merger_name");
            String cityName = AddressUtils.getCityName(merger_name);
            dealerInfo.put("cityName", cityName);

            List<Object> dealerInfos = new ArrayList<>();
            dealerInfos.add(dealerInfo);
            MapContext dealerMap = MapContext.newOne();
            dealerMap.put("title", "经销商信息");
            dealerMap.put("status", "2");
            dealerMap.put("A", dealerInfos);
            result.add(dealerMap);//经销商信息
        }
        //查询C端的信息
        UserAreaDto userdto = this.userService.findByUid(customerId);
        MapContext userInfo = MapContext.newOne();
        if (userdto != null) {
            String cityAreaName = userdto.getCityAreaName();
            String cityName1 = AddressUtils.getCityName(cityAreaName);
            userInfo.put("address", userdto.getAddress());
            userInfo.put("cityAreaName", cityName1);
            userInfo.put("name", userdto.getName());
            userInfo.put("mobile", userdto.getMobile());
            List<Object> userInfos = new ArrayList<>();
            userInfos.add(userInfo);
            MapContext CUserMap = MapContext.newOne();
            CUserMap.put("title", "C端客户信息");
            CUserMap.put("status", "3");
            CUserMap.put("A", userInfos);
            result.add(CUserMap);//C端信息
        }


        //下单产品信息
        List<Map> orderproductList = this.orderProductService.findByOrderId(orderId);
        if (orderproductList != null && orderproductList.size() > 0) {
            for (Map map : orderproductList) {
                String belongId = (String) map.get("id");
                List<CustomOrderFiles> customOrderFilesList = this.customOrderFilesService.selectByOrderIdAndType(orderId, CustomOrderFilesType.ORDER_PRODUCT.getValue(), belongId);
                List<String> filesPath = new ArrayList<>();
                for (CustomOrderFiles files : customOrderFilesList) {
                    String path = files.getPath();
                    path = WebUtils.getDomainUrl() + path;
                    filesPath.add(path);
                }
                map.put("filesPath", filesPath);
            }
            MapContext orderproductMap = MapContext.newOne();
            orderproductMap.put("title", "下单产品信息");
            orderproductMap.put("status", "4");
            orderproductMap.put("A", orderproductList);
            result.add(orderproductMap);//下单产品信息
        }
        //接单信息
        MapContext receivingOrderInfo = MapContext.newOne();
        User merchandiserUser = this.userService.findById(merchandiser);
        if (merchandiserUser != null) {
            receivingOrderInfo.put("clerk", merchandiserUser.getName());//接单员
        } else {
            receivingOrderInfo.put("clerk", "");//接单员
        }
        User designerUser = this.userService.findById(designer);
        if (designerUser != null) {
            receivingOrderInfo.put("designer", designerUser.getName());//设计师
        } else {
            receivingOrderInfo.put("designer", "");//设计师
        }
        String businessManagerName = null;
        if (dealerInfo!=null&&dealerInfo.size()>0){
            businessManagerName = (String) dealerInfo.get("businessManagerName");
        }
        if (businessManagerName != null) {
            receivingOrderInfo.put("regionalManager", businessManagerName);//大区经理
        } else {
            receivingOrderInfo.put("regionalManager", "");//大区经理
        }
        if (receivingOrderInfo != null && receivingOrderInfo.size() > 0) {
            List<Object> receivingOrderInfos = new ArrayList<>();
            receivingOrderInfos.add(receivingOrderInfo);
            MapContext receivingMap = MapContext.newOne();
            receivingMap.put("title", "接单信息");
            receivingMap.put("status", "5");
            receivingMap.put("A", receivingOrderInfos);
            result.add(receivingMap);//接单信息
        }
        //发货信息
        Map shipmentsInfo = this.customOrderService.findShipmentsInfoByOrderId(orderId);
        if (shipmentsInfo != null && shipmentsInfo.size() > 0) {
            String cityName = (String) shipmentsInfo.get("merger_name");
            cityName = AddressUtils.getCityName(cityName);
            shipmentsInfo.put("cityName", cityName);
            shipmentsInfo.remove("merger_name");
            List<Object> shipmentsInfos = new ArrayList<>();
            shipmentsInfos.add(shipmentsInfo);
            MapContext shipmentsInfoMap = MapContext.newOne();
            shipmentsInfoMap.put("title", "发货信息");
            shipmentsInfoMap.put("status", "6");
            shipmentsInfoMap.put("A", shipmentsInfos);
            result.add(shipmentsInfoMap);//发货信息
        }
        //生产拆单信息
        List<Map> produceOrderList = this.produceOrderService.findByOrderId(orderId);
        if (produceOrderList != null && produceOrderList.size() > 0) {
            List<Object> produceOrderWay = new ArrayList<>();
            List<Object> produceOrderNoWay = new ArrayList<>();
            for (Map map : produceOrderList) {
                String typeName = (String) map.get("typeName");
                String wayName = (String) map.get("wayName");
                Integer way = (Integer) map.get("way");
                String name = typeName + "--" + wayName;
                String produceOrderId = (String) map.get("id");
                //查询拆单的图片
                List<CustomOrderFiles> customOrderFilesList = this.customOrderFilesService.selectByOrderIdAndType(orderId, CustomOrderFilesType.PRODUCE_ORDER.getValue(), produceOrderId);
                List<String> filesPath = new ArrayList<>();
                for (CustomOrderFiles files : customOrderFilesList) {
                    String path = files.getPath();
                    path = WebUtils.getDomainUrl() + path;
                    filesPath.add(path);
                }
                if (way == ProduceOrderWay.COORDINATION.getValue()) {
                    produceOrderWay.add(map);
                } else {
                    map.remove("coordinationBank");
                    map.remove("amount");
                    produceOrderNoWay.add(map);
                }
                map.put("filesPath", filesPath);
                map.put("name", name);
                map.remove("typeName");
                map.remove("wayName");
            }

            MapContext productOrderMap = MapContext.newOne();
            productOrderMap.put("title", "生产拆单信息");
            productOrderMap.put("status", "7");
            if (produceOrderNoWay != null && produceOrderNoWay.size() > 0) {
                productOrderMap.put("A", produceOrderNoWay);
            }
            if (produceOrderWay != null && produceOrderWay.size() > 0) {
                productOrderMap.put("B", produceOrderWay);
            }
            result.add(productOrderMap);//生产拆单
        }
        //设计师信息
        Map designInfo = this.customOrderDesignService.findByOrderIdAndStatus(orderId, OrderDesignStatus.CONFIRMED.getValue());
        if (designInfo != null && designInfo.size() > 0) {
            MapContext accountLog = MapContext.newOne();
            accountLog.put("orderId", orderId);
            accountLog.put("type", PaymentType.B_TO_F_WITHHOLD.getValue());
            accountLog.put("funds", PaymentFunds.DESIGN_FEE_PAY.getValue());
            PaymentDto designPay = this.paymentService.findByOrderIdAndTypeAndFundsAndStatus(accountLog);
            if (designPay != null && designPay.getStatus() == 1) {
                designInfo.put("isPay", "已审核");
                designInfo.put("amount", designPay.getAmount());
            } else if (designPay != null && designPay.getStatus() == 0) {
                designInfo.put("isPay", "未审核");
                designInfo.put("amount", designPay.getAmount());
            } else {
                designInfo.put("isPay", "未审核");
                designInfo.put("amount",0);
            }
            List<Object> designInfos = new ArrayList<>();
            designInfos.add(designInfo);
            MapContext designMap = MapContext.newOne();
            designMap.put("title", "设计师信息");
            designMap.put("status", "8");
            designMap.put("A", designInfos);
            result.add(designMap);//设计信息
        }
        //财务处理信息
        if (orderPayment != null) {
            MapContext orderPay = MapContext.newOne();
            orderPay.put("auditorName", "");
            orderPay.put("audited", "");
            BigDecimal amount = orderPayment.getAmount();
            String auditorName = orderPayment.getAuditorName();
            if (LwxfStringUtils.isNotBlank(auditorName)) {
                Date audi = orderPayment.getAudited();
                String audited = DateUtil.dateTimeToString(audi, DateUtil.FORMAT_DATETIME);
                orderPay.put("auditorName", auditorName);
                orderPay.put("audited", audited);
            }
            Integer status = orderPayment.getStatus();
            if (status == 1) {
                orderPay.put("status", "已审核");
            } else {
                orderPay.put("status", "未审核");
            }
            orderPay.put("amount", amount);
            orderPay.put("payType", "冲抵");
            List<Object> orderPays = new ArrayList<>();
            orderPays.add(orderPay);
            MapContext orderPayMap = MapContext.newOne();
            orderPayMap.put("title", "财务处理信息");
            orderPayMap.put("status", "9");
            orderPayMap.put("A", orderPays);
            result.add(orderPayMap);//财务处理信息
        }

        //外协单财务处理信息
        MapContext oIdAndType = MapContext.newOne();
        oIdAndType.put("orderId", orderId);
        oIdAndType.put("type", PaymentType.F_TO_COORDINATION.getValue());
        oIdAndType.put("funds", PaymentFunds.COORDINATION.getValue());
        List<Map> paymentMap = this.paymentService.findByOrderIdAndType(oIdAndType);
        if (paymentMap != null && paymentMap.size() > 0) {
            for (Map payMap : paymentMap) {
                BigDecimal amount1 = (BigDecimal) payMap.get("amount");
                if (produceOrderList != null && produceOrderList.size() > 0) {
                    String coordinationName = "";
                    for (Map map : produceOrderList) {
                        Integer way = (Integer) map.get("way");
                        BigDecimal amount = (BigDecimal) map.get("amount");
                        if (way == 1 && amount.compareTo(amount1) == 0) {
                            coordinationName = (String) map.get("coordinationName");
                        }
                    }
                    payMap.put("coordinationName", coordinationName);
                }
                String paymentId = (String) payMap.get("id");
                List<PaymentFiles> PaymentFiles = this.paymentFilesService.findByPaymentId(paymentId);
                List<String> filesPath = new ArrayList<>();
                for (PaymentFiles files : PaymentFiles) {
                    String path = files.getPath();
                    path = WebUtils.getDomainUrl() + path;
                    filesPath.add(path);
                }
                payMap.put("filesPath", filesPath);
            }
            MapContext coordinationPayMap = MapContext.newOne();
            coordinationPayMap.put("title", "外协单财务处理信息");
            coordinationPayMap.put("status", "10");
            coordinationPayMap.put("B", paymentMap);
            result.add(coordinationPayMap);//外协单财务处理信息
        }


        //生产跟单信息
        CustomOrder CustomOrder = this.customOrderService.findById(orderId);
        if (CustomOrder != null) {
            String name = "";
            String merchandiser1 = CustomOrder.getMerchandiser();
            if (LwxfStringUtils.isNotBlank(merchandiser)) {
                User u = this.userService.findById(merchandiser1);
                name = u.getName();
            }
            MapContext documentary = MapContext.newOne();
            Date documentaryTime = CustomOrder.getDocumentaryTime();//下车间时间
            String documentaryNotes = CustomOrder.getDocumentaryNotes();//跟单备注
            documentary.put("merchandiser", name);//跟单员
            documentary.put("documentaryTime", documentaryTime);
            documentary.put("documentaryNotes", documentaryNotes);
            List<Object> documentarys = new ArrayList<>();
            documentarys.add(documentary);
            MapContext documentaryMap = MapContext.newOne();
            documentaryMap.put("title", "生产跟进信息");
            documentaryMap.put("status", "11");
            documentaryMap.put("A", documentarys);
            result.add(documentaryMap);//生产跟单信息

        }

//        //生产信息 // TODO 需要改,目前只有生产信息，没有入库的信息
//        List<Map> prodessList = this.produceOrderService.findByOrderId(orderId);
//        if (prodessList != null && prodessList.size() > 0) {
//            MapContext orderIdAndType1 = MapContext.newOne();
//            orderIdAndType1.put("orderId", orderId);
//            for (Map map : prodessList) {
//                String produceOrderId = (String) map.get("id");
//                String type = (String) map.get("type");
//                String way = (String) map.get("way");
//                List<Map> produceFlowList = this.produceFlowService.findByProduceOrderId(produceOrderId);
//                if (produceFlowList != null && prodessList.size() > 0) {
//                    map.put("produceFlowList", produceFlowList);
//                    for (Map m : produceFlowList) {
//                        String node = (String) m.get("node");
//                        if (node.equals("包装")) {
//                            orderIdAndType1.put("type", orderId);//查询包裹柜体和门板自产的包裹
//                            List<FinishedStockItemDto> FinishedStockItemDtos = this.finishedStockItemService.findByOrderIdAndType(orderIdAndType1);
//                            if (FinishedStockItemDtos != null && FinishedStockItemDtos.size() > 0) {
//                                int parcelNumber = FinishedStockItemDtos.size();
//                                Integer operatorNum = 0;
//                                String notes1 = FinishedStockItemDtos.get(0).getNotes();
//                                Date operated = FinishedStockItemDtos.get(0).getOperated();
//                                String operatorName = FinishedStockItemDtos.get(0).getOperatorName();
//                                for (FinishedStockItemDto dto : FinishedStockItemDtos) {
//                                    Boolean in = dto.getIn();
//                                    if (in) {
//                                        operatorNum = operatorNum+1;
//                                    }
//                                }
//                                map.put("parcelNumber", parcelNumber);
//                                map.put("notes1", notes1);
//                                map.put("operated", operated);
//                                map.put("operatorName", operatorName);
//                                map.put("operatorNum", operatorNum);
//                            }
//                        }
//                    }
//                }
//            }
//            List<Map> putStorageMap = new ArrayList<>();
//            for (int i = 0; i < 3; i++) {
//                Map map = new HashMap();
//                map.put("name", "品名");
//                map.put("count", "数量");
//
//                map.put("notes1", "说明");
//                map.put("operated", "入库时间");
//                map.put("operatorName", "入库人");
//                map.put("operatorNum", "入库包裹");
//                map.put("notes2", "入库备注");
//                putStorageMap.add(map);
//            }
//            MapContext processMap = MapContext.newOne();
//            processMap.put("title", "生产执行&入库信息");
//            processMap.put("status", "12");
//            processMap.put("A", prodessList);
//            processMap.put("B", putStorageMap);
//            result.add(processMap);//生产信息&入库信息
//        }


        //生产信息
        List<Map> prodessList = this.produceOrderService.findByOrderId(orderId);
        if (prodessList != null && prodessList.size() > 0) {
            List<Map> putStorageMap = new ArrayList<>();
            List<Map> prodesss = new ArrayList<>();
            for (Map map : prodessList) {
                List<Map> produceFlowList = new ArrayList<>();
                Map prodess = new HashMap();
                Integer value;
                String produceOrderId = (String) map.get("id");
                Integer type = (Integer) map.get("type");//0柜体1门板2五金
                Integer way = (Integer) map.get("way");//0自产1外协2特供实木
                String name = (String) map.get("name");//品名
                Integer count = (Integer) map.get("count");//数量
                String notes2 = (String) map.get("notes");//说明
                switch (type) {
                    case 0://柜体
                        produceFlowList = this.produceFlowService.findByProduceOrderId(produceOrderId);
                        value = 0;
                        prodess.put("name", name);
                        prodess.put("count", count);
                        prodess.put("notes", notes2);
                        prodess.put("parcelNumber", 0);
                        prodesss.add(prodess);
                        break;
                    case 1:
                        switch (way) {
                            case 0://门板自产
                                produceFlowList = this.produceFlowService.findByProduceOrderId(produceOrderId);
                                value = 1;
                                prodess.put("name", name);
                                prodess.put("count", count);
                                prodess.put("notes", notes2);
                                prodess.put("parcelNumber", 0);
                                prodesss.add(prodess);
                                break;
                            case 1://门板外协
                                value = 2;
                                break;
                            default://门板特供实木
                                value = 3;
                                break;
                        }
                        break;
                    default://五金
                        value = 4;
                        break;

                }
                //查询柜体和门板自产的流程，已经查询柜体和门板自产的包裹（包裹总数以及入库的包裹数）
                MapContext orderIdAndType1 = MapContext.newOne();
                orderIdAndType1.put("orderId", orderId);
                if (produceFlowList != null && produceFlowList.size() > 0) {
                    prodess.put("produceFlowList", produceFlowList);
                    for (Map m : produceFlowList) {
                        String node = (String) m.get("node");
                        if (node.equals("包装")) {
                            //查询包裹柜体0和门板自产1的包裹
                            orderIdAndType1.put("type", value);//value = 0/1
                            List<FinishedStockItemDto> FinishedStockItemDtos = this.finishedStockItemService.findByOrderIdAndType(orderIdAndType1);
                            if (FinishedStockItemDtos != null && FinishedStockItemDtos.size() > 0) {
                                int parcelNumber = FinishedStockItemDtos.size();
                                Integer operatorNum = 0;
                                String notes1 = FinishedStockItemDtos.get(0).getNotes();
                                Date operated = FinishedStockItemDtos.get(0).getOperated();
                                String operatorName = FinishedStockItemDtos.get(0).getOperatorName();
                                for (FinishedStockItemDto dto : FinishedStockItemDtos) {
                                    Boolean in = dto.getIn();
                                    if (in) {
                                        operatorNum = operatorNum + 1;
                                    }
                                }
                                prodess.put("operatorNum", operatorNum);
                                prodess.put("parcelNumber", parcelNumber);
                                prodess.put("notes1", notes1);
                                prodess.put("operated", operated);
                                prodess.put("operatorName", operatorName);
                            }
                        }
                    }

                } else {
                    //查询包裹门板外协2，门板特供实木3，五金4的包裹
                    orderIdAndType1.put("type", value);//value = 2/3/4
                    orderIdAndType1.put("isIn", true);
                    List<FinishedStockItemDto> FinishedStockItemDtos = this.finishedStockItemService.findByOrderIdAndType(orderIdAndType1);
                    if (FinishedStockItemDtos != null && FinishedStockItemDtos.size() > 0) {
                        int parcelNumber = FinishedStockItemDtos.size();
                        String notes1 = FinishedStockItemDtos.get(0).getNotes();
                        Date operated = FinishedStockItemDtos.get(0).getOperated();
                        String operatorName = FinishedStockItemDtos.get(0).getOperatorName();


                        //入库的信息
                        Map StoMap = new HashMap();
                        StoMap.put("name", name);
                        StoMap.put("count", count);
                        StoMap.put("notes1", notes2);
                        StoMap.put("operated", operated);
                        StoMap.put("operatorName", operatorName);
                        StoMap.put("operatorNum", parcelNumber);
                        StoMap.put("notes2", notes1);
                        putStorageMap.add(StoMap);

                    }
                }

            }
            MapContext processMap = MapContext.newOne();
            processMap.put("title", "生产执行&入库信息");
            processMap.put("status", "12");
            processMap.put("A", prodesss);
            processMap.put("B", putStorageMap);
            result.add(processMap);//生产信息&入库信息
        }


        //物流信息
        List<Map> dispatchBillDtos = this.dispatchBillService.findFactoryDispatchsByOrderId(orderId);
        if (dispatchBillDtos != null && dispatchBillDtos.size() > 0) {
            List<Map> dispatchBillDtoList = new ArrayList<>();
            for (Map BillDto : dispatchBillDtos) {
                String id = (String) BillDto.get("id");
                List<Map> dispatchBill = this.finishedStockItemService.findBydispatchBillId(id);
                if (dispatchBill != null && dispatchBill.size() > 0) {
                    for (Map billMap : dispatchBill) {
                        Map map = new HashMap();
                        map.putAll(BillDto);
                        map.putAll(billMap);
                        dispatchBillDtoList.add(map);
                    }
                }
            }
            MapContext logisticsMap = MapContext.newOne();
            logisticsMap.put("title", "物流信息");
            logisticsMap.put("status", "13");
            logisticsMap.put("A", dispatchBillDtoList);
            result.add(logisticsMap);//物流信息

        }
        //售后信息
        List<Map> aftersaleList = this.aftersaleApplyService.findAftersaleByOrderId(orderId);
        if (aftersaleList != null && aftersaleList.size() > 0) {
            MapContext aftersaleMap = MapContext.newOne();
            aftersaleMap.put("title", "售后信息");
            aftersaleMap.put("status", "14");
            aftersaleMap.put("A", aftersaleList);
            result.add(aftersaleMap);//售后信息
        }
        return ResultFactory.generateRequestResult(result);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addOrderExcel(MultipartFile multipartFile) {
        try {
            List<String[]> list = POIUtil.readExcel(multipartFile);
            for (String[] arr : list) {
                CustomOrder customOrder = new CustomOrder();
                MapContext params = MapContext.newOne();
                params.put("name", arr[6]);
                List<Map> CityNameAndName = this.companyService.findByCityNameAndName(params);
                String companyId;
                if (CityNameAndName!=null&&CityNameAndName.size()==1){
                    companyId= (String)CityNameAndName.get(0).get("id");
                }else {
                    companyId = "4vq4anmz045c";
                }
                try {
                    customOrder.setNo(arr[0]);
                    //customOrder.setCustomerId();
                    customOrder.setCompanyId(companyId);
                    //customOrder.setCityAreaId();
                    customOrder.setAddress(arr[1]);
                    customOrder.setAcreage("0");
                    customOrder.setStatus(OrderStatus.RECEIVED.getValue());
                    //customOrder.setSalesman("4i5gkmpeqi33");
                    customOrder.setMerchandiser("4i5gkmpeqi33");
                    customOrder.setCreator("4i5gkmpeqi33");
                    Calendar c = new GregorianCalendar(1900,0,-1);
                    Date created= DateUtils.addDays(c.getTime(), Integer.valueOf(arr[8]).intValue());
                    customOrder.setCreated(created);
                    //customOrder.setDesigner();
                    //customOrder.setDesignScheme();
                    customOrder.setEarnest(0);
                    customOrder.setImprest(BigDecimal.ZERO);
                    customOrder.setRetainage(BigDecimal.ZERO);
                    customOrder.setAmount(BigDecimal.ZERO);
                    customOrder.setNotes(arr[2]);
                    //customOrder.setDesignStyle();
                    customOrder.setType(OrderType.NORMALORDER.getValue());
                    //customOrder.setParentId();
                    //customOrder.setEstimatedDeliveryDate();
                    //customOrder.setDeliveryDate();
                    customOrder.setDesignFee(0);
                    customOrder.setFactoryPrice(new BigDecimal(arr[3]));
                    //customOrder.setCustomerTel();
                    customOrder.setMarketPrice(BigDecimal.ZERO);
                    customOrder.setDiscounts(BigDecimal.ZERO);
                    customOrder.setFactoryDiscounts(BigDecimal.ZERO);
                    customOrder.setFactoryFinalPrice(new BigDecimal(arr[3]));
                    customOrder.setConfirmFprice(true);
                    customOrder.setConfirmCprice(true);
                    customOrder.setIsDesign(CustomOrderIsDesign.UNWANTED_DESIGN.getValue());
                    //customOrder.setTimeConsuming();
                    if (LwxfStringUtils.isNotBlank(arr[4])) {
                        customOrder.setCoordination(CustomOrderCoordination.NEED_COORDINATION.getValue());
                    } else {
                        customOrder.setCoordination(CustomOrderCoordination.UNWANTED_COORDINATION.getValue());
                    }
                    customOrder.setDocumentaryNotes("已下车间");
                    Date date= DateUtils.addDays(c.getTime(), Integer.valueOf(arr[5]).intValue());
                    customOrder.setDocumentaryTime(date);
                    customOrder.setConsigneeName(arr[6]);
                    customOrder.setConsigneeTel(arr[7]);
                    this.customOrderService.add(customOrder);



                    Payment payment = new Payment();
                    payment.setHolder("红田集团");
                    payment.setPayee("4j1u3r1efshq");
                    //payment.setNotes();
                    payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
                    payment.setStatus(PaymentStatus.PAID.getValue());
                    payment.setFunds(PaymentFunds.ORDER_FEE_CHARGE.getValue());
                    payment.setType(PaymentType.B_TO_F_WITHHOLD.getValue());
                    payment.setWay(PaymentWay.BANK.getValue());
                    payment.setCustomOrderId(customOrder.getId());
                    payment.setCreator("4i5gkmpeqi33");
                    payment.setCreated(date);
                    payment.setCompanyId(customOrder.getCompanyId());
                    payment.setAmount(customOrder.getFactoryFinalPrice());
                    payment.setAuditor("4i5gkmpeqi33");
                    payment.setAudited(date);
                    //payment.setDepositBank();
                    payment.setPayTime(date);
                    payment.setName("货款");
                    //payment.setHolderAccount("货款");
                    this.paymentService.add(payment);

                    DealerAccountLog dealerAccountLog = new DealerAccountLog();
                    dealerAccountLog.setAmount(customOrder.getFactoryFinalPrice());
                    dealerAccountLog.setCompanyId(customOrder.getCompanyId());
                    dealerAccountLog.setContent("订单货款");
                    dealerAccountLog.setCreated(date);
                    dealerAccountLog.setCreator("4i5gkmpeqi33");
                    if (customOrder.getCompanyId()!=null){
                        DealerAccount dea = this.dealerAccountService.findByCompanIdAndNatureAndType(customOrder.getCompanyId(), DealerAccountNature.PUBLIC.getValue(), DealerAccountType.FREE_ACCOUNT.getValue());
                        dealerAccountLog.setDealerAccountId(dea.getId());
                    }
                    dealerAccountLog.setDisburse(false);
                    dealerAccountLog.setResourceId(customOrder.getId());
                    dealerAccountLog.setType(2);
                    //dealerAccountLog.setNotes();
                    this.dealerAccountLogService.add(dealerAccountLog);


                    OrderProduct orderProduct = new OrderProduct();
                    String no = arr[0];
                    String substring = no.substring(0,5);
                    if (substring.contains("J")){
                        orderProduct.setType(OrderProductType.CUPBOARD.getValue());
                    }else if (substring.contains("B")){
                        orderProduct.setType(OrderProductType.WARDROBE.getValue());
                    }else if (substring.contains("Y")){
                        orderProduct.setType(OrderProductType.SAMPLE_PIECE.getValue());
                    }

                    orderProduct.setColor(arr[9]);
                    orderProduct.setCreated(created);
                    orderProduct.setCreator("4i5gkmpeqi33");
                    orderProduct.setCustomOrderId(customOrder.getId());
                    orderProduct.setNotes(arr[9]);
                    orderProduct.setDoor(arr[9]);
                    this.orderProductService.add(orderProduct);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    //continue;
                }
            }
        } catch (IOException ex) {
            ResultFactory.generateErrorResult("0000000", "数据导入失败");
        }
        return ResultFactory.generateSuccessResult();
    }

}
