package com.actionsoft.apps.cmcc.enterprise.statusImp;

import com.actionsoft.apps.cmcc.enterprise.util.CmccUrlUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.sdk.local.SDK;
/**
 * 企标管理
 * 流程结束后，调用接口给其返回流程结束状态
 * @author chenxf
 *
 */
public class CloseState_TaskAfterComplete  
	extends ExecuteListener
	implements ExecuteListenerInterface {

	@Override
	public String getDescription() {
		return "企标管理：流程结束后，返回结束状态";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		//流程实例ID
		String processId = param.getProcessInstance().getId();
		//调取应用需求部门ID的接口URL
	    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.enterprise", "RETURNSTATE");
	    //拼接
	    StringBuffer sb = new StringBuffer(url);
	    sb.append(processId);
	    //close==1-->表示关闭状态
	    sb.append("&state=1");
	    System.err.println("====企标管理URL流程结束接口："+sb.toString()+"===========");
		
	    //获取返回值
	    String json = CmccUrlUtil.get(sb.toString());
	}
}
