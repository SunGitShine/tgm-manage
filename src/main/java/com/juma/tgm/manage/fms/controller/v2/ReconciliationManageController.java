package com.juma.tgm.manage.fms.controller.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageQueryCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.common.Constants;
import com.juma.tgm.fms.domain.Reconciliation;
import com.juma.tgm.fms.domain.Task;
import com.juma.tgm.fms.domain.v2.ReconciliationNew;
import com.juma.tgm.fms.domain.v2.vo.CustomerStatisticsQueryVo;
import com.juma.tgm.fms.domain.v2.vo.CustomerStatisticsVo;
import com.juma.tgm.fms.domain.v2.vo.ReconciliationChangeLogByCarVo;
import com.juma.tgm.fms.domain.v2.vo.ReconciliationChangeLogByTenantVo;
import com.juma.tgm.fms.domain.v2.vo.ReconciliationForVehicleQueryVo;
import com.juma.tgm.fms.domain.v2.vo.ReconciliationForVehicleVo;
import com.juma.tgm.fms.domain.v2.vo.ReconciliationOverviewVo;
import com.juma.tgm.fms.domain.v2.vo.ReconciliationQueryVo;
import com.juma.tgm.fms.domain.v2.vo.ReconciliationVo;
import com.juma.tgm.fms.domain.v2.vo.ReconciliationWaybillDetailQueryVo;
import com.juma.tgm.fms.domain.v2.vo.ReconciliationWaybillDetailVo;
import com.juma.tgm.fms.service.v2.ReconciliationChangeLogService;
import com.juma.tgm.fms.service.v2.ReconciliationService;
import com.juma.tgm.imageUploadManage.domain.ImageUploadManage;
import com.juma.tgm.imageUploadManage.service.ImageUploadManageService;
import com.juma.tgm.manage.fms.controller.v2.vo.excelVo.CustomerChangeLogExcelVo;
import com.juma.tgm.manage.fms.controller.v2.vo.excelVo.ReconciliationOverViewExcelVo;
import com.juma.tgm.manage.fms.controller.v2.vo.excelVo.VehicleChangeLogExcelVo;
import com.juma.tgm.manage.fms.controller.v2.vo.excelVo.VehicleReconciliationExcelVo;
import com.juma.tgm.manage.fms.controller.v2.vo.excelVo.WaybillReconciliationExcelVo;

import me.about.poi.writer.XssfWriter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: ReconciliationManage
 * @Description:
 * @author: liang
 * @date: 2018-06-07 20:55
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
@RestController
public class ReconciliationManageController {
    private static final String CONTENT_TYPE = "application/vnd.ms-excel";

    @Autowired
    private ReconciliationService reconciliationServiceV2;
    @Autowired
    private ImageUploadManageService imageUploadManageService;

    @Autowired
    private ReconciliationChangeLogService reconciliationChangeLogService;

    /**
     * 对账单列表queryVo
     */
    public static class ReconciliationManageQueryVo extends PageQueryCondition<ReconciliationQueryVo> {

        public ReconciliationManageQueryVo() {
        }
    }

    /**
     * 车辆对账单queryVO
     */
    public static class VehicleReconciliationQueryVo extends PageQueryCondition<ReconciliationForVehicleQueryVo> {

        public VehicleReconciliationQueryVo() {
        }
    }


    /**
     * 对账单下运单列表查询
     */
    public static class ReconciliationWaybillQueryVo extends PageQueryCondition<ReconciliationWaybillDetailQueryVo> {
        public ReconciliationWaybillQueryVo() {
        }
    }

