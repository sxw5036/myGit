package com.lwxf.industry4.webapp.facade.app.factory.factoryCustomer;

import javax.servlet.http.HttpServletRequest;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/2 0002 9:18
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FactoryCustomerFacade {
	RequestResult findFactoryCustomersCount();

	RequestResult findCustomers(Integer pageNum, Integer pageSize, MapContext params);

	RequestResult findCustomerInfo(String userId, String customerId);

	RequestResult findCustomerOrderInfo(String userId, String customerId);

	RequestResult factoryAddCustomer(String dealerId, MapContext mapContext, HttpServletRequest request);

	RequestResult findCompanyEmployees(String companyId);

	RequestResult updateDealerCustomer(String dealerId, String userId, MapContext mapContext);
}
