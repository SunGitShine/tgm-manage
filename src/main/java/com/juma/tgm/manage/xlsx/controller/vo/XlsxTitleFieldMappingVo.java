package com.juma.tgm.manage.xlsx.controller.vo;

import java.util.ArrayList;
import java.util.List;

import com.juma.tgm.xlsx.domain.XlsxTitleFieldMapping;

public class XlsxTitleFieldMappingVo {

    private List<XlsxTitleFieldMapping> mappings = new ArrayList<XlsxTitleFieldMapping>();

    public List<XlsxTitleFieldMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<XlsxTitleFieldMapping> mappings) {
        this.mappings = mappings;
    }
    
}
