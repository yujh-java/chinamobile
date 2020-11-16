package com.actionsoft.apps.cmcc.ybwt;

import java.sql.Connection;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;

/**
 * 一般委托相关部所会签子流程
 * @author wxx
 * @date 2019-6-10
 */
public class YB_DeptContact_TaskAfterComplete extends ExecuteListener implements
ExecuteListenerInterface{
	public String getDescription() {
		return "相关部所会签子流程";
	}

	
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		Connection conn = null;
		try{
			conn = DBSql.open();
			String subBindid = param.getProcessInstance().getId();//流程实例ID
			//查询父流程
			String parentBindid = DBSql.getString(conn,"SELECT PARENTPROCESSINSTID FROM WFC_PROCESS WHERE ID = ?", new Object[]{subBindid});//父流程实例ID
			//System.err.println("zhuid"+parentBindid);
			String acticityId = YBActivityIdConst.phyfjgjkr;//记录节点ID
			//System.err.println("ziid"+acticityId);
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
