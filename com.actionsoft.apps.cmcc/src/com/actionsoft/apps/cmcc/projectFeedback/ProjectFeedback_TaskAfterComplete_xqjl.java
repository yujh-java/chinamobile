
package com.actionsoft.apps.cmcc.projectFeedback;
/*
 * 一般委托流程
 * 需求部门项目经理退回时调用
 * @author zhaoxs
 * @date  2017-12-04
 * 
 * */
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;

public class ProjectFeedback_TaskAfterComplete_xqjl extends ExecuteListener implements ExecuteListenerInterface {

	@Override
	public void execute(ProcessExecutionContext pec) throws Exception {
		String taskid = pec.getTaskInstance().getId();
		String processId = pec.getProcessInstance().getId();
		boolean bol = SDK.getTaskAPI().isChoiceActionMenu(taskid, "退回研发机构接口人处理");
		System.err.println("bol="+bol);
		if (bol) {
			SDK.getLogAPI().getLogger(this.getClass()).error("任务实例ID:"+taskid+",bol="+bol);
			String sourceAppId = CmccConst.sourceAppId;
			// aslp服务地址
			String aslp = CmccConst.projectInfor;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("processid", processId);
			map.put("isback", 3);
			ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, map);
			String code = ro.getErrorCode();
			String msg = ro.getMsg();
			if (!code.equals("1")) {
				throw new BPMNError("0312", "项目信息写入失败" + msg);
			}
		}

	}

}
