package com.juma.fms.manage.base;

import lombok.Data;

/**
 * 功能 :
 *
 * @author : Bruce(刘正航) 08:49 2019-04-01
 */
@Data
public class Result {
    private Integer code;
    private String message;
    private Object data;
}
