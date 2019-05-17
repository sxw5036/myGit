package com.lwxf.industry4.webapp.controller.admin.factory.system;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.admin.factory.system.LogisticsCompanyFacade;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/26/026 15:59
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/company/logistics",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class LogisticsCompanyController {
	@Resource(name = "logisticsCompanyFacade")
	private LogisticsCompanyFacade logisticsCompanyFacade;

	@GetMapping("{orderId}")
	private RequestResult findList(@PathVariable String orderId){
		return this.logisticsCompanyFacade.findList(orderId);
	}
}
