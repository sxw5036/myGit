package com.lwxf.industry4.webapp.facade.admin.factory.finance;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2019/1/9/009 10:02
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FinanceFacade extends BaseFacade {
	RequestResult findOrderFinanceList(MapContext mapContext,Integer pageNum,Integer pageSize);

	RequestResult findPurchaseList(MapContext mapContext, Integer pageNum, Integer pageSize);

	RequestResult findDealerPayList(MapContext mapContext, Integer pageNum, Integer pageSize);

	RequestResult updateFinancePayStatus(String id,Integer type);

	RequestResult findSupplierList(MapContext mapContext, Integer pageNum, Integer pageSize);

	RequestResult updateSupplier(String id, Integer status);

	RequestResult findAftersaleList(MapContext mapContext, Integer pageNum, Integer pageSize);

	RequestResult findDealerList(MapContext mapContext, Integer pageNum, Integer pageSize);

	RequestResult updateDealerPay(String id);

	RequestResult updateCoordinationPay(String id);
}
