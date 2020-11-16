package com.actionsoft.apps.cmcc.budget.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.actionsoft.apps.cmcc.budget.CmccConst;
import com.actionsoft.bpms.bpmn.constant.UserTaskRuntimeConst;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/** 
* @author 作者 E-mail: yujh
* @version 创建时间：2019年4月18日 下午3:07:50 
* 为了解决加签类任务完成后不触发完成后事件作出补充
*/
public class TaskCompleteJQStep {
	
	/**
	 * 加签场景接口回调
	 * @param taskInst
	 * @param param
	 */
	public void sendTaskInfoByStep(TaskInstance taskInst, ProcessExecutionContext param){
		String taskId = taskInst.getId();//任务实例ID
		String processId = taskInst.getProcessInstId();//流程实例ID
		ProcessInstance processInst = SDK.getProcessAPI().getInstanceById(processId);
		String submitterid = processInst.getCreateUser();//流程创建者
		String processDefId = processInst.getProcessDefId();//流程定义ID
		TaskInstance parentInstance = SDK.getTaskAPI().getInstanceById(taskInst.getParentTaskInstId());//父任务实例
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String taskEndTime= sdf.format(date);
		String statename= "";//节点名称
		if(null == parentInstance){
			return;
		}
		String sql = "SELECT ID,ACTIVITYDEFID,OWNER,TARGET,TASKSTATE FROM WFC_TASK WHERE PROCESSINSTID  = ? AND ID = ? ";
		RowMap map = DBSql.getMap(sql, new Object[]{processId,parentInstance.getId()});
		String activityId = map.getString("ACTIVITYDEFID");
		if(!activityId.equals(CmccConst.budget_step01_activityid)){//已流转至其他节点
			return;
		}
		if(parentInstance.getState() == UserTaskRuntimeConst.STATE_TYPE_ADHOC){//上一个节点的状态为加签，则为第一加签节点
			String stateNameSql= "SELECT JQ_ACTIVITYNAME1 FROM BO_ACT_EXBUTTON_MEMO_DATA WHERE PROCESSACTIVITYID = ? AND CMCCPROCESSDEFID = ?";
			statename = DBSql.getString(stateNameSql ,new Object[]{CmccConst.budget_step01_activityid , processDefId});
		}else{
			statename = CmccCommon.getNoteName(parentInstance.getState(), parentInstance.getId(), parentInstance.getTitle(), parentInstance.getActivityDefId(), processDefId);
		}
		//调取应用需求部门ID的接口URL
	    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.budget", "BACKDATAINTERFACE");
		//调用接口
		StringBuffer sb = new StringBuffer(url);
	    sb.append("parentProcessId=");//父流程实例ID
	    sb.append("");//
	    sb.append("&processId=");//流程实例ID
	    sb.append(processId);
	    sb.append("&taskId=");//任务实例ID
	    sb.append(parentInstance.getId());
	    sb.append("&parentTaskId=");//父任务实例ID
	    sb.append(taskId);//父任务实例ID相当于当前已办任务的ID
	    sb.append("&taskBeginTime=");//任务开始时间
	    try {
	    	taskEndTime= URLEncoder.encode(taskEndTime, "UTF-8");
			taskEndTime = taskEndTime.replace("+", "%20");
			statename=URLEncoder.encode(statename, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    sb.append(taskEndTime);
	    sb.append("&state=4");//流程状态,审批通过
	    sb.append("&userName=");//流程创建者
	    sb.append(submitterid);
	    sb.append("&submitterid=");//流程提交人（为当前任务的办理人）
	    sb.append(taskInst.getTarget());//
	    sb.append("&ownerId=");//当前任务办理者（父加签任务的办理人）
	    sb.append(map.get("TARGET"));//
	    sb.append("&node=");//节点名称
	    sb.append(statename);
	    sb.append("&taskState=");//任务状态
	    sb.append(map.get("TASKSTATE"));
	    //获取返回值
	    System.err.println(">>>>>url:"+sb.toString());
		String json = CmccUrlUtil.get(sb.toString());
	}
	
	/**
	 * 转办任务接口回调
	 * @param taskInst
	 * @param param
	 */
	public void sendTaskInfoByStepZB(TaskInstance taskInst, ProcessExecutionContext param){
		String taskId = taskInst.getId();//任务实例ID
		String processId = taskInst.getProcessInstId();//流程实例ID、
		String owner = taskInst.getTarget();
		String title = param.getTaskInstance().getTitle();//任务标题
		ProcessInstance processInst = SDK.getProcessAPI().getInstanceById(processId);
		String submitterid = processInst.getCreateUser();//流程创建者
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String taskEndTime= sdf.format(date);
		String statename= "";//节点名称
		//调取应用需求部门ID的接口URL
		/*
		 * 获取当前任务的提交人和办理人
		 */
		String sql = "select CONCAT_WS(',',OWNER,TARGET) from WFC_TASK "
				+ "where processinstid = ? "
				+ "and (taskstate = 1 or taskstate = 11) "
				+ "order by begintime desc" ;
		String targetAndOwner = DBSql.getString(sql,new Object[]{processId});
		//办理人
		String target = targetAndOwner.split(",")[1];
		//提交人
		//String owner = targetAndOwner.split(",")[0];
		statename = CmccCommon.getNoteName(taskInst.getState(), taskId, title, taskInst.getActivityDefId(), taskInst.getProcessDefId());
	    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.budget", "BACKDATAINTERFACE");
		//调用接口
		StringBuffer sb = new StringBuffer(url);
	    sb.append("parentProcessId=");//父流程实例ID
	    sb.append("");//
	    sb.append("&processId=");//流程实例ID
	    sb.append(processId);
	    sb.append("&taskId=");//任务实例ID
	    sb.append(taskId);
	    sb.append("&parentTaskId=");//父任务实例ID
	    sb.append(taskId);//父任务实例ID相当于当前已办任务的ID
	    sb.append("&taskBeginTime=");//任务开始时间
	    try {
	    	taskEndTime= URLEncoder.encode(taskEndTime, "UTF-8");
			taskEndTime = taskEndTime.replace("+", "%20");
			statename=URLEncoder.encode(statename, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    sb.append(taskEndTime);
	    sb.append("&state=4");//审批通过
	    sb.append("&userName=");//流程创建者
	    sb.append(submitterid);
	    sb.append("&submitterid=");//流程提交人（为当前任务的办理人）
	    sb.append(owner);//
	    sb.append("&ownerId=");//当前任务办理者（父加签任务的办理人）
	    sb.append(target);
	    sb.append("&node=");//节点名称
	    sb.append(statename);
	    sb.append("&taskState=");//任务状态
	    sb.append(taskInst.getState());
	    //获取返回值
		String json = CmccUrlUtil.get(sb.toString());
	}
}
