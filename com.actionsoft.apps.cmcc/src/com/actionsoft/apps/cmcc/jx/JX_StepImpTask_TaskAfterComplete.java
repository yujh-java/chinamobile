package com.actionsoft.apps.cmcc.jx;
/**
 * 结项流程
 * 项目结项项目接口人任务完成事件
 * @author nch
 * @date 20170622
 */
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;

public class JX_StepImpTask_TaskAfterComplete extends ExecuteListener implements
		ExecuteListenerInterface {
	public String getDescription() {
		return "移除记录节点参与者信息";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		String bindid = param.getProcessInstance().getId();//流程实例ID
		String acticityId = param.getTaskInstance().getActivityDefId();//记录节点ID
		//删除BO_ACT_CMCC_PROCESSHANDLE对应的记录
		String deleteSql = "DELETE FROM BO_ACT_CMCC_PROCESSHANDLE WHERE PROCESSID = '"+bindid+"'" +
				" AND ACTIVITYID = '"+acticityId+"'";
		DBSql.update(deleteSql);
	}
}
