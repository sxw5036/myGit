package com.lwxf.industry4.webapp.facade.app.factory.factoryorder.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderDesignService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderFilesService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.company.EmployeeStatus;
import com.lwxf.industry4.webapp.common.enums.company.FactoryEmployeeRole;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderFilesType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentStatus;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentWay;
import com.lwxf.industry4.webapp.common.enums.order.OrderDesignStatus;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderDesign;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderFiles;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryorder.FactoryOrderDesignFacade;
import com.lwxf.industry4.webapp.facade.app.factory.factoryorder.FactoryOrderProcessFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/7 10:48
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factoryOrderDesignFacade")
public class FactoryOrderDesignFacadeImpl extends BaseFacadeImpl implements FactoryOrderDesignFacade {

    @Resource(name = "customOrderDesignService")
    private CustomOrderDesignService customOrderDesignService;
    @Resource(name = "companyEmployeeService")
    private CompanyEmployeeService companyEmployeeService;
    @Resource(name = "customOrderFilesService")
    private CustomOrderFilesService customOrderFilesService;
    @Resource(name = "factoryOrderProcessFacade")
    private FactoryOrderProcessFacade factoryOrderProcessFacade;
    @Resource(name = "paymentService")
    private PaymentService paymentService;
    @Resource(name = "roleService")
    private RoleService roleService;
    @Resource(name = "customOrderService")
    private CustomOrderService customOrderService;

    /**
     * 设计概览 - 待设计
     *
     * @return
     */
    @Override
    public RequestResult findUnDesign(Pagination pagin, MapContext params) {
        PaginatedFilter paginatedFilter = PaginatedFilter.newOne();
        paginatedFilter.setFilters(params);
        paginatedFilter.setPagination(pagin);
        PaginatedList<Map> unDesign = customOrderDesignService.findUnDesign(paginatedFilter);
        List<Map> rows = unDesign.getRows();
        Pagination pagination = unDesign.getPagination();
        int totalCount = pagination.getTotalCount();
        //查询接单人
        MapContext cidAndStatus = MapContext.newOne();
        cidAndStatus.put(WebConstant.KEY_ENTITY_COMPANY_ID, WebUtils.getCurrCompanyId());
        cidAndStatus.put("status", EmployeeStatus.NORMAL.getValue());
        List<Map> empName = this.companyEmployeeService.findListCidAndStatus(cidAndStatus);

        MapContext result = MapContext.newOne();
        result.put("totalCount", totalCount);
        result.put("unDesign", rows);
        result.put("empName", empName);
        return ResultFactory.generateRequestResult(result);
    }

    /**
     * 设计概览 - 已设计
     *
     * @return
     */
    @Override
    public RequestResult findDesigned(Pagination pagin, MapContext params) {
        PaginatedFilter paginatedFilter = PaginatedFilter.newOne();
        paginatedFilter.setFilters(params);
        paginatedFilter.setPagination(pagin);
        PaginatedList<Map> Designed = this.customOrderDesignService.findDesigned(paginatedFilter);
        List<Map> rows = Designed.getRows();
        Pagination pagination = Designed.getPagination();
        int totalCount = pagination.getTotalCount();
        //查询设计师
        Role role = this.roleService.findRoleByKey(FactoryEmployeeRole.DESIGNER.getValue());
        MapContext RIdAndCId= MapContext.newOne();
        RIdAndCId.put("roleId", role.getId());
        RIdAndCId.put("companyId", WebUtils.getCurrCompanyId());
        List<Map> designers = this.companyEmployeeService.findEmployeeNameByIdByRoleId(RIdAndCId);
        MapContext result = MapContext.newOne();
        result.put("totalCount", totalCount);
        result.put("Designed", rows);
        result.put("designers", designers);
        return ResultFactory.generateRequestResult(result);
    }


