package com.actionsoft.apps.cmcc.projectFeedback;

/*
 * 一般委托流程
 * 用来校验项目是否发布
 * @author zhaoxs
 * @date  2017-06-22
 * 
 * */
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;

public class ProjectInforValidate_TaskBeforeComplete extends InterruptListener implements InterruptListenerInterface {

	@Override
	public boolean execute(ProcessExecutionContext pec) throws Exception {
		String processId = pec.getProcessInstance().getId();
		String sourceAppId = CmccConst.sourceAppId;
		// aslp服务地址
		String aslp = CmccConst.projectInfor;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("processid", processId);
		map.put("isback", 2);
		boolean bol = SDK.getTaskAPI().isChoiceActionMenu(pec.getTaskInstance().getId(), "退回研发机构任务负责人");
		System.err.println("bol="+bol);
		if(bol){
		ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, map);
		String code = ro.getErrorCode();
		String msg = ro.getMsg();
		if (code.equals("0")) {
			throw new BPMNError("0312", "项目信息写入失败" + msg);
		} else if (code.equals("1")) {
			return true;
		} else {
			throw new BPMNError("0312", "请先发布项目,再点击提交下一步!");
		}

		}
		return true;
	}

}
