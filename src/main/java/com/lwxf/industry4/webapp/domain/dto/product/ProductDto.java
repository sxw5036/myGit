package com.lwxf.industry4.webapp.domain.dto.product;

import com.lwxf.industry4.webapp.domain.entity.product.Product;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/4/004 15:38
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public class ProductDto extends Product {
	private String categoryName;
	private String creatorName;



	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}


	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
}
