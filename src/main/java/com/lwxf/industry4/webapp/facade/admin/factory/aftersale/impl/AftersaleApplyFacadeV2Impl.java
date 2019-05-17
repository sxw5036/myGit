package com.lwxf.industry4.webapp.facade.admin.factory.aftersale.impl;

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
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.aftersale.AftersaleStatus;
import com.lwxf.industry4.webapp.common.enums.aftersale.AftersaleType;
import com.lwxf.industry4.webapp.common.enums.company.CompanyStatus;
import com.lwxf.industry4.webapp.common.enums.company.CompanyType;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApply;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApplyFiles;
import com.lwxf.industry4.webapp.domain.entity.common.CityArea;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.customer.CompanyCustomer;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.customorder.OrderProduct;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.aftersale.AfterSaleApplyV2Facade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
@Component("fAftersaleApplyV2Facade")
public class AftersaleApplyFacadeV2Impl extends BaseFacadeImpl implements AfterSaleApplyV2Facade {

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
     * @return
     */
    @Override
    public RequestResult factoryAftersaleApplyInfo(String aftersaleApplyId) {
        MapContext result = MapContext.newOne();
        //售后信息
        AftersaleApply aftersaleApply = this.aftersaleApplyService.findOneById(aftersaleApplyId);
        if (aftersaleApply == null) {
            return ResultFactory.generateResNotFoundResult();
        }
        String orderId = aftersaleApply.getCustomOrderId();
        Integer type=2;
        MapContext params=MapContext.newOne();
        params.put("type",type);
        params.put("orderId",orderId);
        Map customOrder = this.customOrderService.findFAppBaseInfoByOrderId(params);
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
        List<CompanyCustomer> companyCustomers = this.companyCustomerService.findCustomerListByCid(dealerId);
        if (companyCustomers.size() < 0 || companyCustomers == null) {
            return ResultFactory.generateSuccessResult();
        }
        List list = new ArrayList();
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
            if(!mapContext.getTypedValue("order",String.class).equals("")) {
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
                }
            }
        }else{
            Map<String,String> created = new HashMap<String, String>();
            created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
            sorts.add(created);
        }
        paginatedFilter.setSorts(sorts);
        PaginatedList<MapContext> aftersalesList = this.aftersaleApplyService.findAftersaleApplyList(paginatedFilter);
        return ResultFactory.generateRequestResult(aftersalesList);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addFactoryAftersale(AftersaleApply aftersaleApply) {
        aftersaleApply.setCreated(DateUtil.getSystemDate());
        aftersaleApply.setCreator(WebUtils.getCurrUserId());
        aftersaleApply.setNo(aftersaleApplyService.getAftersaleOrderNo(aftersaleApply.getCustomOrderId(),aftersaleApply.getNo()));
        //aftersaleApply.setCreator("4guvykqz2ebk");
        aftersaleApply.setStatus(0);
        aftersaleApply.setCharge(false);
        aftersaleApply.setChargeAmount(new BigDecimal(0));
        this.aftersaleApplyService.add(aftersaleApply);
        //更新图片信息为正常
        if(aftersaleApply.getFileIds()!=null && !aftersaleApply.getFileIds().equals("")){
            String[] ids = aftersaleApply.getFileIds().split(",");
            for(String file_id: ids){
                MapContext map = MapContext.newOne();
                map.put("id",file_id);
                map.put("aftersale_apply_id",aftersaleApply.getId());
                map.put("status",1);
                this.aftersaleApplyFilesService.updateByMapContext(map);
            }
        }
        MapContext mapParam = MapContext.newOne();
        mapParam.put("id",aftersaleApply.getId());
        return ResultFactory.generateRequestResult(findAftersaleApplyList(1,1,mapParam).getData());
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult handleFactoryAftersale(MapContext mapContext) {
        String resultOrderId = null;
        //售后单类型
        Integer aftersaleType = mapContext.getTypedValue("aftersaleType", Integer.class);
        //售后订单编号
        String aftersaleOrderNo = mapContext.getTypedValue("aftersaleOrderNo", String.class);
        String creator =WebUtils.getCurrUserId();
       //  creator ="4gv4m5yrj18g";
        String companyId = mapContext.getTypedValue("dealerId", String.class);
        if (aftersaleType == AftersaleType.BULIAO.getValue()) {
            //原订单ID
            String orderId = mapContext.getTypedValue("orderId", String.class);
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
            customOrder.setCreator(creator);
            customOrder.setStatus(OrderStatus.TO_AUDIT.getValue());
            customOrder.setType(1);
            customOrder.setCustomerTel(user.getMobile());
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
            customOrder.setCreator(creator);
            customOrder.setCreated(DateUtil.getSystemDate());
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
            orderProduct.setCreator(creator);
            this.orderProductService.add(orderProduct);
        }
        //更新售后单
        mapContext.put("resultOrderId",resultOrderId);
        mapContext.put("status","4");
        return ResultFactory.generateRequestResult(this.aftersaleApplyService.updateByMapContext(mapContext));
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addFiles(List<MultipartFile> multipartFiles) {
        String uid = WebUtils.getCurrUserId();
        //uid = "123456789";
        List<Map<String, Object>> list = new ArrayList<>();
        AftersaleApplyFiles aftersaleApplyFiles = new AftersaleApplyFiles();
        UploadInfo uploadInfo;
        for (MultipartFile mul : multipartFiles) {
            uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, mul, UploadResourceType.AFTERSALE_APPLY,"factory");
            //aftersaleApplyFiles.setAftersaleApplyId(aftersaleId);
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
            mapContext.put("imagePath", aftersaleApplyFiles.getFullPath());
            mapContext.put("fileId", aftersaleApplyFiles.getId());
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

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updateAftersaleStatus(String aftersaleId, String status) {
        MapContext map = MapContext.newOne();
        map.put("id",aftersaleId);
        map.put("status",status);
        aftersaleApplyService.updateByMapContext(map);
        return  ResultFactory.generateRequestResult(map);
    }
}
