package com.actionsoft.apps.cmcc.util;

import java.sql.Connection;
import java.util.List;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;

/**
 * 表单快照信息查询
 * @author nch
 * @date 2017-8-16
 */
public class SnapshotForm {
	/**
	 * 根据流程实例ID，获得最大版本号（生成时间最大）
	 * @author nch
	 * @date 2017-8-16
	 * @param conn
	 * @param processid 流程实例ID
	 * @return
	 */
	public static String getSnapshotEdition(Connection conn,String processid){
		String sql = "SELECT ID FROM WFH_FORMSNAPSHOT WHERE PROCESSINSTID = ? ORDER BY RECORDTIME DESC";
		List<RowMap> listRowMap = DBSql.getMaps(conn, sql, new Object[]{processid});
		if(listRowMap != null && listRowMap.size() > 0){
			String version_id = listRowMap.get(0).getString("ID");
			return version_id;
		}
		return "";
	}
}
