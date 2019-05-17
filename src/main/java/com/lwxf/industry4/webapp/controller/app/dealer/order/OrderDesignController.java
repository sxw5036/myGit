package com.lwxf.industry4.webapp.controller.app.dealer.order;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderDesignFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/15 14:27
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class OrderDesignController extends BaseDealerController {

    @Resource(name = "orderDesignFacade")
    private OrderDesignFacade orderDesignFacade;

    /**
     * 根据订单查询设计列表
     * @param orderId
     * @param request
     * @return
     */
    @GetMapping("/companies/{companyId}/customorders/{orderId}/customdesigns")
    public String findByOrderId(@PathVariable String orderId,
                                       HttpServletRequest request,
                                @PathVariable String companyId){

        String cid = request.getHeader("X-CID");//公司id

        JsonMapper mapper = new JsonMapper();
        if (!companyId.equals(cid)){
            return  mapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
        String xp = "bdesignmng-designment-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return mapper.toJson(result1);
        }
        RequestResult result = this.orderDesignFacade.findByOrderId(orderId);
        return mapper.toJson(result);
    }

    /**
     *  通过订单设计Id查询设计的详情和图片
     * @param orderId
     * @param designId
     * @param request
     * @return
     */
    @GetMapping("/companies/{companyId}/customorders/{orderId}/customdesigns/{designId}")
    public RequestResult findBydesignId(@PathVariable String orderId,
                                        @PathVariable String designId,
                                        HttpServletRequest request,
                                        @PathVariable String companyId){

        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bdesignmng-designment-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        return this.orderDesignFacade.findBydesignId(orderId, designId);
    }


    /**
     * 订单设计修改意见
     * @param orderId
     * @param designId
     * @param param
     * @param request
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/customorders/{orderId}/customdesigns/{designId}")
    public RequestResult addAmendmentsById(@PathVariable String orderId,
                                           @PathVariable String designId,
                                           @RequestBody MapContext param,
                                           HttpServletRequest request,
                                           @PathVariable String companyId){

        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bdesignmng-designment-edit";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        param.put("id",designId);
        return this.orderDesignFacade.addAmendmentsById(orderId,param);
    }


    /**
     * 修改订单设计状态
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/customorders/{orderId}/customdesigns/{designId}/{status}")
    public RequestResult putStatusById(@PathVariable String status,
                                        @PathVariable String orderId,
                                       @PathVariable String designId,
                                       HttpServletRequest request,
                                       @PathVariable String companyId){

        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bdesignmng-designment-update_status";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        MapContext mapContext = MapContext.newOne();
        mapContext.put("id",designId);
        mapContext.put("status",status);
        return this.orderDesignFacade.putStatusById(orderId,mapContext);
    }


}

