package com.lwxf.industry4.webapp.controller.admin.factory.aftersale;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.aftersale.AftersaleStatus;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApply;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.aftersale.AftersaleApplyFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2019/1/8/008 9:56
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController(value = "fAftersaleApplyController")
@RequestMapping(value = "/api/f/aftersales", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class AftersaleApplyController {
    @Resource(name = "fAftersaleApplyFacade")
    private AftersaleApplyFacade aftersaleApplyFacade;

    /**
     * 查询售后单列表
     *
     * @param type
     * @param status
     * @param no
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping
    private RequestResult findListAftersale(@RequestParam(required = false) Integer type,
                                            @RequestParam(required = false) Integer status,
                                            @RequestParam(required = false) String no,
                                            @RequestParam(required = false) Integer pageNum,
                                            @RequestParam(required = false) Integer pageSize) {

        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        MapContext mapContext = this.createMapContext(type, status, no);
        return this.aftersaleApplyFacade.findListAftersale(mapContext, pageNum, pageSize);
    }

    /**
     * 新增售后单
     *
     * @param orderId
     * @param aftersaleApply
     * @return
     */
    @PostMapping("{orderId}")
    private RequestResult addAftersale(@PathVariable String orderId,
                                       @RequestBody AftersaleApply aftersaleApply) {
        aftersaleApply.setCustomOrderId(orderId);
        aftersaleApply.setCreated(DateUtil.getSystemDate());
        aftersaleApply.setCreator(WebUtils.getCurrUserId());
        aftersaleApply.setStatus(AftersaleStatus.WAIT.getValue());
        aftersaleApply.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.AFTERSALE_APPLY_NO));
        RequestResult result = aftersaleApply.validateFields();
        if (result != null) {
            return result;
        }
        return this.aftersaleApplyFacade.addAftersale(orderId, aftersaleApply);
    }


    /**
     * 批量上传售后图片
     *
     * @param id
     * @param multipartFileList
     * @return
     */
    @PostMapping("{id}/files")
    private RequestResult uploadImgFile(@PathVariable String id,
                                        @RequestBody List<MultipartFile> multipartFileList) {
        Map<String, String> result = new HashMap<>();
        if (multipartFileList == null || multipartFileList.size() == 0) {
            result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, result);
        }
        for (MultipartFile multipartFile : multipartFileList) {
            if (multipartFile == null) {
                result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            } else if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
                result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
            } else if (multipartFile.getSize() > 1024 * 1024 * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
                return ResultFactory.generateErrorResult(ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
            }
            if (result.size() > 0) {
                return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, result);
            }
        }
        return this.aftersaleApplyFacade.uploadImgFile(multipartFileList, id);
    }

    /**
     * 删除售后单下的图片
     *
     * @param id
     * @param fileId
     * @return
     */
    @DeleteMapping("{id}/files/{fileId}")
    private RequestResult deleteFileImg(@PathVariable String id,
                                        @PathVariable String fileId) {
        return this.aftersaleApplyFacade.deleteFileImg(id, fileId);
    }

    /**
     * 修改售后单
     *
     * @param id
     * @param mapContext
     * @return
     */
    @PutMapping("{id}")
    private RequestResult updateAftersaleApply(@PathVariable String id,
                                               @RequestBody MapContext mapContext) {
        RequestResult result = AftersaleApply.validateFields(mapContext);
        if (result != null) {
            return result;
        }
        return this.aftersaleApplyFacade.updateAftersaleApply(id, mapContext);
    }

    /**
     * 删除售后单
     *
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    private RequestResult deleteAftersaleApply(@PathVariable String id) {
        return this.aftersaleApplyFacade.deleteAftersaleApply(id);
    }

    private MapContext createMapContext(Integer type, Integer status, String no) {
        MapContext mapContext = MapContext.newOne();
        if (type != null && type != -1) {
            mapContext.put("type", type);
        }
        if (status != null && status != -1) {
            mapContext.put(WebConstant.KEY_ENTITY_STATUS, status);
        }
        if (no != null && !no.trim().equals("")) {
            mapContext.put(WebConstant.STRING_NO, no);
        }
        return mapContext;
    }
}
