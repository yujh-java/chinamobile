package com.actionsoft.apps.cmcc.budget.rtclass;

import java.util.Timer;
import java.util.TimerTask;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
/**
 * 预算调整
 * 第一个节点任务创建后事件
 * 自动结束任务
 * @author chenxf
 *
 */
public class BudgetAutoEndRtclass_CreateTaskAfter extends ExecuteListener
implements ExecuteListenerInterface{
	public String getDescription() {
		return "第一个节点任务创建后事件";
	}
	@Override
	public void execute(ProcessExecutionContext cxt) throws Exception {
		String processId = cxt.getProcessInstance().getId();
		System.err.println("=======processId:"+processId+"============");
		//任务ID
		String taskId = cxt.getTaskInstance().getId();
		System.err.println("=======taskId:"+taskId+"============");
		//账号
		String userid = cxt.getUserContext().getUID();
		System.err.println("=======userid:"+userid+"============");
		//预算子流程父流程实例ID
		String parentProcessId = SDK.getProcessAPI().getInstanceById(processId).getParentProcessInstId();
		String title = DBSql.getString("SELECT TITLE FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID= ?", new Object[]{parentProcessId});
		SDK.getProcessAPI().setTitle(processId, title);
		SDK.getTaskAPI().setTitle(taskId, title);
		System.err.println("=======自动结束任务============");
		/*
		 * 定时器，3秒钟后执行
		 */
		Timer timer = new Timer();
	    timer.schedule(new TimerTask() {
			@Override
			public void run() {
				SDK.getTaskAPI().completeTask(taskId, userid,true);//自动完成任务
			}
	    }, 2000);// 设定指定的时间time,此处为3000毫秒
	}
}
