package com.lwxf.industry4.webapp.controller.app.factory.factoryhomepage;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.app.factory.factoryhomepage.FactoryHomePageFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/1 9:39
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "app/f",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryHomePageController {
    @Resource(name = "factoryHomePageFacade")
    private FactoryHomePageFacade factoryHomePageFacade;


    @GetMapping(value = "/homepage")
    public String findAdvertisingAndNotice(){
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult advertisingAndNotice = this.factoryHomePageFacade.findAdvertisingAndNotice();
        return mapper.toJson(advertisingAndNotice);
    }

}
