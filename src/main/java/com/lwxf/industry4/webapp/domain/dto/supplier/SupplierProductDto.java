package com.lwxf.industry4.webapp.domain.dto.supplier;

import com.lwxf.industry4.webapp.domain.entity.supplier.SupplierProduct;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/29/029 19:21
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public class SupplierProductDto extends SupplierProduct {
	private String companyName;
	private String productName;
	private String createName;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}
}
