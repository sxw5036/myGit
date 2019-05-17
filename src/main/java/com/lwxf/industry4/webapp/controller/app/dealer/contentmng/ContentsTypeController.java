package com.lwxf.industry4.webapp.controller.app.dealer.contentmng;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.app.dealer.contentmng.ContentsTypeFacade;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/3/29 0029 9:57
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b/contenttypes", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class ContentsTypeController {
	@Resource(name = "contentsTypeFacade")
	private ContentsTypeFacade contentsTypeFacade;

	/**
	 * 查询分类列表
	 * @return
	 */
	@GetMapping
	public String findContentsTypeList(){
		JsonMapper jsonMapper=JsonMapper.createAllToStringMapper();
		RequestResult result=this.contentsTypeFacade.findContentsTypeList();
		return jsonMapper.toJson(result);
	}
}
