package com.lwxf.industry4.webapp.controller.app.dealer.company;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.user.UserFacade;
import com.lwxf.industry4.webapp.facade.user.UserScheduleItemFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 *
 * @create：2018/12/15 0015 10:54
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b/users/0", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class CompanyUserController extends BaseDealerController {
	@Resource(name="userFacade")
	private UserFacade userFacade;
	@Resource(name="userScheduleItemFacade")
	private UserScheduleItemFacade userScheduleItemFacade;

//个人信息展示
	@GetMapping("/{userId}")
	public String userMessageShow(@PathVariable String userId,
								  HttpServletRequest request){
		JsonMapper resultMapper=new JsonMapper();
		String xp="usersetmng-userinfo-view";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return resultMapper.toJson(result);
		}
		return resultMapper.toJson(this.userFacade.findUserMessageById(userId));
	}

//个人信息修改
   @PutMapping("/{userId}")
   public RequestResult updateUserMessage(@PathVariable String userId,
										  @RequestBody MapContext mapContext,
										  HttpServletRequest request){
	   String xp="usersetmng-userinfo-edit";
	   RequestResult result = this.validUserPermission(request,xp);
	   if(result!=null){
		   return result;
	   }
       if(!userId.equals(request.getHeader("X-UID"))){
       	return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
	   }
		return this.userFacade.updateUserMessage(userId,mapContext);
   }
//修改个人密码
   @PutMapping("/{userId}/password")
   public RequestResult updatePassword(@PathVariable String userId,
									   @RequestBody MapContext mapContext,
									   HttpServletRequest request){
	   String xp="usersetmng-userinfo-edit";
	   RequestResult result = this.validUserPermission(request,xp);
	   if(result!=null){
		   return result;
	   }
		String uid=request.getHeader("X-UID");
		if(!uid.equals(userId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}

		return this.userFacade.updateUserPassword(mapContext);
   }

	/**
	 * 个人日程列表
	 * @param userId
	 * @param time
	 * @param request
	 * @return
	 */
    @GetMapping("/{userId}/userScheduleItems")
	public String findUserScheduleItems (@PathVariable String userId,
										 @RequestParam String time,
										 HttpServletRequest request) {
	     JsonMapper resultMapper=new JsonMapper();
		String xp="bschedulemng-myschedule-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
	     RequestResult result=this.userScheduleItemFacade.findUserScheduleItems(userId,time,request);
	     return resultMapper.toJson(result);
	}

	/**
	 * 日程添加
	 * @param userId
	 * @param mapContext
	 * @param request
	 * @return
	 */
	@PostMapping("/{userId}/userScheduleItems/add")
	public RequestResult addUserScheduleItems(@PathVariable String userId,
											  @RequestParam String scheduleTime,
											  @RequestBody MapContext mapContext,
											  HttpServletRequest request){
		String xp="bschedulemng-myschedule-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		if(LwxfStringUtils.isBlank(scheduleTime)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_REQUEST_PARAM_ERROR_10000,AppBeanInjector.i18nUtil.getMessage("BIZ_REQUEST_PARAM_ERROR_10000"));
		}
		String uid=request.getHeader("X-UID");
		if(!uid.equals(userId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
	return this.userScheduleItemFacade.addUserScheduleItem(userId,scheduleTime,mapContext);
	}

	/**
	 * 日程修改
	 * @param userId
	 * @param scheduleItemId
	 * @param mapContext
	 * @param request
	 * @return
	 */
	@PutMapping("/{userId}/userScheduleItems/{scheduleItemId}")
	public RequestResult updateUserScheduleItem(@PathVariable String userId,
												@PathVariable String scheduleItemId,
												@RequestBody MapContext mapContext,
												HttpServletRequest request){
		String xp="bschedulemng-myschedule-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		String uid=request.getHeader("X-UID");
		if(!uid.equals(userId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
  return this.userScheduleItemFacade.updateUserScheduleItem(scheduleItemId,mapContext);
	}

	/**
	 * 日程删除
	 * @param userId
	 * @param scheduleItemId
	 * @param request
	 * @return
	 */
	@DeleteMapping("/{userId}/userScheduleItems/{scheduleItemId}")
	public RequestResult deleteUserScheduleItem(@PathVariable String userId,
												@PathVariable String scheduleItemId,
												HttpServletRequest request){
		String xp="bschedulemng-myschedule-update_status";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		String uid=request.getHeader("X-UID");
		if(!uid.equals(userId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		return this.userScheduleItemFacade.deleteUserScheduleItem(scheduleItemId);
	}

	/**
	 * 手机账号注册
	 * @param mapContext
	 * @return
	 */
	@PostMapping("/register")
	public RequestResult userRegister(@RequestBody MapContext mapContext){
		return this.userFacade.BuserRegister(mapContext);
	}
	/**
	 *注册验证码验证
	 */
    @PostMapping("/registerAuthCode")
	public RequestResult registerAuthCode(@RequestBody MapContext mapContext){
    	RequestResult result=this.userFacade.registerAuthCode(mapContext);
    	return result;
	}
}
