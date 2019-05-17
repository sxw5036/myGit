package com.lwxf.industry4.webapp.facade.app.dealer.account.impl;

import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountLogService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentFilesService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.common.enums.company.DealerEmployeeRole;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.AddressUtils;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDto;
import com.lwxf.industry4.webapp.domain.dto.dealer.DealerAccountLogDto;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentDto;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccountLog;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentFiles;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.account.DealerAccountLogFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/1/7 16:28
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("dealerAccountLogFacade")
public class DealerAccountLogFacadeImpl extends BaseFacadeImpl implements DealerAccountLogFacade {

    @Resource(name = "dealerAccountLogService")
    private DealerAccountLogService dealerAccountLogService;

    @Resource(name = "paymentService")
    private PaymentService paymentService;
    @Resource(name = "paymentFilesService")
    private PaymentFilesService paymentFilesService;
    @Resource(name = "customOrderService")
    private CustomOrderService customOrderService;
    @Resource(name = "roleService")
    private RoleService roleService;

    /**
     * 列表
     * @param mapContext
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public RequestResult findDetailsLog(MapContext mapContext,Integer pageSize,Integer pageNum,String uid) {
        String companyId = mapContext.getTypedValue("companyId",String.class);
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
        filter.setFilters(mapContext);
        PaginatedList<DealerAccountLog> accountLogs = this.dealerAccountLogService.findByCompanyIdAndCondition(filter);
        return ResultFactory.generateRequestResult(accountLogs);
    }


    /**
     * 详情
     * @param resourceId
     * @return
     */
    @Override
    public RequestResult findPaymentById(String resourceId,String logId,Integer type,String uid,String companyId) {
        //判断用户是不是B端经销商店主
        Role role = this.roleService.findRoleByCidAndUid(uid, companyId);
        if (null==role){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        if (!role.getKey().equals(DealerEmployeeRole.SHOPKEEPER.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }

        MapContext data = MapContext.newOne();
        CustomOrderDto orderDto = this.customOrderService.findByOrderId(resourceId);
        if (null!=orderDto){
            String city = orderDto.getCityName();
            String address1 = orderDto.getAddress();//详细地址
            String address = AddressUtils.getAddress(city, address1);
            orderDto.setAddress(address);
            DealerAccountLogDto accountLog= this.dealerAccountLogService.findByLogId(logId);
            if (null!=accountLog){
                accountLog.setCondition("已完成");
            }
            data.put("order",orderDto);
            data.put("accountLog",accountLog);
        }else {
            PaymentDto paymentDto = this.paymentService.findByPId(resourceId);
            List<PaymentFiles> paymentFiles = this.paymentFilesService.findByPaymentId(resourceId);
            if (paymentFiles == null || paymentFiles.size() == 0) {
                data.put("paymentFiles", paymentFiles);
            } else {
                List<String> filesPath = new ArrayList<>();
                for (PaymentFiles files : paymentFiles) {
                    filesPath.add(files.getPath());
                    data.put("filePath", filesPath);
                }
            }
            data.put("paymentDto", paymentDto);
        }
        data.put("type", type);
        return ResultFactory.generateRequestResult(data);
    }

    @Override
    public RequestResult findByCompanyId(PaginatedFilter filter,String uid,String companyId){
        //判断用户是不是B端经销商店主
        Role role = this.roleService.findRoleByCidAndUid(uid, companyId);
        if (null==role){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        if (!role.getKey().equals(DealerEmployeeRole.SHOPKEEPER.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
        }
        PaginatedList<Payment> paymentList = this.paymentService.findByCompanyIdAndStatusAndType(filter);
        return ResultFactory.generateRequestResult(paymentList);
    }

}

