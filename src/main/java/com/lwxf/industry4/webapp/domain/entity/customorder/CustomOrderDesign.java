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
 * 功能：custom_order_design 实体类
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @created：2018-12-04 05:34 
 * @version：2018 Version：1.0 
 * @company：老屋新房 Created with IntelliJ IDEA 
 */ 
@Table(name = "custom_order_design",displayName = "custom_order_design")
@ApiModel(value = "订单设计信息",discriminator = "订单设计信息")
public class CustomOrderDesign extends IdEntity  {
	private static final long serialVersionUID = 1L;
	@Column(type = Types.VARCHAR,length = 50,nullable = false,name = "name",displayName = "设计名称：比如橱柜、衣柜、阳台，再比如厨房、客厅、卧室等")
	@ApiModelProperty(value = "设计名称")
	private String name;
	@Column(type = Types.CHAR,length = 13,nullable = false,name = "designer",displayName = "设计师用户id")
	@ApiModelProperty(value = "设计师用户id")
	private String designer;
	@Column(type = TypesExtend.DATETIME,nullable = false,name = "created",displayName = "创建时间")
	@ApiModelProperty(value = "创建时间")
	private Date created;
	@Column(type = Types.VARCHAR,length = 500,name = "notes",displayName = "设计说明")
	@ApiModelProperty(value = "设计说明")
	private String notes;
	@Column(type = Types.TINYINT,nullable = false,name = "status",displayName = "状态：0 - 设计中；1 - 待审核；2 - 待发布；3 - 待确认；4 - 已确认，正常情况下，设计的确认是由客户操作的，目前由设计人员进行确认，确认不通过，状态回到0")
	@ApiModelProperty(value = "状态：0 - 设计中；1 - 待审核；2 - 待发布；3 - 待确认；4 - 已确认，正常情况下，设计的确认是由客户操作的，目前由设计人员进行确认，确认不通过，状态回到0")
	private Integer status;
	@Column(type = Types.CHAR,length = 13,nullable = false,name = "custom_order_id",displayName = "订单id")
	@ApiModelProperty(value = "订单id")
	private String customOrderId;
	@Column(type = Types.VARCHAR,length = 50,nullable = false,name = "no",displayName = "编号")
	@ApiModelProperty(value = "编号")
	private String no;
	@Column(type = TypesExtend.DATETIME,nullable = false,name = "end_time",displayName = "设计结束时间")
	@ApiModelProperty(value = "设计结束时间")
    private Date endTime;
	@Column(type = Types.VARCHAR,length = 500,name = "amendments",displayName = "修改意见")
	@ApiModelProperty(value = "修改意见")
	private String amendments;
	@Column(type = Types.DECIMAL,precision = 10,scale=3,nullable = false,name = "valuation",displayName = "估价：默认为0")
	@ApiModelProperty(value = "估价")
	private BigDecimal valuation;
    public CustomOrderDesign() {  
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
		if (this.no == null) {
			validResult.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.no) > 50) {
				validResult.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.designer == null) {
			validResult.put("designer", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
 			if (LwxfStringUtils.getStringLength(this.designer) > 13) {
				validResult.put("designer", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.created == null) {
			validResult.put("created", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (LwxfStringUtils.getStringLength(this.notes) > 500) {
			validResult.put("notes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}

		if (LwxfStringUtils.getStringLength(this.amendments) > 500) {
			validResult.put("amendments", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (this.status == null) {
			validResult.put("status", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (this.customOrderId == null) {
			validResult.put("customOrderId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
 			if (LwxfStringUtils.getStringLength(this.customOrderId) > 13) {
				validResult.put("customOrderId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}

	private final static List<String> propertiesList = Arrays.asList("id","name","designer","created","notes","status","customOrderId","no","amendments","valuation","endTime");

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
		if(map.containsKey("status")) {
			if (!DataValidatorUtils.isByte1(map.getTypedValue("status",String.class))) {
				validResult.put("status", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
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
		if(map.containsKey("no")) {
			if (map.getTypedValue("no",String.class)  == null) {
				validResult.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}else{
				if (LwxfStringUtils.getStringLength(map.getTypedValue("no",String.class)) > 50) {
					validResult.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
				}
			}
		}
		if(map.containsKey("designer")) {
			if (map.getTypedValue("designer",String.class)  == null) {
				validResult.put("designer", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}else{
 				if (LwxfStringUtils.getStringLength(map.getTypedValue("designer",String.class)) > 13) {
					validResult.put("designer", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
				}
			}
		}
		if(map.containsKey("created")) {
			if (map.get("created")  == null) {
				validResult.put("created", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if(map.containsKey("notes")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("notes",String.class)) > 500) {
				validResult.put("notes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(map.containsKey("amendments")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("amendments",String.class)) > 500) {
				validResult.put("amendments", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(map.containsKey("status")) {
			if (map.get("status")  == null) {
				validResult.put("status", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
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
		if(map.containsKey("valuation")) {
			if (!DataValidatorUtils.isDecmal4(map.getTypedValue("valuation",String.class))) {
				validResult.put("valuation", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}


	public void setName(String name){
		this.name=name;
	}

	public String getName(){
		return name;
	}

	public void setDesigner(String designer){
		this.designer=designer;
	}

	public String getDesigner(){
		return designer;
	}

	public void setCreated(Date created){
		this.created=created;
	}

	public Date getCreated(){
		return created;
	}

	public void setNotes(String notes){
		this.notes=notes;
	}

	public String getNotes(){
		return notes;
	}

	public void setStatus(Integer status){
		this.status=status;
	}

	public Integer getStatus(){
		return status;
	}

	public void setCustomOrderId(String customOrderId){
		this.customOrderId=customOrderId;
	}

	public String getCustomOrderId(){
		return customOrderId;
	}
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

	public String getAmendments() {
		return amendments;
	}

	public void setAmendments(String amendments) {
		this.amendments = amendments;
	}

	public BigDecimal getValuation() {
		return valuation;
	}

	public void setValuation(BigDecimal valuation) {
		this.valuation = valuation;
	}
}
