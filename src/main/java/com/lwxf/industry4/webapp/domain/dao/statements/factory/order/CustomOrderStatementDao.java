package com.lwxf.industry4.webapp.domain.dao.statements.factory.order;


import com.lwxf.industry4.webapp.domain.dao.base.BaseNoIdDao;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByMonthDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByQuarterDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByWeekDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByYearDto;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.mybatis.utils.MapContext;

@IBatisSqlTarget
public interface CustomOrderStatementDao extends BaseNoIdDao {

     Integer selectByfilter(MapContext map);

     CountByWeekDto countOrderByWeek(MapContext map);

     CountByMonthDto countOrderByMonth(MapContext map);

     CountByQuarterDto countOrderByQuarter(MapContext map);

     CountByYearDto countOrderByYear(MapContext map);

}
