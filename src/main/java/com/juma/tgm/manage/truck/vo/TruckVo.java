package com.juma.tgm.manage.truck.vo;

import java.io.Serializable;

public class TruckVo implements Serializable {

    private static final long serialVersionUID = -4802623097556415160L;
    /** 货车ID */
    private Integer truckId;
    /** 货车ID */
    private Integer vehicleId;
    /** 车牌号 */
    private String plateNumber;
    /** 品牌 */
    private String brandName;
    /** 车型名称 */
    private String truckTypeName;
    /** 入城证 */
    private String entryLicenseName;
    /** 载重 */
    private Integer maxLoadCapacity;
    /** 车辆停放区域 */
    private String regionName;
    /** 司机姓名 */
    private String driverName;
    /** 车队名称 */
    private String truckFleetName;
    /** 停运计划 */
    private Integer stopWorkerCount;
    /** 状态 */
    private String statusName;

    public Integer getTruckId() {
        return truckId;
    }

    public void setTruckId(Integer truckId) {
        this.truckId = truckId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getTruckTypeName() {
        return truckTypeName;
    }

    public void setTruckTypeName(String truckTypeName) {
        this.truckTypeName = truckTypeName;
    }

    public String getEntryLicenseName() {
        return entryLicenseName;
    }

    public void setEntryLicenseName(String entryLicenseName) {
        this.entryLicenseName = entryLicenseName;
    }

    public Integer getMaxLoadCapacity() {
        return maxLoadCapacity;
    }

    public void setMaxLoadCapacity(Integer maxLoadCapacity) {
        this.maxLoadCapacity = maxLoadCapacity;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getTruckFleetName() {
        return truckFleetName;
    }

    public void setTruckFleetName(String truckFleetName) {
        this.truckFleetName = truckFleetName;
    }

    public Integer getStopWorkerCount() {
        return stopWorkerCount;
    }

    public void setStopWorkerCount(Integer stopWorkerCount) {
        this.stopWorkerCount = stopWorkerCount;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return "TruckVo [truckId=" + truckId + ", vehicleId=" + vehicleId + ", plateNumber=" + plateNumber
                + ", brandName=" + brandName + ", truckTypeName=" + truckTypeName + ", entryLicenseName="
                + entryLicenseName + ", maxLoadCapacity=" + maxLoadCapacity + ", regionName=" + regionName
                + ", driverName=" + driverName + ", truckFleetName=" + truckFleetName + ", stopWorkerCount="
                + stopWorkerCount + ", statusName=" + statusName + "]";
    }

}
