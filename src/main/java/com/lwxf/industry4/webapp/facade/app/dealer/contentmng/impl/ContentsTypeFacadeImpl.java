package com.lwxf.industry4.webapp.facade.app.dealer.contentmng.impl;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.stereotype.Component;

import com.lwxf.industry4.webapp.bizservice.contentmng.ContentsTypeService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.entity.contentmng.ContentsType;
import com.lwxf.industry4.webapp.facade.app.dealer.contentmng.ContentsTypeFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/3/29 0029 13:09
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "contentsTypeFacade")
public class ContentsTypeFacadeImpl extends BaseFacadeImpl implements ContentsTypeFacade {
	@Resource(name = "contentsTypeService")
	private ContentsTypeService contentsTypeService;
	@Override
	public RequestResult findContentsTypeList() {
		List<ContentsType> contentsType=this.contentsTypeService.findContentsTypeList();
		return ResultFactory.generateRequestResult(contentsType);
	}
}
