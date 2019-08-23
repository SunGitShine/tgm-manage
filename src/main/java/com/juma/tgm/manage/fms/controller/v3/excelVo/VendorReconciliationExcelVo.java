package com.juma.tgm.manage.fms.controller.v3.excelVo;

import me.about.poi.ExcelColumn;

import java.io.Serializable;
import java.math.BigDecimal;

public class VendorReconciliationExcelVo implements Serializable {

    @ExcelColumn(name = "承运商")
    private String name;

    @ExcelColumn(name = "车牌号")
    private String plateNumber;

    @ExcelColumn(name = "承运商含税金额")
    private BigDecimal estimateFreight;

    @ExcelColumn(name = "对账后调整金额")
    private BigDecimal adjustFreight;

    @ExcelColumn(name = "最终含税金额")
    private BigDecimal afterAdjustFreight;

    @ExcelColumn(name = "司机搬运费")
    private BigDecimal driverTransportFee;

    @ExcelColumn(name = "小工搬运费")
    private BigDecimal temporaryTransportFee;

    @ExcelColumn(name = "管理费")
    private BigDecimal managementFee;

    @ExcelColumn(name = "可抵扣进项税")
    private BigDecimal deductionTaxFee;

    @ExcelColumn(name = "计税参考")
    private BigDecimal referenceTaxFee;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public BigDecimal getEstimateFreight() {
        return estimateFreight;
    }

    public void setEstimateFreight(BigDecimal estimateFreight) {
        this.estimateFreight = estimateFreight;
    }

    public BigDecimal getAdjustFreight() {
        return adjustFreight;
    }

    public void setAdjustFreight(BigDecimal adjustFreight) {
        this.adjustFreight = adjustFreight;
    }

    public BigDecimal getAfterAdjustFreight() {
        return afterAdjustFreight;
    }

    public void setAfterAdjustFreight(BigDecimal afterAdjustFreight) {
        this.afterAdjustFreight = afterAdjustFreight;
    }

    public BigDecimal getDriverTransportFee() {
        return driverTransportFee;
    }

    public void setDriverTransportFee(BigDecimal driverTransportFee) {
        this.driverTransportFee = driverTransportFee;
    }

    public BigDecimal getTemporaryTransportFee() {
        return temporaryTransportFee;
    }

    public void setTemporaryTransportFee(BigDecimal temporaryTransportFee) {
        this.temporaryTransportFee = temporaryTransportFee;
    }

    public BigDecimal getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(BigDecimal managementFee) {
        this.managementFee = managementFee;
    }

    public BigDecimal getDeductionTaxFee() {
        return deductionTaxFee;
    }

    public void setDeductionTaxFee(BigDecimal deductionTaxFee) {
        this.deductionTaxFee = deductionTaxFee;
    }

    public BigDecimal getReferenceTaxFee() {
        return referenceTaxFee;
    }

    public void setReferenceTaxFee(BigDecimal referenceTaxFee) {
        this.referenceTaxFee = referenceTaxFee;
    }
}
