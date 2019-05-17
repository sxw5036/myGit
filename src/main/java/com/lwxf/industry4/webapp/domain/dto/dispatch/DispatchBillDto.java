package com.lwxf.industry4.webapp.domain.dto.dispatch;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBill;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/20 15:42
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@ApiModel(value = "配送单信息",discriminator = "配送单信息")
public class DispatchBillDto extends DispatchBill {
    @ApiModelProperty(value = "物流名称")
    private String logisticsName;//物流名称
    @ApiModelProperty(value = "发货人名字")
    private String consignorName;//发货人名字
    @ApiModelProperty(value = "经销商名称")
    private String dealerName;//经销商名称
    @ApiModelProperty(value = "经销商地址")
    private String dealerAddress;//经销商地址
    @ApiModelProperty(value = "经销商所在地区ID")
    private String dealerCityAreaId;//经销商所在地区ID
    @ApiModelProperty(value = "经销商电话")
    private String dealerTel;//经销商电话
    @ApiModelProperty(value = "订单中收货人名称")
    private String orderConsigneeName;//订单中收货人名称
    @ApiModelProperty(value = "订单中收货人地址")
    private String orderConsigneeAddress;//订单中收货人地址
    @ApiModelProperty(value = "订单中收货人所在地区ID")
    private String orderConsigneeCityAreaId;//订单中收货人所在地区ID
    @ApiModelProperty(value = "订单中的收货人的电话")
    private String orderConsigneeTel;//订单中的收货人的电话
    @ApiModelProperty(value = "发货包裹集合")
    private List<DispatchBillItemDto> dispatchBillItemDtoList;

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getConsignorName() {
        return consignorName;
    }

    public void setConsignorName(String consignorName) {
        this.consignorName = consignorName;
    }

    public List<DispatchBillItemDto> getDispatchBillItemDtoList() {
        return dispatchBillItemDtoList;
    }

    public void setDispatchBillItemDtoList(List<DispatchBillItemDto> dispatchBillItemDtoList) {
        this.dispatchBillItemDtoList = dispatchBillItemDtoList;
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

    public String getOrderConsigneeName() {
        return orderConsigneeName;
    }

    public void setOrderConsigneeName(String orderConsigneeName) {
        this.orderConsigneeName = orderConsigneeName;
    }

    public String getOrderConsigneeAddress() {
        return orderConsigneeAddress;
    }

    public void setOrderConsigneeAddress(String orderConsigneeAddress) {
        this.orderConsigneeAddress = orderConsigneeAddress;
    }

    public String getDealerCityAreaId() {
        return dealerCityAreaId;
    }

    public void setDealerCityAreaId(String dealerCityAreaId) {
        this.dealerCityAreaId = dealerCityAreaId;
    }

    public String getOrderConsigneeCityAreaId() {
        return orderConsigneeCityAreaId;
    }

    public void setOrderConsigneeCityAreaId(String orderConsigneeCityAreaId) {
        this.orderConsigneeCityAreaId = orderConsigneeCityAreaId;
    }

    public String getDealerTel() {
        return dealerTel;
    }

    public void setDealerTel(String dealerTel) {
        this.dealerTel = dealerTel;
    }

    public String getOrderConsigneeTel() {
        return orderConsigneeTel;
    }

    public void setOrderConsigneeTel(String orderConsigneeTel) {
        this.orderConsigneeTel = orderConsigneeTel;
    }
}

