package com.lwxf.industry4.webapp.domain.dto.customorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import com.lwxf.industry4.webapp.domain.entity.common.UploadFiles;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderFiles;
import com.lwxf.industry4.webapp.domain.entity.customorder.OrderProduct;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/4/12/012 15:05
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@ApiModel(value = "订单产品信息DTO",discriminator = "订单产品信息DTO")
public class OrderProductDto extends OrderProduct {
	@ApiModelProperty(value = "创建人名称")
	private String creatorName;
	@ApiModelProperty(value = "修改人名称")
	private String updateUserName;
	@ApiModelProperty(value = "附件集合")
	private List<CustomOrderFiles> uploadFiles;

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

	public List<CustomOrderFiles> getUploadFiles() {
		return uploadFiles;
	}

	public void setUploadFiles(List<CustomOrderFiles> uploadFiles) {
		this.uploadFiles = uploadFiles;
	}
}
