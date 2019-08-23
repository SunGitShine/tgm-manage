package com.juma.tgm.manage.project.controller;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.operateLog.enumeration.OperateTypeEnum;
import com.juma.tgm.project.domain.RoadMap;
import com.juma.tgm.project.domain.RoadMapPriceRule;
import com.juma.tgm.project.service.RoadMapDestAdressService;
import com.juma.tgm.project.service.RoadMapPriceRuleService;
import com.juma.tgm.project.service.RoadMapService;
import com.juma.tgm.project.service.RoadMapSrcAdressService;
import com.juma.tgm.project.vo.RoadMapQuery;
import com.juma.tgm.project.vo.RoadMapVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName RoadMapController.java
 * @Description 路线信息
 * @author Libin.Wei
 * @Date 2018年9月29日 下午2:33:15
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("roadMap")
public class RoadMapController extends BaseController {

    @Resource
    private RoadMapService roadMapService;
    @Resource
    private RoadMapPriceRuleService roadMapPriceRuleService;
    @Resource
    private RoadMapSrcAdressService roadMapSrcAdressService;
    @Resource
    private RoadMapDestAdressService roadMapDestAdressService;

    /**
     * 分页
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<RoadMapQuery> search(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) {
        return roadMapService.searchIncludeAddressAndPrice(pageCondition, loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "{roadMapId}/detail", method = RequestMethod.GET)
    public RoadMapVo detail(@PathVariable Integer roadMapId) {
        return roadMapService.findRoadMapIncludeAddressAndPrice(roadMapId);
    }

    @ResponseBody
    @RequestMapping(value = "modify", method = RequestMethod.POST)
    public void modify(@RequestBody RoadMapVo roadMapVo, LoginEmployee loginEmployee) {
        RoadMap roadMap = roadMapVo.getRoadMap();
        if (null == roadMap) {
            return;
        }
        if (null == roadMap.getRoadMapId()) {
            roadMapService.insert(roadMapVo, loginEmployee);
            super.insertLog(OperateTypeEnum.ADD_ROAD_MAP, roadMapVo.getRoadMap().getProjectId(), null, loginEmployee);
            return;
        }

        roadMapService.update(roadMapVo, loginEmployee);
        super.insertLog(OperateTypeEnum.MODIFY_ROAD_MAP, roadMapVo.getRoadMap().getProjectId(), null, loginEmployee);
    }

    /**
     * 删除路线信息
     */
    @ResponseBody
    @RequestMapping(value = "{roadMapId}/delete", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer roadMapId, LoginEmployee loginEmployee) {
        RoadMap roadMap = roadMapService.getRoadMap(roadMapId);
        roadMapService.delete(roadMapId, loginEmployee);
        super.insertLog(OperateTypeEnum.DEL_ROAD_MAP, roadMap.getProjectId(), null, loginEmployee);
    }

    /**
     * 删除单条价格信息
     */
    @ResponseBody
    @RequestMapping(value = "{roadMapValuationModelId}/priceModel/delete", method = RequestMethod.DELETE)
    public void deletePriceModel(@PathVariable Integer roadMapValuationModelId, LoginEmployee loginEmployee) {
        roadMapPriceRuleService.delete(roadMapValuationModelId, loginEmployee);
    }

    /**
     * 删除单条取货地信息
     */
    @ResponseBody
    @RequestMapping(value = "{roadMapSrcAdressId}/srcAdress/delete", method = RequestMethod.DELETE)
    public void deleteSrcAdress(@PathVariable Integer roadMapSrcAdressId, LoginEmployee loginEmployee) {
        roadMapSrcAdressService.delete(roadMapSrcAdressId, loginEmployee);
    }

    /**
     * 删除单条配送地信息
     */
    @ResponseBody
    @RequestMapping(value = "{roadMapDestAdressId}/destAdress/delete", method = RequestMethod.DELETE)
    public void deleteDestAdress(@PathVariable Integer roadMapDestAdressId, LoginEmployee loginEmployee) {
        roadMapDestAdressService.delete(roadMapDestAdressId, loginEmployee);
    }

    /**
     * 更新线路取货地、目的地
     */
    @ResponseBody
    @RequestMapping(value = "updateRoad", method = RequestMethod.POST)
    public void updateRoad(@RequestBody RoadMapVo roadMapVo, LoginEmployee loginEmployee) {
        roadMapSrcAdressService.batchUpdate(roadMapVo.getRoadMap().getRoadMapId(),roadMapVo.getListRoadMapSrcAdress(),loginEmployee);
        roadMapDestAdressService.batchUpdate(roadMapVo.getRoadMap().getRoadMapId(),roadMapVo.getListRoadMapDestAdress(),loginEmployee);
    }

    /**
     *根据线路id、车型id获取计价规则
     */
    @ResponseBody
    @RequestMapping(value = "getValuationWay", method = RequestMethod.POST)
    public RoadMapPriceRule getValuationWay(@RequestBody RoadMapPriceRule roadMapPriceRule) {
        RoadMapPriceRule r = roadMapPriceRuleService.findByRoadMapIdAndTypeId(roadMapPriceRule.getRoadMapId(), roadMapPriceRule.getTruckTypeId());
        return r;
    }
    
    @ResponseBody
    @RequestMapping(value = "query/roadMap/{projectId}/Num", method = RequestMethod.GET)
    public int queryRoadMapNum(@PathVariable Integer projectId) {
        return roadMapService.countRoadMapByProjectId(projectId);
    }
}
