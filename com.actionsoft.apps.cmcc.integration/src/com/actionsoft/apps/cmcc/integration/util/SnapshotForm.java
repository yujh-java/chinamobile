package com.actionsoft.apps.cmcc.integration.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

/**
 * 表单快照信息查询
 * @author nch
 * @date 2017-8-16
 */
public class SnapshotForm {
	/**
	 * 获取指定部门下生成的快照
	 * @author nch
	 * @date 2017-8-21
	 * @param conn
	 * @param processid
	 * @param deptid
	 * @return
	 */
	public static Map<String,String> getSnapshotHtml(Connection conn,String processid,String deptid){
		Map<String,String> map = new HashMap<String,String>();
		SDK.getProcessAPI().getInstanceById(processid).getSubInstType();
		List<ProcessInstance> list_subProcess = SDK.getProcessQueryAPI().parentProcessInstId(processid).list();
		List<String> list = new ArrayList<String>();
		list.add(processid);
		if(list_subProcess != null && list_subProcess.size() > 0){
			for(int sub = 0;sub < list_subProcess.size();sub++){
				String subProcessId = list_subProcess.get(sub).getId();
				list.add(subProcessId);
			}
		}
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("SELECT ID,PROCESSINSTID,AUTHORUID FROM WFH_FORMSNAPSHOT WHERE ("); 
		Object[] obj = new Object[list.size()];
		for(int l = 0;l < list.size();l++){
			if(l==0){
				sb_sql.append("PROCESSINSTID = ?");	
			}else{
				sb_sql.append(" OR PROCESSINSTID = ?");	
			}
			obj[l] = list.get(l);
		}
		sb_sql.append(") ORDER BY RECORDTIME DESC");
		List<RowMap> listRowMap = DBSql.getMaps(conn, sb_sql.toString(), obj);
		if(listRowMap != null && listRowMap.size() > 0){
			for(int i =0;i < listRowMap.size();i++){
				String version_id = listRowMap.get(i).getString("ID");
				String PROCESSINSTID = listRowMap.get(i).getString("PROCESSINSTID");
				String AUTHORUID = listRowMap.get(i).getString("AUTHORUID");
				if(!UtilString.isEmpty(AUTHORUID)){
					String authorDeptid = UserCache.getModel(AUTHORUID).getDepartmentId();
					String deptPathid = DepartmentCache.getModel(authorDeptid).getPathIdOfCache();
					if(deptPathid.contains(deptid)){
						map.put("version_id", version_id);
						map.put("process_id", PROCESSINSTID);
						return map;
					}
				}
			}
		}
		return null;
	}
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
	/**
	 * 判断流程是否结束
	 * @author nch
	 * @date 2017-8-21
	 * @param processId
	 * @return
	 */
	public static boolean processIsEnd(String processId){
		boolean is_end = SDK.getProcessAPI().isEndById(processId);
		return is_end;
	}
	/**
	 * 判断用户是否属于研究院
	 * @author nch
	 * @date 2017-8-21
	 * @param userid
	 * @return
	 */
	public static boolean userIsYjy(String userid){
		boolean bol = false;
		//用户所在部门全路径
		String deptid = UserCache.getModel(userid).getDepartmentId();
		String deptPathId = DepartmentCache.getModel(deptid).getPathIdOfCache();
		//研究院部门ID
		String yjy_deptid = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "YJY_DEPTID");
		if(deptPathId.contains(yjy_deptid)){
			bol = true;
		}
		return bol;
	}
}
