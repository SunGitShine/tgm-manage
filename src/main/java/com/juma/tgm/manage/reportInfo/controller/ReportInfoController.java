package com.juma.tgm.manage.reportInfo.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.conf.domain.ConfParamOption;
import com.juma.conf.service.ConfParamService;
import com.juma.tgm.common.Constants;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.driver.domain.ReportInfo;
import com.juma.tgm.driver.domain.ReportInfoBo;
import com.juma.tgm.driver.domain.ReportInfoDetails;
import com.juma.tgm.manage.reportInfo.vo.ReportInfoVO;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.reportInfo.service.ReportInfoDetailService;
import com.juma.tgm.reportInfo.service.ReportInfoService;
import com.juma.tgm.tools.service.AmsCommonService;
import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillBo;
import com.juma.tgm.waybill.domain.WaybillDeliveryAddress;
import com.juma.tgm.waybill.service.WaybillDeliveryAddressService;
import com.juma.tgm.waybill.service.WaybillService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Libin.Wei
 * @version 1.0.0
 * @ClassName RoadConditionReportController.java
 * @Description 路况报备
 * @Date 2017年5月4日 上午10:50:58
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("report/info")
public class ReportInfoController extends BaseController {

    @Resource
    private ReportInfoService reportInfoService;
    @Resource
    private ReportInfoDetailService reportInfoDetailService;
    @Resource
    private WaybillDeliveryAddressService waybillDeliveryAddressService;
    @Resource
    private WaybillService waybillService;
    @Resource
    private ConfParamService confParamService;
    @Resource
    private AmsCommonService amsCommonService;
    @Resource
    private VmsCommonService vmsCommonService;

    /**
     * 分页列表
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<ReportInfoVO> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        List<ReportInfoVO> result = new ArrayList<ReportInfoVO>();
        super.formatAreaCodeToList(pageCondition, false);
        Page<ReportInfoBo> page = reportInfoService.search(pageCondition, loginEmployee);
        List<ConfParamOption> reportTypes = this.doGetReportTypes();
        ReportInfoVO vo = null;
        for (ReportInfoBo bo : page.getResults()) {
            vo = buildReportInfoVO(bo);
            vo.setReportTypes(reportTypes);
            vo.setCustomerInfo(bo.getCustomerInfo());
            vo.setDriver(bo.getDriver());
            vo.setShowDriverAndTruckDetail(this.showDriverAndTruckDetail(bo.getReportInfo().getWaybillId()));
            result.add(vo);
        }
        return new Page<ReportInfoVO>(page.getPageNo(), page.getPageSize(), page.getTotal(), result);
    }

    // 是否显示车辆司机详情
    private boolean showDriverAndTruckDetail(Integer waybillId) {
        Waybill waybill = waybillService.getWaybill(waybillId);
        if (null == waybill) {
            return true;
        }
        
        if (waybill.getReceiveWay() == Waybill.ReceiveWay.TRANSFORM_BILL.getCode()) {
            return false;
        }
        return true;
    }

    /**
     * 获取有坐标的路况报备详情
     */
    @ResponseBody
    @RequestMapping(value = "have/coordinate/{reportInfoId}/detailList", method = RequestMethod.GET)
    public List<ReportInfoDetails> getDetailList(@PathVariable Integer reportInfoId, LoginEmployee loginEmployee) {
        List<ReportInfoDetails> result = new ArrayList<ReportInfoDetails>();
        // 根据路况报备ID获取所有的详情
        //List<ReportInfoDetails> detailList = reportInfoDetailService.ListByReportId(reportInfoId);
        // 计数器
        int temp = 0;
        ReportInfo report = reportInfoService.getReportInfo(reportInfoId);
        if (null == report) {
            return result;
        }
        // 取货地信息
        ReportInfoDetails deliveryAddressInfo = buildDeliveryAddressInfo(report);
        if (null != deliveryAddressInfo) {
            result.add(deliveryAddressInfo);
        }

        // 车辆停放地信息
        ReportInfoDetails parkingInfo = buildParkingInfo(report, loginEmployee);
        if (null != parkingInfo) {
            result.add(parkingInfo);
        }
        //报备类型
        List<ConfParamOption> reportTypes = confParamService.findParamOptions(Constants.REPORT_INFO_TYPE_KEY);
        Waybill waybill = waybillService.getWaybill(report.getWaybillId());
        List<ReportInfoDetails> detailList = reportInfoDetailService.listByWaybillId(waybill.getWaybillId(), "asc");
        for (ReportInfoDetails detail : detailList) {
            temp += 1;
            detail.setLabelPointType(ReportInfoDetails.LabelPointType.ROAD_CONDITION_REPORT.toString());
            detail.setTitle("报" + temp);
            detail.setAllReportTypes(reportTypes);
            result.add(detail);

        }
        // 报备信息
        /*ReportInfo report = reportInfoService.getReportInfo(reportInfoId);
        if (null == report) {
            return result;
        }*/
        return result;
    }

