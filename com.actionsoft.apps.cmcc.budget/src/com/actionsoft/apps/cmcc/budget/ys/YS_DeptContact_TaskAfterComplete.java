package com.actionsoft.apps.cmcc.budget.ys;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.actionsoft.apps.cmcc.budget.util.CmccUrlUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

public class YS_DeptContact_TaskAfterComplete extends ExecuteListener implements
ExecuteListenerInterface{
	public String getDescription() {
		return "预算子流程流程结束后事件";
	}

	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		Connection conn = null;
		try{
			conn = DBSql.open();
			String subBindid = param.getProcessInstance().getId();//流程实例ID
			//查询父流程
			String parentBindid = DBSql.getString(conn,"SELECT PARENTPROCESSINSTID FROM WFC_PROCESS WHERE ID = ?", new Object[]{subBindid});//父流程实例ID
			String acticityId = YsActivityIdConst.phyfjgjkr;//记录节点ID
			//删除BO_ACT_PROJECT_PROCONTACT中记录的参与者
			String deleteSql = "DELETE FROM BO_ACT_CMCC_PROCESSHANDLE WHERE PROCESSID = '"+parentBindid+"' AND "
					+"(ACTIVITYID = '"+acticityId+"')";
			DBSql.update(conn,deleteSql);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
	}

}
