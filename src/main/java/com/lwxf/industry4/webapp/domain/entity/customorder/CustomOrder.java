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
 * 功能：custom_order 实体类
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @created：2018-12-26 07:31
 * @version：2018 Version：1.0
 * @dept：老屋新房 Created with IntelliJ IDEA
 */
@Table(name = "custom_order", displayName = "custom_order")
@ApiModel(value = "订单信息", discriminator = "订单信息")
public class CustomOrder extends IdEntity {
	private static final long serialVersionUID = 1L;
	@Column(type = Types.VARCHAR, length = 50, name = "no", displayName = "订单编号：按照一定规则编号")
	@ApiModelProperty(value = "订单编号")
	private String no;
	@Column(type = Types.CHAR, length = 13, name = "customer_id", displayName = "客户id")
	@ApiModelProperty(value = "客户id")
	private String customerId;
	@Column(type = Types.CHAR, length = 13, name = "company_id", displayName = "公司id（经销商）")
	@ApiModelProperty(value = "公司id")
	private String companyId;
	@Column(type = Types.CHAR, length = 13, name = "city_area_id", displayName = "客户所在地区id")
	@ApiModelProperty(value = "客户所在地区id")
	private String cityAreaId;
	@Column(type = Types.VARCHAR, length = 100, name = "address", displayName = "订单客户的地址")
	@ApiModelProperty(value = "订单客户的地址")
	private String address;
	@Column(type = Types.VARCHAR, length = 10, name = "acreage", displayName = "客户房屋面积：单位㎡，数据库只存数字")
	@ApiModelProperty(value = "客户房屋面积")
	private String acreage;
	@Column(type = Types.TINYINT, nullable = false, name = "status", displayName = "状态：0 - 待处理（新订单）；1 - 确认需求；2 - 设计待审核；3 - 待设计；4 - 设计中；5 - 设计待确认；6 - 支付待审核；7 - 待生产；8 - 生产中：备料；9 - 生产中：加工；10 - 生产中：封边；11 - 生产中：修边；12 - 生产中：质检；13 - 生产中：包装；14 - 生产中：待入库；15 - 待配送；16 - 配送中；17 - 已配送；18 - 已完成；")
	@ApiModelProperty(value = "状态")
	private Integer status;
	@Column(type = Types.CHAR, length = 13, name = "salesman", displayName = "业务员（经销商）")
	@ApiModelProperty(value = "业务员")
	private String salesman;
	@Column(type = Types.CHAR, length = 13, name = "merchandiser", displayName = "跟单员（厂方）")
	@ApiModelProperty(value = "跟单员")
	private String merchandiser;
	@Column(type = Types.CHAR, length = 13, nullable = false, name = "creator", displayName = "创建人")
	@ApiModelProperty(value = "创建人")
	private String creator;
	@Column(type = TypesExtend.DATETIME, nullable = false, name = "created", displayName = "创建时间")
	@ApiModelProperty(value = "创建时间")
	private Date created;
	@Column(type = Types.CHAR, length = 13, name = "designer", displayName = "设计师")
	@ApiModelProperty(value = "设计师")
	private String designer;
	@Column(type = Types.CHAR, length = 13, name = "design_scheme", displayName = "参考设计方案")
	@ApiModelProperty(value = "参考设计方案")
	private String designScheme;
	@Column(type = Types.INTEGER, nullable = false, name = "earnest", displayName = "客户定金：默认0")
	@ApiModelProperty(value = "客户定金")
	private Integer earnest;
	@Column(type = Types.DECIMAL, precision = 10, scale = 2, nullable = false, name = "imprest", displayName = "客户预付款：默认0")
	@ApiModelProperty(value = "客户预付款")
	private BigDecimal imprest;
	@Column(type = Types.DECIMAL, precision = 10, scale = 2, nullable = false, name = "retainage", displayName = "客户尾款（保留款）：默认0")
	@ApiModelProperty(value = "客户尾款")
	private BigDecimal retainage;
	@Column(type = Types.DECIMAL, precision = 10, scale = 2, nullable = false, name = "amount", displayName = "客户总金额：经销商给客户的最终报价，其值为 market_price - discounts，默认0")
	@ApiModelProperty(value = "客户总金额")
	private BigDecimal amount;
	@Column(type = Types.VARCHAR, length = 200, name = "notes", displayName = "描述")
	@ApiModelProperty(value = "描述")
	private String notes;
	@Column(type = Types.CHAR, length = 13, name = "design_style", displayName = "设计风格")
	@ApiModelProperty(value = "设计风格")
	private String designStyle;
	@Column(type = Types.TINYINT, nullable = false, name = "type", displayName = "订单类型：0 - 正常订单；1 - 补产订单；2 - 返货单；3 - 打样订单；4 - 样板订单；5 - 展示厅订单;6 - 补发订单")
	@ApiModelProperty(value = "订单类型")
	private Integer type;
	@Column(type = Types.CHAR, length = 13, name = "parent_id", displayName = "父订单id：当为增补单时，需要写入原订单id")
	@ApiModelProperty(value = "父订单id")
	private String parentId;
	@Column(type = Types.DATE, name = "estimated_delivery_date", displayName = "预计交货日期：根据厂家的算法，在确认生产时计算一个日期保存进去")
	@ApiModelProperty(value = "预计交货日期")
	private Date estimatedDeliveryDate;
	@Column(type = Types.DATE, name = "delivery_date", displayName = "实际交货日期")
	@ApiModelProperty(value = "实际交货日期")
	private Date deliveryDate;
	@Column(type = Types.INTEGER, nullable = false, name = "design_fee", displayName = "设计费：默认0")
	@ApiModelProperty(value = "设计费")
	private Integer designFee;
	@Column(type = Types.DECIMAL, precision = 10, scale = 2, nullable = false, name = "factory_price", displayName = "厂家报价：对经销商的报价，客户看不到，默认0，即出厂价")
	@ApiModelProperty(value = "厂家报价")
	private BigDecimal factoryPrice;
	@Column(type = Types.CHAR, length = 11, name = "customer_tel", displayName = "客户电话")
	@ApiModelProperty(value = "客户电话")
	private String customerTel;
	@Column(type = Types.DECIMAL, precision = 10, scale = 2, nullable = false, name = "market_price", displayName = "市场价：按市场单价计算出来的价格")
	@ApiModelProperty(value = "市场价")
	private BigDecimal marketPrice;
	@Column(type = Types.DECIMAL, precision = 10, scale = 2, nullable = false, name = "discounts", displayName = "优惠/折扣额：对客户优惠多少钱")
	@ApiModelProperty(value = "优惠/折扣额")
	private BigDecimal discounts;
	@Column(type = Types.DECIMAL, precision = 10, scale = 2, nullable = false, name = "factory_discounts", displayName = "工厂优惠价：工厂对经销商的优惠/折扣额，默认0")
	@ApiModelProperty(value = "工厂优惠价")
	private BigDecimal factoryDiscounts;
	@Column(type = Types.DECIMAL, precision = 10, scale = 2, nullable = false, name = "factory_final_price", displayName = "工厂的最终报价：值为 factory_price - factory_discounts，默认为0")
	@ApiModelProperty(value = "工厂的最终报价")
	private BigDecimal factoryFinalPrice;
	@Column(type = Types.BIT, nullable = false, name = "is_confirm_fprice", displayName = "厂方最终报价是否已确认：false - 未确认；true - 已确认；已确认的，订单报价不可更改，默认为false")
	@ApiModelProperty(value = "厂方最终报价是否已确认：false - 未确认；true - 已确认；已确认的，订单报价不可更改，默认为false")
	private Boolean confirmFprice;
	@Column(type = Types.BIT, nullable = false, name = "is_confirm_cprice", displayName = "经销商最终报价是否已确认：false - 未确认；true - 已确认；已确认的，订单报价不可更改，默认为false")
	@ApiModelProperty(value = "经销商最终报价是否已确认：false - 未确认；true - 已确认；已确认的，订单报价不可更改，默认为false")
	private Boolean confirmCprice;
	@Column(type = Types.TINYINT, name = "is_design", displayName = "是否需要设计，0 - 不需要设计，1 - 需要设计")
	@ApiModelProperty(value = "是否需要设计，0 - 不需要设计，1 - 需要设计")
	private Integer isDesign;
	@Column(type = Types.VARCHAR, length = 50, name = "time_consuming", displayName = "耗时")
	@ApiModelProperty(value = "耗时")
	private String timeConsuming;
	@Column(type = Types.BIT, name = "is_coordination", displayName = "是否需要外协: 0 - 不需要 ; 1 - 需要")
	@ApiModelProperty(value = "是否需要外协: 0 - 不需要 ; 1 - 需要")
	private Boolean coordination;
	@Column(type = Types.VARCHAR, length = 200, name = "documentary_notes", displayName = "跟单备注")
	@ApiModelProperty(value = "跟单备注")
	private String documentaryNotes;
	@Column(type = TypesExtend.DATETIME, name = "documentary_time", displayName = "下车间时间")
	@ApiModelProperty(value = "下车间时间")
	private Date documentaryTime;
	@Column(type = Types.VARCHAR, length = 50, name = "consignee_name", displayName = "收货人姓名")
	@ApiModelProperty(value = "收货人姓名")
	private String consigneeName;
	@Column(type = Types.VARCHAR, length = 20, name = "consignee_tel", displayName = "收货人电话")
	@ApiModelProperty(value = "收货人电话")
	private String consigneeTel;
	@Column(type = Types.VARCHAR, length = 100, name = "discounts_note", displayName = "订单优惠说明")
	@ApiModelProperty(value = "订单优惠说明")
	private String discountsNote;
	private String branchId;
	@Column(type = Types.CHAR, length = 13, nullable = false, name = "receiver", displayName = "接单人")
	@ApiModelProperty(value = "接单人")
	private String receiver;


