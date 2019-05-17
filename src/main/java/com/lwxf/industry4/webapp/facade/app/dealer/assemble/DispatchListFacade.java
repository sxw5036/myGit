package com.lwxf.industry4.webapp.facade.app.dealer.assemble;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/19 0019 15:18
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface DispatchListFacade extends BaseFacade {
	RequestResult findDispatcjLists(MapContext mapContext, Integer pageNum, Integer pageSize,HttpServletRequest request);



	RequestResult dispatchListMessage(String dispatchListId,HttpServletRequest request);

	RequestResult addDispatchList(MapContext mapContext, HttpServletRequest request);


	RequestResult addFiles(String dispatchListId, List<MultipartFile> multipartFiles, HttpServletRequest request);

	RequestResult updateDispatchListById(String dispatchListId, MapContext mapContext, Integer status, HttpServletRequest request);

	RequestResult dispatchListsCount(MapContext mapContext, HttpServletRequest request);
}
