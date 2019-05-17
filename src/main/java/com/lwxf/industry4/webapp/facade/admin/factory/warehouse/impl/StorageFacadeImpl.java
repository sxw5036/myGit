package com.lwxf.industry4.webapp.facade.admin.factory.warehouse.impl;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import com.lwxf.industry4.webapp.bizservice.company.EmployeePermissionService;
import com.lwxf.industry4.webapp.bizservice.procurement.PurchaseService;
import com.lwxf.industry4.webapp.bizservice.product.ProductCategoryService;
import com.lwxf.industry4.webapp.bizservice.system.MenusService;
import com.lwxf.industry4.webapp.bizservice.system.RolePermissionService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StockService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.product.ProductCategoryType;
import com.lwxf.industry4.webapp.common.enums.system.MenusDisabled;
import com.lwxf.industry4.webapp.common.enums.system.MenusKey;
import com.lwxf.industry4.webapp.common.enums.system.MenusType;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.dto.procurement.PurchaseDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.StockDto;
import com.lwxf.industry4.webapp.domain.entity.product.ProductCategory;
import com.lwxf.industry4.webapp.domain.entity.system.Menus;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStock;
import com.lwxf.industry4.webapp.domain.entity.warehouse.Storage;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.warehouse.StorageFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/13/013 15:34
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("storageFacade")
public class StorageFacadeImpl extends BaseFacadeImpl implements StorageFacade {
	@Resource(name = "storageService")
	private StorageService storageService;
	@Resource(name = "productCategoryService")
	private ProductCategoryService productCategoryService;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "stockService")
	private StockService stockService;
	@Resource(name = "menusService")
	private MenusService menusService;
	@Resource(name = "finishedStockService")
	private FinishedStockService finishedStockService;
	@Resource(name = "employeePermissionService")
	private EmployeePermissionService employeePermissionService;
	@Resource(name = "rolePermissionService")
	private RolePermissionService rolePermissionService;
	@Resource(name = "purchaseService")
	private PurchaseService purchaseService;

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addStorage(Storage storage) {
		//判断分类是否存在
		ProductCategory category = this.productCategoryService.findById(storage.getProductCategoryId());
		if(category==null||!category.getKey().equals(storage.getKey())){
			return ResultFactory.generateResNotFoundResult();
		}
		//判断名称是否重复
		if(this.storageService.findOneByName(storage.getName())!=null){
			Map result = new HashMap<String,String>();
			result.put(WebConstant.KEY_ENTITY_NAME,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,result);
		}
		//判断管理员是否存在
		if(!this.userService.isExist(storage.getStorekeeper())){
			return ResultFactory.generateResNotFoundResult();
		}
		this.storageService.add(storage);
		//给当前新增仓库设置 库存下菜单 以及 出入库下菜单
		Menus storageMenus = new Menus();
		storageMenus.setType(MenusType.FACTORY_BACKSTAGE.getValue());
		storageMenus.setKey(MenusKey.STOCKMNG.getValue()+"_"+storage.getId());
		storageMenus.setFolder(false);
		storageMenus.setName(storage.getName());
		storageMenus.setOutLink(false);
		storageMenus.setParentId(this.menusService.findOneByKey(MenusKey.STOCKMNG.getValue()).getId());
		//普通仓库的话  新增出入库下菜单 成品库的话 只新增库存菜单
		if(this.productCategoryService.findById(storage.getProductCategoryId()).getType().equals(ProductCategoryType.FINISHED_PRODUCT.getValue())){
			storageMenus.setPath("finishedstock?pathdata="+storage.getId());
		}else{
			storageMenus.setPath("warehouselist?pathdata="+storage.getId());
			Menus storageOutputInMenus = new Menus();
			storageOutputInMenus.setType(MenusType.FACTORY_BACKSTAGE.getValue());
			storageOutputInMenus.setKey(MenusKey.OUTINMNG.getValue()+"_"+storage.getId());
			storageOutputInMenus.setFolder(false);
			storageOutputInMenus.setName(storage.getName());
			storageOutputInMenus.setOutLink(false);
			storageOutputInMenus.setParentId(this.menusService.findOneByKey(MenusKey.OUTINMNG.getValue()).getId());
			storageOutputInMenus.setPath("warehouseoutin?pathdata="+storage.getId());
			storageOutputInMenus.setRelevantResource(0);
			storageOutputInMenus.setDisabled(MenusDisabled.UNDISABLED.getValue());
			this.menusService.add(storageOutputInMenus);
		}
		storageMenus.setRelevantResource(0);
		storageMenus.setDisabled(MenusDisabled.UNDISABLED.getValue());
		this.menusService.add(storageMenus);



		return ResultFactory.generateRequestResult(this.storageService.findOneById(storage.getId()));
	}

	@Override
	public RequestResult findStorageList(MapContext mapContext, Integer pageNum, Integer pageSize) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageNum(pageNum);
		pagination.setPageSize(pageSize);
		paginatedFilter.setPagination(pagination);
		paginatedFilter.setFilters(mapContext);
		List<Map<java.lang.String,java.lang.String>> sortList= new ArrayList<Map<java.lang.String,java.lang.String>>();
		Map<java.lang.String, java.lang.String> createdSort= new HashMap<java.lang.String, java.lang.String>();
		createdSort.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sortList.add(createdSort);
		paginatedFilter.setSorts(sortList);
		return ResultFactory.generateRequestResult(this.storageService.selectByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateStorage(String id, MapContext mapContext) {
		//判断资源是否存在
		Storage storage = this.storageService.findById(id);
		if(storage==null){
			return ResultFactory.generateResNotFoundResult();
		}
		String name = mapContext.getTypedValue("name", String.class);
		if (name!=null&&!name.trim().equals("")){
			Storage byName = this.storageService.findOneByName(name);
			if(byName!=null&&!byName.getId().equals(id)){
				Map result = new HashMap();
				result.put(WebConstant.KEY_ENTITY_NAME,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
				return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,result);
			}
			//如果修改名称 且名称符合要求 则顺带修改仓库菜单信息
			this.menusService.updateNameByLikeKey(name,id);
		}
		String productCategoryId = mapContext.getTypedValue("productCategoryId", String.class);
		String key = mapContext.getTypedValue(WebConstant.KEY_WxPay_KEY, String.class);
		if(productCategoryId!=null&&key!=null){
			ProductCategory category = this.productCategoryService.findById(productCategoryId);
			if(category!=null){
				if (!category.getKey().equals(key)){
					return ResultFactory.generateResNotFoundResult();
				}
			}else{
				return ResultFactory.generateResNotFoundResult();
			}
		}else{
			mapContext.put(WebConstant.KEY_WxPay_KEY,null);
		}
		String storekeeper = mapContext.getTypedValue("storekeeper", String.class);
		if(storekeeper!=null){
			if(!this.userService.isExist(storekeeper)){
				return ResultFactory.generateResNotFoundResult();
			}
		}
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		this.storageService.updateByMapContext(mapContext);
		return ResultFactory.generateRequestResult(this.storageService.findOneById(id));
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteById(String id) {
		//判断该资源是否存在
		Storage storage = this.storageService.findById(id);
		if(storage==null){
			return ResultFactory.generateSuccessResult();
		}
		//判断是否被库存表使用
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		paginatedFilter.setFilters(mapContext);
		PaginatedList<StockDto> filter = this.stockService.findListByFilter(paginatedFilter);
		if(filter.getRows()!=null&&filter.getRows().size()!=0){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_RES_BE_IN_USE_10006,AppBeanInjector.i18nUtil.getMessage("BIZ_RES_BE_IN_USE_10006"));
		}

		//判断是否存在采购单
		MapContext productMapContext = new MapContext();
		productMapContext.put("storageId",id);
		paginatedFilter.setFilters(productMapContext);
		PaginatedList<PurchaseDto> purchaseDtoPaginatedList = this.purchaseService.selectByFilter(paginatedFilter);
		if(purchaseDtoPaginatedList.getRows()!=null&&purchaseDtoPaginatedList.getRows().size()!=0){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_RES_BE_IN_USE_10006,AppBeanInjector.i18nUtil.getMessage("BIZ_RES_BE_IN_USE_10006"));
		}

		//判断是否被成品库使用
		List<FinishedStock> finishedStockList = this.finishedStockService.findListByStorageId(id);
		if(finishedStockList!=null&&finishedStockList.size()!=0){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_RES_BE_IN_USE_10006,AppBeanInjector.i18nUtil.getMessage("BIZ_RES_BE_IN_USE_10006"));
		}
		this.storageService.deleteById(id);

		//删除仓库时 删除所有使用该仓库菜单的
		List<Menus> menusList = this.menusService.findListByLikeKey(storage.getId());
		for(Menus menus:menusList){
			//删除使用该菜单的员工权限
			this.employeePermissionService.deleteByKey(menus.getKey());
			//删除使用该菜单的 角色权限
			this.rolePermissionService.deleteByKey(menus.getKey());
			//删除菜单
			this.menusService.deleteById(menus.getId());
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findAllProduct() {
		List<StockDto> stockDtos= this.storageService.findAllProduct();
		return ResultFactory.generateRequestResult(stockDtos);
	}
}
