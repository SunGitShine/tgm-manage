package com.juma.tgm.manage.web.vo;

import java.io.Serializable;

/**
 * @ClassName MessageRequstVo.java
 * @Description 返回信息
 * @author Libin.Wei
 * @Date 2017年8月22日 上午11:29:49
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class MessageRequstVo implements Serializable {

    private static final long serialVersionUID = -5345711973775858177L;
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
