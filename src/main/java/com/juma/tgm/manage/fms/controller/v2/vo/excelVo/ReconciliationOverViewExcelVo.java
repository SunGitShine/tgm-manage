package com.juma.tgm.manage.fms.controller.v2.vo.excelVo;

import me.about.poi.ExcelColumn;

/**
 * excel vo
 * @ClassName: ReconciliationOverView
 * @Description:
 * @author: liang
 * @date: 2018-06-10 20:09
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
public class ReconciliationOverViewExcelVo {

    @ExcelColumn(name = " ")
    private String overViewString;

    public String getOverViewString() {
        return overViewString;
    }

    public void setOverViewString(String overViewString) {
        this.overViewString = overViewString;
    }
}
