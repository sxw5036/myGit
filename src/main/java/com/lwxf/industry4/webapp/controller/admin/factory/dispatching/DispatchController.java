package com.lwxf.industry4.webapp.controller.admin.factory.dispatching;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.dispatch.DispatchBillStatus;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.dispatch.DispatchBillDto;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBill;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.dispatching.DispatchFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/25/025 13:53
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/dispatchs", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
@Api(value = "DispatchController",tags = "配送单管理")
public class DispatchController {
    @Resource(name = "dispatchFacade")
    private DispatchFacade dispatchFacade;

    /**
     * 查询发货单列表
     *
     * @param orderNo
     * @param logisticsNo
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping
    private RequestResult findDispatchList(@RequestParam(required = false) String orderNo,
                                           @RequestParam(required = false) String logisticsNo,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(required = false) String id,
                                           @RequestParam(required = false) Integer pageNum,
                                           @RequestParam(required = false) Integer pageSize) {
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        MapContext mapContext = this.createMapContent(orderNo, logisticsNo, status,id);
        return this.dispatchFacade.findDispatchList(mapContext, pageNum, pageSize);
    }

    /**
     * 新增发货单 并同时发货
     *
     * @param dispatchBillDto
     * @return
     */
    @ApiOperation(value = "新增发货单 并同时发货",notes = "新增发货单 并同时发货")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "配送单信息",name = "DispatchBillDto",dataTypeClass = DispatchBillDto.class,paramType = "body")
    })
    @PostMapping
    private RequestResult addDispatch(@RequestBody DispatchBillDto dispatchBillDto) {
        if(dispatchBillDto.getDispatchBillItemDtoList().size()==0){
            return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
        }
        dispatchBillDto.setCreator(WebUtils.getCurrUserId());
        dispatchBillDto.setCreated(DateUtil.getSystemDate());
        dispatchBillDto.setStatus(DispatchBillStatus.TRANSPORT.getValue());
        dispatchBillDto.setActualDate(DateUtil.getSystemDate());
        dispatchBillDto.setDeliverer(WebUtils.getCurrUserId());
        //计划发货时间取当前时间
        dispatchBillDto.setPlanDate(DateUtil.getSystemDate());
        return this.dispatchFacade.addDispatch(dispatchBillDto);
    }

    /**
     * 删除发货单
     *
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    private RequestResult deleteById(@PathVariable String id) {
        return this.dispatchFacade.deleteById(id);
    }


    /**
     * 发货单发货
     *
     * @param id
     * @param mapContext
     * @return
     */
    @PutMapping("{id}/deliver")
    private RequestResult deliverById(@PathVariable String id,
                                      @RequestBody MapContext mapContext) {
        RequestResult result = DispatchBill.validateFields(mapContext);
        if (result != null) {
            return result;
        }
        return this.dispatchFacade.deliverById(mapContext, id);
    }

    private MapContext createMapContent(String orderNo, String logisticsNo, Integer status,String id) {
        MapContext mapContext = MapContext.newOne();
        if (orderNo != null && !orderNo.trim().equals("")) {
            mapContext.put("orderNo", orderNo);
        }
        if (logisticsNo != null && !logisticsNo.trim().equals("")) {
            mapContext.put("logisticsNo", logisticsNo);
        }
        if (status != null) {
            mapContext.put(WebConstant.KEY_ENTITY_STATUS, status);
        }
        if(id!=null){
            mapContext.put(WebConstant.KEY_ENTITY_ID,id);
        }
        return mapContext;
    }
}
