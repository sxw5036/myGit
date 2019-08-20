package com.lwxf.industry4.webapp.domain.dto.customorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/6/15 0015 11:44
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@ApiModel(value = "F端微信小程序：订单列表输出模型",description = "F端微信小程序：订单列表输出模型")
public class WxCustomOrderDto {
	@ApiModelProperty(value = "状态 ")
	private Integer status;
	@ApiModelProperty(value = "状态名称 ")
	private String statusName;
	@ApiModelProperty(value = "经销商名称")
	private String dealerName;
	@ApiModelProperty(value = "客户名称")
	private String customerName;
	@ApiModelProperty(value = "订单编号")
	private String orderNo;
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "创建时间")
	private Date orderCreated;
	@ApiModelProperty(value = "订单id")
	private String orderId;
	@ApiModelProperty(value = "业务经理名称")
	private String salesmanName;



	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getOrderCreated() {
		return orderCreated;
	}

	public void setOrderCreated(Date orderCreated) {
		this.orderCreated = orderCreated;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getStatusName() {
		switch (this.status){
			case 0:
				statusName="设计";break;
			case 2:
			case 3:
				statusName="生产";break;
			case 4:
				statusName="入库";break;
			case 5:
			case 6:
				statusName="发货";break;
				default:
					statusName="其他";
		}
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getSalesmanName() {
		return salesmanName;
	}

	public void setSalesmanName(String salesmanName) {
		this.salesmanName = salesmanName;
	}
}
