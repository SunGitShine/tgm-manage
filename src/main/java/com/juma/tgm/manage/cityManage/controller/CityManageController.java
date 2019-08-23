package com.juma.tgm.manage.cityManage.controller;

import java.util.List;

import javax.annotation.Resource;

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
import com.juma.tgm.cityManage.domain.CityManageBo;
import com.juma.tgm.cityManage.service.CityManageService;
import com.juma.tgm.manage.cityManage.vo.CityManageVo;

@Controller
@RequestMapping("cityManage")
public class CityManageController {

    @Resource
    private CityManageService cityManageService;

    /**
     * 列表
     */
    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Page<CityManage> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        pageCondition.setOrderBy("order_no asc, province_code asc, city_manage_id desc");
        return cityManageService.searchDetails(pageCondition, loginEmployee);
    }
    
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<CityManage> search() {
        return cityManageService.getParaentManageList();
    }

    /**
     * 增加城市
     */
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestBody CityManageVo cityManageVo, LoginEmployee loginEmployee) {
        CityManage cityManage = new CityManage();
        CityManageBo cityManageBo = new CityManageBo();
        cityManage.setCitySign(cityManageVo.getSign());
        cityManage.setParentCityManageId(0);
        cityManageBo.setCityManage(cityManage);
        cityManageBo.setRegionId(cityManageVo.getRegionId());
        cityManageService.insert(cityManageBo, loginEmployee);
    }

    /**
     * 启用
     */
    @ResponseBody
    @RequestMapping(value = "/{cityManageId}/enable", method = RequestMethod.GET)
    public void enable(@PathVariable Integer cityManageId, LoginEmployee loginEmployee) {
        cityManageService.updateToEnable(cityManageId, loginEmployee);
    }

    /**
     * 停用
     */
    @ResponseBody
    @RequestMapping(value = "/{cityManageId}/disable", method = RequestMethod.GET)
    public void disable(@PathVariable Integer cityManageId, LoginEmployee loginEmployee) {
        cityManageService.updateToDisable(cityManageId, loginEmployee);
    }

    /**
     * 上移
     */
    @ResponseBody
    @RequestMapping(value = "/{cityManageId}/up", method = RequestMethod.GET)
    public void up(@PathVariable Integer cityManageId, LoginEmployee loginEmployee) {
        cityManageService.updateToUp(cityManageId, loginEmployee);
    }

    /**
     * 下移
     */
    @ResponseBody
    @RequestMapping(value = "/{cityManageId}/down", method = RequestMethod.GET)
    public void down(@PathVariable Integer cityManageId, LoginEmployee loginEmployee) {
        cityManageService.updateToDown(cityManageId, loginEmployee);
    }
}
