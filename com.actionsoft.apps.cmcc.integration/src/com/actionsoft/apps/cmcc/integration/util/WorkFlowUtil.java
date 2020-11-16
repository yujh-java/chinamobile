package com.actionsoft.apps.cmcc.integration.util;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.actionsoft.apps.cmcc.integration.CMCCConst;
import com.actionsoft.apps.cmcc.integration.constant.WorkFlowAPIConstant;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.cache.util.UserTaskDefUtil;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.def.ext.CommentModel;
import com.actionsoft.bpms.bpmn.engine.model.run.TaskCommentModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.form.engine.FormEngineHelper;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.HistoryTaskQueryAPI;
import com.actionsoft.sdk.local.api.ORGAPI;
import com.actionsoft.sdk.local.api.ProcessAPI;
import com.actionsoft.sdk.local.api.RepositoryAPI;
import com.actionsoft.sdk.local.api.TaskAPI;
import com.actionsoft.sdk.local.api.TaskQueryAPI;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class WorkFlowUtil {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static ORGAPI orgAPI = SDK.getORGAPI(); 
	public static TaskAPI taskAPI = SDK.getTaskAPI();
	public static ProcessAPI processAPI = SDK.getProcessAPI();
	public static RepositoryAPI repositoryAPI = SDK.getRepositoryAPI();
	
	/**
	 * 校验taskId是否合法
	 * @return
	 */
	public boolean checkTaskId(String taskId){
		if(UtilString.isNotEmpty(taskId) && taskAPI.getInstanceById(taskId)!=null){
			return true;
		}
		return false;
	}
	
	/**
	 * 转换日期格式Timestamp-->String
	 * @param time
	 * @return
	 */
	public String timeFormatToString(Timestamp time){
		String timeString ="";
		if(!UtilString.isEmpty(time)){
			timeString = sdf.format(time);
		}
		return timeString;
	}
	
	/**
	 * 转换日期格式Timestamp-->long
	 * @param time
	 * @return
	 */
	public long timeFormatToLong(Timestamp time){
		long timeLong = 0L;
		if(!UtilString.isEmpty(time)){
			timeLong = time.getTime();
		}
		return timeLong;
	}
	
	/**
	 * 判断该任务是否是需要接收办理的（抢办的） 
	 * @param taskId
	 * @return
	 */
	public int isParallelReceiveTask(String taskId){
		TaskInstance taskInstance = taskAPI.getTaskInstance(taskId);
		ProcessInstance processInstance = processAPI.getInstanceById(taskInstance.getProcessInstId());
		UserTaskModel userTaskModel = repositoryAPI.getUserTaskModel(taskInstance.getProcessDefId(), taskInstance.getActivityDefId());
		if(FormEngineHelper.getInstance().isParallelReceiveTask(processInstance,taskInstance,userTaskModel)){
			return 2;
		}
		return 1;
	}
	
	/**
	 * 检验审批动作是否合法
	 * @param actionName
	 * @param taskId
	 * @return
	 */
	public boolean checkActionName(String actionName,String taskId){
		if(UtilString.isNotEmpty(actionName)){
			TaskInstance taskInstance = taskAPI.getInstanceById(taskId);
			String activityDefId = taskInstance.getActivityDefId();
			String processDefId = taskInstance.getProcessDefId();
			List<CommentModel> userTaskOpinionList = repositoryAPI.getUserTaskOpinionList(processDefId, activityDefId);
			for (CommentModel commentModel : userTaskOpinionList) {
				if(commentModel.getActionName().equals(actionName)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 修改任务标题,任务办理人
	 * @param conn
	 * @param taskId
	 * @param taskTitle
	 * @param target
	 */
	public void setTaskInfo(Connection conn, String taskId ,String taskTitle ,String target){
		StringBuilder sb =new StringBuilder("UPDATE WFC_TASK SET ");
		if(UtilString.isNotEmpty(taskTitle)){
			sb.append(" TASKTITLE = '");
			sb.append(taskTitle+"'");
			sb.append(" ,");
		}
		if(UtilString.isNotEmpty(target)){
			String targetDepartment = orgAPI.getDepartmentByUser(target).getId();
			sb.append(" TARGET = '");
			sb.append(target +"'");
			sb.append(" ,");
			
			sb.append(" TARGETDEPTID = '");
			sb.append(targetDepartment +"'");
			sb.append(" ,");
		}
		if(sb.indexOf(",")>0){
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append(" WHERE ID = '");
		sb.append(taskId+"'");
		if(null != conn){
			DBSql.update(conn,sb.toString());
		}else{
			DBSql.update(sb.toString());
		}
	}
	
	/**
	 * 获取节点名称
	 * @param activityDefId
	 * @param processDefId
	 * @return
	 */
	public String getStepName(String activityDefId,String processDefId){
		String stepName ="";
		UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefId, activityDefId);
		if(null !=taskModel){
			stepName=taskModel.name;
		}
		return stepName;
	}
	
	/**
	 * 获取流程类型
	 * @param processDefId
	 * @return
	 */
	public String getProcessType(String processDefId){
		String sql = "SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ? ORDER BY CREATEDATE DESC";
		String flowType = DBSql.getString(sql,new Object[]{processDefId});
		return flowType;
	}
	
	/**
	 * 判断是否是转办的审核菜单 
	 * @param actionId
	 * @return
	 */
	public boolean isTransfer(String actionId){
		String actionIds = SDK.getAppAPI().getProperty(WorkFlowAPIConstant.APPID, WorkFlowAPIConstant.transferActionIds);
		if(actionIds.indexOf(actionId)>0){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否是第一环节的审核菜单
	 * @param activityId
	 * @return
	 */
	public boolean isFirstStep(String activityId){
		String workFlowFirstStep = SDK.getAppAPI().getProperty(WorkFlowAPIConstant.APPID, WorkFlowAPIConstant.workFlowFirstStep);
		if(workFlowFirstStep.indexOf(activityId)>0){
			return true;
		}
		return false;
	}
	
	/**
	 * 根据流程实例ID获取流程变量
	 * @param processInstId
	 * @return
	 */
	public String getSupportingParam(String processInstId){
		Map<String, Object> maps = processAPI.getVariables(processInstId);
		if(null == maps || maps.size()<=0){
			return "";
		}else{
			new JSONObject(maps);
			return new JSONObject(maps).toString();
		}
		
	}
	
	/**
	 * 获取流程审批意见
	 * @param processInstId
	 * @return
	 */
	public JSONArray getProcessCommentsById(String processInstId,int type){
		JSONArray array=new JSONArray();
		List<TaskCommentModel> processComments = processAPI.getCommentsById(processInstId);
		if(null != processComments && processComments.size()>0){
			for (TaskCommentModel taskCommentModel : processComments) {
				if(WorkFlowAPIConstant.SPYJ == type){//如果是表单内的审批意见掉撤回等信息
					if(taskCommentModel.getMsg().indexOf("撤回")>0){
						continue;
					}
				}else if(WorkFlowAPIConstant.LCGZ ==type){//流程跟踪则过滤样式
					//暂时不调整
				}
				JSONObject json =new JSONObject();
				TaskInstance taskInstance = taskAPI.getTaskInstance(taskCommentModel.getTaskInstId());
				String targetDepartmentId = taskInstance.getTargetDepartmentId();
				DepartmentModel departmentModel = orgAPI.getDepartmentById(targetDepartmentId);
				String dealUserDepartmentCode = departmentModel.getOuterId();
				String dealUserDepartmentName = departmentModel.getName();
				String dealUserDepartmentPathName = departmentModel.getPathNameOfCache();
				json.put("preTaskId", taskInstance.getParentTaskInstId());
				json.put("taskId", taskCommentModel.getTaskInstId());
				json.put("stepName", taskCommentModel.getActivityName());
				json.put("arrivedTime", sdf.format(taskInstance.getBeginTime()));
				json.put("dealTime", sdf.format(taskCommentModel.getCreateDate()));
				json.put("dealUserId", taskCommentModel.getCreateUser());
				json.put("dealUserName", orgAPI.getUser(taskCommentModel.getCreateUser()).getUserName());
				json.put("dealUserDepartmentId", targetDepartmentId);
				json.put("dealUserDepartmentCode", dealUserDepartmentCode);
				json.put("dealUserDepartmentName", dealUserDepartmentName);
				json.put("dealUserDepartmentPathName", dealUserDepartmentPathName);
				json.put("dealComment", taskCommentModel.getMsg());
				json.put("commentGroup", getCommentGroup(processInstId, taskInstance.getActivityDefId()));
				array.add(json);
			}
		}
		return array;
	}
	
	/**
	 * 获取流程待办审批意见
	 * @param processInstId
	 * @return
	 */
	public JSONArray getProcessCommentsByTodo(String processInstId){
		JSONArray array=new JSONArray();
		List<TaskInstance> taskList = SDK.getTaskQueryAPI().userTaskOfWorking().addQuery("PROCESSINSTID = ", processInstId).addQuery("TASKSTATE != ", "4").orderBy("BEGINTIME").list();
		if(null!=taskList && taskList.size()>0){
			for (TaskInstance task : taskList) {
				JSONObject json =new JSONObject();
				UserTaskModel taskModel = UserTaskDefUtil.getModel(task.getProcessDefId(), task.getActivityDefId());
				String targetDepartmentId = task.getTargetDepartmentId();
				DepartmentModel departmentModel = orgAPI.getDepartmentById(targetDepartmentId);
				String dealUserDepartmentCode = departmentModel.getOuterId();
				String dealUserDepartmentName = departmentModel.getName();
				String dealUserDepartmentPathName = departmentModel.getPathNameOfCache();
				json.put("preTaskId", task.getParentTaskInstId());
				json.put("taskId", task.getId());
				json.put("stepName", taskModel.getName());
				json.put("arrivedTime", sdf.format(task.getBeginTime()));
				json.put("dealTime", WorkFlowAPIConstant.NO_END);
				json.put("dealUserId",task.getTarget());
				json.put("dealUserName", orgAPI.getUser(task.getTarget()).getUserName());
				json.put("dealUserDepartmentId", targetDepartmentId);
				json.put("dealUserDepartmentCode", dealUserDepartmentCode);
				json.put("dealUserDepartmentName", dealUserDepartmentName);
				json.put("dealUserDepartmentPathName", dealUserDepartmentPathName);
				json.put("dealComment", WorkFlowAPIConstant.EMPTY);
				json.put("commentGroup", WorkFlowAPIConstant.EMPTY);
				array.add(json);
			}
		}
		System.err.println(">>>待办："+array.toString());
		return array;
	}
	
	/**
	 * 获取流程意见分组
	 * @param processInstId
	 * @param activityId
	 * @return
	 */
	public String getCommentGroup(String processInstId,String activityId){
		String commentGroup = "";
		ProcessInstance processInstance = processAPI.getInstanceById(processInstId);
		String masterBindId = SDK.getBOAPI()
				.getByKeyField(CMCCConst.ROUTE_MASTER_TABLE, "PROCESSDEFIDINFO", processInstance.getProcessDefId()).getBindId();
		List<BO> stepList = SDK.getBOAPI().query(CMCCConst.ROUTE_CHILD_TABLE, false).bindId(masterBindId)
				.addQuery("STEPID=", activityId).orderBy("UPDATEDATE").desc().list();
		if (stepList.size() > 0) {
			commentGroup = stepList.get(0).getString("COMMENTGROUP");
		}
		return commentGroup;
	}
	
	/**
	 * 获取业务系统控制类型
	 * @param processInstId
	 * @param activityId
	 * @return
	 */
	public String getTaskControl(String processInstId,String activityId){
		String controlType = "";
		ProcessInstance processInstance = processAPI.getInstanceById(processInstId);
		String masterBindId = SDK.getBOAPI()
				.getByKeyField(CMCCConst.ROUTE_MASTER_TABLE, "PROCESSDEFIDINFO", processInstance.getProcessDefId()).getBindId();
		List<BO> stepList = SDK.getBOAPI().query(CMCCConst.ROUTE_CHILD_TABLE, false).bindId(masterBindId)
				.addQuery("STEPID=", activityId).orderBy("UPDATEDATE").desc().list();
		if (stepList.size() > 0) {
			controlType = stepList.get(0).getString("CONTROLTYPE").trim().equals("")?"fasle":stepList.get(0).getString("CONTROLTYPE");
		}
		return controlType;
	}
	
	/**
	 * 查询待办任务
	 * @param userid
	 * @param startTime
	 * @param endTime
	 * @param processType
	 * @param taskTitle
	 * @return
	 */
	public JSONObject getTaskResult(Connection conn,String userid,String startTime,String endTime,String processType,String taskTitle,String processId,String IOSID,int start_sizte,int end_size,int taskStates){
		JSONObject taskList =new JSONObject();
		List<TaskInstance> list= new ArrayList<TaskInstance>();
		TaskQueryAPI taskQueryApi = SDK.getTaskQueryAPI();
		if(taskStates==1){
			taskQueryApi = taskQueryApi.userTaskOfWorking().supportDelegateTask().connection(conn).addQuery("TASKSTATE != ", "4");
		}else if (taskStates==9){
			taskQueryApi = taskQueryApi.userTaskOfNotification().supportDelegateTask().connection(conn).addQuery("TASKSTATE != ", "4");
		}
		/** 添加条件开始  **/
		if(!UtilString.isEmpty(userid)){
			taskQueryApi.target(userid);
		}
		if(!UtilString.isEmpty(startTime)){
			Date startDate = null;
			try {
				startDate = sdf.parse(startTime);
			} catch (ParseException e) {
				e.printStackTrace(System.err);
			}
			taskQueryApi = taskQueryApi.beginBefore(startDate);
		}
		if(!UtilString.isEmpty(endTime)){
			Date endDate = null;
			try {
				endDate = sdf.parse(endTime);
			} catch (ParseException e) {
				e.printStackTrace(System.err);
			}
			taskQueryApi = taskQueryApi.beginAfter(endDate);
		}
		if(!UtilString.isEmpty(taskTitle)){
			taskQueryApi = taskQueryApi.titleLike("%"+taskTitle+"%");
		}
		
		if(!UtilString.isEmpty(processId)){
            String[] processIds = processId.split(",");
            StringBuilder sb =new StringBuilder("(");
            for(int i = 0 ;i<processIds.length;i++){
                sb.append("'");
                sb.append(processIds[i]);
                sb.append("'");
                sb.append(",");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append(")");
            taskQueryApi = taskQueryApi.addQuery("PROCESSINSTID IN "+sb.toString(), null);
        }
		
		if(!UtilString.isEmpty(IOSID)){
			taskQueryApi = taskQueryApi.IOS(IOSID);
		}
		if(!UtilString.isEmpty(processType)){
			List<RowMap> list_processDefid = DBSql.getMaps(conn,"SELECT PROCESSDEFNID FROM BO_ACT_PROCESS_DATA WHERE PROCESSTYPE=?", new Object[]{processType});
			if(list_processDefid != null && list_processDefid.size() > 0){
				for(int i = 0;i<list_processDefid.size();i++){
					String processDefid  = list_processDefid.get(i).getString("PROCESSDEFNID");
					if(!UtilString.isEmpty(processDefid)){
						taskQueryApi = taskQueryApi.processDefId(processDefid);
						
					}
				}
			}
		}
		/** 添加条件结束  **/
		//获取最终数据
		list = taskQueryApi.list();
		List<TaskInstance> newListTask = new ArrayList<TaskInstance>();
		if(list != null && list.size() > 0){
			for(int i = 0;i<list.size();i++){
				String taskId = list.get(i).getId();
				List<TaskInstance> list2 = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn).parentTaskInstId(taskId).list();
				if(list2 == null || list2.size() == 0){
					newListTask.add(list.get(i));
				}
			}
		}
		list = newListTask;
		int end_i = 0;
		JSONArray taskData = new JSONArray();
		if(list.size() > 0 && list != null){
			int start_i = 0;
			end_i = list.size();
			taskList.put("dataCount", end_i);
			if(end_size >= 0 && end_i > end_size){
				start_i = start_sizte;
				end_i = end_size + 1;
			}else if(end_i == end_size){
				start_i = start_sizte;
				end_i = end_size;
			}else if(end_i < end_size){
				start_i = start_sizte;
			}
			taskList.put("count", end_i-start_i);
			for(int i = start_i;i<end_i;i++){
				JSONObject taskInfoJson = new JSONObject();
				TaskInstance taskInstance = list.get(i);
				String taskId = taskInstance.getId();//任务实例ID
				/** 流程信息开始  **/
				String processInstId = taskInstance.getProcessInstId();//流程实例ID
				ProcessInstance processInstance = SDK.getProcessAPI().getInstanceById(processInstId);
				//流程标题
				String processTitle = processInstance.getTitle();
				//替换[重新激活]
				if(processTitle != null && !"".equals(processTitle) && processTitle.indexOf("(重新激活)") != -1){
					processTitle = processTitle.replaceAll("重新激活", "");
					processTitle = processTitle.replaceAll("\\(|\\)", "");
				}
				String processDefId = processInstance.getProcessDefId();
				String processCreateUserId = processInstance.getCreateUser();
				String processCreateUserName = orgAPI.getUser(processCreateUserId).getUserName();
				String processCreateDate = timeFormatToString(processInstance.getCreateTime());
				long processCreateDateToLong = timeFormatToLong(processInstance.getCreateTime());
				String processEndDate = timeFormatToString(processInstance.getEndTime());
				long processEndDateToLong = timeFormatToLong(processInstance.getEndTime());
				boolean processStatus = processInstance.isEnd();
				/** 流程信息结束  **/
				/** 任务信息开始  **/
				String parentTaskInstId = taskInstance.getParentTaskInstId();
				String taskStepId = taskInstance.getActivityDefId();
				String taskReadTime = timeFormatToString(taskInstance.getReadTime());
				long taskReadTimeToLong = timeFormatToLong(taskInstance.getReadTime());
				String taskStepName = getStepName(taskStepId,taskInstance.getProcessDefId());
				String taskInstanceTitle = taskInstance.getTitle();
				String taskBeginTime = timeFormatToString(taskInstance.getBeginTime());
				long taskBeginTimeToLong = timeFormatToLong(taskInstance.getBeginTime());
				String taskEndTime = timeFormatToString(taskInstance.getEndTime());
				long taskEndTimeToLong = timeFormatToLong(taskInstance.getEndTime());
				String taskOwner = taskInstance.getOwner();
				String taskOwnerName =  orgAPI.getUser(taskOwner).getUserName();
				String taskTarget = taskInstance.getTarget();
				String taskTargetName =  orgAPI.getUser(taskTarget).getUserName();
				if(UtilString.isEmpty(processType)){
					processType = getProcessType(processDefId);
				}
				/** 任务信息结束  **/
				//组装数据
				taskInfoJson.put("taskId", taskId);
				taskInfoJson.put("processId", processInstId);
				taskInfoJson.put("processTitle", processTitle);
				taskInfoJson.put("processCreateUserId", processCreateUserId);
				taskInfoJson.put("processCreateUserName", processCreateUserName);
				taskInfoJson.put("processCreateDate", processCreateDate);
				taskInfoJson.put("processCreateDateToLong", processCreateDateToLong);
				taskInfoJson.put("processEndDate", processEndDate);
				taskInfoJson.put("processEndDateToLong", processEndDateToLong);
				taskInfoJson.put("processStatus", processStatus);
				taskInfoJson.put("parentTaskInstId", parentTaskInstId);
				taskInfoJson.put("taskStepId", taskStepId);
				taskInfoJson.put("taskStepName", taskStepName);
				taskInfoJson.put("taskInstanceTitle", taskInstanceTitle);
				taskInfoJson.put("taskBeginTime", taskBeginTime);
				taskInfoJson.put("taskBeginTimeToLong", taskBeginTimeToLong);
				taskInfoJson.put("taskEndTime", taskEndTime);
				taskInfoJson.put("taskEndTimeToLong", taskEndTimeToLong);
				taskInfoJson.put("taskReadTime", taskReadTime);
				taskInfoJson.put("taskReadTimeToLong", taskReadTimeToLong);
				taskInfoJson.put("taskOwner", taskOwner);
				taskInfoJson.put("taskOwnerName", taskOwnerName);
				taskInfoJson.put("taskTarget", taskTarget);
				taskInfoJson.put("taskTargetName", taskTargetName);
				taskInfoJson.put("taskStates", taskStates);
				taskInfoJson.put("flowType", processType);
				taskData.add(taskInfoJson);
			}
			taskList.put("list", taskData);
		}else{
			taskList.put("count", 0);
			taskList.put("list", taskData);
		}
		
		return taskList;
	}
	
	/**
	 * 获取已办数据
	 * @param conn
	 * @param userid
	 * @param startTime
	 * @param endTime
	 * @param processType
	 * @param taskTitle
	 * @param IOSID
	 * @param start_sizte
	 * @param end_size
	 * @return
	 */
	public JSONObject getHisTaskResult(Connection conn,String userid,String startTime,String endTime,String processType,String taskTitle,String processId,String IOSID,int start_sizte,int end_size,int taskStates){
		JSONObject taskList =new JSONObject();
		List<HistoryTaskInstance> list= new ArrayList<HistoryTaskInstance>();
		HistoryTaskQueryAPI taskQueryApi=SDK.getHistoryTaskQueryAPI();
		if(taskStates==2){
			taskQueryApi = SDK.getHistoryTaskQueryAPI().userTaskOfWorking().connection(conn).addQuery("TASKSTATE != ", "4");
		}else if (taskStates==10){
			taskQueryApi = SDK.getHistoryTaskQueryAPI().userTaskOfNotification().connection(conn).addQuery("TASKSTATE != ", "4");
		}

		/** 添加条件开始  **/
		if(!UtilString.isEmpty(userid)){
			taskQueryApi.target(userid);
		}
		if(!UtilString.isEmpty(startTime)){
			Date startDate = null;
			try {
				startDate = sdf.parse(startTime);
			} catch (ParseException e) {
				e.printStackTrace(System.err);
			}
			taskQueryApi = taskQueryApi.beginBefore(startDate);
		}
		if(!UtilString.isEmpty(endTime)){
			Date endDate = null;
			try {
				endDate = sdf.parse(endTime);
			} catch (ParseException e) {
				e.printStackTrace(System.err);
			}
			taskQueryApi = taskQueryApi.beginAfter(endDate);
		}
		if(!UtilString.isEmpty(taskTitle)){
			taskQueryApi = taskQueryApi.titleLike("%"+taskTitle+"%");
		}
		
		if(!UtilString.isEmpty(processId)){
            String[] processIds = processId.split(",");
            StringBuilder sb =new StringBuilder("(");
            for(int i = 0 ;i<processIds.length;i++){
                sb.append("'");
                sb.append(processIds[i]);
                sb.append("'");
                sb.append(",");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append(")");
            taskQueryApi = taskQueryApi.addQuery("PROCESSINSTID IN "+sb.toString(), null);
        }
		
		if(!UtilString.isEmpty(IOSID)){
			taskQueryApi = taskQueryApi.IOS(IOSID);
		}
		if(!UtilString.isEmpty(processType)){
			List<RowMap> list_processDefid = DBSql.getMaps(conn,"SELECT PROCESSDEFNID FROM BO_ACT_PROCESS_DATA WHERE PROCESSTYPE=?", new Object[]{processType});
			if(list_processDefid != null && list_processDefid.size() > 0){
				for(int i = 0;i<list_processDefid.size();i++){
					String processDefid  = list_processDefid.get(i).getString("PROCESSDEFNID");
					if(!UtilString.isEmpty(processDefid)){
						taskQueryApi = taskQueryApi.processDefId(processDefid);
						
					}
				}
			}
		}
		/** 添加条件结束  **/
		//获取最终数据 start (去重，排序)
		list = taskQueryApi.orderByEndTime().desc().list();
		list= removeDuplicatePlan(list);
		ListSort(list);
		//获取最终数据 end
		int end_i = 0;
		JSONArray taskData = new JSONArray();
		if(list.size() > 0 && list != null){
			int start_i = 0;
			end_i = list.size();
			taskList.put("dataCount", end_i);
			if(end_size >= 0 && end_i > end_size){
				start_i = start_sizte;
				end_i = end_size + 1;
			}else if(end_i == end_size){
				start_i = start_sizte;
				end_i = end_size;
			}else if(end_i < end_size){
				start_i = start_sizte;
			}
			taskList.put("count", end_i-start_i);
			for(int i = start_i;i<end_i;i++){
				JSONObject taskInfoJson = new JSONObject();
				TaskInstance taskInstance = list.get(i);
				String taskId = taskInstance.getId();//任务实例ID
				/** 流程信息开始  **/
				String processInstId = taskInstance.getProcessInstId();//流程实例ID
				ProcessInstance processInstance = SDK.getProcessAPI().getInstanceById(processInstId);
				//流程标题
				String processTitle = processInstance.getTitle();
				//替换[重新激活]
				if(processTitle != null && !"".equals(processTitle) && processTitle.indexOf("(重新激活)") != -1){
					processTitle = processTitle.replaceAll("重新激活", "");
					processTitle = processTitle.replaceAll("\\(|\\)", "");
				}
				String processDefId = processInstance.getProcessDefId();
				String processCreateUserId = processInstance.getCreateUser();
				String processCreateUserName = orgAPI.getUser(processCreateUserId).getUserName();
				String processCreateDate = timeFormatToString(processInstance.getCreateTime());
				long processCreateDateToLong = timeFormatToLong(processInstance.getCreateTime());
				String processEndDate = timeFormatToString(processInstance.getEndTime());
				long processEndDateToLong = timeFormatToLong(processInstance.getEndTime());
				boolean processStatus = processInstance.isEnd();
				if(UtilString.isEmpty(processType)){
					processType = getProcessType(processDefId);
				}
				/** 流程信息结束  **/
				/** 任务信息开始  **/
				String parentTaskInstId = taskInstance.getParentTaskInstId();
				String taskStepId = taskInstance.getActivityDefId();
				String taskStepName = getStepName(taskStepId,taskInstance.getProcessDefId());
				String taskInstanceTitle = taskInstance.getTitle();
				String taskBeginTime = timeFormatToString(taskInstance.getBeginTime());
				long taskBeginTimeToLong = timeFormatToLong(taskInstance.getBeginTime());
				String taskEndTime = timeFormatToString(taskInstance.getEndTime());
				long taskEndTimeToLong = timeFormatToLong(taskInstance.getEndTime());
				String taskReadTime = timeFormatToString(taskInstance.getReadTime());
				long taskReadTimeToLong = timeFormatToLong(taskInstance.getReadTime());
				String taskOwner = taskInstance.getOwner();
				String taskOwnerName =  orgAPI.getUser(taskOwner).getUserName();
				String taskTarget = taskInstance.getTarget();
				String taskTargetName =  orgAPI.getUser(taskTarget).getUserName();
				/** 任务信息结束  **/
				//组装数据
				taskInfoJson.put("taskId", taskId);
				taskInfoJson.put("processId", processInstId);
				taskInfoJson.put("processTitle", processTitle);
				taskInfoJson.put("processCreateUserId", processCreateUserId);
				taskInfoJson.put("processCreateUserName", processCreateUserName);
				taskInfoJson.put("processCreateDate", processCreateDate);
				taskInfoJson.put("processCreateDateToLong", processCreateDateToLong);
				taskInfoJson.put("processEndDate", processEndDate);
				taskInfoJson.put("processEndDateToLong", processEndDateToLong);
				taskInfoJson.put("processStatus", processStatus);
				taskInfoJson.put("parentTaskInstId", parentTaskInstId);
				taskInfoJson.put("taskStepId", taskStepId);
				taskInfoJson.put("taskStepName", taskStepName);
				taskInfoJson.put("taskInstanceTitle", taskInstanceTitle);
				taskInfoJson.put("taskBeginTime", taskBeginTime);
				taskInfoJson.put("taskBeginTimeToLong", taskBeginTimeToLong);
				taskInfoJson.put("taskEndTime", taskEndTime);
				taskInfoJson.put("taskEndTimeToLong", taskEndTimeToLong);
				taskInfoJson.put("taskReadTime", taskReadTime);
				taskInfoJson.put("taskReadTimeToLong", taskReadTimeToLong);
				taskInfoJson.put("taskOwner", taskOwner);
				taskInfoJson.put("taskOwnerName", taskOwnerName);
				taskInfoJson.put("taskTarget", taskTarget);
				taskInfoJson.put("taskTargetName", taskTargetName);
				taskInfoJson.put("taskStates", taskStates);
				taskInfoJson.put("flowType", processType);
				taskData.add(taskInfoJson);
			}
			taskList.put("list", taskData);
		}else{
			taskList.put("count", 0);
			taskList.put("list", taskData);
		}
		return taskList;
	}
	
	/**
	 * 根据节点Id获取任务相关办理人
	 * @param taskId
	 * @param processDefId
	 * @param activityId
	 * @param isTransfer 过滤转办的人员
	 * @return
	 */
	public JSONArray getTaskTargetsByActivityId(String taskId,String processDefId,String activityId,boolean isTransfer){
		JSONArray taskUsers =new JSONArray();
		Connection conn = DBSql.open();
		try {
			String masterBindId = SDK.getBOAPI()
					.getByKeyField(CMCCConst.ROUTE_MASTER_TABLE, "PROCESSDEFIDINFO", processDefId).getBindId();
			List<BO> stepList = SDK.getBOAPI().query(CMCCConst.ROUTE_CHILD_TABLE, false).bindId(masterBindId)
					.addQuery("STEPID=", activityId).orderBy("UPDATEDATE").desc().list();
			if (stepList.size() > 0) {
				String id = stepList.get(0).getId();
				// 查询当前节点普通任务到达路由
				List<BO> arriveRouteList = SDK.getBOAPI().query(CMCCConst.ROUTE_FIELD_TABLE, false).bindId(id)
						.addQuery("TASKTYPE=", "1").list();
				if (arriveRouteList.size() > 0) {
					/** 获取当前节点路由信息 **/
					BO arriveRoute = arriveRouteList.get(0);
					taskUsers = getTaskUserList(conn, taskId, arriveRoute, isTransfer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBSql.close(conn);
		}
		return taskUsers;
	}
	
	/**
	 * 获取相同节点历史最近的一个办理人
	 * @param processInstId
	 * @param activityId
	 * @return
	 */
	public String getHistoryTaskUsersByActivityId(String processInstId,String activityId){
		JSONArray taskUsers =new JSONArray();
		List<HistoryTaskInstance> list = taskAPI.queryHistory().processInstId(processInstId).activityDefId(activityId).orderByEndTime().desc().list();
		if(null != list && list.size()>0){
			StringBuilder sb = new StringBuilder();
			for (HistoryTaskInstance historyTaskInstance : list) {
				String target = historyTaskInstance.getTarget();
				//重复的历史办理人需要过滤掉
				if(sb.indexOf(target)>-1){
					continue;
				}
				sb.append(target);
				JSONObject json=new JSONObject();
				json.put("nextTaskUserId", target);
				json.put("nextTaskUserName", orgAPI.getUser(target).getUserName());
				json.put("userDeptPathName", orgAPI.getDepartmentById(historyTaskInstance.getTargetDepartmentId()).getPathNameOfCache());
				taskUsers.add(json);
			}
		}
		return taskUsers.toString();
	}
	
	/**
	 * 根据路由信息拼接sql
	 * @param bo
	 * @param taskId
	 * @return
	 */
	public JSONArray getTaskUserList(Connection conn,String taskId,BO bo,boolean isTransfer){
		JSONArray taskUsers =new JSONArray();
		StringBuilder sql =new StringBuilder();
		/** 路由相关信息开始 **/
		int processFilter = Integer.valueOf(bo.get("PROCESSFILTER").toString());//与流程相关场景
		String processStepId = bo.getString("PROCESSSTEPID");//流程相关节点值
		String companyId = bo.getString("COMPANYID");//公司ID
		String companyEXT = bo.getString("COMPANYEXT");//公司扩展条件
		String departmentId = bo.getString("DEPARTMENTID");//部门ID
		String departmentEXT = bo.getString("DEPARTMENTEXT");//部门扩展条件
		String personLevel = bo.getString("PERSONLEVEL");//人员级别
		String personLeverEXT = bo.getString("PERSONLEVEREXT");//人员级别扩展条件
		String roleEXT = bo.getString("ROLEEXT");//角色扩展条件
		String fixPerson = bo.getString("FIXPERSON");//固定人
		String fixPersonEXT = bo.getString("FIXPERSONEXT");//固定人扩展条件
		String routeInterfaceUrl = bo.getString("ROUTEINTERFACEURL");//第三方接口
		OrgUtil orgUtil = new OrgUtil();
		String roleId = orgUtil.convertRoleId(bo.getString("ROLEID"));////角色ID 需要先转化
		/** 路由相关信息结束 **/
		/** 任务相关信息开始 **/
		TaskInstance taskInstance = taskAPI.getTaskInstance(taskId);
		String currentTarget = taskInstance.getTarget();
		String processInstId = taskInstance.getProcessInstId();
		ProcessInstance processInstance = SDK.getProcessAPI().getInstanceById(processInstId);
		String parentProcessInstId = processInstance.getParentProcessInstId(); 
		/** 任务相关信息结束 **/
		//固定人
		if(UtilString.isNotEmpty(fixPerson)){
			String[] users = fixPerson.split(" ");
			for(int i = 0;i<users.length;i++){
				JSONObject userInfo =new JSONObject();
				if("".equals(orgAPI.validateUsers(users[i]))){
					UserModel user = orgAPI.getUser(users[i]);
					String pathNameOfCache = orgAPI.getDepartmentById(user.getDepartmentId()).getPathNameOfCache();
					userInfo.put("nextTaskUserId", user.getUID());
					userInfo.put("nextTaskUserName", user.getUserName());
					userInfo.put("UserDeptPathName", pathNameOfCache);
					taskUsers.add(userInfo);
				}
			}
			return taskUsers;
		}
		/** 拼接sql开始 **/
		conn = null == conn ? DBSql.open() : conn;
		sql.append(" SELECT USERID,USERNAME,DEPARTMENTID FROM ORGUSER WHERE CLOSED=0 ");
		//与流程相关场景
		if(!UtilString.isEmpty(processFilter) &&  !UtilString.isEmpty(processStepId)){
			if(processFilter == CMCCConst.PROCESSINFO_IRRELEVANT){
				
			}else if (processFilter==CMCCConst.PROCESSINFO_STEP_COMPANY){//与节点公司相关
				
			}else if (processFilter==CMCCConst.PROCESSINFO_STEP_DEPARTMENT){//与节点办理人部门相关
				String departmentIdSQL="SELECT TARGETDEPTID FROM WFC_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? "
						+ "UNION SELECT TARGETDEPTID FROM WFH_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? ";
				String targetDepartmentId= DBSql.getString(conn,departmentIdSQL,new Object[]{processStepId,processInstId,processStepId,processInstId});
				
				sql.append(getSQLByRoleId(targetDepartmentId, roleId));
			}else if (processFilter==CMCCConst.PROCESSINFO_STEP_HENDLER){//与节点相关办理人相关
				String targetSQL="SELECT TARGET FROM WFC_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? "
						+ "UNION SELECT TARGET FROM WFH_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? ";
				String target= DBSql.getString(conn,targetSQL,new Object[]{processStepId,processInstId,processStepId,processInstId});
				if(UtilString.isNotEmpty(target)){
					sql.append(" AND USERID = '");
					sql.append(target);
					sql.append("' ");
				}

			}else if (processFilter==CMCCConst.PROCESSINFO_PARENTSTEP_HENDLER){//与父节点办理人相关
				String targetSQL="SELECT TARGET FROM WFH_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? ORDER BY BEGINTIME DESC";
				String target= DBSql.getString(conn,targetSQL,new Object[]{processStepId,parentProcessInstId});
				if(UtilString.isNotEmpty(target)){
					sql.append(" AND USERID = '");
					sql.append(target);
					sql.append("' ");
				}
			}
		}
		//公司ID
		if(UtilString.isNotEmpty(companyId)){
			sql.append(" AND DEPARTMENTID = '");
			sql.append(companyId);
			sql.append("' ");
		}
		//部门ID
		sql.append(getSQLByRoleId(departmentId, roleId));
		
		//人员级别
		if(UtilString.isNotEmpty(personLevel)){
			sql.append(" AND EXT1 = '");
			sql.append(personLevel);
			sql.append("' ");
		}
		
		/** 拼接sql结束 **/
		/** 组装数据开始 **/
		List<RowMap> maps = DBSql.getMaps(conn, sql.toString(), new Object[]{});
		if(maps.size()>0){
			for (RowMap rowMap : maps) {
				if(isTransfer && currentTarget.equals(rowMap.getString("USERID"))){//如果是转办场景，则过滤当前办理人
					continue;
				}
				JSONObject json=new JSONObject();
				json.put("nextTaskUserId", rowMap.get("USERID"));
				json.put("nextTaskUserName", rowMap.get("USERNAME"));
				if(UtilString.isEmpty(roleId)){				                         
					json.put("userDeptPathName", orgAPI.getDepartmentById(rowMap.get("DEPARTMENTID").toString()).getPathNameOfCache());
					json.put("userDeptPathCode", orgAPI.getDepartmentById(rowMap.get("DEPARTMENTID").toString()).getOuterId());
				}else if(UtilString.isNotEmpty(roleId)){
					String departmentIdSql="SELECT DEPARTMENTID FROM ORGUSERMAP WHERE USERID = ? AND ROLEID = ?";
					String departmentIdByRole = DBSql.getString(conn,departmentIdSql,new Object[]{rowMap.get("USERID"),roleId});
					json.put("userDeptPathName", orgAPI.getDepartmentById(departmentIdByRole).getPathNameOfCache());
					json.put("userDeptPathCode", orgAPI.getDepartmentById(departmentIdByRole).getOuterId());
				}
				taskUsers.add(json);
			}
		}
		/** 组装数据结束 **/
		return taskUsers;
	}
	
	/**
	 * 根据部门和角色参数返回sql
	 * 角色参数不为空时，需要查兼职表；为空时，部门查orguser表
	 * @param departmentId
	 * @param roleId
	 * @return
	 */
	public StringBuilder getSQLByRoleId(String departmentId,String roleId){
		StringBuilder sql =new StringBuilder();
		//部门ID
		if(UtilString.isNotEmpty(departmentId) && UtilString.isNotEmpty(roleId)){//如果部门和角色并存则找兼职下角色
			sql.append(" AND USERID IN (");
			sql.append(" SELECT USERID FROM ORGUSERMAP WHERE 1=1 ");
			sql.append(" AND ROLEID = '");
			sql.append(roleId);
			sql.append("' ");
			sql.append(" AND DEPARTMENTID = '");
			sql.append(departmentId);
			sql.append("' ");
			sql.append(") ");
		}else if(UtilString.isNotEmpty(roleId)){//只有角色
			sql.append(" AND USERID IN (");
			sql.append(" SELECT USERID FROM ORGUSERMAP WHERE 1=1 ");
			sql.append(" AND ROLEID = '");
			sql.append(roleId);
			sql.append("' ");
			sql.append(") ");
		}else if(UtilString.isNotEmpty(departmentId)){//只有部门
			sql.append(" AND DEPARTMENTID = '");
			sql.append(departmentId);
			sql.append("' ");
		}
		return sql;
	}
	
	/**
	 * 去重List
	 * @param planList
	 * @return
	 */
	public static List<HistoryTaskInstance> removeDuplicatePlan(List<HistoryTaskInstance> planList) {
        Set<HistoryTaskInstance> set = new TreeSet<HistoryTaskInstance>(new Comparator<HistoryTaskInstance>() {
            @Override
            public int compare(HistoryTaskInstance a, HistoryTaskInstance b) {
                // 字符串则按照asicc码升序排列
                return a.getProcessInstId().compareTo(b.getProcessInstId());
            }
        });
        set.addAll(planList);
        return new ArrayList<HistoryTaskInstance>(set);
    }
	
	/**
	 * 排序list
	 * @param list
	 */
	public static void ListSort(List<HistoryTaskInstance> list) {
        Collections.sort(list, new Comparator<HistoryTaskInstance>() {
            @Override
            //定义一个比较器
            public int compare(HistoryTaskInstance o1, HistoryTaskInstance o2) {
                try {
                    if (o1.getEndTime().getTime() > o2.getEndTime().getTime() ) {
                        return -1;
                    } else if (o1.getEndTime().getTime() < o2.getEndTime().getTime()) {
                        return 1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
	
	/**
	 * 判断流程是否结束
	 */
	public static boolean processIsEnd(String processId){
		boolean is_end = SDK.getProcessAPI().isEndById(processId);
		return is_end;
	}
}
