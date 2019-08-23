package com.juma.tgm.manage.costReimbursed.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.giants.common.collections.CollectionUtils;
import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginUser;
import com.juma.tgm.cms.domain.ExportTask;
import com.juma.tgm.cms.service.ExportTaskService;
import com.juma.tgm.costReimbursed.domain.CostReimbursed;
import com.juma.tgm.costReimbursed.service.CostReimbursedService;
import com.juma.tgm.driver.domain.Driver;
import com.juma.tgm.driver.service.DriverService;
import com.juma.tgm.export.domain.ExportParam;
import com.juma.tgm.imageUploadManage.domain.ImageUploadManage;
import com.juma.tgm.imageUploadManage.service.ImageUploadManageService;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.waybill.service.WaybillService;

import me.about.poi.reader.XlsxReader;

/**
 * Created by shawn_lin on 2017/7/11.
 */
@Controller
@RequestMapping("cost/reimbursed")
public class CostReimbursedController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(CostReimbursedController.class);
    @Resource
    private CostReimbursedService costReimbursedService;
    @Resource
    private DriverService driverService;
    @Resource
    private WaybillService waybillService;
    @Resource
    private ImageUploadManageService imageUploadManageService;
    @Resource
    private ExportTaskService exportTaskService;

    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<CostReimbursed> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        pageCondition.getFilters().put("costReimbursedKey", CostReimbursed.CostReimbursedKey.DRIVER_COST_REIMBURSED.toString());
        super.formatAreaCodeToList(pageCondition, false);
        structPageCondition(pageCondition, loginEmployee);
        // 只可见本业务区域的非分享运单和其他业务区域的分享运单，不可见本业务区域的分享运单
        pageCondition.getFilters().put("ownerAreaSahreCanNotSee", true);
        return costReimbursedService.search(pageCondition, loginEmployee);
    }

    /**
     * excel导出
     */
    @ResponseBody
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public void export(@RequestBody ExportParam exportParam, LoginEmployee loginEmployee) {
        // 初始化任务
        Integer exportTaskId = exportTaskService.insertInit(ExportTask.TaskSign.COST_REIMBURSED, exportParam,
                loginEmployee);
        try {
            // 获取数据并上传云
            PageCondition pageCondition = new PageCondition();
            pageCondition.setPageNo(1);
            pageCondition.setPageSize(Integer.MAX_VALUE);
            pageCondition.setFilters(exportParam.getFilters());
            super.formatAreaCodeToList(pageCondition, false);
            structPageCondition(pageCondition, loginEmployee);
            // 只可见本业务区域的非分享运单和其他业务区域的分享运单，不可见本业务区域的分享运单
            pageCondition.getFilters().put("ownerAreaSahreCanNotSee", true);
            costReimbursedService.asyncCostReimbursedExport(pageCondition, exportTaskId, loginEmployee);
        } catch (Exception e) {
            exportTaskService.failed(exportTaskId, e.getMessage(), loginEmployee);
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 报销是否操作页面：json
     */
    @ResponseBody
    @RequestMapping(value = "{costReimbursedId}/confirm/json/edit", method = RequestMethod.GET)
    public CostReimbursed confirmJsonEdit(@PathVariable Integer costReimbursedId, LoginEmployee loginEmployee) {
        return costReimbursedService.getCostReimbursed(costReimbursedId);
    }

    /**
     * 确定报销(添加备注和金额)
     */
    @ResponseBody
    @RequestMapping(value = "confirm", method = RequestMethod.POST)
    public void confirm(@RequestBody CostReimbursed costReimbursed, LoginEmployee loginEmployee) {
        costReimbursed.setAuditResult(CostReimbursed.AuditResult.PASS.getCode());
        costReimbursedService.update(costReimbursed, loginEmployee);
    }

    /**
     * 不报销（添加备注）
     */
    @ResponseBody
    @RequestMapping(value = "cancel", method = RequestMethod.POST)
    public void cancel(@RequestBody CostReimbursed costReimbursed, LoginEmployee loginEmployee) {
        costReimbursed.setAuditResult(CostReimbursed.AuditResult.DID_NOT_PASS.getCode());
        costReimbursedService.update(costReimbursed, loginEmployee);
    }

    /**
     * 获取凭证图片
     */
    @ResponseBody
    @RequestMapping(value = "image/{costReimbursedId}/list", method = RequestMethod.GET)
    public List<ImageUploadManage> costReimbursedImageList(@PathVariable Integer costReimbursedId) {
        return imageUploadManageService.listByRelationIdAndSign(costReimbursedId,
                ImageUploadManage.ImageUploadManageSign.COST_REIMBURSED.getCode());
    }

    /**
     * 导入
     */
    @ResponseBody
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public String importCostReimbursed(@RequestParam(required = false) MultipartFile uploadXlsx,
            LoginEmployee loginEmployee) throws IOException, Exception {
        if (uploadXlsx == null || uploadXlsx.isEmpty()) {
            throw new BusinessException("fileEmptyError", "import.xlsx.empty.error");
        }
        if (!FilenameUtils.isExtension(uploadXlsx.getOriginalFilename(), "xlsx")) {
            throw new BusinessException("fileExtensionError", "import.xlsx.extension.error");
        }
        List<CostReimbursed> rows = XlsxReader.fromInputStream(uploadXlsx.getInputStream(), CostReimbursed.class, 1);

        if (rows.size() > 300) {
            return "单次上传不能超多300条数据，请修改后重新上传";
        }

        StringBuffer buffer = new StringBuffer("");
        int temp = 1;
        int success = 0;
        for (CostReimbursed costReimbursed : rows) {
            temp++;
            if (StringUtils.isEmpty(costReimbursed.getCostReimbursedNo())) {
                // 流水号为空
                String reason = "EXCEL行号【" + temp + "】导入数据流水号为空";
                buffer.append(reason).append("；<br/>");
                continue;
            }
            if (costReimbursed.getReimbursedAmount() != null) {
                if (costReimbursed.getReimbursedAmount().compareTo(new BigDecimal("999999.99")) == 1) {
                    // 报销金额大小
                    String reason = "流水号【" + costReimbursed.getCostReimbursedNo() + "】结算金额过大";
                    buffer.append(reason).append("；<br/>");
                    continue;
                }
            } else {
                String reason = "流水号【" + costReimbursed.getCostReimbursedNo() + "】没有填写结算金额";
                buffer.append(reason).append("；<br/>");
                continue;
            }

            if (StringUtils.isNotBlank(costReimbursed.getAuditResultplus())) {
                if (!costReimbursed.getAuditResultplus().equals("确认报销")
                        && !costReimbursed.getAuditResultplus().equals("不报销")) {
                    String reason = "流水号【" + costReimbursed.getCostReimbursedNo() + "】审核结果输入不合规范";
                    buffer.append(reason).append("；<br/>");
                    continue;
                }
                if (costReimbursed.getAuditResultplus().equals("待审核")) {
                    costReimbursed.setAuditResult(1);
                } else if (costReimbursed.getAuditResultplus().equals("确认报销")) {
                    costReimbursed.setAuditResult(2);
                } else if (costReimbursed.getAuditResultplus().equals("不报销")) {
                    costReimbursed.setAuditResult(3);
                }
            }
            CostReimbursed costReimbursedTemp = costReimbursedService
                    .getByCostReimbursedNO(costReimbursed.getCostReimbursedNo());
            if (costReimbursedTemp == null) {
                // 流水号不存在
                String reanson = "流水号【" + costReimbursed.getCostReimbursedId() + "】不存在";
                buffer.append(reanson).append("；<br/>");
                continue;
            }
            costReimbursed.setCostReimbursedId(costReimbursedTemp.getCostReimbursedId());
            LoginUser loginUser = new LoginUser(loginEmployee.getUserId());
            costReimbursedService.update(costReimbursed, loginUser);
            success++;
        }
        return "总共读取到EXCEL数据：" + rows.size() + "条；<br/> 执行成功：" + success + "条；<br/> 失败的数据及原因如下：" + buffer.toString();
    }

    // 筛选条件的处理
    private void structPageCondition(PageCondition pageCondition, LoginEmployee loginEmployee) {
        Map<String, Object> filters = pageCondition.getFilters();
        if (null == filters) {
            filters = new HashMap<String, Object>();
        } else {
            // 根据司机手机号或用车人手机号查询，只能精确查询
            Object obj = filters.get("driverPhone");
            filters.remove("driverPhone");
            if (null != obj) {
                // 根据司机手机号获取司机的ID(driverId)
                Driver driver = driverService.findDriverByPhone(obj.toString().trim());
                if (null != driver) {
                    filters.put("driverId", driver.getDriverId());
                } else {
                    // 查询不到是的策略
                    filters.put("driverId", -1);
                }
            }
            obj = filters.get("driverName");
            filters.remove("driverName");
            if (null != obj) {
                // 根据司机姓名获取司机的ID(driverId)
                List<Driver> list = driverService.listDriverByName(obj.toString().trim());
                if (CollectionUtils.isNotEmpty(list)) {
                    List<Integer> driverIdList = new ArrayList<Integer>();
                    for (Driver driver : list) {
                        driverIdList.add(driver.getDriverId());
                    }
                    filters.put("driverIdList", driverIdList);
                } else {
                    // 查询不到是的策略
                    filters.put("driverId", -1);
                }
            }

        }
        pageCondition.setFilters(filters);
    }
}
