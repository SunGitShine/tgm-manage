package com.juma.tgm.manage.waybill.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.juma.tgm.customer.domain.TruckCustomer;
import com.juma.tgm.waybill.domain.TruckRequire;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillDeliveryAddress;
import com.juma.tgm.waybill.domain.WaybillReceiveAddress;

/**
 * @ClassName WaybillPendingVO.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年1月4日 下午3:21:20
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class WaybillPendingVO implements Serializable {

    private static final long serialVersionUID = 5803876981951522000L;
    private Waybill waybill;
    private TruckRequire truckRequire;
    private TruckCustomer truckCustomer;
    private List<WaybillDeliveryAddress> listDeliveryAddress = new ArrayList<WaybillDeliveryAddress>();
    private List<WaybillReceiveAddress> listReceiveAddress = new ArrayList<WaybillReceiveAddress>();
    private Date minDate;

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }

    public TruckRequire getTruckRequire() {
        return truckRequire;
    }

    public void setTruckRequire(TruckRequire truckRequire) {
        this.truckRequire = truckRequire;
    }

    public TruckCustomer getTruckCustomer() {
        return truckCustomer;
    }

    public void setTruckCustomer(TruckCustomer truckCustomer) {
        this.truckCustomer = truckCustomer;
    }

    public List<WaybillDeliveryAddress> getListDeliveryAddress() {
        return listDeliveryAddress;
    }

    public void setListDeliveryAddress(List<WaybillDeliveryAddress> listDeliveryAddress) {
        this.listDeliveryAddress = listDeliveryAddress;
    }

    public List<WaybillReceiveAddress> getListReceiveAddress() {
        return listReceiveAddress;
    }

    public void setListReceiveAddress(List<WaybillReceiveAddress> listReceiveAddress) {
        this.listReceiveAddress = listReceiveAddress;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

}
