package com.lwxf.industry4.webapp.controller.app.factory.factorycontents;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.CompanyFinanceListDto;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factorycontents.FactoryContentsFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/2 10:40
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Api(value="FactoryContentsController",tags={"F端APP接口:内容管理"})
@RestController
@RequestMapping(value = "/app/f",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryContentsController {
    @Resource(name = "factoryContentsFacade")
    private FactoryContentsFacade factoryContentsFacade;

    /**
     * 查询信息中的公告列表
     * @param pageSize
     * @param pageNum
     * @return
     */
    @ApiOperation(value = "公告查询", notes = "")
    @GetMapping(value = "/contents/notices")
    public String findListByCodeAndStatus(@RequestParam(required = false) Integer pageSize,
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
        RequestResult result = this.factoryContentsFacade.findListByCodeAndStatus(pagination);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }

    @GetMapping(value = "/contents/notices/{noticeId}")
    public String findNoticeInfoById(@PathVariable String noticeId){
        RequestResult result = this.factoryContentsFacade.findNoticeInfoById(noticeId);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }



}
