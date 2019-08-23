package com.juma.tgm.manage.waybill.controller;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.EmployeeInfo;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.EmployeeService;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.common.Constants;
import com.juma.tgm.common.DateUtil;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.crm.domain.UserUnderCustomer;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.manage.waybill.vo.WaybillCancelVO;
import com.juma.tgm.manage.waybill.vo.WaybillPendingVO;
import com.juma.tgm.manage.waybill.vo.WaybillPrice;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.truck.domain.TruckType;
import com.juma.tgm.truck.service.TruckTypeService;
import com.juma.tgm.user.domain.CurrentUser;
import com.juma.tgm.waybill.domain.CityAdressData;
import com.juma.tgm.waybill.domain.DistanceAndPriceData;
import com.juma.tgm.waybill.domain.TruckRequire;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillBo;
import com.juma.tgm.waybill.domain.WaybillDeliveryAddress;
import com.juma.tgm.waybill.domain.WaybillOperateTrackNotRequieParam;
import com.juma.tgm.waybill.domain.WaybillParam;
import com.juma.tgm.waybill.domain.WaybillReceiveAddress;
import com.juma.tgm.waybill.domain.drools.PriceProxy;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.TruckRequireService;
import com.juma.tgm.waybill.service.WaybillDeliveryAddressService;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.service.WaybillReceiveAddressService;
import com.juma.tgm.waybill.service.WaybillService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Libin.Wei
 * @version 1.0.0
 * @ClassName WaybillPendingController.java
 * @Description 待审运单
 * @Date 2017年1月10日 下午2:38:15
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("waybill/pending")
public class WaybillPendingController extends BaseController {

