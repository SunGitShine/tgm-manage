package com.juma.tgm.manage.reportInfo.vo;

import com.juma.conf.domain.ConfParamOption;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.driver.domain.Driver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Libin.Wei
 * @version 1.0.0
 * @ClassName RoadConditionReportVO.java
 * @Description 路况报备VO
 * @Date 2017年5月4日 上午11:01:06
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class ReportInfoVO implements Serializable {

    private static final long serialVersionUID = 2853550781481606391L;
    private Integer reportInfoId;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 司机姓名
     */
    private String driverName;
    /**
     * 司机电话
     */
    private String driverPhone;
    /**
     * 运单号
     */
    private String waybillNo;
    /**
     * 运单ID
     */
    private Integer waybillId;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 报备类型
     */
    private Integer reportInfoType;
    /**
     * 报备时间
     */
    private Date firstReportTime;
    /**
     * 报备次数
     */
    private Integer ReportInfoCount;
    /**
     * 报备备注
     */
    private String remark;
    /**
     * 是否显示司机车辆详情
     */
    private boolean showDriverAndTruckDetail = true;

    private List<ConfParamOption> reportTypes;

    private CustomerInfo customerInfo;

    private Driver driver;

    public Integer getReportInfoCount() {
        return ReportInfoCount;
    }

    public void setReportInfoCount(Integer reportInfoCount) {
        ReportInfoCount = reportInfoCount;
    }

    public Date getFirstReportTime() {
        return firstReportTime;
    }

    public void setFirstReportTime(Date firstReportTime) {
        this.firstReportTime = firstReportTime;
    }

    public Integer getReportInfoId() {
        return reportInfoId;
    }

    public void setReportInfoId(Integer reportInfoId) {
        this.reportInfoId = reportInfoId;
    }

    public Integer getReportInfoType() {
        return reportInfoType;
    }

    public void setReportInfoType(Integer reportInfoType) {
        this.reportInfoType = reportInfoType;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public Integer getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(Integer waybillId) {
        this.waybillId = waybillId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isShowDriverAndTruckDetail() {
        return showDriverAndTruckDetail;
    }

    public void setShowDriverAndTruckDetail(boolean showDriverAndTruckDetail) {
        this.showDriverAndTruckDetail = showDriverAndTruckDetail;
    }

    public void setReportTypes(List<ConfParamOption> reportTypes) {
        this.reportTypes = reportTypes;
    }

    public String getReportTypeName() {
        if (CollectionUtils.isEmpty(this.reportTypes))
            return null;

        if (this.getReportInfoType() == null)
            return null;

        for (ConfParamOption conf : this.reportTypes) {
            try {
                if (NumberUtils.compare(NumberUtils.createInteger(conf.getOptionValue()),
                        this.getReportInfoType()) == 0) {
                    return conf.getOptionName();
                }
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    public Integer getCustomerId() {
        if (this.customerInfo == null)
            return null;

        return customerInfo.getCustomerId();
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public Integer getDriverId() {
        if (this.driver == null)
            return null;
        return driver.getDriverId();
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
