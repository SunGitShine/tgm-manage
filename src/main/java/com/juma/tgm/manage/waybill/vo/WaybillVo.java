package com.juma.tgm.manage.waybill.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.juma.tgm.waybill.domain.WaybillDeliveryAddress;
import com.juma.tgm.waybill.domain.WaybillReceiveAddress;

/**
 * @ClassName WaybillBo.java
 * @Description 建单
 * @author Libin.Wei
 * @Date 2017年1月12日 下午5:54:57
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class WaybillVo implements Serializable {

    private static final long serialVersionUID = -4832208086910088127L;
    private Integer waybillId;
    private String truckCustomerName;
    private String startCoordinates;
    private String endCoordinates;
    private Integer estimateDistance;
    private Integer estimateTimeConsumption;
    private BigDecimal estimateFreight;
    private BigDecimal calculatedFreight;
    private String planDeliveryDate;
    private Integer receiptType;
    private String additionalFunctionIds;
    private String goodsType;
    private String goodsWeight;
    private String goodsVolume;
    private Integer taxRateId;
    private String remark;
    private List<WaybillDeliveryAddress> deliveryAddress = new ArrayList<WaybillDeliveryAddress>();
    private List<WaybillReceiveAddress> receiveAddress = new ArrayList<WaybillReceiveAddress>();

    public Integer getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(Integer waybillId) {
        this.waybillId = waybillId;
    }

    public String getTruckCustomerName() {
        return truckCustomerName;
    }

    public void setTruckCustomerName(String truckCustomerName) {
        this.truckCustomerName = truckCustomerName;
    }

    public String getStartCoordinates() {
        return startCoordinates;
    }

    public void setStartCoordinates(String startCoordinates) {
        this.startCoordinates = startCoordinates;
    }

    public String getEndCoordinates() {
        return endCoordinates;
    }

    public void setEndCoordinates(String endCoordinates) {
        this.endCoordinates = endCoordinates;
    }

    public Integer getEstimateDistance() {
        return estimateDistance;
    }

    public void setEstimateDistance(Integer estimateDistance) {
        this.estimateDistance = estimateDistance;
    }

    public Integer getEstimateTimeConsumption() {
        return estimateTimeConsumption;
    }

    public void setEstimateTimeConsumption(Integer estimateTimeConsumption) {
        this.estimateTimeConsumption = estimateTimeConsumption;
    }

    public BigDecimal getEstimateFreight() {
        return estimateFreight;
    }

    public void setEstimateFreight(BigDecimal estimateFreight) {
        this.estimateFreight = estimateFreight;
    }

    public BigDecimal getCalculatedFreight() {
        return calculatedFreight;
    }

    public void setCalculatedFreight(BigDecimal calculatedFreight) {
        this.calculatedFreight = calculatedFreight;
    }

    public String getPlanDeliveryDate() {
        return planDeliveryDate;
    }

    public void setPlanDeliveryDate(String planDeliveryDate) {
        this.planDeliveryDate = planDeliveryDate;
    }

    public Integer getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(Integer receiptType) {
        this.receiptType = receiptType;
    }

    public String getAdditionalFunctionIds() {
        return additionalFunctionIds;
    }

    public void setAdditionalFunctionIds(String additionalFunctionIds) {
        this.additionalFunctionIds = additionalFunctionIds;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsWeight() {
        return goodsWeight;
    }

    public void setGoodsWeight(String goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    public String getGoodsVolume() {
        return goodsVolume;
    }

    public void setGoodsVolume(String goodsVolume) {
        this.goodsVolume = goodsVolume;
    }

    public Integer getTaxRateId() {
        return taxRateId;
    }

    public void setTaxRateId(Integer taxRateId) {
        this.taxRateId = taxRateId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<WaybillDeliveryAddress> getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(List<WaybillDeliveryAddress> deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<WaybillReceiveAddress> getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(List<WaybillReceiveAddress> receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    @Override
    public String toString() {
        return "WaybillVo [waybillId=" + waybillId + ", truckCustomerName=" + truckCustomerName + ", startCoordinates="
                + startCoordinates + ", endCoordinates=" + endCoordinates + ", estimateDistance=" + estimateDistance
                + ", estimateTimeConsumption=" + estimateTimeConsumption + ", estimateFreight=" + estimateFreight
                + ", calculatedFreight=" + calculatedFreight + ", planDeliveryDate=" + planDeliveryDate
                + ", receiptType=" + receiptType + ", additionalFunctionIds=" + additionalFunctionIds + ", goodsType="
                + goodsType + ", goodsWeight=" + goodsWeight + ", goodsVolume=" + goodsVolume + ", taxRateId="
                + taxRateId + ", remark=" + remark + ", deliveryAddress=" + deliveryAddress + ", receiveAddress="
                + receiveAddress + "]";
    }

}
