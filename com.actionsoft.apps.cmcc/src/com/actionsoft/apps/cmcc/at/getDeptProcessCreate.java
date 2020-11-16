package com.actionsoft.apps.cmcc.at;
/**
 * 获得流程实例创建者的部门ID。成果提交流程，研发接口人节点
 * @author nch
 * @date 20170622
 */
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.exception.AWSExpressionException;
import com.actionsoft.sdk.local.SDK;

public class getDeptProcessCreate extends AbstExpression {
	public getDeptProcessCreate(ExpressionContext atContext,
			String expressionValue) {
		super(atContext, expressionValue);
	}
	@Override
	public String execute(String expression) throws AWSExpressionException {
		// TODO Auto-generated method stub
		String process_id = getParameter(expression, 1);//流程实例ID
		ProcessInstance processInstance = SDK.getProcessAPI().getInstanceById(process_id);
		String deptid = processInstance.getCreateUserDeptId();
		return deptid;
	}

}
