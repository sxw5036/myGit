package com.lwxf.industry4.webapp.facade.app.dealer.contentmng.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

import org.springframework.stereotype.Component;

import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.contentmng.ContentsFilesService;
import com.lwxf.industry4.webapp.bizservice.contentmng.ContentsService;
import com.lwxf.industry4.webapp.bizservice.contentmng.ContentsTypeService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.dto.contentmng.ContentsDto;
import com.lwxf.industry4.webapp.domain.entity.contentmng.ContentsContent;
import com.lwxf.industry4.webapp.domain.entity.contentmng.ContentsFiles;
import com.lwxf.industry4.webapp.domain.entity.contentmng.ContentsType;
import com.lwxf.industry4.webapp.facade.app.dealer.contentmng.ContentsFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/3/27 0027 13:03
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("contentsFacade")
public class ContentsFacadeImpl extends BaseFacadeImpl implements ContentsFacade {
	@Resource(name="contentsService")
	private ContentsService contentsService;
	@Resource(name="contentsTypeService")
	private ContentsTypeService contentsTypeService;
	@Resource(name = "contentsFilesService")
	private ContentsFilesService contentsFilesService;


	@Override
	public RequestResult findContentsList(String typeIdOrCode, HttpServletRequest request) {
		String typeId;
		if (LwxfStringUtils.getStringLength(typeIdOrCode) > 10) {
			typeId=typeIdOrCode;
		}else {
			ContentsType contentsType = this.contentsTypeService.findContentsListByCode(typeIdOrCode);
			typeId=contentsType.getId();
		}
		List<ContentsDto> contentsDtoList=this.contentsService.findContentsList(typeId);
		MapContext mapContext=MapContext.newOne();
		mapContext.put("contentsDto",contentsDtoList);
		return ResultFactory.generateRequestResult(mapContext);
	}

	@Override
	public RequestResult findContentMessage(String contentsId) {
		MapContext mapContext=MapContext.newOne();
		ContentsContent contentsContent=this.contentsService.findContentMessage(contentsId);
       List<ContentsFiles> contentsFilesList=this.contentsFilesService.findContentsFiles(contentsId);
       mapContext.put("contentsContent",contentsContent);
       mapContext.put("contentsFilesList",contentsFilesList);
		return ResultFactory.generateRequestResult(mapContext);
	}
}
