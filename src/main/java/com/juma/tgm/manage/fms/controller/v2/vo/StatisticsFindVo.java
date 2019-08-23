package com.juma.tgm.manage.fms.controller.v2.vo;

import java.io.Serializable;

public class StatisticsFindVo  implements Serializable {

    // 客户id
    private Integer customerId ;

    //项目名称
    private String projectName ;


    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
