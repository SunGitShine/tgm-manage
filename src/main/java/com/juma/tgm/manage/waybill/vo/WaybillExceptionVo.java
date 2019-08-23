package com.juma.tgm.manage.waybill.vo;

import java.io.Serializable;

public class WaybillExceptionVo implements Serializable{

    private static final long serialVersionUID = 1707238744063584083L;
    /** 异常ID */
    private Integer exceptionId;
    /** 运单ID */
    private Integer waybillId;
    /** 司机ID */
    private Integer driverId;
    /** 货车ID */
    private Integer truckId;
    /** 异常原因 */
    private String note;

    public Integer getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(Integer exceptionId) {
        this.exceptionId = exceptionId;
    }

    public Integer getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(Integer waybillId) {
        this.waybillId = waybillId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getTruckId() {
        return truckId;
    }

    public void setTruckId(Integer truckId) {
        this.truckId = truckId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
