package com.actionsoft.apps.cmcc.at;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSExpressionException;
/**
 * 从项目管理维护表中，获取项目管理人员
 * @author chenxf
 *
 */
public class getProductManagerPeople extends AbstExpression{
	
	public getProductManagerPeople(ExpressionContext atContext,
			String expressionValue) {
		super(atContext, expressionValue);
	}
	@Override
	public String execute(String expression) throws AWSExpressionException {
		//获取项目类型
		String productType = getParameter(expression, 1);
		//获取项目管理人员的sql
		String sql = "select GROUP_CONCAT(glr order by px asc) glr from BO_ACT_PM_MAINTENANCEINFO_S";
		//获取拼接后的人员userid集合
		String userid = DBSql.getString(sql);
		//把逗号替换成空格
		userid = userid.replaceAll(",", " ");
		return userid;
	}
}
