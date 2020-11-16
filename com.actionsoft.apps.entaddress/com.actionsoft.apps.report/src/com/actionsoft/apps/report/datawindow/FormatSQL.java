/**
 * @author zhaoxs
 * @date 2017年9月1日上午11:44:21
 */
package com.actionsoft.apps.report.datawindow;
import com.actionsoft.bpms.dw.design.event.DataWindowFormatSQLEventInterface;
import com.actionsoft.bpms.dw.exec.component.DataView;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;

/**
 * @author w13021111134
 *
 */
public class FormatSQL implements DataWindowFormatSQLEventInterface {

	@Override
	public String formatSQL(UserContext me, DataView view, String sql) {
		String roleid = me.getRoleModel().getId();
		String rolename = DBSql.getString("SELECT ROLENAME FROM ORGROLE WHERE ID=?",new Object[]{roleid});
		String str = "项目评价打分";
		if (!rolename.equals("系统管理员")) { 
			sql = sql.replace("1=1", "流程类型 = '" + str + "'"); 
		    } 
		
		return sql;
		  } 

	

}
