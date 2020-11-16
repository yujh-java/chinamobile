package com.actionsoft.apps.cmcc.standardization.event;

import java.sql.Connection;
import com.actionsoft.apps.cmcc.standardization.constant.StandardizationConstant;
import com.actionsoft.apps.cmcc.standardization.util.UrlUtil;
import com.actionsoft.bpms.bpmn.engine.cache.util.UserTaskDefUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

import net.sf.json.JSONObject;

/** 
* @author yujh
* @version 创建时间：2018年12月24日 上午11:36:29 
* 任务生成后，回写状态（close起草）
*/
public class StandardAfterCompleteEvent_Close extends ExecuteListener implements ExecuteListenerInterface{
	public String getDescription() {
		return "任务生成后，回写状态（close起草）";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		int taskState = param.getTaskInstance().getState();
		String processDefId = param.getTaskInstance().getProcessDefId();
		String activityDefId = param.getTaskInstance().getActivityDefId();
		if(taskState == 1 || taskState == 4 || taskState==11){
			String process_id = param.getProcessInstance().getId();
			Connection conn = DBSql.open();
			try{
				String dateStr = DBSql.getString(conn,"SELECT PROSUBTIME FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",new Object[]{process_id});
				if(!UtilString.isEmpty(dateStr)){
					String task_id = param.getTaskInstance().getId();//任务ID
					String status = "close";//结束
					String url = SDK.getAppAPI().getProperty(StandardizationConstant.APPID,"STANDARD_WORKFLOW_INFO");
					StringBuilder sb = new StringBuilder(url);
					long submittime = System.currentTimeMillis();//时间戳
					String type = DBSql.getString("SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?",
							new Object[] { processDefId });// 查询流程类型
					String activityName = "";
					UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefId, activityDefId);
					if(taskModel != null ){
						activityName = taskModel.name;
					}//节点名称
					sb.append("?process_id=");
					sb.append(process_id);
					sb.append("&task_id=");
					sb.append(task_id);
					sb.append("&status=");
					sb.append(status);
					sb.append("&submittime=");
					sb.append(submittime);
					sb.append("&type=");
					sb.append(type);
					sb.append("&statename=");
					sb.append(activityName);
					String str = UrlUtil.get(sb.toString());
					try{
						JSONObject resultJson = JSONObject.fromObject(str);
						JSONObject datajson  = resultJson.getJSONObject("data");
						int code = (Integer)datajson.get("code");
						String msg = datajson.getString("msg");
						if(code != 1){ 
							SDK.getLogAPI().getLogger(this.getClass()).error("标准化管理接口状态回写接口失败,流程实例ID:"+process_id+";任务ID:"+task_id+";状态:"+status);
							SDK.getLogAPI().getLogger(this.getClass()).error("errormsg:"+msg);
						}
					}catch(Exception e){
						SDK.getLogAPI().getLogger(this.getClass()).error("标准化管理接口状态回写接口失败,流程实例ID:"+process_id+";任务ID:"+task_id+";状态:"+status);
					}
				}
			}catch(Exception e){
				e.printStackTrace(System.err);
			}finally{
				DBSql.close(conn);
			}
		}
	}
}
