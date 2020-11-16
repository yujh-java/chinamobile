package com.actionsoft.apps.report.at;
/**
 * 获取用户的所有角色名称
 * @author zhaoxs
 * @date 2017-09-27
 */

import java.util.List;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSExpressionException;

public class getAllRoleIDExpress extends AbstExpression {

	public getAllRoleIDExpress(ExpressionContext atContext, String expressionValue) {
		super(atContext, expressionValue);
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		String userid = getParameter(expression, 1);// 用户userid

		String roleid = UserCache.getModel(userid).getRoleId();
		StringBuffer sbf = new StringBuffer();
		sbf.append("'").append(roleid).append("'");

		List<RowMap> oList = DBSql.getMaps("SELECT * FROM ORGUSERMAP WHERE USERID=? ", new Object[] { userid });
		if (oList != null && oList.size() > 0) {
			for (int i = 0; i < oList.size(); i++) {
				sbf.append(",").append("'").append(oList.get(i).getString("ROLEID")).append("'");
			}
		}

		return sbf.toString().trim();
	}
}
