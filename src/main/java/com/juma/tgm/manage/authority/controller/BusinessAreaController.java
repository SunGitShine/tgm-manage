package com.juma.tgm.manage.authority.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.juma.auth.conf.domain.BusinessArea;
import com.juma.auth.conf.domain.BusinessAreaNode;
import com.juma.auth.conf.service.BusinessAreaService;
import com.juma.auth.employee.domain.DepartmentBo;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.DepartmentService;
import com.juma.tgm.businessArea.service.TgmBusinessAreaService;
import com.juma.tgm.manage.authority.vo.BusinessAreaVo;

/**
 * @author Libin.Wei
 * @version 1.0.0
 * @ClassName BusinessAreaController.java
 * @Description 业务范围
 * @Date 2017年8月10日 下午4:25:43
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("businessArea")
public class BusinessAreaController {

    @Resource
    private BusinessAreaService businessAreaService;
    @Resource
    private DepartmentService departmentService;
    @Resource
    private TgmBusinessAreaService tgmBusinessAreaService;

    @RequestMapping(value = "children", method = RequestMethod.GET)
    @ResponseBody
    public List<BusinessArea> listChildren(String areaCode, String defaultAreaCode, LoginEmployee loginEmployee) {
        if (areaCode == null) {
            if (defaultAreaCode == null) {
                return listEmployeeBusinessArea(loginEmployee);

            } else if (defaultAreaCode.trim().equals("")) {
                return businessAreaService.findChildBusinessArea("", loginEmployee);

            } else {
                return businessAreaService.findChildBusinessArea(defaultAreaCode, loginEmployee);
            }
        }
        return businessAreaService.findChildBusinessArea(areaCode, loginEmployee);
    }

    /**
     * vue 树
     */
    @RequestMapping(value = "/{areaCode}/children", method = RequestMethod.GET)
    @ResponseBody
    public List<BusinessArea> listChildren2(@PathVariable String areaCode, LoginEmployee loginEmployee) {
        String _areaCode = StringUtils.isBlank(areaCode) ? "0" : areaCode;
        if (_areaCode.equals("0")) {
            return listEmployeeBusinessArea(loginEmployee);
        }
        return businessAreaService.findChildBusinessArea(areaCode, loginEmployee);
    }

    /**
     * vue 树
     */
    @RequestMapping(value = "/{departmentCode}/departmentBusinessArea", method = RequestMethod.GET)
    @ResponseBody
    public List<BusinessArea> listDepartmentBusinessArea2(@PathVariable String departmentCode, LoginEmployee loginEmployee) {
        String _departmentCode = StringUtils.isBlank(departmentCode) ? "0" : departmentCode;

        DepartmentBo departmentBo = departmentService.findDepartmentBo(_departmentCode, loginEmployee);
        if (departmentBo == null || CollectionUtils.isEmpty(departmentBo.getBusinessAreas())) {
            return new ArrayList<>(0);
        }
        return departmentBo.getBusinessAreas();
    }

    @RequestMapping(value = "departmentBusinessArea", method = RequestMethod.GET)
    @ResponseBody
    public List<BusinessArea> listDepartmentBusinessArea(String departmentCode, LoginEmployee loginEmployee) {
        if (departmentCode == null) {
            return new ArrayList<>(0);
        }
        DepartmentBo departmentBo = departmentService.findDepartmentBo(departmentCode, loginEmployee);
        if (departmentBo == null || CollectionUtils.isEmpty(departmentBo.getBusinessAreas())) {
            return new ArrayList<>(0);
        }
        return departmentBo.getBusinessAreas();
    }

    @RequestMapping(value = "employeeBusinessArea", method = RequestMethod.GET)
    @ResponseBody
    public List<BusinessArea> listEmployeeBusinessArea(LoginEmployee loginEmployee) {
        boolean hasBusinessArea = loginEmployee.getLoginDepartment() != null
                && CollectionUtils.isNotEmpty(loginEmployee.getLoginDepartment().getBusinessAreas());
        if (!hasBusinessArea) {
            return new ArrayList<>(0);
        }
        List<LoginEmployee.LoginDepartment.BusinessArea> businessAreas = loginEmployee.getLoginDepartment()
                .getBusinessAreas();
        List<BusinessArea> businessAreaList = new ArrayList<>(businessAreas.size());
        for (LoginEmployee.LoginDepartment.BusinessArea businessArea : businessAreas) {
            BusinessArea one = businessAreaService.loadBusinessArea(businessArea.getAreaCode(), loginEmployee);
            if (one == null) {
                continue;
            }
            businessAreaList.add(one);
        }
        return businessAreaList;
    }

    @RequestMapping(value = "listLogicBusinessAreaParallel", method = RequestMethod.GET)
    @ResponseBody
    public List<BusinessAreaNode> listLogicBusinessAreaParallel(LoginEmployee loginEmployee) {
        List<BusinessAreaNode> logicBusinessArea = tgmBusinessAreaService.getLogicBusinessAreaParallel(loginEmployee);
        for (BusinessAreaNode businessAreaNode : logicBusinessArea) {
            if (!businessAreaNode.isLogic()) {
                continue;
            }
            businessAreaNode.setAreaName(businessAreaNode.getAreaName() + "(可分享)");
        }
        return logicBusinessArea;
    }

    /**
     * 获取非逻辑区域集合
     */
    @ResponseBody
    @RequestMapping(value = "noLogic/area/list", method = RequestMethod.GET)
    public List<String> noLogicAreaList(LoginEmployee loginEmployee) {
        List<BusinessAreaNode> logicBusinessArea = tgmBusinessAreaService.getLogicBusinessAreaParallel(loginEmployee);
        List<String> areaCodeList = new ArrayList<String>();
        for (BusinessAreaNode businessAreaNode : logicBusinessArea) {
            if (!businessAreaNode.isLogic()) {
                areaCodeList.add(businessAreaNode.getAreaCode());
            }
        }
        return areaCodeList;
    }

    /**
     * 获取可分享的业务区域
     *
     * @param areaId
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "/{areaId}/shareArea", method = RequestMethod.GET)
    @ResponseBody
    public List<BusinessAreaVo> loadShareBusinessArea(@PathVariable("areaId") Integer areaId, LoginEmployee loginEmployee) {
        if (NumberUtils.compare(areaId, 0) == 0) {
            areaId = null;
        }
        List<BusinessArea> businessAreas = businessAreaService.findChildBusinessArea(areaId, loginEmployee);
        List<BusinessAreaVo> vos = new ArrayList<>();
        if (CollectionUtils.isEmpty(businessAreas)) return vos;

        BusinessAreaVo vo = null;
        for (BusinessArea area : businessAreas) {
            vo = new BusinessAreaVo();
            BeanUtils.copyProperties(area, vo);

            vos.add(vo);
            if (!vo.isLogic()) continue;
            vo.setAreaName(vo.getAreaName() + "(可分享)");
        }

        return vos;
    }

}
