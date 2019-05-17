package com.lwxf.industry4.webapp.facade.app.dealer.activity.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.activity.ActivityFilesService;
import com.lwxf.industry4.webapp.bizservice.activity.ActivityInfoService;
import com.lwxf.industry4.webapp.bizservice.activity.ActivityJoinService;
import com.lwxf.industry4.webapp.bizservice.activity.ActivityParamsService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.activity.ActivityStatus;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.activity.ActivityFiles;
import com.lwxf.industry4.webapp.domain.entity.activity.ActivityInfo;
import com.lwxf.industry4.webapp.domain.entity.activity.ActivityJoin;
import com.lwxf.industry4.webapp.domain.entity.activity.ActivityParams;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.activity.ActivityFacade;
import com.lwxf.industry4.webapp.facade.app.dealer.customer.CustomerFacade;
import com.lwxf.industry4.webapp.facade.user.UserFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/8 11:51
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("activityFacade")
public class ActivityFacadeImpl extends BaseFacadeImpl implements ActivityFacade {

    @Resource(name = "activityInfoService")
    private ActivityInfoService activityInfoService;
    @Resource(name = "activityParamsService")
    private ActivityParamsService activityParamsService;
    @Resource(name = "activityFilesService")
    private ActivityFilesService activityFilesService;
    @Resource(name = "customerFacade")
    private CustomerFacade customerFacade;
    @Resource(name = "userFacade")
    private UserFacade userFacade;
    @Resource(name = "activityJoinService")
    private ActivityJoinService activityJoinService;


    /**
     * 暂时无用
     * @param pagination
     * @param params
     * @return
     */
    @Override
    public RequestResult findAllActivity(Pagination pagination, MapContext params) {
        PaginatedFilter filter = new PaginatedFilter();
        filter.setFilters(params);
        filter.setPagination(pagination);
        List<Map<String, String>> sorts = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<>();
        map.put("status", "asc");
        map.put("created", "desc");
        sorts.add(map);
        filter.setSorts(sorts);
        this.activityInfoService.findIdAndCover(filter);

        return null;
    }


