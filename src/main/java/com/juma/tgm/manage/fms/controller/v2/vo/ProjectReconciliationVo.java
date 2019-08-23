package com.juma.tgm.manage.fms.controller.v2.vo;

import java.io.Serializable;

public class ProjectReconciliationVo implements Serializable {

    private Integer customerId;// 客户id

    private String projectName; // 项目名称


    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    //

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
