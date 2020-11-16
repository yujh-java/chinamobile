package com.actionsoft.apps.cmcc.statusImp;
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

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.apps.cmcc.util.CommontUtil;
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
			String dateStr = DBSql.getString(conn,"SELECT PROSUBTIME FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",new Object[]{process_id});
			if(!UtilString.isEmpty(dateStr)){
				String task_id = param.getTaskInstance().getId();//任务ID
				String activityId = param.getTaskInstance().getActivityDefId();//节点id
				String parentTaskID = param.getTaskInstance().getParentTaskInstId();//父类任务实例id
				String task_title = param.getTaskInstance().getTitle();//任务标题
				String processDefid = param.getProcessDef().getId();//流程定义id
				int taskState = param.getTaskInstance().getState();//任务状态
				String statename = CommontUtil.getNoteName(taskState, parentTaskID, task_title, activityId, processDefid);
				String submitterid = param.getProcessInstance().getCreateUser();//流程创建者
				String processType = DBSql.getString(conn,"SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ? ", new Object[]{process_defid});
				String title = param.getProcessInstance().getTitle();//流程标题
				Map<String,Object> mapBindid = new HashMap<String,Object>();
				mapBindid.put("PROCESSINSTID", process_id);
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String closetime = sdf.format(date);
				// 调用App 
				String sourceAppId = CmccConst.sourceAppId;
				// aslp服务地址 
				String aslp = CmccConst.stateAslp;
				// 参数定义列表  
				Map<String, Object> params = new HashMap<String, Object>();
				Map<String,String> map  = new HashMap<String,String>();
				map.put("process_id", process_id);
				map.put("task_id", task_id);
				map.put("status", "close");
				map.put("type", processType);
				map.put("title", title);
				map.put("statename",statename);
				map.put("submitterid", submitterid);
				map.put("ownerids", "");
				map.put("submittime", dateStr);
				map.put("closetime", closetime);
				map.put("passedtime", "");
				map.put("iscancelworkflow", "false");
				map.put("providetype", "AWS");
				//流程状态信息,非必填 
				params.put("mapMsg", map);

				//调用spms状态回写接口 
				ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, params);
			}
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
	}

}
