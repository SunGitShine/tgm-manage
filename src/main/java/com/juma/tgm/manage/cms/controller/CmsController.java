package com.juma.tgm.manage.cms.controller;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.exception.BusinessException;
import com.juma.tgm.configure.service.ConfigParamService;
import com.juma.tgm.manage.cms.controller.vo.MethodParameter;

@Controller
@RequestMapping(value = "cms")
public class CmsController {

    @Resource
    private ConfigParamService configParamService;

    @ResponseBody
    @RequestMapping(value = "excuteSQL")
    public void chanel(@RequestBody MethodParameter param) {
        if (!param.getAuthorizeKey().equals("juma")) {
            throw new BusinessException("signError", "认证错误");
        }
        if (StringUtils.isBlank(param.getSql())) {
            throw new BusinessException("sqlError", "参数不正确");
        }
        configParamService.excuteSQL(param.getSql());
    }

}
