package com.actionsoft.apps.cmcc.enterprise.statusImp;
/**
 * 企标报批
 * @author wuxx
 *
 */
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.apps.cmcc.enterprise.util.CmccUrlUtil;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class BP_PendingState_TaskAfterComplete extends ExecuteListener implements
ExecuteListenerInterface{
	public String getDescription() {
		return "pending状态信息回写：流程审批中";
	}

	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		int taskState = param.getTaskInstance().getState();
		//流程实例ID
		String processId = param.getProcessInstance().getId();
		//任务实ID
		String taskId = param.getTaskInstance().getId();
		 //打开数据库
	    Connection con = DBSql.open();
	    
		if(taskState == 1 || taskState == 4||taskState==11){
			String activityID = param.getTaskInstance().getActivityDefId();
			if(activityID.equals(CmccCommon.sub_jsxqbm)){//如果是企标报批子流程的第一个节点，将子流程的标题写进去
				//企标复审需求部门子流程:企标管理节点
				/*String parentProcessId = SDK.getProcessAPI().getInstanceById(processId).getParentProcessInstId();
				String title = DBSql.getString(con,"SELECT TITLE FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID= ?", new Object[]{parentProcessId});
				SDK.getProcessAPI().setTitle(processId, title);
				SDK.getTaskAPI().setTitle(taskId, title);*/
			}
		}
		String parentProcessId = SDK.getProcessAPI().getInstanceById(processId).getParentProcessInstId();
		if(!UtilString.isEmpty(parentProcessId)){
			processId = parentProcessId;
		}
		//调取应用需求部门ID的接口URL
	    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.enterprise", "RETURNSTATE");
	    //拼接
	    StringBuffer sb = new StringBuffer(url);
	    sb.append(processId);
	    sb.append("&state=pending");
	    System.err.println("====企标管理URL流程结束接口："+sb.toString()+"===========");
		
	    //获取返回值
	    String json = CmccUrlUtil.get(sb.toString());
	    /**
	     * 主流程的时候才执行
	     */
	    if(UtilString.isEmpty(parentProcessId)){
	    	try {
		    	/**
		    	 * 起草环节，流程提交后，更新提交时间
		    	 */
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				BO bo = new BO();
				bo.set("PROSUBTIME", sdf.format(date));
				String id = DBSql.getString("select id from BO_ACT_CMCC_PROCESSDATA where PROCESSID = ?", new Object[] { processId });
				if (id == null|| "".equals(id)){
					SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSDATA", bo, UserContext.fromUID("admin"), con);
				}else{
					bo.setId(id);
				    //更新数据
				    SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSDATA", bo, con);
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}finally{
				DBSql.close(con);
			}
	    }
	}

}
