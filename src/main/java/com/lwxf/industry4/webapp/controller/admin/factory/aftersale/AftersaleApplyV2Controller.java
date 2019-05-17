package com.lwxf.industry4.webapp.controller.admin.factory.aftersale;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApply;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.aftersale.AfterSaleApplyV2Facade;
import com.lwxf.mybatis.utils.MapContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 功能：
 *
 * @author：zhangxiaolin(3965488@qq.com)
 * @create：2019/1/8/008 9:56
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Api(value="AftersaleApplyV2Controller",tags={"F端后台管理接口:售后管理"})
@RestController(value = "fAftersaleApplyV2Controller")
@RequestMapping(value = "/api/f/aftersales/v2", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class AftersaleApplyV2Controller {

    @Resource(name = "fAftersaleApplyV2Facade")
    private AfterSaleApplyV2Facade afterSaleApplyV2Facade;


    /**
     * 售后列表
     */
    @ApiOperation(value = "售后单查询列表", notes = "")
    @GetMapping("/aftersaleApplies")
    public  String findAftersaleApplyList(@RequestParam(required = false) String beginTime,
                                          @RequestParam(required = false) String endTime,
                                          @RequestParam(required = false) String leaderName,
                                          @RequestParam(required = false) String custName,
                                          @RequestParam(required = false) String cityName,
                                          @RequestParam(required = false) String prodType,
                                          @RequestParam(required = false) String series,
                                          @RequestParam(required = false) Integer status,
                                          @RequestParam(required = false) Integer pageNum,
                                          @RequestParam(required = false) Integer pageSize){
        JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        MapContext mapContext = createMapContent(beginTime,endTime,leaderName,custName,cityName,prodType,series,status);
        RequestResult result=this.afterSaleApplyV2Facade.findAftersaleApplyList(pageNum,pageSize,mapContext);
        return jsonMapper.toJson(result);
    }
    /**
     * 售后单详情
     * @param aftersaleApplyId
     * @return
     */
    @ApiOperation(value = "售后单详情", notes = "")
    @GetMapping("/aftersaleApplies/{aftersaleApplyId}")
    public String factoryAftersaleApplyInfo(@PathVariable String aftersaleApplyId){
        JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
        RequestResult result=this.afterSaleApplyV2Facade.factoryAftersaleApplyInfo(aftersaleApplyId);
        return jsonMapper.toJson(result);
    }

    /**
     * 经销商列表
     * @return
     */
    @ApiOperation(value = "经销商列表", notes = "")
    @GetMapping("/dealers")
    public String findDealersList(@RequestParam(required = false) String mergerName,
                                  @RequestParam(required = false) String dealerName){
        JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
        MapContext params=MapContext.newOne();
        if(LwxfStringUtils.isNotBlank(mergerName)){
            params.put("mergerName",mergerName);
        }
        if(LwxfStringUtils.isNotBlank(dealerName)){
            params.put("dealerName",dealerName);
        }
        RequestResult result=this.afterSaleApplyV2Facade.findDealersList(params);
        return jsonMapper.toJson(result);
    }


    /**
     * 客户列表
     * @param dealerId
     * @param request
     * @return
     */
    @ApiOperation(value = "客户列表", notes = "")
    @GetMapping("/dealers/{dealerId}/customers")
    public String findCustomerList(@PathVariable String dealerId,
                                   HttpServletRequest request){
        JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
        RequestResult result=this.afterSaleApplyV2Facade.findCustomerList(dealerId);
        return jsonMapper.toJson(result);
    }

    /**
     * 创建售后单
     * @return
     */
    @ApiOperation(value = "添加售后申请单", notes = "")
    @PostMapping("/add")
    public RequestResult addFactoryAftersale(@RequestBody AftersaleApply aftersaleApply){
        RequestResult result=this.afterSaleApplyV2Facade.addFactoryAftersale(aftersaleApply);
        return  result;
    }

    /**
     * 更新售后单状态
     * @return
     */
    @ApiOperation(value = " 更新售后单状态", notes = "")
    @PutMapping("/aftersaleApplies/{aftersaleId}/status/{status}")
    public RequestResult updateAftersaleStatus(@PathVariable String aftersaleId,@PathVariable String status){
        RequestResult result=this.afterSaleApplyV2Facade.updateAftersaleStatus(aftersaleId,status);
        return  result;
    }

    /**
     * 处理售后单
     * @return
     */
    @ApiOperation(value = "售后单处理", notes = "")
    @PutMapping("/handle")
    public RequestResult handleFactoryAftersale(@RequestBody MapContext mapContext){
        RequestResult result=this.afterSaleApplyV2Facade.handleFactoryAftersale(mapContext);
        return  result;
    }

    /**
     * 上传售后申请证据图片
     */
    @ApiOperation(value = " 上传售后申请证据图片", notes = "")
    @PostMapping("/aftersaleApplies/addfiles")
    public RequestResult addFiles(@RequestBody List<MultipartFile> multipartFiles){
        Map<String, Object> errorInfo = new HashMap<>();
        if (multipartFiles == null||multipartFiles.size()==0) {
            errorInfo.put("multipartFiles", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
        }
        for (MultipartFile multipartFile:multipartFiles) {
            if (multipartFile==null){
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            }
            if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
            }
            if (multipartFile.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
                return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
            }
        }
        if (errorInfo.size() > 0) {
            return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR, errorInfo);
        }
        return this.afterSaleApplyV2Facade.addFiles(multipartFiles);
    }

    /**
     * 参数组成
     * @param beginTime  开始时间
     * @return
     */
    private MapContext createMapContent(String beginTime,String endTime,String leaderName,String custName,String cityName,String prodType,String series,Integer status) {
        MapContext mapContext = MapContext.newOne();
        if (beginTime!=null && !beginTime.equals("")) {
            mapContext.put("startTime", beginTime);
        }
        if (endTime!=null && !endTime.equals("")) {
            mapContext.put("endTime", endTime);
        }
        if (leaderName!=null && !leaderName.equals("")) {
            mapContext.put("leaderName", leaderName);
        }
        if (custName!=null && !custName.equals("")) {
            mapContext.put("status", custName);
        }
        if (cityName!=null && !cityName.equals("")) {
            mapContext.put("cityName", cityName);
        }
        if (prodType!=null && !prodType.equals("")) {
            mapContext.put("productType", prodType);
        }
        if (series!=null && !series.equals("")) {
            mapContext.put("productSeries", series);
        }
        if (status!=null) {
            mapContext.put("status", status);
        }
        return mapContext;
    }

}
