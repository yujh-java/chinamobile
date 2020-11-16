package com.actionsoft.apps.report.datawindow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import com.actionsoft.apps.report.util.ProcessUtil;
import com.actionsoft.bpms.dw.design.event.DataWindowFormatDataEventInterface;
import com.actionsoft.bpms.dw.exec.component.Column;
import com.actionsoft.bpms.dw.exec.data.DataSourceEngine;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 流程查询表格数据格式化
 * 
 * @author zhaoxs
 * @date 2017-09-10
 *
 */
public class ProcessQueryDataWindowFormatEvent implements DataWindowFormatDataEventInterface {
	public void formatData(UserContext usercontext, JSONArray datas) {
		for (Object datao : datas) {
			JSONObject data = (JSONObject) datao;
			Connection conn = null;
			try {

				conn = DBSql.open();
				String processDefid = data.getString("PROCESSDEFID");// 流程定义ID
				String createTime = data.getString("CREATETIME");// 流程创建时间
				String processEndTime = data.getString("ENDTIME");// 流程结束时间
				String processid = data.getString("PROCESSINSTID");// 流程实例ID
				String activityId = data.getString("ACTIVITYDEFID");// 节点ID
				data.put("TIMES" + DataSourceEngine.AWS_DW_FIXED_CLOMUN_SHOW_RULE_SUFFIX,
						ProcessUtil.getCostTime(createTime, processEndTime));
				data.put("ACTIVITYDEFID" + DataSourceEngine.AWS_DW_FIXED_CLOMUN_SHOW_RULE_SUFFIX,
						ProcessUtil.getActivityName(activityId, processid, processDefid));
				data.put("PERSON" + DataSourceEngine.AWS_DW_FIXED_CLOMUN_SHOW_RULE_SUFFIX,
						ProcessUtil.getPerson(processid));
				data.put("NOTEID" + DataSourceEngine.AWS_DW_FIXED_CLOMUN_SHOW_RULE_SUFFIX,
						ProcessUtil.getNoteName(processid, activityId, processDefid));
			} catch (Exception e) {
				e.printStackTrace(System.err);
			} finally {
				DBSql.close(conn);
			}

		}

	}

	/**
	 * 格式化导出数据
	 * 
	 * @param me
	 *            用户session
	 * @param rs
	 *            数据库结果集
	 * @param colModel
	 *            字段的相关配置模型
	 * @param fieldId
	 *            字段名
	 * @return null 时不执行
	 * @throws SQLException
	 * @throws ParseException
	 */
	public String formatGridExport(UserContext me, ResultSet rs, Column colModel, String fieldId)
			throws SQLException, ParseException {

		if (fieldId.equals("ACTIVITYDEFID")) { // 如果名字是ACTIVITYDEFID
			String activityId = rs.getString("ACTIVITYDEFID");// 节点ID
			String processDefid = rs.getString("PROCESSDEFID");// 流程定义ID
			String processid = rs.getString("PROCESSINSTID");
			;// 流程实例ID PROCESSINSTID
			return ProcessUtil.getActivityName(activityId, processid, processDefid);
		}
		// TIMES 处理时长 NOTEID上一节点 PERSON 经办人
		if (fieldId.equals("TIMES")) { // 如果名字是TIMES

			String createTime = rs.getString("CREATETIME");// 流程创建时间
			String processEndTime = rs.getString("ENDTIME");// 流程结束时间
			return ProcessUtil.getCostTime(createTime, processEndTime);
		}

		if (fieldId.equals("NOTEID")) { // 如果名字是NOTEID
			String activityId = rs.getString("ACTIVITYDEFID");// 节点ID
			String processDefid = rs.getString("PROCESSDEFID");// 流程定义ID
			String processid = rs.getString("PROCESSINSTID");
			;// 流程实例ID PROCESSINSTID
			return ProcessUtil.getNoteName(processid, activityId, processDefid);
		}

		if (fieldId.equals("PERSON")) { // 如果名字是PERSON
			String processid = rs.getString("PROCESSINSTID");
			;// 流程实例ID PROCESSINSTID
			return ProcessUtil.getPerson(processid);
		}

		return null;

	}

}
