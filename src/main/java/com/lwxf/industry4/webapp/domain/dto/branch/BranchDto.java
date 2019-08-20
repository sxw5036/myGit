package com.lwxf.industry4.webapp.domain.dto.branch;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.lwxf.industry4.webapp.domain.entity.branch.Branch;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/6/5/005 10:29
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@ApiModel(value = "企业信息",description = "企业信息")
public class BranchDto extends Branch{
	@ApiModelProperty(value = "状态名称")
	private String statusName;
	@ApiModelProperty(value = "类型名称")
	private String typeName;

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