    /**
     * 查询经销商参加工厂的活动和自己的活动
     *
     * @param pagination
     * @param params
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult findBAndFActivity(Pagination pagination, MapContext params) {
        PaginatedFilter filter = new PaginatedFilter();
        filter.setFilters(params);
        filter.setPagination(pagination);
        List<Map<String, String>> bAndFActivity = this.activityInfoService.findBAndFActivity(filter).getRows();
        List<Map<String, String>> listMap = new ArrayList<>();
        for (Map activity : bAndFActivity) {
            Map<String, String> timeAndCover = new HashMap<>();
            String endTime = activity.get("end_time").toString();//结束时间
            String status = activity.get("status").toString();
            Date systemDate = DateUtil.getSystemDate();//系统时间
            //String类型的系统时间
            String systemDateString = DateUtil.dateTimeToString(systemDate, "yyyy-MM-dd");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d1 = dateFormat.parse(endTime);
                Date d2 = dateFormat.parse(systemDateString);
              if (d1.getTime()<d2.getTime()){
                  timeAndCover.put("end_time","已结束");
                  if (!status.equals("2")){
                      MapContext activityMap = MapContext.newOne();
                      activityMap.put("id", activity.get("id"));
                      activityMap.put("status", 2);
                      this.activityInfoService.updateByMapContext(activityMap);
                  }
              }if (d1.getTime()>=d2.getTime()){
                    timeAndCover.put("end_time",endTime);
              }
            } catch (ParseException e) {
                e.printStackTrace();

            }
            String cover = activity.get("cover").toString();
            String id = activity.get("id").toString();
            timeAndCover.put("cover",cover);
            timeAndCover.put("id",id);
            listMap.add(timeAndCover);
        }
        return ResultFactory.generateRequestResult(listMap);
    }

    /**
     * 经销商添加活动
     *
     * @param activityInfo
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addCompanyActivity(ActivityInfo activityInfo) {
        activityInfo.setCreated(DateUtil.getSystemDate());
        //activityInfo.setClassify(1);
        activityInfo.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.ACTIVITY_NO));
        activityInfo.setViews(0);
        activityInfo.setStatus(0);//状态 （0 未发布，1已发布，2活动结束）
        activityInfo.setType(1);//创建人类型 0厂家、1经销商
        activityInfo.setTarget(1);//面向顾客 0 经销商 1终端
        RequestResult result = activityInfo.validateFields();
        if (null != result) {
            return result;
        }
        this.activityInfoService.add(activityInfo);
        return ResultFactory.generateRequestResult(activityInfo);
    }

    /**
     * 活动详情
     *
     * @param activityId
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult findByActivityId(String activityId,String userId) {
        MapContext result = MapContext.newOne();
        ActivityInfo activityInfo = this.activityInfoService.findById(activityId);
        List<ActivityParams> activityParamsList = this.activityParamsService.findByActivityId(activityId);

        String id = activityInfo.getId();
        String companyId = activityInfo.getCompanyId();
        MapContext params = MapContext.newOne();
        params.put("activityId", id);
        params.put("companyId", companyId);
        params.put("userId", userId);
        ActivityJoin activityJoin = this.activityJoinService.findOneByCidAndUidAndAIdAndType(params);
        if(activityJoin!=null){
            activityInfo.setIsJoins("1");
        }else {
            activityInfo.setIsJoins("0");
        }
        result.put("activityInfo", activityInfo);
        result.put("activityParamsList", activityParamsList);
        return ResultFactory.generateRequestResult(result);
    }

    /**
     * 修改活动的状态（0 未发布 1已发布  2已结束）
     *
     * @param params
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult putActivityStatus(MapContext params) {
        RequestResult requestResult = ActivityInfo.validateFields(params);
        if (null != requestResult) {
            return requestResult;
        }
        this.activityInfoService.updateByMapContext(params);
        return ResultFactory.generateRequestResult(params);
    }

    /**
     * 添加封面/内容中的图片
     *
     * @param multipartFile
     * @param companyId
     * @param activityId
     * @param uid
     * @param type
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addCover(MultipartFile multipartFile, String companyId, String activityId, String uid, String type) {
        //添加图片到本地
        UploadInfo uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, multipartFile, UploadResourceType.ACTIVITY, companyId, activityId, type);
        //添加图片到数据库 --开始--
        ActivityFiles activityFiles = new ActivityFiles();
        activityFiles.setActivityId(activityId);
        activityFiles.setCreated(DateUtil.getSystemDate());
        activityFiles.setCreator(uid);
        activityFiles.setFullPath(WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
        activityFiles.setMime(uploadInfo.getFileMimeType().getRealType());
        activityFiles.setName(uploadInfo.getFileName());
        activityFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
        activityFiles.setPath(uploadInfo.getRelativePath());
        activityFiles.setStatus(1);
        activityFiles.setType(0);
        this.activityFilesService.add(activityFiles);
        //添加图片到数据库 --结束--

        MapContext map = MapContext.newOne();
        map.put("id",activityId);
        //修改活动信息表的封面图
        if (type.equals("cover")) {
            map.put("cover",uploadInfo.getRelativePath());
            this.activityInfoService.updateByMapContext(map);
            return ResultFactory.generateRequestResult(activityFiles.getPath());
        }
        map.put("content",uploadInfo.getRelativePath());
        this.activityInfoService.updateByMapContext(map);
        return ResultFactory.generateRequestResult(activityFiles.getFullPath());
    }


    /**
     * 修改活动信息
     *
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updateActivity(String companyId, String activityId, MapContext params) {

        ActivityInfo activityInfo = this.activityInfoService.findById(activityId);
        if (null == activityInfo || !companyId.equals(activityInfo.getCompanyId())) {
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        if (activityInfo.getStatus()!=0){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        RequestResult result = ActivityInfo.validateFields(params);
        if (null != result) {
            return result;
        }
        params.put(WebConstant.KEY_ENTITY_ID,activityId);
        this.activityInfoService.updateByMapContext(params);
        return ResultFactory.generateRequestResult(params);
    }


    /**
     *  修改活动的参数
     * @param companyId
     * @param activityId
     * @param map
     * @param paramsId
     * @return
     */
    @Override
    public RequestResult updateActivityParams(String companyId,
                                              String activityId,
                                              MapContext map,
                                              String paramsId) {
        ActivityInfo activityInfo = this.activityInfoService.findById(activityId);
        if (null == activityInfo || !companyId.equals(activityInfo.getCompanyId())) {
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        if (activityInfo.getStatus()!=0){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        RequestResult result = ActivityParams.validateFields(map);
        if (null != result) {
            return result;
        }
        map.put(WebConstant.KEY_ENTITY_ID,paramsId);
        this.activityInfoService.updateByMapContext(map);
        return ResultFactory.generateRequestResult(map);
    }

    /**
     * 用户参加活动
     * @param companyId
     * @param activityId
     * @param parmas
     * @param request
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addActivityJoin(String companyId, String activityId,
                                         MapContext parmas, HttpServletRequest request) {


        String uid = request.getHeader("X-UID");
        String cid = request.getHeader("X-CID");
        String mobile = parmas.getTypedValue("mobile", String.class);
        String name = parmas.getTypedValue("name", String.class);
        this.customerFacade.addCustomer(companyId, parmas, uid, cid);
        User user = this.userFacade.findByMobile(mobile);
        if (null==user){
            return ResultFactory.generateErrorResult(ErrorCodes.SYS_EXECUTE_FAIL_00001,AppBeanInjector.i18nUtil.getMessage("SYS_EXECUTE_FAIL_00001"));
        }
        ActivityInfo activityInfo = this.activityInfoService.findById(activityId);
        if (activityInfo==null||activityInfo.getStatus()==ActivityStatus.END.getValue()){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        //当前人参与的id
        String userId = user.getId();
        MapContext map = MapContext.newOne();
        map.put("companyId",companyId);
        map.put("userId",userId);
        map.put("activityId",activityId);
        map.put("type",1);
        ActivityJoin join = this.activityJoinService.findOneByCidAndUidAndAIdAndType(map);
        if (null!=join){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_USER_HAS_BEEN_ADDED_10056,AppBeanInjector.i18nUtil.getMessage("BIZ_USER_HAS_BEEN_ADDED_10056"));
        }

        ActivityJoin activityJoin = new ActivityJoin();
        activityJoin.setActivityId(activityId);
        activityJoin.setCompanyId(companyId);
        activityJoin.setCreated(DateUtil.getSystemDate());
        activityJoin.setCreator(uid);
        activityJoin.setUserId(userId);
        activityJoin.setType(1);

        RequestResult requestResult = activityJoin.validateFields();
        if (null!=requestResult){
            return requestResult;
        }
        this.activityJoinService.add(activityJoin);
        return ResultFactory.generateRequestResult(activityJoin);
    }


    /**
     * 参与活动记录
     * @return
     */
    @Override
    public RequestResult findJoinRecord(MapContext params) {
        List<Map> joinList = this.activityJoinService.findListByCidAndUidAndAIdAndType(params);
        for (int i = 0;i<joinList.size();i++){
            Map map = joinList.get(i);
            if (map==null||map.size()==0){
                return ResultFactory.generateRequestResult(joinList);
            }
            map.put("index",i+1);
        }
        return ResultFactory.generateRequestResult(joinList);
    }


    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updateCover(MultipartFile multipartFile,String activityId,String companyId,String type,String path) {
        MapContext parmas = MapContext.newOne();
        parmas.put("activityId",activityId);
        parmas.put("path",path);
        UploadInfo uploadInfo = null;
        ActivityFiles activityFiles = null;
        ActivityInfo activityInfo = this.activityInfoService.findById(activityId);
        if (activityInfo == null) {
            return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_RES_NOT_FOUND_10001, AppBeanInjector.i18nUtil.getMessage("BIZ_RES_NOT_FOUND_10001"));
        }

        if(null!=type&&type.equals("content")){//type是content是修改内容中的图片
            uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL,multipartFile,UploadResourceType.ACTIVITY,companyId,activityId,type);
            activityFiles = this.activityFilesService.findByActivityAndPath(parmas);
        }else if (null!=type&&type.equals("cover")){//type是cover是修改封面
            uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL,multipartFile,UploadResourceType.ACTIVITY,companyId,activityId,type);
            activityFiles = this.activityFilesService.findByActivityAndPath(parmas);
        }

