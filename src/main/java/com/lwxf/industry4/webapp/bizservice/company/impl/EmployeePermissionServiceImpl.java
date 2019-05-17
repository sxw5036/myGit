package com.lwxf.industry4.webapp.bizservice.company.impl;


import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Component;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.entity.system.RolePermission;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.lwxf.industry4.webapp.bizservice.base.BaseServiceImpl;
import com.lwxf.industry4.webapp.domain.dao.company.EmployeePermissionDao;
import com.lwxf.industry4.webapp.bizservice.company.EmployeePermissionService;
import com.lwxf.industry4.webapp.domain.entity.company.EmployeePermission;


/**
 * 功能：
 * 
 * @author：dongshibo(F_baisi@163.com)
 * @created：2018-12-05 11:24:08
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Service("employeePermissionService")
public class EmployeePermissionServiceImpl extends BaseServiceImpl<EmployeePermission, String, EmployeePermissionDao> implements EmployeePermissionService {
	@Resource
	@Override
	public void setDao( EmployeePermissionDao employeePermissionDao) {
		this.dao = employeePermissionDao;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<EmployeePermission> selectByFilter(PaginatedFilter paginatedFilter) {
		//
		return this.dao.selectByFilter(paginatedFilter) ;
	}

	@Override
	public List<EmployeePermission> findListByEmployeeId(String employeeId) {
		return this.dao.findListByEmployeeId(employeeId);
	}

	@Override
	public int addList(List<RolePermission> rolePermissionList) {
		return this.dao.addList(rolePermissionList);
	}

	@Override
	public int deleteByEmployeeId(String eid) {
		return this.dao.deleteByEmployeeId(eid);
	}

	@Override
	public EmployeePermission findByEmpIdAndkey(String employeeId,String moduleKey, String menuKey) {
		return this.dao.findByEmpIdAndkey(employeeId,moduleKey,menuKey);
	}

	@Override
	public List<EmployeePermission> findByMenusKey(String key) {
		return this.dao.findByMenusKey(key);
	}

	@Override
	public int deleteByKey(String key) {
		return this.dao.deleteByKey(key);
	}
}