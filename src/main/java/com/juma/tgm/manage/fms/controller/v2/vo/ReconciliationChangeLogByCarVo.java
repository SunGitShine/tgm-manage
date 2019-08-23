package com.juma.tgm.manage.fms.controller.v2.vo;

import java.io.Serializable;
import java.util.Date;

public class ReconciliationChangeLogByCarVo implements Serializable{

    private Integer reconciliationId;


    private String driverName;

    private String plateNumber;

    private String beforeTaxFreight;


    private String afterTaxFreight;

    private String userName;

    private Integer customerId;

    private String customerName;

    private String note;

    private Integer createUserId;

    private Date createTime;

    private Date vehicleUseTime;

    private Integer vendorId;

    private String vendorName;


    private static final long serialVersionUID = 1L;

    public Integer getVendorId() {
        return vendorId;
    }


    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public Integer getReconciliationId() {
        return reconciliationId;
    }

    public void setReconciliationId(Integer reconciliationId) {
        this.reconciliationId = reconciliationId;
    }


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



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getVehicleUseTime() {
        return vehicleUseTime;
    }

    public void setVehicleUseTime(Date vehicleUseTime) {
        this.vehicleUseTime = vehicleUseTime;
    }

    public String getBeforeTaxFreight() {
        return beforeTaxFreight;
    }

    public void setBeforeTaxFreight(String beforeTaxFreight) {
        this.beforeTaxFreight = beforeTaxFreight;
    }

    public String getAfterTaxFreight() {
        return afterTaxFreight;
    }

    public void setAfterTaxFreight(String afterTaxFreight) {
        this.afterTaxFreight = afterTaxFreight;
    }
}
