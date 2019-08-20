package com.lwxf.industry4.webapp.controller.admin.factory.finance;

import io.swagger.annotations.*;

import javax.annotation.Resource;

import java.util.*;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.order.ProduceOrderWay;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentDto;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentFiles;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.admin.factory.finance.FinanceFacade;
import com.lwxf.industry4.webapp.facade.admin.factory.dealer.OrderFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2019/1/9/009 9:58
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/finances", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
@Api(value = "FinanceController",tags = "财务审核管理")
public class FinanceController {
    @Resource(name = "financeFacade")
    private FinanceFacade financeFacade;
	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

    /**
     * 查询需要财务审核的订单与支付记录列表
     *
     * @param type 订单类型
     * @param companyId 经销商公司ID
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("customorders")
    @ApiOperation(value = "查询需要财务审核的订单与支付记录列表",notes = "查询需要财务审核的订单与支付记录列表",response = PaymentDto.class)
    private RequestResult findOrderFinanceList(@RequestParam(required = false)@ApiParam(value = "类型") Integer type,
                                               @RequestParam(required = false)@ApiParam(value = "公司Id") String companyId,
                                               @RequestParam(required = false)@ApiParam(value = "页码") Integer pageNum,
                                               @RequestParam(required = false)@ApiParam(value = "状态") Integer status,
                                               @RequestParam(required = false)@ApiParam(value = "订单编号") String orderNo,
                                               @RequestParam(required = false)@ApiParam(value = "数据量") Integer pageSize,
                                               @RequestParam(required = false)@ApiParam(value = "客户手机号")String customerTel,
                                               @RequestParam(required = false)@ApiParam(value = "款项") Integer funds) {

        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        MapContext mapContext = this.createMapContext(type, companyId, null, null, null, null, null, orderNo, null,status,funds,customerTel,null);
        return this.financeFacade.findOrderFinanceList(mapContext, pageNum, pageSize);
    }

    @GetMapping("customorders/{paymentId}")
    @ApiOperation(value = "支付记录详情" ,notes = "支付记录详情",response = PaymentDto.class)
    private String findOrderFinanceInfo(@PathVariable String paymentId){
        JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
        RequestResult result=this.financeFacade.findOrderFinanceInfo(paymentId);
        return jsonMapper.toJson(result);
    }
    /**
     * 查询待审批的采购单列表
     *
     * @param name
     * @param supplierId
     * @param batch
     * @param storageId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("purchases")
    private RequestResult findPurchaseList(@RequestParam(required = false) String name,
                                           @RequestParam(required = false) String supplierId,
                                           @RequestParam(required = false) String batch,
                                           @RequestParam(required = false) String storageId,
                                           @RequestParam(required = false) Integer pageNum,
                                           @RequestParam(required = false) Integer pageSize) {
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }

        MapContext mapContext = this.createMapContext(null, null, name, supplierId, batch, storageId, null, null, null,null,null,null,null);
        return this.financeFacade.findPurchaseList(mapContext, pageNum, pageSize);
    }

    /**
     * 查询待审核的经销商收支列表
     *
     * @param type
     * @param leaderTel
     * @param no
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("dealerpays")
    private RequestResult findDealerPayList(@RequestParam(required = false) Integer type,
                                            @RequestParam(required = false) String leaderTel,
                                            @RequestParam(required = false) String no,
                                            @RequestParam(required = false) Integer pageNum,
                                            @RequestParam(required = false) Integer pageSize) {

//        if (null == pageSize) {
//            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
//        }
//        if (null == pageNum) {
//            pageNum = 1;
//        }
//        MapContext mapContext = this.createMapContext(null, null, null, null, null, null, leaderTel, no, null,null,null);
//        if (type != null && type != -1) {
//            mapContext.put("type", Arrays.asList(type));
//        } else {
//            mapContext.put("type", Arrays.asList(2, 3));
//        }
//        return this.financeFacade.findDealerPayList(mapContext, pageNum, pageSize);
        return ResultFactory.generateRequestResult(new PaginatedList());
    }

    /**
     * 修改经销商支付记录状态
     *
     * @param id
     * @param type
     * @return
     */
    @PutMapping("dealerpays/{id}")
    private RequestResult updateFinancePayStatus(@PathVariable String id, @RequestParam Integer type) {
        return this.financeFacade.updateFinancePayStatus(id, type);
    }

