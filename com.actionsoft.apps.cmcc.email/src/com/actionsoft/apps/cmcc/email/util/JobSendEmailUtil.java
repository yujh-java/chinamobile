package com.actionsoft.apps.cmcc.email.util;
/**
 * 针对定时器处理发送邮件、短信
 * @author niech
 * @date 20170926
 */
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.apps.cmcc.email.EmailConst;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.cache.util.UserTaskDefUtil;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class JobSendEmailUtil {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public void sendMsg(List<BO> list,Connection conn) throws ParseException{
		EmailUtil emailUtil = new EmailUtil();//email公用方法类

		List<BO> list_fixed = SDK.getBOAPI().query("BO_ACT_EMAIL_FIXED").connection(conn).list();
		//查询适用此定时器流程邮件配置
		if(list != null && list.size() > 0){
			for(int i = 0 ; i < list.size();i++){
				String EMAILID = list.get(i).getString("EMAILID");//邮件模板
				String PROCESSTYPEID = list.get(i).getString("PROCESSTYPEID");//流程类型ID
				String ACTIVITYID = list.get(i).getString("ACTIVITYID");//节点ID
				String SENDDATE = list.get(i).getString("SENDDATE");//任务时间(单位：天)
				String CREATEACTIVITYID = list.get(i).getString("CREATEACTIVITYID");//任务发起节点
				String ISMESSAGE = list.get(i).getString("ISMESSAGE");
				String ISEMAIL = list.get(i).getString("ISEMAIL");
				//查询流程、节点的所有待办任务
				List<List<TaskInstance>> list_tasks =  emailUtil.getTaskForEmail(PROCESSTYPEID,ACTIVITYID,conn);
				Map<String,Map<String,String>> map = new HashMap<String,Map<String,String>>();
				if(list_tasks != null && list_tasks.size() > 0){
					for(int j = 0;j < list_tasks.size();j++){
						List<TaskInstance> list_task = list_tasks.get(j);
						for(int t = 0;t < list_task.size();t++){
							String activityid = list_task.get(t).getActivityDefId();//节点ID
							//过滤起草节点。当前节点属于第一个节点，不发送邮件
							if(!EmailConst.list_firstActivityid.toString().contains(activityid)){
								String processid = list_task.get(t).getProcessInstId();//流程实例ID
								String taskId = list_task.get(t).getId();//任务ID
								String taskTarget = list_task.get(t).getTarget();//当前任务办理人
								Timestamp taskBeginTime = list_task.get(t).getBeginTime();//任务开始时间
								String taskBeginTimeStr = sdf.format(taskBeginTime);
								ProcessInstance processInstance = SDK.getProcessAPI().getInstanceById(processid);
								String processTitle = processInstance.getTitle();//流程标题
								String processCreater = processInstance.getCreateUser();//流程创建者
								String processDefid = processInstance.getProcessDefId();//流程定义ID
								String parentProcessId = processInstance.getParentProcessInstId();//父流程实例ID
								if(!UtilString.isEmpty(parentProcessId)){
									ProcessInstance parentProcessInstance = SDK.getProcessAPI().getInstanceById(parentProcessId);
									processCreater = parentProcessInstance.getCreateUser();
								}
								//流程、任务相关信息集合
								Map<String,String> map_task = new HashMap<String,String>();
								map_task.put("processid", processid);
								map_task.put("processDefid", processDefid);
								map_task.put("parentProcessId",parentProcessId);
								map_task.put("processCreater", processCreater);
								map_task.put("processTitle", processTitle);

								map_task.put("taskId", taskId);
								map_task.put("taskTarget", taskTarget);
								map_task.put("activityid", activityid);
								map_task.put("taskBeginTime", taskBeginTimeStr);

								map.put(processCreater+processid, map_task);
							}
						}
					}
				}
				//固定词组转换成实际值，并发送短信邮件
				if(map != null && map.size() > 0){
					for(String key:map.keySet()){
						Map<String,String> map_task = map.get(key);
						//判断是否符合发送邮件、短信条件:
						//当前时间距离CREATEACTIVITYID节点办理时间大于SENDDATE天

						//SENDDATE为空：不做判断；CREATEACTIVITYID为空：取任务创建时间
						boolean bol = true;
						if(!UtilString.isEmpty(SENDDATE)){
							String taskBeginTime  = "";
							if(UtilString.isEmpty(CREATEACTIVITYID)){
								//任务创建时间
								taskBeginTime = map_task.get("taskBeginTime");
							}else{
								//某节点办理时间（最后办理时间）PROCESSTYPEID
								taskBeginTime = emailUtil.getActivityIdTime(PROCESSTYPEID,CREATEACTIVITYID,conn);
							}
							//与当前时间相差天数
							//Calendar ca = Calendar.getInstance();
//							ca.add(Calendar.DATE, 6);// num为增加的天数，可以改变的
							//String nowdate = sdf.format(ca.getTime());
							//int days = emailUtil.betweenTwoDate(nowdate,taskBeginTime);
							int sendDays = Integer.parseInt(SENDDATE);
							long taskTime = sdf.parse(taskBeginTime).getTime()/1000;
							Date date = new Date();
							long nowTime = date.getTime()/1000;
							int day = emailUtil.GetDays(taskTime, nowTime);
							//获取当前日期是否为节假日
							int isWorkday = emailUtil.GetDays(nowTime, nowTime+60);
							if(isWorkday==0){
								bol = false;
							}else {
							if( day< sendDays){
								bol = false;
							}
							}
						}
						if(bol){
							//转换固定词组所需信息
							String processDefid = map_task.get("processDefid");
							String activityId = map_task.get("activityid");
							String processTitle = map_task.get("processTitle");
							String processCreater = map_task.get("processCreater");
							String taskTarget = map_task.get("taskTarget");

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
										UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefid, activityId);
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
					}
				}
			}
		}
	}
}
