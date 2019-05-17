package com.lwxf.industry4.webapp.controller.app.dealer.company;

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
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.company.DealerShopFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：店铺管理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/14 0014 9:12
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class DealerShopController extends BaseDealerController {
	@Resource(name = "DealerShopFacade")
	private DealerShopFacade DealerShopFacade;
	//店铺展示
	@GetMapping("/companies/{companyId}/dealerShops/{dealerShopId}")
	public String shopShow(@PathVariable String companyId,
						   @PathVariable String dealerShopId,
							HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		String xp="bdealermng-information-view";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return jsonMapper.toJson(result);
		}
		RequestResult result1=this.DealerShopFacade.shopShow(companyId,dealerShopId);
		return jsonMapper.toJson(result1);
	}
	//店铺信息修改
	@PutMapping("/companies/{companyId}/dealerShops/{dealerShopId}")
	public RequestResult updateShop(@PathVariable String companyId,
									@PathVariable String dealerShopId,
									@RequestBody MapContext mapContext,
									HttpServletRequest request){
		String xp="bdealermng-information-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		return this.DealerShopFacade.updateShop(companyId,dealerShopId,mapContext);
	}
	//店铺封面图片替换
	@PostMapping("/companies/{companyId}/dealerShops/{dealerShopId}/coverImage")
	public RequestResult updateCoverImage(@PathVariable String companyId,
										  @PathVariable String dealerShopId,
										  @RequestBody MultipartFile multipartFile,
										  HttpServletRequest request){
		String xp="bdealermng-information-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		Map<String, Object> errorInfo = new HashMap<>();
			if (multipartFile==null){
				errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
			if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
				errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
			}
			if (multipartFile.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
				return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
			}
		if (errorInfo.size() > 0) {
			return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR, errorInfo);
		}
		return this.DealerShopFacade.updateCoverImage(companyId,dealerShopId,multipartFile,request);
	}
	//店铺展示图片添加
	@PostMapping("/companies/{companyId}/dealerShops/{dealerShopId}/showImages")
	public RequestResult addShowImages(@PathVariable String companyId,
									   @PathVariable String dealerShopId,
									   @RequestBody List<MultipartFile> multipartFiles,
									   HttpServletRequest request){
		String xp="bdealermng-information-edit";
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
		return this.DealerShopFacade.addShowImages(companyId,dealerShopId,multipartFiles,request);
	}
    //店铺展示图片删除
	@DeleteMapping("/companies/{companyId}/dealerShops/{dealerShopId}/showImages")
	public RequestResult deleteShowImages(@PathVariable String companyId,
										  @PathVariable String dealerShopId,
										  @RequestParam String showImageId,
										  HttpServletRequest request){
		String xp="bdealermng-information-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		return this.DealerShopFacade.deleteShowImages(companyId,dealerShopId,showImageId,request);
	}
	//店铺列表
	@GetMapping("/companies/0/dealerShops")
	public String companyList(@RequestParam String address){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		RequestResult result=this.DealerShopFacade.findShopList(address);
		return jsonMapper.toJson(result);
	}
}
