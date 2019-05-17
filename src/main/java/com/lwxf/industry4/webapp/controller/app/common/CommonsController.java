package com.lwxf.industry4.webapp.controller.app.common;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.common.CityAreaFacade;

/**
 * 功能：APP端的公共api定义
 *
 * @author：renzhongshan(d3shan@126.com)
 * @create：2018/12/11 0011 13:24
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/commons", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class CommonsController {
	@Resource(name = "cityAreaFacade")
	private CityAreaFacade cityAreaFacade;
	@GetMapping(value = "/cities")
	public String getAllCities(){
		RequestResult result = this.cityAreaFacade.findALl();
		JsonMapper mapper = new JsonMapper();
		return mapper.toJson(result);
	}
}
