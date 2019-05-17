package com.lwxf.industry4.webapp.domain.entity.procurement;
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
 * 功能：purchase_product 实体类
 *
 * @author：F_baisi(F_baisi@163.com)
 * @created：2018-12-17 10:52 
 * @version：2018 Version：1.0 
 * @dept：老屋新房 Created with IntelliJ IDEA 
 */ 
@Table(name = "purchase_product",displayName = "purchase_product")
public class PurchaseProduct extends IdEntity  {
	private static final long serialVersionUID = 1L;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "purchase_id",displayName = "采购单id")
	private String purchaseId;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "supplier_product_id",displayName = "")
	private String supplierProductId;
	@Column(type = Types.DECIMAL,precision = 10,scale=2,nullable = false,name = "price",displayName = "采购价：默认为供应商的产品报价,可手工更改")
	private BigDecimal price;
	@Column(type = Types.INTEGER,nullable = false,name = "quantity",displayName = "")
	private Integer quantity;
	@Column(type = Types.TINYINT,name = "status",displayName = "状态：0 - 待质检；1 - 不合格；2 - 待入库；3 - 已入库；4 - 已退货")
	private Integer status;
	@Column(type = Types.VARCHAR,length = 200,name = "notes",displayName = "附加说明：质检未通过或其他不能入库的说明")
	private String notes;

    public PurchaseProduct() {  
     } 

	public RequestResult validateFields() {
		Map<String, String> validResult = new HashMap<>();
		if (this.purchaseId == null) {
			validResult.put("purchaseId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
 			if (LwxfStringUtils.getStringLength(this.purchaseId) > 13) {
				validResult.put("purchaseId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.supplierProductId == null) {
			validResult.put("supplierProductId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
 			if (LwxfStringUtils.getStringLength(this.supplierProductId) > 13) {
				validResult.put("supplierProductId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.quantity == null) {
			validResult.put("quantity", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (LwxfStringUtils.getStringLength(this.notes) > 200) {
			validResult.put("notes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}

	private final static List<String> propertiesList = Arrays.asList("price","quantity","status","notes");

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
		if(map.containsKey("price")) {
			if (!DataValidatorUtils.isDecmal4(new BigDecimal(map.getTypedValue("price",String.class)).toPlainString())) {
				validResult.put("price", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
			if (map.getTypedValue("price",BigDecimal.class).compareTo(new BigDecimal(100000000))!=-1){
				validResult.put("price",AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
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
		if(map.containsKey("quantity")) {
			if (map.get("quantity")  == null) {
				validResult.put("quantity", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if(map.containsKey("notes")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("notes",String.class)) > 200) {
				validResult.put("notes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}


	public void setPurchaseId(String purchaseId){
		this.purchaseId=purchaseId;
	}

	public String getPurchaseId(){
		return purchaseId;
	}

	public void setSupplierProductId(String supplierProductId){
		this.supplierProductId=supplierProductId;
	}

	public String getSupplierProductId(){
		return supplierProductId;
	}

	public void setPrice(BigDecimal price){
		this.price=price;
	}

	public BigDecimal getPrice(){
		return price;
	}

	public void setQuantity(Integer quantity){
		this.quantity=quantity;
	}

	public Integer getQuantity(){
		return quantity;
	}

	public void setStatus(Integer status){
		this.status=status;
	}

	public Integer getStatus(){
		return status;
	}

	public void setNotes(String notes){
		this.notes=notes;
	}

	public String getNotes(){
		return notes;
	}
}
