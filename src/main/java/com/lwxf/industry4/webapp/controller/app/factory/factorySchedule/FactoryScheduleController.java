package com.lwxf.industry4.webapp.controller.app.factory.factorySchedule;

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
import com.lwxf.industry4.webapp.facade.app.factory.factorySchedule.FactoryScheduleFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：日程管理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/4 0004 11:31
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/f/users/0",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryScheduleController {
	@Resource(name = "factoryScheduleFacade")
	private FactoryScheduleFacade factoryScheduleFacade;

	/**
	 * 个人日程列表
	 *
	 * @param time
	 * @param request
	 * @return
	 */
	@GetMapping("/userScheduleItems")
	public String findUserScheduleItems(@RequestParam String time,
										HttpServletRequest request) {
		JsonMapper resultMapper = JsonMapper.createAllToStringMapper();
		String userId = request.getHeader("X-UID");
		RequestResult result = this.factoryScheduleFacade.findUserScheduleItems(userId, time, request);
		return resultMapper.toJson(result);
	}

	/**
	 * 日程添加
	 * @param scheduleTime
	 * @param mapContext
	 * @param request
	 * @return
	 */
	@PostMapping("/userScheduleItems")
	public RequestResult addUserScheduleItems(@RequestParam String scheduleTime,
											  @RequestBody MapContext mapContext,
											  HttpServletRequest request) {
		String userId=request.getHeader("X-UID");
		String xp=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(userId,xp)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED,AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		if(LwxfStringUtils.isBlank(scheduleTime)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_REQUEST_PARAM_ERROR_10000, AppBeanInjector.i18nUtil.getMessage("BIZ_REQUEST_PARAM_ERROR_10000"));
		}
		return this.factoryScheduleFacade.addUserScheduleItem(userId,scheduleTime,mapContext);
	}
	/**
	 * 日程修改
	 * @param scheduleItemId
	 * @param mapContext
	 * @param request
	 * @return
	 */
	@PutMapping("/userScheduleItems/{scheduleItemId}")
	public RequestResult updateUserScheduleItem(@PathVariable String scheduleItemId,
												@RequestBody MapContext mapContext,
												HttpServletRequest request){

		String userId=request.getHeader("X-UID");
		String xp=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(userId,xp)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED,AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		return this.factoryScheduleFacade.updateUserScheduleItem(scheduleItemId,mapContext);
	}
	/**
	 * 日程删除
	 * @param scheduleItemId
	 * @param request
	 * @return
	 */
	@DeleteMapping("/userScheduleItems/{scheduleItemId}")
	public RequestResult deleteUserScheduleItem(@PathVariable String scheduleItemId,
												HttpServletRequest request){
		String userId=request.getHeader("X-UID");
		String xp=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(userId,xp)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED,AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		return this.factoryScheduleFacade.deleteUserScheduleItem(scheduleItemId);
	}
}