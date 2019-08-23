package com.juma.tgm.manage.waybill.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName WaybillPrice.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年5月3日 下午9:00:57
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class WaybillPrice implements Serializable {


    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7181947843059777473L;

    /**
     * 预估费用
     */
    private BigDecimal estimateFreight;
    
    /**
     * 司机结算价
     */
    private BigDecimal show4DriverFreight;
    
    /**
     * 参考报价
     */
    private BigDecimal referenceFreight;
    
    /**
     * 返点费
     */
    private BigDecimal rebateFee;
    
    /**
     * 返点率
     */
    private BigDecimal rebateRate;

    /**
     * 成本价
     */
    private BigDecimal calculatedFreight;

    /**
     * 税后运费
     */
    private BigDecimal afterTaxFreight;

    /**
     * 预估距离
     */
    private Integer estimateDistance;

    /**
     * 预估耗时
     */
    private Integer estimateTimeConsumption;

    /**
     * 起点坐标
     */
    private String startCoordinates;

    /**
     * 终点坐标
     */
    private String endCoordinates;

    /**
     * 高速路费
     */
    private BigDecimal tolls;

    public BigDecimal getEstimateFreight() {
        if(estimateFreight != null) {
            this.estimateFreight = estimateFreight.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
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

    public BigDecimal getAfterTaxFreight() {
        return afterTaxFreight;
    }

    public void setAfterTaxFreight(BigDecimal afterTaxFreight) {
        this.afterTaxFreight = afterTaxFreight;
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

    public BigDecimal getTolls() {
        return tolls;
    }

    public void setTolls(BigDecimal tolls) {
        this.tolls = tolls;
    }

    public BigDecimal getReferenceFreight() {
        if(calculatedFreight != null) {
            this.referenceFreight = calculatedFreight.multiply(new BigDecimal(1.2)).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return referenceFreight;
    }

    public void setReferenceFreight(BigDecimal referenceFreight) {
        this.referenceFreight = referenceFreight;
    }

    public BigDecimal getRebateFee() {
        if(estimateFreight != null && rebateRate != null) {
            this.rebateFee = estimateFreight.multiply(rebateRate).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return rebateFee;
    }

    public void setRebateFee(BigDecimal rebateFee) {
        this.rebateFee = rebateFee;
    }

    public BigDecimal getRebateRate() {
        return rebateRate;
    }

    public void setRebateRate(BigDecimal rebateRate) {
        this.rebateRate = rebateRate;
    }

    public BigDecimal getShow4DriverFreight() {
        return show4DriverFreight;
    }

    public void setShow4DriverFreight(BigDecimal show4DriverFreight) {
        this.show4DriverFreight = show4DriverFreight;
    }

}
