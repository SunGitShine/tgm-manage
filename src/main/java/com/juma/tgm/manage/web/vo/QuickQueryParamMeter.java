package com.juma.tgm.manage.web.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName QuickQueryParamMeter.java
 * @Description 快捷查询参数
 * @author Libin.Wei
 * @Date 2018年7月3日 下午4:06:20
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class QuickQueryParamMeter implements Serializable {

    private String key;
    private String title;
    private Boolean visible;
    private String value;
    private boolean selected;
    private List<QuickQueryParamMeter> children = new ArrayList<QuickQueryParamMeter>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<QuickQueryParamMeter> getChildren() {
        return children;
    }

    public void setChildren(List<QuickQueryParamMeter> children) {
        this.children = children;
    }

}
