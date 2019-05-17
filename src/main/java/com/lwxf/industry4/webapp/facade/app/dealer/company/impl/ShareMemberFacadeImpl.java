package com.lwxf.industry4.webapp.facade.app.dealer.company.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.ValidateUtils;
import com.lwxf.industry4.webapp.bizservice.company.CompanyShareMemberService;
import com.lwxf.industry4.webapp.bizservice.user.UserBasisService;
import com.lwxf.industry4.webapp.bizservice.user.UserExtraService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.user.UserThirdInfoService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.company.CompanyShareMemberIdentity;
import com.lwxf.industry4.webapp.common.enums.company.CompanyShareMemberStatus;
import com.lwxf.industry4.webapp.common.enums.user.UserSex;
import com.lwxf.industry4.webapp.common.enums.user.UserState;
import com.lwxf.industry4.webapp.common.enums.user.UserType;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.domain.dto.company.companyShareMember.CompanyShareMemberDto;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyShareMember;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.company.ShareMemberFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/10 0010 15:30
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("shareMemberFacade")
public class ShareMemberFacadeImpl extends BaseFacadeImpl implements ShareMemberFacade {
	@Resource(name="companyShareMemberService")
	private CompanyShareMemberService companyShareMemberService;
	@Resource(name="userService")
	private UserService userService;
	@Resource(name="userExtraService")
	private UserExtraService userExtraService;
	@Resource(name = "userThirdInfoService")
	private UserThirdInfoService userThirdInfoService;
	@Resource(name = "userBasisService")
	private UserBasisService userBasisService;

	Map<String, String> errorMap = new HashMap<>();
	//安装人员列表
	@Override
	public RequestResult findShareMemberList(Integer pageNum, Integer pageSize, String companyId,Integer identity,Integer status) {
		PaginatedFilter paginatedFilter=PaginatedFilter.newOne();
		//添加过滤条件
		MapContext mapContext=MapContext.newOne();
		mapContext.put("companyId",companyId);
		mapContext.put("identity",identity);
		mapContext.put("status",status);
		paginatedFilter.setFilters(mapContext);
		//添加分页信息
		Pagination pagination=Pagination.newOne();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		//添加排序方式
		List sorts=new ArrayList();
		Map dataSort = new HashMap();
		sorts.add(dataSort);
		dataSort.put("created","desc");
		paginatedFilter.setSorts(sorts);
		PaginatedList<CompanyShareMemberDto> companyShareMemberDtoPaginatedList=this.companyShareMemberService.selectByFilter(paginatedFilter);
		MapContext mapContext1=MapContext.newOne();
		mapContext1.put("shareMember",companyShareMemberDtoPaginatedList.getRows());
		return ResultFactory.generateRequestResult(mapContext1,companyShareMemberDtoPaginatedList.getPagination());
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addShareMember(String companyId,MapContext mapContext, HttpServletRequest request) {
		String uid = request.getHeader("X-UID");
		String cid = request.getHeader("X-CID");
		if(uid==null||uid.trim().equals("")){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_REQUEST_PARAM_ERROR_10000,AppBeanInjector.i18nUtil.getMessage("BIZ_REQUEST_PARAM_ERROR_10000"));
		}
		if(cid==null||cid.trim().equals("")){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_REQUEST_PARAM_ERROR_10000,AppBeanInjector.i18nUtil.getMessage("BIZ_REQUEST_PARAM_ERROR_10000"));
		}
		if(!cid.equals(companyId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
			String mobile=mapContext.getTypedValue("mobile",String.class);
		//验证电话是否正确
		if (!ValidateUtils.isChinaPhoneNumber(mobile)) {
			errorMap.put(WebConstant.KEY_ENTITY_MOBILE, AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, mobile);
		}
		//验证电话是否存在
		User us = userService.findByMobile(mobile);
		if(us!=null){
			String shareMemberId=us.getId();
			CompanyShareMember shareMember=this.companyShareMemberService.findShareMemberByCUID(companyId,shareMemberId);
			if(shareMember!=null){
				return  ResultFactory.generateErrorResult(ErrorCodes.BIZ_USER_HAS_BEEN_ADDED_10056,AppBeanInjector.i18nUtil.getMessage("BIZ_USER_HAS_BEEN_ADDED_10056"));
			}
			CompanyShareMember companyShareMember=new CompanyShareMember();
			companyShareMember.setCompanyId(companyId);
			companyShareMember.setUserId(shareMemberId);
			companyShareMember.setIdentity(CompanyShareMemberIdentity.ERECTOR.getValue());
			companyShareMember.setCreated(DateUtil.getSystemDate());
			companyShareMember.setStatus(CompanyShareMemberStatus.NORMAL.getValue());
			this.companyShareMemberService.add(companyShareMember);
			return ResultFactory.generateSuccessResult();
		}
		//添加基础信息
		User user=new User();
		user.setType(UserType.DEALER.getValue());
		user.setName(mapContext.getTypedValue("name",String.class));
		user.setSex(UserSex.MAN.getValue());
		user.setMobile(mapContext.getTypedValue("mobile",String.class));
		user.setAvatar(AppBeanInjector.configuration.getUserDefaultAvatar());
		user.setTimeZone(WebConstant.TIMEZONE_DEFAULT);
		user.setLanguage(WebConstant.LANGUAGE_DEFAULT);
		user.setCreated(DateUtil.getSystemDate());
		user.setState(UserState.ENABLED.getValue());
		user.setLoginName(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.USER_LOGNAME_NO));
		user.setFollowers(0);
		user.setChangedLoginName(false);
		this.userService.add(user);
		//添加安装人员和公司的关联信息
		CompanyShareMember companyShareMember=new CompanyShareMember();
		companyShareMember.setCompanyId(cid);
        companyShareMember.setUserId(user.getId());
        companyShareMember.setIdentity(CompanyShareMemberIdentity.ERECTOR.getValue());
        companyShareMember.setCreated(DateUtil.getSystemDate());
        companyShareMember.setStatus(CompanyShareMemberStatus.NORMAL.getValue());
        this.companyShareMemberService.add(companyShareMember);

		return ResultFactory.generateSuccessResult();
	}

	//安装人员信息修改
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateShareMember(String companyId, String userId, MapContext mapContext) {
		//查询用户基础信息是否存在
		User user=this.userService.findByUserId(userId);
		if(user==null){
			return ResultFactory.generateResNotFoundResult();
		}
		CompanyShareMember companyShareMember=this.companyShareMemberService.findShareMemberByCUID(companyId,userId);
		if(companyShareMember==null){
			ResultFactory.generateResNotFoundResult();
		}
		 mapContext.put("id",userId);
		this.userService.updateByMapContext(mapContext);

		String id=companyShareMember.getId();
		mapContext.put("id",id);
		this.companyShareMemberService.updateByMapContext(mapContext);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteShareMember(String companyId,String userId) {
		CompanyShareMember companyShareMember=this.companyShareMemberService.findShareMemberByCUID(companyId,userId);
		MapContext mapContext=MapContext.newOne();
		Integer status=CompanyShareMemberStatus.DISABLED.getValue();
		String id=companyShareMember.getId();
		mapContext.put("id",id);
		mapContext.put("status",status);
		this.companyShareMemberService.updateByMapContext(mapContext);
		return ResultFactory.generateSuccessResult();
	}




}
