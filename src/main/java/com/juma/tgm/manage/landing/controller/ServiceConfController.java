package com.juma.tgm.manage.landing.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.tools.Page;
import com.giants.common.tools.PageCondition;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.configure.domain.ServiceConf;
import com.juma.tgm.configure.service.ServiceConfItemService;
import com.juma.tgm.configure.service.ServiceConfService;

/**
 * @author Libin.Wei
 * @version 1.0.0
 * @ClassName ServiceConfController.java
 * @Description 请填写注释...
 * @Date 2017年11月28日 下午2:17:13
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("serviceConf")
public class ServiceConfController {

    private final Logger log = LoggerFactory.getLogger(ServiceConfController.class);
    @Resource
    private ServiceConfService serviceConfService;
    @Resource
    private ServiceConfItemService serviceConfItemService;

    /**
     * 分页列表
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<ServiceConf> search(PageCondition pageCondition, LoginEmployee loginEmployee) {
        return serviceConfService.search(pageCondition, loginEmployee);
    }

    /**
     * 添加
     */
    @ResponseBody
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(@RequestBody ServiceConf serviceConf, LoginEmployee loginEmployee) {
        serviceConfService.insert(serviceConf, loginEmployee);
    }

    /**
     * 修改
     */
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(@RequestBody ServiceConf serviceConf, LoginEmployee loginEmployee) {
        serviceConfService.update(serviceConf, loginEmployee);
    }

    /**
     * 回调
     */
    @RequestMapping(value = "callback", method = RequestMethod.GET)
    public void callback(HttpServletRequest request, HttpServletResponse response) {
        String fenceId = request.getParameter("fenceId");
        String ext = request.getParameter("ext");
        log.info("创建电子围栏回调参数fenceId：{}", fenceId);
        log.info("创建电子围栏回调参数ext：{}", ext);

        if (StringUtils.isBlank(fenceId) || StringUtils.isBlank(ext)) {
            return;
        }

        serviceConfItemService.doCallBack(Integer.parseInt(ext), Integer.parseInt(fenceId), null);

        response.setContentType("text/plain");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try {
            PrintWriter out = response.getWriter();
            String jsonpCallback = request.getParameter("callback");
            out.println(jsonpCallback + "()");
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取详情
     *
     * @param serviceConfId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "{serviceConfId}/detail", method = RequestMethod.GET)
    public ServiceConf getDetail(@PathVariable Integer serviceConfId) {
        return serviceConfService.getServiceConf(serviceConfId);
    }
}
