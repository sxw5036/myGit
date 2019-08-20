package com.lwxf.industry4.webapp.facade.admin.factory.aftersale.impl;

import javax.annotation.Resource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyFilesService;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.aftersale.AftersaleStatus;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.aftersale.AftersaleDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.AftersalesPrintTableDto;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApply;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApplyFiles;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.aftersale.AftersaleApplyFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2019/1/8/008 10:04
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("fAftersaleApplyFacade")
public class AftersaleApplyFacadeImpl extends BaseFacadeImpl implements AftersaleApplyFacade {

	@Resource(name = "aftersaleApplyService")
	private AftersaleApplyService aftersaleApplyService;
	@Resource(name = "aftersaleApplyFilesService")
	private AftersaleApplyFilesService aftersaleApplyFilesService;
	@Resource(name = "customOrderService")
	private CustomOrderService customOrderService;

	@Override
	public RequestResult findListAftersale(MapContext mapContext,Integer pageNum,Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		paginatedFilter.setFilters(mapContext);
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		List sort = new ArrayList();
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.aftersaleApplyService.findByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addAftersale(String orderId, AftersaleApply aftersaleApply) {
		//判断订单是否存在
		CustomOrder customOrder = this.customOrderService.findById(orderId);
		if (customOrder==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//添加售后单
		this.aftersaleApplyService.add(aftersaleApply);
		return ResultFactory.generateRequestResult(this.aftersaleApplyService.findOneById(aftersaleApply.getId()));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult uploadImgFile(List<MultipartFile> multipartFileList,String id) {
		//判断售后单是否存在
		AftersaleApply aftersaleApply = this.aftersaleApplyService.findById(id);
		if(aftersaleApply==null){
			return ResultFactory.generateResNotFoundResult();
		}
		AftersaleApplyFiles aftersaleApplyFiles = new AftersaleApplyFiles();
		aftersaleApplyFiles.setCreated(DateUtil.getSystemDate());
		aftersaleApplyFiles.setCreator(WebUtils.getCurrUserId());
		aftersaleApplyFiles.setStatus(1);
		aftersaleApplyFiles.setAftersaleApplyId(id);
		List pathList = new ArrayList();
		for(MultipartFile multipartFile:multipartFileList){
			//赋值给uploadinfo,数据进行处理
			UploadInfo uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.TEMP, multipartFile, UploadResourceType.AFTERSALE_APPLY, id);
			aftersaleApplyFiles.setMime(uploadInfo.getFileMimeType().getRealType());
			aftersaleApplyFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
			aftersaleApplyFiles.setPath(uploadInfo.getRelativePath());
			aftersaleApplyFiles.setFullPath(AppBeanInjector.configuration.getUploadFileRootDir().concat(uploadInfo.getRealPath()));
			aftersaleApplyFiles.setName(uploadInfo.getFileName());
			this.aftersaleApplyFilesService.add(aftersaleApplyFiles);
			pathList.add(aftersaleApplyFiles);
		}
		return ResultFactory.generateRequestResult(pathList);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteFileImg(String id, String fileId) {
		AftersaleApplyFiles applyFiles = this.aftersaleApplyFilesService.findById(fileId);
		if(applyFiles==null||!applyFiles.getAftersaleApplyId().equals(id)){
			return ResultFactory.generateSuccessResult();
		}
		this.aftersaleApplyFilesService.deleteById(fileId);
		//清除本地文件
		AppBeanInjector.baseFileUploadComponent.deleteFileByDir(AppBeanInjector.configuration.getUploadFileRootDir().concat(applyFiles.getPath()));
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateAftersaleApply(String id, MapContext mapContext) {
		Integer status = mapContext.getTypedValue("status", Integer.class);
		if (status!=null&&!status.equals(AftersaleStatus.COMPLETED.getValue())){
			mapContext.put("checker",WebUtils.getCurrUserId());
			mapContext.put("checkTime",DateUtil.getSystemDate());
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		this.aftersaleApplyService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.aftersaleApplyService.findOneById(id));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteAftersaleApply(String id) {
		AftersaleDto aftersaleDto = this.aftersaleApplyService.findOneById(id);
		if(aftersaleDto==null){
			return ResultFactory.generateSuccessResult();
		}
		for(AftersaleApplyFiles aftersaleApplyFiles : aftersaleDto.getAftersaleApplyFilesList()){
			this.aftersaleApplyFilesService.deleteById(aftersaleApplyFiles.getId());
		}
		this.aftersaleApplyService.deleteById(id);
		//删除本地资源
		AppBeanInjector.baseFileUploadComponent.deleteFileByDir(AppBeanInjector.configuration.getUploadAvatarRootDir().concat(UploadResourceType.AFTERSALE_APPLY.getModule()).concat(File.separator).concat(aftersaleDto.getId()));
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findAftersalesPrintInfo(String id) {
		AftersalesPrintTableDto aftersalesPrintTableDto = this.aftersaleApplyService.findAftersalesPrintInfo(id);
		return ResultFactory.generateRequestResult(aftersalesPrintTableDto);
	}
}
