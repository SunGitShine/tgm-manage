package com.juma.fms.manage.vo;

import com.juma.fms.v2.core.payment.reimbursement.domain.FmsReimbursementVo;
import com.juma.fms.v2.core.payment.requisition.vo.BaseRequisitionVO;


/**
 * 
 * @ClassName:   ReimbursementView   
 * @Description: 报销视图   报销+请款
 * @author:      Administrator
 * @date:        2018年11月27日 下午5:07:19  
 *
 * @Copyright:   2018 www.jumapeisong.com Inc. All rights reserved.
 */
public class ReimbursementView {

    private FmsReimbursementVo reimbursement;
    
    private BaseRequisitionVO requisition;
    
    public FmsReimbursementVo getReimbursement() {
        return reimbursement;
    }

    public void setReimbursement(FmsReimbursementVo reimbursement) {
        this.reimbursement = reimbursement;
    }

    public BaseRequisitionVO getRequisition() {
        return requisition;
    }

    public void setRequisition(BaseRequisitionVO requisition) {
        this.requisition = requisition;
    }
    
}
