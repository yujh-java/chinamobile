package com.actionsoft.apps.cmcc.lx;
/**
 * 立项流程
 * 配合研发机构子流程任务创建后，自动执行
 * @author nch
 * @date 20170622
 */
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

public class Lx_PhYfjg_Sub_TaskAfterCreate extends ExecuteListener implements
		ExecuteListenerInterface {

	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		String taskId = param.getTaskInstance().getId();
		String userid = param.getUserContext().getUID();
		String processId = param.getProcessInstance().getId();
		//查询记录流程创建时标题
		String title = DBSql.getString("SELECT TITLE FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID= ?", new Object[]{processId});
		//修改流程标题
		SDK.getProcessAPI().setTitle(processId, title);
		//完成任务
		SDK.getTaskAPI().completeTask(taskId, userid,true);
	}

}
