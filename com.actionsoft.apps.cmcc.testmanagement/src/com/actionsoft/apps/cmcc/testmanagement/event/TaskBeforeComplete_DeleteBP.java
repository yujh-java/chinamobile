package com.actionsoft.apps.cmcc.testmanagement.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.sdk.local.SDK;

/** 
* @author yujh
* @version 创建时间：2019年10月10日 上午10:42:27 
* 类说明 
*/
public class TaskBeforeComplete_DeleteBP extends InterruptListener implements InterruptListenerInterface{
	public String getDescription() {
		return "任务完成前，删除其他并签任务，实现一人办理其他任务消失功能（抢办）";
	}

	@Override
	public boolean execute(ProcessExecutionContext param) throws Exception {
		String taskInstId = param.getTaskInstance().getId();
		String uid = param.getUserContext().getUID();
		SDK.getTaskAPI().deleteOtherTask(taskInstId, uid);
		return true;
	}



}
