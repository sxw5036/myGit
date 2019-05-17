package com.lwxf.industry4.webapp.facade.app.factory.factorystatement.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockItemService;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentType;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.DateUtilsExt;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factorystatement.IndexStatementFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/5/16 14:59
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "indexStatementFacade")
public class IndexStatementFacadeImpl extends BaseFacadeImpl implements IndexStatementFacade {

    @Resource(name = "customOrderService")
    private CustomOrderService customOrderService;
    @Resource(name = "paymentService")
    private PaymentService paymentService;
    @Resource(name = "companyService")
    private CompanyService companyService;
    @Resource(name = "dispatchBillService")
    private DispatchBillService dispatchBillService;
    @Resource(name = "finishedStockItemService")
    private FinishedStockItemService finishedStockItemService;

    @Override
    public RequestResult statementHomePage(Integer type, String bTime, String eTime) {
        Date systemDate = DateUtil.getSystemDate();
        Date yesterdayEnd = DateUtil.getYesterdayEnd();
        String beginTime = null;
        String endTime = null;
        String day = null;
        switch (type) {
            case 1://今天
                day = DateUtil.dateTimeToString(systemDate, DateUtil.FORMAT_DATE);
                break;
            case 2://昨天
                day = DateUtil.dateTimeToString(yesterdayEnd, DateUtil.FORMAT_DATE);
                break;
            case 3://近7天
                beginTime = DateUtilsExt.getDayDate(7);
                endTime = DateUtil.dateTimeToString(systemDate, DateUtil.FORMAT_DATE);
                break;
            case 4://近30天
                beginTime = DateUtilsExt.getDayDate(30);
                endTime = DateUtil.dateTimeToString(systemDate, DateUtil.FORMAT_DATE);
                break;
            case 5://自定义
                if (bTime==null&&eTime==null){
                    return ResultFactory.generateErrorResult(ErrorCodes.BIZ_REQUEST_PARAM_ERROR_10000, AppBeanInjector.i18nUtil.getMessage("BIZ_REQUEST_PARAM_ERROR_10000"));
                }
                beginTime = bTime;
                endTime = eTime;
                break;
            default:
                return ResultFactory.generateErrorResult(ErrorCodes.BIZ_REQUEST_PARAM_ERROR_10000, AppBeanInjector.i18nUtil.getMessage("BIZ_REQUEST_PARAM_ERROR_10000"));
        }

        List<MapContext> result = new ArrayList<>();
        //总订单
        Integer allOrderNum = this.customOrderService.getAllByCreated(beginTime, endTime, day);
        MapContext allOrder = MapContext.newOne();
        allOrder.put("name", "总订单");
        allOrder.put("countNumber", allOrderNum.toString());
        result.add(allOrder);
        //有效订单
        Integer paidOrderNum = this.customOrderService.findPaidOrderNumByTime(beginTime, endTime, day);
        MapContext paidOrder = MapContext.newOne();
        paidOrder.put("name", "有效订单");
        paidOrder.put("countNumber", paidOrderNum.toString());
        result.add(paidOrder);
        //无效订单
        Integer unPaidOrderNum = this.customOrderService.findUnpaidOrderNumByTime(beginTime, endTime, day);
        MapContext unPaidOrder = MapContext.newOne();
        unPaidOrder.put("name", "无效订单");
        unPaidOrder.put("countNumber", unPaidOrderNum.toString());
        result.add(unPaidOrder);
        //订单金额收款
        BigDecimal paidOrderAmount = this.customOrderService.findPaidOrderAmountByTime(beginTime, endTime, day);
        if (paidOrderAmount == null) {
            paidOrderAmount = BigDecimal.ZERO;
        }
        MapContext paidOrderAmounts = MapContext.newOne();
        paidOrderAmounts.put("name", "订单金额收款");
        paidOrderAmounts.put("countNumber", paidOrderAmount.toString());
        result.add(paidOrderAmounts);
        //收入
        Integer B_TO_F = PaymentType.B_TO_F.getValue();
        BigDecimal incomeAmount = this.paymentService.findByTypeAndCreated(B_TO_F, beginTime, endTime, day);
        if (incomeAmount == null) {
            incomeAmount = BigDecimal.ZERO;
        }
        MapContext income = MapContext.newOne();
        income.put("name", "收入");
        income.put("countNumber", incomeAmount.toString());
        result.add(income);
        //支出
        Integer F_TO_B = PaymentType.F_TO_B.getValue();
        BigDecimal expenditureAmount = this.paymentService.findByTypeAndCreated(F_TO_B, beginTime, endTime, day);
        if (expenditureAmount == null) {
            expenditureAmount = BigDecimal.ZERO;
        }
        MapContext expenditure = MapContext.newOne();
        expenditure.put("name", "支出");
        expenditure.put("countNumber", expenditureAmount.toString());
        result.add(expenditure);
        //下单经销商
        Integer companyNum = this.companyService.findCompanyNumByOrderCreated(beginTime, endTime, day);
        MapContext company = MapContext.newOne();
        company.put("name", "下单经销商");
        company.put("countNumber", companyNum.toString());
        result.add(company);

        List<CustomOrder> allOrderInfo = this.customOrderService.getAllOrderByCreated(beginTime, endTime, day);

        Integer nearNum = 0; //临期
        Integer exceedNum = 0;//超期
        for (CustomOrder order : allOrderInfo) {
            //预计交货日期
            Date deliveryDate = order.getEstimatedDeliveryDate();
            if (deliveryDate != null) {
                long nh = 1000 * 60 * 60;//一小时的毫秒数
                //系统时间
                long systemDateTime = systemDate.getTime();
                long deliveryDateTime = deliveryDate.getTime();
                long diff = deliveryDateTime - systemDateTime;
                long allHour = diff / nh;
                //超期
                if (systemDateTime > deliveryDateTime) {
                    exceedNum++;
                }
                //临期
                if (0 <= allHour && allHour <= 72) {
                    nearNum++;
                }
            }
        }
        //临期订单
        MapContext nearTime = MapContext.newOne();
        nearTime.put("name", "临期订单");
        nearTime.put("countNumber", nearNum.toString());
        result.add(nearTime);
        //过期单
        MapContext exceedTime = MapContext.newOne();
        exceedTime.put("name", "过期单");
        exceedTime.put("countNumber", exceedNum.toString());
        result.add(exceedTime);
        //入库包裹数
        Integer stockNum = this.finishedStockItemService.findNumByCreated(beginTime, endTime, day);
        MapContext stockItem = MapContext.newOne();
        stockItem.put("name", "入库包裹数");
        stockItem.put("countNumber", stockNum.toString());
        result.add(stockItem);
        //发货单数
        Integer dispatchNum = this.dispatchBillService.findNumByCreated(beginTime, endTime, day);
        MapContext dispatchBill = MapContext.newOne();
        dispatchBill.put("name", "发货单数");
        dispatchBill.put("countNumber", dispatchNum.toString());
        result.add(dispatchBill);
        return ResultFactory.generateRequestResult(result);
    }
}
