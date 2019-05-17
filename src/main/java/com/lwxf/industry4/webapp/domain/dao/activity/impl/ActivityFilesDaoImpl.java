package com.lwxf.industry4.webapp.domain.dao.activity.impl;


import java.util.List;
import java.util.Map;


import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDaoImpl;
import com.lwxf.industry4.webapp.domain.dao.activity.ActivityFilesDao;
import com.lwxf.industry4.webapp.domain.entity.activity.ActivityFiles;


/**
 * 功能：
 * 
 * @author：panchenxiao(Mister_pan@126.com)
 * @created：2019-03-08 10:10:37
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Repository("activityFilesDao")
public class ActivityFilesDaoImpl extends BaseDaoImpl<ActivityFiles, String> implements ActivityFilesDao {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<ActivityFiles> selectByFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilter");
		//
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<ActivityFiles> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}


	@Override
	public ActivityFiles findByActivityAndPath(MapContext parmas) {
		String sqlId = this.getNamedSqlId("findByActivityAndPath");
		return this.getSqlSession().selectOne(sqlId,parmas);
	}

	@Override
	public ActivityFiles findByActivityId(String activityId) {
		String sqlId = this.getNamedSqlId("findByActivityId");
		return this.getSqlSession().selectOne(sqlId,activityId);
	}
}