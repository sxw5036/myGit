package com.lwxf.industry4.webapp.facade.admin.factory.order.impl;

import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderLogService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.facade.admin.factory.order.OrderLogFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("orderLogFacade")
public class OrderLogFacadeImpl extends BaseFacadeImpl implements OrderLogFacade {

    @Resource(name = "customOrderLogService")
    private CustomOrderLogService customOrderLogService;

    @Override
    public RequestResult findOrderLogs(String id) {
        return ResultFactory.generateRequestResult(customOrderLogService.findByOrderId(id));
    }
}
