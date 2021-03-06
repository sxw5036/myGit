package com.lwxf.industry4.webapp.domain.dao.activity;


import java.util.List;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.entity.activity.ActivityParams;


/**
 * 功能：
 * 
 * @author：panchenxiao(Mister_pan@126.com)
 * @created：2019-03-08 10:10:37
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@IBatisSqlTarget
public interface ActivityParamsDao extends BaseDao<ActivityParams, String> {

	PaginatedList<ActivityParams> selectByFilter(PaginatedFilter paginatedFilter);

    List<ActivityParams> findByActivityId(String activityId);

    int deleteByActivityId(String activityId);

}