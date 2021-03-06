package com.actionsoft.apps.cmcc.standardization.event;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.apps.cmcc.standardization.constant.StandardizationConstant;
import com.actionsoft.apps.cmcc.standardization.util.UrlUtil;
import com.actionsoft.bpms.bpmn.engine.cache.util.UserTaskDefUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.AppAPI;
/** 
* @author yujh
* @version 创建时间：2019年9月25日 上午10:30:56 
* 实现公共回写接口方法
*/
public class StateWriteBackImpl extends ExecuteListener implements ExecuteListenerInterface {
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {	
		int taskState = param.getTaskInstance().getState();
		if(taskState == 1 || taskState == 4 || taskState==11){
			String processId = param.getProcessInstance().getId();
			Connection conn = DBSql.open();
			try{
				String dateStr = DBSql.getString(conn,"SELECT PROSUBTIME FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",new Object[]{processId});
				if(!UtilString.isEmpty(dateStr)){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String url = SDK.getAppAPI().getProperty(StandardizationConstant.APPID,"STANDARD_WORKFLOW_INFO");
					String parentProcessId ="";
					String taskId = param.getTaskInstance().getId();//任务ID
					String parentTaskId = param.getTaskInstance().getParentTaskInstId();//父任务ID
					String taskBeginTime= sdf.format(param.getTaskInstance().getBeginTime());
					String userName =param.getProcessInstance().getCreateUser();
					String submitterid =  param.getTaskInstance().getOwner();
					String ownerId =  param.getTaskInstance().getTarget();
					
					String activityName = "";
					String state = "pending";
					String processDefId = param.getTaskInstance().getProcessDefId();
					String activityDefId = param.getTaskInstance().getActivityDefId();
					UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefId, activityDefId);
					if(taskModel != null ){
						activityName = taskModel.name;
					}//节点名称
					long submittime = System.currentTimeMillis();//时间戳
					String type = DBSql.getString("SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?",
							new Object[] { processDefId });// 查询流程类型
					sendWriteBackASLP(url, parentProcessId, processId, taskId, parentTaskId, taskBeginTime, userName, submitterid, ownerId, activityName, state, "", "");
					
					
				}
			}catch(Exception e){
				e.printStackTrace(System.err);
			}finally{
				DBSql.close(conn);
			}
		}
	}
	
	/**
	 * 发送回写ASLP请求
	 * @param url
	 * @param parentProcessId
	 * @param processId
	 * @param taskId
	 * @param parentTaskId
	 * @param taskBeginTime
	 * @param userName
	 * @param submitterid
	 * @param ownerId
	 * @param node
	 * @param state
	 * @param EXT1
	 * @param EXT2
	 * @return
	 */
	public String sendWriteBackASLP(String url,String parentProcessId, String processId,String taskId,
			String parentTaskId ,String taskBeginTime, String userName, String submitterid, String ownerId, 
			String node, String state, String EXT1, String EXT2){
		// 调用App 
		  String sourceAppId = StandardizationConstant.APPID;
		// aslp服务地址 
		String aslp = "aslp://com.actionsoft.apps.cmcc.integration/StateWriteBackAslp";
		// 参数定义列表  
		Map<String, Object> params = new HashMap<String, Object>();
		//回写接口地址,必填 
		params.put("url", url);
		//父流程实例ID,非必填 
		params.put("parentProcessId", parentProcessId);
		//流程实例ID,必填 
		params.put("processId", processId);
		//任务实例ID,必填 
		params.put("taskId", taskId);
		//父任务实例ID,必填 
		params.put("parentTaskId", parentTaskId);
		//任务接受时间,必填 
		params.put("taskBeginTime", taskBeginTime);
		//流程创建者姓名,非必填 
		params.put("userName", userName);
		//任务提交人,必填 
		params.put("submitterid", submitterid);
		//当前任务办理者,必填 
		params.put("ownerId", ownerId);
		//环节名称,非必填 
		params.put("node", node);
		//流程状态,必填 
		params.put("state", state);
		//流程平台任务状态,必填 
		params.put("taskState", state);
		//扩展字段一,非必填 
		params.put("EXT1", EXT1);
		//扩展字段二,非必填 
		params.put("EXT2", EXT2);
		AppAPI appAPI =  SDK.getAppAPI(); 
		//状态回写接口，支持所有流程调用 
		ResponseObject ro = appAPI.callASLP(appAPI.getAppContext(sourceAppId), aslp, params);
		return "";
	}
	
}
