package com.juma.tgm.manage.truck.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import com.juma.tgm.configure.domain.TruckTypeCity;
import com.juma.tgm.configure.service.TruckTypeCityService;
import com.juma.tgm.manage.truck.vo.TruckTypeCityVo;
import com.juma.tgm.truck.domain.TruckType;
import com.juma.tgm.truck.service.TruckTypeService;

/**
 * @author weilibin
 * @version V1.0
 * @Description: 车型城市管理
 * @date 2016年7月4日 下午6:35:15
 */

@Controller
@RequestMapping("/truckTypeCity")
public class TruckTypeCityController {

    @Resource
    private TruckTypeCityService truckTypeCityService;

    @Resource
    private TruckTypeService truckTypeService;

    /**
     * 列表
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<TruckTypeCityVo> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        pageCondition.setOrderBy(" region_code, order_no asc ");
        Page<TruckTypeCity> rawData = truckTypeCityService.search(pageCondition, loginEmployee);
        Page<TruckTypeCityVo> rstData = new Page<>(rawData.getPageNo(), rawData.getPageSize(), rawData.getTotal());
        if (CollectionUtils.isEmpty(rawData.getResults()))
            return rstData;

        List<TruckTypeCityVo> vos = new ArrayList<>();
        TruckTypeCityVo vo = null;
        for (TruckTypeCity typeCity : rawData.getResults()) {
            vo = new TruckTypeCityVo();
            BeanUtils.copyProperties(typeCity, vo);

            vos.add(vo);
        }

        rstData.setResults(vos);
        return rstData;
    }

    /**
     * 创建
     */
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(@RequestBody TruckTypeCity truckTypeCity, LoginEmployee loginEmployee) {
        truckTypeCityService.insert(truckTypeCity, loginEmployee);
    }

    /**
     * 启用
     */
    @ResponseBody
    @RequestMapping(value = "{truckTypeCityId}/enable", method = RequestMethod.GET)
    public void enable(@PathVariable Integer truckTypeCityId, LoginEmployee loginEmployee) {
        truckTypeCityService.updateToEnable(truckTypeCityId, loginEmployee);
    }

    /**
     * 禁用
     */
    @ResponseBody
    @RequestMapping(value = "{truckTypeCityId}/disable", method = RequestMethod.GET)
    public void disable(@PathVariable Integer truckTypeCityId, LoginEmployee loginEmployee) {
        truckTypeCityService.updateToDisable(truckTypeCityId, loginEmployee);
    }

    /**
     * 获取所有可用的车型
     * 
     * @param loginEmployee
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAbleTruckTypeList", method = RequestMethod.GET)
    public List<TruckType> getAbleTruckTypeList(LoginEmployee loginEmployee) {
        return truckTypeService.listAllTruckTypeByOrderNoAsc(loginEmployee.getTenantId(), false);
    }

    /**
     * 城市车型详情
     * 
     * @param truckTypeCityId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "{truckTypeCityId}/detail", method = RequestMethod.GET)
    public TruckTypeCity getDetail(@PathVariable Integer truckTypeCityId) {
        TruckTypeCity truckTypeCity = truckTypeCityService.getTruckTypeCity(truckTypeCityId);
        return truckTypeCity;
    }

    /**
     * 根据城市获取城市车型
     * 
     * @param regionCode
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "{regionCode}/list", method = RequestMethod.GET)
    public List<TruckType> listByRegionCode(@PathVariable String regionCode, LoginEmployee loginEmployee) {
        List<TruckType> result = new ArrayList<TruckType>();
        if (StringUtils.isBlank(regionCode)) {
            return result;
        }
        List<TruckTypeCity> list = truckTypeCityService.listByRegionCode(
                regionCode.length() > 5 ? regionCode.substring(0, 5) : regionCode, true, loginEmployee);
        for (TruckTypeCity truckTypeCity : list) {
            TruckType truckType = truckTypeService.getTruckType(truckTypeCity.getTruckTypeId());
            if (null == truckType) {
                continue;
            }

            truckType.setTruckTypeName(truckTypeService.findTruckTypeNameByTypeId(truckType.getTruckTypeId()));
            result.add(truckType);
        }
        return result;
    }
    

    /**
     * 获取当前可用的箱型
     */
    @ResponseBody
    @RequestMapping(value = "{regionCode}/vehicleBoxType/list", method = RequestMethod.GET)
    public Set<Map<String, String>> vehicleBoxTypeList(@PathVariable String regionCode, LoginEmployee loginEmployee) {
        Set<Map<String, String>> rst = new HashSet<>();
        if (StringUtils.isBlank(regionCode)) {
            return rst;
        }

        List<TruckType> truckTypeList = truckTypeService.listByRegionCode(
                regionCode.length() > 5 ? regionCode.substring(0, 5) : regionCode, true, loginEmployee);

        Map<Integer, String> boxNameMap = this.doGetVehicleBoxTypes();
        Map<String, String> tmpMap = null;
        for (TruckType tt : truckTypeList) {
            tmpMap = new HashMap<>();
            String vehicleBoxName = boxNameMap.get(tt.getVehicleBoxType());
            if (StringUtils.isBlank(vehicleBoxName))
                continue;

            tmpMap.put("code", tt.getVehicleBoxType() + "");
            tmpMap.put("desc", vehicleBoxName);

            rst.add(tmpMap);
        }

        return rst;
    }

    //箱型列表
    private Map<Integer, String> doGetVehicleBoxTypes() {
        List<BaseEnumDomian> listVehicleBoxType = truckTypeService.listVehicleBoxType();
        if (CollectionUtils.isEmpty(listVehicleBoxType)) return null;

        Map<Integer, String> boxNameMap = new HashMap<>();
        for (BaseEnumDomian domian : listVehicleBoxType) {
            try {
                boxNameMap.put(domian.getCode(), domian.getDesc());
            } catch (Exception e) {
                continue;
            }
        }

        return boxNameMap;
    }

    /**
     * 上移
     */
    @ResponseBody
    @RequestMapping(value = "{truckTypeCityId}/up", method = RequestMethod.GET)
    public void up(@PathVariable Integer truckTypeCityId, LoginEmployee loginEmployee) {
        truckTypeCityService.updateToMoveUp(truckTypeCityId, loginEmployee);
    }

    /**
     * 下移
     */
    @ResponseBody
    @RequestMapping(value = "{truckTypeCityId}/down", method = RequestMethod.GET)
    public void down(@PathVariable Integer truckTypeCityId, LoginEmployee loginEmployee) {
        truckTypeCityService.updateToMoveDown(truckTypeCityId, loginEmployee);
    }
}
