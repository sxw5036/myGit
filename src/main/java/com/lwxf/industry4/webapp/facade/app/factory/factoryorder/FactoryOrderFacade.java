package com.lwxf.industry4.webapp.facade.app.factory.factoryorder;

import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/28 11:33
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FactoryOrderFacade extends BaseFacade {
    RequestResult findFactoryOrder();

    RequestResult findFactoryOrderByCreated(Pagination pagination,Integer type,Integer status);
    RequestResult findOrderInfoByOrderId(String orderId);

    RequestResult findFactoryOrderByCondition(Pagination pagination, MapContext mapContext);

    RequestResult findOrderListByStatus(Pagination pagination,MapContext params);

    RequestResult findOrderHomePage();

    RequestResult findOrderListByCondition(Pagination pagination, MapContext mapContext,String countType);

    RequestResult findOrderInfos(String orderId);

    RequestResult findCompnayByCNameAndBName(String name,String cityName);

    RequestResult findCreatedOrder();

    RequestResult addOrderInfo(MapContext params);

    RequestResult findCompanyCustomer(String companyId);

    RequestResult addOrderExcel(MultipartFile multipartFile);
}
