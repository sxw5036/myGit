package com.lwxf.industry4.webapp.controller.admin.factory.system;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.system.PermissionShow;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.system.RolePermission;
import com.lwxf.industry4.webapp.facade.admin.factory.system.RoleFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/14/014 15:41
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/roles",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class RoleController {
	@Resource(name = "roleFacade")
	private RoleFacade roleFacade;

//	/**
//	 * 查询工厂全部角色
//	 * @return
//	 */
//	@GetMapping
//	private RequestResult findAllRoles(){
//		return this.roleFacade.findAllRoles();
//	}

	/**
	 * 根据类型查询角色列表
	 * @param type
	 * @return
	 */
	@GetMapping
	private RequestResult findRolesByType(@RequestParam(required = false) Integer type){
		return this.roleFacade.findListByType(type,null);
	}
	/**
	 * 新增角色
	 * @param role
	 * @return
	 */
	@PostMapping
	private RequestResult addRoles(@RequestBody Role role){
		RequestResult result = role.validateFields();
		if(result!=null){
			return result;
		}
		return this.roleFacade.addRoles(role);
	}

	/**
	 * 修改角色信息(名称 以及 是否是管理权限)
	 * @param mapContext
	 * @param id
	 * @return
	 */
	@PutMapping("{id}")
	private RequestResult updataRoles(@RequestBody MapContext mapContext,@PathVariable String id){
		RequestResult result = Role.validateFields(mapContext);
		if(result!=null){
			return result;
		}
		return this.roleFacade.updataRoles(mapContext,id);
	}

	/**
	 * 删除角色
	 * @param id
	 * @return
	 */
	@DeleteMapping("{id}")
	private RequestResult deleteRoles(@PathVariable String id){
		return this.roleFacade.deleteRoles(id);
	}

	/**
	 * 查询角色下的全部权限
	 * @param id
	 * @return
	 */
	@GetMapping("{id}/permissions")
	private RequestResult findRolePermission(@PathVariable String id){
		return this.roleFacade.findRolePermission(id);
	}

	/**
	 * 修改角色下权限信息
	 * @param id
	 * @param rolePermissions
	 * @return
	 */
	@PutMapping("{id}/permissions")
	private RequestResult updateRoleMenus(@PathVariable String id, @RequestBody List<RolePermission> rolePermissions){
		for(RolePermission rolePermission: rolePermissions){
			rolePermission.setRoleId(id);
			rolePermission.setShow(PermissionShow.SHOW.getValue());
			RequestResult result = rolePermission.validateFields();
			if(result!=null){
				return result;
			}
		}
		return this.roleFacade.updateRoleMenus(id,rolePermissions);
	}

	/**
	 * 根据分类查询全部按钮菜单
	 * @param type
	 * @return
	 */
	@GetMapping("all")
	private RequestResult findAllByType(@RequestParam Integer type){
		return this.roleFacade.findAllByType(type);
	}
}