	public CustomOrder() {
	}

	public RequestResult validateFields() {
		Map<String, String> validResult = new HashMap<>();
		if (LwxfStringUtils.getStringLength(this.no) > 50) {
			validResult.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.customerId) > 13) {
			validResult.put("customerId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.companyId) > 13) {
			validResult.put("companyId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.cityAreaId) > 13) {
			validResult.put("cityAreaId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.address) > 100) {
			validResult.put("address", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.acreage) > 10) {
			validResult.put("acreage", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (this.status == null) {
			validResult.put("status", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (LwxfStringUtils.getStringLength(this.salesman) > 13) {
			validResult.put("salesman", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (this.merchandiser == null) {
			validResult.put("merchandiser", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		} else {
			if (LwxfStringUtils.getStringLength(this.merchandiser) > 13) {
				validResult.put("merchandiser", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.creator == null) {
			validResult.put("creator", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		} else {
			if (LwxfStringUtils.getStringLength(this.creator) > 13) {
				validResult.put("creator", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (this.created == null) {
			validResult.put("created", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (LwxfStringUtils.getStringLength(this.designer) > 13) {
			validResult.put("designer", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.designScheme) > 13) {
			validResult.put("designScheme", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (this.earnest == null) {
			validResult.put("earnest", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (LwxfStringUtils.getStringLength(this.notes) > 200) {
			validResult.put("notes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.designStyle) > 13) {
			validResult.put("designStyle", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (this.type == null) {
			validResult.put("type", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (LwxfStringUtils.getStringLength(this.parentId) > 13) {
			validResult.put("parentId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (this.designFee == null) {
			validResult.put("designFee", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (LwxfStringUtils.getStringLength(this.customerTel) > 11) {
			validResult.put("customerTel", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (this.confirmFprice == null) {
			validResult.put("confirmFprice", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (this.confirmCprice == null) {
			validResult.put("confirmCprice", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		if (LwxfStringUtils.getStringLength(this.timeConsuming) > 50) {
			validResult.put("timeConsuming", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (LwxfStringUtils.getStringLength(this.discountsNote) > 100) {
			validResult.put("discountsNote", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
		}
		if (validResult.size() > 0) {
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, validResult);
		} else {
			return null;
		}
	}

	private final static List<String> propertiesList = Arrays.asList("no", "cityAreaId", "address", "acreage", "status", "salesman", "merchandiser", "designer", "designScheme", "earnest", "imprest", "retainage", "amount", "notes", "designStyle", "type", "parentId", "estimatedDeliveryDate", "deliveryDate", "designFee", "factoryPrice", "customerTel", "marketPrice", "discounts", "factoryDiscounts", "factoryFinalPrice", "confirmFprice", "confirmCprice", "design", "timeConsuming", "coordination", "consigneeName", "consigneeTel", "documentaryNotes", "documentaryTime", "discountsNote");

	public static RequestResult validateFields(MapContext map) {
		Map<String, String> validResult = new HashMap<>();
		if (map.size() == 0) {
			return ResultFactory.generateErrorResult("error", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
		}
		boolean flag;
		Set<String> mapSet = map.keySet();
		flag = propertiesList.containsAll(mapSet);
		if (!flag) {
			return ResultFactory.generateErrorResult("error", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
		}
		if (map.containsKey("status")) {
			if (!DataValidatorUtils.isByte1(map.getTypedValue("status", String.class))) {
				validResult.put("status", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("earnest")) {
			if (!DataValidatorUtils.isInteger1(map.getTypedValue("earnest", String.class))) {
				validResult.put("earnest", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("imprest")) {
			if (!DataValidatorUtils.isDecmal4(map.getTypedValue("imprest", String.class))) {
				validResult.put("imprest", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("retainage")) {
			if (!DataValidatorUtils.isDecmal4(map.getTypedValue("retainage", String.class))) {
				validResult.put("retainage", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("amount")) {
			if (!DataValidatorUtils.isDecmal4(map.getTypedValue("amount", String.class))) {
				validResult.put("amount", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("type")) {
			if (!DataValidatorUtils.isByte1(map.getTypedValue("type", String.class))) {
				validResult.put("type", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("estimatedDeliveryDate")) {
			if (!DataValidatorUtils.isDate(map.getTypedValue("estimatedDeliveryDate", String.class))) {
				validResult.put("estimatedDeliveryDate", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("deliveryDate")) {
			if (!DataValidatorUtils.isDate(map.getTypedValue("deliveryDate", String.class))) {
				validResult.put("deliveryDate", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("designFee")) {
			if (!DataValidatorUtils.isInteger1(map.getTypedValue("designFee", String.class))) {
				validResult.put("designFee", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("factoryPrice")) {
			if (!DataValidatorUtils.isDecmal4(new BigDecimal(map.getTypedValue("price", String.class)).toPlainString())) {
				validResult.put("factoryPrice", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
			if (map.getTypedValue("factoryPrice", BigDecimal.class).compareTo(new BigDecimal(100000000)) != -1) {
				validResult.put("factoryPrice", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("marketPrice")) {
			if (!DataValidatorUtils.isDecmal4(map.getTypedValue("marketPrice", String.class))) {
				validResult.put("marketPrice", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("discounts")) {
			if (!DataValidatorUtils.isDecmal4(map.getTypedValue("discounts", String.class))) {
				validResult.put("discounts", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("factoryDiscounts")) {
			if (!DataValidatorUtils.isDecmal4(map.getTypedValue("factoryDiscounts", String.class))) {
				validResult.put("factoryDiscounts", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("factoryFinalPrice")) {
			if (!DataValidatorUtils.isDecmal4(map.getTypedValue("factoryFinalPrice", String.class))) {
				validResult.put("factoryFinalPrice", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("confirmFprice")) {
			if (!DataValidatorUtils.isBoolean(map.getTypedValue("confirmFprice", String.class))) {
				validResult.put("confirmFprice", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("confirmCprice")) {
			if (!DataValidatorUtils.isBoolean(map.getTypedValue("confirmCprice", String.class))) {
				validResult.put("confirmCprice", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("design")) {
			if (!DataValidatorUtils.isBoolean(map.getTypedValue("design", String.class))) {
				validResult.put("design", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("no")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("no", String.class)) > 50) {
				validResult.put("no", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("cityAreaId")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("cityAreaId", String.class)) > 13) {
				validResult.put("cityAreaId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("address")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("address", String.class)) > 100) {
				validResult.put("address", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("acreage")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("acreage", String.class)) > 10) {
				validResult.put("acreage", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("status")) {
			if (map.get("status") == null) {
				validResult.put("status", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if (map.containsKey("salesman")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("salesman", String.class)) > 13) {
				validResult.put("salesman", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("merchandiser")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("merchandiser", String.class)) > 13) {
				validResult.put("merchandiser", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("designer")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("designer", String.class)) > 13) {
				validResult.put("designer", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("designScheme")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("designScheme", String.class)) > 13) {
				validResult.put("designScheme", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("earnest")) {
			if (map.get("earnest") == null) {
				validResult.put("earnest", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if (map.containsKey("notes")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("notes", String.class)) > 200) {
				validResult.put("notes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("designStyle")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("designStyle", String.class)) > 13) {
				validResult.put("designStyle", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("type")) {
			if (map.get("type") == null) {
				validResult.put("type", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if (map.containsKey("parentId")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("parentId", String.class)) > 13) {
				validResult.put("parentId", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("designFee")) {
			if (map.get("designFee") == null) {
				validResult.put("designFee", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if (map.containsKey("customerTel")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("customerTel", String.class)) > 11) {
				validResult.put("customerTel", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("confirmFprice")) {
			if (map.get("confirmFprice") == null) {
				validResult.put("confirmFprice", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if (map.containsKey("confirmCprice")) {
			if (map.get("confirmCprice") == null) {
				validResult.put("confirmCprice", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			}
		}
		if (map.containsKey("timeConsuming")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("timeConsuming", String.class)) > 50) {
				validResult.put("timeConsuming", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("coordination")) {
			if (!DataValidatorUtils.isBoolean(map.getTypedValue("coordination", String.class))) {
				validResult.put("coordination", AppBeanInjector.i18nUtil.getMessage("VALIDATE_INCORRECT_DATA_FORMAT"));
			}
		}
		if (map.containsKey("consigneeName")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("consigneeName", String.class)) > 50) {
				validResult.put("consigneeName", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("consigneeTel")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("consigneeTel", String.class)) > 20) {
				validResult.put("consigneeTel", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("documentaryNotes")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("documentaryNotes", String.class)) > 200) {
				validResult.put("documentaryNotes", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}
		if (map.containsKey("discountsNote")) {
			if (LwxfStringUtils.getStringLength(map.getTypedValue("discountsNote", String.class)) > 100) {
				validResult.put("discountsNote", AppBeanInjector.i18nUtil.getMessage("VALIDATE_LENGTH_TOO_LONG"));
			}
		}

		if (validResult.size() > 0) {
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, validResult);
		} else {
			return null;
		}
	}


	public void setNo(String no) {
		this.no = no;
	}

	public String getNo() {
		return no;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCityAreaId(String cityAreaId) {
		this.cityAreaId = cityAreaId;
	}

	public String getCityAreaId() {
		return cityAreaId;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setAcreage(String acreage) {
		this.acreage = acreage;
	}

	public String getAcreage() {
		return acreage;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}

	public String getSalesman() {
		return salesman;
	}

	public void setMerchandiser(String merchandiser) {
		this.merchandiser = merchandiser;
	}

	public String getMerchandiser() {
		return merchandiser;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public void setDesigner(String designer) {
		this.designer = designer;
	}

	public String getDesigner() {
		return designer;
	}

	public void setDesignScheme(String designScheme) {
		this.designScheme = designScheme;
	}

	public String getDesignScheme() {
		return designScheme;
	}

	public void setEarnest(Integer earnest) {
		this.earnest = earnest;
	}

	public Integer getEarnest() {
		return earnest;
	}

	public void setImprest(BigDecimal imprest) {
		this.imprest = imprest;
	}

	public BigDecimal getImprest() {
		return imprest;
	}

	public void setRetainage(BigDecimal retainage) {
		this.retainage = retainage;
	}

	public BigDecimal getRetainage() {
		return retainage;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes() {
		return notes;
	}

	public void setDesignStyle(String designStyle) {
		this.designStyle = designStyle;
	}

	public String getDesignStyle() {
		return designStyle;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getType() {
		return type;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
		this.estimatedDeliveryDate = estimatedDeliveryDate;
	}

	public Date getEstimatedDeliveryDate() {
		return estimatedDeliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDesignFee(Integer designFee) {
		this.designFee = designFee;
	}

	public Integer getDesignFee() {
		return designFee;
	}

	public void setFactoryPrice(BigDecimal factoryPrice) {
		this.factoryPrice = factoryPrice;
	}

	public BigDecimal getFactoryPrice() {
		return factoryPrice;
	}

	public void setCustomerTel(String customerTel) {
		this.customerTel = customerTel;
	}

	public String getCustomerTel() {
		return customerTel;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setDiscounts(BigDecimal discounts) {
		this.discounts = discounts;
	}

	public BigDecimal getDiscounts() {
		return discounts;
	}

	public void setFactoryDiscounts(BigDecimal factoryDiscounts) {
		this.factoryDiscounts = factoryDiscounts;
	}

	public BigDecimal getFactoryDiscounts() {
		return factoryDiscounts;
	}

	public void setFactoryFinalPrice(BigDecimal factoryFinalPrice) {
		this.factoryFinalPrice = factoryFinalPrice;
	}

	public BigDecimal getFactoryFinalPrice() {
		return factoryFinalPrice;
	}

	public void setConfirmFprice(Boolean confirmFprice) {
		this.confirmFprice = confirmFprice;
	}

	public Boolean getConfirmFprice() {
		return confirmFprice;
	}

	public void setConfirmCprice(Boolean confirmCprice) {
		this.confirmCprice = confirmCprice;
	}

	public Boolean getConfirmCprice() {
		return confirmCprice;
	}

	public Integer getIsDesign() {
		return isDesign;
	}

	public void setIsDesign(Integer isDesign) {
		this.isDesign = isDesign;
	}

	public String getTimeConsuming() {
		return timeConsuming;
	}

	public void setTimeConsuming(String timeConsuming) {
		this.timeConsuming = timeConsuming;
	}

	public Boolean getCoordination() {
		return coordination;
	}

	public void setCoordination(Boolean coordination) {
		this.coordination = coordination;
	}

	public String getDocumentaryNotes() {
		return documentaryNotes;
	}

	public void setDocumentaryNotes(String documentaryNotes) {
		this.documentaryNotes = documentaryNotes;
	}

	public Date getDocumentaryTime() {
		return documentaryTime;
	}

	public void setDocumentaryTime(Date documentaryTime) {
		this.documentaryTime = documentaryTime;
	}

	public String getConsigneeName() {
		return consigneeName;
	}

	public void setConsigneeName(String consigneeName) {
		this.consigneeName = consigneeName;
	}

	public String getConsigneeTel() {
		return consigneeTel;
	}

	public void setConsigneeTel(String consigneeTel) {
		this.consigneeTel = consigneeTel;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getDiscountsNote() {
		return discountsNote;
	}

	public void setDiscountsNote(String discountsNote) {
		this.discountsNote = discountsNote;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
}
