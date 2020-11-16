package com.actionsoft.apps.budget.statusImp;
/**
 * 节点1，任务创建后，状态信息回写 公共事件
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.apps.budget.BudgetConst;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class NopassStatus_TaskAfterCreate_step01 extends ExecuteListener implements
ExecuteListenerInterface {
	public String getDescription() {
		return "任务创建后，状态信息回写";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		int taskState = param.getTaskInstance().getState();
		if(taskState == 1 || taskState == 4){
			String process_id = param.getProcessInstance().getId();
			String process_defid = param.getProcessInstance().getProcessDefId();//流程定义ID
			Connection conn = DBSql.open();
			try{
				String dateStr = DBSql.getString(conn,"SELECT PROSUBTIME FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",new Object[]{process_id});
				if(!UtilString.isEmpty(dateStr)){
					String task_id = param.getTaskInstance().getId();//任务ID
					String title = param.getTaskInstance().getTitle();//标题
					String submitterid = param.getProcessInstance().getCreateUser();//流程创建者
					String processType = DBSql.getString(conn,"SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?", new Object[]{process_defid});

					StringBuffer ownerids = new StringBuffer();//当前任务办理人
					List<RowMap> list = DBSql.getMaps(conn,"SELECT TARGET FROM WFC_TASK WHERE PROCESSINSTID= ? AND TASKSTATE != ?", new Object[]{process_id,9});
					for(int i = 0;i< list.size();i++){
						String owner = (String) list.get(i).get("TARGET");
						if(i == list.size() -1){
							ownerids.append("'"+owner+"'");
						}else{
							ownerids.append("'"+owner+"',");
						}
					}

					// 调用App 
					String sourceAppId = BudgetConst.sourceAppId;
					// aslp服务地址 
					String aslp = BudgetConst.stateAslp;
					// 参数定义列表  
					Map<String, Object> params = new HashMap<String, Object>();
					Map<String,String> map  = new HashMap<String,String>();
					map.put("process_id", process_id);
					map.put("task_id", task_id);
					map.put("status", "nopass");
					map.put("type", processType);
					map.put("title", title);
					map.put("statename", "退回起草");
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
