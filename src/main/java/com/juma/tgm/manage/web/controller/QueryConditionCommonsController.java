package com.juma.tgm.manage.web.controller;

import com.juma.auth.conf.domain.BusinessArea;
import com.juma.auth.employee.domain.Department;
import com.juma.auth.employee.domain.DepartmentCompany;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.DepartmentService;
import com.juma.auth.employee.service.ECompanyService;
import com.juma.conf.domain.ConfParamOption;
import com.juma.conf.service.ConfParamService;
import com.juma.server.vm.common.DriverStatusEnum;
import com.juma.server.vm.common.VehicleStatusEnum;
import com.juma.tgm.basicTruckType.service.ConfParamInfoService;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.common.Constants;
import com.juma.tgm.costReimbursed.domain.CostReimbursed;
import com.juma.tgm.costReimbursed.domain.CostReimbursed.AuditResult;
import com.juma.tgm.manage.web.vo.BaseResultVo;
import com.juma.tgm.manage.web.vo.QuickQueryParamMeter;
import com.juma.tgm.manage.web.vo.VendorRequest;
import com.juma.tgm.project.common.CustomerForProductAndDept;
import com.juma.tgm.project.domain.RoadMap;
import com.juma.tgm.project.domain.v2.enums.ProjectEnum;
import com.juma.tgm.project.enumeration.ValuationWayEnum;
import com.juma.tgm.project.service.RoadMapService;
import com.juma.tgm.project.vo.ContractFilter;
import com.juma.tgm.project.vo.ContractVo;
import com.juma.tgm.tools.service.AuthCommonService;
import com.juma.tgm.tools.service.BusinessAreaCommonService;
import com.juma.tgm.tools.service.CrmCommonService;
import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.tgm.truck.domain.AdditionalFunction;
import com.juma.tgm.truck.domain.AdditionalFunctionBo;
import com.juma.tgm.truck.service.AdditionalFunctionService;
import com.juma.tgm.vendor.domain.VendorMapping;
import com.juma.tgm.vendor.service.VendorMappingService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.Waybill.*;
import com.juma.tgm.waybill.domain.vo.WaybillCarrierVo;
import com.juma.tgm.waybill.domain.vo.WaybillCarrierVo.TransformCarrierSettlementType;
import com.juma.tgm.waybill.service.TaxRateService;
import com.juma.vms.driver.domain.Driver;
import com.juma.vms.driver.enumeration.DriverTypeEnum;
import com.juma.vms.driver.external.DriverExternalFilter;
import com.juma.vms.external.service.VmsService;
import com.juma.vms.truck.enumeration.TruckRunTypeEnum;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName QueryConditionCommonsController.java
 * @Description 公共查询条件获取
 * @author Libin.Wei
 * @Date 2017年12月21日 下午2:27:22
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("query/condition/commons")
public class QueryConditionCommonsController {

    @Resource
    private ConfParamService confParamService;
    @Resource
    private ConfParamInfoService confParamInfoService;
    @Resource
    private TaxRateService taxRateService;
    @Resource
    private AdditionalFunctionService additionalFunctionService;
    @Resource
    private VendorMappingService vendorMappingService;
    @Resource
    private VmsService vmsService;
    @Resource
    private RoadMapService roadMapService;
    @Resource
    private CrmCommonService crmCommonService;
    @Resource
    private DepartmentService departmentService;
    @Resource
    private ECompanyService eCompanyService;
    @Resource
    private VmsCommonService vmsCommonService;
    @Resource
    private BusinessAreaCommonService businessAreaCommonService;
    @Resource
    private AuthCommonService authCommonService;

    /**
     * 支付方式
     */
    @ResponseBody
    @RequestMapping(value = "receipt/type", method = RequestMethod.GET)
    public Object listReceiptType(String hidenCodes) {
        List<Integer> hidenCodeList = strsToList(hidenCodes);

        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (ReceiptType receiptType : Waybill.ReceiptType.values()) {
            if (hidenCodeList.contains(receiptType.getCode())) {
                continue;
            }

            BaseResultVo vo = new BaseResultVo();
            vo.setCode(receiptType.getCode());
            vo.setDesc(receiptType.getDescr());
            result.add(vo);
        }

        return result;
    }

