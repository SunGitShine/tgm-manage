package com.juma.tgm.manage.driver.controller;

import java.util.List;

import javax.annotation.Resource;

import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.vms.driver.external.DriverExternalFilter;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.driver.service.DriverService;
import com.juma.tgm.manage.web.controller.BaseController;

/**
 * @author Administrator
 * @version V1.0
 * @Description: 司机信息
 * @date 2016年5月16日 下午5:29:01
 */

@Controller
@RequestMapping(value = "driver")
public class DriverController extends BaseController {

    @Resource
    private DriverService driverService;
    @Resource
    private VmsCommonService vmsCommonService;

    /**
     * 根据ID获取
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "{driverId}/json/detail", method = RequestMethod.GET)
    public Driver jsonDetail(@PathVariable Integer driverId, LoginEmployee loginEmployee) {
        return driverService.getDriver(driverId);
    }

    /**
     * 根据业务范围查询
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "{areaCode}/areaCode/list", method = RequestMethod.GET)
    public List<Driver> list(@PathVariable String areaCode, LoginEmployee loginEmployee) {
        return driverService.listByAreaCodeLike(loginEmployee.getTenantId(), areaCode, null);
    }

    @ApiOperation(value = "根据司机姓名或手机号获取", notes = "callbackPageSize拼接于URL之后，返回条数，非必填，默认15条，最大200条")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "司机姓名"),
            @ApiImplicitParam(name = "phone", value = "司机手机号")
    })
    @ResponseBody
    @RequestMapping(value = "listDriver/like", method = RequestMethod.POST)
    public List<com.juma.vms.driver.domain.Driver> listDriverBy(@RequestBody DriverExternalFilter driverExternalFilter,
                                                                Integer callbackPageSize, LoginEmployee loginEmployee) {
        return vmsCommonService.listDriverBy(driverExternalFilter, callbackPageSize, loginEmployee);
    }
}
