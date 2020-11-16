package com.actionsoft.apps.cmcc.integration.web;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.actionsoft.apps.cmcc.integration.constant.CodeMessageConstant;
import com.actionsoft.apps.cmcc.integration.constant.WorkFlowAPIConstant;
import com.actionsoft.apps.cmcc.integration.util.PubUtil;
import com.actionsoft.apps.cmcc.integration.util.SnapshotForm;
import com.actionsoft.apps.cmcc.integration.util.UserUtil;
import com.actionsoft.apps.cmcc.integration.util.WorkFlowUtil;
import com.actionsoft.bpms.bpmn.constant.TaskRuntimeConst;
import com.actionsoft.bpms.bpmn.engine.core.delegate.SimulationPath;
import com.actionsoft.bpms.bpmn.engine.model.def.ActivityModel;
import com.actionsoft.bpms.bpmn.engine.model.def.ProcessDefinition;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.def.ext.CommentModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.login.constant.LoginConst;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.server.SSOUtil;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ORGAPI;
import com.actionsoft.sdk.local.api.RepositoryAPI;
import com.actionsoft.sdk.local.api.TaskAPI;
import com.actionsoft.sdk.service.response.StringResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 工作流程Web
 * 
 * @author Administrator
 *
 */
public class WorkFlowWeb {
	public static ORGAPI orgAPI = SDK.getORGAPI();
	public static TaskAPI taskAPI = SDK.getTaskAPI();
	public static RepositoryAPI repositoryAPI = SDK.getRepositoryAPI();

