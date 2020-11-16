package com.actionsoft.apps.cmcc.VirtualIncome;
/**
 * 虚拟收入查询需求部门
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

public class VirtualIncome_CompleteValidate_step01 extends InterruptListener implements InterruptListenerInterface {

	@Override
	public boolean execute(ProcessExecutionContext param) throws Exception {
		String process_id = param.getProcessInstance().getId();
		String process_definid = param.getProcessInstance().getProcessDefId();// 流程定义ID
		String process_type = DBSql.getString("SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?",
				new Object[] { process_definid });
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("processid", process_id);
		map.put("processtype", process_type);
		// 调用App
		String sourceAppId = CmccConst.sourceAppId;
		// aslp服务地址
		String aslp = CmccConst.virtualAslp;
		ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, map);
		String code = ro.getErrorCode();
		if (code.equals("1")) {
			return true;
		} else {
			String msg = ro.getMsg();
			throw new AWSForbiddenException("获取项目信息失败:" + msg);
		}
	}

}
