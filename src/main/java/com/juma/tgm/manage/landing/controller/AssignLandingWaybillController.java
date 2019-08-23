package com.juma.tgm.manage.landing.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.common.DateUtil;
import com.juma.tgm.landingWaybill.domain.LandingWaybill;
import com.juma.tgm.landingWaybill.service.LandingWaybillService;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName AssignLandingWaybillController.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年11月22日 下午2:22:31
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("assignLandingWaybill")
public class AssignLandingWaybillController extends BaseController {

    @Resource
    private LandingWaybillService landingWaybillService;
    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;

    /**
     * 列表：不分页
     */
    @ResponseBody
    @RequestMapping(value = "landingWaybill/list", method = RequestMethod.POST)
    public List<LandingWaybill> listLandingWaybill(@RequestBody PageCondition pageCondition,
            LoginEmployee loginEmployee) {
        pageCondition.setPageNo(1);
        pageCondition.setPageSize(Integer.MAX_VALUE);
        // 只能指派用车时间为未来时间的运单
        pageCondition.getFilters().put("startTime", DateUtil.format());
        pageCondition.getFilters().put("endTime", DateUtil.format(DateUtil.addDays(new Date(), 7)));
        pageCondition.getFilters().put("receiveWayNotEqual", Waybill.ReceiveWay.TRANSFORM_BILL.getCode());
        pageCondition.setOrderBy(" plan_delivery_time ");
        pageCondition.setOrderSort(" desc ");
        super.formatAreaCodeToList(pageCondition, true);
        return landingWaybillService.listLandingWatingReceiveWaybill(pageCondition, loginEmployee);
    }

    /**
     * 派单基础信息
     */
    @ResponseBody
    @RequestMapping(value = "{waybillId}/waybillAssignBaseInfo", method = RequestMethod.GET)
    public LandingWaybill waybillAssignBaseInfo(@PathVariable Integer waybillId, LoginEmployee loginEmployee) {
        return landingWaybillService.getLandingWaybill(waybillId, loginEmployee);
    }

    /**
     * 列表
     */
    @ResponseBody
    @RequestMapping(value = "scattered/search", method = RequestMethod.POST)
    public Page<LandingWaybill> listScatteredWaybill(PageCondition pageCondition, LoginEmployee loginEmployee) {
        return landingWaybillService.listScatteredWaybill(pageCondition, loginEmployee);
    }

    /**
     * 确认收款:落地配业务临时使用
     */
    @ResponseBody
    @RequestMapping(value = "confirm/{waybillId}/receipt", method = RequestMethod.GET)
    public void confirmReceipt(@PathVariable Integer waybillId, LoginEmployee loginEmployee) {
        landingWaybillService.confirmReceipt(waybillId, loginEmployee);

        waybillOperateTrackService.insert(waybillId, OperateType.CONFIRM_RECEIPT,
                OperateApplication.BACKGROUND_SYS, null, loginEmployee);
    }
}
