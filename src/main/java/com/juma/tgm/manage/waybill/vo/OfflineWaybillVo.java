package com.juma.tgm.manage.waybill.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName OfflineWaybillVo.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年2月10日 上午10:10:02
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class OfflineWaybillVo implements Serializable {

    private static final long serialVersionUID = 8832112757143298174L;
    private List<Integer> offlineWaybillIds;
    private String departmentCode;

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public List<Integer> getOfflineWaybillIds() {
        return offlineWaybillIds;
    }

    public void setOfflineWaybillIds(List<Integer> offlineWaybillIds) {
        this.offlineWaybillIds = offlineWaybillIds;
    }

}
