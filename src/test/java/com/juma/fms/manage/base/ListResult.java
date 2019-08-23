package com.juma.fms.manage.base;

import lombok.Data;

import java.util.List;

/**
 * 功能 :
 *
 * @author : Bruce(刘正航) 17:55 2019-03-29
 */
@Data
public class ListResult {
    private Integer code;
    private String message;
    private List<Object> data;
}
