package com.lwxf.industry4.webapp.controller.app.dealer.account;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.account.DealerAccountLogFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/1/7 16:26
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class DealerAccountLogController extends BaseDealerController {
    @Resource(name = "dealerAccountLogFacade")
    private DealerAccountLogFacade dealerAccountLogFacade;


    /**
     * 查询对厂账户的明细列表
     * @param companyId
     * @param request
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/accountLogs")
    public String findDetailsLog(@PathVariable String companyId,
                                        HttpServletRequest request,
                                        @RequestParam(required = false)Integer pageSize,
                                        @RequestParam(required = false)Integer pageNum,
                                        @RequestParam(required = false)String beginTime,
                                        @RequestParam(required = false) String endTime,
                                        @RequestParam(required = false) Integer type){

        if(null == pageSize){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if(null == pageNum){
            pageNum = 1;
        }
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id
        String xp = "bdealer-prepay-view";//权限验证
        JsonMapper mapper = new JsonMapper();
        if (!companyId.equals(cid)){
            return mapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return mapper.toJson(result1);
        }
        MapContext mapContext = MapContext.newOne();
        mapContext.put("companyId",companyId);
        mapContext.put("beginTime",beginTime);
        mapContext.put("endTime",endTime);
        mapContext.put("type",type);
        RequestResult detailsLog = this.dealerAccountLogFacade.findDetailsLog(mapContext, pageSize, pageNum,uid);
        return mapper.toJson(detailsLog);
    }


    /**
     * 查询对厂账户的明细详情
     * @param companyId
     * @param request
     * @param resourceId
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/accountLogs/{logId}")
    public String findPaymentById(@PathVariable String companyId,
                                  HttpServletRequest request,
                                  @RequestParam String resourceId,
                                  @PathVariable String logId,
                                  @RequestParam Integer type){
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id
        String xp = "bdealer-prepay-view";//权限验证
        JsonMapper mapper = new JsonMapper();
        if (!companyId.equals(cid)){
            return mapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return mapper.toJson(result1);
        }
        RequestResult res = this.dealerAccountLogFacade.findPaymentById(resourceId,logId,type,uid,companyId);
        return mapper.toJson(res);
    }


    /**
     * 转入转出记录
     * @param companyId
     * @param type
     * @param status
     * @param request
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/factory/payments")
    public RequestResult findByCompanyId(@PathVariable String companyId,
                                         @RequestParam(required = false) Integer type,
                                         @RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) Integer pageSize,
                                         @RequestParam(required = false) Integer pageNum,
                                         HttpServletRequest request){
        if(null == pageSize){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if(null == pageNum){
            pageNum = 1;
        }
        String uid = request.getHeader("X-UID");//用户Id

        String cid = request.getHeader("X-CID");//公司id
        String xp = "bdealer-prepay-view";//权限验证
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }

        RequestResult result = this.validUserPermission(request,xp);
        if (null!=result){
            return result;
        }
        PaginatedFilter filter = PaginatedFilter.newOne();
        Pagination pagination = Pagination.newOne();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        filter.setPagination(pagination);
        MapContext params = MapContext.newOne();
        if (type==null){
            params.put("type","2 or type = 3");
        }else {
            params.put("type",type);
        }
        params.put("companyId",companyId);
        params.put("status",status);
        filter.setFilters(params);
        return this.dealerAccountLogFacade.findByCompanyId(filter,uid,companyId);
    }



}

