package com.juma.tgm.manage.fms.controller.vo;

import com.juma.workflow.core.domain.TaskDetail;

public class WorkflowTask {

    private String taskId;
    
    private TaskDetail taskDetail;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public TaskDetail getTaskDetail() {
        return taskDetail;
    }

    public void setTaskDetail(TaskDetail taskDetail) {
        this.taskDetail = taskDetail;
    }
}
