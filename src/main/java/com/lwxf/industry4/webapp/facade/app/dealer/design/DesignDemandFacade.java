package com.lwxf.industry4.webapp.facade.app.dealer.design;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderDemand;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/1 14:38
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface DesignDemandFacade extends BaseFacade {


    RequestResult addHomeStyleImage(List<MultipartFile> multipartFiles, String orderId, String belongId, String userId, Integer category, Integer type);
    RequestResult addDesignDemand (CustomOrderDemand customOrderDemand, HttpServletRequest request);
    RequestResult updateDesignDemand(MapContext updateMap,HttpServletRequest request);
    RequestResult findByOrderId(String id);
    RequestResult findDemandImageByDemandId(String orderId, String demandId);
    RequestResult findDoorInfo();

}
