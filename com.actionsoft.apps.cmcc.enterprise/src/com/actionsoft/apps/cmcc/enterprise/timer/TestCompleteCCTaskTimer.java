package com.actionsoft.apps.cmcc.enterprise.timer;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;

public class TestCompleteCCTaskTimer implements IJob{

	@Override
	public void execute(JobExecutionContext jx) throws JobExecutionException {
		//任务ID
		String taskid = "3277c2bd-c3c8-4d6b-bb5a-514fe628b0e3";
		//任务实例对象
		TaskInstance task = SDK.getTaskAPI().getTaskInstance(taskid);
		//人员账号
		String userid = "wuqianqian@hq.cmcc";
		//人员对象
		UserContext uc = UserContext.fromUID(userid);
		
		//自动阅文
		SDK.getTaskAPI().completeTask(task, uc);
	}
}
