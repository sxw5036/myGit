package com.lwxf.industry4.webapp.controller.app.factory.factoryuser;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.LoginUtil;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.userPermission.UserPermissionFacade;
import com.lwxf.mybatis.utils.MapContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：zhangxiaolin(Mister_pan@126.com)
 * @create：2019/3/26 13:14
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Api(value="UserPermissionController",tags={"F端APP接口:用户菜单权限控制"})
@RestController("UserPermissionController")
@RequestMapping(value = "/app/f/userPermission",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class UserPermissionController {

    @Resource(name = "userPermissionFacade")
    private UserPermissionFacade userPermissionFacade;

    /**
     * 查询角色下的全部模块权限
     * @return
     */
    @GetMapping("/modules")
    @ApiOperation(value = "查询当前用户的模块权限", notes = "")
    private String getRoleModulesPermission(HttpServletRequest request){
        String atoken=request.getHeader("X-ATOKEN");
        String cid=request.getHeader("X-CID");
        JsonMapper jsonMapper = new JsonMapper();
        //根据token查询出角色，再根据角色查询出操作
        MapContext mapInfo = LoginUtil.checkLogin(atoken);
        String uid =mapInfo.get("userId").toString();
        if(uid==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        return jsonMapper.toJson(this.userPermissionFacade.getRoleModulesPermissions(uid,cid));
    }

    /**
     * 查询模块下全部菜单权限
     * @return
     */
    @GetMapping("/modules/{moduleKey}/menus")
    @ApiOperation(value = "获得该页面的操作权限", notes = "")
    private String getRoleMenusPermission(@PathVariable String moduleKey, HttpServletRequest request){
        String atoken=request.getHeader("X-ATOKEN");
        String cid=request.getHeader("X-CID");
        JsonMapper jsonMapper = new JsonMapper();
        //根据token查询出角色，再根据角色查询出操作
        MapContext mapInfo = LoginUtil.checkLogin(atoken);
        String uid =mapInfo.get("userId").toString();
        if(uid==null){
            return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
        }
        return jsonMapper.toJson(this.userPermissionFacade.getRoleMenusPermissions(moduleKey,uid,cid));
    }
}
