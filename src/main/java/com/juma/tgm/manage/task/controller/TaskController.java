package com.juma.tgm.manage.task.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.conf.domain.ConfParamOption;
import com.juma.tgm.capacity.domian.vo.CapacityFilter;
import com.juma.tgm.common.query.QueryCond;
import com.juma.tgm.common.vo.Page;
import com.juma.tgm.manage.task.controller.vo.ChangeCapacity;
import com.juma.tgm.manage.task.controller.vo.TaskOperate;
import com.juma.tgm.manage.task.controller.vo.UpdateBillPeriod;
import com.juma.tgm.task.dto.manage.TaskFilter;
import com.juma.tgm.task.service.TaskFacadeService;
import com.juma.tgm.task.service.TaskScheduledService;
import com.juma.tgm.task.vo.gateway.InviteRequest;
import com.juma.tgm.task.vo.manage.CapacityPoolPage;
import com.juma.tgm.task.vo.manage.Task;
import com.juma.tgm.task.vo.manage.TaskDetail;
import com.juma.tgm.tools.service.AuthCommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @description: ${description}
 *
 * @author: xieqiang
 *
 * @create: 2019-07-12 18:09
 **/
@Api(value = "任务管理")
@RestController
@RequestMapping(value = "task")
public class TaskController {

	@Resource
	private TaskScheduledService taskScheduledService;

	@Resource
	private TaskFacadeService taskFacadeService;

	@Resource
	private AuthCommonService authCommonService;

	@ApiOperation(value = "任务列表",notes = "任务管理")
	@RequestMapping(value = "findTaskByPage",method = RequestMethod.POST)
	public Page<Task> findTaskByPage (@RequestBody QueryCond<TaskFilter> query, @ApiParam(hidden = true) LoginEmployee loginEmployee) {
		return taskScheduledService.findTaskByPage(query,loginEmployee);
	}

	@ApiOperation(value = "运力列表",notes = "任务派车")
	@RequestMapping(value = "findCapacityPoolPage",method = RequestMethod.POST)
	public Page<CapacityPoolPage> findCapacityPoolPage (@RequestBody QueryCond<CapacityFilter> queryCond, @ApiParam(hidden = true) LoginEmployee loginEmployee) {
		return taskScheduledService.findCapacityPoolPage(queryCond,loginEmployee);
	}

	@ApiOperation(value = "任务详情",notes = "任务管理")
	@RequestMapping(value = "findTaskDetail/{taskId}",method = RequestMethod.GET)
	public TaskDetail findTaskDetail (@PathVariable Integer taskId, @ApiParam(hidden = true) LoginEmployee loginEmployee) {
		return taskScheduledService.findTaskDetail(taskId, loginEmployee);
	}

	@ApiOperation(value = "任务指派",notes = "任务管理")
	@RequestMapping(value = "inviteVendor",method = RequestMethod.POST)
	public void inviteVendor (@RequestBody InviteRequest request, @ApiParam(hidden = true) LoginEmployee loginEmployee) {
		taskScheduledService.inviteVendorMange(request, loginEmployee);
	}

	@ApiOperation(value = "修改账期",notes = "任务管理")
	@RequestMapping(value = "updatePeriod",method = RequestMethod.POST)
	public void updateBillPeriod (@RequestBody UpdateBillPeriod billPeriod, @ApiParam(hidden = true) LoginEmployee loginEmployee) {
		taskFacadeService.updateBillPeriod(billPeriod.getTaskId()
			,billPeriod.getBillPeriod()
			,billPeriod.getBillPeriodReason(),loginEmployee);
	}

	@ApiOperation(value = "是否有待回复", notes = "任务改派")
	@RequestMapping(value = "haveWaitBack/{taskId}",method = RequestMethod.GET)
	public Boolean haveWaitBack(@PathVariable Integer taskId){
		return taskScheduledService.haveWaitBack(taskId);
	}

