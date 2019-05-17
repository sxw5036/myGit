package com.lwxf.industry4.webapp.controller.app.factory.factoryorder;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.design.DesignDemandFacade;
import com.lwxf.industry4.webapp.facade.app.factory.factoryorder.FactoryOrderDesignFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：F端APP设计管理
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/7 10:46
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/f", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryOrderDesignController {

    @Resource(name = "factoryOrderDesignFacade")
    private FactoryOrderDesignFacade factoryOrderDesignFacade;
    @Resource(name = "designDemandFacade")
    private DesignDemandFacade designDemandFacade;

    /**
     * 设计概览 - 待设计
     *
     * @return
     */
    @GetMapping(value = "/orders/undesigns")
    public String findUnDesign(@RequestParam(required = false) Integer pageSize,
                               @RequestParam(required = false) Integer pageNum,
                               @RequestParam(required = false) String beginTime,
                               @RequestParam(required = false) String endTime,
                               @RequestParam(required = false) String creator,
                               @RequestParam(required = false) String no) {

        if (pageSize == null) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }

        if (pageNum == null) {
            pageNum = 1;
        }
        Pagination pagin = Pagination.newOne();
        pagin.setPageSize(pageSize);
        pagin.setPageNum(pageNum);
        MapContext params = MapContext.newOne();
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        params.put("creator", creator);
        params.put("no", no);

        RequestResult result = factoryOrderDesignFacade.findUnDesign(pagin, params);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }

    /**
     * 设计概览 - 已设计
     *
     * @return
     */
    @GetMapping(value = "/orders/designeds")
    public String findDesigned(@RequestParam(required = false) Integer pageSize,
                               @RequestParam(required = false) Integer pageNum,
                               @RequestParam(required = false) String beginTime,
                               @RequestParam(required = false) String endTime,
                               @RequestParam(required = false) String designer,
                               @RequestParam(required = false) String no) {

        if (pageSize == null) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }

        if (pageNum == null) {
            pageNum = 1;
        }
        Pagination pagin = Pagination.newOne();
        pagin.setPageSize(pageSize);
        pagin.setPageNum(pageNum);
        MapContext params = MapContext.newOne();
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        params.put("designer", designer);
        params.put("no", no);
        RequestResult result = factoryOrderDesignFacade.findDesigned(pagin, params);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }


    /**
     * 设计详情
     *
     * @param orderId
     * @param designId
     * @return
     */
    @GetMapping(value = "/orders/{orderId}/designs/{designId}/info")
    public String findDesignInfo(@PathVariable String orderId,
                                 @PathVariable(required = false) String designId) {
        RequestResult result = factoryOrderDesignFacade.findDesignInfo(orderId, designId);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);

    }

    /**
     * 添加设计
     *
     * @param mapContext
     * @param request
     * @return
     */
    @PostMapping(value = "/orders/{orderId}/designs")
    public RequestResult addDesign(@RequestBody MapContext mapContext,
                                   HttpServletRequest request,
                                   @PathVariable String orderId) {
        String uid = request.getHeader("X-UID");
        RequestResult result = this.factoryOrderDesignFacade.addDesign(mapContext, uid);
        return result;
    }


    /**
     * 上传附件
     *
     * @param multipartFiles 图片
     * @param orderId        订单的id
     * @param belongId       所属资源的id 设计的id
     * @param request
     * @return
     */
    @PostMapping("/orders/{orderId}/designs/{belongId}/files")
    public RequestResult addAccessory(@RequestBody List<MultipartFile> multipartFiles,
                                      @PathVariable String orderId,
                                      @PathVariable String belongId,
                                      HttpServletRequest request) {
        String userId = request.getHeader("X-UID");
        Map<String, Object> errorInfo = new HashMap<>();
        if (multipartFiles == null || multipartFiles.size() == 0) {
            errorInfo.put("multipartFiles", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
        }

        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile == null) {
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
        return this.designDemandFacade.addHomeStyleImage(multipartFiles, orderId, belongId, userId, 0, 1);


    }

}
