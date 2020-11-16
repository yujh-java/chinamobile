package com.actionsoft.apps.cmcc.budget.rtclass;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.tools.ant.util.UnicodeUtil;

import com.actionsoft.apps.cmcc.budget.util.CmccCommon;
import com.actionsoft.apps.cmcc.budget.util.CmccUrlUtil;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
/**
 * 预算调整流程，创建流程信息
 * 起草环节，参数回传
 * @author chenxf
 *
 */
public class BudgetCreateProcessData_BF extends ExecuteListener
implements ExecuteListenerInterface{
	
	@Override
	public String getDescription() {
		return "1.预算调整流程，创建流程信息；<br/>2.起草环节，参数回传";
	}
	
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		String process_id = param.getProcessInstance().getId();
		String process_definid = param.getProcessInstance().getProcessDefId();// 流程定义ID
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String PROSUBTIME = sdf.format(date);
		Connection conn = null;
		try {
			conn = DBSql.open();
			List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", process_id)
					.connection(conn).list();
			if (list != null && list.size() > 0) {
				BO bo = list.get(0);
				String tjsj = bo.getString(PROSUBTIME);
				if (UtilString.isEmpty(tjsj)) {
					bo.set("PROSUBTIME", PROSUBTIME);
					SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSDATA", bo, conn);

				}
			} else {
				BO bo = new BO();
				String createUser = param.getProcessInstance().getCreateUser();
				String title = param.getProcessInstance().getTitle();
				String PROCESSTYPE = DBSql.getString(conn,
						"SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?",
						new Object[] { process_definid });
				bo.set("PROSUBTIME", PROSUBTIME);
				bo.set("PROCESSID", process_id);
				bo.set("CREATEUSERID", createUser);
				bo.set("TITLE", title);
				bo.set("PROCESSTYPE", PROCESSTYPE);
				SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSDATA", bo, param.getUserContext(), conn);
			}
			/**
			 * 起草环节，参数回传
			 */
			//penddingParams(param,conn);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(conn);
		}
		
	}
	/**
	 * 起草环节，流程提交后，状态回传
	 * @author chenxf
	 * @date   2018年9月27日 下午3:02:55
	 * @param param
	 * @param conn
	 */
	public void penddingParams(ProcessExecutionContext param,Connection conn){
		int taskState = param.getTaskInstance().getState();
		if(taskState == 1 || taskState == 4 || taskState==11){
			String task_id = param.getTaskInstance().getId();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Timestamp bTime = param.getTaskInstance().getBeginTime();
			String taskBeginTime= sdf.format(bTime);
			String process_id = param.getProcessInstance().getId();
			String activityID = param.getTaskInstance().getActivityDefId();
			String titles = "";
			String parentProcessId = "";
			if(activityID.equals("obj_c80d760a3c900001fd721e50e540abe0")){//预算子流程第一个节点ID
				//预算子流程父流程实例ID
				parentProcessId = SDK.getProcessAPI().getInstanceById(process_id).getParentProcessInstId();
				titles = DBSql.getString(conn,"SELECT TITLE FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID= ?", new Object[]{parentProcessId});
			}
			String dateStr = DBSql.getString(conn,"SELECT PROSUBTIME FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",new Object[]{process_id});
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
				System.err.println("======提交人和办理人的sql："+sql+"===========");
				
				String targetAndOwner = DBSql.getString(sql,new Object[]{process_id});
				//办理人
				String target = targetAndOwner.split(",")[1];
				//提交人
				String owner = targetAndOwner.split(",")[0];
				
				String parentTaskID = param.getTaskInstance().getParentTaskInstId();//父类任务实例id
				titles = param.getTaskInstance().getTitle();//任务标题
				String processDefid = param.getProcessDef().getId();//流程定义id
				String statename = CmccCommon.getNoteName(taskState, parentTaskID, titles, activityID, processDefid);
				
				//调取应用需求部门ID的接口URL
			    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.budget", "BACKDATAINTERFACE");
			    /*
			     * userName:提交人
			     * ownerId：待办人
			     * node:节点名称
			     * state: 0（流程结束_审批不通过）、1（流程注销）、2（退回起草）、3（流程结束_审批通过）、4（流程审批中）
			     */
			    StringBuffer sb = new StringBuffer(url);
			    sb.append("parentProcessId=");//父流程实例ID
			    sb.append(parentProcessId);
			    sb.append("&processId=");//当前流程实例ID
			    sb.append(process_id);
			    sb.append("&taskId=");//任务实例ID
			    sb.append(task_id);
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
			    //获取返回值
				String json = CmccUrlUtil.get(sb.toString());
			}
		}
	}
}
