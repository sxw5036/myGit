package com.lwxf.industry4.webapp.controller.app.factory.factoryAftersale;

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
import com.lwxf.industry4.webapp.facade.app.factory.factoryAftersaleApply.FactoryAftersaleApplyFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：F端售后管理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/3/30 0030 14:05
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/f", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryAftersaleApplyController {
	@Resource(name = "factoryAftersaleApplyFacade")
	private FactoryAftersaleApplyFacade factoryAftersaleApplyFacade;

	/**
	 * 查询今日 本周 本月的售后单数量
	 * @param companyId
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/aftersaleApplies/count")
	public String findFactoryAftersaleApply(@PathVariable String companyId,
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
		RequestResult result=this.factoryAftersaleApplyFacade.findFactoryAftersaleApply(companyId);
		return jsonMapper.toJson(result);
	}
	/**
	 * 售后列表
	 */
     @PostMapping("/companies/{companyId}/aftersaleApplies")
	 public  String findAftersaleApplyList(@RequestParam(required = false) Integer pageNum,
										   @RequestParam(required = false) Integer pageSize,
	 		                               @PathVariable String companyId,
										   @RequestBody MapContext mapContext,
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
		 RequestResult result=this.factoryAftersaleApplyFacade.findAftersaleApplyList(pageNum,pageSize,mapContext);
		 return jsonMapper.toJson(result);
	 }
	/**
	 * 售后单详情
	 * @param aftersaleApplyId
	 * @param companyId
	 * @param request
	 * @return
	 */
    @GetMapping("/companies/{companyId}/aftersaleApplies/{aftersaleApplyId}")
	public String factoryAftersaleApplyInfo(@PathVariable String aftersaleApplyId,
											@PathVariable String companyId,
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
		RequestResult result=this.factoryAftersaleApplyFacade.factoryAftersaleApplyInfo(aftersaleApplyId,companyId);
		return jsonMapper.toJson(result);

	}

	/**
	 * 经销商列表
	 * @param companyId
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/dealers")
	public String findDealersList(@PathVariable String companyId,
								  @RequestParam(required = false) String mergerName,
								  @RequestParam(required = false) String dealerName,
								  HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED,AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		MapContext params=MapContext.newOne();
		if(LwxfStringUtils.isNotBlank(mergerName)){
			params.put("mergerName",mergerName);
		}
		if(LwxfStringUtils.isNotBlank(dealerName)){
			params.put("dealerName",dealerName);
		}
		RequestResult result=this.factoryAftersaleApplyFacade.findDealersList(params);
    	return jsonMapper.toJson(result);
	}


	/**
	 * 客户列表
	 * @param companyId
	 * @param dealerId
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/dealers/{dealerId}/customers")
	public String findCustomerList(@PathVariable String companyId,
								   @PathVariable String dealerId,
								   HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED,AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		RequestResult result=this.factoryAftersaleApplyFacade.findCustomerList(dealerId);
		return jsonMapper.toJson(result);
	}

	/**
	 * 创建售后单
	 * @param companyId
	 * @param mapContext
	 * @param request
	 * @return
	 */
    @PostMapping("/companies/{companyId}/aftersaleApplies/add")
	public RequestResult addFactoryAftersale(@PathVariable String companyId,
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
		RequestResult result=this.factoryAftersaleApplyFacade.addFactoryAftersale(mapContext,request);
		return  result;
	}
	/**
	 * 上传售后申请证据图片
	 */
	@PostMapping("/companies/{companyId}/aftersales/{aftersaleId}/addfiles")
	public RequestResult addFiles(@PathVariable String companyId,
								  @PathVariable String aftersaleId,
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
		return this.factoryAftersaleApplyFacade.addFiles(aftersaleId,multipartFiles,request);
	}
}
