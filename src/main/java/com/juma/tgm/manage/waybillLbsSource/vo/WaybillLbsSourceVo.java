package com.juma.tgm.manage.waybillLbsSource.vo;

import com.juma.tgm.driver.domain.ReportInfoDetails;

import java.util.Date;
import java.util.List;

/**
 * Created by shawn_lin on 2017/6/21.
 */
public class WaybillLbsSourceVo {

    /** 报备集合 */
    private List<ReportInfoDetails> detailList;
    /** 运单编号 */
    private String waybillNo;
    /** 所属客户 */
    private String customerName;
    /** 客户经理 */
    private String customerManagerName;
    /** 司机姓名 */
    private String driverName;
    /** 用车时间 */
    private Date planDeliveryTime;
    /** 到仓时间 */
    private Date arriveDepotTime;
    /** 迟到时间 */
    private String timeConsuming;
    /** 取货地址 */
    private String deliveryAddress;
    /** 签到地址 */
    private String signInfoAddress;
    /** 迟到距离 */
    private Integer distance;
    /** 电子围栏到仓时间 */
    private Date fenceArriveDepotTime;

    public Date getFenceArriveDepotTime() {
        return fenceArriveDepotTime;
    }

    public void setFenceArriveDepotTime(Date fenceArriveDepotTime) {
        this.fenceArriveDepotTime = fenceArriveDepotTime;
    }

    public List<ReportInfoDetails> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<ReportInfoDetails> detailList) {
        this.detailList = detailList;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerManagerName() {
        return customerManagerName;
    }

    public void setCustomerManagerName(String customerManagerName) {
        this.customerManagerName = customerManagerName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Date getPlanDeliveryTime() {
        return planDeliveryTime;
    }

    public void setPlanDeliveryTime(Date planDeliveryTime) {
        this.planDeliveryTime = planDeliveryTime;
    }

    public Date getArriveDepotTime() {
        return arriveDepotTime;
    }

    public void setArriveDepotTime(Date arriveDepotTime) {
        this.arriveDepotTime = arriveDepotTime;
    }

    public String getTimeConsuming() {
        return timeConsuming;
    }

    public void setTimeConsuming(String timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getSignInfoAddress() {
        return signInfoAddress;
    }

    public void setSignInfoAddress(String signInfoAddress) {
        this.signInfoAddress = signInfoAddress;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}
