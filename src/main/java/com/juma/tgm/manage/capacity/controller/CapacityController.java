package com.juma.tgm.manage.capacity.controller;


import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.capacity.domian.vo.CapacityFilter;
import com.juma.tgm.capacity.domian.vo.CapacityQuery;
import com.juma.tgm.capacity.service.CapacityService;
import com.juma.tgm.common.BaseUtil;
import com.juma.tgm.common.query.QueryCond;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.tools.service.VmsCommonService;
import com.juma.tgm.truck.domain.TruckType;
import com.juma.tgm.truck.service.TruckTypeService;
import com.juma.vms.truck.domain.Truck;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("capacity")
public class CapacityController extends BaseController {

    @Resource
    private CapacityService capacityService;
    @Resource
    private TruckTypeService truckTypeService;
    @Resource
    private VmsCommonService vmsCommonService;


    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<CapacityQuery> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        QueryCond<CapacityFilter> queryCond = new QueryCond<>();
        CapacityFilter filter = new CapacityFilter();
        super.formatAreaCodeToList(pageCondition, false);
        if (pageCondition.getFilters().containsKey("waybillId")) {
            filter.setWaybillId(Integer.parseInt(pageCondition.getFilters().get("waybillId").toString()));
        }
        this.buildParam(filter, pageCondition);
        Object obj = pageCondition.getFilters().get("isQueryAll");
        if (null == obj || BaseUtil.strToNum(obj.toString()) != 1) {
            filter.setCapacityStatus(true);
        }

        queryCond.setFilters(filter);
        queryCond.setPageNo(pageCondition.getPageNo());
        queryCond.setPageSize(pageCondition.getPageSize());

        return capacityService.searchCapacity(queryCond, loginEmployee);
    }

    // 条件转换，临时使用
    private void buildParam(CapacityFilter filter, PageCondition pageCondition) {
        Map<String, Object> filters = pageCondition.getFilters();
        if (null == filters) {
            return;
        }

        // 业务区域
         filter.setAreaCodeList((List) filters.get("areaCodeList"));

        // 司机姓名
        Object driverId = filters.get("driverId");
        if (null != driverId && StringUtils.isNumeric(driverId.toString())) {
            filter.setDriverId(Integer.parseInt(driverId.toString()));
        }

        // plateNumber
        Object plateNumber = filters.get("plateNumber");
        if (null != plateNumber) {
            Truck truck = vmsCommonService.loadTruckByPlateNumber(plateNumber.toString());
            if (null != truck) {
                filter.setTruckId(truck.getTruckId());
            } else {
                filter.setTruckId(-1);
            }
        }

        // goCityLicenseType
        Object goCityLicenseType = filters.get("goCityLicenseType");
        if (null != goCityLicenseType && StringUtils.isNumeric(goCityLicenseType.toString())) {
            filter.setGoCityLicenseType(Integer.parseInt(goCityLicenseType.toString()));
        }

        // truckTypeId
        Object truckTypeId = filters.get("truckTypeId");
        if (null != truckTypeId && StringUtils.isNumeric(truckTypeId.toString())) {
            TruckType truckType = truckTypeService.getTruckType(Integer.parseInt(truckTypeId.toString()));
            if (null != truckType) {
                filter.setVehicleBoxType(truckType.getVehicleBoxType());
                filter.setVehicleBoxLength(truckType.getTruckLengthId());
            }
        }
    }
}
