package com.actionsoft.apps.cmcc.projectFeedback;
/**
 * 一般委托流程查询需求部门
 * @author zhaoxs
 * @date 2017-06-21
 */
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSForbiddenException;
import com.actionsoft.sdk.local.SDK;

public class ProjectFeedback_CompleteValidate_step01 extends InterruptListener implements InterruptListenerInterface {

	public String getDescription() {
		return "一般委托流程表单办理前校验";
	}

	@Override
	public boolean execute(ProcessExecutionContext param) throws Exception {
		String process_id = param.getProcessInstance().getId();
		String process_definid = param.getProcessInstance().getProcessDefId();// 流程定义ID
		String taskid = param.getTaskInstance().getId();
		String userid = param.getUserContext().getUID();
		String process_type = DBSql.getString("SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?",
				new Object[] { process_definid });
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("processid", process_id);
		map.put("taskid", taskid);
		map.put("userid", userid);
		map.put("processtype", process_type);
		// 调用App
		String sourceAppId = CmccConst.sourceAppId;
		// aslp服务地址
		String aslp = CmccConst.xqbmAslp;
		ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, map);
		String code = ro.getErrorCode();
		if (code.equals("1")) {
			return true;
		}else if(code.equals("2")){
			String msg = ro.get("msg").toString();
			throw new AWSForbiddenException("获取项目信息失败:" + msg);
		}else {
			String msg = ro.getMsg();
			throw new AWSForbiddenException("获取项目信息失败:" + msg);
		}
	}

}
