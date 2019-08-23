package com.juma.tgm.manage.web.vo;

/**
 * @ClassName VendorRequest.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年11月30日 上午11:55:28
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class VendorRequest {

    private String vendorName;
    private String areaCode;
    private Integer backPageSize = 15;

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Integer getBackPageSize() {
        return backPageSize;
    }

    public void setBackPageSize(Integer backPageSize) {
        this.backPageSize = backPageSize;
    }

}
