package com.lwxf.industry4.webapp.facade.app.dealer.order;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/17 14:26
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface OrderTrackingFacade extends BaseFacade {

    RequestResult findProcessByOrderId(String orderId);

    RequestResult findProcessVideosByOrderId(String orderId,String processId);

    RequestResult findDispatchsByOrderId(String orderId);

    RequestResult findByDispatchId(String dispatchId);

    RequestResult putStatusByDispatchId(String dispatchId,String uid,String cid,String orderId);
}
