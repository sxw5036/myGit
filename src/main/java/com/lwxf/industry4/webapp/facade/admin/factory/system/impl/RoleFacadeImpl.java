package com.lwxf.industry4.webapp.facade.admin.factory.system.impl;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.industry4.webapp.baseservice.tsmanager.TSManualData;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.EmployeePermissionService;
import com.lwxf.industry4.webapp.bizservice.system.MenusService;
import com.lwxf.industry4.webapp.bizservice.system.OperationsService;
import com.lwxf.industry4.webapp.bizservice.system.RolePermissionService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.OperateEventEnum;
import com.lwxf.industry4.webapp.common.enums.system.MenusDisabled;
import com.lwxf.industry4.webapp.common.enums.system.MenusType;
import com.lwxf.industry4.webapp.common.enums.system.OperationsType;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.system.Menus;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.system.RolePermission;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.system.RoleFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/14/014 15:47
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("roleFacade")
public class RoleFacadeImpl extends BaseFacadeImpl implements RoleFacade {
	@Resource(name = "roleService")
	private RoleService roleService;
	@Resource(name = "companyEmployeeService")
	private CompanyEmployeeService companyEmployeeService;
	@Resource(name="rolePermissionService")
	private RolePermissionService rolePermissionService;
	@Resource(name = "menusService")
	private MenusService menusService;
	@Resource(name = "operationsService")
	private OperationsService operationsService;
	@Resource(name = "employeePermissionService")
	private EmployeePermissionService employeePermissionService;

