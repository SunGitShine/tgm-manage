package com.juma.tgm.manage.project.controller.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "项目仓库")
public class ProjectDepotDto {

    @ApiModelProperty(value = "项目id")
    private Integer projectId;

    @ApiModelProperty(value = "仓库名字")
    private String depotName;

    @ApiModelProperty(value = "仓库地址")
    private String depotAddress;

    @ApiModelProperty(value = "仓库坐标")
    private String depotCoordinates;

    @ApiModelProperty(value = "仓库联系人")
    private String linkMan;

    @ApiModelProperty(value = "仓库联系人电话")
    private String linkManPhone;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

    public String getDepotAddress() {
        return depotAddress;
    }

    public void setDepotAddress(String depotAddress) {
        this.depotAddress = depotAddress;
    }

    public String getDepotCoordinates() {
        return depotCoordinates;
    }

    public void setDepotCoordinates(String depotCoordinates) {
        this.depotCoordinates = depotCoordinates;
    }

    public String getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan;
    }

    public String getLinkManPhone() {
        return linkManPhone;
    }

    public void setLinkManPhone(String linkManPhone) {
        this.linkManPhone = linkManPhone;
    }
}
