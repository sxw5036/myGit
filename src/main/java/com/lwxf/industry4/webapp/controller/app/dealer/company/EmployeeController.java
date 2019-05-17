package com.lwxf.industry4.webapp.controller.app.dealer.company;

import io.rong.models.response.TokenResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.baseservice.rongcloud.RongCloudUtils;
import com.lwxf.industry4.webapp.baseservice.sms.yunpian.SmsUtil;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.dto.UserInfoObj;
import com.lwxf.industry4.webapp.common.enums.company.DealerEmployeeRole;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.company.EmployeeFacade;
import com.lwxf.industry4.webapp.facade.admin.factory.system.RoleFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：员工管理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/3 0003 13:25
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b/companies/{companyId}", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class EmployeeController extends BaseDealerController {
	@Resource(name = "employeeFacade")
	private EmployeeFacade employeeFacade;
	@Resource(name = "roleFacade")
	private RoleFacade roleFacade;

	//获取员工列表
	@GetMapping("/employees")
	public String selectEmployeeList(@RequestParam(required = false) Integer pageNum,
									 @RequestParam(required = false) Integer pageSize,
									 @RequestParam(required = false) Integer status,
									 @RequestParam(required = false) String condition,
									 @PathVariable String companyId,
									 HttpServletRequest request) {
		if(null == pageSize){
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if(null == pageNum){
			pageNum = 1;
		}
		JsonMapper resultMapper = new JsonMapper();
		String xp="bempl-baseInfo-edit";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
		MapContext mapContext = MapContext.newOne();
		mapContext.put("companyId", companyId);
		mapContext.put("status", status);
		if (LwxfStringUtils.isNotBlank(condition)) {
			mapContext.put("condition", condition);
		}
		RequestResult result = this.employeeFacade.selectEmployeeList(pageNum, pageSize, mapContext, request);
		return resultMapper.toJson(result);

	}

	//员工信息添加
	@PostMapping("/employees")
	private String addEmployee(@PathVariable String companyId,
							   @RequestBody MapContext mapContext,
							   HttpServletRequest request) {
		JsonMapper resultMapper = new JsonMapper();
		String xp="bempl-baseInfo-edit";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
		StringBuffer pwd = new StringBuffer();
		String name = mapContext.getTypedValue("name", String.class);
		String mobile = mapContext.getTypedValue("mobile", String.class);
		RequestResult result = this.employeeFacade.addEmployee(companyId, mapContext, pwd);
		//注册成功后发提醒信息
		if (Integer.parseInt((String) result.get("code")) == (200)) {
			SmsUtil.sendDealerInfoMessage(mobile, name, pwd.toString());
		}

		//处理融云token
		UserInfoObj data = (UserInfoObj) result.getData();
		if (data != null) {
			User user = data.getUser();
			TokenResult tokenResult = RongCloudUtils.registerUser(user);
			if (tokenResult != null) {
				String token = tokenResult.getToken();
				AppBeanInjector.userThirdInfoFacade.updateRongToken(user.getId(), token);
				UserThirdInfo userThirdInfo = data.getUserThirdInfo();
				userThirdInfo.setRongcloudToken(token);
			}
		}
		return resultMapper.toJson(result);
	}

	//员工辞退、离职操作
	@DeleteMapping("/employees/{userId}")
	public RequestResult deleteEmployee(String companyId,
										@PathVariable String userId,
										HttpServletRequest request) {
		String xp="bempl-baseInfo-update_status";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		return this.employeeFacade.deleteEmployee(companyId, userId, request);
	}

	//员工禁用和启用
	@PutMapping("/employees/{userId}/{status}")
	public RequestResult updateEmployeeState(@PathVariable String companyId,
											 @PathVariable String userId,
											 @PathVariable Integer status,
											 HttpServletRequest request) {
		String xp="bempl-baseInfo-disabled";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		return this.employeeFacade.updateEmployeeStatus(companyId, userId, status, request);
	}

	//员工修改
	@PutMapping("/employees/{userId}")
	public RequestResult updateEmployee(@PathVariable String companyId,
										@PathVariable String userId,
										@RequestBody MapContext mapContext,
										HttpServletRequest request
	) {
		String xp="bempl-baseInfo-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		return this.employeeFacade.updateEmployee(companyId, userId, mapContext, request);
	}

	//设置角色
	@PutMapping("/employees/{userId}/roles/{roleId}")
	public RequestResult setEmployeeByURId(@PathVariable String companyId,
										   @PathVariable String userId,
										   @PathVariable String roleId,
										   HttpServletRequest request) {
		String xp="bempl-baseInfo-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		return this.employeeFacade.setEmployeeByURId(companyId, userId, roleId);
	}

	//角色列表
	@GetMapping("/roles/{type}")
	public RequestResult fidRoleList(@PathVariable Integer type,
									 HttpServletRequest request) {

		String xp="bempl-baseInfo-view";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		String key = DealerEmployeeRole.SHOPKEEPER.getValue();
		return this.roleFacade.findListByType(type, key);
	}

	//员工详情
	@GetMapping("/employees/{userId}")
	public String findEmployeeMessage(@PathVariable String companyId,
									  @PathVariable String userId,
									  HttpServletRequest request) {
		JsonMapper resultMapper = new JsonMapper();
		String xp="bempl-baseInfo-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
		RequestResult result = this.employeeFacade.findEmployeeMessage(companyId, userId, request);
		return resultMapper.toJson(result);
	}
}
