package com.juma.tgm.manage.landing.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.configure.domain.FreightRule;
import com.juma.tgm.configure.service.FreightRuleService;
import com.juma.tgm.manage.landing.vo.FreightRuleVO;

/**
 * @ClassName LandingDistributionFreightController.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年11月16日 下午4:29:36
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("freight/rule")
public class FreightRuleController {

    private static final Logger log = LoggerFactory.getLogger(FreightRuleController.class);

    @Resource
    private FreightRuleService freightRuleService;

    /**
     * 分页查询
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<FreightRuleVO> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        List<FreightRuleVO> result = new ArrayList<FreightRuleVO>();
        Page<FreightRule> page = freightRuleService.search(pageCondition, loginEmployee);
        for (FreightRule freight : page.getResults()) {
            result.add(buildUnitToDivide(freight));
        }
        return new Page<FreightRuleVO>(page.getPageNo(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 添加
     */
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(@RequestBody FreightRuleVO freightRuleVO, LoginEmployee loginEmployee) {
        freightRuleService.insert(buildUnitToMultiply(freightRuleVO), loginEmployee);
    }

    /**
     * 编辑
     */
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody FreightRuleVO freightRuleVO, LoginEmployee loginEmployee) {
        freightRuleService.update(buildUnitToMultiply(freightRuleVO), loginEmployee);
    }

    /**
     * 启用
     */
    @ResponseBody
    @RequestMapping(value = "{freightRuleId}/enable", method = RequestMethod.GET)
    public void enable(@PathVariable Integer freightRuleId, LoginEmployee loginEmployee) {
        freightRuleService.updateToEnable(freightRuleId, loginEmployee);
    }

    /**
     * 禁用
     */
    @ResponseBody
    @RequestMapping(value = "{freightRuleId}/disable", method = RequestMethod.GET)
    public void disable(@PathVariable Integer freightRuleId, LoginEmployee loginEmployee) {
        freightRuleService.updateToDisable(freightRuleId, loginEmployee);
    }

    /**
     * 详情
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "{freightRuleId}/detail", method = RequestMethod.GET)
    public FreightRule getDetail(@PathVariable Integer freightRuleId){
        return buildUnitToDivide(freightRuleService.getFreightRule(freightRuleId));
    }

    private FreightRuleVO buildUnitToMultiply(FreightRuleVO freightRuleVO) {
        freightRuleVO.setBaseWeight(freightRuleVO.getBaseWeightDecimal() == null ? 0
                : freightRuleVO.getBaseWeightDecimal().multiply(new BigDecimal("1000")).intValue());
        freightRuleVO.setBaseVolume(freightRuleVO.getBaseVolumeDecimal() == null ? 0
                : freightRuleVO.getBaseVolumeDecimal().multiply(new BigDecimal("1000")).intValue());
        freightRuleVO.setMaxWeight(freightRuleVO.getMaxWeightDecimal() == null ? 0
                : freightRuleVO.getMaxWeightDecimal().multiply(new BigDecimal("1000")).intValue());
        freightRuleVO.setMaxVolume(freightRuleVO.getMaxVolumeDecimal() == null ? 0
                : freightRuleVO.getMaxVolumeDecimal().multiply(new BigDecimal("1000")).intValue());
        return freightRuleVO;
    }

    private FreightRuleVO buildUnitToDivide(FreightRule freightRule) {
        FreightRuleVO freightRuleVO = new FreightRuleVO();
        
        try {
            BeanUtils.copyProperties(freightRuleVO, freightRule);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            log.error(e.getMessage(), e);
        }
        
        freightRuleVO.setBaseWeightDecimal(freightRule.getBaseWeight() == null ? BigDecimal.ZERO
                : new BigDecimal(freightRule.getBaseWeight().toString()).divide(new BigDecimal("1000"), 2,
                        BigDecimal.ROUND_HALF_UP));
        freightRuleVO.setBaseVolumeDecimal(freightRule.getBaseVolume() == null ? BigDecimal.ZERO
                : new BigDecimal(freightRule.getBaseVolume().toString()).divide(new BigDecimal("1000"), 2,
                        BigDecimal.ROUND_HALF_UP));
        freightRuleVO.setMaxWeightDecimal(freightRule.getMaxWeight() == null ? BigDecimal.ZERO
                : new BigDecimal(freightRule.getMaxWeight().toString()).divide(new BigDecimal("1000"), 2,
                        BigDecimal.ROUND_HALF_UP));
        freightRuleVO.setMaxVolumeDecimal(freightRule.getMaxVolume() == null ? BigDecimal.ZERO
                : new BigDecimal(freightRule.getMaxVolume().toString()).divide(new BigDecimal("1000"), 2,
                        BigDecimal.ROUND_HALF_UP));
        return freightRuleVO;
    }
}
