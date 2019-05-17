package com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp;

import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentWay;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * F端app财务-经销商财务信息详情
 * created by zhangxiaolin
 */
public class CompanyFinanceInfoDto {


    private Date created;
    private String companyName;
    private Integer type;
    private Integer way;
    private String wayName;
    private String typeName;
    private Integer funds;
    private Double amount;
    private String notes;
    private List<String> fileList;

    public Integer getWay() {
        return way;
    }

    public void setWay(Integer way) {
        this.way = way;
    }

    public String getWayName() {
        return PaymentWay.getByValue(this.way)==null?"":PaymentWay.getByValue(this.way).getName();
    }

    public void setWayName(String wayName) {
        this.wayName = wayName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeName() {
        return PaymentType.getByValue(this.type)==null?"":PaymentType.getByValue(this.type).getName();
    }

    public Integer getFunds() {
        return funds;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getFundsName() {
        return PaymentFunds.getByValue(this.getFunds())==null?"":PaymentFunds.getByValue(this.getFunds()).getName();
    }

    public void setFunds(Integer funds) {
        this.funds = funds;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }
}
