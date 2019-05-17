package com.lwxf.industry4.webapp.domain.dao.system.impl;



import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Repository;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDaoImpl;
import com.lwxf.industry4.webapp.domain.dao.system.BasecodeDao;
import com.lwxf.industry4.webapp.domain.entity.system.Basecode;

import java.util.List;


/**
 * 功能：
 * 
 * @author：zhangxiaolin(3965488@qq.com)
 * @created：2019-05-04 10:55:31
 * @version：2019 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Repository("basecodeDao")
public class BasecodeDaoImpl extends BaseDaoImpl<Basecode, String> implements BasecodeDao {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<Basecode> selectByFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilter");
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<Basecode> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public List<Basecode> selectByFilter(MapContext mapContext) {
		String sqlId=this.getNamedSqlId("selectByFilter");
		return this.getSqlSession().selectList(sqlId,mapContext);
	}

	@Override
	public List<Basecode> findAll() {
		String sqlId = this.getNamedSqlId("findAll");
		return this.getSqlSession().selectList(sqlId);
	}
}