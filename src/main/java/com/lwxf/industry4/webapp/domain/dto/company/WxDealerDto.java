package com.lwxf.industry4.webapp.domain.dto.company;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.lwxf.industry4.webapp.domain.entity.company.Company;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/6/14/014 10:30
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@ApiModel(value="微信经销商公司详情",description="微信经销商公司详情")
public class WxDealerDto extends Company {
	@ApiModelProperty(value = "头像")
	private String avatar;
	@ApiModelProperty(value = "用户名称")
	private String dealerName;
	@ApiModelProperty(value = "经销商地址")
	private String cityName;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
}
