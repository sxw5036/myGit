package com.lwxf.industry4.webapp.domain.entity.procurement;
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
 * 功能：purchase_request 实体类
 *
 * @author：F_baisi(F_baisi@163.com)
 * @created：2018-12-17 10:52 
 * @version：2018 Version：1.0 
 * @dept：老屋新房 Created with IntelliJ IDEA 
 */ 
@Table(name = "purchase_request",displayName = "purchase_request")
public class PurchaseRequest extends IdEntity  {
	private static final long serialVersionUID = 1L;
	@Column(type = Types.VARCHAR,length = 100,nullable = false,name = "name",displayName = "采购名称")
	private String name;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "storage_id",displayName = "仓库id")
	private String storageId;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "product_id",displayName = "产品id")
	private String productId;
	@Column(type = Types.TINYINT,nullable = false,name = "type",displayName = "采购类型：0 - 人为采购；1 - 系统采购；2 - 样品设计采购；3 - 活动采购")
	private Integer type;
	@Column(type = Types.INTEGER,nullable = false,name = "quantity",displayName = "采购数量：默认0")
	private Integer quantity;
	@Column(type = Types.VARCHAR,length = 500,name = "notes",displayName = "采购说明")
	private String notes;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "creator",displayName = "系统采购时存一个默认的系统账号，如：System")
	private String creator;
	@Column(type = TypesExtend.DATETIME,nullable = false,updatable = false,name = "created",displayName = "")
	private Date created;
	@Column(type = Types.TINYINT,nullable = false,name = "status",displayName = "0：待采购；1 - 已采购；2 - 已拒绝；3 - 已取消")
	private Integer status;
	@Column(type = Types.VARCHAR,length = 30,nullable = false,updatable = false,name = "no",displayName = "采购申请唯一编号")
	private String no;

    public PurchaseRequest() {  
     } 

	public RequestResult validateFields() {
		Map<String, String> validResult = new HashMap<>();
		if (this.name == null) {
			validResult.put("name", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
 			if (LwxfStringUtils.getStringLength(this.name) > 100) {
				validResult.put("name", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.storageId == null) {
			validResult.put("storageId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
 			if (LwxfStringUtils.getStringLength(this.storageId) > 13) {
				validResult.put("storageId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.productId == null) {
			validResult.put("productId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
 			if (LwxfStringUtils.getStringLength(this.productId) > 13) {
				validResult.put("productId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.type == null) {
			validResult.put("type", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (this.quantity == null) {
			validResult.put("quantity", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (LwxfStringUtils.getStringLength(this.notes) > 500) {
			validResult.put("notes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
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
		if (this.status == null) {
			validResult.put("status", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (this.no == null) {
			validResult.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
 			if (LwxfStringUtils.getStringLength(this.no) > 30) {
				validResult.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}

	private final static List<String> propertiesList = Arrays.asList("name","type","quantity","notes","status");

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
		if(map.containsKey("quantity")) {
			if (!DataValidatorUtils.isInteger1(map.getTypedValue("quantity",String.class))) {
				validResult.put("quantity", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
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
 				if (LwxfStringUtils.getStringLength(map.getTypedValue("name",String.class)) > 100) {
					validResult.put("name", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
				}
			}
		}
		if(map.containsKey("type")) {
			if (map.get("type")  == null) {
				validResult.put("type", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if(map.containsKey("quantity")) {
			if (map.get("quantity")  == null) {
				validResult.put("quantity", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if(map.containsKey("notes")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("notes",String.class)) > 500) {
				validResult.put("notes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(map.containsKey("status")) {
			if (map.get("status")  == null) {
				validResult.put("status", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
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

	public void setStorageId(String storageId){
		this.storageId=storageId;
	}

	public String getStorageId(){
		return storageId;
	}

	public void setProductId(String productId){
		this.productId=productId;
	}

	public String getProductId(){
		return productId;
	}

	public void setType(Integer type){
		this.type=type;
	}

	public Integer getType(){
		return type;
	}

	public void setQuantity(Integer quantity){
		this.quantity=quantity;
	}

	public Integer getQuantity(){
		return quantity;
	}

	public void setNotes(String notes){
		this.notes=notes;
	}

	public String getNotes(){
		return notes;
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

	public void setStatus(Integer status){
		this.status=status;
	}

	public Integer getStatus(){
		return status;
	}

	public void setNo(String no){
		this.no=no;
	}

	public String getNo(){
		return no;
	}
}
