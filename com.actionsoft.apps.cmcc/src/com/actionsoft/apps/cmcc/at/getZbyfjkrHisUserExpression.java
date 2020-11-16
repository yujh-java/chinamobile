package com.actionsoft.apps.cmcc.at;

import java.sql.Connection;
/**
 * 获取角色名称和节点查询历史参与者
 * @author zhaoxs
 * @date   2017-06-22
 */
import java.util.HashMap;
import java.util.List;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSExpressionException;
import com.actionsoft.sdk.local.SDK;

public class getZbyfjkrHisUserExpression extends AbstExpression {
	public getZbyfjkrHisUserExpression(ExpressionContext atContext, String expressionValue) {
		super(atContext, expressionValue);
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		String processId = getParameter(expression, 1);// 当前流程实例ID
		String activityId = getParameter(expression, 2);// 历史参与者节点ID
		String rolename = getParameter(expression, 3);// 角色名称
		String taskid = getParameter(expression, 4);// 获取任务实例id
		String noteid = getParameter(expression, 5);// 指定节点ID
		String noid = getParameter(expression, 6);// 指定节点ID
		Connection conn = null;
		StringBuffer sbf = new StringBuffer();
		try {
			conn = DBSql.open();
			String note = SDK.getTaskAPI().getInstanceById(taskid).getActivityDefId();// 获取节点id
			HashMap<String, Object> hashMap = new HashMap<>();
			List<HistoryTaskInstance> list = null;
			String target = null;
			if (note.equals(noteid) || note.equals(noid)) {
				list = SDK.getHistoryTaskQueryAPI().connection(conn).activityDefId(activityId).processInstId(processId)
						.userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc().list();

			}
			if (list != null && list.size() > 0) {
				target = list.get(list.size() - 1).getTarget();
				return target;
			} else {

				String roleid = DBSql.getString(conn, "SELECT ID FROM ORGROLE WHERE ROLENAME=?",
						new Object[] { rolename });// 获取角色id
				// String roleid = DBSql.getString("SELECT ID FROM ORGROLE WHERE
				// ROLENAME=?", new Object[] { rolename });// 获取角色id
				hashMap.put("ROLEID", roleid);
				// List<RowMap> userMap = DBSql.getMaps("SELECT * FROM
				// ORGUSERMAP WHERE ROLEID=:ROLEID", hashMap);
				List<RowMap> userMap = DBSql.getMaps(conn, "SELECT * FROM ORGUSERMAP WHERE  ROLEID=:ROLEID", hashMap);
				if (userMap != null && userMap.size() > 0) {
					for (int i = 0; i < userMap.size(); i++) {
						if (i == userMap.size() - 1) {

							sbf.append(userMap.get(i).getString("USERID"));
						} else {
							sbf.append(userMap.get(i).getString("USERID") + " ");
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(conn);
		}
		return sbf.toString().trim();

	}

}
