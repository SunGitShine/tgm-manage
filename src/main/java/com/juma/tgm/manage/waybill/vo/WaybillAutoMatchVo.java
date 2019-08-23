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

public class WaybillAutoMatchVo implements Serializable {

    private static final long serialVersionUID = 785757811094472428L;
    private List<Integer> waybillIdList;

    public List<Integer> getWaybillIdList() {
        return waybillIdList;
    }

    public void setWaybillIdList(List<Integer> waybillIdList) {
        this.waybillIdList = waybillIdList;
    }

}
