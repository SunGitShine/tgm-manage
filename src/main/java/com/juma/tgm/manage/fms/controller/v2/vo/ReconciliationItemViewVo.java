package com.juma.tgm.manage.fms.controller.v2.vo;

import com.juma.tgm.fms.domain.ReconciliationItem;

import java.io.Serializable;

/**
 * @ClassName: ReconciliationItemViewVo
 * @Description:
 * @author: liang
 * @date: 2018-06-15 23:36
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
public class ReconciliationItemViewVo extends ReconciliationItem implements Serializable {

    //对账单号
    private String reconciliationNo;


    public String getReconciliationNo() {
        return reconciliationNo;
    }

    public void setReconciliationNo(String reconciliationNo) {
        this.reconciliationNo = reconciliationNo;
    }
}
