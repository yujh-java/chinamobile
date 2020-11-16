package com.actionsoft.apps.cmcc.email.aslp;
/**
 * 根据任务发送邮件、短信
 */
import java.util.Map;

import com.actionsoft.apps.cmcc.email.util.TaskSendEmail;
import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.apps.resource.interop.aslp.Meta;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;

public class TaskSendMsgASPLP implements ASLP {
	@Meta(parameter = { "name:'taskId',desc:'任务id'","name:'ognlSign',desc:'流程邮件模板唯一标识'"}) 
	public ResponseObject call(Map<String, Object> param) {
		String taskId = (String) param.get("taskId");
		String ognlSign = (String) param.get("ognlSign");
		//调用任务发送信息通知
		TaskSendEmail sendMsg = new TaskSendEmail();
		sendMsg.sendMsgForTask(ognlSign, taskId);
		return null;
	}

}
