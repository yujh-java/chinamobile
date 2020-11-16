package com.actionsoft.apps.cmcc.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.cache.util.UserTaskDefUtil;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

/**
 * 公共处理类
 * 
 * @author sunk
 * @data 2017/05/23
 */
public class CommontUtil {

	/**
	 * 功能:查询,将结果集放入一个Hashtable<String,Object>中
	 * 
	 * @param sql:查询语句
	 * @return 根据查询语句返回对应的结果集信息
	 * @author sunk
	 * @date 2017-05-23
	 */
	public static Hashtable<String, Object> queryHT(Connection conn, String sql) {
		Hashtable<String, Object> ht = new Hashtable<String, Object>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			while (rs.next()) {
				// 遍历每一行
				for (int index = 1; index <= count; index++) {
					String columnName = rsmd.getColumnName(index);
					String type = rsmd.getColumnTypeName(index);
					String value = rs.getString(columnName);
					if (value != null) {
						// if(value.length()==21 &&
						// value.lastIndexOf(".0")==19){
						// value = DateUtil.strToFormat(value);
						// }
						if (type.equals("DATE")) {
							if (value.startsWith(" 00:00:00", 10)) {
								value = DateUtil.strToFormat(value);
							}
							if (value.length() == 21 && value.lastIndexOf(".0") == 19) {
								value = DateUtil.strToFormat(value, "yyyy-MM-dd HH:mm:ss");
							}
						}
					} else {
						value = CommontUtil.defaultValueIfNull(value);
					}
					ht.put(columnName.toUpperCase(), value);
				}
			}
		} catch (Exception e) {
			System.out.println("公共方法查询出错！");
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(stmt, rs);
		}
		return ht;
	}

	/**
	 * 功能:查询,将结果集放入一个Vector<Hashtable<String,Object>>中
	 * 
	 * @param sql:查询语句
	 * @return 根据查询语句返回对应的结果集信息
	 * @author sunk
	 * @throws IntegrationException
	 * @date 2017-05-23
	 */
	public static Vector<Hashtable<String, Object>> query(Connection conn, String sql) {
		Vector<Hashtable<String, Object>> vector = new Vector<Hashtable<String, Object>>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			while (rs.next()) {
				// 每一行语句放入一个Hashtable中
				Hashtable<String, Object> ht = new Hashtable<String, Object>();
				// 遍历每一行
				for (int index = 1; index <= count; index++) {
					String columnName = rsmd.getColumnName(index);
					String type = rsmd.getColumnTypeName(index);
					String value = rs.getString(columnName);
					if (value != null) {
						// if(value.length()==21 &&
						// value.lastIndexOf(".0")==19){
						// value = DateUtil.strToFormat(value);
						// }
						if (type.equals("DATE")) {
							if (value.startsWith(" 00:00:00", 10)) {
								value = DateUtil.strToFormat(value);
							}
						}
					} else {
						value = defaultValueIfNull(value);
					}
					ht.put(columnName.toUpperCase(), value);
				}
				vector.add(ht);
			}
		} catch (Exception e) {
			System.out.println("公共方法查询出错！" + e.getMessage());
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(stmt, rs);
		}
		return vector;
	}

	public static String defaultValueIfNull(String arg) {
		return (arg == null ? "" : arg);
	}

	public static String defaultValueIfNull(String arg, String defaultValue) {
		return (arg == null || "".equals(arg.trim()) ? defaultValue : arg);
	}

	public static boolean isEmpty(String arg) {
		return (arg == null || "".equals(arg.trim()));
	}

	public static boolean isNotEmpty(String arg) {
		return !isEmpty(arg);
	}

	/**
	 * 根据流程定义ID，判断是否是主流程
	 * 
	 * @author nch
	 * @date 2017-10-20
	 * @param processdefid
	 * @return
	 */
	public static boolean isMainProcess(String processdefid) {

		List<BO> list = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").addQuery("PROCESSDEFNID = ", processdefid)
				.addQuery("ISMAIN = ", "否").list();
		boolean bol = true;
		if (list != null && list.size() > 0) {
			bol = false;
		}
		return bol;
	}

	// 拼接传阅流程跟踪
	public static String circulatedHtml(String taskid, String processid) {
		StringBuffer sbf = new StringBuffer();
		Connection conn = null;
		try {
			conn = DBSql.open();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sql1 = "SELECT GROUP_CONCAT(a.TARGET) TARGET FROM " + "(SELECT TARGET FROM WFC_TASK  "
					+ "WHERE TASKSTATE='2' AND processinstid=? AND parenttaskinstid = ? " + "UNION "
					+ "SELECT TARGET FROM WFH_TASK "
					+ "WHERE TASKSTATE='2' AND processinstid=? AND parenttaskinstid = ?) a";
			String sql = "SELECT * FROM ("
					+ "SELECT ID, TARGET,'unread' AS STATE,BEGINTIME,NULL AS READTIME FROM WFC_TASK  "
					+ "WHERE taskstate='2' AND PROCESSINSTID=? AND PARENTTASKINSTID = ?" + "UNION"
					+ " SELECT id, TARGET,'read' AS STATE,BEGINTIME,READTIME FROM WFH_TASK "
					+ "WHERE TASKSTATE='2' AND PROCESSINSTID=? AND PARENTTASKINSTID = ?) A ORDER BY READTIME";
			List<RowMap> list = DBSql.getMaps(conn,sql, new Object[] { processid, taskid, processid, taskid });
			if (list.size() > 0 && list != null) {
				for (RowMap map : list) {
					String state = map.get("state").toString();
					String id = map.get("id").toString();
					taskid = id;
					String readtime = null;
					String name;
					if (!UtilString.isEmpty(map.getString("BEGINTIME"))) {
						try {
							readtime = sdf.format(sdf.parse(map.getString("BEGINTIME")));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					if (state.equals("unread")) {
						name = UserCache.getModel(map.getString("TARGET")).getUserName();
						sbf.append("<tr><td ><FONT color='#000000'>" + "分发阅文" + "</FONT></td>");
						sbf.append("<td ><FONT color='#000000'>" + name + "</FONT></td>");
						sbf.append("<td ><FONT color='#000000'>" + readtime + "</FONT></td>");
						sbf.append("<td ><FONT color='#000000'>" + "无" + "</FONT></td>");
						sbf.append("<td ><FONT color='#000000'>" + "无" + "</FONT></td>");
						sbf.append("<td ><FONT color='#000000'>" + "无" + "</FONT></td>");
						sbf.append(
								"</tr><tr><td colspan='6'><HR noshade style='height:1px;border-width:0;color:gray;background-color:gray' width='100%' align='right' ></td></tr>");
					} else {

						List<RowMap> cclist = DBSql.getMaps(conn,sql, new Object[] { processid, taskid, processid, taskid });
						if(cclist.size()>0&&cclist!=null){
							for(int i=0;i<cclist.size();i++){
								name = UserCache.getModel(map.getString("TARGET")).getUserName();
								String uname = DBSql.getString(conn, sql1,new Object[]{processid,taskid,processid,taskid});
								String[]  names = uname.split(",");
								StringBuffer namesbf = new StringBuffer();
								if(names!=null&&names.length>0){
									for(int n=0;n<names.length;n++){
										String sname = UserCache.getModel(names[n]).getUserName();
										if(n==0){
											namesbf.append(sname);
										}else{
											namesbf.append(",");
											namesbf.append(sname);
										}

									}
								}
								sbf.append("<tr><td ><FONT color='#000000'>" + "分发阅文" + "</FONT></td>");
								sbf.append("<td ><FONT color='#000000'>" + name + "</FONT></td>");
								sbf.append("<td ><FONT color='#000000'>" + sdf.format(map.getDate("BEGINTIME"))+ "</FONT></td>");
								sbf.append("<td ><FONT color='#000000'>" + sdf.format(map.getDate("READTIME")) + "</FONT></td>");
								sbf.append("<td ><FONT color='#000000'>" + "分发阅文" + "</FONT></td>");
								sbf.append("<td ><FONT color='#000000'>" + namesbf.toString() + "</FONT></td>");
								sbf.append(
										"</tr><tr><td colspan='6'><HR noshade style='height:1px;border-width:0;color:gray;background-color:gray' width='100%' align='right' ></td></tr>");
							}
							sbf.append(circulatedHtml(taskid, processid));	
						}else{
							List<RowMap> hlist = DBSql.getMaps(conn,"SELECT * FROM WFH_TASK WHERE ID=?",new Object[]{taskid});
							name = UserCache.getModel(hlist.get(0).getString("TARGET")).getUserName();
							sbf.append("<tr><td ><FONT color='#000000'>" + "分发阅文" + "</FONT></td>");
							sbf.append("<td ><FONT color='#000000'>" + name + "</FONT></td>");
							sbf.append("<td ><FONT color='#000000'>" + sdf.format(hlist.get(0).getDate("BEGINTIME"))+ "</FONT></td>");
							sbf.append("<td ><FONT color='#000000'>" + sdf.format(hlist.get(0).getDate("READTIME")) + "</FONT></td>");
							sbf.append("<td ><FONT color='#000000'>" + "提交结束" + "</FONT></td>");
							sbf.append("<td ><FONT color='#000000'>" + "无" + "</FONT></td>");
							sbf.append(
									"</tr><tr><td colspan='6'><HR noshade style='height:1px;border-width:0;color:gray;background-color:gray' width='100%' align='right' ></td></tr>");
						}
					}

				}

			} else {

				/*List<RowMap> hlist = DBSql.getMaps(conn,"SELECT * FROM WFH_TASK WHERE ID=?",new Object[]{taskid});
				String username = UserCache.getModel(hlist.get(0).getString("TARGET")).getUserName();
				sbf.append("<tr><td ><FONT color='#000000'>" + "分发阅文" + "</FONT></td>");
				sbf.append("<td ><FONT color='#000000'>" + username + "</FONT></td>");
				sbf.append("<td ><FONT color='#000000'>" + sdf.format(hlist.get(0).getDate("BEGINTIME"))+ "</FONT></td>");
				sbf.append("<td ><FONT color='#000000'>" + sdf.format(hlist.get(0).getDate("READTIME")) + "</FONT></td>");
				sbf.append("<td ><FONT color='#000000'>" + "提交结束" + "</FONT></td>");
				sbf.append("<td ><FONT color='#000000'>" + "无" + "</FONT></td>");
				sbf.append(
						"</tr><tr><td colspan='6'><HR noshade style='height:1px;border-width:0;color:gray;background-color:gray' width='100%' align='right' ></td></tr>");*/

			}

		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(conn);
		}
		return sbf.toString();	
	}
	
	public static String getNoteName(int taskState,String parentTaskID,String task_title,String activityId,String processDefid){
		String activityName = "";
		UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefid, activityId);
		if(taskModel != null ){
			activityName = taskModel.name;
		}//节点名称

		if(taskState == 11){
			//任务为加签或阅办类任务
			//父任务状态
			TaskInstance parent_taskinstance = SDK.getTaskAPI().getInstanceById(parentTaskID);
			if(parent_taskinstance != null){
				List<BO> list_processStep = SDK.getBOAPI().query("BO_ACT_EXBUTTON_MEMO_DATA").addQuery("PROCESSACTIVITYID = ", activityId).list();
				if(list_processStep != null && list_processStep.size() > 0){
					int parent_TaskState =  parent_taskinstance.getState();//父任务状态
					String parent_TaskTitle = parent_taskinstance.getTitle();//父任务任务标题
					if(parent_TaskState!=11){
						if(!UtilString.isEmpty(task_title) && task_title.contains("(加签)")){
							activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME1");
						}else if(!UtilString.isEmpty(task_title) && task_title.contains("(阅办)")){
							activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME1");
						}else if(!UtilString.isEmpty(task_title) && task_title.contains("(转办)")
								&& !UtilString.isEmpty(parent_TaskState) && task_title.contains("(加签)")){
							activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME1");
						}else if(!UtilString.isEmpty(task_title) && task_title.contains("(转办)")
								&& !UtilString.isEmpty(parent_TaskState) && task_title.contains("(阅办)")){
							activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME1");
						}
					}else{
						if(!UtilString.isEmpty(parent_TaskTitle) && !UtilString.isEmpty(task_title)){
							if(parent_TaskTitle.contains("(加签)")
									&&  task_title.contains("(加签)")){
								activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME2");
							}else if(parent_TaskTitle.contains("(阅办)")
									&&  task_title.contains("(阅办)")){
								activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME2");
							}else if(parent_TaskTitle.contains("(加签)")
									&&  task_title.contains("(阅办)")){
								activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME1");
							}else if(parent_TaskTitle.contains("(阅办)")
									&&  task_title.contains("(加签)")){
								activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME1");
							}else if(parent_TaskTitle.contains("(阅办)")
									&&  task_title.contains("(转办)")){
								activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME2");
							}else if(parent_TaskTitle.contains("(加签)")
									&&  task_title.contains("(转办)")){
								activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME2");
							}
						}
					}
				}

			}
		}
		return activityName;
	}
}
