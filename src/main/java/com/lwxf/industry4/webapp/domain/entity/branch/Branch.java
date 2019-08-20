package com.lwxf.industry4.webapp.domain.entity.branch;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.*;
import java.sql.*;
import java.util.Date;
import java.util.Arrays;
import java.util.List;

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
 * 功能：branch 实体类
 *
 * @author：F_baisi(F_baisi@163.com)
 * @created：2019-06-05 10:19 
 * @version：2018 Version：1.0 
 * @dept：老屋新房 Created with IntelliJ IDEA 
 */ 
@Table(name = "branch",displayName = "branch")
@ApiModel(value = "企业信息",description = "企业信息")
public class Branch extends IdEntity  {
	private static final long serialVersionUID = 1L;
	@Column(type = Types.VARCHAR,length = 50,nullable = false,name = "name",displayName = "企业名称")
	@ApiModelProperty(value = "企业名称")
	private String name;
	@Column(type = Types.TINYINT,nullable = false,name = "status",displayName = "状态:1：试用，2：正常：3：停用")
	@ApiModelProperty(value = "状态:1：试用，2：正常：3：停用")
	private Integer status;
	@Column(type = Types.VARCHAR,length = 255,name = "address",displayName = "企业地址")
	@ApiModelProperty(value = "企业地址")
	private String address;
	@Column(type = Types.VARCHAR,length = 100,name = "tel",displayName = "负责人电话")
	@ApiModelProperty(value = "负责人电话")
	private String tel;
	@Column(type = Types.VARCHAR,length = 50,name = "leader_name",displayName = "负责人名称")
	@ApiModelProperty(value = "负责人名称")
	private String leaderName;
	@Column(type = Types.VARCHAR,length = 50,name = "area",displayName = "地区")
	@ApiModelProperty(value = "地区")
	private String area;
	@Column(type = Types.TINYINT,nullable = false,name = "type",displayName = "类型：1:免费用户，2:付费用户")
	@ApiModelProperty(value = "类型：1:免费用户，2:付费用户")
	private Integer type;
	@Column(type = TypesExtend.DATETIME,nullable = false,updatable = false,name = "created",displayName = "创建时间")
	@ApiModelProperty(value = "创建时间")
	private Date created;
	@Column(type = Types.VARCHAR,name = "expire_date",displayName = "有效期至")
	@ApiModelProperty(value = "有效期至")
	private String expireDate;
	@Column(type = TypesExtend.DATETIME,name = "pay_date",displayName = "付款时间")
	@ApiModelProperty(value = "付款时间")
	private Date payDate;

    public Branch() {  
     } 

