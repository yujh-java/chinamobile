package com.actionsoft.apps.cmcc.web;
/**
 * 流程跟踪web类
 * @author nch
 * @date 20170622
 */
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.actionsoft.apps.cmcc.util.CommontUtil;
import com.actionsoft.apps.cmcc.util.NoFilterSameStepOptionUtil;
import com.actionsoft.apps.cmcc.util.OptionUtil;
import com.actionsoft.apps.cmcc.util.PubSubProcessUtil;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.cache.util.UserTaskDefUtil;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.run.TaskCommentModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.commons.mvc.view.ActionWeb;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class SubProcessOptionWeb extends ActionWeb {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 立项流程跟踪
	 * @param processId
	 * @param processDefid
	 * @return
	 * nch 20170622
	 */
	public String getOptionHtmlForLx(String processId,String processDefid){

		Map<String, Object> map = new HashMap<String, Object>();
		String title = DBSql.getString("SELECT PROTRACKTITLE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?", new Object[]{processDefid});

		/**
		 * 主流程定义ID改成从维护表BO_ACT_PROCESS_DATA中获取，不在单独在常量中维护
		 * @update niech
		 * @date 20170921
		 */
		/*if(CmccConst.list_processDefid.toString().contains(processDefid)){
			//当前流程为立项主流程
			parentProcessId = processId;
		}else{//当前流程为子流程
			parentProcessId = DBSql.getString("SELECT PARENTPROCESSINSTID FROM WFC_PROCESS WHERE ID = ?", new Object[]{processId});
		}*/
		//从维护表BO_ACT_PROCESS_DATA中获取主流程定义ID，判断当前流程是主/子流程
		//主流程流程定义id的集合
		List<String> list_processDefid = new ArrayList<String>();
		List<BO> list_mainProcess = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").addQuery("ISMAIN = ", "是").list();
		if(list_mainProcess != null && list_mainProcess.size() > 0){
			for(int m = 0;m<list_mainProcess.size();m++){
				String main_processdefid = list_mainProcess.get(m).getString("PROCESSDEFNID");
				list_processDefid.add(main_processdefid);
			}
		}
		//主流程实例ID
		String parentProcessId = "";
		//chenxf add 2018-4-23
		//父流程定义ID
		String parentProcessDefId = "";
		if(list_processDefid != null && list_processDefid.toString().contains(processDefid)){
			//当前流程为立项主流程
			parentProcessId = processId;
		}else{
			//当前流程为子流程
			parentProcessId = DBSql.getString("SELECT PARENTPROCESSINSTID FROM WFC_PROCESS WHERE ID = ?", new Object[]{processId});
			//父流程定义ID
			parentProcessDefId = DBSql.getString("select PROCESSDEFID from WFC_PROCESS where ID = ?", new Object[]{parentProcessId});
			
		}
		/*
		 * 根据父流程实例ID查询出所有子流程的实例ID集合
		 */
		List<String> list_subProcessId = new ArrayList<String>();
		List<RowMap> list_subprocessId = DBSql.getMaps("SELECT ID FROM WFC_PROCESS WHERE PARENTPROCESSINSTID= ? ", new Object[]{parentProcessId});
		if(list_subprocessId != null && list_subprocessId.size() > 0){
			for(int i =0;i < list_subprocessId.size();i++){
				String subProcessId = list_subprocessId.get(i).getString("ID");//子流程实例ID
				list_subProcessId.add(subProcessId);//子流程实例ID集合
			}
		}

		/*
		 * 通过主流程定义ID，在扩展按钮维护表（BO_ACT_EXBUTTON_MEMO_DATA）中，
		 * 查出所有主流程维护数据
		 */
		List<BO> list_extend_memo = new ArrayList<BO>();
		if(!UtilString.isEmpty(parentProcessId)){
			//查询主流程定义ID
			String cmccprocessDefId = SDK.getProcessAPI().getInstanceById(parentProcessId).getProcessDefId();
			list_extend_memo = SDK.getBOAPI().query("BO_ACT_EXBUTTON_MEMO_DATA").addQuery("CMCCPROCESSDEFID = ", cmccprocessDefId).list();

		}
		/*
		 * 同上，通过子流程定义ID，在维护表中查出所有子流程维护数据
		 */
		if(list_subProcessId != null && list_subProcessId.size() >0){
			for(int i = 0 ; i < list_subProcessId.size();i++){
				//子流程实例ID
				String subprocessId = list_subProcessId.get(i);
				//子流程定义ID
				String cmccprocessDefId = SDK.getProcessAPI().getInstanceById(subprocessId).getProcessDefId();
				//合并主、子流程扩展按钮维护数据
				list_extend_memo.addAll(SDK.getBOAPI().query("BO_ACT_EXBUTTON_MEMO_DATA").addQuery("CMCCPROCESSDEFID = ", cmccprocessDefId).list());
			}
		}
		//节点ID，扩展按钮对应信息
		Map<String,BO> map_extend_memo = new HashMap<String,BO>();
		if(list_extend_memo != null && list_extend_memo.size() > 0){
			for(int i = 0;i<list_extend_memo.size();i++){
				BO bo = list_extend_memo.get(i);
				//部署的流程节点ID，包括会签、加签、转办等
				String cmcc_activityId = bo.getString("PROCESSACTIVITYID");
				map_extend_memo.put(cmcc_activityId, bo);
			}
		}
		//返回HTML
		StringBuffer sb_table = new StringBuffer();
		List<String> list_taskId = new ArrayList<String>();//任务ID,校验循环一次

		//查询审批记录
		//当前流程实例ID审批意见
		List<TaskCommentModel> list = new ArrayList<TaskCommentModel>();
		//主流程、子流程按照审批时间排序后的审批记录的集合
		list = OptionUtil.getTaskCommentModel(parentProcessId, list_subProcessId);
		
		Map<String,Map<String,String>> mapParenttask = new HashMap<String,Map<String,String>>();
		mapParenttask = PubSubProcessUtil.HisProcessMsg(list_taskId,list);
		//查询待办任务实例集合
		List<TaskInstance> list_task = OptionUtil.getWorkIngTaskModel(parentProcessId,list_subProcessId);
		if(list_task != null && list_task.size() > 0){
			//过滤拼接代办记录
			Map<String,Map<String,String>> todo_map = PubSubProcessUtil.UserTaskProcessMsg(list_taskId,list_task);
			if(todo_map != null){
				for(String todoTask:todo_map.keySet()){
					//记录中包含
					Map<String,String> hisTodouser_map = mapParenttask.get(todoTask);
					Map<String,String> todouser_map = todo_map.get(todoTask);
					if(mapParenttask != null && mapParenttask.containsKey(todoTask)){
						for(String activityname:todouser_map.keySet()){
							if(hisTodouser_map != null && hisTodouser_map.containsKey(activityname)){
								String hisTaskUserId = hisTodouser_map.get(activityname);
								String taskUserId = todouser_map.get(activityname);
								if(UtilString.isEmpty(hisTaskUserId) || !hisTaskUserId.contains(taskUserId)){
									hisTaskUserId = hisTaskUserId + "," +taskUserId;
									hisTodouser_map.put(activityname, hisTaskUserId);
								}
							}else{
								hisTodouser_map.put(activityname, todouser_map.get(activityname));
							}
						}
						mapParenttask.put(todoTask,hisTodouser_map);
					}else{
						mapParenttask.put(todoTask,todouser_map);
					}
				}
			}
		}
		//记录历史人物操作菜单
		Map<String,String> map_hiscomment = new HashMap<String,String>();
		//拼接流程跟踪html
		//已办任务
		List<Map<String,String>> list_lastTaskTime = new ArrayList<Map<String,String>>();
		//Map<String,String> map_lastTaskTime = new LinkedHashMap<String,String>();
		List<RowMap>  hlist = DBSql.getMaps("SELECT * FROM WFH_TASK WHERE TASKSTATE='2' AND PROCESSINSTID=? order by begintime asc", new Object[]{processId});
		List<RowMap> clist = DBSql.getMaps("SELECT * FROM WFC_TASK WHERE TASKSTATE='2' AND PROCESSINSTID=? order by begintime asc", new Object[]{processId});
		// if(processDefid.equals("obj_d23544417cf54becb210b93a7c13752b")){
		//加签代办
		List<HistoryTaskInstance> tlist = SDK.getHistoryTaskQueryAPI().userTaskOfNotification().addQuery("TASKSTATE=", "2").addQuery("PROCESSINSTID=", processId).list();
		if(list != null && list.size() > 0){
			for(int i = 0;i<list.size();i++){
				String activityName = list.get(i).getActivityName();//节点名
				String createUserid = list.get(i).getCreateUser();//留言人账号
				String userName = UserCache.getModel(createUserid).getUserName();
				String taskId = list.get(i).getTaskInstId();//任务实例ID
				Timestamp commentCreateTime = list.get(i).getCreateDate();
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskId);
				String actionName = list.get(i).getActionName();//操作路径
				
				/**
				 * yujh add 2019-07-22
				 * 流程跟踪中"收回"修改为"撤办"
				 */
				if("收回".equals(actionName)){
					actionName = "撤办";
				}
				//chenxf add 2018-06-04
				/*
				 * 过滤激活审批记录
				 */
//				if(actionName.equals("激活")){
//					int length = sb_table.toString().lastIndexOf("<tr><td>");
//					sb_table.delete(length,sb_table.length());
//					continue;
//				}
				String taskTitle = taskInstance.getTitle();//任务标题
				int task_state = taskInstance.getState();//任务状态
				String task_activitydefid = taskInstance.getActivityDefId();//任务节点ID
				String task_parentTaskId = taskInstance.getParentTaskInstId();//父任务实例ID
				String createData = "";//任务创建时间
				if(list_lastTaskTime != null && list_lastTaskTime.size() > 0){
					String beforeTaskTime = "";//当前任务上一次出现时间
					for(int lastTaskTimeSize = list_lastTaskTime.size() - 1;lastTaskTimeSize>= 0;
							lastTaskTimeSize--){
						Map<String,String> map_TaskTime = list_lastTaskTime.get(lastTaskTimeSize);
						if(map_TaskTime.get("taskId").equals(taskId)){
							beforeTaskTime = map_TaskTime.get("taskId");
							break;
						}
					}
					if(UtilString.isEmpty(beforeTaskTime)){
						Timestamp createTimestamp = taskInstance.getBeginTime();//审批记录创建时间
						createData = sdf.format(createTimestamp);//开始时间
					}else{
						List<HistoryTaskInstance> list_subTaskIds = SDK.getHistoryTaskQueryAPI().parentTaskInstId(taskId).userTaskOfWorking().list();
						for(int lastTaskTimeSize2 = list_lastTaskTime.size() - 1;lastTaskTimeSize2 >= 0;lastTaskTimeSize2-- ){
							Map<String,String> map_TaskTime = list_lastTaskTime.get(lastTaskTimeSize2);
							String taskIdValue = map_TaskTime.get("taskId");
							String taskEndData = map_TaskTime.get("endData");
							if(list_subTaskIds.toString().contains(taskIdValue)
									|| taskId.equals(taskIdValue)){
								createData = taskEndData;
								break;
							}
						}
					}
				}
				Timestamp endTimestamp = commentCreateTime;
				String endData = "";
				if(UtilString.isEmpty(endTimestamp)){
					endData = "未结束";
				}else{
					endData = sdf.format(endTimestamp);//结束时间
				}
				String nextActivityUser = "";//后续处理节点
				String nextActivityName = actionName;//后续路径
				//非加签、转办任务
				if(!nextActivityName.equals("加签") && !nextActivityName.equals("阅办")
						&& !nextActivityName.equals("转办") && !nextActivityName.equals("会签")&&!nextActivityName.equals("协同")
						&& !nextActivityName.equals("加签完毕") && !nextActivityName.equals("阅办完毕")
						&& !nextActivityName.equals("会签完毕")&& !nextActivityName.equals("协同完毕")){
					if(mapParenttask != null && mapParenttask.containsKey(taskId)){
						Map<String,String> mapNextUser = mapParenttask.get(taskId);
						if(mapNextUser != null && mapNextUser.size() > 0){
							for(String key:mapNextUser.keySet()){//
								String users = "";
								users = mapNextUser.get(key);
								StringBuffer username_sb = new StringBuffer();
								if(!UtilString.isEmpty(users) /*&& !key.equals(activityName)*/){
									//chenxf modify 2017-12-01
									//不过滤流程同个节点多个审批人（不同人）
									if(NoFilterSameStepOptionUtil.list_NoFilterProcessUUID.toString().contains(processDefid) 
											|| NoFilterSameStepOptionUtil.list_NoFilterProcessUUID.toString().contains(parentProcessDefId)){
										//当前不过滤的任务节点ID
										
										if(NoFilterSameStepOptionUtil.list_NoFilterProcessStepId.toString().
												contains(task_activitydefid)){
											String[] usersArr = users.split(",");
											for(int m = 0;m < usersArr.length;m++){
												//chenxf add 2018-11-09
												//过滤加签的人员
												String state = DBSql.getString("select taskstate from WFH_TASK "
																	+ "where parenttaskinstid = ? and target = ?", 
																		new Object[]{ taskId, usersArr[m]});
												if(state == null || !state.equals("1")){
													state = DBSql.getString("select taskstate from WFC_TASK "
															+ "where parenttaskinstid = ? and target = ?",
															new Object[]{ taskId, usersArr[m]});
												}
															
												if(state == null || (!state.equals("1") && !state.equals("4"))){
													continue;
												}
												String userName2 = UserCache.getModel(usersArr[m]).getUserName();
												username_sb.append(userName2+",");
											}
										}else{
											if(!key.equals(activityName)){
												String[] usersArr = users.split(",");
												for(int m = 0;m < usersArr.length;m++){
													String userName2 = UserCache.getModel(usersArr[m]).getUserName();
													username_sb.append(userName2+",");
												}
											}
										}
									}else{
										if(!key.equals(activityName)){
											String[] usersArr = users.split(",");
											for(int m = 0;m < usersArr.length;m++){
												String userName2 = UserCache.getModel(usersArr[m]).getUserName();
												username_sb.append(userName2+",");
											}
										}
									}
								}
								if(!UtilString.isEmpty(username_sb.toString())){
									if(username_sb.toString().length()>0){
										nextActivityUser =nextActivityUser + key+":"+username_sb.substring(0, username_sb.toString().length()-1);
									}else{
										nextActivityUser =nextActivityUser + key+":";
									}
								}
							}
						}
					}
				}else if(nextActivityName.equals("加签") || nextActivityName.equals("阅办")
						|| nextActivityName.equals("转办") || nextActivityName.equals("会签") 
						|| nextActivityName.equals("协同")){
					/*
					 * 加签、转办类任务
					 */
					//转办类任务
					if(actionName.equals("转办") && task_state == 1){
						String msg = list.get(i).getMsg();
						if(!UtilString.isEmpty(msg)){
							nextActivityUser = msg.split("，")[0];
						}
					}else {
						/*
						 * 加签类任务
						 * 修改当前节点名称、下一节点名称
						 */
						String nextStepName = nextActivityName;//下一节点名称
						if(map_extend_memo != null && map_extend_memo.containsKey(task_activitydefid)){
							BO bo = map_extend_memo.get(task_activitydefid);
							
							//1修改当前节点名称activityName
							//2修改路径名称nextActivityName
							//3.下一节点名称nextStepName
							if(task_state == 11){
								//父任务ID状态为11，取第二个节点名称，否则取第一个task_parentTaskId
								TaskInstance parent_taskInstance =  SDK.getTaskAPI().getInstanceById(task_parentTaskId);
								if(parent_taskInstance != null){
									
									int parent_task_state = parent_taskInstance.getState();
									
									if(parent_task_state == 11){
										//加签
										if(actionName.equals("加签") && taskTitle.contains("(加签)")){
											activityName = bo.getString("JQ_ACTIVITYNAME2");
											nextActivityName =  bo.getString("JQ_MEMONAME2");
											nextStepName = bo.getString("JQ_ACTIVITYNAME2");
										}else if(actionName.equals("阅办") && taskTitle.contains("(阅办)")){
											//阅办
											activityName = bo.getString("YB_ACTIVITYNAME2");
											nextActivityName =  bo.getString("YB_MEMONAME2");
											nextStepName = bo.getString("YB_ACTIVITYNAME2");
										}else if(actionName.equals("加签") && taskTitle.contains("(阅办)")){
											activityName = bo.getString("YB_ACTIVITYNAME2");
											nextActivityName =  bo.getString("JQ_MEMONAME2");
											nextStepName = bo.getString("JQ_ACTIVITYNAME2");
										}else if(actionName.equals("阅办") && taskTitle.contains("(加签)")){
											activityName = bo.getString("JQ_ACTIVITYNAME2");
											nextActivityName =  bo.getString("YB_MEMONAME2");
											nextStepName = bo.getString("YB_ACTIVITYNAME2");
										}else if(actionName.equals("转办") && taskTitle.contains("(加签)")){
											activityName = bo.getString("JQ_ACTIVITYNAME2");
											String msg = list.get(i).getMsg();
											if(!UtilString.isEmpty(msg)){
												nextActivityUser = msg.split("，")[0];
											}
										}else if(actionName.equals("转办") && taskTitle.contains("(阅办)")){
											activityName = bo.getString("YB_ACTIVITYNAME2");
											String msg = list.get(i).getMsg();
											if(!UtilString.isEmpty(msg)){
												nextActivityUser = msg.split("，")[0];
											}
										}
									}else{
										//加签
										if(actionName.equals("加签") && taskTitle.contains("(加签)")){
											activityName = bo.getString("JQ_ACTIVITYNAME1");
											nextActivityName =  bo.getString("JQ_MEMONAME2");
											nextStepName =  bo.getString("JQ_ACTIVITYNAME2");
										}else if(actionName.equals("阅办") && taskTitle.contains("(阅办)")){
											//阅办
											activityName = bo.getString("YB_ACTIVITYNAME1");
											nextActivityName =  bo.getString("YB_MEMONAME2");
											nextStepName =  bo.getString("YB_ACTIVITYNAME2");
										}else if(actionName.equals("加签") && taskTitle.contains("(阅办)")){
											activityName = bo.getString("YB_ACTIVITYNAME1");
											nextActivityName =  bo.getString("JQ_MEMONAME2");
											nextStepName =  bo.getString("JQ_ACTIVITYNAME2");
										}else if(actionName.equals("阅办") && taskTitle.contains("(加签)")){
											activityName = bo.getString("JQ_ACTIVITYNAME1");
											nextActivityName =  bo.getString("YB_MEMONAME2");
											nextStepName =  bo.getString("YB_ACTIVITYNAME2");
										}else if(actionName.equals("转办") && taskTitle.contains("(加签)")){
											activityName = bo.getString("JQ_ACTIVITYNAME1");
											String msg = list.get(i).getMsg();
											if(!UtilString.isEmpty(msg)){
												nextActivityUser = msg.split("，")[0];
											}
										}else if(actionName.equals("转办") && taskTitle.contains("(阅办)")){
											activityName = bo.getString("YB_ACTIVITYNAME1");
											String msg = list.get(i).getMsg();
											if(!UtilString.isEmpty(msg)){
												nextActivityUser = msg.split("，")[0];
											}
										}
									}
								}
							}else{
								//加签
								if(actionName.equals("加签")){
									nextActivityName =  bo.getString("JQ_MEMONAME1");
									nextStepName =  bo.getString("JQ_ACTIVITYNAME1");
								}else if(actionName.equals("阅办")){
									//阅办
									nextActivityName =  bo.getString("YB_MEMONAME1");
									nextStepName =  bo.getString("YB_ACTIVITYNAME1");
								}else if(actionName.equals("转办")){
									//转办
									String msg = list.get(i).getMsg();
									if(!UtilString.isEmpty(msg)){
										nextActivityUser = msg.split("，")[0];
									}
								}else if(actionName.equals("协同")){
									//协同
									nextActivityName =  bo.getString("XT_MEMONAME1");
									nextStepName =  bo.getString("XT_ACTIVITYNAME1");
								}
							}
						}
						
						StringBuffer sb_userName = new StringBuffer();
						Timestamp commentCreateTime2 = null;//下次任务记录出现时间
						Map<String,String> mapTaskId = new HashMap<String,String>();
						for(int subTask = i+1;subTask<list.size();subTask++){
							String taskId2 = list.get(subTask).getTaskInstId();//任务实例ID
							String parentTaskId2 = SDK.getTaskAPI().getInstanceById(taskId2).getParentTaskInstId();//父任务实例ID
							if(taskId2.equals(taskId)){//任务实例相同，为发起加签和加签完毕
								commentCreateTime2 = list.get(subTask).getCreateDate();
								break;
							}else if(parentTaskId2.equals(taskId)){
								//同一任务ID只获取一次。过滤多次加签问题
								if(mapTaskId == null || mapTaskId.size() == 0 || !mapTaskId.containsKey(taskId2)){
									String target = list.get(subTask).getCreateUser();
									String targetName = UserCache.getModel(target).getUserName();
									sb_userName.append(targetName+",");
									mapTaskId.put(taskId2, taskId2);
								}
							}
						}
						List<TaskInstance> parentTask_list = new ArrayList<TaskInstance>();
						if(UtilString.isEmpty(commentCreateTime2)){
							//commentCreateTime2为空，说明任务发起的加签任务未加签完毕，存在待办任务。
							//由当前任务创建的任务
							parentTask_list = SDK.getTaskQueryAPI().parentTaskInstId(taskId).list();
						}
						if(parentTask_list != null && parentTask_list.size()>0){
							for(int i2 = 0;i2< parentTask_list.size();i2++){
								String taskId3 = parentTask_list.get(i2).getId();
								//同一任务ID只获取一次。过滤多次加签问题
								if(mapTaskId == null || mapTaskId.size() == 0 || !mapTaskId.containsKey(taskId3)){
									String target2 = parentTask_list.get(i2).getTarget();
									String target2Name = UserCache.getModel(target2).getUserName();
									sb_userName.append(target2Name+",");
								}
							}
						}
						if(!UtilString.isEmpty(sb_userName.toString())){
							nextActivityUser = nextStepName+":"+sb_userName.toString().substring(0, sb_userName.toString().length()-1);
						}
					}
				}else if(nextActivityName.equals("加签完毕") || nextActivityName.equals("阅办完毕")
						|| nextActivityName.equals("会签完毕")|| nextActivityName.equals("协同完毕")){

					/*
					 * 加签类任务
					 * 修改当前节点名称、下一节点名称
					 */
					String nextStepName = nextActivityName;//下一节点名称
					if(map_extend_memo != null && map_extend_memo.containsKey(task_activitydefid)){
						BO bo = map_extend_memo.get(task_activitydefid);
						//1修改当前节点名称activityName
						//2修改路径名称nextActivityName
						//3.下一节点名称nextStepName
						if(task_state == 11){
							//父任务ID状态为11，取第二个节点名称，否则取第一个task_parentTaskId
							TaskInstance parent_taskInstance =  SDK.getTaskAPI().getInstanceById(task_parentTaskId);
							if(parent_taskInstance != null){
								String parentTasktitle = parent_taskInstance.getTitle();
								int parent_task_state = parent_taskInstance.getState();
								if(parent_task_state == 11){
									//加签
									if(actionName.equals("加签完毕") && parentTasktitle.contains("(加签)")){
										activityName = bo.getString("JQ_ACTIVITYNAME2");
										nextActivityName =  bo.getString("JQ_COMPLETE2");
										nextStepName = bo.getString("JQ_ACTIVITYNAME1");
									}else if(actionName.equals("阅办完毕") && parentTasktitle.contains("(阅办)")){
										//阅办
										activityName = bo.getString("YB_ACTIVITYNAME2");
										nextActivityName =  bo.getString("YB_COMPLETE2");
										nextStepName = bo.getString("YB_ACTIVITYNAME1");
									}else if(actionName.equals("加签完毕") && parentTasktitle.contains("(阅办)")){
										activityName = bo.getString("JQ_ACTIVITYNAME2");
										nextActivityName =  bo.getString("JQ_COMPLETE2");
										nextStepName = bo.getString("YB_ACTIVITYNAME1");
									}else if(actionName.equals("阅办完毕") && parentTasktitle.contains("(加签)")){
										activityName = bo.getString("YB_ACTIVITYNAME2");
										nextActivityName =  bo.getString("YB_COMPLETE2");
										nextStepName = bo.getString("JQ_ACTIVITYNAME1");
									}
								}else{
									//加签
									if(actionName.equals("加签完毕")){
										nextStepName = activityName;
										activityName = bo.getString("JQ_ACTIVITYNAME1");
										nextActivityName =  bo.getString("JQ_COMPLETE1");
									}else if(actionName.equals("阅办完毕")){
										//阅办
										nextStepName = activityName;
										activityName = bo.getString("YB_ACTIVITYNAME1");
										nextActivityName =  bo.getString("YB_COMPLETE1");
									}else if(actionName.equals("协同完毕")){
										//阅办
										nextStepName = activityName;
										activityName = bo.getString("XT_ACTIVITYNAME1");
										nextActivityName =  bo.getString("XT_COMPLETE1");
									}
								}
							}
						}
					}
					String parenttaskId3 = taskInstance.getParentTaskInstId();
					String parenttargetId = SDK.getTaskAPI().getTaskInstance(parenttaskId3).getTarget();
					String parenttargetName = UserCache.getModel(parenttargetId).getUserName();
					nextActivityUser = nextStepName+":"+parenttargetName;
				}
				if(UtilString.isEmpty(nextActivityName)){
					nextActivityName = "无";
					if(i == list.size()-1){
						boolean isEnd = SDK.getProcessAPI().isEndById(processId);//判断流程是否结束
						if(isEnd){
							nextActivityName = "结束";
						}
					}
				}
				if(UtilString.isEmpty(nextActivityUser)){
					nextActivityUser = "无";
					String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
					if("cancel".equals(controlState)){
						nextActivityUser = "被收回";
					}
					/*-------------------------------------*/
					StringBuffer sbf = new StringBuffer();
					if(NoFilterSameStepOptionUtil.fdz_uuid.equals(processDefid) 
							|| NoFilterSameStepOptionUtil.dz_uuid.equals(processDefid)
							|| NoFilterSameStepOptionUtil.bp_uuid.equals(processDefid)
							|| NoFilterSameStepOptionUtil.bp_uuid_new.equals(processDefid)
							|| NoFilterSameStepOptionUtil.dz_uuid_new.equals(processDefid)
							|| NoFilterSameStepOptionUtil.fdz_uuid_new.equals(processDefid)
							|| NoFilterSameStepOptionUtil.jhxz_uuid.equals(processDefid)
							|| NoFilterSameStepOptionUtil.jhbg_uuid.equals(processDefid)
							|| NoFilterSameStepOptionUtil.jhqx_uuid.equals(processDefid)
							|| NoFilterSameStepOptionUtil.fs_uuid.equals(processDefid)){
						if(clist.size()>0&&clist!=null){
							for(int j=0;j<clist.size();j++){
								String uid = clist.get(j).getString("TARGET");
								if(j==0){
									sbf.append("阅知:");
									sbf.append(UserCache.getModel(uid).getUserName());
								}else{
									sbf.append("、");
									sbf.append(UserCache.getModel(uid).getUserName());
								}
							}
						}
						if(hlist.size()>0&&hlist!=null){
							for(int j=0;j<hlist.size();j++){
								String uid = hlist.get(j).getString("TARGET");
								if(j==0 && UtilString.isEmpty(sbf.toString())){
									sbf.append("阅知:");
									sbf.append(UserCache.getModel(uid).getUserName());
								}else{
									sbf.append("、");
									sbf.append(UserCache.getModel(uid).getUserName());
								}
							}
						}
		
//						String sql1 = "SELECT GROUP_CONCAT(a.TARGET) TARGET FROM " + "(SELECT TARGET FROM WFC_TASK  "
//								+ "WHERE TASKSTATE='2' AND processinstid=? AND parenttaskinstid = ? " 
//								+ "UNION "
//								+ "SELECT TARGET FROM WFH_TASK "
//								+ "WHERE TASKSTATE='2' AND processinstid=? AND parenttaskinstid = ?) a";
//						if(tlist.size()>0&&tlist!=null){
//							List<HistoryTaskInstance> htlist =  SDK.getHistoryTaskQueryAPI().userTaskOfWorking().addQuery("TASKSTATE=", "1").addQuery("PROCESSINSTID=", processId).orderByEndTime().asc().list();
//							if(htlist!=null&&htlist.size()>0){
//								String tid = htlist.get(htlist.size()-1).getId();
//								String name = DBSql.getString(sql1,new Object[]{processId,tid,processId,tid});
//								if(!UtilString.isEmpty(name)){
//									String[] names = name.split(",");
//									if(names!=null&&names.length>0){
//										for(int l=0;l<names.length;l++){
//											String sname = UserCache.getModel(names[l]).getUserName();
//											if(l==0){
//												sbf.append(sname);
//											}else{
//												sbf.append(",");
//												sbf.append(sname);
//											}
//										}
//			
//									}
//								}
//		
//							}
//						}
					}

					if(!UtilString.isEmpty(sbf.toString())){
						boolean isEnd = SDK.getProcessAPI().isEndById(processId);//判断流程是否结束
						if(isEnd){
							nextActivityUser = sbf.toString();
						}else{
							nextActivityUser = "无";
						}

					}else{
						nextActivityUser = "无";
					}

					/*-------------------------------------*/	
				}
				if(UtilString.isEmpty(createData)){
					createData = "无";
				}
				sb_table.append("<tr><td><FONT color='#000000'>"+activityName+"</FONT></td>");
				sb_table.append("<td><FONT color='#000000'>"+userName+"</FONT></td>");
				sb_table.append("<td><FONT color='#000000'>"+createData+"</FONT></td>");
				sb_table.append("<td><FONT color='#000000'>"+endData+"</FONT></td>");
				sb_table.append("<td><FONT color='#000000'>"+nextActivityName+"</FONT></td>");
				sb_table.append("<td><FONT color='#000000'>"+nextActivityUser+"</FONT></td>");
				sb_table.append("</tr><tr><td colspan='6'><HR noshade style='height:1px;border-width:0;color:gray;background-color:gray' width='100%' align='right' ></td></tr>");
				//map_lastTaskTime.put(taskId, endData);
				Map<String,String> map_lastTaskTime = new HashMap<String,String>();
				map_lastTaskTime.put("taskId", taskId);
				map_lastTaskTime.put("endData", endData);
				list_lastTaskTime.add(map_lastTaskTime);
				map_hiscomment.put(taskId, nextActivityUser);
			}
		}

		/*-------------------------------------*/
		//已阅
		boolean End = SDK.getProcessAPI().isEndById(processId);//判断流程是否结束 
		/*
		 * 企标项目加上阅知记录
		 */
		if(NoFilterSameStepOptionUtil.fdz_uuid.equals(processDefid) 
				|| NoFilterSameStepOptionUtil.dz_uuid.equals(processDefid)
				|| NoFilterSameStepOptionUtil.bp_uuid.equals(processDefid)
				|| NoFilterSameStepOptionUtil.bp_uuid_new.equals(processDefid)
				|| NoFilterSameStepOptionUtil.dz_uuid_new.equals(processDefid)
				|| NoFilterSameStepOptionUtil.fdz_uuid_new.equals(processDefid)
				|| NoFilterSameStepOptionUtil.jhxz_uuid.equals(processDefid)
				|| NoFilterSameStepOptionUtil.jhbg_uuid.equals(processDefid)
				|| NoFilterSameStepOptionUtil.jhqx_uuid.equals(processDefid)
				|| NoFilterSameStepOptionUtil.fs_uuid.equals(processDefid)){
			SimpleDateFormat  sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(End){
				if(hlist!=null &&hlist.size()>0){
					for(int k=0;k<hlist.size();k++){
						if(processDefid.equals(NoFilterSameStepOptionUtil.fdz_uuid) 
								|| processDefid.equals(NoFilterSameStepOptionUtil.fdz_uuid_new)){
							sb_table.append("<tr><td><FONT color='#000000'>"+"技术部企标管理员阅知"+"</FONT></td>");
						}else{
							sb_table.append("<tr><td><FONT color='#000000'>"+"需求单位阅知"+"</FONT></td>");
						}
						
						String target = UserCache.getModel(hlist.get(k).getString("TARGET")).getUserName();
						sb_table.append("<td><FONT color='#000000'>"+target+"</FONT></td>");
						sb_table.append("<td><FONT color='#000000'>"+sdf.format(hlist.get(k).getDate("BEGINTIME"))+"</FONT></td>");
						String endtime="";
						if(UtilString.isEmpty(hlist.get(k).getDate("ENDTIME"))){
							endtime = "无";
						}else{
							endtime = sdf.format(hlist.get(k).getDate("ENDTIME"));
						}
						sb_table.append("<td><FONT color='#000000'>"+endtime+"</FONT></td>");
						sb_table.append("<td><FONT color='#000000'>"+"阅知结束"+"</FONT></td>");
						sb_table.append("<td><FONT color='#000000'>"+"无"+"</FONT></td>");
						sb_table.append("</tr><tr><td colspan='6'><HR noshade style='height:1px;border-width:0;color:gray;background-color:gray' width='100%' align='right' ></td></tr>");
					}
				}
			}
		}
		/*-------------------------------------*/

		//待办任务
		if(list_task != null && list_task.size() > 0){
			for(int i = 0;i< list_task.size();i++){
				String activityId = list_task.get(i).getActivityDefId();//当前节点ID
				String target = list_task.get(i).getTarget();
				String userName = "";
				if(!UtilString.isEmpty(target)){
					userName = UserCache.getModel(target).getUserName();
				}
				String processDefId2 = list_task.get(i).getProcessDefId();
				UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefId2, activityId);
				String taskId = list_task.get(i).getId();
				String activityName = taskModel.name;//节点名称
				int taskState = list_task.get(i).getState();//任务状态
				if(taskState == 11){
					String parentTaskId = list_task.get(i).getParentTaskInstId();
					if(map_hiscomment != null && map_hiscomment.containsKey(parentTaskId)){
						String activtityActionname = map_hiscomment.get(parentTaskId);
						activityName =activtityActionname.split(":")[0];
					}
				}

				String createData = "";//任务创建时间
				String beforeTaskTime = "";//当前任务上一次出现时间
				for(int lastTaskTimeSize = list_lastTaskTime.size() - 1;lastTaskTimeSize>= 0;
						lastTaskTimeSize--){
					Map<String,String> map_TaskTime = list_lastTaskTime.get(lastTaskTimeSize);
					if(map_TaskTime.get("taskId").equals(taskId)){
						beforeTaskTime = map_TaskTime.get("taskId");
						break;
					}
				}
				if(UtilString.isEmpty(beforeTaskTime)){
					Timestamp createTimestamp = list_task.get(i).getBeginTime();//审批记录创建时间
					createData = sdf.format(createTimestamp);//开始时间
				}else{
					List<HistoryTaskInstance> list_subTaskIds = SDK.getHistoryTaskQueryAPI().parentTaskInstId(taskId).userTaskOfWorking().list();
					for(int lastTaskTimeSize2 = list_lastTaskTime.size() - 1;lastTaskTimeSize2 >= 0;lastTaskTimeSize2-- ){
						Map<String,String> map_TaskTime = list_lastTaskTime.get(lastTaskTimeSize2);
						String taskIdValue = map_TaskTime.get("taskId");
						String taskEndData = map_TaskTime.get("endData");
						if(list_subTaskIds.toString().contains(taskIdValue)
								|| taskId.equals(taskIdValue)){
							createData = taskEndData;
							break;
						}
					}
				}
				if(UtilString.isEmpty(createData)){
					createData = "无";
				}
				String endData = "未结束";
				String nextActivityName = "未提交";
				String nextActivityUser = "";
				StringBuffer sbf = new StringBuffer();
				if(NoFilterSameStepOptionUtil.fdz_uuid.equals(processDefid) 
						|| NoFilterSameStepOptionUtil.dz_uuid.equals(processDefid)
						|| NoFilterSameStepOptionUtil.bp_uuid.equals(processDefid)
						|| NoFilterSameStepOptionUtil.bp_uuid_new.equals(processDefid)
						|| NoFilterSameStepOptionUtil.dz_uuid_new.equals(processDefid)
						|| NoFilterSameStepOptionUtil.fdz_uuid_new.equals(processDefid)
						|| NoFilterSameStepOptionUtil.jhxz_uuid.equals(processDefid)
						|| NoFilterSameStepOptionUtil.jhbg_uuid.equals(processDefid)
						|| NoFilterSameStepOptionUtil.jhqx_uuid.equals(processDefid)
						|| NoFilterSameStepOptionUtil.fs_uuid.equals(processDefid)){
					if(clist.size() > 0 && clist != null){
						boolean isEnd = SDK.getProcessAPI().isEndById(processId);//判断流程是否结束
						if(isEnd){
							for(int j=0;j<clist.size();j++){
								String uid = clist.get(j).getString("TARGET");
								if(j==0){
									sbf.append("阅知:");
									sbf.append(UserCache.getModel(uid).getUserName());
								}else{
									sbf.append("、");
									sbf.append(UserCache.getModel(uid).getUserName());
								}
							}
							nextActivityUser =  sbf.toString();
						}else{
							nextActivityUser = "无";
						}
					}else{
						nextActivityUser = "无";
					}
				}else{
					nextActivityUser = "无";
				}
				sb_table.append("<tr><td><FONT color='#000000'>"+activityName+"</FONT></td>");
				sb_table.append("<td><FONT color='#000000'>"+userName+"</FONT></td>");
				sb_table.append("<td><FONT color='#000000'>"+createData+"</FONT></td>");
				sb_table.append("<td><FONT color='#000000'>"+endData+"</FONT></td>");
				sb_table.append("<td><FONT color='#000000'>"+nextActivityName+"</FONT></td>");
				sb_table.append("<td><FONT color='#000000'>"+nextActivityUser+"</FONT></td>");
				sb_table.append("</tr><tr><td colspan='6'><HR noshade style='height:1px;border-width:0;color:gray;background-color:gray' width='100%' align='right' ></td></tr>");

			}

		}
		/*-------------------------------------*/
		//待阅
		if(NoFilterSameStepOptionUtil.fdz_uuid.equals(processDefid) 
				|| NoFilterSameStepOptionUtil.dz_uuid.equals(processDefid)
				|| NoFilterSameStepOptionUtil.bp_uuid.equals(processDefid)
				|| NoFilterSameStepOptionUtil.bp_uuid_new.equals(processDefid)
				|| NoFilterSameStepOptionUtil.dz_uuid_new.equals(processDefid)
				|| NoFilterSameStepOptionUtil.fdz_uuid_new.equals(processDefid)
				|| NoFilterSameStepOptionUtil.jhxz_uuid.equals(processDefid)
				|| NoFilterSameStepOptionUtil.jhbg_uuid.equals(processDefid)
				|| NoFilterSameStepOptionUtil.jhqx_uuid.equals(processDefid)
				|| NoFilterSameStepOptionUtil.fs_uuid.equals(processDefid)){
			if(End){
				if(clist!=null &&clist.size()>0){
					for(int k=0;k<clist.size();k++){
						String target = UserCache.getModel(clist.get(k).getString("TARGET")).getUserName();
						String endtime="";
						if(UtilString.isEmpty(clist.get(k).getDate("ENDTIME"))){
							endtime = "无";
						}else{
							endtime = sdf.format(clist.get(k).getDate("ENDTIME"));
						}
						if(processDefid.equals(NoFilterSameStepOptionUtil.fdz_uuid)
								|| processDefid.equals(NoFilterSameStepOptionUtil.fdz_uuid_new)){
							sb_table.append("<tr><td><FONT color='#000000'>"+"技术部企标管理员阅知"+"</FONT></td>");
						}else{
							sb_table.append("<tr><td><FONT color='#000000'>"+"需求单位阅知"+"</FONT></td>");
						}
						sb_table.append("<td><FONT color='#000000'>"+target+"</FONT></td>");
						sb_table.append("<td><FONT color='#000000'>"+sdf.format(clist.get(k).getDate("BEGINTIME"))+"</FONT></td>");
						sb_table.append("<td><FONT color='#000000'>"+endtime+"</FONT></td>");
						sb_table.append("<td><FONT color='#000000'>"+"待阅"+"</FONT></td>");
						sb_table.append("<td><FONT color='#000000'>"+"无"+"</FONT></td>");
						sb_table.append("</tr><tr><td colspan='6'><HR noshade style='height:1px;border-width:0;color:gray;background-color:gray' width='100%' align='right' ></td></tr>");
					}
				}
			}
		}
		//分发阅文
		/*-------------------------------------*/
		map.put("title", title);
		map.put("optionMsg", sb_table.toString());
		return HtmlPageTemplate.merge("com.actionsoft.apps.cmcc", "com.actionsoft.apps.cmcc.regzt.htm", map);
	}
}
