package com.lwxf.industry4.webapp.controller.app.factory.company;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.company.CompanyStatus;
import com.lwxf.industry4.webapp.common.enums.company.CompanyType;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.common.utils.excel.POIUtil;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyDtoForApp;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyDtoForAppAdd;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyInfoDtoForApp;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.company.CompanyFacade;
import com.lwxf.mybatis.utils.MapContext;
import io.swagger.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * 功能：
 *
 * @author：zhangxiaolin(3965488@qq.com)
 * @create：2019/03/27/ 13:24
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */

@Api(value="CompanyController",tags={"F端APP接口:经销商管理"})
@RestController("CompanyControllerFApp")
@RequestMapping(value = "/app/f/companies", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class CompanyController {

    @Resource(name = "companyFacadeFapp")
    private CompanyFacade companyFacade;

    /**
     * 获取经销商公司统计信息
     * @return
     */
    @ApiOperation(value = "获取经销商统计信息", notes = "signed:签约在线,unchecked:待审核,quited:退网,eg:{signed:0,unchecked:0,quit:0}")
    @GetMapping("/companyStatistics")
    private String viewCompanyStatistics(HttpServletRequest request) {
        JsonMapper jsonMapper = new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext map = LoginUtil.checkLogin(atoken);
        if(map==null ||map.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        RequestResult result = this.companyFacade.getCompanyStatistics();
        return jsonMapper.toJson(result);
    }

    /**
     * 获取经销商财务统计信息
     * @return
     */
     @ApiOperation(value = "获取经销商财务统计信息", notes = "companyCustCount:经销商客户数(头部);balance:经销商余额(头部);companyOrderCount:经销商订单数(头部);sumBalance:经销商所有账户总金额(中部);" +
            "accountType:所有账户类型(中部);accountInfo:每个账户里的金额(中部);sumAmount:列表金额合计(下部);pageCount:下测列表总条数(下部)/n" +
            "paymentList:支付信息列表;fundsName:科目名;amount:总金额;created:创建时间")
    @GetMapping("/{companyId}/companyAccountInfo")
    private String viewCompanyAccountInfo(@PathVariable String companyId, HttpServletRequest request) {
        JsonMapper jsonMapper = new JsonMapper();
         String atoken=request.getHeader("X-ATOKEN");
         MapContext map = LoginUtil.checkLogin(atoken);
         if(map==null ||map.get("userId")==null){
             return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
         }
        RequestResult result = this.companyFacade.viewCompanyAccountInfo(companyId);
        return jsonMapper.toJson(result);
    }

    /**
     * 获取经销商公司统计信息
     * @return
     */
    @ApiOperation(value = "经销商管理首页", notes = "")
    @GetMapping("/viewIndex")
    private String viewCompanyIndex(HttpServletRequest request) {
        JsonMapper jsonMapper = new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext map = LoginUtil.checkLogin(atoken);
        if(map==null ||map.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        RequestResult result = this.companyFacade.viewCompanyIndex();
        return jsonMapper.toJson(result);
    }

    /**
     * 条件查询经销商
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功", response = CompanyDtoForApp.class)
    @ApiOperation(value = "条件查询经销商", notes = "fromMonth为签约日期起，toMonth为签约日期止",response = CompanyDtoForApp.class)
    @GetMapping("/companies")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "起始时间 eg:2018-01-01", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "终止时间 eg:2018-01-01", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "companyName", value = "公司名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "cityAreaId", value = "地区ID:只传递最终节点id,不传省市,如北京则传110000", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "类型:0：生产商/制造商/总店；1：直营店；2：店中店；3：专卖店；4：加盟店；5：散单;6:供应商", dataType = "integer", paramType = "query" ),
            @ApiImplicitParam(name = "status", value = "状态:状态：0 - 意向；1 - 已签约（正常）；2 - 已退网；3 - 已倒闭；4 - 已禁用;5 - 无价值;6 - 待财务审核;7 - 待启用。对于厂家来说，状态为1，经销商初始状态为0", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "排序列，按日期排序则传递：created,按客户数排序传递：cust_count", dataType = "string", paramType = "query" ),
            @ApiImplicitParam(name = "sort", value = "排序方式，desc：倒序,asc：正序", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "topParam", value = "app顶部按钮查询，1:本月新增，2：签约在网，3：退网", dataType = "string", paramType = "query")
    })
    private String findCompanyList(@RequestParam(required = false) String beginTime,
                                   @RequestParam(required = false) String endTime,
                                   @RequestParam(required = false) String companyName,
                                   @RequestParam(required = false) String cityAreaId,
                                   @RequestParam(required = false) Integer type,
                                   @RequestParam(required = false) Integer status,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) Integer pageNum,
                                   @RequestParam(required = false) String order,
                                   @RequestParam(required = false) String sort,
                                   @RequestParam(required = false) Integer topParam,
                                   HttpServletRequest request){
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext mapValidate= LoginUtil.checkLogin(atoken);
        if(mapValidate==null ||mapValidate.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        MapContext map = createMapContent(beginTime,endTime,companyName,cityAreaId,type,status,topParam);
        if(order!=null && order!=""){
            map.put("order",order);
        }else{
            map.put("order","created");
        }
        if(sort!=null && sort !=""){
            map.put("sort",sort);
        }else{
            map.put("sort","desc");
        }
        RequestResult result=this.companyFacade.findCompanyList(map,pageNum,pageSize);
        return jsonMapper.toJson(result);
    }

    /**
     * 条件查询经销商(for app)
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功", response = CompanyDtoForApp.class)
    @ApiOperation(value = "条件查询经销商", notes = "fromMonth为签约日期起，toMonth为签约日期止",response = CompanyDtoForApp.class)
    @PostMapping("/findCompaniesForApp")
    private String findCompanyListForApp(@RequestBody(required = false) MapContext mapParam,
                                   HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext mapValidate= LoginUtil.checkLogin(atoken);
        if(mapValidate==null ||mapValidate.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        int pageNum=mapParam.get("pageNum")==null?1:Integer.parseInt(mapParam.get("pageNum").toString());
        int pageSize=mapParam.get("pageSize")==null? pageSize = AppBeanInjector.configuration.getPageSizeLimit():Integer.parseInt(mapParam.get("pageSize").toString());
        if(mapParam.get("order")==null || mapParam.get("order").toString().equals("")){
            mapParam.put("order","created");
        }
        if(mapParam.get("sort")==null || mapParam.get("sort").toString().equals("")){
            mapParam.put("sort","desc");
        }
        String xp="baftersalemng-aftersalement-view";
        RequestResult result=this.companyFacade.findCompanyList(mapParam,pageNum,pageSize);
        return jsonMapper.toJson(result);
    }

    /**
     * 获取经销商状态信息
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功", response = CompanyDtoForApp.class)
    @ApiOperation(value = "获取经销商状态信息", notes = "获取经销商状态信息")
    @GetMapping("/status")
    private String getCompanyStatusData(){
        MapContext map= MapContext.newOne();
        for (CompanyStatus s : CompanyStatus.values()) {
            map.put(s.getName(),s.getValue());
        }
        JsonMapper jsonMapper=new JsonMapper();
        return jsonMapper.toJson(map);
    }

    /**
     * 经销商按月统计订单数
     * @return
     */
    @ApiOperation(value="经销商订单趋势图数据",notes="经销商订单趋势图")
    @GetMapping("/{companyIds}/ordersCountMonthly")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyIds", value = "经销商公司id:companyIds为经销商id集合，用逗号分开，（如213dbui21,12321ax）", dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "beginDate", value = "起始时间 eg:2018-01-01  beginDate和endDate不能相同", dataType = "date", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "终止时间 eg:2018-01-01  beginDate和endDate不能相同", dataType = "date", paramType = "query")
    })
    private String getCompaniesOrdersCountMonthly(@PathVariable String companyIds,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date beginDate,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,
                                                  HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext mapValidate= LoginUtil.checkLogin(atoken);
        if(mapValidate==null ||mapValidate.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        MapContext map = createMapMonthOrders(companyIds,beginDate,endDate);
        RequestResult result=this.companyFacade.getCompaniesOrdersCountMonthly(map);
        return jsonMapper.toJson(result);
    }

    /**
     * 根据ID查询经销商详细信息
     * @return
     */
    @ApiResponse(code = 200, message = "查询成功")
    @ApiOperation(value="查看经销商详情",notes="查看经销商详情",response = CompanyInfoDtoForApp.class)
    @GetMapping("/{companyId}")
    @ApiImplicitParam(name = "companyId", value = "公司id", dataType = "string", paramType = "path")
    private String findCompanyInfo(@PathVariable String companyId,HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext mapValidate= LoginUtil.checkLogin(atoken);
        if(mapValidate==null ||mapValidate.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        RequestResult result=this.companyFacade.findCompanyInfo(companyId);
        if (result==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.commons.exception.ErrorCodes.SYS_ACCOUNT_NOT_ACTIVED_00012,"账号不存在"));
        }
        return jsonMapper.toJson(result);
    }

    /**
     * 设置公司状态
     * @param companyId  活动id
     * @param status  待更新的状态
     * @return
     */
    @ApiOperation(value="更新经销商公司状态",notes="更新经销商公司状态")
    @PutMapping(value = "/{companyId}/status/{status}")
    public String putCompanyStatus(@PathVariable String companyId,
                                           @PathVariable Integer status,HttpServletRequest request) {
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext mapValidate= LoginUtil.checkLogin(atoken);
        if(mapValidate==null ||mapValidate.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        MapContext params = MapContext.newOne();
        params.put("id",companyId);
        params.put("status",status);
        return  jsonMapper.toJson(this.companyFacade.updateStatus(params));
    }
    @ApiOperation(value="经销商添加操作",notes="经销商添加操作")
    @PostMapping(value="/")
    public String addCompany(@RequestBody CompanyDtoForAppAdd company, HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext mapValidate= LoginUtil.checkLogin(atoken);
        if(mapValidate==null ||mapValidate.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        String uid =mapValidate.get("userId").toString();
        Company checkCompany =this.companyFacade.checkCompanyNo(company.getNo());
        if(checkCompany!=null){
            return  jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_COMPANY_NO_DUPLICATE_10087,"经销商编码已存在"));
        } else if(companyFacade.checkUserMobile(company.getLeaderTel())==0){
            return  jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_USER_MOBILE_DUPLICATE_10088,"负责人电话已存在"));
        }else{
            company.setCreator(uid);
            company.setCreated(DateUtil.getSystemDate());
            company.setFollowers(0);
            company.setStatus(7);
            company.setFounderName(company.getLeaderName());
            return jsonMapper.toJson(ResultFactory.generateRequestResult(this.companyFacade.addCompany(company)));
        }
    }

    @PutMapping(value="/{companyId}")
    public String updateCompany(@PathVariable String companyId,@RequestBody MapContext map,HttpServletRequest request){
        JsonMapper jsonMapper=new JsonMapper();
        String atoken=request.getHeader("X-ATOKEN");
        MapContext mapValidate= LoginUtil.checkLogin(atoken);
        if(mapValidate==null ||mapValidate.get("userId")==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        map.put("id",companyId);
        return jsonMapper.toJson(this.companyFacade.updateCompany(map));
    }

    @ApiOperation(value="添加页面获取大区经理和负责人信息",notes="加页面获取大区经理和负责人信息")
    @GetMapping(value="/getUsersForAdd")
    public RequestResult getUsersForAdd(HttpServletRequest request){
        return this.companyFacade.getUsersForAdd();
    }

    /**
     * 参数组成
     * @param beginTime  开始时间
     * @return
     */
    private MapContext createMapContent(String beginTime,String endTime,String companyName,String cityAreaId,Integer type, Integer status,Integer topParam) {
        MapContext mapContext = MapContext.newOne();
        if (beginTime!=null && !beginTime.equals("")) {
            mapContext.put("beginTime", beginTime);
        }
        if (endTime!=null && !endTime.equals("")) {
            mapContext.put("endTime", endTime);
        }
        if (companyName!=null && !companyName.equals("")) {
            mapContext.put("name", companyName);
        }
        if (cityAreaId!=null && !cityAreaId.equals("")) {
            String cityCode = cityAreaId.replaceAll("0*$", "");//去除后面的0
            mapContext.put("cityAreaId", cityCode);
        }

        if (status!=null && !status.equals("")) {
            mapContext.put("status", status);
        }
        if (type!=null && !type.equals("")) {
            mapContext.put("type", type);
        }
        if (topParam!=null && !topParam.equals("")) {
            mapContext.put("topParam", topParam);
        }
        return mapContext;
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
        String uid = request.getHeader("X-UID");//用户Id
        String cid = request.getHeader("X-CID");//公司ID
        return this.companyFacade.uploadImage(uid,cid,multipartFileList);
    }

    /**
     * 经销商excel导入
     * @return
     */
    @ApiOperation(value="经销商excel导入",notes="返回值样例:{ \"code\": \"200\", \"data\": { \"ids\": [ \"4tuqvoqutzpc\", \"4tuqvovumolc\" ] } }")
    @PostMapping(value = "/dataImport")
    public RequestResult importCompanyByExcel(@RequestBody MultipartFile multipartFile, HttpServletRequest request){
        try {
            List<String[]> list = POIUtil.readExcel(multipartFile);
            for(String[] arr :list){
                CompanyDtoForAppAdd dto = new CompanyDtoForAppAdd();
                dto.setName(arr[0]);
                switch (arr[1]){
                    case "直营店":
                        dto.setType(CompanyType.MANUFACTURERS.getValue());
                        break;
                    case "店中店":
                        dto.setType(CompanyType.SHOP_IN_STORE.getValue());
                        break;
                    case "专卖店":
                        dto.setType(CompanyType.EXCLUSIVE_SHOP.getValue());
                        break;
                    case "加盟店":
                        dto.setType(CompanyType.FRANCHISED_STORE.getValue());
                        break;
                    case "散单":
                        dto.setType(CompanyType.LOOSE_ORDER.getValue());
                        break;
                    case "供应商":
                        dto.setType(CompanyType.SUPPLIER.getValue());
                        break;
                }
                try {
                String daqu = request.getHeader("X-ATOKEN");
                String uid = request.getHeader("X-UID");//用户Id
                dto.setLeaderName(arr[2]);
                dto.setLeaderTel(arr[3]);
                dto.setFounderName(arr[2]);
                dto.setAddress(arr[5]==null?"":arr[5]);
                dto.setContractTime(DateUtil.stringToDate("2019-05-08","yyyy-MM-dd"));
                dto.setBusinessManager(daqu);//4w0437x77ks
                dto.setCityAreaId(arr[4]);
                dto.setCreated(DateUtil.getSystemDate());
                dto.setCreator(uid);
                dto.setFollowers(0);
                dto.setStatus(7);
                dto.setFounderName(dto.getLeaderName());
                dto.setGrade(0);
                companyFacade.addCompany(dto);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }catch (IOException ex){
            ResultFactory.generateErrorResult("0000000","数据导入失败");
        }
        return ResultFactory.generateSuccessResult();
    }

    /**
     * 参数组成
     * @param beginTime  开始时间
     * @return
     */
    private MapContext createMapMonthOrders(String companyIds,Date beginTime,Date endTime) {
        List<String> list = new ArrayList<String>();
        MapContext mapContext = MapContext.newOne();
        if (beginTime!=null) {
            mapContext.put("beginTime", beginTime);
        }
        if (endTime!=null) {
            mapContext.put("endTime", endTime);
        }
        if (companyIds!=null) {
            String[] ids = companyIds.split(",");
            mapContext.put("companyIds", ids);
        }

        return mapContext;
    }
}
