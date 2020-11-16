package com.actionsoft.apps.cmcc.util;
/**
 * 审批意见公用方法
 * @author nch
 * @date 20170622
 */
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.bpmn.engine.model.run.TaskCommentModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class OptionUtil {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 获取流程所有待办任务，含子流程
	 * @param processid 主流程实例ID
	 * @param list_subProcessId 子流程实例ID集合
	 * @return
	 */
	public static List<TaskInstance> getWorkIngTaskModel(String processid,List<String> list_subProcessId){
		List<TaskInstance> list_task = new ArrayList<TaskInstance>(); 
		List<TaskInstance> list_parentTask = SDK.getTaskQueryAPI().userTaskOfWorking().addQuery("PROCESSINSTID = ", processid).addQuery("TASKSTATE != ", "4").orderBy("BEGINTIME").list();
		//添加主流程任务集合
		if(list_parentTask != null && list_parentTask.size() > 0){
			list_task.addAll(list_parentTask);
		}
		//添加子流程任务集合
		if(list_subProcessId != null && list_subProcessId.size() > 0){
			for(int i = 0;i < list_subProcessId.size();i++){
				String subProcessId = list_subProcessId.get(i);
				List<TaskInstance> list_subtask = SDK.getTaskQueryAPI().userTaskOfWorking().addQuery("PROCESSINSTID = ", subProcessId).addQuery("TASKSTATE != ", "4").orderBy("BEGINTIME").list();
				if(list_subtask != null && list_subtask.size() > 0){
					list_task.addAll(list_subtask);
				}
			}
		}
		List<TaskInstance> listbf = new ArrayList<TaskInstance>();
		if(list_task != null && list_task.size() > 0){
			for(int j = 0;j<list_task.size();j++){
				String taskid2 = list_task.get(j).getId();
				List<TaskInstance> list2 = SDK.getTaskQueryAPI().userTaskOfWorking().parentTaskInstId(taskid2).list();
				if(list2 == null || list2.size() == 0){
					listbf.add(list_task.get(j));
				}
			}
		}
		return listbf;
	}
	/**
	 * 立项审批List<TaskCommentModel>重新排序
	 * @param processid 主流程实例ID
	 * @param list_subProcessId 所有子流程实例ID集合
	 * @return
	 */
	public static List<TaskCommentModel> getTaskCommentModel(String processid,List<String> list_subProcessId){
		/*
		 * 通过流程实例Id获得流程实例的审批留言记录，主流程、子流程合并
		 */
		List<TaskCommentModel> list_parent = SDK.getProcessAPI().getCommentsById(processid);
		if(list_subProcessId != null && list_subProcessId.size() > 0){
			//遍历子流程实例ID
			for(int i = 0;i < list_subProcessId.size();i++){
				String subProcessId = list_subProcessId.get(i);
				//子流程审批记录
				List<TaskCommentModel> list_subTaskCommentModel = SDK.getProcessAPI().getCommentsById(subProcessId);
				if(list_subTaskCommentModel != null && list_subTaskCommentModel.size() > 0){
					//获取子流程审批记录的创建时间
					Timestamp subCreateTime = list_subTaskCommentModel.get(0).getCreateDate();
					boolean bol = false;
					//遍历父流程审批记录
					for(int j = 0;j<list_parent.size();j++){
						//获取主流程每条记录的创建时间
						Timestamp CreateTime = list_parent.get(j).getCreateDate();
						String processId = list_parent.get(j).getProcessInstId();
						//主流程审批记录创建时间第一次大于子流程开始时间，添加
						/*
						 * 主流程中间环节添加子流程审批记录，根据审批记录的创建日期
						 */
						if(processId.equals(processid) && CreateTime.getTime() > subCreateTime.getTime()){
							//放到中间
							list_parent.addAll(j, list_subTaskCommentModel);
							bol = true;
							break;
						}
					}
					//主流程审批记录创建时间全部小于子流程创建时间，添加
					if(!bol){
						list_parent.addAll(list_subTaskCommentModel);
					}
				}
			}
		}
		return list_parent;
	}
	/**
	 * 审批意见拼接html
	 * @param list
	 * @param tableName
	 * @return
	 */
	public static String optionMosaic(List<Map<String,String>> list,String tableName){
		StringBuffer html = new StringBuffer();
		list = filterList(list);//过滤审批意见list集合，审批意见msg为空，不显示
		int size = list.size();
		for(int h1 = 0;h1 < size; h1++){
			String msg = list.get(h1).get("msg");
			String deptname = list.get(h1).get("deptname");
			String userName = list.get(h1).get("userName");
			
			String createData = list.get(h1).get("createData");
			String zwdj = list.get(h1).get("zwdj");
			if(!UtilString.isEmpty(zwdj)){
				int zwdj_numb = Integer.parseInt(zwdj);
				if(zwdj_numb < 3){//font-size:18px;
					msg = "<div style='font-weight:bolder'>"+msg+"</div>";
				}
			}
			if(h1 == 0){
				html.append("<tr><td width=\"20%\" rowspan=\""+size+"\"><div align=\"center\"><div align=\"center\">");
				html.append(tableName+"</div></td><td width=\"80%\"><div align=\"left\">");
				html.append(msg + "</div><div align=\"right\">("+deptname+")"+userName+createData+"</div></td></tr>");
			}else{
				html.append("<tr><td width=\"80%\"><div align=\"left\">");
				html.append(msg + "</div><div align=\"right\">("+deptname+")"+userName+createData+"</div></td></tr>");
			}
		}
		return html.toString();
	}
	/**
	 * 过滤审批意见list集合，审批意见msg为空，不显示
	 * @author nch
	 * @date 2017-7-11
	 * @param list
	 * @return
	 */
	public static List<Map<String,String>> filterList(List<Map<String,String>> list){
		List<Map<String,String>> returnlist = new ArrayList<Map<String,String>>();
				
		if(list != null && list.size()>0){
			for(int h1 = 0;h1 < list.size(); h1++){
				String msg = list.get(h1).get("msg");
				//chenxf add 2018-5-8
				//填写意见人员
				String userName = list.get(h1).get("userName");
				/*
				 * 如果为管理员后台移交、跳转、激活等审批意见过滤
				 */
				if("管理员".equals(userName) || msg.contains("red")){
					continue;
				}
				if(!UtilString.isEmpty(msg)){
					returnlist.add(list.get(h1));
				}
			}
		}
		return returnlist;
	}
}
