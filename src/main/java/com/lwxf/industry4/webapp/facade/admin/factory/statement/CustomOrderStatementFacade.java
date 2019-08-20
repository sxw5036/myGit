package com.lwxf.industry4.webapp.facade.admin.factory.statement;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

import java.util.Date;


public interface CustomOrderStatementFacade extends BaseFacade {

    RequestResult  selectByfilter(Date startTime, Date endTime, MapContext map,Integer dateType);

    RequestResult  countOrderByWeek(MapContext map);

    RequestResult  countOrderByMonth(MapContext map);

    RequestResult  countOrderByQuarter(MapContext map);

    RequestResult  countOrderByYear(MapContext map);
}
