package com.juma.tgm.manage.waybillAccount.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.conf.domain.BusinessAreaNode;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.cms.domain.ExportTask;
import com.juma.tgm.cms.service.ExportTaskService;
import com.juma.tgm.export.domain.ExportParam;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.user.domain.CurrentUser;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillCountResponse;
import com.juma.tgm.waybill.service.WaybillQueryService;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.tgm.waybillReport.domain.WaybillReportExport;
import com.juma.tgm.waybillReport.service.WaybillReportService;

/**
 * @author weilibin
 * @version V1.0
 * @Description: 项目运单
 * @date 2016年7月5日 下午7:15:50
 */

@Deprecated
@Controller
@RequestMapping("waybill/report")
public class WaybillReportController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(WaybillReportController.class);
    @Resource
    private WaybillReportService waybillReportService;
    @Resource
    private WaybillService waybillService;
    @Resource
    private ExportTaskService exportTaskService;
    @Resource
    private WaybillQueryService waybillQueryService;

    /**
     * 运单报表分页查询
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<WaybillReportExport> reportSearch(PageCondition pageCondition, CurrentUser currentUser,
            LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, true);
        List<String> areaCodeList = new ArrayList<String>();
        for (BusinessAreaNode businessAreaNode : currentUser.getBusinessAreas()) {
            areaCodeList.add(businessAreaNode.getAreaCode());
        }
        return waybillReportService.search(pageCondition, areaCodeList, loginEmployee);
    }

    /**
     * 统计
     */
    @ResponseBody
    @RequestMapping(value = "count/freight", method = RequestMethod.POST)
    public WaybillCountResponse tableCount(@RequestBody ExportParam exportParam, LoginEmployee loginEmployee) {
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNo(1);
        pageCondition.setPageSize(1);
        pageCondition.setFilters(exportParam.getFilters());
        super.formatAreaCodeToList(pageCondition, true);
        pageCondition.getFilters().put("statusView", Waybill.StatusView.FINISH.getCode());
        pageCondition.getFilters().put("reconciliationStatus", Waybill.ReconciliationStatus.HAS_RECONCILIATION.getCode());
        return waybillService.getFreight(pageCondition, loginEmployee);
    }

    /**
     * excel导出
     */
    @ResponseBody
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public void export(@RequestBody ExportParam exportParam, LoginEmployee loginEmployee) {
        // 初始化任务
        Integer exportTaskId = exportTaskService.insertInit(ExportTask.TaskSign.WAYBILL_REPORT, exportParam,
                loginEmployee);
        try {
            // 获取数据并上传云
            PageCondition pageCondition = new PageCondition();
            pageCondition.setPageNo(1);
            pageCondition.setPageSize(Integer.MAX_VALUE);
            pageCondition.setFilters(exportParam.getFilters());
            pageCondition.getFilters().put("status", Waybill.Status.PAIED.getCode());
            pageCondition.getFilters().put("reconciliationStatus", Waybill.ReconciliationStatus.HAS_RECONCILIATION.getCode());
            super.formatAreaCodeToList(pageCondition, true);
            pageCondition.getFilters().put("backstage", true);
            waybillReportService.asyncExport(pageCondition, exportTaskId, loginEmployee);
        } catch (Exception e) {
            exportTaskService.failed(exportTaskId, e.getMessage(), loginEmployee);
            log.error(e.getMessage(), e);
        }
    }
}
