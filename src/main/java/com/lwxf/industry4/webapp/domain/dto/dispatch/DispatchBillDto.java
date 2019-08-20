package com.lwxf.industry4.webapp.domain.dto.dispatch;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import com.lwxf.industry4.webapp.domain.entity.common.UploadFiles;
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
    @ApiModelProperty(value = "发货包裹集合")
    private List<DispatchBillItemDto> dispatchBillItemDtoList;
    @ApiModelProperty(value = "资源文件ID集合")
    private List<String> fileIds;
    @ApiModelProperty(value = "资源文件集合")
    private List<UploadFiles> uploadFiles;
    @ApiModelProperty(value = "城市名称")
    private String cityName;
    @ApiModelProperty(value = "发货人名称")
    private String delivererName;

    public String getDelivererName() {
        return delivererName;
    }

    public void setDelivererName(String delivererName) {
        this.delivererName = delivererName;
    }

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
    public List<String> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }

    public List<UploadFiles> getUploadFiles() {
        return uploadFiles;
    }

    public void setUploadFiles(List<UploadFiles> uploadFiles) {
        this.uploadFiles = uploadFiles;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}

