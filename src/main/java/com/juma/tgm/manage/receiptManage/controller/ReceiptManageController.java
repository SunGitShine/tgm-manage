package com.juma.tgm.manage.receiptManage.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.exception.BusinessException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.receiptManage.domain.ReceiptManage;
import com.juma.tgm.receiptManage.service.ReceiptManageService;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.service.WaybillCommonService;

/**
 * Created by shawn_lin on 2017/7/10.
 */
@Controller
@RequestMapping("receipt/manage")
public class ReceiptManageController {

    @Resource
    private ReceiptManageService receiptManageService;
    @Resource
    private WaybillCommonService waybillCommonService;

    /**
     * 图片上传
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public void uploadReceiptImage(@RequestBody ReceiptManage receiptManage, LoginEmployee loginEmployee) {
        // 判断运单是不是承运运单，若是承运运单，则使用原单ID添加
        Waybill waybill = waybillCommonService.findWaybillByTransformBillId(receiptManage.getWaybillId());
        if (null != waybill) {
            receiptManage.setWaybillId(waybill.getWaybillId());
            if(waybill.getStatusView() != null && waybill.getStatusView() != Waybill.StatusView.FINISH.getCode()) {
                throw new BusinessException("receipt.forbidden", "只有已完成的运单才能上传回单");
            }
        } else {
            // 不是转运单
            waybill = waybillCommonService.getWaybillById(receiptManage.getWaybillId());
            if (null == waybill) {
                // 没有找到运单，直接返回
                return;
            }

            if(waybill.getStatusView() != null && waybill.getStatusView() != Waybill.StatusView.FINISH.getCode()) {
                throw new BusinessException("receipt.forbidden", "只有已完成的运单才能上传回单");
            }
        }

        if (null == receiptManage.getReceiptManageId()) {
            receiptManageService.insert(receiptManage, loginEmployee);
            return;
        }
        receiptManageService.deleteAndInsert(receiptManage, loginEmployee);
    }

    /**
     * 回单回显
     */
    @RequestMapping(value = "image/{waybillId}/show", method = RequestMethod.GET)
    @ResponseBody
    public List<ReceiptManage> showReceipt(@PathVariable Integer waybillId) {
        List<ReceiptManage> receiptManages = receiptManageService.listByWaybillId(waybillId);
        return receiptManages.isEmpty() ? null : receiptManages;
    }
}
