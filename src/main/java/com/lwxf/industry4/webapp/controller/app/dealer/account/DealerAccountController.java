package com.lwxf.industry4.webapp.controller.app.dealer.account;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.account.DealerAccountFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/24 18:29
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class DealerAccountController extends BaseDealerController {

    @Resource(name = "dealerAccountFacade")
    private DealerAccountFacade dealerAccountFacade;

    /**
     * 查询经销商的对厂账户资金信息
     * @param companyId
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/factory/dealerAccounts")
    public String findDealerAccountFactory(@PathVariable String companyId,
                                           HttpServletRequest request){

        String uid = request.getHeader("X-UID");//用户Id

        String cid = request.getHeader("X-CID");//公司id
        String xp = "bdealer-prepay-view";//权限验证
        JsonMapper mapper = new JsonMapper();
        if (!companyId.equals(cid)){
            return mapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
        RequestResult result = this.validUserPermission(request,xp);
        if (null!=result){
            return mapper.toJson(result);
        }
        return mapper.toJson(this.dealerAccountFacade.findDealerAccount(companyId,uid));
    }


    /**
     * 添加银行账号信息
     * @param companyId
     * @param request
     * @param params
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/addBank")
    public RequestResult addBank(@PathVariable String companyId,
                                 HttpServletRequest request,
                                 @RequestBody  MapContext params){
        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        RequestResult result = Company.validateFields(params);
        if (null!=result){
            return result;
        }
        String xp = "bdealer-prepay-add";//权限验证
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        params.put("id",companyId);
        return this.dealerAccountFacade.addBank(params);
    }


    /**
     * 查询经销商个人财务总账
     * @param companyId
     * @param request
     * @param time
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/mime/dealerAccounts")
    public RequestResult findDealerAccountMime(@PathVariable String companyId,
                                               HttpServletRequest request,
                                               @RequestParam(required = false) String time){
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bincomepaymng-incomeinfo-view";//权限验证
        RequestResult result = this.validUserPermission(request,xp);
        if (null!=result){
            return result;
        }
        return this.dealerAccountFacade.findDealerAccountMime(companyId,time,uid);
    }


    /**
     * 查询经销商个人财务明细列表
     * @param companyId
     * @param request
     * @param time
     * @param status
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/mime/dealerAccounts/details")
    public String findMimeAccountDetails(@PathVariable String companyId,
                                                HttpServletRequest request,
                                                @RequestParam(required = false) String time,
                                                @RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) Integer pageSize,
                                         @RequestParam(required = false) Integer pageNum){

        if(null == pageSize){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if(null == pageNum){
            pageNum = 1;
        }
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        if (!companyId.equals(cid)){
            return mapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
        String xp = "bincomepaymng-incomeinfo-view";//权限验证
        RequestResult result = this.validUserPermission(request,xp);
        if (null!=result){
            return mapper.toJson(result);
        }
        RequestResult mimeAccountDetails = this.dealerAccountFacade.findMimeAccountDetails(companyId, status, time,pageSize,pageNum,uid);
        return mapper.toJson(mimeAccountDetails);
    }


    /**
     * 经销商的个人账户的收入详情
     * @param companyId
     * @param request
     * @param orderId
     * @param paymentId
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/mime/dealerAccounts/inform")
    public String findMimeAccountInform(@PathVariable String companyId,
                                               HttpServletRequest request,
                                               @RequestParam String orderId,
                                               @RequestParam(required = false) String paymentId){
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        if (!companyId.equals(cid)){
            return mapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
        String xp = "bincomepaymng-incomeinfo-view";//权限验证
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return mapper.toJson(result1);
        }
        RequestResult result = this.dealerAccountFacade.findMimeAccountInform(companyId, orderId, paymentId,uid);
        return mapper.toJson(result);
    }


    /**
     * 经销商催款
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/customorders/{orderId}/reminders")
    public RequestResult reminders(@RequestBody MapContext params,
                                   HttpServletRequest request,
                                   @PathVariable String companyId
                                   ){
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id
        JsonMapper mapper = new JsonMapper();
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bincomepaymng-incomeinfo-view";//权限验证
        RequestResult result = this.validUserPermission(request,xp);
        if (null!=result){
            return result;
        }
        params.put("companyId",companyId);
        params.put("sender",uid);
        return this.dealerAccountFacade.reminders(params);
    }



}

