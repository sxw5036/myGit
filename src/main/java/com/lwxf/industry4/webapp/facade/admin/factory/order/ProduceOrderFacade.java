package com.lwxf.industry4.webapp.facade.admin.factory.order;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/4/8/008 15:56
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface ProduceOrderFacade extends BaseFacade {
	RequestResult findCoordinationCount(String branchId);
	RequestResult findProduceOrderOverview();

	RequestResult findCoordinationPrintInfo(String id);
}
