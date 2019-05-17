package com.lwxf.industry4.webapp.domain.dao.system.impl;


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
import com.lwxf.industry4.webapp.domain.dao.system.RoleDao;
import com.lwxf.industry4.webapp.domain.entity.system.Role;


/**
 * 功能：
 * 
 * @author：dongshibo(zjl869319827@outlook.com)
 * @created：2018-12-07 14:10:48
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Repository("roleDao")
public class RoleDaoImpl extends BaseDaoImpl<Role, String> implements RoleDao {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<Role> selectByFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilter");
		//
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<Role> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public Role selectByKey(String key) {
		String sql = this.getNamedSqlId("selectByKey");
		return this.getSqlSession().selectOne(sql,key);
	}

	@Override
	public List<Role> findListByType(Integer type,String key) {
		String sql = this.getNamedSqlId("findListByType");
		MapContext mapContext=MapContext.newOne();
		mapContext.put("type",type);
		mapContext.put("key",key);
		return this.getSqlSession().selectList(sql,mapContext);
	}


	@Override
	public Role findRoleByCidAndUid(String userId, String companyId) {
		String sql = this.getNamedSqlId("findRoleByCidAndUid");
		MapContext params = MapContext.newOne();
		params.put("userId",userId);
		params.put("companyId",companyId);
		return this.getSqlSession().selectOne(sql,params);
	}



	@Override
	public List<Role> findAllFactoryRole() {
		String sql = this.getNamedSqlId("findAllFactoryRole");
		return this.getSqlSession().selectList(sql);
	}

	@Override
	public Role findRoleByKey(String key) {
		String sql = this.getNamedSqlId("findRoleByKey");
		return this.getSqlSession().selectOne(sql,key);
	}

}