package com.juma.tgm.manage.fms.controller.v2.vo;


/***
 *
 * @author huangxing
 *
 *
 * 没有分页参数 只接收 filters
 *
 * */
public class FiltersVo <T>{

    private T filters;

    public T getFilters() {
        return filters;
    }

    public void setFilters(T filters) {
        this.filters = filters;
    }
}
