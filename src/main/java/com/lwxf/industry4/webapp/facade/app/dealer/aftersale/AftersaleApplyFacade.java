package com.lwxf.industry4.webapp.facade.app.dealer.aftersale;

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
 * @create：2019/1/2 0002 20:33
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface AftersaleApplyFacade extends BaseFacade {

	RequestResult addAftersale(String companyId, HttpServletRequest request, MapContext mapContext);

	RequestResult aftersaleList(Integer pageNum, Integer pageSize, MapContext mapContext, HttpServletRequest request);

	RequestResult updateAftersaleApply(String companyId, String aftersaleId, HttpServletRequest request,MapContext mapContext);


	RequestResult findAftersaleMessage(String companyId, String aftersaleId,String orderId, HttpServletRequest request);

	RequestResult addFiles(String aftersaleId, List<MultipartFile> multipartFiles, HttpServletRequest request);


	RequestResult aftersaleApplyRedis(HttpServletRequest request);

	RequestResult findAftersaleCount(MapContext mapContext, HttpServletRequest request);
}
