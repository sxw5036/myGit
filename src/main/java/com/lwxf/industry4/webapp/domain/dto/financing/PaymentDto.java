package com.lwxf.industry4.webapp.domain.dto.financing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.lwxf.industry4.webapp.domain.entity.financing.Payment;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/19 15:23
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@ApiModel(value = "支付记录信息",discriminator = "支付记录信息")
public class PaymentDto extends Payment {

    @ApiModelProperty(value = "收款人")
    private  String payeeName;//收款人
    @ApiModelProperty(value = "审核人")
    private  String auditorName;//审核人
    @ApiModelProperty(value = "交易类型，线上，线下等")
    private Integer dalType;//交易类型，线上，线下等
    @ApiModelProperty(value = "创建人")
    private String creatorName;//创建人
    @ApiModelProperty(value = "转账时间")
    private String transferTime;//转账时间
    @ApiModelProperty(value = "订单编号")
    private String orderNo;//订单编号
    @ApiModelProperty(value = "公司名称")
    private String companyName;//公司名称
    @ApiModelProperty(value = "客户名称")
    private String customName;//客户名称
    @ApiModelProperty(value = "订单类型:0 - 正常订单；1 - 补产订单；2 - 返货单；3 - 打样订单；4 - 样板订单；5 - 展示厅订单;6 - 补发订单")
    private Integer orderType;

    public String getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(String transferTime) {
        transferTime = transferTime;
    }

    public Integer getDalType() {
        return dalType;
    }

    public void setDalType(Integer dalType) {
        this.dalType = dalType;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
}

