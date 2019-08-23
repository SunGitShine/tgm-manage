package com.juma.fms.manage.base;

import com.giants.common.tools.Page;
import lombok.Data;

/**
 * 功能 :
 *
 * @author : Bruce(刘正航) 08:47 2019-04-01
 */
@Data
public class PageResult {
    private Integer code;
    private String message;
    private Page<Object> data;
}
