package com.juma.tgm.manage.fms.controller.v3;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.google.common.collect.Lists;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.conf.domain.ConfParamOption;
import com.juma.conf.service.ConfParamService;
import com.juma.tgm.common.Constants;
import com.juma.tgm.fms.domain.v3.bo.ReconciliationWaybillDetailBo;
import com.juma.tgm.fms.domain.v3.bo.ReconcilicationForPayApply;
import com.juma.tgm.fms.domain.v3.bo.WaybillAdjustFrightForPayable;
import com.juma.tgm.fms.service.v3.ReconcilicationForPayApplyService;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillParam;
import com.juma.tgm.waybill.service.WaybillParamService;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.vms.driver.enumeration.DriverTypeEnum;
import com.juma.vms.external.service.VmsService;
import com.juma.vms.vendor.domain.Vendor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import me.about.poi.writer.XssfWriter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ReconcilicationForPayApplyController.java
 * @Description 应付对账申请
 * @author Libin.Wei
 * @Date 2018年11月26日 下午4:45:42
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("reconcilicationForPay/apply/v3")
@Api(value = "RECONCILICATION-For-PayApply-Controller")
public class ReconcilicationForPayApplyController extends BaseController {

    private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    @Resource
    private ReconcilicationForPayApplyService reconcilicationForPayApplyService;
    @Resource
    private WaybillParamService waybillParamService;
    @Resource
    private VmsService vmsService;
    @Resource
    private ConfParamService confParamService;
    @Resource
    private WaybillService waybillService;
    @Resource
    private ImportUpdateFreighForPayabletCheck importUpdateFreighForPayabletCheck;

