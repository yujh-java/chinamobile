package com.actionsoft.apps.cmcc.budget.at;

import com.actionsoft.apps.cmcc.budget.util.CmccCommon;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.exception.AWSExpressionException;
/**
 * at公式--在角色维护表中获取指定角色名、部门ID的人员账号
 * ID：@getUserIdByRoleData(roleName,depdId)
 * 例如：@getUserIdByRoleData(研发单位企标接口人,@departmentId)
 * @author chenxf
 *
 */
public class getBudgetUserIdByRoleData extends AbstExpression{

	public UserContext uc;
	public getBudgetUserIdByRoleData(ExpressionContext atContext, String expressionValue) {
		super(atContext, expressionValue);
		this.uc = atContext.getUserContext();
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		//角色名
		String rolename = getParameter(expression, 1);
		//部门ID
		String deptId = getParameter(expression, 2);
		//返回人员账号
		return CmccCommon.getEnterpriseManager(rolename,deptId);
	}
}
