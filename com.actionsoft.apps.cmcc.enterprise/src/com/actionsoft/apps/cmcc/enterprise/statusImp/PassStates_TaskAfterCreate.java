package com.actionsoft.apps.cmcc.enterprise.statusImp;
import com.actionsoft.apps.cmcc.enterprise.util.CmccUrlUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.sdk.local.SDK;
/**
 * 企标管理
 * 流程通过有效
 * @author chenxf
 *
 */
public class PassStates_TaskAfterCreate extends ExecuteListener implements
ExecuteListenerInterface {
	public String getDescription() {
		return "pass状态信息回写：流程通过有效";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		//流程实例ID
		String processId = param.getProcessInstance().getId();
		String taskId = param.getTaskInstance().getId();
		//判断是否是退回起草路径
		boolean flag = SDK.getTaskAPI().isChoiceActionMenu(taskId, "退回起草");
		if(flag){
			//调取应用需求部门ID的接口URL
		    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.enterprise", "RETURNSTATE");
		    //拼接
		    StringBuffer sb = new StringBuffer(url);
		    sb.append(processId);
		    sb.append("&state=pass");
		    System.err.println("====企标管理URL流程结束接口："+sb.toString()+"===========");
			
		    //获取返回值
		    String json = CmccUrlUtil.get(sb.toString());
		}
	}
}
