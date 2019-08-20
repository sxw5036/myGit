package com.lwxf.industry4.webapp.domain.dto.warehouse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillPlan;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillPlanItem;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/5/10/010 9:20
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@ApiModel(value = "配送计划信息",discriminator = "配送计划信息")
public class DispatchBillPlanDto extends DispatchBillPlan {
	@ApiModelProperty(value = "配送计划条目信息")
	private List<DispatchBillPlanItem> dispatchBillPlanItems;
	@ApiModelProperty(value = "订单集合")
	private List<String> orderIds;

	public List<DispatchBillPlanItem> getDispatchBillPlanItems() {
		return dispatchBillPlanItems;
	}

	public void setDispatchBillPlanItems(List<DispatchBillPlanItem> dispatchBillPlanItems) {
		this.dispatchBillPlanItems = dispatchBillPlanItems;
	}

	public List<String> getOrderIds() {
		return orderIds;
	}

	public void setOrderIds(List<String> orderIds) {
		this.orderIds = orderIds;
	}
}
