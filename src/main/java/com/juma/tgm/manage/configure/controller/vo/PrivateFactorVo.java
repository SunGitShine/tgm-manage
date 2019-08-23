package com.juma.tgm.manage.configure.controller.vo;

import java.util.ArrayList;
import java.util.List;

public class PrivateFactorVo {
    
    private List<PrivateFactorColumn> cols = new ArrayList<PrivateFactorColumn>();
    
    private Object kmMap;

    public List<PrivateFactorColumn> getCols() {
        return cols;
    }

    public void setCols(List<PrivateFactorColumn> cols) {
        this.cols = cols;
    }

    public Object getKmMap() {
        return kmMap;
    }

    public void setKmMap(Object kmMap) {
        this.kmMap = kmMap;
    }


}
