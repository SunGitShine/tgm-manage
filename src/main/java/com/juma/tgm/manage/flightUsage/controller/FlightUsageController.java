package com.juma.tgm.manage.flightUsage.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.flight.domain.bo.FlightUsageQuery;
import com.juma.tgm.flightUsage.service.TmsFlightUsageService;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.user.domain.CurrentUser;

/**
 * @ClassName FlightByPageController.java
 * @Description 班次
 * @author Libin.Wei
 * @Date 2017年5月17日 上午10:34:22
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Deprecated
@Controller
@RequestMapping("flight/usage")
public class FlightUsageController extends BaseController {

    @Resource
    private TmsFlightUsageService tmsFlightUsageService;

    /**
     * 全部班次分页列表
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<FlightUsageQuery> search(PageCondition pageCondition,
            @ModelAttribute("currentUser") CurrentUser currentUser, LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, false);
        return tmsFlightUsageService.search(pageCondition, loginEmployee);
    }

    /**
     * 可用班次分页列表：专车、分公司
     */
    @ResponseBody
    @RequestMapping(value = "available/flight/search", method = RequestMethod.POST)
    public Page<FlightUsageQuery> availableFlightSearch(PageCondition pageCondition, LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, false);
        Object obj = pageCondition.getFilters().get("isQueryAll");
        pageCondition.getFilters().remove("isQueryAll");
        if (null != obj && BaseUtil.strToNum(obj.toString()) == 1) {
            return tmsFlightUsageService.allVehicleHasBindDriverSearch(pageCondition, loginEmployee);
        }
        return tmsFlightUsageService.availableFlightSearch(pageCondition, loginEmployee);
    }

    /**
     * 可用班次分页列表：落地配
     */
    @ResponseBody
    @RequestMapping(value = "landing/available/flight/search", method = RequestMethod.POST)
    public Page<FlightUsageQuery> langdingAvailableFlightSearch(PageCondition pageCondition,
            LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, false);
        Object obj = pageCondition.getFilters().get("isQueryAll");
        pageCondition.getFilters().remove("isQueryAll");
        if (null != obj && BaseUtil.strToNum(obj.toString()) == 1) {
            return tmsFlightUsageService.landingAllVehicleHasBindDriverSearch(pageCondition, loginEmployee);
        }
        return tmsFlightUsageService.langdingAvailableFlightSearch(pageCondition, loginEmployee);
    }
}
