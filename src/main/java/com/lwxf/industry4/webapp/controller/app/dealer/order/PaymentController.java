package com.lwxf.industry4.webapp.controller.app.dealer.order;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.order.PaymentFacade;
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
 * @create：2018/12/19 9:47
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class PaymentController extends BaseDealerController {

    @Resource(name = "paymentFacade")
    private PaymentFacade paymentFacade;

    /**
     * 添加订单支付
     * @param orderId
     * @param
     * @param request
     * @param companyId
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/customorders/{orderId}/payments")
    public RequestResult addPayment(@PathVariable String orderId,
                                    @RequestBody MapContext params,
                                    HttpServletRequest request,
                                    @PathVariable String companyId){
        String userId = request.getHeader("X-UID");
        String cid = request.getHeader("X-CID");
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "borderpaymentmng-paymentinfo-edit";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
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
        return this.paymentFacade.addPayment(payment,userId,companyId);
    }


    /**
     * 查询订单支付列表
     * @param orderId
     * @param request
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/customorders/{orderId}/payments")
    public RequestResult findByOrderId(@PathVariable String orderId,
                                       HttpServletRequest request,
                                       @PathVariable String companyId){
        String cid = request.getHeader("X-CID");//公司id
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "borderpaymentmng-paymentinfo-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        return this.paymentFacade.findByOrderId(orderId);
    }


    /**
     * 查询订单支付的详情和图片
     * @param orderId
     * @param paymentId
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/customorders/{orderId}/payments/{paymentId}")
    public  String findByPaymentId(@PathVariable String orderId,
                                          @PathVariable String paymentId,
                                          @PathVariable String companyId,
                                   HttpServletRequest request){

        String cid = request.getHeader("X-CID");//公司id

        JsonMapper mapper = new JsonMapper();
        if (!companyId.equals(cid)){
            return  mapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
        String xp = "borderpaymentmng-paymentinfo-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return mapper.toJson(result1);
        }
        RequestResult result = this.paymentFacade.findByPaymentId(orderId, paymentId);
        return mapper.toJson(result);
    }


    /**
     * 上传订单支付文件
     * @param multipartFiles
     * @return
     */
    @PostMapping("/companies/{companyId}/customorders/{orderId}/payments/{paymentId}/files")
    public RequestResult addOrderPaymentFiles(@RequestBody List<MultipartFile> multipartFiles,
                                         @PathVariable String orderId,
                                         @PathVariable String paymentId,
                                         @PathVariable String companyId,
                                         HttpServletRequest request){

        Map<String, Object> errorInfo = new HashMap<>();

        if (multipartFiles == null||multipartFiles.size()==0) {
            errorInfo.put("multipartFiles", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
        }

        for (MultipartFile multipartFile:multipartFiles) {
            if (multipartFile==null){
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
        String userId = request.getHeader("X-UID");
        String cid = request.getHeader("X-CID");
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }

        return this.paymentFacade.addPaymentFiles(multipartFiles,paymentId,userId);
    }



    /**
     * 充值/提现上传图片
     * @param multipartFiles
     * @return
     */
    @PostMapping("/companies/{companyId}/payments/{paymentId}/files")
    public RequestResult addPaymentFiles(@RequestBody List<MultipartFile> multipartFiles,
                                         @PathVariable String paymentId,
                                         @PathVariable String companyId,
                                         HttpServletRequest request){

        Map<String, Object> errorInfo = new HashMap<>();

        if (multipartFiles == null||multipartFiles.size()==0) {
            errorInfo.put("multipartFiles", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
        }

        for (MultipartFile multipartFile:multipartFiles) {
            if (multipartFile==null){
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
        String userId = request.getHeader("X-UID");
        String cid = request.getHeader("X-CID");
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }

        return this.paymentFacade.addPaymentFiles(multipartFiles,paymentId,userId);
    }










    /**
     * B端经销商审核C端的付款(确认收款)
     * @param request
     * @param companyId
     * @param orderId
     * @param paymentId
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/customorders/{orderId}/payments/{paymentId}")
    public RequestResult BAuditPayment(HttpServletRequest request,
                                       @PathVariable String companyId,
                                       @PathVariable String orderId,
                                       @PathVariable String paymentId){
        String uid = request.getHeader("X-UID");
        String cid = request.getHeader("X-CID");

        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "borderpaymentmng-paymentinfo-update_status";
        RequestResult result = this.validUserPermission(request,xp);
        if (null!=result){
            return result;
        }
        return this.paymentFacade.BAuditPayment(uid,paymentId);
    }

}

