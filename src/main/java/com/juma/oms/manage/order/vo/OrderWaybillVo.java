package com.juma.oms.manage.order.vo;

import com.juma.tgm.waybill.domain.Waybill;

/**
 * @ClassName OrderWaybillVo.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年4月27日 下午5:33:08
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class OrderWaybillVo {

    private Waybill waybill;
    private String driverPhone;

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

}
