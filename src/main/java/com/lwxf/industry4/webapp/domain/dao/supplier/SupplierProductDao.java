package com.lwxf.industry4.webapp.domain.dao.supplier;


import java.util.List;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.supplier.SupplierDtoFowWx;
import com.lwxf.industry4.webapp.domain.dto.supplier.SupplierProductDto;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.entity.supplier.SupplierProduct;


/**
 * 功能：
 * 
 * @author：F_baisi(F_baisi@163.com)
 * @created：2018-12-28 09:25:21
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@IBatisSqlTarget
public interface SupplierProductDao extends BaseDao<SupplierProduct, String> {

	PaginatedList<SupplierProduct> selectByFilter(PaginatedFilter paginatedFilter);

	SupplierProduct findOneBySupplierAndProductId(String id, String productId);

	List<SupplierProductDto> findListBySupplierAndProductIds(String id, List productIds);

	List<SupplierProduct> selectAllSupplierProduct(MapContext map);

	int deleteBySupplierId(String supplierId);

}