package com.juma.tgm.manage.fms.controller.v3.excelVo;

import me.about.poi.ExcelColumn;

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
