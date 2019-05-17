package com.lwxf.industry4.webapp.facade.app.dealer.company;

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
 * @create：2018/12/14 0014 9:15
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface DealerShopFacade extends BaseFacade {
	RequestResult shopShow(String companyId,String dealerShopId);

	RequestResult updateShop(String companyId,String dealerShopId,MapContext mapContext);

	RequestResult findShopList(String address);

	RequestResult updateCoverImage(String companyId,String dealerShopId, MultipartFile multipartFile, HttpServletRequest request);

	RequestResult addShowImages(String companyId,String dealerShopId, List<MultipartFile> multipartFiles, HttpServletRequest request);

	RequestResult deleteShowImages(String companyId,String dealerShopId, String showImageId, HttpServletRequest request);
}
