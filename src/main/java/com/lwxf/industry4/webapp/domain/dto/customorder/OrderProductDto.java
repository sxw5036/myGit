package com.lwxf.industry4.webapp.domain.dto.customorder;

import com.lwxf.industry4.webapp.domain.entity.customorder.ProduceOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockItemDto;
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
	@ApiModelProperty(value = "生产单集合")
	private List<ProduceOrderDto> produceOrderList;
	@ApiModelProperty(value = "类型名称")
	private String typeName;
	@ApiModelProperty(value = "系列名称")
	private String seriesName;
	@ApiModelProperty(value = "资源文件ID")
	private List<String> fileIds;
	@ApiModelProperty(value = "包裹信息")
	private List<FinishedStockItemDto> finishedStockItemDtos;


	public List<ProduceOrderDto> getProduceOrderList() {
		return produceOrderList;
	}

	public void setProduceOrderList(List<ProduceOrderDto> produceOrderList) {
		this.produceOrderList = produceOrderList;
	}

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

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public List<String> getFileIds() {
		return fileIds;
	}

	public void setFileIds(List<String> fileIds) {
		this.fileIds = fileIds;
	}

	public List<FinishedStockItemDto> getFinishedStockItemDtos() {
		return finishedStockItemDtos;
	}

	public void setFinishedStockItemDtos(List<FinishedStockItemDto> finishedStockItemDtos) {
		this.finishedStockItemDtos = finishedStockItemDtos;
	}

}
