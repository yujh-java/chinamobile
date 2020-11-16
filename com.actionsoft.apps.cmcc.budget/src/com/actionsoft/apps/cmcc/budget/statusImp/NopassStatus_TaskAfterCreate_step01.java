package com.actionsoft.apps.cmcc.budget.statusImp;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/**
 * 预算调整流程
 * 节点1，任务创建后，状态信息回写 公共事件
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.actionsoft.apps.cmcc.budget.CmccConst;
import com.actionsoft.apps.cmcc.budget.util.CmccCommon;
import com.actionsoft.apps.cmcc.budget.util.CmccUrlUtil;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.constant.UserTaskRuntimeConst;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class NopassStatus_TaskAfterCreate_step01 extends ExecuteListener implements
ExecuteListenerInterface {
	public String getDescription() {
		return "任务创建后，状态信息回写";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		int taskState = param.getTaskInstance().getState();
		if(taskState == 1 || taskState == 4 || taskState==11){
			String process_id = param.getProcessInstance().getId();
			String task_id = param.getTaskInstance().getId();
			String parentTaskId = param.getTaskInstance().getParentTaskInstId();
			String submitterid = param.getProcessInstance().getCreateUser();//流程创建者
			String activityId = param.getTaskInstance().getActivityDefId();//节点id
			String task_title = param.getTaskInstance().getTitle();//任务标题
			String processDefid = param.getProcessDef().getId();//流程定义id
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Timestamp bTime = param.getTaskInstance().getBeginTime();
			String taskBeginTime= sdf.format(bTime);
			String parentProcessId = "";//wxx
			String statename="";
			int state =2 ; //默认为退回起草
			parentProcessId = SDK.getProcessAPI().getInstanceById(process_id).getParentProcessInstId();//wxx
			Connection conn = DBSql.open();
			//办理人
			String target = param.getTaskInstance().getTarget();
			String owner = param.getTaskInstance().getOwner();
			try{
				String dateStr = DBSql.getString(conn,"SELECT PROSUBTIME FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ? ORDER BY PROSUBTIME DESC",new Object[]{process_id});
				if(!UtilString.isEmpty(dateStr) && taskState==11){//起草节点的加签任务
					state =4 ;
					int parentState = SDK.getTaskAPI().getTaskInstance(parentTaskId).getState();//父任务状态
					if(parentState == UserTaskRuntimeConst.STATE_TYPE_WAIT){//4:等待加签中，为第一加签节点
						String sql= "SELECT JQ_ACTIVITYNAME1 FROM BO_ACT_EXBUTTON_MEMO_DATA WHERE PROCESSACTIVITYID = ? AND CMCCPROCESSDEFID = ?";
						statename = DBSql.getString(sql ,new Object[]{CmccConst.budget_step01_activityid , processDefid});
					}else if(parentState == UserTaskRuntimeConst.STATE_TYPE_ADHOC){//11：加签中，为第二次加签节点
						String sql= "SELECT JQ_ACTIVITYNAME2 FROM BO_ACT_EXBUTTON_MEMO_DATA WHERE PROCESSACTIVITYID = ? AND CMCCPROCESSDEFID = ?";
						statename = DBSql.getString(sql ,new Object[]{CmccConst.budget_step01_activityid , processDefid});
					}
				}else{//创建流程信息
					String PROSUBTIME = sdf.format(new Date());
					BO bo = new BO();
					String createUser = param.getProcessInstance().getCreateUser();
					String title = param.getProcessInstance().getTitle();
					String PROCESSTYPE = DBSql.getString(conn,
							"SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?",
							new Object[] { processDefid });
					bo.set("PROSUBTIME", PROSUBTIME);
					bo.set("PROCESSID", process_id);
					bo.set("CREATEUSERID", createUser);
					bo.set("TITLE", title);
					bo.set("PROCESSTYPE", PROCESSTYPE);
					SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSDATA", bo, param.getUserContext(), conn);
					statename = CmccCommon.getNoteName(taskState, parentTaskId, task_title, activityId, processDefid);
				}
				if(task_title.contains("(收回)")){//如果是收回场景,则修改parentTaskId
					String sql = "SELECT ID FROM WFC_TASK WHERE PROCESSINSTID = ? ORDER BY BEGINTIME ASC LIMIT 0,1";
					String sql1 = "SELECT ID FROM WFH_TASK WHERE PROCESSINSTID = ? ORDER BY BEGINTIME ASC LIMIT 0,1";
					parentTaskId=DBSql.getString(sql,new Object[]{process_id});
					System.err.println(">>>>parentTaskId"+parentTaskId);
					System.err.println(">>>>parentTaskId_1"+DBSql.getString(sql1,new Object[]{process_id}));
					statename=SDK.getRepositoryAPI().getProcessNode(processDefid, activityId).getName();
					state = 6;//收回类的任务
					owner = target;//收回类任务提交人和办理人应该为同一人
				}
			    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.budget", "BACKDATAINTERFACE");
			    /*
			     * userName:提交人
			     * ownerId：待办人
			     * node:节点名称
			     * state: 0（流程结束_审批不通过）、1（流程起草）、2（退回起草）、3（流程结束_审批通过）、4（流程审批中）
			     */
			    StringBuffer sb = new StringBuffer(url);
			    sb.append("parentProcessId=");//父流程实例ID
			    sb.append(parentProcessId);//wxx
			    sb.append("&processId=");//流程实例ID
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
			    sb.append("&state=");//流程状态 默认为退回起草，收回类任务为6
			    sb.append(state);
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
			  //获取返回值
				String json = CmccUrlUtil.get(sb.toString());
			}catch(Exception e){
				e.printStackTrace(System.err);
			}finally{
				DBSql.close(conn);
			}
		}
	}
}
