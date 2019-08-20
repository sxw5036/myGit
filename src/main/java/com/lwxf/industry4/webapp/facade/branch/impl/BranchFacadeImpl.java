package com.lwxf.industry4.webapp.facade.branch.impl;

import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.apache.shiro.crypto.hash.Md5Hash;

import com.lwxf.commons.uniquekey.IIdGenerator;
import com.lwxf.commons.uniquekey.LwxfWorkerIdGenerator;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.ValidateUtils;
import com.lwxf.industry4.webapp.baseservice.cache.constant.RedisConstant;
import com.lwxf.industry4.webapp.bizservice.branch.BranchService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.company.EmployeeInfoService;
import com.lwxf.industry4.webapp.bizservice.company.EmployeePermissionService;
import com.lwxf.industry4.webapp.bizservice.contentmng.ContentsContentService;
import com.lwxf.industry4.webapp.bizservice.contentmng.ContentsService;
import com.lwxf.industry4.webapp.bizservice.contentmng.ContentsTypeService;
import com.lwxf.industry4.webapp.bizservice.product.ProductCategoryService;
import com.lwxf.industry4.webapp.bizservice.system.LogisticsCompanyService;
import com.lwxf.industry4.webapp.bizservice.system.RolePermissionService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserBasisService;
import com.lwxf.industry4.webapp.bizservice.user.UserExtraService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.user.UserThirdInfoService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageService;
import com.lwxf.industry4.webapp.common.authcode.AuthCodeUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.LogisticsCompany.LogisticsCompanyStatus;
import com.lwxf.industry4.webapp.common.enums.company.*;
import com.lwxf.industry4.webapp.common.enums.product.ProductCategoryType;
import com.lwxf.industry4.webapp.common.enums.system.RoleType;
import com.lwxf.industry4.webapp.common.enums.user.EmployeeInfoStatus;
import com.lwxf.industry4.webapp.common.enums.user.UserSex;
import com.lwxf.industry4.webapp.common.enums.user.UserState;
import com.lwxf.industry4.webapp.common.enums.user.UserType;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.UserExtraUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.contentmng.ContentsDto;
import com.lwxf.industry4.webapp.domain.entity.branch.Branch;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.company.EmployeeInfo;
import com.lwxf.industry4.webapp.domain.entity.contentmng.ContentsContent;
import com.lwxf.industry4.webapp.domain.entity.contentmng.ContentsType;
import com.lwxf.industry4.webapp.domain.entity.product.ProductCategory;
import com.lwxf.industry4.webapp.domain.entity.system.LogisticsCompany;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.system.RolePermission;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserBasis;
import com.lwxf.industry4.webapp.domain.entity.user.UserExtra;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;
import com.lwxf.industry4.webapp.domain.entity.warehouse.Storage;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.branch.BranchFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/6/5/005 9:51
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("branchFacade")
public class BranchFacadeImpl extends BaseFacadeImpl implements BranchFacade {
	@Resource(name = "branchService")
	private BranchService branchService;
	@Resource(name = "companyService")
	private CompanyService companyService;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "userExtraService")
	private UserExtraService userExtraService;
	@Resource(name = "userThirdInfoService")
	private UserThirdInfoService userThirdInfoService;
	@Resource(name = "userBasisService")
	private UserBasisService userBasisService;
	@Resource(name = "roleService")
	private RoleService roleService;
	@Resource(name = "companyEmployeeService")
	private CompanyEmployeeService companyEmployeeService;
	@Resource(name = "rolePermissionService")
	private RolePermissionService rolePermissionService;
	@Resource(name = "employeePermissionService")
	private EmployeePermissionService employeePermissionService;
	@Resource(name = "employeeInfoService")
	private EmployeeInfoService employeeInfoService;
	@Resource(name = "productCategoryService")
	private ProductCategoryService productCategoryService;
	@Resource(name = "storageService")
	private StorageService storageService;
	@Resource(name = "contentsTypeService")
	private ContentsTypeService contentsTypeService;
	@Resource(name = "contentsService")
	private ContentsService contentsService;
	@Resource(name = "contentsContentService")
	private ContentsContentService contentsContentService;
	@Resource(name = "logisticsCompanyService")
	private LogisticsCompanyService logisticsCompanyService;

	@Override
	public RequestResult findBranchList(MapContext mapContext,Integer pageNum,Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		paginatedFilter.setFilters(mapContext);
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		return ResultFactory.generateRequestResult(this.branchService.findListByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addBrandInfo(Branch branch) {

		//验证电话号码是否正确
		if (!ValidateUtils.isChinaPhoneNumber(branch.getTel())) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorMap);
		}
		//判断手机号是否已存在
		if(this.userService.findByMobile(branch.getTel())!=null){
			Map result = new HashMap<>();
			result.put(WebConstant.KEY_ENTITY_MOBILE, AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,result);
		}

		//企业信息添加
		this.branchService.add(branch);
//		//对企业的有效期进行AES加密
		String id=branch.getId();
		String branchName=branch.getName();
		String leaderName=branch.getLeaderName();
		String leaderTel=branch.getTel();
		String expireDateValue=branch.getExpireDate();
		String keys=branchName+leaderName+leaderTel;
		String aesEncode=AESEncode(keys,expireDateValue);
		if(aesEncode==null){
			return ResultFactory.generateErrorResult(ErrorCodes.SYS_UNKNOW_00000,AppBeanInjector.i18nUtil.getMessage("SYS_UNKNOW_00000"));
		}
        //更新企业的有效期为加密后的数据
		MapContext params=MapContext.newOne();
		params.put("id",id);
		params.put("expireDate",aesEncode);
		this.branchService.updateByMapContext(params);
		//用户信息
		User user = new User();
		user.setName(branch.getLeaderName()	);
		user.setMobile(branch.getTel());
		user.setType(UserType.FACTORY.getValue());
		user.setSex(UserSex.MAN.getValue());
		user.setAvatar(AppBeanInjector.configuration.getUserDefaultAvatar());
		user.setTimeZone(WebConstant.TIMEZONE_DEFAULT);
		user.setLanguage(WebConstant.LANGUAGE_DEFAULT);
		user.setCreated(DateUtil.getSystemDate());
		user.setState(UserState.ENABLED.getValue());
		user.setLoginName(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.USER_LOGNAME_NO));
		user.setFollowers(0);
		user.setChangedLoginName(false);
		user.setBranchId(branch.getId());
		this.userService.add(user);

		//公司信息添加
		Company company = new Company();
		company.setType(CompanyType.MANUFACTURERS.getValue());
		company.setAddress(branch.getAddress());
		company.setFollowers(0);
		company.setCreator(WebUtils.getCurrUserId());
		company.setCreated(DateUtil.getSystemDate());
		company.setBranchId(branch.getId());
		company.setLeader(user.getId());
		company.setLeaderTel(branch.getTel());
		company.setFounderName(branch.getLeaderName());
		company.setName(branch.getName());
		company.setStatus(CompanyStatus.NORMAL.getValue());
		company.setGrade(CompanyGrade.LEVELONE.getValue());
		company.setContractExpiredDate(branch.getExpireDate());
		company.setCityAreaId(branch.getArea());
		this.companyService.add(company);

		//经销商角色信息添加
		String[] roleName = new String[]{DealerEmployeeRole.SHOPKEEPER.getName(),DealerEmployeeRole.CLERK.getName(),DealerEmployeeRole.MANAGER.getName()};
		String[] key = new String[]{DealerEmployeeRole.SHOPKEEPER.getValue(),DealerEmployeeRole.CLERK.getValue(),DealerEmployeeRole.MANAGER.getValue()};
		for(int i = 0;i<roleName.length;i++){
			Role role = new Role();
			role.setName(roleName[i]);
			role.setType(RoleType.DEALER.getValue());
			role.setKey(key[i]);
			role.setBranchId(branch.getId());
			role.setAdmin(true);
			this.roleService.add(role);
		}


		//基础角色信息添加 :管理层
		Role role = new Role();
		role.setAdmin(true);
		role.setBranchId(branch.getId());
		role.setKey(FactoryEmployeeRole.SUPER_COOL.getValue());
		role.setName(FactoryEmployeeRole.SUPER_COOL.getName());
		role.setType(RoleType.FACTORY.getValue());
		this.roleService.add(role);
		//查询红田管理层的角色ID  赋值红田管理层的权限赋值给新企业的管理层
		Role superCool = this.roleService.selectByKey(FactoryEmployeeRole.SUPER_COOL.getValue(), "40ord3va6adp");
		//基础角色权限信息添加
		List<RolePermission> rolePermissionList= this.rolePermissionService.selectRolePermissionList(superCool.getId());
		if(rolePermissionList!=null&&rolePermissionList.size()!=0){
			for(RolePermission rolePermission:rolePermissionList){
				//重新生成主键ID
				rolePermission.setId(AppBeanInjector.idGererateFactory.nextStringId());
				rolePermission.setRoleId(role.getId());
			}
			this.rolePermissionService.addList(rolePermissionList);
		}


		//用户扩展信息
		UserExtra userExtra = new UserExtra();
		userExtra.setUserId(user.getId());
		UserExtraUtil.saltingPassword(userExtra,new Md5Hash("111111").toString());
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


		//用户基本信息表
		UserBasis userBasis = new UserBasis();
		userBasis.setUserId(user.getId());
		this.userBasisService.add(userBasis);

		//给该用户添加公司角色 管理层
		CompanyEmployee companyEmployee = new CompanyEmployee();
		companyEmployee.setCompanyId(company.getId());
		companyEmployee.setUserId(user.getId());
		//公司老板编号暂不设置
		companyEmployee.setRoleId(role.getId());
		companyEmployee.setStatus(EmployeeStatus.NORMAL.getValue());
		companyEmployee.setCreated(DateUtil.getSystemDate());
		this.companyEmployeeService.add(companyEmployee);
		//把总经理相关权限添加至 员工表中
		if(rolePermissionList!=null&&rolePermissionList.size()!=0){
			for(RolePermission rolePermission:rolePermissionList){
				//重新生成主键ID
				rolePermission.setId(AppBeanInjector.idGererateFactory.nextStringId());
				//用公司员工主键ID替换权限ID
				rolePermission.setRoleId(companyEmployee.getId());
			}
			this.employeePermissionService.addList(rolePermissionList);
		}

		//新增员工时 设置 员工信息 初始数据
		EmployeeInfo employeeInfo = new EmployeeInfo();
		employeeInfo.setCompanyEmployeeId(companyEmployee.getId());
		employeeInfo.setUserId(user.getId());
		employeeInfo.setStatus(EmployeeInfoStatus.NORMAL.getValue()+"");
		this.employeeInfoService.add(employeeInfo);
		//产品类型:成品
		ProductCategory productCategory = new ProductCategory();
		productCategory.setBranchId(branch.getId());
		productCategory.setKey("cp");
		productCategory.setName("成品");
		productCategory.setNotes("成品");
		productCategory.setType(ProductCategoryType.FINISHED_PRODUCT.getValue());
		this.productCategoryService.add(productCategory);
		//仓库:成品物流仓
		Storage storage = new Storage();
		storage.setCreator(WebUtils.getCurrUserId());
		storage.setCreated(DateUtil.getSystemDate());
		storage.setBranchId(branch.getId());
		storage.setName("成品物流仓");
		storage.setNotes("成品库发货专用仓库");
		storage.setProductCategoryId(productCategory.getId());
		storage.setStorekeeper(user.getId());
		this.storageService.add(storage);
		String answerId = "";
		//文件内容类型
		List<ContentsType> contentsTypeListByBranchId = this.contentsTypeService.findContentsTypeListByBranchId("40ord3va6adp");
		for(ContentsType contentsType :contentsTypeListByBranchId){
			if(contentsType.getId().equals(com.lwxf.industry4.webapp.common.enums.content.ContentsType.ANSWER.getValue())){
				answerId=contentsType.getId();
			}
			this.contentsTypeService.addContentType(contentsType);
		}
		//问题帮助
		List<ContentsDto> contentsList = this.contentsService.findTopContentsList(com.lwxf.industry4.webapp.common.enums.content.ContentsType.ANSWER.getValue(), "40ord3va6adp");
		for (ContentsDto contentsDto:contentsList){
			contentsDto.setContentsTypeId(answerId);
			contentsDto.setBranchId(branch.getId());
			contentsDto.setCreated(DateUtil.getSystemDate());
			contentsDto.setCreator(WebUtils.getCurrUserId());
			this.contentsService.add(contentsDto);
		}
		//物流公司
		MapContext mapContext=MapContext.newOne();
		mapContext.put("branchId",WebUtils.getCurrBranchId());
		mapContext.put("status", LogisticsCompanyStatus.NORMAL.getValue());
		List<LogisticsCompany> allNormalByBranchId = this.logisticsCompanyService.findAllNormalByBranchId(mapContext);
		for(LogisticsCompany logisticsCompany: allNormalByBranchId){
			logisticsCompany.setCreated(DateUtil.getSystemDate());
			logisticsCompany.setCreator(WebUtils.getCurrUserId());
			logisticsCompany.setBranchId(branch.getId());
			this.logisticsCompanyService.add(logisticsCompany);
		}
		return ResultFactory.generateSuccessResult();
	}

	private String AESEncode(String keys, String expireDateValue) {
		try {
			//1.构造密钥生成器，指定为AES算法,不区分大小写
			KeyGenerator keygen= KeyGenerator.getInstance("AES");
			//2.根据ecnodeRules规则初始化密钥生成器
			//生成一个128位的随机源,根据传入的字节数组
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG") ;
			secureRandom.setSeed(keys.getBytes());
			keygen.init(128, secureRandom);
			//keygen.init(128, new SecureRandom(keys.getBytes()));
			//3.产生原始对称密钥
			SecretKey original_key=keygen.generateKey();
			//4.获得原始对称密钥的字节数组
			byte [] raw=original_key.getEncoded();
			//5.根据字节数组生成AES密钥
			SecretKey key=new SecretKeySpec(raw, "AES");
			//6.根据指定算法AES自成密码器
			Cipher cipher=Cipher.getInstance("AES");
			//7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
			cipher.init(Cipher.ENCRYPT_MODE, key);
			//8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
			byte [] byte_encode=expireDateValue.getBytes("utf-8");
			//9.根据密码器的初始化方式--加密：将数据加密
			byte [] byte_AES=cipher.doFinal(byte_encode);
			//10.将加密后的数据转换为字符串
			String AES_encode=new String(new BASE64Encoder().encode(byte_AES));
			//11.将字符串返回
			return AES_encode;
		} catch (Exception e) {
			e.printStackTrace();
		}

		//如果有错就返加nulll
		return null;

	}
}
