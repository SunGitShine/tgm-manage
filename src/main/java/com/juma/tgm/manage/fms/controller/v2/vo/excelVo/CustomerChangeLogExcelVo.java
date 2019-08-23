package com.juma.tgm.manage.fms.controller.v2.vo.excelVo;

import me.about.poi.ExcelColumn;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: CustomerChangeLogExcelVo
 * @Description:
 * @author: liang
 * @date: 2018-06-10 21:09
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
public class CustomerChangeLogExcelVo implements Serializable {


    @ExcelColumn(name = "含税金额（调整额）")
    private BigDecimal beforeTaxFreight;

    @ExcelColumn(name = "不含税金额（调整额）")
    private BigDecimal afterTaxFreight;

    @ExcelColumn(name = "调整时间")
    private String createTime;

    @ExcelColumn(name = "调整原因")
    private String note;


    public BigDecimal getBeforeTaxFreight() {
        return beforeTaxFreight;
    }

    public void setBeforeTaxFreight(BigDecimal beforeTaxFreight) {
        this.beforeTaxFreight = beforeTaxFreight;
    }

    public BigDecimal getAfterTaxFreight() {
        return afterTaxFreight;
    }

    public void setAfterTaxFreight(BigDecimal afterTaxFreight) {
        this.afterTaxFreight = afterTaxFreight;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