    private List<Integer> strsToList(String strs) {
        List<Integer> list = new ArrayList<Integer>();
        if (StringUtils.isBlank(strs)) {
            return list;
        }
        if (strs.indexOf(",") != -1) {
            String[] split = strs.split(",");
            for (String str : split) {
                if (!StringUtils.isNumeric(str)) {
                    continue;
                }

                list.add(BaseUtil.strToNum(str));
            }
        } else if (StringUtils.isNumeric(strs)) {
            list.add(BaseUtil.strToNum(strs));
        }

        return list;
    }

    /**
     * 派车方式
     */
    @ResponseBody
    @RequestMapping(value = "receive/way", method = RequestMethod.GET)
    public Object listReceiveWay(String hidenCodes) {
        List<Integer> hidenCodeList = strsToList(hidenCodes);

        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (ReceiveWay receiveWay : Waybill.ReceiveWay.values()) {
            if (hidenCodeList.contains(receiveWay.getCode())) {
                continue;
            }

            BaseResultVo vo = new BaseResultVo();
            vo.setCode(receiveWay.getCode());
            vo.setDesc(receiveWay.getDescr());
            result.add(vo);
        }

        return result;
    }

    /**
     * 发单渠道
     */
    @ResponseBody
    @RequestMapping(value = "waybill/source", method = RequestMethod.GET)
    public Object listWaybillSource() {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (WaybillSource waybillSource : Waybill.WaybillSource.values()) {
            BaseResultVo vo = new BaseResultVo();
            vo.setCode(waybillSource.getCode());
            vo.setDesc(waybillSource.getDescr());
            result.add(vo);
        }

        return result;
    }

    /**
     * 回单状态
     */
    @ResponseBody
    @RequestMapping(value = "need/receipt", method = RequestMethod.GET)
    public Object listNeedReceipt() {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (NeedReceipt needReceipt : Waybill.NeedReceipt.values()) {
            BaseResultVo vo = new BaseResultVo();
            vo.setCode(needReceipt.getCode());
            vo.setDesc(needReceipt.getDescr());
            result.add(vo);
        }

        return result;
    }

    /**
     * 运单类型
     * 
     * @param returnDataKey
     *            决定返回哪些数据，为空返回全部 ALL：全部 SPECIAL_CAR：专车和分公司 LANDING:落地配
     */
    @ResponseBody
    @RequestMapping(value = "business/branch", method = RequestMethod.GET)
    public Object listBusinessBranch(String returnDataKey) {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        BusinessBranch[] branchs = Waybill.BusinessBranch.values();

        if ("SPECIAL_CAR".equalsIgnoreCase(returnDataKey)) {
            for (BusinessBranch businessBranch : branchs) {
                if (businessBranch.getCode() == 0 || businessBranch.getCode() == 1) {
                    BaseResultVo vo = new BaseResultVo();
                    vo.setCode(businessBranch.getCode());
                    vo.setDesc(businessBranch.getDescr());
                    result.add(vo);
                }
            }
        } else if ("LANDING".equalsIgnoreCase(returnDataKey)) {
            for (BusinessBranch businessBranch : branchs) {
                if (businessBranch.getCode() == 2 || businessBranch.getCode() == 3) {
                    BaseResultVo vo = new BaseResultVo();
                    vo.setCode(businessBranch.getCode());
                    vo.setDesc(businessBranch.getDescr());
                    result.add(vo);
                }
            }
        } else
            for (BusinessBranch businessBranch : branchs) {
                BaseResultVo vo = new BaseResultVo();
                vo.setCode(businessBranch.getCode());
                vo.setDesc(businessBranch.getDescr());
                result.add(vo);
            }

        return result;
    }

    /**
     * 报销费用类型
     */
    @ResponseBody
    @RequestMapping(value = "cost/reimbursed/type", method = RequestMethod.GET)
    public Object listCostReimbursedType() {
        return confParamService.findParamOptions(Constants.COST_REIMBURSED_TYPE);
    }

    /**
     * 报销费用审核结果
     */
    @ResponseBody
    @RequestMapping(value = "cost/audit/result", method = RequestMethod.GET)
    public Object listCostAuditResult() {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (AuditResult auditResult : CostReimbursed.AuditResult.values()) {
            BaseResultVo vo = new BaseResultVo();
            vo.setCode(auditResult.getCode());
            vo.setDesc(auditResult.getDesc());
            result.add(vo);
        }

        return result;
    }

