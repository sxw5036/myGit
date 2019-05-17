package com.lwxf.industry4.webapp.facade.app.dealer.account.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountDetailsService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.common.enums.company.DealerEmployeeRole;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccountDetails;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.account.DealerAccountDetailsFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/24 19:17
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("dealerAccountDetailsFacade")
public class DealerAccountDetailsFacadeImpl extends BaseFacadeImpl implements DealerAccountDetailsFacade {


    @Resource(name = "dealerAccountDetailsService")
    private DealerAccountDetailsService dealerAccountDetailsService;

    @Resource(name = "companyEmployeeService")
    private CompanyEmployeeService companyEmployeeService;

    @Resource(name = "paymentService")
    private PaymentService paymentService;
    @Resource(name = "roleService")
    private RoleService roleService;


    /**
     * 充值或提现
     * @param params
     * @param companyId
     * @param uid
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addAccountDetails(MapContext params,String companyId,String uid) {

        //判断用户是不是B端经销商店主
        Role role = this.roleService.findRoleByCidAndUid(uid, companyId);
        if (null==role){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        if (!role.getKey().equals(DealerEmployeeRole.SHOPKEEPER.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }

        Date payTime = DateUtil.stringToDate((String) params.get("payTime"), "yyyy-MM-dd HH:mm");//支付时间
        String way = (String) params.get("way");//支付方式
        String funds = (String) params.get("funds");//支付款型
        String holder = (String) params.get("holder");//收款人
        String amount = (String) params.get("amount");//金额
        String type = (String) params.get("type");//支付类型
        String name = (String) params.get("name");//标题
        String notes = (String) params.get("notes");//备注
        String depositBank = (String) params.get("depositBank");//收款银行
        String holderAccount = (String) params.get("holderAccount");//收款账号

        Payment payment = new Payment();
        payment.setPayee("4j1u3r1efshq");
        payment.setCompanyId(companyId);
        payment.setStatus(0);
        payment.setCreated(DateUtil.getSystemDate());
        payment.setPayTime(payTime);
        payment.setWay(Integer.valueOf(way));
        payment.setFunds(Integer.valueOf(funds));
        payment.setHolder(holder);
        payment.setAmount(new BigDecimal(amount));
        payment.setType(Integer.valueOf(type));
        payment.setName(name);
        payment.setCreator(uid);
        payment.setNotes(notes);
        payment.setDepositBank(depositBank);
        payment.setHolderAccount(holderAccount);
        payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
        //payment.setCustomOrderId();
        RequestResult result = payment.validateFields();
        if (null!=result){
            return  result;
        }
        this.paymentService.add(payment);


        MapContext mapContext = MapContext.newOne();
        mapContext.put("id",payment.getId());
        return ResultFactory.generateRequestResult(mapContext);
    }







    /**
     * 明细列表
     * @param companyId
     * @param accountId
     * @return
     */
    @Override
    public RequestResult findAccountDetails(String companyId, String accountId,String uid) {
        //判断用户是不是B端经销商店主
        Role role = this.roleService.findRoleByCidAndUid(uid, companyId);
        if (null==role){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        if (!role.getKey().equals(DealerEmployeeRole.SHOPKEEPER.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        List<DealerAccountDetails> dealerAccountDetails = this.dealerAccountDetailsService.findByAccountId(accountId);
        return ResultFactory.generateRequestResult(dealerAccountDetails);
    }

    /**
     * 明细详情
     * @return
     */
    @Override
    public RequestResult findAccountDetailsById(String detailsId,String uid,String companyId) {
        //判断用户是不是B端经销商店主
        Role role = this.roleService.findRoleByCidAndUid(uid, companyId);
        if (null==role){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        if (!role.getKey().equals(DealerEmployeeRole.SHOPKEEPER.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        DealerAccountDetails dealerAccountDetails = this.dealerAccountDetailsService.findById(detailsId);
        return ResultFactory.generateRequestResult(dealerAccountDetails);
    }


}

