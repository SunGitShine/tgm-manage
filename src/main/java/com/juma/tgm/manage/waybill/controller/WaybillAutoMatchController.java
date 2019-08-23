package com.juma.tgm.manage.waybill.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.giants.cache.redis.RedisClient;
import com.giants.common.collections.CollectionUtils;
import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.common.Constants;
import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.driver.service.DriverService;
import com.juma.tgm.export.domain.ExportParam;
import com.juma.tgm.manage.waybill.vo.WaybillAutoMatchVo;
import com.juma.tgm.manage.waybill.vo.WaybillParamVo;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.truck.domain.Truck;
import com.juma.tgm.truck.service.TruckService;
import com.juma.tgm.waybill.domain.ToAutoMatchWaybill;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillBo;
import com.juma.tgm.waybill.domain.WaybillOperateTrackNotRequieParam;
import com.juma.tgm.waybill.domain.WaybillResponse;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateApplication;
import com.juma.tgm.waybill.enumeration.WaybillOperateTrackEnum.OperateType;
import com.juma.tgm.waybill.service.WaybillAutoMatchService;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.service.WaybillService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName WaybillAutoMatchController.java
 * @Description 自动派车
 * @author Libin.Wei
 * @Date 2017年3月27日 下午3:25:36
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */
@Deprecated
@Controller
@RequestMapping("waybill/autoMatch")
public class WaybillAutoMatchController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(WaybillAutoMatchController.class);
    @Resource
    private WaybillService waybillService;
    @Resource
    private TruckService truckService;
    @Resource
    private DriverService driverService;
    @Autowired
    private RedisClient redisClient;
    @Resource
    private WaybillAutoMatchService waybillAutoMatchService;
    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;

    /**
     * 按业务规则查询 本组织和非本组织
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<WaybillBo> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, false);
        return waybillAutoMatchService.findToAutoMatchWaybillList(pageCondition, loginEmployee);
    }

    /**
     * 自动匹配
     */
    @ResponseBody
    @RequestMapping(value = "autoMatch", method = RequestMethod.POST)
    public void autoMatch(@RequestBody ExportParam exportParam, LoginEmployee loginEmployee) {
        redisClient.del(Constants.WAYBILL_AUTO_MATCH_LIST);
        // 组装部门信息
        Map<String, Object> filters = this.handleAreaCode(exportParam.getFilters(), false);
        // 判断并进行自动匹配
        if (checkAutoMatchButton(loginEmployee)) {
            // 调取查询条件
            ToAutoMatchWaybill toAutoMatchWaybill = waybillAutoMatchService
                    .findToAutoMatchWaybillPlan((List<String>) filters.get("areaCodeList"), loginEmployee);
            String msg = post(toAutoMatchWaybill);
            if (StringUtils.isNotBlank(msg)) {
                throw new BusinessException("autoMatchfailedMessage", "errors.autoMatchfailedMessage", msg);
            }
            // 控制自动匹配按钮是否可以使用
            redisClient.set(Constants.WAYBILL_AUTO_MATCH_BUTTON_CONTROL, true, 2 * 60);
        }
    }

    /**
     * 检查自动匹配按钮是否可以使用
     */
    @ResponseBody
    @RequestMapping(value = "checkAutoMatchBtn", method = RequestMethod.GET)
    public boolean checkAutoMatchBtn(LoginEmployee loginEmployee) {
        return checkAutoMatchButton(loginEmployee);
    }

    // 检查自动匹配按钮是否可以使用
    private boolean checkAutoMatchButton(LoginEmployee loginEmployee) {
        Object obj = redisClient.get(Constants.WAYBILL_AUTO_MATCH_BUTTON_CONTROL);
        if (null == obj || !((boolean) obj)) {
            // 可以使用
            return true;
        }
        return false;
    }

    /**
     * 确认派车
     */
    @ResponseBody
    @RequestMapping(value = "{waybillId}/success", method = RequestMethod.GET)
    public void success(@PathVariable Integer waybillId, LoginEmployee loginEmployee) {
        // 获取到缓存数据
        List<WaybillBo> list = getCacheData();
        for (WaybillBo bo : list) {
            Waybill waybill = bo.getWaybill();
            if (waybillId.equals(waybill.getWaybillId())) {
                Truck truck = bo.getTruck();
                Driver driver = bo.getDriver();
                waybillService.changeToAssigned(waybillId, driver.getDriverId(), truck.getTruckId(), null,
                         Waybill.ReceiveWay.MANUAL_ASSIGN.getCode(), null, loginEmployee);

                // 操作轨迹
                waybillOperateTrackService.insert(waybillId, OperateType.MANUAL_ASSIGN,
                        OperateApplication.BACKGROUND_SYS,
                        new WaybillOperateTrackNotRequieParam("自动匹配"), loginEmployee);

                // 重新设置运单数据
                bo.setWaybill(waybillService.getWaybill(waybillId));
                break;
            }
        }
        saveAgainWaybillInfoToRdis(list, "确认派车");
    }

    /**
     * 确认批量派车
     */
    @ResponseBody
    @RequestMapping(value = "batchSuccess", method = RequestMethod.POST)
    public String batchSuccess(@RequestBody WaybillAutoMatchVo waybillAutoMatchVo, LoginEmployee loginEmployee) {
        if (null == waybillAutoMatchVo) {
            throw new BusinessException("waybillNotSelect", "errors.waybillNotSelect");
        }
        List<Integer> waybillIdList = waybillAutoMatchVo.getWaybillIdList();
        if (CollectionUtils.isEmpty(waybillIdList)) {
            throw new BusinessException("waybillNotSelect", "errors.waybillNotSelect");
        }
        Map<Integer, Integer> waybillIdMap = new HashMap<Integer, Integer>();
        for (Integer waybillId : waybillIdList) {
            waybillIdMap.put(waybillId, waybillId);
        }
        
        StringBuffer errorMsg = new StringBuffer("");
        
        // 获取到缓存数据
        List<WaybillBo> list = getCacheData();
        for (WaybillBo bo : list) {
            Waybill waybill = bo.getWaybill();
            Integer waybillId = waybillIdMap.get(waybill.getWaybillId());
            if (null != waybillId && waybillId.equals(waybill.getWaybillId())) {
                Truck truck = bo.getTruck();
                Driver driver = bo.getDriver();
                if (null != truck && null != driver && driver.getDriverId() != null && truck.getTruckId() != null) {
                    try {
                        waybillService.changeToAssigned(waybillId, driver.getDriverId(), truck.getTruckId(), null,
                                 Waybill.ReceiveWay.MANUAL_ASSIGN.getCode(), null, loginEmployee);
                        
                        // 操作轨迹
                        waybillOperateTrackService.insert(waybillId, OperateType.MANUAL_ASSIGN,
                                OperateApplication.BACKGROUND_SYS,
                                new WaybillOperateTrackNotRequieParam("自动匹配"), loginEmployee);
                        
                        bo.setWaybill(waybillService.getWaybill(waybill.getWaybillId()));
                    } catch (Exception e) {
                        if (e instanceof BusinessException) {
                            errorMsg.append(((BusinessException) e).getErrorMessage()).append("<br/>");
                        }
                        log.info("确认批量派车失败waybillId:{}", waybillId);
                    }
                    // 重新设置运单数据
                }
            }
        }
        saveAgainWaybillInfoToRdis(list, "确认批量派车");
        return errorMsg.toString();
    }

    /**
     * 修改
     */
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody WaybillParamVo waybillParamVo, LoginEmployee loginEmployee) {
        Integer waybillId = waybillParamVo.getWaybillId();
        // 获取到缓存数据
        List<WaybillBo> list = getCacheData();
        Truck truck = truckService.findTruckByVehicleIdAndCheckExist(waybillParamVo.getVehicleId());
        Driver driver = driverService.findDriverByAmsDriverIdAndCheckExist(waybillParamVo.getAmsDriverId());
        for (WaybillBo bo : list) {
            Waybill wb = bo.getWaybill();
            if (waybillId.equals(wb.getWaybillId())) {
                wb.setFlightId(waybillParamVo.getFlightId());
                bo.setWaybill(wb);
                bo.setTruck(truck);
                bo.setDriver(driver);
                // 标明运单已经修改了车辆
                bo.setHasReplaceCar(true);
                break;
            }
        }
        // 解绑原数据
        for (WaybillBo bo : list) {
            Integer wbId = bo.getWaybill().getWaybillId();
            Truck tk = bo.getTruck();
            if (!wbId.equals(waybillId) && null != tk && truck.getPlateNumber().equals(tk.getPlateNumber())) {
                bo.setTruck(null);
                bo.setDriver(null);
                break;
            }
        }
        saveAgainWaybillInfoToRdis(list, "修改派车(update)");
    }

    /**
     * 人工干预
     */
    @ResponseBody
    @RequestMapping(value = "unbundling", method = RequestMethod.POST)
    public void unbundling(@RequestBody Waybill waybill, LoginEmployee loginEmployee) {
        Integer waybillId = waybill.getWaybillId();
        List<WaybillBo> list = getCacheData();
        for (WaybillBo bo : list) {
            Waybill wb = bo.getWaybill();
            if (waybillId.equals(wb.getWaybillId())) {
                bo.setTruck(null);
                bo.setDriver(null);
                waybillAutoMatchService.updateWaybillUnbundling(waybillId, waybill.getWaybillUnbundlingReason(),
                        loginEmployee);
                // 重新设置运单数据
                bo.setWaybill(waybillService.getWaybill(waybillId));
                break;
            }
        }
        saveAgainWaybillInfoToRdis(list, "人工干预(update)");
    }

    /**
     * 重新保存运单数据到redis
     */
    private void saveAgainWaybillInfoToRdis(List<WaybillBo> list, String operateName) {
        String json = JSON.toJSONString(list);
        log.info("WaybillAutoMatchController>operateName:{}, info:{}", operateName, json);
        if (!list.isEmpty()) {
            redisClient.set(Constants.WAYBILL_AUTO_MATCH_LIST, json, (2 * 60 * 60));
        }
    }

    /**
     * 获取缓存数据
     */
    private List<WaybillBo> getCacheData() {
        List<WaybillBo> result = new ArrayList<WaybillBo>();
        log.info("读取缓存运单数据");
        Object obj = redisClient.get(Constants.WAYBILL_AUTO_MATCH_LIST);
        if (null != obj) {
            result = JSON.parseArray(obj.toString(), WaybillBo.class);
        }
        return result;
    }

    /**
     * 自动匹配调用
     */
    private String post(ToAutoMatchWaybill toAutoMatchWaybill) {
        if (null == toAutoMatchWaybill) {
            return null;
        }
        String json = JSON.toJSONString(toAutoMatchWaybill);
        log.info("jpush param : {}.", json);
        try {
            String str = Request.Post(Constants.WAYBILL_AUTO_MATCH_URL).bodyString(json, ContentType.APPLICATION_JSON)
                    .execute().handleResponse(new ResponseHandler<String>() {
                        @Override
                        public String handleResponse(HttpResponse response)
                                throws ClientProtocolException, IOException {
                            HttpEntity entity = response.getEntity();
                            return entity != null ? EntityUtils.toString(entity, Consts.UTF_8) : null;
                        }
                    });
            log.info("jpush return : {}.", str);
            if (null != str) {
                JSONObject object = JSON.parseObject(str);
                Object obj = object.get("code");
                if (null != obj && Integer.parseInt(obj.toString()) != 0) {
                    return String.valueOf(object.get("message"));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("autoMatchfailed", "errors.autoMatchfailed");
        }
        return null;
    }

    /**
     * 测试:模拟自动匹配数据
     */
    @ResponseBody
    @RequestMapping(value = "build/data", method = RequestMethod.POST)
    public void test(@RequestBody WaybillResponse response) {
        // 参数格式
        /*
         * { "unDistributedOrderIds": [ "2017050879131003964",
         * "2017050832492003965" ], "filteredOrders": [ { "orderId":
         * "2017050836767003962", "filterReason": "没有车辆" }, { "orderId":
         * "2017050812814003963", "filterReason": "没有空闲车辆" } ],
         * "distributedOrders": [ { "orderId": "2017050840971003959",
         * "plateNumber": "津AXK672" }, { "orderId": "2017050849798003960",
         * "plateNumber": "粤AD72Y5" } ] }
         */

        // 放入自动匹配系统缓存
        log.info("测试:模拟自动匹配数据:{}", JSON.toJSONString(response));
        redisClient.set(Constants.TRUCK_MATCH_RESULT, JSON.toJSONString(response));
    }

    /**
     * 测试:删除
     */
    @ResponseBody
    @RequestMapping(value = "del/data", method = RequestMethod.GET)
    public void test(String key) {
        redisClient.del(key);
    }
}
