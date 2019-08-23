package com.juma.tgm.manage.fms.controller.v2.vo.excelVo;

import me.about.poi.ExcelColumn;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: VehicleReconciliationExcelVo
 * @Description:
 * @author: liang
 * @date: 2018-06-10 20:14
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
public class VehicleReconciliationExcelVo implements Serializable {

    @ExcelColumn(name = "结算对象")
    private String settlementObject;

    @ExcelColumn(name = "车牌号/承运商")
    private String plateNumber;

    @ExcelColumn(name = "返点费")
    private BigDecimal rebateFee;

    @ExcelColumn(name = "司机搬运费")
    private BigDecimal driverHandlingFee;

    @ExcelColumn(name = "小工搬运费")
    private BigDecimal laborerHandlingFee;

    @ExcelColumn(name = "含税金额（调整前）")
    private BigDecimal driverInitialBeforeTax;

    @ExcelColumn(name = "不含税金额（调整前）")
    private BigDecimal driverInitialAfterTax;

    @ExcelColumn(name = "含税金额（调整后）")
    private BigDecimal driverFinalBeforeTax;

    @ExcelColumn(name = "不含税金额（调整后）")
    private BigDecimal driverFinalAfterTax;

    @ExcelColumn(name = "结算状态")
    private String payStatusName;

    public String getSettlementObject() {
        return settlementObject;
    }

    public void setSettlementObject(String settlementObject) {
        this.settlementObject = settlementObject;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public BigDecimal getRebateFee() {
        return rebateFee;
    }

    public void setRebateFee(BigDecimal rebateFee) {
        this.rebateFee = rebateFee;
    }

    public BigDecimal getDriverHandlingFee() {
        return driverHandlingFee;
    }

    public void setDriverHandlingFee(BigDecimal driverHandlingFee) {
        this.driverHandlingFee = driverHandlingFee;
    }

    public BigDecimal getLaborerHandlingFee() {
        return laborerHandlingFee;
    }

    public void setLaborerHandlingFee(BigDecimal laborerHandlingFee) {
        this.laborerHandlingFee = laborerHandlingFee;
    }

    public BigDecimal getDriverInitialBeforeTax() {
        return driverInitialBeforeTax;
    }

    public void setDriverInitialBeforeTax(BigDecimal driverInitialBeforeTax) {
        this.driverInitialBeforeTax = driverInitialBeforeTax;
    }

    public BigDecimal getDriverInitialAfterTax() {
        return driverInitialAfterTax;
    }

    public void setDriverInitialAfterTax(BigDecimal driverInitialAfterTax) {
        this.driverInitialAfterTax = driverInitialAfterTax;
    }

    public BigDecimal getDriverFinalBeforeTax() {
        return driverFinalBeforeTax;
    }

    public void setDriverFinalBeforeTax(BigDecimal driverFinalBeforeTax) {
        this.driverFinalBeforeTax = driverFinalBeforeTax;
    }

    public BigDecimal getDriverFinalAfterTax() {
        return driverFinalAfterTax;
    }

    public void setDriverFinalAfterTax(BigDecimal driverFinalAfterTax) {
        this.driverFinalAfterTax = driverFinalAfterTax;
    }

    public String getPayStatusName() {
        return payStatusName;
    }

    public void setPayStatusName(String payStatusName) {
        this.payStatusName = payStatusName;
    }
}
