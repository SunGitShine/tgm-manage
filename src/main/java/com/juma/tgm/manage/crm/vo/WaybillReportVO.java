package com.juma.tgm.manage.crm.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName WaybillReportVO.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年2月10日 下午4:15:13
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class WaybillReportVO implements Serializable {

    private static final long serialVersionUID = -8212170512716362705L;
    private List<Integer> batchSelect = new ArrayList<Integer>();

    public List<Integer> getBatchSelect() {
        return batchSelect;
    }

    public void setBatchSelect(List<Integer> batchSelect) {
        this.batchSelect = batchSelect;
    }

}
