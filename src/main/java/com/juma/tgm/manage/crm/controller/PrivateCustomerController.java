package com.juma.tgm.manage.crm.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.crm.domain.PrivateCustomer;
import com.juma.tgm.crm.service.PrivateCustomerService;
import com.juma.tgm.manage.web.controller.BaseController;

/**
 * 
 * @ClassName:   PrivateCustomerController   
 * @Description: 个人货主
 * @author:      Administrator
 * @date:        2017年3月13日 下午5:53:17  
 *
 * @Copyright:   2017 www.jumapeisong.com Inc. All rights reserved.
 */
@Controller
@RequestMapping(value = "privateCustomer")
public class PrivateCustomerController extends BaseController {
    
    @Resource
    private PrivateCustomerService privateCustomerService;

    /**
     * 
     * @Title: search
     * @Description: 分页列表
     * @return Page<TruckCustomerBo>
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<PrivateCustomer> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        return privateCustomerService.searchDetails(pageCondition);
    }

    /**
     * 
     * @Title: update
     * @Description: 编辑
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestBody PrivateCustomer privateCustomer, LoginEmployee loginEmployee) throws Exception {
        privateCustomerService.updatePrivateCustomer(privateCustomer);
    }

}