	/**
	 * 发起流程
	 * 
	 * @param opeUserId
	 * @param flowType
	 * @param processTitle
	 * @param taskTitle
	 * @param nextUserId
	 * @return
	 */
	public StringResponse startProcess(String opeUserId, String flowType, String processTitle, String taskTitle,
			String nextUserId) {
		StringResponse result = new StringResponse();
		if (!UtilString.isEmpty(opeUserId) && opeUserId.indexOf("@") == -1) {
			opeUserId = opeUserId + WorkFlowAPIConstant.SUFFIX;
		}
		// 校验用户是否合法
		if (!new UserUtil().validateUser(opeUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数opeUserId");
			result.setData("");
			return result;
		}
		// 校验流程类型是否合法
		if (UtilString.isEmpty(flowType)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数flowType");
			result.setData("");
			return result;
		}
		Connection conn = null;
		try {
			conn = DBSql.open();
			String sql = "SELECT PROCESSDEFNID FROM BO_ACT_PROCESS_DATA WHERE PROCESSTYPE = ? AND ISMAIN = ? ORDER BY CREATEDATE DESC";
			List<RowMap> list = DBSql.getMaps(conn, sql, new Object[] { flowType, WorkFlowAPIConstant.YES });
			if (UtilString.isEmpty(list)) {
				// 参数配置错误
				result.setErrorCode(CodeMessageConstant.CODE_5);
				result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查flowType参数");
			} else {
				// 最大版本号
				int versionNo_z = 0;
				// 最大版本号流程定义ID
				String processDefid_z = list.get(0).getString("PROCESSDEFNID");
				// 遍历流程定义ID
				for (RowMap map : list) {
					String processDefid = (String) map.get("PROCESSDEFNID");
					int versionNo = repositoryAPI.getProcessDefinition(processDefid).getVersionNo();
					// 判断当前遍历版本号是否大于上一个版本号，如果大于的话，则取最大版本号流程定义ID
					if (versionNo_z < versionNo) {
						processDefid_z = processDefid;
						versionNo_z = versionNo;
					}
				}
				// 创建流程实例，并启动流程
				ProcessInstance process = SDK.getProcessAPI().createProcessInstance(processDefid_z, opeUserId,
						processTitle);
				SDK.getProcessAPI().start(process);
				String processId = process.getId();
				String taskId = DBSql.getString(conn, "SELECT ID FROM WFC_TASK WHERE PROCESSINSTID = ? ",
						new Object[] { processId });
				TaskInstance taskInstance = taskAPI.getTaskInstance(taskId);
				WorkFlowUtil workFlowUtil = new WorkFlowUtil();
				workFlowUtil.setTaskInfo(conn, taskId, taskTitle, nextUserId);

				// 返回数据
				JSONObject taskJson = new JSONObject();
				taskJson.put("processId", processId);
				taskJson.put("taskId", taskId);
				taskJson.put("taskStepId", taskInstance.getActivityDefId());
				taskJson.put("taskStepName",
						workFlowUtil.getStepName(taskInstance.getProcessDefId(), taskInstance.getActivityDefId()));
				taskJson.put("taskUserId", taskInstance.getTarget());
				taskJson.put("taskUserName", orgAPI.getUser(taskInstance.getTarget()).getUserName());
				taskJson.put("flowType", flowType);
				result.setData(taskJson.toString());
			}
		} finally {
			DBSql.close(conn);
		}
		System.out.println(">>>result:" + result);
		return result;
	}

	/**
	 * 获取流程任务信息
	 * 
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	public StringResponse getTaskInfo(String opeUserId, String taskId) {
		StringResponse result = new StringResponse();
		if (!UtilString.isEmpty(opeUserId) && opeUserId.indexOf("@") == -1) {
			opeUserId = opeUserId + WorkFlowAPIConstant.SUFFIX;
		}
		// 校验用户是否合法
		if (!new UserUtil().validateUser(opeUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数opeUserId");
			result.setData("");
			return result;
		}
		WorkFlowUtil workFlowUtil = new WorkFlowUtil();
		// 检验任务Id是否合法
		if (!workFlowUtil.checkTaskId(taskId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数taskId");
			result.setData("");
			return result;
		}
		JSONObject taskInfo = new JSONObject();
		TaskInstance taskInstance = taskAPI.getTaskInstance(taskId);
		String parentTaskInstId = taskInstance.getParentTaskInstId();
		// 判断是否第一节点
		if (WorkFlowAPIConstant.FRIST_PARENT_TASK_ID.equals(parentTaskInstId)) {
			taskInfo.put("preTaskId", WorkFlowAPIConstant.FRIST_PARENT_TASK_ID);
			taskInfo.put("preTaskStepId", WorkFlowAPIConstant.FRIST_PARENT_TASK_ID);
			taskInfo.put("preTaskStepName", "");
			taskInfo.put("preTaskUserId", "");
			taskInfo.put("preTaskUserName", "");
			taskInfo.put("preTaskCompleteTime", "");
		} else {
			// 获取上一节点相关信息
			TaskInstance parentTaskInstance = taskAPI.getTaskInstance(parentTaskInstId);
			taskInfo.put("preTaskId", parentTaskInstance.getId());
			taskInfo.put("preTaskStepId", parentTaskInstance.getActivityDefId());
			taskInfo.put("preTaskStepName", workFlowUtil.getStepName(parentTaskInstance.getProcessDefId(),
					parentTaskInstance.getActivityDefId()));
			taskInfo.put("preTaskUserId", parentTaskInstance.getTarget());
			taskInfo.put("preTaskUserName", orgAPI.getUser(parentTaskInstance.getTarget()).getUserName());
			taskInfo.put("preTaskCompleteTime", parentTaskInstance.getEndTime());
		}
		taskInfo.put("taskStepId", taskInstance.getActivityDefId());
		taskInfo.put("taskStepName",
				workFlowUtil.getStepName(taskInstance.getProcessDefId(), taskInstance.getActivityDefId()));
		taskInfo.put("taskUserId", taskInstance.getTarget());
		taskInfo.put("taskUserName", orgAPI.getUser(taskInstance.getTarget()).getUserName());
		int parallelReceiveTask = workFlowUtil.isParallelReceiveTask(taskId);// 判断是否是需要抢办的任务
		taskInfo.put("taskType", parallelReceiveTask);
		taskInfo.put("flowType", workFlowUtil.getProcessType(taskInstance.getProcessDefId()));
		taskInfo.put("isHistory", taskInstance.isHistoryTask());// 判断是否为已办
		taskInfo.put("processId", taskInstance.getProcessInstId());
		taskInfo.put("tracks",
				workFlowUtil.getProcessCommentsById(taskInstance.getProcessInstId(), WorkFlowAPIConstant.SPYJ));
		result.setErrorCode(CodeMessageConstant.CODE_1);
		result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
		result.setData(taskInfo.toString());
		System.out.println(">>>result:" + result);
		return result;
	}

	/**
	 * 获取页面流程任务动作信息
	 * 
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	public StringResponse getActions(String opeUserId, String taskId) {
		StringResponse result = new StringResponse();
		if (!UtilString.isEmpty(opeUserId) && opeUserId.indexOf("@") == -1) {
			opeUserId = opeUserId + WorkFlowAPIConstant.SUFFIX;
		}
		// 校验用户是否合法
		if (!new UserUtil().validateUser(opeUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数opeUserId");
			result.setData("");
			return result;
		}
		WorkFlowUtil workFlowUtil = new WorkFlowUtil();
		// 检验任务Id是否合法
		if (!workFlowUtil.checkTaskId(taskId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数taskId");
			result.setData("");
			return result;
		}
		JSONObject actionJson = new JSONObject();
		TaskInstance taskInstance = taskAPI.getTaskInstance(taskId);
		String processInstId = taskInstance.getProcessInstId();
		String processDefId = taskInstance.getProcessDefId();
		String activityDefId = taskInstance.getActivityDefId();
		if (taskInstance.isHistoryTask()) {// 如果是已办任务，则只需要判断是否可以收回
			// 判断是否是第一环节
			boolean isFirstStep = workFlowUtil.isFirstStep(activityDefId);
			boolean undoTask = taskAPI.isUndoTask(taskId);
			if (undoTask && isFirstStep) {
				actionJson.put("containaction", WorkFlowAPIConstant.YES_KEY);
				JSONObject actionInfo = new JSONObject();
				actionInfo.put("actionName", WorkFlowAPIConstant.UNDO);
				actionInfo.put("actionId", "");
				actionInfo.put("nextActivityDefId", taskInstance.getActivityDefId());
				actionInfo.put("occupyType", false);
				actionInfo.put("supportingParam", workFlowUtil.getSupportingParam(processInstId));
				actionInfo.put("nextTaskUser", taskInstance.getTarget());
				JSONArray actionArray = new JSONArray();
				actionArray.add(actionInfo);
				actionJson.put("actionInfo", actionArray);
			} else {
				actionJson.put("containaction", WorkFlowAPIConstant.NO_KEY);
			}
			result.setErrorCode(CodeMessageConstant.CODE_1);
			result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
			result.setData(actionJson.toString());
		} else if (workFlowUtil.isParallelReceiveTask(taskId) == 2) {// 需要抢办的任务
			actionJson.put("containaction", WorkFlowAPIConstant.YES_KEY);
			JSONObject actionInfo = new JSONObject();
			actionInfo.put("actionName", WorkFlowAPIConstant.RECEIVE_TASK);
			actionInfo.put("actionId", "");
			actionInfo.put("nextActivityDefId", taskInstance.getActivityDefId());
			actionInfo.put("occupyType", false);
			actionInfo.put("supportingParam", workFlowUtil.getSupportingParam(processInstId));
			actionInfo.put("nextTaskUser", taskInstance.getTarget());
			JSONArray actionArray = new JSONArray();
			actionArray.add(actionInfo);
			actionJson.put("actionInfo", actionArray);
			result.setErrorCode(CodeMessageConstant.CODE_1);
			result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
			result.setData(actionJson.toString());
		} else {// 普通任务
				// 获取所有的审核菜单
			List<CommentModel> userTaskOpinionList = repositoryAPI.getUserTaskOpinionList(processDefId, activityDefId);
			if (!UtilString.isEmpty(userTaskOpinionList)) {
				actionJson.put("containaction", WorkFlowAPIConstant.YES_KEY);
				JSONArray actionArray = new JSONArray();
				for (CommentModel commentModel : userTaskOpinionList) {
					JSONObject actionInfo = new JSONObject();
					String actionName = commentModel.getActionName();
					String actionId = commentModel.getId();
					String nextActivityDefId = "";
					String supportingParam = "";
					boolean occupyType = false;// 抢办标记
					boolean isTransfer = false;// 转办标记
					// 暂存审核菜单，模拟可能的路径
					taskAPI.setComment(taskId, actionName, "");
					List<SimulationPath> simulationNextPath = taskAPI.simulationNextPath(opeUserId, processInstId,
							taskId);
					if (workFlowUtil.isTransfer(actionId)) {// 如果是转办的审核菜单
						nextActivityDefId = activityDefId;// 下一环节就是当前环节
						supportingParam = workFlowUtil.getSupportingParam(processInstId);
						occupyType = false;// 转办无抢办
						isTransfer = true;
					} else if (!UtilString.isEmpty(simulationNextPath)) {
						nextActivityDefId = simulationNextPath.get(0).getId();
						String nextPathType = simulationNextPath.get(0).getType();
						// 判断下一节点的任务类型
						if (TaskRuntimeConst.ACTIVITY_TYPE_USERTASK.equals(nextPathType)) {// 如果是常规任务
							UserTaskModel nextActivityNode = repositoryAPI.getUserTaskModel(processDefId,
									nextActivityDefId);
							occupyType = nextActivityNode.isClaimTask();// 是否允许抢办
							// 获取设定的参数
							supportingParam = workFlowUtil.getSupportingParam(processInstId);
						} else if (TaskRuntimeConst.ACTIVITY_TYPE_ENDEVENT.equals(nextPathType)) {// 如果是流程结束
							occupyType = false;// 是否允许抢办
						}
					}
					// 组装action信息
					actionInfo.put("actionName", actionName);
					actionInfo.put("actionId", actionId);
					actionInfo.put("nextActivityDefId", nextActivityDefId);
					actionInfo.put("occupyType", occupyType);
					actionInfo.put("supportingParam", supportingParam);
					actionInfo.put("controlType",workFlowUtil.getTaskControl(processInstId, nextActivityDefId));
					actionInfo.put("nextTaskUser", workFlowUtil.getTaskTargetsByActivityId(taskId, processDefId,
							nextActivityDefId, isTransfer));
					actionInfo.put("nextTaskUserHistory",
							workFlowUtil.getHistoryTaskUsersByActivityId(processInstId, nextActivityDefId));
					actionArray.add(actionInfo);
				}
				actionJson.put("actionInfo", actionArray);
				result.setErrorCode(CodeMessageConstant.CODE_1);
				result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
				result.setData(actionJson.toString());
			} else {
				actionJson.put("containaction", WorkFlowAPIConstant.NO_KEY);
				actionJson.put("actionInfo", null);
				result.setErrorCode(CodeMessageConstant.CODE_1);
				result.setMsg(CodeMessageConstant.CODE_1_MESSAGE + ";无相关操作按钮");
				result.setData(actionJson.toString());
			}
		}
		System.out.println(">>>result:" + result.getData().toString());
		return result;
	}

	/**
	 * 提交下一步
	 * 
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
	public StringResponse next(String opeUserId, String taskId, String taskTitle, String taskComment, String actionName,
			String actionId, String nextStepId, String nextTaskUserId) {
		StringResponse result = new StringResponse();
		if (!UtilString.isEmpty(opeUserId) && opeUserId.indexOf("@") == -1) {
			opeUserId = opeUserId + WorkFlowAPIConstant.SUFFIX;
		}
		// 校验操作用户是否合法
		if (!new UserUtil().validateUser(opeUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数opeUserId");
			result.setData("");
			return result;
		}
		
		WorkFlowUtil workFlowUtil = new WorkFlowUtil();
		// 检验任务Id是否合法
		if (!workFlowUtil.checkTaskId(taskId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数taskId");
			result.setData("");
			return result;
		}
		// 校验审核动作是否合法
		if (!workFlowUtil.checkActionName(actionName, taskId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数actionName");
			result.setData("");
			return result;
		}
		TaskInstance taskInstance = taskAPI.getInstanceById(taskId);
		if (UtilString.isEmpty(taskTitle)) {
			taskTitle = SDK.getProcessQueryAPI().detailById(taskInstance.getProcessInstId()).getTitle();
		}
		taskAPI.setComment(taskId, actionName, taskComment);
		if (workFlowUtil.isTransfer(actionId)) {// 如果是转办类型按钮
			taskAPI.delegateTask(taskId, taskInstance.getTarget(), nextTaskUserId, taskComment);
			JSONObject json = new JSONObject();
			json.put("processStatus", CodeMessageConstant.PROCESS_STATUS_1);
			result.setData(json.toString());
			result.setErrorCode(CodeMessageConstant.CODE_1);
			result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
			return result;
		}
		List<SimulationPath> simulationNextPath = taskAPI.simulationNextPath(opeUserId, taskInstance.getProcessInstId(),
				taskId);
		if (!UtilString.isEmpty(simulationNextPath)) {
			if (TaskRuntimeConst.ACTIVITY_TYPE_ENDEVENT.equals(simulationNextPath.get(0).getType())) {// 如果是流程结束
				JSONObject json = new JSONObject();
				taskAPI.completeTask(taskId, opeUserId, true, false);// 完成该任务,并且正常close
				json.put("processStatus", CodeMessageConstant.PROCESS_STATUS_2);
				result.setData(json.toString());
				result.setErrorCode(CodeMessageConstant.CODE_1);
				result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
			} else if (TaskRuntimeConst.ACTIVITY_TYPE_CALLACTIVITY.equals(simulationNextPath.get(0).getType())) {// 如果是发起子流程节点
				HashMap<String, String> extParameters = new HashMap<String, String>();
				extParameters.put("EXT6", nextTaskUserId);
				taskAPI.setExtParameter(taskId, extParameters);
				taskAPI.completeTask(taskId, opeUserId, true, false);// 完成该任务
			} else {
				taskAPI.completeTask(taskId, opeUserId, false, false);// 完成该任务
				List<TaskInstance> newTaskInstance = taskAPI.createUserTaskInstance(taskInstance.getProcessInstId(),
						taskId, opeUserId, nextStepId, nextTaskUserId, taskTitle);
				if (!UtilString.isEmpty(newTaskInstance)) {
					JSONObject json = new JSONObject();
					json.put("processStatus", CodeMessageConstant.PROCESS_STATUS_1);
					result.setData(json.toString());
					result.setErrorCode(CodeMessageConstant.CODE_1);
					result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
				}
			}
		}
		return result;
	}

	/**
	 * 获取人员待办已办
	 * 
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
	public StringResponse getTaskList(String opeUserId, String flowType, int taskStates, String IOSID,
			String taskUserId, String taskUserDepartmentId, String taskUserCompanyId, String taskTitle,
			String processId, String processCreateUserId, String processCreateUserDepartmentId,
			String processCreateUserCompanyId, String taskBeginDate, String taskEndDate, int processStatus, int pageNO,
			int pageSize) {
		StringResponse result = new StringResponse();
		WorkFlowUtil workFlowUtil = new WorkFlowUtil();
		if (!UtilString.isEmpty(opeUserId) && opeUserId.indexOf("@") == -1) {
			opeUserId = opeUserId + WorkFlowAPIConstant.SUFFIX;
		}
		// 校验用户是否合法
		if (!new UserUtil().validateUser(opeUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数opeUserId");
			result.setData("");
			return result;
		}
		Connection conn = null;
		JSONObject taskResult = new JSONObject();
		try {
			int startSize = WorkFlowAPIConstant.START_SIZE;// 开始条数
			int endSize = WorkFlowAPIConstant.END_SIZE;// 结束条数
			if (!UtilString.isEmpty(pageNO) && !UtilString.isEmpty(pageSize)) {
				startSize = (pageNO - 1) * pageSize;
				endSize = pageSize * pageNO - 1;
			}
			if (taskStates == 1) {// 查询待办任务
				taskResult = workFlowUtil.getTaskResult(conn, taskUserId, taskBeginDate, taskEndDate, flowType,
						taskTitle, processId, IOSID, startSize, endSize, taskStates);
			} else if (taskStates == 2) {// 已办任务查询
				taskResult = workFlowUtil.getHisTaskResult(conn, taskUserId, taskBeginDate, taskEndDate, flowType,
						taskTitle, processId, IOSID, startSize, endSize, taskStates);
			} else if (taskStates == 9) {// 通知类待阅任务查询
				taskResult = workFlowUtil.getTaskResult(conn, taskUserId, taskBeginDate, taskEndDate, flowType,
						taskTitle, processId, IOSID, startSize, endSize, taskStates);
			} else if (taskStates == 10) {// 通知类已阅任务查询
				taskResult = workFlowUtil.getHisTaskResult(conn, taskUserId, taskBeginDate, taskEndDate, flowType,
						taskTitle, processId, IOSID, startSize, endSize, taskStates);
			}
		} finally {
			DBSql.close(conn);
		}
		result.setErrorCode(CodeMessageConstant.CODE_1);
		result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
		result.setData(taskResult.toString());
		return result;
	}

	/**
	 * 注销流程
	 * 
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	public StringResponse deleteProcessInstance(String opeUserId, String taskId) {
		StringResponse result = new StringResponse();
		WorkFlowUtil workFlowUtil = new WorkFlowUtil();
		// 校验用户是否合法
		if (!new UserUtil().validateUser(opeUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数opeUserId");
			result.setData("");
			return result;
		}
		// 检验任务Id是否合法
		if (!workFlowUtil.checkTaskId(taskId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数taskId");
			result.setData("");
			return result;
		}
		String processInstId = taskAPI.getTaskInstance(taskId).getProcessInstId();
		SDK.getProcessAPI().deleteById(processInstId, opeUserId);
		result.setErrorCode(CodeMessageConstant.CODE_1);
		result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
		return result;
	}

	/**
	 * 收回任务
	 * 
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	public StringResponse withdrawTask(String opeUserId, String taskId) {
		StringResponse result = new StringResponse();
		WorkFlowUtil workFlowUtil = new WorkFlowUtil();
		// 校验用户是否合法
		if (!new UserUtil().validateUser(opeUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数opeUserId");
			result.setData("");
			return result;
		}
		// 检验任务Id是否合法
		if (!workFlowUtil.checkTaskId(taskId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数taskId");
			result.setData("");
			return result;
		}
		boolean flag = false;
		try {
			flag = taskAPI.isUndoTask(taskId);
		} catch (Exception e) {
			flag = false;
		}
		if (flag) {
			taskAPI.undoTask(taskId, opeUserId, WorkFlowAPIConstant.UNDO);
			result.setErrorCode(CodeMessageConstant.CODE_1);
			result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
		} else {
			result.setErrorCode(CodeMessageConstant.CODE_100);
			result.setMsg(CodeMessageConstant.CODE_100_MESSAGE + ";该任务不允许撤回");
		}
		return result;
	}

	/**
	 * 创建传阅任务
	 * @param opeUserId
	 * @param taskId
	 * @param nextUserId
	 * @return
	 */
	public StringResponse circulationTask(String opeUserId, String taskId, String nextUserId) {
		StringResponse result = new StringResponse();
		WorkFlowUtil workFlowUtil = new WorkFlowUtil();
		// 校验用户是否合法
		if (!new UserUtil().validateUser(opeUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数opeUserId");
			result.setData("");
			return result;
		}
		// 校验下一步用户是否合法
		if (!new UserUtil().validateUser(nextUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数nextUserId");
			result.setData("");
			return result;
		}
		TaskInstance taskInst = taskAPI.getInstanceById(taskId);
		List<TaskInstance> userCCTaskInstance = taskAPI.createUserCCTaskInstance(taskInst.getProcessInstId(), taskId, opeUserId, nextUserId, taskInst.getTitle());
		if(null!=userCCTaskInstance && userCCTaskInstance.size()>0){
			JSONArray taskResult =new JSONArray();
			for (TaskInstance taskInstance : userCCTaskInstance) {
				JSONObject taskInfoJson =new JSONObject();
				taskInfoJson.put("taskId",taskInstance.getId());
				taskInfoJson.put("taskOwner", taskInstance.getOwner());
				taskInfoJson.put("taskTarget", taskInstance.getTarget());
				taskInfoJson.put("taskStepId", taskInstance.getActivityDefId());
				taskInfoJson.put("taskInstanceTitle", taskInstance.getTitle());
				taskResult.add(taskInfoJson);
			}
			result.setErrorCode(CodeMessageConstant.CODE_1);
			result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
			result.setData(taskResult.toString());
		}
		return result;
	}

	/**
	 * 设置流程变量
	 * 
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	public StringResponse context(String opeUserId, String taskId, String context) {
		StringResponse result = new StringResponse();
		WorkFlowUtil workFlowUtil = new WorkFlowUtil();
		// 校验用户是否合法
		if (!new UserUtil().validateUser(opeUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数opeUserId");
			result.setData("");
			return result;
		}
		// 检验任务Id是否合法
		if (!workFlowUtil.checkTaskId(taskId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数taskId");
			result.setData("");
			return result;
		}
		// 校验全局变量是否合法
		if (UtilString.isNotEmpty(context)) {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				String processInstId = taskAPI.getTaskInstance(taskId).getProcessInstId();
				JSONObject json = JSONObject.parseObject(context);
				for (Object key : json.keySet()) {
					map.put(key.toString(), json.getString(key.toString()));
				}
				SDK.getProcessAPI().setVariables(processInstId, map);
				result.setErrorCode(CodeMessageConstant.CODE_1);
				result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
			} catch (Exception e) {
				// 参数配置错误
				result.setErrorCode(CodeMessageConstant.CODE_5);
				result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数context");
				result.setData("");
				return result;
			}
		}
		return result;
	}

	/**
	 * 获取流程跟踪
	 * 
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	public StringResponse tracks(String opeUserId, String taskId) {
		StringResponse result = new StringResponse();
		WorkFlowUtil workFlowUtil = new WorkFlowUtil();
		// 校验用户是否合法
		if (!new UserUtil().validateUser(opeUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数opeUserId");
			result.setData("");
			return result;
		}
		// 检验任务Id是否合法
		if (!workFlowUtil.checkTaskId(taskId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数taskId");
			result.setData("");
			return result;
		}
		String processInstId = taskAPI.getInstanceById(taskId).getProcessInstId();
		// 获取流程审批意见
		JSONArray tracks = workFlowUtil.getProcessCommentsById(processInstId, WorkFlowAPIConstant.LCGZ);
		// 组装待办的审批意见
		tracks.addAll(workFlowUtil.getProcessCommentsByTodo(processInstId));
		if (tracks.size() > 0) {
			result.setErrorCode(CodeMessageConstant.CODE_1);
			result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
			result.setData(tracks.toString());
		} else {
			result.setErrorCode(CodeMessageConstant.CODE_1);
			result.setMsg(CodeMessageConstant.CODE_1_MESSAGE + ";没有数据！请检查taskId");
			result.setData(tracks.toString());
		}
		return result;
	}

	/**
	 * 抢办任务（接收办理）
	 * 
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	public StringResponse occupy(String opeUserId, String taskId) {
		StringResponse result = new StringResponse();
		WorkFlowUtil workFlowUtil = new WorkFlowUtil();
		// 校验用户是否合法
		if (!new UserUtil().validateUser(opeUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数opeUserId");
			result.setData("");
			return result;
		}
		// 检验任务Id是否合法
		if (!workFlowUtil.checkTaskId(taskId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数taskId");
			result.setData("");
			return result;
		}
		taskAPI.deleteOtherTask(taskId, opeUserId);
		result.setErrorCode(CodeMessageConstant.CODE_1);
		result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
		return result;
	}

	/**
	 * 抢办任务（接收办理）
	 * 
	 * @param opeUserId
	 * @param taskId
	 * @return
	 */
	public StringResponse flowSheet(String opeUserId, String taskId) {
		StringResponse result = new StringResponse();
		WorkFlowUtil workFlowUtil = new WorkFlowUtil();
		// 校验用户是否合法
		if (!new UserUtil().validateUser(opeUserId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数opeUserId");
			result.setData("");
			return result;
		}
		// 检验任务Id是否合法
		if (!workFlowUtil.checkTaskId(taskId)) {
			// 参数配置错误
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE + ";请检查参数taskId");
			result.setData("");
			return result;
		}
		String processInstId = taskAPI.getTaskInstance(taskId).getProcessInstId();
		StringBuilder sb = new StringBuilder();
		sb.append(SDK.getPlatformAPI().getPortalUrl());
		sb.append("/r/or?");
		sb.append("cmd=").append(WorkFlowAPIConstant.FLOW_SHEET_CMD);
		String sid = new SSOUtil().registerClientSessionNoPassword(opeUserId, LoginConst.DEFAULT_LANG, "localhost",
				LoginConst.DEVICE_PC);
		sb.append("&sid=").append(sid);
		sb.append("&processInstId=").append(processInstId);
		sb.append("&formInfo=&supportCanvas=true");
		System.err.println("url:" + sb.toString());
		result.setData(sb.toString());
		result.setErrorCode(CodeMessageConstant.CODE_1);
		result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
		return result;
	}

	/**
	 * 流程实例查询
	 * @param opeUserId
	 * @param flowType
	 * @param processId
	 * @param processTitle
	 * @param processCreateUserId
	 * @param processCreateUserName
	 * @param processState
	 * @param beginDateFrom
	 * @param beginDateTo
	 * @param pageSize
	 * @param pageNO
	 * @return
	 */
	public StringResponse processList(String opeUserId,String processApp,String flowType,String processId,String processTitle,
			String processCreateUserId,String processCreateUserName,String processState,
			String beginDateFrom,String beginDateTo,String endDateFrom,String endDateTo,
			String target,String owner,String createUserDeptId,String pageSize,String pageNO,String system){
		StringResponse result = new StringResponse();
		JSONObject processList = new JSONObject();
		try {
			//过滤后的总数量
			StringBuffer sb_size = new StringBuffer();
			sb_size.append("select count(*) c from VIEW_ACT_PROCESSVIEW PROCESS ");
			sb_size.append("where 1=1 ");
			
			StringBuffer sb = new StringBuffer();
			sb.append("select PROCESS.* from ");
			//先排序
			sb.append("(select * from VIEW_ACT_PROCESSVIEW ORDER BY CREATETIME desc) PROCESS ");
			sb.append("where 1=1 ");
			//应用类型
			if(!UtilString.isEmpty(processApp)){
				sb.append("and PROCESS.PROCESSAPP = '"+processApp+"' ");
				sb_size.append("and PROCESS.PROCESSAPP = '"+processApp+"' ");
			}
			//流程类型
			if(!UtilString.isEmpty(flowType)){
				String[] processTypeList = flowType.trim().split(" ");
				if(processTypeList.length>=1){
					sb.append(" AND (");
					sb_size.append(" AND (");
					for(int i=0;i<processTypeList.length;i++){
						if(i==0){
							sb.append(" PROCESS.PROCESSTYPE = '"+processTypeList[i]+"' ");
							sb_size.append(" PROCESS.PROCESSTYPE = '"+processTypeList[i]+"' ");
						}else{
							sb.append("OR PROCESS.PROCESSTYPE = '"+processTypeList[i]+"' ");
							sb_size.append("OR PROCESS.PROCESSTYPE = '"+processTypeList[i]+"' ");
						}
					}
					sb.append(" )");
					sb_size.append(" )");
				}else{
					sb.append("and PROCESS.PROCESSTYPE = '"+flowType+"' ");
					sb_size.append("and PROCESS.PROCESSTYPE = '"+flowType+"' ");
				}
			}
			
			//流程Id
			if(!UtilString.isEmpty(processId)){
				String[] processIdList = processId.trim().split(",");
				if(processIdList.length>=1){
					sb.append(" AND (");
					sb_size.append(" AND (");
					for(int i=0;i<processIdList.length;i++){
						if(i==0){
							sb.append(" PROCESS.PROCESSINSTID = '"+processIdList[i]+"' ");
							sb_size.append(" PROCESS.PROCESSINSTID = '"+processIdList[i]+"' ");
						}else{
							sb.append("OR PROCESS.PROCESSINSTID = '"+processIdList[i]+"' ");
							sb_size.append("OR PROCESS.PROCESSINSTID = '"+processIdList[i]+"' ");
						}
					}
					sb.append(" )");
					sb_size.append(" )");
				}else{
					sb.append("and PROCESS.PROCESSINSTID = '"+processId+"' ");
					sb_size.append("and PROCESS.PROCESSINSTID = '"+processId+"' ");
				}
			}
			
			//流程状态
			if(!UtilString.isEmpty(processState)){
				sb.append("and PROCESS.STATE = '"+processState+"' ");
				sb_size.append("and PROCESS.STATE = '"+processState+"' ");
			}
			//流程创建人账号
			if(!UtilString.isEmpty(processCreateUserId)){
				if(processCreateUserId.indexOf("@") == -1){
					processCreateUserId += "@hq.cmcc";
				}
				sb.append("and PROCESS.CREATEUSER = '"+processCreateUserId+"' ");
				sb_size.append("and PROCESS.CREATEUSER = '"+processCreateUserId+"' ");
			}
			//流程代办人
			if(!UtilString.isEmpty(target)){
				if(target.indexOf("@") == -1){
					target += "@hq.cmcc";
				}
				sb.append("and PROCESS.TARGET like '%"+target+"%' ");
				sb_size.append("and PROCESS.TARGET like '%"+target+"%' ");
			}
			//当前办理人部所ID
			if(!UtilString.isEmpty(createUserDeptId)){
				String sql = "select id from ORGDEPARTMENT where outerid = '"+createUserDeptId+"'";
				//获取对应部门ID
				String deptid = DBSql.getString(sql);
				sb.append("and PROCESS.TARGETDEPTID = '"+deptid+"' ");
				sb_size.append("and PROCESS.TARGETDEPTID = '"+deptid+"' ");
			}
			
			//发起日期从
			if(!UtilString.isEmpty(beginDateFrom)){
				sb.append("and PROCESS.CREATETIME >= '"+beginDateFrom+"' ");
				sb_size.append("and PROCESS.CREATETIME >= '"+beginDateFrom+"' ");
			}
			//发起日期到
			if(!UtilString.isEmpty(beginDateTo)){
				sb.append("and PROCESS.CREATETIME <= '"+beginDateTo+"' ");
				sb_size.append("and PROCESS.CREATETIME <= '"+beginDateTo+"' ");
			}
			//结束日期从
			if(!UtilString.isEmpty(endDateFrom)){
				sb.append("and PROCESS.ENDTIME >= '"+endDateFrom+"' ");
				sb_size.append("and PROCESS.ENDTIME >= '"+endDateFrom+"' ");
			}
			//结束日期到
			if(!UtilString.isEmpty(endDateTo)){
				sb.append("and PROCESS.ENDTIME <= '"+endDateTo+"' ");
				sb_size.append("and PROCESS.ENDTIME <= '"+endDateTo+"' ");
			}
			/*
			 * 通过system过滤条件模糊查询流程类型
			 */
			if(!UtilString.isEmpty(system)){
				sb.append("and PROCESS.PROCESSTYPE like '%"+system+"%' ");
				sb_size.append("and PROCESS.PROCESSTYPE like '%"+system+"%' ");
			}
			/*
			 * 过滤经办人
			 */
			if(!UtilString.isEmpty(owner)){
				sb.append("and PROCESS.PENSON like '%"+owner+"%' ");
				sb_size.append("and PROCESS.PENSON like '%"+owner+"%' ");
			}
			
			/*
			 * 过滤流程标题
			 */
			if(!UtilString.isEmpty(processTitle)){
				sb.append("and PROCESS.PROCESSTITLE like '%"+processTitle+"%' ");
				sb_size.append("and PROCESS.PROCESSTITLE like '%"+processTitle+"%' ");
			}
			
			/*
			 *增加排序查询 
			 */
			sb.append(" ORDER BY CREATETIME DESC ");
			
			/*
			 * 分页查询
			 */
			int start_size = -1;//开始条数
			int curPage_bak  = -1;//当前页码
			int pageSize_bak = -1;//数量
			if(!UtilString.isEmpty(pageNO) && !UtilString.isEmpty(pageSize)){
				curPage_bak = Integer.parseInt(pageNO);
				pageSize_bak = Integer.parseInt(pageSize);
			}
			if(curPage_bak > 0 && pageSize_bak > 0){
				start_size = (curPage_bak - 1) * pageSize_bak;
			}
			if(start_size >= 0){
				sb.append("LIMIT "+start_size+","+pageSize_bak+" ");
			}
			//返回的流程数据
			JSONArray processData = new JSONArray();
			//查询出过滤后的所有流程数据
			List<RowMap> list = DBSql.getMaps(sb.toString());
			
			//过滤后的总数量
			String count = DBSql.getString(sb_size.toString());
			//格式化日期
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(list != null && list.size() > 0){
				//遍历流程数据
				for(int i = 0;i < list.size();i++){
					JSONObject processInfo = new JSONObject();
					RowMap map = list.get(i);
					String this_processTitle = map.get("PROCESSTITLE").toString();
					if(this_processTitle != null 
							&& !"".equals(this_processTitle) 
							&& this_processTitle.indexOf("(重新激活)") != -1){
						
						this_processTitle = this_processTitle.replaceAll("重新激活", "");
						this_processTitle = this_processTitle.replaceAll("\\(|\\)", "");
					}
					//流程实例ID
					processInfo.put("processInstId", map.get("PROCESSINSTID"));
					//流程类型
					processInfo.put("processType", map.get("PROCESSNAME"));
					//流程名称
					processInfo.put("processTitle", this_processTitle);
					//流程定义ID
					String processDefid = (String) map.get("PROCESSDEFID");
					//流程节点ID
					String activityId = (String) map.get("ACTIVITYDEFID");
					//环节名称
					RowMap taskInfo = DBSql.getMap("SELECT ACTIVITYDEFID,TASKSTATE FROM WFC_TASK WHERE PROCESSINSTID=?", new Object[]{map.get("PROCESSINSTID").toString()});
					if(null ==taskInfo || taskInfo.size() ==0 ){
						processInfo.put("activityName", PubUtil.getNoteIdName(processDefid, activityId));
					}else{
						if(taskInfo.getInt("TASKSTATE")==0){//任务为父流程调用任务
							ProcessDefinition processDefinition = SDK.getRepositoryAPI().getProcessDefinition(processDefid);
							ActivityModel activityModel = processDefinition.getTasks().get(taskInfo.getString("ACTIVITYDEFID"));
							processInfo.put("activityName", activityModel.getName());
						}else{
							processInfo.put("activityName", PubUtil.getNoteIdName(processDefid, activityId));
						}
						
					}
					String this_target = map.getString("TARGET");
					String taskId ="";
					if(UtilString.isNotEmpty(this_target) && opeUserId.equals(this_target)){//待办人等于操作人
						List<TaskInstance> taskList = SDK.getTaskQueryAPI().processInstId(map.getString("PROCESSINSTID")).list();
						if(null != taskList && taskList.size()>0){
							taskId = taskList.get(0).getId();
						}
					}
					//发起人
					String createUser = map.getString("CREATEUSER");
					String createUserName = "";
					if(UtilString.isEmpty(SDK.getORGAPI().validateUsers(createUser))){
						createUserName = SDK.getORGAPI().getUser(createUser).getUserName();
					}
					//发起人
					processInfo.put("processCreateUser", createUser);
					//发起人姓名
					processInfo.put("processCreateUserName", createUserName);
					//代办人
					processInfo.put("target", map.get("TARGET"));
					//代办人姓名
					processInfo.put("targetName", map.get("TARGETNAME"));
					//taskId
					processInfo.put("taskId", taskId);
					//创建日期
					processInfo.put("processCreateTime", map.get("CREATETIME") == null ? "" :sdf2.format(map.get("CREATETIME")));
					//结束日期
					processInfo.put("processEndTime", map.get("ENDTIME") == null ? "" : sdf2.format(map.get("ENDTIME")));
					//放入json数组中
					processData.add(processInfo);
				}
				processList.put("list", processData);
				processList.put("count", count);
				processList.put("dataCount", list.size());
				result.setData(processList.toString());
				result.setErrorCode(CodeMessageConstant.CODE_1);
				result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
			}else{
				processList.put("list", "");
				processList.put("count", 0);
				processList.put("dataCount", 0);
				result.setData(processList.toString());
				result.setErrorCode(CodeMessageConstant.CODE_1);
				result.setMsg(CodeMessageConstant.CODE_1_MESSAGE);
			}
			
		} catch (Exception e) {
			result.setData("");
			result.setErrorCode(CodeMessageConstant.CODE_5);
			result.setMsg(CodeMessageConstant.CODE_5_MESSAGE);
			e.printStackTrace(System.err);
		}
		return result;
	}
}
