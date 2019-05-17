package com.lwxf.industry4.webapp.domain.dao.company.impl;


import java.util.List;
import java.util.Map;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.lwxf.industry4.webapp.domain.dto.company.*;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.CompanyFinanceInfoDto;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.CompanyFinanceListDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDaoImpl;
import com.lwxf.industry4.webapp.domain.dao.company.CompanyDao;
import com.lwxf.industry4.webapp.domain.entity.company.Company;


/**
 * 功能：
 * 
 * @author：dongshibo(F_baisi@163.com)
 * @created：2018-12-05 11:24:07
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Repository("companyDao")
public class CompanyDaoImpl extends BaseDaoImpl<Company, String> implements CompanyDao {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<CompanyDto> selectByFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilter");
		//
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<CompanyDto> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public Company selectByNo(String no) {
		String sql = this.getNamedSqlId("selectByNo");
		return this.getSqlSession().selectOne(sql,no);
	}

	@Override
	public List<Company> findShopList(String address) {
		String sql = this.getNamedSqlId("findShopList");
		MapContext mapContext=MapContext.newOne();
		mapContext.put("address",address);
		return this.getSqlSession().selectList(sql,mapContext);
	}

	@Override
	public CompanyDto findByCompanyId(@Param(value="companyId") String companyId) {
		String sqlId = this.getNamedSqlId("findByCompanyId");
		return this.getSqlSession().selectOne(sqlId,companyId);
	}

	@Override
	public List<MonthsOrdersDto> selectCompaniesOrdersCountMonthly(MapContext mapContext) {
		String sqlId = this.getNamedSqlId("selectCompaniesOrdersCountMonthly");
		getSqlSession().clearCache();
		 return this.getSqlSession().selectList(sqlId,mapContext);
	}

	@Override
	public List<Company> findAllCompany(MapContext params) {
		String sqlId=this.getNamedSqlId("findAllCompany");
		return this.getSqlSession().selectList(sqlId,params);
	}

	@Override
	public Map<String, String> findCompanyStatistics() {
		String sqlId = this.getNamedSqlId("findCompanyStatistics");
		return this.getSqlSession().selectOne(sqlId);
	}

	@Override
	public PaginatedList<CompanyDtoForApp> selectByFilterForApp(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilterForApp");
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<CompanyDtoForApp> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public List<String> getTop5CustOrdersCompaniesId(MapContext mapContext) {
		String sqlId = this.getNamedSqlId("getTop5CustOrdersCompaniesId");
		return this.getSqlSession().selectList(sqlId,mapContext);
	}

	@Override
	public CompanyInfoDtoForApp findCompanyByIdForApp(String companyId) {
		String sqlId = this.getNamedSqlId("findCompanyByIdForApp");
		return this.getSqlSession().selectOne(sqlId,companyId);
	}

	public List<Map<String,Object>> companyTypesStatistics(){
		String sqlId = this.getNamedSqlId("companyTypesStatistics");
		return this.getSqlSession().selectList(sqlId);
	}

	@Override
	public int balancePlusForFactory(Double amount,Integer accountType) {
		String sqlId = this.getNamedSqlId("balancePlusForFactory");
		MapContext map = MapContext.newOne();
		map.put("amount",amount);
		map.put("type",accountType);
		return this.getSqlSession().update(sqlId,map);
	}

	@Override
	public int balanceMinusForFactory(Double amount,Integer accountType) {
		String sqlId = this.getNamedSqlId("balanceMinusForFactory");
		MapContext map = MapContext.newOne();
		map.put("amount",amount);
		map.put("type",accountType);
		return this.getSqlSession().update(sqlId,map);
	}

	@Override
	public Map findBCompanyInfoById(String companyId) {
		String sqlId = this.getNamedSqlId("findBCompanyInfoById");
		return this.getSqlSession().selectOne(sqlId,companyId);

	}

	@Override
	public List<Map> findByCityNameAndName(MapContext mapContext) {
		String sqlId = this.getNamedSqlId("findByCityNameAndName");
		return this.getSqlSession().selectList(sqlId,mapContext);
	}

	@Override
	public Map getCompanyInfoForApp(String companyId) {
		String sqlId = this.getNamedSqlId("getCompanyInfoForApp");
		return this.getSqlSession().selectOne(sqlId,companyId);
	}

	@Override
	public List<CompanyAccountDto> getCompanyAccountInfoForApp(String companyId) {
		String sqlId = this.getNamedSqlId("getCompanyAccountInfoForApp");
		return this.getSqlSession().selectList(sqlId,companyId);
	}

	@Override
	public Double getCompanyBalance(String companyId) {
		String sqlId = this.getNamedSqlId("getCompanyBalance");
		return this.getSqlSession().selectOne(sqlId,companyId);
	}

	@Override
	public Company findCompanyByType(Integer type) {
		String sqlId = this.getNamedSqlId("findCompanyByType");
		return this.getSqlSession().selectOne(sqlId,type);
	}

	@Override
	public Integer findCompanyNumByOrderCreated(MapContext params) {
		String sqlId = this.getNamedSqlId("findCompanyNumByOrderCreated");
		return this.getSqlSession().selectOne(sqlId,params);
	}
}