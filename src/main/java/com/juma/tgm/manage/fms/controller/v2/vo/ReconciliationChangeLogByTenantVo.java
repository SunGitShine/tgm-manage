package com.juma.tgm.manage.fms.controller.v2.vo;

import java.io.Serializable;
import java.util.Date;

public class ReconciliationChangeLogByTenantVo implements Serializable{


    private Integer reconciliationId;



    private String  beforeTaxFreight;

    private String afterTaxFreight;


    private Date createTime;

    private String note;



    private static final long serialVersionUID = 1L;


    public Integer getReconciliationId() {
        return reconciliationId;
    }

    public void setReconciliationId(Integer reconciliationId) {
        this.reconciliationId = reconciliationId;
    }



    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
