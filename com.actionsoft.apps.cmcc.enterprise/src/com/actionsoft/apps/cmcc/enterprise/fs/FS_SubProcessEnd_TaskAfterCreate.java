package com.actionsoft.apps.cmcc.enterprise.fs;
/**
 * 企标复审流程
 * 子流程最后节点自动完成事件
 * @author chenxf
 * @date 20181008
 */


import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.sdk.local.SDK;

public class FS_SubProcessEnd_TaskAfterCreate extends ExecuteListener implements ExecuteListenerInterface {
	public String getDescription() {
		return "子流程最后节点自动完成事件";
	}

	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		String taskId = param.getTaskInstance().getId();// 任务ID
		TaskInstance taskInstance = SDK.getTaskAPI().getTaskInstance(taskId);
		String userid = taskInstance.getTarget();
		SDK.getTaskAPI().completeTask(taskId, userid, true);// 自动完成任务
	}
}
