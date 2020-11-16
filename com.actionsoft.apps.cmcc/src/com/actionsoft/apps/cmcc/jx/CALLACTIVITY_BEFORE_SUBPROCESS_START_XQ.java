package com.actionsoft.apps.cmcc.jx;
import java.util.ArrayList;
/**
 * 结项流程
 * 结项创建配合研发机构子流程事件,记录对应研发机构
 * @author nch
 * @date 20170622
 */
import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.constant.CallActivityDefinitionConst;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.core.delegate.TaskBehaviorContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class CALLACTIVITY_BEFORE_SUBPROCESS_START_XQ extends ExecuteListener {

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
			String QTXQBMIDS = list.get(0).getString("QTXQBMID");//牵头需求部门ID
			String PHXQBMIDS = list.get(0).getString("PHXQBMID");//配合需求部门ID
			 List<String> listDept = new ArrayList<String>();
			if(!UtilString.isEmpty(QTXQBMIDS)){
				if(!UtilString.isEmpty(QTXQBMIDS)){
					String[] QTXQBMIDArr = QTXQBMIDS.split(",");
					for(int j = 0 ; j < QTXQBMIDArr.length ;j++){
						String QTXQBMID = QTXQBMIDArr[j];
						if(!UtilString.isEmpty(QTXQBMID)){
							listDept.add(QTXQBMID);
						}
					}
				}
			}
			if(!UtilString.isEmpty(PHXQBMIDS)){
				if(!UtilString.isEmpty(PHXQBMIDS)){
					String[] PHXQBMIDArr = PHXQBMIDS.split(",");
					for(int j = 0 ; j < PHXQBMIDArr.length ;j++){
						String PHXQBMID = PHXQBMIDArr[j];
						if(!UtilString.isEmpty(PHXQBMID)){
							listDept.add(PHXQBMID);
						}
					}
				}
			}
			
			if(!UtilString.isEmpty(listDept)){
				for(int i = 0;i < listDept.size();i++){
					String PHXQBMID = listDept.get(i);
					if(dept_pathId.contains(PHXQBMID)){
						deptId = PHXQBMID;
						break;
					}
				}
			}
			bo.set("PROCESSID", processId);
			if(!UtilString.isEmpty(deptId)){
				deptBm =  DBSql.getString("SELECT OUTERID FROM ORGDEPARTMENT WHERE ID = ?", new Object[]{deptId});
			}
			bo.set("PHXQBMID", deptId);
			bo.set("PHXQBM", deptBm);
			bo.remove("ID");
			//创建新的bo记录
			SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSDATA", bo, UserContext.fromUID("admin"));
		}
	}
}
