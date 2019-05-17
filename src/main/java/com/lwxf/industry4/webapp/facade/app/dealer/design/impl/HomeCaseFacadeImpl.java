package com.lwxf.industry4.webapp.facade.app.dealer.design.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.company.CompanyShareMemberService;
import com.lwxf.industry4.webapp.bizservice.design.*;
import com.lwxf.industry4.webapp.bizservice.user.UserAttentionService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.scheme.SchemeStatus;
import com.lwxf.industry4.webapp.common.enums.user.UserAttentionType;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.design.DesignSchemeDto;
import com.lwxf.industry4.webapp.domain.dto.design.SchemeContentDto;
import com.lwxf.industry4.webapp.domain.entity.design.*;
import com.lwxf.industry4.webapp.domain.entity.user.UserAttention;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.design.HomeCaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/1 14:50
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("homeCaseFacade")
public class HomeCaseFacadeImpl extends BaseFacadeImpl implements HomeCaseFacade {
    @Resource(name = "designSchemeService")
    private DesignSchemeService designSchemeService;
    @Resource(name = "userAttentionService")
    private UserAttentionService userAttentionService;
    @Resource(name = "designStyleService")
    private DesignStyleService designStyleService;
    @Resource(name = "designSchemeFilesService")
    private DesignSchemeFilesService designSchemeFilesService;
    @Resource(name = "companyShareMemberService")
    private CompanyShareMemberService companyShareMemberService;
    @Resource(name = "doorStateService")
    private DoorStateService doorStateService;
    @Resource(name = "schemeContentService")
    private SchemeContentService schemeContentService;


    /**
     * 根据条件分页查询首页案例
     *
     * @param pageNum
     * @param pageSize
     * @param filters
     * @return
     */
    @Override
    public RequestResult selectByCondition(Integer pageNum, Integer pageSize, MapContext filters,HttpServletRequest request) {
        String userId = request.getHeader("X-UID");
        String aToken = request.getHeader("X-ATOKEN");


        PaginatedFilter filter = PaginatedFilter.newOne();
        Pagination pagination = Pagination.newOne();
        pagination.setPageNum(pageNum);
        pagination.setPageSize(pageSize);
        filter.setPagination(pagination);
        filter.setFilters(filters);
        PaginatedList<DesignSchemeDto> pagelist = this.designSchemeService.selectByCondition(filter);




        List<DesignSchemeDto> designSchemeList = pagelist.getRows();
        if (null == designSchemeList || designSchemeList.size() == 0) {
            return ResultFactory.generateRequestResult(designSchemeList);
        }
        for (int i =0;i<designSchemeList.size();i++) {
            String designer = designSchemeList.get(i).getDesigner();
            Integer count = this.designSchemeService.findCountByDesigner(designer);
            Integer countByResIdAndType = this.userAttentionService.findCountByResIdAndType(designer, UserAttentionType.MEMBER.getValue());
            designSchemeList.get(i).setScheme_count(count);
            designSchemeList.get(i).setDesigner_count(countByResIdAndType);
            designSchemeList.get(i).setSharePath("/app/b/homecases/"+designSchemeList.get(i).getId());
        }

        //如果用户已登录 则判断用户是否关注这些案例 未登录则关注字段全为false
        boolean isLogin = LoginUtil.isLogin(userId, aToken);
        if(isLogin){
            //查询用户关注列表 如果用户关注量为 0 则返回 null 或者size 为0
            Map<String,Long> attentionList = this.userAttentionService.isExistByUserIdAndResIds(userId, designSchemeList,"id");
            //用户未关注全部为fasle
            if (attentionList==null||attentionList.size()==0){
                for (int i =0;i<designSchemeList.size();i++) {
                    designSchemeList.get(i).setCollect(false);
                }
            }else{
                for (int i =0;i<designSchemeList.size();i++) {
                    String id = designSchemeList.get(i).getId();
                    Boolean aBoolean = (attentionList.get(id).toString()).equals("0")?false:true;
                    designSchemeList.get(i).setCollect(aBoolean);
                }
            }
        }else{//用户未登录 则关注字段全部为 false
            for (int i =0;i<designSchemeList.size();i++) {
                designSchemeList.get(i).setCollect(false);
            }
        }
        return ResultFactory.generateRequestResult(pagelist);
    }



