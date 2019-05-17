package com.lwxf.industry4.webapp.facade.app.factory.factoryEmployee;

import javax.servlet.http.HttpServletRequest;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/3/28 0028 9:52
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FactoryEmployeeFacade extends BaseFacade {

	RequestResult findDeptEmployeeCount(String companyId);

	RequestResult findDeptList(String companyId);

	RequestResult findRoleList(String companyId);

	RequestResult findEmployeeList(Integer pageNum, Integer pageSize, MapContext mapContext);

	RequestResult findEmployeeInfo(String companyId,String userId);

	RequestResult findAllEmployees(String companyId);

	RequestResult updateDianzhu();
}
