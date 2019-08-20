package com.lwxf.industry4.webapp.controller.wxapi.dealer.index;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.domain.dto.product.ProductDto;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.wxapi.dealer.Index.IndexFacade;
import com.lwxf.mybatis.utils.MapContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Api(value = "IndexController", tags = {"B端微信小程序接口:经销商首页"})
@RestController
@RequestMapping(value = "/wxapi/b/dealer", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class IndexController {

	@Resource(name = "wxDealerIndexFacade")
	private IndexFacade indexFacade;

	/**
	 * 查询产品列表接口
	 *
	 * @return
	 */
	@GetMapping("/index")
	@ApiOperation(value = "查询产品列表接口", response = ProductDto.class)
	private RequestResult viewIndex(HttpServletRequest request) {
		String atoken = request.getHeader("X-ATOKEN");
		MapContext mapInfo = LoginUtil.checkLogin(atoken);
		String uid = mapInfo.get("userId") == null ? null : mapInfo.get("userId").toString();
		if (uid == null) {
			return ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		return this.indexFacade.viewIndex(mapInfo.get("companyId").toString());
	}
}
