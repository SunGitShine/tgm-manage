package com.juma.oms.manage.notify.controller;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.oms.cms.service.NotifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(value = "排线结果/派车结果通知")
@RestController
public class NotifyController {

    @Resource
    private NotifyService notifyService;

    /**
     * 提交派车结果
     */
    @ApiOperation(value = "排线车次派车通知", notes = "排线车次派车通知")
    @RequestMapping(value = "notify/request/{requestId}/truck", method = RequestMethod.PUT)
    public void arrangingTruckNotify(@PathVariable String requestId, LoginEmployee loginEmployee) {
        notifyService.arrangingTruckNotify(requestId, loginEmployee);
    }


    /**
     * 提交排线结果
     */
    @ApiOperation(value = "排线结果通知", notes = "排线结果通知")
    @RequestMapping(value = "notify/request/{requestId}/plan", method = RequestMethod.PUT)
    public void drivingRoutePlanNotify(@PathVariable String requestId, @RequestParam("cause") String cause, LoginEmployee loginEmployee) {
        notifyService.drivingRoutePlanNotify(requestId, cause, loginEmployee);
    }

}
