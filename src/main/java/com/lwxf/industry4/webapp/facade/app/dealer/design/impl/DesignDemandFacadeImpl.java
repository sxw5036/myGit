package com.lwxf.industry4.webapp.facade.app.dealer.design.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderDemandService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderDesignService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderFilesService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.product.*;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.product.ProductType;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDemandDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderDemand;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderFiles;
import com.lwxf.industry4.webapp.domain.entity.product.ProductColor;
import com.lwxf.industry4.webapp.domain.entity.product.ProductDoor;
import com.lwxf.industry4.webapp.domain.entity.product.ProductMaterial;
import com.lwxf.industry4.webapp.domain.entity.product.ProductSpec;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.design.DesignDemandFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/1 14:39
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("designDemandFacade")
public class DesignDemandFacadeImpl extends BaseFacadeImpl implements DesignDemandFacade {
    @Resource(name = "customOrderService")
    private CustomOrderService customOrderService;//订单
    @Resource(name = "customOrderDesignService")
    private CustomOrderDesignService customOrderDesignService;//设计
    @Resource(name = "customOrderDemandService")
    private CustomOrderDemandService customOrderDemandService;//设计需求
    @Resource(name = "customOrderFilesService")
    private CustomOrderFilesService customOrderFilesService;
    @Resource(name = "productColorService")
    private ProductColorService productColorService;//产品颜色
    @Resource(name = "productMaterialService")
    private ProductMaterialService productMaterialService;//产品材质
    @Resource(name = "productSpecService")
    private ProductSpecService productSpecService;//产品规格
    @Resource(name = "productCategoryService")
    private ProductCategoryService productCategoryService;//产品分类
    @Resource(name = "productDoorService")
    private ProductDoorService productDoorService;//产品门型


    @Override
    public RequestResult findByOrderId(String id) {
        List<CustomOrderDemand> customOrderDemandList = this.customOrderDemandService.findByorderId(id);
        return ResultFactory.generateRequestResult(customOrderDemandList);
    }

    /**
     * 上传文件
     * @param multipartFiles
     * @param orderId
     * @param belongId
     * @param userId
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addHomeStyleImage(List<MultipartFile> multipartFiles, String orderId, String belongId, String userId, Integer category, Integer type) {
        UploadInfo uploadInfo;
        List<Map<String,Object>> parmas = new ArrayList<>();
        for (MultipartFile multipartFile:multipartFiles) {
            uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, multipartFile, UploadResourceType.CUSTOM_ORDER, orderId, belongId);
            //创建
            CustomOrderFiles customOrderFiles = CustomOrderFiles.create(orderId, belongId, uploadInfo, UploadType.FORMAL, category, type);
            //获取上传的ID并赋值
            customOrderFiles.setCreator(userId);
            this.customOrderFilesService.add(customOrderFiles);

            MapContext result = MapContext.newOne();
            result.put("imagePath", uploadInfo.getRelativePath());
            parmas.add(result);
        }
        return ResultFactory.generateRequestResult(parmas);
    }


    /**
     * 添加设计需求
     * @param customOrderDemand
     * @param request
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addDesignDemand(CustomOrderDemand customOrderDemand, HttpServletRequest request) {
        String userId = request.getHeader("X-UID");
        customOrderDemand.setCreated(DateUtil.getSystemDate());
        customOrderDemand.setCreator(userId);
        customOrderDemand.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.ORDER_DEMAND_NO));
        RequestResult result = customOrderDemand.validateFields();
        if(null!=result){
            return  result;
        }
        this.customOrderDemandService.add(customOrderDemand);
        MapContext mapContext = MapContext.newOne();
        mapContext.put("id",customOrderDemand.getId() );
        mapContext.put("orderId",customOrderDemand.getCustomOrderId());
        return ResultFactory.generateRequestResult(mapContext);
}

    /**
     * 修改设计需求
     * @param updateMap
     * @param request
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updateDesignDemand(MapContext updateMap, HttpServletRequest request) {
        RequestResult result = CustomOrderDemand.validateFields(updateMap);
        if (null!=result){
            return  result;
        }
        this.customOrderDemandService.updateByMapContext(updateMap);
        return ResultFactory.generateRequestResult(updateMap);
    }

    //根据订单id查询需求的详情和图片
    @Override
    public RequestResult findDemandImageByDemandId(String orderId, String demandId) {
        List<CustomOrderFiles> filesList = this.customOrderFilesService.selectByOrderIdAndType(orderId, null, demandId);
        List<String> filesPath = new ArrayList<>();
        if(null!=filesList||filesList.size()>0){
            for (CustomOrderFiles customOrderFile :filesList){
                String path = customOrderFile.getPath();
                filesPath.add(path);
            }
        }
        CustomOrderDemandDto customOrderDemand = this.customOrderDemandService.selectByDemandId(demandId);
        MapContext result = MapContext.newOne();
        result.put("filesPath",filesPath);
        result.put("customOrderDemand",customOrderDemand);
        return ResultFactory.generateRequestResult(result);
    }


    /**
     * 添加需求的时候需要展示的门板的信
     * @return
     */
    @Override
    public RequestResult findDoorInfo() {

        List<ProductMaterial> doorMaterials = this.productMaterialService.selectByType(ProductType.DOOR_PLANK.getValue());
        List<ProductColor> doorColors = this.productColorService.selectByType(ProductType.DOOR_PLANK.getValue());
        List<ProductDoor> productDoors = this.productDoorService.findAll();

        List<ProductMaterial> cabinetiMaterials = this.productMaterialService.selectByType(ProductType.CABINET_PLANK.getValue());
        List<ProductColor> cabinetiColors = this.productColorService.selectByType(ProductType.CABINET_PLANK.getValue());

        List<ProductSpec> handleSpecs = this.productSpecService.selectByType(ProductType.HANDLE.getValue());
        List<ProductColor> handleColors = this.productColorService.selectByType(ProductType.HANDLE.getValue());


        MapContext params = MapContext.newOne();
        params.put("doorMaterials",doorMaterials);//门板材质
        params.put("doorColors",doorColors);//门板颜色
        params.put("doorStyles",productDoors);//门板门型
        params.put("cabinetMaterials",cabinetiMaterials);//柜体材质
        params.put("cabinetColors",cabinetiColors);//柜体颜色
        params.put("handleSpecs",handleSpecs);//把手样式
        params.put("handleColors",handleColors);//把手颜色
        return ResultFactory.generateRequestResult(params);
    }

}

