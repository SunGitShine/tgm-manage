package com.juma.tgm.manage.waybill.vo;

import java.util.Date;

/**
 * Created by shawn_lin on 2017/7/25.
 */
public class FreightAuditVo {
    private Integer waybillId;
    private String waybillNo;
    private Date planDeliveryDate;
    private String customerName;
    private String customerManageName;
    /** 原税前费用 */
    private String estimateFreight;
    /** 新税前费用 */
    private String freightToBeAudited;
    private String remark;
    /** 审核状态 */
    private String UpdateFreightAuditStatus;

    public Integer getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(Integer waybillId) {
        this.waybillId = waybillId;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public Date getPlanDeliveryDate() {
        return planDeliveryDate;
    }

    public void setPlanDeliveryDate(Date planDeliveryDate) {
        this.planDeliveryDate = planDeliveryDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerManageName() {
        return customerManageName;
    }

    public void setCustomerManageName(String customerManageName) {
        this.customerManageName = customerManageName;
    }

    public String getEstimateFreight() {
        return estimateFreight;
    }

    public void setEstimateFreight(String estimateFreight) {
        this.estimateFreight = estimateFreight;
    }

    public String getFreightToBeAudited() {
        return freightToBeAudited;
    }

    public void setFreightToBeAudited(String freightToBeAudited) {
        this.freightToBeAudited = freightToBeAudited;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUpdateFreightAuditStatus() {
        return UpdateFreightAuditStatus;
    }

    public void setUpdateFreightAuditStatus(String updateFreightAuditStatus) {
        UpdateFreightAuditStatus = updateFreightAuditStatus;
    }
}
