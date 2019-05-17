package com.lwxf.industry4.webapp.facade.app.dealer.homepage.impl;

import com.lwxf.industry4.webapp.bizservice.advertising.AdvertisingService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.entity.advertising.Advertising;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.homepage.AdvertisingFacade;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/3 13:37
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "advertisingFacade")
public class AdvertisingFacadeImpl extends BaseFacadeImpl implements AdvertisingFacade {
    @Resource(name = "advertisingService")
    private AdvertisingService advertisingService;

    /**
     * 查询首页轮播信息
     * @return
     */
    @Override
    public RequestResult findHomeCarousel(Integer position) {
        List<Advertising> advertisingList= this.advertisingService.findByPosition(position);
        return ResultFactory.generateRequestResult(advertisingList);
    }
}