    /**
     * 入城证
     */
    @ResponseBody
    @RequestMapping(value = "entry/license", method = RequestMethod.GET)
    public Object listEntryLicense() {
        return confParamService.findParamOptions(Constants.ENTRY_CITY_LICENSE_TYPE);
    }

    /**
     * 车辆状态
     */
    @ResponseBody
    @RequestMapping(value = "vehicle/status", method = RequestMethod.GET)
    public Object listVehicleStatus() {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (VehicleStatusEnum vehicleStatusEnum : VehicleStatusEnum.values()) {
            BaseResultVo vo = new BaseResultVo();
            vo.setCode(vehicleStatusEnum.getCode().intValue());
            vo.setDesc(vehicleStatusEnum.getDesc());
            result.add(vo);
        }

        return result;
    }

    /**
     * 用户类型
     */
    @ResponseBody
    @RequestMapping(value = "driver/type", method = RequestMethod.GET)
    public Object listDriverType() {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (DriverTypeEnum driverTypeEnum : DriverTypeEnum.values()) {
            BaseResultVo vo = new BaseResultVo();
            vo.setCode(driverTypeEnum.getCode());
            vo.setDesc(driverTypeEnum.getDesc());
            result.add(vo);
        }

        return result;
    }

    /**
     * 车辆类型
     */
    @ResponseBody
    @RequestMapping(value = "truck/type", method = RequestMethod.GET)
    public Object listTruckType() {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (TruckRunTypeEnum truckRunTypeEnum : TruckRunTypeEnum.values()) {
            if (truckRunTypeEnum.getCode().equals(TruckRunTypeEnum.NO_OWN_SALE.getCode())) {
                continue;
            }

            BaseResultVo vo = new BaseResultVo();
            vo.setCode(truckRunTypeEnum.getCode());
            vo.setDesc(truckRunTypeEnum.getDesc());
            result.add(vo);
        }

        return result;
    }

    /**
     * 司机停工状态
     */
    @ResponseBody
    @RequestMapping(value = "driver/status", method = RequestMethod.GET)
    public Object listDriverStatus() {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (DriverStatusEnum driverStatusEnum : DriverStatusEnum.values()) {
            BaseResultVo vo = new BaseResultVo();
            vo.setCode(driverStatusEnum.getCode().intValue());
            vo.setDesc(driverStatusEnum.getDesc());
            result.add(vo);
        }

        return result;
    }

    /**
     * 收款账户
     */
    @ResponseBody
    @RequestMapping(value = "receivable/account", method = RequestMethod.GET)
    public Object receivableAccount() {
        return confParamService.findParamOptions(Constants.RECEIVABLE_ACCOUNT_KEY);
    }

    /**
     * 税率
     */
    @ResponseBody
    @RequestMapping(value = "tax/rate", method = RequestMethod.GET)
    public Object listTaxRate(LoginEmployee loginEmployee) {
        return taxRateService.loadByTenant(loginEmployee);
    }

    /**
     * 货物类型
     */
    @ResponseBody
    @RequestMapping(value = "goods/type", method = RequestMethod.GET)
    public Object listGoodsType() {
        return confParamInfoService.goodsTypeList().getOptionList();
    }

    /**
     * 用车要求
     */
    @ResponseBody
    @RequestMapping(value = "additional/function", method = RequestMethod.GET)
    public Object listAdditionalFunction(String hidenCodes, LoginEmployee loginEmployee) {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        List<Integer> hidenCodeList = strsToList(hidenCodes);
        List<AdditionalFunctionBo> list = additionalFunctionService.getFunctionList();
        for (AdditionalFunctionBo function : list) {
            if (hidenCodeList.contains(function.getAdditionalFunctionId())) {
                continue;
            }

            Integer tenantId = loginEmployee.getTenantId();
            if (null != tenantId && tenantId.equals(2)) {
                // 专车
                if (function.getFunctionKey().equals(AdditionalFunction.FunctionKeys.CARRY.toString())) {
                    continue;
                }
                BaseResultVo vo = new BaseResultVo();
                vo.setCode(function.getAdditionalFunctionId());
                vo.setDesc(function.getFunctionName());
                result.add(vo);
            } else {
                // 非专车
//                if (function.getFunctionKey().equals(AdditionalFunction.FunctionKeys.DRIVER_CARRY.toString())
                ////                        || function.getFunctionKey().equals(AdditionalFunction.FunctionKeys.LABORER_CARRY.toString())) {
                ////                    continue;
                ////                }
                BaseResultVo vo = new BaseResultVo();
                vo.setCode(function.getAdditionalFunctionId());
                vo.setDesc(function.getFunctionName());
                result.add(vo);
            }

        }

        return result;
    }