    @ApiOperation("应付对账申请分页列表")
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<ReconcilicationForPayApply> search(@RequestBody PageCondition pageCondition,
            LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, true);
        super.filtersIsMapThenRemoveVulueIsNull(pageCondition);
        return reconcilicationForPayApplyService.search(pageCondition, loginEmployee);
    }

    @ApiOperation(value = "应付对账申请客户对应运单分页列表", notes = "客户ID(customerId)是必须条件，项目ID(projectId)根据列表确定，列表有项目名称则为必须条件")
    @ResponseBody
    @RequestMapping(value = "waybills/search", method = RequestMethod.POST)
    public Page<ReconciliationWaybillDetailBo> searchWaybills(@RequestBody PageCondition pageCondition,
            LoginEmployee loginEmployee) {
        super.filtersIsMapThenRemoveVulueIsNull(pageCondition);
        return reconcilicationForPayApplyService.searchWaybills(pageCondition, loginEmployee);
    }

    @ApiOperation("应付生成对账单")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public String create(@RequestBody List<Integer> waybillIdList, LoginEmployee loginEmployee) {
        return reconcilicationForPayApplyService.createReconcilication(waybillIdList, loginEmployee);
    }

    @ApiOperation("应付单条改价")
    @ResponseBody
    @RequestMapping(value = "single/update/cost", method = RequestMethod.POST)
    public void updateCost(@RequestBody WaybillAdjustFrightForPayable waybillAdjustFrightForPayable,
            LoginEmployee loginEmployee) {
        List<WaybillAdjustFrightForPayable> waybillAdjustFrightForPayables = new ArrayList<WaybillAdjustFrightForPayable>();
        waybillAdjustFrightForPayables.add(waybillAdjustFrightForPayable);
        changeFreight(waybillAdjustFrightForPayable.getWaybillId(),false);
        reconcilicationForPayApplyService.updateCostBatch(waybillAdjustFrightForPayables, loginEmployee);
    }

    @ApiOperation(value = "应付单条改价前获取价格信息", notes = "根据运单ID(waybillId)获取")
    @ApiImplicitParam(name = "waybillId", value = "运单ID", paramType = "path")
    @ResponseBody
    @RequestMapping(value = "load/{waybillId}/detail", method = RequestMethod.POST)
    public WaybillAdjustFrightForPayable loadCostDetail(@PathVariable Integer waybillId, LoginEmployee loginEmployee) {
        return reconcilicationForPayApplyService.findupdateFrightDetails(waybillId, loginEmployee);
    }

    @ApiOperation("应付导出改价模板")
    @RequestMapping(value = "export-update-model", method = RequestMethod.POST)
    public void exportUpdateModel(int[] waybillIds, HttpServletResponse httpServletResponse,
            LoginEmployee loginEmployee) throws BusinessException {
        List<Integer> waybillIdList = Lists.newArrayList();
        for (int waybillId : waybillIds) { waybillIdList.add(waybillId); }
        List<Waybill> waybills = reconcilicationForPayApplyService.listByWaybillIds(waybillIdList, loginEmployee);
        List<WaybillAdjustFrightForPayable> waybillAdjustFrightForPayables = new ArrayList<WaybillAdjustFrightForPayable>();
        for (Waybill waybill : waybills) {
            WaybillParam waybillParam = waybillParamService.findByWaybillId(waybill.getWaybillId());
            WaybillAdjustFrightForPayable wfp = new WaybillAdjustFrightForPayable(waybill, waybillParam);

            // 转运单
            if (NumberUtils.compare(waybill.getReceiveWay(), Waybill.ReceiveWay.TRANSFORM_BILL.getCode()) == 0) {
                Vendor vendor = vmsService.loadByVenorId(waybill.getVendorId());
                wfp.setDriverOrVendorName(vendor == null ? "" : vendor.getVendorName());
                wfp.setPlateNumber(Constants.VENDOR_WAYBILL_PLATENUMBER);
            } else {
                // 非转运单
                if (null != waybill.getDriverType()
                        && NumberUtils.compare(waybill.getDriverType(), DriverTypeEnum.OWN_SALE.getCode()) == 0) {
                    wfp.setDriverOrVendorName(waybill.getDriverName());
                } else {
                    Vendor vendor = vmsService.loadByVenorId(waybill.getVehicleToVendor());
                    if (null != vendor) {
                        wfp.setDriverOrVendorName(vendor.getVendorName());
                    }
                }
            }

            waybillAdjustFrightForPayables.add(wfp);
        }
        try {
            httpServletResponse.setContentType(CONTENT_TYPE);
            httpServletResponse.setHeader("Content-disposition", "attachment; filename=update-model.xlsx");
            new XssfWriter().appendToSheet("运单改价模板", waybillAdjustFrightForPayables)
                    .writeToOutputStream(httpServletResponse.getOutputStream());
        } catch (Exception e) {
            throw new BusinessException("export error ", "import.xlsx.export.error");
        }
    }

    
    private void changeFreight(Integer waybillId,boolean isBatch) {
        //用户中心配置改价前提
        List<ConfParamOption> options = confParamService.findParamOptions("date_limit_for_freight");
        if (!CollectionUtils.isEmpty(options)) {
            for (ConfParamOption option : options) {
                String optionName = option.getOptionName();
                String optionValue = option.getOptionValue() + " 23:59:59";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = null;
                try {
                    d = sdf.parse(optionValue);
                } catch (ParseException e) {
                    throw new BusinessException("error.date.parse", "日期格式解析错误");
                }

                if (StringUtils.isNotBlank(optionName) && optionName.equals("payable")) {
                    if (waybillId != null) {
                        Waybill waybill = waybillService.getWaybill(waybillId);
                        if(waybill == null || waybill.getPlanDeliveryTime() == null) return;
                        if(waybill.getPlanDeliveryTime().getTime() <= d.getTime()) {
                            if(isBatch) {
                                throw new BusinessException("error.forbidden.freight",
                                        "用车时间为" + option.getOptionValue() + "以前的运单已被限制改价，您上传的运单中含有限制改价的运单，如有疑问请咨询大区或总部运营中心");
                            } else {
                                throw new BusinessException("error.forbidden.freight",
                                        "用车时间为" + option.getOptionValue() + "以前的运单已被限制改价，如有疑问请咨询大区或总部运营中心");
                            }
                        }
                    }
                }
            }
        }
    }
    
    @ApiOperation("应付导入excel批量改价")
    @ResponseBody
    @RequestMapping(value = "import-update", method = RequestMethod.POST)
    public void importResult(@RequestParam(required = false) MultipartFile uploadPic, LoginEmployee loginEmployee)
            throws BusinessException {
        List<WaybillAdjustFrightForPayable> waybillAdjustFrightForPayables = importUpdateFreighForPayabletCheck
                .checkImportFileAndFrom(uploadPic, loginEmployee);
        for (WaybillAdjustFrightForPayable waybillAdjustFrightForPayable : waybillAdjustFrightForPayables) {
            changeFreight(waybillAdjustFrightForPayable.getWaybillId(),true);
        }
        reconcilicationForPayApplyService.updateCostBatch(waybillAdjustFrightForPayables, loginEmployee);
    }
}
