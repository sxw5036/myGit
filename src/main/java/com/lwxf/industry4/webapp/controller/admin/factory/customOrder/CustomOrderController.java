package com.lwxf.industry4.webapp.controller.admin.factory.customOrder;

import io.swagger.annotations.*;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.miemiedev.mybatis.paginator.domain.Order;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.company.FactoryEmployeeRole;
import com.lwxf.industry4.webapp.common.enums.company.ProduceOrderPay;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderCoordination;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderIsDesign;
import com.lwxf.industry4.webapp.common.enums.customorder.ProduceOrderState;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentStatus;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentWay;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.enums.order.ProduceOrderWay;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.common.utils.UserUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.customorder.*;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.*;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/25/025 16:21
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Api(value = "CustomOrderController",tags = {"订单管理接口"})
@RestController
@RequestMapping(value = "/api/f/customorders", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class CustomOrderController {
    @Resource(name = "orderFacade")
    private OrderFacade orderFacade;

    /**
     * 查询可生成成品库单的订单
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/finishedorders")
    private RequestResult findFinishedOrderList(@RequestParam(required = false) Integer pageNum,
                                                @RequestParam(required = false) Integer pageSize) {
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        return this.orderFacade.findFinishedOrderList(Arrays.asList(OrderStatus.TO_WAREHOUSE.getValue()), pageNum, pageSize, true);
    }

    /**
     * 查询可生成成品库单的订单
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/finishedorders/all")
    private RequestResult findAllFinishedOrderList(@RequestParam(required = false) Integer pageNum,
                                                   @RequestParam(required = false) Integer pageSize) {
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        return this.orderFacade.findFinishedOrderList(Arrays.asList(OrderStatus.TO_OUT_STOCK.getValue(), OrderStatus.TO_DISPATCH.getValue(), OrderStatus.DISPATCHING.getValue()), pageNum, pageSize, false);
    }

//    /**
//     * 修改订单状态
//     *
//     * @param id
//     * @param status
//     * @param mapContext
//     * @return
//     */
//    @PutMapping("/{id}/{status}")
//    private RequestResult updateOrderStatus(@PathVariable String id,
//                                            @PathVariable Integer status,
//                                            @RequestBody(required = false) MapContext mapContext) {
//        return this.orderFacade.updateOrderStatus(id, status, mapContext);
//    }

    /**
     * 设计管理模块修改订单状态
     * @param id
     * @return
     */
    @PutMapping("/{id}/designerssubmit")
    private RequestResult designerUpdaterOrderStatus(@PathVariable String id,
                                                     @RequestBody MapContext mapContext){
        Integer status = mapContext.getTypedValue(WebConstant.KEY_ENTITY_STATUS,Integer.class);
        //判断是否修改订单状态为 5(设计中) 6(设计待确认) 7(出厂价待确认)
        if(!status.equals(OrderStatus.DESIGNING.getValue())&&!status.equals(OrderStatus.TO_SUBMIT.getValue())&&!status.equals(OrderStatus.FACTORY_CONFIRMED_FPROCE.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
        }
        return this.orderFacade.updateOrderStatus(id,status,mapContext);
    }
    /**
     * 设计管理模块下修改订单审价状态
     * @param id
     * @return
     */
    @PutMapping("/{id}/designevaluation")
    private RequestResult designEvaluationUpdaterOrderStatus(@PathVariable String id,
                                                             @RequestBody MapContext mapContext){
        Integer status = mapContext.getTypedValue(WebConstant.KEY_ENTITY_STATUS,Integer.class);
        //判断是否修改订单状态为 |2(设计费待确认)(取消)| 3(设计费待审核)
        if(!status.equals(OrderStatus.TO_AUDIT_DESIGN.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
        }
        return this.orderFacade.updateOrderStatus(id,status,null);
    }
    /**
     * 订单管理模块修改订单审价状态
     * @param id
     * @return
     */
    @PutMapping("/{id}/orderevaluation")
    private RequestResult orderPriceEvaluationUpdaterOrderStatus(@PathVariable String id,
                                                     @RequestBody MapContext mapContext){
        Integer status = mapContext.getTypedValue(WebConstant.KEY_ENTITY_STATUS,Integer.class);
        //判断是否修改订单状态为 |8(经销商待确认出厂价)(停用)| 9(货款支付审核)
        if(!status.equals(OrderStatus.TO_AUDIT.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
        }
        return this.orderFacade.updateOrderStatus(id,status,null);
    }
    /**
     * 生产计划管理模块修改订单状态
     * @param id
     * @return
     */
    @PutMapping("/{id}/manufactureplan")
    private RequestResult manufacturePlanUpdaterOrderStatus(@PathVariable String id,
                                                     @RequestBody MapContext mapContext){
        Integer status = mapContext.getTypedValue(WebConstant.KEY_ENTITY_STATUS,Integer.class);
        //判断是否修改订单状态为 11(生产中)
        if(!status.equals(OrderStatus.IN_PRODUCTION.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
        }
        return this.orderFacade.updateOrderStatus(id,status,null);
    }
    /**
     * 生产过程管理模块修改订单状态
     * @param id
     * @return
     */
    @PutMapping("/{id}/manufactureprocess")
    private RequestResult manufactureProcessUpdaterOrderStatus(@PathVariable String id,
                                                     @RequestBody MapContext mapContext){
        Integer status = mapContext.getTypedValue(WebConstant.KEY_ENTITY_STATUS,Integer.class);
        //判断是否修改订单状态为 12(待入库)
        if(!status.equals(OrderStatus.TO_WAREHOUSE.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
        }
        return this.orderFacade.updateOrderStatus(id,status,null);
    }

    /**
     * 财务管理模块修改订单状态
     * @param id
     * @return
     */
    @PutMapping("/{id}/finance")
    private RequestResult financeUpdaterOrderStatus(@PathVariable String id,
                                                    @RequestBody MapContext mapContext){
        Integer status = mapContext.getTypedValue(WebConstant.KEY_ENTITY_STATUS,Integer.class);
        //判断是否修改订单状态为 4(待设计) 10(待生产)
        if(!status.equals(OrderStatus.TO_DESIGN.getValue())&&!status.equals(OrderStatus.TO_PRODUCTION.getValue())){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ORDER_STATUS_ERROR_10077,AppBeanInjector.i18nUtil.getMessage("BIZ_ORDER_STATUS_ERROR_10077"));
        }
        return this.orderFacade.updateOrderStatus(id,status,null);
    }



    /**
     * 通过条件查询订单列表
     *
     * @param no
     * @param customerTel
     * @param pageNum
     * @param pageSize
     * @param status
     * @param allocated     false 未分配设计师订单 true 已分配设计师订单
     * @param confirmFprice false 厂家最终报价已确认 true 厂家最终报价未确认
     * @return
     */
    @GetMapping
    @ApiOperation(value = "通过条件查询订单列表",notes = "通过条件查询订单列表",response = CustomOrderDto.class)
    private String findOrderList(@RequestParam(required = false)@ApiParam(value = "订单编号") String no,
                                 @RequestParam(required = false)@ApiParam(value = "客户电话") String customerTel,
                                 @RequestParam(required = false)@ApiParam(value = "页码") Integer pageNum,
                                 @RequestParam(required = false)@ApiParam(value = "每页数据量") Integer pageSize,
                                 @RequestParam(required = false)@ApiParam(value = "订单状态集合") List<Integer> status,
                                 @RequestParam(required = false)@ApiParam(value = "是否分配设计师") Boolean allocated,
                                 @RequestParam(required = false)@ApiParam(value = "厂房最终报价是否已确认") Boolean confirmFprice,
                                 @RequestParam(required = false)@ApiParam(value = "经销商电话") String dealerTel,
                                 @RequestParam(required = false)@ApiParam(value = "公司主键ID") String companyId) {
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        MapContext mapContext = this.createMapContext(no, customerTel, status,companyId , allocated, confirmFprice,dealerTel);
        JsonMapper jsonMapper = new JsonMapper();
        return jsonMapper.toJson(this.orderFacade.findOrderList(mapContext, pageNum, pageSize));
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @ApiResponses({
            @ApiResponse(code = 200,message = "查询成功",response =CustomOrderInfoDto.class )
    })
    @ApiOperation(value = "查询订单详情",notes = "查询订单详情",response =CustomOrderInfoDto.class )
    @GetMapping("/{id}/info")
    private RequestResult findOrderInfo(@PathVariable String id) {
        return this.orderFacade.findOrderInfo(id);
    }

    /**
     * 给订单设置设计人员
     *
     * @param userId
     * @param id
     * @return
     */
    @PutMapping("/{id}/designers/{userId}")
    private RequestResult updateOrderDesigner(@PathVariable String userId,
                                              @PathVariable String id) {
        return this.orderFacade.updateOrderDesigner(userId, id);
    }

    /**
     * 查询订单下需求详情
     *
     * @param id
     * @param demandId
     * @return
     */
    @GetMapping("/{id}/demands/{demandId}")
    private RequestResult findOrderDemand(@PathVariable String id,
                                          @PathVariable String demandId) {
        return this.orderFacade.findOrderDemand(id, demandId);
    }

    /**
     * 查询订单下的设计详情
     *
     * @param id
     * @param designId
     * @return
     */
    @ApiOperation(value = "查询订单下的设计详情",notes = "查询订单下的设计详情",response =CustomOrderDesignDto.class )
    @GetMapping("/{id}/designs/{designId}")
    private RequestResult findOrderDesignId(@PathVariable@ApiParam(value = "订单主键ID") String id,
                                            @PathVariable@ApiParam(value = "设计方案主键ID") String designId) {
        return this.orderFacade.findOrderDesignId(id, designId);
    }

    /**
     * 新建设计记录
     *
     * @param id
     * @param customOrderDesign
     * @return
     */
    @PostMapping("/{id}/designs")
    private RequestResult addOrderDesign(@PathVariable String id,
                                         @RequestBody CustomOrderDesign customOrderDesign) {
        customOrderDesign.setCustomOrderId(id);
        customOrderDesign.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.DESIGN_NO));
        customOrderDesign.setCreated(DateUtil.getSystemDate());
        customOrderDesign.setDesigner(WebUtils.getCurrUserId());
        customOrderDesign.setStatus(0);
        RequestResult result = customOrderDesign.validateFields();
        if (result != null) {
            return result;
        }
        return this.orderFacade.addOrderDesign(id, customOrderDesign);
    }

    /**
     * 修改设计记录
     *
     * @param id
     * @param designId
     * @param mapContext
     * @return
     */
    @PutMapping("/{id}/designs/{designId}")
    private RequestResult updateDesign(@PathVariable String id,
                                       @PathVariable String designId,
                                       @RequestBody MapContext mapContext) {
        RequestResult result = CustomOrderDesign.validateFields(mapContext);
        if (result != null) {
            return result;
        }
        Integer status = mapContext.getTypedValue("status", Integer.class);
        if (status != null && status.equals(3)) {
            mapContext.put("endTime", DateUtil.getSystemDate());
        } else if (status != null && status.equals(0)) {
            mapContext.put("endTime", null);
        }
        return this.orderFacade.updateDesign(id, designId, mapContext);
    }

    /**
     * 删除设计记录
     *
     * @param id
     * @param designId
     * @return
     */
    @DeleteMapping("{id}/designs/{designId}")
    private RequestResult deleteDesign(@PathVariable String id,
                                       @PathVariable String designId) {
        return this.orderFacade.deleteDesign(id, designId);
    }

    /**
     * 上传设计图片
     *
     * @param id
     * @param designId
     * @param category
     * @param multipartFileList
     * @return
     */
    @PostMapping("{id}/designs/{designId}/files")
    private RequestResult uploadFile(@PathVariable String id,
                                     @PathVariable String designId,
                                     @RequestParam Integer category,
                                     @RequestBody List<MultipartFile> multipartFileList) {
        Map<String, String> result = new HashMap<>();
        if (multipartFileList == null || multipartFileList.size() == 0) {
            result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, result);
        }
        for (MultipartFile multipartFile : multipartFileList) {
            if (multipartFile == null) {
                result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            } else if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
                result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
            } else if (multipartFile.getSize() > 1024 * 1024 * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
                return ResultFactory.generateErrorResult(ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
            }
            if (result.size() > 0) {
                return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, result);
            }
        }
        return this.orderFacade.uploadFile(id, designId, multipartFileList, category);
    }

    /**
     * 删除设计记录下某张图片
     *
     * @param id
     * @param designId
     * @param fileId
     * @return
     */
    @DeleteMapping("{id}/designs/{designId}/files/{fileId}")
    private RequestResult deleteFile(@PathVariable String id,
                                     @PathVariable String designId,
                                     @PathVariable String fileId) {
        return this.orderFacade.deleteFile(id, designId, fileId);
    }

    /**
     * 新增订单
     *
     * @param customOrderInfoDto
     * @return
     */
    @ApiResponses({
            @ApiResponse(code = 200,message = "添加成功")
    })
    @ApiOperation(value = "新增订单",notes = "新增订单",response = CustomOrderInfoDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "订单数据",name = "customOrderInfoDto",dataTypeClass = CustomOrderInfoDto.class,paramType = "body")
    })
    @PostMapping
    private String addOrder(@RequestBody CustomOrderInfoDto customOrderInfoDto) {
        CustomOrderDto customOrder = customOrderInfoDto.getCustomOrder();
//        Payment payment = null;
        if(customOrder.getIsDesign().equals(CustomOrderIsDesign.NEED_DESIGN.getValue())){
            //如果订单需要设计 则状态为 订单设计费待审核 否则状态为 货款支付审核
//            payment = new Payment();
//            payment.setHolder("红田集团");
//            payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
//            payment.setAmount(new BigDecimal(customOrder.getDesignFee()));
//            payment.setCompanyId(customOrder.getCompanyId());
//            payment.setStatus(PaymentStatus.PENDING_PAYMENT.getValue());
//            payment.setCreated(DateUtil.getSystemDate());
//            payment.setCreator(WebUtils.getCurrUserId());
//            payment.setWay(PaymentWay.BANK.getValue());
//            payment.setType(PaymentType.B_TO_F_WITHHOLD.getValue());
//            payment.setPayee("4j1u3r1efshq");
            customOrder.setStatus(OrderStatus.TO_ADD_DESIGNFEE.getValue());
//            payment.setFunds(PaymentFunds.DESSIGN_FEE.getValue());
        }else{
            customOrder.setStatus(OrderStatus.FACTORY_CONFIRMED_FPROCE.getValue());
        }
        customOrder.setCreated(DateUtil.getSystemDate());
        customOrder.setCreator(WebUtils.getCurrUserId());
        customOrder.setNo(AppBeanInjector.uniquneCodeGenerator.getNoByTime(DateUtil.stringToDate(customOrder.getOrderTime())));
        customOrder.setImprest(new BigDecimal(0));
        customOrder.setRetainage(new BigDecimal(0));
        customOrder.setMerchandiser(WebUtils.getCurrUserId());
        customOrder.setEarnest(0);
        customOrder.setFactoryPrice(new BigDecimal(0));
        customOrder.setFactoryFinalPrice(new BigDecimal(0));
        customOrder.setDesignFee(0);
        customOrder.setMarketPrice(new BigDecimal(0));
        customOrder.setDiscounts(new BigDecimal(0));
        customOrder.setFactoryDiscounts(new BigDecimal(0));
        customOrder.setAmount(new BigDecimal(0));
        customOrder.setConfirmFprice(false);
        customOrder.setConfirmCprice(false);
        customOrder.setCoordination(CustomOrderCoordination.UNWANTED_COORDINATION.getValue());
        RequestResult result = customOrder.validateFields();
        JsonMapper jsonMapper = new JsonMapper();
        if (result != null) {
            return jsonMapper.toJson(result);
        }
        return jsonMapper.toJson(this.orderFacade.factoryAddOrder(customOrderInfoDto, null,null));
    }

    /**
     * 修改订单信息
     *
     * @param id
     * @param mapContext
     * @return
     */
    @PutMapping("{id}")
    private RequestResult factoryUpdateOrder(@PathVariable String id,
                                             @RequestBody MapContext mapContext) {
        RequestResult result = CustomOrder.validateFields(mapContext);
        if (result != null) {
            return result;
        }
        return this.orderFacade.factoryUpdateOrder(id, mapContext);
    }

    /**
     * 根据订单 获取扣款详情
     *
     * @param orderId
     * @return
     */
    @GetMapping("{orderId}/amount")
    private RequestResult findAmountInfo(@PathVariable String orderId) {
        return this.orderFacade.findAmountInfo(orderId);
    }

    /**
     * 新建售后订单
     *
     * @param customOrder
     * @return
     */
    @PostMapping("{aftersaleId}/aftersales")
    private RequestResult addAftersaleOrder(@RequestBody CustomOrder customOrder,
                                            @PathVariable String aftersaleId) {
        //出厂价必填
        if (customOrder.getFactoryPrice() == null) {
            Map<String, String> validResult = new HashMap<>();
            validResult.put("factoryPrice", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, validResult);
        }
        customOrder.setCreated(DateUtil.getSystemDate());
        customOrder.setCreator(WebUtils.getCurrUserId());
        customOrder.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.ORDER_NO));
        customOrder.setAmount(new BigDecimal(0));
        customOrder.setImprest(new BigDecimal(0));
        customOrder.setRetainage(new BigDecimal(0));
        customOrder.setEarnest(0);
        customOrder.setDesignFee(0);
        customOrder.setMarketPrice(new BigDecimal(0));
        customOrder.setDiscounts(new BigDecimal(0));
        customOrder.setFactoryDiscounts(new BigDecimal(0));
        customOrder.setFactoryFinalPrice(customOrder.getFactoryPrice());
        customOrder.setConfirmFprice(false);
        customOrder.setConfirmCprice(false);
        customOrder.setCoordination(CustomOrderCoordination.UNWANTED_COORDINATION.getValue());
        RequestResult result = customOrder.validateFields();
        if (result != null) {
            return result;
        }
        return this.orderFacade.factoryAddOrder(null, aftersaleId,null);
    }

