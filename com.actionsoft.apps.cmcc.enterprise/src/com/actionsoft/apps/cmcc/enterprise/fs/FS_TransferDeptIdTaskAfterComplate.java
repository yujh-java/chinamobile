package com.actionsoft.apps.cmcc.enterprise.fs;

import java.sql.Connection;
import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
/**
 * 企标复审--需求部门企标管理员环节
 * 任务完成后，给第三方传输需求企标管理部门ID
 * 暂时不用
 * @author chenxf
 *
 */
public class FS_TransferDeptIdTaskAfterComplate extends ExecuteListener
		implements ExecuteListenerInterface{

	@Override
	public String getDescription() {
		return "任务完成后，给第三方传输需求企标管理部门ID！";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		//任务ID
		String taskId = param.getTaskInstance().getId();
		//流程实例ID
		String bindid = param.getProcessInstance().getId();
		TaskInstance taskInstance = SDK.getTaskAPI().getTaskInstance(taskId);
		//当前节点ID
		String acticityId = taskInstance.getActivityDefId();
		Connection conn = DBSql.open();
		StringBuffer sb = new StringBuffer();
		try {
			//查询 (BO_ACT_CMCC_PROCESSHANDLE) 是否存在记录
			List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSHANDLE").
											addQuery("PROCESSID = ", bindid).
											addQuery("ACTIVITYID = ", acticityId).
											connection(conn).list();
			/**
			 * 只有在有个需求部门管理员的情况，才传输信息
			 */
			if(list != null && list.size() > 0){
				for(BO bo : list){
					//参与者
					String handlers = bo.getString("HANDLERS");
					//根据人员账号获取部门ID
					String deptid = UserContext.fromUID(handlers).getDepartmentModel().getId();
					if(!UtilString.isEmpty(deptid)){
						//获取第三方部门ID样式
						String outerid = DBSql.getString("select outerid from ORGDEPARTMENT id = ?", new Object[]{ deptid });
						sb.append(outerid + " ");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
	}
}
