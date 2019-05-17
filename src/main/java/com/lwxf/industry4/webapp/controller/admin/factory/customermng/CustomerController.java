package com.lwxf.industry4.webapp.controller.admin.factory.customermng;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.customermng.CustomerFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/1/15 9:52
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController("fcustomerController")
@RequestMapping(value = "/api/f",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
@Api(value = "CustomerController",tags = "客户管理")
public class CustomerController {

    @Resource(name = "fCustomerFacade")
    private CustomerFacade fCustomerFacade;
    @Resource(name = "customerFacade")
    private com.lwxf.industry4.webapp.facade.app.dealer.customer.CustomerFacade bCustomerFacade;

    /**
     *
     * 查询所有的客户（可以根据公司id和顾客姓名，电话查询）
     * @param companyId
     * @param condition
     * @return
     */
    @GetMapping(value = "customers")
    public RequestResult findByClient(@RequestParam(required = false) String companyId,
                                      @RequestParam(required = false) String condition,
                                      @RequestParam(required = false) Integer pageSize,
                                      @RequestParam(required = false) Integer pageNum){
        if(null == pageSize){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if(null == pageNum){
            pageNum = 1;
        }
        RequestResult result = this.fCustomerFacade.findByClient(companyId, condition,pageSize,pageNum);
        return result;
    }

    /**
     * 添加顾客
     * @param params
     * @return
     */
    @PostMapping(value = "customers")
    public RequestResult addClient(@RequestBody MapContext params
                                   ){
        String companyId = (String) params.get("companyId");
        String currUserId = WebUtils.getCurrUserId();
        RequestResult result = fCustomerFacade.addCustomer(companyId, params, currUserId);
        return result;
    }

    /**
     * 修改客户信息
     * @param cid
     * @param mapContext
     * @return
     */
    @PutMapping("/customers/{cid}")
    @ApiOperation(value = "修改客户信息",notes = "修改客户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "客户的用户主键ID",name = "cid",dataType = "string",paramType = "path"),
            @ApiImplicitParam(value = "所需修改的内容",name = "mapContext",dataTypeClass = MapContext.class,paramType = "body")
    })
    public RequestResult updateClient(@PathVariable String cid,@RequestBody MapContext mapContext){
        RequestResult result = User.validateFields(mapContext);
        if(result!=null){
            return result;
        }
        return this.fCustomerFacade.updateClient(cid,mapContext);
    }


}

