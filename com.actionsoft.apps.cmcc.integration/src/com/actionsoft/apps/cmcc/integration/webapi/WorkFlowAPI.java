package com.actionsoft.apps.cmcc.integration.webapi;

import javax.jws.WebParam;

import com.actionsoft.apps.cmcc.integration.web.WorkFlowWeb;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.HandlerType;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.actionsoft.bpms.server.bind.annotation.Param;
import com.actionsoft.sdk.service.response.StringResponse;

@Controller(type=HandlerType.OPENAPI, apiName = "WorkFlow API", desc = "工作流程API")
public class WorkFlowAPI {
	
	/**
	 * 发起流程
	 * @param opeUserId
	 * @param flowType
	 * @param processTitle`
	 * @param taskTitle
	 * @param nextUserId
	 * @return
	 */
	@Mapping(value = "WorkFlow.startProcess")
	public StringResponse startProcess(
			@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "flowType" , desc = "流程类型" , required = true) String flowType,
			@Param(value = "processTitle" , desc = "流程名称" , required = true) String processTitle,
			@Param(value = "taskTitle" , desc = "任务标题" , required = false) String taskTitle,
			@Param(value = "nextUserId" , desc = "下一步办理人" , required = false) String nextUserId){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.startProcess(opeUserId, flowType, processTitle, taskTitle, nextUserId);
	}
	
	/**
	 * 获取流程任务信息
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	@Mapping(value = "WorkFlow.getTaskInfo")
	public StringResponse getTaskInfo(
			@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "taskId" , desc = "任务ID" , required = true) String taskId){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.getTaskInfo(opeUserId, taskId);
	}
	
	/**
	 * 获取页面流程任务动作信息
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	@Mapping(value = "WorkFlow.getTaskAction")
	public StringResponse getActions(
			@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "taskId" , desc = "任务ID" , required = true) String taskId){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.getActions(opeUserId, taskId);
	}
	
	/**
	 * 提交下一步
	 * @param opeUserId
	 * @param taskId
	 * @param taskTitle
	 * @param taskComment
	 * @param actionName
	 * @param actionId
	 * @param nextStepId
	 * @param nextTaskUserId
	 * @return
	 */
	@Mapping(value = "WorkFlow.next")
	public StringResponse next(@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "taskId" , desc = "任务ID" , required = true) String taskId,
			@Param(value = "taskTitle" , desc = "任务标题" , required = false) String taskTitle,
			@Param(value = "taskComment" , desc = "意见" , required = false) String taskComment,
			@Param(value = "actionName" , desc = "动作名称" , required = true) String actionName,
			@Param(value = "actionId" , desc = "动作ID" , required = true) String actionId,
			@Param(value = "nextStepId" , desc = "下一步节点" , required = true) String nextStepId,
			@Param(value = "nextTaskUserId" , desc = "下一步办理人" , required = false) String nextTaskUserId){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.next(opeUserId, taskId, taskTitle, taskComment, actionName, actionId, nextStepId, nextTaskUserId);
	}
	
	
	/**
	 * 获取人员待办已办
	 * @param opeUserId
	 * @param flowType
	 * @param taskStates
	 * @param taskUserId
	 * @param taskUserDepartmentId
	 * @param taskUserCompanyId
	 * @param taskTitle
	 * @param processCreateUserId
	 * @param processCreateUserDepartmentId
	 * @param processCreateUserCompanyId
	 * @param taskBeginDate
	 * @param taskEndDate
	 * @param processStatus
	 * @param pageNO
	 * @param pageSize
	 * @return
	 */
	@Mapping(value = "WorkFlow.getTaskList")
	public StringResponse getTaskList(@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "flowType" , desc = "流程类型" , required = false) String flowType,
			@Param(value = "taskStates" , desc = "任务类型" , required = true) int taskStates,
			@Param(value = "IOSID" , desc = "系统ID" , required = false) String IOSID,
			@Param(value = "taskUserId" , desc = "任务所属办理人" , required = false) String taskUserId,
			@Param(value = "taskUserDepartmentId" , desc = "任务所属办理人所属部门" , required = false) String taskUserDepartmentId,
			@Param(value = "taskUserCompanyId" , desc = "任务所属办理人所属公司" , required = false) String taskUserCompanyId,
			@Param(value = "taskTitle" , desc = "任务标题" , required = false) String taskTitle,
			@Param(value = "processId" , desc = "流程ID" , required = false) String processId,
			@Param(value = "processCreateUserId" , desc = "流程创建人" , required = false) String processCreateUserId,
			@Param(value = "processCreateUserDepartmentId" , desc = "流程创建人所属部门" , required = false) String processCreateUserDepartmentId,
			@Param(value = "processCreateUserCompanyId" , desc = "流程创建人所属公司" , required = false) String processCreateUserCompanyId,
			@Param(value = "taskBeginDate" , desc = "任务创建日期" , required = false) String taskBeginDate,
			@Param(value = "taskEndDate" , desc = "任务结束日期" , required = false) String taskEndDate,
			@Param(value = "processStatus" , desc = "流程状态" , required = false) int processStatus,
			@Param(value = "pageNO" , desc = "页数" , required = false) int pageNO,
			@Param(value = "pageSize" , desc = "分页大小" , required = false) int pageSize){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.getTaskList(opeUserId, flowType, taskStates, IOSID, taskUserId, taskUserDepartmentId, taskUserCompanyId, taskTitle , processId, processCreateUserId, processCreateUserDepartmentId, processCreateUserCompanyId, taskBeginDate, taskEndDate, processStatus, pageNO, pageSize);
	}
	
	/**
	 * 注销流程
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	@Mapping(value = "WorkFlow.deleteProcessInstance")
	public StringResponse deleteProcessInstance(
			@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "taskId" , desc = "任务ID" , required = true) String taskId){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.deleteProcessInstance(opeUserId,taskId);
	}
	
	/**
	 * 收回任务
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	@Mapping(value = "WorkFlow.withdrawTask")
	public StringResponse withdrawTask(
			@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "taskId" , desc = "任务ID" , required = true) String taskId){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.withdrawTask(opeUserId, taskId);
	}

	/**
	 * 创建传阅任务
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	@Mapping(value = "WorkFlow.circulationTask")
	public StringResponse circulationTask(
			@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "taskId" , desc = "任务ID" , required = true) String taskId,
			@Param(value = "nextUserId" , desc = "下一步办理人" , required = true) String nextUserId){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.circulationTask(opeUserId, taskId ,nextUserId);
	}
	
	/**
	 * 抢办任务（接收办理）
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	@Mapping(value = "WorkFlow.occupy")
	public StringResponse occupy(
			@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "taskId" , desc = "任务ID" , required = true) String taskId){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.occupy(opeUserId, taskId);
	}
	
	/**
	 * 设置流程变量
	 * @param opeUserId
	 * @param taskId
	 * @param context
	 * @return
	 */
	@Mapping(value = "WorkFlow.context")
	public StringResponse context(
			@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "taskId" , desc = "任务ID" , required = true) String taskId,
			@Param(value = "context" , desc = "全局变量" , required = true) String context){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.context(opeUserId, taskId,context);
	}
	
	/**
	 * 获取流程跟踪
	 * @return
	 */
	@Mapping(value = "WorkFlow.tracks")
	public StringResponse tracks(
			@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "taskId" , desc = "任务ID" , required = true) String taskId){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.tracks(opeUserId, taskId);
	}
	
	/**
	 * 获取流程跟踪图
	 * @return
	 */
	@Mapping(value = "WorkFlow.flowSheet")
	public StringResponse flowSheet(
			@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "taskId" , desc = "任务ID" , required = true) String taskId){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.flowSheet(opeUserId, taskId);
	}
	
	/**
	 * 流程查询
	 * @return
	 */
	@Mapping(value = "WorkFlow.getProcessList")
	public StringResponse processList(
			@Param(value = "opeUserId" , desc = "操作人" , required = true) String opeUserId,
			@Param(value = "processApp", desc = "应用类型", required = false) String processApp,
			@Param(value = "flowType" , desc = "流程类型" , required = false) String flowType,
			@Param(value = "processId" , desc = "流程Id" , required = false) String processId,
			@Param(value = "processTitle" , desc = "系统标题" , required = false) String processTitle,
			@Param(value = "processCreateUserId" , desc = "流程创建人" , required = false) String processCreateUserId,
			@Param(value = "processCreateUserName" , desc = "流程创建人名称" , required = false) String processCreateUserName,
			@Param(value = "processState", desc = "流程状态", required = false) String processState,
			@Param(value = "beginDateFrom", desc = "发起日期从", required = false) String beginDateFrom,
			@Param(value = "beginDateTo", desc = "发起日期到", required = false) String beginDateTo,
			@Param(value = "endDateFrom", desc = "结束日期从", required = false) String endDateFrom,
			@Param(value = "endDateTo", desc = "结束日期到", required = false) String endDateTo,
			@Param(value = "target", desc = "流程代办人", required = false) String target,
			@Param(value = "owner", desc = "流程经办人", required = false) String owner,
			@Param(value = "createUserDeptId", desc = "流程发起人部所", required = false) String createUserDeptId,
			@Param(value = "pageNO" , desc = "页数" , required = true) String pageNO,
			@Param(value = "pageSize", desc = "分页大小", required = false) String pageSize,
			@Param(value = "system", desc = "流程类型模糊查询", required = false) String system){
		WorkFlowWeb web = new WorkFlowWeb();
		return web.processList(opeUserId, processApp, flowType, processId, processTitle, processCreateUserId, processCreateUserName, processState, beginDateFrom, beginDateTo, endDateFrom, endDateTo, target, owner, createUserDeptId, pageSize, pageNO, system);
	}
}
