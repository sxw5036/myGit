package com.lwxf.industry4.webapp.facade.app.dealer.dealeruser.impl;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.baseservice.sms.yunpian.SmsUtil;
import com.lwxf.industry4.webapp.baseservice.sms.yunpian.VerificationCodeType;
import com.lwxf.industry4.webapp.bizservice.common.CityAreaService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.dept.DeptService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserExtraService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.constant.BizConstant;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.company.CompanyStatus;
import com.lwxf.industry4.webapp.common.enums.company.DealerEmployeeRole;
import com.lwxf.industry4.webapp.common.enums.company.EmployeeStatus;
import com.lwxf.industry4.webapp.common.enums.user.UserState;
import com.lwxf.industry4.webapp.common.enums.user.UserType;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.AddressUtils;
import com.lwxf.industry4.webapp.common.utils.UserExtraUtil;
import com.lwxf.industry4.webapp.domain.dto.user.UserAreaDto;
import com.lwxf.industry4.webapp.domain.dto.user.UserDto;
import com.lwxf.industry4.webapp.domain.entity.common.CityArea;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.org.Dept;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserExtra;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.dealeruser.DealerUserFacade;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lwxf.industry4.webapp.facade.AppBeanInjector.baseFileUploadComponent;
import static com.lwxf.industry4.webapp.facade.AppBeanInjector.i18nUtil;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/11/30 15:01
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("dealerUserFacade")
public class DealerUserFacadeImpl extends BaseFacadeImpl implements DealerUserFacade {
    private Logger logger = LoggerFactory.getLogger(UserFacadeImpl.class);
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "companyEmployeeService")
    private CompanyEmployeeService companyEmployeeService;
    @Resource(name = "deptService")
    private DeptService deptService;
    @Resource(name = "roleService")
    private RoleService roleService;
    @Resource(name = "cityAreaService")
    private CityAreaService cityAreaService;
    @Resource(name = "companyService")
    private CompanyService companyService;
    @Resource(name = "userExtraService")
    private UserExtraService userExtraService;

    @Override
    public RequestResult isDisabled(String userId) {
        int i = 0;
        MapContext data = MapContext.newOne();
        data.put("value",i);
        User user = AppBeanInjector.userService.findById(userId);
        if (user == null) {
            return ResultFactory.generateRequestResult(data);
        }
        //判断用户是否被禁用
        if (user.getState().intValue() == UserState.DISABLED.getValue()) {
            return ResultFactory.generateRequestResult(data);
    }
        //判断用户是否是B端的员工
        if (user.getType().intValue()==UserType.FACTORY.getValue()){
            return ResultFactory.generateRequestResult(data);
        }

        //判断B端的员工是否被禁用
        CompanyEmployee employee = this.companyEmployeeService.findCompanyByUidAndStatus(userId,EmployeeStatus.NORMAL.getValue());
        if (null==employee){
            return ResultFactory.generateRequestResult(data);
        }
        String companyId = employee.getCompanyId();
        Company company = this.companyService.findById(companyId);
        //判断经销商的公司是否被禁用
        if (company.getStatus().intValue()!=CompanyStatus.NORMAL.getValue()){
            return ResultFactory.generateRequestResult(data);
    }
        i = 1;
        data.put("value",i);
        return ResultFactory.generateRequestResult(data);
    }

    /**
     * 验证验证码是否正确
     * @param params
     * @return
     */
    @Override
    public RequestResult authCode(MapContext params){
        Map<String, String> errorMap = new HashMap<>();
        String loginName = params.getTypedValue(WebConstant.KEY_ENTITY_LOGINNAME, String.class);
        //页面短信验证码
        String mobileCode = params.getTypedValue("smsCode", String.class);
        //从缓存中取短信验证码
        String serverMobileCode = SmsUtil.getMobileVerificationCode(loginName, VerificationCodeType.UPDATE_PASSWORD);
        if (serverMobileCode == null || !serverMobileCode.equals(mobileCode)) {
            errorMap.put("smsCode", AppBeanInjector.i18nUtil.getMessage("VALIDATE_AUTHCODE_ERROR_20024"));
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorMap);
        }
        MapContext data = MapContext.newOne();
        return ResultFactory.generateRequestResult(data);
    }


    /**
     * 忘记密码
     * @param params
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult forgetPassword(MapContext params) {
        String loginName = params.getTypedValue(WebConstant.KEY_ENTITY_LOGINNAME, String.class);
        //新密码
        String newPassword = params.getTypedValue("newPassword", String.class);
        //确认密码
        String affirmPassword= params.getTypedValue("affirmPassword", String.class);
        if (!newPassword.equals(affirmPassword)){
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_PASSWORD_IS_NOT_CONSISTENT,AppBeanInjector.i18nUtil.getMessage("VALIDATE_PASSWORD_IS_NOT_CONSISTENT"));
        }

        User user = AppBeanInjector.userService.findByMobile(loginName);
        if (user == null) {
            return ResultFactory.generateErrorResult(ErrorCodes.LOGIN_FAIL_90000, AppBeanInjector.i18nUtil.getMessage("LOGIN_FAIL_90000"));
        }
        //用户扩展信息
        UserExtra userExtra = new UserExtra();
        userExtra.setUserId(user.getId());
        UserExtraUtil.saltingPassword(userExtra,new Md5Hash(affirmPassword).toString());
        userExtra.setUpdated(DateUtil.getSystemDate());
        this.userExtraService.updateUserExtra(userExtra);
        MapContext data = MapContext.newOne();
        return ResultFactory.generateRequestResult(data);
    }

    @Override
    public RequestResult selectPersonByUserId(String userId) {
        User user = this.userService.findById(userId);
        if (null==user){
            return ResultFactory.generateResNotFoundResult();
        }
        UserDto userDto = new UserDto();
        String cityAreaId = user.getCityAreaId();
        String cityAreaName = "";
        if (null != cityAreaId) {
            CityArea cityArea = this.cityAreaService.findById(cityAreaId);
            if (null != cityArea) {
                String mergerName = cityArea.getMergerName();
                int i = mergerName.indexOf(",");
                cityAreaName = mergerName.substring(i + 1);
            }
        }

        userDto.setCityAreaName(cityAreaName);
        userDto.setAvatar(user.getAvatar());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setCreated(user.getCreated());
        userDto.setState(user.getState());
        userDto.setSex(user.getSex());
        userDto.setCityAreaId(cityAreaId);
        userDto.setMobile(user.getMobile());
        MapContext result = MapContext.newOne();
        CompanyEmployee companyEmployee = this.companyEmployeeService.selectByUserId(userId);
        if (null==companyEmployee){
            return ResultFactory.generateResNotFoundResult();
        }
        String empId = companyEmployee.getId();
        String roleId = companyEmployee.getRoleId();
        Role role = this.roleService.findById(roleId);
        if (null==role){
            result.put("role","");

        }else {

            result.put("role", role.getName());
        }
        Dept dept = this.deptService.selectByEmpId(empId);
        if (null==dept){
            result.put("dept","");

        }else {

            result.put("dept", dept.getName());
        }

        result.put("user", userDto);
        result.put("no", companyEmployee.getNo());

        return ResultFactory.generateRequestResult(result);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updatePersonByMap(MapContext userMap) {
        String email = (String) userMap.get("email");
        User u = this.userService.findByEmail(email);
        if (null!=u){
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_EMAIL_HAVE_EXISTED,AppBeanInjector.i18nUtil.getMessage("VALIDATE_EMAIL_HAVE_EXISTED"));
        }
        this.userService.updateByMapContext(userMap);
        MapContext data = MapContext.newOne();
        return ResultFactory.generateRequestResult(data);
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

    @Override
    public RequestResult findEmpIdAndEmpNameByCid(MapContext params) {
        List<Map<String, String>> empIdAndEmpNameByCid = this.userService.findEmpIdAndEmpNameByCid(params);

        if (null==empIdAndEmpNameByCid||empIdAndEmpNameByCid.size()==0){
            return ResultFactory.generateRequestResult(empIdAndEmpNameByCid);
        }
        return ResultFactory.generateRequestResult(empIdAndEmpNameByCid);
    }


    @Override
    public RequestResult findCustomerByCompanyIdAndCustomer(String companyId, String name,String creator) {
        MapContext mapContext = MapContext.newOne();
        Role role = this.roleService.findRoleByCidAndUid(creator, companyId);
        if (null!=role&&role.getKey().equals(DealerEmployeeRole.CLERK.getValue())){
            mapContext.put("creator",creator);
        }
        mapContext.put("companyId",companyId);
        mapContext.put("name",name);

        List<UserAreaDto> userAreaDtoList = this.userService.findCustomerByCompanyIdAndCustomer(mapContext);
        for (UserAreaDto userAreaDto :userAreaDtoList){
            String cityAreaName = userAreaDto.getCityAreaName();
            String address1 = userAreaDto.getAddress();
            String address = AddressUtils.getAddress(cityAreaName, address1);
            userAreaDto.setCityAreaName(address);
        }
        return ResultFactory.generateRequestResult(userAreaDtoList);
    }
}