    // 取货地信息
    private ReportInfoDetails buildDeliveryAddressInfo(ReportInfo report) {
        WaybillDeliveryAddress deliveryAddress = waybillDeliveryAddressService.findByWaybillId(report.getWaybillId());
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

    // 车辆停放地信息
    private ReportInfoDetails buildParkingInfo(ReportInfo report, LoginEmployee loginEmployee) {
        String plateNumber = report.getPlateNumber();
        if (StringUtils.isBlank(plateNumber)) {
            return null;
        }

        com.juma.vms.driver.domain.Driver driver = vmsCommonService.loadDriverByPlateNumber(report.getPlateNumber(), loginEmployee);
        if (null == driver) {
            return null;
        }

        com.juma.server.vm.domain.Driver amsDriver = amsCommonService.findDriver(driver.getAmsDriverId(), loginEmployee);
        if (null == amsDriver) {
            return null;
        }

        ReportInfoDetails detail = new ReportInfoDetails();
        if (null != amsDriver.getLongitude() && null != amsDriver.getLatitude()) {
            detail.setCoordinate(amsDriver.getLongitude() + "," + amsDriver.getLatitude());
        }
        detail.setAddressDetail(StringUtils.isBlank(amsDriver.getAddress()) ? "暂无" : amsDriver.getAddress());
        detail.setReportTime(
                amsDriver.getLastUpdateTime() == null ? amsDriver.getCreateTime() : amsDriver.getLastUpdateTime());
        detail.setLabelPointType(ReportInfoDetails.LabelPointType.TRUCK_PARKING.toString());
        detail.setTitle("停");
        return detail;
    }

    // 组装VO信息
    private ReportInfoVO buildReportInfoVO(ReportInfoBo bo) {
        ReportInfoVO vo = new ReportInfoVO();

        // 报备信息
        ReportInfo reportInfo = bo.getReportInfo();
        if (null != reportInfo) {
            vo.setReportInfoId(reportInfo.getReportInfoId());
            vo.setPlateNumber(reportInfo.getPlateNumber());
            vo.setWaybillNo(reportInfo.getWaybilNo());
            vo.setWaybillId(reportInfo.getWaybillId());
            vo.setReportInfoType(reportInfo.getReportInfoType());
            vo.setRemark(reportInfo.getRemark());
            vo.setFirstReportTime(reportInfo.getFirstReportTime());
            vo.setReportInfoCount(reportInfo.getReportInfoCount());

        }

        // 司机信息
        Driver driver = bo.getDriver();
        if (null != driver) {
            vo.setDriverName(driver.getNickname());
            vo.setDriverPhone(driver.getContactPhone());
        }

        // 企业客户信息
        CustomerInfo customerInfo = bo.getCustomerInfo();
        if (null != customerInfo) {
            vo.setCustomerName(customerInfo.getCustomerName());
        }
        return vo;
    }

    /**
     * 根据报备id获取其对应的运单信息
     */
    @ResponseBody
    @RequestMapping(value = "/{reportInfoId}/show/waybill", method = RequestMethod.GET)
    public WaybillBo waybillByReportId(@PathVariable Integer reportInfoId, LoginEmployee loginEmployee) {
        ReportInfo reportInfo = reportInfoService.getReportInfo(reportInfoId);
        WaybillDeliveryAddress deliveryAddress = waybillDeliveryAddressService.findByWaybillId(reportInfo.getWaybillId());
        Integer waybillId = reportInfo.getWaybillId();
        WaybillBo waybillBo = waybillService.getWaybillBoById(waybillId, loginEmployee);
        waybillBo.setDeliveryAddressStr(deliveryAddress.getAddressDetail());
        return waybillBo;
    }

    /**
     * 获取所有的报备类型
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getReportTypes", method = RequestMethod.GET)
    public List<ConfParamOption> getReportTypes() {
        return this.doGetReportTypes();
    }

    private List<ConfParamOption> doGetReportTypes() {
        return confParamService.findParamOptions(Constants.REPORT_INFO_TYPE_KEY);
    }
}
