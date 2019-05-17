package com.lwxf.industry4.webapp.facade.app.factory.factoryavtivity;

import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/1 15:14
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FactoryActivityFacade extends BaseFacade {
    RequestResult findFactoryActivities(Pagination pagination);

    RequestResult findActivityInfoByActivityId(String activityId);
}
