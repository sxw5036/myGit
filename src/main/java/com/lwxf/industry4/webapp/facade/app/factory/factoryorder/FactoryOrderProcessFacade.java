package com.lwxf.industry4.webapp.facade.app.factory.factoryorder;

import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.entity.customorder.ProduceFlow;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/1 13:13
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FactoryOrderProcessFacade extends BaseFacade {

    RequestResult findProcessFollow(MapContext params, Pagination pagination);

    RequestResult findProcessFollowList(String deliveryDate, String created, MapContext params, Pagination pagination);


    RequestResult findProcessExecuteInfo(String orderId);

    RequestResult addProcessExecute(ProduceFlow produceFlow,String orderId);

    RequestResult findProcessFollowInfo(String orderId);

    RequestResult goWorkshop(MapContext mapContext);

    RequestResult addProcessExecutePack(ProduceFlow produceFlow, String orderId,String uid,String[] barcodesList);

    Map findOrderBaseInfo(String orderId, Integer type);
}
