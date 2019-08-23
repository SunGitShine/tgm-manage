package com.juma.tgm.manage.sop.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.manage.sop.controller.vo.ElementVo;
import com.juma.tgm.manage.sop.controller.vo.StepVo;
import com.juma.tgm.sop.domain.Element;
import com.juma.tgm.sop.domain.Sop;
import com.juma.tgm.sop.domain.Step;
import com.juma.tgm.sop.service.SopService;

@Controller
@RequestMapping(value = "sop")
public class SopController {

    @Resource
    private SopService sopService;

    @ResponseBody
    @RequestMapping(value = "searchElements", method = RequestMethod.POST)
    public Page<Element> searchElements(PageCondition cond, LoginEmployee loginEmployee) {
        return sopService.searchElements(cond, loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "searchSops", method = RequestMethod.POST)
    public Page<Sop> searchSops(PageCondition cond, LoginEmployee loginEmployee) {
        return sopService.searchSops(cond, loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "steps", method = RequestMethod.GET)
    public List<Step> steps() {
        return sopService.findAllStep();
    }

    @ResponseBody
    @RequestMapping(value = "saveSop", method = RequestMethod.POST)
    public void saveSop(@RequestBody Sop sop, LoginEmployee loginEmployee) {
        sopService.saveSop(sop, loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "element/add", method = RequestMethod.POST)
    public void elementAdd(@RequestBody Element element, LoginEmployee loginEmployee) {
        sopService.addElement(element, loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "element/update", method = RequestMethod.POST)
    public void elementUpdate(@RequestBody Element element, LoginEmployee loginEmployee) {
        sopService.updateElement(element, loginEmployee);
    }
    
    @ResponseBody
    @RequestMapping(value = "element/{elementId}", method = RequestMethod.DELETE)
    public void elementDelete(@PathVariable Integer elementId, LoginEmployee loginEmployee) {
        sopService.deleteElement(elementId, loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "element/{elementId}", method = RequestMethod.GET)
    public Element getElement(@PathVariable Integer elementId) {
        return sopService.getElement(elementId);
    }

    
    @ResponseBody
    @RequestMapping(value = "{sopId}", method = RequestMethod.GET)
    public List<StepVo> getSop(@PathVariable Integer sopId) {
        Sop sop = sopService.getSop(sopId);
        if (sop == null) throw new BusinessException("errors.exsitErr", "errors.exsitErr", "Sop " + sopId);
        String jsonStr = sop.getSopJson();
        List<StepVo> sopObjectArr = JSON.parseArray(jsonStr, StepVo.class);
        return sopObjectArr;
    }
    
    @ResponseBody
    @RequestMapping(value = "elements", method = RequestMethod.GET)
    public List<StepVo> elements() {
        List<StepVo> stepGroup = new ArrayList<StepVo>();
        List<Step> rows = sopService.findAllStep();
        for (Step step : rows) {
            StepVo stepVo = new StepVo();
            BeanUtils.copyProperties(step, stepVo);
            stepVo.setCreateTime(null);
            stepVo.setCreateUserId(null);
            stepVo.setLastUpdateTime(null);
            stepVo.setLastUpdateUserId(null);
            stepVo.setIsDelete(null);
            List<Element> elements = sopService.findElementByStepId(step.getStepId());
            for(Element element : elements) {
                ElementVo elementVo = new ElementVo();
                BeanUtils.copyProperties(element, elementVo);
                elementVo.setEditable(false);
                elementVo.setDisplay(false);
                elementVo.setCreateTime(null);
                elementVo.setCreateUserId(null);
                elementVo.setLastUpdateTime(null);
                elementVo.setLastUpdateUserId(null);
                elementVo.setIsDelete(null);
                stepVo.addElement(elementVo);
            }
            stepGroup.add(stepVo);
        }
        return stepGroup;
    }

}
