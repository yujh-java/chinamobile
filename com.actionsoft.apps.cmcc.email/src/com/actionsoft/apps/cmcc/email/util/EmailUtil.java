package com.actionsoft.apps.cmcc.email.util;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.apps.cmcc.email.EmailConst;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.bpms.util.UtilURL;
import com.actionsoft.sdk.local.SDK;
import com.google.gson.JsonObject;

import net.sf.json.JSONObject;

/**
 * 邮件应用公用方法
 * @author nch
 * @date 2017-9-22
 */
public class EmailUtil {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 查询邮件应用流程、节点的所有待办任务
	 */
	public List<List<TaskInstance>> getTaskForEmail(String processType,String activityids,Connection conn){
		List<List<TaskInstance>> list_result = new ArrayList<List<TaskInstance>>();
		List<BO> list = new ArrayList<BO>();
		if(!UtilString.isEmpty(processType) && processType.contains(",")){
			String[] processTypeArr = processType.split(",");
			for(int t = 0;t < processTypeArr.length; t++){
				String processTypeid = processTypeArr[t];
				List<BO> list_processData = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").addQuery("PROCESSTYPE = ", processTypeid).connection(conn).list();
				if(!UtilString.isEmpty(list_processData)){
					list.addAll(list_processData);
				}
			}
		}else if(!UtilString.isEmpty(processType)){
			list = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").addQuery("PROCESSTYPE = ", processType).connection(conn).list();
		}
		if(list != null && list.size() > 0){
			//流程类型对应多的流程定义ID(有子流程)
			for(int i = 0;i < list.size(); i++){
				String PROCESSDEFNID = list.get(i).getString("PROCESSDEFNID");//流程定义ID
				if(UtilString.isEmpty(activityids)){//节点ID为空，应用于流程所有节点
					List<TaskInstance> list_task = SDK.getTaskQueryAPI().processDefId(PROCESSDEFNID).list();
					if(!UtilString.isEmpty(list_task)){
						list_result.add(list_task);
					}
				}else{//取指定节点待办任务
					if(activityids.contains(",")){
						String[] activityIdArr = activityids.split(",");
						for(int j = 0;j<activityIdArr.length;j++){
							String activityid = activityIdArr[j];
							//节点是否是发起子流程节点。发起子流程节点，取子流程中所有节点
							if(EmailConst.map_activityis_processdefid.containsKey(activityid)){
								String sub_processDefid = EmailConst.map_activityis_processdefid.get(activityid);
								List<TaskInstance> list_task = SDK.getTaskQueryAPI().processDefId(sub_processDefid).connection(conn).list();
								if(!UtilString.isEmpty(list_task)){
									list_result.add(list_task);
								}
							}else{
								List<TaskInstance> list_task = SDK.getTaskQueryAPI().processDefId(PROCESSDEFNID).activityDefId(activityid).connection(conn).list();
								if(!UtilString.isEmpty(list_task)){
									list_result.add(list_task);
								}
							}

						}
					}else{
						//节点是否是发起子流程节点。发起子流程节点，取子流程中所有节点
						if(EmailConst.map_activityis_processdefid.containsKey(activityids)){
							String sub_processDefid = EmailConst.map_activityis_processdefid.get(activityids);
							List<TaskInstance> list_task = SDK.getTaskQueryAPI().processDefId(sub_processDefid).connection(conn).list();
							if(!UtilString.isEmpty(list_task)){
								list_result.add(list_task);
							}
						}else{
							List<TaskInstance> list_task = SDK.getTaskQueryAPI().processDefId(PROCESSDEFNID).activityDefId(activityids).connection(conn).list();
							if(!UtilString.isEmpty(list_task)){
								list_result.add(list_task);
							}
						}
					}
				}
			}
		}
		return list_result;
	}
	/**
	 * 根据配置模板，发送邮件短信
	 * @author nch
	 * @date 2017-9-22
	 * @param map_fixed
	 * @param EMAILID
	 * @param ISEMAIL
	 * @param ISMESSAGE
	 * @param conn
	 */
	public void sendEmail(Map<String,String> map_fixed,String EMAILID,String ISEMAIL,String ISMESSAGE,Connection conn){
		String EMAIL_TITLE = "";//邮件标题
		String EMAILTO = "";//收件人
		String EMAIL_CONTENT = "";//邮件正文
		String SHORTMESSAGE = "";//短信正文
		String mobile = "";//短信接收手机号
		//根据邮件模板ID，查询模板信息
		List<BO> list_email = SDK.getBOAPI().query("BO_ACT_EMAIL_TEMPLETE",true).addQuery("ID = ", EMAILID).connection(conn).list();
		if(list_email != null && list_email.size() > 0){
			EMAIL_TITLE = list_email.get(0).getString("EMAIL_TITLE");
			EMAIL_CONTENT = list_email.get(0).getString("EMAIL_CONTENT");
			SHORTMESSAGE = list_email.get(0).getString("SHORTMESSAGE");
			EMAILTO = list_email.get(0).getString("EMAILTO");
		}
		//固定词组替换成实际值
		if(map_fixed != null && map_fixed.size() > 0){
			//处理流程创建人、任务办理人姓名，邮箱
			String processCreater = map_fixed.get("【PROCESSCREATER】");
			UserModel processCreaterModel = UserCache.getModel(processCreater);
			String processCreaterName = processCreaterModel.getUserName();
			String processCreaterEmail = processCreaterModel.getEmail();
			String processCreaterMobile = processCreaterModel.getMobile();
			map_fixed.put("【PROCESSCREATER】",processCreaterName);

			String taskTarget = map_fixed.get("【TASKTARGET】");
			UserModel taskTargetModel = UserCache.getModel(taskTarget);
			String taskTargetName = taskTargetModel.getUserName();
			String taskTargetEmail = taskTargetModel.getEmail();
			String taskTargetMobile = taskTargetModel.getMobile();
			map_fixed.put("【TASKTARGET】",taskTargetName);
			//处理收件人
			if("processCreater".equals(EMAILTO)){
				EMAILTO = processCreaterEmail;
				mobile = processCreaterMobile;
			}else if("taskTarget".equals(EMAILTO)){
				EMAILTO = taskTargetEmail;
				mobile = taskTargetMobile;
			}
			//处理其他固定词组
			for(String key:map_fixed.keySet()){
				if(!UtilString.isEmpty(EMAIL_TITLE)){
					EMAIL_TITLE = EMAIL_TITLE.replace(key, map_fixed.get(key));
				}
				if(!UtilString.isEmpty(EMAIL_CONTENT)){
					EMAIL_CONTENT = EMAIL_CONTENT.replace(key, map_fixed.get(key));
				}
				if(!UtilString.isEmpty(SHORTMESSAGE)){
					SHORTMESSAGE = SHORTMESSAGE.replace(key, map_fixed.get(key));
				}
			}
		}
		
		//---------------------------------------------------------------------//
		/*
		List<Map<String,String>> list3 = new ArrayList<Map<String,String>>();
		Map<String,String> map1 = new HashMap<String,String>();
		map1.put("EMAILTO", "1609261332@qq.com");
		map1.put("mobile", "18037778593");
		list3.add(map1);
		Map<String,String> map2 = new HashMap<String,String>();
		map2.put("EMAILTO", "justwuqian@163.com");
		map2.put("mobile", "13401125798");
		list3.add(map2);
		Map<String,String> map3 = new HashMap<String,String>();
		map3.put("EMAILTO", "397566732@qq.com");
		map3.put("mobile", "18210116430");
		list3.add(map3);
		Map<String,String> map4 = new HashMap<String,String>();
		map4.put("EMAILTO", "liliu1104@163.com");
		map4.put("mobile", "13699110983");
		list3.add(map4);
		
		Map<String,String> map5 = new HashMap<String,String>();
		map5.put("EMAILTO", "475703416@163.com");
		map5.put("mobile", "13021111134");
		list3.add(map4);
		
		for(int l = 0;l < list3.size();l++){
			Map<String,String> mapUser= list3.get(l);
			EMAILTO =  mapUser.get("EMAILTO");
			mobile = mapUser.get("mobile");
			//调用短信、邮件ASLP
			// 调用App 
			String sourceAppId = EmailConst.sourceAppId;
			// aslp服务地址 
			String aslp = EmailConst.stateAslp;
			Map<String, Object> params = new HashMap<String, Object>();//参数
			Map<String,String> map  = new HashMap<String,String>();
			map.put("ISEMAIL", ISEMAIL);
			map.put("ISMESSAGE", ISMESSAGE);
			map.put("TOEMAIL", EMAILTO);
			map.put("EMAIL_CONTENT", EMAIL_CONTENT);
			map.put("TITLE", EMAIL_TITLE);
			map.put("MOBILE", mobile);
			map.put("SHORTMESSAGE", SHORTMESSAGE);
			params.put("mapMsg", map);
			SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, params);
		}
		*/
		//---------------------------------------------------------------------//
		/*//调用短信、邮件ASLP
		// 调用App 
		String sourceAppId = EmailConst.sourceAppId;
		// aslp服务地址 
		String aslp = EmailConst.stateAslp;
		Map<String, Object> params = new HashMap<String, Object>();//参数
		Map<String,String> map  = new HashMap<String,String>();
		map.put("ISEMAIL", ISEMAIL);
		map.put("ISMESSAGE", ISMESSAGE);
		map.put("TOEMAIL", EMAILTO);
		map.put("EMAIL_CONTENT", EMAIL_CONTENT);
		map.put("TITLE", EMAIL_TITLE);
		map.put("MOBILE", mobile);
		map.put("SHORTMESSAGE", SHORTMESSAGE);
		params.put("mapMsg", map);
		SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, params);*/
	}
	/**
	 * 查询指定节点办理时间
	 * @author nch
	 * @date 2017-9-25
	 * @param PROCESSTYPEID
	 * @param activityid
	 * @param conn
	 * @param sdf
	 * @return
	 */
	public String getActivityIdTime(String PROCESSTYPEID,String activityid,Connection conn){
		String lastDate = null;
		List<BO> list_bo = new ArrayList<BO>();
		if(!UtilString.isEmpty(PROCESSTYPEID) && PROCESSTYPEID.contains(",")){
			String[] processTypeArr = PROCESSTYPEID.split(",");
			for(int t = 0;t < processTypeArr.length; t++){
				String processTypeid = processTypeArr[t];
				List<BO> list_processData = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").addQuery("PROCESSTYPE = ", processTypeid).connection(conn).list();
				if(!UtilString.isEmpty(list_processData)){
					list_bo.addAll(list_processData);
				}
			}
		}else if(!UtilString.isEmpty(PROCESSTYPEID)){
			list_bo = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").addQuery("PROCESSTYPE = ", PROCESSTYPEID).connection(conn).list();
		}
		
		if(list_bo != null && list_bo.size() > 0){
			for(int i = 0;i < list_bo.size();i++){
				String processDefid = list_bo.get(i).getString("PROCESSDEFNID");
				List<HistoryTaskInstance> list_task = SDK.getHistoryTaskQueryAPI().activityDefId(activityid).processDefId(processDefid).userTaskOfWorking().orderByEndTime().desc().list();
				if(list_task != null && list_task.size() > 0){
					Timestamp lastTime = list_task.get(0).getEndTime();
					lastDate = sdf.format(lastTime);
				}
			}
		}
		return lastDate;
	}
	/**
	 * 比较两日期大小
	 * @param date1
	 * @param date2
	 * @return date1 > date2 返回false
	 * @throws ParseException
	 * 
	 */
	public int betweenTwoDate(String date1,String date2){
		try {
			long date1Times = sdf.parse(date1).getTime();
			long date2Times = sdf.parse(date2).getTime();
			int days = (int) ((date1Times - date2Times)/(60*60*24*1000));
			return days;
		} catch (ParseException e) {
			e.printStackTrace(System.err);
			return 0;
		}
	}
	
	public  int GetDays(long taskTime,long nowTime){
		StringBuffer sbf = new StringBuffer();
		//String url = "http://10.2.5.187/spms/cmri/calendar/getday";
		String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.email", "Email_Holiday");
		sbf.append(url).append("?start="+taskTime+"&end="+nowTime);
		String str = UtilURL.get(sbf.toString());
		JSONObject json = JSONObject.fromObject(str);
		JSONObject datajson = (JSONObject) json.get("data");
		JSONObject time =  (JSONObject) datajson.get("list");
		int day = time.getInt("workingday");
		System.err.println("day="+day);
		return day;
	}
	
	public static void main(String[] args) {
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, 6);// num为增加的天数，可以改变的
		String date = sdf.format(ca.getTime());
		System.out.println(date);
	}
}
