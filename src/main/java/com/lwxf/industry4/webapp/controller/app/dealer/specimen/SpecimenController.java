package com.lwxf.industry4.webapp.controller.app.dealer.specimen;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderFilesCategory;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderFilesType;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.enums.order.OrderType;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderDemand;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.design.DesignDemandFacade;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderDesignFacade;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderFacade;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderTrackingFacade;
import com.lwxf.industry4.webapp.facade.app.dealer.order.PaymentFacade;
import com.lwxf.industry4.webapp.facade.app.dealer.specimen.SpecimenFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/15 18:05
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class SpecimenController extends BaseDealerController {

    @Resource(name = "orderFacade")
    private OrderFacade orderFacade;
    @Resource(name = "specimenFacade")
    private SpecimenFacade specimenFacade;
    @Resource(name = "orderTrackingFacade")
    private OrderTrackingFacade orderTrackingFacade;
    @Resource(name = "designDemandFacade")
    private DesignDemandFacade designDemandFacade;
    @Resource(name = "orderDesignFacade")
    private OrderDesignFacade orderDesignFacade;
    @Resource(name = "paymentFacade")
    private PaymentFacade paymentFacade;
    /**
     * 查询上样订单列表
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/specimens")
    public String findSpecimen(@RequestParam(required = false) Integer pageNum,
                                      @RequestParam(required = false) Integer pageSize,
                                      @RequestParam(required = false) Integer status,
                                      @PathVariable String companyId,
                                      HttpServletRequest request){

        if(null == pageSize){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if(null == pageNum){
            pageNum = 1;
        }
        Pagination pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        MapContext filters = MapContext.newOne();
        filters.put("companyId", companyId);
        filters.put("type", OrderType.EXHIBITIONORDER.getValue());
        if (status!=null) {
            if (status == 0) {
                filters.put("status", 0);
            }
            if (status == 1) {
                filters.put("status", "1 or o.status = 2 or o.status = 3 or o.status = 4 or o.status = 5 or o.status = 6");
            }
            if (status == 2) {
                filters.put("status", "7 or o.status = 8 or o.status = 9 or o.status = 10 or o.status = 11 or o.status = 12 or o.status = 13");
            }
            if (status == 3) {
                filters.put("status", "14 or o.status = 15");
            }
            if (status == 4) {
                filters.put("status", 16);
            }
        }
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.specimenFacade.findSpecimenOrderList(pagination, filters);
        return mapper.toJson(result);
    }

    /**
     * 查询订单的状态
     * @return
     */
    @GetMapping(value = "/orderStatus")
    public RequestResult findOrderStatus(){
        List<Map> values = OrderStatus.getStatus();
        return ResultFactory.generateRequestResult(values);
    }



    /**
     * 新建上样订单
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/createspecimen")
    public RequestResult createSpecimen(@PathVariable String companyId){
        MapContext params = MapContext.newOne();
        params.put("orderNum", AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.ORDER_NO));
        params.put("created", DateUtil.getSystemDate());
        params.put("deliveryDate", "");
        return ResultFactory.generateRequestResult(params );
    }





    /**
     * 添加上样订单
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/specimens")
    public RequestResult addSpecimen(@RequestBody MapContext params,
                                     @PathVariable String companyId,
                                     HttpServletRequest request){
        return this.specimenFacade.addSpecimen(params,companyId,request);
    }


    /**
     * 查询上样 需求
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/specimenDemands")
    public String findSpecimenDemand(@PathVariable String companyId,
                                     @PathVariable String orderId){
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.designDemandFacade.findByOrderId(orderId);
        return mapper.toJson(result);
    }


    /**
     * 获取上样需求的详情和需求图片
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/specimenDemands/{demandId}")
    public String findDemandImageByDemandId(@PathVariable String orderId,
                                            @PathVariable String demandId,
                                            HttpServletRequest request,
                                            @PathVariable String companyId){

        String cid = request.getHeader("X-CID");//公司id
        JsonMapper mapper = new JsonMapper();
        if (!companyId.equals(cid)){
            return  mapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
        String xp = "bdesignneedmng-designneed-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return mapper.toJson(result1);
        }
        RequestResult demandImageByDemand = this.designDemandFacade.findDemandImageByDemandId(orderId, demandId);

        return mapper.toJson(demandImageByDemand);
    }



    /**
     * 添加订单需求
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/specimenDemands")
    public RequestResult addSpecimenDemand(@PathVariable String companyId,
                                           @PathVariable String orderId,
                                           @RequestBody MapContext params,
                                           HttpServletRequest request){
        CustomOrderDemand customOrderDemand = new CustomOrderDemand();
        customOrderDemand.setCustomOrderId(orderId);
        customOrderDemand.setName((String)params.get("name"));
        customOrderDemand.setContent((String)params.get("content"));
        if (LwxfStringUtils.isBlank((String)params.get("designScheme"))){
            customOrderDemand.setDesignScheme(null);
        }else {
            customOrderDemand.setDesignScheme((String)params.get("designScheme"));
        }
        if (LwxfStringUtils.isBlank((String)params.get("productSeries"))){
            customOrderDemand.setProductSeries(null);
        }else {
            customOrderDemand.setProductSeries((String)params.get("productSeries"));
        }
        if (LwxfStringUtils.isBlank((String)params.get("productModel"))){
            customOrderDemand.setProductModel(null);
        }else {
            customOrderDemand.setProductModel((String)params.get("productModel"));
        }
        RequestResult result = this.designDemandFacade.addDesignDemand(customOrderDemand, request);
        return result;
    }


    /**
     * 添加上样图片
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/files")
    public RequestResult addSpecimenImages(@RequestBody List<MultipartFile> multipartFiles,
                                           @PathVariable String orderId,
                                           @PathVariable String companyId,
                                           @RequestParam String belongId,
                                           @RequestParam Integer category,
                                           @RequestParam Integer type,
                                           HttpServletRequest request) {
        String userId = request.getHeader("X-UID");
        String cid = request.getHeader("X-CID");
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        Map<String, Object> errorInfo = new HashMap<>();

        Boolean containsType = CustomOrderFilesType.contains(type);
        if (!containsType) {
            return ResultFactory.generateErrorResult(AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"), AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
        }
        Boolean containsCategory = CustomOrderFilesCategory.contains(category);
        if (!containsCategory) {
            return ResultFactory.generateErrorResult(AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"), AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
        }
        if (multipartFiles == null||multipartFiles.size()==0) {
            errorInfo.put("multipartFiles", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR, errorInfo);
        }

        for (MultipartFile multipartFile:multipartFiles) {
            if (multipartFile==null){
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            }
            if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
            }
            if (multipartFile.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
                return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
            }
        }
        if (errorInfo.size() > 0) {
            return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR, errorInfo);
        }
        return this.designDemandFacade.addHomeStyleImage(multipartFiles, orderId, belongId, userId, category, type);
    }


    /**
     * 查询上样的设计列表
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/designs")
    public String findSpecimenDesignList(@PathVariable String companyId,
                                                @PathVariable String orderId){
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.orderDesignFacade.findByOrderId(orderId);
        return mapper.toJson(result);
    }

    /**
     * 查看上样设计的详情
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/designs/{designId}")
    public RequestResult findSpecimenDesign(@PathVariable String companyId,
                                            @PathVariable String orderId,
                                            @PathVariable String designId){

        RequestResult result = this.orderDesignFacade.findBydesignId(orderId, designId);
        return result;
    }

    /**
     * 添加上样设计的意见
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/designs/{designId}")
    public RequestResult addSpecimenDesignOpinion(@PathVariable String orderId,
                                                  @PathVariable String designId,
                                                  @RequestBody MapContext param,
                                                  HttpServletRequest request,
                                                  @PathVariable String companyId){
        param.put("id",designId);
        RequestResult result = this.orderDesignFacade.addAmendmentsById(orderId, param);
        return result;
    }

    /**
     * 修改上样设计的状态（确认设计）
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/designs/{designId}/{status}")
    public RequestResult updateSpecimenDesignStatus(@PathVariable String status,
                                                    @PathVariable String orderId,
                                                    @PathVariable String designId,
                                                    HttpServletRequest request,
                                                    @PathVariable String companyId){
        MapContext mapContext = MapContext.newOne();
        mapContext.put("id",designId);
        mapContext.put("status",status);
        RequestResult result = this.orderDesignFacade.putStatusById(orderId, mapContext);
        return result;
    }


    /**
     * 查看上样的生产列表
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/Process")
    public String findSpecimenProcessList(@PathVariable String companyId,
                                          @PathVariable String orderId,
                                          HttpServletRequest request){

        JsonMapper jsonMapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.orderTrackingFacade.findProcessByOrderId(orderId);
        return jsonMapper.toJson(result);
    }


    /**
     * 查看上样生产视频
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/Process/{processId}")
    public String findSpecimenProcessVideos(@PathVariable String companyId,
                                            @PathVariable String orderId,
                                            @PathVariable String processId,
                                            HttpServletRequest request){
        JsonMapper jsonMapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.orderTrackingFacade.findProcessVideosByOrderId(orderId, processId);
        return jsonMapper.toJson(result);
    }


    /**
     * 查看上样配送列表
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/dispatchs")
    public RequestResult findSpecimenDispatchsList(@PathVariable String companyId,
                                                   @PathVariable String orderId,
                                                   HttpServletRequest request){

        RequestResult result = this.orderTrackingFacade.findDispatchsByOrderId(orderId);
        return result;
    }

    /**
     * 查看上样配送详情
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/dispatchs/{dispatchId}")
    public RequestResult findSpecimenDispatchs(@PathVariable String companyId,
                                               @PathVariable String orderId,
                                               @PathVariable String dispatchId,
                                               HttpServletRequest request){
        RequestResult result = this.orderTrackingFacade.findByDispatchId(dispatchId);
        return result;
    }



    /**
     * 修改上样配送的状态（确认收货）
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/dispatchs/{dispatchId}")
    public RequestResult putSpecimenDispatchStatus(@PathVariable String companyId,
                                                   @PathVariable String orderId,
                                                   @PathVariable String dispatchId,
                                                   HttpServletRequest request){

        String uid = request.getHeader("X-UID");
        String cid = request.getHeader("X-CID");
        RequestResult result = this.orderTrackingFacade.putStatusByDispatchId(dispatchId, uid, cid, orderId);
        return result;
    }




    /**
     * 添加订单支付
     * @param orderId
     * @param
     * @param request
     * @param companyId
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/payments")
    public RequestResult addPayment(@PathVariable String orderId,
                                    @RequestBody MapContext params,
                                    HttpServletRequest request,
                                    @PathVariable String companyId){
        String userId = request.getHeader("X-UID");
        String cid = request.getHeader("X-CID");
//        if (!companyId.equals(cid)){
//            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
//        }
//        String xp = "borderpaymentmng-paymentinfo-edit";
//        RequestResult result1 = this.validUserPermission(request,xp);
//        if (null!=result1){
//            return result1;
//        }
        Date payTime = DateUtil.stringToDate((String) params.get("payTime"),"yyyy-MM-dd HH:mm");
        String way = (String) params.get("way");
        String funds = (String) params.get("funds");
        String holder = (String) params.get("holder");
        String amount = (String) params.get("amount");
        String type = (String) params.get("type");
        String name = (String) params.get("name");
        Payment payment = new Payment();
        payment.setCustomOrderId(orderId);
        payment.setPayTime(payTime);
        payment.setWay(Integer.valueOf(way));
        payment.setFunds(Integer.valueOf(funds));
        payment.setHolder(holder);
        payment.setAmount(new BigDecimal(amount));
        payment.setType(Integer.valueOf(type));
        payment.setName(name);
        return this.specimenFacade.addPayment(payment,userId,companyId);
    }


    /**
     * 查询订单支付列表
     * @param orderId
     * @param request
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/payments")
    public RequestResult findByOrderId(@PathVariable String orderId,
                                       HttpServletRequest request,
                                       @PathVariable String companyId){
//        String cid = request.getHeader("X-CID");//公司id
//        if (!companyId.equals(cid)){
//            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
//        }
//        String xp = "borderpaymentmng-paymentinfo-view";
//        RequestResult result1 = this.validUserPermission(request,xp);
//        if (null!=result1){
//            return result1;
//        }
        return this.paymentFacade.findByOrderId(orderId);
    }


    /**
     * 查询订单支付的详情和图片
     * @param orderId
     * @param paymentId
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/specimenOrders/{orderId}/payments/{paymentId}")
    public  String findByPaymentId(@PathVariable String orderId,
                                   @PathVariable String paymentId,
                                   @PathVariable String companyId,
                                   HttpServletRequest request){

        String cid = request.getHeader("X-CID");//公司id

        JsonMapper mapper = new JsonMapper();
//        if (!companyId.equals(cid)){
//            return  mapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
//        }
//        String xp = "borderpaymentmng-paymentinfo-view";
//        RequestResult result1 = this.validUserPermission(request,xp);
//        if (null!=result1){
//            return mapper.toJson(result1);
//        }
        RequestResult result = this.paymentFacade.findByPaymentId(orderId, paymentId);
        return mapper.toJson(result);
    }


    /**
     * 上传订单支付文件
     * @param multipartFiles
     * @return
     */
    @PostMapping("/companies/{companyId}/specimenOrders/{orderId}/payments/{paymentId}/files")
    public RequestResult addOrderPaymentFiles(@RequestBody List<MultipartFile> multipartFiles,
                                              @PathVariable String orderId,
                                              @PathVariable String paymentId,
                                              @PathVariable String companyId,
                                              HttpServletRequest request){

        Map<String, Object> errorInfo = new HashMap<>();

        if (multipartFiles == null||multipartFiles.size()==0) {
            errorInfo.put("multipartFiles", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR, errorInfo);
        }

        for (MultipartFile multipartFile:multipartFiles) {
            if (multipartFile==null){
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            }
            if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
            }
            if (multipartFile.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
                return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
            }
        }
        if (errorInfo.size() > 0) {
            return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR, errorInfo);
        }
        String userId = request.getHeader("X-UID");
        String cid = request.getHeader("X-CID");
//        if (!companyId.equals(cid)){
//            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
//        }

        return this.paymentFacade.addPaymentFiles(multipartFiles,paymentId,userId);
    }





}
