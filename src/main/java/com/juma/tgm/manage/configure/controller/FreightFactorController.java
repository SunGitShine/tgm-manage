package com.juma.tgm.manage.configure.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.fastjson.JSON;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.common.FreightEnum;
import com.juma.tgm.configure.domain.FreightFactor;
import com.juma.tgm.configure.domain.PrivateFreightContext;
import com.juma.tgm.configure.domain.PrivateFreightFactor;
import com.juma.tgm.configure.domain.TenantFreightFactor;
import com.juma.tgm.configure.service.FreightFactorService;
import com.juma.tgm.configure.service.PrivateFreightFactorService;
import com.juma.tgm.manage.configure.controller.dto.PrivateFreightFactorQuery;
import com.juma.tgm.manage.configure.controller.vo.PrivateFactorColumn;
import com.juma.tgm.manage.configure.controller.vo.PrivateFactorVo;

@Controller
@RequestMapping("freight")
public class FreightFactorController {
    
    @Resource
    private FreightFactorService freightFactorService;
    
    @Resource
    private PrivateFreightFactorService privateFreightFactorService;
    
    
    /**
     * 分页租户  整车、零担 计价条件定义查询
     */
    @ResponseBody
    @RequestMapping(value = "private/{freightWay}/search", method = RequestMethod.POST)
    public Page<FreightFactor> privateSearch(@PathVariable Integer freightWay, PageCondition pageCondition, LoginEmployee loginEmployee) {
        return freightFactorService.getPager(freightWay,pageCondition,loginEmployee);
    }
    
    /**
     * 分页租户  整车、零担 计价条件查询
     */
    @ResponseBody
    @RequestMapping(value = "private/search", method = RequestMethod.POST)
    public Page<PrivateFreightFactor> privateSearch(PrivateFreightFactorQuery query, PageCondition pageCondition, LoginEmployee loginEmployee) {
        String regionCode = null;
        Integer freightWay = null;
        Integer truckTypeId = null;
        
        Map<String,Object> filters = pageCondition.getFilters();
        if(filters != null && !filters.isEmpty()) {
            Object _regionCode = filters.get("regionCode");
            if(_regionCode !=null) {
                regionCode = _regionCode.toString();
            }
            Object _freightWay = filters.get("freightWay");
            if(_freightWay != null) {
                freightWay = Integer.valueOf(_freightWay.toString());
            }
            Object _truckTypeId = filters.get("truckTypeId");
            if(_truckTypeId != null) {
                truckTypeId = Integer.valueOf(_truckTypeId.toString());
            }
        }
        
        return privateFreightFactorService.getPager(regionCode,freightWay,truckTypeId,pageCondition,loginEmployee);
    }
    
    /**
     * 
     * @Title: factor   
     * @Description: 计价所有的条件
     */
    @ResponseBody
    @RequestMapping(value = "factor", method = RequestMethod.GET)
    public List<FreightFactor> factor() {
        return freightFactorService.findAll();
    }
    
    /**
     * 
     * @Title: factor   
     * @Description: 计价所有的条件
     */
    @ResponseBody
    @RequestMapping(value = "factor/save", method = RequestMethod.POST)
    public void saveFactor(@RequestBody FreightFactor domain , LoginEmployee loginEmployee) {
         freightFactorService.save(domain, loginEmployee);
    }
    
    
    /**
     * 
     * @Title: factor.save
     * @Description: 保存租户对应计价条件
     */
    @ResponseBody
    @RequestMapping(value = "private/save", method = RequestMethod.POST)
    public void savePrivate(@RequestBody TenantFreightFactor domain, LoginEmployee loginEmployee) {
        privateFreightFactorService.saveTenantFreightFactor(domain, loginEmployee);
    }
    
    
    /**
     * 
     * @Title: factor.get
     * @Description: 动态根据整车、零担显示计价条件
     */
    @ResponseBody
    @RequestMapping(value = "private/new/{freightWay}", method = RequestMethod.GET)
    public TenantFreightFactor newPrivate(@PathVariable Integer freightWay, LoginEmployee loginEmployee) {
        return privateFreightFactorService.findByFreightWay(freightWay, loginEmployee);
    }
    
