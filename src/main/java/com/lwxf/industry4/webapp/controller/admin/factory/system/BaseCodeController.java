package com.lwxf.industry4.webapp.controller.admin.factory.system;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.entity.system.Basecode;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.system.BaseCodeFacade;
import com.lwxf.mybatis.utils.MapContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@Api(value="ActivityController",tags={"F端后台管理接口:字典管理"})
@RestController
@RequestMapping(value = "/api/f/basecodes",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class BaseCodeController {

    @Resource(name = "baseCodeFacade")
    private BaseCodeFacade baseCodeFacade;

    /**
     * 查询所有字典
     * @return 字典信息列表
     */
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功", response = Basecode.class)
    })
    @ApiOperation(value="获取字典信息",notes="")
    @GetMapping
    public RequestResult findBaseCodes(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) String type,
                                         @RequestParam(required = false) Integer pageSize,
                                         @RequestParam(required = false) Integer pageNum) {
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        Pagination pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        MapContext mapContent = MapContext.newOne();
        mapContent.put("name",name);
        mapContent.put("type",type);
        return this.baseCodeFacade.findListBasecodes(mapContent,pageNum,pageSize);
    }

    /**
     * 厂家添加活动
     * @RequestBody 活动信息对象
     * @return
     */
    @ApiOperation(value = "字典添加", notes = "test")
    @PostMapping
    public String addBaseCode(@RequestBody  Basecode Basecode) {
        JsonMapper jsonMapper = new JsonMapper();
        RequestResult requestResult = this.baseCodeFacade.add(Basecode);
        return jsonMapper.toJson(requestResult);
    }

    /**
     * 查询字典详情
     *
     * @return
     */
    @ApiOperation(value = "字典详情", notes = "")
    @GetMapping(value = "/{baseCodeId}")
    public String findById(@PathVariable String baseCodeId) {
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.baseCodeFacade.findById(baseCodeId);
        return mapper.toJson(result);
    }

    /**
     * 根据字典类型查询数据
     *
     * @return
     */
    @ApiOperation(value = "根据字典类型查询数据", notes = "")
    @GetMapping(value = "/types/{type}")
    public String findByType(@PathVariable String type) {
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        MapContext map = MapContext.newOne();
        map.put("type",type);
        RequestResult result = this.baseCodeFacade.findByMap(map);
        return mapper.toJson(result);
    }

    /**
     * @param baseCodeId
     * @param parmas 更新的内容
     * 商家编辑活动（只能修改自己的创建的活动）
     * @return
     */
    @ApiOperation(value="编辑字典",notes="编辑字典")
    @PutMapping(value = "/{baseCodeId}")
    public RequestResult updateBaseCode(@PathVariable String baseCodeId,
                                        @RequestBody MapContext parmas) {
        parmas.put("id",baseCodeId);
        return this.baseCodeFacade.update(baseCodeId,parmas);
    }

    /**
     * 删除字典
     * @param baseCodeId 字典id
     * @return
     */
    @ApiOperation(value="字典删除",notes="字典删除")
    @DeleteMapping(value = "/{baseCodeId}")
    public RequestResult delete(@PathVariable String baseCodeId){
        return this.baseCodeFacade.delete(baseCodeId);
    }
}
