package com.juma.tgm.manage.xlsx.controller.vo;

public class TitleFieldMappingVo {

    private String title;
    
    private String field;
    
    private boolean required;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    
}
