package com.actionsoft.apps.cmcc.testmanagement.at;

import com.actionsoft.apps.cmcc.testmanagement.util.InterfaceUtil;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.exception.AWSExpressionException;

import net.sf.json.JSONObject;

/** 
* @author yujh
* @version 创建时间：2019年7月25日 上午9:35:11 
* 根据流程ID获取项目经理
*/
public class GetProjectManager extends AbstExpression {

	public GetProjectManager (ExpressionContext atContext, String expressionValue) {
		super(atContext, expressionValue);
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		String processId = getParameter(expression, 1);//获取流程ID
		InterfaceUtil util =new InterfaceUtil();
		JSONObject projectInfo = JSONObject.fromObject(util.getProjectInfo(processId));
		String projectManagerId = projectInfo.getString("projectManagerId");
		return projectManagerId;
	}

}
