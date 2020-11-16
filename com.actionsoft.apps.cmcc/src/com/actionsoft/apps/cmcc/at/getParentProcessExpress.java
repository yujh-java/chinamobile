package com.actionsoft.apps.cmcc.at;
/**
 * 获取子流程的父流程实例ID（未使用）
 * @author nch
 * @date 20170622
 */
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.exception.AWSExpressionException;
import com.actionsoft.sdk.local.SDK;

public class getParentProcessExpress extends AbstExpression {

	public getParentProcessExpress(ExpressionContext atContext,
			String expressionValue) {
		super(atContext, expressionValue);
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		String processId = getParameter(expression, 1);//流程实例ID
		ProcessInstance processInstance = SDK.getProcessAPI().getInstanceById(processId);
		if(processInstance != null){
			String parentProcessId = processInstance.getParentProcessInstId();
			return parentProcessId;
		}
		return null;
	}
}
