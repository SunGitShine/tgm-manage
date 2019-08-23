package com.juma.tgm.manage.fms.controller.v2.vo;

/**
 * Created by admin on 2018/6/11.
 */
public class CalculateAfterTaxFreightByCustomerVo {

    private String reconciliationId;

    private String  beforeTaxFreight;




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
}
