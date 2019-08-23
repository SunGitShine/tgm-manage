package com.juma.tgm.manage.waybill.vo;

import java.io.Serializable;

/**
 * @ClassName WaybillCancelVO.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年1月3日 上午11:19:06
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class WaybillCancelVO implements Serializable {

    private static final long serialVersionUID = 2576683417627430745L;
    private Integer waybillId;
    private String waybillCancelRemark;

    public Integer getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(Integer waybillId) {
        this.waybillId = waybillId;
    }

    public String getWaybillCancelRemark() {
        return waybillCancelRemark;
    }

    public void setWaybillCancelRemark(String waybillCancelRemark) {
        this.waybillCancelRemark = waybillCancelRemark;
    }

}
