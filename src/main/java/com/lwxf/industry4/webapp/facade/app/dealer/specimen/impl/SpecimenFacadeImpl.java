package com.lwxf.industry4.webapp.facade.app.dealer.specimen.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderDemandService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.enums.order.OrderType;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.domain.dto.user.UserAreaDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderDemand;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.specimen.SpecimenFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/18 15:01
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("specimenFacade")
public class SpecimenFacadeImpl extends BaseFacadeImpl implements SpecimenFacade {

    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "customOrderService")
    private CustomOrderService customOrderService;
    @Resource(name = "customOrderDemandService")
    private CustomOrderDemandService customOrderDemandService;
    @Resource(name = "paymentService")
    private PaymentService paymentService;


    @Override
    public RequestResult findSpecimenOrderList(Pagination pagination,MapContext filters) {
        PaginatedFilter paginatedFilter = new PaginatedFilter();
        paginatedFilter.setFilters(filters);
        paginatedFilter.setPagination(pagination);
        PaginatedList<Map> specimenOrderList = this.customOrderService.findSpecimenOrderList(paginatedFilter);
        List<Map> rows = specimenOrderList.getRows();
        for (Map map:rows){
            Integer status = ((Integer)map.get("orderStatus")).intValue();
            if (status==0){
                map.put("orderStatus", "待处理");
            }
            if (1<=status&&status<=6){
                map.put("orderStatus", "设计中");
            }
            if (7<=status&&status<=13){
                map.put("orderStatus", "生产中");
            }
            if (14<=status&&status<=15){
                map.put("orderStatus", "配送中");
            }
            if (16==status){
                map.put("orderStatus", "已完成");
            }
        }
        return ResultFactory.generateRequestResult(rows);
    }

    /**
     * 添加上样
     * @param params
     * @param companyId
     * @param request
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addSpecimen(MapContext params, String companyId, HttpServletRequest request) {
        String no = params.getTypedValue("no", String.class);
        Date created = params.getTypedValue("created", Date.class);
        String name = params.getTypedValue("name", String.class);
        String content = params.getTypedValue("content", String.class);
        String designScheme = params.getTypedValue("designScheme", String.class);
        String productSeries = params.getTypedValue("productSeries", String.class);
        String productModel = params.getTypedValue("productModel", String.class);


        String cid = request.getHeader("X-CID");
        String uid = request.getHeader("X-UID");
        UserAreaDto userAreaDto = this.userService.findByUid(uid);
        CustomOrder order = new CustomOrder();
        order.setNo(no);
        order.setCustomerId(uid);
        order.setCustomerTel(userAreaDto.getMobile());
        order.setCompanyId(cid);
        order.setCityAreaId(userAreaDto.getCityAreaId());
        order.setAddress(userAreaDto.getAddress());
        order.setStatus(OrderStatus.NEW_ORDER.getValue());
        order.setCreator(uid);
        order.setCreated(created);
        order.setEarnest(0);
        order.setImprest(new BigDecimal(0));
        order.setRetainage(new BigDecimal(0));
        order.setAmount(new BigDecimal(0));
        order.setType(OrderType.EXHIBITIONORDER.getValue());
        order.setDesignFee(0);
        order.setFactoryFinalPrice(new BigDecimal(0));
        order.setMarketPrice(new BigDecimal(0));
        order.setDiscounts(new BigDecimal(0));
        order.setFactoryDiscounts(new BigDecimal(0));
        order.setFactoryPrice(new BigDecimal(0));
        order.setConfirmFprice(false);
        order.setConfirmCprice(false);

        RequestResult result = order.validateFields();
        if (result!=null){
            return result;
        }
        this.customOrderService.add(order);
        CustomOrderDemand customOrderDemand = new CustomOrderDemand();
        customOrderDemand.setContent(content);
        customOrderDemand.setName(name);
        customOrderDemand.setDesignScheme(designScheme);
        customOrderDemand.setProductModel(productModel);
        customOrderDemand.setProductSeries(productSeries);
        customOrderDemand.setCustomOrderId(order.getId());
        customOrderDemand.setCreated(DateUtil.getSystemDate());
        customOrderDemand.setCreator(uid);
        customOrderDemand.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.ORDER_DEMAND_NO));
        RequestResult result1 = customOrderDemand.validateFields();
        if (result1!=null){
            return result1;
        }
        this.customOrderDemandService.add(customOrderDemand);
        MapContext mapContext = MapContext.newOne();
        mapContext.put("orderId", order.getId());
        mapContext.put("demandId", customOrderDemand.getId());
        return ResultFactory.generateRequestResult(mapContext);
    }


    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addPayment(Payment payment, String userId, String companyId) {

        payment.setCreated(DateUtil.getSystemDate());
        payment.setStatus(0);
        payment.setCreator(userId);
        payment.setCompanyId(companyId);
        payment.setPayee("4j1u3r1efshq");
        payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
        RequestResult result = payment.validateFields();
        if (null!=result){
            return  result;
        }
        this.paymentService.add(payment);
        MapContext map = MapContext.newOne();
        map.put("id",payment.getId());
        return ResultFactory.generateRequestResult(map);
    }
}
