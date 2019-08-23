package com.juma.tgm.manage.truck.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.base.domain.BaseEnumDomian;
import com.juma.tgm.manage.truck.vo.TruckTypeVo;
import com.juma.tgm.truck.domain.TruckType;
import com.juma.tgm.truck.service.TruckTypeService;

/**
 * @author weilibin
 * @version V1.0
 * @Description: 车型管理
 * @date 2016年7月4日 下午6:35:15
 */

@Controller
@RequestMapping("/truckType")
public class TruckTypeController {

    @Resource
    private TruckTypeService truckTypeService;

    /**
     * 列表
     */
    @ResponseBody
    @RequestMapping(value = "{paging}/search", method = RequestMethod.POST)
    public Page<TruckTypeVo> search(PageCondition pageCondition, @PathVariable boolean paging, LoginEmployee loginEmployee) {
        if (!paging) {
            pageCondition.setPageNo(1);
            pageCondition.setPageSize(Integer.MAX_VALUE);
        }
        pageCondition.getFilters().put("tenantCode", loginEmployee.getTenantCode());
        pageCondition.setOrderBy(" order_num asc ");
        Page<TruckType> rawData = truckTypeService.search(pageCondition, loginEmployee);

        Page<TruckTypeVo> rstData = new Page<>(rawData.getPageNo(), rawData.getPageSize(), rawData.getTotal());

        if (CollectionUtils.isEmpty(rawData.getResults())) return rstData;

        //箱型列表
        List<BaseEnumDomian> vehicleBoxTypes =  truckTypeService.listVehicleBoxType();
        //箱长列表
        List<BaseEnumDomian> vehicleBoxLengths = truckTypeService.listVehicleBoxlength();

        List<TruckTypeVo> vos = new ArrayList<>();
        TruckTypeVo vo = null;

        for (TruckType type : rawData.getResults()) {
            vo = new TruckTypeVo();

            BeanUtils.copyProperties(type, vo);

            vo.setVehicleBoxLengths(vehicleBoxLengths);
            vo.setVehicleBoxTypes(vehicleBoxTypes);

            vos.add(vo);
        }
        rstData.setResults(vos);

        return rstData;

    }

    /**
     * 获取箱型配置类表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getVehicleBoxTypes", method = RequestMethod.GET)
    public List<BaseEnumDomian> getVehicleBoxTypes() {
        return truckTypeService.listVehicleBoxType();
    }

    /**
     * 获取箱长配置
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getVehicleBoxLengths", method = RequestMethod.GET)
    public List<BaseEnumDomian> getVehicleBoxLengths() {
        return truckTypeService.listVehicleBoxlength();
    }

    /**
     * 创建
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(@RequestBody TruckType truckType, LoginEmployee loginEmployee) {
        truckType.setTenantCode(loginEmployee.getTenantCode());
        truckTypeService.insert(truckType, loginEmployee);
    }

    /**
     * 编辑
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody TruckType truckType, LoginEmployee loginEmployee) {
        truckType.setTenantCode(loginEmployee.getTenantCode());
        truckTypeService.update(truckType, loginEmployee);
    }
    
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public void save(@RequestBody TruckType truckType, LoginEmployee loginEmployee) {
        truckType.setTenantCode(loginEmployee.getTenantCode());
        if(truckType.getTruckTypeId() == null){
            truckTypeService.insert(truckType, loginEmployee);
        } else {
            truckTypeService.update(truckType, loginEmployee);
        }
    }

    /**
     * 启用
     */
    @ResponseBody
    @RequestMapping(value = "{truckTypeId}/enable", method = RequestMethod.GET)
    public void enable(@PathVariable Integer truckTypeId, LoginEmployee loginEmployee) {
        truckTypeService.updateToEnable(truckTypeId, loginEmployee);
    }

    /**
     * 禁用
     */
    @ResponseBody
    @RequestMapping(value = "{truckTypeId}/disable", method = RequestMethod.GET)
    public void disable(@PathVariable Integer truckTypeId, LoginEmployee loginEmployee) {
        truckTypeService.updateToDisable(truckTypeId, loginEmployee);
    }

    /**
     * 上移
     */
    @ResponseBody
    @RequestMapping(value = "{truckTypeId}/up", method = RequestMethod.GET)
    public void up(@PathVariable Integer truckTypeId, LoginEmployee loginEmployee) {
        truckTypeService.updateToUp(truckTypeId, loginEmployee);
    }

    /**
     * 下移
     */
    @ResponseBody
    @RequestMapping(value = "{truckTypeId}/down", method = RequestMethod.GET)
    public void down(@PathVariable Integer truckTypeId, LoginEmployee loginEmployee) {
        truckTypeService.updateToDown(truckTypeId, loginEmployee);
    }

    /**
     * 根据租户获取车型列表
     */
    @ResponseBody
    @RequestMapping(value = "list/truckType", method = RequestMethod.GET)
    public List<TruckType> listTruckType(LoginEmployee loginEmployee) {
        return truckTypeService.listAllTruckTypeByOrderNoAsc(loginEmployee.getTenantId(), false);
    }

    /**
     * 获取详情
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "{truckTypeId}/detail", method = RequestMethod.GET)
    public TruckType getDetail(@PathVariable Integer truckTypeId){
        TruckType type = truckTypeService.getTruckType(truckTypeId);
        return type;
    }
}
