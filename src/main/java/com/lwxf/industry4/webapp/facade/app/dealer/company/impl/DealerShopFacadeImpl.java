package com.lwxf.industry4.webapp.facade.app.dealer.company.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.common.UploadFilesService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.company.DealerShopService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.company.dealerShop.DealerShopDto;
import com.lwxf.industry4.webapp.domain.entity.common.UploadFiles;
import com.lwxf.industry4.webapp.domain.entity.company.DealerShop;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.company.DealerShopFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：店铺管理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/14 0014 9:16
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("DealerShopFacade")
public class DealerShopFacadeImpl extends BaseFacadeImpl implements DealerShopFacade {
	@Resource(name = "companyService")
	private CompanyService companyService;
	@Resource(name ="uploadFilesService")
	private UploadFilesService uploadFilesService;
	@Resource(name = "dealerShopService")
    private DealerShopService dealerShopService;
//店铺展示
	@Override
	public RequestResult shopShow(String companyId,String dealerShopId) {
		//CompanyDto companyDto=this.companyService.findCompanyById(dealerShopId);
		DealerShop dealerShop=this.dealerShopService.findById(dealerShopId);
		if(dealerShop==null){
			return ResultFactory.generateResNotFoundResult();
		}
		if(!companyId.equals(dealerShop.getCompanyId())){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		DealerShopDto dealerShopDto=this.dealerShopService.findDealerShopById(dealerShopId);
		if(dealerShopDto==null){
			return ResultFactory.generateResNotFoundResult();
		}
		String cityName=dealerShopDto.getMergerName();
		if(cityName!=null){
			int a=cityName.indexOf(",");
			String mergerName = cityName.substring(a + 1);
			dealerShopDto.setMergerName(mergerName);
		}
		MapContext result=MapContext.newOne();
		result.put("dealerShop",dealerShopDto);
		Integer status= UploadType.FORMAL.getValue();
		//封面图片
		Integer resourceType1= UploadResourceType.COVER.getType();
		UploadFiles uploadFiles=this.uploadFilesService.findCoverImageByCidAndStatusAndResourceType(dealerShopId,status,resourceType1);
		List coverList=new ArrayList();
       if(uploadFiles!=null) {
		   String coverImageId = uploadFiles.getId();
		   String coverImagePath=uploadFiles.getPath();
		   coverList.add(coverImageId);
		   coverList.add(coverImagePath);
	   }
		result.put("coverList",coverList);
        //展示图片
		Integer resourceType=UploadResourceType.BACKGROUND.getType();
		List<UploadFiles> uploadFilesList=this.uploadFilesService.findShowImageByCidAndStatusAndResType(dealerShopId,status,resourceType);
		List showList=new ArrayList();
		if(uploadFilesList!=null&&uploadFilesList.size()>0){
			for(UploadFiles uploadFiles1:uploadFilesList){
				List showImageList=new ArrayList();
				String showImageId=uploadFiles1.getId();
				String showImagePath=uploadFiles1.getPath();
				showImageList.add(showImageId);
				showImageList.add(showImagePath);
				showList.add(showImageList);
			}
		}
		result.put("showList",showList);
		return ResultFactory.generateRequestResult(result);
	}
//店铺信息修改
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateShop(String companyId,String dealerShopId,MapContext mapContext) {
		DealerShop dealerShop=this.dealerShopService.findById(dealerShopId);
		if(dealerShop==null){
			return ResultFactory.generateResNotFoundResult();
		}
		if(!companyId.equals(dealerShop.getCompanyId())){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
        mapContext.put("id",dealerShopId);
		this.companyService.updateByMapContext(mapContext);
		this.dealerShopService.updateByMapContext(mapContext);
		return ResultFactory.generateSuccessResult();
	}
	@Override
	public RequestResult findShopList(String address) {
		List<DealerShop> list=this.dealerShopService.findShopList(address);
		return ResultFactory.generateRequestResult(list);
	}

	/**
	 * 店铺封面图替换
	 * @param dealerShopId
	 * @param multipartFile
	 * @param request
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateCoverImage(String companyId,String dealerShopId, MultipartFile multipartFile, HttpServletRequest request) {
		DealerShop dealerShop=this.dealerShopService.findById(dealerShopId);
		if(dealerShop==null){
			return ResultFactory.generateResNotFoundResult();
		}
		if(!companyId.equals(dealerShop.getCompanyId())){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}

		Integer status= UploadType.FORMAL.getValue();
		//封面图片
		Integer resourceType= UploadResourceType.COVER.getType();
		UploadFiles uploadFiles=this.uploadFilesService.findCoverImageByCidAndStatusAndResourceType(dealerShopId,status,resourceType);
		if(uploadFiles!=null){
			String id=uploadFiles.getId();
			this.uploadFilesService.deleteById(id);
		}
		String uid = request.getHeader("X-UID");
		UploadFiles uploadFiles1=new UploadFiles();
		UploadInfo uploadInfo=AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, multipartFile, UploadResourceType.COVER, dealerShopId);
		uploadFiles1.setCompanyId(dealerShopId);
		uploadFiles1.setName(uploadInfo.getFileName());
		uploadFiles1.setCreator(uid);
		uploadFiles1.setCreated(DateUtil.getSystemDate());
		uploadFiles1.setPath(uploadInfo.getRelativePath());
		uploadFiles1.setFullPath(WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
		uploadFiles1.setMime(uploadInfo.getFileMimeType().getRealType());
		uploadFiles1.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
		uploadFiles1.setStatus(UploadType.FORMAL.getValue());
		uploadFiles1.setResourceType(UploadResourceType.COVER.getType());
		this.uploadFilesService.add(uploadFiles1);
		MapContext mapContext=MapContext.newOne();
		mapContext.put("coverFullPath",uploadInfo.getRelativePath());
		return ResultFactory.generateRequestResult(mapContext);
	}

	/**
	 * 展示图片添加
	 * @param dealerShopId
	 * @param multipartFiles
	 * @param request
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addShowImages(String companyId,String dealerShopId, List<MultipartFile> multipartFiles, HttpServletRequest request) {
		String uid=request.getHeader("X-UID");
		List<Map<String, Object>> list = new ArrayList<>();
		UploadFiles uploadFiles = new UploadFiles();
		UploadInfo uploadInfo;
		for (MultipartFile mul : multipartFiles) {
			uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, mul, UploadResourceType.BACKGROUND, dealerShopId);
			uploadFiles.setCompanyId(dealerShopId);
			uploadFiles.setName(uploadInfo.getFileName());
			uploadFiles.setCreator(uid);
			uploadFiles.setCreated(DateUtil.getSystemDate());
			uploadFiles.setPath(uploadInfo.getRelativePath());
			uploadFiles.setFullPath(WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
			uploadFiles.setMime(uploadInfo.getFileMimeType().getRealType());
			uploadFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
			uploadFiles.setStatus(UploadType.FORMAL.getValue());
			uploadFiles.setResourceType(UploadResourceType.BACKGROUND.getType());
			this.uploadFilesService.add(uploadFiles);
			MapContext mapContext = MapContext.newOne();
			mapContext.put("imagePath",uploadInfo.getRelativePath());
			list.add(mapContext);
		}
		return ResultFactory.generateRequestResult(list);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteShowImages(String companyId,String dealerShopId, String showImageId, HttpServletRequest request) {
		if (showImageId.equals("")||showImageId==null){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
			this.uploadFilesService.deleteById(showImageId);

		return ResultFactory.generateSuccessResult();
	}
}
