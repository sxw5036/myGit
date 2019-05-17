package com.lwxf.industry4.webapp.facade.app.dealer.specimen;

import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/18 15:00
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface SpecimenFacade extends BaseFacade {
    RequestResult addSpecimen(MapContext params, String companyId, HttpServletRequest request);

    RequestResult findSpecimenOrderList(Pagination pagination, MapContext filters);

    RequestResult addPayment(Payment payment, String userId, String companyId);
}
