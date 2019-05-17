package com.lwxf.industry4.webapp.facade.app.dealer.addressbook.impl;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.commons.utils.ValidateUtils;
import com.lwxf.industry4.webapp.bizservice.company.PersonalAddressbookService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.company.CompanyShareMemberIdentity;
import com.lwxf.industry4.webapp.common.enums.company.CompanyShareMemberStatus;
import com.lwxf.industry4.webapp.common.enums.company.EmployeeStatus;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.dto.user.UserAreaDto;
import com.lwxf.industry4.webapp.domain.entity.company.PersonalAddressbook;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.addressbook.AddressBookFacade;
import com.lwxf.industry4.webapp.facade.app.dealer.company.ShareMemberFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/7 13:56
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("addressBookFacade")
public class AddressBookFacadeImpl extends BaseFacadeImpl implements AddressBookFacade {
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "personalAddressbookService")
    private PersonalAddressbookService personalAddressbookService;
    @Resource(name = "shareMemberFacade")
    private ShareMemberFacade shareMemberFacade;



    /**
     * 根据条件查询公司下的员工的电话，共享人员的电话，以及业务人员自己单独的客户电话
     * @param companyId
     * @param status
     * @return
     */
    @Override
    public RequestResult findTelBookList(String companyId,Integer status,
                                         Integer type,Integer identity,
                                         Pagination pagination,String uid,String condition) {
        PaginatedFilter filter = new PaginatedFilter();
        filter.setPagination(pagination);
        MapContext params  = MapContext.newOne();
        params.put("companyId",companyId);
        if (LwxfStringUtils.isNotBlank(condition)){
            params.put("condition", condition);
        }
        if (null!=type&&type==1){
            if (null!=status){
                params.put("status",status);
            }else {
                params.put("status",EmployeeStatus.NORMAL.getValue());
            }
            filter.setFilters(params);
            //所有员工的通讯录（根据状态可以查看 0-正常 1-禁用 2-离职）
            List<User> EmpList = this.userService.findEmpInfoByCompanyIdAndStatus(filter).getRows();
            return  ResultFactory.generateRequestResult(EmpList);
        }

        if (null!=type&&type==2){
            if (null!=status){
                params.put("status",status);
            }else {
                params.put("status",CompanyShareMemberStatus.APPROVAL_PENDING.getValue());
            }
            if (null!=identity){
                params.put("identity",identity);
            }else {
                params.put("identity",CompanyShareMemberIdentity.ERECTOR.getValue());
            }
            filter.setFilters(params);
            //查看共享人员的电话
            List<UserAreaDto> shareMemberList = this.userService.findShareMemberByPidAndStatusAndIdentity(filter).getRows();
            return  ResultFactory.generateRequestResult(shareMemberList);
        }

        if (null!=type&&type==3){
            params.put("creator",uid);
            params.put("cid",companyId);
            filter.setFilters(params);
            //查询自己的客户
            List<PersonalAddressbook> addressBookList = this.personalAddressbookService.selectByFilter(filter).getRows();
            for (PersonalAddressbook personalAddressbook:addressBookList){
                personalAddressbook.setCover("");
            }
            return ResultFactory.generateRequestResult(addressBookList);
        }
        return ResultFactory.generateResNotFoundResult();
    }


    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addTelBook(String companyId, MapContext mapContext, HttpServletRequest request) {
        Integer type = mapContext.getTypedValue("type", Integer.class);
        String name = mapContext.getTypedValue("name", String.class);
        String notes = mapContext.getTypedValue("notes", String.class);
        String companyName = mapContext.getTypedValue("companyName", String.class);
        //type==1，表示保存的是安装工
        if (type!=null&&type==1){
             this.shareMemberFacade.addShareMember(companyId, mapContext, request);
        }
        //type==1，表示保存的是个人的顾客
        if(type!=null&&type==2){
            String uid = request.getHeader("X-UID");
            Map<String, String> errorMap = new HashMap<>();
            String mobile=mapContext.getTypedValue("mobile",String.class);
            //验证电话是否正确
            if (!ValidateUtils.isChinaPhoneNumber(mobile)) {
                errorMap.put(WebConstant.KEY_ENTITY_MOBILE, AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
                return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, mobile);
            }
            PersonalAddressbook personalAddressbook = new PersonalAddressbook();
            MapContext params = MapContext.newOne();
            params.put("companyId",companyId);
            params.put("uid",uid);
            params.put("mobile",mobile);
            PersonalAddressbook Addressbook = this.personalAddressbookService.findByCidAndUidAndMobile(params);
            if (Addressbook!=null){
                return ResultFactory.generateErrorResult(ErrorCodes.BIZ_USER_HAS_BEEN_ADDED_10056,AppBeanInjector.i18nUtil.getMessage("BIZ_USER_HAS_BEEN_ADDED_10056"));
            }
            personalAddressbook.setMobile(mobile);
            personalAddressbook.setCompanyId(companyId);
            personalAddressbook.setCreated(DateUtil.getSystemDate());
            personalAddressbook.setCreator(uid);
            personalAddressbook.setName(name);
            personalAddressbook.setNotes(notes);
            personalAddressbook.setCompanyName(companyName);
            RequestResult result = personalAddressbook.validateFields();
            if (null!=result){
                return  result;
            }
            this.personalAddressbookService.add(personalAddressbook);
        }
        return  ResultFactory.generateRequestResult(mapContext);
    }
}
