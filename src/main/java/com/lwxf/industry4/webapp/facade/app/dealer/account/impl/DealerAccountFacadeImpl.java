package com.lwxf.industry4.webapp.facade.app.dealer.account.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentFilesService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserNotifyService;
import com.lwxf.industry4.webapp.common.enums.company.DealerAccountNature;
import com.lwxf.industry4.webapp.common.enums.company.DealerAccountType;
import com.lwxf.industry4.webapp.common.enums.company.DealerEmployeeRole;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.AddressUtils;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDto;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentDto;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccount;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentFiles;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.user.UserNotify;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.account.DealerAccountFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/24 18:40
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("dealerAccountFacade")
public class DealerAccountFacadeImpl extends BaseFacadeImpl implements DealerAccountFacade {

    @Resource(name = "dealerAccountService")
    private DealerAccountService dealerAccountService;
    @Resource(name = "companyService")
    private CompanyService companyService;
    @Resource(name = "customOrderService")
    private CustomOrderService customOrderService;
    @Resource(name = "paymentService")
    private PaymentService paymentService;
    @Resource(name = "paymentFilesService")
    private PaymentFilesService paymentFilesService;
    @Resource(name = "userNotifyService")
    private UserNotifyService userNotifyService;
    @Resource(name = "companyEmployeeService")
    private CompanyEmployeeService companyEmployeeService;
    @Resource(name = "roleService")
    private RoleService roleService;

