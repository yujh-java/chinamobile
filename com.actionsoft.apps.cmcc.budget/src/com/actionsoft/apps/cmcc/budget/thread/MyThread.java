package com.actionsoft.apps.cmcc.budget.thread;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.sdk.local.SDK;
/**
 * 预算调整
 * 设置线程，延迟获取
 * @author chenxf
 *
 */
public class MyThread extends Thread{
	public ProcessExecutionContext cxt;
	public MyThread(ProcessExecutionContext cxt){
		this.cxt = cxt;
	}
	@Override
	public void run() {
		//任务ID
		String taskId = cxt.getTaskInstance().getId();
		//账号
		String userid = cxt.getUserContext().getUID();
		SDK.getTaskAPI().completeTask(taskId, userid,true);//自动完成任务
	}
}
