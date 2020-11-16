package com.actionsoft.apps.cmcc.resultSub;
/**
 * 成果提交
 * 查询需求部门,成果提交提报节点
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSForbiddenException;
import com.actionsoft.sdk.local.SDK;

public class ResultSub_CompleteValidate_step01 extends InterruptListener
implements InterruptListenerInterface {
	public String getDescription() {
		return "查询需求部门";
	}
	@Override
	public boolean execute(ProcessExecutionContext param) throws Exception {
		String process_id = param.getProcessInstance().getId();
		String process_definid = param.getProcessInstance().getProcessDefId();//流程定义ID
		//查询需求部门
		String xqbmid_me = "";
		//查询承担部门
		String cdbmid_me = "";
		String process_type = DBSql.getString("SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?", new Object[]{process_definid});
		//获取项目信息
		// 调用App 
		String sourceAppId = CmccConst.sourceAppId;
		// aslp服务地址 
		String aslp = CmccConst.resultSubAslp;
		// 参数定义列表  
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("process_type", process_type);
		params.put("process_id", process_id);
		ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, params);
		String code = ro.getErrorCode();
		if(code.equals("1")){
			//需求部门ID
			String xqbmid = (String) ro.get("xqbmid");
			//承担部门ID
			String cdbmid = (String) ro.get("cdbmid");
			xqbmid_me = DBSql.getString("SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?", new Object[]{xqbmid});
			cdbmid_me = DBSql.getString("SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?", new Object[]{cdbmid});
		}else{
			String msg = ro.getMsg();
			throw new AWSForbiddenException("获取项目信息失败:"+msg);
		}
		Connection conn = DBSql.open();
		try{
			//记录信息
			List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", process_id).connection(conn).list();
			if(list != null && list.size() > 0){
				BO bo = list.get(0);
				bo.set("SUBRESULTXQBM", xqbmid_me);
				bo.set("QTYFJGBMID", cdbmid_me);
				SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSDATA", bo,conn);
			}else{
				BO bo = new BO();
				String createUser = param.getProcessInstance().getCreateUser();
				String title = param.getProcessInstance().getTitle();
				String PROCESSTYPE = DBSql.getString(conn,"SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?", new Object[]{process_definid});
				bo.set("PROCESSID", process_id);
				bo.set("CREATEUSERID", createUser);
				bo.set("TITLE", title);
				bo.set("SUBRESULTXQBM", xqbmid_me);
				bo.set("QTYFJGBMID", cdbmid_me);
				bo.set("PROCESSTYPE", PROCESSTYPE);
				SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSDATA", bo,param.getUserContext(),conn);
			}
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
		return true;
	}

}
