/**
 * 
 */
package com.juma.tgm.manage.authority.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author vencent.lu
 *
 */
public class UserInfoVo implements Serializable {

    private static final long serialVersionUID = -8183815919099133539L;
    private Integer userId;
    private String loginName;
    private String password;
    private String name;
    private boolean isTest = false;
    private Byte sex;
    private Byte age;
    private String emailAddress;
    private String mobileNumber;

    private Integer stationId;
    private List<Integer> roleIds;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getLoginName() {
        return loginName;
    }
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Byte getSex() {
        return sex;
    }
    public void setSex(Byte sex) {
        this.sex = sex;
    }
    public Byte getAge() {
        return age;
    }
    public void setAge(Byte age) {
        this.age = age;
    }
    public Integer getStationId() {
        return stationId;
    }
    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }
    public List<Integer> getRoleIds() {
        return roleIds;
    }
    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean isTest) {
        this.isTest = isTest;
    }
    
}