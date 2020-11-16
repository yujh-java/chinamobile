package com.actionsoft.apps.cmcc.at;
/**
 * 根据角色名称获取角色ID.所有流程，按角色取人
 * @author nch
 * @date 20170622
 */
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSExpressionException;

public class getRoleIdByRoleNameExpressipon extends AbstExpression {
	public getRoleIdByRoleNameExpressipon(ExpressionContext atContext,
			String expressionValue) {
		super(atContext, expressionValue);
	}
	@Override
	public String execute(String expression) throws AWSExpressionException {
		String rolename = getParameter(expression, 1);//角色名称
		String roleid = DBSql.getString("SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{rolename});
		return roleid;
	}

}
