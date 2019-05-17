package com.lwxf.industry4.webapp.facade.app.factory.userPermission;

import com.lwxf.industry4.webapp.common.result.RequestResult;

public interface UserPermissionFacade {

    RequestResult getRoleModulesPermissions(String userId,String companyId);

    RequestResult getRoleMenusPermissions(String moduleKey,String userId,String companyId);


}
