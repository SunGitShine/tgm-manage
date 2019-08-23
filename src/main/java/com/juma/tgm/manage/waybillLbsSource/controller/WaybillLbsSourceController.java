package com.juma.tgm.manage.waybillLbsSource.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.server.vm.domain.Driver;
import com.juma.tgm.cms.domain.ExportTask;
import com.juma.tgm.cms.service.ExportTaskService;
import com.juma.tgm.driver.domain.ReportInfoDetails;
import com.juma.tgm.export.domain.ExportParam;
import com.juma.tgm.manage.waybillLbsSource.vo.WaybillLbsSourceVo;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.reportInfo.service.ReportInfoDetailService;
import com.juma.tgm.tools.service.AmsCommonService;
import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillDeliveryAddress;
import com.juma.tgm.waybill.domain.WaybillMap;
import com.juma.tgm.waybill.service.WaybillDeliveryAddressService;
import com.juma.tgm.waybill.service.WaybillQueryService;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.tgm.waybillLbsSource.domain.WaybillLbsSource;
import com.juma.tgm.waybillLbsSource.domain.WaybillLbsSourceQuery;
import com.juma.tgm.waybillLbsSource.service.WaybillLbsSourceService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName WaybillLbsSourceController.java
 * @Description 装货迟到
 * @author Libin.Wei
 * @Date 2017年6月20日 上午10:45:02
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("waybill/lbs/source")
public class WaybillLbsSourceController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(WaybillLbsSourceController.class);
    @Resource
    private WaybillLbsSourceService waybillLbsSourceService;
    @Resource
    private WaybillService waybillService;
    @Resource
    private ReportInfoDetailService reportInfoDetailService;
    @Resource
    private WaybillDeliveryAddressService waybillDeliveryAddressService;
    @Resource
    private WaybillQueryService waybillQueryService;
    @Resource
    private ExportTaskService exportTaskService;
    @Resource
    private AmsCommonService amsCommonService;
    @Resource
    private VmsCommonService vmsCommonService;

    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<WaybillLbsSourceQuery> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        this.formatAreaCodeToList(pageCondition, true);
        pageCondition.getFilters().put("sign", WaybillLbsSource.Sign.DRIVER_LATE.getCode());
        // 只可见本业务区域的非分享运单和其他业务区域的分享运单，不可见本业务区域的分享运单
        pageCondition.getFilters().put("ownerAreaSahreCanNotSee", true);
        return waybillLbsSourceService.search(pageCondition, loginEmployee);
    }

    /**
     * 获取有坐标的迟到详情
     */
    @ResponseBody
    @RequestMapping(value = "have/coordinate/{waybillLbsSourceId}/detailList", method = RequestMethod.GET)
    public WaybillLbsSourceVo getDetailList(@PathVariable Integer waybillLbsSourceId, LoginEmployee loginEmployee) {
        WaybillLbsSourceVo waybillLbsSourceVo = new WaybillLbsSourceVo();
        List<ReportInfoDetails> result = new ArrayList<ReportInfoDetails>();
        WaybillLbsSource waybillLbsSource = waybillLbsSourceService.getWaybillLbsSource(waybillLbsSourceId);
        Waybill waybill = waybillService.getWaybill(waybillLbsSource.getWaybillId());
        if (null != waybill) {
            WaybillMap waybillMap = waybillQueryService.findWaybillMapById(waybill.getWaybillId());
            waybillLbsSourceVo.setFenceArriveDepotTime(waybillMap.getFenceArriveDepotTime());
            waybillLbsSourceVo.setArriveDepotTime(waybillMap.getArriveDepotTime());
            waybillLbsSourceVo.setCustomerManagerName(waybillMap.getCustomerManagerName());
            waybillLbsSourceVo.setCustomerName(waybillMap.getCustomerName());
            waybillLbsSourceVo.setDriverName(waybillMap.getDriverName());
            waybillLbsSourceVo.setWaybillNo(waybillMap.getWaybillNo());
            waybillLbsSourceVo.setPlanDeliveryTime(waybillMap.getPlanDeliveryTime());
            if (null != waybillLbsSource.getTimeConsuming()) {
                BigDecimal divide = new BigDecimal(waybillLbsSource.getTimeConsuming()).divide(new BigDecimal("3600"),
                        2, BigDecimal.ROUND_HALF_UP);
                waybillLbsSourceVo.setTimeConsuming(divide == null ? null : divide.toString());
            }
            // 车辆停放地
            ReportInfoDetails parkingInfo = buildParkingInfo(waybillMap, loginEmployee);
            if (null != parkingInfo) {
                result.add(parkingInfo);
            }
            // 取货地信息
            ReportInfoDetails deliveryAddressInfo = buildDeliveryAddressInfo(waybillMap);
            if (null != deliveryAddressInfo) {
                waybillLbsSourceVo.setDeliveryAddress(deliveryAddressInfo.getAddressDetail());
                result.add(deliveryAddressInfo);
            }
            // 签到地信息
            ReportInfoDetails signInfo = buildSignInfo(waybillLbsSourceId);
            if (null != signInfo) {
                result.add(signInfo);
                waybillLbsSourceVo.setSignInfoAddress(signInfo.getAddressDetail());
            }
            List<ReportInfoDetails> detailList = reportInfoDetailService.listByWaybillId(waybill.getWaybillId(), "asc");
            // 模拟数据
            // List<ReportInfoDetails> detailList =
            // reportInfoDetailService.listByWaybillId(3885);
            // 计数器
            int temp = 0;
            if (detailList != null) {
                // 获取对应的报备点集合。
                for (ReportInfoDetails reportInfoDetails : detailList) {
                    temp++;
                    reportInfoDetails.setTitle("报" + temp);
                    reportInfoDetails
                            .setLabelPointType(ReportInfoDetails.LabelPointType.ROAD_CONDITION_REPORT.toString());
                    result.add(reportInfoDetails);
                }
            }
        }
        waybillLbsSourceVo.setDetailList(result);
        return waybillLbsSourceVo;
    }

    // 车辆停放地信息
    private ReportInfoDetails buildParkingInfo(WaybillMap waybillMap, LoginEmployee loginEmployee) {
        String plateNumber = waybillMap.getPlateNumber();
        if (StringUtils.isBlank(plateNumber)) {
            return null;
        }

        com.juma.vms.driver.domain.Driver driver = vmsCommonService.loadDriverByPlateNumber(plateNumber, loginEmployee);
        if (null == driver) {
            return null;
        }

        Driver amsDriver = amsCommonService.findDriver(driver.getAmsDriverId(), loginEmployee);
        if (null == amsDriver) {
            return null;
        }

        ReportInfoDetails detail = new ReportInfoDetails();
        if (null != amsDriver.getLongitude() && null != amsDriver.getLatitude()) {
            detail.setCoordinate(amsDriver.getLongitude() + "," + amsDriver.getLatitude());
        }
        detail.setAddressDetail(StringUtils.isBlank(amsDriver.getParkAddress()) ? "暂无" : amsDriver.getParkAddress());
        detail.setReportTime(driver.getLastUpdateTime() == null ? driver.getCreateTime() : driver.getLastUpdateTime());
        detail.setLabelPointType(ReportInfoDetails.LabelPointType.TRUCK_PARKING.toString());
        detail.setTitle("停");
        return detail;
    }

    // 取货地信息
    private ReportInfoDetails buildDeliveryAddressInfo(WaybillMap waybillMap) {
        WaybillDeliveryAddress deliveryAddress = waybillDeliveryAddressService
                .findByWaybillId(waybillMap.getWaybillId());
        if (null == deliveryAddress) {
            return null;
        }

        ReportInfoDetails detail = new ReportInfoDetails();
        detail.setCoordinate(deliveryAddress.getCoordinates());
        detail.setAddressDetail(
                StringUtils.isBlank(deliveryAddress.getAddressDetail()) ? "暂无" : deliveryAddress.getAddressDetail());
        detail.setReportTime(deliveryAddress.getCreateTime());
        detail.setLabelPointType(ReportInfoDetails.LabelPointType.DELIVERY_ADDRESS.toString());
        detail.setTitle("取");
        return detail;
    }

    // 签到点信息
    private ReportInfoDetails buildSignInfo(Integer waybillLbsSourceId) {
        WaybillLbsSource waybillLbsSource = waybillLbsSourceService.getWaybillLbsSource(waybillLbsSourceId);
        if (null == waybillLbsSource) {
            return null;
        }
        ReportInfoDetails reportInfoDetails = new ReportInfoDetails();
        reportInfoDetails.setCoordinate(waybillLbsSource.getCoordinate());
        reportInfoDetails.setAddressDetail(
                StringUtils.isBlank(waybillLbsSource.getAddress()) ? "暂无" : waybillLbsSource.getAddress());
        reportInfoDetails.setReportTime(waybillLbsSource.getCreateTime());
        reportInfoDetails.setLabelPointType(ReportInfoDetails.LabelPointType.SIGN_INFO.toString());
        reportInfoDetails.setTitle("签");
        return reportInfoDetails;
    }

    // 计算迟到距离
    /*
     * private Integer distanceByCoordinate(String startcoordinate,String
     * endCoordinate){
     * 
     * DistanceAndPriceData distanceAndPriceData =
     * gaoDeMapService.getDistanceSimple(startcoordinate,endCoordinate); return
     * distanceAndPriceData.getDistance(); }
     */

    /**
     * excel导出
     */
    @ResponseBody
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public void export(@RequestBody ExportParam exportParam, LoginEmployee loginEmployee) {
        // 初始化任务
        Integer exportTaskId = exportTaskService.insertInit(ExportTask.TaskSign.DRIVER_LATE, exportParam,
                loginEmployee);
        try {
            // 获取数据并上传云
            PageCondition pageCondition = new PageCondition();
            pageCondition.setPageNo(1);
            pageCondition.setPageSize(Integer.MAX_VALUE);
            pageCondition.setFilters(exportParam.getFilters());
            super.formatAreaCodeToList(pageCondition, true);
            pageCondition.getFilters().put("sign", WaybillLbsSource.Sign.DRIVER_LATE.getCode());
            waybillLbsSourceService.asyncExport(pageCondition, exportTaskId, loginEmployee);
        } catch (Exception e) {
            exportTaskService.failed(exportTaskId, e.getMessage(), loginEmployee);
            log.error(e.getMessage(), e);
        }
    }
}
