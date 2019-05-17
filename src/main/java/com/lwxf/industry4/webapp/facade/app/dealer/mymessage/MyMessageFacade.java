package com.lwxf.industry4.webapp.facade.app.dealer.mymessage;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/10 10:12
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface MyMessageFacade extends BaseFacade {

    RequestResult selectByFilter(Integer pageSize, Integer pageNum,MapContext params);
    RequestResult selectByNotifyId(String notifyId);


}
