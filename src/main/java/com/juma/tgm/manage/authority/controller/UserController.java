/**
 * 
 */
package com.juma.tgm.manage.authority.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.exception.BusinessException;
import com.giants.common.lang.exception.CategoryCodeFormatException;
import com.juma.auth.conf.service.BusinessAreaService;
import com.juma.auth.employee.domain.Department;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.domain.LoginEmployee.LoginDepartment;
import com.juma.auth.employee.domain.LoginEmployee.LoginDepartment.BusinessArea;
import com.juma.auth.employee.service.DepartmentService;
import com.juma.auth.employee.service.EmployeeService;
import com.juma.tgm.manage.authority.vo.LoginEmployeeVo;

/**
 * @author vencent.lu
 *
 *         Create Date:2014年2月23日
 */
@Controller
@RequestMapping(value = "user")
public class UserController {
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Resource
    private EmployeeService employeeService;
    @Resource
    private BusinessAreaService businessAreaService;
    
    @Resource
    private DepartmentService departmentService;
    
    @RequestMapping(value = "employee/{employeeId}/subcompany", method = RequestMethod.GET)
    @ResponseBody
    public List<Department> getSubCompany(@PathVariable Integer employeeId) throws BusinessException, CategoryCodeFormatException {
        return departmentService.findSubCompanyByEmployee(employeeId);
    }
    
    @RequestMapping(value = "employee/subcompany", method = RequestMethod.GET)
    @ResponseBody
    public List<Department> getSubCompany(LoginEmployee loginEmployee) throws BusinessException, CategoryCodeFormatException {
        return departmentService.findSubCompanyByEmployee(loginEmployee.getEmployeeId());
    }
    

    /**
     * 当前用户相关信息放入会话
     * 
     * @param loginEmployee
     * @return
     */
    @RequestMapping(value = "getLoginUser", method = RequestMethod.GET)
    @ResponseBody
    public LoginEmployeeVo getLoginUser(LoginEmployee loginEmployee) {
        LoginEmployeeVo vo = new LoginEmployeeVo();
        try {
            BeanUtils.copyProperties(vo, loginEmployee);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        vo.setAuthTenants(this.employeeService.loadTenants(loginEmployee));
        vo.setAuthDepartments(loginEmployee.getAuthDepartments());
        vo.setAuthTenants(this.employeeService.loadTenants(loginEmployee));

        LoginDepartment loginDepartment = loginEmployee.getLoginDepartment();
        if (null == loginDepartment) {
            return vo;
        }

        List<BusinessArea> businessAreas = loginDepartment.getBusinessAreas();
        if (CollectionUtils.isEmpty(businessAreas)) {
            return vo;
        }

        for (BusinessArea businessArea : businessAreas) {
            if (StringUtils.isBlank(businessArea.getAreaCode())) {
                continue;
            }

            com.juma.auth.conf.domain.BusinessArea loadBusinessArea = businessAreaService
                    .loadBusinessArea(businessArea.getAreaCode(), loginEmployee);
            if (null == loadBusinessArea) {
                continue;
            }

            if (null == loadBusinessArea.getParentBusinessAreaId()) {
                vo.getBusinessAreaIds().add(0);
                break;
            }

            vo.getBusinessAreaIds().add(loadBusinessArea.getBusinessAreaId());
        }

        return vo;
    }

    @RequestMapping(value = "switchLoginDepartment/{departmentId}", method = RequestMethod.GET)
    @ResponseBody
    public LoginEmployee switchLoginDepartment(@PathVariable Integer departmentId, LoginEmployee loginEmployee) {
        return this.employeeService.refreshLoginDepartment(loginEmployee, departmentId);
    }

}
