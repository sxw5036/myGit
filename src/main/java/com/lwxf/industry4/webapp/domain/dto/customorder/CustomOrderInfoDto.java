package com.lwxf.industry4.webapp.domain.dto.customorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderFiles;
import com.lwxf.industry4.webapp.domain.entity.customorder.OrderProduct;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/4/15/015 15:32
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@ApiModel(value = "订单详细信息",discriminator = "订单详细信息")
public class CustomOrderInfoDto {
	@ApiModelProperty(value = "订单数据",required = true)
	private CustomOrderDto customOrder;
	@ApiModelProperty(value = "订单需求")
	private List<CustomOrderDemandDto> orderDemand;
	@ApiModelProperty(value = "订单设计")
	private List<CustomOrderDesignDto> orderDesign;
	@ApiModelProperty(value = "订单合同附件")
	private List<CustomOrderFiles> orderContractFiles;
	@ApiModelProperty(value = "订单报价修改记录")
	private List<OrderAccountLogDto> orderAccountLogList;
	@ApiModelProperty(value = "订单产品")
	private List<OrderProductDto> orderProducts;
	@ApiModelProperty(value = "生产拆单")
	private List<ProduceOrderDto> produceOrderDtos;

	public CustomOrderDto getCustomOrder() {
		return customOrder;
	}

	public void setCustomOrder(CustomOrderDto customOrder) {
		this.customOrder = customOrder;
	}

	public List<CustomOrderDemandDto> getOrderDemand() {
		return orderDemand;
	}

	public void setOrderDemand(List<CustomOrderDemandDto> orderDemand) {
		this.orderDemand = orderDemand;
	}

	public List<CustomOrderDesignDto> getOrderDesign() {
		return orderDesign;
	}

	public void setOrderDesign(List<CustomOrderDesignDto> orderDesign) {
		this.orderDesign = orderDesign;
	}

	public List<CustomOrderFiles> getOrderContractFiles() {
		return orderContractFiles;
	}

	public void setOrderContractFiles(List<CustomOrderFiles> orderContractFiles) {
		this.orderContractFiles = orderContractFiles;
	}

	public List<OrderAccountLogDto> getOrderAccountLogList() {
		return orderAccountLogList;
	}

	public void setOrderAccountLogList(List<OrderAccountLogDto> orderAccountLogList) {
		this.orderAccountLogList = orderAccountLogList;
	}

	public List<OrderProductDto> getOrderProducts() {
		return orderProducts;
	}

	public void setOrderProducts(List<OrderProductDto> orderProducts) {
		this.orderProducts = orderProducts;
	}

	public List<ProduceOrderDto> getProduceOrderDtos() {
		return produceOrderDtos;
	}

	public void setProduceOrderDtos(List<ProduceOrderDto> produceOrderDtos) {
		this.produceOrderDtos = produceOrderDtos;
	}
}
