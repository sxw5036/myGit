package com.lwxf.industry4.webapp.facade.app.factory.factoryAftersaleApply;

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
 * @create：2019/3/30 0030 14:07
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FactoryAftersaleApplyFacade extends BaseFacade {
	RequestResult findFactoryAftersaleApply(String companyId);

	RequestResult factoryAftersaleApplyInfo(String aftersaleApplyId,String companyId);

	RequestResult findDealersList(MapContext params);



	RequestResult findCustomerList(String dealerId);



	RequestResult findAftersaleApplyList(Integer pageNum, Integer pageSize, MapContext mapContext);

	RequestResult addFactoryAftersale(MapContext mapContext, HttpServletRequest request);

	RequestResult addFiles(String aftersaleId, List<MultipartFile> multipartFiles, HttpServletRequest request);
}
