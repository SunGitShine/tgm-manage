package com.juma.tgm.manage.waybill.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.user.domain.CurrentUser;
import com.juma.tgm.waybill.domain.vo.WaybillQueryVo;
import com.juma.tgm.waybill.service.DeliveryPointSupplementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @ClassName SupplementDeliveryController.java
 * @Description 修改路线
 * @author Libin.Wei
 * @Date 2017年6月21日 下午7:02:08
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("supplement/delivery")
public class SupplementDeliveryController extends BaseController {

    @Resource
    private DeliveryPointSupplementService deliveryPointSupplementService;

    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<WaybillQueryVo> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        this.formatAreaCodeToList(pageCondition, true);
        return deliveryPointSupplementService.search(pageCondition,loginEmployee);
    }

    /**
     * 能否修改路线
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "{waybillId}/can/update/address", method = RequestMethod.GET)
    public void canUpdateAddress(@PathVariable Integer waybillId, CurrentUser currentUser,
            LoginEmployee loginEmployee) {
    }
}
