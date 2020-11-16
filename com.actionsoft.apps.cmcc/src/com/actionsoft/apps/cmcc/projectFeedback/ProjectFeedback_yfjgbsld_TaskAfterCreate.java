package com.actionsoft.apps.cmcc.projectFeedback;
/**
 * @author zhaoxs
 * @date 2017-11-02
 * 研发机构承担部所领导审批节点，任务创建后，调用状态会写和发送邮件短信通知
 */
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.apps.cmcc.statusImp.PendingStates_TaskAfterCreate;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.sdk.local.SDK;

public class ProjectFeedback_yfjgbsld_TaskAfterCreate extends ExecuteListener
		implements ExecuteListenerInterface {
	public String getDescription() {
		return "任务创建后，状态会写和发送邮件短信通知";
	}
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		//调用pending状态会写
		try{
			PendingStates_TaskAfterCreate pendind = new PendingStates_TaskAfterCreate();
			pendind.execute(param);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
		//给接口人发送短信、邮件通知
		String ognlSign = CmccConst.Dept_projectFeedback;//邮件唯一标识
		String taskId = param.getTaskInstance().getId();
		// 调用App 
		String sourceAppId = CmccConst.sourceAppId;
		// aslp服务地址 
		String aslp = CmccConst.sendMsgAslp;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("taskId", taskId);
		params.put("ognlSign", ognlSign);
		//调用发送邮件短信ASLP 
		//SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, params);

	}
}
