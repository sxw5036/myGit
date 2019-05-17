package com.lwxf.industry4.webapp.facade.app.dealer.order;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/15 16:24
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface OrderDesignFacade extends BaseFacade {

    RequestResult findByOrderId(String orderId);
    RequestResult findBydesignId(String orderId, String designId);
    RequestResult addAmendmentsById(String orderId, MapContext params);
    RequestResult putStatusById(String orderId, MapContext params);
}
