package com.juma.tgm.manage.truck.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class FreightVo implements Serializable{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8725772986882613218L;
    /** 运费ID */
    private Integer freightId;
    /** 车型ID */
    private Integer truckTypeId;
    /** 时间成本 */
    private BigDecimal pricePerDay;
    /** 司机搬运*/
    private BigDecimal driverHandlingCost;
    /** 小工搬运*/
    private BigDecimal laborerHandlingCost;
    /** 运费起价 */
    private BigDecimal lowestFreight;
    /** 起价公里数 */
    private Integer lowestMileage;
    /** 超出每公里单价 */
    private BigDecimal beyondUnitPrice;
    /** 封顶运费 */
    private BigDecimal highestFreight;
    /** 配送点价格 */
    private BigDecimal distributionPointPrice;
	/** 备注 */
    private String note;
    /** 附加功能集合 */
    private String[] functions;
    /** 添加来源 */
    private String addWay;
    
    /** 冷链溢价比例 */
    private BigDecimal coldChainFreight;
    /** 代收货款费 */
    private BigDecimal collectionPaymentFreight;
    /** 返仓费 */
    private BigDecimal backStorageFreight;
    /** 回单费 */
    private BigDecimal receiptFreight;
    /** 搬运费 */
    private BigDecimal carryFreight;
    /** 入城证费 */
    private BigDecimal entryLicenseFreight;
    private Integer cityManageId;

    public Integer getFreightId() {
        return freightId;
    }

    public void setFreightId(Integer freightId) {
        this.freightId = freightId;
    }

    public Integer getTruckTypeId() {
        return truckTypeId;
    }

    public void setTruckTypeId(Integer truckTypeId) {
        this.truckTypeId = truckTypeId;
    }

    public BigDecimal getLowestFreight() {
        return lowestFreight;
    }

    public void setLowestFreight(BigDecimal lowestFreight) {
        this.lowestFreight = lowestFreight;
    }

    public Integer getLowestMileage() {
        return lowestMileage;
    }

    public void setLowestMileage(Integer lowestMileage) {
        this.lowestMileage = lowestMileage;
    }

    public BigDecimal getBeyondUnitPrice() {
        return beyondUnitPrice;
    }

    public void setBeyondUnitPrice(BigDecimal beyondUnitPrice) {
        this.beyondUnitPrice = beyondUnitPrice;
    }

    public BigDecimal getHighestFreight() {
        return highestFreight;
    }

    public void setHighestFreight(BigDecimal highestFreight) {
        this.highestFreight = highestFreight;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String[] getFunctions() {
        return functions;
    }

    public void setFunctions(String[] functions) {
        this.functions = functions;
    }

    public String getAddWay() {
        return addWay;
    }

    public void setAddWay(String addWay) {
        this.addWay = addWay;
    }
    public BigDecimal getColdChainFreight() {
        return coldChainFreight;
    }

    public void setColdChainFreight(BigDecimal coldChainFreight) {
        this.coldChainFreight = coldChainFreight;
    }

    public BigDecimal getCollectionPaymentFreight() {
        return collectionPaymentFreight;
    }

    public void setCollectionPaymentFreight(BigDecimal collectionPaymentFreight) {
        this.collectionPaymentFreight = collectionPaymentFreight;
    }

    public BigDecimal getBackStorageFreight() {
        return backStorageFreight;
    }

    public void setBackStorageFreight(BigDecimal backStorageFreight) {
        this.backStorageFreight = backStorageFreight;
    }

    public BigDecimal getReceiptFreight() {
        return receiptFreight;
    }

    public void setReceiptFreight(BigDecimal receiptFreight) {
        this.receiptFreight = receiptFreight;
    }

    public BigDecimal getCarryFreight() {
        return carryFreight;
    }

    public void setCarryFreight(BigDecimal carryFreight) {
        this.carryFreight = carryFreight;
    }

    public BigDecimal getEntryLicenseFreight() {
        return entryLicenseFreight;
    }

    public void setEntryLicenseFreight(BigDecimal entryLicenseFreight) {
        this.entryLicenseFreight = entryLicenseFreight;
    }
    public BigDecimal getDistributionPointPrice() {
  		return distributionPointPrice;
  	}

  	public void setDistributionPointPrice(BigDecimal distributionPointPrice) {
  		this.distributionPointPrice = distributionPointPrice;
  	}

    public Integer getCityManageId() {
        return cityManageId;
    }

    public void setCityManageId(Integer cityManageId) {
        this.cityManageId = cityManageId;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public BigDecimal getDriverHandlingCost() {
        return driverHandlingCost;
    }

    public void setDriverHandlingCost(BigDecimal driverHandlingCost) {
        this.driverHandlingCost = driverHandlingCost;
    }

    public BigDecimal getLaborerHandlingCost() {
        return laborerHandlingCost;
    }

    public void setLaborerHandlingCost(BigDecimal laborerHandlingCost) {
        this.laborerHandlingCost = laborerHandlingCost;
    }

}
