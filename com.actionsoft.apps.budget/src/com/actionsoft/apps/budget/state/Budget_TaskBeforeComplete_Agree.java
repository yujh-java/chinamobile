/**
 * @author zhaoxs
 * @date 2017年8月3日下午2:32:07
 */
package com.actionsoft.apps.budget.state;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.sdk.local.SDK;

/**
 * @author w13021111134
 *
 */
public class Budget_TaskBeforeComplete_Agree extends InterruptListener implements InterruptListenerInterface {

	 public boolean  execute(ProcessExecutionContext ctx) throws Exception {
		 
		 boolean bh = SDK.getTaskAPI().isChoiceActionMenu(ctx.getTaskInstance().getId(), "同意");
		 if(!bh){
			 SDK.getProcessAPI().setVariable(ctx.getProcessInstance().getId(), "state", "不同意");
		 }
		 
	 return true;
	 }
}
