package com.actionsoft.apps.cmcc.budget.statusImp;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/**
 * close状态信息回写。公共事件
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.apps.cmcc.budget.util.CmccCommon;
import com.actionsoft.apps.cmcc.budget.util.CmccUrlUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class CloseStates_TaskAfterComplete_stepEnd extends ExecuteListener implements
ExecuteListenerInterface {
	public String getDescription() {
		return "close状态信息回写";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		String process_id = param.getProcessInstance().getId();
		String process_defid = param.getProcessInstance().getProcessDefId();//流程定义ID
		Connection conn = DBSql.open();
		try{
			String dateStr = DBSql.getString(conn,"SELECT PROSUBTIME FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ? ORDER BY PROSUBTIME DESC",new Object[]{process_id});
			if(!UtilString.isEmpty(dateStr)){
				String activityId = param.getTaskInstance().getActivityDefId();//节点id
				String parentTaskID = param.getTaskInstance().getParentTaskInstId();//父类任务实例id
				String task_title = param.getTaskInstance().getTitle();//任务标题
				String processDefid = param.getProcessDef().getId();//流程定义id
				String taskId =param.getTaskInstance().getId();//任务ID
				int taskState = param.getTaskInstance().getState();//任务状态
				String submitterid = param.getTaskInstance().getTarget();
				String parentProcessId = "";//wxx
				parentProcessId = SDK.getProcessAPI().getInstanceById(process_id).getParentProcessInstId();//父流程实例ID
				String statename = CmccCommon.getNoteName(taskState, parentTaskID, task_title, activityId, processDefid);
				String userName = param.getProcessInstance().getCreateUser();//流程创建者
				String processType = DBSql.getString(conn,"SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ? ", new Object[]{process_defid});
				String title = param.getProcessInstance().getTitle();//流程标题
				Map<String,Object> mapBindid = new HashMap<String,Object>();
				mapBindid.put("PROCESSINSTID", process_id);
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String closetime = sdf.format(date);
				
				//调取应用需求部门ID的接口URL
			    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.budget", "BACKDATAINTERFACE");
			    
			    StringBuffer sb = new StringBuffer(url);
			    /*
			     * userName:提交人
			     * ownerId：待办人
			     * node:节点
			     */
			    
			    sb.append("processId=");
			    sb.append(process_id);
			    sb.append("&parentProcessId=");//父流程实例ID
			    sb.append(parentProcessId);
			    sb.append("&parentTaskId=");//父任务实例ID
			    sb.append(taskId);
			    sb.append("&state=3");
			    sb.append("&taskBeginTime=");//任务开始时间
			    try {
			    	closetime= URLEncoder.encode(closetime, "UTF-8");
			    	closetime = closetime.replace("+", "%20");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    sb.append(closetime);
			    sb.append("&userName=");
			    sb.append(userName);
			    sb.append("&submitterid=");
			    sb.append(submitterid);
			    sb.append("&ownerId=");
			    sb.append(submitterid);
			    sb.append("&taskState=");//任务状态
			    sb.append(taskState);
			    System.err.println("======url:"+sb.toString()+"=====");
			    //http://10.2.5.92:8082/bms/updatefinanceprocess.shtml?processId=bcf062d2-df75-4bca-8d20-abe1bb1e4df6&state=3&taskBeginTime=2019-03-26%2017%3A24%3A52&userName=yinxiaoqian@hq.cmcc&taskState=1=====
			 	//获取返回值
				String json = CmccUrlUtil.get(sb.toString());
//				// 调用App 
//				String sourceAppId = CmccConst.sourceAppId;
//				// aslp服务地址 
//				String aslp = CmccConst.stateAslp;
				// 参数定义列表  
//				Map<String, Object> params = new HashMap<String, Object>();
//				Map<String,String> map  = new HashMap<String,String>();
//				map.put("process_id", process_id);
//				map.put("task_id", task_id);
//				map.put("status", "close");
//				map.put("type", processType);
//				map.put("title", title);
//				map.put("statename",statename);
//				map.put("submitterid", submitterid);
//				map.put("ownerids", "");
//				map.put("submittime", dateStr);
//				map.put("closetime", closetime);
//				map.put("passedtime", "");
//				map.put("iscancelworkflow", "false");
//				map.put("providetype", "AWS");
//				//流程状态信息,非必填 
//				params.put("mapMsg", map);
//
//				//调用spms状态回写接口 
//				ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, params);
			}
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
	}

}
