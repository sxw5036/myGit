package com.lwxf.industry4.webapp.controller.app.factory.factoryCustomer;

import io.rong.models.response.TokenResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.baseservice.rongcloud.RongCloudUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.dto.UserInfoObj;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryCustomer.FactoryCustomerFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：C端管理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/2 0002 9:15
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/f", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryCustomerController {
	@Resource(name = "factoryCustomerFacade")
    private FactoryCustomerFacade factoryCustomerFacade;

	/**
	 * C端客户概览
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/customers/count")
	public String findFactoryCustomersCount(@PathVariable String companyId,
			                                HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		String uid=request.getHeader("X-UID");
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		RequestResult result=this.factoryCustomerFacade.findFactoryCustomersCount();
		return jsonMapper.toJson(result);
	}

	/**
	 * 根据区域名称 或 姓名 或 手机号查询客户
	 * 客户来源 所属经销商
	 * @param mergerName
	 * @param condition
	 * @param request
	 *  @param pageNum
	 *  @param pageSize
	 * @return
	 */
	@GetMapping("/companies/{companyId}/customers")
	public String findCustomers(@RequestParam(required = false) Integer pageNum,
								@RequestParam(required = false) Integer pageSize,
								@RequestParam(required = false) String mergerName,
								@RequestParam(required = false) String condition,
								@RequestParam(required = false) String source,
								@RequestParam(required = false) String dealerName,
								@RequestParam(required = false) String orderNo,
								@RequestParam(required = false) String topParam,
								@RequestParam(required = false) String order,
								@RequestParam(required = false) String sort,
								@PathVariable String companyId,
								HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		if (null == pageSize) {
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if (null == pageNum) {
			pageNum = 1;
		}
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		MapContext params=MapContext.newOne();
		if(LwxfStringUtils.isNotBlank(mergerName)){
			params.put("mergerName",mergerName);
		}
		if(LwxfStringUtils.isNotBlank(condition)){
			params.put("condition",condition);
		}
		if(LwxfStringUtils.isNotBlank(source)){
			params.put("source",source);
		}
		if(LwxfStringUtils.isNotBlank(dealerName)){
			params.put("dealerName",dealerName);
		}
		if(LwxfStringUtils.isNotBlank(orderNo)){
			params.put("orderNo",orderNo);
		}
		if(LwxfStringUtils.isNotBlank(topParam)){
			params.put("topParam",topParam);
		}
		if(LwxfStringUtils.isNotBlank(order)){
			params.put("order",order);
		}
		if(LwxfStringUtils.isNotBlank(sort)){
			params.put("sort",sort);
		}
       RequestResult result=this.factoryCustomerFacade.findCustomers(pageNum,pageSize,params);
		return jsonMapper.toJson(result);

	}

	/**
	 * 客户详情
	 * @param userId
	 * @param customerId
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/dealers/{customerId}/customers/{userId}")
	public String customerInfo(@PathVariable String userId,
							   @PathVariable String companyId,
							   @PathVariable String customerId,
							   HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		RequestResult result=this.factoryCustomerFacade.findCustomerInfo(userId,customerId);
		return jsonMapper.toJson(result);
	}

	/**
	 * 客户所下的订单列表信息
	 * @param userId
	 * @param companyId
	 * @param customerId
	 * @param request
	 * @return
	 */
	@GetMapping("/companies/{companyId}/dealers/{customerId}/customers/{userId}/orderInfo")
	public String findCustomerOrderInfo(@PathVariable String userId,
										@PathVariable String companyId,
										@PathVariable String customerId,
										HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		RequestResult result=this.factoryCustomerFacade.findCustomerOrderInfo(userId,customerId);
		return jsonMapper.toJson(result);
	}

	/**
	 * F端添加客户
	 * @param companyId
	 * @param mapContext
	 * @param request
	 * @return
	 */
	@PostMapping("/companies/{companyId}/dealers/{dealerId}/customers")
	public RequestResult factoryAddCustomer(@PathVariable String companyId,
			                                @PathVariable String dealerId,
			                                @RequestBody MapContext mapContext,
			                                HttpServletRequest request){
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		RequestResult result =this.factoryCustomerFacade.factoryAddCustomer(dealerId,mapContext,request);

		UserInfoObj data = (UserInfoObj) result.getData();
		if(data!=null){
			User user=data.getUser();
			TokenResult tokenResult = RongCloudUtils.registerUser(user);
			if(tokenResult!=null){
				String token = tokenResult.getToken();
				AppBeanInjector.userThirdInfoFacade.updateRongToken(user.getId(),token);
				UserThirdInfo userThirdInfo = data.getUserThirdInfo();
				userThirdInfo.setRongcloudToken(token);
			}
		}
		return result;
	}

	/**
	 * F端客户修改
	 * @param companyId
	 * @param dealerId
	 * @param userId
	 * @param mapContext
	 * @param request
	 * @return
	 */
	@PutMapping("/companies/{companyId}/dealers/{dealerId}/customers/{userId}")
	public RequestResult updateDealerCustomer(@PathVariable String companyId,
											  @PathVariable String dealerId,
											  @PathVariable String userId,
											  @RequestBody MapContext mapContext,
											  HttpServletRequest request){
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		return this.factoryCustomerFacade.updateDealerCustomer(dealerId,userId,mapContext);
	}
	/**
	 * 某个经销商下的员工列表
	 */
	@GetMapping("/companies/{companyId}/dealers/{dealerId}/employees")
	public RequestResult findCompanyEmployees(@PathVariable String companyId,
									          @PathVariable String dealerId,
									          HttpServletRequest request){
		String uid=request.getHeader("X-UID");
		String atoken=request.getHeader("X-ATOKEN");
		if(!LoginUtil.isLogin(uid,atoken)){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED"));
		}
		String cid=request.getHeader("X-CID");
		if(!cid.equals(companyId)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004"));
		}
		RequestResult result=this.factoryCustomerFacade.findCompanyEmployees(dealerId);
		return  result;
	}
}
