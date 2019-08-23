package com.juma.oms.manage.order.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OrderDetailVo.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年4月25日 下午6:50:41
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class OrderDetailVo {

    /**
     * 订单状态
     */
    private String orderStatusText;

    /**
     * 业务区域
     */
    private String areaName;

    /**
     * 取消渠道
     */
    private String cancelChannelText;

    /**
     * 厢型
     */
    private String boxTypeText;

    /**
     * 是否回单
     */
    private boolean isNeedReceipt;

    /**
     * 其它用车要求
     */
    private String otherAdditionalFunction;

    /**
     * 下单渠道
     */
    private String sourceText;

    /**
     * 联系人
     */
    private String linkMan;

    /**
     * 联系人电话
     */
    private String linkManPhone;

    /**
     * 客户经理
     */
    private String customerManager;

    /**
     * 运单列表
     */
    private List<OrderWaybillVo> listWaybill = new ArrayList<OrderWaybillVo>();

    public String getOrderStatusText() {
        return orderStatusText;
    }

    public void setOrderStatusText(String orderStatusText) {
        this.orderStatusText = orderStatusText;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCancelChannelText() {
        return cancelChannelText;
    }

    public void setCancelChannelText(String cancelChannelText) {
        this.cancelChannelText = cancelChannelText;
    }

    public String getBoxTypeText() {
        return boxTypeText;
    }

    public void setBoxTypeText(String boxTypeText) {
        this.boxTypeText = boxTypeText;
    }

    public boolean isNeedReceipt() {
        return isNeedReceipt;
    }

    public void setNeedReceipt(boolean isNeedReceipt) {
        this.isNeedReceipt = isNeedReceipt;
    }

    public String getOtherAdditionalFunction() {
        return otherAdditionalFunction;
    }

    public void setOtherAdditionalFunction(String otherAdditionalFunction) {
        this.otherAdditionalFunction = otherAdditionalFunction;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan;
    }

    public String getLinkManPhone() {
        return linkManPhone;
    }

    public void setLinkManPhone(String linkManPhone) {
        this.linkManPhone = linkManPhone;
    }

    public String getCustomerManager() {
        return customerManager;
    }

    public void setCustomerManager(String customerManager) {
        this.customerManager = customerManager;
    }

    public List<OrderWaybillVo> getListWaybill() {
        return listWaybill;
    }

    public void setListWaybill(List<OrderWaybillVo> listWaybill) {
        this.listWaybill = listWaybill;
    }

}
