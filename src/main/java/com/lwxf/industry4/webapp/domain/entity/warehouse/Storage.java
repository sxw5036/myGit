package com.lwxf.industry4.webapp.domain.entity.warehouse;
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
 * 功能：storage 实体类
 *
 * @author：F_baisi(F_baisi@163.com)
 * @created：2018-12-14 09:59
 * @version：2018 Version：1.0
 * @dept：老屋新房 Created with IntelliJ IDEA
 */
@Table(name = "storage",displayName = "storage")
public class Storage extends IdEntity  {
	private static final long serialVersionUID = 1L;
	@Column(type = Types.VARCHAR,length = 20,nullable = false,name = "name",displayName = "仓库名称")
	private String name;
	@Column(type = Types.CHAR,length = 50,nullable = false,name = "product_category_id",displayName = "产品分类")
	private String productCategoryId;
	@Column(type = Types.VARCHAR,length = 20,nullable = false,name = "key",displayName = "仓库的key，来自于产品分类的key，该字段为隐含字段，在管理页面中不显示，用于业务逻辑处理，存在管理功能可以根据该key判断打开不同的页面")
	private String key;
	@Column(type = Types.VARCHAR,length = 200,name = "notes",displayName = "仓库说明")
	private String notes;
	@Column(type = Types.CHAR,length = 13,nullable = false,updatable = false,name = "creator",displayName = "创建人")
	private String creator;
	@Column(type = TypesExtend.DATETIME,nullable = false,updatable = false,name = "created",displayName = "创建时间")
	private Date created;
	@Column(type = Types.CHAR,length = 13,nullable = false,name = "storekeeper",displayName = "仓库管理员")
	private String storekeeper;

	public Storage() {
	}

	public RequestResult validateFields() {
		Map<String, String> validResult = new HashMap<>();
		if (this.name == null) {
			validResult.put("name", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.name) > 20) {
				validResult.put("name", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.productCategoryId == null) {
			validResult.put("productCategoryId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.productCategoryId) > 50) {
				validResult.put("productCategoryId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.key == null) {
			validResult.put("key", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.key) > 20) {
				validResult.put("key", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (LwxfStringUtils.getStringLength(this.notes) > 200) {
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
		if (this.storekeeper == null) {
			validResult.put("storekeeper", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}else{
			if (LwxfStringUtils.getStringLength(this.storekeeper) > 13) {
				validResult.put("storekeeper", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}

	private final static List<String> propertiesList = Arrays.asList("name","productCategoryId","key","notes","storekeeper");

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
		if(map.containsKey("name")) {
			if (map.getTypedValue("name",String.class)  == null) {
				validResult.put("name", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}else{
				if (LwxfStringUtils.getStringLength(map.getTypedValue("name",String.class)) > 20) {
					validResult.put("name", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
				}
			}
		}
		if(map.containsKey("productCategoryId")) {
			if (map.getTypedValue("productCategoryId",String.class)  == null) {
				validResult.put("productCategoryId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}else{
				if (LwxfStringUtils.getStringLength(map.getTypedValue("productCategoryId",String.class)) > 50) {
					validResult.put("productCategoryId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
				}
			}
		}
		if(map.containsKey("key")) {
			if (map.getTypedValue("key",String.class)  == null) {
				validResult.put("key", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}else{
				if (LwxfStringUtils.getStringLength(map.getTypedValue("key",String.class)) > 20) {
					validResult.put("key", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
				}
			}
		}
		if(map.containsKey("notes")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("notes",String.class)) > 200) {
				validResult.put("notes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if(map.containsKey("storekeeper")) {
			if (map.getTypedValue("storekeeper",String.class)  == null) {
				validResult.put("storekeeper", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}else{
				if (LwxfStringUtils.getStringLength(map.getTypedValue("storekeeper",String.class)) > 13) {
					validResult.put("storekeeper", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
				}
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

	public void setProductCategoryId(String productCategoryId){
		this.productCategoryId=productCategoryId;
	}

	public String getProductCategoryId(){
		return productCategoryId;
	}

	public void setKey(String key){
		this.key=key;
	}

	public String getKey(){
		return key;
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

	public void setStorekeeper(String storekeeper){
		this.storekeeper=storekeeper;
	}

	public String getStorekeeper(){
		return storekeeper;
	}
}
