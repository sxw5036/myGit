package com.lwxf.industry4.webapp.domain.dto.procurement;

import com.lwxf.industry4.webapp.domain.entity.procurement.PurchaseRequest;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/17/017 11:38
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public class PurchaseRequestDto extends PurchaseRequest {
	private String storageName;
	private String productName;
	private String creatorName;

	public String getStorageName() {
		return storageName;
	}

	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
}
