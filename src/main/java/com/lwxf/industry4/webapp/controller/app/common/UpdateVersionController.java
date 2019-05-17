package com.lwxf.industry4.webapp.controller.app.common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.dto.version.VersionDto;
import com.lwxf.industry4.webapp.facade.app.common.version.UpdateVersionFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：公共接口:版本管理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/5/6 0006 10:17
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Api(value="UpdateVersionController",tags={"公共接口:版本管理"})
@RestController
@RequestMapping(value = "/app/commons", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class UpdateVersionController {
	@Resource(name = "updateVersionFacade")
	private UpdateVersionFacade updateVersionFacade;

	/**
	 * 查询版本信息
	 * @param sysType
	 * @param platform
	 * @return
	 */
	@ApiOperation(value = "获取版本信息", notes = "",response = VersionDto.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sysType",value = "系统类型:0：工业4.0整合版，1：工业4.0F端，2：工业4.0B端，3：工业4.0C端",dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "platform",value = "平台,0:ios,1:android，2：PC端",dataType = "Integer", paramType = "query")
	})
	@GetMapping("/updateVersions")
	public String findVersion(@RequestParam Integer sysType,
							  @RequestParam Integer platform){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		RequestResult result=this.updateVersionFacade.findVersionNo(sysType,platform);
		return jsonMapper.toJson(result);

	}

	/**
	 * 添加版本信息
	 * @param mapContext
	 * @return
	 */
	@ApiOperation(value = "添加版本信息", notes = "添加版本信息")
	@PostMapping("/updateVersions")
	public RequestResult addVersion(@RequestBody MapContext mapContext){
		return this.updateVersionFacade.addVersion(mapContext);
	}
	/**
	 * 更新版本信息
	 * @param mapContext
	 * @return
	 */
	@ApiOperation(value = "更新版本信息", notes = "更新版本信息")
	@PutMapping("/updateVersions")
	public RequestResult updateVersion(@RequestBody MapContext mapContext){
		return this.updateVersionFacade.updateVersion(mapContext);

	}
}
