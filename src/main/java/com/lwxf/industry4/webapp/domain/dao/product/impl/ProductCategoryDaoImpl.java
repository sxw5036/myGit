package com.lwxf.industry4.webapp.domain.dao.product.impl;


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
import com.lwxf.industry4.webapp.domain.dao.product.ProductCategoryDao;
import com.lwxf.industry4.webapp.domain.entity.product.ProductCategory;


/**
 * 功能：
 * 
 * @author：F_baisi(F_baisi@163.com)
 * @created：2018-12-01 10:21:33
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Repository("productCategoryDao")
public class ProductCategoryDaoImpl extends BaseDaoImpl<ProductCategory, String> implements ProductCategoryDao {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<ProductCategory> selectByFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilter");
		//
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<ProductCategory> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public List<ProductCategory> findAll() {
		String sql = this.getNamedSqlId("findAll");
		return this.getSqlSession().selectList(sql);
	}

	@Override
	public List<ProductCategory> selectProductCategoryByFilter(MapContext map) {
		String sql = this.getNamedSqlId("selectProductCategoryByFilter");
		return this.getSqlSession().selectList(sql,map);
	}

	@Override
	public ProductCategory selectProductCategoryByName(String name) {
		String sql = this.getNamedSqlId("selectProductCategoryByName");
		return this.getSqlSession().selectOne(sql,name);
	}

}