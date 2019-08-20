package com.lwxf.industry4.webapp.facade.wxapi.factory.statements.impl;

import com.lwxf.industry4.webapp.bizservice.statements.app.factory.orderStatements.CustomOrderStatementService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.DateUtilsExt;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByMonthDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByQuarterDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByWeekDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByYearDto;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.wxapi.factory.statements.CustomOrderStatementFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component("wxCustomOrderStatementFacade")
public class CustomOrderStatementFacadeImpl extends BaseFacadeImpl implements CustomOrderStatementFacade {

    @Resource(name = "customOrderStatementService")
    private CustomOrderStatementService customOrderStatementService;

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult selectByfilter(Date StartDate,Date endDate,MapContext map) {
        MapContext mapRes = MapContext.newOne();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        MapContext value = MapContext.newOne();
        if(StartDate!=null && endDate!=null){
            List<Date> list = DateUtilsExt.findDates(StartDate,endDate);
            if(list!=null&&list.size()>0){
                for(Date date: list){
                    map.put("created",date);
                    value.put(sdf.format(date),customOrderStatementService.selectByfilter(map));
                }
            }
        }
        mapRes.put("chart1",value);
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
