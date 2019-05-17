package com.lwxf.industry4.webapp.controller.app.dealer.customer;

import io.rong.models.response.TokenResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.baseservice.rongcloud.RongCloudUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.dto.UserInfoObj;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.customer.CustomerFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：客户管理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/4 0004 10:12
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value="/app/b/companies/{companyId}", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class CustomerController extends BaseDealerController {
	@Resource(name="customerFacade")
    private CustomerFacade customerFacade;
//客户列表查询
	@GetMapping("/customers")
	public String selectCustomersList(@RequestParam(required = false) Integer pageNum,
											 @RequestParam(required = false) Integer pageSize,
											 @RequestParam(required = false) String customerManager,
											 @RequestParam(required = false) String condition,
											 @PathVariable String companyId,
									         HttpServletRequest request
											 ){

		if(null == pageSize){
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if(null == pageNum){
			pageNum = 1;
		}

		JsonMapper resultMapper = new JsonMapper();
		String xp="bcustomermng-custombase-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
		MapContext mapContext=MapContext.newOne();
		mapContext.put("companyId",companyId);
		if(LwxfStringUtils.isNotBlank(customerManager)) {
			mapContext.put("customerManager", customerManager);
		}
       if(LwxfStringUtils.isNotBlank(condition)){
       	mapContext.put("condition",condition);
	   }

		RequestResult result=this.customerFacade.selectCustomersList(mapContext,pageNum,pageSize,request);

		return resultMapper.toJson(result);


	}
//客户信息修改
	@PutMapping("/customers/{userId}")


	public RequestResult updateCustomer(@PathVariable String companyId,
										@PathVariable String userId,
										@RequestBody MapContext mapContext,
										HttpServletRequest request
										){
		String xp="bcustomermng-custombase-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		return this.customerFacade.updateCustomer(companyId,userId,mapContext,request);
	}

//客户信息添加
	@PostMapping(value = "/customers")
	public String  addCustomer(@PathVariable String companyId,
							   @RequestBody MapContext mapContext,
							  HttpServletRequest request){
		JsonMapper resultMapper =new JsonMapper();
		String xp="bcustomermng-custombase-edit";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
		String uid=request.getHeader("X-UID");
		String cid=request.getHeader("X-CID");
		RequestResult result = this.customerFacade.addCustomer(companyId,mapContext,uid,cid);

		//处理融云token
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

		return resultMapper.toJson(result);
	}

	//客户删除
	@DeleteMapping(value = "/customers/{userId}")
	public RequestResult deleteCustomer(@PathVariable String userId,
										HttpServletRequest request){

		String xp="bcustomermng-custombase-update_status";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		return this.customerFacade.deleteCustomer(userId);

	}
	//客户详情
	@GetMapping("/customers/{userId}")
	public String findCustomerMessageById(@PathVariable String companyId,
										  @PathVariable String userId,
										  HttpServletRequest request){
		JsonMapper resultMapper=new JsonMapper();
		String xp="bcustomermng-custombase-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
		RequestResult result=this.customerFacade.findCustomerMessageById(companyId,userId,request);

		return resultMapper.toJson(result);
	}
	@GetMapping("/customers/count")
	public String customersCount(@PathVariable String companyId,
								 @RequestParam(required = false) String selectTime,
								 @RequestParam(required = false) String saleMan,
								 HttpServletRequest request){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		//判断用户权限
		String xp="bassemblemng-dispatchlistinfo-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return jsonMapper.toJson(result1);
		}
		if(!companyId.equals(request.getHeader("X-CID"))){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(ErrorCodes.BIZ_IS_NOT_ORG_MEMBER_10004,AppBeanInjector.i18nUtil.getMessage("BIZ_IS_NOT_ORG_MEMBER_10004")));
		}
		MapContext mapContext=MapContext.newOne();
		mapContext.put("companyId",companyId);
		if(LwxfStringUtils.isNotBlank("saleMan")){
			mapContext.put("saleMan",saleMan);
		}
		if(LwxfStringUtils.isNotBlank("selectTime")){
			mapContext.put("selectTime",selectTime);
		}
		RequestResult result=this.customerFacade.findCustomersCount(mapContext,request);
		return jsonMapper.toJson(result);
	}
}
