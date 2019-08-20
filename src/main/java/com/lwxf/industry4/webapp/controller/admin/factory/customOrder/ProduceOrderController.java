package com.lwxf.industry4.webapp.controller.admin.factory.customOrder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.facade.admin.factory.order.ProduceOrderFacade;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/4/8/008 15:45
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/produceorder",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
@Api(value = "ProduceOrderController",tags = {"生产订单管理接口"})
public class ProduceOrderController {

	@Resource(name = "produceOrderFacade")
	private ProduceOrderFacade productOrderFacade;

//	/**
//	 * 查询生产订单列表
//	 * @param pageNum
//	 * @param pageSize
//	 * @param orderNo
//	 * @param no
//	 * @param state
//	 * @return
//	 */
//	@ApiOperation(value = "查询生产订单列表",notes = "查询生产订单列表")
//	@GetMapping
//	private RequestResult findProduceOrderList(@RequestParam(required = false,defaultValue = "1")Integer pageNum,@RequestParam(required = false,defaultValue = "10")Integer pageSize
//	,@RequestParam(required = false) String orderNo,@RequestParam(required = false)String no,@RequestParam(required = false)Integer state
//	){
//		return null;
//	}
	/**
	 * 外协信息统计
	 *
	 * @param
	 * @return
	 */
	@GetMapping("/count")
	@ApiOperation(value = "外协信息统计",notes = "外协信息统计")
	private RequestResult findCoordinationCount() {
		String branchId= WebUtils.getCurrBranchId();
		return this.productOrderFacade.findCoordinationCount(branchId);
	}

	@GetMapping("/overview")
	@ApiOperation(value = "生产管理信息统计",notes = "生产管理信息统计")
	private RequestResult findProduceOrderOverview(){
		return this.productOrderFacade.findProduceOrderOverview();
	}
}
