package com.lwxf.industry4.webapp.facade.admin.factory.supplier;

import java.util.List;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyDto;
import com.lwxf.industry4.webapp.domain.entity.supplier.SupplierProduct;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/29/029 15:25
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface SupplierFacade extends BaseFacade {
	RequestResult findAllSupplierList(Integer pageNum,Integer pageSize,MapContext mapContext);

	RequestResult addSupplierProdcut(String id, List<SupplierProduct> supplierProductList);

	RequestResult addSupplier(CompanyDto company);

	RequestResult addSupplierLeader(User user,String id);

	RequestResult findProductList(MapContext mapContext, Integer pageNum, Integer pageSize);

	RequestResult updateProduct(String id, String productId, MapContext mapContext);

	RequestResult deleteProduct(String id, String productId);
}