    /**
     * 根据id查询首页案例详情
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult findById(String id) {
        MapContext result = MapContext.newOne();
        Map schemeMap = this.designSchemeService.selectBySchemeId(id);
        if (null == schemeMap) {
            return ResultFactory.generateRequestResult(result);
        }
        //查询案例的内容
        List<SchemeContentDto> contentList = this.schemeContentService.findBySchemeId(id);
        //如果不为null，查询出内容中的图片，并赋值；如果为空则不显示
        if (null!=contentList&&contentList.size()>0){
            for (SchemeContentDto scdto:contentList){
                String resourceId = scdto.getId();
                List<String> paths = this.designSchemeFilesService.findByResouceId(resourceId);
                scdto.setPaths(paths);
            }
        }
        Integer pageView = Integer.valueOf(schemeMap.get("pageView").toString());
        pageView = pageView + 1;
        MapContext params = MapContext.newOne();
        params.put("id", id);
        params.put("pageView", pageView);
        this.designSchemeService.updateByMapContext(params);
        result.put("schemeMap",schemeMap);
        result.put("contentList",contentList);
        return ResultFactory.generateRequestResult(result);

    }



    /**
     * 关注、取消关注
     * @param userId
     * @param resourceId
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addSchemeAttention(String userId, String resourceId) {
        DesignScheme designScheme = this.designSchemeService.findById(resourceId);
        if (null == designScheme) {
            return ResultFactory.generateRequestResult(designScheme);
        }
        Integer attention = designScheme.getAttention();//得到关注数
        MapContext map = MapContext.newOne();
        map.put("userId", userId);
        map.put("resourceId", resourceId);
        boolean exist = this.userAttentionService.isExistByUserIdAndResourceId(userId, resourceId);
        if (exist) {
            int isSucc = this.userAttentionService.deleteByUserIdAndResourceId(map);
            if (isSucc < 1) {
                return ResultFactory.generateErrorResult(ErrorCodes.BIZ_DELETE_FAILURE_10073, "取消关注失败");
            }
            attention = attention - 1;
        } else {
            UserAttention userAttention = new UserAttention();
            userAttention.setUserId(userId);
            userAttention.setResourceId(resourceId);
            userAttention.setCreated(DateUtil.getSystemDate());
            userAttention.setResourceType(UserAttentionType.SCHEME.getValue());
            int i = this.userAttentionService.add(userAttention);
            if (i < 1) {
                return ResultFactory.generateErrorResult(ErrorCodes.BIZ_ADD_FAILURE_10072, "关注失败");
            }
            attention = attention + 1;
        }
        MapContext params = MapContext.newOne();
        params.put("id", resourceId);
        params.put("attention", attention);
        this.designSchemeService.updateByMapContext(params);
        MapContext data = MapContext.newOne();
        return ResultFactory.generateRequestResult(data);
    }

    /**
     * 分享
     * @param id
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addShare(String id) {
        DesignScheme designScheme = this.designSchemeService.findById(id);
        if (null == designScheme) {
            return ResultFactory.generateRequestResult(designScheme);
        }
        Integer share = designScheme.getShare();
        share = share + 1;
        MapContext params = MapContext.newOne();
        params.put("id", id);
        params.put("share", share);
        this.designSchemeService.updateByMapContext(params);
        MapContext data = MapContext.newOne();
        return ResultFactory.generateRequestResult(data);
    }

    /**
     * 查询所有的设计样式和户型
     * @return
     */
    @Override
    public RequestResult findDesignStyleAndDoorState() {
        List<DesignStyle> allDesignStyle = this.designStyleService.findAllDesignStyle();
        List<DoorState> allDoorState = this.doorStateService.findAllDoorState();
        MapContext map = MapContext.newOne();
        map.put("allDesignStyle",allDesignStyle);
        map.put("allDoorState",allDoorState);
        return ResultFactory.generateRequestResult(map);
    }


