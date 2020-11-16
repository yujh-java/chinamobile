package com.actionsoft.apps.budget.event;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.apps.budget.BudgetConst;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/** 
* @author  yujh
* @version 创建时间：2019年2月27日 下午4:39:50 
* 节点离开后发送催办邮件(预算流程)
*/
public class ActivityAfterLeaveEvent_Email extends ExecuteListener implements ExecuteListenerInterface{
	public String getDescription() {
		return "节点离开后发送催办邮件(预算流程)";
	}
	
	
	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		String processId =param.getProcessInstance().getId();
		List<TaskInstance> taskList = SDK.getTaskQueryAPI().processInstId(processId).list();
		System.err.println(">>>>邮件接口测试的:"+taskList.size());
		if(taskList.size()>0){
			String currentActivityId = taskList.get(0).getActivityDefId();
			if(!currentActivityId.equals(BudgetConst.Budget_step01_activityid)){//起草节点不发送邮件
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
					if(EMAIL_TITLE.indexOf(BudgetConst.PROCESSTITLE)>0){
						EMAIL_TITLE.replace("【PROCESSTITLE】", title);
					}
					if(EMAIL_CONTENT.indexOf(BudgetConst.PROCESSTITLE)>0){
						EMAIL_CONTENT=EMAIL_CONTENT.replace("【PROCESSTITLE】", "《"+title+"》");
					}
					if(EMAIL_CONTENT.indexOf(BudgetConst.COMPANYNAME)>0){
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
					SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(BudgetConst.APPID),BudgetConst.SENDEMAILASLP , params);
				}
			}
		}
	}

}
