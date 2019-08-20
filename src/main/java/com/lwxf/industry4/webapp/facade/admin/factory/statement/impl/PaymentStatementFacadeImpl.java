package com.lwxf.industry4.webapp.facade.admin.factory.statement.impl;

import com.lwxf.industry4.webapp.bizservice.statements.app.factory.paymentStatement.PaymentStatementService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.DateUtilsExt;
import com.lwxf.industry4.webapp.domain.dto.statement.*;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.statement.PaymentStatementFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component("paymentStatementFacade")
public class PaymentStatementFacadeImpl extends BaseFacadeImpl implements PaymentStatementFacade {

    @Resource(name = "paymentStatementService")
    private PaymentStatementService paymentStatementService;

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult selectByfilter(Date StartDate,Date endDate,MapContext map,Integer dateType) {
        MapContext mapRes = MapContext.newOne();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MapContext value = MapContext.newOne();
        List<MapContext> listRes = new ArrayList<>();
        if(dateType==null){
            dateType=0;
        }
        if(dateType==0){ //按日统计
            if(StartDate!=null && endDate!=null) {
                List<Date> list = DateUtilsExt.findDates(StartDate, endDate);
                if (list != null && list.size() > 0) {
                    SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd");
                    for (Date date : list) {
                        map.put("created", sdf.format(date));
                        MapContext mapValue = MapContext.newOne();
                        mapValue.put("date",sdf1.format(date));
                        mapValue.put("value",paymentStatementService.selectByfilter(map));
                        listRes.add(mapValue);
                    }
                }
            }
        }else if(dateType==1){ //按月统计
            SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat sdfParam = new SimpleDateFormat("yyyy-MM-dd");
            if(StartDate!=null && endDate!=null){
                List<Date> list = null;
                try {
                    list = DateUtilsExt.getMonthBetween(StartDate,endDate);
                    if(list!=null&&list.size()>0){
                        for(Date date: list){
                            map.put("beginDate",sdfParam.format(DateUtilsExt.getFirstDayOfMonth(date)));
                            map.put("endDate",sdfParam.format(DateUtilsExt.getLastDayOfMonth(date)));
                            MapContext mapValue = MapContext.newOne();
                            mapValue.put("date",sdfMonth.format(date));
                            mapValue.put("value",paymentStatementService.selectByfilter(map));
                            listRes.add(mapValue);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }else if(dateType==2){ //按年统计
            SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy");
            SimpleDateFormat sdfParam = new SimpleDateFormat("yyyy-MM-dd");
            if(StartDate!=null && endDate!=null){
                List<Date> list = null;
                try {
                    list = DateUtilsExt.getYearBetween(StartDate,endDate);
                    if(list!=null&&list.size()>0){
                        for(Date date: list){
                            map.put("beginDate",sdfParam.format(DateUtilsExt.getFirstDayOfYear(date)));
                            map.put("endDate",sdfParam.format(DateUtilsExt.getLastDayOfYear(date)));
                            MapContext mapValue = MapContext.newOne();
                            mapValue.put("date",sdfMonth.format(date));
                            mapValue.put("value",paymentStatementService.selectByfilter(map));
                            listRes.add(mapValue);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        mapRes.put("chart1",value);
        if(!map.get("countType").toString().equals("4"))
        {
            List<MapContext> chart2List = new ArrayList<>();
            List<PaymentCountByFunds> list2 = paymentStatementService.selectFundsStatementByFilter(map);
            for(PaymentCountByFunds obj : list2){
                MapContext m = MapContext.newOne();
                m.put("name",obj.getFundsName());
                m.put("value",obj.getAmount());
                chart2List.add(m);
            }
            mapRes.put("chart2",chart2List);
        }
        return ResultFactory.generateRequestResult(mapRes);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult countPaymentByWeek(MapContext map) {
        MapContext mapRes = MapContext.newOne();
        CountByWeekDto dto = paymentStatementService.countPaymentByWeek(map);
        MapContext mapChart1 = MapContext.newOne();
        try {
            if (dto != null) {
                Class cls = dto.getClass();
                Field[] fields = cls.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    f.setAccessible(true);
                    mapChart1.put(f.getName(), f.get(dto));
                }
            }
        }catch(IllegalAccessException ex) {
                ex.printStackTrace();
        }
        mapRes.put("chart1",mapChart1);
        if(!map.get("countType").toString().equals("4"))
        {
            List<MapContext> chart2List = new ArrayList<>();
            List<PaymentCountByFunds> list = paymentStatementService.countFundsStatementByWeek(map);
            for(PaymentCountByFunds obj : list){
                MapContext m = MapContext.newOne();
                m.put("name",obj.getFundsName());
                m.put("value",obj.getAmount());
                chart2List.add(m);
            }
            mapRes.put("chart2",chart2List);
        }
        return ResultFactory.generateRequestResult(mapRes);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult countPaymentByMonth(MapContext map) {
        MapContext mapRes = MapContext.newOne();
        CountByMonthDto dto = paymentStatementService.countPaymentByMonth(map);
        MapContext mapChart1 = MapContext.newOne();
        try {
            if (dto != null) {
                Class cls = dto.getClass();
                Field[] fields = cls.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    f.setAccessible(true);
                    mapChart1.put(f.getName(), f.get(dto));
                }
            }
        }catch(IllegalAccessException ex) {
            ex.printStackTrace();
        }
        mapRes.put("chart1",mapChart1);
        if(!map.get("countType").toString().equals("4"))
        {
            List<MapContext> chart2List = new ArrayList<>();
            List<PaymentCountByFunds> list = paymentStatementService.countFundsStatementByMonth(map);
            for(PaymentCountByFunds obj : list){
                MapContext m = MapContext.newOne();
                m.put("name",obj.getFundsName());
                m.put("value",obj.getAmount());
                chart2List.add(m);
            }
            mapRes.put("chart2",chart2List);
        }
        return ResultFactory.generateRequestResult(mapRes);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult countPaymentByQuarter(MapContext map) {
        MapContext mapRes = MapContext.newOne();
        CountByQuarterDto dto = paymentStatementService.countPaymentByQuarter(map);
        MapContext mapChart1 = MapContext.newOne();
        try {
            if (dto != null) {
                Class cls = dto.getClass();
                Field[] fields = cls.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    f.setAccessible(true);
                    mapChart1.put(f.getName(), f.get(dto));
                }
            }
        }catch(IllegalAccessException ex) {
            ex.printStackTrace();
        }
        mapRes.put("chart1",mapChart1);
        if(!map.get("countType").toString().equals("4"))
        {
            List<MapContext> chart2List = new ArrayList<>();
            List<PaymentCountByFunds> list = paymentStatementService.countFundsStatementByQuarter(map);
            for(PaymentCountByFunds obj : list){
                MapContext m = MapContext.newOne();
                m.put("name",obj.getFundsName());
                m.put("value",obj.getAmount());
                chart2List.add(m);
            }
            mapRes.put("chart2",chart2List);
        }
        return ResultFactory.generateRequestResult(mapRes);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult countPaymentByYear(MapContext map) {
        MapContext mapRes = MapContext.newOne();
        CountByYearDto dto = paymentStatementService.countPaymentByYear(map);
        MapContext mapChart1 = MapContext.newOne();
        try {
            if (dto != null) {
                Class cls = dto.getClass();
                Field[] fields = cls.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    f.setAccessible(true);
                    mapChart1.put(f.getName(), f.get(dto));
                }
            }
        }catch(IllegalAccessException ex) {
            ex.printStackTrace();
        }
        mapRes.put("chart1",mapChart1);
        if(!map.get("countType").toString().equals("4"))
        {
            List<MapContext> chart2List = new ArrayList<>();
            List<PaymentCountByFunds> list = paymentStatementService.countFundsStatementByYear(map);
            for(PaymentCountByFunds obj : list){
                MapContext m = MapContext.newOne();
                m.put("name",obj.getFundsName());
                m.put("value",obj.getAmount());
                chart2List.add(m);
            }
            mapRes.put("chart2",chart2List);
        }
        return ResultFactory.generateRequestResult(mapRes);
    }
}
