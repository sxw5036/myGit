package com.lwxf.industry4.webapp.facade.app.factory.factoryavtivity.impl;

import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.activity.ActivityInfoService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.activity.ActivityStatus;
import com.lwxf.industry4.webapp.common.enums.activity.ActivityType;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.activity.ActivityInfo;
import com.lwxf.industry4.webapp.facade.app.factory.factoryavtivity.FactoryActivityFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/1 15:15
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factoryActivityFacade")
public class FactoryActivityFacadeImpl extends BaseFacadeImpl implements FactoryActivityFacade {

    @Resource(name = "activityInfoService")
    private ActivityInfoService activityInfoService;
    @Override
    public RequestResult findFactoryActivities(Pagination pagination) {
        String companyId = WebUtils.getCurrCompanyId();
        PaginatedFilter filter = PaginatedFilter.newOne();
        MapContext params = MapContext.newOne();
        params.put("FCompanyId", companyId);
        params.put("status",ActivityStatus.PUBLISHED.getValue());
        params.put("type",ActivityType.FACTORY.getValue());
        filter.setFilters(params);
        filter.setPagination(pagination);
        PaginatedList<Map> mapList = this.activityInfoService.findByFCompanyId(filter);
        List<Map> rows = mapList.getRows();
        if (rows!=null&&rows.size()>0){
            for (Map map:rows){
                String cover = (String) map.get("cover");
                String userAvatar = (String) map.get("userAvatar");
                if (LwxfStringUtils.isNotBlank(cover)){
                    cover = WebUtils.getDomainUrl()+cover;
                    map.put("cover", cover);
                }
                if (LwxfStringUtils.isNotBlank(userAvatar)){
                    userAvatar = WebUtils.getDomainUrl()+userAvatar;
                    map.put("userAvatar", userAvatar);
                }
            }
        }
        return ResultFactory.generateRequestResult(rows);
    }

    @Override
    public RequestResult findActivityInfoByActivityId(String activityId) {
        ActivityInfo activityInfo = this.activityInfoService.findById(activityId);
        if (activityInfo!=null){
            String cover = activityInfo.getCover();
            cover = WebUtils.getDomainUrl()+cover;
            activityInfo.setCover(cover);
            String content = activityInfo.getContent();
            Pattern pattern = Pattern.compile(LwxfStringUtils.format("/dev/activity/{0}/{0}/(cover|content)/(.*?)", WebConstant.REG_ID_MATCH));
            Matcher matcher = pattern.matcher(content);
            if (matcher.matches()){
                content = WebUtils.getDomainUrl()+content;
                activityInfo.setContent(content);
            }
        }
        return ResultFactory.generateRequestResult(activityInfo);
    }
}
