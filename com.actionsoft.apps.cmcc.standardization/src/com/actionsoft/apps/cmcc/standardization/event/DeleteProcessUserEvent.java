package com.actionsoft.apps.cmcc.standardization.event;

import java.sql.Connection;
import java.util.List;
import com.actionsoft.apps.cmcc.standardization.constant.StandardizationConstant;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2019年4月25日 下午5:40:34 
* 类说明 
*/
public class DeleteProcessUserEvent extends ExecuteListener implements ExecuteListenerInterface{
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		String taskId = param.getTaskInstance().getId();// 任务ID
		TaskInstance taskInstance = SDK.getTaskAPI().getTaskInstance(taskId);
		String userid = taskInstance.getTarget();
		String parentProcessId = param.getProcessInstance().getParentProcessInstId();// 父流程实例ID
		String task_acticityId = taskInstance.getActivityDefId();
		String acticityId = "";
		if (task_acticityId.equals(StandardizationConstant.END_STEP)) {// 子流程最后一个节点
			acticityId = StandardizationConstant.HQ_STEP;// 主流程会签节点
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
					.orderBy("UPDATEDATE").desc().connection(conn).list();
			if (list != null && list.size() > 0) {// 记录表中已存在
				BO bo = list.get(0);
				bo.set("HANDLERS", "");
				SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSHANDLE", bo,conn);// 更新记录
			} 
			SDK.getTaskAPI().completeTask(taskId, userid, true);// 自动完成任务
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
		
	}
	
}