	@ApiOperation(value = "更换运力前的冲突检查",notes = "更换运力")
	@RequestMapping(value = "conflictChangeCapacity",method = RequestMethod.POST)
	public int conflictChangeCapacity(@RequestBody ChangeCapacity changeCapacity,@ApiParam(hidden = true)LoginEmployee loginEmployee) {
		return taskScheduledService.conflictChangeCapacity(changeCapacity.getTaskId(), changeCapacity.getChangeDate()
			,changeCapacity.getDriverId(),changeCapacity.getTruckId(),changeCapacity.getType(),loginEmployee);
	}

	@ApiOperation(value = "任务派车前的冲突检查",notes = "任务派车/改派")
	@RequestMapping(value = "conflictInviteVendor",method = RequestMethod.POST)
	public int conflictInviteVendor(@RequestBody InviteRequest request, @ApiParam(hidden = true) LoginEmployee loginEmployee) {
		return taskScheduledService.conflictInviteVendor(request.getTaskId(), request.getTruckId(), loginEmployee);
	}

	@ApiOperation(value = "更换运力",notes = "任务管理")
	@RequestMapping(value = "changeCapacity",method = RequestMethod.POST)
	public void changeCapacity(@RequestBody ChangeCapacity changeCapacity,@ApiParam(hidden = true)LoginEmployee loginEmployee) {
		taskScheduledService.doChangeCapacity(changeCapacity.getTaskId(), changeCapacity.getChangeDate()
			,changeCapacity.getDriverId(),changeCapacity.getTruckId(),changeCapacity.getType(),loginEmployee);
	}

	@ApiOperation(value = "取消任务",notes = "任务管理")
	@RequestMapping(value = "cancel",method = RequestMethod.POST)
	public void cancelTask (@RequestBody TaskOperate taskOperate, @ApiParam(hidden = true)LoginEmployee loginEmployee) {
		taskFacadeService.cancelTask(taskOperate.getTaskId(),taskOperate.getReason(),loginEmployee);
	}

	@ApiOperation(value = "结束任务",notes = "任务管理")
	@RequestMapping(value = "end",method = RequestMethod.POST)
	public void endTask (@RequestBody TaskOperate taskOperate, @ApiParam(hidden = true)LoginEmployee loginEmployee) {
		taskFacadeService.endTask(taskOperate.getTaskId(),loginEmployee);
	}

	@ApiOperation(value = "重启任务",notes = "任务管理")
	@RequestMapping(value = "recover",method = RequestMethod.POST)
	public void recoverTask (@RequestBody TaskOperate taskOperate, @ApiParam(hidden = true)LoginEmployee loginEmployee) {
		taskFacadeService.recoverTask(taskOperate.getTaskId(),loginEmployee);
	}

	@ApiOperation(value = "获取配置数据", notes = "获取配置数据"
		+ "运力冲突有效冗余：transport_conflict_valid_redundant"
		+ "任务最大预开始日期: tax_max_pre_start_date"
		+ "任务有效期最大天数：tax_max_valid_day"
		+ "运单最长生成天数：waybill_max_create_day"
		+ "时段冲突允许通过天数比例: time_conflict_day_percent"
		+ "时段冲突允许通过天数：time_conflict_day")
	@RequestMapping(value = "config/integer",method = RequestMethod.GET)
	public Integer configList(@RequestParam String configKey){
		List<ConfParamOption> confParamOptions = authCommonService.listOption(configKey);
		if(!confParamOptions.isEmpty()){
			String value = confParamOptions.get(0).getOptionValue();
			return Integer.parseInt(value);
		}
		return null;
	}

	@ApiOperation(value = "承运商结算账期列表、结束任务原因", notes = "修改账期、结束任务"
		+ "承运商结算账期：vendor_account_period"
		+ "结束任务原因：end_task_reason")
	@RequestMapping(value = "config/list",method = RequestMethod.GET)
	public List<ConfParamOption> getConfig(@RequestParam String configKey){
		return authCommonService.listOption(configKey);
	}
}
