package com.lwxf.industry4.webapp.facade.app.dealer.activity;

import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.entity.activity.ActivityInfo;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/8 11:51
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface ActivityFacade extends BaseFacade {

    RequestResult findAllActivity(Pagination pagination, MapContext params);

    RequestResult findBAndFActivity(Pagination pagination, MapContext params);

    RequestResult addCompanyActivity(ActivityInfo activityInfo);

    RequestResult findByActivityId(String activityId,String userId);


    RequestResult putActivityStatus(MapContext params);

    RequestResult addCover(MultipartFile multipartFile,String companyId,String activityId,String uid,String type);

    RequestResult updateActivity(String companyId,String activityId,MapContext parmas);

    RequestResult addActivityJoin(String companyId,String activityId,MapContext parmas,HttpServletRequest request);

    RequestResult findJoinRecord(MapContext params);

    RequestResult updateCover(MultipartFile multipartFile,String activityId,String companyId,String type,String path);

    RequestResult updateActivityParams(String companyId, String activityId, MapContext map, String paramsId);

    RequestResult findByCompanyId(Pagination pagination,MapContext params);

    RequestResult findActivityByFCompanyId(Pagination pagination,MapContext params,Integer type,String companyId);

    RequestResult BJoinFActivity(String companyId, String activityId,String userId);
}
