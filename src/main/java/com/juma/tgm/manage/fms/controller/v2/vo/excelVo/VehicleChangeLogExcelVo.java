package com.juma.tgm.manage.fms.controller.v2.vo.excelVo;

import me.about.poi.ExcelColumn;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: VehicleChangeLogExcelVo
 * @Description:
 * @author: liang
 * @date: 2018-06-10 21:03
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
public class VehicleChangeLogExcelVo implements Serializable {

    @ExcelColumn(name = "司机/承运商姓名")
    private String driverName;

    @ExcelColumn(name = "收车时间")
    private String vehicleUseTime;

    @ExcelColumn(name = "车牌号")
    private String plateNumber;

    @ExcelColumn(name = "含税金额（调整额）")
    private BigDecimal beforeTaxFreight;

    @ExcelColumn(name = "不含税金额（调整额）")
    private BigDecimal afterTaxFreight;

    @ExcelColumn(name = "调整时间")
    private String createTime;

    @ExcelColumn(name = "调整原因")
    private String note;



    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public BigDecimal getBeforeTaxFreight() {
        return beforeTaxFreight;
    }

    public void setBeforeTaxFreight(BigDecimal beforeTaxFreight) {
        this.beforeTaxFreight = beforeTaxFreight;
    }

    public BigDecimal getAfterTaxFreight() {
        return afterTaxFreight;
    }

    public void setAfterTaxFreight(BigDecimal afterTaxFreight) {
        this.afterTaxFreight = afterTaxFreight;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getVehicleUseTime() {
        return vehicleUseTime;
    }

    public void setVehicleUseTime(String vehicleUseTime) {
        this.vehicleUseTime = vehicleUseTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
