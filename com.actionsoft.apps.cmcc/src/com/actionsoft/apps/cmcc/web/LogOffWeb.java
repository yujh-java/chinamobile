package com.actionsoft.apps.cmcc.web;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.commons.mvc.view.ActionWeb;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.bpms.util.UtilURL;
import com.actionsoft.sdk.local.SDK;

import net.sf.json.JSONObject;


public class LogOffWeb extends ActionWeb {
	public LogOffWeb() {
		super();
	}

	public LogOffWeb(UserContext userContext) {
		super(userContext);
	}
	/**
	 *  根据任务实例和用户id查询是否为任务实例待办人
	 * @param me             用户上下文
	 * @param currentOpUser   用户id
	 * @param taskInstId     任务实例id
	 * @return
	 */
	public String isPresentUser(UserContext me,String currentOpUser,String taskInstId)
	{
		//查询是否存在任务实例待办人
		String id = DBSql.getString("SELECT ID FROM WFC_TASK WHERE ID = ? AND TARGET = ? AND TASKSTATE='1'", new Object[]{taskInstId,currentOpUser});

		if(UtilString.isEmpty(id))
		{
			ResponseObject ro  = ResponseObject.newErrResponse();
			return ro.toString();//返回数据
		}else
		{
			ResponseObject ro = ResponseObject.newOkResponse();
			return ro.toString();//返回数据
		}

	}

	/**
	 * 根据流程实例id删除流程实例
	 * @param me              用户上下文
	 * @param processInstId   流程实例id
	 * @param processDefid    流程定义id
	 * @return
	 */
	public String DeleteProcessInst (UserContext me, String processInstId,String processDefid)
	{
		ProcessInstance instance = SDK.getProcessAPI().getInstanceById(processInstId);//根据流程实例id获取流程实例
		String target=DBSql.getString("SELECT TARGET FROM WFC_TASK WHERE processinstid = ? AND TASKSTATE=?",new Object[]{processInstId,1});
		if(!me.getUID().equals(target)){
			ResponseObject ro = ResponseObject.newErrResponse();
			ro.put("msg", "当前任务已提交,注销失败!");//返回状态
			return ro.toString();
		}

//		String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "LOGOFF_BUTTON");//获取url接口地址
		String type = DBSql.getString("SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?", new Object[]{processDefid});//查询流程类型
		/*
		 * chenxf modify
		 */
		//获取url接口地址
		String url = "";
		//判断是否为研究院，从而调用不同的接口参数
		if(type.indexOf("cmri") != -1){
			url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "LOGOFF_BUTTON_CMRI");
		}else{
			url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "LOGOFF_BUTTON");
		}
		System.err.println("=====注销接口URL："+url+"=======");
		StringBuffer sbf = new StringBuffer();
		sbf.append(url).append("?type=").append(type).append("&process_id=").append(instance.getId());//拼接接口地址

		String str = UtilURL.get(sbf.toString().trim());
		JSONObject resultJson = JSONObject.fromObject(str); //从url接口获取数据
		String data = resultJson.getString("data");
		JSONObject datajson = JSONObject.fromObject(data);
		String msg = datajson.getString("msg");//获取接口返回的状态信息
		
		//if(msg.equals("ok"))
		if(msg.equals("1") || msg.equals("2"))//如果等于1或者等于2，删除流程实例
		{
			ResponseObject ro = ResponseObject.newOkResponse();//获取返回数据
			String sql = "DELETE FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID='"+instance.getId()+"'";
			DBSql.update(sql);
			SDK.getProcessAPI().delete(instance, me);//删除流程实例
			ro.put("msg", "注销成功!");//返回状态
			return ro.toString();
		}else
		{
			ResponseObject ro = ResponseObject.newErrResponse();
			ro.put("msg", "注销失败!");//返回状态
			return ro.toString();
		}
	}

}

