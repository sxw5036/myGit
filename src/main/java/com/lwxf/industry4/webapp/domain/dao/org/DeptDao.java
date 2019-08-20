package com.lwxf.industry4.webapp.domain.dao.org;


import java.util.List;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.dept.DeptDto;
import com.lwxf.industry4.webapp.domain.dto.dept.EmployeeDeptDto;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.entity.org.Dept;


/**
 * 功能：
 * 
 * @author：F_baisi(F_baisi@163.com)
 * @created：2018-12-08 15:14:31
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@IBatisSqlTarget
public interface DeptDao extends BaseDao<Dept, String> {

	PaginatedList<Dept> selectByFilter(PaginatedFilter paginatedFilter);

	Dept findDeptByNameAndParentId(String name,String parentId,String branchId);

	Dept findDeptByKey(String key,String branchId);

	Dept selectByEmpId(String empId);

	List<Dept> selectDeptByCompanyIdAndParentId(String companyId, String parentId);

	Dept findOneByKeyOrNameAndCompanyId(MapContext mapContext);

	List<Dept> findListByCompanyId(String companyId);

	List<Dept> findAll(String companyId);

	EmployeeDeptDto findOneByUserId(String id);

	List<Dept> findListByParentId(String id);

	List<Dept> findListByEmployeeId(String id);

	List<Dept> findListByCompanyIdAndParent(String companyId);

	List<Dept> findListByUserId(String currUserId);
}