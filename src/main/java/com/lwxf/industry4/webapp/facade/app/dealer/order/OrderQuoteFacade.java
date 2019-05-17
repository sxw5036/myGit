package com.lwxf.industry4.webapp.facade.app.dealer.order;

import javax.servlet.http.HttpServletRequest;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/28 0028 14:42
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface OrderQuoteFacade extends BaseFacade {





	RequestResult updateOrderQuoteMessage(String customOrderId, MapContext mapContext);


	RequestResult findOrderQuoteMessage(String companyId, String confirmFprice, String condition, Integer pageNum, Integer pageSize, HttpServletRequest request);
}
