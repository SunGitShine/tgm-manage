package com.juma.tgm.manage.waybill.vo;

/**
 * @ClassName ValuationDetail.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年10月22日 上午10:35:00
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class ValuationDetailVo {

    private String valuationConstView;
    private String ValuationWayView;
    private Integer ValuationWay;

    public String getValuationConstView() {
        return valuationConstView;
    }

    public void setValuationConstView(String valuationConstView) {
        this.valuationConstView = valuationConstView;
    }

    public String getValuationWayView() {
        return ValuationWayView;
    }

    public void setValuationWayView(String valuationWayView) {
        ValuationWayView = valuationWayView;
    }

    public Integer getValuationWay() {
        return ValuationWay;
    }

    public void setValuationWay(Integer valuationWay) {
        ValuationWay = valuationWay;
    }

}
