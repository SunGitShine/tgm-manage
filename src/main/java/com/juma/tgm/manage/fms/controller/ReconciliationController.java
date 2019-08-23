package com.juma.tgm.manage.fms.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.common.DateUtil;
import com.juma.tgm.export.domain.ExportParam;
import com.juma.tgm.fms.domain.Reconciliation;
import com.juma.tgm.fms.domain.ReconciliationItem;
import com.juma.tgm.fms.domain.Task;
import com.juma.tgm.fms.domain.bo.ReconciliationMaster;
import com.juma.tgm.fms.service.ReconciliationService;
import com.juma.tgm.imageUploadManage.domain.ImageUploadManage;
import com.juma.tgm.imageUploadManage.service.ImageUploadManageService;
import com.juma.tgm.manage.fms.controller.vo.ReconciliationVo;
import com.juma.tgm.waybill.domain.TruckRequire;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillParam;
import com.juma.tgm.waybill.service.TruckRequireService;
import com.juma.tgm.waybill.service.WaybillParamService;
import com.juma.tgm.waybill.service.WaybillService;

import me.about.poi.writer.XlsxWriter;

@Controller
@RequestMapping(value="reconciliation/v2")
public class ReconciliationController {

    private final Logger log = LoggerFactory.getLogger(ReconciliationController.class);
    @Resource
    private ReconciliationService reconciliationService;
    @Resource
    private WaybillService waybillService;
    @Resource
    private WaybillParamService waybillParamService;
    @Resource
    private TruckRequireService truckRequireService;
    @Resource
    private ImageUploadManageService imageUploadManageService;

    /**
     * 对帐单-管理
     */
    @RequestMapping(value="search",method=RequestMethod.POST)
    @ResponseBody
    public Page<ReconciliationMaster> search(PageCondition cond,LoginEmployee loginEmployee) {
        cond.getFilters().put("submitter", loginEmployee.getUserId());
        return reconciliationService.searchPage(cond, loginEmployee);
    }
    
    /**
     * 对帐单-生成
     */
    @RequestMapping(value="create",method=RequestMethod.POST)
    @ResponseBody
    public void createReconciliation(@RequestBody ReconciliationVo vo,LoginEmployee loginEmployee) {
        log.info("对帐单-生成:{}", vo.toString());
        reconciliationService.createReconciliation(vo.getListWaybillId(), vo.getListImage(), loginEmployee);
    }
    
    /**
     * 对帐单-调整
     */
    @RequestMapping(value="adjustment",method=RequestMethod.POST)
    @ResponseBody
    public void adjustmentReconciliation(@RequestBody ReconciliationVo vo,LoginEmployee loginEmployee) {
        log.info("对帐单-生成:{}", vo.toString());
        reconciliationService.adjustmentReconciliation(vo.getReconciliationId(), vo.getTotalFee(), vo.getListImage(), loginEmployee);
    }

    /**
     * 对帐单-改价
     */
    @RequestMapping(value="changePrice",method=RequestMethod.POST)
    @ResponseBody
    public void changePrice(@RequestBody ReconciliationItem item,LoginEmployee loginEmployee) {
        reconciliationService.updateReconciliationItem(item);
    }

    /**
     * 对帐单-详情
     */
    @RequestMapping(value="{reconciliationId}/items",method=RequestMethod.POST)
    @ResponseBody
    public Page<ReconciliationMaster> items(@PathVariable Integer reconciliationId, ExportParam exportParam) {
        PageCondition cond = new PageCondition();
        cond.setFilters(exportParam.getFilters() == null ? new HashMap<String, Object>() : exportParam.getFilters());
        cond.getFilters().put("reconciliationId", reconciliationId);
        cond.setPageNo(1);
        cond.setPageSize(Integer.MAX_VALUE);
        List<ReconciliationMaster> rows = reconciliationService.findItemsById(cond);
//        for (ReconciliationMaster master : rows) {
//            Waybill waybill = waybillService.getWaybill(master.getWaybillId());
//            if (null == waybill) {
//                continue;
//            }
//
//            master.setWaybillCreateDate(DateUtil.format(waybill.getCreateTime()));
//        }
        return new Page<ReconciliationMaster>(1, rows.size(), rows.size(), rows);
    }

