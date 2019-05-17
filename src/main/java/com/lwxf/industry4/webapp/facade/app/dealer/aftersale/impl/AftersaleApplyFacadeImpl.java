package com.lwxf.industry4.webapp.facade.app.dealer.aftersale.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyFilesService;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.aftersale.AftersaleStatus;
import com.lwxf.industry4.webapp.common.enums.aftersale.AftersaleType;
import com.lwxf.industry4.webapp.common.enums.company.DealerEmployeeRole;
import com.lwxf.industry4.webapp.common.enums.company.EmployeeStatus;
import com.lwxf.industry4.webapp.common.enums.order.OrderType;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.aftersale.AftersaleDto;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.companyEmployee.CompanyEmployeeDto;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApply;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApplyFiles;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.aftersale.AftersaleApplyFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/1/2 0002 20:34
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("aftersaleApplyFacade")
public class AftersaleApplyFacadeImpl extends BaseFacadeImpl implements AftersaleApplyFacade {

	@Resource(name = "aftersaleApplyService")
	private AftersaleApplyService aftersaleApplyService;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "companyEmployeeService")
	private CompanyEmployeeService companyEmployeeService;
	@Resource(name = "customOrderService")
	private CustomOrderService customOrderService;
	@Resource(name = "aftersaleApplyFilesService")
	private AftersaleApplyFilesService aftersaleApplyFilesService;
	@Resource(name = "roleService")
	private RoleService roleService;

	/**
	 * 创建售后申请单
	 *
	 * @param companyId
	 * @param request
	 * @param mapContext
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addAftersale(String companyId, HttpServletRequest request, MapContext mapContext) {
		String uid = request.getHeader("X-UID");
		String cid = request.getHeader("X-CID");
		if (uid == null || uid.equals("")) {
			return ResultFactory.generateErrorResult(AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"), AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
		}
		User user = this.userService.findByUserId(uid);
		if (user == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		CompanyEmployeeDto companyEmployeeDto = this.companyEmployeeService.findKeyByCidAndCustomerManager(cid, uid);
		if (companyEmployeeDto == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		String customOrderId = mapContext.getTypedValue("customOrderId", String.class);
		if (customOrderId == null || customOrderId.trim().equals("")) {
			return ResultFactory.generateErrorResult(AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"), AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
		}
		AftersaleApply aftersaleApply = new AftersaleApply();
		aftersaleApply.setCustomOrderId(customOrderId);
		if(mapContext.containsKey("type")) {
			String typevalue = mapContext.getTypedValue("type", String.class);
			aftersaleApply.setType(Integer.valueOf(typevalue));
		}
		aftersaleApply.setNotes(mapContext.getTypedValue("notes", String.class));
		aftersaleApply.setStatus(AftersaleStatus.WAIT.getValue());
		aftersaleApply.setNo(mapContext.getTypedValue("no", String.class));
		aftersaleApply.setCreator(uid);
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date createdValue = dateFormat.parse(mapContext.getTypedValue("created", String.class));
			aftersaleApply.setCreated(createdValue);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		aftersaleApply.setCompanyId(companyId);
		this.aftersaleApplyService.add(aftersaleApply);
		return ResultFactory.generateRequestResult(aftersaleApply);
	}

	/**
	 * 售后单列表
	 *
	 * @param pageNum
	 * @param pageSize
	 * @param mapContext
	 * @param request
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult aftersaleList(Integer pageNum, Integer pageSize, MapContext mapContext, HttpServletRequest request) {
		String uid = request.getHeader("X-UID");
		if (uid == null) {
			return ResultFactory.generateErrorResult(AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"), AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		String cid = request.getHeader("X-CID");
		String companyId = (String) mapContext.get("companyId");
		if (!cid.equals(companyId)) {
			return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_RES_NOT_FOUND_10001, AppBeanInjector.i18nUtil.getMessage("BIZ_RES_NOT_FOUND_10001"));
		}
		CompanyEmployeeDto companyEmployeeDto = this.companyEmployeeService.findKeyByCidAndCustomerManager(cid, uid);
		if (companyEmployeeDto == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		String key = companyEmployeeDto.getKey();
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		//判断登陆人的职位
		if (key.equals(DealerEmployeeRole.CLERK.getValue())) {
			mapContext.put("creator", uid);
		}
		paginatedFilter.setFilters(mapContext);
		Map dataSort = new HashMap();
		List sorts = new ArrayList();
		dataSort.put("created", "desc");
		sorts.add(dataSort);
		paginatedFilter.setSorts(sorts);
		PaginatedList<AftersaleDto> aftersaleDtoPaginatedList = this.aftersaleApplyService.selectByFilter(paginatedFilter);
		MapContext result = MapContext.newOne();
		result.put("aftersaleApply", aftersaleDtoPaginatedList.getRows());
		return ResultFactory.generateRequestResult(result, aftersaleDtoPaginatedList.getPagination());
	}

	/**
	 * 修改申请单
	 *
	 * @param companyId
	 * @param aftersaleId
	 * @param request
	 * @return
	 */

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateAftersaleApply(String companyId, String aftersaleId, HttpServletRequest request, MapContext mapContext) {
		mapContext.put("id", aftersaleId);
		return ResultFactory.generateRequestResult(this.aftersaleApplyService.updateByMapContext(mapContext));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult findAftersaleMessage(String companyId, String aftersaleId, String orderId, HttpServletRequest request) {
		String cid = request.getHeader("X-CID");
		String uid = request.getHeader("X-UID");
		MapContext mapContext = MapContext.newOne();
		if (!cid.equals(companyId)) {
			return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_RES_NOT_FOUND_10001, AppBeanInjector.i18nUtil.getMessage("BIZ_RES_NOT_FOUND_10001"));
		}
		List<AftersaleApplyFiles> aftersaleApplyFilesList = this.aftersaleApplyFilesService.findFilesByAfterId(aftersaleId);
		AftersaleDto aftersaleDto = this.aftersaleApplyService.findAftersaleMessage(aftersaleId);
		if (aftersaleDto == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//调整地址的显示
		String mergerName = aftersaleDto.getMergerName();
		String customerAddress = aftersaleDto.getCustomerAddress();
		if (mergerName != null) {
			int a = mergerName.indexOf(",");
			String cityName = mergerName.substring(a + 1);
			String replaceName = cityName.replace(",", "");
			if (customerAddress != null) {
				aftersaleDto.setCustomerAddress(replaceName + customerAddress);
			} else {
				aftersaleDto.setCustomerAddress(replaceName);
			}
		}
		//查询创建人信息
		String creator = aftersaleDto.getCreator();
		User creatUser = this.userService.findByUserId(creator);
		String creatorName = creatUser.getName();
		String creatorMobile = creatUser.getMobile();
		aftersaleDto.setCreatorMobile(creatorMobile);
		aftersaleDto.setCreatorName(creatorName);
		Integer aftersaleType=aftersaleDto.getType();
		if(aftersaleType!=null){
			aftersaleDto.setAftersaleType(aftersaleType.toString());
		}
		if (aftersaleDto.getStatus() != AftersaleStatus.WAIT.getValue()) {
			String checker = aftersaleDto.getChecker();
			User user = this.userService.findByUserId(checker);
			String loginUserName = user.getName();
			aftersaleDto.setCheckerName(loginUserName);
		}
		if (!aftersaleApplyFilesList.isEmpty()) {
			mapContext.put("aftersaleApplyFiles", aftersaleApplyFilesList);
		}
		mapContext.put("aftersaleApply", aftersaleDto);
		return ResultFactory.generateRequestResult(mapContext);

	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addFiles(String aftersaleId, List<MultipartFile> multipartFiles, HttpServletRequest request) {
		String uid = request.getHeader("X-UID");
		List<Map<String, Object>> list = new ArrayList<>();
		AftersaleApplyFiles aftersaleApplyFiles = new AftersaleApplyFiles();
		UploadInfo uploadInfo;
		for (MultipartFile mul : multipartFiles) {
			uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, mul, UploadResourceType.AFTERSALE_APPLY, aftersaleId);
			aftersaleApplyFiles.setAftersaleApplyId(aftersaleId);
			aftersaleApplyFiles.setName(uploadInfo.getFileName());
			aftersaleApplyFiles.setCreator(uid);
			aftersaleApplyFiles.setCreated(DateUtil.getSystemDate());
			aftersaleApplyFiles.setPath(uploadInfo.getRelativePath());
			aftersaleApplyFiles.setFullPath(WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
			aftersaleApplyFiles.setMime(uploadInfo.getFileMimeType().getRealType());
			aftersaleApplyFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
			aftersaleApplyFiles.setStatus(UploadType.FORMAL.getValue());
			this.aftersaleApplyFilesService.add(aftersaleApplyFiles);
			MapContext mapContext = MapContext.newOne();
			mapContext.put("imagePath", uploadInfo.getRelativePath());
			list.add(mapContext);
		}
		return ResultFactory.generateRequestResult(list);
	}

	@Override
	public RequestResult aftersaleApplyRedis(HttpServletRequest request) {
		MapContext mapContext = MapContext.newOne();
		String no = AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.AFTERSALE_APPLY_NO);
		String creator = request.getHeader("X-UID");
		User user = this.userService.findByUserId(creator);
		String userName = user.getName();
		String mobile = user.getMobile();
		Date created = DateUtil.getSystemDate();
		mapContext.put("no", no);
		mapContext.put("creator", userName);
		mapContext.put("mobile", mobile);
		mapContext.put("created", created);
		return ResultFactory.generateRequestResult(mapContext);
	}

	@Override
	public RequestResult findAftersaleCount(MapContext mapContext, HttpServletRequest request) {
		String uid = request.getHeader("X-UID");
		String cid = request.getHeader("X-CID");
		if (uid == null || uid.equals("")) {
			return ResultFactory.generateErrorResult(AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"), AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
		}
		User user = this.userService.findByUserId(uid);
		if (user == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		Integer status = EmployeeStatus.NORMAL.getValue();
		CompanyEmployee companyEmployee = this.companyEmployeeService.findCompanyByUidAndStatus(uid, status);
		if (companyEmployee == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		if (!companyEmployee.getCompanyId().equals(cid)) {
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_REQUEST_PARAM_ERROR_10000, AppBeanInjector.i18nUtil.getMessage("BIZ_REQUEST_PARAM_ERROR_10000"));
		}
		Role role = this.roleService.findRoleByCidAndUid(uid, cid);
		String key = role.getKey();
		if (key.equals(DealerEmployeeRole.CLERK.getValue())) {
			mapContext.put("saleMan", uid);
		}
		//售后原因统计
		Integer loufaValue = AftersaleType.LOUFA.getValue();
		Integer sunhuaiValue = AftersaleType.SUNHUAI.getValue();
		Integer cuofaValue = AftersaleType.CUOFA.getValue();
		Integer qitaValue = AftersaleType.QITA.getValue();
		List<Integer> list = new ArrayList<>();
		list.add(loufaValue);
		list.add(sunhuaiValue);
		list.add(cuofaValue);
		list.add(qitaValue);
		Integer type;
		Map<String,Object> result = new HashMap<>();
		for (int i = 0; i < list.size(); i++) {
			type = list.get(i);
			mapContext.put("type", type);
			Integer count = this.aftersaleApplyService.findCountByCidAndType(mapContext);
			switch (type) {
				case 0:
					result.put("louFa", count);
					break;
				case 1:
					result.put("sunHuai", count);
					break;
				case 2:
					result.put("cuoFa", count);
					break;
				case 3:
					result.put("qiTa", count);
					break;
			}
		}
		mapContext.remove("type");
		//处理结果统计
		Integer supplement;//同意并补产
		Integer replacement;//同意并补发
		Integer stat = AftersaleStatus.REFUSE.getValue();
		mapContext.put("status",stat);
		Integer refuse = this.aftersaleApplyService.findCountByCidAndStatus(mapContext);//拒绝
		result.put("refuse", refuse);
		mapContext.remove("status");
		List<AftersaleApply> aftersaleApplyList = this.aftersaleApplyService.findAftersaleListByCid(mapContext);
		if (aftersaleApplyList.isEmpty()) {
			supplement = 0;
			result.put("supplement", supplement);
			replacement = 0;
			result.put("replacement", replacement);
		} else {
			List orderIds = new ArrayList();
			for (AftersaleApply orderId : aftersaleApplyList) {
				orderIds.add(orderId.getResultOrderId());
			}
			List<Integer> orderTypes = new ArrayList();
			Integer supple=OrderType.SUPPLEMENTORDER.getValue();
			Integer replace=OrderType.REPLACEMENT.getValue();
			orderTypes.add(supple);
			orderTypes.add(replace);
			for (int i = 0; i < orderTypes.size(); i++) {
				Integer orderType = orderTypes.get(i);
				Integer num = this.aftersaleApplyService.findCountByOederIdAndType(orderType, orderIds);
				switch (orderType) {
					case 1:
						result.put("supplement", num);
						break;
					case 6:
						result.put("replacement", num);
						break;
				}
			}

		}
		//柱状图数据统计
		mapContext.remove("status");
		List<DateNum> dateNum=this.aftersaleApplyService.findAftersaleByDate(mapContext);
		result.put("dateNum",dateNum);
		return ResultFactory.generateRequestResult(result);
	}
}
