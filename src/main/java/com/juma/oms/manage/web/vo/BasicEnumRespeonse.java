package com.juma.oms.manage.web.vo;

import java.io.Serializable;

/**
 * @ClassName BasicEnumRespeonse.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年4月26日 下午9:48:16
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class BasicEnumRespeonse implements Serializable {

    private int code;
    private String desc;

    public BasicEnumRespeonse() {
    }

    public BasicEnumRespeonse(Integer code, String desc) {
        super();
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
