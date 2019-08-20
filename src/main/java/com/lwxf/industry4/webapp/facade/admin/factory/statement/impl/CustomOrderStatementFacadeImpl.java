package com.lwxf.industry4.webapp.facade.admin.factory.statement.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.statements.app.factory.orderStatements.CustomOrderStatementService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.DateUtilsExt;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByMonthDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByQuarterDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByWeekDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByYearDto;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.statement.CustomOrderStatementFacade;
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

@Component("customOrderStatementFacade")
public class CustomOrderStatementFacadeImpl extends BaseFacadeImpl implements CustomOrderStatementFacade {

    @Resource(name = "customOrderStatementService")
    private CustomOrderStatementService customOrderStatementService;

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult selectByfilter(Date StartDate,Date endDate,MapContext map,Integer dateType) {
        MapContext mapRes = MapContext.newOne();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<MapContext> listRes = new ArrayList<>();
        if(dateType==null){
            dateType=0;
        }
        if(dateType==0){ //按日统计
            if(StartDate!=null && endDate!=null) {
                SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd");
                List<Date> list = DateUtilsExt.findDates(StartDate, endDate);
                if (list != null && list.size() > 0) {
                    for (Date date : list) {
                        map.put("created",sdf.format(date));
                        MapContext mapValue = MapContext.newOne();
                        mapValue.put("date",sdf1.format(date));
                        mapValue.put("value",customOrderStatementService.selectByfilter(map));
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
                            mapValue.put("value",customOrderStatementService.selectByfilter(map));
                            listRes.add(mapValue);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }else if(dateType==2){ //按年统计
            SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
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
                            mapValue.put("date",sdfYear.format(date));
                            mapValue.put("value",customOrderStatementService.selectByfilter(map));
                            listRes.add(mapValue);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        mapRes.put("chart1",listRes);
        return ResultFactory.generateRequestResult(mapRes);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult countOrderByWeek(MapContext map) {
        MapContext mapRes = MapContext.newOne();
        CountByWeekDto dto = customOrderStatementService.countOrderByWeek(map);
        List<MapContext> listRes = new ArrayList<MapContext>();
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
        return ResultFactory.generateRequestResult(mapRes);
    }

    @Override
    public RequestResult countOrderByMonth(MapContext map) {
        MapContext mapRes = MapContext.newOne();
        CountByMonthDto dto = customOrderStatementService.countOrderByMonth(map);
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
        return ResultFactory.generateRequestResult(mapRes);
    }

    @Override
    public RequestResult countOrderByQuarter(MapContext map) {
        MapContext mapRes = MapContext.newOne();
        CountByQuarterDto dto = customOrderStatementService.countOrderByQuarter(map);
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
        return ResultFactory.generateRequestResult(mapRes);
    }

    @Override
    public RequestResult countOrderByYear(MapContext map) {
        MapContext mapRes = MapContext.newOne();
        CountByYearDto dto = customOrderStatementService.countOrderByYear(map);
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
        return ResultFactory.generateRequestResult(mapRes);
    }
}
