package com.lwxf.industry4.webapp.controller.app.dealer.order;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderTrackingFacade;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/13 13:15
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b/",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class OrderTrackingController extends BaseDealerController {
    @Resource(name = "orderTrackingFacade")
    private OrderTrackingFacade orderTrackingFacade;


    /**
     * 生产列表
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/customorders/{orderId}/process")
    public String findProcessByOrderId(@PathVariable String orderId,
                                              @PathVariable String companyId,
                                              HttpServletRequest request){
        String cid = request.getHeader("X-CID");//公司id
        JsonMapper jsonMapper = new JsonMapper();

        if (!companyId.equals(cid)){
            return  jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
//        String xp = "bdesignmng-designment-view";
//        RequestResult result1 = this.validUserPermission(request);
//        if (null!=result1){
//            return jsonMapper.toJson(result1);
//        }
        return jsonMapper.toJson(this.orderTrackingFacade.findProcessByOrderId(orderId));
    }
    @GetMapping(value = "/companies/{companyId}/customorders/{orderId}/process/{processId}")
    public String findProcessVideosByOrderId(@PathVariable String orderId,
                                             @PathVariable String companyId,
                                             HttpServletRequest request,
                                             @PathVariable String processId){


        String cid = request.getHeader("X-CID");//公司id
        JsonMapper jsonMapper = new JsonMapper();

        if (!companyId.equals(cid)){
            return  jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
 //       String xp = "bdesignmng-designment-view";
//        RequestResult result1 = this.validUserPermission(request);
//        if (null!=result1){
//            return jsonMapper.toJson(result1);
//        }
        return jsonMapper.toJson(this.orderTrackingFacade.findProcessVideosByOrderId(orderId,processId));
    }



    /**
     * 查询配送列表
     * @param orderId
     * @param request
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/customorders/{orderId}/dispatchs")
    public RequestResult findDispatchsByOrderId(@PathVariable String orderId,
                                                @PathVariable String companyId,
                                                HttpServletRequest request){

        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bdispatchingmng-dispatchstate-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        return  this.orderTrackingFacade.findDispatchsByOrderId(orderId);
    }

    /**
     * 查询配送详情
     * @param companyId
     * @param orderId
     * @param dispatchId
     * @param request
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/customorders/{orderId}/dispatchs/{dispatchId}")
    public RequestResult findByDispatchId(@PathVariable String companyId,
                                          @PathVariable String orderId,
                                          @PathVariable String dispatchId,
                                          HttpServletRequest request){
        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bdispatchingmng-dispatchstate-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        return this.orderTrackingFacade.findByDispatchId(dispatchId);
    }

    /**
     * 修改状态（确认收货）
     * 确认收货并创建派工单
     * @param companyId
     * @param orderId
     * @param dispatchId
     * @param request
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/customorders/{orderId}/dispatchs/{dispatchId}")
    public RequestResult putStatusByDispatchId(@PathVariable String companyId,
                                               @PathVariable String orderId,
                                               @PathVariable String dispatchId,
                                               HttpServletRequest request){
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id

        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bdispatchingmng-dispatchstate-update_status";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        return this.orderTrackingFacade.putStatusByDispatchId(dispatchId,uid,cid,orderId);
    }


}

