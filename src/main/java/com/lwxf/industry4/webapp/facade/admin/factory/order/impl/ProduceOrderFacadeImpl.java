package com.lwxf.industry4.webapp.facade.admin.factory.order.impl;

import javax.annotation.Resource;

import java.util.*;

import org.springframework.stereotype.Component;

import com.lwxf.industry4.webapp.bizservice.customorder.OrderProductService;
import com.lwxf.industry4.webapp.bizservice.customorder.ProduceOrderService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.customorder.ProduceOrderState;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.dto.printTable.CoordinationPrintTableDto;
import com.lwxf.industry4.webapp.facade.admin.factory.order.ProduceOrderFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;

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
	@Resource(name = "orderProductService")
	private OrderProductService orderProductService;

	@Override
	public RequestResult findCoordinationCount(String branchId) {
		MapContext mapContext=this.produceOrderService.findCoordinationCount(branchId);
		List result=new ArrayList();
		int a=0;
		for(int i=0;i<3;i++){
			Map map = new HashMap();
			switch (a) {
				case 	0:
					map.put("name", "新增外协数量");
					map.put("value",mapContext.getTypedValue("newNum",Integer.class));
					a=a+1;
					break;
				case 1:
					map.put("name", "待支付外协单数量");
					map.put("value",mapContext.getTypedValue("afterNum",Integer.class));
					a=a+1;
					break;
				case 2:
					map.put("name", "外协完成数量");
					map.put("value",mapContext.getTypedValue("endNum",Integer.class));
					a=a+1;
					break;
			}
			result.add(map);
		}
		return ResultFactory.generateRequestResult(result);

	}

	@Override
	public RequestResult findProduceOrderOverview() {
		MapContext result = new MapContext();

		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(99999999);
		pagination.setPageNum(1);
		paginatedFilter.setPagination(pagination);
		//今日生产数量
		MapContext filter1 = new MapContext();
		filter1.put("actualTimeNow",1);
		filter1.put("funds",PaymentFunds.ORDER_FEE_CHARGE.getValue());
		paginatedFilter.setFilters(filter1);
		result.put("produceOrderAccount",this.produceOrderService.findListByFilter(paginatedFilter).getRows().size());
		//今日完成数量
		MapContext filter2 = new MapContext();
		filter2.put("completionTime",1);
		filter2.put("funds",PaymentFunds.ORDER_FEE_CHARGE.getValue());
		paginatedFilter.setFilters(filter2);
		result.put("produceOrderCompleteAccount",this.produceOrderService.findListByFilter(paginatedFilter).getRows().size());
		//今日临产数量
		MapContext filter3 = new MapContext();
		filter3.put("plannedTimeNow",1);
		filter3.put("funds",PaymentFunds.ORDER_FEE_CHARGE.getValue());
		filter3.put(WebConstant.KEY_ENTITY_STATE,Arrays.asList(ProduceOrderState.NOT_YET_BEGUN.getValue()));
		paginatedFilter.setFilters(filter3);
		result.put("produceOrderScheduleAccount",this.produceOrderService.findListByFilter(paginatedFilter).getRows().size());
		//今日超期数量
		MapContext filter4 = new MapContext();
		filter4.put("plannedTimeYes",1);
		filter4.put("funds",PaymentFunds.ORDER_FEE_CHARGE.getValue());
		filter4.put(WebConstant.KEY_ENTITY_STATE,Arrays.asList(ProduceOrderState.NOT_YET_BEGUN.getValue()));
		paginatedFilter.setFilters(filter4);
		result.put("produceOrderOverdueAccount",this.produceOrderService.findListByFilter(paginatedFilter).getRows().size());
		return ResultFactory.generateRequestResult(result);
	}

	@Override
	public RequestResult findCoordinationPrintInfo(String id) {
		CoordinationPrintTableDto coordinationPrintTableDto = this.produceOrderService.findCoordinationPrintInfo(id);
		coordinationPrintTableDto.setOrderProduct(this.orderProductService.findOneById(coordinationPrintTableDto.getOrderProductId()));
		return ResultFactory.generateRequestResult(coordinationPrintTableDto);
	}
}
