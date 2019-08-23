package com.juma.tgm.manage.truck.controller;

import com.giants.common.exception.BusinessException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.tgm.truck.domain.MuilEditTruck;
import com.juma.tgm.truck.domain.Truck;
import com.juma.tgm.truck.service.TruckService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.vms.truck.external.TruckExternalFilter;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @Description: 车辆管理
 * @author weilibin
 * @date 2016年5月19日 下午5:07:44
 * @version V1.0
 */

@Controller
@RequestMapping("/truck")
public class TruckController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(TruckController.class);
    @Resource
    private WaybillService waybillService;
    @Resource
    private TruckService truckService;
    @Resource
    private VmsCommonService vmsCommonService;

    /**
     * 编辑
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody Truck truck, LoginEmployee loginEmployee) {
        ArrayList<Integer> waybillIds = new ArrayList<Integer>();
        waybillIds.add(truck.getWaybillId());
        updateFlightUsage(waybillIds, truck.getEstimateFinishTime(), loginEmployee.getUserId());
        truckService.update(truck, loginEmployee);
    }

    // 修改占用班次时间
    private List<Integer> updateFlightUsage(List<Integer> waybillIds, Date estimateFinishTime, Integer userId) {
        List<Integer> truckIds = new ArrayList<>();
        for (Integer waybillId : waybillIds) {
            Waybill waybill = waybillService.getWaybill(waybillId);
            if (null == waybill) {
                continue;
            }
            if (waybill.getPlanDeliveryTime().after(estimateFinishTime)) {
                throw new BusinessException("errors.common.prompt", "errors.common.prompt",
                        "运单ID【" + waybillId + "】填写时间必须大于运单开始时间");
            }

            truckIds.add(waybill.getTruckId());
        }
        return truckIds;
    }

    /**
     * 批量更新
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "mutilUpdate", method = RequestMethod.POST)
    public void mutilUpdate(@RequestBody MuilEditTruck truck, LoginEmployee loginEmployee) {
        List<Integer> truckIds = updateFlightUsage(truck.getWaybillIds(), truck.getEstimateFinishTime(),
                loginEmployee.getUserId());
        truck.setTruckIds(truckIds);
        truckService.mutilUpdateTruck(truck);
    }

    /**
     * 根据truckId查询
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "{truckId}/json/detail", method = RequestMethod.GET)
    public Truck jsonDetail(@PathVariable Integer truckId, LoginEmployee loginEmployee) {
        return truckService.getTruck(truckId);
    }


    @ApiOperation(value = "根据司机姓名或手机号获取", notes = "callbackPageSize拼接于URL之后，返回条数，非必填，默认15条，最大200条")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "plateNumber", value = "车牌号"),
            @ApiImplicitParam(name = "truckIdentificationNo", value = "车架号")
    })
    @ResponseBody
    @RequestMapping(value = "listTruck/like", method = RequestMethod.POST)
    public List<com.juma.vms.truck.domain.Truck> listTruckBy(@RequestBody TruckExternalFilter truckExternalFilter,
                                                             Integer callbackPageSize, LoginEmployee loginEmployee) {
        return vmsCommonService.listTruckBy(truckExternalFilter, callbackPageSize, loginEmployee);
    }

}
