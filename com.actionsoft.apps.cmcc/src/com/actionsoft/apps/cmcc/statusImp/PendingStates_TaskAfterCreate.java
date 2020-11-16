package com.actionsoft.apps.cmcc.statusImp;
/**
 * 状态信息回写,pending 公共事件
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.apps.cmcc.jx.JxActivityConst;
import com.actionsoft.apps.cmcc.lx.LxActivityIdConst;
import com.actionsoft.apps.cmcc.util.CommontUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class PendingStates_TaskAfterCreate extends ExecuteListener implements
ExecuteListenerInterface {
	public String getDescription() {
		return "pending状态信息回写";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		int taskState = param.getTaskInstance().getState();
		if(taskState == 1 || taskState == 4||taskState==11){
			String process_id = param.getProcessInstance().getId();
			String activityID = param.getTaskInstance().getActivityDefId();
			String task_id = param.getTaskInstance().getId();//任务ID
			Connection conn = DBSql.open();
			try{
				String title = "";
				System.err.println(">>>>step:"+activityID);
				System.err.println(">>>>taskState:"+taskState);
				if((activityID.equals(LxActivityIdConst.sub_xqbmjkr) && taskState ==1)
						|| activityID.equals(JxActivityConst.sub_peyfjgjkr)
						|| activityID.equals(LxActivityIdConst.sub_peyfjgjkr)){
					//立项需求部门子流程:部门接口人节点
					String parentProcessId = SDK.getProcessAPI().getInstanceById(process_id).getParentProcessInstId();
					title = DBSql.getString(conn,"SELECT TITLE FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID= ?", new Object[]{parentProcessId});
					System.err.println(">>>>title:"+title);
					SDK.getProcessAPI().setTitle(process_id, title);
					SDK.getTaskAPI().setTitle(task_id, title);
				}
				String process_defid = param.getProcessInstance().getProcessDefId();//流程定义ID
				String dateStr = DBSql.getString(conn,"SELECT PROSUBTIME FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",new Object[]{process_id});
				
				if(!UtilString.isEmpty(dateStr)){
					if(title == null || "".equals(title)){
						title = param.getProcessInstance().getTitle();//标题
					}
					String submitterid = param.getProcessInstance().getCreateUser();//流程创建者
					String processType = DBSql.getString(conn,"SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?", new Object[]{process_defid});

					StringBuffer ownerids = new StringBuffer();//当前任务办理人
					Map<String,Object> mapBindid = new HashMap<String,Object>();
					mapBindid.put("PROCESSINSTID", process_id);
					List<RowMap> list = DBSql.getMaps(conn,"SELECT TARGET FROM WFC_TASK WHERE PROCESSINSTID= ? AND (TASKSTATE != ? OR TASKSTATE != ?)", new Object[]{process_id,9,4});
					for(int i = 0;i< list.size();i++){
						String owner = (String) list.get(i).get("TARGET");
						if(i == list.size() -1){
							ownerids.append("'"+owner+"'");
						}else{
							ownerids.append("'"+owner+"',");
						}
					}
					String parentProcessId = SDK.getProcessAPI().getInstanceById(process_id).getParentProcessInstId();
					if(!UtilString.isEmpty(parentProcessId)){
						process_id = parentProcessId;
					}
					
					String parentTaskID = param.getTaskInstance().getParentTaskInstId();//父类任务实例id
					String task_title = param.getTaskInstance().getTitle();//任务标题
					String processDefid = param.getProcessDef().getId();//流程定义id
					String statename = CommontUtil.getNoteName(taskState, parentTaskID, task_title, activityID, processDefid);
					// 调用App 
					String sourceAppId = CmccConst.sourceAppId;
					// aslp服务地址 
					String aslp = CmccConst.stateAslp;
					// 参数定义列表  
					Map<String, Object> params = new HashMap<String, Object>();
					Map<String,String> map  = new HashMap<String,String>();
					map.put("process_id", process_id);
					map.put("task_id", task_id);
					map.put("status", "pending");
					map.put("type", processType);
					map.put("title", title);
					map.put("statename", statename);
					map.put("submitterid", submitterid);
					map.put("ownerids", ownerids.toString());
					map.put("submittime", dateStr);
					map.put("closetime", "");
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
}
