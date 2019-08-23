package com.juma.tgm.manage.fms.controller.v2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.giants.common.exception.BusinessException;
import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.giants.common.tools.PageQueryCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.fms.domain.v2.ReconciliationItemNew;
import com.juma.tgm.fms.domain.v2.vo.ChangeLogQueryByCarVo;
import com.juma.tgm.fms.domain.v2.vo.ChangeLogQueryByTenantVo;
import com.juma.tgm.fms.domain.v2.vo.ReconciliationVo;
import com.juma.tgm.fms.service.v2.ReconciliationChangeLogService;
import com.juma.tgm.fms.service.v2.ReconciliationService;
import com.juma.tgm.manage.fms.controller.v2.vo.CalculateAfterTaxFreightByCarVo;
import com.juma.tgm.manage.fms.controller.v2.vo.CalculateAfterTaxFreightByCustomerVo;
import com.juma.tgm.manage.fms.controller.v2.vo.ReconciliationChangeLogByCarVo;
import com.juma.tgm.manage.fms.controller.v2.vo.ReconciliationChangeLogByTenantVo;
import com.juma.tgm.manage.fms.controller.v2.vo.ReconciliationItemViewVo;

@Controller
@RequestMapping("/reconciliationChangeLog")
public class ReconciliationChangeLogController {

    @Resource
    private ReconciliationChangeLogService reconciliationChangeLogService;

    @Autowired
    private ReconciliationService reconciliationServiceV2;


