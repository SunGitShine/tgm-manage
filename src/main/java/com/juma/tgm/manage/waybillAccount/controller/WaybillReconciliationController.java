package com.juma.tgm.manage.waybillAccount.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.conf.domain.BusinessAreaNode;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.base.domain.UploadExcelFailedReason;
import com.juma.tgm.base.domain.UploadExcelResult;
import com.juma.tgm.export.domain.ExportParam;
import com.juma.tgm.manage.waybillAccount.vo.WaybillReconciliationVO;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.manage.web.vo.MessageRequstVo;
import com.juma.tgm.user.domain.CurrentUser;
import com.juma.tgm.waybill.domain.TaxRate;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillCountResponse;
import com.juma.tgm.waybill.service.TaxRateService;
import com.juma.tgm.waybill.service.WaybillCommonService;
import com.juma.tgm.waybill.service.WaybillOperateTrackService;
import com.juma.tgm.waybill.service.WaybillParamService;
import com.juma.tgm.waybill.service.WaybillService;
import com.juma.tgm.waybillReconciliation.domain.WaybillReconciliation;
import com.juma.tgm.waybillReconciliation.service.WaybillReconciliationService;

import me.about.poi.reader.XlsxReader;

/**
 * @ClassName WaybillReconciliationController.java
 * @Description 运单对账管理
 * @author Libin.Wei
 * @Date 2017年7月25日 上午11:02:05
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Deprecated
@Controller
@RequestMapping("waybill/reconciliation")
public class WaybillReconciliationController extends BaseController {

    @Resource
    private WaybillService waybillService;
    @Resource
    private WaybillCommonService waybillCommonService;
    @Resource
    private WaybillParamService waybillParamService;
    @Resource
    private TaxRateService taxRateService;
    @Resource
    private WaybillOperateTrackService waybillOperateTrackService;
    @Resource
    private WaybillReconciliationService waybillReconciliationService;

    /**
     * 分页列表
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<WaybillReconciliation> search(PageCondition pageCondition, CurrentUser currentUser,
            LoginEmployee loginEmployee) {
        super.formatAreaCodeToList(pageCondition, true);
        List<String> areaCodeList = new ArrayList<String>();
        for (BusinessAreaNode businessAreaNode : currentUser.getBusinessAreas()) {
            areaCodeList.add(businessAreaNode.getAreaCode());
        }
        return waybillReconciliationService.search(pageCondition, areaCodeList, loginEmployee);
    }

    /**
     * 修改页面:JSON
     */
    @ResponseBody
    @RequestMapping(value = "{waybillId}/json/edit", method = RequestMethod.GET)
    public WaybillReconciliation jsonEdit(@PathVariable Integer waybillId, LoginEmployee loginEmployee) {
        return waybillReconciliationService.findByWaybillId(waybillId);
    }

    /**
     * 修改页面
     */
    @Deprecated
    @RequestMapping(value = "{waybillId}/edit", method = RequestMethod.GET)
    public ModelAndView edit(@PathVariable Integer waybillId, LoginEmployee loginEmployee) {
        ModelAndView modelAndView = new ModelAndView("pages/waybillAccount/dialog/updateFreightDialog");
        modelAndView.addObject("waybillReconciliationResponse",
                waybillReconciliationService.findByWaybillId(waybillId));
        return modelAndView;
    }

    /**
     * 判断运单是否待审
     */
    @ResponseBody
    @RequestMapping(value = "check/{waybillId}/waiting/audit", method = RequestMethod.GET)
    public void checkWaitingAudit(@PathVariable Integer waybillId, CurrentUser currentUser,
            LoginEmployee loginEmployee) {
        Waybill waybill = waybillService.getWaybillAndCheckExist(waybillId);
        if (waybill.getUpdateFreightAuditStatus() == Waybill.UpdateFreightAuditStatus.WATING_AUDIT.getCode()) {
            throw new BusinessException("waybillIsAuditStatus", "waybill.error.waybillIsAuditStatus");
        }

        if (null == currentUser) {
            return;
        }
    }

    /**
     * 系统计算司机结算价
     */
    @ResponseBody
    @RequestMapping(value = "cal/sys/driver/freight", method = RequestMethod.POST)
    public BigDecimal calSysDriverFreight(@RequestBody WaybillReconciliation waybillReconciliation,
            LoginEmployee loginEmployee) {
        return waybillReconciliationService.calSysDriverFreight(waybillReconciliation);
    }

    /**
     * 改价
     */
    @ResponseBody
    @RequestMapping(value = "update/freight", method = RequestMethod.POST)
    public void updateFreight(@RequestBody WaybillReconciliation waybillReconciliation, CurrentUser currentUser,
            LoginEmployee loginEmployee) {
        List<String> areaNodeList = new ArrayList<String>();
        if (null != currentUser) {
            for (BusinessAreaNode businessAreaNode : currentUser.getBusinessAreas()) {
                areaNodeList.add(businessAreaNode.getAreaCode());
            }
        }

        waybillReconciliationService.update(waybillReconciliation, areaNodeList, loginEmployee);
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
        pageCondition.getFilters().put("reconciliationStatus", Waybill.ReconciliationStatus.NOT_RECONCILIATION.getCode());
        pageCondition.getFilters().put("statusView", Waybill.StatusView.FINISH.getCode());
        return waybillService.getFreight(pageCondition, loginEmployee);
    }
    

    /**
     * 导入:改价
     */
    @ResponseBody
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public String importResult(@RequestParam(required = false) MultipartFile uploadXlsx, CurrentUser currentUser,
            LoginEmployee loginEmployee) throws IOException, Exception {
        if (uploadXlsx == null || uploadXlsx.isEmpty()) {
            throw new BusinessException("fileEmptyError", "import.xlsx.empty.error");
        }
        if (!FilenameUtils.isExtension(uploadXlsx.getOriginalFilename(), "xlsx")) {
            throw new BusinessException("fileExtensionError", "import.xlsx.extension.error");
        }

        List<String> areaNodeList = new ArrayList<String>();
        if (null != currentUser) {
            for (BusinessAreaNode businessAreaNode : currentUser.getBusinessAreas()) {
                areaNodeList.add(businessAreaNode.getAreaCode());
            }
        }

        List<WaybillReconciliation> rows = XlsxReader.fromInputStream(uploadXlsx.getInputStream(), WaybillReconciliation.class, 1);

        // 计数器
        int countNo = 0;

        // 循环去空
        for (WaybillReconciliation lt : rows) {
            if (StringUtils.isNotBlank(lt.getWaybillNo())
                    || (null != lt.getEstimateFreight() && lt.getEstimateFreight().compareTo(BigDecimal.ZERO) == 1)
                    || null != lt.getTaxRateValue()
                    || (null != lt.getDriverHandlingCost()
                            && lt.getDriverHandlingCost().compareTo(BigDecimal.ZERO) == 1)
                    || (null != lt.getLaborerHandlingCost()
                            && lt.getLaborerHandlingCost().compareTo(BigDecimal.ZERO) == 1)
                    || (null != lt.getShow4DriverFreight()
                            && lt.getShow4DriverFreight().compareTo(BigDecimal.ZERO) == 1)
                    || StringUtils.isNotBlank(lt.getUpdateFreightRemark())) {
                countNo++;
                if (countNo > 300) {
                    return "单次上传不能超过300条数据，请修改后重新上传";
                }
            }
        }

        String waybillNo = null;
        int success = 0;
        // 计数器
        int temp = 1;
        StringBuffer reseansf = new StringBuffer("");
        for (WaybillReconciliation waybillReconciliation : rows) {
            temp++;
            if (StringUtils.isBlank(waybillReconciliation.getWaybillNo())) {
                String resean = "EXCEL行号【" + temp + "】导入数据运单号为空";
                reseansf.append(resean).append("；<br/>");
                continue;
            }
            waybillNo = waybillReconciliation.getWaybillNo().trim();

            Waybill waybill = waybillService.findWaybillByWaybillNo(waybillNo, loginEmployee);
            if (null == waybill) {
                String resean = "运单号【" + waybillNo + "】运单不存在，请检查";
                reseansf.append(resean).append("；<br/>");
                continue;
            }

            if (null == waybillReconciliation.getTaxRateValue()) {
                String resean = "运单号【" + waybillNo + "】税率不能为空，请检查";
                reseansf.append(resean).append("；<br/>");
                continue;
            }

            TaxRate taxRate = taxRateService.findTaxRateBy(waybillReconciliation.getTaxRateValue(), loginEmployee);
            if (null == taxRate) {
                String resean = "运单号【" + waybillNo + "】税率不正确，请检查";
                reseansf.append(resean).append("；<br/>");
                continue;
            }

            if (waybill.getReconciliationNo() != null) {
                String resean = "运单号【" + waybillNo + "】有对帐单号，不能批量改价";
                reseansf.append(resean).append("；<br/>");
                continue;
            }

            waybillReconciliation.setWaybillId(waybill.getWaybillId());
            try {
                waybillReconciliationService.update(waybillReconciliation, areaNodeList, loginEmployee);
                success++;
            } catch (Exception e) {
                if (e instanceof BusinessException) {
                    String resean = "运单号【" + waybillNo + "】" + ((BusinessException) e).getErrorMessage();
                    reseansf.append(resean).append("；<br/>");
                }
            }
        }

        if (NumberUtils.compare(countNo, success) == 0) {
            return "总共读取到EXCEL数据：" + countNo + "条； 执行成功：" + success + "条；";
            
        }

        return "总共读取到EXCEL数据：" + countNo + "条； 执行成功：" + success + "条； <br/>失败的数据及原因如下：<br/>" + reseansf.toString();
    }

    /**
     * 导入:改价
     */
    @ResponseBody
    @RequestMapping(value = "import/callbackList", method = RequestMethod.POST)
    public UploadExcelResult importResultCallbackList(@RequestParam(required = false) MultipartFile uploadXlsx, CurrentUser currentUser,
            LoginEmployee loginEmployee) throws IOException, Exception {
        if (uploadXlsx == null || uploadXlsx.isEmpty()) {
            throw new BusinessException("fileEmptyError", "import.xlsx.empty.error");
        }
        if (!FilenameUtils.isExtension(uploadXlsx.getOriginalFilename(), "xlsx")) {
            throw new BusinessException("fileExtensionError", "import.xlsx.extension.error");
        }
        UploadExcelResult result = new UploadExcelResult();

        List<String> areaNodeList = new ArrayList<String>();
        if (null != currentUser) {
            for (BusinessAreaNode businessAreaNode : currentUser.getBusinessAreas()) {
                areaNodeList.add(businessAreaNode.getAreaCode());
            }
        }

        List<WaybillReconciliation> rows = XlsxReader.fromInputStream(uploadXlsx.getInputStream(),
                WaybillReconciliation.class, 1);

        // 计数器
        int countNo = 0;

        // 循环去空
        for (WaybillReconciliation lt : rows) {
            if (StringUtils.isNotBlank(lt.getWaybillNo())
                    || (null != lt.getEstimateFreight() && lt.getEstimateFreight().compareTo(BigDecimal.ZERO) == 1)
                    || null != lt.getTaxRateValue()
                    || (null != lt.getDriverHandlingCost()
                            && lt.getDriverHandlingCost().compareTo(BigDecimal.ZERO) == 1)
                    || (null != lt.getLaborerHandlingCost()
                            && lt.getLaborerHandlingCost().compareTo(BigDecimal.ZERO) == 1)
                    || (null != lt.getShow4DriverFreight()
                            && lt.getShow4DriverFreight().compareTo(BigDecimal.ZERO) == 1)
                    || StringUtils.isNotBlank(lt.getUpdateFreightRemark())) {
                countNo++;
                if (countNo > 300) {
                    result.setErrorMsg("单次上传不能超过300条数据，请修改后重新上传");
                    return result;
                }
            }
        }

        List<UploadExcelFailedReason> failedList = new ArrayList<UploadExcelFailedReason>();
        String waybillNo = null;
        int success = 0;
        // 计数器
        int temp = 1;
        StringBuffer reseansf = new StringBuffer("");
        for (WaybillReconciliation waybillReconciliation : rows) {
            temp++;
            if (StringUtils.isBlank(waybillReconciliation.getWaybillNo())) {
                buildFailedList(waybillNo, temp, "此EXCEL行号导入数据运单号为空", failedList);
                continue;
            }
            waybillNo = waybillReconciliation.getWaybillNo().trim();

            Waybill waybill = waybillService.findWaybillByWaybillNo(waybillNo, loginEmployee);
            if (null == waybill) {
                buildFailedList(waybillNo, temp, "运单不存在，请检查", failedList);
                continue;
            }

            if (null == waybillReconciliation.getTaxRateValue()) {
                String resean = "运单号【" + waybillNo + "】税率不能为空，请检查";
                reseansf.append(resean).append("；<br/>");
                continue;
            }

            TaxRate taxRate = taxRateService.findTaxRateBy(waybillReconciliation.getTaxRateValue(), loginEmployee);
            if (null == taxRate) {
                buildFailedList(waybillNo, temp, "税率不正确，请检查", failedList);
                continue;
            }

            if (StringUtils.isNotBlank(waybill.getReconciliationNo())) {
                buildFailedList(waybillNo, temp, "有对帐单号，不能批量改价", failedList);
                continue;
            }

            waybillReconciliation.setWaybillId(waybill.getWaybillId());
            try {
                waybillReconciliationService.update(waybillReconciliation, areaNodeList, loginEmployee);
                success++;
            } catch (Exception e) {
                if (e instanceof BusinessException) {
                    String resean = "运单号【" + waybillNo + "】" + ((BusinessException) e).getErrorMessage();
                    reseansf.append(resean).append("；<br/>");
                }
            }
        }
        
        result.setListFailedReason(failedList);
        result.setCount(countNo);
        result.setSuccess(success);

        return result;
    }
    
    private void buildFailedList(String waybillNo, Integer excelRowNo, String reason, List<UploadExcelFailedReason> failedList) {
        UploadExcelFailedReason failedReason = new UploadExcelFailedReason();
        failedReason.setExcelRowNo(excelRowNo);
        failedReason.setWaybillNo(waybillNo);
        failedReason.setReason(reason);
        failedList.add(failedReason);
    }

    /**
     * 确认对账完成
     */
    @ResponseBody
    @RequestMapping(value = "confirm/finish", method = RequestMethod.POST)
    public MessageRequstVo confirmFinish(@RequestBody WaybillReconciliationVO waybillReconciliationVO,
            CurrentUser currentUser, LoginEmployee loginEmployee) {
        MessageRequstVo result = new MessageRequstVo();
        StringBuffer errorMsgSf = new StringBuffer("");

        List<String> areaNodeList = new ArrayList<String>();
        if (null != currentUser) {
            for (BusinessAreaNode businessAreaNode : currentUser.getBusinessAreas()) {
                areaNodeList.add(businessAreaNode.getAreaCode());
            }
        }

        for (Integer waybillId : waybillReconciliationVO.getWaybillIdList()) {
            Waybill waybill = waybillService.getWaybill(waybillId);
            if (null == waybill) {
                errorMsgSf.append("尾号【").append(waybillId).append("】的运单不存在，请确认；<br/>");
                continue;
            }

            // 校验运单是否已完成
            if (NumberUtils.compare(Waybill.StatusView.FINISH.getCode(), waybill.getStatusView()) != 0) {
                errorMsgSf.append("运单号【").append(waybill.getWaybillNo()).append("】不是已完成运单，请先完成运单；<br/>");
                continue;
            }

            // 校验运单有无改价待审
            if (NumberUtils.compare(Waybill.UpdateFreightAuditStatus.WATING_AUDIT.getCode(),
                    waybill.getUpdateFreightAuditStatus()) == 0) {
                errorMsgSf.append("运单号【").append(waybill.getWaybillNo()).append("】运单改价审核中，请先处理；<br/>");
                continue;
            }

            waybillReconciliationService.updateReconciliationStatus(waybillId, loginEmployee);
        }

        if (StringUtils.isBlank(errorMsgSf.toString())) {
            result.setSuccess(true);
            result.setMessage("对账成功！<br/>运单已到收款管理，可以在收款管理直接导入ERP");
            return result;
        }
        result.setSuccess(false);
        result.setMessage(errorMsgSf.toString());
        return result;
    }
}
