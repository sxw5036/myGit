package com.lwxf.industry4.webapp.domain.dao.statements.factory.order.impl;

import com.lwxf.industry4.webapp.domain.dao.base.BaseNoIdDaoImpl;
import com.lwxf.industry4.webapp.domain.dao.statements.factory.order.CustomOrderStatementDao;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByMonthDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByQuarterDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByWeekDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByYearDto;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Repository;


@Repository("orderStatementDao")
public class OrderStatementDaoImpl extends BaseNoIdDaoImpl implements CustomOrderStatementDao {

    @Override
    public CountByWeekDto countOrderByWeek(MapContext map) {
        String sqlId = this.getNamedSqlId("countOrderByWeek");
        return this.getSqlSession().selectOne(sqlId,map);
    }

    @Override
    public CountByMonthDto countOrderByMonth(MapContext map) {
        String sqlId = this.getNamedSqlId("countOrderByMonth");
        return this.getSqlSession().selectOne(sqlId,map);
    }

    @Override
    public CountByQuarterDto countOrderByQuarter(MapContext map) {
        String sqlId = this.getNamedSqlId("countOrderByQuarter");
        return this.getSqlSession().selectOne(sqlId,map);
    }

    @Override
    public CountByYearDto countOrderByYear(MapContext map) {
        String sqlId = this.getNamedSqlId("countOrderByYear");
        return this.getSqlSession().selectOne(sqlId,map);
    }

    @Override
    public Integer selectByfilter(MapContext map) {
        String sqlId = this.getNamedSqlId("selectByfilter");
        return this.getSqlSession().selectOne(sqlId,map);
    }

}
