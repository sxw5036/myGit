package com.lwxf.industry4.webapp.controller.app.common;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.common.UserLoginFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/29 9:29
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class userLoginController {
    @Resource(name = "userLoginFacade")
    private UserLoginFacade userLoginFacade;

    /**
     * 用户手机号登录（经销商和经销商员工）
     *
     * @param userMap
     * @return
     */
    @PostMapping(value = "/users/login")
    public String userLogin(@RequestBody MapContext userMap) {
        RequestResult result = this.userLoginFacade.userLogin(userMap);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }

    /**
     * 登录名（邮箱/手机/登录名 ）和密码登录
     *
     * @param userMap
     * @return
     */
    @PostMapping(value = "/users/password/login")
    public String userPasswordlogin(@RequestBody MapContext userMap) {
        RequestResult result = this.userLoginFacade.userPasswordLogin(userMap);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }




}
