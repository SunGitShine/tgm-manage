package com.juma.tgm.manage.fms.controller.v3;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.juma.conf.domain.ConfParamOption;
import com.juma.tgm.fms.domain.v3.bo.MonthlyBillBo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.common.Constants;
import com.juma.tgm.fms.domain.Task;
import com.juma.tgm.fms.domain.v3.ReconciliationExtraForPayable;
import com.juma.tgm.fms.domain.v3.ReconcilicationForPayable;
import com.juma.tgm.fms.domain.v3.ReconcilicationForPayableItem;
import com.juma.tgm.fms.domain.v3.bo.ReconciliationExtraForPayableBo;
import com.juma.tgm.fms.domain.v3.bo.ReconcilicationForPayableBo;
import com.juma.tgm.fms.domain.v3.enums.PayableSettleAccountTypeEnum;
import com.juma.tgm.fms.service.v3.ReconcilicationForPayableService;
import com.juma.tgm.manage.fms.controller.v3.excelVo.ReconciliationOverViewExcelVo;
import com.juma.tgm.manage.fms.controller.v3.excelVo.VendorReconciliationExcelVo;
import com.juma.tgm.manage.fms.controller.v3.excelVo.WaybillReconciliationExcelVo;
import com.juma.tgm.manage.fms.controller.vo.WorkflowTask;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.operateLog.enumeration.LogSignEnum;
import com.juma.tgm.operateLog.enumeration.OperateTypeEnum;
import com.juma.tgm.tools.service.AuthCommonService;
import com.juma.workflow.core.domain.TaskDetail;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.about.poi.writer.XssfWriter;

@Controller
@RequestMapping("/v3/reconcilicationForPayable")
public class ReconcilicationForPayableController extends BaseController {

    private static final String CONTENT_TYPE = "application/vnd.ms-excel";

    @Autowired
    private ReconcilicationForPayableService reconcilicationForPayableService;

    @Autowired
    private AuthCommonService authCommonService;

    /**
     * @Description: 定时任务 暂估月账单
     */
    @RequestMapping(value = "cron/monthlyBill", method = RequestMethod.POST)
    @ResponseBody
    public void monthlyBill(@RequestParam(required=false) String firstDay, @RequestParam(required=false) String lastDay){
        reconcilicationForPayableService.monthlyBill(firstDay,lastDay);
    }

    /**
     * @Description: 补发月账单
     */
    @RequestMapping(value = "monthlyBill/resend", method = RequestMethod.POST)
    @ResponseBody
    public void resendMonthlyBill(@RequestBody MonthlyBillBo monthlyBillBo){
        reconcilicationForPayableService.resendMonthlyBill(monthlyBillBo);
    }

