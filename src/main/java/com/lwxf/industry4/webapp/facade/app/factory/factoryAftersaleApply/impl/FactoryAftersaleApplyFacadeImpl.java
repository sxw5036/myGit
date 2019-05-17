package com.lwxf.industry4.webapp.facade.app.factory.factoryAftersaleApply.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyFilesService;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyService;
import com.lwxf.industry4.webapp.bizservice.common.CityAreaService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.customer.CompanyCustomerService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.customorder.OrderProductService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.aftersale.AftersaleStatus;
import com.lwxf.industry4.webapp.common.enums.aftersale.AftersaleType;
import com.lwxf.industry4.webapp.common.enums.company.CompanyStatus;
import com.lwxf.industry4.webapp.common.enums.company.CompanyType;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.aftersale.AftersaleDto;
import com.lwxf.industry4.webapp.domain.dto.aftersale.AftersaleOrderDto;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApply;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApplyFiles;
import com.lwxf.industry4.webapp.domain.entity.common.CityArea;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.customer.CompanyCustomer;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.customorder.OrderProduct;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryAftersaleApply.FactoryAftersaleApplyFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：F端app售后管理
 *
 * @author：zhangxiaolin(3965488@qq.com)
 * @create：2019/3/30 0030 14:08
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factoryAftersaleApplyFacade")
public class FactoryAftersaleApplyFacadeImpl extends BaseFacadeImpl implements FactoryAftersaleApplyFacade {
	@Resource(name = "aftersaleApplyService")
	private AftersaleApplyService aftersaleApplyService;
	@Resource(name = "companyService")
	private CompanyService companyService;
	@Resource(name = "customOrderService")
	private CustomOrderService customOrderService;
	@Resource(name = "aftersaleApplyFilesService")
	private AftersaleApplyFilesService aftersaleApplyFilesService;
	@Resource(name = "cityAreaService")
	private CityAreaService cityAreaService;
	@Resource(name = "companyCustomerService")
	private CompanyCustomerService companyCustomerService;
	@Resource(name = "dispatchBillService")
	private DispatchBillService dispatchBillService;
	@Resource(name = "orderProductService")
	private OrderProductService orderProductService;

	/**
	 * 查询今日 本周 本月的售后单数量
	 *
	 * @param companyId
	 * @return
	 */
	@Override
	public RequestResult findFactoryAftersaleApply(String companyId) {
		MapContext result = MapContext.newOne();
		Integer today = this.aftersaleApplyService.findTodayCount();
		Integer thisWeek = this.aftersaleApplyService.findThisWeekCount();
		Integer thisMonth = this.aftersaleApplyService.findThisMonthCount();
		result.put("today", today);
		result.put("thisWeek", thisWeek);
		result.put("thisMonth", thisMonth);
		return ResultFactory.generateRequestResult(result);
	}

