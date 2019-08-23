package com.juma.oms.manage.order.vo;

import java.io.Serializable;

/**
 * @ClassName CancelParam.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年4月19日 下午4:57:05
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class CancelParamVO implements Serializable {

    private static final long serialVersionUID = 94551613677870492L;
    private Integer orderId;
    private String cancelNote;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getCancelNote() {
        return cancelNote;
    }

    public void setCancelNote(String cancelNote) {
        this.cancelNote = cancelNote;
    }

}
