package com.juma.tgm.manage.waybill.controller;

import com.giants.common.exception.BusinessException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginUser;
import com.juma.tgm.manage.waybill.vo.WaybillAssigneVo;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillOperateTrackNotRequieParam;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.service.WaybillService;
import javax.annotation.Resource;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName WaybillBackOperateController.java
 * @Description 后台指派操作
 * @author Libin.Wei
 * @Date 2017年5月20日 下午12:55:39
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("waybill/assigne")
public class WaybillAssigneController extends BaseController {

    @Resource
    private WaybillService waybillService;
    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;

    /**
     * 后台人工指派
     */
    @ResponseBody
    @RequestMapping(value = "/manualAssign", method = RequestMethod.POST)
    public void manualAssign(@RequestBody WaybillAssigneVo waybillAssigneVo, LoginEmployee loginEmployee) {
        Waybill waybill = waybillService.getWaybill(waybillAssigneVo.getWaybillId());
        if (null == waybill) {
            return;
        }

        if (NumberUtils.compare(waybill.getReceiveWay(), Waybill.ReceiveWay.TRANSFORM_BILL.getCode()) == 0) {
            throw new BusinessException("transformBillCannotAssign", "waybill.error.transformBillCannotAssign");
        }

        buildWaybill(waybill, waybillAssigneVo);

        if (isCanChangeCar(waybill)) {
            this.changeCar(waybill, loginEmployee);
        } else if (Waybill.Status.WATING_RECEIVE.getCode() == waybill.getStatus()
                || Waybill.Status.UNDETERMINED.getCode() == waybill.getStatus()
                || Waybill.Status.NO_DRIVER_ANSWER.getCode() == waybill.getStatus()) {
            // 新运单指派车辆：运单没有车辆，第一次指派
            this.changeToAssigned(waybill, loginEmployee);
        }
    }

    // 指派车辆
    private void changeToAssigned(Waybill waybill, LoginUser loginUser) {
        // 新运单指派车辆：运单没有车辆，第一次指派
        waybillService.changeToAssigned(waybill.getWaybillId(), waybill.getDriverId(), waybill.getTruckId(),
                waybill.getVehicleToVendor(), Waybill.ReceiveWay.MANUAL_ASSIGN.getCode(), waybill.getAssignWaybillRemark(),
                loginUser);

        // 判断指派成功
        Waybill wb = waybillService.getWaybill(waybill.getWaybillId());
        if (null != wb && NumberUtils.compare(Waybill.Status.ASSIGNED.getCode(), wb.getStatus()) == 0) {
            // 操作轨迹
            waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.ASSIGNED_SYS,
                    OperateApplication.BACKGROUND_SYS, null, loginUser);
        }
    }

    // 更换车辆
    private void changeCar(Waybill waybill, LoginUser loginUser) {
        // 运单更换车辆：运单已经指派了车辆，车辆不符合要求或其他原因，人工更换车辆
        waybillService.changeCar(waybill.getWaybillId(), waybill.getDriverId(), waybill.getTruckId(),
                waybill.getVehicleToVendor(), Waybill.ReceiveWay.MANUAL_ASSIGN.getCode(), waybill.getAssignWaybillRemark(),
                loginUser);

        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.MODIFY_POINT_TRUCK,
                OperateApplication.BACKGROUND_SYS,
                new WaybillOperateTrackNotRequieParam(waybill.getAssignWaybillRemark()), loginUser);
    }

    // 判断能否更改车辆
    private boolean isCanChangeCar(Waybill waybill) {
        // 1、待配送；2、待确认
        if (Waybill.Status.ASSIGNED.getCode() == waybill.getStatus()
                || Waybill.Status.WAITINT_DRIVER_ANSWER.getCode() == waybill.getStatus()) {
            return true;
        }
        return false;
    }

    // 组装运单司机车辆信息
    private void buildWaybill(Waybill waybill, WaybillAssigneVo waybillAssigneVo) {
        if (null == waybillAssigneVo.getTruckId() || null == waybillAssigneVo.getDriverId()
                || null == waybillAssigneVo.getVendorId()) {
            throw new BusinessException("pleaseSelectCapacity", "errors.common.prompt", "请选择车辆或联系售后人员");
        }
        waybill.setTruckId(waybillAssigneVo.getTruckId());
        waybill.setDriverId(waybillAssigneVo.getDriverId());
        waybill.setVehicleToVendor(waybillAssigneVo.getVendorId());
        waybill.setAssignWaybillRemark(waybillAssigneVo.getRemark());
        waybill.setReceiveWay(Waybill.ReceiveWay.MANUAL_ASSIGN.getCode());
    }


    /**
     * 后台系统派车
     * 
     * @param waybillId
     *            运单ID
     */
    @ResponseBody
    @RequestMapping(value = "{waybillId}/sysAssign", method = RequestMethod.GET)
    public void sysAssign(@PathVariable Integer waybillId, LoginEmployee loginEmployee) {
        waybillService.changeToWaitingReceive(waybillId, loginEmployee);

        // 操作轨迹
        waybillOperateTrackService.insert(waybillId, OperateType.ASSIGNED,
                OperateApplication.BACKGROUND_SYS, null, loginEmployee);
    }
}
