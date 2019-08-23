package com.juma.fms.manage.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.juma.auth.employee.service.DepartmentService;
import com.juma.fms.v2.core.payment.reimbursement.domain.FmsReimbursementDictionary;
import com.juma.fms.v2.core.payment.reimbursement.domain.FmsReimbursementFormType;
import com.juma.fms.v2.core.payment.reimbursement.service.FmsReimbursementDictionaryService;
import com.juma.fms.v2.core.payment.requisition.enums.BillTypeEnum;
import com.juma.fms.v2.core.payment.requisition.service.RequisitionService;
import com.juma.fms.v2.core.payment.requisition.vo.RequisitionTypeCodeVO;

import io.swagger.annotations.Api;

@Controller
@RequestMapping("fms/common")
@Api(tags={"FMS-Controller"})
public class FmsCommonController {

    @Resource
    private RequisitionService requisitionService;
    
    @Resource
    private DepartmentService departmentService;
    
    @Resource
    private FmsReimbursementDictionaryService fmsReimbursementDictionaryService;
    
    @ResponseBody
    @RequestMapping(value = "requisitionType", method = RequestMethod.GET)
    public List<RequisitionTypeCodeVO> requisitionType() {
        return requisitionService.getRequisitionType(BillTypeEnum.LOGISTICS.getCode());
    }
    
    @ResponseBody
    @RequestMapping(value = "reimbursementType", method = RequestMethod.GET)
    public List<FmsReimbursementDictionary> reimbursementType() {
        return fmsReimbursementDictionaryService.getFmsReimbursementType(FmsReimbursementFormType.LOGISTICS);
        
    }
    
}
