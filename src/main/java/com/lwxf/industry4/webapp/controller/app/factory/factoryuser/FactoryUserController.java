package com.lwxf.industry4.webapp.controller.app.factory.factoryuser;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryuser.FactoryUserFacade;
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
 * @create：2019/3/26 13:14
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/f",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryUserController {

    @Resource(name = "factoryUserFacade")
    private FactoryUserFacade factoryUserFacade;


    /**
     * 工厂端查询个人信息
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/users/{userId}")
    public String factoryUserPersonalInfo(@PathVariable String companyId,
                                          @PathVariable String userId,
                                          HttpServletRequest request){
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.factoryUserFacade.factoryUserPersonalInfo(companyId, userId);
        return mapper.toJson(result);
    }


    /**
     * 修改用户的个人信息
     * @param companyId
     * @param userId
     * @param request
     * @param params
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/users/{userId}")
    public RequestResult updateFactoryUserPersonalInfo(@PathVariable String companyId,
                                                       @PathVariable String userId,
                                                       HttpServletRequest request,
                                                       @RequestBody MapContext params){
        RequestResult result = this.factoryUserFacade.updateFactoryUserPersonalInfo(companyId, userId,params);
        return result;
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
//        String xp = "usersetmng-userinfo-edit";
//        RequestResult result1 = this.validUserPermission(request,xp);
//        if (null!=result1){
//            return result1;
//        }
        return this.factoryUserFacade.updateAvatar(userId, multipartFile);
    }


    /**
     * 查看账户信息
     * @param companyId
     * @param userId
     * @param request
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/users/{userId}/accountInfo")
    public String findAccountInfo(@PathVariable String companyId,
                                  @PathVariable String userId,
                                  HttpServletRequest request){

        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.factoryUserFacade.findAccountInfo(companyId, userId);
        return mapper.toJson(result);
    }

    /**
     * 修改密码
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/users/{userId}/putpassword")
    public RequestResult updatePassword(@PathVariable String companyId,
                                        @PathVariable String userId,
                                        HttpServletRequest request,
                                        @RequestBody MapContext mapContext){

        RequestResult result = this.factoryUserFacade.updatePassword(userId, mapContext);
        return result;
    }


}
