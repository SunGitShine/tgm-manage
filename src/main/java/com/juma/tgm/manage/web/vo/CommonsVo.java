package com.juma.tgm.manage.web.vo;

import java.io.Serializable;

/**
 * @ClassName CommonsVo.java
 * @Description 请填写注释...
 * @author Libin.Wei
 * @Date 2017年6月6日 下午1:48:00
 * @version 1.0.0
 * @Copyright 2016 www.jumapeisong.com Inc. All rights reserved.
 */

public class CommonsVo implements Serializable {

    private static final long serialVersionUID = 3159769549529216918L;
    private String areaCodes;
    private String regionCode;
    private String regionName;
    private String ipAddress;

    public String getAreaCodes() {
        return areaCodes;
    }

    public void setAreaCodes(String areaCodes) {
        this.areaCodes = areaCodes;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}
