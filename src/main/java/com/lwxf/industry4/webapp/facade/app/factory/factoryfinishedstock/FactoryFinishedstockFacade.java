package com.lwxf.industry4.webapp.facade.app.factory.factoryfinishedstock;

import javax.servlet.http.HttpServletRequest;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/18 0018 10:37
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FactoryFinishedstockFacade extends BaseFacade {
	RequestResult findFactoryFinishedstockList(Integer pageNum, Integer pageSize, MapContext mapContext,String isIn);

	RequestResult findFactoryFinishedStockItemInfo(String finishedStockItemId, String orderId);


	RequestResult addFinishedStock(String orderId, String finishedStockItemId);

	RequestResult updateFinishedStockItem(String finishedStockItemId, MapContext mapContext, HttpServletRequest request);

	RequestResult addFinishedStockItem(MapContext mapContext, HttpServletRequest request);
}
