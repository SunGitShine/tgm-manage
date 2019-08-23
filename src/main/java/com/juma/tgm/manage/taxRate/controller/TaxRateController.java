package com.juma.tgm.manage.taxRate.controller;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.waybill.domain.TaxRate;
import com.juma.tgm.waybill.service.TaxRateService;

/**
 * @ClassName TaxRateController.java
 * @Description 税率
 * @author Libin.Wei
 * @Date 2018年7月4日 下午6:33:14
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("taxRate")
public class TaxRateController {

    @Resource
    private TaxRateService taxRateService;

    /**
     * 分页
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<TaxRate> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        return taxRateService.search(pageCondition, loginEmployee);
    }

    /**
     * 编辑
     */
    @ResponseBody
    @RequestMapping(value = "modify", method = RequestMethod.POST)
    public void modify(@RequestBody TaxRate taxRate, LoginEmployee loginEmployee) {
        if (StringUtils.isNotBlank(taxRate.getTaxRateValueText())) {
            taxRate.setTaxRateValue(new BigDecimal(taxRate.getTaxRateValueText()).divide(new BigDecimal("100")));
        }

        if (null == taxRate.getTaxRateId()) {
            taxRateService.insert(taxRate, loginEmployee);
            return;
        }

        taxRateService.update(taxRate, loginEmployee);
    }

    /**
     * 禁用
     */
    @ResponseBody
    @RequestMapping(value = "{taxRateId}/disable", method = RequestMethod.POST)
    public void disable(@PathVariable Integer taxRateId, LoginEmployee loginEmployee) {
        taxRateService.updateToDisable(taxRateId, loginEmployee);
    }

    /**
     * 启用
     */
    @ResponseBody
    @RequestMapping(value = "{taxRateId}/enable", method = RequestMethod.POST)
    public void enable(@PathVariable Integer taxRateId, LoginEmployee loginEmployee) {
        taxRateService.updateToEnable(taxRateId, loginEmployee);
    }
}
