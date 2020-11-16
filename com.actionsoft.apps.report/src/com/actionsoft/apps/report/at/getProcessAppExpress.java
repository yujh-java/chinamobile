package com.actionsoft.apps.report.at;
/**
 * 获取角色对应的流程应用或流程类型
 * @author zhaoxs
 * @date 2017-09-29
 */

import java.util.List;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSExpressionException;

public class getProcessAppExpress extends AbstExpression {

	public getProcessAppExpress(ExpressionContext atContext, String expressionValue) {
		super(atContext, expressionValue);
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		String type = getParameter(expression, 1);// 类型(区分是流程应用还是流程类型)
		String allRoleid = getParameter(expression, 2);// 用户所有角色ID

		StringBuffer sf = new StringBuffer();
		StringBuffer sbf = new StringBuffer();
		String sql = "SELECT * FROM  BO_ACT_REPORT_LIMIT WHERE ROLEID IN";
		sbf.append(sql).append(" ").append("(").append(allRoleid).append(")");
		List<RowMap> list = DBSql.getMaps(sbf.toString());
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (i != 0) {
					sf.append(",");
				}
				if (type.equals("PROCESSTYPE")) {
					String processtype = list.get(i).getString("PROCESSTYPE");
					String[] pt = processtype.split(",");
					if (pt != null && pt.length > 0) {
						for (int j = 0; j < pt.length; j++) {
							if (j != 0) {
								sf.append(",");
							}
							sf.append("'").append(pt[j]).append("'");
						}
					}
				} else if (type.equals("PROCESSAPP")) {
					String processapp = list.get(i).getString("PROCESSAPP");
					String[] pa = processapp.split(",");
					if (pa != null && pa.length > 0) {
						for (int j = 0; j < pa.length; j++) {
							if (j != 0) {
								sf.append(",");
							}
							sf.append("'").append(pa[j]).append("'");
						}
					}

				}
			}
			return sf.toString();
		} else {
			return null;
		}

	}
}
