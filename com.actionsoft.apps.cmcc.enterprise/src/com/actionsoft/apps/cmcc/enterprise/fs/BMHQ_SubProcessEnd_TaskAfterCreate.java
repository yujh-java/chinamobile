package com.actionsoft.apps.cmcc.enterprise.fs;

import java.sql.Connection;
import java.util.List;

import com.actionsoft.apps.cmcc.enterprise.ys.QbActivityIdConst;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/**
 * 企标非重点流程
 * 配合部所会签子流程最后节点自动完成事件
 * @author wxx
 * @date 20190423
 */
public class BMHQ_SubProcessEnd_TaskAfterCreate extends ExecuteListener implements ExecuteListenerInterface{
	public String getDescription() {
		return "子流程最后节点自动完成事件";
	}

	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		String taskId = param.getTaskInstance().getId();// 任务ID
		TaskInstance taskInstance = SDK.getTaskAPI().getTaskInstance(taskId);
		String userid = taskInstance.getTarget();

		String parentProcessId = param.getProcessInstance().getParentProcessInstId();// 父流程实例ID
		String task_acticityId = taskInstance.getActivityDefId();
		String acticityId = "";
		if (task_acticityId.equals(QbActivityIdConst.ysbmjkr)) {// 子流程最后一个节点
			acticityId = QbActivityIdConst.qtyfjkr;// 主流程中技术部企标管理员处理节点
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
					.addQuery("ACTIVITYID = ", "obj_c86b19b02b4000019aafad4018701989").connection(conn).list();
			if (list != null && list.size() > 0) {// 记录表中已存在
				BO bo = list.get(0);
				bo.set("HANDLERS", target);
				SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSHANDLE", bo,conn);// 更新记录
			} else {// 记录表中不存在此记录
				/*BO bo = new BO();
				bo.set("PROCESSID", parentProcessId);
				bo.set("ACTIVITYID", acticityId);
				bo.set("HANDLERS", target);
				SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSHANDLE", bo, param.getUserContext(),conn);*/// 记录表中创建新记录
			}

			SDK.getTaskAPI().completeTask(taskId, userid, true);// 自动完成任务
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
	}

}
