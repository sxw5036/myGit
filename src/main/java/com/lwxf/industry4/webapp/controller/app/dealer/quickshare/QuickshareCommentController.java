package com.lwxf.industry4.webapp.controller.app.dealer.quickshare;

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
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/3/6 0006 15:56
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value="/app/b", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class QuickshareCommentController  {
	@Resource(name = "quickshareCommentFacade")
	private QuickshareCommentFacade quickshareCommentFacade;

	/**
	 * 评论列表（暂时不需要）
	 * @param quickshareId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/quickshares/{quickshareId}/quickshareComments")
	public String selectQuickshareCommentList(@PathVariable String quickshareId,
											  @RequestParam(required = false) Integer pageNum,
											  @RequestParam(required = false) Integer pageSize,
											  HttpServletRequest request){
		JsonMapper resultMapper=JsonMapper.createAllToStringMapper();
		if(null == pageSize){
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if(null == pageNum){
			pageNum = 1;
		}
		RequestResult result=this.quickshareCommentFacade.selectQuickshareCommentList(quickshareId,pageNum,pageSize);
		return resultMapper.toJson(result);
	}

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
