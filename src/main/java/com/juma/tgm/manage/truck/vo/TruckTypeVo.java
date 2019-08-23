package com.juma.tgm.manage.truck.vo;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.juma.tgm.base.domain.BaseEnumDomian;
import com.juma.tgm.truck.domain.TruckType;

/**
 * @ClassName: TruckTypeVo
 * @Description:
 * @author: liang
 * @date: 2017-12-29 10:21
 * @Copyright: 2017 www.jumapeisong.com Inc. All rights reserved.
 */
public class TruckTypeVo extends TruckType {

    private List<BaseEnumDomian> vehicleBoxTypes;

    private List<BaseEnumDomian> vehicleBoxLengths;

    // 箱型
    private String vehicleBoxTypeName;
    // 箱长
    private String truckLengthName;
    // 车型名称
    private String truckTypeName;

    public String getVehicleBoxTypeName() {
        if (CollectionUtils.isEmpty(this.vehicleBoxTypes))
            return vehicleBoxTypeName;

        for (BaseEnumDomian domain : this.vehicleBoxTypes) {
            try {
                if (NumberUtils.compare(domain.getCode(), this.getVehicleBoxType()) == 0)
                    return domain.getDesc();
            } catch (Exception e) {
                continue;
            }
        }
        return vehicleBoxTypeName;
    }

    public String getTruckLengthName() {
        if (CollectionUtils.isEmpty(this.vehicleBoxLengths))
            return truckLengthName;

        for (BaseEnumDomian domain : this.vehicleBoxLengths) {

            try {
                if (NumberUtils.compare(domain.getCode(), this.getTruckLengthId()) == 0) {
                    return domain.getDesc();
                }
            } catch (Exception e) {
                continue;
            }

        }

        return truckLengthName;
    }

    @Override
    public String getTruckTypeName() {
        if (StringUtils.isBlank(this.getVehicleBoxTypeName()) || StringUtils.isBlank(this.getTruckLengthName()))
            return null;

        return this.getVehicleBoxTypeName() + this.getTruckLengthName();
    }

    public void setVehicleBoxTypes(List<BaseEnumDomian> vehicleBoxTypes) {
        this.vehicleBoxTypes = vehicleBoxTypes;
    }

    public void setVehicleBoxLengths(List<BaseEnumDomian> vehicleBoxLengths) {
        this.vehicleBoxLengths = vehicleBoxLengths;
    }
}
