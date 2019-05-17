package com.lwxf.industry4.webapp.domain.dto.warehouse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import java.util.Date;
import java.util.List;

import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderFiles;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStockItem;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/24/024 14:54
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@ApiModel(value = "包裹信息",discriminator = "包裹信息")
public class FinishedStockItemDto extends FinishedStockItem {
	@ApiModelProperty(value = "入库人名称")
	private String operatorName;//入库人名称
	@ApiModelProperty(value = "发货人名称")
	private String delivererName;//发货人名称
	@ApiModelProperty(value = "创建人名称")
	private String creatorName;//创建人名称
	@ApiModelProperty(value = "订单编号")
	private String orderNo;//订单编号
	@ApiModelProperty(value = "订单ID")
	private String orderId;
	@ApiModelProperty(value = "客户名称")
	private String customerName;
	@ApiModelProperty(value = "收货地址")
	private String address;
	@ApiModelProperty(value = "客户电话")
	private String customerTel;
	@ApiModelProperty(value = "收货人姓名")
	private String consigneeName;
	@ApiModelProperty(value = "收货人电话")
	private String consigneeTel;
	@ApiModelProperty(value = "发货计划备注")
	private String planNote;
	@ApiModelProperty(value = "包裹资源文件")
	private List<CustomOrderFiles> fileList;
	@ApiModelProperty(value = "经销商姓名")
	private String dealerName;
	@ApiModelProperty(value = "经销商电话")
	private String dealerTel;
	@ApiModelProperty(value = "包裹资源文件主键ID")
	private List<String> fileIds;
	@ApiModelProperty(value = "计划创建人")
	private String planCreatorName;
	@ApiModelProperty(value = "计划创建时间")
	private Date planCreated;
	@ApiModelProperty(value = "发货单号")
	private String delivererNo;
	@ApiModelProperty(value = "发货单主键ID")
	private String dispatchId;

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getDelivererName() {
		return delivererName;
	}

	public void setDelivererName(String delivererName) {
		this.delivererName = delivererName;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCustomerTel() {
		return customerTel;
	}

	public void setCustomerTel(String customerTel) {
		this.customerTel = customerTel;
	}

	public String getConsigneeName() {
		return consigneeName;
	}

	public void setConsigneeName(String consigneeName) {
		this.consigneeName = consigneeName;
	}

	public String getConsigneeTel() {
		return consigneeTel;
	}

	public void setConsigneeTel(String consigneeTel) {
		this.consigneeTel = consigneeTel;
	}

	public String getPlanNote() {
		return planNote;
	}

	public void setPlanNote(String planNote) {
		this.planNote = planNote;
	}

	public List<CustomOrderFiles> getFileList() {
		return fileList;
	}

	public void setFileList(List<CustomOrderFiles> fileList) {
		this.fileList = fileList;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getDealerTel() {
		return dealerTel;
	}

	public void setDealerTel(String dealerTel) {
		this.dealerTel = dealerTel;
	}

	public List<String> getFileIds() {
		return fileIds;
	}

	public void setFileIds(List<String> fileIds) {
		this.fileIds = fileIds;
	}

	public String getPlanCreatorName() {
		return planCreatorName;
	}

	public void setPlanCreatorName(String planCreatorName) {
		this.planCreatorName = planCreatorName;
	}

	public Date getPlanCreated() {
		return planCreated;
	}

	public void setPlanCreated(Date planCreated) {
		this.planCreated = planCreated;
	}

	public String getDelivererNo() {
		return delivererNo;
	}

	public void setDelivererNo(String delivererNo) {
		this.delivererNo = delivererNo;
	}

	public String getDispatchId() {
		return dispatchId;
	}

	public void setDispatchId(String dispatchId) {
		this.dispatchId = dispatchId;
	}
}
