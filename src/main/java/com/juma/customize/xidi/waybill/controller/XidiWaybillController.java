package com.juma.customize.xidi.waybill.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.customize.annotation.CustomizeLayer;
import com.juma.customize.annotation.Customized;
import com.juma.tgm.common.Constants;
import com.juma.tgm.manage.waybill.util.WaybillControllerUtil;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.WaybillService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName: JumaPsWaybillController
 * @Description:
 * @author: liang
 * @date: 2018-03-28 15:31
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
@Customized(tenantKey = Constants.TENANT_KEY_XIDI_LOGISTICS, layer = CustomizeLayer.controller)
@Component
public class XidiWaybillController {

    @Resource
    private WaybillService waybillService;

    @Resource
    private WaybillControllerUtil waybillControllerUtil;

    public Page<Waybill> search(LoginEmployee loginEmployee, PageCondition pageCondition) {
        waybillControllerUtil.formatAreaCodeToList(pageCondition, true);
        pageCondition.getFilters().put("wechatPending", false);
        pageCondition.getFilters().put("backstage", true);
        pageCondition.setOrderBy(
                StringUtils.isBlank(pageCondition.getOrderBy()) ? " planDeliveryTime " : pageCondition.getOrderBy());
        pageCondition.setOrderSort(
                StringUtils.isBlank(pageCondition.getOrderSort()) ? " desc " : pageCondition.getOrderSort());
        Page<Waybill> page = waybillService.search(loginEmployee, pageCondition);
        for (Waybill waybill : page.getResults()) {
            waybill.setAllowSendCar(false);
        }
        return page;
    }
}
