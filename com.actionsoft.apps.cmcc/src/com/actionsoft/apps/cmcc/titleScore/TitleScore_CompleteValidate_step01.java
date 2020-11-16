package com.actionsoft.apps.cmcc.titleScore;
/**
 * 结题评分流程存储需求部门
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

public class TitleScore_CompleteValidate_step01 extends InterruptListener implements InterruptListenerInterface {

	@Override
	public boolean execute(ProcessExecutionContext arg0) throws Exception {
		String defid = arg0.getProcessInstance().getProcessDefId();// 流程定义id
		String process_id = arg0.getProcessInstance().getId();// 流程实例id
		String userid = arg0.getUserContext().getUID();
		String taskid = arg0.getTaskInstance().getId();
		String process_type = DBSql.getString("SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?",
				new Object[] { defid });// 流程类型
		// 调用App
		String sourceAppId = CmccConst.sourceAppId;
		// aslp服务地址
		String aslp = CmccConst.xqbmAslp;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("processid", process_id);
		map.put("taskid", taskid);
		map.put("userid", userid);
		map.put("processtype", process_type);
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
