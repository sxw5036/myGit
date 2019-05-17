package com.lwxf.industry4.webapp.facade.app.factory.userPermission.impl;

import com.lwxf.industry4.webapp.bizservice.system.RolePermissionService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.common.enums.system.MenuKeyForAppFinance;
import com.lwxf.industry4.webapp.common.enums.system.ModuleKeyForApp;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.system.RolePermission;
import com.lwxf.industry4.webapp.facade.app.factory.userPermission.UserPermissionFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
@Component(value = "userPermissionFacade")
public class UserPermissionFacadeImpl extends BaseFacadeImpl implements UserPermissionFacade {

    @Resource(name = "rolePermissionService")
    private RolePermissionService rolePermissionService;
    @Resource(name = "roleService")
    private RoleService roleService;

//    @Override
//    public RequestResult getRoleMenuPermissions(String userId,String companyId) {
//        List<MapContext> menuList = new ArrayList<>();
//        Role role = roleService.findRoleByCidAndUid(userId,companyId);
//        if(role==null){
//            for(MenuKeyForApp key : MenuKeyForApp.values()){
//                MapContext mapPermission = MapContext.newOne();
//                mapPermission.put("menuKey",key.getValue());
//                mapPermission.put("value",0);
//                menuList.add(mapPermission);
//            }
//        }else{
//            List<RolePermission> RolePermissionList =rolePermissionService.selectRolePermissionList(role.getId());
//            //去掉重复的菜单
//            int count =0;
//            for(MenuKeyForApp key : MenuKeyForApp.values()){
//                MapContext mapPermission = MapContext.newOne();
//                count =0;
//                for(RolePermission rp : RolePermissionList){
//                    if(key.getValue().equals(rp.getMenuKey())){
//                        count++;
//                    }
//                }
//                if(count==0){
//                    mapPermission.put("menuKey",key.getValue());
//                    mapPermission.put("value",0);
//                }else{
//                    mapPermission.put("menuKey",key.getValue());
//                    mapPermission.put("value",1);
//                }
//                menuList.add(mapPermission);
//            }
//        }
//
//        return ResultFactory.generateRequestResult(menuList);
//    }

    @Override
    public RequestResult getRoleModulesPermissions(String userId,String companyId) {
        List<MapContext> menuList = new ArrayList<>();
        Role role = roleService.findRoleByCidAndUid(userId,companyId);
        if(role==null){
            for(ModuleKeyForApp key : ModuleKeyForApp.values()){
                MapContext mapPermission = MapContext.newOne();
                mapPermission.put("moduleKey",key.getValue());
                mapPermission.put("permission",0);
                menuList.add(mapPermission);
            }
        }else{
            List<RolePermission> RolePermissionList =rolePermissionService.selectRolePermissionList(role.getId());
            //去掉重复的菜单
            int count =0;
            for(ModuleKeyForApp key : ModuleKeyForApp.values()){
                MapContext mapPermission = MapContext.newOne();
                count =0;
                for(RolePermission rp : RolePermissionList){
                   // System.out.println("++++++key.getValue():"+key.getValue()+"rp.getModuleKey():"+rp.getModuleKey());
                    if(key.getValue().equals(rp.getModuleKey())){
                        count++;
                    }
                }
                if(count==0){
                    mapPermission.put("moduleKey",key.getValue());
                    mapPermission.put("permission",0);
                }else{
                    mapPermission.put("moduleKey",key.getValue());
                    mapPermission.put("permission",1);
                }
                menuList.add(mapPermission);
            }
        }

        return ResultFactory.generateRequestResult(menuList);
    }

    @Override
    public RequestResult getRoleMenusPermissions(String moduleKey,String userId,String companyId) {
        Role role = roleService.findRoleByCidAndUid(userId,companyId);
        List<MapContext> menuList = new ArrayList<>();
        //bug先做简单处理 以后要修改
        List<String> list = new ArrayList<>();
        if(role==null){
            for(MenuKeyForAppFinance key : MenuKeyForAppFinance.values()){
                MapContext mapPermission = MapContext.newOne();
                mapPermission.put("menuKey",key.getValue());
                mapPermission.put("permission",0);
                menuList.add(mapPermission);
            }
        }else{
            List<RolePermission> RolePermissionList =rolePermissionService.selectRolePermissionList(role.getId());
            //去掉重复的菜单
            int count =0;
            if(moduleKey.equals("financingmng")) {
                for (MenuKeyForAppFinance key : MenuKeyForAppFinance.values()) {
                    MapContext mapPermission = MapContext.newOne();
                    count = 0;
                    for (RolePermission rp : RolePermissionList) {
                        if (key.getValue().equals(rp.getMenuKey())) {
                            count++;
                        }
                    }
                    if (count == 0) {
                        mapPermission.put("menuKey", key.getValue());
                        mapPermission.put("permission", 0);
                    } else {
                        mapPermission.put("menuKey", key.getValue());
                        mapPermission.put("permission", 1);
                    }
                    menuList.add(mapPermission);
                }
            }
        }

        return ResultFactory.generateRequestResult(menuList);
    }
}
