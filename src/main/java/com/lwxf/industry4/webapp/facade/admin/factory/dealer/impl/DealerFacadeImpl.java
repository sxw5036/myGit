package com.lwxf.industry4.webapp.facade.admin.factory.dealer.impl;

import javax.annotation.Resource;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.lwxf.industry4.webapp.common.aop.syslog.OperationMoudule;
import com.lwxf.industry4.webapp.common.aop.syslog.OperationType;
import com.lwxf.industry4.webapp.common.aop.syslog.SysOperationLog;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.apache.shiro.crypto.hash.Md5Hash;

import com.lwxf.commons.uniquekey.IIdGenerator;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.commons.utils.ValidateUtils;
import com.lwxf.industry4.webapp.baseservice.cache.constant.RedisConstant;
import com.lwxf.industry4.webapp.bizservice.common.UploadFilesService;
import com.lwxf.industry4.webapp.bizservice.company.*;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.system.RolePermissionService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserBasisService;
import com.lwxf.industry4.webapp.bizservice.user.UserExtraService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.user.UserThirdInfoService;
import com.lwxf.industry4.webapp.common.authcode.AuthCodeUtils;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.dto.UserInfoObj;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.company.CompanyStatus;
import com.lwxf.industry4.webapp.common.enums.company.DealerAccountType;
import com.lwxf.industry4.webapp.common.enums.company.DealerEmployeeRole;
import com.lwxf.industry4.webapp.common.enums.company.EmployeeStatus;
import com.lwxf.industry4.webapp.common.enums.user.*;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.UserExtraUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyDto;
import com.lwxf.industry4.webapp.domain.entity.common.UploadFiles;
import com.lwxf.industry4.webapp.domain.entity.company.*;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccount;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.system.RolePermission;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserBasis;
import com.lwxf.industry4.webapp.domain.entity.user.UserExtra;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.dealer.DealerFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/5/005 13:27
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("dealerFacade")
public class DealerFacadeImpl extends BaseFacadeImpl implements DealerFacade {
	@Resource(name = "companyService")
	private CompanyService companyService;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "userExtraService")
	private UserExtraService userExtraService;
	@Resource(name = "userThirdInfoService")
	private UserThirdInfoService userThirdInfoService;
	@Resource(name = "companyEmployeeService")
	private CompanyEmployeeService companyEmployeeService;
	@Resource(name = "rolePermissionService")
	private RolePermissionService rolePermissionService;
	@Resource(name = "employeePermissionService")
	private EmployeePermissionService employeePermissionService;
	@Resource(name = "roleService")
	private RoleService roleService;
	@Resource(name = "userBasisService")
	private UserBasisService userBasisService;
	@Resource(name = "dealerAccountService")
	private DealerAccountService dealerAccountService;
	@Resource(name = "uploadFilesService")
	private UploadFilesService uploadFilesService;
	@Resource(name = "paymentService")
	private PaymentService paymentService;
	@Resource(name = "dealerShopService")
	private DealerShopService dealerShopService;
	@Resource(name = "employeeInfoService")
	private EmployeeInfoService employeeInfoService;
	@Resource(name = "dealerShippingLogisticsService")
	private DealerShippingLogisticsService dealerShippingLogisticsService;

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "新增经销商公司信息",operationType = OperationType.INSERT,operationMoudule = OperationMoudule.COMPANY)
	public RequestResult addDealer(CompanyDto company) {
		String branchId=WebUtils.getCurrBranchId();
		if(company.getNo()==null||company.getNo().trim().equals("")){
			company.setNo(null);
		}
		if(company.getNo()!=null&&this.companyService.selectByNo(company.getNo(),branchId)!=null){
			Map result = new HashMap();
			result.put(WebConstant.STRING_NO,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,result);
		}
		company.setBranchId(branchId);
		this.companyService.add(company);
		for (int i = 1; i < 6; i++) {
			DealerAccount dealerAccount = new DealerAccount();
			dealerAccount.setType(i);
			dealerAccount.setBalance(new BigDecimal(0));
			dealerAccount.setCompanyId(company.getId());
			dealerAccount.setNature(1);
			dealerAccount.setStatus(1);
			dealerAccount.setBranchId(WebUtils.getCurrBranchId());
			this.dealerAccountService.add(dealerAccount);
		}
		User user = new User();
		//用户信息
		user.setMobile(company.getLeaderTel());
		user.setName(company.getFounderName());
		user.setType(UserType.DEALER.getValue());
		user.setSex(UserSex.MAN.getValue());
		user.setAvatar(AppBeanInjector.configuration.getUserDefaultAvatar());
		user.setTimeZone(WebConstant.TIMEZONE_DEFAULT);
		user.setLanguage(WebConstant.LANGUAGE_DEFAULT);
		user.setCreated(DateUtil.getSystemDate());
		user.setState(UserState.DISABLED.getValue());
		user.setLoginName(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.USER_LOGNAME_NO));
		user.setFollowers(0);
		user.setChangedLoginName(false);
		user.setBranchId(WebUtils.getCurrBranchId());
		RequestResult validate = user.validateFields();
		if (validate != null) {
			return validate;
		}

		//验证电话号码是否正确
		if (!ValidateUtils.isChinaPhoneNumber(user.getMobile())) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, user.getMobile());
		}
		//判断手机号和名称是否已存在
		if(this.companyService.findByTelAndName(company.getLeaderTel(),company.getName(),WebUtils.getCurrBranchId())!=null){
			Map result = new HashMap<>();
			result.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,result);
		}

		//判断公司是否存在店主
		if(this.companyEmployeeService.selectShopkeeperByCompanyIds(Arrays.asList(company.getId())).size()!=0){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_SHOPKEEPERS_ALREADY_EXIST_10080,AppBeanInjector.i18nUtil.getMessage("BIZ_SHOPKEEPERS_ALREADY_EXIST_10080"));
		}
		//给经销商设定用户生日
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			user.setBirthday(simpleDateFormat.parse("1970-1-1 00:00:00"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.userService.add(user);

		//用户扩展信息
		UserExtra userExtra = new UserExtra();
		userExtra.setUserId(user.getId());
		UserExtraUtil.saltingPassword(userExtra,new Md5Hash("111111").toString());
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
		userThirdInfo.setCompanyId(WebUtils.getCurrCompanyId());
		userThirdInfo.setBranchId(WebUtils.getCurrBranchId());
		AppBeanInjector.redisUtils.hPut(RedisConstant.PLATFORM_TAG, user.getId(), Integer.valueOf(1));
		this.userThirdInfoService.add(userThirdInfo);

		//用户基本信息表
		UserBasis userBasis = new UserBasis();
		userBasis.setUserId(user.getId());
		userBasis.setPoliticalOutlook(UserPoliticalOutlookType.MASSES.getValue());
		userBasis.setEducation(EducationType.UNDERGRADUATE.getValue());
		userBasis.setIncome(IncomeType.FOUR.getValue());
		this.userBasisService.add(userBasis);

		//修改公司的状态为正常
		MapContext updateCompany = new MapContext();
		updateCompany.put(WebConstant.KEY_ENTITY_ID,company.getId());
		updateCompany.put(WebConstant.KEY_ENTITY_STATUS,CompanyStatus.NORMAL.getValue());
		updateCompany.put("leader",user.getId());
		this.companyService.updateByMapContext(updateCompany);

		//生成店铺数据
		DealerShop dealerShop = new DealerShop();
		dealerShop.setId(company.getId());
		dealerShop.setAddress(company.getAddress());
		dealerShop.setBusinessManager(company.getBusinessManager());
		dealerShop.setCityAreaId(company.getCityAreaId());
		dealerShop.setCompanyId(company.getId());
		dealerShop.setCreated(company.getCreated());
		dealerShop.setCreator(company.getCreator());
		dealerShop.setFollowers(company.getFollowers());
		dealerShop.setGrade(company.getGrade());
		dealerShop.setLat(company.getLat());
		dealerShop.setLeader(user.getId());
		dealerShop.setLeaderTel(company.getLeaderTel());
		dealerShop.setLng(company.getLng());
		dealerShop.setLogo(company.getLogo());
		dealerShop.setName(company.getName());
		dealerShop.setServiceStaff(company.getServiceStaff());
		dealerShop.setServiceTel(company.getServiceTel());
		dealerShop.setShopArea(company.getShopArea());
		dealerShop.setShopInfo(company.getCompanyInfo());
		dealerShop.setStatus(company.getStatus());
		dealerShop.setType(company.getType());
		this.dealerShopService.add(dealerShop);

		CompanyDto companyDto = new CompanyDto();
		companyDto.clone(company);
		companyDto.setCreatorName(this.userService.findById(companyDto.getCreator()).getName());

		//给该用户添加公司角色 店主
		CompanyEmployee companyEmployee = new CompanyEmployee();
		companyEmployee.setCompanyId(company.getId());
		companyEmployee.setUserId(user.getId());
		//店主编号未定
		Role role = this.roleService.selectByKey(DealerEmployeeRole.SHOPKEEPER.getValue(),WebUtils.getCurrBranchId());
		companyEmployee.setRoleId(role.getId());
		companyEmployee.setStatus(EmployeeStatus.NORMAL.getValue());
		companyEmployee.setCreated(DateUtil.getSystemDate());
		this.companyEmployeeService.add(companyEmployee);
		//把店主相关权限添加至 员工表中
		List<RolePermission> rolePermissionList= this.rolePermissionService.selectRolePermissionList(role.getId());
		if(rolePermissionList!=null&&rolePermissionList.size()!=0){
			IIdGenerator idGenerator = AppBeanInjector.idGererateFactory;
			for(RolePermission rolePermission:rolePermissionList){
				//重新生成主键ID
				rolePermission.setId(idGenerator.nextStringId());
				//用公司员工主键ID替换权限ID
				rolePermission.setRoleId(companyEmployee.getId());
			}
			this.employeePermissionService.addList(rolePermissionList);
		}

		//设置经销商的资金账户信息
//		DealerAccount dealerAccount = new DealerAccount();
//		dealerAccount.setBalance(new BigDecimal(0));
//		dealerAccount.setCompanyId(cid);
//		dealerAccount.setNature(1);
//		dealerAccount.setStatus(1);
//		dealerAccount.setBranchId(WebUtils.getCurrBranchId());
//		for (int i =3;i>0;i--){
//			dealerAccount.setType(i);
//			this.dealerAccountService.add(dealerAccount);
//		}

		//新增员工时 设置 员工信息 初始数据
		EmployeeInfo employeeInfo = new EmployeeInfo();
		employeeInfo.setCompanyEmployeeId(companyEmployee.getId());
		employeeInfo.setUserId(user.getId());
		employeeInfo.setStatus(EmployeeInfoStatus.NORMAL.getValue()+"");
		this.employeeInfoService.add(employeeInfo);
		return ResultFactory.generateRequestResult(this.companyService.findCompanyById(company.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult initDealerAccount(String branchId) {
		MapContext map = MapContext.newOne();
		map.put("branchId",branchId);
		List<Company> companys = companyService.findAllCompany(map);
		for(Company c: companys){
			for (int i = 1; i < 6; i++) {
				DealerAccount dealerAccount = new DealerAccount();
				dealerAccount.setType(i);
				dealerAccount.setBalance(new BigDecimal(0));
				dealerAccount.setCompanyId(c.getId());
				dealerAccount.setNature(1);
				dealerAccount.setStatus(1);
				dealerAccount.setBranchId(branchId);
				this.dealerAccountService.add(dealerAccount);
			}
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findDealerCompanyList(MapContext mapContent, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		mapContent.put("key",DealerEmployeeRole.SHOPKEEPER.getValue());
		mapContent.put("accountType",DealerAccountType.FREE_ACCOUNT.getValue());
		mapContent.put("depositType",DealerAccountType.DEPOSIT_ACCOUNT.getValue());
		mapContent.put("designType",DealerAccountType.FREEZE_ACCOUNT.getValue());
		paginatedFilter.setFilters(mapContent);
		Map dataSort = new HashMap();
		List sorts = new ArrayList();
		dataSort.put("created","desc");
		sorts.add(dataSort);
		paginatedFilter.setSorts(sorts);
		PaginatedList<CompanyDto> companyPaginatedList = this.companyService.selectByFilter(paginatedFilter);
		List ids = new ArrayList();
		for(Company company:companyPaginatedList.getRows()){
			ids.add(company.getId());
		}
		MapContext result = MapContext.newOne();
		List<CompanyDto> companyDtoList = new ArrayList<>();
		if(companyPaginatedList.getRows().size()>0){
			result.put("userList",new ArrayList<>());
			//给每个经销商填充账户信息
			for(CompanyDto company:companyPaginatedList.getRows()){
				List<DealerAccount> listDealerAccount=dealerAccountService.findByCompanIdAndNature(company.getId(),1);
				company.setAccountList(listDealerAccount);
				companyDtoList.add(company);
			}
			result.put("companyList",companyDtoList);
			return ResultFactory.generateRequestResult(result,companyPaginatedList.getPagination());
		}
		if(ids.size()>0){
			List<User> userList = this.userService.selectCompanyShopkeeperByCompanyIds(ids,DealerEmployeeRole.SHOPKEEPER.getValue());
			result.put("userList",userList);
		}else{
			result.put("userList",new ArrayList<>());
		}
		result.put("companyList",companyPaginatedList.getRows());
		return ResultFactory.generateRequestResult(result,companyPaginatedList.getPagination());
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult openDealer(User user,String cid,StringBuffer pwd) {
		//判断公司是否存在
		Company company = this.companyService.findById(cid);
		if(company==null||!company.getStatus().equals(CompanyStatus.TO_BE_ENABLED.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		//用户信息
		user.setMobile(company.getLeaderTel());
		user.setName(company.getFounderName());
		user.setType(UserType.DEALER.getValue());
		user.setSex(UserSex.MAN.getValue());
		user.setAvatar(AppBeanInjector.configuration.getUserDefaultAvatar());
		user.setTimeZone(WebConstant.TIMEZONE_DEFAULT);
		user.setLanguage(WebConstant.LANGUAGE_DEFAULT);
		user.setCreated(DateUtil.getSystemDate());
		user.setState(UserState.ENABLED.getValue());
		user.setLoginName(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.USER_LOGNAME_NO));
		user.setFollowers(0);
		user.setChangedLoginName(false);
		user.setBranchId(WebUtils.getCurrBranchId());
		RequestResult validate = user.validateFields();
		if (validate != null) {
			return validate;
		}

		//验证电话号码是否正确
		if (!ValidateUtils.isChinaPhoneNumber(user.getMobile())) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, user.getMobile());
		}
		//判断手机号是否已存在
		if(this.userService.findByMobile(user.getMobile())!=null){
			Map result = new HashMap<>();
			result.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,result);
		}

		//判断公司是否存在店主
		if(this.companyEmployeeService.selectShopkeeperByCompanyIds(Arrays.asList(cid)).size()!=0){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_SHOPKEEPERS_ALREADY_EXIST_10080,AppBeanInjector.i18nUtil.getMessage("BIZ_SHOPKEEPERS_ALREADY_EXIST_10080"));
		}
		//给经销商设定用户生日
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			user.setBirthday(simpleDateFormat.parse("1970-1-1 00:00:00"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.userService.add(user);

		//用户扩展信息
		UserExtra userExtra = new UserExtra();
		userExtra.setUserId(user.getId());
		pwd.append(AuthCodeUtils.getRandomNumberCode(6));
		UserExtraUtil.saltingPassword(userExtra,new Md5Hash(pwd.toString()).toString());
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
		userThirdInfo.setCompanyId(WebUtils.getCurrCompanyId());
		userThirdInfo.setBranchId(WebUtils.getCurrBranchId());
		AppBeanInjector.redisUtils.hPut(RedisConstant.PLATFORM_TAG, user.getId(), Integer.valueOf(1));
		this.userThirdInfoService.add(userThirdInfo);

		//用户基本信息表
		UserBasis userBasis = new UserBasis();
		userBasis.setUserId(user.getId());
		userBasis.setPoliticalOutlook(UserPoliticalOutlookType.MASSES.getValue());
		userBasis.setEducation(EducationType.UNDERGRADUATE.getValue());
		userBasis.setIncome(IncomeType.FOUR.getValue());
		this.userBasisService.add(userBasis);

		//修改公司的状态为正常
		MapContext updateCompany = new MapContext();
		updateCompany.put(WebConstant.KEY_ENTITY_ID,cid);
		updateCompany.put(WebConstant.KEY_ENTITY_STATUS,CompanyStatus.NORMAL.getValue());
		updateCompany.put("leader",user.getId());
		this.companyService.updateByMapContext(updateCompany);

		//生成店铺数据
		DealerShop dealerShop = new DealerShop();
		dealerShop.setId(cid);
		dealerShop.setAddress(company.getAddress());
		dealerShop.setBusinessManager(company.getBusinessManager());
		dealerShop.setCityAreaId(company.getCityAreaId());
		dealerShop.setCompanyId(cid);
		dealerShop.setCreated(company.getCreated());
		dealerShop.setCreator(company.getCreator());
		dealerShop.setFollowers(company.getFollowers());
		dealerShop.setGrade(company.getGrade());
		dealerShop.setLat(company.getLat());
		dealerShop.setLeader(user.getId());
		dealerShop.setLeaderTel(company.getLeaderTel());
		dealerShop.setLng(company.getLng());
		dealerShop.setLogo(company.getLogo());
		dealerShop.setName(company.getName());
		dealerShop.setServiceStaff(company.getServiceStaff());
		dealerShop.setServiceTel(company.getServiceTel());
		dealerShop.setShopArea(company.getShopArea());
		dealerShop.setShopInfo(company.getCompanyInfo());
		dealerShop.setStatus(company.getStatus());
		dealerShop.setType(company.getType());
		this.dealerShopService.add(dealerShop);

		CompanyDto companyDto = new CompanyDto();
		companyDto.clone(company);
		companyDto.setCreatorName(this.userService.findById(companyDto.getCreator()).getName());

		//给该用户添加公司角色 店主
		CompanyEmployee companyEmployee = new CompanyEmployee();
		companyEmployee.setCompanyId(cid);
		companyEmployee.setUserId(user.getId());
		//店主编号未定
		Role role = this.roleService.selectByKey(DealerEmployeeRole.SHOPKEEPER.getValue(),WebUtils.getCurrBranchId());
		companyEmployee.setRoleId(role.getId());
		companyEmployee.setStatus(EmployeeStatus.NORMAL.getValue());
		companyEmployee.setCreated(DateUtil.getSystemDate());
		this.companyEmployeeService.add(companyEmployee);
		//把店主相关权限添加至 员工表中
		List<RolePermission> rolePermissionList= this.rolePermissionService.selectRolePermissionList(role.getId());
		if(rolePermissionList!=null&&rolePermissionList.size()!=0){
			IIdGenerator idGenerator = AppBeanInjector.idGererateFactory;
			for(RolePermission rolePermission:rolePermissionList){
				//重新生成主键ID
				rolePermission.setId(idGenerator.nextStringId());
				//用公司员工主键ID替换权限ID
				rolePermission.setRoleId(companyEmployee.getId());
			}
			this.employeePermissionService.addList(rolePermissionList);
		}

		//设置经销商的资金账户信息
//		DealerAccount dealerAccount = new DealerAccount();
//		dealerAccount.setBalance(new BigDecimal(0));
//		dealerAccount.setCompanyId(cid);
//		dealerAccount.setNature(1);
//		dealerAccount.setStatus(1);
//		dealerAccount.setBranchId(WebUtils.getCurrBranchId());
//		for (int i =3;i>0;i--){
//			dealerAccount.setType(i);
//			this.dealerAccountService.add(dealerAccount);
//		}

		//新增员工时 设置 员工信息 初始数据
		EmployeeInfo employeeInfo = new EmployeeInfo();
		employeeInfo.setCompanyEmployeeId(companyEmployee.getId());
		employeeInfo.setUserId(user.getId());
		employeeInfo.setStatus(EmployeeInfoStatus.NORMAL.getValue()+"");
		this.employeeInfoService.add(employeeInfo);
		return ResultFactory.generateRequestResult(UserInfoObj.newOne(user,userExtra,userThirdInfo,companyDto));
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "更新经销商公司信息",operationType = OperationType.UPDATE,operationMoudule = OperationMoudule.COMPANY)
	public RequestResult updateDealerCompany(MapContext mapContext, String cid,String logisticsCompanyId) {
		String branchId=WebUtils.getCurrBranchId();
		//判断公司是否存在
		Company companyById= this.companyService.findById(cid);
		if(companyById==null){
			return ResultFactory.generateResNotFoundResult();
		}
		Integer status = mapContext.getTypedValue("status",Integer.class);
		if(status!=null){
			//如果修改状态为 已退网或者 已禁用 已倒闭 则经销商下员工状态改为离职
			if(status==CompanyStatus.CLOSED_DOWN.getValue()||status==CompanyStatus.RETIRED_NETWORK.getValue()||status==CompanyStatus.DISABLED.getValue()){
				this.companyEmployeeService.updateAllDisabledByCompanyId(cid);
			}
			//如果修改状态为 正常 则修改经销商店主状态为正常
			if(status==CompanyStatus.NORMAL.getValue()){
				this.companyEmployeeService.updateShopkeeperByCompanyId(cid,DealerEmployeeRole.SHOPKEEPER.getValue());
			}
			//如果是提交操作
			if(status==CompanyStatus.PENDING_APPROVAL.getValue()){
				MapContext result = new MapContext();
				//判断是否设置签约时间 到期时间 地址是否为空
				if(companyById.getContractExpiredDate()==null){
					result.put("contractExpiredDate",AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
				}
				if(companyById.getContractTime()==null){
					result.put("contractTime",AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
				}
				if(LwxfStringUtils.isBlank(companyById.getAddress())){
					result.put("address",AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
				}
				if(result.size()>0){
					return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,result);
				}
			}
		}
		String no = mapContext.getTypedValue("no", String.class);
		if(no==null||no.trim().equals("")){
			mapContext.put("no",null);
			no=null;
		}
		if(no!=null){
			Company company = this.companyService.selectByNo(no,branchId);
			if(company!=null&&!company.getId().equals(cid)){
				Map result = new HashMap();
				result.put(WebConstant.STRING_NO,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
				return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,result);
			}
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID,cid);
		//修改公司信息
		this.companyService.updateByMapContext(mapContext);
		//是否修改默认物流公司
		if(logisticsCompanyId!=null){
			DealerShippingLogistics oneByCompanyId = this.dealerShippingLogisticsService.findOneByCompanyId(cid);
			if(oneByCompanyId==null){
				DealerShippingLogistics dealerShippingLogistics = new DealerShippingLogistics();
				dealerShippingLogistics.setCompanyId(cid);
				dealerShippingLogistics.setLogisticsCompanyId(logisticsCompanyId);
				dealerShippingLogistics.setBranchId(WebUtils.getCurrBranchId());
				this.dealerShippingLogisticsService.add(dealerShippingLogistics);
			}else{
				MapContext updateDealerShipping = new MapContext();
				updateDealerShipping.put("companyId",cid);
				updateDealerShipping.put("logisticsCompanyId",logisticsCompanyId);
				this.dealerShippingLogisticsService.updateByCompanyId(updateDealerShipping);
			}
		}

		CompanyDto companyDto = this.companyService.findCompanyById(cid);
		List<User> userList = this.userService.selectCompanyShopkeeperByCompanyIds(Arrays.asList(companyDto.getId()),DealerEmployeeRole.SHOPKEEPER.getValue());
		MapContext result = new MapContext();
		result.put("company",companyDto);
		result.put("user",userList.get(0));

		//所需修改的数据中 移除店铺表所不存在的数据 如果还存在修改 就修改店铺
		mapContext.remove("no");
		mapContext.remove("depositBank");
		mapContext.remove("bankAccount");
		mapContext.remove("bankAccountHolder");
		if(mapContext.size()!=0){
			this.dealerShopService.updateByMapContext(mapContext);
		}
		return ResultFactory.generateRequestResult(result);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult uploadDealerFiles(String cid, List<MultipartFile> multipartFileList) {
		List list=new ArrayList<>();
		UploadInfo uploadInfo ;
		for (MultipartFile multipartFile : multipartFileList){
			uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL,multipartFile, UploadResourceType.COMPANY,cid);
			UploadFiles uploadFiles = UploadFiles.create(cid,null,uploadInfo,UploadResourceType.COMPANY,UploadType.FORMAL);
			uploadFiles.setCompanyId(cid);
			this.uploadFilesService.add(uploadFiles);
			list.add(uploadFiles);
		}
		return ResultFactory.generateRequestResult(list);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteDealerFiles(String cid, String fileId) {
		//判断资源是否存在
		UploadFiles files = this.uploadFilesService.findById(fileId);
		if(!files.getCompanyId().equals(cid)){
			return ResultFactory.generateResNotFoundResult();
		}
		//删除本地资源
		AppBeanInjector.baseFileUploadComponent.deleteFileByDir(AppBeanInjector.configuration.getUploadFileRootDir().concat(files.getPath()));
		this.uploadFilesService.deleteById(fileId);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult submitDealer(String cid,Payment payment) {
		//判断经销商公司是否存在  当前状态是否是意向
		Company company = this.companyService.findById(cid);
		if(company==null||!company.getStatus().equals(CompanyStatus.INTENTION.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		//生成支付记录
		this.paymentService.add(payment);
		//修改公司状态为待财务审核
		MapContext updateCompany = new MapContext();
		updateCompany.put(WebConstant.KEY_ENTITY_ID,cid);
		updateCompany.put(WebConstant.KEY_ENTITY_STATUS,CompanyStatus.PENDING_APPROVAL.getValue());
		this.companyService.updateByMapContext(updateCompany);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findDealerList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		Role role = this.roleService.selectByKey(DealerEmployeeRole.SHOPKEEPER.getValue(), WebUtils.getCurrBranchId());
		if(role!=null){
			mapContext.put("roleId",role.getId());
		}
		paginatedFilter.setFilters(mapContext);
		List<Map<String,String>> sorts = new ArrayList<Map<String, String>>();
		Map<String,String> ceated = new HashMap<String, String>();
		ceated.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sorts.add(ceated);
		return ResultFactory.generateRequestResult(this.companyEmployeeService.findListByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "更新经销商信息",operationType = OperationType.UPDATE,operationMoudule = OperationMoudule.COMPANY)
	public RequestResult updateDealer(MapContext mapContext, String id) {
		//判断该店主是否存在
		CompanyEmployee companyEmployee = this.companyEmployeeService.findById(id);
		if(companyEmployee==null||!companyEmployee.getStatus().equals(EmployeeStatus.NORMAL.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		//修改用户信息
		mapContext.put(WebConstant.KEY_ENTITY_ID,companyEmployee.getUserId());
		this.userService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.userService.findById(companyEmployee.getUserId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "更新经销商手机信息",operationType = OperationType.UPDATE,operationMoudule = OperationMoudule.COMPANY)
	public RequestResult updateDealerMobile(String id, String mobile) {
		//判断该店主是否存在
		CompanyEmployee companyEmployee = this.companyEmployeeService.findById(id);
		if(companyEmployee==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//验证电话号码是否正确
		if (!ValidateUtils.isChinaPhoneNumber(mobile)) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, mobile);
		}
		//判断新的登录名 是否已存在
		User byMobile = this.userService.findByMobile(mobile);
		if(byMobile!=null&&!byMobile.getId().equals(companyEmployee.getUserId())){
			Map<String, String> validResult = new HashMap<>();
			validResult.put("mobile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_MOBILE,mobile);
		mapContext.put(WebConstant.KEY_ENTITY_ID,companyEmployee.getUserId());
		this.userService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.userService.findById(companyEmployee.getUserId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "更新经销商密码信息",operationType = OperationType.UPDATE,operationMoudule = OperationMoudule.COMPANY)
	public RequestResult updateDealerAccountPwd(String id, String newPassword) {
		//判断该店主是否存在
		CompanyEmployee companyEmployee = this.companyEmployeeService.findById(id);
		if(companyEmployee==null||!companyEmployee.getStatus().equals(EmployeeStatus.NORMAL.getValue())){
			return ResultFactory.generateResNotFoundResult();
		}
		UserExtra userExtra = new UserExtra();
		UserExtraUtil.saltingPassword(userExtra, newPassword);
		MapContext toSave = new MapContext();
		toSave.put(WebConstant.KEY_USER_EXTRA_SALT, userExtra.getSalt());
		toSave.put(WebConstant.KEY_ENTITY_USER_ID, companyEmployee.getUserId());
		toSave.put(WebConstant.KEY_USER_EXTRA_PASSWORD, userExtra.getPassword());
		toSave.put(WebConstant.KEY_USER_EXTRA_TOKEN, userExtra.getToken());
		this.userExtraService.updateByMapContext(toSave);
		toSave = MapContext.newOne();
		toSave.put(WebConstant.KEY_ENTITY_USER_ID, companyEmployee.getUserId());
		toSave.put(WebConstant.KEY_ENTITY_APP_TOKEN, UserExtraUtil.generateAppToken(userExtra, null));
		this.userThirdInfoService.updateByMapContext(toSave);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findDealerCompanyInfo(String cid) {
		MapContext mapContext = new MapContext();
		mapContext.put("fileList",this.uploadFilesService.findByResourceId(cid));
		mapContext.put("company",this.companyService.findCompanyById(cid));
		List<DealerAccount> listDealerAccount=dealerAccountService.findByCompanIdAndNature(cid,1);
		mapContext.put("accountList",listDealerAccount);
		return ResultFactory.generateRequestResult(mapContext);
	}

	@Override
	public RequestResult findDealerCount(String branchId) {
		MapContext mapContext=this.companyService.findDealerCount(branchId);
		List result=new ArrayList();
		int a=0;
		for(int i=0;i<3;i++){
			Map map = new HashMap();
			switch (a) {
				case 	0:
					map.put("name", "今日意向经销商数量");
					map.put("value",mapContext.getTypedValue("intentionNum",Integer.class));
					a=a+1;
					break;
				case 1:
					map.put("name", "今日签约经销商数量");
					map.put("value",mapContext.getTypedValue("SignNum",Integer.class));
					a=a+1;
					break;
				case 2:
					map.put("name", "今日下单经销商数量");
					map.put("value",mapContext.getTypedValue("placeOrder",Integer.class));
					a=a+1;
					break;
			}
			result.add(map);
		}
		return ResultFactory.generateRequestResult(result);
	}

}
