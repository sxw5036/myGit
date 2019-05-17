package com.lwxf.industry4.webapp.controller.app.factory.factoryEmployee;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryEmployee.FactoryEmployeeFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/3/28 0028 9:50
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/f", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class factoryEmployeeController {
	@Resource(name = "factoryEmployeeFacade")
	private FactoryEmployeeFacade factoryEmployeeFacade;

	/**
	 * 工厂各部门人数统计
	 *
	 * @param companyId
	 * @param
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/depts/0/employees")
	public String findEmployeeList(@PathVariable String companyId,
								   HttpServletRequest request) {
		JsonMapper jsonMapper = JsonMapper.createAllToStringMapper();
		String uid = request.getHeader("X-UID");
		String atoken = request.getHeader("X-ATOKEN");
		if (!LoginUtil.isLogin(uid, atoken)) {
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		RequestResult result = this.factoryEmployeeFacade.findDeptEmployeeCount(companyId);
		return jsonMapper.toJson(result);
	}

	/**
	 * 查询工厂部门列表
	 *
	 * @param companyId
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/depts")
	public String findDeptList(@PathVariable String companyId,
							   HttpServletRequest request) {
		JsonMapper jsonMapper = JsonMapper.createAllToStringMapper();
		String uid = request.getHeader("X-UID");
		String atoken = request.getHeader("X-ATOKEN");
		if (!LoginUtil.isLogin(uid, atoken)) {
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		RequestResult result = this.factoryEmployeeFacade.findDeptList(companyId);
		return jsonMapper.toJson(result);
	}

	/**
	 * 查询工厂端角色列表
	 *
	 * @param companyId
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/roles")
	public String findRoleList(@PathVariable String companyId,
							   HttpServletRequest request) {
		JsonMapper jsonMapper = JsonMapper.createAllToStringMapper();
		String uid = request.getHeader("X-UID");
		String atoken = request.getHeader("X-ATOKEN");
		if (!LoginUtil.isLogin(uid, atoken)) {
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		RequestResult result = this.factoryEmployeeFacade.findRoleList(companyId);
		return jsonMapper.toJson(result);
	}

	/**
	 * 查询工厂员工列表
	 * 部门、角色、姓名为条件查询
	 *
	 * @param companyId
	 * @param pageNum
	 * @param pageSize
	 * @param deptId
	 * @param roleId
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/employees")
	public String findEmployeeList(@PathVariable String companyId,
								   @RequestParam(required = false) Integer pageNum,
								   @RequestParam(required = false) Integer pageSize,
								   @RequestParam(required = false) String deptId,
								   @RequestParam(required = false) String roleId,
								   @RequestParam(required = false) String employeeName,
								   HttpServletRequest request) {
		JsonMapper jsonMapper = JsonMapper.createAllToStringMapper();
		String uid = request.getHeader("X-UID");
		String atoken = request.getHeader("X-ATOKEN");
		if (!LoginUtil.isLogin(uid, atoken)) {
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		if (null == pageSize) {
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if (null == pageNum) {
			pageNum = 1;
		}
		MapContext mapContext = MapContext.newOne();
		mapContext.put("companyId", companyId);
		if (LwxfStringUtils.isNotBlank(deptId)) {
			mapContext.put("deptId", deptId);
		}
		if (LwxfStringUtils.isNotBlank(roleId)) {
			mapContext.put("roleId", roleId);
		}
		if (LwxfStringUtils.isNotBlank(employeeName)) {
			mapContext.put("employeeName", employeeName);
		}
		RequestResult result = this.factoryEmployeeFacade.findEmployeeList(pageNum, pageSize, mapContext);
		return jsonMapper.toJson(result);
	}

	/**
	 * 查询员工详细信息
	 * @param companyId
	 * @param userId
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/employees/{userId}")
	public String findEmployeeInfo(@PathVariable String companyId,
								   @PathVariable String userId,
								   HttpServletRequest request){
		JsonMapper jsonMapper = JsonMapper.createAllToStringMapper();
		String uid = request.getHeader("X-UID");
		String atoken = request.getHeader("X-ATOKEN");
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		if (!LoginUtil.isLogin(uid, atoken)) {
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		RequestResult result=this.factoryEmployeeFacade.findEmployeeInfo(companyId,userId);
		return jsonMapper.toJson(result);
	}

	/**
	 * 查询公司下所有员工
	 * @param companyId
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/employees/all")

	public String findAllEmployees(@PathVariable String companyId,
								   HttpServletRequest request){
		JsonMapper jsonMapper = JsonMapper.createAllToStringMapper();
		String uid = request.getHeader("X-UID");
		String atoken = request.getHeader("X-ATOKEN");
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		if (!LoginUtil.isLogin(uid, atoken)) {
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		RequestResult result=this.factoryEmployeeFacade.findAllEmployees(companyId);
		return jsonMapper.toJson(result);
	}


	/**
	 * 补全店主的权限*(非常规,请谨慎使用)
	 */
	@GetMapping("/companies/rolePermissions")
	public RequestResult updateDianzhu(){
		return this.factoryEmployeeFacade.updateDianzhu();
	}
}