	@Override
	public RequestResult findListByType(Integer type,String key) {
		return ResultFactory.generateRequestResult(this.roleService.findListByType(type,key,WebUtils.getCurrBranchId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addRoles(Role role) {
		//判断key 是否重复
		Role oldRole = this.roleService.selectByKey(role.getKey(),WebUtils.getCurrBranchId());
		if(oldRole!=null){
			MapContext mapContext = MapContext.newOne();
			mapContext.put("key",AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,mapContext);
		}
		role.setBranchId(WebUtils.getCurrBranchId());
		this.roleService.add(role);
		return ResultFactory.generateRequestResult(role);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updataRoles(MapContext mapContext, String id) {
		//判断角色是否存在
		Role role = this.roleService.findById(id);
		if(role==null){
			return ResultFactory.generateResNotFoundResult();
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		this.roleService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.roleService.findById(id));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteRoles(String id) {
		Role role = this.roleService.findById(id);
		if(role==null){
			return ResultFactory.generateSuccessResult();
		}
		//判断角色是否被使用
		List<CompanyEmployee> companyEmployees = this.companyEmployeeService.findListByRoleId(id);
		if(companyEmployees==null||companyEmployees.size()==0){
			//角色下的权限
			this.rolePermissionService.deleteByRoleId(id);
			//删除角色
			this.roleService.deleteById(id);
			return ResultFactory.generateSuccessResult();

		}
		return ResultFactory.generateErrorResult(ErrorCodes.BIZ_RES_BE_IN_USE_10006,AppBeanInjector.i18nUtil.getMessage("BIZ_RES_BE_IN_USE_10006"));
	}

	@Override
	public RequestResult findRolePermission(String id) {
		return ResultFactory.generateRequestResult(this.rolePermissionService.selectRolePermissionList(id));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateRoleMenus(String roleId, List<RolePermission> rolePermissions) {
		//判断角色是否存在
		Role role = this.roleService.findById(roleId);
		if(role==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断菜单是否存在
		Set<String> menus = new HashSet<String>();
		for(RolePermission rolePermission:rolePermissions){
			menus.add(rolePermission.getMenuKey());
			menus.add(rolePermission.getModuleKey());
		}
		if(menus.size()!=0){
			MapContext mapContext = MapContext.newOne();
			mapContext.put("keys",menus);
			List<Menus> menusList = this.menusService.findListByMapContext(mapContext);
			if (menusList.size()!=menus.size()){
				return ResultFactory.generateResNotFoundResult();
			}
		}

		// 手动控制事务业务
		TSManualData tsManualData = WebUtils.getTSManualData();
		tsManualData.setParams(roleId);
		tsManualData.setClazz(RolePermission.class);
		tsManualData.setEvent(OperateEventEnum.ROLE_PERMISSION_MD.getValue());

		this.rolePermissionService.updateRolePermissions(roleId,rolePermissions);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findAllByType(Integer type) {
		MapContext filter = new MapContext();
		filter.put("type",type);
		filter.put(WebConstant.KEY_ENTITY_DISABLED,MenusDisabled.UNDISABLED.getValue());
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_PRELOAD_MENUS,this.menusService.findListByMapContext(filter));
		if(type.equals(MenusType.FACTORY_BACKSTAGE.getValue())){//判断是否是厂家后台
			filter.put("type",Arrays.asList(OperationsType.PUBLIC.getValue(),OperationsType.FACTORY.getValue()));
		}else if(type.equals(MenusType.DEALER_BACKSTAGE.getValue())||type.equals(MenusType.DEALER_APP.getValue())){//判断是否是经销商菜单
			filter.put("type",Arrays.asList(OperationsType.PUBLIC.getValue(),OperationsType.DEALER.getValue()));
		}else{
			return ResultFactory.generateResNotFoundResult();
		}
		mapContext.put(WebConstant.KEY_PRELOAD_OPERATIONS,this.operationsService.findListByMapContext(filter));
		return ResultFactory.generateRequestResult(mapContext);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult settingDefault() {
//		String[] userIds = new String[]{"4vqav3l0av40","4v14mj3ampdx","4v14mj3ampdy","4v14mj3ampdz","4v14mj3ampe0","4v1dagld44cg","4v1daglfm0ow","4v1daglfm0ox","4v1daglfm0oy","4vu5azlsbri8","4v1daglfm0p1","4v1daglfm0p2","4v1daglfm0p3"};
		List<Menus> menusList = this.menusService.findAllByTypeAndDisabled(MenusType.FACTORY_BACKSTAGE.getValue(),MenusDisabled.UNDISABLED.getValue());
//		Set<Role> roleSet = new HashSet<Role>();
//		for (int i = 0; i < userIds.length; i++) {
//			CompanyEmployee companyEmployee = this.companyEmployeeService.selectByUserId(userIds[i]);
//			Role role = this.roleService.findById(companyEmployee.getRoleId());
//			roleSet.add(role);
//		}
		String[] roleIds = new String[]{"52l1fojmje2w","4k8e27rahm2r","52l1fojmje2u","4k8cbm6hbswf","52l1fojmje2r"};
		for(String roleId:roleIds){
			for(Menus menus:menusList){
				RolePermission rolePermission = new RolePermission();
				rolePermission.setShow(1);
				rolePermission.setRoleId(roleId);
				rolePermission.setMenuKey(menus.getKey());
				rolePermission.setModuleKey(menus.getKey());
				rolePermission.setOperations("view,approval,edit,submit,update_status,enabled,disabled,delete");
				this.rolePermissionService.add(rolePermission);
			}
		}

//		for (int i = 0; i < userIds.length; i++) {
//			CompanyEmployee companyEmployee = this.companyEmployeeService.selectByUserId(userIds[i]);
//			//查询新角色下的权限
//			List<RolePermission> rolePermissions = this.rolePermissionService.selectRolePermissionList(companyEmployee.getRoleId());
//			if(rolePermissions!=null&&rolePermissions.size()!=0){
//				for(RolePermission rolePermission:rolePermissions){
//					//重新生成主键ID
//					rolePermission.setId(AppBeanInjector.idGererateFactory.nextStringId());
//					//用公司员工主键ID替换权限ID
//					rolePermission.setRoleId(companyEmployee.getId());
//				}
//				this.employeePermissionService.addList(rolePermissions);
//			}
//		}
		return null;
	}
}
