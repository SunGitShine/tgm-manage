package com.juma.customize.xidi.waybill.service;

import com.giants.common.exception.BusinessException;
import com.juma.customize.annotation.CustomizeLayer;
import com.juma.customize.annotation.Customized;
import com.juma.tgm.common.Constants;
import com.juma.tgm.waybill.domain.WaybillMap;
import com.juma.tgm.waybill.service.customize.xidi.XidiWaybillQueryService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName: XidiWaybillQueryServiceCustomize
 * @Description:
 * @author: liang
 * @date: 2018-03-15 14:16
 * @Copyright: 2018 www.jumapeisong.com Inc. All rights reserved.
 */
@Customized(tenantKey = Constants.TENANT_KEY_XIDI_LOGISTICS, layer = CustomizeLayer.service)
@Component
public class XidiWaybillQueryServiceCustomize {
//public class XidiWaybillQueryServiceCustomize implements XidiWaybillQueryService {

    @Resource
    private XidiWaybillQueryService xidiWaybillQueryService;

//    @Override
    public WaybillMap findWaybillMapById(Integer waybillId) throws BusinessException {
        return xidiWaybillQueryService.findWaybillMapById(waybillId);
    }
}
