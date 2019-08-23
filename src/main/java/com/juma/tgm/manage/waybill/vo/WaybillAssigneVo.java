package com.juma.tgm.manage.waybill.vo;

import java.io.Serializable;

/**
 * @ClassName WaybillAssigneVo.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年5月23日 下午6:58:53
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class WaybillAssigneVo implements Serializable {

    private static final long serialVersionUID = -5842857703438360937L;
    // 运单ID
    private Integer waybillId;
    // 车辆ID
    private Integer truckId;
    // 司机ID
    private Integer driverId;
    // 承运商ID
    private Integer vendorId;
    // 运力ID
    private Integer capacityPoolId;
    // 派车备注
    private String remark;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(Integer waybillId) {
        this.waybillId = waybillId;
    }

    public Integer getTruckId() {
        return truckId;
    }

    public void setTruckId(Integer truckId) {
        this.truckId = truckId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public Integer getCapacityPoolId() {
        return capacityPoolId;
    }

    public void setCapacityPoolId(Integer capacityPoolId) {
        this.capacityPoolId = capacityPoolId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
