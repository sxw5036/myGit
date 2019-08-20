package com.lwxf.industry4.webapp.domain.dto.customorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/13 15:57
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@ApiModel(value = "订单信息",discriminator = "订单信息")
public class CustomOrderDto extends CustomOrder{
    @ApiModelProperty(value = "城市名称")
    private String cityName; //城市名称
    @ApiModelProperty(value = "业务员名称")
    private String salesmanName; //业务员名称
    @ApiModelProperty(value = "业务员电话")
    private String salesmanTel;  //业务员电话
    @ApiModelProperty(value = "客户名称")
    private String customerName;  //客户名称
    @ApiModelProperty(value = "业务员头像")
    private String salesmanAvatar;  //业务员头像
    @ApiModelProperty(value = "经销商名称")
    private String dealerName;//经销商名称
    @ApiModelProperty(value = "经销商地址")
    private String dealerAddress;//经销商地址
    @ApiModelProperty(value = "经销商所在地区ID")
    private String dealerCityAreaId;//经销商所在地区ID
    @ApiModelProperty(value = "经销商电话")
    private String dealerTel;//经销商电话
    @ApiModelProperty(value = "创建人名称")
    private String creatorName;//创建人名称
    @ApiModelProperty(value = "设计师名称")
    private String designerName;//设计师名称
    @ApiModelProperty(value = "包裹总数")
    private Integer counts;
    @ApiModelProperty(value = "跟单员名称")
    private String merchandiserName;
    @ApiModelProperty(value = "下单时间")
    private String orderTime;
    @ApiModelProperty(value = "财务审核时间")
    private Date payTime;
    @ApiModelProperty(value = "省ID")
    private String provinceId;
    @ApiModelProperty(value = "市ID")
    private String cityId;
    @ApiModelProperty(value = "业务经理名称")
    private String businessManagerName;
    @ApiModelProperty(value = "业务经理电话")
    private String businessManagerTel;
    @ApiModelProperty(value = "门板包裹数量")
    private Integer doorCount;
    @ApiModelProperty(value = "柜体包裹数量")
    private Integer cabinetCount;
    @ApiModelProperty(value = "五金包裹数量")
    private Integer hardwareCount;
    @ApiModelProperty(value = "是否存在门板生产单")
    private Integer existDoor;
    @ApiModelProperty(value = "是否存在柜体生产单")
    private Integer existCabinet;
    @ApiModelProperty(value = "是否存在五金生产单")
    private Integer existHardware;
    @ApiModelProperty(value = "订单状态")
    private String orderStatus;
    @ApiModelProperty(value = "客户地址")
    private String customerAddress;
    @ApiModelProperty(value = "类型转义")
    private String typeName;
    @ApiModelProperty(value = "是否需要设计转义")
    private String designName;
    @ApiModelProperty(value = "接单员名称")
    private String receiverName;
    @ApiModelProperty(value = "订单产品列表")
    private List<OrderProductDto> orderProductDtoList;


    private BigDecimal orderAmount = new BigDecimal(0);//订单金额


    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getSalesmanAvatar() {
        return salesmanAvatar;
    }

    public void setSalesmanAvatar(String salesmanAvatar) {
        this.salesmanAvatar = salesmanAvatar;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSalesmanName() {
        return salesmanName;
    }

    public void setSalesmanName(String salesmanName) {
        this.salesmanName = salesmanName;
    }

    public String getSalesmanTel() {
        return salesmanTel;
    }

    public void setSalesmanTel(String salesmanTel) {
        this.salesmanTel = salesmanTel;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getDealerAddress() {
        return dealerAddress;
    }

    public void setDealerAddress(String dealerAddress) {
        this.dealerAddress = dealerAddress;
    }

    public String getDealerCityAreaId() {
        return dealerCityAreaId;
    }

    public void setDealerCityAreaId(String dealerCityAreaId) {
        this.dealerCityAreaId = dealerCityAreaId;
    }

    public String getDealerTel() {
        return dealerTel;
    }

    public void setDealerTel(String dealerTel) {
        this.dealerTel = dealerTel;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getDesignerName() {
        return designerName;
    }

    public void setDesignerName(String designerName) {
        this.designerName = designerName;
    }

    public Integer getCounts() {
        return counts;
    }

    public void setCounts(Integer counts) {
        this.counts = counts;
    }

    public String getMerchandiserName() {
        return merchandiserName;
    }

    public void setMerchandiserName(String merchandiserName) {
        this.merchandiserName = merchandiserName;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getBusinessManagerName() {
        return businessManagerName;
    }

    public void setBusinessManagerName(String businessManagerName) {
        this.businessManagerName = businessManagerName;
    }

    public String getBusinessManagerTel() {
        return businessManagerTel;
    }

    public void setBusinessManagerTel(String businessManagerTel) {
        this.businessManagerTel = businessManagerTel;
    }

    public Integer getDoorCount() {
        return doorCount;
    }

    public void setDoorCount(Integer doorCount) {
        this.doorCount = doorCount;
    }

    public Integer getCabinetCount() {
        return cabinetCount;
    }

    public void setCabinetCount(Integer cabinetCount) {
        this.cabinetCount = cabinetCount;
    }

    public Integer getHardwareCount() {
        return hardwareCount;
    }

    public void setHardwareCount(Integer hardwareCount) {
        this.hardwareCount = hardwareCount;
    }

    public Integer getExistDoor() {
        return existDoor;
    }

    public void setExistDoor(Integer existDoor) {
        this.existDoor = existDoor;
    }

    public Integer getExistCabinet() {
        return existCabinet;
    }

    public void setExistCabinet(Integer existCabinet) {
        this.existCabinet = existCabinet;
    }

    public Integer getExistHardware() {
        return existHardware;
    }

    public void setExistHardware(Integer existHardware) {
        this.existHardware = existHardware;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDesignName() {
        return designName;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public List<OrderProductDto> getOrderProductDtoList() {
        return orderProductDtoList;
    }

    public void setOrderProductDtoList(List<OrderProductDto> orderProductDtoList) {
        this.orderProductDtoList = orderProductDtoList;
    }
}

