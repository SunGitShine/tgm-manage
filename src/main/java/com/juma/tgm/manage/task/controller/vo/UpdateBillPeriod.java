package com.juma.tgm.manage.task.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @description: ${description}
 *
 * @author: xieqiang
 *
 * @create: 2019-07-16 10:50
 **/
@ApiModel(value = "修改账期")
public class UpdateBillPeriod {

	@ApiModelProperty(value = "任务id")
	private Integer taskId;

	@ApiModelProperty(value = "账期")
	private Integer billPeriod;

	@ApiModelProperty(value = "账期原因")
	private String billPeriodReason;

	@ApiModelProperty(value = "日志备注")
	private String remark;

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getBillPeriod() {
		return billPeriod;
	}

	public void setBillPeriod(Integer billPeriod) {
		this.billPeriod = billPeriod;
	}

	public String getBillPeriodReason() {
		return billPeriodReason;
	}

	public void setBillPeriodReason(String billPeriodReason) {
		this.billPeriodReason = billPeriodReason;
	}
}
