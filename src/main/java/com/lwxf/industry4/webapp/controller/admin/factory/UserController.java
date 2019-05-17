package com.lwxf.industry4.webapp.controller.admin.factory;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.user.UserState;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.shiro.ShiroUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.user.LoginedUser;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.user.UserFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/11/30/030 11:10
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/users",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class UserController {
	@Resource(name = "userFacade")
	private UserFacade userFacade;

	/**
	 * F端用户登录接口
	 * @param userMap
	 * @param request
	 * @return
	 */
	@PostMapping("login")
	public RequestResult userLogin(@RequestBody MapContext userMap, HttpServletRequest request){
		LoginedUser currUser = WebUtils.getCurrUser();
		if (currUser != null) {
			String loginName = userMap.getTypedValue("loginName", String.class);
			if((LwxfStringUtils.isEquals(loginName,currUser.getMobile(),true) || LwxfStringUtils.isEquals(loginName,currUser.getEmail(),true))){
				if(currUser.getState()==UserState.ENABLED.getValue()) {
					CompanyEmployee employee = currUser.getCompanyEmployee();
					boolean isMember = null == employee || !employee.getCompanyId().equals(WebUtils.getCurrCompanyId());
					RequestResult result = ResultFactory.generateSuccessResult();
					if (isMember) {
						result.put("go", WebConstant.REDIRECT_PATH_404);
					} else {
						result.put(WebConstant.KEY_ENTITY_COMPANY_ID, employee.getCompanyId());
						result.put("go", WebConstant.REDIRECT_PATH_FACTORY_ADMIN);
					}
					return result;
				}
			}else{
				ShiroUtil.logoutCurrUser();
			}
		}
		return this.userFacade.toFactoryLogin(userMap,request);
	}

	/**
	 * 修改用户密码
	 * @param mapContext
	 * @return
	 */
	@PutMapping("password")
	public RequestResult updateUserPassword(@RequestBody MapContext mapContext){
		return this.userFacade.updateUserPassword(mapContext);
	}

	/**
	 * 修改当前登录用户信息
 	 * @param mapContext
	 * @return
	 */
	@PutMapping("info")
	public RequestResult updateUserInfo(@RequestBody MapContext mapContext){
		RequestResult result = User.validateFields(mapContext);
		if (result!=null){
			return result;
		}
		return this.userFacade.updateUser(mapContext);
	}

	@GetMapping("info")
	public RequestResult findUserInfo(){
		return this.userFacade.findUserInfo(WebUtils.getCurrUserId());
	}
}
