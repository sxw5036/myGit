package com.lwxf.industry4.webapp.controller.app.factory.factoryfinishedstock;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryfinishedstock.FactoryFinishedstockFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/18 0018 10:34
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/f/companies/{companyId}", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryFinishedStockController {
	@Resource(name = "factoryFinishedstockFacade")
	private FactoryFinishedstockFacade factoryFinishedstockFacade;


	/**
	 * 入库列表信息查询
	 *
	 * @param companyId
	 * @param pageNum
	 * @param pageSize
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @param orderNo
	 * @param request
	 * @return
	 */
	@GetMapping("/finishedStocks")
	public String findFactoryFinishedstockList(@PathVariable String companyId,
											   @RequestParam(required = false) Integer pageNum,
											   @RequestParam(required = false) Integer pageSize,
											   @RequestParam(required = false) String isIn,
											   @RequestParam(required = false) String startTime,
											   @RequestParam(required = false) String endTime,
											   @RequestParam(required = false) String type,
											   @RequestParam(required = false) String orderNo,
											   HttpServletRequest request) {
		JsonMapper jsonMapper = JsonMapper.createAllToStringMapper();
		if (null == pageSize) {
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if (null == pageNum) {
			pageNum = 1;
		}
		String uid = request.getHeader("X-UID");
		String atoken = request.getHeader("X-ATOKEN");
		if (!LoginUtil.isLogin(uid, atoken)) {
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		String cid = request.getHeader("X-CID");
		if (!cid.equals(companyId)) {
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004, AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		MapContext mapContext = MapContext.newOne();
		if (LwxfStringUtils.isNotBlank(startTime)) {
			mapContext.put("startTime", startTime);
		}
		if (LwxfStringUtils.isNotBlank(endTime)) {
			mapContext.put("endTime", endTime);
		}
		if (LwxfStringUtils.isNotBlank(type)) {
			mapContext.put("type", type);
		}
		if (LwxfStringUtils.isNotBlank(orderNo)) {
			mapContext.put("orderNo", orderNo);
		}
		RequestResult result = this.factoryFinishedstockFacade.findFactoryFinishedstockList(pageNum, pageSize, mapContext, isIn);
		return jsonMapper.toJson(result);
	}

	/**
	 * 入库详情
	 *
	 * @param companyId
	 * @param finishedStockItemId
	 * @param orderId
	 * @param request
	 * @return
	 */
	@GetMapping("/finishedStocks/{finishedStockItemId}")
	public String findFactoryFinishedStockItemInfo(@PathVariable String companyId,
												   @PathVariable String finishedStockItemId,
												   @RequestParam String orderId,
												   HttpServletRequest request) {
		JsonMapper jsonMapper = JsonMapper.createAllToStringMapper();
		String uid = request.getHeader("X-UID");
		String atoken = request.getHeader("X-ATOKEN");
		if (!LoginUtil.isLogin(uid, atoken)) {
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		String cid = request.getHeader("X-CID");
		if (!cid.equals(companyId)) {
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004, AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		RequestResult result = this.factoryFinishedstockFacade.findFactoryFinishedStockItemInfo(finishedStockItemId, orderId);
		return jsonMapper.toJson(result);
	}

	/**
	 * 入库操作相关信息展示
	 * @param companyId
	 * @param orderId
	 * @param finishedStockItemId
	 * @param request
	 * @return
	 */
	@GetMapping("/finishedStocks/show")
	public RequestResult addFinishedStock(@PathVariable String companyId,
										  @RequestParam String orderId,
										  @RequestParam(required = false) String finishedStockItemId,
										  HttpServletRequest request){
		String uid = request.getHeader("X-UID");
		String atoken = request.getHeader("X-ATOKEN");
		if (!LoginUtil.isLogin(uid, atoken)) {
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		String cid = request.getHeader("X-CID");
		if (!cid.equals(companyId)) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004, AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		RequestResult result=this.factoryFinishedstockFacade.addFinishedStock(orderId,finishedStockItemId);
		return result;
	}

	/**
	 * 包裹入库处理
	 * @param companyId
	 * @param finishedStockItemId
	 * @param mapContext
	 * @param request
	 * @return
	 */
	@PutMapping("/finishedStocks/{finishedStockItemId}")
	public RequestResult updateFinishedStockItem(@PathVariable String companyId,
			                                     @PathVariable String finishedStockItemId,
												 @RequestBody MapContext mapContext,
												 HttpServletRequest request){
		String uid = request.getHeader("X-UID");
		String atoken = request.getHeader("X-ATOKEN");
		if (!LoginUtil.isLogin(uid, atoken)) {
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		String cid = request.getHeader("X-CID");
		if (!cid.equals(companyId)) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004, AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		RequestResult result=this.factoryFinishedstockFacade.updateFinishedStockItem(finishedStockItemId,mapContext,request);
		return  result;
	}

	/**
	 * 外协 特供 五金打包入库(暂不使用)
	 * @param companyId
	 * @param mapContext
	 * @param request
	 * @return
	 */
    @PostMapping("/finishedStocks/add")
	public RequestResult addFinishedStockItem(@PathVariable String companyId,
											  @RequestBody MapContext mapContext,
											  HttpServletRequest request){
		String uid = request.getHeader("X-UID");
		String atoken = request.getHeader("X-ATOKEN");
		if (!LoginUtil.isLogin(uid, atoken)) {
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		String cid = request.getHeader("X-CID");
		if (!cid.equals(companyId)) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004, AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		RequestResult result=this.factoryFinishedstockFacade.addFinishedStockItem(mapContext,request);
		return  result;
	}

}
