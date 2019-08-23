package com.juma.tgm.manage.configure.controller.vo;

public class PrivateFactorColumn {

    private String labelName;

    private String labelInputName;

    private String inputValue;

    private Boolean required;

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelInputName() {
        return labelInputName;
    }

    public void setLabelInputName(String labelInputName) {
        this.labelInputName = labelInputName;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

}