    /**
     * 
     * @Title: factor.disable
     * @Description: 禁用计价规则
     */
    @ResponseBody
    @RequestMapping(value = "private/{privateFreightFactorId}/disable", method = RequestMethod.GET)
    public void disable(@PathVariable Integer privateFreightFactorId, LoginEmployee loginEmployee) {
        privateFreightFactorService.disable(privateFreightFactorId, loginEmployee);
    }
    
    /**
     * 
     * @throws ParseException 
     * @Title: factor.get
     * @Description: 动态根据整车、零担显示计价条件
     */
    @ResponseBody
    @RequestMapping(value = "private/{privateFreightFactorId}/edit", method = RequestMethod.GET)
    public PrivateFreightFactor editPrivate(@PathVariable Integer privateFreightFactorId, LoginEmployee loginEmployee) {
        
        Map<String,FreightFactor> factorMap = new HashMap<String,FreightFactor>();
        List<FreightFactor> rows = freightFactorService.findAll();
        for(FreightFactor factor : rows) {
            factorMap.put(factor.getLabelInputName(), factor);
        }
        
        PrivateFactorVo vo = new PrivateFactorVo();
        PrivateFreightFactor privateFreightFactor = privateFreightFactorService.findByPrimaryKey(privateFreightFactorId);
        if(privateFreightFactor != null && StringUtils.isNotBlank(privateFreightFactor.getFactorJson())) {
            Map<String,Object> mapTypes = JSON.parseObject(privateFreightFactor.getFactorJson(), Map.class);
            Iterator<Entry<String, Object>> it =  mapTypes.entrySet().iterator();
            while(it.hasNext()) {
                Entry<String, Object> entry = it.next();
                FreightFactor factor = factorMap.get(entry.getKey());
                if(factor != null) {
                    PrivateFactorColumn col = new PrivateFactorColumn();
                    col.setLabelName(factor.getLabelName());
                    col.setInputValue(String.valueOf(entry.getValue()));
                    col.setLabelInputName(entry.getKey());
                    col.setRequired(factor.getRequired());
                    vo.getCols().add(col);
                } else {
                    vo.setKmMap(entry.getValue());
                }
            }
            privateFreightFactor.setFactorJson(JSON.toJSONString(vo));
        }
        
        return privateFreightFactor;
    }
    
    /**
     * 
     * @Title: factor.save
     * @Description: 保存计价条件值
     */
    @ResponseBody
    @RequestMapping(value = "private/value/save", method = RequestMethod.POST)
    public void savePrivateValue(@RequestBody PrivateFreightFactor domain, LoginEmployee loginEmployee) {
        privateFreightFactorService.save(domain, loginEmployee);
    }

    /**
     * 根据条件获取计价维度列表
     */
    @ResponseBody
    @RequestMapping(value = "list/freightFactor", method = RequestMethod.GET)
    public List<FreightFactor> listFreightFactor(String freightWay, LoginEmployee loginEmployee) {
        List<FreightFactor> result = new ArrayList<FreightFactor>();
        if (StringUtils.isBlank(freightWay)) {
            return result;
        }

        for (FreightEnum f : FreightEnum.values()) {
            if (f.toString().toUpperCase().equals(freightWay.toString().toUpperCase())) {
                return freightFactorService.findByFreightWay(f.getCode(), loginEmployee);
            }
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "test", method = RequestMethod.GET)
    public BigDecimal test(@RequestParam(defaultValue="00")String regionCode
            , @RequestParam(defaultValue="1")Integer freightWay
            ,Integer  truckTypeId, @RequestParam(defaultValue="1")BigDecimal km
            ,String functionIds
            ,BigDecimal shipmentMin
            ,@RequestParam(defaultValue="1")BigDecimal volumn, @RequestParam(defaultValue="1")BigDecimal weight, LoginEmployee loginEmployee) {
        
        PrivateFreightContext ctx = new PrivateFreightContext();
        ctx.setKm(km);
        ctx.setVolumn(volumn);
        ctx.setWeight(weight);
        ctx.setShipmentMin(shipmentMin);
        
        ctx.setFunctionIds(functionIds);
        
        return privateFreightFactorService.calFreight(regionCode,freightWay,truckTypeId,ctx, loginEmployee);
    }

}