    @Override
    public RequestResult findDesignInfo(String orderId, String designId) {

        Map orderBaseInfo = this.factoryOrderProcessFacade.findOrderBaseInfo(orderId, 3);
        MapContext result = MapContext.newOne();
        if (!designId.equals("0")) {
            MapContext params = MapContext.newOne();
            params.put("orderId", orderId);
            params.put("designId", designId);
            Map designInfo = this.customOrderDesignService.findByOrderIdAndDesignId(params);
            if (designInfo != null) {
                List<CustomOrderFiles> customOrderFilesList = this.customOrderFilesService.selectByOrderIdAndType(orderId, CustomOrderFilesType.DESIGN.getValue(), designId);
                List<String> filesPath = new ArrayList<>();
                for (CustomOrderFiles files : customOrderFilesList) {
                    String path = files.getPath();
                    path = WebUtils.getDomainUrl() + path;
                    filesPath.add(path);
                }
                designInfo.put("filesPath", filesPath);
            }
            result.put("designInfo", designInfo);
        } else {
            //查询设计师
            Role role = this.roleService.findRoleByKey(FactoryEmployeeRole.DESIGNER.getValue());
            MapContext RIdAndCId= MapContext.newOne();
            RIdAndCId.put("roleId", role.getId());
            RIdAndCId.put("companyId", WebUtils.getCurrCompanyId());
            RIdAndCId.put("status",EmployeeStatus.NORMAL.getValue());
            List<Map> designers = this.companyEmployeeService.findEmployeeNameByIdByRoleId(RIdAndCId);
            result.put("designers", designers);
        }
        result.put("orderBaseInfo", orderBaseInfo);
        return ResultFactory.generateRequestResult(result);
    }


    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addDesign(MapContext mapContext, String uid) {
        BigDecimal amount = mapContext.getTypedValue("amount", BigDecimal.class);
        String notes = mapContext.getTypedValue("notes", String.class);
        String designer = mapContext.getTypedValue("designer", String.class);
        Date created = mapContext.getTypedValue("created", Date.class);
        String orderId = mapContext.getTypedValue("orderId", String.class);
        String companyId = mapContext.getTypedValue("companyId", String.class);
        List<String> cids = new ArrayList<>();
        cids.add(companyId);
        List<CompanyEmployee> companyEmployees = this.companyEmployeeService.selectShopkeeperByCompanyIds(cids);
        if (companyEmployees == null || companyEmployees.size() != 1) {
            return ResultFactory.generateErrorResult(ErrorCodes.SYS_EXECUTE_FAIL_00001, AppBeanInjector.i18nUtil.getMessage("SYS_EXECUTE_FAIL_00001"));
        }
        String shopKeeper = companyEmployees.get(0).getUserId();

        //添加支付
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setCompanyId(companyId);
        payment.setCreated(DateUtil.getSystemDate());
        payment.setCreator(uid);
        payment.setCustomOrderId(orderId);
        payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
        payment.setWay(PaymentWay.BANK.getValue());
        payment.setType(PaymentType.B_TO_F_WITHHOLD.getValue());
        payment.setFunds(PaymentFunds.DESIGN_FEE_CHARGE.getValue());
        payment.setStatus(PaymentStatus.PENDING_PAYMENT.getValue());
        payment.setPayee(shopKeeper);//付款人
        payment.setHolder("红田集团");
        RequestResult paymentSesult = payment.validateFields();
        if (paymentSesult != null) {
            return paymentSesult;
        }
        this.paymentService.add(payment);

        //修改订单中的设计费
        MapContext orderMap = MapContext.newOne();
        orderMap.put("id", orderId);
        orderMap.put("designFee", amount);
        //orderMap.put("status", OrderStatus.);
        CustomOrder customOrder = this.customOrderService.findById(orderId);
        if (LwxfStringUtils.isBlank(customOrder.getDesigner())){
            orderMap.put("designer", designer);
        }
        this.customOrderService.updateByMapContext(orderMap);
        //添加设计
        CustomOrderDesign customOrderDesign = new CustomOrderDesign();
        customOrderDesign.setCreated(DateUtil.getSystemDate());
        customOrderDesign.setCustomOrderId(orderId);
        customOrderDesign.setDesigner(designer);
        customOrderDesign.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.DESIGN_NO));
        customOrderDesign.setNotes(notes);
        customOrderDesign.setStatus(OrderDesignStatus.CONFIRMED.getValue());
        customOrderDesign.setName("无名");
        customOrderDesign.setValuation(new BigDecimal(0));
        customOrderDesign.setEndTime(created);
        RequestResult customOrderDesignSesult = customOrderDesign.validateFields();
        if (customOrderDesignSesult != null) {
            return customOrderDesignSesult;
        }

        this.customOrderDesignService.add(customOrderDesign);
        MapContext result = MapContext.newOne();
        result.put("designId", customOrderDesign.getId());
        result.put("orderId", orderId);
        return ResultFactory.generateRequestResult(result);
    }
}
