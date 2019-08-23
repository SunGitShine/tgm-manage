package com.juma.tgm.manage.authority.vo;

import java.util.ArrayList;
import java.util.List;

import com.juma.auth.employee.domain.LoginEmployee.AuthDepartment;
import com.juma.auth.employee.domain.LoginEmployee.LoginDepartment;
import com.juma.auth.tenant.domain.Tenant;
import com.juma.auth.user.domain.LoginUser;

/**
 * @ClassName LoginEmployeeVo.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年12月12日 下午5:11:17
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class LoginEmployeeVo extends LoginUser {

    private LoginDepartment loginDepartment;

    private List<Tenant> authTenants;

    private List<AuthDepartment> authDepartments;

    private List<Integer> businessAreaIds = new ArrayList<Integer>();

    public LoginDepartment getLoginDepartment() {
        return loginDepartment;
    }

    public void setLoginDepartment(LoginDepartment loginDepartment) {
        this.loginDepartment = loginDepartment;
    }

    public List<Tenant> getAuthTenants() {
        return authTenants;
    }

    public void setAuthTenants(List<Tenant> authTenants) {
        this.authTenants = authTenants;
    }

    public List<AuthDepartment> getAuthDepartments() {
        return authDepartments;
    }

    public void setAuthDepartments(List<AuthDepartment> authDepartments) {
        this.authDepartments = authDepartments;
    }

    public List<Integer> getBusinessAreaIds() {
        return businessAreaIds;
    }

    public void setBusinessAreaIds(List<Integer> businessAreaIds) {
        this.businessAreaIds = businessAreaIds;
    }

}