    /**
     * 查询经销商的对厂账户资金信息
     *
     * @param companyId
     * @return
     */
    @Override
    public RequestResult findDealerAccount(String companyId,String uid) {
        //判断用户是不是B端经销商店主
        Role role = this.roleService.findRoleByCidAndUid(uid, companyId);
        if (null==role){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        if (!role.getKey().equals(DealerEmployeeRole.SHOPKEEPER.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }

        //查询公司信息，拿到公司名字
        Company com = this.companyService.findById(companyId);
        String companyName = com.getName();

        //查询公司的账户
        //List<DealerAccount> dealerAccountList = this.dealerAccountService.findByCompanIdAndNature(companyId, 1);
        //查询公司的预付账户
        DealerAccount dealerAccount = this.dealerAccountService.findByCompanIdAndNatureAndType(companyId, DealerAccountNature.PUBLIC.getValue(), DealerAccountType.FREE_ACCOUNT.getValue());
        BigDecimal balance = new BigDecimal(0);
        if (null != dealerAccount) {
            balance = dealerAccount.getBalance();
        }
        //查询根据预支付的订单
        MapContext mapContext = MapContext.newOne();
        mapContext.put("companyId", companyId);
        mapContext.put("status", OrderStatus.TO_AUDIT.getValue());
        List<CustomOrderDto> customOrders = this.customOrderService.findByCompanyIdAndStatus(mapContext);
        MapContext params = MapContext.newOne();
        params.put("companyName", companyName);
        if (null == customOrders || customOrders.size() == 0) {
            params.put("orderNum", 0);
            params.put("advance", 0);
        } else {
            params.put("orderNum", customOrders.size());
            BigDecimal advance = new BigDecimal(0);
            for (CustomOrder order : customOrders) {
                advance = advance.add(order.getFactoryFinalPrice());
            }
            params.put("advance", advance);
        }
        params.put("balance", balance);
        return ResultFactory.generateRequestResult(params);
    }


    /**
     * 添加银行卡信息
     *
     * @param params
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addBank(MapContext params) {
        this.companyService.updateByMapContext(params);
        return ResultFactory.generateSuccessResult();
    }


    /**
     * 查询经销商个人财务总账
     *
     * @param companyId
     * @param time
     * @return
     */
    @Override
    public RequestResult findDealerAccountMime(String companyId, String time,String uid) {
        //判断用户是不是B端经销商店主
        Role role = this.roleService.findRoleByCidAndUid(uid, companyId);
        if (null==role){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        if (!role.getKey().equals(DealerEmployeeRole.SHOPKEEPER.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        MapContext data = MapContext.newOne();

        //查询收入
        MapContext params = MapContext.newOne();
        params.put("companyId", companyId);
        params.put("type", 0);
        params.put("status", 1);
        params.put("audited", time);
        List<Payment> Incomes = this.paymentService.findByCompanyIdAndStatusAndType(params);
        //已入账
        BigDecimal incomed = new BigDecimal(0);
        if (null != Incomes && Incomes.size() > 0) {
            for (Payment payment : Incomes) {
                incomed = incomed.add(payment.getAmount());
            }
        }
        //待审核
        params.put("status", 0);
        params.remove("audited");
        params.put("created", time);
        List<Payment> to_incomes = this.paymentService.findByCompanyIdAndStatusAndType(params);
        BigDecimal for_income = new BigDecimal(0);
        if (null != to_incomes && to_incomes.size() > 0) {
            for (Payment payment : to_incomes) {
                for_income = for_income.add(payment.getAmount());
            }
        }

        //待收入
        params.remove("status");
        params.remove("type");
        List<CustomOrderDto> customOrders = this.customOrderService.findByCompanyIdAndStatus(params);//查询公司下的所有的订单
        BigDecimal remainingCases = new BigDecimal(0);
        if (customOrders != null && customOrders.size() > 0) {
            for (CustomOrder order : customOrders) {
                String id = order.getId();
                BigDecimal amount = order.getAmount();
                if (amount.compareTo(BigDecimal.ZERO) != 0) {
                    List<Payment> paymentList = this.paymentService.findByOrderIdAndStatus(id, 1);//查询已审核的订单财务
                    if (null != paymentList && paymentList.size() > 0) {
                        BigDecimal yishoukuan = new BigDecimal(0);
                        for (Payment p : paymentList) {
                            yishoukuan = yishoukuan.add(p.getAmount());
                        }
                        BigDecimal cases = amount.subtract(yishoukuan);//总额-已收的钱
                        if (cases.compareTo(BigDecimal.ZERO)>0){
                            remainingCases = remainingCases.add(cases);
                        }
                    } else {
                        remainingCases = remainingCases.add(amount);
                    }

                }
            }
        }

        data.put("remainingCases", remainingCases);//待收取
        data.put("toIncome", for_income);//待审核
        data.put("incomed", incomed);//已入账
        return ResultFactory.generateRequestResult(data);
    }


    /**
     * 个人财务收入明细列表
     *
     * @param companyId
     * @param status
     * @param time
     * @return
     */
    @Override
    public RequestResult findMimeAccountDetails(String companyId, Integer status, String time,Integer pageSize,Integer pageNum,String uid) {

        //判断用户是不是B端经销商店主
        Role role = this.roleService.findRoleByCidAndUid(uid, companyId);
        if (null==role){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        if (!role.getKey().equals(DealerEmployeeRole.SHOPKEEPER.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }

        PaginatedFilter filter = PaginatedFilter.newOne();
        Pagination pagination = Pagination.newOne();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        filter.setPagination(pagination);

        MapContext params = MapContext.newOne();
        params.put("companyId", companyId);
        if(LwxfStringUtils.isNotBlank(time)){
            params.put("created", time);
        }
        filter.setFilters(params);
        List<MapContext> remainingCases = new ArrayList<>();
        if (null == status || status == 2) {
            PaginatedList<CustomOrderDto> pageCustomOrders = this.customOrderService.findByCompanyIdAndStatus(filter);//查询公司下的所有的订单
            List<CustomOrderDto> customOrders = pageCustomOrders.getRows();
            if (customOrders != null && customOrders.size() > 0) {
                for (CustomOrderDto order : customOrders) {
                    String id = order.getId();
                    BigDecimal amount = order.getAmount();
                    if (amount.compareTo(BigDecimal.ZERO) != 0) {
                        List<Payment> paymentList = this.paymentService.findByOrderIdAndStatus(id, 1);//查询已审核的订单财务
                        MapContext remainingCasesq = MapContext.newOne();
                        if (null != paymentList && paymentList.size() > 0) {
                            BigDecimal yishoukuan = new BigDecimal(0);
                            for (Payment p : paymentList) {
                                yishoukuan = yishoukuan.add(p.getAmount());
                            }
                            BigDecimal cases = amount.subtract(yishoukuan);//总额-已收的钱
                            if (cases.compareTo(BigDecimal.ZERO) > 0) {
                                remainingCasesq.put("id", order.getId());
                                remainingCasesq.put("amount", cases);
                                remainingCasesq.put("salesmanName", order.getSalesmanName());
                                remainingCasesq.put("condition", "待收取");
                                remainingCasesq.put("no", order.getNo());
                                remainingCasesq.put("customerName", order.getCustomerName());
                                remainingCases.add(remainingCasesq);
                            }
                        } else {
                            remainingCasesq.put("id", order.getId());
                            remainingCasesq.put("amount", amount);
                            remainingCasesq.put("salesmanName", order.getSalesmanName());
                            remainingCasesq.put("condition", "待收取");
                            remainingCasesq.put("no", order.getNo());
                            remainingCasesq.put("customerName", order.getCustomerName());
                            remainingCases.add(remainingCasesq);
                        }

                    }
                }
                return ResultFactory.generateRequestResult(remainingCases);
            }
        }
            params.put("type", 0);
            params.put("status", status);
            params.remove("created");
            if (status==0){
                params.put("created",time);
            }else if (status==1){
                params.put("audited",time);
            }

            filter.setFilters(params);
            PaginatedList<Payment> payments = this.paymentService.findByCompanyIdAndStatusAndType(filter);
            return ResultFactory.generateRequestResult(payments.getRows());
    }


    /**
     * 经销商的个人账户的收入详情
     *
     * @return
     */
    @Override
    public RequestResult findMimeAccountInform(String companyId, String orderId, String paymentId,String uid) {
        //判断用户是不是B端经销商店主
        Role role = this.roleService.findRoleByCidAndUid(uid, companyId);
        if (null==role){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        if (!role.getKey().equals(DealerEmployeeRole.SHOPKEEPER.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        MapContext data = MapContext.newOne();
        if (LwxfStringUtils.isNotBlank(orderId)) {
            CustomOrderDto customOrder = this.customOrderService.findByOrderId(orderId);
            if (customOrder != null) {
                String id = customOrder.getId();
                BigDecimal amount = customOrder.getAmount();
                String city = customOrder.getCityName();
                String address1 = customOrder.getAddress();//详细地址
                String address = AddressUtils.getAddress(city, address1);
                customOrder.setAddress(address);
                customOrder.setOrderAmount(customOrder.getAmount());
                if (amount.compareTo(BigDecimal.ZERO) != 0) {
                    List<Payment> paymentList = this.paymentService.findByOrderIdAndStatus(id, 1);//查询已审核的订单财务
                    if (null != paymentList && paymentList.size() > 0) {
                        BigDecimal yishoukuan = new BigDecimal(0);
                        for (Payment p : paymentList) {
                            yishoukuan = yishoukuan.add(p.getAmount());
                        }
                        BigDecimal cases = amount.subtract(yishoukuan);//总额-已收的钱
                        customOrder.setOrderAmount(cases);
                    }
                }
            }
            data.put("customOrder", customOrder);
        }
        List<String> filesPath = new ArrayList<>();
        if (LwxfStringUtils.isNotBlank(paymentId)) {
            PaymentDto paymentDto = this.paymentService.findByPaymentId(paymentId);
            List<PaymentFiles> paymentFiles = this.paymentFilesService.findByPaymentId(paymentId);
            if (paymentFiles != null && paymentFiles.size() > 0) {
                for (PaymentFiles files : paymentFiles) {
                    String path = files.getPath();
                    filesPath.add(path);
                }
            }
            data.put("paymentDto", paymentDto);
            data.put("filesPath", filesPath);
            return ResultFactory.generateRequestResult(data);
        }

        return ResultFactory.generateRequestResult(data);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult reminders(MapContext params) {

        String companyId = (String) params.get("companyId");
        String salmanId = (String) params.get("salmanId");
        String sender = (String) params.get("sender");
        String customName = (String) params.get("customName");
        String customTel = (String) params.get("customTel");
        String address = (String) params.get("address");
        //BigDecimal amount = new BigDecimal((Integer.valueOf((String.valueOf(params.get("amount"))))));
        String amount = String.valueOf(params.get("amount"));
        String no = (String) params.get("no");

        UserNotify notify = new UserNotify();
        Date systemDate = DateUtil.getSystemDate();
        notify.setCompanyId(companyId);
        notify.setContent("订单催款：顾客：" + customName + ",电话：" + customTel + ",地址：" + address + ",金额：" + amount + "元,订单编号：" + no + ",需要去收款，记得别忘啊！");
        notify.setCreated(systemDate);
        notify.setName("催款");
        notify.setReaded(false);
        notify.setSender(sender);
        notify.setTopTime(systemDate);
        notify.setUserId(salmanId);
        notify.setType(1);
        int i = this.userNotifyService.add(notify);
        MapContext value = MapContext.newOne();
        value.put("value","已通知业务员！");
        return ResultFactory.generateRequestResult(value);
    }
}

