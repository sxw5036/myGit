package com.lwxf.industry4.webapp.domain.dao.system;


import java.util.List;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.entity.system.Role;


/**
 * 功能：
 * 
 * @author：dongshibo(zjl869319827@outlook.com)
 * @created：2018-12-07 14:10:48
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@IBatisSqlTarget
public interface RoleDao extends BaseDao<Role, String> {

	PaginatedList<Role> selectByFilter(PaginatedFilter paginatedFilter);

	Role selectByKey(String key);

	List<Role> findListByType(Integer type,String key);


	Role findRoleByCidAndUid(String userId,String companyId);



	List<Role> findAllFactoryRole();

	Role findRoleByKey(String key);
}