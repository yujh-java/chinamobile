package com.actionsoft.apps.cmcc.testmanagement.event;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.apps.cmcc.testmanagement.util.UrlUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

import jodd.util.URLDecoder;
import net.sf.json.JSONObject;
/**
 * @author yujh
 * @version 创建时间：2018年12月24日 上午11:32:52 类说明
 * 任务生成后，回写状态（pending审批中）
 */
public class TaskAfterCompleteEvent_Pending extends ExecuteListener implements ExecuteListenerInterface {
	public String getDescription() {
		return "任务生成后，回写状态（pending审批中）";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		int taskState = param.getTaskInstance().getState();
		if (taskState == 1 || taskState == 4 || taskState == 11) {
			String process_id = param.getProcessInstance().getId();
			Connection conn = DBSql.open();
			try {
				String dateStr = DBSql.getString(conn,
						"SELECT PROSUBTIME FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
						new Object[] { process_id });
				if (!UtilString.isEmpty(dateStr)) {
					String task_id = param.getTaskInstance().getId();// 任务ID
					String status = "pending";// 审批中
					String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.testmanagement","TSM_WORKFLOW_INFO");
					StringBuilder sb = new StringBuilder(url);
					long dayTime = System.currentTimeMillis();//时间戳
					sb.append("?process_id=");
					sb.append(process_id);
					sb.append("&task_id=");
					sb.append(task_id);
					sb.append("&status=");
					sb.append(status);
					sb.append("&dayTime=");
					sb.append(dayTime);
					String str = UrlUtil.get(sb.toString());
					try {
						JSONObject resultJson = JSONObject.fromObject(str);
						JSONObject datajson = resultJson.getJSONObject("data");
						int code = (Integer) datajson.get("code");
						String msg = datajson.getString("msg");
						//回写项目信息至流程扩展字段当中
						JSONObject dataInfo = datajson.getJSONObject("data");
						String projectName= URLDecoder.decode(dataInfo.getString("taskName"));
						String sql="UPDATE BO_ACT_CMCC_PROCESSDATA SET PROJECTNAME=? WHERE PROCESSID=?";
						DBSql.update(conn, sql ,new Object[]{projectName,process_id});
						//修改流程标题
						String reportName= URLDecoder.decode(dataInfo.getString("reportName"));
						if(null!=reportName && !reportName.isEmpty() && !"null".equals(reportName)){
							SDK.getProcessAPI().setTitle(process_id, reportName);
						}
						if (code != 1) {
							SDK.getLogAPI().getLogger(this.getClass()).error(
									"测试管理接口状态回写接口失败,流程实例ID:" + process_id + ";任务ID:" + task_id + ";状态:" + status);
							SDK.getLogAPI().getLogger(this.getClass()).error("errormsg:" + msg);
						}else{
							SDK.getLogAPI().getLogger(this.getClass()).error(">>>>>测试管理接口状态回写接口成功,流程实例ID:"+process_id+";任务ID:"+task_id+";状态:"+status);
							SDK.getLogAPI().getLogger(this.getClass()).error("errormsg:"+msg);
						}
					} catch (Exception e) {
						SDK.getLogAPI().getLogger(this.getClass())
								.error("测试管理接口状态回写接口失败,流程实例ID:" + process_id + ";任务ID:" + task_id + ";状态:" + status);
					}
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			} finally {
				DBSql.close(conn);
			}
		}
	}
}
