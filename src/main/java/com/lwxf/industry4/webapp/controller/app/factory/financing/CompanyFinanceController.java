package com.lwxf.industry4.webapp.controller.app.factory.financing;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentSimpleFunds;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyDtoForApp;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyInfoDtoForApp;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentSimpleDto;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.financing.CompanyFinanceFacade;
import com.lwxf.mybatis.utils.MapContext;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能：
 *
 * @author：zhangxiaolin(3965488@qq.com)
 * @create：2019/03/27/ 13:24
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Api(value="CompanyFinanceController",tags={"F端APP接口:经销商财务信息管理"})
@RestController("CompanyFinanceControllerForApp")
@RequestMapping(value = "/app/f/companyFinances", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class CompanyFinanceController {

    @Resource(name = "CompanyFinanceFacade")
    private CompanyFinanceFacade companyFinanceFacade;

    /**
     * 获取经销商公司统计信息
     * @return
     */
    @ApiOperation(value = "经销商财务管理首页", notes = "signedCompany:签约公司,company_balance:账户余额,incomeToday:当日收入,outlayToday:今日支出,类型:1,收入,2,支出，3，扣款;type 1:收入,2:支出,3:扣款, funds ，type对应的科目")
    @GetMapping("/viewIndex")
    private String viewCompanyIndex(HttpServletRequest request) {
        JsonMapper jsonMapper = new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext map = LoginUtil.checkLogin(atoken);
        String uid =map.get("userId").toString();
        if(uid==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        RequestResult result = this.companyFinanceFacade.viewIndex();
        return jsonMapper.toJson(result);
    }

    /**
     * 条件查询经销商
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功", response = CompanyDtoForApp.class)
    @ApiOperation(value = "条件查询经销商", notes = "",response = CompanyDtoForApp.class)
    @GetMapping("/companies")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "起始时间 eg:2018-01-01", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "终止时间 eg:2018-01-01", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "companyName", value = "公司名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "1:收入，2，支出", dataType = "integer", paramType = "query" ),
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
                                   @RequestParam(required = false) String sort,
                                   @RequestParam(required = false) Integer type,
                                   HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext mapInfo = LoginUtil.checkLogin(atoken);
        String uid =mapInfo.get("userId").toString();
        if(uid==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }

        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        String xp="baftersalemng-aftersalement-view";
        MapContext map = createMapContent(beginTime,endTime,companyName,funds,order,sort,type);
        RequestResult result=this.companyFinanceFacade.findCompanyFinanceList(map,pageNum,pageSize);
        return jsonMapper.toJson(result);
    }

    /**
     * 根据ID查询经销商详细信息
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功")
    @ApiOperation(value="查看经销商详情",notes="查看经销商详情",response = CompanyInfoDtoForApp.class)
    @GetMapping("/{paymentId}")
    @ApiImplicitParam(name = "paymentId", value = "公司id", dataType = "string", paramType = "path")
    private String findCompanyInfo(@PathVariable String paymentId, HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        RequestResult result=this.companyFinanceFacade.getCompanyFinanceById(paymentId);
        return jsonMapper.toJson(result);
    }

    /**
     * 参数组成
     * @param beginTime  开始时间
     * @return
     */
    private MapContext createMapContent(String beginTime, String endTime,String companyName,Integer funds,String order,String sort,Integer type) {
        MapContext mapContext = MapContext.newOne();
        if (beginTime!=null) {
            mapContext.put("beginTime", beginTime);
        }
        if (type!=null) {
            mapContext.put("type", type);
        }
        if (endTime!=null) {
            mapContext.put("endTime", endTime);
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

}
