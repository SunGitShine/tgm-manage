package com.juma.tgm.manage.truck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.cityManage.domain.CityManage;
import com.juma.tgm.cityManage.domain.CityManageInfo;
import com.juma.tgm.cityManage.service.CityManageService;
import com.juma.tgm.manage.truck.vo.FreightVo;
import com.juma.tgm.truck.domain.AdditionalFunctionFreightBo;
import com.juma.tgm.truck.domain.TruckTypeFreight;
import com.juma.tgm.truck.domain.TruckTypeFreightBo;
import com.juma.tgm.truck.service.TruckTypeFreightService;

/**
 * 
 * @Description: 价格配置
 * @author weilibin
 * @date 2016年5月19日 下午6:35:02
 * @version V1.0
 */

@Controller
@RequestMapping("/truckTypeFreight")
public class TruckTypeFreightController {

    @Autowired
    private TruckTypeFreightService truckTypeFreightService;
    @Autowired
    private CityManageService cityManageService;
    
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<TruckTypeFreightBo> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        return truckTypeFreightService.search(pageCondition, loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "{freightId}/get", method = RequestMethod.GET)
    public TruckTypeFreightBo get(@PathVariable Integer freightId) {
        return truckTypeFreightService.getTruckTypeFreigthById(freightId);
    }


    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public void save(@RequestBody FreightVo freightVo, LoginEmployee loginEmployee)  {
        if(freightVo.getFreightId() == null){
            truckTypeFreightService.insert(structFreight(freightVo), loginEmployee);
        } else {
            truckTypeFreightService.update(structFreight(freightVo), loginEmployee);
        }
    }

    /**
     * 启用
     */
    @ResponseBody
    @RequestMapping(value = "/{freightId}/enable", method = RequestMethod.GET)
    public void enable(@PathVariable Integer freightId, LoginEmployee loginEmployee) {
    	truckTypeFreightService.updateToEnable(freightId, loginEmployee);
    }

    /**
     * 停用
     */
    @ResponseBody
    @RequestMapping(value = "/{freightId}/disable", method = RequestMethod.GET)
    public void disable(@PathVariable Integer freightId, LoginEmployee loginEmployee) {
    	truckTypeFreightService.updateToDisable(freightId, loginEmployee);
    }

    /**
     * 城市数据列表
     */
    @ResponseBody
    @RequestMapping(value = "cityList", method = RequestMethod.POST)
    public CityManageInfo provinceList(@RequestBody CityManage cityManage) {
        cityManage.setCitySign(CityManage.Sign.AREA_MANAGE.getCode());
        return cityManageService.getCityList(cityManage);
    }

    private TruckTypeFreightBo structFreight(FreightVo freightVo) {
        TruckTypeFreightBo freightBo = new TruckTypeFreightBo();
        AdditionalFunctionFreightBo additionalFunctionFreightBo = new AdditionalFunctionFreightBo();
        TruckTypeFreight freight = new TruckTypeFreight();
        freight.setFreightId(freightVo.getFreightId());
        freight.setTruckTypeId(freightVo.getTruckTypeId());
        
        freight.setPricePerDay(freightVo.getPricePerDay());
        freight.setLowestFreight(freightVo.getLowestFreight());
        freight.setLowestMileage(freightVo.getLowestMileage());
        freight.setBeyondUnitPrice(freightVo.getBeyondUnitPrice());
        freight.setDistributionPointPrice(freightVo.getDistributionPointPrice());
        freight.setHighestFreight(freightVo.getHighestFreight());
        freight.setCityManageId(freightVo.getCityManageId());
        
        additionalFunctionFreightBo.setDriverHandlingCost(freightVo.getDriverHandlingCost());
        additionalFunctionFreightBo.setLaborerHandlingCost(freightVo.getLaborerHandlingCost());
        additionalFunctionFreightBo.setColdChainFreight(freightVo.getColdChainFreight());
        additionalFunctionFreightBo.setBackStorageFreight(freightVo.getBackStorageFreight());
        additionalFunctionFreightBo.setCarryFreight(freightVo.getCarryFreight());
        additionalFunctionFreightBo.setCollectionPaymentFreight(freightVo.getCollectionPaymentFreight());
        additionalFunctionFreightBo.setEntryLicenseFreight(freightVo.getEntryLicenseFreight());
        additionalFunctionFreightBo.setReceiptFreight(freightVo.getReceiptFreight());
        
        freightBo.setTruckTypeFreight(freight);
        freightBo.setAffFreight(additionalFunctionFreightBo);
        freightBo.setAddWay(freightVo.getAddWay());
        return freightBo;
    }

    /**
     * 新增：已过期
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(@RequestBody FreightVo freightVo, LoginEmployee loginEmployee) throws Exception {
        truckTypeFreightService.insert(structFreight(freightVo), loginEmployee);
    }

    /**
     * 编辑：已过期
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody FreightVo freightVo, LoginEmployee loginEmployee) throws Exception  {
        truckTypeFreightService.update(structFreight(freightVo), loginEmployee);

    }
}
