package com.juma.tgm.manage.web.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName DesignDateVo.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年5月15日 下午12:42:49
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class DesignDateVo implements Serializable {

    private Date startTime;
    private Date endTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

}
