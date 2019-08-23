package com.juma.tgm.manage.waybillLbsSource.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.cms.domain.ExportTask;
import com.juma.tgm.cms.service.ExportTaskService;
import com.juma.tgm.export.domain.ExportParam;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.waybillLbsSource.domain.ActualMileageQuery;
import com.juma.tgm.waybillLbsSource.service.ActualMileageService;

/**
 * @ClassName ActualMileageController.java
 * @Description 实际里程异常
 * @author Libin.Wei
 * @Date 2017年6月21日 下午4:19:53
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("actual/mileage")
public class ActualMileageController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ActualMileageController.class);
    @Resource
    private ActualMileageService actualMileageService;
    @Resource
    private ExportTaskService exportTaskService;

    /**
     * 分页列表
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<ActualMileageQuery> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, true);
        return actualMileageService.search(pageCondition, loginEmployee);
    }

    /**
     * excel导出
     */
    @ResponseBody
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public void export(@RequestBody ExportParam exportParam, LoginEmployee loginEmployee) {
        // 初始化任务
        Integer exportTaskId = exportTaskService.insertInit(ExportTask.TaskSign.ACTUAL_MILEAGE_EXCEPTION, exportParam,
                loginEmployee);
        try {
            // 获取数据并上传云
            PageCondition pageCondition = new PageCondition();
            pageCondition.setPageNo(1);
            pageCondition.setPageSize(Integer.MAX_VALUE);
            pageCondition.setFilters(exportParam.getFilters());
            super.formatAreaCodeToList(pageCondition, true);
            actualMileageService.asyncExport(pageCondition, exportTaskId, loginEmployee);
        } catch (Exception e) {
            exportTaskService.failed(exportTaskId, e.getMessage(), loginEmployee);
            log.error(e.getMessage(), e);
        }
    }
}