    /**
     * 查询待审批供应商列表
     *
     * @param name
     * @param leaderTel
     * @param leaderName
     * @param no
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("suppliers")
    private RequestResult findSupplierList(@RequestParam(required = false) String name,
                                           @RequestParam(required = false) String leaderTel,
                                           @RequestParam(required = false) String leaderName,
                                           @RequestParam(required = false) String no,
                                           @RequestParam(required = false) Integer pageNum,
                                           @RequestParam(required = false) Integer pageSize) {

        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        MapContext mapContext = this.createMapContext(null, null, name, null, null, null, leaderTel, no, leaderName,null,null,null,null);
        return this.financeFacade.findSupplierList(mapContext, pageNum, pageSize);
    }

    /**
     * 审批供应商
     *
     * @param id
     * @param status
     * @return
     */
    @PutMapping("suppliers/{id}/{status}")
    private RequestResult updateSupplier(@PathVariable String id, @PathVariable Integer status) {
        return this.financeFacade.updateSupplier(id, status);
    }

    /**
     * 查询待审批的售后单列表
     *
     * @param type
     * @param no
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("aftersale")
    private RequestResult findAftersaleList(@RequestParam(required = false) Integer type,
                                            @RequestParam(required = false) String no,
                                            @RequestParam(required = false) Integer pageNum,
                                            @RequestParam(required = false) Integer pageSize) {

        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        MapContext mapContext = this.createMapContext(type, null, null, null, null, null, null, no, null,null,null,null,null);
        return this.financeFacade.findAftersaleList(mapContext, pageNum, pageSize);
    }

    /**
     * 查询待审批的经销商加盟费支付记录
     *
     * @param name
     * @param no
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("dealer")
    private RequestResult findDealerList(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) String no,
                                         @RequestParam(required = false) Integer pageNum,
                                         @RequestParam(required = false) Integer pageSize) {

        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        MapContext mapContext = this.createMapContext(null, null, name, null, null, null, null, no, null,null,null,null,null);
        return this.financeFacade.findDealerList(mapContext, pageNum, pageSize);
    }

    /**
     * 加盟费确认收款
     * @param id
     * @return
     */
    @PutMapping("/dealer/{id}")
    private RequestResult updateDealerPay(@PathVariable String id){
        return this.financeFacade.updateDealerPay(id);
    }

