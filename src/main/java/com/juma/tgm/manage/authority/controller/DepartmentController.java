package com.juma.tgm.manage.authority.controller;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.juma.auth.employee.domain.Department;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.auth.employee.service.DepartmentService;
import com.juma.auth.user.domain.LoginUser;

/**
 * @ClassName DepartmentController.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年8月10日 上午11:06:27
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

@Controller
@RequestMapping("department")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @RequestMapping(value = "/children", method = RequestMethod.GET)
    @ResponseBody
    public List<Department> listChildren(String departmentCode, String defaultDepartmentCode,
            LoginEmployee loginEmployee) {
        if (departmentCode == null) {
            if (defaultDepartmentCode == null) {
                return listEmployeeDepartment(loginEmployee);

            } else if (defaultDepartmentCode.trim().equals("")) {
                return departmentService.findRootDepartment(loginEmployee);

            } else {
                return departmentService.findChildDepartment(defaultDepartmentCode, loginEmployee);
            }
        }
        return departmentService.findChildDepartment(departmentCode, loginEmployee);
    }
    
    @RequestMapping(value = "/{departmentCode}/children", method = RequestMethod.GET)
    @ResponseBody
    public List<Department> listChildren2(@PathVariable String departmentCode,
            LoginEmployee loginEmployee,
            HttpServletRequest request,
            HttpServletResponse response) {
        String _departmentCode = StringUtils.isBlank(departmentCode) ? "0" : departmentCode;
        if(_departmentCode.equals("0")) {
            return listEmployeeDepartment(loginEmployee);
        }
        return departmentService.findChildDepartment(departmentCode, loginEmployee);
    }

    @RequestMapping(value = "/employeeDepartment", method = RequestMethod.GET)
    @ResponseBody
    public List<Department> listEmployeeDepartment(LoginEmployee loginEmployee) {
        LoginEmployee.LoginDepartment loginDepartment = loginEmployee.getLoginDepartment();
        if (loginDepartment == null) {
            return null;
        }
        List<Department> departmentList = new ArrayList<Department>(1);
        Department department = departmentService.loadDepartment(loginDepartment.getDepartmentId(), loginEmployee);
        if (department != null) {
            departmentList.add(department);
        }
        return departmentList;
    }

    @RequestMapping(value = "/employeeAllDepartment", method = RequestMethod.GET)
    @ResponseBody
    public List<Department> listEmployeeAllDepartment(LoginEmployee loginEmployee) {
        LoginEmployee.LoginDepartment loginDepartment = loginEmployee.getLoginDepartment();
        if (loginDepartment == null) {
            return new ArrayList<Department>();
        }

        return parallelTree(departmentService.loadDepartment(loginDepartment.getDepartmentId(), loginEmployee),
                loginEmployee);
    }

    private List<Department> parallelTree(Department department, LoginUser loginUser) {
        List<Department> departmentTree = new ArrayList<Department>();

        if (null == department) {
            return departmentTree;
        }

        departmentTree.add(department);

        // 深度优先算法
        Deque<Integer> dfs = new LinkedList<Integer>();
        if (!department.isLeaf()) {
            dfs.push(department.getDepartmentId());
        }

        while (!dfs.isEmpty()) {
            Integer departmentId = dfs.pop();
            List<Department> list = departmentService.findChildDepartment(departmentId, loginUser);
            for (Department d : list) {
                if (d.isLeaf()) {
                    continue;
                }
                dfs.push(d.getDepartmentId());
            }
            departmentTree.addAll(list);
        }
        return departmentTree;
    }
}
