package com.lwxf.industry4.webapp.controller.app.dealer.contentmng;

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
import com.lwxf.industry4.webapp.facade.app.dealer.contentmng.ContentsFacade;

/**
 * 功能：学习帮助相关接口
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/3/27 0027 13:02
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b/contenttypes/{typeIdOrCode}/contents", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class ContentsController {
	@Resource(name="contentsFacade")
	private ContentsFacade contentsFacade;

	/**
	 * 列表查询
	 * @param typeIdOrCode
	 * @param request
	 * @return
	 */
	@GetMapping
	public String findContentsList(@PathVariable String typeIdOrCode,
								   HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		String uid=request.getHeader("X-UID");
		String token=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,token)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}

		RequestResult result=this.contentsFacade.findContentsList(typeIdOrCode,request);
		return jsonMapper.toJson(result);
	}

	/**
	 * 详情查看
	 * @param contentsId
	 * @param request
	 * @return
	 */
	@GetMapping("/{contentsId}")
	public String findContentsMessage(@PathVariable String contentsId,
									  HttpServletRequest request){
	JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
	String uid=request.getHeader("X-UID");
	String token=request.getHeader("X-ATOKEN");
	if(!LoginUtil.isLogin(uid,token)){
		return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
	}
	RequestResult result=this.contentsFacade.findContentMessage(contentsId);
	return jsonMapper.toJson(result);
}
}
