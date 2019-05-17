package com.lwxf.industry4.webapp.domain.entity.supplier;
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
 * 功能：supplier_product 实体类
 *
 * @author：F_baisi(F_baisi@163.com)
 * @created：2018-12-28 09:18 
 * @version：2018 Version：1.0 
 * @dept：老屋新房 Created with IntelliJ IDEA 
 */ 
@Table(name = "supplier_product",displayName = "supplier_product")
public class SupplierProduct extends IdEntity  {
	private static final long serialVersionUID = 1L;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "supplier_id",displayName = "供应商id：即company的类型为供应商的公司id")
	private String supplierId;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "product_id",displayName = "产品id：关联product表的id，即厂家创建的产品id")
	private String productId;
	@Column(type = Types.DECIMAL,precision = 10,scale=2,nullable = false,name = "price",displayName = "供应商报价")
	private BigDecimal price;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "creator",displayName = "创建人")
	private String creator;
	@Column(type = TypesExtend.DATETIME,nullable = false,updatable = false,name = "created",displayName = "创建时间")
	private Date created;
	@Column(type = Types.VARCHAR,length = 200,name = "notes",displayName = "供应商的产品说明")
	private String notes;

    public SupplierProduct() {  
     } 

	public RequestResult validateFields() {
		Map<String, String> validResult = new HashMap<>();
		if (this.supplierId == null) {
			validResult.put("supplierId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
 			if (LwxfStringUtils.getStringLength(this.supplierId) > 13) {
				validResult.put("supplierId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.productId == null) {
			validResult.put("productId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
 			if (LwxfStringUtils.getStringLength(this.productId) > 13) {
				validResult.put("productId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
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
		if (this.price==null){
			validResult.put("price",AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (this.price.compareTo(new BigDecimal(100000000))!=-1){
				validResult.put("price",AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
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

	private final static List<String> propertiesList = Arrays.asList("price","notes");

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


	public void setSupplierId(String supplierId){
		this.supplierId=supplierId;
	}

	public String getSupplierId(){
		return supplierId;
	}

	public void setProductId(String productId){
		this.productId=productId;
	}

	public String getProductId(){
		return productId;
	}

	public void setPrice(BigDecimal price){
		this.price=price;
	}

	public BigDecimal getPrice(){
		return price;
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

	public void setNotes(String notes){
		this.notes=notes;
	}

	public String getNotes(){
		return notes;
	}
}
