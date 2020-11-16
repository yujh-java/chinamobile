package com.actionsoft.apps.cmcc.integration.util;
import java.sql.Connection;
/**
 * 公共方法类
 * @author nch
 * @date 20170622
 */
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.actionsoft.apps.cmcc.integration.CMCCConst;
import com.actionsoft.bpms.api.Utils;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.cache.util.UserTaskDefUtil;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.run.TaskCommentModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.HistoryTaskQueryAPI;
import com.actionsoft.sdk.local.api.TaskQueryAPI;
import com.alibaba.fastjson.JSONArray;

import net.sf.json.JSONObject;

public class PubUtilSystem {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 根据用户账号，查询待办任务
	 * @param userid
	 * @return
	 */
	public static String getTaskOfWorking(Connection conn,String userid,String startTime,String endTime,String processType,String taskTitle,int start_sizte,int end_size,String processApp,String IOSID,String system){
		TaskQueryAPI taskQueryApi = null;
		if(UtilString.isEmpty(userid)){
//			taskQueryApi = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn).addQuery("TASKSTATE != ", "4");
			taskQueryApi = SDK.getTaskQueryAPI().userTaskOfWorking().supportDelegateTask().connection(conn).addQuery("TASKSTATE != ", "4");
		}else{
//			taskQueryApi = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn).target(userid).addQuery("TASKSTATE != ", "4");
			taskQueryApi = SDK.getTaskQueryAPI().userTaskOfWorking().supportDelegateTask().connection(conn).target(userid).addQuery("TASKSTATE != ", "4");
		}
		if(!UtilString.isEmpty(IOSID)){
			taskQueryApi.IOS(IOSID);
		}
		return getTaskresult(conn,userid,startTime,endTime,processType,taskTitle,taskQueryApi,start_sizte,end_size,processApp,system);
	}
	/**
	 * 根据用户账号，查询已办任务
	 * @param userid
	 * @return
	 */
	public static String getTaskComplete(Connection conn,String userid,String startTime,String endTime,String processType,String taskTitle,int start_sizte,int end_size,String processApp,String IOSID,String system,String processStates,String wfhTask){
		HistoryTaskQueryAPI hisTaskQueryApi = null;
		if(UtilString.isEmpty(userid)){
			hisTaskQueryApi = SDK.getHistoryTaskQueryAPI().userTaskOfWorking().connection(conn);
		}else{
			hisTaskQueryApi = SDK.getHistoryTaskQueryAPI().userTaskOfWorking().connection(conn).target(userid);
		}
		if(!UtilString.isEmpty(IOSID)){
			hisTaskQueryApi.IOS(IOSID);
		}
		return getHisTaskresult(conn,userid,startTime,endTime,processType,taskTitle,hisTaskQueryApi,start_sizte,end_size,processApp,system,processStates,wfhTask,IOSID);
	}
	/**
	 * 根据用户账号，查询已阅任务
	 * @param userid
	 * @return
	 */
	public static String getTaskNotification(Connection conn,String userid,String startTime,String endTime,String processType,String taskTitle,int start_sizte,int end_size,String processApp,String IOSID,String system,String processStates,String wfhtask){
		HistoryTaskQueryAPI hisTaskQueryApi = null;
		if(UtilString.isEmpty(userid)){
			hisTaskQueryApi = SDK.getHistoryTaskQueryAPI().userTaskOfNotification().connection(conn);
		}else{
			hisTaskQueryApi = SDK.getHistoryTaskQueryAPI().userTaskOfNotification().connection(conn).target(userid);
		}
		if(!UtilString.isEmpty(IOSID)){
			hisTaskQueryApi.IOS(IOSID);
		}
		return getHisTaskresult(conn,userid,startTime,endTime,processType,taskTitle,hisTaskQueryApi,start_sizte,end_size,processApp,system,processStates,wfhtask,IOSID);
	}
	/**
	 * 通知类待办查询
	 * @param userid
	 * @return
	 */
	public static String getTaskOfNotification(Connection conn,String userid,String startTime,String endTime,String processType,String taskTitle,int start_sizte,int end_size,String processApp,String IOSID,String system){
		TaskQueryAPI taskQueryApi = null;
		if(UtilString.isEmpty(userid)){
			taskQueryApi = SDK.getTaskQueryAPI().userTaskOfNotification().connection(conn);
		}else{
			taskQueryApi = SDK.getTaskQueryAPI().userTaskOfNotification().connection(conn).target(userid);
		}
		if(!UtilString.isEmpty(IOSID)){
			taskQueryApi.IOS(IOSID);
		}
		return getTaskresult(conn,userid,startTime,endTime,processType,taskTitle,taskQueryApi,start_sizte,end_size,processApp,system);
	}
	/**
	 * 
	 * @param userid
	 * @param startTime
	 * @param endTime
	 * @param processType
	 * @param taskTitle
	 * @param start_sizte
	 * @param end_size
	 * @param processApp
	 * @return
	 */
	public static List<TaskInstance> getJqlHisTask(Connection conn,String userid,String startTime,String endTime,String processType,String taskTitle,int start_sizte,int end_size,String processApp,String IOSID){
		List<TaskInstance> list = new ArrayList<TaskInstance>();
		TaskQueryAPI taskQueryApi = null;

		if(UtilString.isEmpty(userid)){
			taskQueryApi = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn);

		}else{
			taskQueryApi = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn).target(userid);
			//		.addQuery("TASKSTATE = ", "4");
		}
		
		//chenxf add 20180920
		if(!UtilString.isEmpty(IOSID)){
			taskQueryApi.IOS(IOSID);
		}
		
		if(!UtilString.isEmpty(startTime)){
			Date startDate = null;
			try {
				startDate = sdf.parse(startTime);
			} catch (ParseException e) {
				e.printStackTrace(System.err);
			}
			taskQueryApi = taskQueryApi.beginAfter(startDate);
		}
		if(!UtilString.isEmpty(endTime)){
			Date endDate = null;
			try {
				endDate = sdf.parse(startTime);
			} catch (ParseException e) {
				e.printStackTrace(System.err);
			}
			taskQueryApi = taskQueryApi.beginBefore(endDate);
		}
		if(!UtilString.isEmpty(taskTitle)){
			taskQueryApi = taskQueryApi.titleLike("%"+taskTitle+"%");
		}
		if(!UtilString.isEmpty(processType)){
			List<RowMap> list_processDefid = DBSql.getMaps(conn,"SELECT PROCESSDEFNID FROM BO_ACT_PROCESS_DATA WHERE PROCESSTYPE=?", new Object[]{processType});
			if(list_processDefid != null && list_processDefid.size() > 0){
				for(int j = 0;j<list_processDefid.size();j++){
					String processDefid  = list_processDefid.get(j).getString("PROCESSDEFNID");
					if(!UtilString.isEmpty(processDefid)){
						taskQueryApi = taskQueryApi.processDefId(processDefid);
						if(!UtilString.isEmpty(processApp)){
							List<BO> listprocessDefid = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").connection(conn).addQuery("PROCESSAPP = ", processApp).list();
							if(listprocessDefid != null && listprocessDefid.size() > 0){
								for(int i = 0;i<listprocessDefid.size();i++){
									String PROCESSDEFNID = listprocessDefid.get(i).getString("PROCESSDEFNID");
									if(!UtilString.isEmpty(PROCESSDEFNID)){
										List<TaskInstance> list_processApp = taskQueryApi.processDefId(PROCESSDEFNID).connection(conn).addQuery("TASKSTATE = ", "4").list();
										list.addAll(list_processApp);
										List<TaskInstance> list_processApp2 = taskQueryApi.processDefId(PROCESSDEFNID).connection(conn).addQuery("TASKSTATE = ", "11").list();
										list.addAll(list_processApp2);
									}
								}
							}
						}else{
							List<TaskInstance> list_processApp = taskQueryApi.connection(conn).addQuery("TASKSTATE = ", "4").list();
							list.addAll(list_processApp);
							List<TaskInstance> list_processApp2 = taskQueryApi.connection(conn).addQuery("TASKSTATE = ", "11").list();
							list.addAll(list_processApp2);
						}
					}
				}
			}
		}else{
			if(!UtilString.isEmpty(processApp)){
				List<BO> listprocessDefid = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").connection(conn).addQuery("PROCESSAPP = ", processApp).list();
				if(listprocessDefid != null && listprocessDefid.size() > 0){
					for(int i = 0;i<listprocessDefid.size();i++){
						String PROCESSDEFNID = listprocessDefid.get(i).getString("PROCESSDEFNID");
						if(!UtilString.isEmpty(PROCESSDEFNID)){
							List<TaskInstance> list_processApp1 = taskQueryApi.processDefId(PROCESSDEFNID).connection(conn).addQuery("TASKSTATE = ", "4").list();
							List<TaskInstance> list_processApp2 = taskQueryApi.processDefId(PROCESSDEFNID).connection(conn).addQuery("TASKSTATE = ", "11").list();
							list.addAll(list_processApp1);
							list.addAll(list_processApp2);
						}
					}
				}
			}else{
				List<TaskInstance> list_processApp = taskQueryApi.connection(conn).addQuery("TASKSTATE = ", "4").list();
				list.addAll(list_processApp);
				List<TaskInstance> list_processApp2 = taskQueryApi.connection(conn).addQuery("TASKSTATE = ", "11").list();
				list.addAll(list_processApp2);
			}
		}
		List<TaskInstance> newListTask = new ArrayList<TaskInstance>();
		if(list != null && list.size() > 0){
			for(int j = 0;j<list.size();j++){
				String taskid2 = list.get(j).getId();
				List<TaskInstance> list2 = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn).parentTaskInstId(taskid2).list();
				if(list2 != null && list2.size() > 0){
					newListTask.add(list.get(j));
				}
			}
		}
		return newListTask;
	}
	/**
	 * 查询历史任务
	 * @param userid
	 * @param startTime
	 * @param endTime
	 * @param processType
	 * @param taskTitle
	 * @param taskQueryApi
	 * @return
	 */
	public static String getHisTaskresult(Connection conn,String userid,String startTime,String endTime,String processType,String taskTitle,HistoryTaskQueryAPI taskQueryApi,int start_sizte,int end_size,String processApp,String system,String processStates,String wfhTask,String IOSID){
		JSONObject jsonResult = new JSONObject();
		jsonResult.put("isSuccess", false);
		jsonResult.put("msg", "");
		jsonResult.put("size", 0);
		jsonResult.put("data", "");
		//根据条件获取已办任务集合
		List<HistoryTaskInstance> list= new ArrayList<HistoryTaskInstance>();
		if(!UtilString.isEmpty(startTime)){
			Date startDate = null;
			try {
				startDate = sdf.parse(startTime);
			} catch (ParseException e) {
				e.printStackTrace(System.err);
			}
			taskQueryApi = taskQueryApi.beginBefore(startDate);
		}
		if(!UtilString.isEmpty(endTime)){
			Date endDate = null;
			try {
				endDate = sdf.parse(endTime);
			} catch (ParseException e) {
				e.printStackTrace(System.err);
			}
			taskQueryApi = taskQueryApi.beginAfter(endDate);
		}
		if(!UtilString.isEmpty(taskTitle)){
			taskQueryApi = taskQueryApi.titleLike("%"+taskTitle+"%");
		}
		if(!UtilString.isEmpty(processType)){
			List<RowMap> list_processDefid = DBSql.getMaps(conn,"SELECT PROCESSDEFNID FROM BO_ACT_PROCESS_DATA WHERE PROCESSTYPE=?", new Object[]{processType});
			if(list_processDefid != null && list_processDefid.size() > 0){
				for(int j = 0;j<list_processDefid.size();j++){
					String processDefid  = list_processDefid.get(j).getString("PROCESSDEFNID");
					if(!UtilString.isEmpty(processDefid)){
						taskQueryApi = taskQueryApi.processDefId(processDefid);
						if(!UtilString.isEmpty(processApp)){
							List<BO> listprocessDefid = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").connection(conn).addQuery("PROCESSAPP = ", processApp).list();
							if(listprocessDefid != null && listprocessDefid.size() > 0){
								for(int i = 0;i<listprocessDefid.size();i++){
									String PROCESSDEFNID = listprocessDefid.get(i).getString("PROCESSDEFNID");
									if(!UtilString.isEmpty(PROCESSDEFNID)){
										List<HistoryTaskInstance> list_processApp = taskQueryApi.connection(conn).processDefId(PROCESSDEFNID).list();
										list.addAll(list_processApp);
									}
								}
							}
						}else{
							list = taskQueryApi.list();
						}
					}
				}
			}
		}else{
			if(!UtilString.isEmpty(processApp)){
				List<BO> listprocessDefid = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").connection(conn).addQuery("PROCESSAPP = ", processApp).list();
				if(listprocessDefid != null && listprocessDefid.size() > 0){
					for(int i = 0;i<listprocessDefid.size();i++){
						String PROCESSDEFNID = listprocessDefid.get(i).getString("PROCESSDEFNID");
						if(!UtilString.isEmpty(PROCESSDEFNID)){
							List<HistoryTaskInstance> list_processApp = taskQueryApi.connection(conn).processDefId(PROCESSDEFNID).list();
							list.addAll(list_processApp);
						}
					}
				}
			}else{
				list = taskQueryApi.list();
			}
		}
		JSONArray arr = new JSONArray();
		int end_i = 0;
		//根据条件获取加签任务几乎
		//chenxf modify 2018-07-20
		List<TaskInstance> listJqlHisTask = null;
		if(wfhTask.equals("2")){
			System.err.println("========查询已办。。。============");
			listJqlHisTask = getJqlHisTask(conn,userid,startTime,endTime,processType,taskTitle,start_sizte,end_size,processApp,IOSID);
		}
		//过滤历史任务
		List<Map<String,Object>> newlist = FilterlistutilSystem.filterHisList(list,listJqlHisTask,system,processStates);
		if(newlist.size() > 0 &&newlist != null){
			int start_i = 0;
			end_i = newlist.size();
			if(end_size >= 0 && end_i > end_size){
				start_i = start_sizte;
				end_i = end_size+1;
			}else if(end_i == end_size){
				start_i = start_sizte;
				end_i = end_size;
			}else if(end_i < end_size){
				start_i = start_sizte;
			}
			for(int i = start_i;i<end_i;i++){
				JSONObject json = new JSONObject();
				String task_id = (String) newlist.get(i).get("ID");//任务实例ID
				String process_id = (String) newlist.get(i).get("PROCESSINSTID");//流程实例ID
				int taskState = (int) newlist.get(i).get("STATE");//任务类型
				String title = SDK.getProcessAPI().getInstanceById(process_id).getTitle();//任务标题
				//chenxf add 2018-06-05
				//过滤《重新激活》的标题字
				if(title != null 
						&& !"".equals(title) 
						&& title.indexOf("(重新激活)") != -1){
					
					title = title.replaceAll("重新激活", "");
					title = title.replaceAll("\\(|\\)", "");
				}
				Timestamp begintime = (Timestamp) newlist.get(i).get("BEGINTIME");//任务创建时间

				Timestamp endtime = (Timestamp) newlist.get(i).get("ENDTIME");//任务结束时间
				String parentTaskID = (String) newlist.get(i).get("PARENTTASKINSTID");//父任务任务ID
				String task_title = (String) newlist.get(i).get("TITLE");//任务标题
				String begintimeStr = null;
				if(!UtilString.isEmpty(begintime)){
					begintimeStr = sdf.format(begintime);
				}
				String endtimeStr = null;
				if(!UtilString.isEmpty(endtime)){
					endtimeStr = sdf.format(endtime);
				}
				String processAppMsg = "";
				String processTypeID = "";
				String processTypeName = "";
				String processDefid = (String) newlist.get(i).get("PROCESSDEFID");
				List<BO> list_processData = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").connection(conn).addQuery("PROCESSDEFNID = ", processDefid).list();
				if(list_processData != null && list_processData.size() > 0){
					processAppMsg = list_processData.get(0).getString("PROCESSAPP");
					processTypeName = list_processData.get(0).getString("PROCESSNAME");
					processTypeID = list_processData.get(0).getString("PROCESSTYPE");
				}
				String owner = (String) newlist.get(i).get("OWNER");//创建人账号
				UserModel userModel =  UserCache.getModel(owner);
				String ownerName = "";
				if(userModel != null){
					ownerName = userModel.getUserName();
				}
				String activityId = (String) newlist.get(i).get("ACTIVITYDEFID");
				UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefid, activityId);
				String activityName = "";
				if(taskModel != null){
					activityName = taskModel.name;//节点名称
				}
				/*//流程当前所在节点名称
				String processCurrentName="";
				List<TaskInstance> processCurrenttask = SDK.getTaskQueryAPI().processInstId(process_id).list();
				if(processCurrenttask.size()>0)){
					String currentActivityId = processCurrenttask.get(0).getActivityDefId();
					UserTaskModel currentTaskModel = UserTaskDefUtil.getModel(processDefid, currentActivityId);
					if(currentTaskModel != null){
						processCurrentName = currentTaskModel.name;//节点名称
					}
				}*/
				//流程信息
				String processCreater = "";//流程创建人
				Timestamp processCteateTime = null;//流程创建时间
				Timestamp processEndTime = null;//流程结束时间
				String processId = (String) newlist.get(i).get("PROCESSINSTID");
				ProcessInstance processInstance = SDK.getProcessAPI().getInstanceById(processId);
				String parentProcessId = processInstance.getParentProcessInstId();
				
				String version_id = "";//获取流程快照版本号
				int status = 0;//主流程结束状态：1：结束，0：未结束
				if(!UtilString.isEmpty(parentProcessId)){
					ProcessInstance parentProcessInstance = SDK.getProcessAPI().getInstanceById(parentProcessId);
					processCreater = parentProcessInstance.getCreateUser();
					processCteateTime = parentProcessInstance.getCreateTime();
					processEndTime = parentProcessInstance.getEndTime();
					//父流程存在，判断父流程是否结束，结束取父流程快照版本号，流程实例ID为父流程实例ID；未结束快照版本号为空
					//判断父流程是否结束
					boolean bol_isend = parentProcessInstance.isEnd();
					if(bol_isend){//流程实例结束
						process_id = parentProcessId;
						status = 1;
					}
				}else{
					processCreater = processInstance.getCreateUser();
					processCteateTime = processInstance.getCreateTime();
					processEndTime = processInstance.getEndTime();
					if(processInstance.isEnd()){//判断流程是否结束
						status = 1;
					}
				}
				if(null==parentProcessId){
					parentProcessId="";
				}
				if(status==1){
					//流程结束，获取流程版本号
					//判断userid是否属于研究院
					/*boolean is_yjy = SnapshotForm.userIsYjy(userid);
					if(is_yjy){
						//判断流程类型是否属于DiffFormSnapshot
						ProcessInstance snapshot_process = SDK.getProcessAPI().getInstanceById(process_id);
						String processDefid_parentPro = snapshot_process.getProcessDefId();
						if(CMCCConst.DiffFormSnapshot.toString().contains(processDefid_parentPro)){
							//查询研究院人生成的表单快照ID
							String yjy_deptid = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "YJY_DEPTID");
							Map<String,String> map_version = SnapshotForm.getSnapshotHtml(conn,process_id,yjy_deptid);
							if(map_version != null && map_version.size() > 0){
								version_id = map_version.get("version_id");
								process_id = map_version.get("process_id");
							}
						}
					}*/
					//查询表单快照
					if(UtilString.isEmpty(version_id)){
						version_id = SnapshotForm.getSnapshotEdition(conn,process_id);
					}
				}
				UserModel processUserModel = UserCache.getModel(processCreater);
				String processCreaterName = "";
				if(processUserModel != null){
					processCreaterName = processUserModel.getUserName();
				}
				String processCteateTimeStr = null;
				if(!UtilString.isEmpty(processCteateTime)){
					processCteateTimeStr = sdf.format(processCteateTime);
				}
				String processEndTimeStr = null;
				if(!UtilString.isEmpty(processEndTime)){
					processEndTimeStr = sdf.format(processEndTime);
				}

				if(taskState == 11){
					//任务为加签或阅办类任务
					//父任务状态
					TaskInstance parent_taskinstance = SDK.getTaskAPI().getInstanceById(parentTaskID);
					if(parent_taskinstance != null){
						List<BO> list_processStep = SDK.getBOAPI().query("BO_ACT_EXBUTTON_MEMO_DATA").connection(conn).addQuery("PROCESSACTIVITYID = ", activityId).list();
						if(list_processStep != null && list_processStep.size() > 0){
							int parent_TaskState =  parent_taskinstance.getState();//父任务状态
							if(parent_TaskState!=11){
								if(!UtilString.isEmpty(task_title) && task_title.contains("(加签)")){
									activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME1");
								}else if(!UtilString.isEmpty(task_title) && task_title.contains("(阅办)")){
									activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME1");
								}
							}else{
								String parent_TaskTitle = parent_taskinstance.getTitle();//父任务任务标题
								if(!UtilString.isEmpty(parent_TaskTitle) && !UtilString.isEmpty(task_title)){
									if(parent_TaskTitle.contains("(加签)")
											&&  task_title.contains("(加签)")){
										activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME2");
									}else if(parent_TaskTitle.contains("(阅办)")
											&&  task_title.contains("(阅办)")){
										activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME2");
									}else if(parent_TaskTitle.contains("(加签)")
											&&  task_title.contains("(阅办)")){
										activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME1");
									}else if(parent_TaskTitle.contains("(阅办)")
											&&  task_title.contains("(加签)")){
										activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME1");
									}
								}
							}
						}

					}
				}
				
				json.put("processCreater", processCreaterName);
				json.put("processCteateTime", processCteateTimeStr);
				json.put("processEndTime", processEndTimeStr);

				json.put("task_id", task_id);
				json.put("process_id", process_id);
				json.put("process_parent_id", parentProcessId);//预算新加 的参数
				/**
				 * 增加企标打开表单方式
				 */
				String openTypeStr = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "OPEN_TYPE");
				JSONObject openTypeList =JSONObject.fromObject(openTypeStr);
				String openType ="";
				try {
					openType = openTypeList.getString(processDefid);
				} catch (Exception e) {
				}
				json.put("openType", openType);
				json.put("taskState", 2);
				json.put("taskTitle", title);
				json.put("beginTime", begintimeStr);
				json.put("endTime", endtimeStr);
				json.put("owner", ownerName);
				json.put("ownerid", owner);
				json.put("processApp", processAppMsg);
				json.put("processType", processTypeID);
				json.put("processTypeName", processTypeName);
				json.put("statename", activityName);
				json.put("version_id", version_id);
				json.put("status", status);
				arr.add(json);
			}
		}
		jsonResult.put("isSuccess", true);
		jsonResult.put("msg", "ok");
		jsonResult.put("size", newlist.size());
		jsonResult.put("data", arr.toString());
		return jsonResult.toString();
	}
	/**
	 * 查询待办任务
	 * @param userid
	 * @param startTime
	 * @param endTime
	 * @param processType
	 * @param taskTitle
	 * @param taskQueryApi
	 * @return
	 */
	public static String getTaskresult(Connection conn,String userid,String startTime,String endTime,String processType,String taskTitle,TaskQueryAPI taskQueryApi,int start_sizte,int end_size,String processApp,String system){
		JSONObject jsonResult = new JSONObject();
		jsonResult.put("isSuccess", false);
		jsonResult.put("msg", "");
		jsonResult.put("size", 0);
		jsonResult.put("data", "");
		List<TaskInstance> list= new ArrayList<TaskInstance>();
		if(!UtilString.isEmpty(startTime)){
			Date startDate = null;
			try {
				startDate = sdf.parse(startTime);
			} catch (ParseException e) {
				e.printStackTrace(System.err);
			}
			taskQueryApi = taskQueryApi.beginBefore(startDate);
		}
		if(!UtilString.isEmpty(endTime)){
			Date endDate = null;
			try {
				endDate = sdf.parse(endTime);
			} catch (ParseException e) {
				e.printStackTrace(System.err);
			}
			taskQueryApi = taskQueryApi.beginAfter(endDate);
		}
		if(!UtilString.isEmpty(taskTitle)){
			taskQueryApi = taskQueryApi.titleLike("%"+taskTitle+"%");
		}
		if(!UtilString.isEmpty(processType)){
			List<RowMap> list_processDefid = DBSql.getMaps(conn,"SELECT PROCESSDEFNID FROM BO_ACT_PROCESS_DATA WHERE PROCESSTYPE=?", new Object[]{processType});
			if(list_processDefid != null && list_processDefid.size() > 0){
				for(int j = 0;j<list_processDefid.size();j++){
					String processDefid  = list_processDefid.get(j).getString("PROCESSDEFNID");
					if(!UtilString.isEmpty(processDefid)){
						taskQueryApi = taskQueryApi.processDefId(processDefid);
						if(!UtilString.isEmpty(processApp)){
							List<BO> listprocessDefid = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").connection(conn).addQuery("PROCESSAPP = ", processApp).list();
							if(listprocessDefid != null && listprocessDefid.size() > 0){
								for(int i = 0;i<listprocessDefid.size();i++){
									String PROCESSDEFNID = listprocessDefid.get(i).getString("PROCESSDEFNID");
									if(!UtilString.isEmpty(PROCESSDEFNID)){
										List<TaskInstance> list_processApp = taskQueryApi.processDefId(PROCESSDEFNID).connection(conn).list();
										list.addAll(list_processApp);
									}
								}
							}
						}else{
							list = taskQueryApi.list();
						}
					}
				}
			}
		}else{
			if(!UtilString.isEmpty(processApp)){
				List<BO> listprocessDefid = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").connection(conn).addQuery("PROCESSAPP = ", processApp).list();
				if(listprocessDefid != null && listprocessDefid.size() > 0){
					for(int i = 0;i<listprocessDefid.size();i++){
						String PROCESSDEFNID = listprocessDefid.get(i).getString("PROCESSDEFNID");
						if(!UtilString.isEmpty(PROCESSDEFNID)){
							List<TaskInstance> list_processApp = taskQueryApi.processDefId(PROCESSDEFNID).connection(conn).list();
							list.addAll(list_processApp);
						}
					}
				}
			}else{
				list = taskQueryApi.list();
			}
		}
		List<TaskInstance> newListTask = new ArrayList<TaskInstance>();
		if(list != null && list.size() > 0){
			for(int j = 0;j<list.size();j++){
				String taskid2 = list.get(j).getId();
				String processDefId =  list.get(j).getProcessDefId();
				List<BO> list_processData = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").connection(conn).addQuery("PROCESSDEFNID = ", processDefId).list();
				String processtype = "";
				if(list_processData != null && list_processData.size() > 0){
//					processtype = list_processData.get(0).getString("PROCESSTYPE");
					//chenxf modify 20180808
					processtype = list_processData.get(0).getString("SYSTEM");
				}
				List<TaskInstance> list2 = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn).parentTaskInstId(taskid2).list();
				if(list2 == null || list2.size() == 0){
					if(!UtilString.isEmpty(processtype)){
						if(processtype.contains(system)&&!UtilString.isEmpty(system)){
							newListTask.add(list.get(j));
						}
					}
				}
			}
		}
		list = newListTask;
		JSONArray arr = new JSONArray();
		int end_i = 0;
		if(list.size() > 0 && list != null){
			int start_i = 0;
			end_i = list.size();
			if(end_size >= 0 && end_i > end_size){
				start_i = start_sizte;
				end_i = end_size + 1;
			}else if(end_i == end_size){
				start_i = start_sizte;
				end_i = end_size;
			}else if(end_i < end_size){
				start_i = start_sizte;
			}
			for(int i = start_i;i<end_i;i++){
				JSONObject json = new JSONObject();
				String task_id = list.get(i).getId();//任务实例ID
				String process_id = list.get(i).getProcessInstId();//流程实例ID
				int taskState = list.get(i).getState();//任务类型
				String title = SDK.getProcessAPI().getInstanceById(process_id).getTitle();//任务标题
				//chenxf add 2018-06-05
				//过滤《重新激活》的标题字
				if(title != null 
						&& !"".equals(title) 
						&& title.indexOf("(重新激活)") != -1){
					
					title = title.replaceAll("重新激活", "");
					title = title.replaceAll("\\(|\\)", "");
				}
				Timestamp begintime = list.get(i).getBeginTime();//任务创建时间
				Timestamp endtime = list.get(i).getEndTime();//任务结束时间
				String task_title = list.get(i).getTitle();//任务标题
				String begintimeStr = null;
				if(!UtilString.isEmpty(begintime)){
					begintimeStr = sdf.format(begintime);
				}
				String endtimeStr = null;
				if(!UtilString.isEmpty(endtime)){
					endtimeStr = sdf.format(endtime);
				}
				String processAppMsg = "";
				String processTypeName = "";
				String processTypeID = "";
				String processDefid = list.get(i).getProcessDefId();
				List<BO> list_processData = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").connection(conn).addQuery("PROCESSDEFNID = ", processDefid).list();
				if(list_processData != null && list_processData.size() > 0){
					processAppMsg = list_processData.get(0).getString("PROCESSAPP");
					processTypeName = list_processData.get(0).getString("PROCESSNAME");
					processTypeID = list_processData.get(0).getString("PROCESSTYPE");
				}
				String parentTaskID = list.get(i).getParentTaskInstId();//父任务实例ID
				ProcessInstance processinstance= SDK.getProcessAPI().getInstanceById(process_id);
				String parentProcessID = processinstance.getParentProcessInstId();//父流程实例ID
				String owner = "";//创建人账号
				//为流程第一个节点任务&&父流程实例ID不为空(为子流程)
				if("00000000-0000-0000-0000-000000000000".equals(parentTaskID)
						&& !UtilString.isEmpty(parentProcessID)){
					/**
					 * 子流程任务，创建者是父流程任务，启动子流程任务节点的上一节点任务的owner
					 */
					String processParentTaskID = processinstance.getParentTaskInstId();//创建流程任务实例ID
					TaskInstance his_TaskInstance1 = SDK.getTaskAPI().getInstanceById(processParentTaskID);
					if(his_TaskInstance1 != null){
						String parentActiovtyType = his_TaskInstance1.getActivityType();
						if("callActivity".equals(parentActiovtyType)){//启动子流程任务节点
							TaskInstance his_TaskInstance2 = SDK.getTaskAPI().getInstanceById(his_TaskInstance1.getParentTaskInstId());
							if(his_TaskInstance2 != null){
								owner = his_TaskInstance2.getOwner();	
							}
						}
					}
				}else{
					String parentTarget = "";
					TaskInstance parent_taskinstance = SDK.getTaskAPI().getInstanceById(parentTaskID);
					if(parent_taskinstance != null){
						parentTarget = parent_taskinstance.getTarget();
					}
					if("00000000-0000-0000-0000-000000000000".equals(parentTaskID) || 
							!UtilString.isEmpty(parentTarget)){
						owner = list.get(i).getOwner();//创建人账号
					}else{
						//子流程结束创建的任务
						List<ProcessInstance> list_subprocessInstance = SDK.getProcessQueryAPI().connection(conn).parentProcessInstId(process_id).parentTaskInstId(parentTaskID).orderBy("ENDTIME").desc().list();
						if(list_subprocessInstance != null && list_subprocessInstance.size() >0){
							String sub_processId = list_subprocessInstance.get(0).getId();
							List<TaskCommentModel> list_TaskCommentModel = SDK.getProcessAPI().getCommentsById(sub_processId);
							if(list_TaskCommentModel != null && list_TaskCommentModel.size() > 0){
								//最后一条任务审批记录办理者，为当前owner
								owner = list_TaskCommentModel.get(list_TaskCommentModel.size()-1).getCreateUser();
							}
//							List<HistoryTaskInstance> list_subHisTaskInstance = SDK.getHistoryTaskQueryAPI().userTaskOfWorking().processInstId(sub_processId).orderByEndTime().desc().list();
//							if(list_subHisTaskInstance !=  null && list_subHisTaskInstance.size() > 0){
//								owner = list_subHisTaskInstance.get(0).getTarget();
//							}
						}
					}

				}
				//查询审批记录
				List<Map<String,Object>> list_taskConnent = Filterlistutil.getTaskComment(process_id);
				if(list_taskConnent != null && list_taskConnent.size() > 0){
					//审批记录中存在此任务ID，说明此任务有转办或加签操作
					//转办、加签操作任务，任务创建时间取记录最后结束时间
					//上一办理人取最后审批记录中最后任务记录创建人（前提：任务ID相同或子任务）
					String beforeTaskTime = "";//当前任务上一次出现时间
					for(int lastTaskTimeSize = list_taskConnent.size() - 1;lastTaskTimeSize>= 0;
							lastTaskTimeSize--){
						Map<String, Object> map_TaskTime = list_taskConnent.get(lastTaskTimeSize);
						if(map_TaskTime.get("taskId").equals(task_id)){
							beforeTaskTime = (String) map_TaskTime.get("taskId");
							break;
						}
					}
					if(!UtilString.isEmpty(beforeTaskTime)){
						List<HistoryTaskInstance> list_subTaskIds = SDK.getHistoryTaskQueryAPI().parentTaskInstId(task_id).userTaskOfWorking().connection(conn).list();
						for(int lastTaskTimeSize2 = list_taskConnent.size() - 1;lastTaskTimeSize2 >= 0;lastTaskTimeSize2-- ){
							Map<String, Object> map_TaskTime = list_taskConnent.get(lastTaskTimeSize2);
							String taskIdValue = (String) map_TaskTime.get("taskId");
							if(list_subTaskIds.toString().contains(taskIdValue)
									|| task_id.equals(taskIdValue)){
								begintime = (Timestamp) map_TaskTime.get("createTime");
								owner = (String) map_TaskTime.get("taskCommentCreater");
								if(!UtilString.isEmpty(begintime)){
									begintimeStr = sdf.format(begintime);
								}
								break;
							}
						}
					}
				}
				UserModel userModel =  UserCache.getModel(owner);
				String ownerName = "";
				if(userModel != null){
					ownerName = userModel.getUserName();
				}
				String activityId = list.get(i).getActivityDefId();
				UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefid, activityId);
				String activityName = "";
				if(taskModel != null ){
					activityName = taskModel.name;
				}//节点名称

				//流程信息
				String processCreater = "";//流程创建人
				Timestamp processCteateTime = null;//流程创建时间
				Timestamp processEndTime = null;//流程结束时间
				String processId = list.get(i).getProcessInstId();
				ProcessInstance processInstance = SDK.getProcessAPI().getInstanceById(processId);
				String parentProcessId = processInstance.getParentProcessInstId();
				if(!UtilString.isEmpty(parentProcessId)){
					ProcessInstance parentProcessInstance = SDK.getProcessAPI().getInstanceById(parentProcessId);
					processCreater = parentProcessInstance.getCreateUser();
					processCteateTime = parentProcessInstance.getCreateTime();
					processEndTime = parentProcessInstance.getEndTime();
				}else{
					processCreater = processInstance.getCreateUser();
					processCteateTime = processInstance.getCreateTime();
					processEndTime = processInstance.getEndTime();
				}
				if(null==parentProcessId){
					parentProcessId="";
				}
				UserModel processUserModel = UserCache.getModel(processCreater);
				String processCreaterName = "";
				if(processUserModel != null){
					processCreaterName = processUserModel.getUserName();
				}
				String processCteateTimeStr = null;
				if(!UtilString.isEmpty(processCteateTime)){
					processCteateTimeStr = sdf.format(processCteateTime);
				}
				String processEndTimeStr = null;
				if(!UtilString.isEmpty(processEndTime)){
					processEndTimeStr = sdf.format(processEndTime);
				}
				json.put("processCreater", processCreaterName);
				json.put("processCteateTime", processCteateTimeStr);
				json.put("processEndTime", processEndTimeStr);
				if(taskState == 11){
					//任务为加签或阅办类任务
					//父任务状态
					TaskInstance parent_taskinstance = SDK.getTaskAPI().getInstanceById(parentTaskID);
					if(parent_taskinstance != null){
						List<BO> list_processStep = SDK.getBOAPI().query("BO_ACT_EXBUTTON_MEMO_DATA").connection(conn).addQuery("PROCESSACTIVITYID = ", activityId).list();
						if(list_processStep != null && list_processStep.size() > 0){
							int parent_TaskState =  parent_taskinstance.getState();//父任务状态
							String parent_TaskTitle = parent_taskinstance.getTitle();//父任务任务标题
							if(parent_TaskState!=11){
								if(!UtilString.isEmpty(task_title) && task_title.contains("(加签)")){
									activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME1");
								}else if(!UtilString.isEmpty(task_title) && task_title.contains("(阅办)")){
									activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME1");
								}else if(!UtilString.isEmpty(task_title) && task_title.contains("(转办)")
										&& !UtilString.isEmpty(parent_TaskState) && task_title.contains("(加签)")){
									activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME1");
								}else if(!UtilString.isEmpty(task_title) && task_title.contains("(转办)")
										&& !UtilString.isEmpty(parent_TaskState) && task_title.contains("(阅办)")){
									activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME1");
								}
							}else{
								if(!UtilString.isEmpty(parent_TaskTitle) && !UtilString.isEmpty(task_title)){
									if(parent_TaskTitle.contains("(加签)")
											&&  task_title.contains("(加签)")){
										activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME2");
									}else if(parent_TaskTitle.contains("(阅办)")
											&&  task_title.contains("(阅办)")){
										activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME2");
									}else if(parent_TaskTitle.contains("(加签)")
											&&  task_title.contains("(阅办)")){
										activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME1");
									}else if(parent_TaskTitle.contains("(阅办)")
											&&  task_title.contains("(加签)")){
										activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME1");
									}else if(parent_TaskTitle.contains("(阅办)")
											&&  task_title.contains("(转办)")){
										activityName = list_processStep.get(0).getString("YB_ACTIVITYNAME2");
									}else if(parent_TaskTitle.contains("(加签)")
											&&  task_title.contains("(转办)")){
										activityName = list_processStep.get(0).getString("JQ_ACTIVITYNAME2");
									}
								}
							}
						}

					}
				}
				json.put("task_id", task_id);
				json.put("process_id", process_id);
				json.put("process_parent_id", parentProcessId);//预算新加 的参数
				/**
				 * 增加企标打开表单方式
				 */
				String openTypeStr = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "OPEN_TYPE");
				JSONObject openTypeList =JSONObject.fromObject(openTypeStr);
				String openType ="";
				try {
					openType = openTypeList.getString(processDefid);
				} catch (Exception e) {
				}
				json.put("openType", openType);
				json.put("taskState", taskState);
				json.put("taskTitle", title);
				json.put("beginTime", begintimeStr);
				json.put("endTime", endtimeStr);
				json.put("owner", ownerName);
				json.put("ownerid", owner);
				json.put("processApp", processAppMsg);
				json.put("processType", processTypeID);
				json.put("processTypeName", processTypeName);
				json.put("statename", activityName);
				json.put("version_id", "");
				arr.add(json);
			}
		}
		jsonResult.put("isSuccess", true);
		jsonResult.put("msg", "ok");
		jsonResult.put("size", list.size());
		jsonResult.put("data", arr.toString());
		return jsonResult.toString();
	}
}
