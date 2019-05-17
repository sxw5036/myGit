package com.lwxf.industry4.webapp.domain.dto.dispatch;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillItem;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/25/025 14:25
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@ApiModel(value = "发货条目信息",discriminator = "发货条目信息")
public class DispatchBillItemDto extends DispatchBillItem {
	@ApiModelProperty(value = "条形码(包裹编号)")
	private String barcode;
	@ApiModelProperty(value = "类型：0 - 柜体；1 - 门板；2 - 五金（含五金配件、厨具、烟机灶具、礼品等）")
	private String type;

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
