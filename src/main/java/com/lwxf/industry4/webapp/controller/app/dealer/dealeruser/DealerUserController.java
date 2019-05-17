package com.lwxf.industry4.webapp.controller.app.dealer.dealeruser;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.dealeruser.DealerUserFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/11/30 14:56
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class DealerUserController extends BaseDealerController {
    @Resource(name = "dealerUserFacade")
    private DealerUserFacade dealerUserFacade;
    /**
     * 每次打开app进入判断用户的禁用信息
     * @param userId
     * @return
     */
    @GetMapping(value = "/users/{userId}/isDisabled")
    public RequestResult isDisabled(@PathVariable String userId){
        RequestResult disabled = this.dealerUserFacade.isDisabled(userId);
        return disabled;
    }

    /**
     * 验证验证码是否正确
     * @param params
     * @return
     */
    @PostMapping(value = "/users/authCode")
    public RequestResult authCode(@RequestBody MapContext params){
        RequestResult result = this.dealerUserFacade.authCode(params);
        return result;
    }

    /**
     * 忘记密码
     * @param params
     * @return
     */
    @PostMapping(value = "/users/forgetPassword")
    public RequestResult forgetPassword(@RequestBody MapContext params){
        return this.dealerUserFacade.forgetPassword(params);
    }


    /**
     * 用户个人信息展
     *
     * @param userId
     * @return
     */
    @GetMapping(value = "/users/{userId}")
    public String selectPersonByUserId(@PathVariable String userId,
                                       HttpServletRequest request) {

        String xp = "usersetmng-userinfo-view";//权限验证
        JsonMapper mapper = new JsonMapper();

        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return mapper.toJson(result1);
        }
        RequestResult result = this.dealerUserFacade.selectPersonByUserId(userId);
        return mapper.toJson(result);
    }

    /**
     * 修改个人信息展
     *
     * @param userMap
     * @param userId
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/users/{userId}")
    public RequestResult updatePersonByMap(@RequestBody MapContext userMap,
                                           @PathVariable String userId,
                                           @PathVariable String companyId,
                                           HttpServletRequest request) {

        String cid = request.getHeader("X-CID");//公司id
        String xp = "usersetmng-userinfo-edit";//权限验证
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }

        RequestResult result = User.validateFields(userMap);
        if (null!=result){
            return  result;
        }
        userMap.put(WebConstant.KEY_ENTITY_ID, userId);
        return this.dealerUserFacade.updatePersonByMap(userMap);
    }

    /**
     * 修改个人头像
     *
     * @param userId
     * @param multipartFile
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/users/{userId}/avatar")
    public RequestResult updateAvatar(@PathVariable String userId,
                                      @RequestBody MultipartFile multipartFile,
                                      HttpServletRequest request) {

        Map<String, Object> errorInfo = new HashMap<>();
        if (multipartFile == null) {
            errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
        } else if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
            errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
        } else if (multipartFile.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
        }
        if (errorInfo.size() > 0) {
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
        }
        String xp = "usersetmng-userinfo-edit";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        return this.dealerUserFacade.updateAvatar(userId, multipartFile);
    }


    /**
     *  查询公司店铺下的员工姓名和id
     *  （为了根据员工检索订单的时候用）
     * @param companyId
     * @param request
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/emp")
    public RequestResult findEmpByCid(@PathVariable String companyId, HttpServletRequest request){
        String cid = request.getHeader("X-CID");//公司id
        String xp = "bempl-baseInfo-view";//权限验证
        if (!companyId.equals(cid)){
            return ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        RequestResult requestResult = this.validUserPermission(request,xp);
        if (null!=requestResult){
            return requestResult;
        }

        MapContext params = MapContext.newOne();
        params.put("companyId",companyId);
        params.put("status",null);
        RequestResult empIdAndEmpNameByCid = this.dealerUserFacade.findEmpIdAndEmpNameByCid(params);
        return empIdAndEmpNameByCid;
    }


    /**
     * 查询公司下的在职员工
     * @param companyId
     * @param request
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/inservice/emp")
    public RequestResult findInServiceEmpByCid(@PathVariable String companyId,
                                               HttpServletRequest request){

        String cid = request.getHeader("X-CID");//公司id
        String xp = "bempl-baseInfo-view";//权限验证
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }

        RequestResult requestResult = this.validUserPermission(request,xp);
        if (null!=requestResult){
            return requestResult;
        }
        MapContext params = MapContext.newOne();
        params.put("companyId",companyId);
        params.put("status",0);
        RequestResult empIdAndEmpNameByCid = this.dealerUserFacade.findEmpIdAndEmpNameByCid(params);
        return empIdAndEmpNameByCid;
    }


    /**
     * 查询公司下的所有的客户信息（根据姓名）
     * @param companyId
     * @param name
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/allcustomers")
    public String findCustomerByCompanyIdAndCustomer(@PathVariable String companyId,
                                                     @RequestParam String name,
                                                     HttpServletRequest request){
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司id
        JsonMapper mapper = new JsonMapper();
        if (!companyId.equals(cid)){
            return  mapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
        String xp = "bcustomermng-custombase-view";
        RequestResult requestResult = this.validUserPermission(request,xp);
        if (null!=requestResult){
            return mapper.toJson(requestResult);
        }

        RequestResult result = this.dealerUserFacade.findCustomerByCompanyIdAndCustomer(companyId, name,uid);
        return mapper.toJson(result);
    }






}

