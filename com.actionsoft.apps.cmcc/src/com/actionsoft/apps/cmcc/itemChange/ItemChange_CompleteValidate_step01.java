package com.actionsoft.apps.cmcc.itemChange;


/**
 * 项目变更流程第一个节点
 * @author zhaoxs
 * @date 2017-07-14
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

public class ItemChange_CompleteValidate_step01 extends InterruptListener implements
InterruptListenerInterface {
	public String getDescription() {
		return "查询项目信息";
	}
	@Override
	public boolean execute(ProcessExecutionContext param) throws Exception {
		String process_id = param.getProcessInstance().getId();
		String process_definid = param.getProcessInstance().getProcessDefId();//流程定义ID
		String process_type = DBSql.getString("SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?", new Object[]{process_definid});

		//获取项目信息
		// 调用App 
		String sourceAppId = CmccConst.sourceAppId;
		// aslp服务地址 
		String aslp = CmccConst.getProjectAslp;
		// 参数定义列表  
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("processType", process_type);
		params.put("processID", process_id);
		ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, params);
		String code = ro.getErrorCode();
		if(code.equals("1")){
			return true;
		}else{
			String msg = ro.getMsg();
			throw new AWSForbiddenException("获取项目信息失败:"+msg);
		}
	}

}
