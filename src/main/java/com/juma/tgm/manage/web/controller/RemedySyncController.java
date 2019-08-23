package com.juma.tgm.manage.web.controller;

import com.giants.common.exception.BusinessException;
import com.juma.tgm.crm.service.RemedySyncService;
import com.juma.tgm.manage.web.vo.RemedyCustomerInfoVo;
import io.swagger.annotations.Api;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 注意：此类只用于同步数据的补救，不可再业务场景中使用
 */

@Api("Remedy-Sync-Controller")
@Deprecated
@Controller
@RequestMapping("remedy/sync")
public class RemedySyncController {

    @Resource
    private RemedySyncService remedySyncService;

    /**
     * 注意：只用于客户同步数据的补救，不可再业务场景中使用
     */
    @ResponseBody
    @RequestMapping(value = "customerInfo", method = RequestMethod.POST)
    public String remedyCustomerInfo(@RequestBody RemedyCustomerInfoVo vo) {
        if (StringUtils.isBlank(vo.getCheckCode())) {
            return "校验码为空";
        }

        if (!"8965796".equals(vo.getCheckCode())) {
            return "校验码不正确";
        }

        if (null == vo.getTenantId() || CollectionUtils.isEmpty(vo.getCrmCustomerIds())) {
            return "租户ID或客户ID为空";
        }

        StringBuffer failedCrmCustomerIds = new StringBuffer();

        for (Integer crmCustomerId : vo.getCrmCustomerIds()) {
            if (null == crmCustomerId) {
                continue;
            }

            try {
                remedySyncService.doSync(crmCustomerId, vo.getTenantId());
            } catch (Exception e) {
                failedCrmCustomerIds.append(crmCustomerId).append("：");
                if (e instanceof BusinessException) {
                    failedCrmCustomerIds.append(((BusinessException) e).getErrorMessage()).append("；");
                } else {
                    failedCrmCustomerIds.append(e.getMessage()).append("；");
                }
            }
        }

        if (failedCrmCustomerIds.length() > 0) {
            return failedCrmCustomerIds.toString();
        }
        return "同步成功";
    }
}
