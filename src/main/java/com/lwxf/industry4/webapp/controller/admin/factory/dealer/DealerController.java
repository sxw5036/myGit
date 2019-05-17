package com.lwxf.industry4.webapp.controller.admin.factory.dealer;

import io.rong.models.response.TokenResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.annotation.Resource;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.baseservice.rongcloud.RongCloudUtils;
import com.lwxf.industry4.webapp.baseservice.sms.yunpian.SmsUtil;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.dto.UserInfoObj;
import com.lwxf.industry4.webapp.common.enums.company.CompanyStatus;
import com.lwxf.industry4.webapp.common.enums.company.CompanyType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentStatus;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentWay;
import com.lwxf.industry4.webapp.common.enums.user.UserType;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyDto;
import com.lwxf.industry4.webapp.domain.dto.companyEmployee.CompanyEmployeeDto;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.dealer.DealerFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/5/005 13:24
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/dealers", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
@Api(value = "经销商管理",tags = "经销商管理")
public class DealerController {
    @Resource(name = "dealerFacade")
    private DealerFacade dealerFacade;

    /**
     * 新增经销商公司
     *
     * @param company
     * @return
     */
    @PostMapping("companies")
    @ApiOperation(value = "新增经销商公司",notes = "新增经销商公司",response = CompanyDto.class)
    private String addDealer(@RequestBody@ApiParam(value = "公司信息") CompanyDto company) {

        JsonMapper jsonMapper = new JsonMapper();
        //公司表信息
        company.setCreated(DateUtil.getSystemDate());
        company.setCreator(WebUtils.getCurrUserId());
        company.setStatus(CompanyStatus.INTENTION.getValue());
        company.setFollowers(0);
        if (company.getNo() == null && company.getNo().trim().equals("")) {
            company.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.COMPANY_NO));
        }
        RequestResult result1 = company.validateFields();
        if (result1 != null) {
            return jsonMapper.toJson(result1);
        }
        RequestResult result = this.dealerFacade.addDealer(company);
        return jsonMapper.toJson(result);

    }

    /**
     * 经销商激活
     *
     * @param cid
     * @return
     */
    @PostMapping("/companies/{cid}/opendealer")
    @ApiOperation(value = "经销商激活",notes = "经销商激活")
    private String openDealer(@PathVariable@ApiParam(value = "公司主键ID") String cid) {
        User user = new User();
        StringBuffer pwd = new StringBuffer();
        RequestResult requestResult = this.dealerFacade.openDealer(user, cid, pwd);
        JsonMapper jsonMapper = new JsonMapper();
        //注册成功后给用户发短信
        if (Integer.parseInt((String) requestResult.get("code")) == (200)) {
            SmsUtil.sendDealerInfoMessage(user.getMobile(), user.getName(), pwd.toString());
        }else{
            return jsonMapper.toJson(requestResult);
        }
        //处理融云token
        UserInfoObj data = (UserInfoObj) requestResult.getData();
        if (data != null) {
            user = data.getUser();
            TokenResult tokenResult = RongCloudUtils.registerUser(user);
            if (tokenResult != null) {
                String token = tokenResult.getToken();
                AppBeanInjector.userThirdInfoFacade.updateRongToken(user.getId(), token);
                UserThirdInfo userThirdInfo = data.getUserThirdInfo();
                userThirdInfo.setRongcloudToken(token);
            }
        }
        return jsonMapper.toJson(requestResult);
    }

    /**
     * 获取经销商公司列表
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @param type
     * @return
     */
    @ApiOperation(value = "获取经销商公司列表",notes = "获取经销商公司列表",response = CompanyDto.class)
    @GetMapping("/companies")
    private String findDealerCompanyList(@RequestParam(required = false) Integer pageNum,
                                  @RequestParam(required = false) Integer pageSize,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) Integer type,
                                  @RequestParam(required = false) String mobile,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) String no) {
        if (null == pageSize) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if (null == pageNum) {
            pageNum = 1;
        }
        MapContext mapContent = this.createMapContent(name, type, mobile, no,status,null);
        RequestResult dealerList = this.dealerFacade.findDealerCompanyList(mapContent, pageNum, pageSize);
        JsonMapper jsonMapper = new JsonMapper();
        return jsonMapper.toJson(dealerList);
    }

    /**
     * 查询经销商公司详情
     * @param cid
     * @return
     */
    @GetMapping("/companies/{cid}")
    @ApiOperation(value = "查询经销商公司详情",notes = "查询经销商公司详情")
    private RequestResult findDealerCompanyInfo(@PathVariable String cid){
        return this.dealerFacade.findDealerCompanyInfo(cid);
    }

    /**
     * 修改经销商公司信息
     *
     * @param cid
     * @param mapContext
     * @return
     */
    @PutMapping("/companies/{cid}")
    @ApiOperation(value = "修改经销商公司信息",notes = "修改经销商公司信息")
    private String updateDealerCompany(@PathVariable String cid, @RequestBody MapContext mapContext) {
        RequestResult result = Company.validateFields(mapContext);
        JsonMapper jsonMapper = new JsonMapper();
        if (result != null) {
            return jsonMapper.toJson(result);
        }
        return jsonMapper.toJson(this.dealerFacade.updateDealerCompany(mapContext, cid));
    }

    /**
     * 经销商上传资源文件
     * @param cid
     * @param multipartFileList
     * @return
     */
    @PostMapping("/companies/{cid}/files")
    @ApiOperation(value = "经销商上传资源文件",notes = "经销商上传资源文件")
    private RequestResult uploadDealerFiles(@PathVariable String cid, @RequestBody List<MultipartFile> multipartFileList){
        Map<String, Object> errorInfo = new HashMap<>();
        if (multipartFileList == null||multipartFileList.size()==0) {
            errorInfo.put("multipartFiles", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
        }
        for (MultipartFile multipartFile:multipartFileList) {
            if (multipartFile==null){
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
            }
            if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
                errorInfo.put("multipartFile", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
            }
            if (multipartFile.getSize() > 1024L * 1024L * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
                return ResultFactory.generateErrorResult(ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
            }
        }
        if (errorInfo.size() > 0) {
            return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, errorInfo);
        }
        return this.dealerFacade.uploadDealerFiles(cid,multipartFileList);
    }

    /**
     * 删除资源文件
     * @param cid
     * @param fileId
     * @return
     */
    @DeleteMapping("/companies/{cid}/files/{fileId}")
    @ApiOperation(value = "删除资源文件",notes = "删除资源文件")
    private RequestResult deleteDealerFiles(@PathVariable String cid,@PathVariable String fileId){
        return this.dealerFacade.deleteDealerFiles(cid,fileId);
    }

    /**
     * 经销商到财务审核
     * @return
     */
    @PutMapping("/companies/{cid}/submit")
    @ApiOperation(value = "经销商到财务审核",notes = "经销商到财务审核")
    private RequestResult submitDealer(@PathVariable String cid, @RequestBody Payment payment){
        payment.setType(PaymentType.B_TO_F_WITHHOLD.getValue());
        payment.setFunds(PaymentFunds.FRANCHISE_FEE.getValue());
        payment.setCreator(WebUtils.getCurrUserId());
        payment.setCreated(DateUtil.getSystemDate());
        payment.setStatus(PaymentStatus.PENDING_PAYMENT.getValue());
        payment.setPayee("4j1u3r1efshq");
        payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
        payment.setCompanyId(cid);
        payment.setWay(PaymentWay.BANK.getValue());
        payment.setHolder("红田集团");
        return this.dealerFacade.submitDealer(cid,payment);
    }

	/**
	 * 查询经销商列表
	 * @param mobile
	 * @param name
	 * @param status
	 * @param loginName
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
    @GetMapping
    @ApiOperation(value = "查询经销商列表",notes = "查询经销商列表",response = CompanyEmployeeDto.class)
    private String findDealerList(@RequestParam(required = false) String mobile,@RequestParam(required = false) String name,@RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) String loginName,@RequestParam(required = false,defaultValue = "1")Integer pageNum,@RequestParam(required = false,defaultValue = "10")Integer pageSize){
        MapContext mapContext = this.createMapContent(name,UserType.DEALER.getValue(),mobile,null,status,loginName);
        JsonMapper jsonMapper = new JsonMapper();
        return jsonMapper.toJson(this.dealerFacade.findDealerList(mapContext,pageNum,pageSize));
    }

    /**
     * 修改经销商的用户信息
     * @param mapContext
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改经销商的用户信息",notes = "修改经销商的用户信息")
    private String updateDealer(@RequestBody MapContext mapContext,@PathVariable String id){
        RequestResult result = User.validateFields(mapContext);
        JsonMapper jsonMapper = new JsonMapper();
        if(result!=null){
            return jsonMapper.toJson(result);
        }
        return jsonMapper.toJson(this.dealerFacade.updateDealer(mapContext,id));
    }

    /**
     * 修改经销商绑定的手机号
     * @param id
     * @param mapContext
     * @return
     */
    @PutMapping("/{id}/account/number")
    @ApiOperation(value = "修改经销商绑定的手机号",notes = "修改经销商绑定的手机号")
    private String updateDealerMobile(@PathVariable String id,@RequestBody MapContext mapContext){
        JsonMapper jsonMapper = new JsonMapper();
        return jsonMapper.toJson(this.dealerFacade.updateDealerMobile(id,mapContext.getTypedValue("mobile",String.class)));
    }

    /**
     * 修改经销商登录密码
     * @param id
     * @param mapContext
     * @return
     */
    @PutMapping("/{id}/account/password")
    @ApiOperation(value = "修改经销商登录密码",notes = "修改经销商登录密码")
    private String updateDealerAccountPwd(@PathVariable String id,@RequestBody MapContext mapContext){
        JsonMapper jsonMapper = new JsonMapper();
        return jsonMapper.toJson(this.dealerFacade.updateDealerAccountPwd(id,mapContext.getTypedValue("newPassword",String.class)));
    }

    private MapContext createMapContent(String name, Integer type, String mobile, String no,Integer status,String loginName) {
        MapContext mapContext = MapContext.newOne();
        if (name != null) {
            mapContext.put(WebConstant.KEY_ENTITY_NAME, name);
        }
        if (type == null || type == -1) {
            mapContext.put("type", Arrays.asList(CompanyType.DIRECT_SHOP.getValue(), CompanyType.SHOP_IN_STORE.getValue(), CompanyType.EXCLUSIVE_SHOP.getValue(), CompanyType.FRANCHISED_STORE.getValue(), CompanyType.LOOSE_ORDER.getValue()));
        } else {
            mapContext.put("type", Arrays.asList(type));
        }
        if (mobile == null || mobile.trim().equals("")) {
            mapContext.put(WebConstant.KEY_ENTITY_MOBILE, null);
        } else {
            mapContext.put(WebConstant.KEY_ENTITY_MOBILE, mobile);
        }
        if (no == null || no.trim().equals("")) {
            mapContext.put(WebConstant.STRING_NO, null);
        } else {
            mapContext.put(WebConstant.STRING_NO, no);
        }
        if(status!=null){
            mapContext.put(WebConstant.KEY_ENTITY_STATUS,status);
        }
        if(loginName!=null&&!loginName.trim().equals("")){
            mapContext.put("loginName",loginName);
        }
        return mapContext;
    }

}
