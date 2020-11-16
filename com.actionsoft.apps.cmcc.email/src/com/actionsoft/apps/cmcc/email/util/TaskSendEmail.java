package com.actionsoft.apps.cmcc.email.util;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.cache.util.UserTaskDefUtil;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

/**
 * 针对任务发送邮件、短信
 * @author nch
 * @date 2017-9-26
 */
public class TaskSendEmail {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public void sendMsgForTask(String ONLYSIGN,String taskid){
		Connection conn = null;
		try{
			EmailUtil emailUtil = new EmailUtil();//email公用方法类
			//邮件固定词组
			List<BO> list_fixed = SDK.getBOAPI().query("BO_ACT_EMAIL_FIXED").connection(conn).list();
			//获取流程邮件模板
			List<BO> list_processEmail = SDK.getBOAPI().query("BO_ACT_EMAIL_APPCONFIG").addQuery("ONLYSIGN = ", ONLYSIGN).connection(conn).list();
			if(!UtilString.isEmpty(list_processEmail)){
				String EMAILID = list_processEmail.get(0).getString("EMAILID");//邮件模板
				String ISMESSAGE = list_processEmail.get(0).getString("ISMESSAGE");
				String ISEMAIL = list_processEmail.get(0).getString("ISEMAIL");

				//根据任务ID，查询任务、流程信息
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
				String taskTarget = taskInstance.getTarget();
				String activityid = taskInstance.getActivityDefId();
				String processid = taskInstance.getProcessInstId();

				ProcessInstance processInstance = SDK.getProcessAPI().getInstanceById(processid);
				String processDefid = processInstance.getProcessDefId();
				String parentProcessId  = processInstance.getParentProcessInstId();
				String processCreater = "";
				if(UtilString.isEmpty(parentProcessId)){
					processCreater = processInstance.getCreateUser();
				}else{
					ProcessInstance parentProcessInstance = SDK.getProcessAPI().getInstanceById(parentProcessId);
					processCreater = parentProcessInstance.getCreateUser();
				}
				String processTitle = processInstance.getTitle();
				//固定词组，及实际对应值
				Map<String,String> map_fixed = new HashMap<String,String>();
				for(int f = 0;f < list_fixed.size();f++){
					String FIXEDPARASE = list_fixed.get(f).getString("FIXEDPARASE");
					String PHRASE = list_fixed.get(f).getString("PHRASE");
					if(!UtilString.isEmpty(PHRASE)){//优先取默认值
						map_fixed.put(FIXEDPARASE, PHRASE);
					}else{
						if("【ACTIVITYNAME】".equals(FIXEDPARASE)){
							//当前任务节点名称
							UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefid, activityid);
							if(taskModel != null ){
								String activityName = taskModel.name;
								map_fixed.put(FIXEDPARASE, activityName);
							}else{
								map_fixed.put(FIXEDPARASE, "");
							}
						}else if("【PROCESSTITLE】".equals(FIXEDPARASE)){
							//流程标题
							map_fixed.put(FIXEDPARASE, processTitle);
						}else if("【PROCESSCREATER】".equals(FIXEDPARASE)){
							//流程发起人姓名
							map_fixed.put(FIXEDPARASE, processCreater);
						}else if("【TASKTARGET】".equals(FIXEDPARASE)){
							//当前任务办理人
							map_fixed.put(FIXEDPARASE, taskTarget);
						}else if("【COMPANYNAME】".equals(FIXEDPARASE)){
							String companyName = CommonUtil.getCompany(taskTarget);
							//当前任务办理人所在单位名称
							map_fixed.put(FIXEDPARASE, companyName);
						}
					}
				}
				//处理邮件信息,并发送邮件
				emailUtil.sendEmail(map_fixed, EMAILID,ISEMAIL,ISMESSAGE,conn);
			}
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
	}
}	
