/**
 * @author zhaoxs
 * @date 2017年8月30日下午2:52:27
 */
package com.actionsoft.apps.report.datawindow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.actionsoft.apps.report.Util.ProcessUtil;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.dw.design.event.DataWindowFormatDataEventInterface;
import com.actionsoft.bpms.dw.exec.component.Column;
import com.actionsoft.bpms.dw.exec.data.DataSourceEngine;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author w13021111134 任务查询表格数据格式化
 *
 */
public class TaskQueryDataWindowFormatEvent implements DataWindowFormatDataEventInterface {
	@Override
	public void formatData(UserContext usercontext, JSONArray datas) {
		for (Object datao : datas) {
			JSONObject data = (JSONObject) datao;
			Connection conn = null;
			try {
				conn = DBSql.open();
				// 格式化上一办理人为用户姓名
				String owner = data.getString("OWNER");
				String ownername = DBSql.getString(conn, "SELECT USERNAME FROM ORGUSER  WHERE USERID = ?",
						new Object[] { owner });
				// 节点ID，流程定义ID
				String activityId = data.getString("ACTIVITYDEFID");
				String processDefid = data.getString("PROCESSDEFID");

				// 任务实例ID
				String taskid = data.getString("ID");
				// 任务结束时间
				String endtime = data.getString("ENDTIME");
				String begintime = data.getString("BEGINTIME");
				String processid = data.getString("PROCESSINSTID");

				String times;
				List<RowMap> ctask = null;
				if (!UtilString.isEmpty(endtime)) { // 已办
					/*
					 * long costtime = data.getLongValue("COSTTTIME"); times =
					 * costtime / (24 * 60 * 60 * 1000 * 1.0);
					 */
					times = ProcessUtil.getCostTime(begintime, endtime);
					// 获取已办任务信息
					ctask = DBSql.getMaps(conn, "SELECT * FROM WFH_TASK WHERE ID=?", new Object[] { taskid });
				} else {// 待办
					// 获取待办任务信息
					ctask = DBSql.getMaps(conn, "SELECT * FROM WFC_TASK WHERE ID=?", new Object[] { taskid });
					// 计算耗时（待办：当前时间到任务接受时间间隔，已办：任务办理所耗时间）
					/*
					 * String Begintime = data.getString("BEGINTIME"); if
					 * (!UtilString.isEmpty(Begintime)) { SimpleDateFormat sdf =
					 * new SimpleDateFormat("yyyy-MM-dd "); Date date =
					 * sdf.parse(Begintime); long ts = date.getTime(); long
					 * nowtime = new Date().getTime(); times = (nowtime - ts) *
					 * 1.0 / (24 * 60 * 60 * 1000);// 将结果转换为天 }
					 */
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					times = ProcessUtil.getCostTime(begintime, sdf.format(date));

				}
				String activityName = ProcessUtil.getNoteIdName(processDefid, activityId);
				data.put("OWNER" + DataSourceEngine.AWS_DW_FIXED_CLOMUN_SHOW_RULE_SUFFIX, ownername);
				data.put("ACTIVITYDEFID" + DataSourceEngine.AWS_DW_FIXED_CLOMUN_SHOW_RULE_SUFFIX, activityName);
				data.put("NOTEID" + DataSourceEngine.AWS_DW_FIXED_CLOMUN_SHOW_RULE_SUFFIX,
						ProcessUtil.getPreviousNoteName(ctask, processid, endtime));
				data.put("COSTTTIME" + DataSourceEngine.AWS_DW_FIXED_CLOMUN_SHOW_RULE_SUFFIX, times);

			} catch (Exception e) {
				e.printStackTrace(System.err);
			} finally {
				DBSql.close(conn);
			}

		}

	}

	public String formatGridExport(UserContext me, ResultSet rs, Column colModel, String fieldId)
			throws ParseException {
		List<RowMap> ctask = null;
		Connection conn = null;
		try {
			conn = DBSql.open();
			if (fieldId.equals("OWNER")) { // 如果名字是OWNER
				String owner = rs.getString("OWNER");
				String ownername = DBSql.getString(conn, "SELECT USERNAME FROM ORGUSER  WHERE USERID = ?",
						new Object[] { owner });
				return ownername;
			}

			if (fieldId.equals("ACTIVITYDEFID")) { // 如果名字是ACTIVITYDEFID
				String activityId = rs.getString("ACTIVITYDEFID");
				String processDefid = rs.getString("PROCESSDEFID");
				return ProcessUtil.getNoteIdName(processDefid, activityId);
			}

			if (fieldId.equals("NOTEID")) { // 如果名字是ACTIVITYDEFID
				String endtime = rs.getString("ENDTIME");// 任务结束时间
				String processid = rs.getString("PROCESSINSTID");// 流程实例ID
				String taskid = rs.getString("ID");// 任务实例id
				if (!UtilString.isEmpty(endtime)) { // 已办
					ctask = DBSql.getMaps(conn, "SELECT * FROM WFH_TASK WHERE ID=?", new Object[] { taskid });
				} else {// 待办
					// 获取待办任务信息
					ctask = DBSql.getMaps(conn, "SELECT * FROM WFC_TASK WHERE ID=?", new Object[] { taskid });
				}
				return ProcessUtil.getPreviousNoteName(ctask, processid, endtime);
			}

			if (fieldId.equals("COSTTTIME")) { // 如果名字是COSTTTIME
				String endtime = rs.getString("ENDTIME");// 任务结束时间
				String begintime = rs.getString("BEGINTIME");// 任务开始时间
				if (!UtilString.isEmpty(endtime)) {
					return ProcessUtil.getCostTime(begintime, endtime);
				} else {
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					return ProcessUtil.getCostTime(begintime, sdf.format(date));
				}

			}
			if (fieldId.equals("STATE")) { // 如果名字是COSTTTIME
				return rs.getString("STATE");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBSql.close(conn);
		}
		return null;
	}

}
