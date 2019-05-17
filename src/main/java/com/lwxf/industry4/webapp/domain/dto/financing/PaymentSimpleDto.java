package com.lwxf.industry4.webapp.domain.dto.financing;

import com.lwxf.industry4.webapp.common.constant.BizConstant;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentSimpleFunds;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentSimple;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;

@ApiModel(value="日常记账实体",description="日常记账实体")
public class PaymentSimpleDto extends PaymentSimple {
    @ApiModelProperty(value="类型名称",name="companyName")
    private String typeName;
    @ApiModelProperty(value="支付方式名称",name="waysName")
    private String waysName;
    @ApiModelProperty(value="银行名称",name="waysName")
    private String bankName;
    @ApiModelProperty(value="操作人名称",name="operatorName")
    private String operatorName;
    @ApiModelProperty(value="图片附件，多个用逗号间隔)",name="fileIds")
    private String fileIds;
    @ApiModelProperty(value="图片url列表，查询时使用)",name="imgPathList")
    private List<String> imgPathList;
    @ApiModelProperty(value="款项名称",name="fundsName")
    private String fundsName;

    public String getWaysName() {
        return waysName;
    }

    public void setWaysName(String waysName) {
        this.waysName = waysName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getFundsName() {
        return PaymentSimpleFunds.getByValue(this.getFunds())==null?"":PaymentSimpleFunds.getByValue(this.getFunds()).getName();
    }

    public void setFundsName(String fundsName) {
        this.fundsName = fundsName;
    }

    public List<String> getImgPathList() {
        return imgPathList;
    }

    public void setImgPathList(List<String> imgPathList) {
        this.imgPathList = imgPathList;
    }

    public String getFileIds() {
        return fileIds;
    }

    public void setFileIds(String fileIds) {
        this.fileIds = fileIds;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getTypeName() {
        if(this.getType()==1){
            return BizConstant.PAYMENT_SIMPLE_TYPE_1;
        }else{
            return BizConstant.PAYMENT_SIMPLE_TYPE_2;
        }
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
