package com.lwxf.industry4.webapp.facade.admin.factory.supplier;

import java.util.List;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyDto;
import com.lwxf.industry4.webapp.domain.dto.supplier.SupplierDto;
import com.lwxf.industry4.webapp.domain.entity.supplier.Supplier;
import com.lwxf.industry4.webapp.domain.entity.supplier.SupplierProduct;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/29/029 15:25
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface SupplierFacade extends BaseFacade {
	RequestResult findAllSupplierList(Integer pageNum, Integer pageSize, MapContext mapContext);

	RequestResult addSupplier(SupplierDto supplier);

	RequestResult addSupplierProduct(String supplierId, List<SupplierProduct> supplierProducts);

	RequestResult viewSupplierInfo(String id);

	RequestResult uploadImage(String userId, List<MultipartFile> multipartFile);

	RequestResult uploadProductImage(String supplierId,MultipartFile multipartFile);

	RequestResult updateSupplier(String supplierId, MapContext map);

	RequestResult deleteSupplier(String supplierId);

	RequestResult deleteSupplierProduct(String prodId);

	RequestResult deleteSupplierImages(String imgId);



	RequestResult countSupplierToday();


	RequestResult updateSupplierProduct(String prodId, MapContext map);
}
