package com.juma.tgm.manage.truck.vo;

import com.juma.tgm.configure.domain.TruckTypeCity;

import java.math.BigDecimal;

/**
 * @ClassName: TruckTypeCityVo
 * @Description:
 * @author: liang
 * @date: 2017-12-29 14:08
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class TruckTypeCityVo extends TruckTypeCity {

    //车型名称
    public String getTruckTypeName(){
        if(this.getTruckType() == null) return null;

        return this.getTruckType().getTruckTypeName();
    }
    //载重
    public String getTruckTypeLoad(){
        if(this.getTruckType() == null) return null;
        if(this.getTruckType().getTruckTypeLoad() == null) return null;

        return this.getTruckType().getTruckTypeLoad().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }
    //体积
    public String getTruckTypeVolume(){
        if(this.getTruckType() == null) return null;
        if(this.getTruckType().getTruckTypeVolume() == null) return null;

        return this.getTruckType().getTruckTypeVolume().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }
    //冗余载重

    public String getRedundancyLoad(){
        if(this.getTruckType() == null) return null;
        if(this.getTruckType().getRedundancyLoad() == null) return null;

        return this.getTruckType().getRedundancyLoad().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }
    //冗余体积

    public String getRedundancyVolume(){
        if(this.getTruckType() == null) return null;
        if(this.getTruckType().getRedundancyVolume() == null) return null;

        return this.getTruckType().getRedundancyVolume().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }
}
