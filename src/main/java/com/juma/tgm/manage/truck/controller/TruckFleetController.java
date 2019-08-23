package com.juma.tgm.manage.truck.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.server.vm.domain1.bo.VehicleBo;
import com.juma.tgm.capacity.domian.vo.CapacityFilter;
import com.juma.tgm.common.query.QueryCond;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.tools.service.AmsCommonService;
import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.tgm.truck.domain.TruckFleet;
import com.juma.tgm.truck.domain.TruckType;
import com.juma.tgm.truck.domain.vo.TruckDriverVo;
import com.juma.tgm.truck.service.TruckFleetService;
import com.juma.tgm.truck.service.TruckFleetTruckService;
import com.juma.tgm.truck.service.TruckTypeService;
import com.juma.tgm.truck.vo.TruckFleetTruckFilter;
import com.juma.tgm.truck.vo.TruckFleetTruckVo;
import com.juma.tgm.user.domain.CurrentUser;
import com.juma.vms.driver.domain.Driver;
import com.juma.vms.transport.domain.CapacityPool;
import com.juma.vms.truck.domain.Truck;
import java.math.BigDecimal;
import java.util.ArrayList;
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
 * @author weilibin
 * @version V1.0
 * @Description: 车队管理
 * @date 2016年5月19日 下午5:07:44
 */

@Controller
@RequestMapping("/truckFleet")
public class TruckFleetController extends BaseController {

    @Resource
    private TruckFleetService truckFleetService;
    @Resource
    private TruckFleetTruckService truckFleetTruckService;
    @Resource
    private TruckTypeService truckTypeService;
    @Resource
    private VmsCommonService vmsCommonService;
    @Resource
    private AmsCommonService amsCommonService;

    /**
     * 车队管理列表
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<TruckFleet> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, false);
        return truckFleetService.search(pageCondition, loginEmployee);
    }

    /**
     * 新增车队
     */
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(@RequestBody TruckFleet truckFleet, LoginEmployee loginEmployee) throws Exception {
        truckFleetService.insert(truckFleet, loginEmployee);
    }

