package com.juma.tgm.manage.configure.controller;

import java.util.List;

import javax.annotation.Resource;

import com.juma.auth.employee.domain.LoginEmployee;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.tgm.configure.domain.ConfigParam;
import com.juma.tgm.configure.service.ConfigParamService;

/**
 * @ClassName ConfParamController.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2016年12月29日 下午3:07:08
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("configParam")
public class ConfigParamController {

    @Resource
    private ConfigParamService configParamService;

    /**
     * 分页数据
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<ConfigParam> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        return configParamService.search(pageCondition);
    }

    /**
     * 分页数据
     */
    @Deprecated
    @RequestMapping(value = "{paramId}/edit", method = RequestMethod.GET)
    public ModelAndView edit(@PathVariable Integer paramId) {
        ModelAndView modelAndView = new ModelAndView("pages/configure/dialog/configParamDialog");
        ConfigParam configParam = configParamService.get(paramId);
        modelAndView.addObject("configParam", configParam);
        return modelAndView;
    }


    /**
     * 更改
     */
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody ConfigParam confParam, LoginEmployee loginEmployee) {
        configParamService.update(confParam);
    }
    
    /**
     * 添加
     */
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(@RequestBody ConfigParam confParam, LoginEmployee loginEmployee) {
        configParamService.insert(confParam);
    }

    /**
     * 删除
     */
    @ResponseBody
    @RequestMapping(value = "{paramId}/delete", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer paramId, LoginEmployee loginEmployee) {
        configParamService.delete(new ConfigParam(paramId));
    }

    /**
     * 通知类型列表
     */
    @ResponseBody
    @RequestMapping(value = "list/conf/param", method = RequestMethod.GET)
    public List<ConfigParam> listConfParam(LoginEmployee loginEmployee) {
        return configParamService.listByTenantId(loginEmployee.getTenantId());
    }
}
