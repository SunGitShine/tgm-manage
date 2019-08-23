package com.juma.tgm.manage.fms.controller.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ReconciliationVo.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年3月8日 下午5:22:41
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class ReconciliationVo implements Serializable {

    private static final long serialVersionUID = 4396351198585929155L;
    private Integer reconciliationId;
    private List<Integer> listWaybillId = new ArrayList<Integer>();
    private List<String> listImage = new ArrayList<String>();
    private BigDecimal totalFee = BigDecimal.ZERO;

    public Integer getReconciliationId() {
        return reconciliationId;
    }

    public void setReconciliationId(Integer reconciliationId) {
        this.reconciliationId = reconciliationId;
    }

    public List<Integer> getListWaybillId() {
        return listWaybillId;
    }

    public void setListWaybillId(List<Integer> listWaybillId) {
        this.listWaybillId = listWaybillId;
    }

    public List<String> getListImage() {
        return listImage;
    }

    public void setListImage(List<String> listImage) {
        this.listImage = listImage;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    @Override
    public String toString() {
        return "ReconciliationVo [reconciliationId=" + reconciliationId + ", listWaybillId=" + listWaybillId
                + ", listImage=" + listImage + "]";
    }

}
