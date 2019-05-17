package com.lwxf.industry4.webapp.facade.app.dealer.assemble.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.assemble.ConstructionInspectionService;
import com.lwxf.industry4.webapp.bizservice.assemble.DispatchListFilesService;
import com.lwxf.industry4.webapp.bizservice.assemble.DispatchListService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.company.DealerEmployeeRole;
import com.lwxf.industry4.webapp.common.enums.company.EmployeeStatus;
import com.lwxf.industry4.webapp.common.enums.dispatchList.ConstructionInspectionResult;
import com.lwxf.industry4.webapp.common.enums.dispatchList.DispatchListStatus;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.companyEmployee.CompanyEmployeeDto;
import com.lwxf.industry4.webapp.domain.dto.dispatchList.ConstructionInspectionDto;
import com.lwxf.industry4.webapp.domain.dto.dispatchList.DispatchListDto;
import com.lwxf.industry4.webapp.domain.entity.assemble.ConstructionInspection;
import com.lwxf.industry4.webapp.domain.entity.assemble.DispatchList;
import com.lwxf.industry4.webapp.domain.entity.assemble.DispatchListFiles;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.assemble.DispatchListFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：安装状态变更
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/19 0019 15:24
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("dispatchListFacade")
public class DispatchListFacadeImpl extends BaseFacadeImpl implements DispatchListFacade {
	@Resource(name="dispatchListService")
	private DispatchListService dispatchListService;
	@Resource(name="dispatchBillService")
	private DispatchBillService dispatchBillService;
	@Resource(name="dispatchListFilesService")
	private DispatchListFilesService dispatchListFilesService;
	@Resource(name="constructionInspectionService")
	private ConstructionInspectionService constructionInspectionService;
	@Resource(name="userService")
	private UserService userService;
	@Resource(name="companyEmployeeService")
	private CompanyEmployeeService companyEmployeeService;
	@Resource(name = "roleService")
	private RoleService roleService;
	@Override
	//安装单列表
	public RequestResult findDispatcjLists(MapContext mapContext, Integer pageNum, Integer pageSize,HttpServletRequest request) {
		String cid=request.getHeader("X-CID");
		String uid=request.getHeader("X-UID");
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		CompanyEmployeeDto companyEmployeeDto=this.companyEmployeeService.findKeyByCidAndCustomerManager(cid,uid);
		if(companyEmployeeDto==null){
			return ResultFactory.generateResNotFoundResult();
		}
		String key=companyEmployeeDto.getKey();
		if(key.equals(DealerEmployeeRole.CLERK.getValue())){
			mapContext.put("creator",uid);
		}
		paginatedFilter.setFilters(mapContext);
		Map dataSort = new HashMap();
		List sorts = new ArrayList();
		dataSort.put("created","desc");
		sorts.add(dataSort);
		paginatedFilter.setSorts(sorts);
		PaginatedList<DispatchListDto> dispatchListPaginatedList=this.dispatchListService.findDispatchListByCid(paginatedFilter);
		MapContext result=MapContext.newOne();
		result.put("dispatchList",dispatchListPaginatedList.getRows());
		return ResultFactory.generateRequestResult(result, dispatchListPaginatedList.getPagination());

	}

//安装单详情
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult dispatchListMessage(String dispatchListId,HttpServletRequest request) {
		String uid=request.getHeader("X-UID");
		User user=this.userService.findByUserId(uid);
		String loginUserName=user.getName();
     MapContext mapContext=MapContext.newOne();
  List<DispatchListFiles> dispatchListFiles=this.dispatchListFilesService.findFilesMessage(dispatchListId);
    ConstructionInspectionDto constructionInspection=this.constructionInspectionService.findConstructionByDispatchId(dispatchListId);
    DispatchListDto dispatchListDto=this.dispatchListService.findDipatchListById(dispatchListId);
   if(!dispatchListFiles.isEmpty()){
    mapContext.put("dispatchListFiles",dispatchListFiles);}
   if (constructionInspection!=null) {
	   mapContext.put("constructionInspection", constructionInspection);
   }
    mapContext.put("dispatchList",dispatchListDto);
    mapContext.put("loginUserName",loginUserName);
		return ResultFactory.generateRequestResult(mapContext);
	}
//创建安装单
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addDispatchList(MapContext mapContext,HttpServletRequest request) {
		DispatchList dispatchList=new DispatchList();
		dispatchList.setName((String)mapContext.get("name"));
		dispatchList.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.DISPATCH_LIST_NO));
		dispatchList.setCreator(request.getHeader("X-UID"));
		dispatchList.setOrderId((String)mapContext.get("orderId"));
		dispatchList.setCompanyId(request.getHeader("X-CID"));
		dispatchList.setTakeDelivery(true);
		dispatchList.setDispatchBillId((String)mapContext.get("dispatchBillId"));
		dispatchList.setStatus(DispatchListStatus.NODISPATCHEDWORKER.getValue());
		dispatchList.setConstructionTime(DateUtil.getSystemDate());
		dispatchList.setAddress((String)mapContext.get("address"));
		dispatchList.setCreated(DateUtil.getSystemDate());
		dispatchList.setNotes((String)mapContext.get("notes"));
		dispatchList.setConsignee((String)mapContext.get("consignee"));
		dispatchList.setPickupTime(DateUtil.getSystemDate());
		this.dispatchListService.add(dispatchList);
		return ResultFactory.generateSuccessResult();
	}
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateDispatchListById(String dispatchListId, MapContext mapContext, Integer status, HttpServletRequest request) {
		String uid=request.getHeader("X-UID");
		if(uid==null){
			return ResultFactory.generateErrorResult(ErrorCodes.SYS_ILLEGAL_ARGUMENT_00005,AppBeanInjector.i18nUtil.getMessage("SYS_ILLEGAL_ARGUMENT_00005"));
		}
		User loginUser=this.userService.findByUserId(uid);
		String loginUserName=loginUser.getName();
		String mobile=loginUser.getMobile();
		DispatchList dispatchList=this.dispatchListService.findById(dispatchListId);
		if(dispatchList==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//未派工状态
		if(status==DispatchListStatus.NODISPATCHEDWORKER.getValue()){
		String takeDelivery=(String) mapContext.get("takeDelivery");
        if(Boolean.valueOf(takeDelivery)){
			mapContext.put("id",dispatchListId);
			mapContext.put("created",DateUtil.getSystemDate());
			mapContext.put("takeDelivery",true);
        	mapContext.put("status",DispatchListStatus.NOTAKEDELIVERY.getValue());
		}else {
			mapContext.put("id",dispatchListId);
			mapContext.put("takeDelivery",false);
			mapContext.put("created",DateUtil.getSystemDate());
			mapContext.put("pickupTime",DateUtil.getSystemDate());
			mapContext.put("status",DispatchListStatus.NOCONTRUCTION.getValue());
		}
			this.dispatchListService.updateByMapContext(mapContext);
		}
		//未提货状态
		else if (status==DispatchListStatus.NOTAKEDELIVERY.getValue()){
			mapContext.put("id",dispatchListId);
			mapContext.put("auditor",uid);
			mapContext.put("pickupTime",DateUtil.getSystemDate());
			mapContext.put("status",DispatchListStatus.NOCONTRUCTION.getValue());
			this.dispatchListService.updateByMapContext(mapContext);
		}
		//未施工状态
		else if(status==DispatchListStatus.NOCONTRUCTION.getValue()){
			mapContext.put("id",dispatchListId);
			mapContext.put("status",DispatchListStatus.NOTCHECKED.getValue());
			this.dispatchListService.updateByMapContext(mapContext);
		}
		//未检查状态
		else if(status==DispatchListStatus.NOTCHECKED.getValue()){
			ConstructionInspection constructionInspection=this.constructionInspectionService.findConstructionByDispatchId(dispatchListId);
			if(constructionInspection!=null){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_RES_ARCHIVED_10008,AppBeanInjector.i18nUtil.getMessage("BIZ_RES_ARCHIVED_10008"));
			}
			ConstructionInspection inspection=new ConstructionInspection();
			inspection.setChecker(uid);
			try {
				SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
				String checkTime=(String) mapContext.get("checkTime");
				inspection.setCheckTime(dateFormat.parse(checkTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			inspection.setName(dispatchList.getName());
			inspection.setNotes((String)mapContext.get("notes"));
			String orderId=(String)mapContext.get("orderId");
			if(orderId==null){
				return ResultFactory.generateErrorResult(AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"),AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
			inspection.setOrderId(orderId);
			inspection.setDispatchListId(dispatchListId);
			String result =(String)mapContext.get("result");
			if(request==null){
				return ResultFactory.generateErrorResult(AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"),AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
			inspection.setResult(Integer.valueOf(result));
            this.constructionInspectionService.add(inspection);
            MapContext mapContext1=MapContext.newOne();
            mapContext1.put("id",dispatchListId);
            mapContext1.put("status",DispatchListStatus.NOREVIEW.getValue());
            this.dispatchListService.updateByMapContext(mapContext1);
		}
		//未复检状态
		else if(status==DispatchListStatus.NOREVIEW.getValue()){
			ConstructionInspection constructionInspection=this.constructionInspectionService.findConstructionByDispatchId(dispatchListId);
			String id=constructionInspection.getId();
			mapContext.put("id",id);
			this.constructionInspectionService.updateByMapContext(mapContext);
			if(mapContext.containsKey("reviewResult")) {
				String rewiewResult = (String) mapContext.get("reviewResult");
				Integer reviewResult = Integer.valueOf(rewiewResult);
				if (reviewResult != ConstructionInspectionResult.INCOMPETENT.getValue()) {
					MapContext mapContext1=MapContext.newOne();
					mapContext1.put("id", dispatchListId);
					mapContext1.put("status", DispatchListStatus.END.getValue());
					this.dispatchListService.updateByMapContext(mapContext1);
				}
			}
		}

		return ResultFactory.generateSuccessResult();
	}

	//上传施工图
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addFiles(String dispatchListId, List<MultipartFile> multipartFiles, HttpServletRequest request) {
		String uid=request.getHeader("X-UID");
		List<Map<String,Object>> list=new ArrayList<>();
		DispatchListFiles dispatchListFiles=new DispatchListFiles();
		UploadInfo uploadInfo ;
		for (MultipartFile mul:multipartFiles) {
			uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, mul, UploadResourceType.DISPATCH_LIST, dispatchListId);
			dispatchListFiles.setDispatchListId(dispatchListId);
			dispatchListFiles.setName(uploadInfo.getFileName());
			dispatchListFiles.setCreator(uid);
			dispatchListFiles.setCreated(DateUtil.getSystemDate());
			dispatchListFiles.setPath(uploadInfo.getRelativePath());
			dispatchListFiles.setFullPath(WebUtils.getDomainUrl()+uploadInfo.getRelativePath());
			dispatchListFiles.setMime(uploadInfo.getFileMimeType().getRealType());
			dispatchListFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
			dispatchListFiles.setStatus(UploadType.FORMAL.getValue());
			this.dispatchListFilesService.add(dispatchListFiles);
			MapContext mapContext=MapContext.newOne();
			mapContext.put("imagePath",uploadInfo.getRelativePath());
			list.add(mapContext);
		}
		return ResultFactory.generateRequestResult(list);
	}

	/**
	 * 安装统计
	 * @param mapContext
	 * @param request
	 * @return
	 */
	@Override
	public RequestResult dispatchListsCount(MapContext mapContext, HttpServletRequest request) {
		String uid=request.getHeader("X-UID");
		String cid=request.getHeader("X-CID");
		User user=this.userService.findByUserId(uid);
		//用户是否存在
		if(user==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断用户是否为公司人员
		Integer status=EmployeeStatus.NORMAL.getValue();
		CompanyEmployee companyEmployee=this.companyEmployeeService.findCompanyByUidAndStatus(uid,status);
		if(companyEmployee==null||!companyEmployee.getCompanyId().equals(cid)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		//判断登陆人职务
		Role role = this.roleService.findRoleByCidAndUid(uid, cid);
		if (role == null) {
			return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_NO_PERMISSION_10003, AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
		}
		if (role.getKey().equals(DealerEmployeeRole.CLERK.getValue())) {
			mapContext.put("salesMan", uid);
		}
		Map<String, Object> result = new HashMap<>();
		//安装状态统计
		Integer nodispatchedworker=DispatchListStatus.NODISPATCHEDWORKER.getValue();
		Integer notakedelivery=DispatchListStatus.NOTAKEDELIVERY.getValue();
		Integer nocontruction=DispatchListStatus.NOCONTRUCTION.getValue();
		Integer notchecked=DispatchListStatus.NOTCHECKED.getValue();
		Integer noreview=DispatchListStatus.NOREVIEW.getValue();
		Integer end=DispatchListStatus.END.getValue();
		List<Integer> list=new ArrayList<>();
		list.add(nodispatchedworker);
		list.add(notakedelivery);
		list.add(nocontruction);
		list.add(notchecked);
		list.add(noreview);
		list.add(end);
		Integer disStatus;
		for(int i=0;i<list.size();i++){
			disStatus=list.get(i);
			mapContext.put("status",disStatus);
			Integer count=this.dispatchListService.findDiscountByCidAndStatus(mapContext);
			switch (disStatus){
				case 0:result.put("nodispatchedworker",count);
				case 1:result.put("notakedelivery",count);
				case 2:result.put("nocontruction",count);
				case 3:result.put("notchecked",count);
				case 4:result.put("noreview",count);
				case 5:result.put("end",count);
			}
		}
		//安装日期统计
		List<DateNum> dateNumList=this.dispatchListService.findDatenumByCreatedAndCid(mapContext);
		result.put("dateNum",dateNumList);
		return ResultFactory.generateRequestResult(result);
	}



}
