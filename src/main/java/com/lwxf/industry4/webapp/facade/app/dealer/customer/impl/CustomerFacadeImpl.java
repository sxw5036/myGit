package com.lwxf.industry4.webapp.facade.app.dealer.customer.impl;

import com.lwxf.commons.constant.CommonConstant;
import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.ValidateUtils;
import com.lwxf.industry4.webapp.baseservice.cache.constant.RedisConstant;
import com.lwxf.industry4.webapp.bizservice.common.CityAreaService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.customer.CompanyCustomerService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserBasisService;
import com.lwxf.industry4.webapp.bizservice.user.UserExtraService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.user.UserThirdInfoService;
import com.lwxf.industry4.webapp.common.authcode.AuthCodeUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.dto.UserInfoObj;
import com.lwxf.industry4.webapp.common.enums.company.*;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.enums.user.*;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.UserExtraUtil;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.companyEmployee.CompanyEmployeeDto;
import com.lwxf.industry4.webapp.domain.dto.customer.CustomerCityCountDto;
import com.lwxf.industry4.webapp.domain.dto.customer.CustomerDto;
import com.lwxf.industry4.webapp.domain.entity.common.CityArea;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.customer.CompanyCustomer;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserBasis;
import com.lwxf.industry4.webapp.domain.entity.user.UserExtra;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.customer.CustomerFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.*;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/4 0004 10:15
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("customerFacade")
public class CustomerFacadeImpl extends BaseFacadeImpl implements CustomerFacade {
	@Resource(name="companyCustomerService")
	private CompanyCustomerService companyCustomerService;
	@Resource(name="userService")
	private UserService userService;
	@Resource(name="userExtraService")
	private UserExtraService userExtraService;
	@Resource(name = "userThirdInfoService")
	private UserThirdInfoService userThirdInfoService;
	@Resource(name="companyService")
	private CompanyService companyService;
    @Resource(name="userBasisService")
	private UserBasisService userBasisService;
    @Resource(name="companyEmployeeService")
    private CompanyEmployeeService companyEmployeeService;
    @Resource(name="roleService")
	private RoleService roleService;
    @Resource(name="cityAreaService")
    private CityAreaService cityAreaService;
	@Resource(name="customOrderService")
    private CustomOrderService customOrderService;


//客户列表

