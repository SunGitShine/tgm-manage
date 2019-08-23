package com.juma.tgm.manage.operateLog.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.operateLog.service.OperateLogService;
import com.juma.tgm.operateLog.vo.OperateLogQuery;

/**
 * @ClassName OperateLogController.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年11月21日 下午5:08:18
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("operateLog")
public class OperateLogController {

    @Resource
    private OperateLogService operateLogService;

    /**
     * 操作记录-分页
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<OperateLogQuery> operateLog(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) {
        // TODO 这是一个临时方案，公共接口不应出现此段代码，下个迭代更改（2019-01-14注）
        pageCondition.getFilters().put("relationTableId", pageCondition.getFilters().get("projectId"));

        return operateLogService.search(pageCondition,loginEmployee);
    }
}
