package com.juma.tgm.manage.waybill.controller;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.tools.service.BusinessAreaCommonService;
import com.juma.tgm.tools.service.CrmCommonService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.WaybillCommonService;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.vms.driver.enumeration.DriverTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 运单信息补录，处理TMS迭代5.2.0上线时间点之前的运单数据，用于补录子公司数据等
 */

@Controller
@RequestMapping("waybill/supplement")
public class WaybillInfoSupplementController extends BaseController {

    @Resource
    private WaybillService waybillService;
    @Resource
    private WaybillCommonService waybillCommonService;
    @Resource
    private BusinessAreaCommonService businessAreaCommonService;
    @Resource
    private CrmCommonService crmCommonService;

    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<Waybill> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, true);
        Map<String, Object> filters = pageCondition.getFilters();
        filters.put("backstage", true);

        List<Integer> statusViewList = new ArrayList<Integer>();
        statusViewList.add(Waybill.StatusView.WATING_RECEIVE.getCode());
        statusViewList.add(Waybill.StatusView.WATING_DELIVERY.getCode());
        statusViewList.add(Waybill.StatusView.DELIVERYING.getCode());
        statusViewList.add(Waybill.StatusView.FINISH.getCode());
        filters.put("statusViewList", statusViewList);
        filters.put("departmentIdIsNull", "departmentIdIsNull");
        filters.put("queryNotReconciliation", "queryNotReconciliation");

        pageCondition.setOrderBy(
                StringUtils.isBlank(pageCondition.getOrderBy()) ? " planDeliveryTime " : pageCondition.getOrderBy());
        pageCondition.setOrderSort(
                StringUtils.isBlank(pageCondition.getOrderSort()) ? " desc " : pageCondition.getOrderSort());
        Page<Waybill> page = waybillService.search(loginEmployee, pageCondition);
        // 由于是临时方法，故在controller组装业务区域名称数据
        for (Waybill waybill : page.getResults()) {
            if (StringUtils.isBlank(waybill.getAreaCode())) {
                continue;
            }

            waybill.setAreaName(businessAreaCommonService.loadLogicAndSelfAreaName(waybill.getAreaCode(), loginEmployee));
        }
        return page;
    }

    @ApiOperation(value = "历史运单信息补录，入参：waybill.waybillIds；waybill.vehicleToVendor")
    @ResponseBody
    @RequestMapping(value = "modify", method = RequestMethod.POST)
    public void supplement(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {
        if (CollectionUtils.isEmpty(waybill.getWaybillIds())) {
            throw new BusinessException("pleaseSelectWaybill", "waybill.error.pleaseSelectWaybill");
        }

        List<Waybill> updateWaybills = new ArrayList<>();

        for (Integer waybillId : waybill.getWaybillIds()) {
            Waybill updateWaybill = new Waybill();
            Waybill wb = waybillCommonService.getWaybillById(waybillId);
            if (null == wb) {
                continue;
            }

            if (NumberUtils.compare(wb.getReceiveWay(), Waybill.ReceiveWay.TRANSFORM_BILL.getCode()) != 0) {
                if (null != wb.getDriverType() && NumberUtils.compare(wb.getDriverType(),
                        DriverTypeEnum.OWN_SALE.getCode()) != 0
                        && null == waybill.getVehicleToVendor()) {
                    throw new BusinessException("errors.common.prompt", "errors.common.prompt",
                            "运单号" + wb.getWaybillNo() +
                            "是非自营司机的运单，请选择承运商");
                }
                updateWaybill.setVehicleToVendor(waybill.getVehicleToVendor());
            } else {
                updateWaybill.setVehicleToVendor(null);
            }
            updateWaybill.setWaybillId(waybillId);

            updateWaybills.add(updateWaybill);
        }

        // 更改
        for (Waybill w : updateWaybills) {
            waybillCommonService.update(w, loginEmployee);
        }
    }
}
