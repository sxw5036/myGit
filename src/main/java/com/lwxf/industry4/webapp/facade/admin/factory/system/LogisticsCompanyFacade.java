package com.lwxf.industry4.webapp.facade.admin.factory.system;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/26/026 16:02
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface LogisticsCompanyFacade extends BaseFacade {
	RequestResult findList(String orderId);
}