    /**
     * 用车要求
     */
    @ResponseBody
    @RequestMapping(value = "additional/function/defaultChecked", method = RequestMethod.GET)
    public Object addFunctionDefaultChecked(LoginEmployee loginEmployee) {
        List<ConfParamOption> list = confParamService.findParamOptions(Constants.ADDFUNCTION_DEFAULT_CHECKED_KEY);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        for (ConfParamOption c : list) {
            if (Integer.valueOf(c.getOptionName()).equals(loginEmployee.getTenantId())) {
                return c.getOptionValue();
            }

        }

        return null;
    }

    /**
     * 运单取消原因
     */
    @ResponseBody
    @RequestMapping(value = "waybill/cancel/reason", method = RequestMethod.GET)
    public Object waybillCancelReason() {
        return confParamService.findParamOptions(Constants.WAYBILL_CANCEL_REASON_KEY);
    }

    /**
     * 司机类型
     */
    @ResponseBody
    @RequestMapping(value = "list/driverType", method = RequestMethod.GET)
    public Object driverTypes() {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (DriverTypeEnum driverTypeEnum : DriverTypeEnum.values()) {
            BaseResultVo vo = new BaseResultVo();
            vo.setCode(driverTypeEnum.getCode().intValue());
            vo.setDesc(driverTypeEnum.getDesc());
            result.add(vo);
        }

        return result;
    }

    /**
     * 运单各种状态
     */
    @ResponseBody
    @RequestMapping(value = "list/waybill/quick/query", method = RequestMethod.GET)
    public Object waybillQuickQuerys() {
        List<QuickQueryParamMeter> result = new ArrayList<QuickQueryParamMeter>();
        // 配送状态
        QuickQueryParamMeter statusViews = new QuickQueryParamMeter();
        statusViews.setKey(Constants.WaybillQuickQueryParameterEnum.STATUS_VIEW_KEY.toString().toLowerCase());
        statusViews.setTitle(Constants.WaybillQuickQueryParameterEnum.STATUS_VIEW_KEY.getName());
        statusViews.setVisible(false);
        for (StatusView view : Waybill.StatusView.values()) {
            if (view.getCode() == -2 || view.getCode() == 0 || view.getCode() == 4) {
                continue;
            }
            QuickQueryParamMeter views = new QuickQueryParamMeter();
            views.setTitle(view.getDescr());
            views.setValue(String.valueOf(view.getCode()));
            statusViews.getChildren().add(views);
        }
        result.add(statusViews);

        // 客户对账状态
        QuickQueryParamMeter receivableReconcilicationStatus = new QuickQueryParamMeter();
        receivableReconcilicationStatus
                .setKey(Constants.WaybillQuickQueryParameterEnum.RECEIVABLE_RECONCILICATION_STATUS_KEY.toString()
                        .toLowerCase());
        receivableReconcilicationStatus
                .setTitle(Constants.WaybillQuickQueryParameterEnum.RECEIVABLE_RECONCILICATION_STATUS_KEY.getName());
        receivableReconcilicationStatus.setVisible(false);
        for (ReconciliationStatus r : Waybill.ReconciliationStatus.values()) {
            if (r.getCode() == Waybill.ReconciliationStatus.INIT.getCode()) {
                continue;
            }
            QuickQueryParamMeter receivableReconcilication = new QuickQueryParamMeter();
            receivableReconcilication.setTitle(r.getDescr());
            receivableReconcilication.setValue(String.valueOf(r.getCode()));
            receivableReconcilicationStatus.getChildren().add(receivableReconcilication);
        }
        result.add(receivableReconcilicationStatus);

        // 客户收款状态
        QuickQueryParamMeter receiptStatus = new QuickQueryParamMeter();
        receiptStatus.setKey(Constants.WaybillQuickQueryParameterEnum.RECEIPT_STATUS_KEY.toString().toLowerCase());
        receiptStatus.setTitle(Constants.WaybillQuickQueryParameterEnum.RECEIPT_STATUS_KEY.getName());
        receiptStatus.setVisible(false);
        for (ReceiptStatus r : Waybill.ReceiptStatus.values()) {
            if (r.getCode() == Waybill.ReceiptStatus.INIT.getCode()
                    || r.getCode() == Waybill.ReceiptStatus.SEGMENT_COLLECTION.getCode()) {
                continue;
            }
            QuickQueryParamMeter receipt = new QuickQueryParamMeter();
            receipt.setTitle(r.getDesc());
            receipt.setValue(String.valueOf(r.getCode()));
            receiptStatus.getChildren().add(receipt);
        }
        result.add(receiptStatus);

        // 承运商对账状态
        QuickQueryParamMeter reconciliationStatus = new QuickQueryParamMeter();
        reconciliationStatus
                .setKey(Constants.WaybillQuickQueryParameterEnum.RECONCILIATION_STATUS_KEY.toString().toLowerCase());
        reconciliationStatus.setTitle(Constants.WaybillQuickQueryParameterEnum.RECONCILIATION_STATUS_KEY.getName());
        reconciliationStatus.setVisible(false);
        for (ReconciliationStatus r : Waybill.ReconciliationStatus.values()) {
            if (r.getCode() == Waybill.ReconciliationStatus.INIT.getCode()) {
                continue;
            }
            QuickQueryParamMeter reconciliation = new QuickQueryParamMeter();
            reconciliation.setTitle(r.getDescr());
            reconciliation.setValue(String.valueOf(r.getCode()));
            reconciliationStatus.getChildren().add(reconciliation);
        }
        result.add(reconciliationStatus);

        // 承运商结算状态
        QuickQueryParamMeter settlementStatus = new QuickQueryParamMeter();
        settlementStatus
                .setKey(Constants.WaybillQuickQueryParameterEnum.SETTLEMENT_STATUS_KEY.toString().toLowerCase());
        settlementStatus.setTitle(Constants.WaybillQuickQueryParameterEnum.SETTLEMENT_STATUS_KEY.getName());
        settlementStatus.setVisible(false);
        for (SettlementStatus s : Waybill.SettlementStatus.values()) {
            if (s.getCode() == SettlementStatus.INIT.getCode() || s.getCode() == Waybill.SettlementStatus.PREPARE_CLEAR.getCode()) {
                continue;
            }
            QuickQueryParamMeter settlement = new QuickQueryParamMeter();
            settlement.setTitle(s.getDesc());
            settlement.setValue(String.valueOf(s.getCode()));
            settlementStatus.getChildren().add(settlement);
        }
        result.add(settlementStatus);

        return result;
    }

