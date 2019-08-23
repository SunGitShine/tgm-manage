package com.juma.customize.xidi.waybill.controller;

import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.juma.tgm.tools.service.AuthCommonService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.fastjson.JSON;
import com.giants.common.exception.BusinessException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.EmployeeService;
import com.juma.auth.user.domain.User;
import com.juma.customize.annotation.CustomizeLayer;
import com.juma.customize.annotation.Customized;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.common.Constants;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.filiale.service.FilialeBillService;
import com.juma.tgm.manage.waybill.util.WaybillControllerUtil;
import com.juma.tgm.manage.waybill.vo.WaybillPrice;
import com.juma.tgm.project.vo.ProjectBillVo;
import com.juma.tgm.tools.service.AuthCommonService;
import com.juma.tgm.truck.domain.TruckType;
import com.juma.tgm.truck.service.TruckTypeFreightService;
import com.juma.tgm.truck.service.TruckTypeService;
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
import com.juma.tgm.waybill.domain.vo.DistanceAndPriceParamVo;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.service.WaybillService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName: JumaPsWaybillController
 * @Description:
 * @author: liang
 * @date: 2018-03-28 15:31
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
@Customized(tenantKey = Constants.TENANT_KEY_JUMA_LOGISTICS, layer = CustomizeLayer.controller)
@Component
public class JumaPsWaybillController {

    private static final Logger log = LoggerFactory.getLogger(JumaPsWaybillController.class);
    @Resource
    private TruckTypeFreightService truckTypeFreightService;

    @Resource
    private CustomerInfoService customerInfoService;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private WaybillService waybillService;

    @Resource
    private WaybillControllerUtil waybillControllerUtil;

    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;

    @Resource
    private AuthCommonService authCommonService;

    @Resource
    private FilialeBillService filialeBillService;

    @Resource
    private TruckTypeService truckTypeService;

