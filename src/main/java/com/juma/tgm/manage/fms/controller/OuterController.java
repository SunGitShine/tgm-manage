package com.juma.tgm.manage.fms.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.exception.BusinessException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.fms.service.ReconciliationService;
import com.juma.tgm.manage.fms.controller.vo.WorkflowTask;
import com.juma.workflow.core.domain.TaskDetail;

@Controller
@RequestMapping(value="reconciliation/v2")
public class OuterController {
    
    @Resource
    private ReconciliationService reconciliationService;

    @Deprecated
    @RequestMapping(value="workflow",method=RequestMethod.GET)
    public String fromWorkflow(@RequestParam(required=false)String taskId,Model model, LoginEmployee loginEmployee) {
        TaskDetail taskDetail = new TaskDetail();
        if(taskId != null) {
            try {
                taskDetail = reconciliationService.getWorkflowElement(taskId, loginEmployee);
                model.addAttribute("taskDetail", taskDetail);
            } catch (Exception e) {
                throw new BusinessException("workflow.error", "workflow.error");
            }
        }
        model.addAttribute("taskId", taskId);
        return "pages/reconciliation/outer/task";
    }
    
    /**
     * 任务
     */
    @RequestMapping(value="task",method=RequestMethod.GET)
    @ResponseBody
    public WorkflowTask task(@RequestParam(required=false)String taskId,LoginEmployee loginEmployee) {
        WorkflowTask task = new WorkflowTask();
        TaskDetail taskDetail = new TaskDetail();
        if(taskId != null) {
            try {
                taskDetail = reconciliationService.getWorkflowElement(taskId, loginEmployee);
            } catch (Exception e) {
                throw new BusinessException("workflow.error", "workflow.error");
            }
        }
        task.setTaskDetail(taskDetail);
        task.setTaskId(taskId);
        return task;
    }
}
