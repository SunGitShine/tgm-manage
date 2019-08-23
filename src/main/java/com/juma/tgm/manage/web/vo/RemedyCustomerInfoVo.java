package com.juma.tgm.manage.web.vo;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class RemedyCustomerInfoVo {

    private Integer tenantId;
    private List<Integer> crmCustomerIds = new ArrayList<>();
    private String checkCode;

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public List<Integer> getCrmCustomerIds() {
        return crmCustomerIds;
    }

    public void setCrmCustomerIds(List<Integer> crmCustomerIds) {
        this.crmCustomerIds = crmCustomerIds;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }
}
