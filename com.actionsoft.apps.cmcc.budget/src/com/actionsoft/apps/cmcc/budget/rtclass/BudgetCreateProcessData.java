package com.actionsoft.apps.cmcc.budget.rtclass;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import com.actionsoft.apps.cmcc.budget.CmccConst;
import com.actionsoft.apps.cmcc.budget.util.CmccCommon;
import com.actionsoft.apps.cmcc.budget.util.CmccUrlUtil;
import com.actionsoft.bpms.bpmn.constant.UserTaskRuntimeConst;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
/**
 * 预算调整流程，创建流程信息
 * 起草环节，参数回传
 * @author chenxf
 *
 */
public class BudgetCreateProcessData extends ExecuteListener
implements ExecuteListenerInterface{
	
	@Override
	public String getDescription() {
		return "1.预算调整流程，创建流程信息；<br/>2.起草环节，参数回传";
	}
	
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		String taskId = param.getTaskInstance().getId();//任务实例ID
		String processId = param.getProcessInstance().getId();//流程实例ID
		String submitterid = param.getProcessInstance().getCreateUser();//流程创建者
		String processDefId = param.getProcessInstance().getProcessDefId();//流程定义ID
		TaskInstance parentInstance = SDK.getTaskAPI().getInstanceById(param.getTaskInstance().getParentTaskInstId());//父任务实例
		Timestamp bTime = param.getTaskInstance().getBeginTime();//开始时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String taskBeginTime= sdf.format(bTime);
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
			taskBeginTime= URLEncoder.encode(taskBeginTime, "UTF-8");
			taskBeginTime = taskBeginTime.replace("+", "%20");
			statename=URLEncoder.encode(statename, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    sb.append(taskBeginTime);
	    sb.append("&state=2");//流程状态,退回起草
	    sb.append("&userName=");//流程创建者
	    sb.append(submitterid);
	    sb.append("&submitterid=");//流程提交人
	    sb.append(map.get("OWNER"));
	    sb.append("&ownerId=");//当前任务办理者
	    sb.append(map.get("TARGET"));
	    sb.append("&node=");//节点名称
	    sb.append(statename);
	    sb.append("&taskState=");//任务状态
	    sb.append(map.get("TASKSTATE"));
	    //获取返回值
		String json = CmccUrlUtil.get(sb.toString());
	}
}
