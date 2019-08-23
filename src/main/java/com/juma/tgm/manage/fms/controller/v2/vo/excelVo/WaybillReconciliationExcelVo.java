package com.juma.tgm.manage.fms.controller.v2.vo.excelVo;

import me.about.poi.ExcelColumn;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: WaybillReconciliationExcelVo
 * @Description:
 * @author: liang
 * @date: 2018-06-10 20:26
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
public class WaybillReconciliationExcelVo implements Serializable {

    @ExcelColumn(name = "运单号")
    private String waybillNo;
    @ExcelColumn(name = "用车时间")
    private String planDeliveryTime;
    @ExcelColumn(name = "含税金额")
    private BigDecimal estimateFreight;
    @ExcelColumn(name = "税率")
    private BigDecimal taxRateValue;
    @ExcelColumn(name = "不含税金额")
    private BigDecimal afterTaxFreight;

    /**
     * 计算
     */
    @ExcelColumn(name = "返点费")
    private BigDecimal rebateFee;
    @ExcelColumn(name = "司机搬运费")
    private BigDecimal driverHandlingCost;
    @ExcelColumn(name = "小工搬运费")
    private BigDecimal laborerHandlingCost;
    @ExcelColumn(name = "备注")
    private String waybillRemark;

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public BigDecimal getEstimateFreight() {
        return estimateFreight;
    }

    public void setEstimateFreight(BigDecimal estimateFreight) {
        this.estimateFreight = estimateFreight;
    }

    public BigDecimal getTaxRateValue() {
        return taxRateValue;
    }

    public void setTaxRateValue(BigDecimal taxRateValue) {
        this.taxRateValue = taxRateValue;
    }

    public BigDecimal getAfterTaxFreight() {
        return afterTaxFreight;
    }

    public void setAfterTaxFreight(BigDecimal afterTaxFreight) {
        this.afterTaxFreight = afterTaxFreight;
    }

    public BigDecimal getRebateFee() {
        return rebateFee;
    }

    public void setRebateFee(BigDecimal rebateFee) {
        this.rebateFee = rebateFee;
    }

    public BigDecimal getDriverHandlingCost() {
        return driverHandlingCost;
    }

    public void setDriverHandlingCost(BigDecimal driverHandlingCost) {
        this.driverHandlingCost = driverHandlingCost;
    }

    public BigDecimal getLaborerHandlingCost() {
        return laborerHandlingCost;
    }

    public void setLaborerHandlingCost(BigDecimal laborerHandlingCost) {
        this.laborerHandlingCost = laborerHandlingCost;
    }

    public String getWaybillRemark() {
        return waybillRemark;
    }

    public void setWaybillRemark(String waybillRemark) {
        this.waybillRemark = waybillRemark;
    }

    public String getPlanDeliveryTime() {
        return planDeliveryTime;
    }

    public void setPlanDeliveryTime(String planDeliveryTime) {
        this.planDeliveryTime = planDeliveryTime;
    }
}
