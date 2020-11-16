package com.actionsoft.apps.cmcc.integration.util;
/**
 * 过滤任务方法类
 * @author nch
 * @date 20170622
 */
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.apps.cmcc.integration.CMCCConst;
import com.actionsoft.bpms.bpmn.engine.model.run.TaskCommentModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class Filterlistutil {
	/**
	 * 过滤历史任务
	 * @param list
	 * @return
	 */
	public static List<Map<String,Object>> filterHisList(List<HistoryTaskInstance> list,List<TaskInstance> listJqlHisTask){
		List<Map<String,Object>> newlist = new ArrayList<Map<String,Object>>();
		Map<String,String> map = new HashMap<String,String>();
		if(list != null && list.size() > 0){
			for(int i = 0;i<list.size();i++){
				String processId = list.get(i).getProcessInstId();
				ProcessInstance processInstance = SDK.getProcessAPI().getInstanceById(processId);
				if(null!=processInstance){
					String parentProcessId = processInstance.getParentProcessInstId();
					if(!UtilString.isEmpty(parentProcessId)){
						processId = parentProcessId;
					}
				}else{
					System.err.println(">>>>>>>processId:"+processId);
				}
				String target = list.get(i).getTarget();
				if(map != null && map.size() > 0){
					if(!map.containsKey(processId+target)){
						String ACTIVITYDEFID = list.get(i).getActivityDefId();
						if(!CMCCConst.NotDoHistoryTask.toString().contains(ACTIVITYDEFID)){
							map.put(processId+target, processId);
							Map<String,Object> filter = new HashMap<String,Object>();
							filter.put("ID", list.get(i).getId());
							filter.put("PROCESSINSTID", list.get(i).getProcessInstId());
							filter.put("STATE", list.get(i).getState());
							filter.put("BEGINTIME", list.get(i).getBeginTime());
							filter.put("ENDTIME", list.get(i).getEndTime());
							filter.put("PARENTTASKINSTID", list.get(i).getParentTaskInstId());
							filter.put("TITLE", list.get(i).getTitle());
							filter.put("PROCESSDEFID", list.get(i).getProcessDefId());
							filter.put("OWNER", list.get(i).getOwner());
							filter.put("ACTIVITYDEFID", ACTIVITYDEFID);
							newlist.add(filter);
						}
					}
				}else{
					String ACTIVITYDEFID = list.get(i).getActivityDefId();
					if(!CMCCConst.NotDoHistoryTask.toString().contains(ACTIVITYDEFID)){
						map.put(processId+target, processId);
						Map<String,Object> filter = new HashMap<String,Object>();
						filter.put("ID", list.get(i).getId());
						filter.put("PROCESSINSTID", list.get(i).getProcessInstId());
						filter.put("STATE", list.get(i).getState());
						filter.put("BEGINTIME", list.get(i).getBeginTime());
						filter.put("ENDTIME", list.get(i).getEndTime());
						filter.put("PARENTTASKINSTID", list.get(i).getParentTaskInstId());
						filter.put("TITLE", list.get(i).getTitle());
						filter.put("PROCESSDEFID", list.get(i).getProcessDefId());
						filter.put("OWNER", list.get(i).getOwner());
						filter.put("ACTIVITYDEFID", ACTIVITYDEFID);
						newlist.add(filter);
					}
				}
			}
		}
		if(listJqlHisTask != null && listJqlHisTask.size() > 0){
			for(int i = 0;i<listJqlHisTask.size();i++){
				String processId = listJqlHisTask.get(i).getProcessInstId();
				ProcessInstance processInstance = SDK.getProcessAPI().getInstanceById(processId);
				String parentProcessId = processInstance.getParentProcessInstId();
				if(!UtilString.isEmpty(parentProcessId)){
					processId = parentProcessId;
				}
				String target = listJqlHisTask.get(i).getTarget();
				if(map != null && map.size() > 0){
					if(!map.containsKey(processId+target)){
						String ACTIVITYDEFID = listJqlHisTask.get(i).getActivityDefId();
						if(!CMCCConst.NotDoHistoryTask.toString().contains(ACTIVITYDEFID)){
							map.put(processId+target, processId);
							Map<String,Object> filter = new HashMap<String,Object>();
							filter.put("ID", listJqlHisTask.get(i).getId());
							filter.put("PROCESSINSTID", listJqlHisTask.get(i).getProcessInstId());
							filter.put("STATE", listJqlHisTask.get(i).getState());
							filter.put("BEGINTIME", listJqlHisTask.get(i).getBeginTime());
							filter.put("ENDTIME", listJqlHisTask.get(i).getEndTime());
							filter.put("PARENTTASKINSTID", listJqlHisTask.get(i).getParentTaskInstId());
							filter.put("TITLE", listJqlHisTask.get(i).getTitle());
							filter.put("PROCESSDEFID", listJqlHisTask.get(i).getProcessDefId());
							filter.put("OWNER", listJqlHisTask.get(i).getOwner());
							filter.put("ACTIVITYDEFID", ACTIVITYDEFID);
							newlist.add(filter);
						}
					}
				}else{
					String ACTIVITYDEFID = listJqlHisTask.get(i).getActivityDefId();
					if(!CMCCConst.NotDoHistoryTask.toString().contains(ACTIVITYDEFID)){
						map.put(processId+target, processId);
						Map<String,Object> filter = new HashMap<String,Object>();
						filter.put("ID", listJqlHisTask.get(i).getId());
						filter.put("PROCESSINSTID", listJqlHisTask.get(i).getProcessInstId());
						filter.put("STATE", listJqlHisTask.get(i).getState());
						filter.put("BEGINTIME", listJqlHisTask.get(i).getBeginTime());
						filter.put("ENDTIME", listJqlHisTask.get(i).getEndTime());
						filter.put("PARENTTASKINSTID", listJqlHisTask.get(i).getParentTaskInstId());
						filter.put("TITLE", listJqlHisTask.get(i).getTitle());
						filter.put("PROCESSDEFID", listJqlHisTask.get(i).getProcessDefId());
						filter.put("OWNER", listJqlHisTask.get(i).getOwner());
						filter.put("ACTIVITYDEFID", ACTIVITYDEFID);
						newlist.add(filter);
					}
				}
			}
		}
		return newlist;
	}
	/**
	 * 根据流程实例ID，查询任务审批记录。
	 * 返回任务ID，任务记录创建者 map集合
	 * @param processid
	 * @return
	 */
	public static List<Map<String,Object>> getTaskComment(String processid){
		List<Map<String,Object>> list_taskdata = new ArrayList<Map<String,Object>>();
		List<TaskCommentModel> list = SDK.getProcessAPI().getCommentsById(processid);
		if(list != null && list.size() > 0){
			for(int i = 0;i < list.size();i++){
				String taskId = list.get(i).getTaskInstId();
				String taskCommentCreater = list.get(i).getCreateUser();
				Timestamp createTime = list.get(i).getCreateDate();
				Map<String,Object> map_task = new HashMap<String,Object>();
				map_task.put("taskId", taskId);
				map_task.put("taskCommentCreater",taskCommentCreater);
				map_task.put("createTime",createTime);
				list_taskdata.add(map_task);
			}
		}
		return list_taskdata;
	}
}
