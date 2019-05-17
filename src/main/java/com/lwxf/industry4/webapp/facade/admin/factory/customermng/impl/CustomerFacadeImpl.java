package com.lwxf.industry4.webapp.facade.admin.factory.customermng.impl;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.ValidateUtils;
import com.lwxf.industry4.webapp.baseservice.cache.constant.RedisConstant;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.customer.CompanyCustomerService;
import com.lwxf.industry4.webapp.bizservice.user.UserBasisService;
import com.lwxf.industry4.webapp.bizservice.user.UserExtraService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.user.UserThirdInfoService;
import com.lwxf.industry4.webapp.common.authcode.AuthCodeUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.company.CompanyCustomerGrade;
import com.lwxf.industry4.webapp.common.enums.company.CompanyCustomerSource;
import com.lwxf.industry4.webapp.common.enums.company.CompanyCustomerStatus;
import com.lwxf.industry4.webapp.common.enums.user.*;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.AddressUtils;
import com.lwxf.industry4.webapp.common.utils.UserExtraUtil;
import com.lwxf.industry4.webapp.domain.dto.user.UserAreaDto;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.customer.CompanyCustomer;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserBasis;
import com.lwxf.industry4.webapp.domain.entity.user.UserExtra;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.customermng.CustomerFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/1/15 10:11
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("fCustomerFacade")
public class CustomerFacadeImpl extends BaseFacadeImpl implements CustomerFacade {
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name="companyCustomerService")
    private CompanyCustomerService companyCustomerService;
    @Resource(name="userExtraService")
    private UserExtraService userExtraService;
    @Resource(name = "userThirdInfoService")
    private UserThirdInfoService userThirdInfoService;
    @Resource(name="companyService")
    private CompanyService companyService;
    @Resource(name="userBasisService")
    private UserBasisService userBasisService;
    @Resource(name="companyEmployeeService")
    private CompanyEmployeeService companyEmployeeService;

    @Override
    public RequestResult findByClient(String companyId,String condition,Integer pageSize,Integer pageNum) {
        PaginatedFilter filter = PaginatedFilter.newOne();
        Pagination pagination = Pagination.newOne();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        MapContext params = MapContext.newOne();
        params.put("type",UserType.CLIENT.getValue());
        params.put("companyId",companyId);
        params.put("condition",condition);
        filter.setFilters(params);
        filter.setPagination(pagination);
        PaginatedList<UserAreaDto> userList = this.userService.findByClient(filter);
        List<UserAreaDto> rows = userList.getRows();
        for (UserAreaDto u :rows){
            String cityAreaName = u.getCityAreaName();
            String address = u.getAddress();
            String address1 = AddressUtils.getAddress(cityAreaName, address);
            u.setCityAreaName(address1);
        }
        return ResultFactory.generateRequestResult(userList);
    }


    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addCustomer(String companyId, MapContext mapContext, String uid) {
        //用户表基本信息
        String mobile=(String) mapContext.get("mobile");
        List<CompanyEmployee> companyEmployees = this.companyEmployeeService.selectShopkeeperByCompanyIds(Arrays.asList(companyId));
        if (null==companyEmployees||companyEmployees.size()!=1){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_USER_NOT_FOUND_10002,AppBeanInjector.i18nUtil.getMessage("BIZ_USER_NOT_FOUND_10002"));
        }
        String customerManager = companyEmployees.get(0).getUserId();
        User u = userService.findByMobile(mobile);
        //验证电话号码是否正确
        if (!ValidateUtils.isChinaPhoneNumber(mobile)) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorMap);
        }
        //判断用户是否存在
        if (null!=u){

            //判断是否在公司内
            CompanyCustomer companyCus=this.companyCustomerService.selectCustomerByCUId(companyId,u.getId());
            if(companyCus==null){
                //把客户添加到公司下
                CompanyCustomer companyCustomer=new CompanyCustomer();
                String id = companyId;
                companyCustomer.setCompanyId(id);
                companyCustomer.setStatus(CompanyCustomerStatus.CREATE.getValue());
                companyCustomer.setCreator(uid);
                companyCustomer.setCreated(DateUtil.getSystemDate());
                companyCustomer.setCustomerManager(customerManager);
                companyCustomer.setGrade(CompanyCustomerGrade.LOW.getValue());
                companyCustomer.setUserId(u.getId());
                companyCustomer.setSource(CompanyCustomerSource.CSOCKET.getValue());
                companyCustomer.setCommunity((String)mapContext.get("community"));
                RequestResult result = companyCustomer.validateFields();
                if(result!=null){
                    return result;
                }
                this.companyCustomerService.add(companyCustomer);
                return ResultFactory.generateSuccessResult();
            }
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_USER_HAS_BEEN_ADDED_10056,AppBeanInjector.i18nUtil.getMessage("BIZ_USER_HAS_BEEN_ADDED_10056"));
        }

        User user=new User();
        user.setType(UserType.CLIENT.getValue());
        user.setName((String)mapContext.get("name"));
        user.setSex(UserSex.MAN.getValue());
        user.setMobile(mobile);
        user.setEmail((String)mapContext.get("email"));
        user.setAvatar(AppBeanInjector.configuration.getUserDefaultAvatar());
        user.setTimeZone(WebConstant.TIMEZONE_DEFAULT);
        user.setLanguage(WebConstant.LANGUAGE_DEFAULT);
        user.setCityAreaId((String)mapContext.get("cityAreaId"));
        user.setCreated(DateUtil.getSystemDate());
        user.setState(UserState.ENABLED.getValue());
        user.setLoginName(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.USER_LOGNAME_NO));
        user.setFollowers(0);
        user.setChangedLoginName(false);
        RequestResult result = user.validateFields();
        if (null!=result){
            return result;
        }
        this.userService.add(user);

        //客户与经销商绑定
        CompanyCustomer companyCustomer=new CompanyCustomer();
        companyCustomer.setCompanyId(companyId);
        companyCustomer.setStatus(CompanyCustomerStatus.CREATE.getValue());
        companyCustomer.setCreator(uid);
        companyCustomer.setCreated(DateUtil.getSystemDate());
        if(customerManager!=null){
            companyCustomer.setCustomerManager(customerManager);
        }else {
            companyCustomer.setCustomerManager(uid);
        }
        companyCustomer.setGrade(CompanyCustomerGrade.LOW.getValue());
        companyCustomer.setUserId(user.getId());
        companyCustomer.setSource(CompanyCustomerSource.CSOCKET.getValue());
        companyCustomer.setCommunity((String)mapContext.get("community"));
        companyCustomer.setAddress((String)mapContext.get("address"));
        companyCustomer.setCityAreaId((String)mapContext.get("cityAreaId"));
        this.companyCustomerService.add(companyCustomer);
        //用户基础扩展信息
        String userId=user.getId();
        UserBasis userBasis=new UserBasis();
        userBasis.setUserId(userId);
        userBasis.setAddress((String)mapContext.get("address"));
        userBasis.setEducation(EducationType.UNDERGRADUATE.getValue());
        userBasis.setIncome(IncomeType.FOUR.getValue());
        userBasis.setWork((String)mapContext.get("work"));
        userBasis.setWorkUnit((String)mapContext.get("workUnit"));
        userBasis.setPoliticalOutlook(UserPoliticalOutlookType.MASSES.getValue());
        this.userBasisService.add(userBasis);
        //用户扩展信息
        UserExtra userExtra = new UserExtra();
        userExtra.setUserId(user.getId());
        String pwd = AuthCodeUtils.getRandomNumberCode(6);
        UserExtraUtil.saltingPassword(userExtra,new Md5Hash(pwd).toString());
        this.userExtraService.add(userExtra);

        // 第三方账号信息
        UserThirdInfo userThirdInfo = new UserThirdInfo();
        userThirdInfo.setWxNickname(user.getMobile());
        userThirdInfo.setWxIsBind(Boolean.FALSE);
        userThirdInfo.setWxIsSubscribe(Boolean.FALSE);
        userThirdInfo.setEmailIsBind(Boolean.FALSE);
        userThirdInfo.setMobileIsBind(Boolean.FALSE);
        userThirdInfo.setUserId(user.getId());
        userThirdInfo.setAppToken(UserExtraUtil.generateAppToken(userExtra,null));
        AppBeanInjector.redisUtils.hPut(RedisConstant.PLATFORM_TAG, user.getId(), Integer.valueOf(1));
        this.userThirdInfoService.add(userThirdInfo);

        UserAreaDto userDto = this.userService.findByUid(user.getId());
        if (null!=userDto){
            String cityAreaName = userDto.getCityAreaName();
            String address = userDto.getAddress();
            String address1 = AddressUtils.getAddress(cityAreaName, address);
            userDto.setCityAreaName(address1);
        }
        userDto.setCustomerManagerName(this.userService.findById(userDto.getCustomerManager()).getName());
        return ResultFactory.generateRequestResult(userDto);



    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updateClient(String cid, MapContext mapContext) {
        //判断客户是否存在
        User client = this.userService.findById(cid);
        if(client==null||!client.getType().equals(UserType.CLIENT.getValue())){
            return ResultFactory.generateResNotFoundResult();
        }
        //如果修改手机号判断手机是否存在
        String mobile = mapContext.getTypedValue("mobile", String.class);
        if(mobile!=null){
            //验证电话号码是否正确
            if (!ValidateUtils.isChinaPhoneNumber(mobile)) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
                return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR, mobile);
            }
            User byMobile = this.userService.findByMobile(mobile);
            if(byMobile!=null&&!byMobile.getId().equals(cid)){
                MapContext result = new MapContext();
                result.put("mobile",AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
                return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,result);
            }
        }
        mapContext.put(WebConstant.KEY_ENTITY_ID,cid);
        this.userService.updateByMapContext(mapContext);
        return ResultFactory.generateSuccessResult();
    }

}

