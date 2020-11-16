package com.actionsoft.apps.cmcc.budget.statusImp;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/**
 * 状态信息回写,pending 公共事件
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.actionsoft.apps.cmcc.budget.CmccConst;
import com.actionsoft.apps.cmcc.budget.util.CmccCommon;
import com.actionsoft.apps.cmcc.budget.util.CmccUrlUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class PendingStates_TaskAfterCreate extends ExecuteListener implements
ExecuteListenerInterface {
	public String getDescription() {
		return "pending状态信息回写";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		int taskState = param.getTaskInstance().getState();
		if(taskState == 1 || taskState == 4 || taskState==11){
			String process_id = param.getProcessInstance().getId();
			String task_id = param.getTaskInstance().getId();
			String parentTaskId = param.getTaskInstance().getParentTaskInstId();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Timestamp bTime = param.getTaskInstance().getBeginTime();
			String taskBeginTime= sdf.format(bTime);
			String activityID = param.getTaskInstance().getActivityDefId();
			Connection conn = DBSql.open();
			try{
				String title = "";
				String parentProcessId = "";
				if(activityID.equals("obj_c80d760a3c900001fd721e50e540abe0") || activityID.equals("obj_c8302458dc600001fbb91672c3901f3f")){//预算子流程第一个节点ID
					String parentActivity = SDK.getTaskAPI().getInstanceById(parentTaskId).getActivityDefId();
					//预算子流程父流程实例ID
					parentProcessId = SDK.getProcessAPI().getInstanceById(process_id).getParentProcessInstId();
					title = DBSql.getString(conn,"SELECT TITLE FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID= ? ORDER BY PROSUBTIME DESC", new Object[]{parentProcessId});
					if(parentActivity.equals(CmccConst.budget_step01_activityid_children_start)){
					String sql="SELECT ID FROM WFH_TASK WHERE PROCESSINSTID= ? AND ACTIVITYDEFID= ? ORDER BY ENDTIME DESC ";
						parentTaskId = DBSql.getString(sql,new Object[]{parentProcessId,CmccConst.budget_step01_activityid_children});
					}
				}
				String dateStr = DBSql.getString(conn,"SELECT PROSUBTIME FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ? ORDER BY PROSUBTIME DESC",new Object[]{process_id});
				if(!UtilString.isEmpty(dateStr) || !UtilString.isEmpty(parentProcessId)){
					//流程创建者
					String submitterid = param.getProcessInstance().getCreateUser();
					/*
					 * 获取当前任务的提交人和办理人
					 */
					String sql = "select CONCAT_WS(',',OWNER,TARGET) from WFC_TASK "
							+ "where processinstid = ? "
							+ "and (taskstate = 1 or taskstate = 11) "
							+ "order by begintime desc" ;
					String targetAndOwner = DBSql.getString(sql,new Object[]{process_id});
					//办理人
					String target = targetAndOwner.split(",")[1];
					//提交人
					String owner = targetAndOwner.split(",")[0];
					
					//上个节点DEFID  （为了处理子流程第一节点接口提交人信息）
					String activityDefId = SDK.getTaskAPI().getTaskInstance(param.getTaskInstance().getParentTaskInstId()).getActivityDefId();
					if(activityID.equals("obj_c80d760a3c900001fd721e50e540abe0") && activityDefId.equals("obj_c808ed4f4fe000013a25157019908c10")){
						String ownerSql ="SELECT OWNER FROM WFH_TASK WHERE ACTIVITYDEFID='obj_c808ef6042300001c45f3b10d4101a46' AND PROCESSINSTID =? ORDER BY ENDTIME DESC LIMIT 0,1";
						owner= DBSql.getString(ownerSql,new Object[]{parentProcessId});
					}
//					if(!UtilString.isEmpty(parentProcessId)){
//						process_id = parentProcessId;
//					}
					
					String parentTaskID = param.getTaskInstance().getParentTaskInstId();//父类任务实例id
					title = param.getTaskInstance().getTitle();//任务标题
					String processDefid = param.getProcessDef().getId();//流程定义id
					String statename = CmccCommon.getNoteName(taskState, parentTaskID, title, activityID, processDefid);
					
					//调取应用需求部门ID的接口URL
				    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.budget", "BACKDATAINTERFACE");
				    /*
				     * userName:提交人
				     * ownerId：待办人
				     * node:节点名称
				     * state: 0（流程结束_审批不通过）、1（流程起草）、2（退回起草）、3（流程结束_审批通过）、4（流程审批中）
				     */
				    StringBuffer sb = new StringBuffer(url);
				    sb.append("parentProcessId=");//父流程实例ID
				    sb.append(parentProcessId);
				    sb.append("&processId=");//当前流程实例ID
				    sb.append(process_id);
				    sb.append("&taskId=");//任务实例ID
				    sb.append(task_id);
				    sb.append("&parentTaskId=");//父任务实例ID
				    sb.append(parentTaskId);
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
				    sb.append("&state=4");//流程状态
				    sb.append("&userName=");//流程创建者
				    sb.append(submitterid);
				    sb.append("&submitterid=");//流程提交人
				    sb.append(owner);
				    sb.append("&ownerId=");//当前任务办理者
				    sb.append(target);
				    sb.append("&node=");//节点名称
				    sb.append(statename);
				    sb.append("&taskState=");//任务状态
				    sb.append(taskState);
				    System.err.println("======url:"+sb.toString()+"=====");
				    //获取返回值
					String json = CmccUrlUtil.get(sb.toString());
					
				}
			}catch(Exception e){
				e.printStackTrace(System.err);
			}finally{
				DBSql.close(conn);
			}
		}
	}
}
