package com.juma.tgm.manage.landing.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.configure.domain.PackFreightRule;
import com.juma.tgm.configure.service.PackFreightRuleService;

/**
 * @author Libin.Wei
 * @version 1.0.0
 * @ClassName LandingDistributionFreightController.java
 * @Description 整车计价规则
 * @Date 2017年11月16日 下午4:29:36
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Deprecated
@Controller
@RequestMapping("pack/freight/rule")
public class PackFreightRuleController {

    @Resource
    private PackFreightRuleService packFreightRuleService;

    /**
     * 分页查询
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<PackFreightRule> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        return packFreightRuleService.search(pageCondition, loginEmployee);
    }

    /**
     * 添加
     */
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(@RequestBody PackFreightRule packFreightRule, LoginEmployee loginEmployee) {
        packFreightRuleService.insert(packFreightRule, loginEmployee);
    }

    /**
     * 编辑
     */
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody PackFreightRule packFreightRule, LoginEmployee loginEmployee) {
        packFreightRuleService.update(packFreightRule, loginEmployee);
    }

    /**
     * 启用
     */
    @ResponseBody
    @RequestMapping(value = "{packFreightRuleId}/enable", method = RequestMethod.GET)
    public void enable(@PathVariable Integer packFreightRuleId, LoginEmployee loginEmployee) {
        packFreightRuleService.updateToEnable(packFreightRuleId, loginEmployee);
    }

    /**
     * 禁用
     */
    @ResponseBody
    @RequestMapping(value = "{packFreightRuleId}/disable", method = RequestMethod.GET)
    public void disable(@PathVariable Integer packFreightRuleId, LoginEmployee loginEmployee) {
        packFreightRuleService.updateToDisable(packFreightRuleId, loginEmployee);
    }

    /**
     * 获取详情
     * @param packFreightRuleId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "{packFreightRuleId}/detail", method = RequestMethod.GET)
    public PackFreightRule getDetail(@PathVariable Integer packFreightRuleId) {
        return packFreightRuleService.getPackFreightRule(packFreightRuleId);
    }

}
