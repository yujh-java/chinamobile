package com.actionsoft.apps.cmcc.standardization.event;

import java.util.Timer;
import java.util.TimerTask;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2019年4月25日 下午4:22:02 
* 类说明  科技委子流程第一个节点任务创建后事件自动办理事件
*/
public class AutoCompleteTaskEvent extends ExecuteListener implements ExecuteListenerInterface{
	public String getDescription() {
		return "子流程第一个节点任务创建后事件自动办理事件";
	}
	@Override
	public void execute(ProcessExecutionContext cxt) throws Exception {
		// TODO Auto-generated method stub
		String processId = cxt.getProcessInstance().getId();
		String taskId = cxt.getTaskInstance().getId();
		String userid = cxt.getUserContext().getUID();
		String parentProcessId = SDK.getProcessAPI().getInstanceById(processId).getParentProcessInstId();
		String title = DBSql.getString("SELECT TITLE FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID= ?", new Object[]{parentProcessId});
		SDK.getProcessAPI().setTitle(processId, title);
		SDK.getTaskAPI().setTitle(taskId, title);
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
