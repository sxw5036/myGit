package com.lwxf.industry4.webapp.controller.app.dealer.order;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.enums.order.OrderType;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderFacade;
import com.lwxf.industry4.webapp.facade.user.UserFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/6 16:30
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class OrderController extends BaseDealerController {
    @Resource(name = "orderFacade")
    private OrderFacade orderFacade;
    @Resource(name = "userFacade")
    private UserFacade userFacade;


    /**
     * 订单的检索和列表
     *
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/customorders")
    public RequestResult selectByCondition(@RequestParam(required = false) Integer pageNum,
                                           @RequestParam(required = false) Integer pageSize,
                                           @RequestParam(required = false) String salesman,
                                           @RequestParam(required = false) String condition,
                                           @PathVariable String companyId,
                                           HttpServletRequest request
    ) {
        if(null == pageSize){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
          if(null == pageNum){
            pageNum = 1;
        }
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }

        String xp = "bordermng-orderment-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        MapContext params = MapContext.newOne();
        if (LwxfStringUtils.isNotBlank(salesman)) {
            params.put("salesman", salesman);
        }

        if (LwxfStringUtils.isNotBlank(condition)) {
            params.put("condition", condition);
        }
        params.put("companyId", companyId);
        params.put("type",OrderType.NORMALORDER.getValue());
        return this.orderFacade.selectByCondition(pageNum, pageSize, params,uid,cid);
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/customorders/{id}")
    public String selectByOrderId(@PathVariable String id,HttpServletRequest request) {
        JsonMapper mapper = new JsonMapper();
        String xp = "bordermng-orderment-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return mapper.toJson(result1);
        }
        String s = mapper.toJson(this.orderFacade.selectByOrderId(id));
        return s;
    }



    /**
     * 添加订单
     *
     * @param
     * @param request
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/customorders")
    public RequestResult addOrder(@RequestBody MapContext params,
                                   @PathVariable String companyId,
                                   HttpServletRequest request) {
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id

        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bordermng-orderment-edit";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        CustomOrder customOrder = new CustomOrder();
        customOrder.setCityAreaId((String)params.get("cityAreaId"));
        customOrder.setAddress((String)params.get("address"));
        customOrder.setCustomerId((String)params.get("customerId"));
        customOrder.setSalesman((String)params.get("salesman"));
        customOrder.setType(Integer.valueOf((String) params.get("type")));
        customOrder.setCustomerTel((String)params.get("customerTel"));
        return orderFacade.addOrder(customOrder,uid,cid);
    }

    /**
     * 修改订单信息
     *
     * @param updateMap
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/customorders/{id}")
    public RequestResult updateOrder(@RequestBody MapContext updateMap,
                                     HttpServletRequest request,
                                     @PathVariable String id,
                                     @PathVariable String companyId) {
        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bordermng-orderment-edit";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        updateMap.put("id", id);
        return this.orderFacade.updateOrder(updateMap, request);
    }

    /**
     * 修改订单的状态
     * 提交需求，将订单的状态改为1
     * @param request
     * @param id
     * @param companyId
     * @param status
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/customorders/{id}/{status}")
    public RequestResult putOrderStatus(HttpServletRequest request,
                                 @PathVariable String id,
                                 @PathVariable String companyId,
                                 @PathVariable Integer status){

        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bordermng-orderment-update_status";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        boolean orderStatus = OrderStatus.contains(status);
        if (!orderStatus){
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ILLEGAL_ARGUMENT, AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
        }

        MapContext params = MapContext.newOne();
        params.put("id",id);
        params.put("status",status);
        return  this.orderFacade.commitOrderDemand(params);
    }





    /**
     * 删除订单
     *
     * @param id
     * @param request
     * @return
     */
    @DeleteMapping(value = "/companies/{companyId}/customorders/{id}")
    public RequestResult deleteByOrderId(@PathVariable String id, HttpServletRequest request,
                                         @PathVariable String companyId) {

        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bordermng-orderment-delete";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        return this.orderFacade.deleteByOrderId(id, request);
    }

    /**
     * 订单统计
     * @param companyId
     * @param selectTime
     * @param request
     * @return
     */
    @GetMapping("/companies/{companyId}/customorders/count")
    public String customerOrderCount(@PathVariable String companyId,
                                     @RequestParam(required = false) String selectTime,
                                     @RequestParam(required = false) String saleMan,
                                     HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        String xp = "bordermng-orderment-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return jsonMapper.toJson(result1);
        }
        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
        MapContext mapContext=MapContext.newOne();
        mapContext.put("companyId",cid);
        if(LwxfStringUtils.isNotBlank(selectTime)){
            mapContext.put("selectTime",selectTime);
        }
        if(LwxfStringUtils.isNotBlank(saleMan)){
            mapContext.put("saleMan",saleMan);
        }
        RequestResult result=this.orderFacade.customerOrderCount(companyId,request,mapContext);
        return jsonMapper.toJson(result);
    }
}

