package com.juma.tgm.manage.waybill.controller;

import com.alibaba.fastjson.JSON;
import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.giants.common.tools.PageQueryCondition;
import com.google.common.collect.Lists;
import com.juma.auth.conf.domain.BusinessArea;
import com.juma.auth.conf.service.BusinessAreaService;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.EmployeeService;
import com.juma.auth.user.domain.User;
import com.juma.auth.user.service.UserService;
import com.juma.log.domain.OperationLogBo;
import com.juma.log.domain.OperationLogFilter;
import com.juma.log.service.OperationLogService;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.crm.domain.CustomerInfoResp;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.customer.domain.TruckCustomer;
import com.juma.tgm.export.domain.ExportParam;
import com.juma.tgm.filiale.service.FilialeBillService;
import com.juma.tgm.imageUploadManage.domain.ImageUploadManage;
import com.juma.tgm.manage.waybill.util.WaybillControllerUtil;
import com.juma.tgm.manage.waybill.vo.ValuationDetailVo;
import com.juma.tgm.manage.waybill.vo.WaybillCancelVO;
import com.juma.tgm.manage.waybill.vo.WaybillPrice;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.project.domain.Project;
import com.juma.tgm.project.service.ProjectService;
import com.juma.tgm.project.vo.ProjectBillVo;
import com.juma.tgm.receiptManage.service.ReceiptManageService;
import com.juma.tgm.tools.service.AuthCommonService;
import com.juma.tgm.truck.domain.TruckType;
import com.juma.tgm.truck.service.TruckTypeService;
import com.juma.tgm.user.domain.CurrentUser;
import com.juma.tgm.waybill.domain.*;
import com.juma.tgm.waybill.domain.vo.DistanceAndPriceParamVo;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.DataSource;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.enumeration.WhoAdjustPriceEnum;
import com.juma.tgm.waybill.service.*;
import com.juma.tgm.waybill.vo.ConfirmWaybillAmountVO;
import com.juma.tgm.waybill.vo.WaybillAdjustPriceVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 
 * @Description: 运单管理
 * @author weilibin
 * @date 2016年5月16日 下午7:09:37
 * @version V1.0
 */

