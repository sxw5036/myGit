package com.lwxf.industry4.webapp.controller.app.factory.factorystatement;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.app.factory.factorystatement.IndexStatementFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/5/16 14:47
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/f", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class IndexStatementController {

    @Resource(name = "indexStatementFacade")
    private IndexStatementFacade indexStatementFacade;

    @GetMapping(value = "/statement/index")
    public String statementHomePage(@RequestParam Integer type,
                                    @RequestParam(required = false) String bTime,
                                    @RequestParam(required = false) String eTime) {
        RequestResult result = this.indexStatementFacade.statementHomePage(type, bTime, eTime);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }

}
