package com.lwxf.industry4.webapp.facade.app.factory.factoryCustomer.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.apache.shiro.crypto.hash.Md5Hash;

import com.lwxf.commons.constant.CommonConstant;
import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.ValidateUtils;
import com.lwxf.industry4.webapp.baseservice.cache.constant.RedisConstant;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.customer.CompanyCustomerService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.user.UserBasisService;
import com.lwxf.industry4.webapp.common.authcode.AuthCodeUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.dto.UserInfoObj;
import com.lwxf.industry4.webapp.common.enums.company.*;
import com.lwxf.industry4.webapp.common.enums.user.*;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.UserExtraUtil;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.customer.CustomerDto;
import com.lwxf.industry4.webapp.domain.dto.customer.FactoryCustomerDto;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.customer.CompanyCustomer;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserBasis;
import com.lwxf.industry4.webapp.domain.entity.user.UserExtra;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryCustomer.FactoryCustomerFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/2 0002 9:18
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factoryCustomerFacade")
public class FactoryCustomerFacadeImpl extends BaseFacadeImpl implements FactoryCustomerFacade {
	@Resource(name = "companyCustomerService")
	private CompanyCustomerService companyCustomerService;
	@Resource(name = "companyService")
    private CompanyService companyService;
	@Resource(name = "customOrderService")
	private CustomOrderService customOrderService;
	@Resource(name = "companyEmployeeService")
	private CompanyEmployeeService companyEmployeeService;
	@Resource(name = "userBasisService")
	private UserBasisService userBasisService;
	/**
	 * C端客户概览
	 * @return
	 */
	@Override
	public RequestResult findFactoryCustomersCount() {
		Integer customerAmount=0;//客户总数
		Integer todayNew=0;//今日新增
		Integer thisMonth=0;//本月趋势
		Integer lastMoth=0;
		boolean upOrDown;
		//查询所有经销商
		//List<Company> AllDealers=this.findAllDealer();
		MapContext result = MapContext.newOne();
		List newAddList=new ArrayList();
			//客户总数
		customerAmount = this.companyCustomerService.findCustomerAmount();
			//今日新增
			List<FactoryCustomerDto> customerDtoList=this.companyCustomerService.findCustomerTodayNew();
			if(customerDtoList!=null&&customerDtoList.size()>0) {
				for(FactoryCustomerDto factoryCustomerDto:customerDtoList){
					String cityName=factoryCustomerDto.getCustomerMergerName();
					String mergerName;
					if(cityName!=null){
						int i=cityName.indexOf(",");
						mergerName=cityName.substring(i);
						factoryCustomerDto.setCustomerAddress(mergerName+factoryCustomerDto.getCustomerAddress());
					}
				}
				newAddList.add(customerDtoList);
				todayNew = customerDtoList.size();
			}
			//本月趋势
			Integer thisNum = this.companyCustomerService.findCustomerThisMonth();
			thisMonth = thisMonth+thisNum;
			 Integer lastNum = this.companyCustomerService.findLastMoth();
			 lastMoth = lastMoth+lastNum;

		//本年度月增客户曲线图
		List<DateNum> dateNumList=this.companyCustomerService.findFactoryCustomerEveryMonthAdd();
		int[] countMonth=new int[12];
		if(dateNumList.size()>0&&dateNumList!=null){
			for(int i=0;i<dateNumList.size();i++){
				DateNum dateNum=dateNumList.get(i);
				if(dateNum!=null){
					String mothValue=dateNum.getCreatTime();
					int a = mothValue.indexOf("-");
					String moth=mothValue.substring(a+1);
					Integer intMoth=Integer.parseInt(moth);
					countMonth[intMoth-1]=dateNum.getCount();
				}
			}
		}
		result.put("countMonth",countMonth);
		if(thisMonth>lastMoth){
			upOrDown=true;//上升
		}else {
			upOrDown=false;//下降
		}
		result.put("customerAmount", customerAmount);
		result.put("todayNew", todayNew);
		result.put("thisMonth",thisMonth);
		result.put("upOrDown",upOrDown);
		result.put("newAddList",newAddList);
		return ResultFactory.generateRequestResult(result);
	}

