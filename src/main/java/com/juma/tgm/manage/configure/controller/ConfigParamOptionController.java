package com.juma.tgm.manage.configure.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.configure.domain.ConfigParamOption;
import com.juma.tgm.configure.service.ConfigParamOptionService;
import com.juma.tgm.manage.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName ConfParamController.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2016年12月29日 下午3:07:08
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("configParamOption")
public class ConfigParamOptionController extends BaseController {

    @Resource
    private ConfigParamOptionService configParamOptionService;

    /**
     * 分页数据
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<ConfigParamOption> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        return configParamOptionService.search(pageCondition, loginEmployee);
    }

    /**
     * 添加或修改
     */
    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public void save(@RequestBody ConfigParamOption confParamOption, LoginEmployee loginEmployee) {
        if(confParamOption.getOptionId() == null) {
            configParamOptionService.insert(confParamOption, loginEmployee);
        } else {
            configParamOptionService.update(confParamOption, loginEmployee);
        }
        
    }
    
    /**
     * 启用
     */
    @ResponseBody
    @RequestMapping(value = "{optionId}/enable", method = RequestMethod.POST)
    public void enable(@PathVariable Integer optionId, LoginEmployee loginEmployee) {
        configParamOptionService.enable(optionId, loginEmployee);
    }

    /**
     * 禁用
     */
    @ResponseBody
    @RequestMapping(value = "{optionId}/disable", method = RequestMethod.POST)
    public void disable(@PathVariable Integer optionId, LoginEmployee loginEmployee) {
        configParamOptionService.disable(optionId, loginEmployee);
    }

    /**
     * 添加：已过期
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(@RequestBody ConfigParamOption confParamOption, LoginEmployee loginEmployee) {
        configParamOptionService.insert(confParamOption, loginEmployee);
    }

    /**
     * 更改：已过期
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody ConfigParamOption confParamOption, LoginEmployee loginEmployee) {
        configParamOptionService.update(confParamOption, loginEmployee);
    }
}