//	@GetMapping("examine/price")
//	private RequestResult findExaminePriceOrderList(@RequestParam(required = false,defaultValue = "1")Integer pageNum,@RequestParam(required = false,defaultValue = "10")Integer){
//
//	}

	/**
	 * 新增生产拆单
	 * @param id
	 * @param produceOrderDto
	 * @return
	 */
    @ApiResponses({
            @ApiResponse(code = 200,message = "添加成功",response =ProduceOrderDto.class )
    })
    @ApiOperation(value = "新增生产拆单",notes = "新增生产拆单")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "订单主键ID",dataType = "string",paramType = "path")
	})
    @PostMapping("/{id}/produces")
    private RequestResult addProduceOrder(@PathVariable String id, @RequestBody ProduceOrderDto produceOrderDto){
        JsonMapper jsonMapper = new JsonMapper();
        return this.orderFacade.addProduceOrder(id,produceOrderDto,produceOrderDto.getFileIds());
    }

	/**
	 * 修改生产拆单
	 * @param id
	 * @param pId
	 * @param mapContext
	 * @return
	 */
    @ApiResponses({
            @ApiResponse(code = 200,message = "修改成功",response =ProduceOrderDto.class )
    })
    @ApiOperation(value = "修改生产拆单",notes = "修改生产拆单")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "订单主键ID",dataType = "string",paramType = "path"),
			@ApiImplicitParam(name = "pId",value = "生产拆单主键ID",dataType = "string",paramType = "path")
	})
    @PutMapping("/{id}/produces/{pId}")
    private RequestResult updateProduceOrder(@PathVariable String id,@PathVariable String pId, @RequestBody MapContext mapContext){
		RequestResult result = ProduceOrder.validateFields(mapContext);
        mapContext.put("updateTime",DateUtil.getSystemDate());
        mapContext.put("updateUser",WebUtils.getCurrUserId());
		if(result!=null){
			return result;
		}
		return this.orderFacade.updateProduceOrder(id,pId,mapContext);
    }
	/**
	 * 删除生产拆单
	 * @param id
	 * @param pId
	 * @return
	 */
    @ApiResponses({
            @ApiResponse(code = 200,message = "删除成功" )
    })
    @ApiOperation(value = "删除生产拆单",notes = "删除生产拆单")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "订单主键ID",dataType = "string",paramType = "path"),
			@ApiImplicitParam(name = "pId",value = "生产拆单主键ID",dataType = "string",paramType = "path")
	})
    @DeleteMapping("/{id}/produces/{pId}")
    private RequestResult deleteProduceOrder(@PathVariable String id,@PathVariable String pId){
		return this.orderFacade.deleteProduceOrder(id,pId);
    }

    /**
     * 新增订单产品
     * @param id
     * @param orderProduct
     * @return
     */
    @ApiResponses({
            @ApiResponse(code = 200,message = "添加成功",response = OrderProductDto.class)
    })
    @ApiOperation(value = "新增订单产品",notes = "新增订单产品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "订单主键ID",dataType = "string",paramType = "path")
    })
    @PostMapping("/{id}/products")
    private RequestResult addOrderProduct(@PathVariable String id,@RequestBody OrderProduct orderProduct){
        orderProduct.setCustomOrderId(id);
        orderProduct.setCreated(DateUtil.getSystemDate());
        orderProduct.setCreator(WebUtils.getCurrUserId());
        RequestResult result = orderProduct.validateFields();
        if(result!=null){
            return result;
        }
        return this.orderFacade.addOrderProduct(id,orderProduct);
    }

    /**
     * 修改订单产品信息
     * @param id
     * @param pId
     * @param mapContext
     * @return
     */
    @ApiResponses({
            @ApiResponse(code = 200,message = "修改成功",response = OrderProductDto.class)
    })
    @ApiOperation(value = "修改订单产品信息",notes = "修改订单产品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "订单主键ID",dataType = "string",paramType = "path"),
            @ApiImplicitParam(name = "pId",value = "订单产品主键ID",dataType = "string",paramType = "path")
    })
    @PutMapping("/{id}/products/{pId}")
    private RequestResult updateOrderProduct(@PathVariable String id,@PathVariable String pId,@RequestBody MapContext mapContext){
        RequestResult result = OrderProduct.validateFields(mapContext);
        if(result!=null){
            return result;
        }
        mapContext.put("updateTime",DateUtil.getSystemDate());
        mapContext.put("updateUser",WebUtils.getCurrUserId());
        return this.orderFacade.updateOrderProduct(id,pId,mapContext);
    }
    /**
     * 删除订单产品信息
     * @param id
     * @param pId
     * @return
     */
    @ApiResponses({
            @ApiResponse(code = 200,message = "删除成功",response = OrderProductDto.class)
    })
    @ApiOperation(value = "删除订单产品信息",notes = "删除订单产品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "订单主键ID",dataType = "string",paramType = "path"),
            @ApiImplicitParam(name = "pId",value = "订单产品主键ID",dataType = "string",paramType = "path")
    })
    @DeleteMapping("/{id}/products/{pId}")
    private RequestResult deleteOrderProduct(@PathVariable String id,@PathVariable String pId){
        return this.orderFacade.deleteOrderProduct(id,pId);
    }

    /**
     * 查询生产跟进生产单
     * @param no
     * @param orderNo
     * @param way
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiResponses({
            @ApiResponse(code = 200,message = "查询成功",response = ProduceOrderDto.class)
    })
    @ApiOperation(value = "查询生产跟进生产单",notes = "查询生产跟进生产单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "no",value = "生产编号",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "orderNo",value = "订单编号",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "plannedTime",value = "计划生产时间 0 全部 1 未安排 2 已安排",dataType = "int",paramType = "query")
    })
    @GetMapping("/produces/plans")
    private RequestResult findProducesPlanList(@RequestParam(required = false)String no, @RequestParam(required = false) String orderNo
            ,@RequestParam(required = false)@ApiParam(value = "生产方式 0 自产 1 外协 2 特供实木") List<Integer> way
            ,@RequestParam(required = false)@ApiParam(value = "生产拆单类型 0 柜体 1 门板 2 五金") List<Integer> type
            ,@RequestParam(required = false)@ApiParam(value = "生产状态 0 未开始 1 生产中 2 已完成") List<Integer> state
    ,@RequestParam(required = false,defaultValue = "1")Integer pageNum,@RequestParam(required = false,defaultValue = "10")Integer pageSize
    ,@RequestParam(required = false)Integer plannedTime
    ,@RequestParam(required = false)@ApiParam(value = "true 已付款 false 未付款 不传查看全部")Boolean pay){
        MapContext mapContext = this.createProduceMapContext(no,orderNo,way,type,state,pay,plannedTime);
        return this.orderFacade.findProducesList(mapContext,pageNum,pageSize);
    }

    /**
     * 批量开始生产
     * @param ids
     * @return
     */
    @ApiOperation(value = "批量开始生产",notes = "批量开始生产")
    @PutMapping("/produces/starts")
	private RequestResult producesPlansListStart(@RequestBody @ApiParam(value = "生产单编号") List<String> ids){
    	return this.orderFacade.producesPlansListStart(ids);
	}

    /**
     * 查询生产执行的生产单
     * @param no
     * @param orderNo
     * @param way
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiResponses({
            @ApiResponse(code = 200,message = "查询成功",response = ProduceOrderDto.class)
    })
    @ApiOperation(value = "查询生产执行的生产单",notes = "查询生产执行的生产单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "no",value = "生产编号",dataType = "string",paramType = "path"),
            @ApiImplicitParam(name = "orderNo",value = "订单编号",dataType = "string",paramType = "path")
    })
    @GetMapping("/produces/processes")
    private RequestResult findProducesProcessList(@RequestParam(required = false)String no, @RequestParam(required = false) String orderNo
    ,@RequestParam(required = false)@ApiParam(value = "生产方式 0 自产 1 外协 2 特供实木") List<Integer> way
    ,@RequestParam(required = false)@ApiParam(value = "生产拆单类型 0 柜体 1 门板 2 五金") List<Integer> type
    ,@RequestParam(required = false)@ApiParam(value = "生产状态 0 未开始 1 生产中 2 已完成") List<Integer> state
    ,@RequestParam(required = false,defaultValue = "1")Integer pageNum,@RequestParam(required = false,defaultValue = "10")Integer pageSize){
        MapContext mapContext = this.createProduceMapContext(no,orderNo,way,type,state,ProduceOrderPay.PAY.getValue(),2);
        return this.orderFacade.findProducesList(mapContext,pageNum,pageSize);
    }
    /**
     * 批量安排生产时间
     * @param mapContext
     * @return
     */
    @ApiOperation(value = "批量安排生产时间",notes = "批量安排生产时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids",value = "生产单编号",dataType = "list",paramType = "body"),
            @ApiImplicitParam(name = "planTime",value = "计划生产时间",dataType = "date",paramType = "body")
    })
    @PutMapping("/produces/plans")
    private RequestResult updateProducesOrderPlanTime(@RequestBody MapContext mapContext){
        return this.orderFacade.updateProducesOrderPlanTime(mapContext.getTypedValue("ids",List.class),mapContext.getTypedValue("planTime",Date.class));
    }

    /**
     * 生产单流程完成
     * @param id
     * @param produceFlow
     * @return
     */
    @ApiResponses({
            @ApiResponse(code = 200,message = "添加成功",response = ProduceFlowDto.class)
    })
    @ApiOperation(value = "生产单流程完成",notes = "生产单流程完成")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "生产单主键ID",dataType = "string",paramType = "path")
    })
    @PostMapping("/produces/processes/{id}")
    private RequestResult addProduceFlow(@PathVariable String id, @RequestBody@ApiParam(value = "生产流程信息") ProduceFlow produceFlow){
        produceFlow.setProduceOrderId(id);
        produceFlow.setOperator(WebUtils.getCurrUserId());
        produceFlow.setOperationTime(DateUtil.getSystemDate());
        produceFlow.setEndTime(DateUtil.getSystemDate());
        RequestResult result = produceFlow.validateFields();
        if(result!=null){
            return result;
        }
        return this.orderFacade.addProduceFlow(id,produceFlow);
    }

    /**
     * 生产单 打包
     * @param id
     * @param produceFlow
     * @return
     */
    @ApiResponses({
			@ApiResponse(code = 200,message = "打包完成",response = ProduceFlowDto.class)
	})
    @ApiOperation(value = "生产单 打包",notes = "生产单 打包")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "produceFlow",value = "生产流程明细",dataTypeClass = ProduceFlow.class,paramType = "body")
    })
    @PostMapping("/produces/processes/{id}/packing")
    private RequestResult produceFlowPacking(@PathVariable String id,@RequestBody ProduceFlow produceFlow){
        produceFlow.setProduceOrderId(id);
        produceFlow.setOperator(WebUtils.getCurrUserId());
        produceFlow.setOperationTime(DateUtil.getSystemDate());
        produceFlow.setEndTime(DateUtil.getSystemDate());
        RequestResult result = produceFlow.validateFields();
        if(result!=null){
            return result;
        }
        return this.orderFacade.produceFlowPacking(id,produceFlow);
    }

    /**
     * 外协单完成
     * @param id
     * @return
     */
    @ApiOperation(value = "外协单完成",notes = "外协单完成")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "外协单主键ID",dataType = "string",paramType = "path")
    })
    @PutMapping("/produces/coordinations/{id}/ends")
    private RequestResult endCoordination(@PathVariable String id){
        return this.orderFacade.endCoordination(id);
    }


    /**
     * 查询设计案例列表
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "查询设计案例列表",notes = "查询设计案例列表",response = CustomOrderDesignDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo",value = "订单编号",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "no",value = "设计单编号",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "pageNum",value = "页码",dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "pageSize",value = "数据量",dataType = "int",paramType = "query"),
    })
    @GetMapping("/designs")
    private RequestResult findAllDesign(@RequestParam(required = false) String orderNo,@RequestParam(required = false)String no,@RequestParam(required = false,defaultValue = "1")Integer pageNum
    ,@RequestParam(required = false,defaultValue = "10")Integer pageSize){
        MapContext mapContext = new MapContext();
        if(orderNo!=null){
            mapContext.put("orderNo",orderNo);
        }
        if(no!=null){
        	mapContext.put("no",no);
		}
        return this.orderFacade.findAllDesign(mapContext,pageNum,pageSize);
    }

	/**
	 * 上传订单相关资源文件(订单产品 生产拆单)
	 *
	 * @param id
	 * @param type
	 * @param resId
	 * @param multipartFileList
	 * @return
	 */
	@ApiOperation(value = "上传订单相关资源文件(订单产品 生产拆单)",notes = "上传订单相关资源文件(订单产品 生产拆单)")
	@PostMapping("/{id}/{type}/{resId}/files")
	private RequestResult uploadOrderFile(@PathVariable@ApiParam(value = "订单主键ID") String id,
									 @PathVariable@ApiParam(value = "0(订单需求) 1(订单设计) 2(合同附件) 3(订单产品) 4(生产拆单) 5(生产流程)") Integer type,
									 @PathVariable@ApiParam(value = "相关资源Id") String resId,
									 @RequestBody@ApiParam(value = "文件集合") List<MultipartFile> multipartFileList) {
		Map<String, String> result = new HashMap<>();
		if (multipartFileList == null || multipartFileList.size() == 0) {
			result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, result);
		}
		for (MultipartFile multipartFile : multipartFileList) {
			if (multipartFile == null) {
				result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			} else if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
				result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
			} else if (multipartFile.getSize() > 1024 * 1024 * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
			}
			if (result.size() > 0) {
				return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, result);
			}
		}
		return this.orderFacade.uploadOrderFiles(id, type,resId, multipartFileList);
	}
    /**
     * 删除订单资源相关文件
     * @param id
     * @param fileId
     * @return
     */
    @ApiOperation(value = "删除订单资源相关文件",notes = "删除订单资源相关文件")
    @DeleteMapping("/{id}/files/{fileId}")
    private RequestResult deleteCustomOrderFile(@PathVariable@ApiParam(value = "订单主键ID") String id,@PathVariable@ApiParam(value = "资源文件主键ID") String fileId){
        return this.orderFacade.deleteCustomOrderFile(id,fileId);
    }

    private MapContext createProduceMapContext(String no, String orderNo, List<Integer> way, List<Integer> type,List<Integer> state,Boolean pay,Integer plannedTime) {
        MapContext mapContext = new MapContext();
        if(no!=null&&!no.trim().equals("")){
            mapContext.put(WebConstant.STRING_NO,no);
        }
        if(orderNo!=null&&!orderNo.trim().equals("")){
            mapContext.put("orderNo",orderNo);
        }
        if(way!=null){
            mapContext.put("way",way);
        }
        if(type!=null){
            mapContext.put("type",type);
        }
        if(state!=null){
            mapContext.put(WebConstant.KEY_ENTITY_STATE,state);
        }
        if(pay!=null){
            mapContext.put("pay",pay);
        }
        if(plannedTime!=null){
            mapContext.put("plannedTime",plannedTime);
        }
        return mapContext;
    }

    private MapContext createMapContext(String no, String customerTel, List<Integer> status, String companyId, Boolean allocated, Boolean confirmFprice,String dealerTel) {
        MapContext mapContext = MapContext.newOne();
        if (no != null && !no.trim().equals("")) {
            mapContext.put(WebConstant.STRING_NO, no);
        }
        if (customerTel != null && !customerTel.trim().equals("")) {
            mapContext.put("customerTel", customerTel);
        }
        if (status != null && status.size() != 0) {
            mapContext.put(WebConstant.KEY_ENTITY_STATUS, status);
        }
        if (companyId!=null&&!companyId.equals("")) {
            mapContext.put(WebConstant.KEY_ENTITY_COMPANY_ID, companyId);
        }
        if (allocated != null) {
            if (allocated) {
                if (UserUtil.hasRoleByKey(WebUtils.getCurrUserId(), FactoryEmployeeRole.DESIGNER.getValue())) {
                    mapContext.put("designer", WebUtils.getCurrUserId());
                } else {
                    mapContext.put("designer", "notNull");
                }
            } else {
                mapContext.put("designer", "null");
            }
        }
        if (confirmFprice != null) {
            mapContext.put("confirmFprice", confirmFprice);
        }
        if(dealerTel!=null){
            mapContext.put("dealerTel",dealerTel);
        }
        return mapContext;
    }
}
