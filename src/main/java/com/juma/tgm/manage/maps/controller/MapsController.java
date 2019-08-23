package com.juma.tgm.manage.maps.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.basicTruckType.service.LocationService;
import com.juma.tgm.user.domain.CurrentUser;
import com.juma.tgm.waybill.domain.map.MapBusinessInfo;

/**
 * 在途监控
 * 
 * @author weilibin
 *
 */

@Controller
@RequestMapping("mapView")
public class MapsController {

    private static final Logger log = LoggerFactory.getLogger(MapsController.class);
    @Resource
    private LocationService locationService;

    @ResponseBody
    @RequestMapping(value = "view", method = RequestMethod.GET)
    public MapBusinessInfo view(HttpServletRequest request, @ModelAttribute("currentUser") CurrentUser currentUser,
            LoginEmployee loginEmployee) {
        log.info(JSON.toJSONString(currentUser.getBusinessAreas()));
        String servletPath = request.getServletPath();
        String url = request.getRequestURL().toString();
        MapBusinessInfo info = locationService.view(currentUser, loginEmployee);
        info.setInitURL(url.replace(servletPath, "/mapView/init.html"));
        info.setCallbackURL(url.replace(servletPath, "/mapView/callback.html"));
        return info;
    }

    @RequestMapping(value = "callback", method = RequestMethod.GET)
    public void callback(HttpServletRequest request, HttpServletResponse response,
            LoginEmployee loginEmployee) {
        String plateNumber = request.getParameter("plateNumber");
        try {
            response.setContentType("text/plain");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            Object json = JSON.toJSON(locationService.callback(plateNumber, loginEmployee));
            PrintWriter out = response.getWriter();
            String jsonpCallback = request.getParameter("callback");
            out.println(jsonpCallback + "(" + json + ")");
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
