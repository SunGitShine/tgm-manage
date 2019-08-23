package com.juma.tgm.manage.waybill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillBo;
import com.juma.tgm.waybill.service.WaybillExceptionService;

/**
 * 
 * @Description: 运单异常
 * @author weilibin
 * @date 2016年5月19日 下午7:56:10
 * @version V1.0
 */

@Controller
@RequestMapping("/waybillException")
public class WaybillExceptionController extends BaseController {

    @Autowired
    private WaybillExceptionService waybillExceptionService;

    
    /**
     * 运单异常分页
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<WaybillBo> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        pageCondition.getFilters().put("backstage", true);
        super.formatAreaCodeToList(pageCondition, true);
        return waybillExceptionService.searchDetails(pageCondition, loginEmployee);
    }

    /**
     * 原因填写
     */
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {
        if (null == waybill.getWaybillId()) {
            return;
        }
        waybillExceptionService.updateReason(waybill.getWaybillId(), waybill.getWaybillRemark(), loginEmployee);
    }
}
