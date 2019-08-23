package com.juma.tgm.manage.landing.vo;

import java.math.BigDecimal;

import com.juma.tgm.configure.domain.FreightRule;

/**
 * @ClassName FreightRuleVO.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年12月19日 下午12:40:14
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class FreightRuleVO extends FreightRule {

    private BigDecimal baseVolumeDecimal;
    private BigDecimal baseWeightDecimal;
    private BigDecimal maxWeightDecimal;
    private BigDecimal maxVolumeDecimal;

    public BigDecimal getBaseVolumeDecimal() {
        return baseVolumeDecimal;
    }

    public void setBaseVolumeDecimal(BigDecimal baseVolumeDecimal) {
        this.baseVolumeDecimal = baseVolumeDecimal;
    }

    public BigDecimal getBaseWeightDecimal() {
        return baseWeightDecimal;
    }

    public void setBaseWeightDecimal(BigDecimal baseWeightDecimal) {
        this.baseWeightDecimal = baseWeightDecimal;
    }

    public BigDecimal getMaxWeightDecimal() {
        return maxWeightDecimal;
    }

    public void setMaxWeightDecimal(BigDecimal maxWeightDecimal) {
        this.maxWeightDecimal = maxWeightDecimal;
    }

    public BigDecimal getMaxVolumeDecimal() {
        return maxVolumeDecimal;
    }

    public void setMaxVolumeDecimal(BigDecimal maxVolumeDecimal) {
        this.maxVolumeDecimal = maxVolumeDecimal;
    }

}
