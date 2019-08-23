package com.juma.tgm.manage.exportTask.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.juma.tgm.cms.domain.ExportTask;
import com.juma.tgm.cms.service.ExportTaskService;
import com.juma.tgm.export.domain.ExportCollection;
import com.juma.tgm.export.domain.ExportParam;

/**
 * @author Libin.Wei
 * @version 1.0.0
 * @ClassName ExportTaskController.java
 * @Description 下载中心
 * @Date 2017年6月4日 下午3:24:37
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("export/task")
public class ExportTaskController {

    
    @Resource
    private ExportTaskService exportTaskService;

    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<ExportTask> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        Map<String, Object> filters = pageCondition.getFilters();
        if (null != filters && null != filters.get("taskSign")) {
            int taskSign = Integer.parseInt(filters.get("taskSign").toString());
            if (taskSign == 1) {
                List<ExportTask> result = new ArrayList<ExportTask>();
                Page<ExportCollection> page = exportTaskService.search(pageCondition.getPageNo(),
                        pageCondition.getPageSize(), loginEmployee);
                for (ExportCollection e : page.getResults()) {
                    ExportTask t = new ExportTask();
                    t.setExportTaskId(e.getId());
                    t.setName(e.getFileName());
                    t.setStatusName(e.getStatusName());
                    t.setCostDate(e.getCostDate());
                    t.setCreateTime(e.getCreateTime());
                    t.setFileUrl(e.getFilePath());
                    t.setTaskSign(1);
                    result.add(t);
                }
                return new Page<ExportTask>(page.getPageNo(), page.getPageSize(), page.getTotal(), result);
            }
        }

        pageCondition.getFilters().put("createUserId", loginEmployee.getUserId());
        pageCondition.getFilters().put("isDelete", false);
        return exportTaskService.search(pageCondition, loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "v2/search", method = RequestMethod.POST)
    public Page<ExportCollection> searchForWaybill(PageCondition pageCondition, LoginEmployee loginEmployee) {
        return exportTaskService.search(pageCondition.getPageNo(), pageCondition.getPageSize(), loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "{exportTaskId}/delete", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer exportTaskId, LoginEmployee loginEmployee) {
        exportTaskService.delete(exportTaskId, loginEmployee);
    }

    /**
     * 获取所有的任务类型
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getTaskSigns", method = RequestMethod.GET)
    public List<Map<String, String>> getTaskSigns() {
        List<Map<String, String>> rstData = new ArrayList<>();
        Map<String, String> rst = null;
        for (ExportTask.TaskSign sign : ExportTask.TaskSign.values()) {
            rst = new HashMap<>();
            rst.put("code", sign.getCode() + "");
            rst.put("desc", sign.getDesc());

            rstData.add(rst);
        }

        return rstData;
    }

    /**
     * 运单导出可选字段
     */
    @ResponseBody
    @RequestMapping(value = "waybill/export/fields", method = RequestMethod.POST)
    public Object waybillExportFields(LoginEmployee loginEmployee) {
        return exportTaskService.exportFields(loginEmployee);
    }

    /**
     * 运单excel导出
     */
    @ResponseBody
    @RequestMapping(value = "v2/waybill/export", method = RequestMethod.POST)
    public Object export(@RequestBody ExportParam exportParam, LoginEmployee loginEmployee){
        return exportTaskService.doExport(exportParam, loginEmployee);
    }

    /**
     * 运单导出数据中心删除接口
     */
    @ResponseBody
    @RequestMapping(value = "{exportTaskId}/deleteData", method = RequestMethod.DELETE)
    public void deleteData(@PathVariable Integer exportTaskId, LoginEmployee loginEmployee) {
        exportTaskService.deleteData(exportTaskId, loginEmployee);
    }

}
