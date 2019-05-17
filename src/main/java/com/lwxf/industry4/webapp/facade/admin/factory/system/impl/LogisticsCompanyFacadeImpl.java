package com.lwxf.industry4.webapp.facade.admin.factory.system.impl;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.stereotype.Component;

import com.lwxf.industry4.webapp.bizservice.company.DealerShippingLogisticsService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.system.LogisticsCompanyService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDto;
import com.lwxf.industry4.webapp.domain.entity.company.DealerShippingLogistics;
import com.lwxf.industry4.webapp.domain.entity.system.LogisticsCompany;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.system.LogisticsCompanyFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/26/026 16:02
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("logisticsCompanyFacade")
public class LogisticsCompanyFacadeImpl extends BaseFacadeImpl implements LogisticsCompanyFacade {

	@Resource(name = "logisticsCompanyService")
	private LogisticsCompanyService logisticsCompanyService;
	@Resource(name = "dealerShippingLogisticsService")
	private DealerShippingLogisticsService dealerShippingLogisticsService;
	@Resource(name = "customOrderService")
	private CustomOrderService customOrderService;

	@Override
	public RequestResult findList(String orderId) {
		CustomOrderDto orderDto = this.customOrderService.findByOrderId(orderId);
		DealerShippingLogistics dealerShippingLogistics = this.dealerShippingLogisticsService.findOneByCompanyId(orderDto.getCompanyId());
		MapContext mapContext = MapContext.newOne();
		List<LogisticsCompany> allNormal = this.logisticsCompanyService.findAllNormal();
		if(dealerShippingLogistics!=null){
			mapContext.put("default",dealerShippingLogistics.getLogisticsCompanyId());
		}else{
			if(allNormal!=null){
				mapContext.put("default",allNormal.get(0).getId());
			}else{
				mapContext.put("default",null);
			}
		}
		mapContext.put("logisticsCompanyList",allNormal);
		return ResultFactory.generateRequestResult(mapContext);
	}
}
