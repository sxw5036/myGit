package com.lwxf.industry4.webapp.controller.app.factory.factoryscheme;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.scheme.SchemeStatus;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.design.HomeCaseFacade;
import com.lwxf.industry4.webapp.facade.app.factory.factoryscheme.FactorySchemeFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/2 13:14
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/f",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactorySchemeController {

    @Resource(name = "factorySchemeFacade")
    private FactorySchemeFacade factorySchemeFacade;
    @Resource(name = "homeCaseFacade")
    private HomeCaseFacade homeCaseFacade;
    @GetMapping(value = "/schemes")
    public String findList(@RequestParam(required = false) String companyId,
                           @RequestParam(required = false) Integer pageSize,
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
        MapContext map = MapContext.newOne();
        if (LwxfStringUtils.isNotBlank(companyId)){
            map.put(WebConstant.KEY_ENTITY_COMPANY_ID, companyId);
        }
        map.put(WebConstant.KEY_ENTITY_STATUS,SchemeStatus.PUBLISHED.getValue());
        RequestResult list = this.factorySchemeFacade.findList(map, pagination);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(list);
    }


    @GetMapping(value = "/schemes/{schemeId}")
    public String findById(@PathVariable String schemeId) {
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.homeCaseFacade.findById(schemeId);
        return mapper.toJson(result);
    }

}
