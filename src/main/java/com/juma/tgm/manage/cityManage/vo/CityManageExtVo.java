package com.juma.tgm.manage.cityManage.vo;

import com.juma.tgm.cityManage.domain.CityManage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.List;

/**
 * @ClassName: CityManageExtVo
 * @Description:
 * @author: liang
 * @date: 2017-12-28 10:45
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class CityManageExtVo extends CityManage {

    private String largeAreaName;

    private List<CityManage> largeAreaDatas;

    public String getLargeAreaName() {
        if (CollectionUtils.isEmpty(largeAreaDatas)) return null;

        for (CityManage parent : largeAreaDatas) {
            if (NumberUtils.compare(this.getParentCityManageId(), parent.getCityManageId()) == 0)
                return parent.getCityName();
        }
        return null;
    }

    public void setLargeAreaDatas(List<CityManage> largeAreaDatas) {
        this.largeAreaDatas = largeAreaDatas;
    }
}