    /**
     * 承运商客户关系
     */
    @ResponseBody
    @RequestMapping(value = "list/vendor", method = RequestMethod.POST)
    public Object listVendor(@RequestBody VendorMapping vendorMapping, boolean joinVendorNameAndCustomerName,
            LoginEmployee loginEmployee) {
        List<VendorMapping> result = vendorMappingService.listVendorMapping(vendorMapping.getVendorName(), 15,
                loginEmployee);
        if (!joinVendorNameAndCustomerName) {
            return result;
        }

        for (VendorMapping v : result) {
            if (StringUtils.isBlank(v.getVendorCustomerName())) {
                continue;
            }
            v.setVendorName(v.getVendorName() + "(" + v.getVendorCustomerName() + ")");
        }
        return result;
    }

    /**
     * 与承运商客户结算方式
     */
    @ResponseBody
    @RequestMapping(value = "list/transformCarrierSettlementType", method = RequestMethod.GET)
    public Object listTransformCarrierSettlementType() {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (TransformCarrierSettlementType t : WaybillCarrierVo.TransformCarrierSettlementType.values()) {
            BaseResultVo vo = new BaseResultVo();
            vo.setCode(t.getCode());
            vo.setDesc(t.getDesc());
            result.add(vo);
        }
        return result;
    }

    /**
     * 计价方式
     */
    @ResponseBody
    @RequestMapping(value = "list/valuationWay", method = RequestMethod.GET)
    public Object valuationWays() {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (ValuationWayEnum v : ValuationWayEnum.values()) {
            BaseResultVo vo = new BaseResultVo();
            vo.setCode(v.getCode());
            vo.setDesc(v.getDesc());
            result.add(vo);
        }
        return result;
    }

