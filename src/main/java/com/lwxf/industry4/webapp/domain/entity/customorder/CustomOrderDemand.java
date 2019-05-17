package com.lwxf.industry4.webapp.domain.entity.customorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.utils.DataValidatorUtils;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.entity.base.IdEntity;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.mybatis.annotation.Column;
import com.lwxf.mybatis.annotation.PrimaryConstraint;
import com.lwxf.mybatis.annotation.Table;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.mybatis.utils.TypesExtend;

import java.sql.Types;
import java.util.*;
/**
 * 功能：custom_order_demand 实体类
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @created：2018-12-18 02:52
 * @version：2018 Version：1.0
 * @dept：老屋新房 Created with IntelliJ IDEA
 */
@Table(name = "custom_order_demand", primaryConstraints = @PrimaryConstraint(fieldNames = {"id","name"}), displayName = "custom_order_demand")
@ApiModel(value = "订单需求",discriminator = "订单需求")
public class CustomOrderDemand extends IdEntity  {
	private static final long serialVersionUID = 1L;
	@Column(type = Types.LONGVARCHAR,name = "content",displayName = "")
	@ApiModelProperty(value = "描述")
	private String content;
	@Column(type = Types.VARCHAR,length = 50,nullable = false,name = "name",displayName = "需求名称：可以是衣柜、橱柜等，也可以是厨房、客厅、卧室等")
	@ApiModelProperty(value = "需求名称")
	private String name;
	@Column(type = Types.CHAR,length = 13,nullable = false,name = "creator",displayName = "创建人")
	@ApiModelProperty(value = "创建人")
	private String creator;
	@Column(type = TypesExtend.DATETIME,nullable = false,name = "created",displayName = "创建时间")
	@ApiModelProperty(value = "创建时间")
	private Date created;
	@Column(type = Types.CHAR,length = 13,nullable = false,name = "custom_order_id",displayName = "定制订单id")
	@ApiModelProperty(value = "定制订单id")
	private String customOrderId;
	@Column(type = Types.VARCHAR,length = 50,nullable = false,name = "no",displayName = "设计需求的编号")
	@ApiModelProperty(value = "设计需求的编号")
	private String no;
	@Column(type = Types.CHAR,length = 13,name = "design_scheme",displayName = "参考设计案例")
	@ApiModelProperty(value = "参考设计案例")
	private String designScheme;
	@Column(type = Types.VARCHAR,length = 50,name = "product_series",displayName = "产品系列")
	@ApiModelProperty(value = "产品系列")
	private String productSeries;
	@Column(type = Types.VARCHAR,length = 50,name = "product_model",displayName = "产品型号")
	@ApiModelProperty(value = "产品型号")
	private String productModel;

	public CustomOrderDemand() {
	}

	public RequestResult validateFields() {
		Map<String, String> validResult = new HashMap<>();
		if (this.name == null) {
			validResult.put("name", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.name) > 50) {
				validResult.put("name", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.creator == null) {
			validResult.put("creator", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.creator) > 13) {
				validResult.put("creator", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.created == null) {
			validResult.put("created", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (this.customOrderId == null) {
			validResult.put("customOrderId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.customOrderId) > 13) {
				validResult.put("customOrderId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.no == null) {
			validResult.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.no) > 50) {
				validResult.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (LwxfStringUtils.getStringLength(this.designScheme) > 13) {
			validResult.put("designScheme", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.productSeries) > 50) {
			validResult.put("productSeries", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.productModel) > 50) {
			validResult.put("productModel", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}

	private final static List<String> propertiesList = Arrays.asList("id","content","name","creator","created","customOrderId","no","designScheme","productSeries","productModel");

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
		if(map.containsKey("created")) {
			if (!DataValidatorUtils.isDate(map.getTypedValue("created",String.class))) {
				validResult.put("created", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if(map.containsKey("name")) {
			if (map.getTypedValue("name",String.class)  == null) {
				validResult.put("name", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}else{
				if (LwxfStringUtils.getStringLength(map.getTypedValue("name",String.class)) > 50) {
					validResult.put("name", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
				}
			}
		}
		if(map.containsKey("creator")) {
			if (map.getTypedValue("creator",String.class)  == null) {
				validResult.put("creator", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}else{
				if (LwxfStringUtils.getStringLength(map.getTypedValue("creator",String.class)) > 13) {
					validResult.put("creator", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
				}
			}
		}
		if(map.containsKey("created")) {
			if (map.get("created")  == null) {
				validResult.put("created", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if(map.containsKey("customOrderId")) {
			if (map.getTypedValue("customOrderId",String.class)  == null) {
				validResult.put("customOrderId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}else{
				if (LwxfStringUtils.getStringLength(map.getTypedValue("customOrderId",String.class)) > 13) {
					validResult.put("customOrderId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
				}
			}
		}
		if(map.containsKey("no")) {
			if (map.getTypedValue("no",String.class)  == null) {
				validResult.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}else{
				if (LwxfStringUtils.getStringLength(map.getTypedValue("no",String.class)) > 50) {
					validResult.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
				}
			}
		}
		if(map.containsKey("designScheme")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("designScheme",String.class)) > 13) {
				validResult.put("designScheme", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(map.containsKey("productSeries")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("productSeries",String.class)) > 50) {
				validResult.put("productSeries", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(map.containsKey("productModel")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("productModel",String.class)) > 50) {
				validResult.put("productModel", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}


	public void setContent(String content){
		this.content=content;
	}

	public String getContent(){
		return content;
	}

	public void setName(String name){
		this.name=name;
	}

	public String getName(){
		return name;
	}

	public void setCreator(String creator){
		this.creator=creator;
	}

	public String getCreator(){
		return creator;
	}

	public void setCreated(Date created){
		this.created=created;
	}

	public Date getCreated(){
		return created;
	}

	public void setCustomOrderId(String customOrderId){
		this.customOrderId=customOrderId;
	}

	public String getCustomOrderId(){
		return customOrderId;
	}

	public void setNo(String no){
		this.no=no;
	}

	public String getNo(){
		return no;
	}

	public void setDesignScheme(String designScheme){
		this.designScheme=designScheme;
	}

	public String getDesignScheme(){
		return designScheme;
	}

	public void setProductSeries(String productSeries){
		this.productSeries=productSeries;
	}

	public String getProductSeries(){
		return productSeries;
	}

	public void setProductModel(String productModel){
		this.productModel=productModel;
	}

	public String getProductModel(){
		return productModel;
	}
}
