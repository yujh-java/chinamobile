package com.actionsoft.apps.cmcc.budget.event;
import com.actionsoft.apps.cmcc.budget.CmccConst;
import com.actionsoft.apps.cmcc.budget.util.TaskCompleteJQStep;
import com.actionsoft.bpms.bpmn.constant.PublicEventConst;
import com.actionsoft.bpms.bpmn.constant.UserTaskRuntimeConst;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ProcessPubicListener;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;

/** 
* @author yujh: 
* @version 创建时间：2019年4月18日 下午2:58:13 
* 为了解决加签类任务完成后不触发完成后事件作出补充
*/
public class ProcessPubicEvent extends ProcessPubicListener{
	@Override
	public void call(String eventName, TaskInstance taskInst, ProcessExecutionContext ctx) {
		if(eventName.equals("TASK_COMPLETE")){//任务完成后
			//判断是否是预算第一节点,并且是否为加签任务
			if(taskInst.getActivityDefId().equals(CmccConst.budget_step01_activityid) && taskInst.getState() ==UserTaskRuntimeConst.STATE_TYPE_ADHOC){
				TaskCompleteJQStep taskComplete =new TaskCompleteJQStep();
				taskComplete.sendTaskInfoByStep(taskInst, ctx);
			}
		}else if(eventName.equals("TASK_DELEGATE")){//任务转办后
			if(taskInst.getActivityDefId().equals("obj_c7db7e0b018000016f37147040d3e560")){
				TaskCompleteJQStep taskComplete =new TaskCompleteJQStep();
				taskComplete.sendTaskInfoByStepZB(taskInst, ctx);
			}
		}
	}
}