    /**
     * 查询需要财务审核的外协生产单
     * @param pageNum
     * @param orderNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "查询需要财务审核的外协生产单",notes = "查询需要财务审核的外协生产单")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码",name = "pageNum",dataType = "int",paramType = "query"),
            @ApiImplicitParam(value = "订单编号",name = "orderNo",dataType = "string",paramType = "query"),
            @ApiImplicitParam(value = "页数",name = "pageSize",dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "no",value = "生产编号",dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "produceState",value = "生产单状态 0 未开始 1 生产中 2 已完成",dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "pay",value = "是否已付款  true 已付款 false 未付款",dataType = "string",paramType = "query")
    })
    @GetMapping("/coordination")
    private RequestResult findCoordinationList(@RequestParam(required = false,defaultValue = "1") Integer pageNum,
                                               @RequestParam(required = false) String orderNo,
                                               @RequestParam(required = false) String no,
                                               @RequestParam(required = false) Integer produceState,
                                               @RequestParam(required = false) Boolean pay,
                                               @RequestParam(required = false,defaultValue = "10") Integer pageSize){
		MapContext mapContext = this.createProduceMapContext(no,orderNo,ProduceOrderWay.COORDINATION.getValue(),null,produceState,pay);
        List<Map<String,String>> sorts = new ArrayList<Map<String, String>>();
        Map<String,String> paySorts = new HashMap<String, String>();
        paySorts.put("is_pay","asc");
        sorts.add(paySorts);
        return this.orderFacade.findProducesList(mapContext,pageNum,pageSize,sorts);
    }

    /**
     * 财务审核外协生产单
     * @return
     */
    @ApiOperation(value = "财务审核外协生产单",notes = "财务审核外协生产单")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "外协单主键ID",name = "id",dataType = "int",paramType = "path")
    })
    @PutMapping("/coordination/{id}")
    private RequestResult updateCoordinationPay(@PathVariable String id,@RequestBody MapContext map){
        return this.financeFacade.updateCoordinationPay(id,map);
    }

    /**
     * 订单货款或者 设计费 支付记录 审核
     * @param id
     * @return
     */
    @PutMapping("/customorders/{id}")
    @ApiOperation(value = "订单货款或者 设计费 支付记录 审核",notes = "订单货款或者 设计费 支付记录 审核")
    private RequestResult updateCustomOrdersPay(@PathVariable@ApiParam(value = "支付记录主键ID")String id, @RequestBody MapContext map){
        return this.financeFacade.updateCustomOrdersPay(id,map);
    }

    @GetMapping
    @ApiOperation(value = "查询支付记录",notes = "查询支付记录")
    private RequestResult findPayemntList(@RequestParam(required = false,defaultValue = "1")Integer pageNum,@RequestParam(required = false,defaultValue = "10")Integer pageSize
    ,@RequestParam(required = false)@ApiParam(value = "状态") Integer status
    ,@RequestParam(required = false)@ApiParam(value = "订单编号") String orderNo
    ,@RequestParam(required = false)@ApiParam(value = "类型") Integer type
    ,@RequestParam(required = false)@ApiParam(value = "公司Id") String companyId
    ,@RequestParam(required = false)@ApiParam(value = "款项") Integer funds){
        MapContext mapContext = this.createMapContext(type,companyId,null,null,null,null,null,null,null,status,funds,null,orderNo);
        return this.financeFacade.findOrderFinanceList(mapContext, pageNum, pageSize);
    }

    @GetMapping("/overview")
    @ApiOperation(value = "查询财务审核统计",notes = "查询财务审核统计")
    private RequestResult findFinanceOverview(){
        return this.financeFacade.findFinanceOverview();
    }


    /**
     * 上传支付审核记录附件
     * @param id
     * @param multipartFileList
     * @return
     */
    @PostMapping("/{id}/files")
    @ApiOperation(value = "上传支付审核记录附件",notes = "上传支付审核记录附件",response = PaymentFiles.class)
    private RequestResult uploadFiles(@PathVariable@ApiParam(value = "支付记录主键ID") String id, @RequestBody List<MultipartFile> multipartFileList){
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
        return this.financeFacade.uploadFiles(id, multipartFileList);
    }

    /**
     * 删除支付记录资源文件
     * @param fileId
     * @return
     */
    @DeleteMapping("/files/{fileId}")
    @ApiOperation(value = "删除支付记录资源文件",notes = "删除支付记录资源文件")
    private RequestResult deleteFile(@PathVariable@ApiParam(value = "资源文件ID") String fileId){
        return this.financeFacade.deleteFile(fileId);
    }

	private MapContext createProduceMapContext(String no, String orderNo, Integer way, Integer type,Integer state,Boolean pay) {
		MapContext mapContext = new MapContext();
		if(no!=null&&!no.trim().equals("")){
			mapContext.put(WebConstant.STRING_NO,no);
		}
		if(orderNo!=null&&!orderNo.trim().equals("")){
			mapContext.put("orderNo",orderNo);
		}
		if(way!=null){
			mapContext.put("way",Arrays.asList(way));
		}
		if(type!=null){
			mapContext.put("type",Arrays.asList(type));
		}
		if(state!=null){
			mapContext.put(WebConstant.KEY_ENTITY_STATE,Arrays.asList(state));
		}
		if(pay!=null){
            mapContext.put("pay",pay);
        }
		return mapContext;
	}

    private MapContext createMapContext(Integer type, String companyId, String name, String supplierId, String batch, String storageId, String leaderTel, String no, String leaderName,Integer status,Integer funds,String customerTel,String orderNo) {
        MapContext mapContext = new MapContext();
        if (type != null && type != -1) {
            mapContext.put("type", type);
        }
        if (companyId != null && !companyId.trim().equals("")) {
            mapContext.put(WebConstant.KEY_ENTITY_CREATOR, companyId);
        }
        if (name != null && !name.trim().equals("")) {
            mapContext.put(WebConstant.KEY_ENTITY_NAME, name);
        }
        if (supplierId != null && !supplierId.trim().equals("")) {
            mapContext.put("supplierId", supplierId);
        }
        if (batch != null && !batch.trim().equals("")) {
            mapContext.put("batch", batch);
        }
        if (storageId != null && !storageId.trim().equals("")) {
            mapContext.put("storageId", storageId);
        }
        if (leaderTel != null && !leaderTel.trim().equals("")) {
            mapContext.put("leaderTel", leaderTel);
        }
        if (no != null && !no.trim().equals("")) {
            mapContext.put("no", no);
        }
        if (leaderName != null && !leaderName.trim().equals("")) {
            mapContext.put("leaderName", leaderName);
        }
        if(status!=null){
            mapContext.put(WebConstant.KEY_ENTITY_STATUS,status);
        }
        if (funds!=null){
            mapContext.put("fundsList",Arrays.asList(funds));
        }else{
            mapContext.put("fundsList", Arrays.asList(PaymentFunds.DESIGN_FEE_CHARGE.getValue(),PaymentFunds.ORDER_FEE_CHARGE.getValue(),PaymentFunds.COORDINATION.getValue()));
        }
        if(customerTel!=null){
            mapContext.put("customerTel",customerTel);
        }
        if(orderNo!=null){
            mapContext.put("orderNo",orderNo);
        }
        return mapContext;
    }
}
