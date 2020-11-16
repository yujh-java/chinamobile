package com.actionsoft.apps.cmcc.util;
/**
 * 流程跟踪处理
 * @author nch
 * @date 20170622
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.bpmn.engine.cache.util.UserTaskDefUtil;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.run.TaskCommentModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class PubSubProcessUtil {
	/**
	 * 过滤待办记录
	 * @param list
	 * @return
	 */
	public static Map<String,Map<String,String>> UserTaskProcessMsg(List<String> list_taskId,List<TaskInstance> list){
		//记录父任务ID，当前任务信息
		Map<String,Map<String,String>> mapParenttask = new HashMap<String,Map<String,String>>();
		//循环已办审批记录，记录map(任务ID，下一节点信息)
		if(list != null && list.size() > 0){
			for(int j = 0;j < list.size();j++){
				String taskId = list.get(j).getId();//任务实例ID
				String ActivityId = list.get(j).getActivityDefId();
				String processDefId = list.get(j).getProcessDefId();
				UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefId, ActivityId);
				String activityName = taskModel.name;//节点名称
				String createUserid = list.get(j).getTarget();//留言人账号
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskId);
				String processId = list.get(j).getProcessInstId();//流程实例ID 
				String parentTaskId = taskInstance.getParentTaskInstId();//父任务实例ID
				List<String> list_parentTaskId = new ArrayList<String>();//父任务节点ID list集
				//没有父任务
				if("00000000-0000-0000-0000-000000000000".equals(parentTaskId)){
					//父流程实例ID
					String parentProcessId = SDK.getProcessAPI().getInstanceById(processId).getParentProcessInstId();
					//如果不为子流程
					if(UtilString.isEmpty(parentProcessId)){
						list_parentTaskId.add(parentTaskId);
					}else{
						//流程为子流程
						//查询创建流程的任务ID
						String createProcessTaskId = SDK.getProcessAPI().getInstanceById(processId).getParentTaskInstId();
						//创建流程任务的节点类型
						TaskInstance createTaskInstance = SDK.getTaskAPI().getInstanceById(createProcessTaskId);
						String createProcessTaskCallActivity =  createTaskInstance.getActivityType();
						if("callActivity".equals(createProcessTaskCallActivity)){//启动子流程节点
							parentTaskId = createTaskInstance.getParentTaskInstId();//父任务实例ID
							//判断list_taskId是否包含父任务，不存在说明任务自动跳过
							if(list_taskId.toString().contains(parentTaskId)){//存在
								list_parentTaskId.add(parentTaskId);
							}else{//不存在，继续向上寻找
								parentTaskId = SDK.getTaskAPI().getInstanceById(parentTaskId).getParentTaskInstId();
								list_parentTaskId.add(parentTaskId);
							}
						}else{
							list_parentTaskId.add(createProcessTaskId);
						}
					}
				}else{
					//判断list_taskId是否包含父任务，不存在说明任务自动跳过
					if(list_taskId.toString().contains(parentTaskId)){//存在
						list_parentTaskId.add(parentTaskId);
					}else{
						TaskInstance parentTaskInstance = SDK.getTaskAPI().getInstanceById(parentTaskId);
						String parentCallActivity = parentTaskInstance.getActivityType();//父任务节点类型
						//父任务为callActivity类型
						if("callActivity".equals(parentCallActivity)){
							//任务创建的子流程
							List<ProcessInstance> list_subProcessInstance = SDK.getProcessQueryAPI().parentTaskInstId(parentTaskId).list();
							if(list_subProcessInstance != null && list_subProcessInstance.size() > 0){
								for(int i = 0;i<list_subProcessInstance.size();i++){
									String subProcessId = list_subProcessInstance.get(i).getId();//流程实例ID
									List<TaskCommentModel> list_parentTask = SDK.getProcessAPI().getCommentsById(subProcessId);
									if(list_parentTask != null && list_parentTask.size() > 0){
										String parentTaskId2 = list_parentTask.get(list_parentTask.size()-1).getTaskInstId();
										list_parentTaskId.add(parentTaskId2);
									}
								}
							}
						}else{
							//父流程实例ID
							String parentProcessId = SDK.getProcessAPI().getInstanceById(processId).getParentProcessInstId();
							//如果为子流程
							if(!UtilString.isEmpty(parentProcessId)){
								String createProcessTaskId = SDK.getProcessAPI().getInstanceById(processId).getParentTaskInstId();
								//创建流程任务的节点类型
								TaskInstance createTaskInstance = SDK.getTaskAPI().getInstanceById(createProcessTaskId);
								String createProcessTaskCallActivity =  createTaskInstance.getActivityType();
								if("callActivity".equals(createProcessTaskCallActivity)){//启动子流程节点
									parentTaskId = createTaskInstance.getParentTaskInstId();//父任务实例ID
									//判断list_taskId是否包含父任务，不存在说明任务自动跳过
									if(list_taskId.toString().contains(parentTaskId)){//存在
										list_parentTaskId.add(parentTaskId);
									}else{//不存在，继续向上寻找
										parentTaskId = SDK.getTaskAPI().getInstanceById(parentTaskId).getParentTaskInstId();
										list_parentTaskId.add(parentTaskId);
									}
								}else{
									list_parentTaskId.add(createProcessTaskId);
								}
							}
						}
					}
				}
				//添加mapParenttask
				if(list_taskId == null || !list_taskId.contains(taskId)){
					for(int i = 0;i<list_parentTaskId.size();i++){
						parentTaskId = list_parentTaskId.get(i);
						if(mapParenttask != null && mapParenttask.containsKey(parentTaskId)){
							Map<String,String> map_nextUser = mapParenttask.get(parentTaskId);
							if(map_nextUser != null && map_nextUser.containsKey(activityName)){
								String nextUser = map_nextUser.get(activityName);
								if(UtilString.isEmpty(nextUser)){
									map_nextUser.put(activityName, createUserid);
								}else if(!nextUser.contains(createUserid)){
									map_nextUser.put(activityName, nextUser+","+createUserid);
								}
							}else{
								map_nextUser.put(activityName, createUserid);
							}
							mapParenttask.put(parentTaskId, map_nextUser);
						}else{
							Map<String,String> map_nextUser = new HashMap<String,String>();
							map_nextUser.put(activityName, createUserid);
							mapParenttask.put(parentTaskId, map_nextUser);
						}
						list_taskId.add(taskId);
					}
				}
				list_taskId.add(taskId);
			}
		}
		return mapParenttask;
	}
	/**
	 * 过滤审批记录
	 * @param list
	 * @return
	 */
	public static Map<String,Map<String,String>> HisProcessMsg(List<String> list_taskId,List<TaskCommentModel> list){
		//记录父任务ID，当前任务信息
		Map<String,Map<String,String>> mapParenttask = new HashMap<String,Map<String,String>>();
		//记录过滤任务ID（避免循环过滤）
		//List<String> list_taskId = new ArrayList<String>();
		//循环已办审批记录，记录map(任务ID，下一节点信息)
		if(list != null && list.size() > 0){
			//遍历流程审批记录集合
			for(int j = 0;j < list.size();j++){
				String taskId = list.get(j).getTaskInstId();//任务实例ID
				String activityName = list.get(j).getActivityName();//节点名
				String createUserid = list.get(j).getCreateUser();//留言人账号
				//获得任务实例对象
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskId);
				//审核菜单名
				String actionName = list.get(j).getActionName();
				if("收回".equals(actionName)){
					createUserid = taskInstance.getTarget();
				}
				String processId = list.get(j).getProcessInstId();//流程实例ID 
				String parentTaskId = taskInstance.getParentTaskInstId();//父任务实例ID
				//主流程所有任务实例ID  list集合
				List<String> list_parentTaskId = new ArrayList<String>();
				//没有父任务
				if("00000000-0000-0000-0000-000000000000".equals(parentTaskId)){
					//父流程实例ID
					String parentProcessId = SDK.getProcessAPI().getInstanceById(processId).getParentProcessInstId();
					//主流程
					if(UtilString.isEmpty(parentProcessId)){
						list_parentTaskId.add(parentTaskId);
					}else{
						//流程为子流程
						//查询创建子流程的父任务实例ID
						String createProcessTaskId = SDK.getProcessAPI().getInstanceById(processId).getParentTaskInstId();
						//创建流程任务的节点类型
						TaskInstance createTaskInstance = SDK.getTaskAPI().getInstanceById(createProcessTaskId);
						String createProcessTaskCallActivity =  createTaskInstance.getActivityType();
						if("callActivity".equals(createProcessTaskCallActivity)){//启动子流程节点
							parentTaskId = createTaskInstance.getParentTaskInstId();//父任务实例ID
							//判断list_taskId是否包含父任务，不存在说明任务自动跳过
							if(list_taskId.toString().contains(parentTaskId)){//存在
								list_parentTaskId.add(parentTaskId);
							}else{//不存在，继续向上寻找
								parentTaskId = SDK.getTaskAPI().getInstanceById(parentTaskId).getParentTaskInstId();
								list_parentTaskId.add(parentTaskId);
							}
						}else{
							list_parentTaskId.add(createProcessTaskId);
						}
					}
				}else{
					//判断list_taskId是否包含父任务，不存在说明任务自动跳过
					if(list_taskId.toString().contains(parentTaskId)){//存在
						list_parentTaskId.add(parentTaskId);
					}else{
						TaskInstance parentTaskInstance = SDK.getTaskAPI().getInstanceById(parentTaskId);
						String parentCallActivity = parentTaskInstance.getActivityType();//父任务节点类型
						//父任务为callActivity类型
						if("callActivity".equals(parentCallActivity)){
							//任务创建的子流程
							List<ProcessInstance> list_subProcessInstance = SDK.getProcessQueryAPI().parentTaskInstId(parentTaskId).list();
							if(list_subProcessInstance != null && list_subProcessInstance.size() > 0){
								for(int i = 0;i<list_subProcessInstance.size();i++){
									String subProcessId = list_subProcessInstance.get(i).getId();//流程实例ID
									List<TaskCommentModel> list_parentTask = SDK.getProcessAPI().getCommentsById(subProcessId);
									if(list_parentTask != null && list_parentTask.size() > 0){
										String parentTaskId2 = list_parentTask.get(list_parentTask.size()-1).getTaskInstId();
										list_parentTaskId.add(parentTaskId2);
									}
								}
							}
						}else{
							//父流程实例ID
							String parentProcessId = SDK.getProcessAPI().getInstanceById(processId).getParentProcessInstId();
							//如果为子流程
							if(!UtilString.isEmpty(parentProcessId)){
								String createProcessTaskId = SDK.getProcessAPI().getInstanceById(processId).getParentTaskInstId();
								//创建流程任务的节点类型
								TaskInstance createTaskInstance = SDK.getTaskAPI().getInstanceById(createProcessTaskId);
								String createProcessTaskCallActivity =  createTaskInstance.getActivityType();
								if("callActivity".equals(createProcessTaskCallActivity)){//启动子流程节点
									parentTaskId = createTaskInstance.getParentTaskInstId();//父任务实例ID
									//判断list_taskId是否包含父任务，不存在说明任务自动跳过
									if(list_taskId.toString().contains(parentTaskId)){//存在
										list_parentTaskId.add(parentTaskId);
									}else{//不存在，继续向上寻找
										parentTaskId = SDK.getTaskAPI().getInstanceById(parentTaskId).getParentTaskInstId();
										list_parentTaskId.add(parentTaskId);
									}
								}else{
									list_parentTaskId.add(createProcessTaskId);
								}
							}
						}
					}
				}
				//添加mapParenttask
				if(list_taskId == null || !list_taskId.contains(taskId)){
					for(int i = 0;i<list_parentTaskId.size();i++){
						parentTaskId = list_parentTaskId.get(i);
						if(mapParenttask != null && mapParenttask.containsKey(parentTaskId)){
							Map<String,String> map_nextUser = mapParenttask.get(parentTaskId);
							if(map_nextUser != null && map_nextUser.containsKey(activityName)){
								String nextUser = map_nextUser.get(activityName);
								if(UtilString.isEmpty(nextUser)){
									map_nextUser.put(activityName, createUserid);
								}else if(!nextUser.contains(createUserid)){
									map_nextUser.put(activityName, nextUser+","+createUserid);
								}
							}else{
								map_nextUser.put(activityName, createUserid);
							}
							mapParenttask.put(parentTaskId, map_nextUser);
						}else{
							Map<String,String> map_nextUser = new HashMap<String,String>();
							map_nextUser.put(activityName, createUserid);
							mapParenttask.put(parentTaskId, map_nextUser);
						}
						list_taskId.add(taskId);
					}
				}
				list_taskId.add(taskId);
			}
		}
		return mapParenttask;
	}
}
