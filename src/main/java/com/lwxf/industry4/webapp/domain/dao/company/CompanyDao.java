package com.lwxf.industry4.webapp.domain.dao.company;


import java.util.List;
import java.util.Map;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.company.*;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.CompanyFinanceInfoDto;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.CompanyFinanceListDto;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.mybatis.utils.MapContext;


/**
 * 功能：
 * 
 * @author：dongshibo(F_baisi@163.com)
 * @created：2018-12-05 11:24:07
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@IBatisSqlTarget
public interface CompanyDao extends BaseDao<Company, String> {

	PaginatedList<CompanyDto> selectByFilter(PaginatedFilter paginatedFilter);

	PaginatedList<CompanyDtoForApp> selectByFilterForApp(PaginatedFilter paginatedFilter);

	Company selectByNo(String no);

	Map<String,String> findCompanyStatistics();

	List<Company> findShopList(String address);

	List<String> getTop5CustOrdersCompaniesId(MapContext MapContext);

	CompanyDto findByCompanyId(String companyId);

	CompanyInfoDtoForApp findCompanyByIdForApp(String companyId);

	List<MonthsOrdersDto> selectCompaniesOrdersCountMonthly(MapContext MapContext);

	List<Company> findAllCompany(MapContext params);

	List<Map<String,Object>> companyTypesStatistics();

	int balancePlusForFactory(Double amount,Integer accountType);

	int balanceMinusForFactory(Double amount,Integer accountType);

    Map findBCompanyInfoById(String companyId);

    Map getCompanyInfoForApp(String companyId);

	List<CompanyAccountDto> getCompanyAccountInfoForApp(String companyId);

	List<Map> findByCityNameAndName(MapContext mapContext);


	Double getCompanyBalance(String companyId);


	Company findCompanyByType(Integer type);

    Integer findCompanyNumByOrderCreated(MapContext params);
}