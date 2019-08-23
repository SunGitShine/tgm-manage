package com.juma.fms.manage.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.fms.v2.core.payment.reimbursement.domain.FmsReimbursementItemVo;
import com.juma.fms.v2.core.payment.reimbursement.domain.FmsReimbursementVo;
import com.juma.fms.v2.core.payment.reimbursement.service.FmsReimbursementService;
import com.juma.fms.v2.core.payment.requisition.service.RequisitionService;
import com.juma.tgm.crm.domain.CustomerInfo;

import io.swagger.annotations.Api;

@Controller
@RequestMapping("fms/reimbursement")
@Api(tags={"FMS-Controller"})
public class ReimbursementController extends AbstractController {

    @Resource
    private FmsReimbursementService reimbursementService;
    
    @Resource
    private RequisitionService requisitionService;
    
    
    /**
     * 报销单 新增
     */
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(@RequestBody FmsReimbursementVo fmsReimbursementVo
            ,@RequestParam Boolean commitAudit,LoginEmployee loginEmployee) {
        dataPrepare(fmsReimbursementVo);
        reimbursementService.create(fmsReimbursementVo, commitAudit, loginEmployee);
    }
    
    
    private void dataPrepare(FmsReimbursementVo fmsReimbursementVo) {
        List<FmsReimbursementItemVo> items = fmsReimbursementVo.getReimbursementItemList();
        if(items != null && !items.isEmpty()) {
            for(FmsReimbursementItemVo item : items) {
                checkReimbursementItem(item);
            }
        }
    }
    
    /**
     * 报销单 更新
     */
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody FmsReimbursementVo fmsReimbursementVo
            ,@RequestParam Boolean commitAudit,LoginEmployee loginEmployee) {
        dataPrepare(fmsReimbursementVo);
        reimbursementService.update(fmsReimbursementVo, commitAudit, loginEmployee);
    }
    
    /**
     * 报销单 详情
     */
    @ResponseBody
    @RequestMapping(value = "{reimbursementId}/info", method = RequestMethod.GET)
    public FmsReimbursementVo info(@PathVariable("reimbursementId")Long reimbursementId) {
        FmsReimbursementVo vo = reimbursementService.get(reimbursementId);
        for(FmsReimbursementItemVo item : vo.getReimbursementItemList()) {
            if(item.getCustomerId() == null) continue;
            CustomerInfo customer = customerInfoService.findAllByCrmId(item.getCustomerId().intValue());
            if(customer != null) {
                item.setCustomerId(Long.parseLong(customer.getCustomerId()+""));
            }
        }
        return vo;
    }
}