	@Override
	public RequestResult selectCustomersList(MapContext mapContext, Integer pageNum, Integer pageSize,HttpServletRequest request) {
		String uid=request.getHeader("X-UID");
		if(uid==null){
			return ResultFactory.generateErrorResult(AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"),AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(mapContext.get("companyId"))){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_RES_NOT_FOUND_10001,AppBeanInjector.i18nUtil.getMessage("BIZ_RES_NOT_FOUND_10001"));
		}
		CompanyEmployeeDto companyEmployeeDto=this.companyEmployeeService.findKeyByCidAndCustomerManager(cid,uid);
		if(companyEmployeeDto==null){
			return ResultFactory.generateResNotFoundResult();
		}
		String key=companyEmployeeDto.getKey();
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		//判断登陆人的权限（店员只能查看自己的客户）
		if(key.equals(DealerEmployeeRole.CLERK.getValue())){
			mapContext.put("customerManager",uid);
		}
		paginatedFilter.setFilters(mapContext);
		Map dataSort = new HashMap();
		List sorts = new ArrayList();
		dataSort.put("created","desc");
		sorts.add(dataSort);
		paginatedFilter.setSorts(sorts);
		PaginatedList<CustomerDto> customerDtoPaginatedList=this.companyCustomerService.selectByFilter(paginatedFilter);
		for(int i=0;i<customerDtoPaginatedList.getRows().size();i++) {
			String cityName = customerDtoPaginatedList.getRows().get(i).getMergerName();
            String address1=customerDtoPaginatedList.getRows().get(i).getAddress();
            String address="";
            if(address1!=null){
            	address=address1;
			}
			if (cityName != null) {
				int a = cityName.indexOf(",");
				String mergerName = cityName.substring(a + 1);
				String replaceName = mergerName.replace(",", "");//去除逗号后的名字
				address=replaceName;
				if(address1!=null){
					address = replaceName+""+address1;
				}

			}
			customerDtoPaginatedList.getRows().get(i).setMergerName(address);

		}
		MapContext mapContext1=MapContext.newOne();
		mapContext1.put("companyCustomer",customerDtoPaginatedList.getRows());
		return ResultFactory.generateRequestResult(mapContext1,customerDtoPaginatedList.getPagination());
	}


	//客户修改
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateCustomer(String companyId, String userId, MapContext mapContext,HttpServletRequest request) {
		//判断被修改的客户是否和登陆人一个公司
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		//判断登陆人的权限
		String uid=request.getHeader("X-UID");
		Role role=this.roleService.findRoleByCidAndUid(uid,cid);
		String key =role.getKey();
		//判断客户信息是否存在
		CompanyCustomer companyCustomer=this.companyCustomerService.selectCustomerByCUId(companyId,userId);
		if(companyCustomer==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//店员只能操作自己负责的客户
		if(key.equals(DealerEmployeeRole.CLERK.getValue())){
			String customerManager=companyCustomer.getCustomerManager();
			if(!customerManager.equals(uid)){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
			}

		}
		User user=this.userService.findByUserId(userId);
		if(user==null){
			return ResultFactory.generateResNotFoundResult();
		}

		MapContext mapContext1=MapContext.newOne();
		if(mapContext.containsKey("name")){
			mapContext1.put("name",mapContext.get("name"));
		}
		if(mapContext.containsKey("mobile")){
			mapContext1.put("mobile",mapContext.get("mobile"));
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
		if(mapContext.containsKey("birthday")){
			String birthdayValue = mapContext.getTypedValue("birthday",String.class);
			if(CommonConstant.STRING_EMPTY.equals(birthdayValue )){
				birthdayValue = null;
			}
			mapContext1.put("birthday",birthdayValue);
		}
		if(mapContext1.size()>0){
		mapContext1.put("id",userId);
		this.userService.updateByMapContext(mapContext1);
		}
		MapContext mapContext2=MapContext.newOne();
		if(mapContext.containsKey("work")){
			mapContext2.put("work",mapContext.get("work"));
		}
		if(mapContext.containsKey("workUnit")){
			mapContext2.put("workUnit",mapContext.get("workUnit"));
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
		if(mapContext.containsKey("created")){
			mapContext3.put("created",mapContext.get("created"));
		}
		if(mapContext.containsKey("community")){
			mapContext3.put("community",mapContext.get("community"));
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
//客户的添加
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addCustomer(String companyId, MapContext mapContext, String uid,String cid) {
		Map<String, String> errorMap = new HashMap<>();
		//用户表基本信息
		String mobile=(String) mapContext.get("mobile");
		if(mobile==null||mobile.trim().equals("")){
			errorMap.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_NOTNULL,errorMap);
		}
		String customerManager=(String)mapContext.get("customerManager");
		//验证电话号码是否正确
		if (!ValidateUtils.isChinaPhoneNumber(mobile)) {
			errorMap.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorMap);
		}
		User u = userService.findByMobile(mobile);
		//判断用户是否存在
		if (null!=u){
			if(!u.getName().equals(mapContext.getTypedValue("name",String.class))){
				MapContext params = MapContext.newOne();
				params.put("name",mapContext.getTypedValue("name",String.class));
				params.put("id",u.getId());
				this.userService.updateByMapContext(params);
			}

			//判断是否在公司内
			CompanyCustomer companyCus=this.companyCustomerService.selectCustomerByCUId(companyId,u.getId());
			if(companyCus==null){
				//把客户添加到公司下
				CompanyCustomer companyCustomer=new CompanyCustomer();
				String id = companyId;
				companyCustomer.setCompanyId(id);
				companyCustomer.setStatus(CompanyCustomerStatus.CREATE.getValue());
				companyCustomer.setCreator(uid);
				companyCustomer.setCreated(DateUtil.getSystemDate());
				if(customerManager!=null){
					companyCustomer.setCustomerManager(customerManager);
				}else {
				companyCustomer.setCustomerManager(uid);
				}
				companyCustomer.setGrade(CompanyCustomerGrade.LOW.getValue());
				companyCustomer.setUserId(u.getId());
				companyCustomer.setSource(CompanyCustomerSource.CSOCKET.getValue());
				companyCustomer.setCommunity((String)mapContext.get("community"));
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
		user.setSex(UserSex.MAN.getValue());
		user.setMobile(mobile);
		user.setEmail((String)mapContext.get("email"));
		user.setAvatar(AppBeanInjector.configuration.getUserDefaultAvatar());
		user.setTimeZone(WebConstant.TIMEZONE_DEFAULT);
		user.setLanguage(WebConstant.LANGUAGE_DEFAULT);
		user.setCityAreaId((String)mapContext.get("cityAreaId"));
		user.setCreated(DateUtil.getSystemDate());
		user.setState(UserState.ENABLED.getValue());
		user.setLoginName(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.USER_LOGNAME_NO));
		user.setFollowers(0);
		user.setChangedLoginName(false);
		RequestResult result = user.validateFields();
		if (null!=result){
			return result;
		}
		this.userService.add(user);

		//客户与经销商绑定
		CompanyCustomer companyCustomer=new CompanyCustomer();
		companyCustomer.setCompanyId(companyId);
		companyCustomer.setStatus(CompanyCustomerStatus.CREATE.getValue());
		companyCustomer.setCreator(uid);
		companyCustomer.setCreated(DateUtil.getSystemDate());
		if(customerManager!=null){
			companyCustomer.setCustomerManager(customerManager);
		}else {
			companyCustomer.setCustomerManager(uid);
		}
		companyCustomer.setGrade(CompanyCustomerGrade.LOW.getValue());
		companyCustomer.setUserId(user.getId());
		companyCustomer.setSource(CompanyCustomerSource.CSOCKET.getValue());
		companyCustomer.setCommunity((String)mapContext.get("community"));
		companyCustomer.setAddress((String)mapContext.get("address"));
		companyCustomer.setCityAreaId((String)mapContext.get("cityAreaId"));
        this.companyCustomerService.add(companyCustomer);
        //用户基础扩展信息
		String userId=user.getId();
		UserBasis userBasis=new UserBasis();
		userBasis.setUserId(userId);
		userBasis.setAddress((String)mapContext.get("address"));
		userBasis.setEducation(EducationType.UNDERGRADUATE.getValue());
		userBasis.setIncome(IncomeType.FOUR.getValue());
		userBasis.setWork((String)mapContext.get("work"));
		userBasis.setWorkUnit((String)mapContext.get("workUnit"));
		userBasis.setPoliticalOutlook(UserPoliticalOutlookType.MASSES.getValue());
		this.userBasisService.add(userBasis);
		//用户扩展信息
		UserExtra userExtra = new UserExtra();
		userExtra.setUserId(user.getId());
		String pwd = AuthCodeUtils.getRandomNumberCode(6);
		UserExtraUtil.saltingPassword(userExtra,new Md5Hash(pwd).toString());
		this.userExtraService.add(userExtra);

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
		this.userThirdInfoService.add(userThirdInfo);
        String cityAreaId=mapContext.getTypedValue("cityAreaId",String.class);
		CityArea cityArea=this.cityAreaService.findById(cityAreaId);
		String mergerName = "";
		if (null!=cityArea) {
			String merger = cityArea.getMergerName();
			int a = merger.indexOf(",");
			mergerName = merger.substring(a + 1);
		}
		return ResultFactory.generateRequestResult(UserInfoObj.newInfo(user,userExtra,userThirdInfo,userBasis,mergerName));
	}
//删除客户
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteCustomer(String userId) {
		User user=userService.findByUserId(userId);
		if(user==null){
			return ResultFactory.generateResNotFoundResult();
		}
//把客户的状态改为——已流逝
		return ResultFactory.generateRequestResult(this.companyCustomerService.updateCustomerStatus(userId));
	}
//客户详情
	@Override
	public RequestResult findCustomerMessageById(String companyId,String userId,HttpServletRequest request) {
		String uid=request.getHeader("X-UID");
		String cid=request.getHeader("X-CID");
		//判断登陆人和客户是否在一个公司
		if(!cid.equals(companyId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		Role role=this.roleService.findRoleByCidAndUid(uid,cid);
		CustomerDto customerDto=this.companyCustomerService.findCustomerMessageById(companyId,userId);
		if(customerDto==null){
			return ResultFactory.generateResNotFoundResult();
		}
		String customerManager=customerDto.getCustomerManager();
		if(role!=null){
			if(role.getKey().equals(DealerEmployeeRole.CLERK.getValue())){
				//店员只能操作自己负责的客户
				if(!uid.equals(customerManager)){
					return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003,AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
				}

			}
		}
		String merger = customerDto.getMergerName();
		if(merger!=null) {
			int a = merger.indexOf(",");
			String mergerName = merger.substring(a + 1);
			customerDto.setMergerName(mergerName);
		}
		return ResultFactory.generateRequestResult(customerDto);
	}

	@Override
	public RequestResult findCustomersCount(MapContext mapContext, HttpServletRequest request) {
		String uid=request.getHeader("X-UID");
		String cid=request.getHeader("X-CID");
		User user=this.userService.findByUserId(uid);
		//用户是否存在
		if(user==null){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断用户是否为公司人员
		Integer status= EmployeeStatus.NORMAL.getValue();
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
			mapContext.put("saleMan", uid);
		}
		Map<String, Object> result = new HashMap<>();

		//客户新增
        List<DateNum> numList=this.companyCustomerService.findCustomerDatenum(mapContext);
        result.put("dateNum",numList);
		//客户地区统计
		Integer qiTa;
		Integer cityNum=0;
		String weizhi;
		List<CompanyCustomer> companyCustomers=this.companyCustomerService.findCustomerByMap(mapContext);
		List<CustomerCityCountDto> cityCountList=this.companyCustomerService.findCityCount(mapContext);
		for(CustomerCityCountDto customerCityCountDto:cityCountList){
			Integer cityNumber=customerCityCountDto.getCount();
			cityNum = cityNum+cityNumber;
		}
		CustomerCityCountDto customerCityCountDto=new CustomerCityCountDto();
		if(companyCustomers.size()-cityNum>0){
			qiTa=companyCustomers.size()-cityNum;
			weizhi="未知地区";
		}else {
			qiTa=0;
			weizhi="未知地区";
		}
		customerCityCountDto.setCount(qiTa);
		customerCityCountDto.setMergerName(weizhi);
		cityCountList.add(customerCityCountDto);
		result.put("cityCount",cityCountList);
		//成交统计
		Integer noOrder=0;
		Integer ordering=0;
		Integer orderEnd=0;
		   //遍历客户，查看是否有订单
        	for(CompanyCustomer companyCustomer:companyCustomers){
        		String customerId=companyCustomer.getUserId();
        		MapContext mapContext1=MapContext.newOne();
        		mapContext1.put("customerId",customerId);
        		mapContext1.put("companyId",cid);
				List<CustomOrder> customOrder=this.customOrderService.findOrderNumByCustomIdAndCid(mapContext1);
				if(customOrder.isEmpty()){
					noOrder=noOrder+1;
				}else {
					Integer orderStatus= OrderStatus.RECEIVED.getValue();
					mapContext1.put("status",orderStatus);
					List<CustomOrder> customerOrder=this.customOrderService.findOrderNumByCustomIdAndCid(mapContext1);
					if(customerOrder.isEmpty()){
						ordering=ordering+1;
					}else {
						orderEnd=orderEnd+1;
					}
				}
			}
     	result.put("noOrder",noOrder);
		result.put("ordering",ordering);
		result.put("orderEnd",orderEnd);
		return ResultFactory.generateRequestResult(result);
	}


}