@Controller
@RequestMapping(value = "waybill")
public class WaybillController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(WaybillController.class);

    @Resource
    private WaybillService waybillService;

    @Resource
    private WaybillCronService waybillCronService;

    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;

    @Resource
    private WaybillQueryService waybillQueryService;

    @Resource
    private WaybillAutoMatchService waybillAutoMatchService;

    @Resource
    private UserService userService;

    @Resource
    private CustomerInfoService customerInfoService;

    @Resource
    private AuthCommonService authCommonService;

    @Resource
    private com.juma.crm.customer.service.CustomerInfoService crmCustomerInfoService;

    @Resource
    private OperationLogService operationLogService;

    @Resource
    private ReceiptManageService receiptManageService;

    @Resource
    private BusinessAreaService businessAreaService;

    @Resource
    private WaybillCommonService waybillCommonService;

    @Resource
    private WaybillAmountService waybillAmountService;

    @Resource
    private FilialeBillService filialeBillService;

    @Resource
    private WaybillControllerUtil waybillControllerUtil;

    @Resource
    private ProjectService projectService;

    @Resource
    private TruckTypeService truckTypeService;

    @Resource
    private EmployeeService employeeService;

    @ApiOperation(value = "运单列表：按业务规则查询 本业务区域和分享业务区域")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filters.startFinishTime", value = "运单完成时间查询，格式：2018-11"),
            @ApiImplicitParam(name = "filters.endFinishTime", value = "运单完成时间查询，格式：2018-11")
    })
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<WaybillVO> search(@ApiParam(hidden = true) LoginEmployee loginEmployee, PageCondition pageCondition) {
        super.formatAreaCodeToList(pageCondition, true);
        pageCondition.getFilters().put("wechatPending", false);
        pageCondition.getFilters().put("backstage", true);
        pageCondition.setOrderBy(
                StringUtils.isBlank(pageCondition.getOrderBy()) ? " planDeliveryTime desc" : pageCondition.getOrderBy());
        return waybillService.searchForManageSys(loginEmployee, pageCondition);
    }

    /**
     * 统计
     */
    @ResponseBody
    @RequestMapping(value = "count/freight", method = RequestMethod.POST)
    public WaybillCountResponse tableCount(@RequestBody ExportParam exportParam, LoginEmployee loginEmployee) {
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNo(1);
        pageCondition.setPageSize(1);
        pageCondition.setFilters(exportParam.getFilters());
        pageCondition.getFilters().put("wechatPending", false);
        super.formatAreaCodeToList(pageCondition, true);
        return waybillService.getFreight(pageCondition, loginEmployee);
    }

    // 再次用车或运单详情
    @ResponseBody
    @RequestMapping(value = "{waybillId}/json/detail", method = RequestMethod.GET)
    public WaybillDetailInfo detailJson(@PathVariable Integer waybillId, String waybillNo,
            LoginEmployee loginEmployee) {
        if (waybillId.equals(0)) {
            Waybill waybill = waybillCommonService.findByWaybillNo(waybillNo);
            waybillId = waybill == null ? null : waybill.getWaybillId();
        }

        WaybillDetailInfo detailInfo = waybillService.findWaybillDetailById(waybillId, loginEmployee);
        if (null == detailInfo) {
            return null;
        }

        ValuationDetailVo valuationDetailVo = waybillControllerUtil.loadValuationDetail(null, null,
                detailInfo.getWaybillParam().getProjectFreightRuleJson(), loginEmployee);
        if (null != valuationDetailVo) {
            detailInfo.getWaybillParam().setProjectFreightRuleJson(valuationDetailVo.getValuationConstView());
            if (StringUtils.isNotBlank(valuationDetailVo.getValuationWayView())) {
                detailInfo.getWaybill().setValuationWayView(valuationDetailVo.getValuationWayView());
            }

            detailInfo.setValuationWay(valuationDetailVo.getValuationWay());
        }
        detailInfo.setAllowChangeCar(waybillService.allowChangeCar(detailInfo.getWaybill()));

        // 获取运单所在区域的逻辑区域
        logicAreaCode(detailInfo, loginEmployee);

        return detailInfo;
    }

    // 项目下单
    @ResponseBody
    @RequestMapping(value = "{projectId}/project/createWaybill/data", method = RequestMethod.GET)
    public WaybillDetailInfo projectCreateWaybill(@PathVariable Integer projectId, LoginEmployee loginEmployee) {
        return projectService.doBuildWaybillInfo(projectId);
    }

    // 获取运单所在区域的逻辑区域:如果是分享运单，则取shareAreaCode
    private void logicAreaCode(WaybillDetailInfo detailInfo, LoginEmployee loginEmployee) {
        try {
            BusinessArea businessArea = businessAreaService
                    .loadLogicBusinessArea(detailInfo.getWaybill().getAreaCode(), loginEmployee);
            if (null != businessArea) {
                detailInfo.getWaybill().setLogicAreaCode(businessArea.getAreaCode());
                detailInfo.getWaybill().setLogicAreaCodeName(businessArea.getAreaName());
            }
        } catch (Exception e) {
        }
    }

    /**
     * 系统取消
     */
    @ResponseBody
    @RequestMapping(value = "sysCancel", method = RequestMethod.POST)
    public void sysCancel(@RequestBody WaybillCancelVO waybillCancelVO, LoginEmployee loginEmployee) {
        Waybill waybill = waybillService.getWaybill(waybillCancelVO.getWaybillId());
        if (null == waybill) {
            return;
        }

        // 承运商不能取消承运运单
        if (NumberUtils.compare(Waybill.WaybillSource.TRANSFORM_BILL.getCode(), waybill.getWaybillSource()) == 0) {
            throw new BusinessException("transformBillCannotCancel", "waybill.error.transformBillCannotCancel");
        }

        waybillService.cancelWaybill(waybillCancelVO.getWaybillId(), Waybill.CancelChannel.BACKGROUND_IMPORT,
                waybillCancelVO.getWaybillCancelRemark(), loginEmployee);

        // 操作轨迹
        waybillOperateTrackService.insert(waybillCancelVO.getWaybillId(), OperateType.CANCEL,
                OperateApplication.BACKGROUND_SYS, null, loginEmployee);
    }

    /**
     * 派车待定
     */
    @ResponseBody
    @RequestMapping(value = "{waybillId}/undetermined", method = RequestMethod.GET)
    public void undetermined(@PathVariable Integer waybillId, CurrentUser currentUser, LoginEmployee loginEmployee) {
        waybillService.updateUndetermined(waybillId, loginEmployee);
    }

    /**
     * 确认回单
     */
    @ResponseBody
    @RequestMapping(value = "{waybillId}/confirmNeedReceipt", method = RequestMethod.POST)
    public void confirmNeedReceipt(@PathVariable Integer waybillId, LoginEmployee loginEmployee) {
        waybillService.updateToHasNeedReceipt(waybillId, loginEmployee);

        // 操作轨迹
        waybillOperateTrackService.insert(waybillId, OperateType.CONFIRM_NEED_RECEIPT,
                OperateApplication.BACKGROUND_SYS, null, loginEmployee);
    }

    /**
     * 不派车反馈
     */
    @RequestMapping("assignCarFeedback")
    @ResponseBody
    public void assignCarFeedback(@RequestBody Waybill waybill, CurrentUser currentUser, LoginEmployee loginEmployee) {
        waybillService.updateAssignCarFeedback(waybill, loginEmployee);

        // 操作轨迹
        try {
            WaybillOperateTrack waybillOperateTrack = new WaybillOperateTrack();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("assignCarFeedback", waybill.getAssignCarFeedback());
            waybillOperateTrack.setRemark(JSON.toJSONString(map));
            waybillOperateTrack.setWaybillId(waybill.getWaybillId());
            waybillOperateTrack.setOperateType(OperateType.ASSIGN_FEED_BACK.getCode());
            waybillOperateTrack.setOperateApplication(OperateApplication.BACKGROUND_SYS.getCode());
            waybillOperateTrack.setDataSource(DataSource.BACKGROUND_SYS.getCode());
            waybillOperateTrack.setCreateTime(new Date());
            waybillOperateTrack.setCreateUserId(loginEmployee.getUserId());
            waybillOperateTrackService.insert(waybillOperateTrack, loginEmployee);
        } catch (Exception e) {
            log.error("不派车反馈:记录操作轨迹失败--》{}", e);
        }
    }

    /**
     * 车辆 地图 运单 关系
     */
    @RequestMapping("{waybillId}/truckMap")
    @ResponseBody
    public WaybillMap waybillTruckMap(@PathVariable("waybillId") Integer waybillId) {
        return waybillQueryService.findWaybillMapById(waybillId);
    }

    /**
     * 创建运单
     */
    @RequestMapping(value = "createWaybill", method = RequestMethod.POST)
    @ResponseBody
    public List<Integer> createWaybill(@RequestBody WaybillBo waybillBo, LoginEmployee loginEmployee) {
        log.info("后台建单接收参数controller：{}", JSON.toJSONString(waybillBo));
        Waybill waybill = waybillBo.getWaybill();
        if (null == waybill.getProjectId()) {
            throw new BusinessException("tenantIdIsNot4CannotCreateWaybillForNoProject", "waybill.error.tenantIdIsNot4CannotCreateWaybillForNoProject");
        }

        // 重量、体积校验
        TruckRequire truckRequire = waybillBo.getTruckRequire();
        TruckType truckType = truckTypeService.getTruckType(truckRequire.getTruckTypeId());
        if (StringUtils.isNotBlank(truckRequire.getGoodsWeight()) && null != truckType.getTruckTypeLoad()) {
            if (truckType.getTruckTypeLoad().compareTo(new BigDecimal(truckRequire.getGoodsWeight())) < 0) {
                throw new BusinessException("truckTypeCheck", "waybill.error.loadRequiredMax");
            }
        }
        if (StringUtils.isNotBlank(truckRequire.getGoodsVolume()) && null != truckType.getTruckTypeVolume()) {
            if (truckType.getTruckTypeVolume().compareTo(new BigDecimal(truckRequire.getGoodsVolume())) < 0) {
                throw new BusinessException("truckTypeCheck", "waybill.error.volumeRequiredMax");
            }
        }

        // 温度参数校验
        WaybillParam waybillParam = waybillBo.getWaybillParam();
        if (null != waybillParam.getRequiredMinTemperature() && null != waybillParam.getRequiredMaxTemperature()) {
            if (waybillParam.getRequiredMinTemperature() > waybillParam.getRequiredMaxTemperature()) {
                throw new BusinessException("temperatureCheck", "waybill.error.temperatureCheck");
            }
        }
        if (null == waybillParam.getRequiredMinTemperature() && null != waybillParam.getRequiredMaxTemperature()) {
            throw new BusinessException("temperatureRequiredMin", "waybill.error.temperatureRequiredMin");
        }
        if (null != waybillParam.getRequiredMinTemperature() && null == waybillParam.getRequiredMaxTemperature()) {
            throw new BusinessException("temperatureRequiredMax", "waybill.error.temperatureRequiredMax");
        }

        if (null == waybill.getBusinessBranch()) {
            throw new BusinessException("businessBranchRequired", "waybill.error.businessBranchRequired");
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
            if (null != waybill.getProjectId()) {
                continue;
            }

            // 操作轨迹:发单
            waybillOperateTrackService.insert(waybillId, OperateType.CREATE_WAYBILL,
                    OperateApplication.BACKGROUND_SYS, buildTrackNotRequieParam(receiveWay),
                    loginEmployee);

            if (NumberUtils.compare(Waybill.ReceiveWay.RECEIVED.getCode(), receiveWay) == 0) {
                waybillService.changeToWaitingReceive(waybillId, loginEmployee);
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
        waybillBo.getWaybill().setWaybillSource(Waybill.WaybillSource.BACKGROUND_NEW.getCode());
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
     *
     * @param waybillBo
     * @return
     */
    @RequestMapping(value = "calculateFreight", method = RequestMethod.POST)
    @ResponseBody
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

        DistanceAndPriceParamVo dp = new DistanceAndPriceParamVo();
        dp.setSrcAddress(formAddress);
        dp.setToAddress(toAddress);
        dp.setWaybill(waybillBo.getWaybill());
        dp.setTruckRequire(truckRequire);
        dp.setWaybillParam(waybillBo.getWaybillParam());
        DistanceAndPriceData priceData = waybillCommonService.calculateStanderPriceWithDriverFreight(dp, loginEmployee);

        WaybillPrice waybillPrice = new WaybillPrice();
        if (null == priceData) {
            return waybillPrice;
        }
        waybillPrice.setEstimateDistance(priceData.getDistance());
        waybillPrice.setEstimateTimeConsumption(BaseUtil.strToNum(priceData.getDuration()));
        waybillPrice.setStartCoordinates(priceData.getStartCoordinates());
        waybillPrice.setEndCoordinates(priceData.getEndCoordinates());
        waybillPrice.setTolls(priceData.getTolls().setScale(2, BigDecimal.ROUND_HALF_UP));
        BigDecimal price = priceData.getPrice() == null ? BigDecimal.ZERO : priceData.getPrice();
        waybillPrice.setCalculatedFreight(price);
        waybillPrice.setEstimateFreight(
                priceData.getWithTaxPrice() == null ? BigDecimal.ZERO : priceData.getWithTaxPrice());
        waybillPrice.setAfterTaxFreight(price);

        if (null != waybillBo.getWaybill().getProjectId()) {
            Project project = projectService.getProject(waybillBo.getWaybill().getProjectId());
            if (null != project && null != project.getRebateRate()) {
                waybillPrice.setRebateFee(waybillPrice.getEstimateFreight().multiply(project.getRebateRate()));
            }
        }

        waybillPrice.setShow4DriverFreight(priceData.getShow4DriverFreight());
        return waybillPrice;
    }

    /**
     * 根据客户ID获取客户
     */
    @ResponseBody
    @RequestMapping(value = "{userId}/getCustomerName", method = RequestMethod.GET)
    public String getCustomerManageByUser(@PathVariable Integer userId) {
        try {
            User user = userService.loadUser(userId);
            if (null != user) {
                return user.getName();
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 根据客户经理的员工ID查询企业客户
     */
    @ResponseBody
    @RequestMapping(value = "{employeeId}/customerInfoList", method = RequestMethod.GET)
    public List<CustomerInfo> customerInfoList(@PathVariable Integer employeeId) {
        List<CustomerInfo> result = new ArrayList<CustomerInfo>();
        CustomerInfoResp customerInfoResp = customerInfoService.findByEmployeeId(null, employeeId);
        if (null == customerInfoResp) {
            return result;
        }

        for (CustomerInfo customerInfo : customerInfoResp.getCustomerInfoList()) {
            if (customerInfo.getStatus() == 2) {
                continue;
            }
            result.add(customerInfo);
        }
        return result;
    }

    /**
     * 根据CRM的大客户ID获取客户经理
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "crm/{crmCustomerId}/getCustomerManage", method = RequestMethod.GET)
    public String getCustomerManageByCustomerInfo(@PathVariable Integer crmCustomerId) {
        com.juma.crm.customer.domain.CustomerInfo info = crmCustomerInfoService.find(crmCustomerId);
        if (null != info) {
            try {
                User user = userService.loadUser(info.getUserId());
                if (null != user) {
                    return user.getName();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 获取回单图片
     */
    @ResponseBody
    @RequestMapping(value = "receipt/image/{waybillId}/list", method = RequestMethod.GET)
    public List<ImageUploadManage> receiptImageList(@PathVariable Integer waybillId) {
        return receiptManageService.listReceiptImageByWaybillId(waybillId);
    }

    /**
     * 关闭运单
     */
    @ResponseBody
    @RequestMapping(value = "colse/{waybillId}/waybill", method = RequestMethod.GET)
    public void colseWaybill(@PathVariable Integer waybillId, LoginEmployee loginEmployee) {
        waybillService.doCloseWaybill(waybillId, loginEmployee);

        waybillOperateTrackService.insert(waybillId, WaybillOperateTrackEnum.OperateType.COLSE_WAYBILL,
                WaybillOperateTrackEnum.OperateApplication.BACKGROUND_SYS, null, loginEmployee);
    }

    @ApiOperation(value = "确认运单运费")
    @ResponseBody
    @RequestMapping(value = "confirm/price", method = RequestMethod.POST)
    public void confirmWaybillAmount(@RequestBody ConfirmWaybillAmountVO confirmWaybillAmountVO, LoginEmployee loginEmployee) {
        waybillCommonService.confirmWaybillAmount(confirmWaybillAmountVO, loginEmployee);
    }

    @ApiOperation(value = "确认运单运费金额限制信息")
    @ResponseBody
    @RequestMapping(value = "amount/limit", method = RequestMethod.GET)
    public WaybillAmountValidVO getWaybillAmountLimit(LoginEmployee loginEmployee) {
        return waybillAmountService.getWaybillAmountLimit();
    }

    /**每天0点刷新前2天运单-运费确认状态**/
    @RequestMapping(value = "cron/update/amount/status", method = RequestMethod.POST)
    @ResponseBody
    public void updateWaybillAmountStatus(int[] waybillIds,Integer gap){
        if( null == waybillIds || waybillIds.length == 0 ){
            waybillCronService.cronUpdateWaybillAmountStatus(Lists.<Integer>newArrayList(),gap);
            return;
        }
        List<Integer> waybillIdList = Lists.newArrayList();
        for (int waybillId : waybillIds){
            waybillIdList.add(waybillId);
        }
        waybillCronService.cronUpdateWaybillAmountStatus(waybillIdList,null);
    }

    // ---------------------------以下是废弃的方法，不建议使用--------------------------------

    /**
     * 操作日志
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "{waybillId}/logs", method = RequestMethod.POST)
    public Page<OperationLogBo> logs(@PathVariable Integer waybillId, PageCondition pageCondition) {

        OperationLogFilter filter = new OperationLogFilter();
        filter.setPrimaryKeyValue("TMS-WAYBILL:" + waybillId);
        PageQueryCondition<OperationLogFilter> cond = new PageQueryCondition<OperationLogFilter>(filter);
        cond.setPageNo(pageCondition.getPageNo());
        cond.setPageSize(pageCondition.getPageSize());

        return operationLogService.searchPages(cond);
    }

    /**
     * 接单未完成的单子
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "searchNoFinishWaybill", method = RequestMethod.POST)
    public Page<WaybillNoFinish> searchNoFinishWaybill(ExportParam exportParam, // PageCondition
                                                       // pageCondition,
                                                       @ModelAttribute("currentUser") CurrentUser currentUser, LoginEmployee loginEmployee) {
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNo(1);
        pageCondition.setPageSize(100000);
        pageCondition.setFilters(exportParam.getFilters());
        super.formatAreaCodeToList(pageCondition, true);
        pageCondition.getFilters().put("autoMatch", "authMatch");
        if (StringUtils.isBlank(pageCondition.getOrderBy()) || StringUtils.isBlank(pageCondition.getOrderSort())) {
            pageCondition.setOrderBy(" plan_delivery_time desc ");
        } else {
            pageCondition.setOrderBy(pageCondition.getOrderBy() + " " + pageCondition.getOrderSort());
        }
        pageCondition.getFilters().put("groupBy", " truckId ");
        return waybillAutoMatchService.searchNoFinishWaybill(pageCondition);
    }

    /**
     * 通过customerId 获取用车人列表
     *
     * @param customerId
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/{customerId}/contactList", method = RequestMethod.GET)
    @ResponseBody
    public List<TruckCustomer> getContactByCustomerId(@PathVariable Integer customerId) {
        return new ArrayList<>();
    }
}
