package com.lwxf.industry4.webapp.controller.app.dealer.base;

import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.LwxfAssert;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.company.EmployeePermission;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 功能：
 *
 * @author：renzhongshan(d3shan@126.com)
 * @create：2019/2/26 0026 16:29
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public abstract class BaseDealerController {
	/**
	 * 验证用户权限，需要验证的接口先调用该方法
	 * @param request
	 * @return
	 */
	protected RequestResult validUserPermission(HttpServletRequest request,String xp){
		String uid = request.getHeader("X-UID");
		String cid = request.getHeader("X-CID");
		String atoken = request.getHeader("X-ATOKEN");
		if(LwxfStringUtils.isBlank(xp)){
			LwxfAssert.isTrue(false,"请求头中未设置X-P");
			return ResultFactory.generateErrorResult(ErrorCodes.LOGIN_FAIL_90000, AppBeanInjector.i18nUtil.getMessage("LOGIN_FAIL_90000"));
		}
		boolean pass = Boolean.TRUE.booleanValue();
		// 1. 验证是否为合法用户，不是合法用户返回资源未找到
		User user = AppBeanInjector.userService.findById(uid);
		if (null==user){
			LwxfAssert.isTrue(false,"未找到用户信息");
			return ResultFactory.generateErrorResult(ErrorCodes.LOGIN_FAIL_90000, AppBeanInjector.i18nUtil.getMessage("LOGIN_FAIL_90000"));
		}
		// 从请求头中获取用户id和公司id，和atoken，验证用户是否为公司员工；验证用户token是否正确
		CompanyEmployee emp = AppBeanInjector.companyEmployeeService.findOneByCompanyIdAndUserId(cid, uid);
		if (null==emp){
			LwxfAssert.isTrue(false,"用户不是员工");
			return ResultFactory.generateErrorResult(ErrorCodes.LOGIN_FAIL_90000, AppBeanInjector.i18nUtil.getMessage("LOGIN_FAIL_90000"));
		}
		UserThirdInfo userThirdInfo = AppBeanInjector.userThirdInfoService.findByUserId(uid);
		if (null==userThirdInfo){
			LwxfAssert.isTrue(false,"用户的第三方信息未设置");
			return ResultFactory.generateErrorResult(ErrorCodes.LOGIN_FAIL_90000, AppBeanInjector.i18nUtil.getMessage("LOGIN_FAIL_90000"));
		}
		String appToken = userThirdInfo.getAppToken();
		if (!appToken.equals(atoken)){
			LwxfAssert.isTrue(false,"用户的A-TOKEN错误");
			return ResultFactory.generateErrorResult(ErrorCodes.LOGIN_FAIL_90000, AppBeanInjector.i18nUtil.getMessage("LOGIN_FAIL_90000"));
		}
		// 2. 验证用户的操作权限，没有操作权限，返回没有操作权限的错误
		String[] xpArr = xp.split("-");
		EmployeePermission empPermission = AppBeanInjector.employeePermissionService.findByEmpIdAndkey(emp.getId(), xpArr[0], xpArr[1]);
		if (null==empPermission){
			LwxfAssert.isTrue(false,"用户权限未找到");
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage(ErrorCodes.BIZ_NO_PERMISSION_10003));
		}
		String operations = empPermission.getOperations();
		if(!Arrays.asList(operations.split(",")).contains(xpArr[2])){
			pass = Boolean.FALSE.booleanValue();
		}

		// pass = 验证用户权限的结果
		if(pass == Boolean.FALSE.booleanValue()){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage(ErrorCodes.BIZ_NO_PERMISSION_10003));
		}
		// 如果验证通过，返回null，每个接口中判断该方法返回的结果，如果不为null，直接返回结果
		return null;
	}
}
