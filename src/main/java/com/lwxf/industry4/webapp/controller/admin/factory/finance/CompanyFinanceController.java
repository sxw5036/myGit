package com.lwxf.industry4.webapp.controller.admin.factory.finance;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentWay;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyPaymentInfoDto;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.CompanyFinanceListDto;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.financing.CompanyFinanceFacade;
import com.lwxf.mybatis.utils.MapContext;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 * @author：zhangxiaolin(3965488@qq.com)
 * @create：2019/03/27/ 13:24
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Api(value="CompanyFinanceController",tags={"F端后台管理接口:经销商财务信息管理"})
@RestController("CompanyFinanceController")
@RequestMapping(value = "/api/f/companyFinances", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class CompanyFinanceController {

    @Resource(name = "CompanyFinanceFacade")
    private CompanyFinanceFacade companyFinanceFacade;

    /**
     * 条件查询经销商
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功")
    @ApiOperation(value = "经销商财务信息", notes = "",response = CompanyFinanceListDto.class)
    @GetMapping("/companies/payments")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "起始时间 eg:2018-01-01", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "终止时间 eg:2018-01-01", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "companyName", value = "公司名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "类型:0：生产商/制造商/总店；1：直营店；2：店中店；3：专卖店；4：加盟店；5：散单;6:供应商", dataType = "integer", paramType = "query" ),
            @ApiImplicitParam(name = "order", value = "排序列，按时间传递：created,按金额排序传递：amount", dataType = "string", paramType = "query" ),
            @ApiImplicitParam(name = "sort", value = "排序方式，desc：倒序,asc：正序", dataType = "string", paramType = "query")
    })
    private String findCompanyFinanceList(
                                   @PathVariable
                                   @RequestParam(required = false) String beginTime,
                                   @RequestParam(required = false) String endTime,
                                   @RequestParam(required = false) String companyName,
                                   @RequestParam(required = false) Integer funds,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) Integer pageNum,
                                   @RequestParam(required = false) String order,
                                   @RequestParam(required = false) String sort){
        JsonMapper jsonMapper=new JsonMapper();
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        MapContext map = createMapContent(beginTime,endTime,companyName,funds,order,sort);
        RequestResult result=this.companyFinanceFacade.findCompanyFinanceList(map,pageNum,pageSize);
        return jsonMapper.toJson(result);
    }


    /**
     * 根据ID查询经销商详细信息
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功")
    @ApiOperation(value="查看经销商费用详情",notes="查看经销商费用详情")
    @GetMapping("/{paymentId}")
    @ApiImplicitParam(name = "paymentId", value = "公司id", dataType = "string", paramType = "path")
    private String findCompanyInfo(@PathVariable String paymentId){
        JsonMapper jsonMapper=new JsonMapper();
        RequestResult result=this.companyFinanceFacade.getCompanyFinanceById(paymentId);
        return jsonMapper.toJson(result);
    }

    @ApiResponse(code = 200, message = "查询成功")
    @ApiOperation(value="添加经销商财务信息",notes="添加经销商财务信息",response = CompanyPaymentInfoDto.class)
    @PostMapping("/add")
    @ApiImplicitParam(name = "paymentId", value = "公司id", dataType = "string", paramType = "path")
    private RequestResult addCompanyPayment(@RequestBody CompanyPaymentInfoDto companyPaymentInfoDto){
        JsonMapper jsonMapper=new JsonMapper();
        return this.companyFinanceFacade.addCompanyPayment(companyPaymentInfoDto);
    }

    /**
     * 参数组成
     * @param beginTime  开始时间
     * @return
     */
    private MapContext createMapContent(String beginTime, String endTime,String companyName,Integer funds,String order,String sort) {
        MapContext mapContext = MapContext.newOne();
        if (beginTime!=null) {
            mapContext.put("beginTime", beginTime);
        }
        if (endTime!=null) {
            mapContext.put("endTime", endTime);
        }
        if (endTime!=null) {
            mapContext.put("funds", funds);
        }
        if (companyName!=null) {
            mapContext.put("companyName", companyName);
        }
        if(order!=null && order!=""){
            if(order.equals("created") || order.equals("amount")){
                mapContext.put("order",order);
            }
        }else{
            mapContext.put("sort","created");
        }
        if(sort!=null && sort !=""){
            if(sort.equals("desc") || order.equals("asc")) {
                mapContext.put("sort",sort);
            }
        }else{
            mapContext.put("sort","desc");
        }
        return mapContext;
    }

    /**
     * 获得科目信息
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功")
    @ApiOperation(value="获得科目信息",notes="获得科目信息")
    @GetMapping("/funds")
    private String getPaymentFunds(){
        JsonMapper jsonMapper=new JsonMapper();
        //类型科目
        MapContext map = MapContext.newOne();
        List<MapContext> listIn = new ArrayList<>();
        for (PaymentFunds s : PaymentFunds.values()) {
            if(s.getValue().intValue()>10 &&s.getValue().intValue()<20) {
                MapContext mapCompanyStatus = MapContext.newOne();
                mapCompanyStatus.put("name", s.getName());
                mapCompanyStatus.put("id", s.getValue());
                listIn.add(mapCompanyStatus);
            }
        }
        map.put("fundsIn",listIn);
        List<MapContext> listOut = new ArrayList<>();
        for (PaymentFunds s : PaymentFunds.values()) {
            if(s.getValue().intValue()>20 &&s.getValue().intValue()<30) {
                MapContext mapCompanyStatus = MapContext.newOne();
                mapCompanyStatus.put("name", s.getName());
                mapCompanyStatus.put("id", s.getValue());
                listOut.add(mapCompanyStatus);
            }
        }
        map.put("fundsOut",listOut);
        return jsonMapper.toJson(ResultFactory.generateRequestResult(map));
    }

    /**
     * 获得科目信息
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功")
    @ApiOperation(value="获得支付方式",notes="获得支付方式")
    @GetMapping("/ways")
    private String getPaymentWays(){
        JsonMapper jsonMapper=new JsonMapper();
        //支付方式
        MapContext map = MapContext.newOne();
        List<MapContext> listways = new ArrayList<>();
        for (PaymentWay s : PaymentWay.values()) {
                MapContext pw = MapContext.newOne();
                pw.put("name", s.getName());
                pw.put("id", s.getValue());
                listways.add(pw);
        }
        map.put("ways",listways);
        return jsonMapper.toJson(ResultFactory.generateRequestResult(map));
    }

    /**
     * 经销商管理图片上传
     * @param multipartFileList 多个附件
     * @return
     */
    @ApiOperation(value=" 经销商管理图片上传",notes="返回值样例:{ \"code\": \"200\", \"data\": { \"ids\": [ \"4tuqvoqutzpc\", \"4tuqvovumolc\" ] } }")
    @PostMapping(value = "/uploadImages")
    public RequestResult uploadImages(@RequestBody List<MultipartFile> multipartFileList, HttpServletRequest request){
        Map<String, Object> errorInfo = new HashMap<>();
        if(multipartFileList!=null && multipartFileList.size()>0) {
            for (MultipartFile multipartFile : multipartFileList) {
                if (multipartFile == null) {
                    errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
                }
                if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
                    errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
                }
                if (multipartFile.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
                    return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
                }
                if (errorInfo.size() > 0) {
                    return ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.VALIDATE_ERROR, errorInfo);
                }
            }
        }
        String uid = WebUtils.getCurrUserId();
        String cid = WebUtils.getCurrCompanyId();
        return this.companyFinanceFacade.uploadImage(uid,cid,multipartFileList);
    }

}
