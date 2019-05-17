package com.lwxf.industry4.webapp.controller.app.dealer.activity;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.domain.entity.activity.ActivityInfo;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.activity.ActivityFacade;
import com.lwxf.industry4.webapp.facade.user.UserFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/2/19 10:27
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class ActivityController extends BaseDealerController {
    @Resource(name = "activityFacade")
    private ActivityFacade activityFacade;
    @Resource(name = "userFacade")
    private UserFacade userFacade;

    /**
     * 查询经销商的参加厂家的活动和自己的活动
     *
     * @param companyId
     * @return
     */
    @GetMapping(value = "companies/{companyId}/activities")
    public String findAllActivity(@PathVariable String companyId,
                                         @RequestParam(required = false) Integer pageSize,
                                         @RequestParam(required = false) Integer pageNum,
                                         HttpServletRequest request) {


        JsonMapper jsonMapper = JsonMapper.createAllToStringMapper();
        if (LwxfStringUtils.isBlank(companyId)){
            return jsonMapper.toJson(ResultFactory.generateResNotFoundResult());
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        MapContext params = MapContext.newOne();
        params.put("companyId", companyId);
        Pagination pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        RequestResult allActivity = this.activityFacade.findBAndFActivity(pagination, params);
        return jsonMapper.toJson(allActivity);
    }


    /**
     * 查询商家活动的详情（工厂的活动详情也可以用）
     *
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/activities/{activityId}")
    public String findByActivityId(@PathVariable String companyId,
                                   @PathVariable String activityId,
                                   HttpServletRequest request) {
        String userId = request.getHeader("X-UID");
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.activityFacade.findByActivityId(activityId,userId);
        return mapper.toJson(result);
    }

    /**
     *  异步查询用户的电话是否存在（用户是否在系统中，如果在返回他的信息）
     * @return
     */
    @GetMapping(value = "/users/0/mobile/{mobile}")
    public String findByMobile(@PathVariable String mobile){
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        User user = this.userFacade.findByMobile(mobile);
        String name = "";
        if (null!=user){
           name = user.getName();
        }
        MapContext params  = MapContext.newOne();
        params.put("name",name);
        params.put("mobile",mobile);
        RequestResult result = ResultFactory.generateRequestResult(params);
        return mapper.toJson(result);
    }



    /**
     * C端参加活动（如果不存在，添加到系统，如果存在拿到用户的id）
     * @param companyId
     * @param activityId
     * @param request
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/activities/{activityId}/activityJoins")
    public RequestResult addActivityJoin(@PathVariable String companyId,
                                         @PathVariable String activityId,
                                         HttpServletRequest request,
                                         @RequestBody MapContext params){
        return this.activityFacade.addActivityJoin(companyId,activityId,params,request);
    }


    /**
     * 经销商参加活动
     * @param companyId
     * @param activityId
     * @param request
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/activities/{activityId}/bjoin")
    public RequestResult BJoinFActivity(@PathVariable String companyId,
                                        @PathVariable String activityId,
                                        HttpServletRequest request){

        String userId = request.getHeader("X-UID");
        String cid = request.getHeader("X-CID");
        RequestResult result = this.activityFacade.BJoinFActivity(cid, activityId, userId);
        return result;
    }


    /**
     * 参与活动记录
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/activities/{activityId}/activityJoins")
    public String findJoinRecord(@PathVariable String companyId,
                                 @PathVariable String activityId,
                                 HttpServletRequest request){
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        MapContext parmas = MapContext.newOne();
        parmas.put("companyId",companyId);
        parmas.put("activityId",activityId);
        parmas.put("type",1);
        RequestResult joinRecord = activityFacade.findJoinRecord(parmas);
        return mapper.toJson(joinRecord);
    }


    /**
     * 商家添加活动
     *
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/activities")
    public RequestResult addActivity(@PathVariable String companyId,
                                     HttpServletRequest request,
                                     @RequestBody MapContext mapContext) {
        ActivityInfo activityInfo = new ActivityInfo();
        //将字段强转
        String uid = request.getHeader("X-UID");
        String name = (String) mapContext.get("name");//标题
        String cover = (String) mapContext.get("cover");//封面
        String summary = (String) mapContext.get("summary");//活动描述
        String content = (String) mapContext.get("content");//海报的路径或者站外的路径
        //String conditions = (String) mapContext.get("conditions");//参与条件
        String regulation = (String) mapContext.get("regulation");//活动规则
        Integer classify = Integer.valueOf((String) mapContext.get("classify"));
        String link = (String) mapContext.get("link");
        if (LwxfStringUtils.isNotBlank((String)mapContext.get("inner"))){
            Integer inner = Integer.valueOf((String) mapContext.get("inner"));
            activityInfo.setInner(inner);
        }
        String begin = (String) mapContext.get("beginTime");
        if (LwxfStringUtils.isNotBlank((String)mapContext.get("endTime"))){
            String end = ((String) mapContext.get("endTime"));
            Date endTime = DateUtil.stringToDate(end, "yyyy-MM-dd");
            activityInfo.setEndTime(endTime);
        }
        Date beginTime = DateUtil.stringToDate(begin, "yyyy-MM-dd");

        //赋值

        activityInfo.setCreator(uid);
        activityInfo.setCompanyId(companyId);
        activityInfo.setName(name);
        activityInfo.setClassify(classify);
        activityInfo.setCover(cover);
        //activityInfo.setConditions(conditions);
        activityInfo.setContent(content);
        activityInfo.setSummary(summary);
        activityInfo.setRegulation(regulation);
        activityInfo.setLink(link);
        activityInfo.setBeginTime(beginTime);
        RequestResult requestResult = this.activityFacade.addCompanyActivity(activityInfo);
        return requestResult;
    }


    /**
     * 添加封面/内容中的图片
     * @param multipartFile
     * @param request
     * @param activityId
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/activities/{activityId}/{type}")
    public RequestResult addCover(@RequestBody MultipartFile multipartFile,
                                  HttpServletRequest request,
                                  @PathVariable String activityId,
                                  @PathVariable String type){

        String cid = request.getHeader("X-CID");
        String uid = request.getHeader("X-UID");
        Map<String, Object> errorInfo = new HashMap<>();
            if (multipartFile==null){
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            }
            if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
            }
            if (multipartFile.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
                return ResultFactory.generateErrorResult(ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
            }
        if (errorInfo.size() > 0) {
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
        }

        return this.activityFacade.addCover(multipartFile,cid,activityId,uid,type);
    }




    /**
     * 商家编辑活动（只能修改自己的创建的活动）
     *
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/activities/{activityId}")
    public RequestResult updateActivity(@PathVariable String companyId,
                                        @PathVariable String activityId,
                                        @RequestBody MapContext parmas,
                                        HttpServletRequest request) {
        return this.activityFacade.updateActivity(companyId,activityId,parmas);
    }



    /**
     * 修改活动参数
     * @param companyId
     * @param activityId
     * @param map
     * @param request
     * @param paramsId
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/activities/{activityId}/activityParams/{paramsId}")
    public RequestResult updateActivityParams(@PathVariable String companyId,
                                              @PathVariable String activityId,
                                              @RequestBody MapContext map,
                                              HttpServletRequest request,
                                              @PathVariable String paramsId){

        return this.activityFacade.updateActivityParams(companyId,activityId,map,paramsId);
    }


    /**
     * 修改活动封面 或 内容中的图片
     * @param companyId
     * @param activityId
     * @param file
     * @param request
     * @param type
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/activities/{activityId}/{type}")
    public RequestResult updateCover(@PathVariable String companyId,
                                     @PathVariable String activityId,
                                     @RequestBody MultipartFile file,
                                     HttpServletRequest request,
                                     @PathVariable String type,
                                     @RequestParam String path){
        return this.activityFacade.updateCover(file,activityId,companyId,type,path);
    }



    /**
     * 设置商家活动状态
     *
     * @return
     */
    @PutMapping(value = "/companies/{companyId}/activities/{activityId}/status/{status}")
    public RequestResult putActivityStatus(@PathVariable String companyId,
                                           @PathVariable String activityId,
                                           @PathVariable Integer status,
                                           HttpServletRequest request) {

        MapContext params = MapContext.newOne();
        params.put("id",activityId);
        params.put("status",status);
        return this.activityFacade.putActivityStatus(params);
    }




    /**
     * 删除商家活动（删除自己的）暂时不做
     *
     * @return
     */
    @DeleteMapping(value = "/companies/{companyId}/activities/{activityId}")
    public RequestResult deleteByActivityId(@PathVariable String companyId,
                                            @PathVariable String activityId,
                                            HttpServletRequest request) {

        return null;
    }


    /**
     * 查询经销商的活动
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/b/activities")
    public String findActivityByCompanyId(@PathVariable String companyId,
                                                 @RequestParam(required = false) Integer pageSize,
                                                 @RequestParam(required = false) Integer pageNum,
                                                 @RequestParam Integer status){

        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        Pagination pagination = new Pagination();
        pagination.setPageNum(pageNum);
        pagination.setPageSize(pageSize);
        MapContext params = MapContext.newOne();
        params.put("status", status);
        params.put("companyId",companyId);
        params.put("type",1);
        RequestResult result = this.activityFacade.findByCompanyId(pagination,params);
        return mapper.toJson(result);
    }

    /**
     * 查询工厂端的活动
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/f/activities")
    public String findActivityByFCompanyId(@RequestParam(required = false) Integer pageSize,
                                           @RequestParam(required = false) Integer pageNum,
                                           @RequestParam Integer type,
                                           @PathVariable String companyId){
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        Pagination pagination = new Pagination();
        pagination.setPageNum(pageNum);
        pagination.setPageSize(pageSize);
        MapContext params = MapContext.newOne();
        params.put("companyId",companyId);
        RequestResult result = this.activityFacade.findActivityByFCompanyId(pagination, params, type, companyId);
        return mapper.toJson(result);
    }




}

