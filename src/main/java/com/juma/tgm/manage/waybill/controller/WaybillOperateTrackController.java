package com.juma.tgm.manage.waybill.controller;

import com.giants.common.tools.Page;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.common.query.QueryCond;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.vo.WaybillOperateTrackFilter;
import com.juma.tgm.waybill.vo.WaybillOperateTrackQuery;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 运单操作轨迹
 * 
 * @author weilibin
 *
 */

@Controller
@RequestMapping("waybillOperateTrack")
public class WaybillOperateTrackController {

    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;

    /**
     * 分页列表
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<WaybillOperateTrackQuery> search(@RequestBody QueryCond<WaybillOperateTrackFilter> queryCond,
        LoginEmployee loginEmployee) {
        return waybillOperateTrackService.search(queryCond);
    }
}
