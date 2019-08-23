package com.juma.tgm.manage.web.vo;

import java.io.Serializable;

/**
 * @ClassName BaseResultVo.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年12月21日 下午2:46:29
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class BaseResultVo implements Serializable {

    private static final long serialVersionUID = -3404011665292893616L;
    private String key;
    private Integer code;
    private String desc;
    private boolean checked;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