    /**
     * 驹马专车报价接口
     * 
     * @param dp
     * @param loginEmployee
     * @return
     */
    public DistanceAndPriceData getDistanceAndPrice(DistanceAndPriceParamVo dp, LoginEmployee loginEmployee) {
        WaybillBo bo = new WaybillBo();
        bo.setTruckRequire(dp.getTruckRequire());
        bo.setWaybill(dp.getWaybill());
        waybillControllerUtil.planEstimateFinishTimeCheck(bo);
        DistanceAndPriceData priceData = waybillService.calWaybillPrice(dp.getSrcAddress(), dp.getToAddress(), bo,
                loginEmployee);

        Waybill waybill = dp.getWaybill();

        if (waybill == null)
            return priceData;

        if (waybill.getEstimateFreight() == null) {
            waybill.setEstimateFreight(priceData.getReferenceFreight());
        }

        // 司机结算价
        if (waybill.getCustomerId() != null) {
            // 获取返点数据
            BigDecimal rebateRate = customerInfoService.getRebateRate(waybill.getCustomerId());
            waybill.setRebateRate(rebateRate);
        }
        // --获取税后价格
        BigDecimal afterTaxFee = truckTypeFreightService.getAfterTaxFright(dp.getTruckRequire(), waybill);
        if (afterTaxFee == null)
            return priceData;// 没有税后价格则不能计算司机结算价

        waybill.setAfterTaxFreight(afterTaxFee);
        // --获取司机结算价格
        WaybillParam waybillParam = waybillService.settingExtraFee(bo, null);
        BigDecimal show4DriverFreight = waybillParam.getShow4DriverFreight();
        if (show4DriverFreight != null) {
            priceData.setShow4DriverFreight(show4DriverFreight.setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        return priceData;
    }

    /**
     * 创建运单
     */
    public List<Integer> createWaybill(@RequestBody WaybillBo waybillBo, LoginEmployee loginEmployee) throws BusinessException {

        log.info("后台建单接收参数controller：{}", JSON.toJSONString(waybillBo));
        Waybill waybill = waybillBo.getWaybill();
        if (null == waybill.getProjectId() && loginEmployee.getTenantId() != 4) {
            throw new BusinessException("tenantIdIsNot4CannotCreateWaybillForNoProject", "waybill.error.tenantIdIsNot4CannotCreateWaybillForNoProject");
        }

        //重量、体积校验
        TruckRequire truckRequire = waybillBo.getTruckRequire();
        TruckType truckType = truckTypeService.getTruckType(truckRequire.getTruckTypeId());
        if(StringUtils.isNotBlank(truckRequire.getGoodsWeight()) && null!= truckType.getTruckTypeLoad()){
            if (truckType.getTruckTypeLoad().compareTo(new BigDecimal(truckRequire.getGoodsWeight())) < 0) {
                throw new BusinessException("truckTypeCheck","waybill.error.loadRequiredMax");
            }
        }
        if(StringUtils.isNotBlank(truckRequire.getGoodsVolume()) && null != truckType.getTruckTypeVolume()){
            if (truckType.getTruckTypeVolume().compareTo(new BigDecimal(truckRequire.getGoodsVolume())) < 0) {
                throw new BusinessException("truckTypeCheck","waybill.error.volumeRequiredMax");
            }
        }

        //温度参数校验
        WaybillParam waybillParam = waybillBo.getWaybillParam();
        if(null != waybillParam.getRequiredMinTemperature() && null!= waybillParam.getRequiredMaxTemperature()){
            if(waybillParam.getRequiredMinTemperature() > waybillParam.getRequiredMaxTemperature()){
                throw new BusinessException("temperatureCheck","waybill.error.temperatureCheck");
            }
        }
        if(null == waybillParam.getRequiredMinTemperature() && null!= waybillParam.getRequiredMaxTemperature()){
            throw new BusinessException("temperatureRequiredMin","waybill.error.temperatureRequiredMin");
        }
        if(null != waybillParam.getRequiredMinTemperature() && null == waybillParam.getRequiredMaxTemperature()){
            throw new BusinessException("temperatureRequiredMax","waybill.error.temperatureRequiredMax");
        }

        if (null == waybill.getBusinessBranch()) {
            throw new BusinessException("businessBranchRequired", "waybill.error.businessBranchRequired");
        }

        if (NumberUtils.compare(waybill.getBusinessBranch(), Waybill.BusinessBranch.BRANCH_SCATTERED.getCode()) == 0) {
            throw new BusinessException("nonsupportBranchScattered", "waybill.error.nonsupportBranchScattered");
        }
        
        // 所属客户
        if (waybill == null || waybill.getCustomerId() == null) {
            throw new BusinessException("customerInfoNotNull", "waybill.errors.customerInfoNotNull");
        }

        // 最大发单数量
        if (waybillBo.getCreateBatchAmount() != null && NumberUtils.compare(waybillBo.getCreateBatchAmount(), 50) > 0) {
            throw new BusinessException("overMaxWaybillSize", "waybill.error.overMaxWaybillSize");
        }

        // 所属客户
        CustomerInfo customerInfo = customerInfoService.findCusInfoById(waybill.getCustomerId());
        if (null != customerInfo) {
            waybill.setAreaCode(customerInfo.getAreaCode());
            waybill.setCustomerManagerId(customerInfo.getCustomerManagerUserId());
        }

        // 验证是否选择了结算方式及支不支持项目结算
        waybillControllerUtil.checkIsProjectCheckout(waybillBo.getWaybill());

        if (waybillBo.getCreateBatchAmount() == null || NumberUtils.compare(waybillBo.getCreateBatchAmount(), 1) <= 0) {
            waybillBo.setCreateBatchAmount(1);
        }

        // 派车方式
        Integer receiveWay = waybill.getReceiveWay();

        // 组装承运商的项目ID
        waybillBo.setWaybillCarrierVo(waybillControllerUtil.buildWaybillCarrierVo(waybillBo.getWaybillCarrierVo(),
                waybill.getProjectId(), loginEmployee));

        log.info("建单参数：{}", JSON.toJSONString(waybillBo));
        List<Integer> ids = new ArrayList<Integer>();
        if (null == waybill.getProjectId()) {
            // 专车下单
            ids = jumaPsCreateWaybill(waybillBo, loginEmployee);
        } else {
            if (NumberUtils.compare(Waybill.ReceiveWay.MANUAL_ASSIGN.getCode(), waybill.getReceiveWay()) != 0
                    && NumberUtils.compare(Waybill.ReceiveWay.TRANSFORM_BILL.getCode(), waybill.getReceiveWay()) != 0) {
                throw new BusinessException("peojectWaybillOnlyManualAssign",
                        "waybill.error.peojectWaybillOnlyManualAssign");
            }
            // 项目下单
            ids = projectCreateWaybill(waybillBo, loginEmployee);
        }

        for (int i = 0; i < ids.size(); i++) {
            Integer waybillId = ids.get(i);

            // 操作轨迹:发单
            waybillOperateTrackService.insert(waybillId, OperateType.CREATE_WAYBILL,
                    OperateApplication.BACKGROUND_SYS, buildTrackNotRequieParam(receiveWay),
                    loginEmployee);

            if (null != waybill.getProjectId()) {
                continue;
            }

            if (NumberUtils.compare(Waybill.ReceiveWay.RECEIVED.getCode(), receiveWay) == 0) {
                // 操作轨迹
                waybillOperateTrackService.insert(waybillId, OperateType.ASSIGNED,
                        OperateApplication.BACKGROUND_SYS, null, loginEmployee);
            } else if (NumberUtils.compare(Waybill.ReceiveWay.MANUAL_ASSIGN.getCode(), receiveWay) == 0) {
                // 操作轨迹
                waybillOperateTrackService.insert(waybillId, OperateType.MANUAL_ASSIGN,
                        OperateApplication.BACKGROUND_SYS, null, loginEmployee);
            } else if (NumberUtils.compare(Waybill.ReceiveWay.TRANSFORM_BILL.getCode(), receiveWay) == 0) {
                // 操作轨迹
                waybillOperateTrackService.insert(waybillId, OperateType.TRANSFORM_BILL,
                        OperateApplication.BACKGROUND_SYS, null, loginEmployee);
            }
        }

        return ids;
    }

    // 专车下单
    private List<Integer> jumaPsCreateWaybill(WaybillBo waybillBo, LoginEmployee loginEmployee) {
        List<Integer> ids = new ArrayList<Integer>();
        log.info("后台建单接收参数-准备建单：{}", JSON.toJSONString(waybillBo));
        User user = employeeService.findUserByEmployeeId(waybillBo.getWaybill().getCustomerManagerId(), loginEmployee);
        if (user != null) {
            waybillBo.getWaybill().setProjectManagerUserId(user.getUserId());
            waybillBo.getWaybill().setTest(user.isTest());
        }
        for (int i = 0; i < waybillBo.getCreateBatchAmount(); i++) {
            Integer id = waybillService.createWaybill(waybillBo, Waybill.WaybillSource.BACKGROUND_NEW, loginEmployee);
            ids.add(id);
        }

        return ids;
    }

    // 项目下单
    private List<Integer> projectCreateWaybill(WaybillBo waybillBo, LoginEmployee loginEmployee) {

        if (null == waybillBo.getWaybill().getProjectManagerUserId()) {
            throw new BusinessException("paramCanNotNullProjectManagerUserId", "errors.paramCanNotNullWithName", "项目经理");
        }

        User user = authCommonService.loadUser(waybillBo.getWaybill().getProjectManagerUserId());
        if (user != null) {
            waybillBo.getWaybill().setTest(user.isTest());
        }

        ProjectBillVo vo = new ProjectBillVo();
        Waybill waybill = waybillBo.getWaybill();
//        waybill.setEstimateFreight(null);
        waybill.setAfterTaxFreight(null);
        waybill.setShow4DriverFreight(null);

        vo.setWaybill(waybill);
        vo.setTruckRequire(waybillBo.getTruckRequire());
        vo.setDeliveryAddress(waybillBo.getDeliveryAddress());
        vo.setReceiveAddress(waybillBo.getReceiveAddress());
        vo.setCreateBatchAmount(waybillBo.getCreateBatchAmount());
        vo.setWaybillParam(waybillBo.getWaybillParam());
        vo.setWaybillCarrierVo(waybillBo.getWaybillCarrierVo());

        com.juma.tgm.project.domain.v2.Project project = new com.juma.tgm.project.domain.v2.Project();
        project.setProjectId(waybillBo.getWaybill().getProjectId());
        project.setName(waybillBo.getWaybill().getProjectName());
        vo.setProject(project);

        return filialeBillService.createProjectBill(vo, loginEmployee);
    }

    // 构造操作轨迹非必填参数
    private WaybillOperateTrackNotRequieParam buildTrackNotRequieParam(Integer receiveWay) {
        WaybillOperateTrackNotRequieParam notRequieParam = new WaybillOperateTrackNotRequieParam();
        notRequieParam.setRemark("首次派车方式:" + receiveWay);
        return notRequieParam;
    }
    

    /**
     * 异步计算运费
     */
    public WaybillPrice calculateFreight(@RequestBody WaybillBo waybillBo, LoginEmployee loginEmployee) {
        // 车型验证
        TruckRequire truckRequire = waybillBo.getTruckRequire();
        if (null == truckRequire || null == truckRequire.getTruckTypeId()) {
            throw new BusinessException("truckTypeMustSelect", "truckTypeFreight.not.truckTypeMustSelect");
        }

        // 取货地验证
        List<WaybillDeliveryAddress> deliveryAddress = waybillBo.getDeliveryAddress();
        if (deliveryAddress.isEmpty() || deliveryAddress.size() == 0) {
            throw new BusinessException("validation.srcAddress", "errors.validation.srcAddress");
        }
        for (WaybillDeliveryAddress address : deliveryAddress) {
            if (StringUtils.isBlank(address.getCoordinates())) {
                throw new BusinessException("validation.srcAddress.lawful", "errors.validation.srcAddress.lawful");
            }
        }

        // 目的地验证
        List<WaybillReceiveAddress> receiveAddress = waybillBo.getReceiveAddress();
        if (receiveAddress.isEmpty() || receiveAddress.size() == 0) {
            throw new BusinessException("validation.srcAddress", "errors.validation.toAddress");
        }

        int temp = 0;
        for (WaybillReceiveAddress address : receiveAddress) {
            temp += 1;
            if (StringUtils.isBlank(address.getCoordinates())) {
                throw new BusinessException("validation.srcAddress.lawful", "errors.validation.toAddress.lawful", temp);
            }
        }

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

        DistanceAndPriceData priceData = waybillService.calWaybillPrice(formAddress, toAddress, waybillBo,
                loginEmployee);
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
            waybillPrice.setAfterTaxFreight(priceProxy.getFinalPrice().multiply(new BigDecimal("1.2")).divide(rate, 2,
                    BigDecimal.ROUND_HALF_UP));

            BigDecimal rebateRate = priceProxy.getRebateRateInfo().getRate();
            waybillPrice.setRebateFee(priceProxy.getFinalPrice().multiply(new BigDecimal("1.2")).multiply(rebateRate)
                    .setScale(2, BigDecimal.ROUND_HALF_UP));

            waybillBo.getWaybill().setAfterTaxFreight(waybillPrice.getAfterTaxFreight());
            waybillBo.getWaybill().setEstimateFreight(waybillPrice.getEstimateFreight());
            waybillBo.getWaybill().setRebateRate(rebateRate);
            WaybillParam waybillParam = waybillService.settingExtraFee(waybillBo, null);
            if (waybillParam != null && waybillParam.getShow4DriverFreight() != null) {
                waybillPrice.setShow4DriverFreight(
                        waybillParam.getShow4DriverFreight().setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        }
        return waybillPrice;
    }
}
