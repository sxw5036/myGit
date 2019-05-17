package com.lwxf.industry4.webapp.controller.app.dealer.account;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.account.DealerAccountDetailsFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/24 19:22
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class DealerAccountDetailsController extends BaseDealerController {


    @Resource(name = "dealerAccountDetailsFacade")
    private DealerAccountDetailsFacade dealerAccountDetailsFacade;

    /**
     * 添加支付或提现
     * @param params
     * @param request
     * @param companyId
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/dealerAccounts")
    public RequestResult addAccountDetails(@RequestBody MapContext params,
                                           HttpServletRequest request,
                                           @PathVariable String companyId){
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bdealer-prepay-add";
        RequestResult result = this.validUserPermission(request,xp);
        if (null!=result){
            return result;
        }
        return this.dealerAccountDetailsFacade.addAccountDetails(params,companyId,uid);
    }


    /**
     * 查询经销商明细表列表
     * @param companyId
     * @param accountId
     * @param request
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/dealerAccounts/{accountId}")
    public RequestResult findAccountDetails(@PathVariable String companyId,
                                            @PathVariable String accountId,
                                            HttpServletRequest request){
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
//        String xp = "bdealer-prepay-view";//权限验证
//        RequestResult result = this.validUserPermission(request,xp);
//        if (null!=result){
//            return result;
//        }
        return this.dealerAccountDetailsFacade.findAccountDetails(companyId, accountId,uid);
    }





    /**
     * 查询经销商明细表单个详情
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/dealerAccounts/{accountId}/dealerAccountDetails/{detailsId}")
    public RequestResult findAccountDetailsById(@PathVariable String companyId,
                                               @PathVariable String accountId,
                                               HttpServletRequest request,
                                               @PathVariable String detailsId){
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
//        String xp = "bdealer-prepay-view";//权限验证
//        RequestResult result = this.validUserPermission(request,xp);
//        if (null!=result){
//            return result;
//        }
        return this.dealerAccountDetailsFacade.findAccountDetailsById(detailsId,uid,companyId);
    }




}

