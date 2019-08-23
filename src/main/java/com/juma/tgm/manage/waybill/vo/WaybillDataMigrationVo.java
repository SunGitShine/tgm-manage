package com.juma.tgm.manage.waybill.vo;

import java.util.List;

/**
 * Created by shawn_lin on 2017/8/10.
 */
public class WaybillDataMigrationVo {
    private List<Integer> waybillIds;

    private String areaCode;

    private Integer customerManagerId;

    public Integer getCustomerManagerId() {
        return customerManagerId;
    }

    public void setCustomerManagerId(Integer customerManagerId) {
        this.customerManagerId = customerManagerId;
    }

    public List<Integer> getWaybillIds() {
        return waybillIds;
    }

    public void setWaybillIds(List<Integer> waybillIds) {
        this.waybillIds = waybillIds;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
}