    @ResponseBody
    @RequestMapping(value = "/{reconciliationId}/addLogByCar", method = RequestMethod.POST)
    public int addLogByCar(@PathVariable String reconciliationId, @RequestBody(required = true) ReconciliationChangeLogByCarVo reconciliationChangeLogByCarVo, LoginEmployee loginEmployee) {
        if (StringUtils.isEmpty(reconciliationId) || (!StringUtils.isNumeric(reconciliationId))) {
            throw new BusinessException("validate error", "对账单id异常");
        }
        if (reconciliationChangeLogByCarVo == null) {
            throw new BusinessException("validate error", "参数为空");
        }
        String note = reconciliationChangeLogByCarVo.getNote();
        if (StringUtils.isEmpty(note) || StringUtils.isEmpty(note.trim())) {
            throw new BusinessException("validate error", "备注为空");
        }
        String beforeTaxFreight = reconciliationChangeLogByCarVo.getBeforeTaxFreight();
        if (StringUtils.isEmpty(beforeTaxFreight)) {
            throw new BusinessException("validate error", "税前总费用为空");
        }
        try {
            new BigDecimal(beforeTaxFreight);
        } catch (Exception e) {
            throw new BusinessException("validate error", "税前总费用数据异常");
        }
        com.juma.tgm.fms.domain.v2.vo.ReconciliationChangeLogByCarVo changeLogByCarVo = new com.juma.tgm.fms.domain.v2.vo.ReconciliationChangeLogByCarVo();
        changeLogByCarVo.setReconciliationId(Integer.parseInt(reconciliationId));
        changeLogByCarVo.setNote(reconciliationChangeLogByCarVo.getNote().trim());
        changeLogByCarVo.setBeforeTaxFreight(new BigDecimal(reconciliationChangeLogByCarVo.getBeforeTaxFreight()));
        changeLogByCarVo.setAfterTaxFreight(new BigDecimal(reconciliationChangeLogByCarVo.getAfterTaxFreight()));
        // 当为 非转运 的单子 才校验以下的东西
        if( reconciliationChangeLogByCarVo.getVendorId() == null ) {
            String driverName = reconciliationChangeLogByCarVo.getDriverName();
            if (StringUtils.isEmpty(driverName) || StringUtils.isEmpty(driverName.trim())) {
                throw new BusinessException("validate error", "司机姓名为空");
            }
            String plateNumber = reconciliationChangeLogByCarVo.getPlateNumber();
            if (StringUtils.isEmpty(plateNumber) || StringUtils.isEmpty(plateNumber.trim())) {
                throw new BusinessException("validate error", "车牌号为空");
            }
            if (reconciliationChangeLogByCarVo.getVehicleUseTime() == null) {
                throw new BusinessException("validate error", "用车时间为空");
            }
            changeLogByCarVo.setPlateNumber(reconciliationChangeLogByCarVo.getPlateNumber().trim());
            changeLogByCarVo.setDriverName(reconciliationChangeLogByCarVo.getDriverName().trim());
            changeLogByCarVo.setVehicleUseTime(reconciliationChangeLogByCarVo.getVehicleUseTime());
        }
        else {
            changeLogByCarVo.setPlateNumber("承运商");
            changeLogByCarVo.setVendorId( reconciliationChangeLogByCarVo.getVendorId());
            changeLogByCarVo.setDriverName(reconciliationChangeLogByCarVo.getVendorName());
        }
        return reconciliationChangeLogService.addChangeLogByCar(changeLogByCarVo, loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "/searchByCar", method = RequestMethod.POST)
    public Page<ReconciliationChangeLogByCarVo> searchByCar(@RequestBody(required = true) PageCondition pageCondition, LoginEmployee loginEmployee) {
        Integer reconciliationId = (Integer) pageCondition.getFilters().get("reconciliationId");
        if (reconciliationId == null) {

            throw new BusinessException("validate error", "对账单id异常");
        }
        String plateNumber = (String) pageCondition.getFilters().get("plateNumber");
        plateNumber = (plateNumber == null) ? null : plateNumber.trim();
        ChangeLogQueryByCarVo changeLogQueryByCarVo = new ChangeLogQueryByCarVo();
        changeLogQueryByCarVo.setPlateNumber(plateNumber);//车牌号
        changeLogQueryByCarVo.setReconciliationId(reconciliationId);
        PageQueryCondition<ChangeLogQueryByCarVo> pageQueryCondition = new PageQueryCondition<ChangeLogQueryByCarVo>(changeLogQueryByCarVo);
        pageQueryCondition.setPageNo(pageCondition.getPageNo());
        pageQueryCondition.setPageSize(pageCondition.getPageSize());
        Page<com.juma.tgm.fms.domain.v2.vo.ReconciliationChangeLogByCarVo> result = reconciliationChangeLogService.searchByCar(pageQueryCondition);
        if (result == null) {
            throw new BusinessException("search error", "数据检索异常");
        }
        Page<ReconciliationChangeLogByCarVo> page = new Page<ReconciliationChangeLogByCarVo>();
        page.setTotal(result.getTotal());
        page.setPageSize(result.getPageSize());
        page.setPageNo(result.getPageNo());
        page.setPageNumCount(result.getPageNumCount());
        List<ReconciliationChangeLogByCarVo> changeLogByCarVoList = new ArrayList<>(result.getTotal());
        if (result.getResults() == null) {
            throw new BusinessException("search error", "数据检索异常");
        } else {
            for (com.juma.tgm.fms.domain.v2.vo.ReconciliationChangeLogByCarVo changeLogByCarVo : result.getResults()) {
                ReconciliationChangeLogByCarVo temp = new ReconciliationChangeLogByCarVo();
                temp.setBeforeTaxFreight(changeLogByCarVo.getBeforeTaxFreight().toString());
                temp.setCreateTime(changeLogByCarVo.getCreateTime());
                temp.setNote(changeLogByCarVo.getNote());
                temp.setDriverName(changeLogByCarVo.getDriverName());
                temp.setPlateNumber(changeLogByCarVo.getPlateNumber());
                temp.setVehicleUseTime(changeLogByCarVo.getVehicleUseTime());
                temp.setAfterTaxFreight(changeLogByCarVo.getAfterTaxFreight().toString());
                temp.setDriverName(changeLogByCarVo.getDriverName());
                changeLogByCarVoList.add(temp);
            }
        }
        page.setResults(changeLogByCarVoList);
        return page;
    }

    @ResponseBody
    @RequestMapping(value = "/{reconciliationId}/addLogByTenant", method = RequestMethod.POST)
    public int addLogByTenant(@RequestBody(required = true) ReconciliationChangeLogByTenantVo reconciliationChangeLogByTenantVo, @PathVariable String reconciliationId, LoginEmployee loginEmployee) {
        if (StringUtils.isEmpty(reconciliationId) || (!StringUtils.isNumeric(reconciliationId))) {
            throw new BusinessException("validate error", "对账单id为空");
        }

        String beforeTaxFreight = reconciliationChangeLogByTenantVo.getBeforeTaxFreight();
        if (StringUtils.isEmpty(beforeTaxFreight)) {
            throw new BusinessException("validate error", "税前总费用为空");
        }
        try {
            new BigDecimal(beforeTaxFreight);
        } catch (Exception e) {
            throw new BusinessException("validate error", "税前总费用数据异常");
        }
        String note = reconciliationChangeLogByTenantVo.getNote();
        if (StringUtils.isEmpty(note) || StringUtils.isEmpty(note.trim())) {
            throw new BusinessException("validate error", "备注为空");
        }
        com.juma.tgm.fms.domain.v2.vo.ReconciliationChangeLogByTenantVo changeLogByTenantVo = new com.juma.tgm.fms.domain.v2.vo.ReconciliationChangeLogByTenantVo();
        changeLogByTenantVo.setReconciliationId(Integer.parseInt(reconciliationId));
        changeLogByTenantVo.setCreateUserId(loginEmployee.getUserId());
        changeLogByTenantVo.setBeforeTaxFreight(new BigDecimal(reconciliationChangeLogByTenantVo.getBeforeTaxFreight()));
        changeLogByTenantVo.setAfterTaxFreight(new BigDecimal(reconciliationChangeLogByTenantVo.getAfterTaxFreight()));
        changeLogByTenantVo.setNote(reconciliationChangeLogByTenantVo.getNote().trim());

        return reconciliationChangeLogService.addChangeLogByTenant(changeLogByTenantVo, loginEmployee);
    }

    @ResponseBody
    @RequestMapping(value = "/searchByTenant", method = RequestMethod.POST)
    public Page<ReconciliationChangeLogByTenantVo> searchByTenant(@RequestBody(required = true) PageCondition pageCondition, LoginEmployee loginEmployee) {

        Integer reconciliationId = (Integer) pageCondition.getFilters().get("reconciliationId");
        if (reconciliationId == null) {

            throw new BusinessException("validate error", "对账单id异常");
        }
        ChangeLogQueryByTenantVo changeLogQueryByTenantVo = new ChangeLogQueryByTenantVo();
        changeLogQueryByTenantVo.setReconciliationId(reconciliationId);
        PageQueryCondition<ChangeLogQueryByTenantVo> params = new PageQueryCondition<ChangeLogQueryByTenantVo>(changeLogQueryByTenantVo);
        params.setPageSize(pageCondition.getPageSize());
        params.setPageNo(pageCondition.getPageNo());
        Page<com.juma.tgm.fms.domain.v2.vo.ReconciliationChangeLogByTenantVo> result = reconciliationChangeLogService.searchByTenant(params);
        if (result == null) {
            throw new BusinessException("search error", "数据检索异常");
        }
        Page<ReconciliationChangeLogByTenantVo> page = new Page<ReconciliationChangeLogByTenantVo>();
        page.setTotal(result.getTotal());
        page.setPageSize(result.getPageSize());

        List<ReconciliationChangeLogByTenantVo> changeLogByTenantVoList = new ArrayList<>(result.getTotal());
        if (result.getResults() == null) {
            throw new BusinessException("search error", "数据检索异常");
        } else {
            for (com.juma.tgm.fms.domain.v2.vo.ReconciliationChangeLogByTenantVo changeLogByTenantVo : result.getResults()) {
                ReconciliationChangeLogByTenantVo temp = new ReconciliationChangeLogByTenantVo();
                temp.setBeforeTaxFreight(changeLogByTenantVo.getBeforeTaxFreight().toString());
                temp.setAfterTaxFreight(changeLogByTenantVo.getAfterTaxFreight().toString());
                temp.setCreateTime(changeLogByTenantVo.getCreateTime());
                temp.setNote(changeLogByTenantVo.getNote());
                changeLogByTenantVoList.add(temp);
            }
        }
        page.setResults(changeLogByTenantVoList);
        return page;
    }

    @ResponseBody
    @RequestMapping(value = "/calculateAfterTaxFreightByCustomer", method = RequestMethod.POST)
    public BigDecimal calculateAfterTaxFreightByCustomer(@RequestBody(required = true) CalculateAfterTaxFreightByCustomerVo calculateAfterTaxFreightVo, LoginEmployee loginEmployee) {

        String reconciliationId = (String) calculateAfterTaxFreightVo.getReconciliationId();
        if (StringUtils.isEmpty(reconciliationId) || (!StringUtils.isNumeric(reconciliationId))) {

            throw new BusinessException("validate error", "对账单id异常");
        }
        String beforeTaxFreight = calculateAfterTaxFreightVo.getBeforeTaxFreight();
        if (StringUtils.isEmpty(beforeTaxFreight)) {
            throw new BusinessException("validate error", "税前总费用为空");
        }
        try {
            new BigDecimal(beforeTaxFreight);
        } catch (Exception e) {
            throw new BusinessException("validate error", "税前总费用数据异常");
        }
        BigDecimal result = reconciliationChangeLogService.calculateAfterTaxFreight(Integer.parseInt(calculateAfterTaxFreightVo.getReconciliationId()), new BigDecimal(calculateAfterTaxFreightVo.getBeforeTaxFreight()), loginEmployee);
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/calculateAfterTaxFreightByCar", method = RequestMethod.POST)
    public BigDecimal calculateAfterTaxFreightByCar(@RequestBody(required = true) CalculateAfterTaxFreightByCarVo calculateAfterTaxFreightByCarVo, LoginEmployee loginEmployee) {

        String reconciliationId = (String) calculateAfterTaxFreightByCarVo.getReconciliationId();
        if (StringUtils.isEmpty(reconciliationId) || (!StringUtils.isNumeric(reconciliationId))) {

            throw new BusinessException("validate error", "对账单id异常");
        }
        String plateNumber = calculateAfterTaxFreightByCarVo.getPlateNumber();
        if (StringUtils.isEmpty(plateNumber) || StringUtils.isEmpty(plateNumber.trim())) {
            throw new BusinessException("validate error", "车牌号为空");
        }

        String beforeTaxFreight = calculateAfterTaxFreightByCarVo.getBeforeTaxFreight();
        if (StringUtils.isEmpty(beforeTaxFreight)) {
            throw new BusinessException("validate error", "税前总费用为空");
        }
        try {
            new BigDecimal(beforeTaxFreight);
        } catch (Exception e) {
            throw new BusinessException("validate error", "税前总费用数据异常");
        }
        BigDecimal result = reconciliationChangeLogService.calculateAfterTaxFreightByCar(Integer.parseInt(reconciliationId), plateNumber, new BigDecimal(beforeTaxFreight));
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/vendor/calculateAfterTaxFreightByCar", method = RequestMethod.POST)
    public BigDecimal vendorCalculateAfterTaxFreightByCar(@RequestBody(required = true) CalculateAfterTaxFreightByCarVo calculateAfterTaxFreightByCarVo, LoginEmployee loginEmployee) {

        String reconciliationId = (String) calculateAfterTaxFreightByCarVo.getReconciliationId();
        if (StringUtils.isEmpty(reconciliationId) || (!StringUtils.isNumeric(reconciliationId))) {

            throw new BusinessException("validate error", "对账单id异常");
        }
        Integer vendorId = calculateAfterTaxFreightByCarVo.getVendorId();
        if (vendorId == null ) {
            throw new BusinessException("validate error", "承运商为空");
        }

        String beforeTaxFreight = calculateAfterTaxFreightByCarVo.getBeforeTaxFreight();
        if (StringUtils.isEmpty(beforeTaxFreight)) {
            throw new BusinessException("validate error", "税前总费用为空");
        }
        try {
            new BigDecimal(beforeTaxFreight);
        } catch (Exception e) {
            throw new BusinessException("validate error", "税前总费用数据异常");
        }
        BigDecimal result = reconciliationChangeLogService.calculateAfterTaxFreightByCar(Integer.parseInt(reconciliationId), calculateAfterTaxFreightByCarVo.getVendorId(), new BigDecimal(beforeTaxFreight));
        return result;
    }


    /**
     * 通过对账单id获取对账单信息
     *
     * @param reconciliationId
     * @return
     */
    @RequestMapping(value = "{reconciliationId}/reconciliationDetail", method = RequestMethod.GET)
    @ResponseBody
    public ReconciliationVo getReconciliationById(@PathVariable(value = "reconciliationId") Integer reconciliationId) {
        return reconciliationServiceV2.getReconciliationVoById(reconciliationId);
    }

    /**
     * 通过对账单id和车牌号获取对账单信息
     *
     * @return
     */
    @RequestMapping(value = "/reconciliationItemDetail", method = RequestMethod.POST)
    @ResponseBody
    public ReconciliationItemNew getReconciliationItemByPlateNumber(@RequestBody ReconciliationItemViewVo itemNew) {
        if (org.apache.commons.lang.StringUtils.isBlank(itemNew.getPlateNumber())) return null;
        if (org.apache.commons.lang.StringUtils.isBlank(itemNew.getReconciliationNo())) return null;

        try {
            return reconciliationServiceV2.findReconciliationItemNewByPlateNumberAndReconciliationNo(itemNew.getReconciliationNo(), itemNew.getPlateNumber());
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获取对账单下的所有对账明细
     * @param reconciliationId
     * @return
     */
    @RequestMapping(value = "{reconciliationId}/allItemByReconciliationId", method = RequestMethod.GET)
    @ResponseBody
    public List<ReconciliationItemNew> getAllItemByReconciliationId(@PathVariable("reconciliationId") Integer reconciliationId) {
        List<ReconciliationItemNew> itemNewList = reconciliationServiceV2.findReconciliationItemByReconciliationId(reconciliationId);

        return itemNewList;
    }

}
