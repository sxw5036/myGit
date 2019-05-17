package com.lwxf.industry4.webapp.controller.app.factory.factoryQuickshare;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.quickshare.QuicksharePraiseFacade;

/**
 * 功能：快享点赞、取消点赞
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/7 0007 9:25
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value="/app/f", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryQuickPraiseController {
	@Resource(name="quicksharePraiseFacade")
	private QuicksharePraiseFacade quicksharePraiseFacade;

	/**
	 * 点赞/取消点赞
	 * @param quickshareId
	 * @param request
	 * @return
	 */
	@PutMapping("/quickshares/{quickshareId}/praises")
	public RequestResult updateQuicksharePraise(@PathVariable String quickshareId,
												HttpServletRequest request){
		String uid=request.getHeader("X-UID");
		String token=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,token)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		return this.quicksharePraiseFacade.updateQuicksharePraise(quickshareId,request);

	}
}
