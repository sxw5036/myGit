package com.lwxf.industry4.webapp.controller.admin.factory.content;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.admin.factory.content.ContentFacade;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/5/15/015 16:33
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Api(value = "内容管理",tags = "内容管理")
@RestController
@RequestMapping(value = "/api/f/contents",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class ContentController {
	@Resource(name = "contentFacade")
	private ContentFacade contentFacade;

	@ApiOperation(value = "查询内容详情",notes = "返回值为三种 案例 (案例+内容数组) 活动 内容")
	@GetMapping("/{type}/{id}")
	private RequestResult findContentByTypeAndId(@PathVariable@ApiParam(value = "0 设计案例 1 活动 2 内容") Integer type, @PathVariable@ApiParam(value = "资源ID") String id){
		return this.contentFacade.findContentByTypeAndId(type,id);
	}
}
