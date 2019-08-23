package com.juma.tgm.manage.fms.controller.v3;

import com.giants.common.exception.BusinessException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.fms.domain.v3.ReconcilicationForPayable;
import com.juma.tgm.fms.domain.v3.enums.ReconcilicationForReceivableEnum;
import com.juma.tgm.fms.service.v3.ReconcilicationForPayableService;
import com.juma.tgm.imageUploadManage.domain.FileUploadFilter;
import com.juma.tgm.imageUploadManage.domain.FileUploadParameter;
import com.juma.tgm.imageUploadManage.domain.ImageUploadManage;
import com.juma.tgm.imageUploadManage.service.ImageUploadManageService;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 凭证上传
 */

@Controller
@RequestMapping("voucher/upload")
public class VoucherUploadContrller {

    @Resource
    private ImageUploadManageService imageUploadManageService;
    @Resource
    private ReconcilicationForPayableService reconcilicationForPayableService;

    @ApiOperation(value = "承运商对账单添加对账凭证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "relationId", value = "承运商对账单ID", required = true),
            @ApiImplicitParam(name = "fileName", value = "凭证名称，非必传", dataType = "String"),
            @ApiImplicitParam(name = "listFileUploadUrl", value = "全量对账凭证URL集合", required = true)
    })
    @ResponseBody
    @RequestMapping(value = "add/forPayable", method = RequestMethod.POST)
    public void addReconcilicationForPayableVoucher(@RequestBody FileUploadParameter fileUploadParameter,
                                                    LoginEmployee loginEmployee) {
        if (null == fileUploadParameter.getRelationId()) {
            throw new BusinessException("reconciliationIdUnknown", "errors.common.prompt", "承运商对账单ID未知，请刷新页面重试或联系客服");
        }
        ReconcilicationForPayable reconciliation =
                reconcilicationForPayableService.getReconciliationById(fileUploadParameter.getRelationId());

        if (null == reconciliation) {
            throw new BusinessException("reconciliationNewExamples", "errors.common.prompt", "承运商对账单不存在");
        }

        if (null != reconciliation.getApprovalStatus()
                && ReconcilicationForReceivableEnum.ApprovalStatusStatus.AGREE.getCode() == reconciliation.getApprovalStatus()) {
            throw new BusinessException("reconciliationHasAgree", "errors.common.prompt", "承运商对账单已通过审核，不能再次上传凭证");
        }

        imageUploadManageService.batchInsert(fileUploadParameter.getListUploadFile(),
                fileUploadParameter.getRelationId(),
                ImageUploadManage.ImageUploadManageSign.RECONCILIATION_FOR_PAYABLE, loginEmployee);
    }

    @ApiOperation(value = "承运商对账单删除对账凭证，单个删除")
    @ApiImplicitParam(paramType = "path", name = "imageUploadManageId", value = "单条凭证主键")
    @ResponseBody
    @RequestMapping(value = "del/{imageUploadManageId}/forPayable", method = RequestMethod.DELETE)
    public void delForPayableVoucher(@PathVariable Integer imageUploadManageId, LoginEmployee loginEmployee) {
        imageUploadManageService.delByImageUploadManageId(imageUploadManageId);
    }

    @ApiOperation(value = "承运商对账单对账凭证回显")
    @ApiImplicitParam(paramType = "path", name = "reconcilicationId", value = "承运商对账单ID")
    @ResponseBody
    @RequestMapping(value = "list/{reconcilicationId}/forPayable", method = RequestMethod.POST)
    public List<ImageUploadManage> listForPayableVoucher(@PathVariable Integer reconcilicationId,
                                                         LoginEmployee loginEmployee) {
        return imageUploadManageService.listByRelationIdAndSign(reconcilicationId,
                ImageUploadManage.ImageUploadManageSign.RECONCILIATION_FOR_PAYABLE.getCode());
    }
}
