package com.juma.tgm.manage.project.controller;

import javax.annotation.Resource;

import com.juma.tgm.tool.domain.ExecuteWorkflowInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.giants.common.exception.BusinessException;
import com.juma.auth.employee.domain.LoginEmployee;
import com.juma.tgm.fms.service.v3.ReconcilicationForPayableService;
import com.juma.tgm.manage.fms.controller.vo.WorkflowTask;
import com.juma.tgm.manage.web.controller.BaseController;
import com.juma.tgm.project.domain.v2.ProjectWorkflow;
import com.juma.tgm.project.domain.v2.ProjectWorkflowTask;
import com.juma.tgm.project.service.ProjectProcessService;
import com.juma.tgm.project.vo.v2.ProjectWorkflowVo;
import com.juma.workflow.core.domain.TaskDetail;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("projectWorkflow")
public class ProjectWorkflowController extends BaseController{

	private static final Logger log = LoggerFactory.getLogger(ProjectWorkflowController.class);
	private final static String REAPPLY_OK = "REAPPLY_OK";
	private final static String REAPPLY_DIS = "REAPPLY_DIS";

	@Resource
	private ProjectProcessService projectProcessService;

	@Resource
	private ReconcilicationForPayableService reconcilicationForPayableService;

	@ApiOperation(value = "申请启动项目", notes = "申请启动项目")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectId", value = "项目id", required = true, dataType = "Integer"),
		@ApiImplicitParam(name = "excuteTime", value = "项目开始时间", required = true, dataType = "Date"),
	})
	@ResponseBody
	@RequestMapping(value = "/applyStart", method = RequestMethod.POST)
	public void applyStart(@RequestBody ProjectWorkflow projectWorkflow, LoginEmployee loginEmployee) {
		projectProcessService.submitApplyStartProject(projectWorkflow, loginEmployee);
	}

	@ApiOperation(value = "申请暂停项目", notes = "申请暂停项目")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectId", value = "项目id", required = true, dataType = "Integer"),
		@ApiImplicitParam(name = "excuteTime", value = "项目暂停时间", required = true, dataType = "Date"),
		@ApiImplicitParam(name = "reason", value = "申请理由", required = true, dataType = "String"),
		@ApiImplicitParam(name = "attachment", value = "附件地址", dataType = "String"),
	})
	@ResponseBody
	@RequestMapping(value = "/applyPause", method = RequestMethod.POST)
	public void applyPause(@RequestBody ProjectWorkflow projectWorkflow, LoginEmployee loginEmployee) {
		projectProcessService.submitApplyPauseProject(projectWorkflow, loginEmployee);
	}

	@ApiOperation(value = "申请结束项目", notes = "申请结束项目")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectId", value = "项目id", required = true, dataType = "Integer"),
		@ApiImplicitParam(name = "excuteTime", value = "项目结束时间", required = true, dataType = "Date"),
		@ApiImplicitParam(name = "reason", value = "申请理由", required = true, dataType = "String"),
		@ApiImplicitParam(name = "attachment", value = "附件地址", dataType = "String"),
	})
	@ResponseBody
	@RequestMapping(value = "/applyEnd", method = RequestMethod.POST)
	public void applyEnd(@RequestBody ProjectWorkflow projectWorkflow, LoginEmployee loginEmployee) {
		projectProcessService.submitApplyEndProject(projectWorkflow, loginEmployee);
	}

	@ApiOperation(value = "申请恢复项目", notes = "申请恢复项目")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectId", value = "项目id", required = true, dataType = "Integer"),
		@ApiImplicitParam(name = "reason", value = "申请理由", required = true, dataType = "String"),
		@ApiImplicitParam(name = "attachment", value = "附件地址", dataType = "String"),
	})
	@ResponseBody
	@RequestMapping(value = "/applyRecover", method = RequestMethod.POST)
	public void applyRecover(@RequestBody ProjectWorkflow projectWorkflow, LoginEmployee loginEmployee) {
		projectProcessService.submitApplyRecoverProject(projectWorkflow, loginEmployee);
	}

	@ApiOperation(value = "撤销审核流程", notes = "撤销审核流程")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectWorkflowId", value = "审核流程id", required = true, dataType = "Integer"),
	})
	@ResponseBody
	@RequestMapping(value = "/cancelWorkFlowTask", method = RequestMethod.GET)
	public void cancelWorkFlowTask(@RequestParam("projectWorkflowId") Integer projectWorkflowId, LoginEmployee loginEmployee) {
		projectProcessService.cancelWorkFlowTask(projectWorkflowId, loginEmployee);
	}

	@ApiOperation(value = "审批项目审核流程", notes = "审批项目审核流程")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectWorkflowId", value = "审批流程id", required = true, dataType = "Integer"),
		@ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer"),
		@ApiImplicitParam(name = "approvalKey", value = "按钮key", required = true, dataType = "String"),
		@ApiImplicitParam(name = "comment", value = "审批意见", required = true, dataType = "String"),
	})
	@ResponseBody
	@RequestMapping(value = "/doWorkFlowtask", method = RequestMethod.POST)
	public void doWorkFlowtask(@RequestBody ProjectWorkflowTask projectWorkflowTask, LoginEmployee loginEmployee) {
//		projectProcessService.finishWorkFlowTask(projectWorkflowTask, loginEmployee);
	}

	@ApiOperation(value = "重新申请项目审批记录", notes = "重新申请项目审批记录")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectWorkflowId", value = "审批流程id", required = true, dataType = "Integer"),
		@ApiImplicitParam(name = "excuteTime", value = "执行时间", required = true, dataType = "Date"),
		@ApiImplicitParam(name = "reason", value = "申请理由", dataType = "String"),
		@ApiImplicitParam(name = "attachment", value = "附件地址", dataType = "String"),
	})
	@ResponseBody
	@RequestMapping(value = "/reapplyProjectWorkflow", method = RequestMethod.POST)
	public void updateProjectWorkflow(@RequestBody ProjectWorkflow projectWorkflow, LoginEmployee loginEmployee) {
		projectProcessService.reapplyProjectWorkflow(projectWorkflow, loginEmployee);
	}

	@ApiOperation(value = "通知工作流", notes = "通知工作流")
	@ResponseBody
	@RequestMapping(value = "sendToWorkflow", method = RequestMethod.POST)
	public void sendToWorkflow(@RequestBody ExecuteWorkflowInfo executeWorkflowInfo, LoginEmployee loginEmployee) {
	    if(REAPPLY_OK.equals(executeWorkflowInfo.getApprovalKey())){
            projectProcessService.reapplyToWorkflow(executeWorkflowInfo, loginEmployee);
        }else if(REAPPLY_DIS.equals(executeWorkflowInfo.getApprovalKey())){
            projectProcessService.giveUpToWorkflow(executeWorkflowInfo, loginEmployee);
        }
	}

	@ApiOperation(value = "查询项目审批详情", notes = "查询项目审批详情")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "projectWorkflowId", value = "审核流程id", required = true, dataType = "Integer"),
	})
	@ResponseBody
	@RequestMapping(value = "/findProjectWorkflowDetail", method = RequestMethod.GET)
	public ProjectWorkflowVo findProjectWorkflowDetail(@RequestParam("projectWorkflowId") Integer projectWorkflowId, LoginEmployee loginEmployee) {
		return projectProcessService.findProjectWorkflowDetail(projectWorkflowId, loginEmployee);
	}

	@ApiOperation(value = "通过流程id查询任务详情", notes = "通过流程id查询任务详情")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "processInstanceId", value = "审核流程id", required = true, dataType = "String"),
	})
	@ResponseBody
	@RequestMapping(value = "/findTaskByProcessInstanceId", method = RequestMethod.GET)
	public TaskDetail findTaskByProcessInstanceId(@RequestParam("processInstanceId") String processInstanceId, LoginEmployee loginEmployee) {
		return projectProcessService.findTaskByProcessInstanceId(processInstanceId, loginEmployee);
	}

	/**
	 * 流程相关API -任务
	 */
	@RequestMapping(value="/task",method=RequestMethod.GET)
	@ResponseBody
	public WorkflowTask task(@RequestParam(required=false)String taskId, LoginEmployee loginEmployee) {
		WorkflowTask task = new WorkflowTask();
		TaskDetail taskDetail = new TaskDetail();
		if(taskId != null) {
			try {
				taskDetail = reconcilicationForPayableService.getWorkflowElement(taskId, loginEmployee);
			} catch (Exception e) {
				throw new BusinessException("workflow.error", "workflow.error");
			}
		}
		task.setTaskDetail(taskDetail);
		task.setTaskId(taskId);
		return task;
	}
}
