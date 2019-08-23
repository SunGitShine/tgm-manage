package com.juma.tgm.manage.fms.controller.v3;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.fms.domain.v3.bo.AdjustForPayableQuery;
import com.juma.tgm.fms.service.v3.AdjustForPayableService;

/**
 * @ClassName AdjustForPayableController.java
 * @Description 应付调价记录
 * @author Libin.Wei
 * @Date 2018年11月23日 上午10:24:27
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("adjustForPayable")
public class AdjustForPayableController {

    @Resource
    private AdjustForPayableService adjustForPayableService;

    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<AdjustForPayableQuery> search(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) {
        return adjustForPayableService.search(pageCondition, loginEmployee);
    }
}
