package com.juma.tgm.manage.waybill.vo;

import java.io.Serializable;
import java.util.List;

import com.juma.tgm.waybill.domain.DeliveryPointSupplement;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillReceiveAddress;

/**
 * @ClassName DeliveryPointSupplementVO.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年1月3日 下午4:27:57
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class DeliveryPointSupplementVO implements Serializable {

    private static final long serialVersionUID = -3816137234760989102L;
    private String dataFrom;
    private boolean receiveListFlag;
    private Waybill waybill;
    private List<WaybillReceiveAddress> listReceiveAddress;
    private List<DeliveryPointSupplement> listDeliveryPointSupplement;

    public String getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(String dataFrom) {
        this.dataFrom = dataFrom;
    }

    public boolean isReceiveListFlag() {
        return receiveListFlag;
    }

    public void setReceiveListFlag(boolean receiveListFlag) {
        this.receiveListFlag = receiveListFlag;
    }

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }

    public List<WaybillReceiveAddress> getListReceiveAddress() {
        return listReceiveAddress;
    }

    public void setListReceiveAddress(List<WaybillReceiveAddress> listReceiveAddress) {
        this.listReceiveAddress = listReceiveAddress;
    }

    public List<DeliveryPointSupplement> getListDeliveryPointSupplement() {
        return listDeliveryPointSupplement;
    }

    public void setListDeliveryPointSupplement(List<DeliveryPointSupplement> listDeliveryPointSupplement) {
        this.listDeliveryPointSupplement = listDeliveryPointSupplement;
    }

}
