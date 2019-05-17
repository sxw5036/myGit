package com.lwxf.industry4.webapp.bizservice.system.impl;


import com.lwxf.industry4.webapp.bizservice.base.BaseServiceImpl;
import com.lwxf.industry4.webapp.bizservice.system.LogisticsCompanyService;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dao.system.LogisticsCompanyDao;
import com.lwxf.industry4.webapp.domain.entity.system.LogisticsCompany;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;


/**
 * 功能：
 * 
 * @author：panchenxiao(Mister_pan@126.com)
 * @created：2018-12-20 15:10:29
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Service("logisticsCompanyService")
public class LogisticsCompanyServiceImpl extends BaseServiceImpl<LogisticsCompany, String, LogisticsCompanyDao> implements LogisticsCompanyService {


	@Resource

	@Override	public void setDao( LogisticsCompanyDao logisticsCompanyDao) {
		this.dao = logisticsCompanyDao;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<LogisticsCompany> selectByFilter(PaginatedFilter paginatedFilter) {
		//
		return this.dao.selectByFilter(paginatedFilter) ;
	}

	@Override
	public List<LogisticsCompany> findAllNormal() {
		return this.dao.findAllNormal();
	}

}