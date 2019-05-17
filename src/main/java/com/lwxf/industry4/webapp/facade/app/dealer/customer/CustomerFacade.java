package com.lwxf.industry4.webapp.facade.app.dealer.customer;

import javax.servlet.http.HttpServletRequest;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/4 0004 10:14
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface CustomerFacade extends BaseFacade {


	RequestResult selectCustomersList(MapContext mapContext, Integer pageNum, Integer pageSize,HttpServletRequest request);

	RequestResult updateCustomer(String companyId, String userId, MapContext mapContext,HttpServletRequest request);


	RequestResult addCustomer(String companyId,MapContext mapContext,String uid,String cid);


	RequestResult deleteCustomer(String userId);


	RequestResult findCustomerMessageById(String companyId,String userId,HttpServletRequest request);

	RequestResult findCustomersCount(MapContext mapContext, HttpServletRequest request);
}
