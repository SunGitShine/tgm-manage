package com.juma.fms.manage.controller;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.giants.common.exception.BusinessException;
import com.juma.fms.v2.core.payment.reimbursement.domain.FmsReimbursementItemVo;
import com.juma.fms.v2.core.payment.requisition.vo.RequisitionItemLogisticsVO;
import com.juma.tgm.crm.domain.CustomerInfo;
import com.juma.tgm.crm.service.CustomerInfoService;
import com.juma.tgm.project.domain.Project;
import com.juma.tgm.project.service.ProjectService;

public class AbstractController {

    @Resource
    protected ProjectService projectService;

    @Resource
    protected CustomerInfoService customerInfoService;

    private String project_bond = "project_bond";

    private String bid_bond = "bid_bond";

    private String project_reserve_fund = "project_reserve_fund";

    private String oil_card = "oil_card";
    
    private String others = "others";

    
    public void checkReimbursementItem(FmsReimbursementItemVo item) {
        if (StringUtils.isBlank(item.getReimbursementType())) {
            throw new BusinessException("reimbursement_type is empty", "", "报销类型不能为空");
        } else {
            String reimbursementType = item.getReimbursementType();
            
            if(item.getCustomerId() != null && item.getProjectId() != null) {
               // 客户和项目是否匹配
                Project project = projectService.findProject(item.getCustomerId().intValue(), item.getProjectId().intValue());
                if (project == null) {
                    throw new BusinessException("customer_id and  project_id is no match", "客户和项目不匹配");
                }
            }
            // 项目保证金 项目备用金 其它
            if (reimbursementType.equals(project_bond) || reimbursementType.equals(project_reserve_fund) || reimbursementType.equals(others)) {
                if (item.getCustomerId() == null || item.getProjectId() == null) {
                    throw new BusinessException("customer_id or project_id is empty", "客户或项目不能为空");
                }
            } else if (reimbursementType.equals(bid_bond)) {
                if (item.getCustomerId() == null) {
                    throw new BusinessException("customer_id is empty", "客户不能为空");
                }
            } else if(reimbursementType.equals(oil_card)) {
                
            }
        }
        if(item.getCustomerId() == null) return;
        // 设置客户Id,客户名字,项目id,项目名称
        CustomerInfo customerInfo = customerInfoService.findCusInfoById(item.getCustomerId().intValue());
        if (customerInfo != null) {
            item.setCustomerId(customerInfo.getCrmCustomerId().longValue());
            item.setCustomerName(customerInfo.getCustomerName());
            if(item.getProjectId() != null) {
                Project project = projectService.getProject(item.getProjectId().intValue());
                if (project != null) {
                    item.setProjectName(project.getName());
                }
            }
        }
    }
    
    
    public void checkRequisitionItem(RequisitionItemLogisticsVO item) {
        if (StringUtils.isBlank(item.getTypeCode())) {
            throw new BusinessException("type_code is empty", "请款类型不能为空");
        } else {
            if(item.getCustomerId() != null && item.getProjectId() != null) {
                // 客户和项目是否匹配
                 Project project = projectService.findProject(item.getCustomerId().intValue(), item.getProjectId().intValue());
                 if (project == null) {
                     throw new BusinessException("customer_id and  project_id is no match", "客户和项目不匹配");
                 }
             }
            String typeCode = item.getTypeCode();
            // 项目保证金 项目备用金 其它
            if (typeCode.equals(project_bond) || typeCode.equals(project_reserve_fund) || typeCode.equals(others)) {
                if (item.getCustomerId() == null || item.getProjectId() == null) {
                    throw new BusinessException("customer_id or project_id is empty", "客户或项目不能为空");
                }
            } else if (typeCode.equals(bid_bond)) {
                if (item.getCustomerId() == null) {
                    throw new BusinessException("customer_id is empty", "客户不能为空");
                }
            } else if(typeCode.equals(oil_card)) {
                
            }
        }
        // 设置客户Id,客户名字,项目id,项目名称
        CustomerInfo customerInfo = customerInfoService.findCusInfoById(item.getCustomerId());
        if (customerInfo != null) {
            item.setCustomerId(customerInfo.getCrmCustomerId());
            item.setCustomerName(customerInfo.getCustomerName());
            if(item.getProjectId() != null) {
                Project project = projectService.getProject(item.getProjectId());
                if (project != null) {
                    item.setProjectName(project.getName());
                }
            }
        }
    }
}
