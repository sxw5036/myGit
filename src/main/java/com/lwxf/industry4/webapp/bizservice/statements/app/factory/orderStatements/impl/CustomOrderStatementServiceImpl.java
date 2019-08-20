package com.lwxf.industry4.webapp.bizservice.statements.app.factory.orderStatements.impl;

import com.lwxf.industry4.webapp.bizservice.base.BaseNoIdServiceImpl;
import com.lwxf.industry4.webapp.bizservice.statements.app.factory.orderStatements.CustomOrderStatementService;
import com.lwxf.industry4.webapp.domain.dao.base.BaseNoIdDao;
import com.lwxf.industry4.webapp.domain.dao.statements.factory.order.CustomOrderStatementDao;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByMonthDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByQuarterDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByWeekDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByYearDto;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("customOrderStatementService")
public class CustomOrderStatementServiceImpl extends BaseNoIdServiceImpl implements CustomOrderStatementService {

    @Resource
    private CustomOrderStatementDao customOrderStatementDao;

    @Override
    public CountByWeekDto countOrderByWeek(MapContext map) {
        return customOrderStatementDao.countOrderByWeek(map);
    }

    @Override
    public CountByMonthDto countOrderByMonth(MapContext map) {
        return customOrderStatementDao.countOrderByMonth(map);
    }

    @Override
    public CountByQuarterDto countOrderByQuarter(MapContext map) {
        return customOrderStatementDao.countOrderByQuarter(map);
    }

    @Override
    public CountByYearDto countOrderByYear(MapContext map) {
        return customOrderStatementDao.countOrderByYear(map);
    }

    @Override
    public void setDao(BaseNoIdDao dao) {
        this.dao = customOrderStatementDao;
    }

    @Override
    public int add(Object entity) {
        return 0;
    }

    @Override
    public Integer selectByfilter(MapContext map) {
        return customOrderStatementDao.selectByfilter(map);
    }
}
