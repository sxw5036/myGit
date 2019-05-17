package com.lwxf.industry4.webapp.controller.admin.factory.procurement;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.procurment.PurchaseRquestStatus;
import com.lwxf.industry4.webapp.common.enums.procurment.PurchaseStatus;
import com.lwxf.industry4.webapp.common.enums.procurment.PurchaseType;
import com.lwxf.industry4.webapp.common.enums.storage.StorageOutputInStatus;
import com.lwxf.industry4.webapp.common.enums.storage.StorageOutputInType;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.procurement.PurchaseDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.StorageOutputInDto;
import com.lwxf.industry4.webapp.domain.entity.procurement.Purchase;
import com.lwxf.industry4.webapp.domain.entity.procurement.PurchaseProduct;
import com.lwxf.industry4.webapp.domain.entity.procurement.PurchaseRequest;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.procurement.PurchaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/17/017 11:13
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/purchases", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class PurchaseController {
    @Resource(name = "purchaseFacade")
    private PurchaseFacade purchaseFacade;

    /**
     * 查询申请采购单列表
     *
     * @param pageSize
     * @param pageNum
     * @param no
     * @param storageId
     * @param name
     * @param productId
     * @param type
     * @param creator
     * @param status
     * @return
     */
    @GetMapping("requests")
    private RequestResult findRequestList(@RequestParam(required = false) Integer pageSize,
                                          @RequestParam(required = false) Integer pageNum,
                                          @RequestParam(required = false) String no,
                                          @RequestParam(required = false) String storageId,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false) String productId,
                                          @RequestParam(required = false) String type,
                                          @RequestParam(required = false) String creator,
                                          @RequestParam(required = false) String status) {

        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        MapContext mapContext = this.createMapContent(no, storageId, name, productId, type, creator, status);
        return this.purchaseFacade.findRequestList(mapContext, pageNum, pageSize);
    }

    /**
     * 新增申请采购单
     *
     * @param purchaseRequest
     * @return
     */
    @PostMapping("requests")
    private RequestResult addRequest(@RequestBody PurchaseRequest purchaseRequest) {
        purchaseRequest.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PURCHASE_REQUEST));
        purchaseRequest.setType(PurchaseType.ARTIFICIAL.getValue());
        purchaseRequest.setCreator(WebUtils.getCurrUserId());
        purchaseRequest.setCreated(DateUtil.getSystemDate());
        purchaseRequest.setStatus(PurchaseRquestStatus.STAY.getValue());
        RequestResult result = purchaseRequest.validateFields();
        if (result != null) {
            return result;
        }
        return this.purchaseFacade.addRequest(purchaseRequest);
    }

    /**
     * 修改采购申请单状态
     *
     * @param id
     * @param status
     * @return
     */
    @PutMapping("requests/{id}/{status}")
    private RequestResult updateStatus(@PathVariable String id,
                                       @PathVariable Integer status) {
        return this.purchaseFacade.updateStatus(id, status);
    }

    /**
     * 查询采购单列表
     *
     * @param status
     * @param name
     * @param paid
     * @param batch
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping
    private RequestResult findPurchaseList(@RequestParam(required = false) Integer status,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) Boolean paid,
                                           @RequestParam(required = false) String batch,
                                           @RequestParam(required = false) Integer pageNum,
                                           @RequestParam(required = false) Integer pageSize) {
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        MapContext mapContext = this.createPurchaseMapContent(status, name, paid, batch);
        return this.purchaseFacade.findPurchaseList(mapContext, pageNum, pageSize);
    }

    /**
     * 修改	采购单信息
     *
     * @param id
     * @param mapContext
     * @return
     */
    @PutMapping("{id}")
    private RequestResult updatePurchase(@PathVariable String id,
                                         @RequestBody MapContext mapContext) {
        RequestResult result = Purchase.validateFields(mapContext);
        if (result != null) {
            return result;
        }
        return this.purchaseFacade.updatePurchase(id, mapContext);
    }

    /**
     * 新增 采购单
     *
     * @param purchaseDto
     * @return
     */
    @PostMapping
    private RequestResult addPurchase(@RequestBody PurchaseDto purchaseDto) {
        purchaseDto.setStatus(PurchaseStatus.PENDING_APPROVAL.getValue());
        purchaseDto.setCreated(DateUtil.getSystemDate());
        purchaseDto.setCreator(WebUtils.getCurrUserId());
        purchaseDto.setBatch(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PURCHASE_BATCH));
        purchaseDto.setPaid(false);
        RequestResult result = purchaseDto.validateFields();
        if (result != null) {
            return result;
        }
        return this.purchaseFacade.addPurchase(purchaseDto);
    }

    /**
     * 修改采购单状态
     *
     * @param id
     * @param status
     * @return
     */
    @PutMapping("{id}/{status}")
    private RequestResult updatePurchaseStatus(@PathVariable String id,
                                               @PathVariable Integer status) {
        return this.purchaseFacade.updatePurchaseStatus(id, status);
    }

    /**
     * 采购单下新增采购产品
     *
     * @param id
     * @param purchaseProductList
     * @return
     */
    @PostMapping("{id}/products")
    private RequestResult addPurcahseProduct(@PathVariable String id,
                                             @RequestBody List<PurchaseProduct> purchaseProductList) {
        for (PurchaseProduct purchaseProduct : purchaseProductList) {
            purchaseProduct.setPurchaseId(id);
            RequestResult result = purchaseProduct.validateFields();
            if (result != null) {
                return result;
            }
        }
        return this.purchaseFacade.addPurcahseProduct(id, purchaseProductList);
    }

    /**
     * 修改采购单产品的价格和数量
     *
     * @param id
     * @param mapContext
     * @param purchaseProductId
     * @return
     */
    @PutMapping("{id}/products/{purchaseProductId}")
    private RequestResult updatePurchaseProduct(@PathVariable String id,
                                                @RequestBody MapContext mapContext,
                                                @PathVariable String purchaseProductId) {
        RequestResult result = PurchaseProduct.validateFields(mapContext);
        if (result != null) {
            return result;
        }
        return this.purchaseFacade.updatePurchaseProduct(id, mapContext, purchaseProductId);
    }

    /**
     * 删除采购单下的产品
     *
     * @param id
     * @param purchaseProductId
     * @return
     */
    @DeleteMapping("{id}/products/{purchaseProductId}")
    private RequestResult deletePurchaseProduct(@PathVariable String id,
                                                @PathVariable String purchaseProductId) {
        return this.purchaseFacade.deletePurchaseProduct(id, purchaseProductId);
    }

    /**
     * 生成入库单
     *
     * @param id
     * @param storageOutputInDto
     * @return
     */
    @PostMapping("{id}/outputins")
    private RequestResult addStorageInput(@PathVariable String id,
                                          @RequestBody StorageOutputInDto storageOutputInDto) {
        storageOutputInDto.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.STORAGE_OUTPUTIN_NO));
        storageOutputInDto.setCreated(DateUtil.getSystemDate());
        storageOutputInDto.setCreator(WebUtils.getCurrUserId());
        storageOutputInDto.setResId(id);
        storageOutputInDto.setStatus(StorageOutputInStatus.CONFIRM.getValue());
        storageOutputInDto.setType(StorageOutputInType.PURCHASE_WAREHOUSING.getValue());
        storageOutputInDto.setFlow(0);
        RequestResult result = storageOutputInDto.validateFields();
        if (result != null) {
            return result;
        }
        return this.purchaseFacade.addStorageInput(id, storageOutputInDto);
    }

    /**
     * 修改采购商品的状态
     *
     * @param id
     * @param purchaseProductId
     * @param status
     * @return
     */
    @PutMapping("{id}/products/{purchaseProductId}/{status}")
    private RequestResult updatePurcahseProductStatus(@PathVariable String id,
                                                      @PathVariable String purchaseProductId,
                                                      @PathVariable Integer status) {
        return this.purchaseFacade.updatePurcahseProductStatus(id, purchaseProductId, status);
    }

    private MapContext createPurchaseMapContent(Integer status, String name, Boolean paid, String batch) {
        MapContext mapContext = new MapContext();
        if (status != null && status != -1) {
            mapContext.put(WebConstant.KEY_ENTITY_STATUS, status);
        }
        if (name != null && !name.trim().equals("")) {
            mapContext.put(WebConstant.KEY_ENTITY_NAME, name);
        }
        if (paid != null) {
            mapContext.put("paid", paid);
        }
        if (batch != null && !batch.trim().equals("")) {
            mapContext.put("batch", batch);
        }
        return mapContext;
    }

    private MapContext createMapContent(String no, String storageId, String name, String productId, String type, String creator, String status) {
        MapContext mapContext = MapContext.newOne();
        if (no != null && !no.trim().equals("")) {
            mapContext.put(WebConstant.STRING_NO, no);
        }
        if (storageId != null && !storageId.trim().equals("")) {
            mapContext.put("storageId", storageId);
        }
        if (name != null && !name.trim().equals("")) {
            mapContext.put(WebConstant.KEY_ENTITY_NAME, name);
        }
        if (storageId != null && !storageId.trim().equals("")) {
            mapContext.put("storageId", storageId);
        }
        if (productId != null && !productId.trim().equals("")) {
            mapContext.put("productId", productId);
        }
        if (type != null && !type.trim().equals("")) {
            mapContext.put("type", type);
        }
        if (creator != null && !creator.trim().equals("")) {
            mapContext.put("creator", creator);
        }
        if (status != null && !status.trim().equals("")) {
            mapContext.put("status", status);
        }
        return mapContext;
    }
}
