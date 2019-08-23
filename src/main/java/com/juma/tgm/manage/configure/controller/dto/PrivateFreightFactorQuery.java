package com.juma.tgm.manage.configure.controller.dto;

public class PrivateFreightFactorQuery {

    private String regionCode;
    
    private Integer freightWay;
    
    private Integer truckTypeId;
    
    public String getRegionCode() {
        return regionCode;
    }
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
    public Integer getFreightWay() {
        return freightWay;
    }
    public void setFreightWay(Integer freightWay) {
        this.freightWay = freightWay;
    }
    public Integer getTruckTypeId() {
        return truckTypeId;
    }
    public void setTruckTypeId(Integer truckTypeId) {
        this.truckTypeId = truckTypeId;
    }
    
}
