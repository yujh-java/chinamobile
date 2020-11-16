package com.actionsoft.apps.cmcc.budget.ys;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/**
 * 预算调整流程
 * 子流程最后节点自动完成事件
 * @author wxx
 * @date 20180622
 */
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.actionsoft.apps.cmcc.budget.util.CmccUrlUtil;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

public class YS_SubProcessEnd_TaskAfterCreate extends ExecuteListener implements ExecuteListenerInterface{
	public String getDescription() {
		return "子流程最后节点自动完成事件";
	}

	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		String processId = param.getProcessInstance().getId();
		String taskId = param.getTaskInstance().getId();// 任务ID
		TaskInstance taskInstance = SDK.getTaskAPI().getTaskInstance(taskId);
		String userid = taskInstance.getTarget();
		String userName = param.getProcessInstance().getCreateUser();//流程创建者
		String ownerId = param.getTaskInstance().getTarget();
		String submitterid = param.getTaskInstance().getOwner();
		String parentTaskId = param.getTaskInstance().getParentTaskInstId();
		int taskState = param.getTaskInstance().getState();//任务状态
		String parentProcessId = param.getProcessInstance().getParentProcessInstId();// 父流程实例ID
		String task_acticityId = taskInstance.getActivityDefId();
		String acticityId = "";
		if (task_acticityId.equals(YsActivityIdConst.ysbmjkr)) {// 子流程最后一个节点
			acticityId = YsActivityIdConst.qtyfjkr;// 主流程中选择相关部所审核节点
		} 
		Connection conn = DBSql.open();
		try{
			List<HistoryTaskInstance> list_task = SDK.getHistoryTaskQueryAPI().activityDefId(acticityId)
					.processInstId(parentProcessId).userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc()
					.connection(conn).list();
			String target = null;
			if (list_task != null && list_task.size() > 0) {
				target = list_task.get(list_task.size() - 1).getTarget();
			}
			// 查询 (BO_ACT_CMCC_PROCESSHANDLE) 是否存在记录
			List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSHANDLE").addQuery("PROCESSID = ", parentProcessId)
					.addQuery("ACTIVITYID = ", acticityId).connection(conn).list();
			if (list != null && list.size() > 0) {// 记录表中已存在
				BO bo = list.get(0);
				bo.set("HANDLERS", target);
				SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSHANDLE", bo,conn);// 更新记录
			} else {// 记录表中不存在此记录
				BO bo = new BO();
				bo.set("PROCESSID", parentProcessId);
				bo.set("ACTIVITYID", acticityId);
				bo.set("HANDLERS", target);
				SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSHANDLE", bo, param.getUserContext(),conn);// 记录表中创建新记录
			}
			//回写接口
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String closetime = sdf.format(date);
			
			//调取应用需求部门ID的接口URL
		    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.budget", "BACKDATAINTERFACE");
		    
		    StringBuffer sb = new StringBuffer(url);
		    /*
		     * userName:提交人
		     * ownerId：待办人
		     * node:节点
		     */
		    
		    sb.append("processId=");
		    sb.append(processId);
		    sb.append("&parentProcessId=");//父流程实例ID
		    sb.append(parentProcessId);
		    sb.append("&parentTaskId=");//父任务实例ID
		    sb.append(parentTaskId);
		    sb.append("&state=5");//子流程结束(相当于会签结束)
		    sb.append("&taskBeginTime=");//任务开始时间
		    try {
		    	closetime= URLEncoder.encode(closetime, "UTF-8");
		    	closetime = closetime.replace("+", "%20");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    sb.append(closetime);
		    sb.append("&userName=");
		    sb.append(userName);
		    sb.append("&submitterid=");
		    sb.append(submitterid);
		    sb.append("&ownerId=");
		    sb.append(ownerId);
		    sb.append("&taskState=");//任务状态
		    sb.append(taskState);
		    System.err.println("======url:"+sb.toString()+"=====");
		    //http://10.2.5.92:8082/bms/updatefinanceprocess.shtml?processId=bcf062d2-df75-4bca-8d20-abe1bb1e4df6&state=3&taskBeginTime=2019-03-26%2017%3A24%3A52&userName=yinxiaoqian@hq.cmcc&taskState=1=====
		 	//获取返回值
			String json = CmccUrlUtil.get(sb.toString());
			SDK.getTaskAPI().completeTask(taskId, userid, true);// 自动完成任务
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
	}

}
