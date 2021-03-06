package com.lwxf.industry4.webapp.domain.dao.finished;


import java.util.List;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.entity.finished.FinishedProductSeries;


/**
 * 功能：
 * 
 * @author：panchenxiao(Mister_pan@126.com)
 * @created：2018-12-18 19:52:24
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@IBatisSqlTarget
public interface FinishedProductSeriesDao extends BaseDao<FinishedProductSeries, String> {

	PaginatedList<FinishedProductSeries> selectByFilter(PaginatedFilter paginatedFilter);

	List<FinishedProductSeries> findAll();

}