            AppBeanInjector.baseFileUploadComponent.deleteFileByDir(AppBeanInjector.configuration.getUploadFileRootDir().concat(activityFiles.getPath()));
            MapContext update = MapContext.newOne();
            update.put(WebConstant.KEY_ENTITY_ID,activityFiles.getId());
            update.put("path",uploadInfo.getRelativePath());
            update.put("name",uploadInfo.getFileName());
            update.put("originalMime",uploadInfo.getFileMimeType().getOriginalType());
            update.put("mime",uploadInfo.getFileMimeType().getRealType());
            update.put("creator",WebUtils.getCurrUserId());
            update.put("created",DateUtil.getSystemDate());
            update.put("full_path",WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
            this.activityFilesService.updateByMapContext(update);

        MapContext mapContext = new MapContext();
        if(null!=type&&type.equals("content")) {
            String oldContent = activityInfo.getContent();
            String newContent = oldContent.replace(WebUtils.getDomainUrl() + activityFiles.getPath(),WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
            mapContext.put("content", newContent);
        }else {
            mapContext.put("cover", uploadInfo.getRelativePath());
        }

        mapContext.put(WebConstant.KEY_ENTITY_ID,activityId);
        this.activityInfoService.updateByMapContext(mapContext);
        Map map = new HashMap();
        map.put("file",uploadInfo.getRelativePath());
        return ResultFactory.generateRequestResult(map);
    }


    @Override
    public RequestResult findByCompanyId(Pagination pagination,MapContext params) {
        PaginatedFilter filter = new PaginatedFilter();
        filter.setPagination(pagination);
        filter.setFilters(params);
        PaginatedList<Map> activityList = this.activityInfoService.findByCompanyId(filter);
        List<Map> rows = activityList.getRows();
        List<Map<String, String>> listMap = new ArrayList<>();
        for (Map activity : rows) {
            Map<String, String> timeAndCover = new HashMap<>();
            String end_time =  activity.get("end_time").toString();
            String status = activity.get("status").toString();
            if (status.equals("0")){
                timeAndCover.put("end_time", "");
            }
            if (status.equals("1")){
                timeAndCover.put("end_time", end_time);
            }
            if (status.equals("2")){
                timeAndCover.put("end_time", "已结束");
            }
            Object cover = activity.get("cover");
            if (cover==null){
                timeAndCover.put("cover","");
            }else {
                String s = cover.toString();
                timeAndCover.put("cover",s);
            }
            String id = activity.get("id").toString();
            timeAndCover.put("id",id);
            listMap.add(timeAndCover);
        }
        return ResultFactory.generateRequestResult(listMap);
    }


    @Override
    @Transactional(value = "transactionManager")
    public RequestResult findActivityByFCompanyId(Pagination pagination,MapContext params,Integer type,String companyId) {
        List<Map<String, String>> listMap = new ArrayList<>();
        PaginatedFilter filter = new PaginatedFilter();
        filter.setPagination(pagination);
        //查询工厂的活动
        if (type==0) {
            MapContext filters = MapContext.newOne();
            filters.put("BcompanyId", companyId);
            filters.put("FcompanyId", WebUtils.getCurrCompanyId());
            filter.setFilters(filters);
            List<Map> rows = this.activityInfoService.findFActivity(filter).getRows();
            for (Map map:rows){
                Map<String, String> timeAndCover = new HashMap<>();
                String status = map.get("status").toString();
                String isJoin = map.get("is_join").toString();
                String end_time = map.get("end_time").toString();//结束时间

                Date systemDate = DateUtil.getSystemDate();//系统时间
                //String类型的系统时间
                String systemDateString = DateUtil.dateTimeToString(systemDate, "yyyy-MM-dd");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date d1 = dateFormat.parse(end_time);
                    Date d2 = dateFormat.parse(systemDateString);
                    if (isJoin.equals("1")&&status.equals("1")){
                        timeAndCover.put("end_time","我已参加");
                    }else if (isJoin.equals("0")&&status.equals("1")){
                        timeAndCover.put("end_time",end_time);
                    }if (d1.getTime()<d2.getTime()){
                        timeAndCover.put("end_time","已结束");
                        if (!status.equals("2")){
                            MapContext activityMap = MapContext.newOne();
                            activityMap.put("id", map.get("id"));
                            activityMap.put("status", 2);
                            this.activityInfoService.updateByMapContext(activityMap);
                        }
                    }
                    String id = map.get("id").toString();
                    String cover = map.get("cover").toString();
                    timeAndCover.put("cover",cover);
                    timeAndCover.put("id",id);
                    listMap.add(timeAndCover);
                } catch (ParseException e) {
                    e.printStackTrace();

                }
            }
            return ResultFactory.generateRequestResult(listMap);
        }
        //查询经销商参加的活动
        if (type==1){
            filter.setFilters(params);
            List<Map> rows = this.activityInfoService.findBJoinActivity(filter).getRows();

            for (Map map:rows){
                Map<String, String> timeAndCover = new HashMap<>();
                String status = map.get("status").toString();
                if (status.equals("2")){
                    timeAndCover.put("end_time", "已结束");
                }
                if (status.equals("1")){
                    timeAndCover.put("end_time","我已参加");
                }
                String id = map.get("id").toString();
                String cover = map.get("cover").toString();
                timeAndCover.put("cover",cover);
                timeAndCover.put("id",id);
                listMap.add(timeAndCover);
            }
            return ResultFactory.generateRequestResult(listMap);
        }
        return ResultFactory.generateResNotFoundResult();


    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult BJoinFActivity(String companyId, String activityId,String userId) {
        MapContext params = MapContext.newOne();
        ActivityInfo activityInfo = this.activityInfoService.findById(activityId);
        if (activityInfo==null||activityInfo.getStatus()==ActivityStatus.END.getValue()){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }

        params.put("companyId", companyId);
        params.put("activityId", activityId);
        params.put("type", 0);
        ActivityJoin join = this.activityJoinService.findOneByCidAndUidAndAIdAndType(params);
        MapContext map = MapContext.newOne();
        if (join!=null){
            map.put("value", "您已参加！");
            return ResultFactory.generateRequestResult(map);
        }
        ActivityJoin activityJoin = new ActivityJoin();
        activityJoin.setCompanyId(companyId);
        activityJoin.setCreator(userId);
        activityJoin.setCreated(DateUtil.getSystemDate());
        activityJoin.setType(0);
        activityJoin.setActivityId(activityId);
        activityJoin.setFree(1);
        activityJoin.setPaid(0);
        activityJoin.setAmount(new BigDecimal(0));

        RequestResult result = activityJoin.validateFields();
        if (result!=null){
            return result;
        }
        this.activityJoinService.add(activityJoin);
        map.put("value", "参加成功");
        return ResultFactory.generateRequestResult(map);
    }
}
