package com.actionsoft.apps.cmcc.testmanagement.event;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.actionsoft.apps.cmcc.testmanagement.constant.TestManagementConst;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/** 
* @author yujh 
* @version 创建时间：2019年1月22日 上午11:41:07 
* 任务生成后,根据模板发送邮件
*/
public class TaskAfterCompleteEvent_Email extends ExecuteListener implements ExecuteListenerInterface{
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		Connection conn = DBSql.open();
		String processDefId = param.getProcessInstance().getProcessDefId();//流程定义ID
		String title = param.getProcessInstance().getTitle();//流程标题
		String companyName = param.getUserContext().getCompanyModel().getName();
		List<BO> list_email = SDK.getBOAPI().query("BO_ACT_EMAIL_TEMPLETE",true).addQuery("TEMPPROCESSDEFID = ", processDefId).connection(conn).list();
		if(list_email != null && list_email.size() > 0){
			String EMAIL_TITLE = list_email.get(0).getString("EMAIL_TITLE");
			String EMAIL_CONTENT = list_email.get(0).getString("EMAIL_CONTENT");
			String SHORTMESSAGE = list_email.get(0).getString("SHORTMESSAGE");
			String EMAILTO = list_email.get(0).getString("EMAILTO");
			String mobile="";
			if(EMAIL_TITLE.indexOf(TestManagementConst.PROCESSTITLE)>0){
				EMAIL_TITLE.replace("【PROCESSTITLE】", title);
			}
			if(EMAIL_CONTENT.indexOf(TestManagementConst.PROCESSTITLE)>0){
				EMAIL_CONTENT=EMAIL_CONTENT.replace("【PROCESSTITLE】", "《"+title+"》");
			}
			if(EMAIL_CONTENT.indexOf(TestManagementConst.COMPANYNAME)>0){
				EMAIL_CONTENT=EMAIL_CONTENT.replace("【COMPANYNAME】", companyName);
			}
			//处理收件人
			if("processCreater".equals(EMAILTO)){
				EMAILTO = UserContext.fromUID(param.getProcessInstance().getCreateUser()).getUserModel().getEmail();
				mobile =UserContext.fromUID(param.getProcessInstance().getCreateUser()).getUserModel().getMobile();
			}else if("taskTarget".equals(EMAILTO)){
				EMAILTO = UserContext.fromUID(param.getTaskInstance().getTarget()).getUserModel().getEmail();
				mobile = UserContext.fromUID(param.getTaskInstance().getTarget()).getUserModel().getMobile();
			}
			Map<String, Object> params = new HashMap<String, Object>();//参数
			Map<String,String> map  = new HashMap<String,String>();
			map.put("ISEMAIL", "是");
			map.put("ISMESSAGE", "否");
			map.put("EMAIL_CONTENT", EMAIL_CONTENT);
			map.put("TITLE", EMAIL_TITLE);
			/*map.put("TOEMAIL", EMAILTO);
			map.put("MOBILE", mobile);*/
			map.put("TOEMAIL", "1303893146@qq.com");
			map.put("MOBILE", "18101089057");
			map.put("SHORTMESSAGE", SHORTMESSAGE);
			params.put("mapMsg", map);
			//发送邮件、短信接口 
			SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(TestManagementConst.APPID),TestManagementConst.SENDEMAILASLP , params);
		}
	}

}
