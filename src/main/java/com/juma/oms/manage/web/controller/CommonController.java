package com.juma.oms.manage.web.controller;

import com.juma.conf.domain.Region;
import com.juma.conf.service.RegionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @ClassName CommonController.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2018年4月26日 下午9:45:07
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("oms/common")
public class CommonController {

    @Resource
    private RegionService regionService;

    /**
     * 注意：此接口只用于订单分发，当订单分发不使用树型时，可以删除 ；返回省市
     */
    @ResponseBody
    @RequestMapping(value = "load/{id}/region", method = RequestMethod.GET)
    public Object loadRegion(@PathVariable Integer id) {
        if (null != id) {
            if (id != 0) {
                Region region = regionService.loadRegion(id);
                if (null != region && StringUtils.isNotBlank(region.getRegionCode())
                        && region.getRegionCode().length() >= 5) {
                    return new ArrayList<Region>();
                }
            } else {
                id = null;
            }
        }
        return regionService.findChildRegion(id);
    }
}
