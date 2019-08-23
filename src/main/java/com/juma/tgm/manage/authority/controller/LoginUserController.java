/**
 *
 */
package com.juma.tgm.manage.authority.controller;


import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author vencent.lu
 *         <p/>
 *         Create Date:2014年2月23日
 */
@Controller
@RequestMapping(value = "user")
public class LoginUserController {

    @Resource
    private EmployeeService employeeService;

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    @ResponseBody
    public void logout(LoginEmployee loginEmployee) {
        employeeService.logout(loginEmployee.getSessionId());
    }

}
