package com.lwxf.industry4.webapp.controller.app.dealer.design;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.scheme.SchemeStatus;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.domain.entity.design.DesignScheme;
import com.lwxf.industry4.webapp.domain.entity.design.SchemeContent;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.design.HomeCaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 功能：设计订单
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/1 14:48
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class HomeCaseController extends BaseDealerController {
    @Resource(name = "homeCaseFacade")
    private HomeCaseFacade homeCaseFacade;


    /**
     * 根据条件查询首页案例（分页）
     * 案例列表 和 案例检索
     *
     * @param pageNum       页码
     * @param pageSize
     * @param designer      设计师id
     * @param companyId     公司id
     * @param styles       设计风格
     * @param area          面积
     * @param doorStateIds    户型 可以多个 set接收
     * @return
     */
    @GetMapping(value = "/homecases")
    public RequestResult findByFilter(@RequestParam(required = false) Integer pageNum,
                                      @RequestParam(required = false) Integer pageSize,
                                      @RequestParam(required = false) String designer,//设计师id
                                      @RequestParam(required = false) String companyId,
                                      @RequestParam(required = false) String styles,//设计风格
                                      @RequestParam(required = false) Float area,//面积
                                      @RequestParam(required = false) Set<String> doorStateIds,//户型（可以传多个）
                                      HttpServletRequest request) {
        if(null == pageSize){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if(null == pageNum){
            pageNum = 1;
        }
        MapContext filters = MapContext.newOne();
        if (LwxfStringUtils.isNotBlank(styles)) {
            filters.put("styles", styles);
        }
        if (LwxfStringUtils.isNotBlank(designer)) {
            filters.put("designer", designer);
        }
        if (LwxfStringUtils.isNotBlank(companyId)) {
            filters.put("companyId", companyId);
        }
        if (area != null) {
            filters.put("area", area);
        }
        if (null != doorStateIds && doorStateIds.size() > 0) {
            filters.put("doorStateIds", doorStateIds);
        }
        filters.put(WebConstant.KEY_ENTITY_STATUS, SchemeStatus.PUBLISHED.getValue());
        return this.homeCaseFacade.selectByCondition(pageNum, pageSize, filters, request);
    }

    /**
     * 根据id查询首页案例详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/homecases/{id}")
    public String findById(@PathVariable String id) {
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.homeCaseFacade.findById(id);
        return mapper.toJson(result);
    }

    /**
     * 收藏设计案例/取消收藏设计案例
     *
     * @param
     * @param id
     * @return
     */
    @GetMapping(value = "/users/{userId}/homecases/{id}/attention")
    public RequestResult addSchemeAttention(
                                            @PathVariable String id,
                                            HttpServletRequest request,
                                            @PathVariable String userId) {
        return this.homeCaseFacade.addSchemeAttention(userId, id);
    }

    /**
     * 添加分享数量
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/homecases/{id}/share")
    public RequestResult addShare(@PathVariable String id,
                                  HttpServletRequest request) {
        return this.homeCaseFacade.addShare(id);
    }

    /**
     * 查询所有的设计风格
     *
     * @return
     */
    @GetMapping(value = "/designstyles")
    public RequestResult findAllDesignStyle(HttpServletRequest request) {
        return this.homeCaseFacade.findDesignStyleAndDoorState();
    }

    /**
     * 商家添加设计案例的主表
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/designScheme/add")
    public RequestResult addDesignScheme(@PathVariable String companyId,
                                         HttpServletRequest request,
                                         @RequestBody DesignScheme designScheme){

        String uid = request.getHeader("X-UID");

        String cid = request.getHeader("X-CID");
        String xp = "bschememng-shopplans-edit";
        if (!cid.equals(companyId)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        RequestResult result = this.validUserPermission(request,xp);
        if (result!=null){
            return result;
        }

        designScheme.setCompanyId(companyId);
        designScheme.setCreator(uid);//创建人
        return this.homeCaseFacade.addDesignScheme(designScheme);
    }


    /**
     * 添加案例中的信息
     * @param companyId
     * @param request
     * @param params
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/designScheme/{schemeId}/schemeContents")
    public  RequestResult addSchemeContent(@PathVariable String companyId,
                                           @PathVariable String schemeId,
                                           HttpServletRequest request,
                                           @RequestBody MapContext params){

        String contentName = params.getTypedValue("contentName", String.class);
        String contentNotes = params.getTypedValue("contentNotes", String.class);
        Integer contentIndex = params.getTypedValue("contentIndex", Integer.class);
        Integer type = params.getTypedValue("type", Integer.class);
        SchemeContent schemeContent = new SchemeContent();
        schemeContent.setSchemeId(schemeId);
        schemeContent.setContentIndex(contentIndex);
        schemeContent.setContentName(contentName);
        schemeContent.setContentNotes(contentNotes);
        return this.homeCaseFacade.addSchemeContent(schemeContent,type);
    }

    /**
     * 添加设计案例的图片（封面）
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/designschemes/{schemeId}/{type}")
    public RequestResult addSchemeFiles(@RequestBody List<MultipartFile> fileList,
                                        @PathVariable int type,
                                        HttpServletRequest request,
                                        @PathVariable String companyId,
                                        @PathVariable String schemeId){

        String uid = request.getHeader("X-UID");
        String cid = request.getHeader("X-CID");
        if (!cid.equals(companyId)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        Map<String, Object> errorInfo = new HashMap<>();
        if (null!=fileList&&fileList.size()>0){
            for (MultipartFile file:fileList){
                if (file == null) {
                    errorInfo.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
                } else if (!FileMimeTypeUtil.isLegalImageType(file)) {
                    errorInfo.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
                } else if (file.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
                    return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
                }
            }
        }else {
            errorInfo.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
        }
        if (errorInfo.size() > 0) {
            return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR, errorInfo);
        }
        return this.homeCaseFacade.addSchemeFiles(fileList,uid,type,schemeId);
    }

    /**
     * 商家编辑案例（只能编辑自己创建的案例）
     * @param schemeId
     * @param companyId
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/designScheme/{schemeId}")
    public RequestResult updateDesignScheme(@PathVariable String schemeId,
                                            @PathVariable String companyId,
                                            @RequestBody MapContext params,
                                            HttpServletRequest request){

        String cid = request.getHeader("X-CID");
        if (!cid.equals(companyId)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bschememng-shopplans-edit";
        RequestResult result = this.validUserPermission(request,xp);
        if (result!=null){
            return result;
        }

        return this.homeCaseFacade.updateDesignScheme(companyId, schemeId, params);
    }

    /**
     * 商家修改案例图片（包括富文本中的图片，封面图，等等图片）
     * @return
     */
    public RequestResult updateSchemeImage(List<MultipartFile> files){


        return null;
    }


    /**
     * 商家设置案例的状态（0 - 草稿；1 - 待审核；2 - 已发布;3 - 已下架）
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/designScheme/{schemeId}/stutus")
    public RequestResult updateSchemeStatus(@PathVariable String schemeId,
                                            @PathVariable String companyId,
                                            @PathVariable Integer status,
                                            HttpServletRequest request){

        String cid = request.getHeader("X-CID");
        if (!cid.equals(companyId)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bschememng-shopplans-update_status";
        RequestResult result = this.validUserPermission(request,xp);
        if (result!=null){
            return result;
        }
        return this.homeCaseFacade.updateSchemeStatus(companyId,schemeId,status);
    }

    /**
     * 商家删除设计案例（只能删除自己的设计案例）
     * @return
     */
    @DeleteMapping(value = "/companies/{companyId}/designScheme/{schemeId}")
    public RequestResult deleteDesignScheme(@PathVariable String schemeId,
                                            @PathVariable String companyId,
                                            HttpServletRequest request){
        String cid = request.getHeader("X-CID");
        if (!cid.equals(companyId)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        String xp = "bschememng-shopplans-delete";
        RequestResult result = this.validUserPermission(request,xp);
        if (result!=null){
            return result;
        }
        return  this.homeCaseFacade.deleteDesignScheme(schemeId, companyId);
    }

    /**
     * 查询设计师
     * @param quotations 价格（设计报价）max最大值， min最小值s
     * @param sort 排序 desc倒叙，asc正序
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/designers")
    public  String findAllDesigner(@RequestParam(required = false) String quotations,
                                   @RequestParam(required = false) String sort,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) Integer pageNum,
                                   @PathVariable String companyId){

        if (null==pageNum){
            pageNum = 1;
        }
        if (null==pageSize){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        Pagination pagination = new Pagination();
        pagination.setPageNum(pageNum);
        pagination.setPageSize(pageSize);
        RequestResult allDesigner = this.homeCaseFacade.findAllDesigner(quotations, sort, pagination, companyId);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(allDesigner);
    }


    /**
     * 查询设计师的详情页面
     * @param companyId
     * @param designer
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/designers/{designer}")
    public String findDesignByDesigner(@PathVariable String companyId,
                                       @PathVariable String designer){

        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.homeCaseFacade.findDesignByDesigner(designer, companyId);
        return mapper.toJson(result);
    }


    /**
     * 查询案例的上架的、下架的、草稿的、删除的案例列表
     * @param pageNum
     * @param pageSize
     * @param companyId
     * @param status
     * @param request
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/homecases/{status}")
    public RequestResult schemeStatusList(@RequestParam(required = false) Integer pageNum,
                                          @RequestParam(required = false) Integer pageSize,
                                          @PathVariable String companyId,
                                          @PathVariable Integer status,
                                           HttpServletRequest request) {

        if(null == pageSize){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if(null == pageNum){
            pageNum = 1;
        }
        MapContext filters = MapContext.newOne();
        if (LwxfStringUtils.isNotBlank(companyId)) {
            filters.put("companyId", companyId);
        }
        boolean contains = SchemeStatus.contains(status);
        if (!contains){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        filters.put(WebConstant.KEY_ENTITY_STATUS, status);
        RequestResult result = this.homeCaseFacade.selectByCondition(pageNum, pageSize, filters, request);
        return result;

    }


    /**
     *
     * 修改案例的状态
     * @param companyId
     * @param status
     * @param request
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/homecases/{status}")
    public RequestResult updateSchemeStatus(@PathVariable String companyId,
                                            @PathVariable Integer status,
                                            @RequestParam String[] schemeIds,
                                            HttpServletRequest request){

        RequestResult result = this.homeCaseFacade.updateSchemeStatus(companyId,status,schemeIds);
        return result;
    }



}


