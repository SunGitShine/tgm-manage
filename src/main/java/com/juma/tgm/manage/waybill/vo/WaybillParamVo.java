package com.juma.tgm.manage.waybill.vo;

import java.io.Serializable;

/**
 * @ClassName WaybillParamVo.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年6月7日 上午9:38:56
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class WaybillParamVo implements Serializable {

    private static final long serialVersionUID = -7962443014860997343L;
    private Integer waybillId;
    private Integer vehicleId;
    private Integer amsDriverId;
    private Integer flightId;

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

    @Override
    public String toString() {
        return "WaybillParamVo [waybillId=" + waybillId + ", vehicleId=" + vehicleId + ", amsDriverId=" + amsDriverId
                + ", flightId=" + flightId + "]";
    }

}
