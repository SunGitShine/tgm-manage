package com.juma.tgm.manage.customer.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.EmployeeInfo;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.EmployeeService;
import com.juma.tgm.common.Constants;
import com.juma.tgm.customer.domain.TruckCustomer;
import com.juma.tgm.customer.domain.TruckCustomerBo;
import com.juma.tgm.export.domain.ExportParam;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.user.domain.CurrentUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Libin.Wei
 * @version 1.0.0
 * @ClassName TruckCustomerController.java
 * @Description 客户经理管理
 * @Date 2017年3月14日 上午9:58:58
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Deprecated
@Controller
@RequestMapping(value = "truckCustomer")
public class TruckCustomerController extends BaseController {

    @Resource
    private EmployeeService employeeService;

    /**
     * 客户经理select
     */
    @ResponseBody
    @RequestMapping(value = "manager/search", method = RequestMethod.POST)
    public List<EmployeeInfo> searchCM(PageCondition pageCondition, LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, false);
        Map<String, Object> filters = pageCondition.getFilters();
        if (filters.get("areaCodeList") == null) return Collections.emptyList();
         List<String> areaCodes = (List<String>) pageCondition.getFilters().get("areaCodeList");
        if (CollectionUtils.isNotEmpty(areaCodes)) {
            return employeeService.findEmployeeInfos(areaCodes.toArray(new String[] {}), Constants.AUTH_KEY_TGM_MANAGE, Constants.CUSTOMER_MANAGER_PERMISSION_KEY,
                    loginEmployee);
        }
        return Collections.emptyList();
    }

    /**
     * 客户经理分页列表
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<TruckCustomerBo> search(PageCondition pageCondition, LoginEmployee loginEmployee, @ModelAttribute("currentUser") CurrentUser currentUser) {
        return null;
    }

    /**
     * 货主姓名列表
     */
    @ResponseBody
    @RequestMapping(value = "private/showNames",method = RequestMethod.POST)
    public Page<TruckCustomer> privateShowNames(LoginEmployee loginEmployee){
        return new Page<>(1, 15, 0, new ArrayList<TruckCustomer>());
    }

    /**
     * 货主帐号分页列表
     */
    @ResponseBody
    @RequestMapping(value = "private/search", method = RequestMethod.POST)
    public Page<TruckCustomer> privateSearch(PageCondition pageCondition, LoginEmployee loginEmployee, @ModelAttribute("currentUser") CurrentUser currentUser) {
        return new Page<>(pageCondition.getPageNo(), pageCondition.getPageSize(), 0, new ArrayList<TruckCustomer>());
    }

    /**
     * 帐号管理 跳转到编辑页面
     */
    @ResponseBody
    @RequestMapping(value = "private/{truckCustomerId}/json/edit", method = RequestMethod.GET)
    public TruckCustomer privateJsonEdit(@PathVariable Integer truckCustomerId) {
        return null;
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestBody TruckCustomer truckCustomer, LoginEmployee loginEmployee) throws Exception {
    }

    /**
     * 货主帐号编辑
     */
    @RequestMapping(value = "private/update", method = RequestMethod.POST)
    @ResponseBody
    public void privateUpdate(@RequestBody TruckCustomer truckCustomer, LoginEmployee loginEmployee) throws Exception {
    }

    /**
     * 新增
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public void create(@RequestBody TruckCustomer truckCustomer, LoginEmployee loginEmployee) throws Exception {
    }

    /**
     * excel导出（账户信息）
     */
    @ResponseBody
    @RequestMapping(value = "private/export", method = RequestMethod.POST)
    public void export(@RequestBody ExportParam exportParam, LoginEmployee loginEmployee) {
    }
}
