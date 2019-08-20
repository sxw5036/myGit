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
import com.lwxf.industry4.webapp.common.enums.customorder.ProduceOrderPermit;
import com.lwxf.industry4.webapp.common.enums.customorder.ProduceOrderState;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentStatus;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentWay;
import com.lwxf.industry4.webapp.common.enums.order.OrderDesignStatus;
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
import com.lwxf.industry4.webapp.facade.admin.factory.dealer.OrderFacade;
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
	@ApiImplicitParams({
			@ApiImplicitParam(value = "开始日期",name = "startTime",dataType = "date",paramType = "query")
	})
    @ApiOperation(value = "通过条件查询订单列表",notes = "通过条件查询订单列表",response = CustomOrderDto.class)
    private String findOrderList(@RequestParam(required = false)@ApiParam(value = "订单编号") String no,
                                 @RequestParam(required = false)@ApiParam(value = "客户电话") String customerTel,
                                 @RequestParam(required = false)@ApiParam(value = "客户名称") String customerName,
                                 @RequestParam(required = false)@ApiParam(value = "页码") Integer pageNum,
                                 @RequestParam(required = false)@ApiParam(value = "每页数据量") Integer pageSize,
                                 @RequestParam(required = false)@ApiParam(value = "订单状态集合") List<Integer> status,
                                 @RequestParam(required = false)@ApiParam(value = "是否分配设计师") Boolean allocated,
                                 @RequestParam(required = false)@ApiParam(value = "厂房最终报价是否已确认") Boolean confirmFprice,
                                 @RequestParam(required = false)@ApiParam(value = "经销商电话") String dealerTel,
                                 @RequestParam(required = false)@ApiParam(value = "收货地址") String address,
                                 @RequestParam(required = false)@ApiParam(value = "公司主键ID") String companyId,
                                 @RequestParam(required = false)@ApiParam(value = "经销商公司名称") String companyName,
								 @RequestParam(required = false)@ApiParam(value = "开始日期") String startTime,
								 @RequestParam(required = false)@ApiParam(value = "是否需要设计 0 不需要 1 需要") Integer design,
								 @RequestParam(required = false)@ApiParam(value = "结束日期") String endTime
								 ) {
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        MapContext mapContext = this.createMapContext(no, customerTel, status,companyId ,address, allocated, confirmFprice,dealerTel,startTime,endTime,design,companyName,customerName);
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
    private String findOrderInfo(@PathVariable String id) {
        JsonMapper jsonMapper = new JsonMapper();
        return jsonMapper.toJson(this.orderFacade.findOrderInfo(id));
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
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @ApiResponses({
            @ApiResponse(code = 200,message = "查询成功",response =CustomOrderInfoDto.class )
    })
    @ApiOperation(value = "查询订单详情",notes = "查询订单详情",response =CustomOrderInfoDto.class )
    @GetMapping("/{id}/infoNew")
    private String viewOrderInfo(@PathVariable String id) {
        JsonMapper jsonMapper = new JsonMapper();
        return jsonMapper.toJson(this.orderFacade.findOrderInfoNew(id));
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
     * 新建设计方案
     *
     * @param id
     * @param customOrderDesign
     * @return
     */
    @PostMapping("/{id}/designs")
    @ApiOperation(value = "新建设计方案",notes = "新建设计方案" )
    private RequestResult addOrderDesign(@PathVariable String id,
                                         @RequestBody@ApiParam(value = "设计方案信息") CustomOrderDesign customOrderDesign) {
        customOrderDesign.setCustomOrderId(id);
        customOrderDesign.setCreated(DateUtil.getSystemDate());
        customOrderDesign.setStartTime(DateUtil.getSystemDate());
        customOrderDesign.setStatus(OrderDesignStatus.BEINGDESIGNED.getValue());
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
    @ApiOperation(value = "修改设计记录",notes = "修改设计记录")
    private RequestResult updateDesign(@PathVariable String id,
                                       @PathVariable String designId,
                                       @RequestBody MapContext mapContext) {
        RequestResult result = CustomOrderDesign.validateFields(mapContext);
        if (result != null) {
            return result;
        }
        Integer status = mapContext.getTypedValue("status", Integer.class);
        if (status != null && status.equals(OrderDesignStatus.TOAUDIT.getValue())) {
            mapContext.put("endTime", DateUtil.getSystemDate());
        } else if (status != null && status.equals(OrderDesignStatus.AUDITED.getValue())) {
            mapContext.put("examineTime", DateUtil.getSystemDate());
            mapContext.put("examineUser",WebUtils.getCurrUserId());
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
    @ApiOperation(value = "删除设计记录",notes = "删除设计记录")
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
    @ApiOperation(value = "上传设计图片",notes = "上传设计图片")
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
        customOrder.setStatus(OrderStatus.TO_PAID.getValue());
        customOrder.setCreated(DateUtil.getSystemDate());
        customOrder.setCreator(WebUtils.getCurrUserId());
        int prodLength = customOrderInfoDto.getOrderProducts().size();
        String orderNo=WebConstant.FACTORY_NAME_CODE+AppBeanInjector.uniquneCodeGenerator.getNoByTime(DateUtil.stringToDate(customOrder.getOrderTime()))+"-"+prodLength;
        customOrder.setNo(orderNo);
        customOrder.setImprest(new BigDecimal(0));
        customOrder.setRetainage(new BigDecimal(0));
//        customOrder.setMerchandiser(WebUtils.getCurrUserId()); 跟单员需用户选择
        customOrder.setEarnest(0);
        customOrder.setFactoryPrice(new BigDecimal(0));
        customOrder.setDesignFee(0);
        customOrder.setMarketPrice(new BigDecimal(0));
        customOrder.setDiscounts(new BigDecimal(0));
        customOrder.setFactoryDiscounts(new BigDecimal(0));
        customOrder.setAmount(new BigDecimal(0));
        customOrder.setConfirmFprice(false);
        customOrder.setConfirmCprice(false);
        customOrder.setEstimatedDeliveryDate(null);
        customOrder.setCoordination(CustomOrderCoordination.UNWANTED_COORDINATION.getValue());
        customOrder.setBranchId(WebUtils.getCurrBranchId());
        RequestResult result = customOrder.validateFields();
        JsonMapper jsonMapper = new JsonMapper();
        if (result != null) {
            return jsonMapper.toJson(result);
        }
        for(OrderProductDto orderProductDto :customOrderInfoDto.getOrderProducts()){
        	orderProductDto.setCreator(WebUtils.getCurrUserId());
        	orderProductDto.setCreated(DateUtil.getSystemDate());
        	//临时赋值 订单ID 后面订单生成后 会覆盖 此值
        	orderProductDto.setCustomOrderId("aa");
            RequestResult result1 = orderProductDto.validateFields();
            if(result1!=null){
                return jsonMapper.toJson(result1);
            }
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
    @PostMapping("/{id}/produces/{productId}")
    private RequestResult addProduceOrder(@PathVariable@ApiParam(value = "订单Id") String id,@PathVariable@ApiParam(value = "产品ID") String productId, @RequestBody ProduceOrderDto produceOrderDto){
        produceOrderDto.setOrderProductId(productId);
        produceOrderDto.setPermit(ProduceOrderPermit.NOT_ALLOW.getValue());
        if (produceOrderDto.getWay()!=null&&produceOrderDto.getWay().equals(ProduceOrderWay.COORDINATION.getValue())) {
            return this.orderFacade.addCorporateProduceOrder(id, produceOrderDto, produceOrderDto.getFileIds());
        }else{
            return this.orderFacade.addProduceOrder(id, produceOrderDto, produceOrderDto.getFileIds());
        }
    }

    @ApiResponses({
            @ApiResponse(code = 200,message = "添加成功",response =ProduceOrderDto.class )
    })
    @ApiOperation(value = "查询生产拆单详情",notes = "查询生产拆单详情")
    @GetMapping("/{id}/produces/{produceId}")
    private String findProduceOrderById(@PathVariable@ApiParam(value = "订单Id") String id,@PathVariable@ApiParam(value = "产品ID") String produceId){
        JsonMapper jsonMapper = new JsonMapper();
        return jsonMapper.toJson(this.orderFacade.findProduceOrderById(id,produceId));
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
    private RequestResult addOrderProduct(@PathVariable String id,@RequestBody OrderProductDto orderProduct){
        orderProduct.setCustomOrderId(id);
        orderProduct.setCreated(DateUtil.getSystemDate());
        orderProduct.setCreator(WebUtils.getCurrUserId());
        RequestResult result = orderProduct.validateFields();
        if(result!=null){
            return result;
        }
        return this.orderFacade.addOrderProduct(id,orderProduct);
    }


    @ApiOperation(value = "查询订单产品详情",notes = "查询订单产品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "订单主键ID",dataType = "string",paramType = "path")
    })
    @GetMapping("/{id}/products/{pId}")
    private RequestResult findProductInfo(@PathVariable@ApiParam(value = "订单ID") String id,@PathVariable@ApiParam(value = "产品ID") String pId){
        return this.orderFacade.findProductInfo(id,pId);
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
        return this.orderFacade.findProducesList(mapContext,pageNum,pageSize,new ArrayList<Map<String, String>>());
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
        return this.orderFacade.findProducesList(mapContext,pageNum,pageSize,new ArrayList<Map<String, String>>());
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
									 @RequestBody@ApiParam(value = "文件集合") List<MultipartFile> multipartFileList) { Map<String, String> result = new HashMap<>();
		if (multipartFileList == null || multipartFileList.size() == 0) {
			result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, result);
		}
		for (MultipartFile multipartFile : multipartFileList) {
			if (multipartFile == null) {
				result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			} else if (FileMimeTypeUtil.isLegalVideoFileType(multipartFile)) {
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

    /**
     * 删除订单
     * @param orderId
     * @return
     */
    @ApiOperation(value = "删除订单",notes = "删除订单")
    @DeleteMapping("/{orderId}")
    private RequestResult deleteOrderById(@PathVariable String orderId){
        return this.orderFacade.deleteOrderById(orderId);
    }

    @ApiOperation(value = "生产批准",notes = "生产批准")
    @PutMapping("/{orderId}/produce/{produceId}/permit")
    private RequestResult producePermit(@PathVariable@ApiParam(value = "订单ID") String orderId,@PathVariable@ApiParam(value = "生产单ID") String produceId){
        return this.orderFacade.producePermit(orderId,produceId);
    }
    @ApiOperation(value = "生产完成",notes = "生产完成")
    @PutMapping("/{orderId}/produce/{produceId}/complete")
    private RequestResult produceComplete(@PathVariable@ApiParam(value = "订单ID") String orderId,@PathVariable@ApiParam(value = "生产单ID") String produceId){
        return this.orderFacade.produceComplete(orderId,produceId);
    }
    @ApiOperation(value = "提交设计费",notes = "提交设计费")
    @PutMapping("/{orderId}/designfee")
    private RequestResult submitDesignfee(@PathVariable String orderId,@RequestBody MapContext mapContext){
        return this.orderFacade.submitDesignfee(orderId,mapContext);
    }
    @ApiOperation(value = "新建订单需求",notes = "新建订单需求")
    @PostMapping("/{orderId}/demands")
    private RequestResult addCustomOrderDemand(@PathVariable String orderId,@RequestBody CustomOrderDemand customOrderDemand){
        customOrderDemand.setCreated(DateUtil.getSystemDate());
        customOrderDemand.setCreator(WebUtils.getCurrUserId());
        customOrderDemand.setCustomOrderId(orderId);
        customOrderDemand.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.ORDER_DEMAND_NO));
        RequestResult result = customOrderDemand.validateFields();
        if(result!=null){
            return result;
        }
        return this.orderFacade.addCustomOrderDemand(orderId,customOrderDemand);
    }
    @ApiOperation(value = "新建订单需求",notes = "新建订单需求")
    @PutMapping("/{orderId}/demands/{demandId}")
    private RequestResult updateCustomOrderDemand(@PathVariable String orderId,@PathVariable String demandId,@RequestBody MapContext mapContext){
        RequestResult result = CustomOrderDemand.validateFields(mapContext);
        if(result!=null){
            return result;
        }
        return this.orderFacade.updateCustomOrderDemand(orderId,demandId,mapContext);
    }
    @ApiOperation(value = "删除订单需求",notes = "删除订单需求")
    @DeleteMapping("/{orderId}/demands/{demandId}")
    private RequestResult deleteCustomOrderDemand(@PathVariable String orderId,@PathVariable String demandId){
        return this.orderFacade.deleteCustomOrderDemand(orderId,demandId);
    }

    @GetMapping("/designs/overview")
    @ApiOperation(value = "查询设计概览",notes = "查询设计概览")
    private RequestResult findDesignOverview(){
        return this.orderFacade.findDesignOverview();
    }

    @GetMapping("/overview")
    @ApiOperation(value = "查询订单概览",notes = "查询订单概览")
    private RequestResult findCustomOrderOverview(){
        return this.orderFacade.findCustomOrderOverview();
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

    private MapContext createMapContext(String no, String customerTel, List<Integer> status, String companyId,String address, Boolean allocated, Boolean confirmFprice,String dealerTel,String startTime,String endTime,Integer design,String companyName,String customerName) {
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
        if(address!=null){
            mapContext.put("address",address);
        }
        if(startTime!=null){
            mapContext.put("startTime",startTime);
		}
		if(endTime!=null){
            mapContext.put("endTime",endTime);
        }
        if(design!=null){
            mapContext.put("design",design);
        }
        if(companyName!=null){
            mapContext.put("companyName",companyName);
        }
        if(customerName!=null){
            mapContext.put("customerName",customerName);
        }
        return mapContext;
    }
}
