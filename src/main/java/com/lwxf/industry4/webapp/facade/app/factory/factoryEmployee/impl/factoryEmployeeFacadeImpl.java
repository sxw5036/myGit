package com.lwxf.industry4.webapp.facade.app.factory.factoryEmployee.impl;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lwxf.commons.uniquekey.IIdGenerator;
import com.lwxf.commons.uniquekey.LwxfWorkerIdGenerator;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.company.EmployeeInfoService;
import com.lwxf.industry4.webapp.bizservice.company.EmployeePermissionService;
import com.lwxf.industry4.webapp.bizservice.dept.DeptMemberService;
import com.lwxf.industry4.webapp.bizservice.dept.DeptService;
import com.lwxf.industry4.webapp.bizservice.system.RolePermissionService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserBasisService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.common.enums.company.CompanyType;
import com.lwxf.industry4.webapp.common.enums.user.UserType;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.company.companyEmployeeDto.CompanyEmploeeDto;
import com.lwxf.industry4.webapp.domain.dto.companyEmployee.CompanyEmployeeDto;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.company.EmployeeInfo;
import com.lwxf.industry4.webapp.domain.entity.org.Dept;
import com.lwxf.industry4.webapp.domain.entity.org.DeptMember;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.system.RolePermission;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserBasis;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryEmployee.FactoryEmployeeFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/3/28 0028 9:53
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factoryEmployeeFacade")
public class factoryEmployeeFacadeImpl extends BaseFacadeImpl implements FactoryEmployeeFacade {
	@Resource(name = "companyEmployeeService")
	private CompanyEmployeeService companyEmployeeService;
	@Resource(name = "deptService")
	private DeptService deptService;
	@Resource(name = "deptMemberService")
	private DeptMemberService deptMemberService;
	@Resource(name = "roleService")
	private RoleService roleService;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "userBasisService")
    private UserBasisService userBasisService;
	@Resource(name = "companyService")
	private CompanyService companyService;
	@Resource(name = "employeeInfoService")
	private EmployeeInfoService employeeInfoService;
	@Resource(name = "employeePermissionService")
	private EmployeePermissionService employeePermissionService;
	@Resource(name="rolePermissionService")
	private RolePermissionService rolePermissionService;
	/**
	 * 查询公司所有部门的人数(饼状图统计)
	 *
	 * @param companyId
	 * @return
	 */
	@Override
	public RequestResult findDeptEmployeeCount(String companyId) {
		MapContext result = MapContext.newOne();
		Company company=this.companyService.findById(companyId);
		if(company==null){
			return ResultFactory.generateResNotFoundResult();
		}
		String companyName=company.getName();
		//查询公司所有部门
		List<Dept> deptList = this.deptService.findListByCompanyIdAndParent(companyId);
		List deptCount = new ArrayList();
		if (!deptList.isEmpty()) {
			for (Dept dept : deptList) {
				String deptName = dept.getName();
				Integer count;
				String deptId = dept.getId();
				//查询部门下的人数
				List<DeptMember> deptMemberList = this.deptMemberService.selectByDeptId(deptId);
				//判断是否有二级部门
				List<Dept> parentDept = this.deptService.findListByParentId(deptId);
				if (parentDept == null || parentDept.size() < 0) {
					count = deptMemberList.size();
				} else {
					Integer count1;//二级部门下的人数
					count = deptMemberList.size();
					for (Dept dept1 : parentDept) {
						String deptId1 = dept1.getId();
						List<DeptMember> deptMemberList1 = this.deptMemberService.selectByDeptId(deptId1);
						count1 = deptMemberList1.size();
						count = count + count1;
					}
				}
				Map map = new HashMap();
				map.put("deptName", deptName);
				map.put("count", count);
				deptCount.add(map);
			}
			result.put("deptCount", deptCount);
		}
		result.put("companyName",companyName);
		return ResultFactory.generateRequestResult(result);
	}

	/**
	 * 查询部门列表
	 *
	 * @param companyId
	 * @return
	 */
	@Override
	public RequestResult findDeptList(String companyId) {
		List<Dept> deptList = this.deptService.findListByCompanyId(companyId);
		return ResultFactory.generateRequestResult(deptList);
	}

	/**
	 * 查询工厂角色列表
	 *
	 * @param companyId
	 * @return
	 */
	@Override
	public RequestResult findRoleList(String companyId) {
		List<Role> roleList = this.roleService.findAllFactoryRole();
		return ResultFactory.generateRequestResult(roleList);
	}

	/**
	 * 根据条件查询员工列表
	 *
	 * @param pageNum
	 * @param pageSize
	 * @param mapContext
	 * @return
	 */
	@Override
	public RequestResult findEmployeeList(Integer pageNum, Integer pageSize, MapContext mapContext) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		Integer type= UserType.ADMIN.getValue();
		mapContext.put("type",type);
		paginatedFilter.setFilters(mapContext);
		PaginatedList<CompanyEmploeeDto> companyEmploeeDtoPaginatedList = this.companyEmployeeService.findEmployeeList(paginatedFilter);
		for(CompanyEmploeeDto companyEmploeeDto:companyEmploeeDtoPaginatedList.getRows()){
			String employeeId=companyEmploeeDto.getEmployeeId();
			List<String> deptNames=this.deptMemberService.findDeptNameByEmployeeId(employeeId);
			companyEmploeeDto.setDeptNames(deptNames);
		}
		MapContext result = MapContext.newOne();
		result.put("employeeList", companyEmploeeDtoPaginatedList.getRows());
		return ResultFactory.generateRequestResult(result, companyEmploeeDtoPaginatedList.getPagination());
	}

	/**
	 * 查询员工详情
	 * @param companyId
	 * @param userId
	 * @return
	 */
	@Override
	public RequestResult findEmployeeInfo(String companyId,String userId) {
		MapContext mapContext=MapContext.newOne();
		Company company=this.companyService.findById(companyId);
		if(company==null){
			return ResultFactory.generateResNotFoundResult();
		}
		List<MapContext> factoryUserDeptName = this.userService.findFactoryUserDeptName(companyId, userId);//部门信息
		//查询部门名称
		String listDeptName = "";
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
		mapContext.put("deptName",listDeptName);
		CompanyEmployee companyEmployee=this.companyEmployeeService.findOneByCompanyIdAndUserId(companyId,userId);
		if(companyEmployee!=null){
		   String roleId=companyEmployee.getRoleId();
		   String roleName=this.roleService.findById(roleId).getName();
		   mapContext.put("roleName",roleName);
		   String employeeId=companyEmployee.getId();
		   EmployeeInfo employeeInfo=this.employeeInfoService.findOneByEid(employeeId);
		   if(employeeInfo!=null){
			mapContext.put("wages",employeeInfo.getWages());
			mapContext.put("supervisorEvaluation",employeeInfo.getSupervisorEvaluation());
			mapContext.put("entryTime",employeeInfo.getEntryTime());
			mapContext.put("status",employeeInfo.getStatus());
		   }else {
		   	mapContext.put("supervisorEvaluation","");
			mapContext.put("entryTime","");
			mapContext.put("status","");
			mapContext.put("wages",0);

		   }
		}else {
			return ResultFactory.generateResNotFoundResult();
		}
		User user=AppBeanInjector.userService.findByUserId(userId);
		if(user==null){
			return ResultFactory.generateResNotFoundResult();
		}
		UserBasis userBasis=this.userBasisService.findByUserId(userId);
		if(userBasis!=null){
			mapContext.put("identityNumber",userBasis.getIdentityNumber());
			mapContext.put("mobile",userBasis.getContactNumber());
		}else {
			mapContext.put("identityNumber","");
			mapContext.put("mobile","");
		}
		mapContext.put("avatar", WebUtils.getDomainUrl()+user.getAvatar());
		mapContext.put("companyName",company.getName());
		mapContext.put("sex",user.getSex());
		mapContext.put("name",user.getName());
		mapContext.put("isOperator","1");//是否操作员
		return ResultFactory.generateRequestResult(mapContext);
	}

	@Override
	public RequestResult findAllEmployees(String companyId) {
		Integer type= CompanyType.MANUFACTURERS.getValue();
		Company company=this.companyService.findCompanyByType(type);
		String companyid=company.getId();
		List<Map> companyEmployees=this.companyEmployeeService.findAllEmployeesByCid(companyid);
		return ResultFactory.generateRequestResult(companyEmployees);
	}

	/**
	 * 补全店主权限
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateDianzhu() {
		List<String> ids=this.companyEmployeeService.findAllDianzhuId();//查询所有的店主id
		List<RolePermission> rolePermissions=this.rolePermissionService.findAllDianzhuRolePer();//查询所有的店主权限
		for(String id:ids){
				IIdGenerator idGenerator = new LwxfWorkerIdGenerator(1);
				for(RolePermission rolePermission:rolePermissions){
					//重新生成主键ID
					rolePermission.setId(idGenerator.nextStringId());
					//用公司员工主键ID替换权限ID
					rolePermission.setRoleId(id);
				}
				this.employeePermissionService.addList(rolePermissions);

		}
		return ResultFactory.generateSuccessResult();
	}
}
