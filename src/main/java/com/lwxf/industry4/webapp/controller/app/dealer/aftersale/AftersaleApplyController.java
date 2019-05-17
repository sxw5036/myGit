package com.lwxf.industry4.webapp.controller.app.dealer.aftersale;

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
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.aftersale.AftersaleApplyFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：售后服务：
 * 售后申请
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/30 0030 10:29
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value="/app/b/companies/{companyId}", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class AftersaleApplyController extends BaseDealerController {

	@Resource(name="aftersaleApplyFacade")
	private AftersaleApplyFacade aftersaleApplyFacade;

	/**
	 * 售后单预加载
	 * @param request
	 * @return
	 */
	@GetMapping("/aftersales/redis")
	public String aftersaleApply(HttpServletRequest request){
      JsonMapper jsonMapper=new JsonMapper();
      String xp="baftersalemng-aftersalement-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return jsonMapper.toJson(result1);
		}
		RequestResult result=this.aftersaleApplyFacade.aftersaleApplyRedis(request);
		return jsonMapper.toJson(result);
	}

	/**
	 * 创建售后申请单
	 * @param companyId
	 * @param request
	 * @param mapContext
	 * @return
	 */
	@PostMapping("/aftersales")
	public RequestResult addAftersale(
									  @PathVariable String companyId,
									  HttpServletRequest request,
									  @RequestBody MapContext mapContext
									  ){
		String xp="baftersalemng-aftersalement-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		String cid=request.getHeader("X-CID");
		if(cid==null||cid.equals("")){
			return ResultFactory.generateErrorResult(AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"),AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
		}
		if(!cid.equals(companyId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
      return this.aftersaleApplyFacade.addAftersale(companyId,request,mapContext);
	}

	/**
	 * 售后单列表
	 * @param companyId
	 * @param creator
	 * @param request
	 * @return
	 */
    @GetMapping("/aftersales")
	public String aftersaleList(@RequestParam(required = false) Integer pageNum,
								@RequestParam(required = false) Integer pageSize,
			                    @PathVariable String companyId,
								@RequestParam(required = false)String creator,
								@RequestParam(required = false)String condition,
								HttpServletRequest request){
		if(null == pageSize){
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if(null == pageNum){
			pageNum = 1;
		}
		JsonMapper resultMapper = new JsonMapper();
		String xp="baftersalemng-aftersalement-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
		MapContext mapContext=MapContext.newOne();
    	mapContext.put("companyId",companyId);
    	if(LwxfStringUtils.isNotBlank(creator)) {
			mapContext.put("creator", creator);
		}
    	if(LwxfStringUtils.isNotBlank(condition)){
    		mapContext.put("condition",condition);
		}

        RequestResult result=this.aftersaleApplyFacade.aftersaleList(pageNum,pageSize,mapContext,request);
        return resultMapper.toJson(result);
	}

	/**
	 * 售后单修改
	 * @param companyId
	 * @param aftersaleId
	 * @param request
	 * @return
	 */
    @PutMapping("/aftersales/{aftersaleId}")
	public RequestResult updateAftersaleApply(@PathVariable String companyId,
											  @PathVariable String aftersaleId,
											  HttpServletRequest request,
											  @RequestBody MapContext mapContext){

		String xp="baftersalemng-aftersalement-edit";
    	RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
       String cid=request.getHeader("X-CID");
       if(!cid.equals(companyId)){
		   return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_RES_NOT_FOUND_10001,AppBeanInjector.i18nUtil.getMessage("BIZ_RES_NOT_FOUND_10001"));
	   }

       return this.aftersaleApplyFacade.updateAftersaleApply(companyId,aftersaleId,request,mapContext);
	}

	/**
	 * 售后单详情
	 * @param companyId
	 * @param aftersaleId
	 * @param request
	 * @return
	 */
	@GetMapping("/customOrders/{orderId}/aftersales/{aftersaleId}")
	public String aftersaleApplyMessage(@PathVariable String companyId,
										@PathVariable String aftersaleId,
										@PathVariable String orderId,
										HttpServletRequest request){
    	JsonMapper resultMapper=new JsonMapper();
		String xp="baftersalemng-aftersalement-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
    	RequestResult result=this.aftersaleApplyFacade.findAftersaleMessage(companyId,aftersaleId,orderId,request);
    	return resultMapper.toJson(result);
	}
	/**
	 * 上传售后申请证据图片
	 */
	@PostMapping("/aftersales/{aftersaleId}/addfiles")
	public RequestResult addFiles(@PathVariable String companyId,
								  @PathVariable String aftersaleId,
								  @RequestBody List<MultipartFile> multipartFiles,
								  HttpServletRequest request){

		String xp="baftersalemng-aftersalement-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		Map<String, Object> errorInfo = new HashMap<>();

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
		return this.aftersaleApplyFacade.addFiles(aftersaleId,multipartFiles,request);
	}
	@GetMapping("/aftersales/count")
	public String aftersaleCount(@RequestParam(required = false) String startTime,
								 @RequestParam(required = false) String endTime,
								 @RequestParam(required = false) String saleMan,
								 @PathVariable String companyId,
								 HttpServletRequest request){
		JsonMapper resultMapper=JsonMapper.createAllToStringMapper();
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return resultMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_RES_NOT_FOUND_10001,AppBeanInjector.i18nUtil.getMessage("BIZ_RES_NOT_FOUND_10001")));
		}
		String xp="baftersalemng-aftersalement-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
		MapContext mapContext=MapContext.newOne();
		mapContext.put("companyId",cid);
		if(LwxfStringUtils.isNotBlank(startTime)){
			mapContext.put("startTime",startTime);
		}
		if(LwxfStringUtils.isNotBlank(startTime)){
			mapContext.put("endTime",endTime);
		}
		if(LwxfStringUtils.isNotBlank(saleMan)){
			mapContext.put("saleMan",saleMan);
		}
		RequestResult result=this.aftersaleApplyFacade.findAftersaleCount(mapContext,request);
		return resultMapper.toJson(result);
	}
}
