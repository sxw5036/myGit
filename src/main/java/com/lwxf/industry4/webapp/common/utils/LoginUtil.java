package com.lwxf.industry4.webapp.common.utils;

import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.company.EmployeePermissionService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.user.UserThirdInfoService;
import com.lwxf.industry4.webapp.common.enums.user.UserState;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.company.EmployeePermission;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;

import com.lwxf.mybatis.utils.MapContext;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/9/25 11:08
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component
public class LoginUtil {

    @Autowired
    private UserThirdInfoService userThirdInfoService;
    @Autowired
    private CompanyEmployeeService companyEmployeeService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmployeePermissionService employeePermissionService;

    private static LoginUtil isLogin;


    @PostConstruct
    public void init(){
        isLogin = this;
        isLogin.userThirdInfoService = this.userThirdInfoService;
        isLogin.userService = this.userService;
        isLogin.companyEmployeeService = this.companyEmployeeService;
        isLogin.employeePermissionService = this.employeePermissionService;

    }


    public static  boolean isLogin (String uid,String atoken){
//        return true;
        String cid = "";
        String xp = "";
        boolean b = false; //true 为登录， false为没有登录或登录错误
        if (LwxfStringUtils.isBlank(uid)||LwxfStringUtils.isBlank(atoken)){
            return b;
        }
        User user = isLogin.userService.findById(uid);
        if (null==user){
            return b;
        }
        Integer state = user.getState();
        boolean contains = UserState.contains(state);
        if (!contains||state==0){
            return b;
        }

        List<UserThirdInfo> userThirdInfos = isLogin.userThirdInfoService.findByAppTokenAndUserId(atoken,uid);
        if(userThirdInfos.size()!=1){
            return b;
        }

//        CompanyEmployee companyEmployee = isLogin.companyEmployeeService.findOneByCompanyIdAndUserId(cid, uid);
//        if (null==companyEmployee||companyEmployee.getStatus()>0){
//            return b;
//        }

//        String[] xps = LoginUtil.xp(xp);
//        String moduleKey = xps[0];
//        String menuKey= xps[1];
//        String operation= xps[2];

//        EmployeePermission employeePermission = isLogin.employeePermissionService.findByEmpIdAndkey(companyEmployee.getId(),moduleKey,menuKey);
//        if (null==employeePermission){
//            return b;
//        }

//        String operations = employeePermission.getOperations();
//        String[] split = operations.split(",");
//
//        for (int i=0;i<split.length;i++){
//            String s = split[i];
//            if (s==operation){
//                b=true;
//                return b;
//            }
//        }
        return b=true;
    }

    /**
     * 用户登录验证
     * @param atoken 唯一token
     * @return 返回用户id
     */
    public static MapContext checkLogin (String atoken) {
        if (LwxfStringUtils.isBlank(atoken)) {
            return null;
        }
        UserThirdInfo userThirdInfos = isLogin.userThirdInfoService.findByAppToken(atoken);
        if (userThirdInfos != null) {
            User user = isLogin.userService.findById(userThirdInfos.getUserId());
            if (null==user){
                return null;
            }
            Integer state = user.getState();
            boolean contains = UserState.contains(state);
            if (!contains||state==0){
                return null;
            }
            MapContext map = MapContext.newOne();
            map.put("userId",userThirdInfos.getUserId());
            map.put("branchId",userThirdInfos.getBranchId());
            map.put("companyId",userThirdInfos.getCompanyId());
            return map;
        }
        return null;
    }


//    public static String[] xp(String xp){
//        String[] split = xp.split("-");
//        return split;
//    }


}