    /**
     * 对帐单-详情-分页
     */
    @RequestMapping(value="{reconciliationId}/page/items",method=RequestMethod.POST)
    @ResponseBody
    public Page<ReconciliationMaster> itemsPage(@PathVariable Integer reconciliationId, PageCondition cond) {
        cond.getFilters().put("reconciliationId", reconciliationId);
        Page<ReconciliationMaster> page = reconciliationService.findItemsPageById(cond);
        for (ReconciliationMaster master : page.getResults()) {
            Waybill waybill = waybillService.getWaybill(master.getWaybillId());
            if (null == waybill) {
                continue;
            }
            
            master.setWaybillCreateDate(DateUtil.format(waybill.getCreateTime()));
        }
        return page;
    }

    /**
     * 对帐单-详情
     * 场景：对账单生成前的详情列表，由于数据还没有入库，又有分页的要求，故独立一个接口使用程序模拟排序，order by waybillId asc
     */
    @RequestMapping(value="before/create/items",method=RequestMethod.POST)
    @ResponseBody
    public Page<ReconciliationMaster> itemsBeforeCreate(PageCondition cond) {
        List<ReconciliationMaster> rows = new ArrayList<ReconciliationMaster>();
        Map<String, Object> filters = cond.getFilters();
        if (null == filters) {
            return new Page<ReconciliationMaster>(1, 0, 0, rows);
        }

        if (null == filters.get("waybillIds")) {
            return new Page<ReconciliationMaster>(1, 0, 0, rows);
        }

        String waybillIdStr = (String) filters.get("waybillIds");
        
        String[] waybillIds = waybillIdStr.split(",");
        
        List<String> listWaybillId = Arrays.asList(waybillIds);
        
        if (CollectionUtils.isEmpty(listWaybillId)) {
            return new Page<ReconciliationMaster>(1, 0, 0, rows);
        }

        if (null == cond.getPageNo() || null == cond.getPageSize()) {
            return new Page<ReconciliationMaster>(1, 0, 0, rows);
        }

        // 运单ID排序
        Collections.sort(listWaybillId);

        int size = listWaybillId.size();
        int length = cond.getStartOffSet() + cond.getPageSize();
        for (int i = cond.getStartOffSet(); i < length; i++) {
            if (NumberUtils.compare(i, size) == 0) {
                break;
            }

            ReconciliationMaster master = new ReconciliationMaster();
            Waybill waybill = waybillService.getWaybill(Integer.valueOf(listWaybillId.get(i)));
            if (null != waybill) {
                master.setWaybillNo(waybill.getWaybillNo());
                master.setWaybillCreateDate(DateUtil.format(waybill.getCreateTime()));
                master.setPlateNumber(waybill.getPlateNumber());
                master.setEstimateFreight(waybill.getEstimateFreight());
                master.setAfterTaxFreight(waybill.getAfterTaxFreight());
                master.setTaxFee(waybill.getEstimateFreight().subtract(waybill.getAfterTaxFreight()));
                
                WaybillParam waybillParam = waybillParamService.findByWaybillId(waybill.getWaybillId());
                if (null != waybillParam) {
                    master.setDriverHandlingFee(waybillParam.getDriverHandlingCost());
                    master.setLaborerHandlingFee(waybillParam.getLaborerHandlingCost());
                }
                
                TruckRequire truckRequire = truckRequireService.findTruckRequireByWaybillId(waybill.getWaybillId(), null);
                if (null != truckRequire) {
                    master.setTaxRateValue(truckRequire.getTaxRateValue());
                }
            }
            rows.add(master);
        }
        return new Page<ReconciliationMaster>(1, cond.getPageNo(), size, rows);
    }

    /**
     * 对帐单-头信息
     */
    @RequestMapping(value="head/info",method=RequestMethod.POST)
    @ResponseBody
    public ReconciliationMaster headInfo(@RequestBody ReconciliationVo vo) {
        if (!vo.getListWaybillId().isEmpty()) {
            return buildNotGenerate(vo.getListWaybillId());
        } else if (null != vo.getReconciliationId()) {
            return buildHasGenerate(vo.getReconciliationId());
        }
        return new ReconciliationMaster();
    }

