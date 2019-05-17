package com.lwxf.industry4.webapp.facade.app.dealer.company.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.apache.shiro.crypto.hash.Md5Hash;

import com.lwxf.commons.constant.CommonConstant;
import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.uniquekey.IIdGenerator;
import com.lwxf.commons.uniquekey.LwxfWorkerIdGenerator;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.ValidateUtils;
import com.lwxf.industry4.webapp.baseservice.cache.constant.RedisConstant;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.EmployeePermissionService;
import com.lwxf.industry4.webapp.bizservice.system.RolePermissionService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserBasisService;
import com.lwxf.industry4.webapp.bizservice.user.UserExtraService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.user.UserThirdInfoService;
import com.lwxf.industry4.webapp.common.authcode.AuthCodeUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.dto.UserInfoObj;
import com.lwxf.industry4.webapp.common.enums.company.DealerEmployeeRole;
import com.lwxf.industry4.webapp.common.enums.company.EmployeeStatus;
import com.lwxf.industry4.webapp.common.enums.user.*;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.UserExtraUtil;
import com.lwxf.industry4.webapp.domain.dto.companyEmployee.CompanyEmployeeDto;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.system.RolePermission;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserBasis;
import com.lwxf.industry4.webapp.domain.entity.user.UserExtra;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.company.EmployeeFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/3 0003 13:27
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("employeeFacade")
public class EmployeeFacadeImpl extends BaseFacadeImpl implements EmployeeFacade {

