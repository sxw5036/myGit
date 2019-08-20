package com.lwxf.industry4.webapp.facade.wxapi.dealer.order;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/6/19 0019 17:35
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface BWxCustomOrderFacade extends BaseFacade {
	RequestResult findWxOrderList(String dealerId, Integer pageNum, Integer pageSize, MapContext mapContext);

	RequestResult findWxOrderInfo(String branchId, String orderId);

	RequestResult findMessageOrderInfo(Integer pageNum, Integer pageSize, String companyId);
}
