package com.lwxf.industry4.webapp.controller.app.dealer.homepage;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.advertising.AdvertisingPosition;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.app.dealer.homepage.AdvertisingFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 功能：首页：轮播功能
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/3 10:19
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class AdvertisingController {
    @Resource(name = "advertisingFacade")
    private AdvertisingFacade advertisingFacade;

    /**
     * 查询首页轮播信息
     * @return
     */
    @GetMapping(value = "/advertisings/homecarousel")
    public String findHomeCarousel(){
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.advertisingFacade.findHomeCarousel(AdvertisingPosition.HOME_CAROUSEL.getValue());
        return mapper.toJson(result);
    }


}