    /**
     * 编辑车队
     */
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody TruckFleet truckFleet, LoginEmployee loginEmployee) throws Exception {
        truckFleetService.update(truckFleet, loginEmployee);
    }

    /**
     * 启用车队
     */
    @ResponseBody
    @RequestMapping(value = "{truckFleetId}/enable", method = RequestMethod.POST)
    public void enable(@PathVariable Integer truckFleetId, LoginEmployee loginEmployee) throws Exception {
        truckFleetService.updateToEnable(truckFleetId, loginEmployee);
    }

    /**
     * 禁用车队
     */
    @ResponseBody
    @RequestMapping(value = "{truckFleetId}/disable", method = RequestMethod.POST)
    public void disable(@PathVariable Integer truckFleetId, LoginEmployee loginEmployee) throws Exception {
        truckFleetService.updateToDisable(truckFleetId, loginEmployee);
    }

    /**
     * 修改关联车辆
     */
    @ResponseBody
    @RequestMapping(value = "truckFleetTruck/update", method = RequestMethod.POST)
    public void updateTruckFleetTruck(@RequestBody TruckFleetTruckFilter truckFleetTruckFilter,
        LoginEmployee loginEmployee) {
        truckFleetTruckService.changeTruckFleetTrucks(truckFleetTruckFilter.getTruckFleetId(),
            truckFleetTruckFilter.getListTruckId());
    }

    /**
     * 删除关联车辆
     */
    @ResponseBody
    @RequestMapping(value = "{truckFleetTruckId}/truckFleetTruck/delete", method = RequestMethod.GET)
    public void deleteTruckFleetTruck(@PathVariable Integer truckFleetTruckId) {
        truckFleetTruckService.delete(truckFleetTruckId);
    }

    /**
     * 关联车辆列表:分页查询
     */
    @ResponseBody
    @RequestMapping(value = "truckFleetTruck/search", method = RequestMethod.POST)
    public Page<TruckFleetTruckVo> truckSelectedSearch(@RequestBody QueryCond<TruckFleetTruckFilter> queryCond,
            LoginEmployee loginEmployee) {
        List<TruckFleetTruckVo> result = new ArrayList<>();
        if (queryCond.getFilters() == null && queryCond.getFilters().getTruckFleetId() == null) {
            return new Page<TruckFleetTruckVo>(queryCond.getPageNo(), queryCond.getPageSize(), 0, result);
        }

        Page<TruckFleetTruckVo> page = truckFleetTruckService.search(queryCond, loginEmployee);
        for (TruckFleetTruckVo vo : page.getResults()) {
            CapacityPool capacityPool = vmsCommonService.loadCapacityByTruckId(vo.getTruckId(), loginEmployee);
            if (null == capacityPool) {
                result.add(vo);
                continue;
            }

            Driver driver = vmsCommonService.loadDriverByDriverId(capacityPool.getDriverId());
            if (null == driver) {
                result.add(vo);
                continue;
            }
            vo.setDriverName(driver.getName());
            vo.setDriverPhone(driver.getPhone());
            result.add(vo);
        }
        return new Page<TruckFleetTruckVo>(page.getPageNo(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 关联车辆列表
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "{truckFleetId}/truckFleetTruck/list", method = RequestMethod.GET)
    public List<TruckFleetTruckVo> listTruckSelected(@PathVariable Integer truckFleetId, LoginEmployee loginEmployee) {
        List<TruckFleetTruckVo> list = truckFleetTruckService.listByTruckFleetId(truckFleetId);
//        for (TruckFleetTruckVo vo : list) {
//            CapacityPool capacityPool = vmsCommonService.loadCapacityByTruckId(vo.getTruckId(), loginEmployee);
//            if (null == capacityPool) {
//                continue;
//            }
//
//            com.juma.vms.driver.domain.Driver driver = vmsCommonService.loadDriverByDriverId(capacityPool.getDriverId());
//            if (null == driver) {
//                continue;
//            }
//            vo.setDriverName(driver.getName());
//            vo.setDriverPhone(driver.getPhone());
//        }
        return list;
    }

    /**
     * 查询车队数据展示在编辑页面
     */
    @ResponseBody
    @RequestMapping(value = "showTrucks", method = RequestMethod.POST)
    public Page<TruckDriverVo> showTrucks(@ModelAttribute("currentUser") CurrentUser currentUser,
            PageCondition pageCondition, LoginEmployee loginEmployee) {
        return new Page<>(pageCondition.getPageNo(), pageCondition.getPageSize(), 0, new ArrayList<TruckDriverVo>());
    }

    /**
     * 查询车队数据展示在编辑页面
     */
    @ResponseBody
    @RequestMapping(value = "v2/showTrucks", method = RequestMethod.POST)
    public Page<TruckDriverVo> showTrucksV2(PageCondition pageCondition, LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, false);
        QueryCond<CapacityFilter> queryCond = new QueryCond<>();
        CapacityFilter filter = new CapacityFilter();
        filter.setAreaCodeList((List) pageCondition.getFilters().get("areaCodeList"));
        if (null != pageCondition.getFilters().get("driverId") && StringUtils.isNumeric(pageCondition.getFilters().get("driverId").toString())) {
            filter.setDriverId(Integer.parseInt(pageCondition.getFilters().get("driverId").toString()));
        }
        if (null != pageCondition.getFilters().get("truckId") && StringUtils.isNumeric(pageCondition.getFilters().get("truckId").toString())) {
            filter.setTruckId(Integer.parseInt(pageCondition.getFilters().get("truckId").toString()));
        }
        queryCond.setFilters(filter);
        queryCond.setPageNo(pageCondition.getPageNo());
        queryCond.setPageSize(pageCondition.getPageSize());

        return truckFleetService.availableTruckSearch(queryCond, loginEmployee);
    }

    /**
     * 根据客户经理获取
     */
    @ResponseBody
    @RequestMapping(value = "manager/search", method = RequestMethod.POST)
    public Page<TruckDriverVo> managerSearch(PageCondition pageCondition, LoginEmployee loginEmployee) {
        List<TruckDriverVo> result = new ArrayList<TruckDriverVo>();
        if (null == pageCondition.getFilters() || null == pageCondition.getFilters().get("employeeId")) {
            return new Page<TruckDriverVo>(pageCondition.getPageNo(), pageCondition.getPageSize(), 0, result);
        }
        TruckFleet truckFleet = truckFleetService.findTruckFleetByEmployeeId(
                Integer.parseInt(pageCondition.getFilters().get("employeeId").toString()), loginEmployee.getTenantId());
        if (null == truckFleet) {
            return new Page<TruckDriverVo>(pageCondition.getPageNo(), pageCondition.getPageSize(), 0, result);
        }

        // server已经跟随迭代优化，http接口未做变更
        QueryCond<TruckFleetTruckFilter> queryCond = new QueryCond<>();
        TruckFleetTruckFilter filter = new TruckFleetTruckFilter();
        filter.setTruckFleetId(truckFleet.getTruckFleetId());
        queryCond.setFilters(filter);
        queryCond.setPageNo(pageCondition.getPageNo());
        queryCond.setPageSize(pageCondition.getPageSize());

        Page<TruckFleetTruckVo> page = truckFleetTruckService.search(queryCond, loginEmployee);
        for (TruckFleetTruckVo t : page.getResults()) {
            TruckDriverVo vo = new TruckDriverVo();
            vo.setTruckId(t.getTruckId());
            vo.setPlateNumber(t.getPlateNumber());

            Truck truck = vmsCommonService.loadTruckByTruckId(t.getTruckId());
            if (null == truck) {
                continue;
            }

            // 车型信息
            TruckType truckType = truckTypeService.findByBoxAndLength(truck.getVehicleBoxType(),
                    truck.getVehicleBoxLength(), loginEmployee.getTenantId());
            if (null != truckType) {
                vo.setTruckTypeName(truckTypeService.findTruckTypeNameByTypeId(truckType.getTruckTypeId()));
            }


            CapacityPool capacityPool = vmsCommonService.loadCapacityByTruckId(truck.getTruckId(), loginEmployee);
            if (null != capacityPool) {
                com.juma.vms.driver.domain.Driver driver = vmsCommonService.loadDriverByDriverId(capacityPool.getDriverId());
                if (null != driver) {
                    vo.setDriverName(driver.getName());
                    vo.setDriverPhone(driver.getPhone());
                }
            }


            if (null != truck.getVehicleId()) {
                // 车辆信息
                VehicleBo vehicleBo = amsCommonService.findVehicle(truck.getVehicleId(), loginEmployee);
                if (null != vehicleBo && null != vehicleBo.getVehicleExtend()) {
                    vo.setLoad(vehicleBo.getVehicleExtend().getMaxLoadCapacity());
                    vo.setVolume(vehicleBo.getVehicleExtend().getLoadVolume() == null ? BigDecimal.ZERO
                            : new BigDecimal(vehicleBo.getVehicleExtend().getLoadVolume().toString()));
                }
            }

            result.add(vo);
        }

        return new Page<TruckDriverVo>(pageCondition.getPageNo(), pageCondition.getPageSize(), page.getTotal(), result);
    }
}
