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
 * 功能：stock 实体类
 *
 * @author：F_baisi(F_baisi@163.com)
 * @created：2018-12-21 04:39
 * @version：2018 Version：1.0
 * @dept：老屋新房 Created with IntelliJ IDEA
 */
@Table(name = "stock",displayName = "stock")
public class Stock extends IdEntity  {
	private static final long serialVersionUID = 1L;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "storage_id",displayName = "仓库id")
	private String storageId;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "product_id",displayName = "产品id")
	private String productId;
	@Column(type = Types.DECIMAL,precision = 10,scale=2,nullable = false,name = "price",displayName = "入库价：计算公式：（库存余量*上次采购价+本次采购量*本次采购价）/(库存余量+本次采购量)，入库时计算出来，可以进行修改，默认0")
	private BigDecimal price;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "operator",displayName = "入库人")
	private String operator;
	@Column(type = TypesExtend.DATETIME,nullable = false,updatable = false,name = "operate_time",displayName = "入库时间")
	private Date operateTime;
	@Column(type = Types.INTEGER,nullable = false,name = "quantity",displayName = "数量：默认0")
	private Integer quantity;
	@Column(type = Types.CHAR,length = 10,nullable = false,name = "shelf",displayName = "所在货架(整行或技巧)：做成枚举，入库时作为下拉选择项，枚举项为：A、B、C、D、E、F、J：产品定为由货架的行、列、层三维定为，行为整行的货架，列为每行中的某个货架，层为货架的上、中、下三层（类似高低床）")
	private String shelf;
	@Column(type = Types.TINYINT,nullable = false,name = "column",displayName = "所在货架的列位，作为下拉选择项：1、2、3、4、5、6、7、8、9、10、11、12、13、14")
	private Integer column;
	@Column(type = Types.TINYINT,nullable = false,name = "tier",displayName = "所在货架的层：1 - 第一层；2 - 第二层；3 -第三层，作为下拉选择项")
	private Integer tier;
	@Column(type = Types.INTEGER,nullable = false,name = "pre_output",displayName = "预出库数量")
	private Integer preOutput;

	public Stock() {
	}

	public RequestResult validateFields() {
		Map<String, String> validResult = new HashMap<>();
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
		if (this.operator == null) {
			validResult.put("operator", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.operator) > 13) {
				validResult.put("operator", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.operateTime == null) {
			validResult.put("operateTime", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
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
		if (this.preOutput == null) {
			validResult.put("preOutput", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}

	private final static List<String> propertiesList = Arrays.asList("price","quantity","shelf","column","tier","preOutput");

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
		if(map.containsKey("preOutput")) {
			if (!DataValidatorUtils.isInteger1(map.getTypedValue("preOutput",String.class))) {
				validResult.put("preOutput", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if(map.containsKey("quantity")) {
			if (map.get("quantity")  == null) {
				validResult.put("quantity", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
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
		if(map.containsKey("preOutput")) {
			if (map.get("preOutput")  == null) {
				validResult.put("preOutput", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
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

	public void setPrice(BigDecimal price){
		this.price=price;
	}

	public BigDecimal getPrice(){
		return price;
	}

	public void setOperator(String operator){
		this.operator=operator;
	}

	public String getOperator(){
		return operator;
	}

	public void setOperateTime(Date operateTime){
		this.operateTime=operateTime;
	}

	public Date getOperateTime(){
		return operateTime;
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

	public void setPreOutput(Integer preOutput){
		this.preOutput=preOutput;
	}

	public Integer getPreOutput(){
		return preOutput;
	}
}
