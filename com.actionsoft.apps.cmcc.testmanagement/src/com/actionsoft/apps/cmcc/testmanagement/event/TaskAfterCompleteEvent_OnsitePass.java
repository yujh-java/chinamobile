package com.actionsoft.apps.cmcc.testmanagement.event;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.apps.cmcc.testmanagement.constant.TestManagementConst;
import com.actionsoft.apps.cmcc.testmanagement.util.UrlUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

import jodd.util.URLDecoder;
import net.sf.json.JSONObject;
/** 
* @author yujh 
* @version 创建时间：2018年12月24日 上午10:45:02 
* 任务生成后，回写状态（现场检查以及文档检查流程专用：pass起草，nopass退回起草,flag=1为现场测试流程，flag=3为文档检查流程）
*/
public class TaskAfterCompleteEvent_OnsitePass extends ExecuteListener implements ExecuteListenerInterface{
	public String getDescription() {
		return "任务生成后，回写状态（现场检查以及文档检查流程专用：pass起草，nopass退回起草,flag=1为现场测试流程，flag=3为文档检查流程）";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		int taskState = param.getTaskInstance().getState();
		if(taskState == 1 || taskState == 4 || taskState==11){
			String process_id = param.getProcessInstance().getId();
			Connection conn = DBSql.open();
			try{
				String prosubTime = DBSql.getString(conn,"SELECT PROSUBTIME FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",new Object[]{process_id});
				if(!prosubTime.isEmpty()){
					String task_id = param.getTaskInstance().getId();//任务ID
					String status = "nopass";//退回起草
					String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.testmanagement", "TSM_WORKFLOW_INFO");
					StringBuilder sb = new StringBuilder(url);
					long dayTime = System.currentTimeMillis();//时间戳
					String parentTaskInstId = param.getTaskInstance().getParentTaskInstId();//父任务ID
					System.err.println(">>>>>parentTaskInstId:"+parentTaskInstId);
					String activityDefId ="";
					if(!parentTaskInstId.isEmpty() && !parentTaskInstId.equals("00000000-0000-0000-0000-000000000000")){
						activityDefId = SDK.getTaskAPI().getInstanceById(parentTaskInstId).getActivityDefId();//父任务节点
					}
					sb.append("?process_id=");
					sb.append(process_id);
					sb.append("&task_id=");
					sb.append(task_id);
					sb.append("&status=");
					sb.append(status);
					sb.append("&dayTime=");
					sb.append(dayTime);
					if(activityDefId.equals(TestManagementConst.CSJL_STEP_ID)){
						sb.append("&flag=1");
					}else if(activityDefId.equals(TestManagementConst.WDJL_STEP_ID)){
						sb.append("&flag=3");
					}
					String str = UrlUtil.get(sb.toString());
					try{
						JSONObject resultJson = JSONObject.fromObject(str);
						JSONObject datajson  = resultJson.getJSONObject("data");
						int code = (Integer)datajson.get("code");
						String msg = datajson.getString("msg");
						//回写项目信息至流程扩展字段当中
						JSONObject dataInfo = datajson.getJSONObject("data");
						String projectName= URLDecoder.decode(dataInfo.getString("taskName"));
						if(code != 1){
							SDK.getLogAPI().getLogger(this.getClass()).error(">>>>>>测试管理接口状态回写接口失败,流程实例ID:"+process_id+";任务ID:"+task_id+";状态:"+status);
							SDK.getLogAPI().getLogger(this.getClass()).error("errormsg:"+msg);
						}else{
							SDK.getLogAPI().getLogger(this.getClass()).error(">>>>>测试管理接口状态回写接口成功,流程实例ID:"+process_id+";任务ID:"+task_id+";状态:"+status);
							SDK.getLogAPI().getLogger(this.getClass()).error("errormsg:"+msg);
						}
					}catch(Exception e){
						SDK.getLogAPI().getLogger(this.getClass()).error(">>>>测试管理接口状态回写接口失败,流程实例ID:"+process_id+";任务ID:"+task_id+";状态:"+status);
					}
				}else{
					String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.testmanagement", "TSM_WORKFLOW_INFO");
					String task_id = param.getTaskInstance().getId();//任务ID
					String status = "pass";//起草
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
					try{
						JSONObject resultJson = JSONObject.fromObject(str);
						JSONObject datajson  = resultJson.getJSONObject("data");
						int code = (Integer)datajson.get("code");
						String msg = datajson.getString("msg");
						//回写项目信息至流程扩展字段当中
						JSONObject dataInfo = datajson.getJSONObject("data");
						if(code != 1){
							SDK.getLogAPI().getLogger(this.getClass()).error(">>>>>>测试管理接口状态回写接口失败,流程实例ID:"+process_id+";任务ID:"+task_id+";状态:"+status);
							SDK.getLogAPI().getLogger(this.getClass()).error("errormsg:"+msg);
						}else{
							SDK.getLogAPI().getLogger(this.getClass()).error(">>>>>测试管理接口状态回写接口成功,流程实例ID:"+process_id+";任务ID:"+task_id+";状态:"+status);
							SDK.getLogAPI().getLogger(this.getClass()).error("errormsg:"+msg);
						}
					}catch(Exception e){
						SDK.getLogAPI().getLogger(this.getClass()).error(">>>>测试管理接口状态回写接口异常,流程实例ID:"+process_id+";任务ID:"+task_id+";状态:"+status);
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
