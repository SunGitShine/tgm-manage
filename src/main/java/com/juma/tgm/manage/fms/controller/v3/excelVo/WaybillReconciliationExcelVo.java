package com.juma.tgm.manage.fms.controller.v3.excelVo;

import me.about.poi.ExcelColumn;

import java.io.Serializable;
import java.math.BigDecimal;

public class WaybillReconciliationExcelVo implements Serializable {

    @ExcelColumn(name = "运单号")
    private String waybillNo;

    @ExcelColumn(name = "用车时间")
    private String planDeliveryTime;

    @ExcelColumn(name = "承运商")
    private String name;

    @ExcelColumn(name = "司机类型")
    private String driverTypeName;

    @ExcelColumn(name = "车牌号")
    private String plateNumber;

    @ExcelColumn(name = "承运商含税金额")
    private BigDecimal show4DriverFreight;

    @ExcelColumn(name = "对账后调整金额")
    private BigDecimal adjustFreight;

    @ExcelColumn(name = "承运侧最终含税金额")
    private BigDecimal afterAdjustFreight;

    @ExcelColumn(name = "司机搬运费")
    private BigDecimal driverTransportFee;

    @ExcelColumn(name = "小工搬运费")
    private BigDecimal temporaryTransportFee;

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getPlanDeliveryTime() {
        return planDeliveryTime;
    }

    public void setPlanDeliveryTime(String planDeliveryTime) {
        this.planDeliveryTime = planDeliveryTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriverTypeName() {
        return driverTypeName;
    }

    public void setDriverTypeName(String driverTypeName) {
        this.driverTypeName = driverTypeName;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public BigDecimal getShow4DriverFreight() {
        return show4DriverFreight;
    }

    public void setShow4DriverFreight(BigDecimal show4DriverFreight) {
        this.show4DriverFreight = show4DriverFreight;
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
}
