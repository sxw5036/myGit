package com.lwxf.industry4.webapp.facade.admin.factory.order.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.lwxf.industry4.webapp.bizservice.customorder.ProduceOrderService;
import com.lwxf.industry4.webapp.facade.admin.factory.order.ProduceOrderFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/4/8/008 15:57
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("produceOrderFacade")
public class ProduceOrderFacadeImpl extends BaseFacadeImpl implements ProduceOrderFacade{
	@Resource(name = "produceOrderService")
	private ProduceOrderService produceOrderService;
}
