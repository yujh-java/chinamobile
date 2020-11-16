package com.actionsoft.apps.cmcc.lx;
/**
 * 立项流程
 * 立项创建配合研发机构子流程事件
 * @author nch
 * @date 20170622
 */
import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.constant.CallActivityDefinitionConst;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.core.delegate.TaskBehaviorContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class CALLACTIVITY_BEFORE_SUBPROCESS_START extends ExecuteListener {

	@Override
	public void execute(ProcessExecutionContext ctx) throws Exception {
		String parentProcessId = ctx.getProcessInstance().getId();
		TaskBehaviorContext subProcessCtx = (TaskBehaviorContext) ctx.getParameter(CallActivityDefinitionConst.PARAM_CALLACTIVITY_CONTEXT);
		String processId = subProcessCtx.getProcessInstance().getId();//子流程实例ID
		String dept_Id = subProcessCtx.getProcessInstance().getCreateUserDeptId();//子流程创建者部门ID
		String dept_pathId = SDK.getORGAPI().getDepartmentById(dept_Id).getPathIdOfCache();//部门全路径
		
		List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID=", parentProcessId).list();
		
		if(list != null && list.size() > 0){
			BO bo = list.get(0);
			String deptId = "";
			String deptBm = "";
			String PHYFJGBMIDs = list.get(0).getString("PHYFJGBMID");
			if(!UtilString.isEmpty(PHYFJGBMIDs)){
				String[] PHYFJGBMIDArr = PHYFJGBMIDs.split(",");
				for(int i = 0;i < PHYFJGBMIDArr.length;i++){
					String PHYFJGBMID = PHYFJGBMIDArr[i];
					if(!UtilString.isEmpty(PHYFJGBMID) && dept_pathId.contains(PHYFJGBMID)){
						deptId = PHYFJGBMID;
						break;
					}
				}
			}
			bo.set("PROCESSID", processId);
			if(!UtilString.isEmpty(deptId)){
				deptBm =  DBSql.getString("SELECT OUTERID FROM ORGDEPARTMENT WHERE ID = ?", new Object[]{deptId});
			}
			bo.set("PHYFJGBMID", deptId);
			bo.set("PHYFJGBM", deptBm);
			bo.remove("ID");
			//创建新的bo记录
			SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSDATA", bo, UserContext.fromUID("admin"));
		}
	}
}
