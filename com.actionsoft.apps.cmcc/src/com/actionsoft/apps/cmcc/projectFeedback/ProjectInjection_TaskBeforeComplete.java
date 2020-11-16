package com.actionsoft.apps.cmcc.projectFeedback;

/**
 * 
 * 一般委托流程项目信息注入
 * @author zhaoxs
 * @date  2017-06-21
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

public class ProjectInjection_TaskBeforeComplete extends InterruptListener implements InterruptListenerInterface {

	@Override
	public boolean execute(ProcessExecutionContext param) throws Exception {
		String process_id = param.getProcessInstance().getId();
		boolean bh = SDK.getTaskAPI().isChoiceActionMenu(param.getTaskInstance().getId(), "送研发机构任务负责人办理");// 获取审核菜单
		boolean bh_read = SDK.getTaskAPI().isChoiceActionMenu(param.getTaskInstance().getId(), "送研发机构任务责任人办理，并送测试管理员阅知");// 获取审核菜单
		if (bh || bh_read) {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, String> mapMsg = new HashMap<String, String>();
			mapMsg.put("process_id", process_id);
			map.put("mapMsg", mapMsg);
			String sourceAppId = CmccConst.sourceAppId;
			// aslp服务地址
			String aslp = CmccConst.projectInjection;
			ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, map);
			String code = ro.getErrorCode();
			if (code.equals("0")) {
				String msg = ro.getMsg();
				throw new BPMNError("0312", "项目信息注入失败!" + msg);
			} else {
				return true;
			}
		}
		return true;
	}

}
