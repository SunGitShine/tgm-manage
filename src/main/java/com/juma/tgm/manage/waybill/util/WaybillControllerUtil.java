package com.juma.tgm.manage.waybill.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.giants.common.exception.BusinessException;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginUser;
import com.juma.tgm.common.Constants;
import com.juma.tgm.common.Constants.WaybillQuickQueryParameterEnum;
import com.juma.tgm.common.FreightEnum;
import com.juma.tgm.configure.domain.FreightFactor;
import com.juma.tgm.configure.service.FreightFactorService;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.manage.waybill.vo.ValuationDetailVo;
import com.juma.tgm.project.domain.Project;
import com.juma.tgm.project.domain.ProjectFreightRule;
import com.juma.tgm.project.enumeration.ValuationWayEnum;
import com.juma.tgm.project.service.ProjectService;
import com.juma.tgm.scatteredWaybill.service.ScatteredWaybillService;
import com.juma.tgm.vendor.domain.VendorProjectMapping;
import com.juma.tgm.vendor.service.VendorMappingService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillBo;
import com.juma.tgm.waybill.domain.vo.WaybillCarrierVo;

/**
 * @ClassName: WaybillControllerUtil
 * @Description:
 * @author: liang
 * @date: 2018-03-28 15:35
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
@Component
public class WaybillControllerUtil {

    private final static Logger log = LoggerFactory.getLogger(WaybillControllerUtil.class);
    @Resource
    private CustomerInfoService customerInfoService;

    @Resource
    private ProjectService projectService;

    @Resource
    private FreightFactorService freightFactorService;

    @Resource
    private VendorMappingService vendorMappingService;

    @Resource
    private ScatteredWaybillService scatteredWaybillService;

    /**
     * 预计完成时间不能早于计划用车时间
     *
     * @param waybillBo
     */
    public void planEstimateFinishTimeCheck(WaybillBo waybillBo) {
        try {
            if (DateUtils.truncatedCompareTo(waybillBo.getWaybill().getPlanDeliveryTime(),
                    waybillBo.getWaybill().getCmEstimateFinishTime(), Calendar.MINUTE) >= 0) {
                throw new BusinessException("estimateFinishTimeEarly", "waybill.error.estimate.finish.time.early");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * 转换pagecondition中areaCodeList字符串到List对象
     *
     * @param pageCondition
     */
    public void formatAreaCodeToList(PageCondition pageCondition, boolean removeAreaCode00) {
        if (pageCondition == null) {
            return;
        }

        Map<String, Object> filter = pageCondition.getFilters();
        this.checkDateNaN(filter);
        pageCondition.setFilters(handleAreaCode(filter, removeAreaCode00));

        // 运单状态的处理
        this.handleWaybillQuickQueryStatus(filter);
    }

    public Map<String, Object> handleAreaCode(Map<String, Object> filter, boolean removeAreaCode00) {
        if (filter == null) {
            filter = new HashMap<String, Object>();
            List<String> target = new ArrayList<String>();
            target.add("-999");
            filter.put("areaCodeList", target);
            return filter;
        }

        if (filter.get("areaCodeList") == null) {
            List<String> target = new ArrayList<String>();
            target.add("-999");
            filter.put("areaCodeList", target);
            return filter;
        }

        String str = filter.get("areaCodeList").toString();
        List<String> target = this.splitStringByComma(str);
        if (target == null) {
            target = new ArrayList<String>();
            target.add("-999");
            filter.put("areaCodeList", target);
            return filter;
        }

        if (!removeAreaCode00) {
            filter.put("areaCodeList", target);
            return filter;
        }

        // target是Arrays.asList生成的，不能使用remove方法
        List<String> arrList = new ArrayList<String>(target);
        // 去掉业务范围全国00
        if (arrList.contains("00")) {
            arrList.remove("00");
        }

        // 若业务范围只包含全国，则不使用业务范围条件
        if (arrList.isEmpty()) {
            filter.remove("areaCodeList");
        } else {
            filter.put("areaCodeList", arrList);
        }

        return filter;
    }

    // 校验时间是否正确
    private void checkDateNaN(Map<String, Object> filter) {
        if (null == filter) {
            return;
        }

        if ((null == filter.get("startTime") && null != filter.get("endTime"))
                || (null != filter.get("startTime") && null == filter.get("endTime"))) {
            throw new BusinessException("startTimeAndEndTimeCoexistence", "errors.startTimeAndEndTimeCoexistence");
        }

        if (null == filter.get("startTime") && null == filter.get("endTime")) {
            return;
        }

        String startTime = filter.get("startTime").toString();
        String endTime = filter.get("endTime").toString();
        if (startTime.contains("NaN") || startTime.contains("aN") || endTime.contains("NaN")
                || endTime.contains("aN")) {
            throw new BusinessException("timeParseException", "errors.timeParseException");
        }
    }

    // 处理运单快捷查询条件
    protected void handleWaybillQuickQueryStatus(Map<String, Object> filter) {
        if (null == filter) {
            return;
        }

        for (WaybillQuickQueryParameterEnum p : Constants.WaybillQuickQueryParameterEnum.values()) {
            String lowerCase = p.toString().toLowerCase();
            if (null != filter.get(lowerCase)) {
                List<Integer> list = new ArrayList<Integer>();
                this.handleList(list, filter.get(lowerCase).toString());
                if (list.isEmpty()) {
                    filter.remove(lowerCase);
                    continue;
                }

                // 配送状态为待配送是有两种status_view in (-2, 2)
                if (Constants.WaybillQuickQueryParameterEnum.STATUS_VIEW_KEY.toString().toLowerCase().equals(lowerCase)
                        && list.contains(Waybill.StatusView.WATING_DELIVERY.getCode())) {
                    list.add(Waybill.StatusView.TEMP.getCode());
                }
                filter.put(p.getKey(), list);
            }
        }
    }

    // 字符串转为list集合
    private void handleList(List<Integer> list, String str) {
        if (StringUtils.isBlank(str)) {
            return;
        }

        if (!str.contains(",")) {
            list.add(Integer.parseInt(str));
            return;
        }

        String[] split = str.split(",");
        for (String string : split) {
            if (StringUtils.isBlank(string)) {
                continue;
            }
            list.add(Integer.parseInt(string));
        }
    }

    /**
     * 逗号分隔的字符串转String List
     *
     * @param targetStr
     * @return
     */
    public List<String> splitStringByComma(String targetStr) {

        if (StringUtils.isBlank(targetStr)) {
            return null;
        }

        return Arrays.asList(StringUtils.split(targetStr, ","));

    }

    // 验证是否选择了结算方式、支不支持项目结算
    public void checkIsProjectCheckout(Waybill waybill) {
        if (null != waybill.getProjectId()) {
            return;
        }

        if (null == waybill.getReceiptType()) {
            throw new BusinessException("canNotBeBlank", "errors.validation.canNotBeBlank", "结算方式");
        }

        // 判断结算方式,只判断项目结算方式的运单
        if (NumberUtils.compare(Waybill.ReceiptType.PROJECTPAY.getCode(), waybill.getReceiptType()) != 0) {
            return;
        }

        CustomerInfo customerInfo = customerInfoService.findCusInfoById(waybill.getCustomerId());
        if (null != customerInfo && !customerInfo.getIsProjectCheckOut()) {
            throw new BusinessException("canNotProjectCheckOut", "errors.canNotProjectCheckOut");
        }
    }

    /**
     * 项目运单计价详情
     */
    public ValuationDetailVo loadValuationDetail(Integer projectId, Integer truckTypeId, String projectFreightRuleJson,
            LoginEmployee loginEmployee) {
        ValuationDetailVo vo = new ValuationDetailVo();

        String factorJson = null;
        Integer valuationWay = ValuationWayEnum.WORKLOAD.getCode();

        if (StringUtils.isNotBlank(projectFreightRuleJson)) {
            ProjectFreightRule projectFreightRule = JSON.parseObject(projectFreightRuleJson, ProjectFreightRule.class);
            if (null != projectFreightRule) {
                valuationWay = projectFreightRule.getValuationWay() == null ? ValuationWayEnum.WORKLOAD.getCode()
                        : projectFreightRule.getValuationWay();
                factorJson = projectFreightRule.getFactorJson();
            }
        }
        vo.setValuationWay(valuationWay);

        if (StringUtils.isBlank(factorJson)) {
            return vo;
        }

        // 计费方式
        List<FreightFactor> freightFactors = freightFactorService.findByFreightWay(FreightEnum.PROJECT.getCode(),
                loginEmployee);
        if (freightFactors.isEmpty() && NumberUtils.compare(valuationWay, ValuationWayEnum.WORKLOAD.getCode()) == 0) {
            return vo;
        }

        Map<String, FreightFactor> factorMap = new HashMap<String, FreightFactor>();
        for (FreightFactor factor : freightFactors) {
            factorMap.put(factor.getLabelInputName(), factor);
        }

        Map<String, Object> map = JSON.parseObject(factorJson, Map.class);

        String unitPriceText = "";
        String startPriceText = "";

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (NumberUtils.compare(valuationWay, ValuationWayEnum.FIXED_PRICE.getCode()) == 0) {
                startPriceText = ValuationWayEnum.FIXED_PRICE.getDesc();
                vo.setValuationConstView(ValuationWayEnum.FIXED_PRICE.getDesc());
                vo.setValuationWayView(entry.getValue() + "元");
                return vo;
            }

            FreightFactor freightFactor = factorMap.get(entry.getKey());
            if (null == freightFactor) {
                continue;
            }
            String labelName = freightFactor.getLabelName();
            if (labelName.startsWith("起步")) {
                startPriceText = labelName + entry.getValue() + "元";
            } else {
                if (null != entry.getValue()) {
                    unitPriceText += (StringUtils.isBlank(unitPriceText) ? "" : " + ") + entry.getValue() + "元/"
                            + labelName;
                }
            }
        }

        if (StringUtils.isBlank(unitPriceText)) {
            vo.setValuationConstView(startPriceText);
            return vo;
        } else if (StringUtils.isBlank(startPriceText)) {
            vo.setValuationConstView(unitPriceText);
            return vo;
        } else {
            vo.setValuationConstView(startPriceText + " + " + unitPriceText);
            return vo;
        }
    }

    // 组装承运商的项目ID
    public WaybillCarrierVo buildWaybillCarrierVo(WaybillCarrierVo vo, Integer transformProjectId, LoginUser loginUser)
            throws BusinessException {
        if (null == vo) {
            return vo;
        }

        // 校验费率0-1
        if (null != vo.getVendorFeeRate()) {
            if (BigDecimal.ZERO.compareTo(vo.getVendorFeeRate()) == 1) {
                throw new BusinessException("vendorFeeRateMustNotLessThanZero",
                        "waybillCarrier.error.vendorFeeRateMustNotLessThanZero");
            }

            if (BigDecimal.ONE.compareTo(vo.getVendorFeeRate()) == -1) {
                throw new BusinessException("vendorFeeRateMustNotGreaterThanOneHundred",
                        "waybillCarrier.error.vendorFeeRateMustNotGreaterThanOneHundred");
            }
        }

        if (null == transformProjectId) {
            transformProjectId = -1;
        }

        VendorProjectMapping projectMapping = vendorMappingService.findVendorProjectMappingBy(vo.getVendorId(),
                vo.getCustomerId(), transformProjectId, loginUser);
        if (null == projectMapping || null == projectMapping.getVendorProjectId()) {
            return vo;
        }

        Project project = projectService.getProject(projectMapping.getVendorProjectId());
        if (null == project) {
            return vo;
        }

        vo.setProjectId(project.getProjectId());
        vo.setRebateRate(project.getRebateRate());
        vo.setBillTaxRate(project.getTaxRateValue());
        return vo;
    }

}
