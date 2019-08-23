package com.juma.tgm.manage.waybillAccount.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName WaybillReconciliationVO.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年7月26日 上午9:58:50
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Deprecated
public class WaybillReconciliationVO implements Serializable {

    private static final long serialVersionUID = -5011965460597604726L;
    private List<Integer> waybillIdList = new ArrayList<Integer>();

    public List<Integer> getWaybillIdList() {
        return waybillIdList;
    }

    public void setWaybillIdList(List<Integer> waybillIdList) {
        this.waybillIdList = waybillIdList;
    }

}
