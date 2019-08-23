package com.juma.tgm.manage.fms.controller.vo;

import com.juma.tgm.fms.domain.v3.ReconcilicationForCompany;

public class ReconcilicationForCompanyVo extends ReconcilicationForCompany {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4140140341406042219L;

    private String contractToCompanyName;
    
    private String payToCompanyName;

    private String payToCompanyEnclosureUrl;

    public String getContractToCompanyName() {
        return contractToCompanyName;
    }

    public void setContractToCompanyName(String contractToCompanyName) {
        this.contractToCompanyName = contractToCompanyName;
    }

    public String getPayToCompanyName() {
        return payToCompanyName;
    }

    public void setPayToCompanyName(String payToCompanyName) {
        this.payToCompanyName = payToCompanyName;
    }

    public String getPayToCompanyEnclosureUrl() {
        return payToCompanyEnclosureUrl;
    }

    public void setPayToCompanyEnclosureUrl(String payToCompanyEnclosureUrl) {
        this.payToCompanyEnclosureUrl = payToCompanyEnclosureUrl;
    }
}
