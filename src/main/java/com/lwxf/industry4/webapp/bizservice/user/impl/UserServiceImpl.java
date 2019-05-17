package com.lwxf.industry4.webapp.bizservice.user.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.domain.dto.user.UserAreaDto;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Service;

import com.lwxf.industry4.webapp.bizservice.base.BaseServiceImpl;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.domain.dao.user.UserDao;
import com.lwxf.industry4.webapp.domain.dto.user.UserDto;
import com.lwxf.industry4.webapp.domain.entity.user.User;

/**
 * 功能：user的service实现
 *
 * @author：renzhongshan(d3shan@126.com)
 * @create：2018-05-24 18:45:49
 * @version：2018 1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<User, String, UserDao> implements UserService {
	@Override
	@Resource(name = "userDao")
	public void setDao(UserDao userDao) {
		this.dao = userDao;
	}

    @Override
    public User findByEmail(String email) {
		return this.dao.selectByEmail(email);
	}

	@Override
	public User findByMobile(String mobile) {
		return this.dao.findByMobile(mobile);
	}

	@Override
    public PaginatedList<User> findByFilter(PaginatedFilter paginatedFilter) {
		return this.dao.selectByFilter(paginatedFilter);
	}

	@Override
	public User findByUserId(String userId) {
		return this.dao.selectByUserId(userId);
	}

	@Override
	public List<UserDto> findUserDtoListByUserIdList(String[] userIdList) {
		return this.dao.selectUserDtoListByUserIds(userIdList);
	}

	@Override
	public boolean isExistsByEmail(String email) {
		return this.dao.isExistsByEmail(email);
	}

	@Override
	public List<String> findAllUserId() {
		return this.dao.selectAllUserId();
	}

	@Override
	public User findByLoginName(String loginName) {
		return this.dao.findByLoginName(loginName);
	}
	@Override
	public List<User> findClerkListByStateAndRole(MapContext mapContext){
		return this.dao.findClerkListByStateAndRole(mapContext);
	}

	@Override
	public List<User> findUserInfoByUserIds(Collection<String> id) {
		return this.dao.findUserInfoByUserIds(id);
	}

	@Override
	public int updateUserName(String username) {
		MapContext mapContext = MapContext.newOne();
		mapContext.put("id", WebUtils.getCurrUserId());
		mapContext.put("username", username);
		return this.dao.updateUserName(mapContext);
	}

	@Override
	public Map<String, Object> findUserById(String id) {
		return this.dao.findUserById(id);
	}

	@Override
	public List<String> findIdByName(String name) {
		return this.dao.findIdByName(name);
	}

	@Override
	public PaginatedList<User> findUserListByLikeName(PaginatedFilter paginatedFilter) {
		return this.dao.findUserListByLikeName(paginatedFilter);
	}

	@Override
	public int addFollowers(String userId) {
		User user = this.dao.selectById(userId);
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_ENTITY_ID,userId);
		mapContext.put("followers",user.getFollowers()+1);
		return this.dao.updateByMapContext(mapContext);
	}

	@Override
	public int closeFollowers(String userId) {
		User user = this.dao.selectById(userId);
		MapContext mapContext = MapContext.newOne();
		mapContext.put(WebConstant.KEY_ENTITY_ID,userId);
		mapContext.put("followers",user.getFollowers()-1);
		return this.dao.updateByMapContext(mapContext);
	}

	@Override
	public List<User> selectCompanyShopkeeperByCompanyIds(List ids,String roleKey) {
		return this.dao.selectCompanyShopkeeperByCompanyIds(ids,roleKey);
	}

	@Override
	public List<User> selectByUserIdList(List<String> ids) {

		return this.dao.selectByUserIdList(ids);
	}

	@Override
	public UserAreaDto findUserMessageById(String userId) {
		return this.dao.findUserMessageById(userId);
	}

	@Override
	public int add(User entity) {
		if(LwxfStringUtils.isBlank(entity.getName())){
			entity.setName("我是谁？");
		}
		return super.add(entity);
	}

	@Override
	public UserAreaDto selectUserAreaDtoById(String id) {
		return this.dao.selectUserAreaDtoById(id);
	}

	@Override
	public List<Map<String, String>> findEmpIdAndEmpNameByCid(MapContext params) {
		return this.dao.findEmpIdAndEmpNameByCid(params);
	}


	@Override
	public UserAreaDto findCityNameByUID(String userId) {
		return this.dao.findCityNameByUID(userId);
	}



	@Override
	public List<UserAreaDto> findCustomerByCompanyIdAndCustomer(MapContext mapContext) {
		return this.dao.findCustomerByCompanyIdAndCustomer(mapContext);
	}

	@Override
	public List<User> findByUserIds(List<String> values) {
		return this.dao.findByUserIds(values);
	}

	@Override
	public PaginatedList<UserAreaDto> findByClient(PaginatedFilter paginatedFilter) {
		return this.dao.findByClient(paginatedFilter);
	}
	@Override
	public UserAreaDto findByUid(String userId) {
		return this.dao.findByUid(userId);
	}

	@Override
	public List<UserAreaDto> findCustInfoByCompanyIdAndStatus(MapContext mapContext) {
		return this.dao.findCustInfoByCompanyIdAndStatus(mapContext);
	}

	@Override
	public PaginatedList<User> findEmpInfoByCompanyIdAndStatus(PaginatedFilter paginatedFilter) {
		return this.dao.findEmpInfoByCompanyIdAndStatus(paginatedFilter);
	}


	@Override
	public PaginatedList<UserAreaDto> findShareMemberByPidAndStatusAndIdentity(PaginatedFilter paginatedFilter) {
		return this.dao.findShareMemberByPidAndStatusAndIdentity(paginatedFilter);
	}

	@Override
	public Map factoryUserPersonalInfo(MapContext params) {
		return this.dao.factoryUserPersonalInfo(params);
	}

	@Override
	public List<MapContext> findFactoryUserDeptName(String companyId,String userId) {
		return this.dao.findFactoryUserDeptName(companyId,userId);
	}

	@Override
	public Map findFactoryUserAccountInfo(String companyId, String userId) {
		return this.dao.findFactoryUserAccountInfo(companyId,userId);
	}
}
