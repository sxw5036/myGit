package com.lwxf.industry4.webapp.bizservice.statements.app.factory.orderStatements;

import com.lwxf.industry4.webapp.bizservice.base.BaseNoIdService;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByMonthDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByQuarterDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByWeekDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByYearDto;
import com.lwxf.mybatis.utils.MapContext;

public interface CustomOrderStatementService  extends BaseNoIdService {

      Integer selectByfilter(MapContext map);

      CountByWeekDto countOrderByWeek(MapContext map);

      CountByMonthDto countOrderByMonth(MapContext map);

      CountByQuarterDto countOrderByQuarter(MapContext map);

      CountByYearDto countOrderByYear(MapContext map);
}