    /**
     * 对账单列表
     *
     * @param pageQueryCondition
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "reconciliationManage/reconciliationList", method = RequestMethod.POST)
    public Page<ReconciliationVo> findReconciliation(@RequestBody ReconciliationManageQueryVo pageQueryCondition, LoginEmployee loginEmployee) {
        Page<ReconciliationVo> pageData = reconciliationServiceV2.reconciliationSearch(pageQueryCondition, loginEmployee);

        return pageData;
    }

    /**
     * 对账单下的运单列表
     *
     * @param queryVo
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "reconciliationManage/waybills")
    public Page<ReconciliationWaybillDetailVo> findReconciliationWaybillList(@RequestBody ReconciliationWaybillQueryVo queryVo, LoginEmployee loginEmployee) {
        Page<ReconciliationWaybillDetailVo> pageData = reconciliationServiceV2.reconciliationSearchWaybill(queryVo, loginEmployee);
        return pageData;
    }


    /**
     * 提交审核
     *
     * @param reconciliation
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "reconciliationManage/submitToWorkFlow", method = RequestMethod.POST)
    public Map<String, String> submitToWorkFlow(@RequestBody Reconciliation reconciliation, LoginEmployee loginEmployee) {
        if (reconciliation == null) return null;
        if (CollectionUtils.isEmpty(reconciliation.getReconciliationIds())) return null;
        //验证是否有对账凭证
        List<Integer> ids = reconciliation.getReconciliationIds();
        if (CollectionUtils.isEmpty(ids)) return null;

        List<Integer> incorrectIds = new ArrayList<>();
        StringBuilder noEvidenceErrorNos = new StringBuilder("");
        StringBuilder statusErrorNos = new StringBuilder("");

        this.filterErrorData(ids, incorrectIds, noEvidenceErrorNos, statusErrorNos);
        ids.removeAll(incorrectIds);
        if (CollectionUtils.isNotEmpty(ids)) {
            reconciliationServiceV2.submitToWorkFlow(reconciliation, loginEmployee);
        }


        Map<String, String> rst = new HashMap<>();
        if (CollectionUtils.isEmpty(incorrectIds)) {
            rst.put("hasError", "false");
        } else {
            String finalMsg = this.buildNoEvidenceMsg(noEvidenceErrorNos.toString())
                + "<br/>"
                + this.buildStatusErrorMsg(statusErrorNos.toString());
            rst.put("hasError", "true");
            rst.put("content", finalMsg);
        }

        return rst;
    }

    /**
     * 过滤错误数据
     * @param ids
     * @param incorrectIds
     * @param noEvidenceErrorNos
     * @param statusErrorNos
     */
    private void filterErrorData(List<Integer> ids, List<Integer> incorrectIds, StringBuilder noEvidenceErrorNos, StringBuilder statusErrorNos) {
        for (Integer id : ids) {
            ReconciliationVo vo = reconciliationServiceV2.getReconciliationVoById(id);

            if (StringUtils.equals(vo.getEvidenceStatus(), ReconciliationVo.EvidenceStatus.NONE.name())) {
                //没有上传对账单
                incorrectIds.add(id);
                noEvidenceErrorNos.append(vo.getReconciliationNew().getReconciliationNo());
                noEvidenceErrorNos.append(",");
            }

            if (NumberUtils.compare(vo.getReconciliationNew().getReconciliationStatus(), Reconciliation.ReconciliationStatus.Append.getCode()) != 0) {
                //对账单状态错误
                incorrectIds.add(id);
                statusErrorNos.append(vo.getReconciliationNew().getReconciliationNo());
                statusErrorNos.append(",");
            }

        }
    }

