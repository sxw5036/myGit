package com.lwxf.industry4.webapp.facade.app.dealer.company;

import javax.servlet.http.HttpServletRequest;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/3 0003 13:27
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface EmployeeFacade extends BaseFacade {



	RequestResult addEmployee(String companyId,MapContext mapContext,StringBuffer pwd);

	RequestResult deleteEmployee(String companyId,String userId,HttpServletRequest request);

	RequestResult updateEmployeeStatus(String companyId,String userId, Integer status,HttpServletRequest request);

	RequestResult updateEmployee(String companyId,String userId, MapContext mapContext,HttpServletRequest request);

	RequestResult setEmployeeByURId(String companyId,String userId, String roleId);

	RequestResult selectEmployeeList(Integer pageNum, Integer pageSize, MapContext mapContext, HttpServletRequest request);

	RequestResult findEmployeeMessage(String companyId, String userId,HttpServletRequest request);
}
