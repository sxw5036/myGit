package com.lwxf.industry4.webapp.domain.dto.warehouse;

import com.lwxf.industry4.webapp.domain.entity.warehouse.Stock;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/14/014 13:29
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public class StockDto extends Stock {
	private String productName;
	private String storageName;
	private String operatorName;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getStorageName() {
		return storageName;
	}

	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
}