	/**
	 * 售后详情
	 *
	 * @param aftersaleApplyId
	 * @param companyId
	 * @return
	 */
	@Override
	public RequestResult factoryAftersaleApplyInfo(String aftersaleApplyId, String companyId) {
		MapContext result = MapContext.newOne();
		//售后信息
		AftersaleDto aftersaleApply = this.aftersaleApplyService.findOneById(aftersaleApplyId);
		if (aftersaleApply == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		String orderId = aftersaleApply.getCustomOrderId();
		MapContext params = MapContext.newOne();
		params.put("orderId", orderId);
		AftersaleOrderDto customOrder = this.aftersaleApplyService.findOrderBaseInfoByOrderId(params);
		result.put("customOrder", customOrder);
		result.put("aftersaleApply", aftersaleApply);
		//处理结果订单
		String resultOrderId = aftersaleApply.getResultOrderId();
		List<MapContext> aftersaleProduct = new ArrayList<>();
		List<Map> dispatchBillList = new ArrayList<>();
		if (resultOrderId != null && !resultOrderId.equals("")) {
			//产品信息
			aftersaleProduct = this.aftersaleApplyService.findAftersaleProductByorderId(resultOrderId);
			//发货物流信息
			dispatchBillList = this.dispatchBillService.findDispatchList(resultOrderId);

		}
		result.put("aftersaleProduct", aftersaleProduct);
		result.put("dispatchBillList", dispatchBillList);
		return ResultFactory.generateRequestResult(result);
	}

	/**
	 * 经销商列表
	 *
	 * @return
	 */
	@Override
	public RequestResult findDealersList(MapContext params) {
		List<Company> AllDealers = this.findAllDealer(params);
		if (AllDealers.size() < 0 || AllDealers == null) {
			return ResultFactory.generateSuccessResult();
		}
		List list = new ArrayList();
		for (Company company : AllDealers) {
			MapContext mapContext = MapContext.newOne();
			String name = company.getName();//公司名称
			String companyId = company.getId();
			mapContext.put("name", name);//公司名称
			mapContext.put("dealerId", companyId);//经销商公司Id
			//经销商门店区域地址
			String cityAreaId = company.getCityAreaId();
			String mergerName = "";
			if (cityAreaId != null && !cityAreaId.equals("")) {
				CityArea cityArea = this.cityAreaService.findById(cityAreaId);
				String mergerNameValue = cityArea.getMergerName();
				int a = mergerNameValue.indexOf(",");
				mergerName = mergerNameValue.substring(a + 1);
			}
			mapContext.put("mergerName", mergerName);
			//经销商详细地址
			String address = company.getAddress();
			mapContext.put("address", address);
			//经销商负责人姓名
			String leader = company.getLeader();
			User user = AppBeanInjector.userService.findByUserId(leader);
			String leaderName = "";
			if (user != null) {
				leaderName = user.getName();
			}
			mapContext.put("leaderName", leaderName);
			//负责人电话
			String leaderTel = company.getLeaderTel();
			mapContext.put("leaderTel", leaderTel);
			list.add(mapContext);
		}

		return ResultFactory.generateRequestResult(list);
	}


	/**
	 * 客户列表
	 *
	 * @param dealerId
	 * @return
	 */
	@Override
	public RequestResult findCustomerList(String dealerId) {
		List list = new ArrayList();
		List<CompanyCustomer> companyCustomers = this.companyCustomerService.findCustomerListByCid(dealerId);
		if (companyCustomers.size() == 0 || companyCustomers == null) {
			MapContext mapContext = MapContext.newOne();
			List<MapContext> orderList=this.customOrderService.findByCid(dealerId);
			mapContext.put("userId", "");
			mapContext.put("customerName", "");
			mapContext.put("customOrderList", orderList);
			mapContext.put("mergerName", "");
			mapContext.put("address", "");
			mapContext.put("mobile", "");
			list.add(mapContext);
		}else {
			for (CompanyCustomer companyCustomer : companyCustomers) {
				MapContext mapContext = MapContext.newOne();
				String userId = companyCustomer.getUserId();
				User user = AppBeanInjector.userService.findByUserId(userId);
				String customerName = user.getName();
				String cityAreaId = companyCustomer.getCityAreaId();
				String mergerName = "";
				if (cityAreaId != null && !cityAreaId.equals("")) {
					CityArea cityArea = this.cityAreaService.findById(cityAreaId);
					String mergerNameValue = cityArea.getMergerName();
					int a = mergerNameValue.indexOf(",");
					mergerName = mergerNameValue.substring(a + 1);
				}
				String address = companyCustomer.getAddress();
				String mobile = user.getMobile();
				List<MapContext> customOrderList = this.customOrderService.findOrderListByCidAndUid(dealerId, userId);
				mapContext.put("userId", userId);
				mapContext.put("customerName", customerName);
				mapContext.put("customOrderList", customOrderList);
				mapContext.put("mergerName", mergerName);
				mapContext.put("address", address);
				mapContext.put("mobile", mobile);
				list.add(mapContext);
			}
		}
		return ResultFactory.generateRequestResult(list);
	}


	/**
	 * 售后列表查询（按条件查询）
	 *
	 * @param pageNum
	 * @param pageSize
	 * @param mapContext
	 * @return
	 */
	@Override
	public RequestResult findAftersaleApplyList(Integer pageNum, Integer pageSize, MapContext mapContext) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		paginatedFilter.setFilters(mapContext);
		Map dataSort = new HashMap();
		List sorts = new ArrayList();
		if (mapContext.containsKey("order")) {
			if (!mapContext.getTypedValue("order", String.class).equals("")) {
				if (mapContext.containsKey("sort")) {
					String sort = mapContext.getTypedValue("sort", String.class);
					if (mapContext.getTypedValue("order", String.class).equals("byCreated")) {//按创建时间排序
						dataSort.put("created", sort);
						paginatedFilter.setSorts(sorts);
					} else if (mapContext.getTypedValue("order", String.class).equals("byActualDate")) {//按发货时间排序
						dataSort.put("actualDate", sort);
					} else if (mapContext.getTypedValue("order", String.class).equals("byProduceTime")) {//按生产时间排序
						dataSort.put("documentaryTime", sort);
					}
					sorts.add(dataSort);
					paginatedFilter.setSorts(sorts);
				}
			}
		} else {
			dataSort.put("created", "desc");
			sorts.add(dataSort);
			paginatedFilter.setSorts(sorts);
		}
		PaginatedList<MapContext> aftersalesList = this.aftersaleApplyService.findAftersaleApplyList(paginatedFilter);
		MapContext result=MapContext.newOne();
		result.put("aftersalesList",aftersalesList.getRows());
		return ResultFactory.generateRequestResult(result,aftersalesList.getPagination());
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addFactoryAftersale(MapContext mapContext, HttpServletRequest request) {
		String inputer=mapContext.getTypedValue("inputer",String.class);
		String resultOrderId = null;
		//源订单编号
		String orderId = mapContext.getTypedValue("orderId", String.class);
		//售后单类型
		Integer aftersaleType = mapContext.getTypedValue("aftersaleType", Integer.class);
		//售后订单编号
		String orderNo=mapContext.getTypedValue("orderNo",String.class);
		String aftersaleOrderNo = this.aftersaleApplyService.getAftersaleOrderNo(orderId,orderNo);
		//判断资源是否同时被操作
		if(aftersaleOrderNo==null){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_RES_BE_IN_USE_10006, AppBeanInjector.i18nUtil.getMessage("BIZ_RES_BE_IN_USE_10006"));
		}
		//String creator = request.getHeader("X-UID");
		String companyId = mapContext.getTypedValue("dealerId", String.class);
		if (aftersaleType == AftersaleType.BULIAO.getValue()) {
			//原订单ID
			CustomOrder oldCustomOrder = this.customOrderService.findByOrderId(orderId);
			String address = oldCustomOrder.getAddress();
			String cityAreaId = oldCustomOrder.getCityAreaId();
			//创建售后订单
			String customerId = mapContext.getTypedValue("customerId", String.class);
			User user = AppBeanInjector.userService.findByUserId(customerId);
			BigDecimal orderMount = mapContext.getTypedValue("chargeAmount", BigDecimal.class);
			CustomOrder customOrder = new CustomOrder();
			customOrder.setNo(aftersaleOrderNo);
			customOrder.setCompanyId(companyId);
			customOrder.setCustomerId(customerId);
			if(mapContext.containsKey("created")) {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date createdValue = dateFormat.parse(mapContext.getTypedValue("created", String.class));
					customOrder.setCreated(createdValue);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else {
				customOrder.setCreated(DateUtil.getSystemDate());
			}
			customOrder.setCityAreaId(cityAreaId);
			customOrder.setAddress(address);
			customOrder.setCreator(inputer);
			customOrder.setStatus(OrderStatus.TO_AUDIT.getValue());
			customOrder.setType(1);
			if(user!=null) {
				customOrder.setCustomerTel(user.getMobile());
			}
			customOrder.setAmount(new BigDecimal(0));
			customOrder.setImprest(new BigDecimal(0));
			customOrder.setRetainage(new BigDecimal(0));
			customOrder.setEarnest(0);
			customOrder.setDesignFee(0);
			customOrder.setFactoryPrice(new BigDecimal(0));
			customOrder.setMarketPrice(new BigDecimal(0));
			customOrder.setDiscounts(new BigDecimal(0));
			customOrder.setFactoryDiscounts(new BigDecimal(0));
			customOrder.setFactoryFinalPrice(orderMount);
			customOrder.setConfirmFprice(false);
			customOrder.setConfirmCprice(false);
			if (mapContext.containsKey("consigneeName")) {
				customOrder.setConsigneeName(mapContext.getTypedValue("consigneeName", String.class));
			}
			if (mapContext.containsKey("consigneeTel")) {
				customOrder.setConsigneeTel(mapContext.getTypedValue("consigneeTel", String.class));
			}
			RequestResult result = customOrder.validateFields();
			if (null != result) {
				return result;
			}
			this.customOrderService.add(customOrder);
			resultOrderId = customOrder.getId();

			//创建售后订单产品信息
			OrderProduct orderProduct = new OrderProduct();
			orderProduct.setCustomOrderId(customOrder.getId());
			orderProduct.setColor(mapContext.get("color").toString());
			orderProduct.setType(Integer.valueOf(mapContext.get("type").toString()));
			orderProduct.setDoor(mapContext.get("door").toString());
			orderProduct.setSeries(Integer.valueOf(mapContext.get("series").toString()));
			orderProduct.setCreated(DateUtil.getSystemDate());
			orderProduct.setCreator(inputer);
			this.orderProductService.add(orderProduct);

		}
		//创建售后单
		AftersaleApply aftersaleApply = new AftersaleApply();
		aftersaleApply.setCustomOrderId(mapContext.getTypedValue("orderId", String.class));
		if (mapContext.containsKey("aftersaleType")) {
			String typevalue = mapContext.getTypedValue("aftersaleType", String.class);
			aftersaleApply.setType(Integer.valueOf(typevalue));
		}
		aftersaleApply.setType(mapContext.getTypedValue("aftersaleType", Integer.class));
		aftersaleApply.setNotes(mapContext.getTypedValue("notes", String.class));
		aftersaleApply.setStatus(AftersaleStatus.COMPLETED.getValue());
		aftersaleApply.setNo(aftersaleOrderNo);
		aftersaleApply.setCreator(inputer);
		if(mapContext.containsKey("created")) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String created = mapContext.get("created").toString();
				Date createdValue = dateFormat.parse(created);
				aftersaleApply.setCreated(createdValue);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else {
			aftersaleApply.setCreated(DateUtil.getSystemDate());
		}
		aftersaleApply.setCompanyId(companyId);
		if (mapContext.containsKey("suggest")) {
			aftersaleApply.setInformation(mapContext.getTypedValue("suggest", String.class));
		}
		if (mapContext.containsKey("isCharge")) {
			Integer isCharge = mapContext.getTypedValue("isCharge", Integer.class);
			if (isCharge == 1) {
				aftersaleApply.setCharge(true);
			}
			if (isCharge == 0) {
				aftersaleApply.setCharge(false);
			}
		} else {
			aftersaleApply.setCharge(false);
		}
		if (mapContext.containsKey("chargeAmount")) {
			aftersaleApply.setChargeAmount(mapContext.getTypedValue("chargeAmount", BigDecimal.class));
		} else {
			BigDecimal amount = new BigDecimal(0.00);
			aftersaleApply.setChargeAmount(amount);
		}
		aftersaleApply.setResultOrderId(resultOrderId);
		this.aftersaleApplyService.add(aftersaleApply);
		return ResultFactory.generateRequestResult(aftersaleApply);
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
			String imagePath=WebUtils.getDomainUrl() + uploadInfo.getRelativePath();
			mapContext.put("imagePath", imagePath);
			list.add(mapContext);
		}
		return ResultFactory.generateRequestResult(list);
	}

	public List<Company> findAllDealer(MapContext params) {
		List companyTypeList = new ArrayList();
		companyTypeList.add(CompanyType.MANUFACTURERS.getValue());
		companyTypeList.add(CompanyType.DIRECT_SHOP.getValue());
		companyTypeList.add(CompanyType.SHOP_IN_STORE.getValue());
		companyTypeList.add(CompanyType.EXCLUSIVE_SHOP.getValue());
		companyTypeList.add(CompanyType.FRANCHISED_STORE.getValue());
		companyTypeList.add(CompanyType.LOOSE_ORDER.getValue());
		Integer status= CompanyStatus.NORMAL.getValue();
		params.put("companyTypeList",companyTypeList);
		params.put("status",status);
		List<Company> companyList = this.companyService.findAllCompany(params);
		return companyList;
	}

}
