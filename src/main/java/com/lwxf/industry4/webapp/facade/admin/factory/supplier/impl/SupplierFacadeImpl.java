package com.lwxf.industry4.webapp.facade.admin.factory.supplier.impl;

import javax.annotation.Resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.commons.utils.ValidateUtils;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.procurement.PurchaseProductService;
import com.lwxf.industry4.webapp.bizservice.product.ProductService;
import com.lwxf.industry4.webapp.bizservice.supplier.SupplierProductService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.company.CompanyType;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyDto;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.procurement.PurchaseProduct;
import com.lwxf.industry4.webapp.domain.entity.product.Product;
import com.lwxf.industry4.webapp.domain.entity.supplier.SupplierProduct;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.supplier.SupplierFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/29/029 15:25
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("supplierFacade")
public class SupplierFacadeImpl extends BaseFacadeImpl implements SupplierFacade {
	@Resource(name = "companyService")
	private CompanyService companyService;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "productService")
	private ProductService productService;
	@Resource(name = "supplierProductService")
	private SupplierProductService supplierProductService;
	@Resource(name = "purchaseProductService")
	private PurchaseProductService purchaseProductService;

	@Override
	public RequestResult findAllSupplierList(Integer pageNum,Integer pageSize,MapContext mapContext) {
		mapContext.put("type", Arrays.asList(CompanyType.SUPPLIER.getValue()));
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		paginatedFilter.setFilters(mapContext);
		Map<String,String> created = new HashMap<String, String>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		List sort = new ArrayList();
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.companyService.selectByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addSupplierProdcut(String id, List<SupplierProduct> supplierProductList) {
		List productIds = new ArrayList();
		for(SupplierProduct supplierProduct : supplierProductList){
			//判断供应商下是否有改产品 允许重复添加
			SupplierProduct oldProduc = this.supplierProductService.findOneBySupplierAndProductId(id,supplierProduct.getProductId());
			if(oldProduc!=null){
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_PRODUCTS_ALREADY_EXIST_10074,AppBeanInjector.i18nUtil.getMessage("BIZ_PRODUCTS_ALREADY_EXIST_10074"));
			}
			Company company = this.companyService.findById(id);
			if(company==null||!company.getType().equals(CompanyType.SUPPLIER.getValue())){
				return ResultFactory.generateResNotFoundResult();
			}
			Product product = this.productService.findById(supplierProduct.getProductId());
			if(product==null){
				return ResultFactory.generateResNotFoundResult();
			}
			this.supplierProductService.add(supplierProduct);
			productIds.add(supplierProduct.getProductId());
		}
		return ResultFactory.generateRequestResult(this.supplierProductService.findListBySupplierAndProductIds(id,productIds));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addSupplier(CompanyDto company) {
		//判断公司编号是否重复
		if(this.companyService.selectByNo(company.getNo())!=null){
			Map<String,String> result = new HashMap<String,String>();
			result.put(WebConstant.STRING_NO,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,result);
		}
		this.companyService.add(company);
		company.setCreatorName(this.userService.findById(company.getCreator()).getName());
		return ResultFactory.generateRequestResult(company);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addSupplierLeader(User user,String id) {
		//验证电话号码是否正确
		if (!ValidateUtils.isChinaPhoneNumber(user.getMobile())) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
			return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR, user.getMobile());
		}
		//判断手机号是否已存在
		if(this.userService.findByMobile(user.getMobile())!=null){
			Map result = new HashMap<>();
			result.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
			return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR,result);
		}
		Company company = this.companyService.findById(id);
		if(company.getLeader()!=null){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ASSIGNEE_IS_EXISTED_10009,AppBeanInjector.i18nUtil.getMessage("BIZ_ASSIGNEE_IS_EXISTED_10009"));
		}
		//给经销商设定用户生日
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			user.setBirthday(simpleDateFormat.parse("1970-1-1 00:00:00"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.userService.add(user);
		//修改供应商公司信息
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		mapContext.put("leader",user.getId());
		mapContext.put("leaderTel",user.getMobile());
		this.companyService.updateByMapContext(mapContext);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findProductList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		paginatedFilter.setFilters(mapContext);
		return ResultFactory.generateRequestResult(this.supplierProductService.selectByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateProduct(String id, String productId, MapContext mapContext) {
		//判断供应商产品是否存在
		SupplierProduct supplierProduct = this.supplierProductService.findById(productId);
		if(supplierProduct==null||!supplierProduct.getSupplierId().equals(id)){
			return ResultFactory.generateResNotFoundResult();
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID,productId);
		this.supplierProductService.updateByMapContext(mapContext);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteProduct(String id, String productId) {
		//判断供应商产品是否存在
		SupplierProduct supplierProduct = this.supplierProductService.findById(productId);
		if(supplierProduct==null||!supplierProduct.getSupplierId().equals(id)){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断是否被采购单使用
		List<PurchaseProduct> purchaseProducts= this.purchaseProductService.findListByProductId(productId);
		if(purchaseProducts.size()!=0){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_RES_BE_IN_USE_10006,AppBeanInjector.i18nUtil.getMessage("BIZ_RES_BE_IN_USE_10006"));
		}
		this.supplierProductService.deleteById(productId);
		return ResultFactory.generateSuccessResult();
	}
}
