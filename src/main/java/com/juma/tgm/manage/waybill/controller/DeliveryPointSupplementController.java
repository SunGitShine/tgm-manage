package com.juma.tgm.manage.waybill.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.collections.CollectionUtils;
import com.giants.common.exception.BusinessException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.user.domain.LoginUser;
import com.juma.tgm.manage.waybill.vo.DeliveryPointSupplementVO;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.user.domain.CurrentUser;
import com.juma.tgm.waybill.domain.DeliveryPointSupplement;
import com.juma.tgm.waybill.domain.Waybill;
import com.juma.tgm.waybill.domain.WaybillParam;
import com.juma.tgm.waybill.domain.WaybillReceiveAddress;
import com.juma.tgm.waybill.domain.bo.WaybillReceiveAddressVo;
import com.juma.tgm.waybill.service.DeliveryPointSupplementService;
import com.juma.tgm.waybill.service.WaybillParamService;
import com.juma.tgm.waybill.service.WaybillReceiveAddressService;
import com.juma.tgm.waybill.service.WaybillService;

/**
 * @ClassName: DeliveryPointSupplementController
 * @Description:
 * @author: liang
 * @date: 2017-04-27 16:52
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
@Controller
@RequestMapping(value = "deliveryPointSupplement")
public class DeliveryPointSupplementController extends BaseController {

    @Resource
    private DeliveryPointSupplementService deliveryPointSupplementService;

    @Resource
    private WaybillReceiveAddressService waybillReceiveAddressService;

    @Resource
    private WaybillService waybillService;

    @Resource
    private WaybillParamService waybillParamService;

    /**
     * 打开修改线路页面
     */
    @ResponseBody
    @RequestMapping(value = "{waybillId}/json/edit")
    public DeliveryPointSupplementVO jsonEdit(@PathVariable(value = "waybillId") Integer waybillId, String from) {
        DeliveryPointSupplementVO vo = new DeliveryPointSupplementVO();
        vo.setListDeliveryPointSupplement(deliveryPointSupplementService.getByWayBill(waybillId));
        // 原有收货地
        List<WaybillReceiveAddress> addresses = waybillReceiveAddressService.findAllByWaybillId(waybillId);
        vo.setReceiveListFlag(CollectionUtils.isNotEmpty(addresses));
        vo.setListReceiveAddress(addresses);
        vo.setWaybill(waybillService.getWaybill(waybillId));
        if (StringUtils.isNotBlank(from) && from.equals("list")) {
            vo.setDataFrom("list");
        }

        return vo;
    }

    /**
     * 更新运单线路
     *
     * @param addressList
     * @param id
     * @param loginEmployee
     */
    @RequestMapping(value = "{waybillId}/doUpdate", method = RequestMethod.POST)
    @ResponseBody
    public void doUpdate(@RequestBody List<WaybillReceiveAddressVo> addressList, @PathVariable("waybillId") Integer id,
            CurrentUser currentUser, LoginEmployee loginEmployee) {

        if (CollectionUtils.isEmpty(addressList)) {
            return;
        }

        Waybill waybill = waybillService.getWaybill(id);
        if (null != waybill && null != waybill.getIsChangeDeliveryPoint()
                && Waybill.ChangeDeliveryPoint.INVALID_UPLOAD.getCode() == waybill.getIsChangeDeliveryPoint()) {
            throw new BusinessException("changeDeliveryPointHasInvalid", "waybill.error.changeDeliveryPointHasInvalid");
        }

        // 判断地址是否修改过：包含顺序的调换;比较方式：地址短名+联系人姓名+联系人电话 的MD5比较
        // 原有收货地
        List<WaybillReceiveAddress> addresses = waybillReceiveAddressService.findAllByWaybillId(id);

        StringBuffer oldAddressSf = new StringBuffer("");
        StringBuffer newAddressSf = new StringBuffer("");
        for (WaybillReceiveAddress receiveAddress : addresses) {
            buildAddresss(oldAddressSf, receiveAddress);
        }

        List<WaybillReceiveAddress> postData = new ArrayList<>();

        for (WaybillReceiveAddress addr : addressList) {
            buildAddresss(newAddressSf, addr);

            addr.setWaybillId(id);
            postData.add(addr);
        }

        // 判断是否修改
        if (DigestUtils.md5Hex(oldAddressSf.toString()).equals(DigestUtils.md5Hex(newAddressSf.toString()))) {
            return;
        }

        deliveryPointSupplementService.updateWaybillReceiveAddress(postData, loginEmployee);

        // 添加修改时间
        WaybillParam param = waybillParamService.findByWaybillId(id);
        if (null == param) {
            param = new WaybillParam();
            param.setWaybillId(id);
            param.setUpdateDeliveryPointSupplementTime(new Date());
            waybillParamService.insert(param, loginEmployee);
            return;
        }

        param.setUpdateDeliveryPointSupplementTime(new Date());
        waybillParamService.update(param, loginEmployee);

        // 判断是不是承运单，若是则同步修改转运方的运单
        this.modifyTransformBillParam(id, loginEmployee);
    }

    // 判断是不是承运单，若是则同步修改转运方的运单
    private void modifyTransformBillParam(Integer transformBillLinkId, LoginUser loginUser) {
        if (null == transformBillLinkId) {
            return;
        }
        
        WaybillParam transformBillParam = waybillParamService.findByTransformBillLinkId(transformBillLinkId);
        if (null == transformBillParam) {
            return;
        }
        
        transformBillParam.setUpdateDeliveryPointSupplementTime(new Date());
        waybillParamService.update(transformBillParam, loginUser);
    }

    // 地址短名+联系人姓名+联系人电话
    private void buildAddresss(StringBuffer sf, WaybillReceiveAddress receiveAddress) {
        if (StringUtils.isNotBlank(receiveAddress.getAddressName())) {
            sf.append(receiveAddress.getAddressName().trim());
        }
        if (StringUtils.isNotBlank(receiveAddress.getContactName())) {
            sf.append(receiveAddress.getContactName().trim());
        }
        if (StringUtils.isNotBlank(receiveAddress.getContactPhone())) {
            sf.append(receiveAddress.getContactPhone().trim());
        }
    }

    /**
     * 确认标记成无效上传
     */
    @RequestMapping(value = "{waybillId}/invalid", method = RequestMethod.GET)
    @ResponseBody
    public void invalid(@PathVariable("waybillId") Integer waybillId, CurrentUser currentUser,
            LoginEmployee loginEmployee) {
        deliveryPointSupplementService.invalid(waybillId, loginEmployee);
    }

    /**
     * 删除线路修改记录
     * 
     * @param pointSupplement
     * @param loginEmployee
     */
    @RequestMapping(value = "doDel", method = RequestMethod.POST)
    @ResponseBody
    public void doDel(@RequestBody DeliveryPointSupplement pointSupplement, CurrentUser currentUser,
            LoginEmployee loginEmployee) {
        if (pointSupplement.getDeliveryPointSupplementId() == null) {
            throw new BusinessException("DeliveryPointSupplementIdNull", "errors.paramCanNotNull");
        }

        DeliveryPointSupplement supplement = deliveryPointSupplementService
                .get(pointSupplement.getDeliveryPointSupplementId());
        if (null == supplement) {
            return;
        }

        deliveryPointSupplementService.del(pointSupplement, loginEmployee);
    }

}
