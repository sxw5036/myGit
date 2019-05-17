package com.lwxf.industry4.webapp.facade.app.factory.factoryuser.impl;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.user.UserBasisService;
import com.lwxf.industry4.webapp.bizservice.user.UserExtraService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.constant.BizConstant;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.AddressUtils;
import com.lwxf.industry4.webapp.common.utils.UserExtraUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserBasis;
import com.lwxf.industry4.webapp.domain.entity.user.UserExtra;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryuser.FactoryUserFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.user.impl.UserFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.lwxf.industry4.webapp.facade.AppBeanInjector.baseFileUploadComponent;
import static com.lwxf.industry4.webapp.facade.AppBeanInjector.i18nUtil;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/27 10:40
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factoryUserFacade")
public class FactoryUserFacadeImpl extends BaseFacadeImpl implements FactoryUserFacade {
    private Logger logger = LoggerFactory.getLogger(UserFacadeImpl.class);
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "userBasisService")
    private UserBasisService userBasisService;
    @Resource(name = "userExtraService")
    private UserExtraService userExtraService;

    /**
     * 查询个人信息
     * @param companyId
     * @param userId
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult factoryUserPersonalInfo(String companyId, String userId) {
        MapContext params = MapContext.newOne();
        params.put("companyId",companyId );
        params.put("userId",userId );
        Map map = this.userService.factoryUserPersonalInfo(params);
        if (map!=null&&map.size()>0){
            String avatar  = (String) map.get("avatar");
            if (LwxfStringUtils.isNotBlank(avatar)){
                avatar = WebUtils.getDomainUrl()+avatar;
                map.put("avatar", avatar);
            }
            String cityName = (String) map.get("cityName");
            if (LwxfStringUtils.isNotBlank(cityName)){
                String cityName1 = AddressUtils.getCityName(cityName);
                map.put("cityName", cityName1);
            }
        }
        return ResultFactory.generateRequestResult(map);
    }

    /**
     * 修改用户的信息
     * @param companyId
     * @param userId
     * @param params
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updateFactoryUserPersonalInfo(String companyId, String userId,MapContext params){

        String wechat_number = (String) params.get("wechatNumber");
        String identity_number = (String) params.get("identityNumber");
        String contact_number = (String) params.get("contactNumber");
        String address = (String) params.get("address");
        String email = (String) params.get("email");
        String name = (String) params.get("name");
        String sex = (String) params.get("sex");
        String cityAreaId = (String) params.get("cityAreaId");

        MapContext userBasisMap = MapContext.newOne();
        if (LwxfStringUtils.isNotBlank(wechat_number)){
            userBasisMap.put("wechatNumber", wechat_number);//微信
        }
        if (LwxfStringUtils.isNotBlank(identity_number)){
            userBasisMap.put("identityNumber", identity_number);//身份证
        }
        if (LwxfStringUtils.isNotBlank(contact_number)){
            userBasisMap.put("contactNumber", contact_number);//联系电话
        }
        if (LwxfStringUtils.isNotBlank(address)){
            userBasisMap.put("address", address);//地址
        }


        if (userBasisMap!=null&&userBasisMap.size()>0){
            RequestResult result1 = UserBasis.validateFields(userBasisMap);
            if (result1!=null){
                return  result1;
            }
            userBasisMap.put("userId",userId);
            this.userBasisService.updateByMapContext(userBasisMap);
        }

        MapContext userMap = MapContext.newOne();
        if (LwxfStringUtils.isNotBlank(email)){
            userMap.put("email", email);
        }
        if (LwxfStringUtils.isNotBlank(name)){
            userMap.put("name", name);
        }
        if (LwxfStringUtils.isNotBlank(sex)){
            userMap.put("sex", sex);
        }
        if (LwxfStringUtils.isNotBlank(cityAreaId)){
            userMap.put("cityAreaId", cityAreaId);
        }


        if (userMap!=null&&userMap.size()>0){
            RequestResult result = User.validateFields(userMap);
            if (result!=null){
                return result;
            }
            userMap.put("id",userId);
            this.userService.updateByMapContext(userMap);
        }

        MapContext map = MapContext.newOne();
        return ResultFactory.generateRequestResult(map);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updateAvatar(String userId, MultipartFile multipartFile) {
        MapContext userMap = MapContext.newOne();
        userMap.put(WebConstant.KEY_ENTITY_ID, userId);
        User user = userService.findById(userId);
        if (user == null) {
            String errMsg = i18nUtil.getMessage("BIZ_RES_NOT_FOUND_10001");
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_RES_NOT_FOUND_10001, errMsg);
        }
        UploadInfo uploadInfo = baseFileUploadComponent.doUploadByModule(multipartFile, UploadResourceType.AVATAR, userId);
        userMap.put("avatar", uploadInfo.getRelativePath());
        try {
            //判断是系统新的头像地址进行删除
            if (user.getAvatar().startsWith(BizConstant.EASPYM4_FILE_DOMAIN) && !user.getAvatar().equals(AppBeanInjector.configuration.getUserDefaultAvatar())) {
                baseFileUploadComponent.deleteFile(user.getAvatar(), UploadResourceType.AVATAR, 0);
            }
            this.userService.updateByMapContext(userMap);
        } catch (RuntimeException ex) {
            logger.error("更新头像失败", ex);
            baseFileUploadComponent.deleteFile(uploadInfo.getRelativePath(), UploadResourceType.AVATAR, 0);
            throw ex;
        }
        userMap.remove("id");
        return ResultFactory.generateRequestResult(userMap);
    }



    /**
     * 查询用户的账号信息
     * @param companyId
     * @param userId
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult findAccountInfo(String companyId, String userId) {

        String listDeptName = "";
        Map factoryUserAccountInfo = this.userService.findFactoryUserAccountInfo(companyId,userId);//信息
        List<MapContext> factoryUserDeptName = this.userService.findFactoryUserDeptName(companyId, userId);//部门信息
        if(null!=factoryUserDeptName&&factoryUserDeptName.size()>0){
            for (int i = 0; i<factoryUserDeptName.size();i++){
                String pullName = "";
                String deptName = factoryUserDeptName.get(i).getTypedValue("deptName",String.class);
                String organizationName = factoryUserDeptName.get(i).getTypedValue("organizationName",String.class);
                if (LwxfStringUtils.isNotBlank(organizationName)){
                    pullName = organizationName;
                }
                if (LwxfStringUtils.isNotBlank(deptName)&&LwxfStringUtils.isNotBlank(organizationName)){
                    pullName = deptName+"-"+organizationName;
                }
                listDeptName = listDeptName+";"+pullName;
            }
            int i = listDeptName.indexOf(";");
            listDeptName= listDeptName.substring(i + 1);
        }
        factoryUserAccountInfo.put("listDeptName", listDeptName);
        return ResultFactory.generateRequestResult(factoryUserAccountInfo);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updatePassword(String userId, MapContext mapContext) {
        User user = this.userService.findById(userId);
        if (user==null){
            return ResultFactory.generateErrorResult(ErrorCodes.LOGIN_FAIL_90000, AppBeanInjector.i18nUtil.getMessage("LOGIN_FAIL_90000"));
        }
        //老密码
        String oldPassword = mapContext.getTypedValue("oldPassword", String.class);
        // 密码验证不通过
        if (!AppBeanInjector.userExtraService.isPasswordCorrect(user.getId(), oldPassword)) {
            return ResultFactory.generateErrorResult(ErrorCodes.LOGIN_FAIL_90000, AppBeanInjector.i18nUtil.getMessage("LOGIN_FAIL_90000"));
        }

        //新密码
        String newPassword = mapContext.getTypedValue("newPassword", String.class);
        //确认密码
        String affirmPassword= mapContext.getTypedValue("affirmPassword", String.class);
        //判断两次密码是否一样
        if (!newPassword.equals(affirmPassword)){
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_PASSWORD_IS_NOT_CONSISTENT,AppBeanInjector.i18nUtil.getMessage("VALIDATE_PASSWORD_IS_NOT_CONSISTENT"));
        }
        //用户扩展信息
        UserExtra userExtra = new UserExtra();
        userExtra.setUserId(user.getId());
        UserExtraUtil.saltingPassword(userExtra,affirmPassword);
        userExtra.setUpdated(DateUtil.getSystemDate());
        this.userExtraService.updateUserExtra(userExtra);
        MapContext data = MapContext.newOne();
        return ResultFactory.generateRequestResult(data);
    }
}