    @Resource
    private WaybillService waybillService;
    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;
    @Resource
    private WaybillDeliveryAddressService waybillDeliveryAddressService;
    @Resource
    private WaybillReceiveAddressService waybillReceiveAddressService;
    @Resource
    private CustomerInfoService customerInfoService;
    @Resource
    private TruckRequireService truckRequireService;
    @Resource
    private EmployeeService employeeService;
    @Resource
    private TruckTypeService truckTypeService;

    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<Waybill> search(PageCondition pageCondition, @ModelAttribute("currentUser") CurrentUser currentUser,
                                  LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, true);
        pageCondition.getFilters().put("waybillSource", Waybill.WaybillSource.WECHAT_CLIENT.getCode());
        pageCondition.getFilters().put("wechatPending", true);
        pageCondition.getFilters().put("backstage", true);
        pageCondition = waybillService.buildDataFilterCondByDepartment(pageCondition, currentUser);
        return waybillService.search(loginEmployee, pageCondition);
    }

    /**
     * 跳转到订单编辑页
     */
    @ResponseBody
    @RequestMapping(value = "{waybillId}/json/edit", method = RequestMethod.GET)
    public WaybillPendingVO jsonEdit(@PathVariable Integer waybillId, LoginEmployee loginEmployee) {
        WaybillPendingVO vo = new WaybillPendingVO();
        Waybill waybill = waybillService.getWaybillAndCheckExist(waybillId);
        vo.setWaybill(waybill);
        vo.setTruckRequire(truckRequireService.findTruckRequireByWaybillId(waybillId, loginEmployee));
        vo.setListDeliveryAddress(waybillDeliveryAddressService.findAllByWaybillId(waybillId));
        vo.setListReceiveAddress(waybillReceiveAddressService.findAllByWaybillId(waybillId));
        vo.setMinDate(DateUtil.addMinutes(new Date(), 30));

        return vo;
    }

    /**
     * 是否仍为待审运单检查
     */
    @ResponseBody
    @RequestMapping(value = "{waybillId}/check", method = RequestMethod.GET)
    public boolean check(@PathVariable Integer waybillId) {
        Waybill waybill = waybillService.getWaybill(waybillId);
        if (null != waybill && Waybill.Status.PENDING_EXAMINE.getCode() != waybill.getStatus()) {
            return true;
        }
        return false;
    }

    /**
     * 保存
     */
    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public void save(@RequestBody WaybillBo waybillBo, LoginEmployee loginEmployee) {
        Waybill waybill = waybillBo.getWaybill();
        //设置箱型
        this.setBoxType(waybillBo, waybill);
        waybillService.saveWaybillSnapshot(waybillBo, loginEmployee);
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.PERFECT_INFO_SAVE,
                OperateApplication.BACKGROUND_SYS, null, loginEmployee);
    }

    /**
     * 系统派车
     */
    @ResponseBody
    @RequestMapping(value = "updateBySys", method = RequestMethod.POST)
    public void updateBySys(@RequestBody WaybillBo waybillBo, LoginEmployee loginEmployee) {
        Waybill waybill = waybillBo.getWaybill();
        //设置箱型
        this.setBoxType(waybillBo, waybill);
        waybillService.updateWaybill(waybillBo, loginEmployee);
        // 操作轨迹(补充信息)
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.PERFECT_INFO,
                OperateApplication.BACKGROUND_SYS, null, loginEmployee);

        // 派车：司机抢单
        waybill.setAfterTaxFreight(null);
        waybillService.changeToWaitingReceive(waybill.getWaybillId(), loginEmployee);
        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.ASSIGNED,
                OperateApplication.BACKGROUND_SYS,
                buildTrackNotRequieParam(waybill.getReceiveWay()), loginEmployee);
    }

    /**
     * 人工派车
     */
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody WaybillBo waybillBo, LoginEmployee loginEmployee) {
        Waybill waybill = waybillBo.getWaybill();
        //设置箱型
        this.setBoxType(waybillBo, waybill);
        waybillService.updateWaybill(waybillBo, loginEmployee);
        // 操作轨迹(补充信息)
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.PERFECT_INFO,
                OperateApplication.BACKGROUND_SYS, null, loginEmployee);

        // 派车：人工派车
        waybill.setAfterTaxFreight(null);
        waybillService.changeNewToManual(waybill.getWaybillId(), loginEmployee);
        // 操作轨迹
        waybillOperateTrackService.insert(waybill.getWaybillId(), OperateType.MANUAL_ASSIGN,
                OperateApplication.BACKGROUND_SYS,
                buildTrackNotRequieParam(waybill.getReceiveWay()), loginEmployee);
    }

    /**
     * 设置箱型
     *
     * @param waybillBo
     * @param waybill
     */
    private void setBoxType(@RequestBody WaybillBo waybillBo, Waybill waybill) {
        TruckRequire truckRequire = waybillBo.getTruckRequire();
        if (truckRequire != null) {
            Integer type = truckRequire.getTruckTypeId();
            TruckType truckType = truckTypeService.getTruckType(type);
            if (truckType != null) {
                waybill.setVehicleBoxType(truckType.getVehicleBoxType());
            }
        }
    }

    // 构造操作轨迹非必填参数
    private WaybillOperateTrackNotRequieParam buildTrackNotRequieParam(Integer receiveWay) {
        WaybillOperateTrackNotRequieParam notRequieParam = new WaybillOperateTrackNotRequieParam();
        notRequieParam.setRemark("首次派车方式:" + receiveWay);
        return notRequieParam;
    }

    /**
     * 根据用车人获取大客户ID列表
     */
    @ResponseBody
    @RequestMapping(value = "{truckCustomerId}/customerInfoList", method = RequestMethod.GET)
    public Object customerInfoList(@PathVariable Integer truckCustomerId, LoginEmployee loginEmployee) {
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNo(1);
        pageCondition.setPageSize(Integer.MAX_VALUE);
        pageCondition.getFilters().put("truckCustomerId", truckCustomerId);
        Page<UserUnderCustomer> rows = customerInfoService.searchUserUnderCustomer(pageCondition, loginEmployee);
        return rows.getResults();
    }

    /**
     * 根据大客户ID获取客户经理
     */
    @ResponseBody
    @RequestMapping(value = "{customerId}/getCustomerManage", method = RequestMethod.GET)
    public String getCustomerManage(@PathVariable Integer customerId, LoginEmployee loginEmployee) {
        CustomerInfo info = customerInfoService.findCusInfoById(customerId);
        if (null == info) {
            return null;
        }

        try {
            EmployeeInfo employeeInfo = employeeService.findEmployeeInfo(info.getCustomerManagerUserId(),
                    loginEmployee);
            return employeeInfo.getName();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 系统取消
     */
    @ResponseBody
    @RequestMapping(value = "sysCancel", method = RequestMethod.POST)
    public void sysCancel(@RequestBody WaybillCancelVO waybillCancelVO, LoginEmployee loginEmployee) {
        waybillService.cancelWaybill(waybillCancelVO.getWaybillId(), Waybill.CancelChannel.BACKGROUND_IMPORT,
                "后台待审运单取消", loginEmployee);

        // 操作轨迹
        waybillOperateTrackService.insert(waybillCancelVO.getWaybillId(), OperateType.CANCEL,
                OperateApplication.BACKGROUND_SYS, null, loginEmployee);
    }

    /**
     * 异步计算运费：未使用
     *
     * @param waybillBo
     * @return
     */
    @Deprecated
    @RequestMapping(value = "calculateFreight", method = RequestMethod.POST)
    @ResponseBody
    public WaybillPrice calculateFreight(@RequestBody WaybillBo waybillBo, LoginEmployee loginEmployee) {

        Waybill waybill = waybillBo.getWaybill();
        String planDeliveryDate = waybillBo.getPlanDeliveryDate();
        if (StringUtils.isBlank(planDeliveryDate)) {
            throw new BusinessException("planDeliveryTimeNotNull", "waybill.error.planDeliveryTimeNotNull");
        }
        waybill.setPlanDeliveryTime(DateUtil.parse(planDeliveryDate, Constants.YYYYMMDDHHMM.toPattern()));
        TruckRequire truckRequire = waybillBo.getTruckRequire();
        List<WaybillDeliveryAddress> deliveryAddress = waybillBo.getDeliveryAddress();
        List<WaybillReceiveAddress> receiveAddress = waybillBo.getReceiveAddress();

        waybillService.checkCreateWaybill(waybill, truckRequire, deliveryAddress, receiveAddress);

        CityAdressData formAddress = new CityAdressData();
        WaybillDeliveryAddress from = waybillBo.getDeliveryAddress().get(0);
        formAddress.setAddress(from.getAddressDetail());
        formAddress.setCoordinate(from.getCoordinates());

        List<CityAdressData> toAddress = new ArrayList<>();
        CityAdressData address = null;
        for (WaybillReceiveAddress waybillReceiveAddress : waybillBo.getReceiveAddress()) {
            address = new CityAdressData();
            address.setAddress(waybillReceiveAddress.getAddressDetail());
            address.setCoordinate(waybillReceiveAddress.getCoordinates());
            toAddress.add(address);
        }
        DistanceAndPriceData priceData = waybillService.calWaybillPrice(formAddress, toAddress, waybillBo, loginEmployee);
        WaybillPrice waybillPrice = new WaybillPrice();
        if (null == priceData) {
            return waybillPrice;
        }
        waybillPrice.setEstimateDistance(priceData.getDistance());
        waybillPrice.setEstimateTimeConsumption(BaseUtil.strToNum(priceData.getDuration()));
        waybillPrice.setStartCoordinates(priceData.getStartCoordinates());
        waybillPrice.setEndCoordinates(priceData.getEndCoordinates());
        waybillPrice.setTolls(priceData.getTolls().setScale(2, BigDecimal.ROUND_HALF_UP));

        PriceProxy priceProxy = priceData.getPriceProxy();
        if (null != priceProxy) {
            waybillPrice.setCalculatedFreight(priceProxy.getFinalPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
            waybillPrice.setEstimateFreight(
                    priceProxy.getFinalPrice().multiply(new BigDecimal("1.2")).setScale(2, BigDecimal.ROUND_HALF_UP));
            BigDecimal rate = priceProxy.getTaxRateInfo().getRate().add(new BigDecimal(1));
            waybillPrice.setAfterTaxFreight(priceProxy.getFinalPrice().divide(rate, 2, BigDecimal.ROUND_HALF_UP));

            if (null != waybillBo.getWaybill()) {
                BigDecimal rebateRate = priceProxy.getRebateRateInfo().getRate();
                waybillPrice.setRebateFee(priceProxy.getFinalPrice().multiply(new BigDecimal("1.2"))
                        .multiply(rebateRate).setScale(2, BigDecimal.ROUND_HALF_UP));
            }

            waybillBo.getWaybill().setAfterTaxFreight(waybillPrice.getAfterTaxFreight());
            WaybillParam waybillParam = waybillService.settingExtraFee(waybillBo,null);
            if (waybillParam != null && waybillParam.getShow4DriverFreight() != null) {
                waybillPrice.setShow4DriverFreight(
                        waybillParam.getShow4DriverFreight().setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        }
        return waybillPrice;
    }

}
