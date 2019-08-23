package com.juma.tgm.manage.fms.controller.v2.vo;

/**
 * Created by admin on 2018/6/11.
 */
public class CalculateAfterTaxFreightByCarVo {

    private String reconciliationId;

    private String plateNumber;

    private String  beforeTaxFreight;

    private Integer vendorId;


    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getBeforeTaxFreight() {
        return beforeTaxFreight;
    }

    public void setBeforeTaxFreight(String beforeTaxFreight) {
        this.beforeTaxFreight = beforeTaxFreight;
    }


    public String getReconciliationId() {
        return reconciliationId;
    }

    public void setReconciliationId(String reconciliationId) {
        this.reconciliationId = reconciliationId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
}
