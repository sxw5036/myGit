package com.lwxf.industry4.webapp.controller.admin.factory.system;

import javax.annotation.Resource;

import java.util.Arrays;

import org.springframework.web.bind.annotation.*;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.entity.system.Operations;
import com.lwxf.industry4.webapp.facade.admin.factory.system.OperationsFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2019/1/5/005 14:58
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/operations",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class OperationsController {
	@Resource(name = "operationsFacade")
	private OperationsFacade operationsFacade;

	/**
	 * 查询操作按钮列表
	 * @param name
	 * @param key
	 * @param type
	 * @return
	 */
	@GetMapping
	private RequestResult findOperationList(@RequestParam(required = false) String name,@RequestParam(required = false) String key,@RequestParam(required = false) Integer type){
		MapContext mapContext = this.createMapContext(name,key,type);
		return this.operationsFacade.findOperationList(mapContext);
	}

	/**
	 * 新增操作按钮
	 * @param operations
	 * @return
	 */
	@PostMapping
	private RequestResult addOperation(@RequestBody Operations operations){
		RequestResult result = operations.validateFields();
		if(result!=null){
			return result;
		}
		return this.operationsFacade.addOperation(operations);
	}

	/**
	 * 删除操作按钮
	 * @param id
	 * @return
	 */
	@DeleteMapping("{id}")
	private RequestResult deleteById(@PathVariable String id){
		return this.operationsFacade.deleteById(id);
	}

	/**
	 * 修改按钮信息
	 * @param id
	 * @param mapContext
	 * @return
	 */
	@PutMapping("{id}")
	private RequestResult updateOperations(@PathVariable String id,@RequestBody MapContext mapContext){
		RequestResult result = Operations.validateFields(mapContext);
		if(result!=null){
			return result;
		}
		return this.operationsFacade.updateOperations(id,mapContext);
	}

	private MapContext createMapContext(String name, String key, Integer type) {
		MapContext mapContext = new MapContext();
		if(name!=null&&!name.trim().equals("")){
			mapContext.put(WebConstant.KEY_ENTITY_NAME,name);
		}
		if(key!=null&&!key.trim().equals("")){
			mapContext.put("key",key);
		}
		if(type!=null){
			mapContext.put("types", Arrays.asList(type));
		}
		return mapContext;
	}
}
