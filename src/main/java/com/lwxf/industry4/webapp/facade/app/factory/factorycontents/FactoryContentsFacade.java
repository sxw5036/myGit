package com.lwxf.industry4.webapp.facade.app.factory.factorycontents;

import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/2 10:34
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FactoryContentsFacade extends BaseFacade {

    RequestResult findListByCodeAndStatus(Pagination pagination);

    RequestResult findNoticeInfoById(String noticeId);
}
