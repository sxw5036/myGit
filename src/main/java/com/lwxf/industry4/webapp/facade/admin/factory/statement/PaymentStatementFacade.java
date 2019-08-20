package com.lwxf.industry4.webapp.facade.admin.factory.statement;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

import java.util.Date;

public interface PaymentStatementFacade extends BaseFacade {

    RequestResult selectByfilter(Date startTime, Date endTime, MapContext map,Integer dateType);

    RequestResult  countPaymentByWeek(MapContext map);

    RequestResult  countPaymentByMonth(MapContext map);

    RequestResult  countPaymentByQuarter(MapContext map);

    RequestResult  countPaymentByYear(MapContext map);

}
