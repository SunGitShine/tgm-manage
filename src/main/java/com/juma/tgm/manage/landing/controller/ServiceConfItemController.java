package com.juma.tgm.manage.landing.controller;

import java.util.List;

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
import com.juma.tgm.configure.domain.ServiceConfItem;
import com.juma.tgm.configure.service.ServiceConfItemService;

/**
 * @ClassName ServiceConfController.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年11月28日 下午2:17:13
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("serviceConfItem")
public class ServiceConfItemController {

    @Resource
    private ServiceConfItemService serviceConfItemService;

    /**
     * 分页列表
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<ServiceConfItem> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        return serviceConfItemService.search(pageCondition, loginEmployee);
    }

    /**
     * 查询
     */
    @ResponseBody
    @RequestMapping(value = "listFenceId", method = RequestMethod.POST)
    public String listFenceId(@RequestBody ServiceConfItem serviceConfItem, LoginEmployee loginEmployee) {
        StringBuffer sf = new StringBuffer();
        List<ServiceConfItem> list = serviceConfItemService.listByServiceConf(serviceConfItem.getServiceConfId(),
                serviceConfItem.getFenceType());

        if (list.isEmpty()) {
            return null;
        }

        for (ServiceConfItem item : list) {
            if (item.getFenceId().equals(serviceConfItem.getFenceId())) {
                continue;
            }
            sf.append(item.getFenceId()).append(",");
        }

        String ids = sf.toString();

        if (ids.endsWith(",")) {
            return ids.substring(0, ids.length() - 1);
        }

        return ids;
    }

    /**
     * 删除
     */
    @ResponseBody
    @RequestMapping(value = "{serviceConfItemId}/delete", method = RequestMethod.GET)
    public void delete(@PathVariable Integer serviceConfItemId, LoginEmployee loginEmployee) {
        serviceConfItemService.delete(serviceConfItemId, loginEmployee);
    }
}