    /**
     * 未上传凭证错误
     * @param noEvidenceNos
     * @return
     */
    private String buildNoEvidenceMsg(String noEvidenceNos) {
        if(StringUtils.isBlank(noEvidenceNos)) return "";

        String msg = "对账单号:" + noEvidenceNos + "提交失败\n请上传凭证后重新提交";

        return msg;
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
     * 对账信息总览
     *
     * @param id
     * @return
     */
    @RequestMapping("reconciliationManage/{id}/overview")
    public ReconciliationOverviewVo getReconciliationOverview(@PathVariable("id") Integer id) {
        return reconciliationServiceV2.getReconciliationOverView(id);
    }

    /**
     * 客户对账单统计
     *
     * @param queryVo
     * @return
     */
    @RequestMapping(value = "reconciliationManage/customerStatistics", method = RequestMethod.POST)
    public CustomerStatisticsVo getCustomerStatistics(@RequestBody CustomerStatisticsQueryVo queryVo) {
        return reconciliationServiceV2.findCustomerStatistics(queryVo);
    }


    //车辆对账单列表
    @RequestMapping(value = "reconciliationManage/findVehicleReconciliation", method = RequestMethod.POST)
    public Page<ReconciliationForVehicleVo> findVehicleReconciliationList(@RequestBody VehicleReconciliationQueryVo queryVo, LoginEmployee loginEmployee) {

        Page<ReconciliationForVehicleVo> pageData = reconciliationServiceV2.reconciliationSearchForVehicle(queryVo, loginEmployee);
        return pageData;
    }

    /**
     * 对账凭证
     *
     * @param queryVo
     * @return
     */
    @RequestMapping(value = "reconciliationManage/findEvidences", method = RequestMethod.POST)
    public List<ImageUploadManage> findEvidenceList(@RequestBody ReconciliationManageQueryVo queryVo) {
        if (queryVo.getFilters() == null) return null;
        if (queryVo.getFilters().getReconciliationId() == null) return null;
        return reconciliationServiceV2.getEvidence(queryVo.getFilters().getReconciliationId());
    }


    /**
     * 上传对账凭证
     *
     * @param imageUploadManage
     * @param loginEmployee
     */
    @RequestMapping(value = "reconciliation/addEvidence", method = RequestMethod.POST)
    public void addReconciliationEvidence(@RequestBody ImageUploadManage imageUploadManage, LoginEmployee loginEmployee) {
        if (imageUploadManage == null) return;
        if (StringUtils.isBlank(imageUploadManage.getImageUploadUrl()))
            throw new BusinessException("evidenceUrlNull", "reconciliation.evidence.nullError");
        if (imageUploadManage.getRelationId() == null)
            throw new BusinessException("reconciliationIdNull", "errors.paramCanNotNullWithName", "对账单id");

        imageUploadManageService.insert(imageUploadManage, loginEmployee);

    }

    /**
     * 删除已上传的凭证
     */
    @RequestMapping(value = "reconciliationManage/{imageId}/delEvidence", method = RequestMethod.POST)
    public void deleteEvidence(@PathVariable(value = "imageId") Integer imageId) {
        imageUploadManageService.delByImageUploadManageId(imageId);
    }

    /**
     * 撤销对账单
     *
     * @param id
     * @param loginEmployee
     */
    @ResponseBody
    @RequestMapping(value = "reconciliationManage/{id}/cancel", method = RequestMethod.GET)
    public void cancelReconciliation(@PathVariable("id") Integer id, LoginEmployee loginEmployee) {
        reconciliationServiceV2.cancelReconciliation(id, loginEmployee);
        return;
    }

    @ResponseBody
    @RequestMapping(value = "reconciliationManage/{id}/cancelWorkFlowTask", method = RequestMethod.GET)
    public void cancelWorkFlowTask(@PathVariable("id") Integer id, LoginEmployee loginEmployee) {
        reconciliationServiceV2.cancelWorkFlowTask(id, loginEmployee);
        return;
    }


    /**
     * 处理工作流审批
     *
     * @param task
     * @param loginEmployee
     */
    @RequestMapping(value = "reconciliationManage/doTask", method = RequestMethod.POST)
    public void doWorkFlowTask(@RequestBody Task task, LoginEmployee loginEmployee) {
        reconciliationServiceV2.finishWorkFlowTask(task, loginEmployee);
    }

    /**
     * 通过工作流实例id获取对账单
     *
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "reconciliationManage/{processInstanceId}/processInstanceId", method = RequestMethod.GET)
    public ReconciliationNew getReconciliationNewByProcessInstanceId(@PathVariable("processInstanceId") String processInstanceId) {
        return reconciliationServiceV2.findReconciliationNewByProcessInstanceId(processInstanceId);
    }

    /**
     * 通过对账单号获取对账单
     *
     * @param reconciliationNo
     * @return
     */
    @RequestMapping(value = "reconciliationManage/{reconciliationNo}/reconciliationNo", method = RequestMethod.GET)
    public ReconciliationNew getReconciliationNewByreconciliationNo(@PathVariable("reconciliationNo") String reconciliationNo) {
        return reconciliationServiceV2.findReconciliationNewByReconciliationNo( reconciliationNo );
    }



    /**
     * 组装导出数据
     *
     * @param id
     * @param httpServletResponse
     */
    @RequestMapping(value = "reconciliationManage/{id}/exportExcel", method = RequestMethod.GET)
    public void exportExcelReconciliation(@PathVariable("id") Integer id, HttpServletResponse httpServletResponse) {
        //对账单详情
        ReconciliationOverviewVo overViewVo = reconciliationServiceV2.getReconciliationOverView(id);
        //对账明细详情
        List<ReconciliationForVehicleVo> vehicleItemVoList = reconciliationServiceV2.findReconciliationItemByReconciliationNewId(id);

        //运单对账
        List<ReconciliationWaybillDetailVo> waybillDetailVos = reconciliationServiceV2.findReconciliationWaybillDetailVoByReconciliationId(id);
        //车辆费用调整记录
        List<ReconciliationChangeLogByCarVo> carChangeLogsVo = reconciliationChangeLogService.findCarReconciliationChangeLogByReconciliationId(id);
        //客户费用调整记录
        List<ReconciliationChangeLogByTenantVo> customerChangeLogsVo = reconciliationChangeLogService.findCustomerReconciliationChangeLogByReconciliationId(id);

        this.doExport(overViewVo, vehicleItemVoList, waybillDetailVos, carChangeLogsVo, customerChangeLogsVo, httpServletResponse);
    }

    /**
     * 执行导出
     *
     * @param overViewVo
     * @param vehicleItemVoList
     * @param waybillDetailVos
     * @param carChangeLogsVo
     * @param customerChangeLogsVo
     * @param httpServletResponse
     */
    private void doExport(ReconciliationOverviewVo overViewVo, List<ReconciliationForVehicleVo> vehicleItemVoList, List<ReconciliationWaybillDetailVo> waybillDetailVos, List<ReconciliationChangeLogByCarVo> carChangeLogsVo, List<ReconciliationChangeLogByTenantVo> customerChangeLogsVo, HttpServletResponse httpServletResponse) {
        //概览
        List<ReconciliationOverViewExcelVo> excelOverViewVos = this.buildExcelOverView(overViewVo);
        //对账明细
        List<VehicleReconciliationExcelVo> excelVehicleVos = this.buildExcelVehicleReconciliation(vehicleItemVoList);
        //运单明细
        List<WaybillReconciliationExcelVo> excelWaybillVos = this.buildExcelReconciliation(waybillDetailVos);
        //车辆改价记录
        List<VehicleChangeLogExcelVo> excelCarLogVos = this.buildVehicleChangeLog(carChangeLogsVo);
        //客户改价记录
        List<CustomerChangeLogExcelVo> excelCustomerLogVos = this.buildCustomerChangeLog(customerChangeLogsVo);
        String fileName = "对账单号" + overViewVo.getReconciliationNo() + "对账明细.xls";

        try {
            XssfWriter xssfWriter = new XssfWriter();
            httpServletResponse.setContentType(CONTENT_TYPE);
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
            xssfWriter.appendToSheet("对账单概览", excelOverViewVos).appendToSheet("车辆-承运商,对账", excelVehicleVos).appendToSheet("运单对账", excelWaybillVos).appendToSheet("车辆-承运商,费用调整记录", excelCarLogVos).appendToSheet("客户费用调整记录", excelCustomerLogVos).writeToOutputStream(httpServletResponse.getOutputStream());
        } catch (Exception e) {
            throw new BusinessException("export error ", "import.xlsx.export.error");
        }
    }

    /**
     * Excel - 组装对账单详情
     *
     * @param overViewVo
     * @return
     */
    private List<ReconciliationOverViewExcelVo> buildExcelOverView(ReconciliationOverviewVo overViewVo) {
        List<ReconciliationOverViewExcelVo> vos = new ArrayList<>();
        if (overViewVo == null) return vos;

        StringBuffer line1Buffer = new StringBuffer("");
        line1Buffer.append("客户:");
        line1Buffer.append(overViewVo.getCustomerName());
        line1Buffer.append(",项目:");
        if (StringUtils.isBlank(overViewVo.getProjectName())) {
            line1Buffer.append("无");
        } else {
            line1Buffer.append(overViewVo.getProjectName());
        }
        line1Buffer.append(",对账单号:");
        line1Buffer.append(overViewVo.getReconciliationNo());
        line1Buffer.append(",共");
        line1Buffer.append(overViewVo.getVehicleCount());
        line1Buffer.append("辆车,");
        line1Buffer.append(overViewVo.getWaybillCount());
        line1Buffer.append("个运单,开票状态:");
        if (StringUtils.isBlank(overViewVo.getInvoice())) {
            line1Buffer.append("未开票,");
        } else {
            line1Buffer.append("已开票（");
            line1Buffer.append(overViewVo.getInvoice());
            line1Buffer.append("),");
        }
        line1Buffer.append("收款状态:");
        if (StringUtils.isBlank(overViewVo.getReceiveStatusName())) {
            line1Buffer.append("未收款");
        } else {
            line1Buffer.append(overViewVo.getReceiveStatusName());
        }

        ReconciliationOverViewExcelVo vo1 = new ReconciliationOverViewExcelVo();
        vo1.setOverViewString(line1Buffer.toString());
        vos.add(vo1);
        String line2 = "客户初始总费用:含税总金额" + overViewVo.getCustomerInitialBeforeTax() + "元,不含税总金额" + overViewVo.getCustomerInitialAfterTax() + "元";
        ReconciliationOverViewExcelVo vo2 = new ReconciliationOverViewExcelVo();
        vo2.setOverViewString(line2);
        vos.add(vo2);
        String line3 = "客户最终总费用:含税总金额" + overViewVo.getCustomerFinalBeforeTax() + "元,不含税总金额" + overViewVo.getCustomerFinalAfterTax() + "元";
        ReconciliationOverViewExcelVo vo3 = new ReconciliationOverViewExcelVo();
        vo3.setOverViewString(line3);
        vos.add(vo3);
        String line4 = "司机/承运商初始总费用:含税总金额" + overViewVo.getDriverInitialBeforeTax() + "元,不含税总金额" + overViewVo.getDriverInitialAfterTax() + "元";
        ReconciliationOverViewExcelVo vo4 = new ReconciliationOverViewExcelVo();
        vo4.setOverViewString(line4);
        vos.add(vo4);
        String line5 = "司机/承运商最终总费用:含税总金额" + overViewVo.getDriverFinalBeforeTax() + "元,不含税总金额" + overViewVo.getDriverFinalAfterTax() + "元";
        ReconciliationOverViewExcelVo vo5 = new ReconciliationOverViewExcelVo();
        vo5.setOverViewString(line5);
        vos.add(vo5);
        return vos;

    }

    /**
     * Excel - 对账明细详情
     *
     * @param vehicleItemVoList
     * @return
     */
    private List<VehicleReconciliationExcelVo> buildExcelVehicleReconciliation(List<ReconciliationForVehicleVo> vehicleItemVoList) {
        List<VehicleReconciliationExcelVo> excelVos = new ArrayList<>();
        if (CollectionUtils.isEmpty(vehicleItemVoList)) return excelVos;

        VehicleReconciliationExcelVo excelVo = null;

        for (ReconciliationForVehicleVo item : vehicleItemVoList) {
            excelVo = new VehicleReconciliationExcelVo();

            excelVo.setDriverFinalAfterTax(item.getReconciliationItemNew().getDriverFinalAfterTax());
            excelVo.setDriverFinalBeforeTax(item.getReconciliationItemNew().getDriverFinalBeforeTax());
            excelVo.setDriverHandlingFee(item.getReconciliationItemNew().getDriverHandlingFee());
            excelVo.setDriverInitialAfterTax(item.getReconciliationItemNew().getDriverInitialAfterTax());
            excelVo.setDriverInitialBeforeTax(item.getReconciliationItemNew().getDriverInitialBeforeTax());
            excelVo.setLaborerHandlingFee(item.getReconciliationItemNew().getLaborerHandlingFee());
            excelVo.setPayStatusName(item.getPayStatusName());

            excelVo.setRebateFee(item.getReconciliationItemNew().getRebateFee());
            if( item.getReconciliationItemNew().getVendorId() !=null ) {
                excelVo.setSettlementObject("承运商");
                excelVo.setPlateNumber(item.getReconciliationItemNew().getVendorName());
            }
            else {
                excelVo.setSettlementObject("司机");
                excelVo.setPlateNumber(item.getReconciliationItemNew().getPlateNumber());
            }
            excelVos.add(excelVo);
        }

        return excelVos;
    }

    /**
     * Excel - 运单对账
     *
     * @param waybillDetailVos
     * @return
     */
    private List<WaybillReconciliationExcelVo> buildExcelReconciliation(List<ReconciliationWaybillDetailVo> waybillDetailVos) {
        List<WaybillReconciliationExcelVo> excelVos = new ArrayList<>();
        if (CollectionUtils.isEmpty(waybillDetailVos)) return excelVos;

        WaybillReconciliationExcelVo excelVo = null;
        for (ReconciliationWaybillDetailVo vo : waybillDetailVos) {
            excelVo = new WaybillReconciliationExcelVo();

            excelVo.setRebateFee(vo.getWaybill().getRebateFee());
            excelVo.setAfterTaxFreight(vo.getWaybill().getAfterTaxFreight());
            excelVo.setEstimateFreight(vo.getWaybill().getEstimateFreight());
            if (vo.getWaybill().getPlanDeliveryTime() != null) {
                excelVo.setPlanDeliveryTime(Constants.YYYYMMDDHHMMSS.format(vo.getWaybill().getPlanDeliveryTime()));
            }

            excelVo.setTaxRateValue(vo.getRequire().getTaxRateValue());
            excelVo.setWaybillNo(vo.getWaybill().getWaybillNo());
            excelVo.setWaybillRemark(vo.getWaybill().getWaybillRemark());
            if (vo.getWaybillParam() != null) {
                excelVo.setLaborerHandlingCost(vo.getWaybillParam().getLaborerHandlingCost());
                excelVo.setDriverHandlingCost(vo.getWaybillParam().getDriverHandlingCost());
            }

            excelVos.add(excelVo);
        }

        return excelVos;
    }

    /**
     * Excel - 车辆费用调整记录
     *
     * @param carChangeLogsVos
     * @return
     */
    private List<VehicleChangeLogExcelVo> buildVehicleChangeLog(List<ReconciliationChangeLogByCarVo> carChangeLogsVos) {
        List<VehicleChangeLogExcelVo> excelVos = new ArrayList<>();
        if (CollectionUtils.isEmpty(carChangeLogsVos)) return excelVos;

        VehicleChangeLogExcelVo excelVo = null;
        for (ReconciliationChangeLogByCarVo logVo : carChangeLogsVos) {
            excelVo = new VehicleChangeLogExcelVo();

            excelVo.setAfterTaxFreight(logVo.getAfterTaxFreight());
            excelVo.setBeforeTaxFreight(logVo.getBeforeTaxFreight());
            if (logVo.getCreateTime() != null) {
                excelVo.setCreateTime(Constants.YYYYMMDDHHMMSS.format(logVo.getCreateTime()));
            }

            excelVo.setDriverName(logVo.getDriverName());
            excelVo.setNote(logVo.getNote());
            excelVo.setPlateNumber(logVo.getPlateNumber());
            if (logVo.getVehicleUseTime() != null) {
                excelVo.setVehicleUseTime(Constants.YYYYMMDDHHMMSS.format(logVo.getVehicleUseTime()));
            }

            excelVos.add(excelVo);
        }

        return excelVos;
    }

    /**
     * Excel - 客户费用调整记录
     *
     * @param customerChangeLogsVo
     * @return
     */
    private List<CustomerChangeLogExcelVo> buildCustomerChangeLog(List<ReconciliationChangeLogByTenantVo> customerChangeLogsVo) {
        List<CustomerChangeLogExcelVo> excelVos = new ArrayList<>();

        if (CollectionUtils.isEmpty(customerChangeLogsVo)) return excelVos;
        CustomerChangeLogExcelVo excelVo = null;
        for (ReconciliationChangeLogByTenantVo vo : customerChangeLogsVo) {
            excelVo = new CustomerChangeLogExcelVo();
            excelVo.setNote(vo.getNote());
            excelVo.setAfterTaxFreight(vo.getAfterTaxFreight());
            excelVo.setBeforeTaxFreight(vo.getBeforeTaxFreight());
            if (vo.getCreateTime() != null) {
                excelVo.setCreateTime(Constants.YYYYMMDDHHMMSS.format(vo.getCreateTime()));
            }

            excelVos.add(excelVo);
        }

        return excelVos;
    }

}
