package com.lwxf.industry4.webapp.facade.admin.factory.procurement;

import java.util.List;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.dto.procurement.PurchaseDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.StorageOutputInDto;
import com.lwxf.industry4.webapp.domain.entity.procurement.PurchaseProduct;
import com.lwxf.industry4.webapp.domain.entity.procurement.PurchaseRequest;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/17/017 11:15
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface PurchaseFacade extends BaseFacade {
	RequestResult findRequestList(MapContext mapContext, Integer pageNum, Integer pageSize);

	RequestResult addRequest(PurchaseRequest purchaseRequest);

	RequestResult updateStatus(String id, Integer status);

	RequestResult findPurchaseList(MapContext mapContext, Integer pageNum, Integer pageSize);

	RequestResult updatePurchase(String id, MapContext mapContext);

	RequestResult addPurchase(PurchaseDto purchaseDto);


	RequestResult updatePurchaseStatus(String id, Integer status);

	RequestResult addPurcahseProduct(String id, List<PurchaseProduct> purchaseProductList);

	RequestResult updatePurchaseProduct(String id, MapContext mapContext,String purchaseProductId);

	RequestResult deletePurchaseProduct(String id, String purchaseProductId);

	RequestResult updatePurcahseProductStatus(String id, String purchaseProductId, Integer status);

	RequestResult addStorageInput(String id, StorageOutputInDto storageOutputInDto);
}
