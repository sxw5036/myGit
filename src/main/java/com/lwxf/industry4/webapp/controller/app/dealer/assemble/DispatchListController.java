package com.lwxf.industry4.webapp.controller.app.dealer.assemble;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.assemble.DispatchListFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：安装单管理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/19 0019 14:36
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b/companies/{companyId}", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class DispatchListController extends BaseDealerController {

	@Resource(name="dispatchListFacade")
	private DispatchListFacade dispatchListFacade;

	/**
	 * 安装单列表
	 * @param pageNum
	 * @param pageSize
	 * @param status
	 * @param request
	 * @return
	 */
  @GetMapping("/dispatchLists")
  public String findDispatchLists(@RequestParam(required = false) Integer pageNum,
								  @RequestParam(required = false) Integer pageSize,
								  @RequestParam(required = false) Integer status,
								  @RequestParam(required = false) String creator,
								  @RequestParam(required = false) String condition,
								  HttpServletRequest request){

	  if(null == pageSize){
		  pageSize = AppBeanInjector.configuration.getPageSizeLimit();
	  }
	  if(null == pageNum){
		  pageNum = 1;
	  }
	  JsonMapper jsonMapper=new JsonMapper();
	  String xp="bassemblemng-dispatchlistinfo-view";
	  RequestResult result1 = this.validUserPermission(request,xp);
	  if(result1!=null){
		  return jsonMapper.toJson(result1);
	  }
	  String companyId = request.getHeader("X-CID");
	  MapContext mapContext=MapContext.newOne();
	  if(LwxfStringUtils.isNotBlank(companyId)){
		  mapContext.put("companyId",companyId);
	  }
	  if(LwxfStringUtils.isNotBlank("status")){
		  mapContext.put("status",status);
	  }
	  if(LwxfStringUtils.isNotBlank(creator)) {
		  mapContext.put("creator", creator);
	  }
	  if(LwxfStringUtils.isNotBlank(condition)){
	  	mapContext.put("condition",condition);
	  }

	  RequestResult result=this.dispatchListFacade.findDispatcjLists(mapContext,pageNum,pageSize,request);
  	return jsonMapper.toJson(result);
  }

	/**
	 * 创建安装单
	 * @param mapContext
	 * @param request
	 * @return
	 */

	@PostMapping("/dispatchLists")
	public RequestResult addDispatchList(@RequestBody MapContext mapContext,
										 HttpServletRequest request){

		String xp="bassemblemng-dispatchlistinfo-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}

  	  return this.dispatchListFacade.addDispatchList(mapContext,request);
	}

	/**
	 * 安装单详情
	 * @param dispatchListId
	 * @param request
	 * @return
	 */

	@GetMapping("/dispatchLists/{dispatchListId}")
	public String dispatchListMessage(@PathVariable String dispatchListId,
											 HttpServletRequest request){

		JsonMapper jsonMapper=new JsonMapper();
		String xp="bassemblemng-dispatchlistinfo-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return jsonMapper.toJson(result1);
		}
		RequestResult result=this.dispatchListFacade.dispatchListMessage(dispatchListId,request);
          return jsonMapper.toJson(result);
	}

	/**
	 * 安装单修改
	 * @param dispatchListId
	 * @param mapContext
	 * @param status
	 * @param request
	 * @return
	 */
    @PutMapping("/dispatchLists/{dispatchListId}/statuses")
	public RequestResult updateDispatchListById(@PathVariable String dispatchListId,
												@RequestBody MapContext mapContext,
												@RequestParam Integer status,
												HttpServletRequest request){
		String xp="bassemblemng-dispatchlistinfo-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		return this.dispatchListFacade.updateDispatchListById(dispatchListId,mapContext,status,request);
	}


	/**
	 * 上传施工现场图
	 * @param dispatchListId
	 * @param multipartFiles
	 * @return
	 */
	@PostMapping("/dispatchLists/{dispatchListId}/addFiles")
	public RequestResult addFiles(@PathVariable String dispatchListId,
								   @RequestBody List<MultipartFile> multipartFiles,
								   HttpServletRequest request){

		String xp="bassemblemng-dispatchlistinfo-edit";
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
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
			}
		}
		if (errorInfo.size() > 0) {
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
		}

  	return this.dispatchListFacade.addFiles(dispatchListId,multipartFiles,request);
	}
	@GetMapping("/dispatchLists/count")
	public String dispatchListsCount(@PathVariable String companyId,
									 @RequestParam(required = false) String selectTime,
									 @RequestParam(required = false) String saleMan,
									 HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		//判断用户权限
		String xp="bassemblemng-dispatchlistinfo-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return jsonMapper.toJson(result1);
		}
		if(!companyId.equals(request.getHeader("X-CID"))){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		MapContext mapContext=MapContext.newOne();
		mapContext.put("companyId",companyId);
		if(LwxfStringUtils.isNotBlank("saleMan")){
			mapContext.put("saleMan",saleMan);
		}
		if(LwxfStringUtils.isNotBlank("selectTime")){
			mapContext.put("selectTime",selectTime);
		}
        RequestResult result=this.dispatchListFacade.dispatchListsCount(mapContext,request);
		return jsonMapper.toJson(result);
	}


}
