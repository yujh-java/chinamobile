package com.actionsoft.apps.cmcc.jx;
/**
 * 结项流程
 * 项目结项子流程流程结束后事件,删除记录表中记录
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;

public class JX_DeptContact_TaskAfterComplete extends ExecuteListener implements
		ExecuteListenerInterface {
	public String getDescription() {
		return "项目结项子流程流程结束后事件";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		String subBindid = param.getProcessInstance().getId();//流程实例ID
		Connection conn = null;
		try{
			conn =  DBSql.open();
			//查询父流程
			String parentBindid = DBSql.getString(conn,"SELECT PARENTPROCESSINSTID FROM WFC_PROCESS WHERE ID = ?", new Object[]{subBindid});//父流程实例ID
			String acticityId = JxActivityConst.phyfjgImper;//记录节点ID
			String acticityId2 = JxActivityConst.xqbmjkr;;//记录节点ID

			//删除BO_ACT_PROJECT_PROCONTACT中记录的参与者
			String deleteSql = "DELETE FROM BO_ACT_CMCC_PROCESSHANDLE WHERE PROCESSID = '"+parentBindid+"' AND "
					+"(ACTIVITYID = '"+acticityId+"' OR ACTIVITYID = '"+acticityId2+"')";
			DBSql.update(conn,deleteSql);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
	}
}
