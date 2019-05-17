package com.lwxf.industry4.webapp.domain.dto.warehouse;

import com.lwxf.industry4.webapp.domain.entity.warehouse.StorageOutputInItem;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/20/020 13:37
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public class StorageOutputInItemDto extends StorageOutputInItem {
	private String productName;


	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
}
