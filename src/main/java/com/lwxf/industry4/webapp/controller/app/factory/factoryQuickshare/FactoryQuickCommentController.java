package com.lwxf.industry4.webapp.controller.app.factory.factoryQuickshare;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.quickshare.QuickshareCommentFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：快享评论的添加和删除
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/7 0007 9:23
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value="/app/f", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryQuickCommentController {
	@Resource(name = "quickshareCommentFacade")
	private QuickshareCommentFacade quickshareCommentFacade;

	/**添加评论
	 *
	 * @param quickshareId
	 * @param mapContext
	 * @return
	 */
	@PostMapping("/quickshares/{quickshareId}/quickshareComments")
	public RequestResult addQuickshareComment(@PathVariable String quickshareId,
											  @RequestBody MapContext mapContext,
											  HttpServletRequest request){

		String uid=request.getHeader("X-UID");
		String token=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,token)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		return this.quickshareCommentFacade.addQuickshareComment(quickshareId,mapContext,request);
	}

	/**
	 * 删除评论
	 * @param quickshareId
	 * @param commentId
	 * @param request
	 * @return
	 */
	@DeleteMapping("/quickshares/{quickshareId}/quickshareComments/{commentId}")
	public RequestResult deleteQuickshareComment(@PathVariable String quickshareId,
												 @PathVariable String commentId,
												 HttpServletRequest request){
		String uid=request.getHeader("X-UID");
		String token=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,token)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		return this.quickshareCommentFacade.deleteQuickshareComment(quickshareId,commentId,request);
	}
}
