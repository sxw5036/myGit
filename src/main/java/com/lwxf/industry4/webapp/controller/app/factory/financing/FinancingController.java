package com.lwxf.industry4.webapp.controller.app.factory.financing;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.FinanceCountDto;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.VerifyDesignPriceDto;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.VerifyOrderPriceDto;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.financing.FinanceFacade;
import com.lwxf.mybatis.utils.MapContext;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author：zhangxiaolin(3965488@qq.com)
 * @create：2019/04/01/ 13:24
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */

@Api(value="FinancingController",tags={"F端APP接口:APP财务审核管理"})
@RestController("FinancingControllerApp")
@RequestMapping(value = "/app/f/", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FinancingController {

    @Resource(name = "financeFacadeFapp")
    private FinanceFacade financeFacade;

    /**
     * 经销商财务信息首页数据接口
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功", response = FinanceCountDto.class)
    @ApiOperation(value = "经销商财务信息首页数据接口", notes = "fundsCode:DESIGN_FEE_CHARGE为设计费，ORDER_FEE_CHARGE为货款，目前列表只有这两种费用",response = FinanceCountDto.class)
    @GetMapping("/finances/index")
    private String viewIndex(HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext map = LoginUtil.checkLogin(atoken);
        if(map==null ||map.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        return jsonMapper.toJson(this.financeFacade.viewIndex());
    }

    /**
     * 查看审核订单金额（自产/货款）信息
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功")
    @ApiOperation(value = "经查看审核订单金额（自产/货款）信息", notes = "",response = VerifyOrderPriceDto.class)
    @GetMapping("/finances/{paymentId}/verifyOrderPrice")
    private String viewVerifyOrderPrice(@PathVariable String paymentId,HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext map = LoginUtil.checkLogin(atoken);
        if(map==null ||map.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        RequestResult result=this.financeFacade.viewVeiryOrderPrice(paymentId);
        return jsonMapper.toJson(result);
    }


    /**
     * 查看审核设计单金额信息
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功", response = VerifyDesignPriceDto.class)
    @ApiOperation(value = "查看审核设计单金额信息", notes = "",response = VerifyDesignPriceDto.class)
    @GetMapping("/finances/{paymentId}/verifyDesignPrice")
    private String viewVerifyDesignPrice(@PathVariable String paymentId,HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext map = LoginUtil.checkLogin(atoken);
        if(map==null ||map.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        RequestResult result=this.financeFacade.viewVeiryDesignOrderPrice(paymentId);
        return jsonMapper.toJson(result);
    }
    /**
     * 审核设计单金额信息
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功", response = VerifyDesignPriceDto.class)
    @ApiOperation(value="审核设计单金额信息.",notes="Map参数：factory_final_price(总金额，必填),notes(备注),pay_time(扣款时间)holder(审核人id),fileIds(附件id,用逗号分隔 )")
    @PutMapping("/finances/{paymentId}/verifyDesignPrice")
    private String verifyDesignPrice(@PathVariable String paymentId, @RequestBody MapContext verifyInfo, HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext map = LoginUtil.checkLogin(atoken);
        if(map==null ||map.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        if(verifyInfo.get("amount")==null || verifyInfo.get("amount").equals("")){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_NOTNULL,"订单金额"+ AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL")));
        }
        if(paymentId==null || paymentId.equals("")){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_NOTNULL,"paymentId"+ AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL")));
        }
        verifyInfo.put("paymentId",paymentId);
        if(verifyInfo.get("pay_time").equals("")){
            verifyInfo.put("pay_time",null);
        }
        RequestResult result=this.financeFacade.verifyDesignPrice(verifyInfo);
        return jsonMapper.toJson(result);
    }
    /**
     * 审核订单金额信息
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功", response = VerifyDesignPriceDto.class)
    @ApiOperation(value="审核订单金额信息",notes="Map参数：factory_final_price(总金额，必填),notes(备注),pay_time(扣款时间)holder(审核人id)")
    @PutMapping("/finances/{paymentId}/verifyOrderPrice")
    private String verifyOrderPrice(@PathVariable String paymentId,@RequestBody MapContext verifyInfo, HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext map = LoginUtil.checkLogin(atoken);
        if(map==null ||map.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        if(verifyInfo.get("amount")==null || verifyInfo.get("amount").equals("")){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_NOTNULL,"订单金额"+ AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL")));
        }
        if(paymentId==null || paymentId.equals("")){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_NOTNULL,"支付ID"+ AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL")));
        }
        verifyInfo.put("paymentId",paymentId);
        if(verifyInfo.get("pay_time").equals("")){
            verifyInfo.put("pay_time",null);
        }
        RequestResult result=this.financeFacade.verifyOrderPrice(verifyInfo);
        return jsonMapper.toJson(result);
    }


//    /**
//     * 审核外协金额信息
//     * @return
//     */
//    @ApiResponse(code = 200, message = "查询成功", response = VerifyOutsourcePriceDto.class)
//    @ApiOperation(value="审核外协金额信息",notes="")
//    @GetMapping("/verifyOutsourcePrice")
//    private String verifyOutsourcePrice(HttpServletRequest request){
//        JsonMapper jsonMapper=new JsonMapper();
//        String uid = request.getHeader("X-UID");//用户Id
//        String cid = request.getHeader("X-CID");//公司id
//        String xp = "bdealer-prepay-view";//权限验证
//        JsonMapper mapper = new JsonMapper();
////        if (!companyId.equals(cid)){
////            return mapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
////        }
////        RequestResult result = this.validUserPermission(request,xp);
//        RequestResult result=this.financeFacade.dealerAccountInfo();
//        return jsonMapper.toJson(result);
//    }

    /**
     * 日常账附件图片上传
     * @param multipartFileList 多个附件
     * @return
     */
    @ApiOperation(value="日常账附件图片上传",notes="返回值样例:{ \"code\": \"200\", \"data\": { \"ids\": [ \"4tuqvoqutzpc\", \"4tuqvovumolc\" ] } }")
    @PostMapping(value = "/finances/uploadImages")
    public RequestResult uploadImages(@RequestBody List<MultipartFile> multipartFileList, HttpServletRequest request){
        Map<String, Object> errorInfo = new HashMap<>();
        JsonMapper jsonMapper=new JsonMapper();
        String cid = request.getHeader("X-CID");//公司ID
        if(cid==null || cid.equals("")){
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_NOTNULL,"amount"+ AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
        }
        if(multipartFileList!=null && multipartFileList.size()>0) {
            for (MultipartFile multipartFile : multipartFileList) {
                if (multipartFile == null) {
                    errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
                }
                if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
                    errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
                }
                if (multipartFile.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
                    return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
                }
                if (errorInfo.size() > 0) {
                    return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR, errorInfo);
                }
            }
        }
        String uid = request.getHeader("X-UID");//用户Id
        return this.financeFacade.uploadImage(uid,cid,multipartFileList);
    }
}
