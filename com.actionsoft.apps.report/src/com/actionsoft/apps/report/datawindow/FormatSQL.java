package com.actionsoft.apps.report.datawindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.actionsoft.apps.report.ReportConst;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.dw.design.event.DataWindowFormatSQLEventInterface;
import com.actionsoft.bpms.dw.exec.component.DataView;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;

/**
 * 格式化sql语句的触发器 (权限控制)
 * 
 * @author zhaoxs
 * @date 2017-09-26
 *
 */
public class FormatSQL implements DataWindowFormatSQLEventInterface {

	@Override
	public String formatSQL(UserContext me, DataView view, String sql) {
		String userid = me.getUID();
		String roleid = me.getRoleModel().getId();
		ArrayList<String> roleidArray = new ArrayList<String>();// 当前用户所有角色ID
		ArrayList<String> typeArray = new ArrayList<String>();
		roleidArray.add(roleid);
		// 获取当前用户的所有角色ID
		List<RowMap> roleList = DBSql.getMaps("SELECT ROLEID FROM ORGUSERMAP WHERE USERID= ?", new Object[] { userid });
		if (roleList != null && roleList.size() > 0) {
			for (int i = 0; i < roleList.size(); i++) {
				String id = roleList.get(i).getString("ROLEID");
				roleidArray.add(id);
			}
		}
		String upperNoteId = null;
		Map<String, Object> sqlParam = view.getDatagrid().getSqlParams();// 获取查询条件以及查询条件的值
		if (sqlParam.toString().contains("ACTIVITYDEFID")) { // 判断查询条件中是否有节点ID属性
			String noteid = (String) sqlParam.get("ACTIVITYDEFID");// 判断是否是子流程节点
			if (!UtilString.isEmpty(noteid)) {
				upperNoteId = noteid.toLowerCase();
			}
		}
		String adminRoleID = DBSql.getString("SELECT ID FROM ORGROLE WHERE ROLENAME=?", new Object[] { "系统管理员" });
		if (roleidArray.contains(adminRoleID)) {
			sql = sql.replace("1=1", "1=1");
			// 当选择的是子流程节点时，更换查询条件
			if (ReportConst.sub_Activityid.contains(upperNoteId) && (!UtilString.isEmpty(upperNoteId))) {
				String subsql = sql.replace("UPPER(ACTIVITYDEFID) like:ACTIVITYDEFID",
						"UPPER(ACTIVITYDEFID) IN (SELECT NOTEID FROM BO_ACT_NOTEID_DATA WHERE CREATEACTIVITYID = '"
								+ upperNoteId + "')");
				return subsql;
			}
			return sql;
		}
		// 获取相应的流程类型
		for (int i = 0; i < roleidArray.size(); i++) {
			List<RowMap> rlist = DBSql.getMaps("SELECT *  FROM BO_ACT_REPORT_LIMIT WHERE ROLEID=?",
					new Object[] { roleidArray.get(i) });

			if (rlist != null && rlist.size() > 0) {
				for (int k = 0; k < rlist.size(); k++) {
					String type = rlist.get(k).getString("PROCESSNAME");
					String[] types = type.split(",");
					if (types != null && types.length > 0) {
						for (int j = 0; j < types.length; j++) {
							if (!typeArray.contains(types[j]))
								typeArray.add(types[j]);
						}
					}

				}
			}
		}

		StringBuffer sbf = new StringBuffer();
		StringBuffer sf = new StringBuffer();
		// 拼接替换sql
		if (typeArray != null && typeArray.size() > 0) {
			for (int s = 0; s < typeArray.size(); s++) {
				if (s == 0) {
					sbf.append("PROCESSNAME ='" + typeArray.get(s) + "'");
				} else {
					sbf.append("OR PROCESSNAME ='" + typeArray.get(s) + "'");
				}
			}
			sql = sql.replace("1=1", sf.append("(").append(sbf.toString()).append(")"));
		} else {
			sql = sql.replace("1=1", "PROCESSNAME=1");
		}

		if (ReportConst.sub_Activityid.contains(upperNoteId) && (!UtilString.isEmpty(upperNoteId))) {
			String subsql = sql.replace("UPPER(ACTIVITYDEFID) like:ACTIVITYDEFID",
					"UPPER(ACTIVITYDEFID) IN (SELECT NOTEID FROM BO_ACT_NOTEID_DATA WHERE CREATEACTIVITYID = '"
							+ upperNoteId + "')");
			return subsql;
		}
		return sql;
	}

}
