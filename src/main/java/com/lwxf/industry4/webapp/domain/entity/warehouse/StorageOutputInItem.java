package com.lwxf.industry4.webapp.domain.entity.warehouse;
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
 * 功能：storage_output_in_item 实体类
 *
 * @author：F_baisi(F_baisi@163.com)
 * @created：2018-12-28 03:50
 * @version：2018 Version：1.0
 * @dept：老屋新房 Created with IntelliJ IDEA
 */
@Table(name = "storage_output_in_item",displayName = "storage_output_in_item")
public class StorageOutputInItem extends IdEntity  {
	private static final long serialVersionUID = 1L;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "output_in_id",displayName = "出入库单id")
	private String outputInId;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "product_id",displayName = "产品id")
	private String productId;
	@Column(type = Types.DECIMAL,precision = 10,scale=2,nullable = false,updatable = false,name = "price",displayName = "入库/出库的价格，入库：入库时的平均价；出库：出库时的库存价")
	private BigDecimal price;
	@Column(type = Types.INTEGER,nullable = false,updatable = false,name = "quantity",displayName = "数量")
	private Integer quantity;
	@Column(type = Types.CHAR,length = 10,nullable = false,name = "shelf",displayName = "所在货架(整行或技巧)：做成枚举，入库时作为下拉选择项，枚举项为：A、B、C、D、E、F、J：产品定为由货架的行、列、层三维定为，行为整行的货架，列为每行中的某个货架，层为货架的上、中、下三层（类似高低床）")
	private String shelf;
	@Column(type = Types.TINYINT,nullable = false,name = "column",displayName = "所在货架的列位，作为下拉选择项：1、2、3、4、5、6、7、8、9、10、11、12、13、14")
	private Integer column;
	@Column(type = Types.TINYINT,nullable = false,name = "tier",displayName = "所在货架的层：1 - 第一层；2 - 第二层；3 -第三层，作为下拉选择项")
	private Integer tier;

	public StorageOutputInItem() {
	}

	public RequestResult validateFields() {
		Map<String, String> validResult = new HashMap<>();
		if (this.outputInId == null) {
			validResult.put("outputInId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.outputInId) > 13) {
				validResult.put("outputInId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.productId == null) {
			validResult.put("productId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.productId) > 13) {
				validResult.put("productId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.quantity == null) {
			validResult.put("quantity", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (this.shelf == null) {
			validResult.put("shelf", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.shelf) > 10) {
				validResult.put("shelf", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.column == null) {
			validResult.put("column", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (this.tier == null) {
			validResult.put("tier", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (this.price==null){
			validResult.put("price",AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (this.price.compareTo(new BigDecimal(10000000))!=-1){
				validResult.put("price",AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}

	private final static List<String> propertiesList = Arrays.asList("shelf","column","tier");

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
		if(map.containsKey("column")) {
			if (!DataValidatorUtils.isByte1(map.getTypedValue("column",String.class))) {
				validResult.put("column", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if(map.containsKey("tier")) {
			if (!DataValidatorUtils.isByte1(map.getTypedValue("tier",String.class))) {
				validResult.put("tier", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if(map.containsKey("shelf")) {
			if (map.getTypedValue("shelf",String.class)  == null) {
				validResult.put("shelf", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}else{
				if (LwxfStringUtils.getStringLength(map.getTypedValue("shelf",String.class)) > 10) {
					validResult.put("shelf", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
				}
			}
		}
		if(map.containsKey("column")) {
			if (map.get("column")  == null) {
				validResult.put("column", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if(map.containsKey("tier")) {
			if (map.get("tier")  == null) {
				validResult.put("tier", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}


	public void setOutputInId(String outputInId){
		this.outputInId=outputInId;
	}

	public String getOutputInId(){
		return outputInId;
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

	public void setQuantity(Integer quantity){
		this.quantity=quantity;
	}

	public Integer getQuantity(){
		return quantity;
	}

	public void setShelf(String shelf){
		this.shelf=shelf;
	}

	public String getShelf(){
		return shelf;
	}

	public void setColumn(Integer column){
		this.column=column;
	}

	public Integer getColumn(){
		return column;
	}

	public void setTier(Integer tier){
		this.tier=tier;
	}

	public Integer getTier(){
		return tier;
	}
}
