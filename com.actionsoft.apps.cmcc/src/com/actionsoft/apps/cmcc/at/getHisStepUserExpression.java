package com.actionsoft.apps.cmcc.at;

/**
 * 获取指定节点历史参与者,立项、结项子流程中最后节点
 * @author nch
 * @date 20170622
 */
import java.util.List;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.exception.AWSExpressionException;
import com.actionsoft.sdk.local.SDK;

public class getHisStepUserExpression extends AbstExpression {
	public getHisStepUserExpression(ExpressionContext atContext, String expressionValue) {
		super(atContext, expressionValue);
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		String processId = getParameter(expression, 1);// 当前流程实例ID
		String activityId = getParameter(expression, 2);// 指定节点ID
		// 查询父流程信息
		String parentProcessId = SDK.getProcessAPI().getInstanceById(processId).getParentProcessInstId();
		List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI().activityDefId(activityId)
				.processInstId(parentProcessId).userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc()
				.list();
		String target = null;
		if (list != null && list.size() > 0) {
			target = list.get(list.size() - 1).getTarget();
		}
		return target;
	}

}