	@Resource(name = "companyEmployeeService")
	private CompanyEmployeeService companyEmployeeService;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "userExtraService")
	private UserExtraService userExtraService;
	@Resource(name = "userThirdInfoService")
	private UserThirdInfoService userThirdInfoService;
	@Resource(name = "userBasisService")
	private UserBasisService userBasisService;
	@Resource(name="roleService")
	private RoleService roleService;
	@Resource(name="rolePermissionService")
	private RolePermissionService rolePermissionService;
	@Resource(name = "employeePermissionService")
	private EmployeePermissionService employeePermissionService;

	Map<String, String> errorMap = new HashMap<>();

	//员工列表查询
	@Override
	public RequestResult selectEmployeeList(Integer pageNum, Integer pageSize, MapContext mapContext, HttpServletRequest request) {
		String companyId=mapContext.getTypedValue("companyId",String.class);
		if(!companyId.equals(request.getHeader("X-CID"))){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		PaginatedFilter paginatedFilter = PaginatedFilter.newOne();
		//添加过滤条件
		paginatedFilter.setFilters(mapContext);
		//添加分页信息
		Pagination pagination = Pagination.newOne();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		//添加排序方式
		List sorts = new ArrayList();
		Map dataSort = new HashMap();
		sorts.add(dataSort);
		dataSort.put("created", "desc");
		paginatedFilter.setSorts(sorts);
		PaginatedList<CompanyEmployeeDto> companyEmployeeDtoPaginatedList = this.companyEmployeeService.findEmployeeListByCID(paginatedFilter);
		MapContext mapContext1 = MapContext.newOne();
		mapContext1.put("companyEmployee", companyEmployeeDtoPaginatedList.getRows());
		return ResultFactory.generateRequestResult(mapContext1, companyEmployeeDtoPaginatedList.getPagination());
	}
//员工详情
	@Override
	public RequestResult findEmployeeMessage(String companyId, String userId,HttpServletRequest request) {
		String cid=request.getHeader("X-CID");
		String uid=request.getHeader("X-UID");
		Role role=this.roleService.findRoleByCidAndUid(uid,cid);
		if(role!=null){
			if(role.getKey().equals(DealerEmployeeRole.CLERK.getValue())){
				if(!userId.equals(uid)){
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
				}
			}
		}else {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
		}
		MapContext mapContext=MapContext.newOne();
		mapContext.put("companyId",companyId);
		mapContext.put("userId",userId);
		return ResultFactory.generateRequestResult(this.companyEmployeeService.findEmployeeMessage(mapContext));
	}
//添加员工
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addEmployee(String companyId, MapContext mapContext,StringBuffer pwd) {
		pwd.append(AuthCodeUtils.getRandomNumberCode(6));
		String key= DealerEmployeeRole.CLERK.getValue();
		Role role=this.roleService.findRoleByKey(key);
		if(role==null){
			return ResultFactory.generateResNotFoundResult();
		}
		String roleId=role.getId();

		//验证电话号码是否正确
		String mobile = (String) mapContext.get("mobile");
		User u = userService.findByMobile(mobile);
		if (!ValidateUtils.isChinaPhoneNumber(mobile)) {
			errorMap.put(WebConstant.KEY_ENTITY_MOBILE, AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorMap);
		}
		//验证用户是否存在
		if (null != u) {
			String userId = u.getId();
			//判断是否属于公司员工
			Integer status=EmployeeStatus.NORMAL.getValue();
			CompanyEmployee companyEmployee = this.companyEmployeeService.findCompanyByUidAndStatus(userId,status);
			if (companyEmployee != null) {
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_USER_HAS_BEEN_ADDED_10056, AppBeanInjector.i18nUtil.getMessage("BIZ_USER_HAS_BEEN_ADDED_10056"));
			} else {
				String no = (String) mapContext.get("no");
				if (no != null) {
					//验证员工编号是否重复
					if (this.companyEmployeeService.selectEmployeeByCidAndNo(companyId,no) != null) {
						errorMap.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
						return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorMap);
					}

				}
				CompanyEmployee employee=new CompanyEmployee();
				employee.setCompanyId(companyId);
				employee.setUserId(userId);
				employee.setRoleId(roleId);
				employee.setCreated(DateUtil.getSystemDate());
				employee.setStatus(EmployeeStatus.NORMAL.getValue());
				employee.setNo(no);
				this.companyEmployeeService.add(employee);
				//判斷用戶類型是否為客戶
				if(u.getType()==UserType.CLIENT.getValue()) {
					MapContext mapContext1 = MapContext.newOne();
					String password = new Md5Hash(pwd.toString()).toString();
					mapContext1.put("userId", userId);
					mapContext1.put("password", password);
					this.userExtraService.updateByMapContext(mapContext1);
					String id=userId;
					UserExtra userExtra=this.userExtraService.findById(id);
					MapContext mapContext2=MapContext.newOne();
					mapContext2.put("userId",userId);
					String appToken=UserExtraUtil.generateAppToken(userExtra, null);
					mapContext2.put("appToken",appToken);
					this.userThirdInfoService.updateByMapContext(mapContext2);
					MapContext mapContext3=MapContext.newOne();
					Integer type=UserType.DEALER.getValue();
					mapContext3.put("type",type);
					mapContext3.put("id",userId);
					this.userService.updateByMapContext(mapContext3);
				}
				//添加员工权限信息
				List<RolePermission> rolePermissionList= this.rolePermissionService.selectRolePermissionList(roleId);
				if(rolePermissionList!=null&&rolePermissionList.size()!=0){
					IIdGenerator idGenerator = new LwxfWorkerIdGenerator(1);
					for(RolePermission rolePermission:rolePermissionList){
						//重新生成主键ID
						rolePermission.setId(idGenerator.nextStringId());
						//用公司员工主键ID替换权限ID
						rolePermission.setRoleId(companyEmployee.getId());
					}
					this.employeePermissionService.addList(rolePermissionList);
				}
                return ResultFactory.generateSuccessResult();
			}

		}
		String no = (String) mapContext.get("no");
		if (no != null) {
			//验证员工编号是否重复
			if (this.companyEmployeeService.selectEmployeeByCidAndNo(companyId,no) != null) {
				errorMap.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
				return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorMap);
			}
		}
		User user = new User();
		user.setType(UserType.DEALER.getValue());
		user.setName((String) mapContext.get("name"));
		user.setSex(UserSex.MAN.getValue());
		user.setMobile(mobile);
		String birthday=(String)mapContext.get("birthday");
		if(birthday!=null) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				user.setBirthday(dateFormat.parse(birthday));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		user.setEmail((String) mapContext.get("email"));
		user.setAvatar(AppBeanInjector.configuration.getUserDefaultAvatar());
		user.setTimeZone(WebConstant.TIMEZONE_DEFAULT);
		user.setLanguage(WebConstant.LANGUAGE_DEFAULT);
		user.setCreated(DateUtil.getSystemDate());
		user.setState(UserState.ENABLED.getValue());
		user.setLoginName(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.USER_LOGNAME_NO));
		user.setFollowers(0);
		user.setChangedLoginName(false);
		this.userService.add(user);

		//员工与经销商关联
		CompanyEmployee companyEmployee = new CompanyEmployee();
		companyEmployee.setCompanyId(companyId);
		companyEmployee.setUserId(user.getId());
		companyEmployee.setRoleId(roleId);
		companyEmployee.setCreated(DateUtil.getSystemDate());
		companyEmployee.setStatus(EmployeeStatus.NORMAL.getValue());
		companyEmployee.setNo(no);
		this.companyEmployeeService.add(companyEmployee);
		//用户基础扩展信息
		UserBasis userBasis = new UserBasis();
		userBasis.setUserId(user.getId());
		userBasis.setAddress((String) mapContext.get("address"));
		userBasis.setEducation(EducationType.UNDERGRADUATE.getValue());
		userBasis.setIncome(IncomeType.FOUR.getValue());
		userBasis.setWork((String) mapContext.get("work"));
		userBasis.setWorkUnit((String) mapContext.get("workUnit"));
		userBasis.setPoliticalOutlook(UserPoliticalOutlookType.MASSES.getValue());
		userBasis.setNation((String)mapContext.get("nation"));
		this.userBasisService.add(userBasis);

		//用户扩展信息
		UserExtra userExtra = new UserExtra();
		userExtra.setUserId(user.getId());
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
		userThirdInfo.setAppToken(UserExtraUtil.generateAppToken(userExtra, null));
		AppBeanInjector.redisUtils.hPut(RedisConstant.PLATFORM_TAG, user.getId(), Integer.valueOf(1));
		this.userThirdInfoService.add(userThirdInfo);
		//添加员工权限信息
		List<RolePermission> rolePermissionList= this.rolePermissionService.selectRolePermissionList(roleId);
		if(rolePermissionList!=null&&rolePermissionList.size()!=0){
			IIdGenerator idGenerator = new LwxfWorkerIdGenerator(1);
			for(RolePermission rolePermission:rolePermissionList){
				//重新生成主键ID
				rolePermission.setId(idGenerator.nextStringId());
				//用公司员工主键ID替换权限ID
				rolePermission.setRoleId(companyEmployee.getId());
			}
			this.employeePermissionService.addList(rolePermissionList);
		}

		return ResultFactory.generateRequestResult(UserInfoObj.newInfo(user, userExtra, userThirdInfo,userBasis,null));

	}

	//辞退员工
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteEmployee(String companyId, String userId,HttpServletRequest request) {
		String uid=request.getHeader("X-UID");
		String cid=request.getHeader("X-CID");
		if(!companyId.equals(cid)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		CompanyEmployee companyEmployee=this.companyEmployeeService.findOneByCompanyIdAndUserId(companyId,userId);
		if(companyEmployee==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断登陆人的职务
		Role role=this.roleService.findRoleByCidAndUid(uid,companyId);
		if(role==null){
			return ResultFactory.generateResNotFoundResult();
		}
		String key=role.getKey();
		if(key.equals(DealerEmployeeRole.CLERK.getValue())){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
		}
		User user = this.userService.findByUserId(userId);
		if (user == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//删除员工权限
		this.employeePermissionService.deleteByEmployeeId(companyEmployee.getId());
		//员工的状态改为离职
		Integer status=EmployeeStatus.DIMISSION.getValue();
		MapContext mapContext=MapContext.newOne();
		mapContext.put("status",status);
		mapContext.put("userId",userId);
		mapContext.put("companyId",companyId);
		this.companyEmployeeService.updateEmployeeStatus(mapContext);
		return ResultFactory.generateSuccessResult();
	}

	//修改员工
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateEmployee(String companyId, String userId, MapContext mapContext,HttpServletRequest request) {
		String uid=request.getHeader("X-UID");
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		//判断登陆人的职务
		Role role=this.roleService.findRoleByCidAndUid(uid,companyId);
		String key=role.getKey();
		//店员只能操作自己的信息
		if(key.equals(DealerEmployeeRole.CLERK.getValue())){
			if(!uid.equals(userId)){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
			}
		}
		//判断员工的基础信息是否存在
		User user = this.userService.findByUserId(userId);
		if (user == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		Integer status=EmployeeStatus.NORMAL.getValue();
		CompanyEmployee companyEmployee = this.companyEmployeeService.findCompanyByUidAndStatus(userId,status);

		if (companyEmployee == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		String cId=companyEmployee.getCompanyId();
        if(!cId.equals(companyId)){
        	return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		String id = companyEmployee.getId();
		MapContext mapContext1=MapContext.newOne();
		//用户不允许操作自己的职位或者非店主不能操作别人的职位
		if(mapContext.containsKey("roleId")){
			if(userId.equals(uid)||!key.equals(DealerEmployeeRole.SHOPKEEPER.getValue())){
				errorMap.put("roleId",AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
				return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorMap);
			}else {
				//根据新职位调整权限
				String roleId=mapContext.getTypedValue("roleId",String.class);
				this.employeePermissionService.deleteByEmployeeId(id);
				List<RolePermission> rolePermissionList= this.rolePermissionService.selectRolePermissionList(roleId);
				if(rolePermissionList!=null&&rolePermissionList.size()!=0){
					IIdGenerator idGenerator = new LwxfWorkerIdGenerator(1);
					for(RolePermission rolePermission:rolePermissionList){
						//重新生成主键ID
						rolePermission.setId(idGenerator.nextStringId());
						//用公司员工主键ID替换权限ID
						rolePermission.setRoleId(id);
					}
					this.employeePermissionService.addList(rolePermissionList);
				}

				mapContext1.put("roleId", roleId);
			}
		}
		if(mapContext.containsKey("no")){
			String no=(String)mapContext.get("no");
			if(CommonConstant.STRING_EMPTY.equals(no)){
				no = null;
			}else{
				CompanyEmployee cm=this.companyEmployeeService.selectEmployeeByCidAndNo(companyId,no);
				if(cm!=null&&!cm.getUserId().equals(userId))
				{
					errorMap.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
					return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorMap);
				}
			}
			mapContext1.put("no",no);
		}
		if(mapContext1.size()>0){
			mapContext1.put("id",id);
			this.companyEmployeeService.updateByMapContext(mapContext1);
		}

		MapContext mapContext2=MapContext.newOne();
		if(mapContext.containsKey("nation")){
			mapContext2.put("nation",mapContext.get("nation"));
		}
		if(mapContext.containsKey("education")){
			String educationValue = mapContext.getTypedValue("education",String.class);
			if(CommonConstant.STRING_EMPTY.equals(educationValue )){
				educationValue = null;
			}
			mapContext2.put("education",educationValue);
		}
		if(mapContext.containsKey("address")){
			mapContext2.put("address",mapContext.get("address"));
		}
		if(mapContext.containsKey("politicalOutlook")){
			String politicalOutlookValue = mapContext.getTypedValue("politicalOutlook",String.class);
			if(CommonConstant.STRING_EMPTY.equals(politicalOutlookValue )){
				politicalOutlookValue = null;
			}
			mapContext2.put("politicalOutlook",politicalOutlookValue);
		}
		if(mapContext2.size()>0){
			mapContext2.put("userId",userId);
			this.userBasisService.updateByMapContext(mapContext2);
		}
		MapContext mapContext3=MapContext.newOne();
		if(mapContext.containsKey("name")){
			mapContext3.put("name",mapContext.get("name"));
		}
		if(mapContext.containsKey("mobile")){
			mapContext3.put("mobile",mapContext.get("mobile"));
		}
		if(mapContext.containsKey("birthday")){
			String birthdayValue = mapContext.getTypedValue("birthday",String.class);
			if(CommonConstant.STRING_EMPTY.equals(birthdayValue )){
				birthdayValue = null;
			}
			mapContext3.put("birthday",birthdayValue);
		}
		if(mapContext3.size()>0){
			mapContext3.put("id", userId);
			this.userService.updateByMapContext(mapContext3);
		}

		return ResultFactory.generateSuccessResult();
	}

	//禁用和启用
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateEmployeeStatus(String companyId, String userId, Integer status,HttpServletRequest request) {
		//判断登陆人的职务
		String uid=request.getHeader("X-UID");
		Role role=this.roleService.findRoleByCidAndUid(uid,companyId);
		if(role==null){
			return ResultFactory.generateResNotFoundResult();
		}
		String key=role.getKey();
		if(key.equals(DealerEmployeeRole.CLERK.getValue())){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
		}
		User user = userService.findByUserId(userId);
		if (user == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		String roleId=role.getId();
        if(status==EmployeeStatus.NORMAL.getValue()){//启用员工，重新添加权限
			List<RolePermission> rolePermissionList= this.rolePermissionService.selectRolePermissionList(roleId);
			if(rolePermissionList!=null&&rolePermissionList.size()!=0) {
				IIdGenerator idGenerator = new LwxfWorkerIdGenerator(1);
				for (RolePermission rolePermission : rolePermissionList) {
					//重新生成主键ID
					rolePermission.setId(idGenerator.nextStringId());
					//用公司员工主键ID替换权限ID
					rolePermission.setRoleId(roleId);
				}
				this.employeePermissionService.addList(rolePermissionList);
			}
		}else {//禁用员工删除相关权限
			CompanyEmployee companyEmployee = this.companyEmployeeService.findOneByCompanyIdAndUserId(companyId,userId);
			this.employeePermissionService.deleteByEmployeeId(companyEmployee.getId());
		}
		return ResultFactory.generateRequestResult(this.companyEmployeeService.updateStatus(userId, status));
	}


	//设置角色
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult setEmployeeByURId(String companyId, String userId, String roleId) {
		CompanyEmployee companyEmployee = this.companyEmployeeService.selectByUserId(userId);
		if (companyEmployee == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//根据角色重新分配权限
		String id = companyEmployee.getId();
		this.employeePermissionService.deleteByEmployeeId(id);
		List<RolePermission> rolePermissionList= this.rolePermissionService.selectRolePermissionList(roleId);
		if(rolePermissionList!=null&&rolePermissionList.size()!=0){
			IIdGenerator idGenerator = new LwxfWorkerIdGenerator(1);
			for(RolePermission rolePermission:rolePermissionList){
				//重新生成主键ID
				rolePermission.setId(idGenerator.nextStringId());
				//用公司员工主键ID替换权限ID
				rolePermission.setRoleId(id);
			}
			this.employeePermissionService.addList(rolePermissionList);
		}
		MapContext mapContext = MapContext.newOne();
		mapContext.put("id", id);
		mapContext.put("roleId", roleId);
		return ResultFactory.generateRequestResult(this.companyEmployeeService.updateByMapContext(mapContext));
	}


}
