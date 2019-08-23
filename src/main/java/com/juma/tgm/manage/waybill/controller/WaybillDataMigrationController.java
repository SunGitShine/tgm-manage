package com.juma.tgm.manage.waybill.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.collections.CollectionUtils;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.manage.waybill.util.WaybillControllerUtil;
import com.juma.tgm.manage.waybill.vo.WaybillDataMigrationVo;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.WaybillService;

/**
 * Created by shawn_lin on 2017/8/11.
 */
@Controller
@RequestMapping(value = "waybill/data/migration")
public class WaybillDataMigrationController extends BaseController {
    @Resource
    private WaybillService waybillService;

    @Resource
    private CustomerInfoService customerInfoService;

    @Resource
    private WaybillControllerUtil waybillControllerUtil;

    /**
     * 按业务规则查询 本组织和非本组织
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<Waybill> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, true);
        pageCondition.getFilters().put("wechatPending", false);
        pageCondition.setOrderBy(" planDeliveryTime desc ");
        // 只可见本业务区域的运单
        pageCondition.getFilters().put("ownerAreaCanSee", true);
        return waybillService.search(loginEmployee, pageCondition);
    }

    // 分配业务区域
    @ResponseBody
    @RequestMapping(value = "businessArea/update", method = RequestMethod.POST)
    public String distributeBusinessArea(@RequestBody WaybillDataMigrationVo waybillDataMigrationVo,
            LoginEmployee loginEmployee) {
        StringBuffer buffer = new StringBuffer("");
        List<Integer> waybillIds = waybillDataMigrationVo.getWaybillIds();
        String areaCode = waybillDataMigrationVo.getAreaCode();
        int success = 0;

        if (CollectionUtils.isEmpty(waybillIds)) {
            return "总共分配数据0条，其中数据分配成功0条；<br/>失败的数据及原因如下：<br/>";
        }

        for (Integer waybillId : waybillIds) {
            // 查找运单所属客户的业务区域
            Waybill waybill = waybillService.getWaybill(waybillId);
            if (waybill == null) {
                String reason = "运单ID【"+waybillId+"】的运单不存在";
                buffer.append(reason).append("；<br/>");
                continue;
            }

            CustomerInfo customerInfo = customerInfoService.findCusInfoById(waybill.getCustomerId());
            if (customerInfo == null) {
                String reason = "运单号【" + waybill.getWaybillNo() + "】的运单所属客户不存在";
                buffer.append(reason).append("；<br/>");
                continue;
            }

            if (!customerInfo.getAreaCode().equals(areaCode)) {
                String reason = "运单号【" + waybill.getWaybillNo() + "】的运单迁移的业务区域和所属客户的业务区域不一致";
                buffer.append(reason).append("；<br/>");
                continue;
            }

            waybillService.updateAreaCode(waybillId, areaCode, loginEmployee);
            success++;
        }
        return "总共分配数据" + waybillIds.size() + "条，其中数据分配成功" + success + "条；<br/>失败的数据及原因如下：<br/>" + buffer.toString();
    }

    // 分配客户经理
    @ResponseBody
    @RequestMapping(value = "customerManager/update", method = RequestMethod.POST)
    public String distributeCustomerManager(@RequestBody WaybillDataMigrationVo waybillDataMigrationVo,
            LoginEmployee loginEmployee) {
        StringBuffer buffer = new StringBuffer("");
        List<Integer> waybillIds = waybillDataMigrationVo.getWaybillIds();
        Integer customerManagerId = waybillDataMigrationVo.getCustomerManagerId();
        int success = 0;

        if (CollectionUtils.isEmpty(waybillIds)) {
            return "总共分配数据0条，其中数据分配成功0条；<br/>失败的数据及原因如下：<br/>";
        }

        for (Integer waybillId : waybillIds) {
            // 查找运单所属客户的业务区域
            Waybill waybill = waybillService.getWaybill(waybillId);
            if (waybill == null) {
                String reason = "运单ID【" + waybillId + "】的运单不存在";
                buffer.append(reason).append("；<br/>");
                continue;
            }

            CustomerInfo customerInfo = customerInfoService.findCusInfoById(waybill.getCustomerId());
            if (customerInfo == null) {
                String reason = "运单号【" + waybill.getWaybillNo() + "】的运单所属客户不存在";
                buffer.append(reason).append("；<br/>");
                continue;
            }

            if (!customerInfo.getCustomerManagerUserId().equals(customerManagerId)) {
                // 客户经理不符合，操作不成功
                String reason = "运单号【" + waybill.getWaybillNo() + "】运单迁移的客户经理和所属客户的客户经理不一致";
                buffer.append(reason).append("；<br/>");
                continue;
            }

            waybillService.updateCustomerManagerId(waybillId, customerManagerId, loginEmployee);
            success++;
        }

        return "总共分配数据" + waybillIds.size() + "条，其中数据分配成功" + success + "条；<br/>失败的数据及原因如下：<br/>" + buffer.toString();
    }
}
