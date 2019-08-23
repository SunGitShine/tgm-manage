package com.juma.tgm.manage.waybill.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.common.DateUtil;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.Waybill.UpdateFreightAuditStatus;
import com.juma.tgm.waybill.domain.WaybillBo;
import com.juma.tgm.waybill.domain.WaybillOperateTrackNotRequieParam;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.service.WaybillParamService;
import com.juma.tgm.waybill.service.WaybillService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by shawn_lin on 2017/7/25.
 */
@Controller
@RequestMapping(value = "freight")
public class FreightAuditController extends BaseController {

    @Resource
    private WaybillService waybillService;

    @Resource
    private WaybillParamService waybillParamService;

    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;

    @ResponseBody
    @RequestMapping(value = "audit/search", method = RequestMethod.POST)
    public Page<WaybillBo> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        List<WaybillBo> result = new ArrayList<WaybillBo>();
        
        this.formatAreaCodeToList(pageCondition, true);

        // 改价待审只显示有改价需求的运单
        if (null == pageCondition.getFilters() || null == pageCondition.getFilters().get("updateFreightAuditStatus")) {
            List<Integer> updateFreightAuditStatusList = new ArrayList<Integer>();
            for (UpdateFreightAuditStatus status : Waybill.UpdateFreightAuditStatus.values()) {
                updateFreightAuditStatusList.add(status.getCode());
            }
            pageCondition.getFilters().put("updateFreightAuditStatusList", updateFreightAuditStatusList);
        }
        // 只可见本业务区域的运单
        pageCondition.getFilters().put("ownerAreaCanSee", true);
        Page<Waybill> page = waybillService.search(loginEmployee, pageCondition);
        for (Waybill waybill : page.getResults()) {
            WaybillBo bo = new WaybillBo();
            bo.setWaybill(waybill);
            bo.setWaybillParam(waybillParamService.findByWaybillId(bo.getWaybill().getWaybillId()));
            bo.setPlanDeliveryDate(DateUtil.format(bo.getWaybill().getPlanDeliveryTime(), DateUtil.YYYYMMDD));
            result.add(bo);
        }
        return new Page<WaybillBo>(page.getPageNo(), page.getPageSize(), page.getTotal(), result);
    }

    @ResponseBody
    @RequestMapping(value = "audit/confirm", method = RequestMethod.POST)
    public void confirm(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {
        String changeInfo = waybillService.updateFreightAuditToPassOrFailed(waybill, loginEmployee);
        String updateFreightAuditRemark = waybill.getUpdateFreightAuditRemark();
        WaybillOperateTrackNotRequieParam notRequieParam = new WaybillOperateTrackNotRequieParam();
        notRequieParam.setRemark(
            changeInfo + "；备注:" + (StringUtils.isBlank(updateFreightAuditRemark) ? "" : updateFreightAuditRemark));
        if (waybill.getUpdateFreightAuditStatus() == Waybill.UpdateFreightAuditStatus.HAS_PASS.getCode()) {
            waybillOperateTrackService.insert(waybill.getWaybillId(),
                OperateType.UPDATE_FREIGHT_PASS,
                OperateApplication.BACKGROUND_SYS, notRequieParam, loginEmployee);
        }
        if (waybill.getUpdateFreightAuditStatus() == UpdateFreightAuditStatus.NOT_PASS.getCode()) {
            waybillOperateTrackService.insert(waybill.getWaybillId(),
                OperateType.UPDATE_FREIGHT_NO_PASS,
                OperateApplication.BACKGROUND_SYS, notRequieParam, loginEmployee);
        }
    }
}
