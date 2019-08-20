package com.lwxf.industry4.webapp.domain.dao.product.impl;


import java.util.List;
import java.util.Map;


import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.product.ProductFilesType;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.product.ProductDto;
import com.lwxf.industry4.webapp.domain.entity.product.ProductColor;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDaoImpl;
import com.lwxf.industry4.webapp.domain.dao.product.ProductDao;
import com.lwxf.industry4.webapp.domain.entity.product.Product;


/**
 * 功能：
 * 
 * @author：F_baisi(F_baisi@163.com)
 * @created：2018-12-01 10:21:32
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Repository("productDao")
public class ProductDaoImpl extends BaseDaoImpl<Product, String> implements ProductDao {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<ProductDto> selectByFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilter");
		//
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<ProductDto> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public List<Product> selectByCategoryId(String id) {
		String sql = this.getNamedSqlId("selectByCategoryId");
		return this.getSqlSession().selectList(sql,id);
	}

	@Override
	public List<Product> selectByColorId(String id) {
		String sql = this.getNamedSqlId("selectByColorId");
		return this.getSqlSession().selectList(sql,id);
	}

	@Override
	public List<Product> selectByMaterialId(String id) {
		String sql = this.getNamedSqlId("selectByMaterialId");
		return this.getSqlSession().selectList(sql,id);
	}

	@Override
	public Product selectByModel(String no) {
		String sql = this.getNamedSqlId("selectByModel");
		return this.getSqlSession().selectOne(sql,no);
	}

	@Override
	public ProductDto selectProductDtoById(String id) {
		String sql = this.getNamedSqlId("selectProductDtoById");
		MapContext mapContext = new MapContext();
		mapContext.put(WebConstant.KEY_ENTITY_ID,id);
		mapContext.put("fileType",ProductFilesType.WX_COVER_MAP.getValue());
		return this.getSqlSession().selectOne(sql,mapContext);
	}

	@Override
	public List<Product> selectBySpecId(String id) {
		String sql = this.getNamedSqlId("selectBySpecId");
		return this.getSqlSession().selectList(sql,id);
	}

	@Override
	public List findResourcesList(MapContext mapContent) {
		String sql = this.getNamedSqlId("findResourcesList");
		return this.getSqlSession().selectList(sql,mapContent);
	}

	@Override
	public List<Product> findProductsBySupplierId(String supplierId) {
		String sql = this.getNamedSqlId("findProductsBySupplierId");
		return this.getSqlSession().selectList(sql,supplierId);
	}

	@Override
	public List<Product> findProductsRecommend(String companyId) {
		String sql = this.getNamedSqlId("findProductsRecommend");
		return this.getSqlSession().selectList(sql,companyId);
	}
}