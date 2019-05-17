package com.lwxf.industry4.webapp.controller.app.factory.factoryorder;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.domain.entity.customorder.ProduceFlow;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.design.DesignDemandFacade;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderFacade;
import com.lwxf.industry4.webapp.facade.app.factory.factoryorder.FactoryOrderProcessFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/1 13:12
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/f",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryOrderProcessController {
    @Resource(name = "factoryOrderProcessFacade")
    private FactoryOrderProcessFacade factoryOrderProcessFacade;
    @Resource(name = "designDemandFacade")
    private DesignDemandFacade designDemandFacade;
    @Resource(name = "orderFacade")
    private OrderFacade orderFacade;
    /**
     * 生产跟进
     * @return
     */
    @GetMapping(value = "/orders/process/follow")
    public String findProcessFollow(@RequestParam(required = false) Integer pageSize,
                                    @RequestParam(required = false) Integer pageNum,
                                    @RequestParam(required = false) String companyName,
                                    @RequestParam(required = false) String customerName,
                                    @RequestParam(required = false) String beginTime,
                                    @RequestParam(required = false) String endTime,
                                    @RequestParam(required = false) String no,
                                    @RequestParam(required = false) String shippStatus){
        if (pageSize==null){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (pageNum==null){
            pageNum = 1;
        }

        Pagination pagination = Pagination.newOne();
        pagination.setPageNum(pageNum);
        pagination.setPageSize(pageSize);
        MapContext params = MapContext.newOne();

        if (LwxfStringUtils.isNotBlank(companyName)){
            params.put("companyName", companyName);
        }
        if (LwxfStringUtils.isNotBlank(customerName)){
            params.put("customerName", customerName);
        }
        if (LwxfStringUtils.isNotBlank(beginTime)){
            params.put("beginTime", beginTime);
        }
        if (LwxfStringUtils.isNotBlank(endTime)){
            params.put("endTime", endTime);
        }
        if (LwxfStringUtils.isNotBlank(no)){
            params.put("no",no );
        }
        if (LwxfStringUtils.isNotBlank(shippStatus)&&shippStatus.equals("未发货")){
            params.put("status", "14 or o.status < 14");
        }
        if (LwxfStringUtils.isNotBlank(shippStatus)&&shippStatus.equals("已发货")){
            params.put("status", OrderStatus.DISPATCHING.getValue());
        }
        params.put("state", "0,1");
        RequestResult result = this.factoryOrderProcessFacade.findProcessFollow(params,pagination);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }

    /**
     * 生产执行
     * @return
     */
    @GetMapping(value = "/orders/process/execute")
    public String findProcessExecute(@RequestParam(required = false) Integer pageSize,
                                     @RequestParam(required = false) Integer pageNum,
                                     @RequestParam(required = false) String companyName,
                                     @RequestParam(required = false) String customerName,
                                     @RequestParam(required = false) String beginTime,
                                     @RequestParam(required = false) String endTime,
                                     @RequestParam(required = false) String no,
                                     @RequestParam(required = false) String shippStatus){

        if (pageSize==null){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (pageNum==null){
            pageNum = 1;
        }

        Pagination pagination = Pagination.newOne();
        pagination.setPageNum(pageNum);
        pagination.setPageSize(pageSize);
        MapContext params = MapContext.newOne();

        if (LwxfStringUtils.isNotBlank(companyName)){
            params.put("companyName", companyName);
        }
        if (LwxfStringUtils.isNotBlank(customerName)){
            params.put("customerName", customerName);
        }
        if (LwxfStringUtils.isNotBlank(beginTime)){
            params.put("beginTime", beginTime);
        }
        if (LwxfStringUtils.isNotBlank(endTime)){
            params.put("endTime", endTime);
        }
        if (LwxfStringUtils.isNotBlank(no)){
            params.put("no",no );
        }
        if (LwxfStringUtils.isNotBlank(shippStatus)&&shippStatus.equals("未发货")){
            params.put("status", "14 or o.status < 14");
        }
        if (LwxfStringUtils.isNotBlank(shippStatus)&&shippStatus.equals("已发货")){
            params.put("status", OrderStatus.DISPATCHING.getValue());
        }
        params.put("state", 1);
        params.put("type", "0,1");
        params.put("way", "0");
        RequestResult result = this.factoryOrderProcessFacade.findProcessFollow(params,pagination);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }


    /**
     * 生产跟进列表
     * @return
     */
    @PostMapping(value = "/orders/process/follow/list")
    public String findProcessFollowList(@RequestBody(required = false) MapContext map){
        String created =(String) map.get("created");
        String deliveryDate =(String) map.get("deliveryDate");
        Integer pageNum =(Integer) map.get("pageNum");
        Integer pageSize =(Integer) map.get("pageSize");
        if (pageSize==null){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (pageNum==null){
            pageNum = 1;
        }

        Pagination pagination = Pagination.newOne();
        pagination.setPageNum(pageNum);
        pagination.setPageSize(pageSize);
        map.remove("pageSize");
        map.remove("pageNum");
        map.remove("deliveryDate");
        map.remove("created");
        RequestResult result = this.factoryOrderProcessFacade.findProcessFollowList(deliveryDate,created,map,pagination);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }




    /**
     * 生产跟进详情
     * @return
     */
    @GetMapping(value = "/orders/{orderId}/process/follow/info")
    public String findProcessFollowInfo(@PathVariable String orderId){
        RequestResult processFollowInfo = this.factoryOrderProcessFacade.findProcessFollowInfo(orderId);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(processFollowInfo);
    }


    /**
     * 下车间（修改订单的状态从10---->11）
     * @return
     */
    @PutMapping(value = "/orders/{orderId}/goworkshop")
    public RequestResult goWorkshop(@RequestBody MapContext mapContext,
                                    @PathVariable String orderId){
        mapContext.put("id", orderId);
        mapContext.put("status",OrderStatus.IN_PRODUCTION.getValue());
        RequestResult result = this.factoryOrderProcessFacade.goWorkshop(mapContext);
        return result;
    }




    /**
     * 生产执行详情
     * @return
     */
    @GetMapping(value = "/orders/{orderId}/process/execute/info")
    public String findProcessExecuteInfo(@PathVariable String orderId){
        RequestResult processFollowInfo = this.factoryOrderProcessFacade.findProcessExecuteInfo(orderId);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(processFollowInfo);
    }

    /**
     * 生产执行确认完成
     * 添加生产流程。
     * @return
     */
    @PostMapping(value = "/orders/{orderId}/process/execute")
    public RequestResult addProcessExecute(@RequestBody MapContext params,
                                           @PathVariable String orderId){

        Date endTime = params.getTypedValue("endTime", Date.class);//完成时间
        String notes = params.getTypedValue("notes", String.class);//备注
        String operationUserId = params.getTypedValue("operationUserId", String.class);//操作人
        String produceOrderId = params.getTypedValue("produceOrderId", String.class);//拆单的id
        Date operationTime = DateUtil.getSystemDate();//操作时间
        Integer node = params.getTypedValue("node", Integer.class);//阶段（0 备料 1 下料 2 封边 3 打孔 4 清洗 5 试装 6 包装）

        ProduceFlow produceFlow = new ProduceFlow();
        produceFlow.setNotes(notes);
        produceFlow.setEndTime(endTime);
        produceFlow.setOperator(operationUserId);
        produceFlow.setNode(node);
        produceFlow.setOperationTime(operationTime);
        produceFlow.setProduceOrderId(produceOrderId);

        RequestResult result = this.factoryOrderProcessFacade.addProcessExecute(produceFlow,orderId);
        return result;
    }


    @GetMapping(value = "/orders/{orderId}/packno")
    private RequestResult findOrderPackagesNo(@PathVariable String orderId){
        return this.orderFacade.findOrderPackagesNo(orderId);
    }



    /**
     * 生产执行
     * 确认完成包装
     * @return
     */
    @PostMapping(value = "/orders/{orderId}/process/execute/pack")
    public RequestResult addProcessExecutePack(@RequestBody MapContext params,
                                               @PathVariable String orderId,
                                               HttpServletRequest request){

        String uid = request.getHeader("X-UID");

        Date endTime = params.getTypedValue("endTime", Date.class);//完成时间
        String notes = params.getTypedValue("notes", String.class);//备注
        String operationUserId = params.getTypedValue("operationUserId", String.class);//操作人
        String produceOrderId = params.getTypedValue("produceOrderId", String.class);//拆单的id
        Date operationTime = DateUtil.getSystemDate();//操作时间
        Integer node = params.getTypedValue("node", Integer.class);//阶段（0 备料 1 下料 2 封边 3 打孔 4 清洗 5 试装 6 包装）
        String barcodes =  params.getTypedValue("barcodes", String.class);

        String[] barcodesList = barcodes.split("/");



        ProduceFlow produceFlow = new ProduceFlow();
        produceFlow.setNotes(notes);
        produceFlow.setEndTime(endTime);
        produceFlow.setOperator(operationUserId);
        produceFlow.setNode(node);
        produceFlow.setOperationTime(operationTime);
        produceFlow.setProduceOrderId(produceOrderId);
        RequestResult result = this.factoryOrderProcessFacade.addProcessExecutePack(produceFlow,orderId,uid,barcodesList);
        return result;
    }




    /**
     * 上传附件
     * @param multipartFiles 图片
     * @param orderId  订单的id
     * @param belongId 所属资源的id 生产流程id
     * @param request
     * @return
     */
    @PostMapping(value = "/orders/{orderId}/process/{belongId}/execute/accessory")
    public RequestResult addAccessory(
        @RequestBody List<MultipartFile> multipartFiles,
        @PathVariable String orderId,
        @PathVariable String belongId,
        HttpServletRequest request) {
        String userId = request.getHeader("X-UID");
        Map<String, Object> errorInfo = new HashMap<>();
        if (multipartFiles == null || multipartFiles.size() == 0) {
            errorInfo.put("multipartFiles", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
        }

        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile == null) {
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            }
            if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
            }
            if (multipartFile.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
                return ResultFactory.generateErrorResult(ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
            }
        }
        if (errorInfo.size() > 0) {
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
        }
        return this.designDemandFacade.addHomeStyleImage(multipartFiles, orderId, belongId, userId, 0, 5);
    }
}