	/**
	 * 客户列表
	 * @param pageNum
	 * @param pageSize
	 * @param params
	 * @return
	 */
	@Override
	public RequestResult findCustomers(Integer pageNum, Integer pageSize, MapContext params) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
//        List<Company> allDealers=this.findAllDealer();
//        List companyIds=new ArrayList();
//        for(Company company:allDealers){
//        	String companyId=company.getId();
//        	companyIds.add(companyId);
//		}
//        params.put("companyIds",companyIds);
		paginatedFilter.setFilters(params);
		Map dataSort = new HashMap();
		List sorts = new ArrayList();
		if (params.containsKey("order")) {
			if (!params.getTypedValue("order", String.class).equals("")) {
				if (params.containsKey("sort")) {
					String sort = params.getTypedValue("sort", String.class);
					if (params.getTypedValue("order", String.class).equals("byCreated")) {
						dataSort.put("created", sort);
						sorts.add(dataSort);
						paginatedFilter.setSorts(sorts);
					}
				}
			}
		}else {
			dataSort.put("created", "desc");
			sorts.add(dataSort);
			paginatedFilter.setSorts(sorts);
		}
		if(params.containsKey("topParam")){
			String topParam=params.getTypedValue("topParam",String.class);
			if(topParam.equals("2")){
				params.put("todayNew",topParam);
			}else if(topParam.equals("3")){
				params.put("thisMonth",topParam);
			}
		}
		PaginatedList<FactoryCustomerDto> customersList=this.companyCustomerService.findCustomers(paginatedFilter);
		for(FactoryCustomerDto factoryCustomerDto:customersList.getRows()){
			String cityName=factoryCustomerDto.getCustomerMergerName();
			String mergerName;
			if(cityName!=null){
				int i=cityName.indexOf(",");
				mergerName=cityName.substring(i);
				factoryCustomerDto.setCustomerAddress(mergerName+factoryCustomerDto.getCustomerAddress());
			}
			String companyId=factoryCustomerDto.getCompanyId();
			String companyName=this.companyService.findById(companyId).getName();
			factoryCustomerDto.setCompanyName(companyName);
		}
		MapContext result=MapContext.newOne();
		result.put("customersList",customersList.getRows());
		return ResultFactory.generateRequestResult(result,customersList.getPagination());
	}

	/**
	 * 客户详情
	 * @param userId
	 * @param customerId
	 * @return
	 */
	@Override
	public RequestResult findCustomerInfo(String userId, String customerId) {
		User user= AppBeanInjector.userService.findByUserId(userId);
		if(user==null){
			return ResultFactory.generateResNotFoundResult();
		}
		CompanyCustomer companyCustomer=this.companyCustomerService.findById(customerId);
		if (companyCustomer==null){
			return ResultFactory.generateResNotFoundResult();
		}
		String companyId=companyCustomer.getCompanyId();
		Company company=this.companyService.findById(companyId);
		String dealerCompanyName=company.getName();
		CustomerDto customerDto=this.companyCustomerService.findCustomerMessageById(companyId,userId);
		//拼接客户地址
		String mergerName = customerDto.getMergerName();
		if(mergerName!=null) {
			int a = mergerName.indexOf(",");
			String address = mergerName.substring(a + 1);
			if(customerDto.getAddress()!=null&&customerDto.getAddress()!=""){
				address=address+customerDto.getAddress();
			}
			customerDto.setAddress(address);
		}
		customerDto.setDealerCompanyName(dealerCompanyName);
		return ResultFactory.generateRequestResult(customerDto);
	}

	/**
	 * 客户下的订单列表
	 * @param userId
	 * @param customerId
	 * @return
	 */
	@Override
	public RequestResult findCustomerOrderInfo(String userId, String customerId) {
		User user= AppBeanInjector.userService.findByUserId(userId);
		if(user==null){
			return ResultFactory.generateResNotFoundResult();
		}
		CompanyCustomer companyCustomer=this.companyCustomerService.findById(customerId);
		if (companyCustomer==null){
			return ResultFactory.generateResNotFoundResult();
		}
		String companyId=companyCustomer.getCompanyId();
		List<MapContext> customOrderList=this.customOrderService.findCustomerOrderInfo(userId,companyId);
		return ResultFactory.generateRequestResult(customOrderList);
	}

	/**
	 * F端添加客户
	 * @param mapContext
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult factoryAddCustomer(String dealerId, MapContext mapContext, HttpServletRequest request) {
		Map<String, String> errorMap = new HashMap<>();
		String uid=request.getHeader("X-UID");
		//验证电话号码是否正确
		String mobile=(String) mapContext.get(WebConstant.KEY_ENTITY_MOBILE);
		if(mobile==null||mobile.trim().equals("")){
			errorMap.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_NOTNULL,errorMap);
		}
		if (!ValidateUtils.isChinaPhoneNumber(mobile)) {
			errorMap.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorMap);
		}
		//判断用户是否存在
		User u = AppBeanInjector.userService.findByMobile(mobile);
		if (null!=u) {
			MapContext params = MapContext.newOne();
			if (!u.getName().equals(mapContext.getTypedValue("name", String.class))) {
				params.put("name", mapContext.getTypedValue("name", String.class));

			}
			if(mapContext.containsKey("sex")){
				Integer sex=Integer.valueOf(mapContext.getTypedValue("sex",String.class));
				if(u.getSex()!=sex){
					params.put("sex",sex);
				}
			}
			if(mapContext.containsKey("cityAreaId")){
				String cityAreaId=mapContext.getTypedValue("cityAreaId", String.class);
				if(!u.getCityAreaId().equals(cityAreaId)){
					params.put("cityAreaId",cityAreaId);
				}
			}
			if(params.size()>0) {
				params.put("id", u.getId());
				AppBeanInjector.userService.updateByMapContext(params);
			}
			//判断是否在公司内
			CompanyCustomer companyCus=this.companyCustomerService.selectCustomerByCUId(dealerId,u.getId());
			if(companyCus==null){
				//把客户添加到公司下
				CompanyCustomer companyCustomer=new CompanyCustomer();
				String id = dealerId;
				companyCustomer.setCompanyId(id);
				companyCustomer.setStatus(CompanyCustomerStatus.CREATE.getValue());
				companyCustomer.setCreator(uid);
				companyCustomer.setCreated(DateUtil.getSystemDate());
				if(mapContext.containsKey("customerManager")) {
					String customerManager = (String) mapContext.get("customerManager");
					companyCustomer.setCustomerManager(customerManager);
				}
				companyCustomer.setGrade(CompanyCustomerGrade.LOW.getValue());
				companyCustomer.setUserId(u.getId());
				companyCustomer.setSource(CompanyCustomerSource.FACTORYADD.getValue());
				if(mapContext.containsKey("remarks")) {
					String remarks=mapContext.getTypedValue("remarks",String.class);
					companyCustomer.setRemarks(remarks);
				}
				if(mapContext.containsKey("cityAreaId")){
					companyCustomer.setCityAreaId(mapContext.getTypedValue("cityAreaId",String.class));
				}else {
					companyCustomer.setCityAreaId(u.getCityAreaId());
				}
				this.companyCustomerService.add(companyCustomer);
				return ResultFactory.generateSuccessResult();
			}
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_USER_HAS_BEEN_ADDED_10056,AppBeanInjector.i18nUtil.getMessage("BIZ_USER_HAS_BEEN_ADDED_10056"));
		}

		User user=new User();
		user.setType(UserType.CLIENT.getValue());
		user.setName((String)mapContext.get("name"));
		if(mapContext.containsKey("sex")){
			Integer sex=Integer.valueOf(mapContext.getTypedValue("sex",String.class));
			user.setSex(sex);
		}else {
			user.setSex(UserSex.MAN.getValue());
		}
		user.setMobile(mobile);
		user.setAvatar(AppBeanInjector.configuration.getUserDefaultAvatar());
		user.setTimeZone(WebConstant.TIMEZONE_DEFAULT);
		user.setLanguage(WebConstant.LANGUAGE_DEFAULT);
		if(mapContext.containsKey("cityAreaId")){
			user.setCityAreaId((String)mapContext.get("cityAreaId"));
		}
		user.setCreated(DateUtil.getSystemDate());
		user.setState(UserState.ENABLED.getValue());
		if(mapContext.containsKey("loginName")){
			String loginName=mapContext.getTypedValue("loginName",String.class);
			if(loginName!=null&&!loginName.equals("")){
				user.setLoginName(loginName);
			}
		}else {
			user.setLoginName(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.USER_LOGNAME_NO));
		}
		user.setFollowers(0);
		user.setChangedLoginName(false);
		RequestResult result = user.validateFields();
		if (null!=result){
			return result;
		}
		AppBeanInjector.userService.add(user);
		//客户与经销商绑定
		CompanyCustomer companyCustomer=new CompanyCustomer();
		companyCustomer.setCompanyId(dealerId);
		companyCustomer.setStatus(CompanyCustomerStatus.CREATE.getValue());
		companyCustomer.setCreator(uid);
		companyCustomer.setCreated(DateUtil.getSystemDate());
		if(mapContext.containsKey("customerManager")) {
			String customerManager = (String) mapContext.get("customerManager");
			companyCustomer.setCustomerManager(customerManager);
		}
		companyCustomer.setGrade(CompanyCustomerGrade.LOW.getValue());
		companyCustomer.setUserId(user.getId());
		companyCustomer.setSource(CompanyCustomerSource.FACTORYADD.getValue());
		if(mapContext.containsKey("address")){
			companyCustomer.setAddress((String)mapContext.get("address"));
		}
		if(mapContext.containsKey("cityAreaId")){
			companyCustomer.setCityAreaId((String)mapContext.get("cityAreaId"));
		}
		if(mapContext.containsKey("remarks")) {
			String remarks=mapContext.getTypedValue("remarks",String.class);
			companyCustomer.setRemarks(remarks);
		}
		this.companyCustomerService.add(companyCustomer);
		//用户基础扩展信息
		String userId=user.getId();
		UserBasis userBasis=new UserBasis();
		userBasis.setUserId(userId);
		if(mapContext.containsKey("address")){
			companyCustomer.setAddress((String)mapContext.get("address"));
		}
		userBasis.setContactNumber(mobile);
		userBasis.setEducation(EducationType.UNDERGRADUATE.getValue());
		userBasis.setIncome(IncomeType.FOUR.getValue());
		userBasis.setPoliticalOutlook(UserPoliticalOutlookType.MASSES.getValue());
		this.userBasisService.add(userBasis);
		//用户扩展信息
		UserExtra userExtra = new UserExtra();
		userExtra.setUserId(user.getId());
		String pwd = mapContext.getTypedValue("password",String.class);
		UserExtraUtil.saltingPassword(userExtra,new Md5Hash(pwd).toString());
		AppBeanInjector.userExtraService.add(userExtra);
		// 第三方账号信息
		UserThirdInfo userThirdInfo = new UserThirdInfo();
		userThirdInfo.setWxNickname(user.getMobile());
		userThirdInfo.setWxIsBind(Boolean.FALSE);
		userThirdInfo.setWxIsSubscribe(Boolean.FALSE);
		userThirdInfo.setEmailIsBind(Boolean.FALSE);
		userThirdInfo.setMobileIsBind(Boolean.FALSE);
		userThirdInfo.setUserId(user.getId());
		userThirdInfo.setAppToken(UserExtraUtil.generateAppToken(userExtra,null));
		AppBeanInjector.redisUtils.hPut(RedisConstant.PLATFORM_TAG, user.getId(), Integer.valueOf(1));
		AppBeanInjector.userThirdInfoService.add(userThirdInfo);
		return ResultFactory.generateRequestResult(UserInfoObj.newInfo(user,userExtra,userThirdInfo,userBasis,null));
	}

	/**
	 * 员工列表
	 * @param companyId
	 * @return
	 */
	@Override
	public RequestResult findCompanyEmployees(String companyId) {
		MapContext mapContext=MapContext.newOne();
		Integer employeeStatus = EmployeeStatus.NORMAL.getValue();
		mapContext.put("employeeStatus", employeeStatus);
		mapContext.put("companyId",companyId);
		List<MapContext> employeeList = this.companyEmployeeService.findCompanyEmployees(mapContext);
		return ResultFactory.generateRequestResult(employeeList);
	}

	/**
	 * F端客户修改
	 * @param dealerId
	 * @param userId
	 * @param mapContext
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateDealerCustomer(String dealerId, String userId, MapContext mapContext) {
		//判断客户信息是否存在
		CompanyCustomer companyCustomer=this.companyCustomerService.selectCustomerByCUId(dealerId,userId);
		if(companyCustomer==null){
			return ResultFactory.generateResNotFoundResult();
		}
		User user=AppBeanInjector.userService.findByUserId(userId);
		if(user==null){
			return ResultFactory.generateResNotFoundResult();
		}
		MapContext mapContext1=MapContext.newOne();
		if(mapContext.containsKey("name")){
			mapContext1.put("name",mapContext.get("name"));
		}
		if (mapContext.containsKey("sex")){
			String sexValue = mapContext.getTypedValue("sex",String.class);
			if(CommonConstant.STRING_EMPTY.equals(sexValue )){
				sexValue = null;
			}
			mapContext1.put("sex",sexValue);
		}
		if(mapContext.containsKey("cityAreaId")){
			mapContext1.put("cityAreaId",mapContext.get("cityAreaId"));
		}
		if(mapContext1.size()>0){
			mapContext1.put("id",userId);
			AppBeanInjector.userService.updateByMapContext(mapContext1);
		}
		MapContext mapContext2=MapContext.newOne();
		if(mapContext.containsKey("mobile")){
			mapContext2.put("contactNumber",mapContext.get("mobile"));
		}
		if(mapContext.containsKey("address")){
			mapContext2.put("address",mapContext.get("address"));
		}
		if(mapContext2.size()>0){
			mapContext2.put("userId",userId);
			this.userBasisService.updateByMapContext(mapContext2);
		}
		MapContext mapContext3=MapContext.newOne();
		if(mapContext.containsKey("customerManager")){
			mapContext3.put("customerManager",mapContext.get("customerManager"));
		}
		if(mapContext.containsKey("source")){
			String sourceValue = mapContext.getTypedValue("source",String.class);
			if(CommonConstant.STRING_EMPTY.equals(sourceValue )){
				sourceValue = null;
			}
			mapContext3.put("source",sourceValue);
		}
		if(mapContext.containsKey("cityAreaId")){
			mapContext3.put("cityAreaId",mapContext.get("cityAreaId"));
		}
		if(mapContext.containsKey("address")){
			mapContext3.put("address",mapContext.get("address"));
		}
		if(mapContext3.size()>0){
			String id=companyCustomer.getId();
			mapContext3.put("id",id);
			this.companyCustomerService.updateByMapContext(mapContext3);
		}
		return ResultFactory.generateRequestResult("update success");
	}

}
