package com.juma.tgm.manage.cityManage.vo;

import java.io.Serializable;

public class CityManageVo implements Serializable {

    private static final long serialVersionUID = 4668713887417640919L;
    private Integer regionId;
	private Integer cityManageId;
	private Integer sign;
	private Integer parentCityManageId;

	public Integer getSign() {
		return sign;
	}

	public void setSign(Integer sign) {
		this.sign = sign;
	}

	public Integer getRegionId() {
		return regionId;
	}

	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}

	public Integer getCityManageId() {
		return cityManageId;
	}

	public void setCityManageId(Integer cityManageId) {
		this.cityManageId = cityManageId;
	}

    public Integer getParentCityManageId() {
        return parentCityManageId;
    }

    public void setParentCityManageId(Integer parentCityManageId) {
        this.parentCityManageId = parentCityManageId;
    }

}
