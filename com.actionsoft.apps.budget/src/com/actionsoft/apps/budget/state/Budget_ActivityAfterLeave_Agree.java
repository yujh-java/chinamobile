/**
 * @author zhaoxs
 * @date 2017年8月15日上午10:20:07
 */
package com.actionsoft.apps.budget.state;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.sdk.local.SDK;

/**
 * @author w13021111134
 *
 */
public class Budget_ActivityAfterLeave_Agree extends ExecuteListener implements ExecuteListenerInterface {

	@Override
	public void execute(ProcessExecutionContext pec) throws Exception {
		
		 SDK.getProcessAPI().setVariable(pec.getProcessInstance().getId(), "state", "同意");


	}

}