    @ApiOperation("承运商列表，区分租户")
    @ApiImplicitParams({ @ApiImplicitParam(name = "vendorRequest.vendorName", value = "承运商名称", dataType = "String"),
            @ApiImplicitParam(name = "vendorRequest.areaCode", value = "业务区域", dataType = "String"),
            @ApiImplicitParam(name = "vendorRequest.backPageSize", value = "返回数据数量,默认15条", dataType = "Integer", defaultValue = "15") })
    @ResponseBody
    @RequestMapping(value = "list/vms/vendor", method = RequestMethod.POST)
    public Object listVmsVendor(@RequestBody VendorRequest vendorRequest, LoginEmployee loginEmployee) {
        Integer backPageSize = vendorRequest.getBackPageSize() == null ? 15 : vendorRequest.getBackPageSize();
        return vmsService.listVendorByVendorNameLike(vendorRequest.getVendorName(), vendorRequest.getAreaCode(),
                backPageSize, loginEmployee);
    }

    @ApiOperation("路线列表，根据项目ID获取")
    @ApiImplicitParams({ @ApiImplicitParam(name = "roadMap.name", value = "路线名称", dataType = "String"),
            @ApiImplicitParam(name = "roadMap.projectId", value = "项目ID,没有项目ID返回空数据", dataType = "Integer"),
            @ApiImplicitParam(name = "backPageSize", value = "返回数据数量,默认15条,拼接到URL后面", dataType = "Integer", defaultValue = "15") })
    @ResponseBody
    @RequestMapping(value = "list/roadMap", method = RequestMethod.POST)
    public Object listRoadMapName(@RequestBody RoadMap roadMap, Integer backPageSize) {
        if (null == roadMap.getProjectId()) {
            return new ArrayList<RoadMap>();
        }
        return roadMapService.listBylikeName(roadMap.getProjectId(), roadMap.getName());
    }

    @ApiOperation("获取当前客户下的子公司信息与所有的物流标签")
    @ResponseBody
    @RequestMapping(value = "load/{customerId}/departmentAndlogisticsProduct", method = RequestMethod.GET)
    public Object loadDepartmentAndLogisticsProduct(@PathVariable Integer customerId,  LoginEmployee loginEmployee) {
        CustomerForProductAndDept customerForProductAndDept =
                crmCommonService.loadCustomerForProductAndDeptByCustomerId(customerId, loginEmployee);
        if (null == customerForProductAndDept || null == customerForProductAndDept.getDeparmentId()) {
            return null;
        }

        DepartmentCompany department = eCompanyService.findOperationECompanyByDepartment(customerForProductAndDept.getDeparmentId());
        if (null == department) {
            return customerForProductAndDept;
        }

        customerForProductAndDept.setBusinessLicenceName(department.getCompanyName());
        return customerForProductAndDept;
    }

    @ApiOperation("获取当前租户下所有的物流标签")
    @ResponseBody
    @RequestMapping(value = "list/logisticsProduct", method = RequestMethod.GET)
    public Object listLogisticsProduct(LoginEmployee loginEmployee) {
        return crmCommonService.listLogisticsProduct(loginEmployee);
    }

    @ApiOperation("获取当前客户下所有的物流标签")
    @ApiImplicitParam(paramType = "path", name = "customerId", value = "tgm的客户ID")
    @ResponseBody
    @RequestMapping(value = "list/{customerId}/logisticsProduct", method = RequestMethod.GET)
    public Object listLogisticsProductByCustomerId(@PathVariable Integer customerId, LoginEmployee loginEmployee) {
        return crmCommonService.listLogisticsProductByCustomerId(customerId, loginEmployee);
    }

    @ApiOperation(value = "判断该租户下是不是展示物流标签", notes = "true:需要展示；false:不需要展示")
    @ResponseBody
    @RequestMapping(value = "isShowLogisticsProduct", method = RequestMethod.GET)
    public boolean isShowLogisticsProduct(LoginEmployee loginEmployee) {
        return crmCommonService.isShowLogisticsProduct(loginEmployee);
    }

    @ApiOperation(value = "根据部门ID获取部门信息")
    @ResponseBody
    @RequestMapping(value = "{departmentId}/loadDepartment", method = RequestMethod.GET)
    public Department loadDepartment(@PathVariable Integer departmentId, LoginEmployee loginEmployee) {
        if (null == departmentId) {
            return null;
        }
        return departmentService.loadDepartment(departmentId, loginEmployee);
    }