	public RequestResult validateFields() {
		Map<String, String> validResult = new HashMap<>();
		if (this.name == null) {
			validResult.put("name", ErrorCodes.VALIDATE_NOTNULL);
		}else{
 			if (LwxfStringUtils.getStringLength(this.name) > 50) {
				validResult.put("name", ErrorCodes.VALIDATE_LENGTH_TOO_LONG);
			}
		}
		if (this.status == null) {
			validResult.put("status", ErrorCodes.VALIDATE_NOTNULL);
		}
		if (LwxfStringUtils.getStringLength(this.address) > 255) {
			validResult.put("address", ErrorCodes.VALIDATE_LENGTH_TOO_LONG);
		}
		if (LwxfStringUtils.getStringLength(this.tel) > 100) {
			validResult.put("tel", ErrorCodes.VALIDATE_LENGTH_TOO_LONG);
		}
		if (LwxfStringUtils.getStringLength(this.leaderName) > 50) {
			validResult.put("leaderName", ErrorCodes.VALIDATE_LENGTH_TOO_LONG);
		}
		if (LwxfStringUtils.getStringLength(this.area) > 50) {
			validResult.put("area", ErrorCodes.VALIDATE_LENGTH_TOO_LONG);
		}
		if (this.type == null) {
			validResult.put("type", ErrorCodes.VALIDATE_NOTNULL);
		}
		if (this.created == null) {
			validResult.put("created", ErrorCodes.VALIDATE_NOTNULL);
		}
		if(validResult.size()>0){
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR,validResult);
		}else {
			return null;
		}
	}

	private final static List<String> propertiesList = Arrays.asList("name","status","address","tel","leaderName","area","type","expireDate","payDate");

	public static RequestResult validateFields(MapContext map) {
		Map<String, String> validResult = new HashMap<>();
		if(map.size()==0){
			return ResultFactory.generateErrorResult("error",ErrorCodes.VALIDATE_NOTNULL);
		}
		boolean flag;
		Set<String> mapSet = map.keySet();
		flag = propertiesList.containsAll(mapSet);
		if(!flag){
			return ResultFactory.generateErrorResult("error",ErrorCodes.VALIDATE_ILLEGAL_ARGUMENT);
		}
		if(map.containsKey("status")) {
			if (!DataValidatorUtils.isByte1(map.getTypedValue("status",String.class))) {
				validResult.put("status", ErrorCodes.VALIDATE_INCORRECT_DATA_FORMAT);
			}
		}
		if(map.containsKey("type")) {
			if (!DataValidatorUtils.isByte1(map.getTypedValue("type",String.class))) {
				validResult.put("type", ErrorCodes.VALIDATE_INCORRECT_DATA_FORMAT);
			}
		}
//		if(map.containsKey("expireDate")) {
//			if (!DataValidatorUtils.isDate(map.getTypedValue("expireDate",String.class))) {
//				validResult.put("expireDate", ErrorCodes.VALIDATE_INCORRECT_DATA_FORMAT);
//			}
//		}
		if(map.containsKey("payDate")) {
			if (!DataValidatorUtils.isDate(map.getTypedValue("payDate",String.class))) {
				validResult.put("payDate", ErrorCodes.VALIDATE_INCORRECT_DATA_FORMAT);
			}
		}
		if(map.containsKey("name")) {
			if (map.getTypedValue("name",String.class)  == null) {
				validResult.put("name", ErrorCodes.VALIDATE_NOTNULL);
			}else{
 				if (LwxfStringUtils.getStringLength(map.getTypedValue("name",String.class)) > 50) {
					validResult.put("name", ErrorCodes.VALIDATE_LENGTH_TOO_LONG);
				}
			}
		}
		if(map.containsKey("status")) {
			if (map.get("status")  == null) {
				validResult.put("status", ErrorCodes.VALIDATE_NOTNULL);
			}
		}
		if(map.containsKey("address")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("address",String.class)) > 255) {
				validResult.put("address", ErrorCodes.VALIDATE_LENGTH_TOO_LONG);
			}
		}
		if(map.containsKey("tel")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("tel",String.class)) > 100) {
				validResult.put("tel", ErrorCodes.VALIDATE_LENGTH_TOO_LONG);
			}
		}
		if(map.containsKey("leaderName")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("leaderName",String.class)) > 50) {
				validResult.put("leaderName", ErrorCodes.VALIDATE_LENGTH_TOO_LONG);
			}
		}
		if(map.containsKey("area")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("area",String.class)) > 50) {
				validResult.put("area", ErrorCodes.VALIDATE_LENGTH_TOO_LONG);
			}
		}
		if(map.containsKey("type")) {
			if (map.get("type")  == null) {
				validResult.put("type", ErrorCodes.VALIDATE_NOTNULL);
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

	public void setStatus(Integer status){
		this.status=status;
	}

	public Integer getStatus(){
		return status;
	}

	public void setAddress(String address){
		this.address=address;
	}

	public String getAddress(){
		return address;
	}

	public void setTel(String tel){
		this.tel=tel;
	}

	public String getTel(){
		return tel;
	}

	public void setLeaderName(String leaderName){
		this.leaderName=leaderName;
	}

	public String getLeaderName(){
		return leaderName;
	}

	public void setArea(String area){
		this.area=area;
	}

	public String getArea(){
		return area;
	}

	public void setType(Integer type){
		this.type=type;
	}

	public Integer getType(){
		return type;
	}

	public void setCreated(Date created){
		this.created=created;
	}

	public Date getCreated(){
		return created;
	}

	public void setExpireDate(String expireDate){
		this.expireDate=expireDate;
	}

	public String getExpireDate(){
		return expireDate;
	}

	public void setPayDate(Date payDate){
		this.payDate=payDate;
	}

	public Date getPayDate(){
		return payDate;
	}
}