    /**
     * 承运商对账单搜索
     *
     * @param pageCondition
     * @param loginEmployee
     * @return
     * @throws BusinessException
     */
    @ApiOperation(value = "承运商对账单-承运商对账单搜索", notes = "承运商对账单搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filters.areaCodeList", value = "业务范围", required = false, dataType = "String"),
            @ApiImplicitParam(name = "filters.reconcilicationNo", value = "对账单号", required = false, dataType = "String"),
            @ApiImplicitParam(name = "filters.projectName", value = "项目名称", required = false, dataType = "String"),
            @ApiImplicitParam(name = "filters.customerName", value = "客户名称", required = false, dataType = "String"),
            @ApiImplicitParam(name = "filters.startTime", value = "提交审核时间", required = false, dataType = "String"),
            @ApiImplicitParam(name = "filters.endTime", value = "结束时间", required = false, dataType = "String"),
            @ApiImplicitParam(name = "filters.approvalStatus", value = "审核状态", required = false, dataType = "String"),
    })
    @RequestMapping(value = "vendor/search", method = RequestMethod.POST)
    @ResponseBody
    public Page<ReconcilicationForPayableBo> vendorSearch(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) throws BusinessException {
        super.formatAreaCodeToList(pageCondition, false);
        super.filtersIsMapThenRemoveVulueIsNull(pageCondition);
        Page<ReconcilicationForPayableBo> pageData = reconcilicationForPayableService.vendorSearch(pageCondition, loginEmployee);
        return pageData;
    }

    /**
     * 承运商对账单详情搜索
     *
     * @param pageCondition
     * @param loginEmployee
     * @return
     * @throws BusinessException
     */
    @ApiOperation(value = "承运商对账单-账单详情搜索", notes = "账单详情搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filters.reconcilicationId", value = "对账单号id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "filters.settleType", value = "承运商(传2)/司机(传1)", required = false, dataType = "String"),
            @ApiImplicitParam(name = "filters.settleAccountName", value = "承运商名称/司机名称", required = false, dataType = "String"),
            @ApiImplicitParam(name = "filters.settleStatus", value = "结算状态", required = false, dataType = "String"),
    })
    @RequestMapping(value = "vendor/searchDetail", method = RequestMethod.POST)
    @ResponseBody
    public Page<ReconciliationExtraForPayableBo> vendorSearchDetail(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) throws BusinessException {
        super.filtersIsMapThenRemoveVulueIsNull(pageCondition);
        Page<ReconciliationExtraForPayableBo> pageData = reconcilicationForPayableService.vendorSearchDetail(pageCondition, loginEmployee);
        return pageData;
    }

    /**
     * 对账单下的运单列表
     *
     * @param pageCondition
     * @param loginEmployee
     * @return
     */
    @ApiOperation(value = "承运商对账单-运单列表", notes = "运单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filters.reconcilicationId", value = "对账单id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "filters.settleType", value = "承运商/司机类别", required = true, dataType = "String"),
            @ApiImplicitParam(name = "filters.settleAccountId", value = "承运商/司机id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "filters.waybillNo", value = "运单号", required = false, dataType = "String"),
    })
    @RequestMapping(value = "waybillDetails", method = RequestMethod.POST)
    @ResponseBody
    public Page<ReconciliationExtraForPayableBo> waybillDetails(@RequestBody PageCondition pageCondition, LoginEmployee loginEmployee) throws BusinessException {
        super.filtersIsMapThenRemoveVulueIsNull(pageCondition);
        Page<ReconciliationExtraForPayableBo> pageData = reconcilicationForPayableService.waybillDetails(pageCondition, loginEmployee);
        return pageData;
    }

    /**
     * 对账单概览
     *
     * @return
     * @throws BusinessException
     */
    @ApiOperation(value = "承运商对账单-对账单概览", notes = "对账单概览")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "对账单id", required = true, dataType = "String"),
    })
    @RequestMapping(value = "{id}/overview")
    @ResponseBody
    public ReconcilicationForPayableBo getReconciliationOverview(@PathVariable("id") Integer id, LoginEmployee loginEmployee) {
        ReconcilicationForPayableBo bo = reconcilicationForPayableService.getReconciliationOverView(id,loginEmployee);
        return bo;
    }

    /**
     * 通过对账单号获取对账单
     *
     * @param reconciliationNo
     * @return
     */
    @RequestMapping(value = "{reconciliationNo}/reconciliationNo", method = RequestMethod.GET)
    @ResponseBody
    public ReconcilicationForPayable findByReconciliationNo(@PathVariable("reconciliationNo") String reconciliationNo) throws BusinessException{
        return reconcilicationForPayableService.findByReconciliationNo( reconciliationNo );
    }

    /**
     * 承运商扣款
     *
     * @param reconciliationExtraForPayable
     * @param loginEmployee
     */
    @ApiOperation(value = "承运商对账单-承运商扣款", notes = "承运商扣款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "extraId", value = "附加表id，编辑时传extra_id，新增不传", required = false, dataType = "String"),
            @ApiImplicitParam(name = "reconciliationId", value = "对账单id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "vendorId", value = "承运商id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "oilCardFee", value = "油卡", required = true, dataType = "String"),
            @ApiImplicitParam(name = "managementFee", value = "管理费", required = true, dataType = "String"),
            @ApiImplicitParam(name = "taxRate", value = "承运商税率", required = true, dataType = "String"),
            @ApiImplicitParam(name = "referenceTaxFee", value = "计税参考", required = true, dataType = "String"),
            @ApiImplicitParam(name = "deductionTaxFee", value = "可抵扣进项税", required = true, dataType = "String"),
    })
    @RequestMapping(value = "vendor/chargre", method = RequestMethod.POST)
    @ResponseBody
    public void vendorCharge(@RequestBody ReconciliationExtraForPayable reconciliationExtraForPayable, LoginEmployee loginEmployee) {
        reconcilicationForPayableService.vendorCharge(reconciliationExtraForPayable, loginEmployee);
    }

    @RequestMapping(value = "vendor/findTaxRate", method = RequestMethod.POST)
    @ResponseBody
    public List<ConfParamOption> findTaxRate(){
        return authCommonService.listOption("VENDOR_TAX_RATE_LIST");
    }

    /**
     * 系统计算计税参考
     *
     * @param reconcilicationForPayableItem
     */
    @ApiOperation(value = "承运商对账单-系统计算计税参考", notes = "系统计算计税参考")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "settleFreight", value = "承运商含税金额", required = true, dataType = "String"),
            @ApiImplicitParam(name = "taxRate", value = "承运商税率", required = true, dataType = "String"),
    })
    @RequestMapping(value = "taxReference", method = RequestMethod.POST)
    @ResponseBody
    public BigDecimal taxReference(@RequestBody ReconcilicationForPayableItem reconcilicationForPayableItem) {
        return reconcilicationForPayableService.taxReference(reconcilicationForPayableItem);
    }

    /**
     * 处理工作流审批
     *
     * @param task
     * @param loginEmployee
     */
    @ApiOperation(value = "承运商对账单-处理工作流审批", notes = "处理工作流审批")
    @RequestMapping(value = "doTask", method = RequestMethod.POST)
    @ResponseBody
    public void doWorkFlowTask(@RequestBody Task task, LoginEmployee loginEmployee) {
//        reconcilicationForPayableService.doFinishWorkFlowTask(task, loginEmployee);
    }

    /**
     * 撤销对账单
     *
     * @param id
     * @param loginEmployee
     */
    @ApiOperation(value = "承运商对账单-撤销对账单", notes = "撤销对账单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "对账单id", required = true, dataType = "String"),
    })
    @RequestMapping(value = "{id}/cancel", method = RequestMethod.GET)
    @ResponseBody
    public void cancelReconciliation(@PathVariable("id") Integer id, LoginEmployee loginEmployee) {
        reconcilicationForPayableService.cancelReconciliation(id, loginEmployee);
        // 异步日志
        try {
            ReconcilicationForPayable reconciliation = reconcilicationForPayableService.getReconciliationById(id);
            super.insertLog(LogSignEnum.RECONCILICATION_AP, OperateTypeEnum.RECONCILICATION_AP_DEL, id,
                    reconciliation.getReconcilicationNo(),loginEmployee);
        } catch (Exception e) {

        }
    }

    /**
     * 撤销审核
     *
     * @param id
     * @param loginEmployee
     */
    @ApiOperation(value = "承运商对账单-撤销审核", notes = "撤销审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "对账单id", required = true, dataType = "String"),
    })
    @RequestMapping(value = "{id}/cancelWorkFlowTask", method = RequestMethod.GET)
    @ResponseBody
    public void cancelWorkFlowTask(@PathVariable("id") Integer id, LoginEmployee loginEmployee) {
        reconcilicationForPayableService.cancelWorkFlowTask(id, loginEmployee);
        // 异步日志
        try {
            super.insertLog(LogSignEnum.RECONCILICATION_AP, OperateTypeEnum.RECONCILICATION_AP_RECALL, id, null,
                    loginEmployee);
        } catch (Exception e) {

        }
    }

    /**
     * 提交审核
     *
     * @param bo
     * @param loginEmployee
     * @return
     */
    @ApiOperation(value = "承运商对账单-提交审核", notes = "提交审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reconciliationIds", value = "对账单id数组", required = true, dataType = "String"),
    })
    @RequestMapping(value = "submitToWorkFlow", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> submitToWorkFlow(@RequestBody ReconcilicationForPayableBo bo, LoginEmployee loginEmployee) {
        if (bo == null) {
            return null;
        }

        //验证是否有对账凭证
        List<Integer> ids = bo.getReconciliationIds();
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }

        List<Integer> incorrectIds = new ArrayList<>();
        StringBuilder statusErrorNos = new StringBuilder("");

        this.filterErrorData(ids, incorrectIds, statusErrorNos);
        ids.removeAll(incorrectIds);
        if (CollectionUtils.isNotEmpty(ids)) {
            reconcilicationForPayableService.submitToWorkFlow(bo, loginEmployee);
        }

        Map<String, String> rst = new HashMap<>();
        if (CollectionUtils.isEmpty(incorrectIds)) {
            rst.put("hasError", "false");
        } else {
            String finalMsg =  this.buildStatusErrorMsg(statusErrorNos.toString());
            rst.put("hasError", "true");
            rst.put("content", finalMsg);
        }
        return rst;
    }

    /**
     * 流程相关API -任务
     */
    @RequestMapping(value="task",method=RequestMethod.GET)
    @ResponseBody
    public WorkflowTask task(@RequestParam(required=false)String taskId, LoginEmployee loginEmployee) {
        WorkflowTask task = new WorkflowTask();
        TaskDetail taskDetail = new TaskDetail();
        if(taskId != null) {
            try {
                taskDetail = reconcilicationForPayableService.getWorkflowElement(taskId, loginEmployee);
            } catch (Exception e) {
                throw new BusinessException("workflow.error", "workflow.error");
            }
        }
        task.setTaskDetail(taskDetail);
        task.setTaskId(taskId);
        return task;
    }

    /**
     * 过滤错误数据
     */
    private void filterErrorData(List<Integer> ids, List<Integer> incorrectIds, StringBuilder statusErrorNos) {
        for (Integer id : ids) {
            ReconcilicationForPayable payable = reconcilicationForPayableService.getReconciliationById(id);
            if (NumberUtils.compare(payable.getApprovalStatus(), ReconcilicationForPayableBo.ReconciliationStatus.Append.getCode()) != 0) {
                //对账单状态错误
                incorrectIds.add(id);
                statusErrorNos.append(payable.getReconcilicationNo());
                statusErrorNos.append(",");
            }

        }
    }

    /**
     * 对账单状态错误
     * @param statusErrorNos
     * @return
     */
    private String buildStatusErrorMsg(String statusErrorNos) {
        if(StringUtils.isBlank(statusErrorNos)) return "";
        String msg = "对账单号:" + statusErrorNos + "提交失败\n对账单状态错误";
        return msg;
    }

    /**
     * 组装导出数据
     *
     * @param id
     * @param httpServletResponse
     */
    @ApiOperation(value = "承运商对账单-导出", notes = "导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "对账单id", required = true, dataType = "String"),
    })
    @RequestMapping(value = "{id}/exportExcel", method = RequestMethod.GET)
    @ResponseBody
    public void exportExcelReconciliation(@PathVariable("id") Integer id, HttpServletResponse httpServletResponse, LoginEmployee loginEmployee) {
        PageCondition pageCondition = new PageCondition();
        Map<String, Object> filter = new HashMap<>();
        filter.put("reconcilicationId", id);
        pageCondition.setFilters(filter);
        pageCondition.setPageSize(Integer.MAX_VALUE);
        pageCondition.setPageNo(1);
        //对账单概览
        ReconcilicationForPayableBo payableBo = reconcilicationForPayableService.getReconciliationOverView(id, loginEmployee);
        //承运商明细
        Page<ReconciliationExtraForPayableBo> page = reconcilicationForPayableService.vendorSearchDetail(pageCondition, loginEmployee);
        List<ReconciliationExtraForPayableBo> extraForPayableBo = (List<ReconciliationExtraForPayableBo>) page.getResults();
        //运单明细
        Page<ReconciliationExtraForPayableBo> pageData = reconcilicationForPayableService.waybillDetails(pageCondition, loginEmployee);
        List<ReconciliationExtraForPayableBo> waybillBO = (List<ReconciliationExtraForPayableBo>) pageData.getResults();
        this.doExport(payableBo, extraForPayableBo, waybillBO, httpServletResponse);
    }

    /**
     * 执行导出
     */
    private void doExport(ReconcilicationForPayableBo payableBo, List<ReconciliationExtraForPayableBo> extraForPayableBo, List<ReconciliationExtraForPayableBo> waybillBO,HttpServletResponse httpServletResponse) {
        //对账单概览
        List<ReconciliationOverViewExcelVo> excelOverViewVos = this.buildExcelOverView(payableBo);
        //承运商明细
        List<VendorReconciliationExcelVo> excelVendorVos = this.buildExcelVendorReconciliation(extraForPayableBo);
        //运单明细
        List<WaybillReconciliationExcelVo> excelWaybillVos = this.buildExcelReconciliation(waybillBO);

        String fileName = "对账单号" + payableBo.getReconcilicationNo() + "对账明细.xls";
        try {
            XssfWriter xssfWriter = new XssfWriter();
            httpServletResponse.setContentType(CONTENT_TYPE);
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
            xssfWriter.appendToSheet("对账单概览", excelOverViewVos).appendToSheet("承运商明细", excelVendorVos).appendToSheet("运单明细", excelWaybillVos).writeToOutputStream(httpServletResponse.getOutputStream());
        } catch (Exception e) {
            throw new BusinessException("export error ", "import.xlsx.export.error");
        }
    }

    /**
     * Excel - 组装对账单概览
     */
    private List<ReconciliationOverViewExcelVo> buildExcelOverView(ReconcilicationForPayableBo payableBo) {
        List<ReconciliationOverViewExcelVo> vos = new ArrayList<>();
        if (payableBo == null){
            return vos;
        }
        StringBuffer line1Buffer = new StringBuffer("");
        line1Buffer.append("客户:");
        line1Buffer.append(payableBo.getCustomerName());
        line1Buffer.append(",项目:");
        if (StringUtils.isBlank(payableBo.getProjectName())) {
            line1Buffer.append("无");
        } else {
            line1Buffer.append(payableBo.getProjectName());
        }
        line1Buffer.append(",对账单号:");
        line1Buffer.append(payableBo.getReconcilicationNo());
        ReconciliationOverViewExcelVo vo1 = new ReconciliationOverViewExcelVo();
        vo1.setOverViewString(line1Buffer.toString());
        vos.add(vo1);

        StringBuffer line2 = new StringBuffer("");
        line2.append(payableBo.getVendorCount());
        line2.append("个承运商；共");
        line2.append(payableBo.getWaybillCount());
        line2.append("个运单；承运商含税总额 ");
        line2.append(payableBo.getSumSettleFreight() == null ? 0 : payableBo.getSumSettleFreight() );
        line2.append("元；承运商不含税总额");
        line2.append(payableBo.getSumNonSettleFreight() == null ? 0 : payableBo.getSumNonSettleFreight());
        line2.append("元");
        ReconciliationOverViewExcelVo vo2 = new ReconciliationOverViewExcelVo();
        vo2.setOverViewString(line2.toString());
        vos.add(vo2);
        return vos;
    }

    /**
     * Excel - 承运商明细
     */
    private List<VendorReconciliationExcelVo> buildExcelVendorReconciliation(List<ReconciliationExtraForPayableBo> extraForPayableBo) {
        List<VendorReconciliationExcelVo> excelVos = new ArrayList<>();
        if (CollectionUtils.isEmpty(extraForPayableBo)){
            return excelVos;
        }

        VendorReconciliationExcelVo excelVo = null;
        for (ReconciliationExtraForPayableBo item : extraForPayableBo) {
            excelVo = new VendorReconciliationExcelVo();
            excelVo.setName(item.getSettleAccountName());
            excelVo.setPlateNumber(item.getPlateNumber());
            excelVo.setEstimateFreight(item.getSettleFreight());
            excelVo.setAdjustFreight(item.getAdjustFreight());
            excelVo.setAfterAdjustFreight(item.getAfterAdjustFreight());
            excelVo.setDriverTransportFee(item.getDriverTransportFee());
            excelVo.setTemporaryTransportFee(item.getTemporaryTransportFee());
            excelVo.setManagementFee(item.getManagementFee());
            excelVo.setDeductionTaxFee(item.getDeductionTaxFee());
            excelVo.setReferenceTaxFee(item.getReferenceTaxFee());
            excelVos.add(excelVo);
        }
        return excelVos;
    }

    /**
     * Excel - 运单明细
     */
    private List<WaybillReconciliationExcelVo> buildExcelReconciliation(List<ReconciliationExtraForPayableBo> waybillDetailVos) {
        List<WaybillReconciliationExcelVo> excelVos = new ArrayList<>();
        if (CollectionUtils.isEmpty(waybillDetailVos)){
            return excelVos;
        }

        WaybillReconciliationExcelVo excelVo = null;
        for (ReconciliationExtraForPayableBo vo : waybillDetailVos) {
            excelVo = new WaybillReconciliationExcelVo();

            excelVo.setWaybillNo(vo.getWaybillNo());
            if (vo.getPlanDeliveryTime() != null) {
                excelVo.setPlanDeliveryTime(Constants.YYYYMMDDHHMMSS.format(vo.getPlanDeliveryTime()));
            }
            if(vo.getSettleType().equals(PayableSettleAccountTypeEnum.DRIVER.getCode())) {
                excelVo.setName(vo.getDriverName());
            }else {
                excelVo.setName(vo.getVendorName());
            }
            excelVo.setPlateNumber(vo.getPlateNumber());
            excelVo.setShow4DriverFreight(vo.getShow4DriverFreight());
            excelVo.setAdjustFreight(vo.getAdjustFreight());
            excelVo.setAfterAdjustFreight(vo.getAfterAdjustFreight());
            excelVo.setDriverTransportFee(vo.getDriverTransportFee());
            excelVo.setTemporaryTransportFee(vo.getTemporaryTransportFee());
            excelVo.setDriverTypeName(vo.getDriverTypeName());
            excelVos.add(excelVo);
        }

        return excelVos;
    }

    /**
     * 对账单更改子公司
     */
    @RequestMapping(value = "updateDepartmentId", method = RequestMethod.POST)
    @ResponseBody
    public void updateDepartmentId(@RequestBody ReconcilicationForPayable reconcilicationForPayable, LoginEmployee loginEmployee) {
        reconcilicationForPayableService.updateDepartmentId(reconcilicationForPayable, loginEmployee);
    }

    /**
     * 根据对账单no查询子公司
     */
    @RequestMapping(value = "getDepartmentName", method = RequestMethod.POST)
    @ResponseBody
    public ReconcilicationForPayableBo getDepartmentName(@RequestBody ReconcilicationForPayable reconcilicationForPayable) {
        return reconcilicationForPayableService.getDepartmentName(reconcilicationForPayable);
    }
    
    /**
     * 
     * @Title: sendToFMS   
     * @Description: 后门  发给FMS MQ消息
     * @param: @param reconcilicationNo
     * @param: @param loginEmployee      
     * @return: void      
     * @throws
     */
    @ResponseBody
    @RequestMapping(value = "{reconcilicationNo}/sendToFms", method = RequestMethod.GET)
    public void sendToFMS(@PathVariable("reconcilicationNo")String reconcilicationNo,@RequestParam Integer tenantId,@RequestParam Integer userId) {
        LoginEmployee loginEmployee = new LoginEmployee();
        loginEmployee.setTenantId(tenantId);
        loginEmployee.setUserId(userId);
        reconcilicationForPayableService.sendToFMS(reconcilicationNo, loginEmployee);
    }
}