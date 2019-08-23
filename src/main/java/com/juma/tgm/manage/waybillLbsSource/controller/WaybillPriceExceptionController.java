package com.juma.tgm.manage.waybillLbsSource.controller;

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
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.cms.domain.ExportTask;
import com.juma.tgm.cms.service.ExportTaskService;
import com.juma.tgm.costReimbursed.service.CostReimbursedService;
import com.juma.tgm.export.domain.ExportParam;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.tgm.waybillLbsSource.domain.WaybillPriceExceptionQuery;
import com.juma.tgm.waybillLbsSource.service.WaybillPriceExceptionService;

/**
 * Created by shawn_lin on 2017/8/22.
 */
@Controller
@RequestMapping(value = "waybill/price")
public class WaybillPriceExceptionController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(WaybillPriceExceptionController.class);
    @Resource
    private WaybillService waybillService;
    @Resource
    private CostReimbursedService costReimbursedService;
    @Resource
    private ExportTaskService exportTaskService;
    @Resource
    private WaybillPriceExceptionService waybillPriceExceptionService;

    @ResponseBody
    @RequestMapping(value = "search",method = RequestMethod.POST)
    public Page<WaybillPriceExceptionQuery> search(PageCondition pageCondition, LoginEmployee loginEmployee){
        super.formatAreaCodeToList(pageCondition, true);
        return waybillPriceExceptionService.search(pageCondition,loginEmployee);
    }

    //数据excel导出
    @ResponseBody
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public void export(@RequestBody ExportParam exportParam, LoginEmployee loginEmployee) {
        // 初始化任务
        Integer exportTaskId = exportTaskService.insertInit(ExportTask.TaskSign.PRICE_EXCEPTION, exportParam,
                loginEmployee);
        try {
            // 获取数据并上传云
            PageCondition pageCondition = new PageCondition();
            pageCondition.setPageNo(1);
            pageCondition.setPageSize(Integer.MAX_VALUE);
            pageCondition.setFilters(exportParam.getFilters());
            super.formatAreaCodeToList(pageCondition, true);
            waybillPriceExceptionService.asyncExport(pageCondition,exportTaskId,loginEmployee);
        } catch (Exception e) {
            exportTaskService.failed(exportTaskId, e.getMessage(), loginEmployee);
            log.error(e.getMessage(), e);
        }
    }

}
