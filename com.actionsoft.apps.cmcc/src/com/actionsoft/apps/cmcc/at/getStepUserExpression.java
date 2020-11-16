package com.actionsoft.apps.cmcc.at;
/**
 * 子流程启动者(未使用)
 * @author nch
 * @date 20170622
 */
import java.util.List;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.exception.AWSExpressionException;
import com.actionsoft.sdk.local.SDK;

public class getStepUserExpression extends AbstExpression {
	public getStepUserExpression(ExpressionContext atContext,
			String expressionValue) {
		super(atContext, expressionValue);
	}
	@Override
	public String execute(String expression) throws AWSExpressionException {
		String processId = getParameter(expression, 1);//当前流程实例ID
		String activityId = getParameter(expression, 2);//指定节点ID
		//查询父流程信息
		String parentProcessId = SDK.getProcessAPI().getInstanceById(processId).getParentProcessInstId();
		List<TaskInstance> list = SDK.getTaskQueryAPI().activityDefId(activityId).processInstId(parentProcessId).userTaskOfWorking().list();
		String target = null;
		if(list != null && list.size() > 0){
			target = list.get(list.size()-1).getTarget();
		}
		return target;
	}

}
