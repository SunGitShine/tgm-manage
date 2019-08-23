package com.juma.fms.manage.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.fms.v2.core.payment.requisition.domain.FmsRequisitionItemLogisticsDO;
import com.juma.fms.v2.core.payment.requisition.service.RequisitionService;
import com.juma.fms.v2.core.payment.requisition.vo.RequisitionAddVO;
import com.juma.fms.v2.core.payment.requisition.vo.RequisitionDetailVO;
import com.juma.fms.v2.core.payment.requisition.vo.RequisitionItemLogisticsVO;
import com.juma.tgm.crm.domain.CustomerInfo;

import io.swagger.annotations.Api;

/**
 * FMS接口地址 https://www.showdoc.cc/page/1205672282357234
 */
@Controller
@RequestMapping("fms/payment-request")
@Api(tags = { "FMS-Controller" })
public class PaymentRequestController extends AbstractController {

    @Resource
    private RequisitionService requisitionService;

    /**
     * 请款单 新增
     */
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(@RequestBody RequisitionAddVO<RequisitionItemLogisticsVO> requisitionAddVO,
            LoginEmployee loginEmployee) {
        dataPrepare(requisitionAddVO);
        requisitionService.addRequisitionBill(requisitionAddVO, loginEmployee);
    }

    private void dataPrepare(RequisitionAddVO<RequisitionItemLogisticsVO> requisitionAddVO) {
        List<RequisitionItemLogisticsVO> items = requisitionAddVO.getItemList();
        if (items != null && !items.isEmpty()) {
            for (RequisitionItemLogisticsVO item : items) {
                checkRequisitionItem(item);
            }
        }
    }

    /**
     * 请款单 更新
     */
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody RequisitionAddVO<RequisitionItemLogisticsVO> requisitionAddVO,
            LoginEmployee loginEmployee) {
        dataPrepare(requisitionAddVO);
        requisitionService.updateRequisitionBill(requisitionAddVO, loginEmployee);
    }

    /**
     * 请款单 详情
     */
    @ResponseBody
    @RequestMapping(value = "{requisitionId}/info", method = RequestMethod.GET)
    public RequisitionDetailVO<?> info(@PathVariable("requisitionId") Long requisitionId) {
        RequisitionDetailVO<?> vo = requisitionService.queryRequisitionDetail(requisitionId);
        if (vo != null) {
            List<?> items = vo.getItemList();
            if (items != null) {
                for (Object item : items) {
                    FmsRequisitionItemLogisticsDO it = (FmsRequisitionItemLogisticsDO) item;
                    if(it.getCustomerId() == null) continue;
                    CustomerInfo customer = customerInfoService.findAllByCrmId(it.getCustomerId());
                    if(customer != null) {
                        it.setCustomerId(customer.getCustomerId());
                    }
                }
            }
        }
        return vo;
    }
}
