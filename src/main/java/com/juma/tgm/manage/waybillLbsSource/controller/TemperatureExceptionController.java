package com.juma.tgm.manage.waybillLbsSource.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.waybillLbsSource.domain.TemperatureException;
import com.juma.tgm.waybillLbsSource.service.TemperatureExceptionService;

/**
 * @ClassName TemperatureExceptionController.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年5月22日 下午4:44:11
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("temperature/exception")
public class TemperatureExceptionController extends BaseController {

    @Resource
    private TemperatureExceptionService temperatureExceptionService;
    
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<TemperatureException> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        this.formatAreaCodeToList(pageCondition, true);
        return temperatureExceptionService.search(pageCondition, loginEmployee);
    }
}
