package com.actionsoft.apps.cmcc.enterprise.fs;

import java.sql.Connection;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;
/**
 * 企标复审流程
 * 流程结束后，删除表BO_ACT_CMCC_PROCESSHANDLE中需求部门企标管理员
 * @author chenxf
 *
 */
public class FS_SubDeleteData_ProcessEndComplate extends ExecuteListener
		implements ExecuteListenerInterface{

	@Override
	public String getDescription() {
		return "流程结束后，删除表BO_ACT_CMCC_PROCESSHANDLE中需求部门企标管理员";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		Connection conn = null;
		try{
			conn = DBSql.open();
			String subBindid = param.getProcessInstance().getId();//流程实例ID
			//查询父流程实例ID
			String parentBindid = DBSql.getString(conn,"SELECT PARENTPROCESSINSTID FROM WFC_PROCESS "
					+ "WHERE ID = ?", new Object[]{subBindid});
			//记录节点ID
			String acticityId = CmccCommon.z_xqbmjkr;
			//删除BO_ACT_PROJECT_PROCONTACT中记录的参与者
			String deleteSql = "DELETE FROM BO_ACT_CMCC_PROCESSHANDLE WHERE PROCESSID = '"+parentBindid+"' AND "
					+"ACTIVITYID = '"+acticityId+"'";
			DBSql.update(conn,deleteSql);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
	}
}
