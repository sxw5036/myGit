package com.lwxf.industry4.webapp.controller.app.factory.factoryQuickshare;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
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
import com.lwxf.industry4.webapp.domain.entity.quickshare.Quickshare;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.quickshare.QuickshareFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：快享管理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/7 0007 9:20
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value="/app/f", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryQuickshareController {
	@Resource(name="quickshareFacade")
	private QuickshareFacade quickshareFacade;
	/**
	 * 快享列表
	 */
	@GetMapping("/quickshares")
	public String findQuickshareList(@RequestParam(required = false) Integer pageNum,
									 @RequestParam(required = false) Integer pageSize,
									 @RequestParam(required = false)String condition,
									 HttpServletRequest request){
		JsonMapper resultMapper=JsonMapper.createAllToStringMapper();
		if(null == pageSize){
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if(null == pageNum){
			pageNum = 1;
		}
		MapContext mapContext=MapContext.newOne();
		if(LwxfStringUtils.isNotBlank(condition)){
			mapContext.put("condition",condition);
		}
		RequestResult result=this.quickshareFacade.findQuickshareList(pageNum,pageSize,mapContext,request);
		return resultMapper.toJson(result);
	}


	/**
	 * 上传快享图片（）
	 * @param
	 * @return
	 */
	@PostMapping(value = "/quickshares/{quickshareId}/files")
	public RequestResult uploadMicTempImage(HttpServletRequest request,
											@PathVariable String quickshareId,
											@RequestBody List<MultipartFile> multipartFiles
	){

		String uid=request.getHeader("X-UID");
		String token=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,token)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		List<MultipartFile> files = new ArrayList<>();
		for(MultipartFile multipartFile : multipartFiles){
			Map<String,Object> errorInfo = new HashMap<String, Object>();
			if(multipartFile==null){
				errorInfo.put("files",ErrorCodes.VALIDATE_NOTNULL);
			}else if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
				errorInfo.put("files", ErrorCodes.VALIDATE_ILLEGAL_ARGUMENT);
			} else if (multipartFile.getSize() > 1024L * 1024L* AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031").concat(":").concat(AppBeanInjector.configuration.getUploadBackgroundMaxsize()+"").concat("M"));
			}
			if (errorInfo.size() > 0) {
				return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
			}
			files.add(multipartFile);
		}
		return this.quickshareFacade.uploadQuickshareImage(files,request,quickshareId);
	}

	/**
	 * 添加帖子
	 * @param
	 * @param
	 * @return
	 */
	@PostMapping(value = "/quickshares")
	public RequestResult addQuickshare(@RequestBody Quickshare quickshare,
									   HttpServletRequest request
	) {
		String uid=request.getHeader("X-UID");
		String token=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,token)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		String userId=request.getHeader("X-UID");
		return this.quickshareFacade.addQuickshareAndImage(quickshare,userId);
	}
	/**
	 * 删除快享帖子以及关联的图片和评论、点赞(暂时无此功能)
	 * @param
	 * @return
	 */
	@DeleteMapping(value = "/quickshares/{quickshareId}")
	public RequestResult delectMicroblog (@PathVariable String quickshareId,
										  HttpServletRequest request){
		String uid=request.getHeader("X-UID");
		String token=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,token)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		return this.quickshareFacade.deleteQuickshare(quickshareId,request);
	}
}
