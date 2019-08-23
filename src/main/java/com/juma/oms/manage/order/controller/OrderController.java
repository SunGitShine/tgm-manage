package com.juma.oms.manage.order.controller;


import com.giants.common.tools.Page;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.oms.order.service.OrderService;
import com.juma.oms.order.vo.manage.AssignmentResult;
import com.juma.oms.order.vo.manage.OptimizeResultGroup;
import com.juma.oms.order.vo.manage.OrderView;
import com.juma.oms.order.vo.manage.ShipmentResult;
import com.juma.oms.query.QueryFilter;
import com.juma.oms.query.order.OptimizeFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "排线管理")
@RestController
public class OrderController {

    @Resource
    private OrderService orderService;

    @ApiOperation(value = "移除车次", notes = "移除车次")
    @RequestMapping(value = "request/{requestId}/shipment/{shipmentId}", method = RequestMethod.DELETE)
    public void clearShipmentId(@PathVariable String requestId, @PathVariable String shipmentId, LoginEmployee loginEmployee) {
        orderService.clearShipmentId(requestId, shipmentId, loginEmployee);
    }

    @ApiOperation(value = "订单加入车次中", notes = "订单加入车次中")
    @RequestMapping(value = "request/{requestId}/shipment/{shipmentId}/order/{orderId}", method = RequestMethod.PUT)
    public void joinToShipment(@PathVariable String requestId, @PathVariable String shipmentId, @PathVariable Integer orderId, LoginEmployee loginEmployee) {
        orderService.joinToShipment(requestId, shipmentId, orderId, loginEmployee);
    }

    @ApiOperation(value = "从车次中移除订单", notes = "从车次中移除订单")
    @RequestMapping(value = "request/{requestId}/shipment/{shipmentId}/order/{orderId}", method = RequestMethod.DELETE)
    public void kickFromShipment(@PathVariable String requestId, @PathVariable String shipmentId, @PathVariable Integer orderId, LoginEmployee loginEmployee) {
        orderService.kickFromShipment(requestId, shipmentId, orderId, loginEmployee);
    }

    @ApiOperation(value = "车次>>订单", notes = "车次>>订单")
    @RequestMapping(value = "request/{requestId}/shipment/{shipmentId}/order", method = RequestMethod.GET)
    public List<OrderView> listByShipmentId(@PathVariable String requestId, @PathVariable String shipmentId, LoginEmployee loginEmployee) {
        return orderService.listByRequestIdAndShipmentId(requestId, shipmentId, loginEmployee);
    }


    @ApiOperation(value = "已排线车次与未排线订单列表", notes = "已排线车次与未排线订单列表")
    @RequestMapping(value = "request/{requestId}/shipment", method = RequestMethod.GET)
    public ShipmentResult shipmentResult(@PathVariable String requestId, LoginEmployee loginEmployee) {
        return orderService.shipmentResult(requestId, loginEmployee);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "areaCodeList", value = "业务区域数组"),
            @ApiImplicitParam(name = "requestId", value = "排线Id"),
            @ApiImplicitParam(name = "projectId", value = "项目Id"),
            @ApiImplicitParam(name = "optimizeStatus", value = "排线状态"),
            @ApiImplicitParam(name = "isAssign", value = "是否派车"),
            @ApiImplicitParam(name = "deliveryTimeStart", value = "配送开始日期"),
            @ApiImplicitParam(name = "deliveryTimeEnd", value = "配送结束日期")
    })
    @ApiOperation(value = "排线管理分页列表", notes = "排线管理分页列表")
    @RequestMapping(value = "optimize/result", method = RequestMethod.POST)
    public Page<OptimizeResultGroup> optimizeResultGroup(@RequestBody QueryFilter<OptimizeFilter> queryFilter, LoginEmployee loginEmployee) {
        OptimizeFilter filters = queryFilter.getFilters();
        if (filters != null) {
            filters.setTenantId(loginEmployee.getTenantId());
        } else {
            filters = new OptimizeFilter();
            filters.setTenantId(loginEmployee.getTenantId());
            queryFilter.setFilters(filters);
        }
        return orderService.optimizeResultGroup(queryFilter, loginEmployee);
    }


    @ApiOperation(value = "排线 --> 车次 --> 指派车辆", notes = "排线 --> 车次 --> 指派车辆")
    @RequestMapping(value = "request/{requestId}/shipment/{shipmentId}/truck/{truckId}/driver/{driverId}", method = RequestMethod.PUT)
    public void assignmentResult(@PathVariable String requestId, @PathVariable String shipmentId,
                                 @PathVariable Integer truckId, @PathVariable Integer driverId, LoginEmployee loginEmployee) {
        orderService.assignmentWithShipmentId(requestId, shipmentId, truckId, driverId, loginEmployee);
    }


    @ApiOperation(value = "排线 --> 车次 --> 派车列表", notes = "排线 --> 车次 --> 派车列表")
    @RequestMapping(value = "request/{requestId}/shipment/assign", method = RequestMethod.GET)
    public AssignmentResult assignmentResult(@PathVariable String requestId, LoginEmployee loginEmployee) {
        return orderService.assignmentResult(requestId, loginEmployee);
    }


}
