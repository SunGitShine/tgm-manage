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

public class WaybillAssigneV2Vo implements Serializable {

    private static final long serialVersionUID = -5842857703438360937L;
    // 运单ID
    private Integer waybillId;
    // AMS系统车辆ID
    private Integer vehicleId;
    // AMS系统司机ID
    private Integer amsDriverId;
    // AMS系统班次ID
    private Integer flightId;
    // 派车备注
    private String remark;

    public Integer getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(Integer waybillId) {
        this.waybillId = waybillId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getAmsDriverId() {
        return amsDriverId;
    }

    public void setAmsDriverId(Integer amsDriverId) {
        this.amsDriverId = amsDriverId;
    }

    public Integer getFlightId() {
        return flightId;
    }

    public void setFlightId(Integer flightId) {
        this.flightId = flightId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
