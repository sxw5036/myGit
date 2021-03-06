package com.lwxf.industry4.webapp.domain.entity.customorder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.*;
import java.sql.*;
import java.util.Date;
import java.util.Arrays;
import java.util.List;

import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.mybatis.utils.TypesExtend;
import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.utils.DataValidatorUtils;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.mybatis.annotation.Table;
import com.lwxf.mybatis.annotation.Column;

import com.lwxf.industry4.webapp.domain.entity.base.IdEntity;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
/**
 * 功能：order_product 实体类
 *
 * @author：F_baisi(F_baisi@163.com)
 * @created：2019-04-11 05:36 
 * @version：2018 Version：1.0 
 * @dept：老屋新房 Created with IntelliJ IDEA 
 */ 
@Table(name = "order_product",displayName = "order_product")
@ApiModel(value = "订单产品信息表",discriminator = "订单产品信息表")
public class OrderProduct extends IdEntity  {
	private static final long serialVersionUID = 1L;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "custom_order_id",displayName = "订单主键ID")
	@ApiModelProperty(value = "订单主键ID",required = true)
	private String customOrderId;
	@Column(type = Types.TINYINT,nullable = false,name = "type",displayName = "类型:0 橱柜(J) 1 衣柜(B) 2 成品家具(J) 3电器(J) 4 五金(J) 5 样块(Y)")
	@ApiModelProperty(value = "类型:0 橱柜(J) 1 衣柜(B) 2 成品家具(J) 3电器(J) 4 五金(J) 5 样块(Y)",required = true)
	private Integer type;
	@Column(type = Types.TINYINT,name = "series",displayName = "系列 0 定制实木 1 特供实木 2 美克 3 康奈 4 慧娜 5 模压")
	@ApiModelProperty(value = "系列 0 定制实木 1 特供实木 2 美克 3 康奈 4 慧娜 5 模压",required = true)
	private Integer series;
	@Column(type = Types.VARCHAR,length = 50,name = "door_color",displayName = "颜色")
	@ApiModelProperty(value = "门板颜色")
	private String doorColor;
	@Column(type = Types.VARCHAR,length = 50,name = "body_color",displayName = "颜色")
	@ApiModelProperty(value = "柜体颜色")
	private String bodyColor;
	@Column(type = Types.VARCHAR,length = 50,name = "door",displayName = "门型")
	@ApiModelProperty(value = "门型")
	private String door;
	@Column(type = Types.VARCHAR,length = 300,name = "notes",displayName = "备注")
	@ApiModelProperty(value = "备注")
	private String notes;
	@Column(type = Types.DATE,nullable = false,updatable = false,name = "created",displayName = "创建时间")
	@ApiModelProperty(value = "创建时间",required = true)
	private Date created;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "creator",displayName = "创建人")
	@ApiModelProperty(value = "创建人",required = true)
	private String creator;
	@Column(type = Types.DATE,name = "update_time",displayName = "修改时间")
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
	@Column(type = Types.CHAR,length = 13,name = "update_user",displayName = "修改人")
	@ApiModelProperty(value = "修改人")
	private String updateUser;
	@Column(type = Types.DECIMAL,precision = 10,scale=2,name = "price",displayName = "价格")
	@ApiModelProperty(value = "价格")
	private BigDecimal price;
	@Column(type = Types.CHAR,length = 13,name = "after_apply_id",displayName = "售后单id")
	@ApiModelProperty(value = "售后单id")
	private String afterApplyId;
	@Column(type = Types.VARCHAR,length = 100,name = "install",displayName = "安装位置")
	@ApiModelProperty(value = "安装位置")
	private String install;
    public OrderProduct() {  
     }

	public RequestResult validateFields() {
		Map<String, String> validResult = new HashMap<>();
		if (this.customOrderId == null) {
			validResult.put("customOrderId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
 			if (LwxfStringUtils.getStringLength(this.customOrderId) > 13) {
				validResult.put("customOrderId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.type == null) {
			validResult.put("type", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (LwxfStringUtils.getStringLength(this.doorColor) > 50) {
			validResult.put("doorColor", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.bodyColor) > 50) {
			validResult.put("bodyColor", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.door) > 50) {
			validResult.put("door", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.notes) > 300) {
			validResult.put("notes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (this.creator == null) {
			validResult.put("creator", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
 			if (LwxfStringUtils.getStringLength(this.creator) > 13) {
				validResult.put("creator", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (LwxfStringUtils.getStringLength(this.updateUser) > 13) {
			validResult.put("updateUser", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if(this.price!=null){
			if(this.price.compareTo(new BigDecimal(100000000))!=-1){
				validResult.put("price",AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}

	private final static List<String> propertiesList = Arrays.asList("type","series","doorColor","bodyColor","door","notes","updateTime","updateUser","price","install");

	public static RequestResult validateFields(MapContext map) {
		Map<String, String> validResult = new HashMap<>();
		if(map.size()==0){
			return ResultFactory.generateErrorResult("error",AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		boolean flag;
		Set<String> mapSet = map.keySet();
		flag = propertiesList.containsAll(mapSet);
		if(!flag){
			return ResultFactory.generateErrorResult("error",AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
		}
		if(map.containsKey("type")) {
			if (!DataValidatorUtils.isByte1(map.getTypedValue("type",String.class))) {
				validResult.put("type", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if(map.containsKey("series")) {
			if (!DataValidatorUtils.isByte1(map.getTypedValue("series",String.class))) {
				validResult.put("series", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if(map.containsKey("updateTime")) {
			if (!DataValidatorUtils.isDate(map.getTypedValue("updateTime",String.class))) {
				validResult.put("updateTime", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if(map.containsKey("type")) {
			if (map.get("type")  == null) {
				validResult.put("type", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if(map.containsKey("series")) {
			if (map.get("series")  == null) {
				validResult.put("series", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if(map.containsKey("doorColor")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("doorColor",String.class)) > 50) {
				validResult.put("doorColor", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(map.containsKey("bodyColor")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("bodyColor",String.class)) > 50) {
				validResult.put("bodyColor", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(map.containsKey("door")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("door",String.class)) > 50) {
				validResult.put("door", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(map.containsKey("notes")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("notes",String.class)) > 300) {
				validResult.put("notes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(map.containsKey("updateUser")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("updateUser",String.class)) > 13) {
				validResult.put("updateUser", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(map.containsKey("price")) {
			if (!DataValidatorUtils.isDecmal4(new BigDecimal(map.getTypedValue("price",String.class)).toPlainString())) {
				validResult.put("price", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
			if (map.getTypedValue("price",BigDecimal.class).compareTo(new BigDecimal(100000000))!=-1){
				validResult.put("price",AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(map.containsKey("install")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("install",String.class)) > 100) {
				validResult.put("install", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}


	public void setCustomOrderId(String customOrderId){
		this.customOrderId=customOrderId;
	}

	public String getCustomOrderId(){
		return customOrderId;
	}

	public void setType(Integer type){
		this.type=type;
	}

	public Integer getType(){
		return type;
	}

	public void setSeries(Integer series){
		this.series=series;
	}

	public Integer getSeries(){
		return series;
	}

	public void setDoorColor(String doorColor){
		this.doorColor=doorColor;
	}

	public String getDoorColor(){
		return doorColor;
	}

	public void setDoor(String door){
		this.door=door;
	}

	public String getDoor(){
		return door;
	}

	public void setNotes(String notes){
		this.notes=notes;
	}

	public String getNotes(){
		return notes;
	}

	public void setCreated(Date created){
		this.created=created;
	}

	public Date getCreated(){
		return created;
	}

	public void setCreator(String creator){
		this.creator=creator;
	}

	public String getCreator(){
		return creator;
	}

	public void setUpdateTime(Date updateTime){
		this.updateTime=updateTime;
	}

	public Date getUpdateTime(){
		return updateTime;
	}

	public void setUpdateUser(String updateUser){
		this.updateUser=updateUser;
	}

	public String getUpdateUser(){
		return updateUser;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getBodyColor() {
		return bodyColor;
	}

	public void setBodyColor(String bodyColor) {
		this.bodyColor = bodyColor;
	}

	public String getAfterApplyId() {
		return afterApplyId;
	}

	public void setAfterApplyId(String afterApplyId) {
		this.afterApplyId = afterApplyId;
	}

	public String getInstall() {
		return install;
	}

	public void setInstall(String install) {
		this.install = install;
	}
}
