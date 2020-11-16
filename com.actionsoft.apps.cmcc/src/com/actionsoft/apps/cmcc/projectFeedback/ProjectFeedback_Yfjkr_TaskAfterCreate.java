package com.actionsoft.apps.cmcc.projectFeedback;
/**
 * @author niech
 * @date 20170927
 * 研发接口人节点，任务创建后，调用状态会写和发送邮件短信通知
 */
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.apps.cmcc.statusImp.PendingStates_TaskAfterCreate;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;

public class ProjectFeedback_Yfjkr_TaskAfterCreate extends ExecuteListener
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
		String ognlSign = CmccConst.ognlSign_projectFeedback;//邮件唯一标识
		String taskId = param.getTaskInstance().getId();
		// 调用App 
		String sourceAppId = CmccConst.sourceAppId;
		// aslp服务地址
		String inforaslp = CmccConst.projectInfor;
		// aslp服务地址 
		String aslp = CmccConst.sendMsgAslp;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("taskId", taskId);
		params.put("ognlSign", ognlSign);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("processid", param.getProcessInstance().getId());
		map.put("isback",0);
		//调用发送邮件短信ASLP 
		//SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, params);
		
		ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), inforaslp, map);
		String code = ro.getErrorCode();
		String msg = ro.getMsg();
		if (!code.equals("1")) {
			SDK.getLogAPI().getLogger(this.getClass()).error("任务实例ID:"+taskId+"msg:"+msg);
		}

	}
}
