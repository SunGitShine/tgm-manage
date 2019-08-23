package com.juma.oms.manage.order.controller;

import com.giants.common.tools.Page;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.oms.order.enums.OptimizeStatus;
import com.juma.oms.order.enums.OrderStatus;
import com.juma.oms.order.service.OrderService;
import com.juma.oms.order.vo.manage.*;
import com.juma.oms.query.QueryFilter;
import com.juma.oms.query.order.OrderFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "订单管理")
@RestController
public class OrderDataController {

    @Resource
    private OrderService orderService;

    @RequestMapping(value = "order/config", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "订单管理所需配置", notes = "订单管理所需配置")
    public Map<String, Object> baseConfig() {
        Map<String, Object> configMap = new HashMap<>();
        OrderStatus[] values = OrderStatus.values();
        List<ConfigVo> orderStatusList = new ArrayList<>();
        for (OrderStatus item : values) {
            orderStatusList.add(new ConfigVo(item.getCode(), item.getDesc()));
        }
        configMap.put("orderStatusList", orderStatusList);

        OptimizeStatus[] optimizeStatuses = OptimizeStatus.values();
        List<ConfigVo> orderStatusList2 = new ArrayList<>();
        for (OptimizeStatus optimizeStatus : optimizeStatuses) {
            orderStatusList2.add(new ConfigVo(optimizeStatus.getCode(), optimizeStatus.getDesc()));
        }
        configMap.put("optimizeStatusList", orderStatusList2);
        return configMap;
    }

    @RequestMapping(value = "order/getDepotByProject", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据项目查询仓库", notes = "根据项目查询仓库")
    public List<KeyValueVo> getDepotByProject(Integer projectId) {
        return orderService.getDepotByProject(projectId);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestId", value = "排线批次号"),
            @ApiImplicitParam(name = "projectId", value = "项目Id"),
            @ApiImplicitParam(name = "orderStatus", value = "订单状态"),
            @ApiImplicitParam(name = "deliveryTimeStart", value = "用车开始日期"),
            @ApiImplicitParam(name = "deliveryTimeEnd", value = "用车结束日期"),
            @ApiImplicitParam(name = "outOrderNo", value = "外部订单号"),
            @ApiImplicitParam(name = "orderNo", value = "驹马订单号"),
            @ApiImplicitParam(name = "storeId", value = "仓库ID"),
    })
    @ApiOperation(value = "订单管理分页列表", notes = "订单管理分页列表")
    @RequestMapping(value = "order/list", method = RequestMethod.POST)
    public Page<OrderDataVo> orderPage(@RequestBody QueryFilter<OrderFilter> queryFilter, LoginEmployee loginEmployee) {
        OrderFilter filters = queryFilter.getFilters();
        if (filters != null) {
            filters.setTenantId(loginEmployee.getTenantId());
        } else {
            filters = new OrderFilter();
            filters.setTenantId(loginEmployee.getTenantId());
            queryFilter.setFilters(filters);
        }
        Page<OrderDataVo> orderDataVoPage = orderService.orderPage(queryFilter, loginEmployee);
        return orderDataVoPage;
    }

    @RequestMapping(value = "order/getOrderByPkId", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据订单ID查询订单", notes = "根据订单ID查询订单")
    public OrderDataVo getOrderByPkId(Integer orderId) {
        return orderService.getOrderByPkId(orderId);
    }

    @RequestMapping(value = "order/orderSplit", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "订单拆分", notes = "订单拆分")
    public void orderSplit(@RequestBody OrderSpitVo orderSpitVo, LoginEmployee loginEmployee) {
        orderService.orderSplit(orderSpitVo, loginEmployee);
    }

    @RequestMapping(value = "order/cancelSplit", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "取消拆分", notes = "取消拆分")
    public void cancelSplit(Integer orderId, LoginEmployee loginEmployee) {
        orderService.cancelSplit(orderId, loginEmployee);
    }

    /**
     * 智能排线
     */
    @RequestMapping(value = "order/routeRequest", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "智能排线", notes = "智能排线")
    public void routePlan(@RequestBody RouteRequestVo routePlan, LoginEmployee loginEmployee) {
        orderService.routePlan(routePlan, loginEmployee);
    }

    /**
     * 绑定运单号派车
     */
    @RequestMapping(value = "order/bingWayBillNo", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "绑定运单号派车", notes = "绑定运单号派车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestId", value = "排线批次号"),
            @ApiImplicitParam(name = "shipmentId", value = "车次号"),
            @ApiImplicitParam(name = "billNo", value = "运单号")
    })
    public void bingWayBillNo(@RequestBody BingWayBillVo bingWayBillVo, LoginEmployee loginEmployee) {
        orderService.bingWayBillNo(bingWayBillVo, loginEmployee);
    }


}
