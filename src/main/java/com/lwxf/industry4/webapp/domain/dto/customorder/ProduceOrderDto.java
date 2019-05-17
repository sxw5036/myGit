package com.lwxf.industry4.webapp.domain.dto.customorder;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

import com.lwxf.industry4.webapp.domain.entity.common.UploadFiles;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderFiles;
import com.lwxf.industry4.webapp.domain.entity.customorder.ProduceFlow;
import com.lwxf.industry4.webapp.domain.entity.customorder.ProduceOrder;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/4/12/012 16:57
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@ApiModel(value = "生产拆单信息",discriminator = "生产拆单信息")
public class ProduceOrderDto extends ProduceOrder {
	@ApiModelProperty(value = "创建人名称")
	private String creatorName;
	@ApiModelProperty(value = "修改人名称")
	private String updateUserName;
	@ApiModelProperty(value = "跟单员名称")
	private String merchandiserName;
	@ApiModelProperty(value = "经销商名称")
	private String dealerName;
	@ApiModelProperty(value = "客户名称")
	private String customerName;
	@ApiModelProperty(value = "有效时间")
	private Date payTime;
	@ApiModelProperty(value = "计划交付时间")
	private Date estimatedDeliveryDate;
	@ApiModelProperty(value = "生产流程信息")
	private List<ProduceFlowDto> produceFlowDtos;
	@ApiModelProperty(value = "附件集合")
	private List<CustomOrderFiles> uploadFiles;
	@ApiModelProperty(value = "图片id集合")
	private List<String> fileIds;

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getUpdateUserName() {
		return updateUserName;
	}

	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	public List<ProduceFlowDto> getProduceFlowDtos() {
		return produceFlowDtos;
	}

	public void setProduceFlowDtos(List<ProduceFlowDto> produceFlowDtos) {
		this.produceFlowDtos = produceFlowDtos;
	}

	public String getMerchandiserName() {
		return merchandiserName;
	}

	public void setMerchandiserName(String merchandiserName) {
		this.merchandiserName = merchandiserName;
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

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getEstimatedDeliveryDate() {
		return estimatedDeliveryDate;
	}

	public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
		this.estimatedDeliveryDate = estimatedDeliveryDate;
	}

	public List<CustomOrderFiles> getUploadFiles() {
		return uploadFiles;
	}

	public void setUploadFiles(List<CustomOrderFiles> uploadFiles) {
		this.uploadFiles = uploadFiles;
	}

	public List<String> getFileIds() {
		return fileIds;
	}

	public void setFileIds(List<String> fileIds) {
		this.fileIds = fileIds;
	}
}
