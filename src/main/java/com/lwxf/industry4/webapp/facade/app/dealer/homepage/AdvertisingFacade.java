package com.lwxf.industry4.webapp.facade.app.dealer.homepage;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/3 13:37
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface AdvertisingFacade extends BaseFacade {

    RequestResult findHomeCarousel(Integer position);
}
