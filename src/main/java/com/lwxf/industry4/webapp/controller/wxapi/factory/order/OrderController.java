package com.lwxf.industry4.webapp.controller.wxapi.factory.order;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.json.JacksonObjectMapper;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyDtoForApp;
import com.lwxf.industry4.webapp.domain.dto.customorder.WxCustomOrderDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.WxCustomerOrderInfoDto;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.wxapi.factory.order.WxOrderFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/6/15 0015 10:25
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Api(value = "WxOrderController",tags ={"F端微信小程序接口: 订单管理"} )
@RestController(value = "WxOrderController")
@RequestMapping(value = "/wxapi/f", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class OrderController {

	@Resource(name = "wxOrderFacade")
	private WxOrderFacade wxOrderFacade;

	/**
	 * 订单列表
	 * @param pageNum
	 * @param pageSize
	 * @param condation
	 * @param startTime
	 * @param endTime
	 * @param cityId
	 * @param dealerId
	 * @return
	 */
	@ApiOperation(value = "查询订单列表接口",notes = "查询订单列表接口",response = WxCustomOrderDto.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum",value = "页码",dataType = "integer",paramType = "query"),
			@ApiImplicitParam(name = "pageSize",value = "页面条数",dataType = "integer",paramType = "query"),
			@ApiImplicitParam(name = "condation",value = "手机号、客户姓名、订单编号",dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "cityId",value = "地区id ",dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "startTime",value = "开始时间 2019-06-01",dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "endTime",value = "结束时间 2019-06-10",dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "customerId",value = "经销商id",dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "dealerId",value = "终端客户id",dataType = "string",paramType = "query")
	})
	@GetMapping("/branchs/orders")
	public String findWxOrderList(@RequestParam(required = false) Integer pageNum,
								  @RequestParam(required = false) Integer pageSize,
								  @RequestParam(required = false) String condation,
								  @RequestParam(required = false) String startTime,
								  @RequestParam(required = false) String endTime,
								  @RequestParam(required = false) String cityId,
								  @RequestParam(required = false) String customerId,
								  @RequestParam(required = false) String dealerId){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		String atoken= WebUtils.getHttpServletRequest().getHeader("X-ATOKEN");
		MapContext mapInfo = LoginUtil.checkLogin(atoken);
		String uid =mapInfo.get("userId")==null?null:mapInfo.get("userId").toString();
		if(uid==null){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		String branchid =mapInfo.get("branchId")==null?null:mapInfo.get("branchId").toString();
		if (null == pageSize) {
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if (null == pageNum) {
			pageNum = 1;
		}
		MapContext mapContext=MapContext.newOne();
		if(LwxfStringUtils.isNotBlank(condation)){
			mapContext.put("condation",condation);
		}
		if(LwxfStringUtils.isNotBlank(startTime)){
			mapContext.put("startTime",startTime);
		}
		if(LwxfStringUtils.isNotBlank(endTime)){
			mapContext.put("endTime",endTime);
		}
		if(LwxfStringUtils.isNotBlank(cityId)){
			mapContext.put("cityId",cityId);
		}
		if(LwxfStringUtils.isNotBlank(customerId)){
			mapContext.put("customerId",customerId);
		}
		if(LwxfStringUtils.isNotBlank(dealerId)){
			mapContext.put("dealerId",dealerId);
		}
		RequestResult result=this.wxOrderFacade.findWxOrderList(branchid,pageNum,pageSize,mapContext);
		return jsonMapper.toJson(result);

	}


	@ApiOperation(value = "查询订单详情接口",notes = "查询订单详情接口",response = WxCustomerOrderInfoDto.class)
	@GetMapping("/branchs/orders/{orderId}")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "orderId",value = "订单id",dataType = "string",paramType = "path",required = true)
	})
	public String findWxOrderInfo(@PathVariable String orderId){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		String atoken= WebUtils.getHttpServletRequest().getHeader("X-ATOKEN");
		MapContext mapInfo = LoginUtil.checkLogin(atoken);
		String uid =mapInfo.get("userId")==null?null:mapInfo.get("userId").toString();
		if(uid==null){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		String branchid =mapInfo.get("branchId")==null?null:mapInfo.get("branchId").toString();
		RequestResult result=this.wxOrderFacade.findWxOrderInfo(branchid,orderId);
		return jsonMapper.toJson(result);

	}

}
