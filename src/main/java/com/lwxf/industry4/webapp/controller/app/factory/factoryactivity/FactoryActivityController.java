package com.lwxf.industry4.webapp.controller.app.factory.factoryactivity;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.activity.ActivityFacade;
import com.lwxf.industry4.webapp.facade.app.factory.factoryavtivity.FactoryActivityFacade;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/1 15:13
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/f",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryActivityController {
    @Resource(name = "factoryActivityFacade")
    private FactoryActivityFacade factoryActivityFacade;
    @Resource(name = "activityFacade")
    private ActivityFacade activityFacade;

    /**
     * 查询发布的活动列表
     * @param pageSize
     * @param pageNum
     * @return
     */
    @GetMapping(value = "/activities")
    public String findFactoryActivities(@RequestParam(required = false) Integer pageSize,
                                        @RequestParam(required = false) Integer pageNum){

        if (pageNum==null){
            pageNum = 1;
        }
        if (pageSize==null){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        Pagination pagination = Pagination.newOne();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        RequestResult factoryActivities = this.factoryActivityFacade.findFactoryActivities(pagination);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(factoryActivities);
    }

    @GetMapping(value = "/activities/{activityId}")
    public String findByActivityId(@PathVariable String activityId,
                                   HttpServletRequest request) {
        String userId = request.getHeader("X-UID");
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.activityFacade.findByActivityId(activityId,userId);
        return mapper.toJson(result);
    }


}