    /**
     * 经销商添加商家案例
     * @param designScheme
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addDesignScheme(DesignScheme designScheme) {
        designScheme.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.DESIGN_SCHEME_NO));//编号
        designScheme.setCreated(DateUtil.getSystemDate());//时间
        designScheme.setAttention(0);//收藏
        designScheme.setPageView(0);//浏览
        designScheme.setShare(0);//分享
        designScheme.setPraise(0);//点赞
        designScheme.setStatus(SchemeStatus.DRAFT.getValue());//状态
        designScheme.setSource(1);//厂家还是经销商的
        RequestResult result = designScheme.validateFields();
        if (null!=result){
            return result;
        }
        this.designSchemeService.add(designScheme);
        MapContext mapContext = MapContext.newOne();
        mapContext.put("id",designScheme.getId() );
        return ResultFactory.generateRequestResult(mapContext);
    }

    /**
     * 添加设计案例的图片（type=0，添加封面，type=1，添加内容中图片）
     * @param files
     * @param uid
     * @param type
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addSchemeFiles(List<MultipartFile> files,String uid,Integer type,String resourceId) {
        UploadInfo uploadInfo;
        MapContext map = MapContext.newOne();
        for (MultipartFile file:files){
            DesignSchemeFiles designSchemeFiles = new DesignSchemeFiles();
            if (type==0){
                uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL,file,UploadResourceType.SCHEME_COVER,resourceId,"cover");
                map.put("filesPath",uploadInfo.getRelativePath());
                MapContext scheme = MapContext.newOne();
                scheme.put("id",resourceId);
                scheme.put("cover",uploadInfo.getRelativePath());
                this.designSchemeService.updateByMapContext(scheme);
                designSchemeFiles.setType(0);

            }else {
                uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL,file,UploadResourceType.SCHEME_CONTENT,resourceId,"content");
                map.put("filesPath",uploadInfo.getRelativePath());
                designSchemeFiles.setType(1);
            }
            designSchemeFiles.setCreated(DateUtil.getSystemDate());
            designSchemeFiles.setCreator(uid);
            designSchemeFiles.setResourceId(resourceId);
            designSchemeFiles.setFullPath(uploadInfo.getRealPath());
            designSchemeFiles.setMime(uploadInfo.getFileMimeType().getRealType());
            designSchemeFiles.setName(uploadInfo.getFileMimeType().getOriginalType());
            designSchemeFiles.setStatus(UploadType.FORMAL.getValue());
            designSchemeFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
            designSchemeFiles.setPath(uploadInfo.getRelativePath());
            designSchemeFiles.setFileType(0);

            RequestResult result = designSchemeFiles.validateFields();
            if (result!=null){
                return result;
            }
            this.designSchemeFilesService.add(designSchemeFiles);

        }

        return ResultFactory.generateRequestResult(map);
    }

    /**
     * 商家设置案例的状态（0 - 草稿；1 - 待审核；2 - 已发布;3 - 已下架）
     * @param companyId
     * @param schemeId
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updateSchemeStatus(String companyId, String schemeId,Integer status) {
        DesignScheme designScheme = this.designSchemeService.findById(schemeId);
        if (null==designScheme||!designScheme.getCompanyId().equals(companyId)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        boolean contains = SchemeStatus.contains(status);
        if (contains){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        MapContext params = MapContext.newOne();
        params.put("id",schemeId);
        params.put("status",status);
        this.designSchemeService.updateByMapContext(params);
        return ResultFactory.generateRequestResult(params);
    }

    /**
     * 商家编辑案例（只能编辑自己创建的案例）
     * @param companyId
     * @param schemeId
     * @param params
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updateDesignScheme(String companyId, String schemeId, MapContext params) {
        DesignScheme designScheme = this.designSchemeService.findById(schemeId);
        if (null==designScheme||!designScheme.getCompanyId().equals(companyId)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        Integer status = designScheme.getStatus();
        if (status==SchemeStatus.PUBLISHED.getValue()||status==SchemeStatus.TO_AUDIT.getValue()){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_PROJECT_MEMBER_10005,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_PROJECT_MEMBER_10005"));
        }
        params.put("id",schemeId);
        this.designSchemeService.updateByMapContext(params);
        return ResultFactory.generateRequestResult(params);
    }


    /**
     * 商家删除自己的店铺设计案例
     * @param schemeId
     * @param companyId
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult deleteDesignScheme(String schemeId, String companyId) {
        DesignScheme designScheme = this.designSchemeService.findById(schemeId);
        if (null==designScheme||!designScheme.getCompanyId().equals(companyId)){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        //循环删除设计案例的本地图片
        List<DesignSchemeFiles> designSchemeFileList  = this.designSchemeFilesService.findBySchemeId(schemeId);
        for (DesignSchemeFiles designSchemeFiles:designSchemeFileList) {
            AppBeanInjector.baseFileUploadComponent.deleteFileByDir(AppBeanInjector.configuration.getUploadFileRootDir().concat(designSchemeFiles.getPath()));

        }
        //删除设计案例的图片
        this.designSchemeFilesService.deleteBySchemeId(schemeId);
        //删除设计案例
        this.designSchemeService.deleteById(schemeId);
        MapContext map = MapContext.newOne();
        return ResultFactory.generateRequestResult(map);
    }


    /**
     * 查询根据公司id查询设计师和工厂的设计师
     * @return
     */
    @Override
    public RequestResult findAllDesigner(String quotations,String sort,Pagination pagination,String companyId) {

        PaginatedFilter Paginatedfilter = new PaginatedFilter();
        MapContext filters = MapContext.newOne();
        List<Map<String,String>> sorts = new ArrayList<Map<String,String>>();
        Map<String,String> map = new HashMap<>();
        if(LwxfStringUtils.isNotBlank(sort)){
            map.put("score",sort);
        }
        if(LwxfStringUtils.isNotBlank(quotations)){
            if (quotations.equals("max")){
                map.put("max_quotations","desc");
            }
            if (quotations.equals("min")){
                map.put("min_quotations","asc");
            }
        }
        if (null!=map&&map.size()>0){
            sorts.add(map);
        }
        filters.put("FCompanyId",WebUtils.getCurrCompanyId());//工厂id
        filters.put("BCompanyId",companyId);//当前公司id
        Paginatedfilter.setPagination(pagination);
        Paginatedfilter.setFilters(filters);
        Paginatedfilter.setSorts(sorts);
        PaginatedList<Map> designer = this.companyShareMemberService.findDesigner(Paginatedfilter);
        return ResultFactory.generateRequestResult(designer.getRows());
    }

