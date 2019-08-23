package com.juma.tgm.manage.cityManage.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.juma.conf.domain.Region;
import com.juma.tgm.cityManage.domain.CityManage;
import com.juma.tgm.cityManage.domain.CityManageBo;
import com.juma.tgm.cityManage.domain.CityManageInfo;
import com.juma.tgm.cityManage.service.CityManageService;
import com.juma.tgm.manage.cityManage.vo.CityManageExtVo;
import com.juma.tgm.manage.cityManage.vo.CityManageVo;
import com.juma.tgm.manage.cityManage.vo.DistrictBasicInfoVo;

@Controller
@RequestMapping("regionManage")
public class RegionManageController {

    @Resource
    private CityManageService cityManageService;

    /**
     * 列表
     */
    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Page<CityManage> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        pageCondition.getFilters().put("citySign", 2);
        pageCondition.setOrderBy(" parent_city_manage_id asc, city_manage_id desc");
        Page<CityManage> rawData = cityManageService.searchDetails(pageCondition, loginEmployee);
        return rawData;
    }

    /**
     * 增加城市
     */
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestBody CityManageVo cityManageVo, LoginEmployee loginEmployee) {
        CityManage cityManage = new CityManage();
        CityManageBo cityManageBo = new CityManageBo();
        cityManage.setCitySign(CityManage.Sign.AREA_MANAGE.getCode());
        cityManage.setParentCityManageId(cityManageVo.getParentCityManageId());
        cityManageBo.setCityManage(cityManage);
        cityManageBo.setRegionId(cityManageVo.getRegionId());
        cityManageService.insertArea(cityManageBo, loginEmployee);
    }

    /**
     * 修改
     */
    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(@RequestBody CityManageVo cityManageVo, LoginEmployee loginEmployee) {
        CityManage cityManage = new CityManage();
        cityManage.setCityManageId(cityManageVo.getCityManageId());
        cityManage.setParentCityManageId(cityManageVo.getParentCityManageId());
        cityManageService.update(cityManage, loginEmployee);
    }

    /**
     * 增加大区
     */
    @ResponseBody
    @RequestMapping(value = "/createProvince", method = RequestMethod.POST)
    public void createProvince(@RequestBody CityManage cityManage, LoginEmployee loginEmployee) {
        CityManageBo cityManageBo = new CityManageBo();
        cityManage.setParentCityManageId(0);
        cityManage.setCitySign(CityManage.Sign.AREA_MANAGE.getCode());
        cityManageBo.setCityManage(cityManage);
        cityManageService.insertArea(cityManageBo, loginEmployee);
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
     * 省份数据列表
     */
    @ResponseBody
    @RequestMapping(value = "{citySign}/provinceList", method = RequestMethod.GET)
    public CityManageInfo provinceList(@PathVariable Integer citySign, LoginEmployee loginEmployee) {
        return cityManageService.getProvinceList(citySign, loginEmployee);
    }

    /**
     * 城市数据列表
     */
    @ResponseBody
    @RequestMapping(value = "region/{regionCode}", method = RequestMethod.GET)
    public List<Region> listRegion(@PathVariable String regionCode) {
        List<Region> result = new ArrayList<Region>();
        if (StringUtils.isBlank(regionCode)) {
            CityManageInfo info = cityManageService.getProvinceList(CityManage.Sign.AREA_MANAGE.getCode(), null);
            if (null == info || null == info.getCityManageList()) {
                return result;
            }

            for (CityManage c : info.getCityManageList()) {
                Region region = new Region();
                region.setRegionCode(c.getProvinceCode());
                region.setRegionName(c.getProvinceName());
                result.add(region);
            }
            return result;
        }

        CityManage cityManage = new CityManage();
        cityManage.setCitySign(CityManage.Sign.AREA_MANAGE.getCode());
        cityManage.setProvinceCode(regionCode);
        CityManageInfo info = cityManageService.getCityList(cityManage);
        if (null == info || null == info.getCityManageList()) {
            return result;
        }

        for (CityManage c : info.getCityManageList()) {
            Region region = new Region();
            region.setRegionCode(c.getCityCode());
            region.setRegionName(c.getCityName());
            result.add(region);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "{citySign}/queryBasicInfo", method = RequestMethod.GET)
    public DistrictBasicInfoVo getQueryBasicInfo(@PathVariable Integer citySign, LoginEmployee loginEmployee) {
        DistrictBasicInfoVo vo = new DistrictBasicInfoVo();
        vo.setCityManages(cityManageService.getParaentManageList());
        vo.setCityManageInfo(cityManageService.getProvinceList(citySign, loginEmployee));

        return vo;
    }

    @ResponseBody
    @RequestMapping(value = "rest/search", method = RequestMethod.POST)
    public Page<CityManageExtVo> searchForRest(PageCondition pageCondition, LoginEmployee loginEmployee) {
        pageCondition.getFilters().put("citySign", 2);
        pageCondition.setOrderBy(" parent_city_manage_id asc, city_manage_id desc");
        Page<CityManage> rawData = cityManageService.searchDetails(pageCondition, loginEmployee);

        Page<CityManageExtVo> rstData = new Page<>(rawData.getPageNo(), rawData.getPageSize(), rawData.getTotal());

        if (CollectionUtils.isEmpty(rawData.getResults())) return rstData;
        //大区数据
        List<CityManage> largeAreaData = cityManageService.getParaentManageList();
        List<CityManageExtVo> rstList = new ArrayList<>();
        CityManageExtVo vo = null;
        for (CityManage raw : rawData.getResults()) {
            vo = new CityManageExtVo();

            BeanUtils.copyProperties(raw, vo);

            vo.setLargeAreaDatas(largeAreaData);
            rstList.add(vo);
        }
        rstData.setResults(rstList);
        return rstData;

    }

}
