package com.lwxf.industry4.webapp.controller.app.factory.factoryDispatchBill;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryDispatchBill.FactoryDispatchBillFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/8 0008 14:40
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/f", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryDispatchBillController {
	@Resource(name = "factoryDispatchBillFacade")
	private FactoryDispatchBillFacade factoryDispatchBillFacade;

	/**
	 * 查询发货单数据
	 * 待发货 今日已发 本月已发
	 * @param companyId
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/dispatchBills/count")
	public String findDispatchBillCount(@PathVariable String companyId,
										HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		RequestResult result=this.factoryDispatchBillFacade.findDispatchBillCount(companyId);
		return jsonMapper.toJson(result);
	}

	/**
	 * 发货列表，按条件搜索
	 * @param companyId
	 * @param pageNum
	 * @param pageSize
	 * @param startTime
	 * @param endTime
	 * @param dealerName
	 * @param customerName
	 * @param mergerName
	 * @param orderNo
	 * @param status
	 * @param order
	 * @param sort
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/dispatchBills")
	public String findDispathcBillList(@PathVariable String companyId,
									   @RequestParam(required = false) Integer pageNum,
									   @RequestParam(required = false) Integer pageSize,
									   @RequestParam(required = false) String startTime,
									   @RequestParam(required = false) String endTime,
									   @RequestParam(required = false) String dealerName,
									   @RequestParam(required = false) String customerName,
									   @RequestParam(required = false) String mergerName,
									   @RequestParam(required = false) String orderNo,
									   @RequestParam(required = false) String status,
									   @RequestParam(required = false) String topParam,
									   @RequestParam(required = false) String order,
									   @RequestParam(required = false) String sort,
									   HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		if (null == pageSize) {
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if (null == pageNum) {
			pageNum = 1;
		}
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED,AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		MapContext mapContext=MapContext.newOne();
		if(LwxfStringUtils.isNotBlank(startTime)){
          mapContext.put("startTime",startTime);
		}
		if(LwxfStringUtils.isNotBlank(endTime)){
			mapContext.put("endTime",endTime);
		}
		if(LwxfStringUtils.isNotBlank(dealerName)){
			mapContext.put("dealerName",dealerName);
		}
		if(LwxfStringUtils.isNotBlank(customerName)){
			mapContext.put("customerName",customerName);
		}
		if(LwxfStringUtils.isNotBlank(mergerName)){
			mapContext.put("mergerName",mergerName);
		}
		if(LwxfStringUtils.isNotBlank(orderNo)){
			mapContext.put("orderNo",orderNo);
		}
		if(LwxfStringUtils.isNotBlank(status)){
			mapContext.put("status",status);
		}
		if(LwxfStringUtils.isNotBlank(topParam)){
			mapContext.put("topParam",topParam);
		}
		if(LwxfStringUtils.isNotBlank(order)){
			mapContext.put("order",order);
		}
		if(LwxfStringUtils.isNotBlank(sort)){
			mapContext.put("sort",sort);
		}
		RequestResult result=this.factoryDispatchBillFacade.findDispathcBillList(pageNum,pageSize,mapContext);
		return jsonMapper.toJson(result);
	}

	/**
	 * 发货管理 发货详情
	 * @param companyId
	 * @param dispatchBillId
	 * @param dispatchBillItemId
	 * @param request
	 * @return
	 */
    @GetMapping("/companies/{companyId}/dispatchBills/{dispatchBillId}/dispatchBillItems/{dispatchBillItemId}")
	public String findDispatchBillInfo(@PathVariable String companyId,
									   @PathVariable String dispatchBillId,
									   @PathVariable String dispatchBillItemId,
									   HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED,AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		RequestResult result=this.factoryDispatchBillFacade.findDispatchBillInfo(dispatchBillId,dispatchBillItemId);
		return jsonMapper.toJson(result);
	}

	/**
	 * 所选择的包裹信息展示
	 * @param companyId
	 * @param finishedStockItemIds
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/dispatchBills/baseInfo")
	public RequestResult dispatchBillRedis(@PathVariable String companyId,
										 @RequestParam String finishedStockItemIds,
										 @RequestParam String orderId,
										 HttpServletRequest request){
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED,AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		RequestResult result=this.factoryDispatchBillFacade.dispatchBillRedis(finishedStockItemIds,orderId);
    	return  result;
	}

	/**
	 * 创建发货单
	 * @param companyId
	 * @param mapContext
	 * @param request
	 * @return
	 */
    @PostMapping("/companies/{companyId}/dispatchBills/add")
	public RequestResult addDispatchBill(@PathVariable String companyId,
										 @RequestBody MapContext mapContext,
										 HttpServletRequest request){
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED,AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		RequestResult result=this.factoryDispatchBillFacade.addDispatchBill(mapContext,request);
		return result;
	}
	/**
	 * 物流公司列表
	 */
	@GetMapping("/logistics")
	public RequestResult findLogisticsList(){
		RequestResult result=this.factoryDispatchBillFacade.findLogisticsList();
		return result;
	}

	/**
	 * 发货附件添加
	 * @param companyId
	 * @param dispatchBillId
	 * @param multipartFiles
	 * @param request
	 * @return
	 */
	@PostMapping("/companies/{companyId}/dispatchBills/{dispatchBillId}/addfiles")
	public RequestResult addFiles(@PathVariable String companyId,
								  @PathVariable String dispatchBillId,
								  @RequestBody List<MultipartFile> multipartFiles,
								  HttpServletRequest request){

		Map<String, Object> errorInfo = new HashMap<>();
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED,AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}

		if (multipartFiles == null||multipartFiles.size()==0) {
			errorInfo.put("multipartFiles", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		for (MultipartFile multipartFile:multipartFiles) {
			if (multipartFile==null){
				errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
			if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
				errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
			}
			if (multipartFile.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
				return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
			}
		}
		if (errorInfo.size() > 0) {
			return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR, errorInfo);
		}
		return this.factoryDispatchBillFacade.addFiles(dispatchBillId,multipartFiles,request);
	}
}
