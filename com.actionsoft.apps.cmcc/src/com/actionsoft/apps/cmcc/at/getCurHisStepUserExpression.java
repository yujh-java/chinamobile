package com.actionsoft.apps.cmcc.at;

import java.util.List;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.exception.AWSExpressionException;
import com.actionsoft.sdk.local.SDK;
/**
 * 获取当前流程制定节点的参与者
 * @getCurrentHisStepUser(@processId(),obj_c7864bf7fb5000013b21150612bd89b0,false)
 * @author chenxf
 *
 */
public class getCurHisStepUserExpression extends AbstExpression {
	public getCurHisStepUserExpression(ExpressionContext atContext, String expressionValue) {
		super(atContext, expressionValue);
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		String processId = getParameter(expression, 1);// 当前流程实例ID
		String activityId = getParameter(expression, 2);// 指定节点ID
		String flag = getParameter(expression, 3);// 是否为第一节点
		System.err.println("=====是否为第一节点："+flag+"========");
		
		//返回参与者
		String target = null;
		//为true说明是第一个节点，获取当前任务办理人
		if("true".equals(flag)){
			System.err.println("====================");
			// 查询当前流程信息
			List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI().activityDefId(activityId)
					.processInstId(processId).userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc()
					.list();
			System.err.println("=======list:"+list+"=============");
			
			if (list != null && list.size() > 0) {
				target = list.get(list.size() - 1).getTarget();
				System.err.println("=======返回时的接口人："+target+"==========");
			}else{
				target = SDK.getProcessAPI().getInstanceById(processId).getCreateUser();
				System.err.println("=======流程创建时接口人："+target+"==========");
			}
			
		}else{
			// 查询当前流程信息
			List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI().activityDefId(activityId)
					.processInstId(processId).userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc()
					.list();
			
			if (list != null && list.size() > 0) {
				target = list.get(list.size() - 1).getTarget();
			}
		}
		System.err.println("=======返回的target："+target+"==========");
		
		return target;
	}

}