    /**
     * 查询设计师的详情
     * @return
     */
    @Override
    public RequestResult findDesignByDesigner(String designer,String companyId){

        MapContext map = MapContext.newOne();
        map.put("designer",designer);
        map.put("companyId",companyId);
        //设计师的详情
        Map<String, String> designerInfo = this.companyShareMemberService.findByDesigner(map);
        //设计师的设计的样式和数量
        PaginatedFilter filter = PaginatedFilter.newOne();
        MapContext filters = MapContext.newOne();
        filters.put("designer",designer);
        filters.put("status",2);
        List<Map<String,String>> sorts = new ArrayList<>();
        Map<String,String> sort = new HashMap<>();
        sort.put("page_view","desc");
        sorts.add(sort);
        Pagination pagination = Pagination.newOne();
        pagination.setPageNum(1);
        pagination.setPageSize(3);
        filter.setPagination(pagination);
        filter.setFilters(filters);
        filter.setSorts(sorts);
        PaginatedList<Map> pagelist = this.designSchemeService.findListBydesigner(filter);
        List<Map> rows = pagelist.getRows();
        MapContext result = MapContext.newOne();
        result.put("designerInfo",designerInfo);
        result.put("schemes",rows);
        return ResultFactory.generateRequestResult(result);
    }

    /**
     * 修改案例的状态
     * @param companyId
     * @param status
     * @param schemeIds
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updateSchemeStatus(String companyId, Integer status, String[] schemeIds) {
        MapContext mapContext = MapContext.newOne();
        for (String schemeId : schemeIds) {
            DesignScheme designScheme = this.designSchemeService.findById(schemeId);
            if (null == designScheme || !designScheme.getCompanyId().equals(companyId)) {
                return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NO_PERMISSION_10003, AppBeanInjector.i18nUtil.getMessage("BIZ_NO_PERMISSION_10003"));
            }
            boolean contains = SchemeStatus.contains(status);
            if (!contains) {
                return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
            }
            mapContext.put("id", schemeId);
            mapContext.put("status", status);
            this.designSchemeService.updateByMapContext(mapContext);
        }
        return ResultFactory.generateRequestResult(mapContext);
    }


    /**
     * 添加案例的内容（目前是支持单个添加）
     * @param schemeContent
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addSchemeContent(SchemeContent schemeContent,Integer type) {
        schemeContent.setContentCreated(DateUtil.getSystemDate());
        RequestResult result = schemeContent.validateFields();
        if (result!=null){
            return result;
        }
        this.schemeContentService.add(schemeContent);
        if (type!=null){
            MapContext typeMap = MapContext.newOne();
            typeMap.put("status",type );
            typeMap.put("id", schemeContent.getSchemeId());
            this.designSchemeService.updateByMapContext(typeMap);
        }
        MapContext mapContext =MapContext.newOne();
        mapContext.put("id",schemeContent.getId());
        return ResultFactory.generateRequestResult(mapContext);
    }
}

