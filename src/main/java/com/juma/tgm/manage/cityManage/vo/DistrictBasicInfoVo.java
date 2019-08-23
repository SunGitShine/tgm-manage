package com.juma.tgm.manage.cityManage.vo;

import com.juma.tgm.cityManage.domain.CityManage;
import com.juma.tgm.cityManage.domain.CityManageInfo;

import java.util.List;

/**
 * @ClassName: districtBasicInfoVo
 * @Description:
 * @author: liang
 * @date: 2017-12-27 17:19
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class DistrictBasicInfoVo {
    /**
     * 出发城市
     */
    private List<CityManage> cityManages;

    /**
     * 已开通的省
     */
    private CityManageInfo cityManageInfo;


    public List<CityManage> getCityManages() {
        return cityManages;
    }

    public void setCityManages(List<CityManage> cityManages) {
        this.cityManages = cityManages;
    }

    public CityManageInfo getCityManageInfo() {
        return cityManageInfo;
    }

    public void setCityManageInfo(CityManageInfo cityManageInfo) {
        this.cityManageInfo = cityManageInfo;
    }
}