    @ApiOperation(value = "根据子公司名称获取子公司列表")
    @ResponseBody
    @RequestMapping(value = "listSubCompany", method = RequestMethod.POST)
    public List<CustomerForProductAndDept> listSubCompany(@RequestBody CustomerForProductAndDept customerForProductAndDept,
                                                          Integer callbackPageSize, LoginEmployee loginEmployee) {
        return crmCommonService.listSubCompanyLikeName(customerForProductAndDept.getBusinessLicenceName(),
                callbackPageSize, loginEmployee);
    }

    /**
     * 项目状态
     */
    @ApiOperation(value = "项目状态列表")
    @ResponseBody
    @RequestMapping(value = "list/projectStatus", method = RequestMethod.GET)
    public Object listProjectStatus() {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (ProjectEnum.ProjectStatus p : ProjectEnum.ProjectStatus.values()) {
            BaseResultVo vo = new BaseResultVo();
            vo.setCode(p.getCode());
            vo.setDesc(p.getDescr());
            result.add(vo);
        }

        return result;
    }

    /**
     * 项目运作类型
     */
    @ApiOperation(value = "项目运作类型列表")
    @ResponseBody
    @RequestMapping(value = "list/projectType", method = RequestMethod.GET)
    public Object listProjectType() {
        List<BaseResultVo> result = new ArrayList<BaseResultVo>();
        for (ProjectEnum.ProjectType p : ProjectEnum.ProjectType.values()) {
            BaseResultVo vo = new BaseResultVo();
            vo.setCode(p.getCode());
            vo.setDesc(p.getDescr());
            result.add(vo);
        }

        return result;
    }

    @ApiOperation(value = "根据TMS的客户ID和合同名称获取合同信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "custoemrId", value = "TMS的客户ID，必传", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "contractName", value = "合同名称，非必传", dataType = "String"),
            @ApiImplicitParam(name = "backPageSize", value = "返回条数，默认15，最大200，非必传", dataType = "Integer")
    })
    @ResponseBody
    @RequestMapping(value = "list/conctract", method = RequestMethod.POST)
    public List<ContractVo> listConctract(@RequestBody ContractFilter contractFilter,
                                           LoginEmployee loginEmployee) {
        return crmCommonService.listContractByContractFilter(contractFilter, loginEmployee);
    }

    @ApiOperation(value = "根据司机姓名或手机号获取司机信息", notes = "callbackPageSize返回条数，默认15，最大200，非必传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "司机姓名"),
            @ApiImplicitParam(name = "phone", value = "司机电话", dataType = "String"),
    })
    @ResponseBody
    @RequestMapping(value = "list/driver", method = RequestMethod.POST)
    public List<Driver> listdrviver(@RequestBody DriverExternalFilter driverExternalFilter, Integer callbackPageSize,
                                      LoginEmployee loginEmployee) {
        return vmsCommonService.listDriverBy(driverExternalFilter, callbackPageSize, loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "businessArea/{parentBusinessAreaId}/children", method = RequestMethod.GET)
    public List<BusinessArea> childrenBusinessArea(@PathVariable Integer parentBusinessAreaId, LoginEmployee loginEmployee) {
        return businessAreaCommonService.listChildBusinessArea(parentBusinessAreaId, loginEmployee);
    }

    @ApiOperation(value = "获取运单改价时间限制")
    @ResponseBody
    @RequestMapping(value = "load/updatePriceTimeLimit", method = RequestMethod.GET)
    public Integer loadUpdatePriceTimeLimit() {
        List<ConfParamOption> options = authCommonService.listOption(Constants.ALLOW_CHANGE_PRICE_TIME_LIMIT_KEY);
        for (ConfParamOption option : options) {
            if (StringUtils.isNotBlank(option.getOptionValue()) && StringUtils.isNumeric(option.getOptionValue())) {
                return Integer.parseInt(option.getOptionValue());
            }

        }
        return Constants.ALLOW_CHANGE_PRICE_DEFAULT_TIME_LIMIT;
    }

    @ApiOperation(value = "根据部门（公司）ID获取企业信用代码")
    @ResponseBody
    @RequestMapping(value = "load/{departmentId}/uniformSocialCreditCode", method = RequestMethod.GET)
    public DepartmentCompany loadUpdatePriceTimeLimit(@PathVariable Integer departmentId) {
        return authCommonService.findDepartmentCompanyByDepartmentId(departmentId);
    }
}
