package com.juma.tgm.manage.crm.vo;

import java.io.Serializable;

import com.juma.tgm.crm.domain.CustomerInfo;

public class CustomerInfoVo implements Serializable {

    private static final long serialVersionUID = 740088454186710982L;
    private CustomerInfo customerInfo;
    private Integer regionCode;
    private String loginUserTel;

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public Integer getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(Integer regionCode) {
        this.regionCode = regionCode;
    }

    public String getLoginUserTel() {
        return loginUserTel;
    }

    public void setLoginUserTel(String loginUserTel) {
        this.loginUserTel = loginUserTel;
    }

}
