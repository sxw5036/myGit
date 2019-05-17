package com.lwxf.industry4.webapp.controller.app.dealer.design;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderFilesCategory;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderFilesType;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderDemand;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.design.DesignDemandFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：设计需求
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/1 14:37
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class DesignDemandController extends BaseDealerController {
    @Resource(name = "designDemandFacade")
    private DesignDemandFacade designDemandFacade;

    /**
     * 查看订单下的设计需求
     * @param orderId
     * @param request
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/customorders/{orderId}/customdemands")
    public String findByOrderId(@PathVariable String orderId,HttpServletRequest request,
                                @PathVariable String companyId){
        String cid = request.getHeader("X-CID");//公司id
        JsonMapper mapper = new JsonMapper();
        if (!companyId.equals(cid)){
            return  mapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
        String xp = "bdesignneedmng-designneed-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return mapper.toJson(result1);
        }
        RequestResult result = designDemandFacade.findByOrderId(orderId);
        return mapper.toJson(result);
    }



    /**
     * 修改设计需求
     *
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/customorders/{orderId}/customdemands/{demandId}")
    public RequestResult updateDesignDemand(@PathVariable String orderId,
                                            HttpServletRequest request,
                                            @PathVariable String demandId,
                                            @RequestBody MapContext updateMap,
                                            @PathVariable String companyId) {
        String cid = request.getHeader("X-CID");//公司id
        String xp = "bdesignneedmng-designneed-edit";
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        updateMap.put("id", demandId);
        return this.designDemandFacade.updateDesignDemand(updateMap, request);
    }

    /**
     * 添加设计需求
     *
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/customorders/{orderId}/customdemands")
    public RequestResult addDesignDemand(@PathVariable String orderId,
                                         @RequestBody MapContext params,
                                         HttpServletRequest request,
                                         @PathVariable String companyId) {
        String cid = request.getHeader("X-CID");//公司id
        String xp = "bdesignneedmng-designneed-edit";
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        CustomOrderDemand customOrderDemand = new CustomOrderDemand();
        customOrderDemand.setCustomOrderId(orderId);
        customOrderDemand.setName((String)params.get("name"));
        customOrderDemand.setContent((String)params.get("content"));
        //这几个都是下拉选择 开始
//        customOrderDemand.setDoorTexture((String)params.get("doorTexture"));//门板材质
//        customOrderDemand.setDoorColor((String)params.get("doorColor"));//门板颜色
//        customOrderDemand.setDoorModel((String)params.get("doorModel"));//门板门型
//        customOrderDemand.setCabinetColor((String)params.get("cabinetColor"));//柜体颜色
//        customOrderDemand.setCabinetSeries((String)params.get("cabinetSeries"));//柜体系列
//        customOrderDemand.setCabinetTexture((String)params.get("cabinetTexture"));//柜体材质
//        customOrderDemand.setHandleColor((String)params.get("handleColor"));//把手颜色
//        customOrderDemand.setHandleStyle((String)params.get("handleStyle"));//把手样式
        //结束
        if (LwxfStringUtils.isBlank((String)params.get("designScheme"))){
            customOrderDemand.setDesignScheme(null);
        }else {
            customOrderDemand.setDesignScheme((String)params.get("designScheme"));
        }
        if (LwxfStringUtils.isBlank((String)params.get("productSeries"))){
            customOrderDemand.setProductSeries(null);
        }else {
            customOrderDemand.setProductSeries((String)params.get("productSeries"));
        }
        if (LwxfStringUtils.isBlank((String)params.get("productModel"))){
            customOrderDemand.setProductModel(null);
        }else {
            customOrderDemand.setProductModel((String)params.get("productModel"));
        }
        return this.designDemandFacade.addDesignDemand(customOrderDemand, request);
    }



    /**
     * 根据场景不同上传type的值
     * 根据文件的分类表示不同的文件类型Category
     * 上传设计需求图或者设计师的设计图
     * 上传3D图、VR图、视频、附件
     * app端目前只支持传平面图和附件
     *
     * @param
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/customorders/{orderId}/files")
    public RequestResult addHomeStyleImage(
            @RequestBody List<MultipartFile> multipartFiles,
            @PathVariable String orderId,
            @PathVariable String companyId,
            @RequestParam(required = false) String belongId,
            @RequestParam Integer category,
            @RequestParam Integer type,
            HttpServletRequest request) {
        String userId = request.getHeader("X-UID");
        String cid = request.getHeader("X-CID");
        if (!companyId.equals(cid)){
            return  ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        Map<String, Object> errorInfo = new HashMap<>();

        Boolean containsType = CustomOrderFilesType.contains(type);
        if (!containsType) {
            return ResultFactory.generateErrorResult(AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"), AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
        }
        Boolean containsCategory = CustomOrderFilesCategory.contains(category);
        if (!containsCategory) {
            return ResultFactory.generateErrorResult(AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"), AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
        }
        if (multipartFiles == null||multipartFiles.size()==0) {
            errorInfo.put("multipartFiles", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
        }

        for (MultipartFile multipartFile:multipartFiles) {
            if (multipartFile==null){
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            }
            if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
            }
            if (multipartFile.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
                return ResultFactory.generateErrorResult(ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
            }
        }
        if (errorInfo.size() > 0) {
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
        }
        return this.designDemandFacade.addHomeStyleImage(multipartFiles, orderId, belongId, userId, category, type);
    }


    /**
     * 获取设计需求的详情和需求图片
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/customorders/{orderId}/customdemands/{demandId}/files")
    public String findDemandImageByDemandId(@PathVariable String orderId,
                                                @PathVariable String demandId,
                                            HttpServletRequest request,
                                            @PathVariable String companyId){

        String cid = request.getHeader("X-CID");//公司id
        JsonMapper mapper = new JsonMapper();
        if (!companyId.equals(cid)){
            return  mapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020")));
        }
        String xp = "bdesignneedmng-designneed-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return mapper.toJson(result1);
        }
        RequestResult demandImageByDemand = this.designDemandFacade.findDemandImageByDemandId(orderId, demandId);

        return mapper.toJson(demandImageByDemand);
    }


    /**
     * 查询门型的材质、颜色、门型,柜体颜色、材质,把手的样式、颜色
     */
    @GetMapping(value = "/customdemands/choose")
    public RequestResult findDoorInfo(){
        return this.designDemandFacade.findDoorInfo();
    }

}