    // 已经生成对账单组装信息
    private ReconciliationMaster buildHasGenerate(Integer reconciliationId) {
        ReconciliationMaster master = new ReconciliationMaster();
        PageCondition cond = new PageCondition();
        cond.setPageNo(1);
        cond.setPageSize(Integer.MAX_VALUE);
        cond.getFilters().put("reconciliationId", reconciliationId);
        List<ReconciliationMaster> rows = reconciliationService.findItemsById(cond);

        if (rows.isEmpty()) {
            return master;
        }

        // 排序:运单ID
        Collections.sort(rows, new Comparator<ReconciliationMaster>() {

            @Override
            public int compare(ReconciliationMaster o1, ReconciliationMaster o2) {
                return o1.getWaybillId().compareTo(o2.getWaybillId());

            }
        });

        int size = rows.size();

        Waybill startWaybill = waybillService.getWaybill(rows.get(0).getWaybillId());
        if (null != startWaybill) {
            master.setCustomerName(startWaybill.getCustomerName());
            master.setStartTime(startWaybill.getCreateTime());
            if (size == 1) {
                master.setEndTime(startWaybill.getCreateTime());
                master.setListImage(imageUploadManageService.listByRelationIdAndSign(reconciliationId,
                        ImageUploadManage.ImageUploadManageSign.RECONCILIATION_RECEIVABLE.getCode()));
                return master;
            }
        }

        Waybill endWaybill = waybillService.getWaybill(rows.get(size - 1).getWaybillId());
        if (null != endWaybill) {
            master.setEndTime(endWaybill.getCreateTime());
        }
        master.setListImage(imageUploadManageService.listByRelationIdAndSign(reconciliationId,
                ImageUploadManage.ImageUploadManageSign.RECONCILIATION_RECEIVABLE.getCode()));

        return master;
    }

    private ReconciliationMaster buildNotGenerate(List<Integer> waybillIds) {
        ReconciliationMaster master = new ReconciliationMaster();

        // 排序
        Collections.sort(waybillIds);

        int size = waybillIds.size();

        Waybill startWaybill = waybillService.getWaybill(waybillIds.get(0));
        if (null != startWaybill) {
            master.setCustomerName(startWaybill.getCustomerName());
            master.setStartTime(startWaybill.getCreateTime());
            if (size == 1) {
                master.setEndTime(startWaybill.getCreateTime());
                return master;
            }
        }

        Waybill endWaybill = waybillService.getWaybill(waybillIds.get(size - 1));
        if (null != endWaybill) {
            master.setEndTime(endWaybill.getCreateTime());
        }

        return master;
    }

    /**
     * 对帐单-汇总
     */
    @RequestMapping(value = "{reconciliationId}/sum", method = RequestMethod.POST)
    @ResponseBody
    public ReconciliationMaster sumItemByReconciliationId(@PathVariable Integer reconciliationId,
            @RequestBody ReconciliationMaster master) {
        master.setReconciliationId(reconciliationId);
        return reconciliationService.sumItemByReconciliationId(master);
    }

    /**
     * 对帐单-提交工作流
     */
    @RequestMapping(value="submit",method=RequestMethod.POST)
    @ResponseBody
    public void submitTask(@RequestBody Reconciliation reconciliation,LoginEmployee loginEmployee) {
        reconciliationService.sumbitTask(reconciliation, loginEmployee);
    }
    
    /**
     * 对帐单-撤销
     */
    @RequestMapping(value="{reconciliationId}/cancel",method=RequestMethod.GET)
    @ResponseBody
    public void cancelTask(@PathVariable Integer reconciliationId,LoginEmployee loginEmployee) {
        reconciliationService.cancelTask(reconciliationId,loginEmployee);
    }
    
    /**
     * 对帐单-审批
     */
    @RequestMapping(value="doTask")
    @ResponseBody
    public void completeTask(@RequestBody Task task,LoginEmployee loginEmployee) {
        try {
            reconciliationService.completeTask(task,loginEmployee);
        } catch (Exception e) {
            log.error("doTask.error", e.getMessage(), e);
        }
        
        
    }
    
    /**
     * 对帐单-详情excel导出
     */
    @RequestMapping(value = "{reconciliationId}/export")
    public void export(@PathVariable Integer reconciliationId,PageCondition cond, HttpServletRequest request, HttpServletResponse response) {
        ServletOutputStream outputStream = null;
        try {
            cond.getFilters().put("reconciliationId", reconciliationId);
            List<ReconciliationMaster> rows = reconciliationService.findItemsById(cond);
            for (ReconciliationMaster master : rows) {
                Waybill waybill = waybillService.getWaybill(master.getWaybillId());
                if (null == waybill) {
                    continue;
                }
                master.setWaybillCreateDate(DateUtil.format(waybill.getCreateTime(), DateUtil.YYYYMMDD));
                master.setTaxFee(master.getEstimateFreight().subtract(master.getAfterTaxFreight()));
            }
            outputStream = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment; filename=items.xlsx");
            XlsxWriter.toOutputStream(rows, outputStream);
        } catch (Exception e) {
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
}
