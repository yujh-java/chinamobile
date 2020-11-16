package com.actionsoft.apps.cmcc.web;
/**
 * web处理类
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.apps.cmcc.VirtualIncome.VirtualIncomeConst;
import com.actionsoft.apps.cmcc.budget.BudgetConst;
import com.actionsoft.apps.cmcc.cancel.CancelConst;
import com.actionsoft.apps.cmcc.congruence.CongruenceConst;
import com.actionsoft.apps.cmcc.enterprise.ESConst;
import com.actionsoft.apps.cmcc.itemChange.ItemChangeConst;
import com.actionsoft.apps.cmcc.jx.JxActivityConst;
import com.actionsoft.apps.cmcc.lx.LxActivityIdConst;
import com.actionsoft.apps.cmcc.projectFeedback.ProjectFeedbackConst;
import com.actionsoft.apps.cmcc.resultSub.ResultConst;
import com.actionsoft.apps.cmcc.titleScore.TitleScoreConst;
import com.actionsoft.apps.cmcc.unplanedProject.UnplanedProjectConst;
import com.actionsoft.apps.cmcc.util.CommontUtil;
import com.actionsoft.apps.cmcc.util.OptionUtil;
import com.actionsoft.apps.cmcc.util.UtilEntryEventClass;
import com.actionsoft.apps.cmcc.yjyjx.YjyJX_Const;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.cache.util.UserTaskDefUtil;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.run.TaskCommentModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.commons.mvc.view.ActionWeb;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.bpms.util.UtilURL;
import com.actionsoft.exception.AWSForbiddenException;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ProcessExecuteQuery;

import net.sf.json.JSONObject;

public class CmccWeb extends ActionWeb {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public CmccWeb() {
		super();
	}

	public CmccWeb(UserContext userContext) {
		super(userContext);
	}
	
	/**
	 * 判断登录人所在部门是否是无领导部门
	 * @author nch
	 * @date 2017-7-11
	 * @param uc
	 * @return
	 */
	public String checkisNoleader(UserContext uc){
		ResponseObject rsobj = ResponseObject.newOkResponse();
		String outids = SDK.getAppAPI().getProperty(CmccConst.sourceAppId, CmccConst.propertyNoleaders);
		if(!UtilString.isEmpty(outids)){
			boolean bol = true;
			String deptid = uc.getDepartmentModel().getId();
			while(bol){
				DepartmentModel deptModel = DepartmentCache.getModel(deptid);
				if(deptModel != null){
					String outid_user = deptModel.getExt1();
					if(outids.contains(outid_user)){
						bol = false;
						rsobj.put("isNoleader", true);
					}else{
						deptid =  deptModel.getParentDepartmentId();
					}
				}else{
					bol = false;
					rsobj.put("isNoleader", false);
				}
			}
		}else{
			rsobj.put("isNoleader", false);
		}
		return rsobj.toString();
	}
	/*
	 * 根据任务ID，获取任务owner
	 */
	public String getTaskOwner(String taskId){
		TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskId);
		int taskState = taskInstance.getState();
		String ownerid = taskInstance.getOwner();
		String ownerName = SDK.getORGAPI().getUserNames(ownerid);
		ResponseObject rsobj = ResponseObject.newOkResponse();
		rsobj.put("ownerid", ownerid);
		rsobj.put("ownerName", ownerName);
		rsobj.put("taskState", taskState);
		return rsobj.toString();
	}
	/**
	 * 获取父任务状态
	 * @param parentTaskId
	 * @return
	 */
	public String getParentTaskState(String parentTaskId){
		String name="";
		List<RowMap> list =DBSql.getMaps("SELECT ACTIONNAME FROM WFC_COMMENT WHERE TASKINSTID=? ORDER BY CREATEDATE", new Object[]{parentTaskId});
		if(list!=null&&list.size()>0){
			name = list.get(list.size()-1).getString("ACTIONNAME");
		}
		TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(parentTaskId);
		if(taskInstance != null){
			int taskState = taskInstance.getState();
			ResponseObject rsobj = ResponseObject.newOkResponse();
			rsobj.put("taskState", taskState);
			rsobj.put("name", name);
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newErrResponse();
			return rsobj.toString();
		}
	}
	/**
	 * 查询父任务节点ID
	 * @param parentTaskId
	 * @return
	 * nch 20170622
	 */
	public String queryParentActivityId(String parentTaskId,String processId){
		String activityID = "";
		if(!UtilString.isEmpty(parentTaskId)){
			TaskInstance parent_taskInstance = SDK.getTaskAPI().getInstanceById(parentTaskId);
			if(parent_taskInstance != null){
				activityID = SDK.getTaskAPI().getInstanceById(parentTaskId).getActivityDefId();
			}
		}
		
		ResponseObject rsobj = ResponseObject.newOkResponse();
		//查询需求部门是否存在
		boolean bol = false;
		String isYjy = "否";
		String projectType = "";
		String PHYFJGBMID = "";
		List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID=", processId).list();
		if(list != null && list.size() > 0){
			String QTXQBM = list.get(0).getString("QTXQBM");//牵头需求部门
			if(!UtilString.isEmpty(QTXQBM)){
				bol = true;
			}else{
				String PHXQBM = list.get(0).getString("PHXQBM");//配合需求部门
				if(!UtilString.isEmpty(PHXQBM)){
					bol = true;
				}
			}
			projectType = list.get(0).getString("PROJECTTYPE");//项目类型
			String QTYFJGBM = list.get(0).getString("QTYFJGBM");
			if("00030087000000000000".equals(QTYFJGBM)){//是否为研究院
				isYjy = "是";
			}
			PHYFJGBMID = list.get(0).getString("PHYFJGBMID");//配合研发机构
			if(!UtilString.isEmpty(PHYFJGBMID)){
				/*
				 * 在表WFH_TASK中，查询是否已有配合研发部门审批
				 */
				String sql = "SELECT * FROM WFH_TASK "
						+ "WHERE ACTIVITYDEFID in ('"+CmccConst.lx_activityid+"','"+CmccConst.jx_activityid+"') "
						+ "AND PROCESSINSTID = '"+processId+"'";
				System.err.println("=====是否有配合部门会签sql:"+sql+"======");
				//配合部门会签集合
				List<RowMap> list_sql  = DBSql.getMaps(sql);
				//如果有配合部门会签，则显示总部接口人路径
				if(list_sql != null && list_sql.size() > 0){
					PHYFJGBMID = "true";
				}else{
					PHYFJGBMID = "false";
				}
			}
		}
		rsobj.put("parentActivityId", activityID);
		rsobj.put("PHYFJGBMID", PHYFJGBMID);
		rsobj.put("projectType", projectType);
		rsobj.put("isYjy", isYjy);
		rsobj.put("ISXQBM", bol);
		return rsobj.toString();
	}
	/**
	 * 查询项目信息ID
	 * @param processId
	 * @return
	 * nch 20170622
	 */
	public String queryXmid(String processId){
		String projectID = DBSql.getString("SELECT PROJECTID FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?", new Object[]{processId});
		if(UtilString.isEmpty(projectID)){
			ResponseObject rsobj = ResponseObject.newErrResponse();
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse();
			rsobj.put("projectID", projectID);
			return rsobj.toString();
		}
	}
	/**
	 * 根据流程实例ID，获取立项项目配合研发机构
	 * @param processId
	 * @return
	 * nch 20170622
	 */
	public String queryProjectPhyfjg(String processId){
		//配合研发机构部门ID
		String PHYFJGBMID = DBSql.getString("SELECT PHYFJGBMID FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?", new Object[]{processId});
		if(UtilString.isEmpty(PHYFJGBMID)){
			ResponseObject rsobj = ResponseObject.newErrResponse();
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse();
			return rsobj.toString();
		}
	}
	/**
	 * 根据流程实例ID，获取立项项目类型
	 * @param processId
	 * @return
	 * nch 20170622
	 */
	public String queryProjectType(String processId){
		ResponseObject rsobj = ResponseObject.newOkResponse();
		//项目类型
		List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", processId).list();
		if(list != null && list.size() > 0){
			String projectType = list.get(0).getString("PROJECTTYPE");
			String QTYFJGBM = list.get(0).getString("QTYFJGBM");
			String isYjy = "否";
			if("00030087000000000000".equals(QTYFJGBM)){//是否为研究院
				isYjy = "是";
			}
			rsobj.put("projectType", projectType);
			rsobj.put("isYjy", isYjy);
		}
		return rsobj.toString();
	}
	/**
	 * 根据父任务id，获得上一节点ID
	 * @param parentTaskId
	 * @return
	 * nch 20170622
	 */
	public String getParentActivetyid(String parentTaskId){
		//上一节点ID
		String parentStepId = SDK.getHistoryTaskQueryAPI().detailById(parentTaskId).getActivityDefId();
		ResponseObject rsobj = ResponseObject.newOkResponse();
		rsobj.put("parentStepId", parentStepId);

		return rsobj.toString();
	}
	/**
	 * 单步收回任务
	 * @param userid
	 * @param taskId
	 * @return
	 * nch 20170622
	 */
	public String undoTask(String userid,String taskId){
		TaskInstance taskInstance = SDK.getTaskAPI().getTaskInstance(taskId);//任务实例
		String processId = taskInstance.getProcessInstId();//任务实例ID
		List<TaskInstance> list = SDK.getTaskQueryAPI().taskState(1).processInstId(processId).list();//状态：1任务实例
		if(list != null && list.size() > 0){
			Timestamp readTime = list.get(0).getReadTime();//任务读取时间
			if(UtilString.isEmpty(readTime)){//未读任务允许收回
				TaskInstance untaskInstance = SDK.getTaskAPI().undoTask(taskId, userid, "");//判断是否允许回收
				if(untaskInstance != null){
					ResponseObject rsobj = ResponseObject.newOkResponse();
					return rsobj.toString();
				}
			}
		}
		ResponseObject errObj = ResponseObject.newErrResponse();
		return errObj.toString();
	}
	/**
	 * 判断是否允许撤销
	 * @param processid
	 * @return
	 */
	public String checkIsundoTask(String processid,UserContext uc){
		List<TaskInstance> list = SDK.getTaskQueryAPI().taskState(1).processInstId(processid).list();
		Boolean isundoTask = false;
		String parentTaskId = "";
		if(list != null && list.size() > 0){
			parentTaskId = list.get(0).getParentTaskInstId();//父任务实例ID
			TaskInstance parentTaskInstance = SDK.getTaskAPI().getInstanceById(parentTaskId);//父任务实例
			if(!"00000000-0000-0000-0000-000000000000".equals(parentTaskId)){//不为流程第一个节点
				String activityID = parentTaskInstance.getActivityDefId();//父任务节点ID
				boolean bol = SDK.getTaskAPI().isUndoTask(parentTaskId);
				String owner = list.get(0).getOwner();//创建人账号

				Timestamp readTime = list.get(0).getReadTime();//任务读取时间
				if(bol && owner.equals(uc.getUID()) && CmccConst.list_isundoTask.contains(activityID) 
						&& UtilString.isEmpty(readTime)){
					isundoTask = true;
				}else{
					isundoTask = false;
				}
			}
		}else{
			isundoTask = false;
		}
		ResponseObject rsobj = ResponseObject.newOkResponse();
		rsobj.put("isundoTask", isundoTask);
		rsobj.put("parentTaskId", parentTaskId);
		return rsobj.toString();
	}
	/**
	 * 查询审批意见（成果提交）
	 * @param processid
	 * @return
	 * @throws ParseException 
	 * nch 20170622
	 */
	public String queryOperatelog(String processid) {
		List<TaskCommentModel> list_now = new ArrayList<TaskCommentModel>();//当前流程实例ID审批意见

		//当前流程审批意见
		list_now = SDK.getProcessAPI().getCommentsById(processid);
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> list_yfjghq = new ArrayList<Map<String,String>>();//研发机构会签意见
		List<Map<String,String>> list_zbyfjg = new ArrayList<Map<String,String>>();//总部研发机构接口人
		List<Map<String,String>> list_xqbmsp = new ArrayList<Map<String,String>>();//需求部门审批意见
		List<Map<String,String>> list_jsbsp = new ArrayList<Map<String,String>>();//技术部意见

		if(list_now != null && list_now.size() > 0){
			for(int i = 0;i < list_now.size();i++){
				String activityName = list_now.get(i).getActivityName();//节点名
				String msg = list_now.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_now.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_now.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_now.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}					
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_now.get(i).getDepartmentName();
					String taskid = list_now.get(i).getTaskInstId();
					TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
					String activityDefid = taskInstance.getActivityDefId();
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);
					if(ResultConst.list_yfjghq.toString().contains(activityDefid)){
						list_yfjghq.add(map);
					}else if(ResultConst.list_zbyfjg.toString().contains(activityDefid)){
						list_zbyfjg.add(map);
					}else if(ResultConst.list_xqbmsp.toString().contains(activityDefid)){
						list_xqbmsp.add(map);
					}else if(ResultConst.list_jsbsp.toString().contains(activityDefid)){
						list_jsbsp.add(map);
					}
				}

			}
		}
		if(list_yfjghq != null && list_yfjghq.size() > 0){
			html.append(OptionUtil.optionMosaic(list_yfjghq, ResultConst.yfjghq_name));
		}
		if(list_zbyfjg != null && list_zbyfjg.size() > 0){
			html.append(OptionUtil.optionMosaic(list_zbyfjg, ResultConst.zbyfjg_name));
		}
		if(list_xqbmsp != null && list_xqbmsp.size() > 0){
			html.append(OptionUtil.optionMosaic(list_xqbmsp, ResultConst.xqbmsp_name));
		}
		if(list_jsbsp != null && list_jsbsp.size() > 0){
			html.append(OptionUtil.optionMosaic(list_jsbsp, ResultConst.jsbsp_name));
		}
		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}
	/**
	 * 根据流程实例ID，定义ID，获取审批记录
	 * 普通流程，不含子流程
	 * @param processId
	 * @param processDefid
	 * @return
	 */
	public String getOptionHtml(String processId,String processDefid){
		Map<String, Object> map = new HashMap<String, Object>();
		String title = DBSql.getString("SELECT PROTRACKTITLE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?", new Object[]{processDefid});
		StringBuffer sb_table = new StringBuffer();
		Map<String,Map<String,String>> mapParenttask = new HashMap<String,Map<String,String>>();
		List<String> list_taskId = new ArrayList<String>();//任务ID,校验循环一次
		//查询待办任务实例
		List<TaskInstance> list_task = SDK.getTaskQueryAPI().userTaskOfWorking().addQuery("PROCESSINSTID = ", processId).orderBy("BEGINTIME").list();

		//查询审批记录
		List<TaskCommentModel> list = new ArrayList<TaskCommentModel>();//当前流程实例ID审批意见
		//当前流程审批意见
		list = SDK.getProcessAPI().getCommentsById(processId);
		if(list != null && list.size() > 0){
			for(int j = 0;j < list.size();j++){
				String taskId = list.get(j).getTaskInstId();//任务实例ID
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskId);
				String parentTaskId = taskInstance.getParentTaskInstId();//父任务实例ID
				String activityName = list.get(j).getActivityName();//节点名
				String createUserid = list.get(j).getCreateUser();//留言人账号
				if(list_taskId == null || !list_taskId.contains(taskId)){
					if(mapParenttask != null && mapParenttask.containsKey(parentTaskId)){
						Map<String,String> map_nextUser = mapParenttask.get(parentTaskId);
						if(map_nextUser != null && map_nextUser.containsKey(activityName)){
							String nextUser = map_nextUser.get(activityName);
							if(UtilString.isEmpty(nextUser)){
								map_nextUser.put(activityName, createUserid);//节点名称，创建人账号
							}else if(!nextUser.contains(createUserid)){
								map_nextUser.put(activityName, nextUser+","+createUserid);//节点名称，创建人账号
							}
						}else{
							map_nextUser.put(activityName, createUserid);//节点名称，创建人账号
						}
						mapParenttask.put(parentTaskId, map_nextUser);//父任务实例ID，下一节点信息
					}else{
						Map<String,String> map_nextUser = new HashMap<String,String>();
						map_nextUser.put(activityName, createUserid);//节点名称，创建人账号
						mapParenttask.put(parentTaskId, map_nextUser);//父任务实例ID，下一节点信息
					}
					list_taskId.add(taskId);
				}
			}
		}
		//待办任务过滤
		if(list_task != null && list_task.size() > 0){
			for(int t = 0;t < list_task.size();t++){
				String activityId = list_task.get(t).getActivityDefId();//当前节点ID
				String parentTaskId = list_task.get(t).getParentTaskInstId();//父任务实例ID
				String target = list_task.get(t).getTarget();
				UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefid, activityId);
				String activityName = taskModel.name;//节点名称

				String taskId = list_task.get(t).getId();//当前任务ID
				if(list_taskId == null || !list_taskId.contains(taskId)){
					if(mapParenttask != null && mapParenttask.containsKey(parentTaskId)){
						Map<String,String> map_nextUser = mapParenttask.get(parentTaskId);
						if(map_nextUser != null && map_nextUser.containsKey(activityName)){
							String nextUser = map_nextUser.get(activityName);
							if(UtilString.isEmpty(nextUser)){
								map_nextUser.put(activityName, target);
							}else if(!nextUser.contains(target)){
								map_nextUser.put(activityName, nextUser+","+target);
							}
						}else{
							map_nextUser.put(activityName, target);
						}
					}else{
						Map<String,String> map_nextUser = new HashMap<String,String>();
						map_nextUser.put(activityName, target);
						mapParenttask.put(parentTaskId, map_nextUser);
					}
					list_taskId.add(taskId);
				}
			}
		}
		Map<String,String> map_lastTaskTime = new HashMap<String,String>();
		//循环已办审批记录，拼接html
		if(list != null && list.size() > 0){
			for(int i = 0;i<list.size();i++){
				String activityName = list.get(i).getActivityName();//节点名
				String createUserid = list.get(i).getCreateUser();//留言人账号
				String userName = UserCache.getModel(createUserid).getUserName();
				String taskId = list.get(i).getTaskInstId();//任务实例ID
				Timestamp commentCreateTime = list.get(i).getCreateDate();
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskId);
				String actionName = list.get(i).getActionName();//操作路径
				String createData = "";//任务创建时间
				if(map_lastTaskTime == null || !map_lastTaskTime.containsKey(taskId)){
					Timestamp createTimestamp = taskInstance.getBeginTime();//审批记录创建时间
					createData = sdf.format(createTimestamp);//开始时间
				}else{
					createData = map_lastTaskTime.get(taskId);
				}
				Timestamp endTimestamp = commentCreateTime;
				String endData = "";
				if(UtilString.isEmpty(endTimestamp)){
					endData = "未结束";
				}else{
					endData = sdf.format(endTimestamp);//结束时间
				}
				int tasState = taskInstance.getState();//任务状态
				String nextActivityUser = "";//后续处理节点
				String nextActivityName = actionName;//后续路径
				if(mapParenttask != null && mapParenttask.containsKey(taskId)){
					Map<String,String> mapNextUser = mapParenttask.get(taskId);
					if(mapNextUser != null && mapNextUser.size() > 0){
						for(String key:mapNextUser.keySet()){
							String users = "";
							if(!key.equals(activityName)){
								users = mapNextUser.get(key);
							}else{
								continue;
							}
							StringBuffer username_sb = new StringBuffer();
							if(!UtilString.isEmpty(users)){
								String[] usersArr = users.split(",");
								for(int m = 0;m < usersArr.length;m++){
									String userName2 = UserCache.getModel(usersArr[m]).getUserName();
									username_sb.append(userName2+",");
								}
							}
							if(username_sb.toString().length()>0){
								nextActivityUser =nextActivityUser + key+":"+username_sb.substring(0, username_sb.toString().length()-1);
							}else{
								nextActivityUser =nextActivityUser + key+":";
							}
						}
					}
				}
				if("加签".equals(actionName) || "阅办".equals(actionName)){
					if(tasState == 11){
						actionName = "相关部门会签";
					}
					nextActivityName = "会签";
					StringBuffer sb_userName = new StringBuffer();
					Timestamp commentCreateTime2 = null;//下次任务记录出现时间
					Map<String,String> mapTaskId = new HashMap<String,String>();
					for(int subTask = i+1;subTask<list.size();subTask++){
						String taskId2 = list.get(subTask).getTaskInstId();
						String parentTaskId2 = SDK.getTaskAPI().getInstanceById(taskId2).getParentTaskInstId();
						if(taskId2.equals(taskId)){
							commentCreateTime2 = list.get(subTask).getCreateDate();
							break;
						}else if(parentTaskId2.equals(taskId)){
							if(mapTaskId == null || mapTaskId.size() == 0 || !mapTaskId.containsKey(taskId)){
								String target = SDK.getTaskAPI().getInstanceById(taskId2).getTarget();
								String targetName = UserCache.getModel(target).getUserName();
								sb_userName.append(targetName+",");
								mapTaskId.put(taskId, taskId);
							}
						}
					}
					List<TaskInstance> parentTask_list = new ArrayList<TaskInstance>();
					if(UtilString.isEmpty(commentCreateTime2)){
						//由当前任务创建的任务
						parentTask_list = SDK.getTaskQueryAPI().parentTaskInstId(taskId).list();
					}
					if(parentTask_list != null && parentTask_list.size()>0){
						for(int i2 = 0;i2< parentTask_list.size();i2++){
							String target2 = parentTask_list.get(i2).getTarget();
							String target2Name = UserCache.getModel(target2).getUserName();
							sb_userName.append(target2Name+",");
						}
					}
					if(!UtilString.isEmpty(sb_userName.toString())){
						nextActivityUser = "相关部门会签"+":"+sb_userName.toString().substring(0, sb_userName.toString().length()-1);
					}
				}
				if("加签完毕".equals(actionName) || "阅办完毕".equals(actionName)){
					nextActivityName = "会签完毕";
					String parenttaskId3 = taskInstance.getParentTaskInstId();
					String parenttargetId = SDK.getTaskAPI().getTaskInstance(parenttaskId3).getTarget();
					String parenttargetName = UserCache.getModel(parenttargetId).getUserName();
					nextActivityUser = activityName+":"+parenttargetName;
					activityName = "相关部门会签";
				}
				if("转办".equals(actionName)){
					String msg = list.get(i).getMsg();
					if(!UtilString.isEmpty(msg)){
						nextActivityUser = msg.split("，")[0];
					}
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
				}
				sb_table.append("<tr><td ><FONT color='#000000'>"+activityName+"</FONT></td>");
				sb_table.append("<td ><FONT color='#000000'>"+userName+"</FONT></td>");
				sb_table.append("<td ><FONT color='#000000'>"+createData+"</FONT></td>");
				sb_table.append("<td ><FONT color='#000000'>"+endData+"</FONT></td>");
				sb_table.append("<td ><FONT color='#000000'>"+nextActivityName+"</FONT></td>");
				sb_table.append("<td ><FONT color='#000000'>"+nextActivityUser+"</FONT></td>");
				sb_table.append("</tr><tr><td colspan='6'><HR noshade style='height:1px;border-width:0;color:gray;background-color:gray' width='100%' align='right' ></td></tr>");
				map_lastTaskTime.put(taskId, endData);
			}
		}
		//待办任务查询过滤，拼接html
		if(list_task != null && list_task.size() > 0){
			for(int i = 0;i< list_task.size();i++){
				String activityId = list_task.get(i).getActivityDefId();//当前节点ID
				String target = list_task.get(i).getTarget();
				String userName = SDK.getORGAPI().getUser(target).getUserName();
				UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefid, activityId);
				String taskId = list_task.get(i).getId();
				String activityName = taskModel.name;//节点名称
				int taskState = list_task.get(i).getState();//任务状态
				if(taskState == 11){
					activityName = "相关部门会签";
				}
				String createData = "";//任务创建时间
				if(map_lastTaskTime == null || !map_lastTaskTime.containsKey(taskId)){
					Timestamp createTimestamp = list_task.get(i).getBeginTime();
					createData = sdf.format(createTimestamp);//开始时间
				}else{
					createData = map_lastTaskTime.get(taskId);
				}
				String endData = "未结束";
				String nextActivityName = "未提交";
				String nextActivityUser = "无";
				sb_table.append("<tr><td ><FONT color='#000000'>"+activityName+"</FONT></td>");
				sb_table.append("<td ><FONT color='#000000'>"+userName+"</FONT></td>");
				sb_table.append("<td ><FONT color='#000000'>"+createData+"</FONT></td>");
				sb_table.append("<td ><FONT color='#000000'>"+endData+"</FONT></td>");
				sb_table.append("<td ><FONT color='#000000'>"+nextActivityName+"</FONT></td>");
				sb_table.append("<td ><FONT color='#000000'>"+nextActivityUser+"</FONT></td>");
				sb_table.append("</tr><tr><td colspan='6'><HR noshade style='height:1px;border-width:0;color:gray;background-color:gray' width='100%' align='right' ></td></tr>");

			}
		}
		map.put("title", title);
		map.put("optionMsg", sb_table.toString());
		return HtmlPageTemplate.merge("com.actionsoft.apps.cmcc", "com.actionsoft.apps.cmcc.regzt.htm", map);
	}
	/**
	 * 查询审批意见（立项）
	 * @param processid
	 * @return
	 * @throws ParseException 
	 * nch 20170622
	 */
	public String queryOperatelog_lx(String processId,String processDefid) {
		List<TaskCommentModel> list_parent = new ArrayList<TaskCommentModel>();//父流程实例ID审批意见
		List<TaskCommentModel> list_sub = new ArrayList<TaskCommentModel>();//子流程实例ID审批意见
		String processId_parent = "";//父流程实例ID
		List<RowMap> list_subprocessId = new ArrayList<RowMap>();//子流程实例ID集合
		if(CommontUtil.isMainProcess(processDefid)){
			processId_parent = processId;
			list_subprocessId = DBSql.getMaps("SELECT ID FROM WFC_PROCESS WHERE PARENTPROCESSINSTID= ? ORDER BY STARTTIME ASC", new Object[]{processId});
		}else{
			processId_parent = DBSql.getString("SELECT PARENTPROCESSINSTID FROM WFC_PROCESS WHERE ID=?", new Object[]{processId});
			list_subprocessId = DBSql.getMaps("SELECT ID FROM WFC_PROCESS WHERE PARENTPROCESSINSTID= ? ORDER BY STARTTIME ASC", new Object[]{processId_parent});
		}

		//当前流程审批意见
		list_parent = SDK.getProcessAPI().getCommentsById(processId_parent);
		if(list_subprocessId != null && list_subprocessId.size() > 0){
			for(int i = 0;i < list_subprocessId.size();i++){
				String sub_processId = list_subprocessId.get(i).getString("ID");
				List<TaskCommentModel> list_subprocess = SDK.getProcessAPI().getCommentsById(sub_processId);
				list_sub.addAll(list_subprocess);
			}
		}

		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> list_qtyfjghq = new ArrayList<Map<String,String>>();//牵头研发机构会签意见
		List<Map<String,String>> list_phyfjghq = new ArrayList<Map<String,String>>();//牵头配合研发机构会签意见
		List<Map<String,String>> list_xqbmsp = new ArrayList<Map<String,String>>();//需求/配合部门审批意见
		List<Map<String,String>> list_jsbsp = new ArrayList<Map<String,String>>();//技术部意见
		List<Map<String,String>> list_jtgs = new ArrayList<Map<String,String>>();//公司领导意见
		List<Map<String,String>> list_zbyfjkr = new ArrayList<Map<String,String>>();//总部研发接口人意见
		List<Map<String,String>> list_xgbmls = new ArrayList<Map<String,String>>();//相关部门落实意见
		list_parent.addAll(list_sub);
		if(list_parent != null && list_parent.size() > 0){
			for(int i = 0;i < list_parent.size();i++){
				String activityName = list_parent.get(i).getActivityName();//节点名
				String msg = list_parent.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
					if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
						String str = msg.split("原因：", 2)[1];
						if(str.equals("无")){
							msg= "";
						}else{
							msg=str;
						}
					}	
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_parent.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_parent.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_parent.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_parent.get(i).getDepartmentName();					
					String taskid =  list_parent.get(i).getTaskInstId();
					TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
					String activityDefid = taskInstance.getActivityDefId();
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);
					if(LxActivityIdConst.list_qtyfjghq.toString().contains(activityDefid)){
						list_qtyfjghq.add(map);
					}else if(LxActivityIdConst.list_phyfjghq.toString().contains(activityDefid)){
						list_phyfjghq.add(map);
					}else if(LxActivityIdConst.list_zbyfjkr.toString().contains(activityDefid)){
						list_zbyfjkr.add(map);
					}else if(LxActivityIdConst.list_xqbmsp.toString().contains(activityDefid)){
						list_xqbmsp.add(map);
					}else if(LxActivityIdConst.list_jsbsp.toString().contains(activityDefid)){
						list_jsbsp.add(map);
					}else if(LxActivityIdConst.list_jtgs.toString().contains(activityDefid)){
						list_jtgs.add(map);
					}else if(LxActivityIdConst.list_xgbmls.toString().contains(activityDefid)){
						list_xgbmls.add(map);
					}
				}

			}
		}
		if(list_qtyfjghq != null && list_qtyfjghq.size() > 0){
			html.append(OptionUtil.optionMosaic(list_qtyfjghq, LxActivityIdConst.qtyfjghq_name));
		}
		if(list_phyfjghq != null && list_phyfjghq.size() > 0){
			html.append(OptionUtil.optionMosaic(list_phyfjghq, LxActivityIdConst.phyfjghq_name));
		}
		if(list_zbyfjkr != null && list_zbyfjkr.size() > 0){
			html.append(OptionUtil.optionMosaic(list_zbyfjkr, LxActivityIdConst.zbyfjkr_name));
		}
		if(list_xqbmsp != null && list_xqbmsp.size() > 0){
			html.append(OptionUtil.optionMosaic(list_xqbmsp, LxActivityIdConst.xqbmsp_name));
		}
		if(list_jsbsp != null && list_jsbsp.size() > 0){
			html.append(OptionUtil.optionMosaic(list_jsbsp, LxActivityIdConst.jsbsp_name));
		}
		if(list_jtgs != null && list_jtgs.size() > 0){
			html.append(OptionUtil.optionMosaic(list_jtgs, LxActivityIdConst.jtgs_name));
		}
		if(list_xgbmls != null && list_xgbmls.size() > 0){
			html.append(OptionUtil.optionMosaic(list_xgbmls, LxActivityIdConst.xgbmls_name));
		}
		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}
	/**
	 * 查询审批意见（结项）
	 * @param processid
	 * @return
	 * @throws ParseException 
	 * nch 20170622
	 */
	public String queryOperatelog_jx(String processId,String processDefid) {
		List<TaskCommentModel> list_parent = new ArrayList<TaskCommentModel>();//父流程实例ID审批意见
		List<TaskCommentModel> list_sub = new ArrayList<TaskCommentModel>();//子流程实例ID审批意见
		String processId_parent = "";//父流程实例ID
		List<RowMap> list_subprocessId = new ArrayList<RowMap>();//子流程实例ID集合
		if(CommontUtil.isMainProcess(processDefid)){
			processId_parent = processId;
			list_subprocessId = DBSql.getMaps("SELECT ID FROM WFC_PROCESS WHERE PARENTPROCESSINSTID= ?  ORDER BY STARTTIME ASC", new Object[]{processId});
		}else{
			processId_parent = DBSql.getString("SELECT PARENTPROCESSINSTID FROM WFC_PROCESS WHERE ID=?", new Object[]{processId});
			list_subprocessId = DBSql.getMaps("SELECT ID FROM WFC_PROCESS WHERE PARENTPROCESSINSTID= ? ORDER BY STARTTIME ASC", new Object[]{processId_parent});
		}

		//当前流程审批意见
		list_parent = SDK.getProcessAPI().getCommentsById(processId_parent);
		if(list_subprocessId != null && list_subprocessId.size() > 0){
			for(int i = 0;i < list_subprocessId.size();i++){
				String sub_processId = list_subprocessId.get(i).getString("ID");
				List<TaskCommentModel> list_subprocess = SDK.getProcessAPI().getCommentsById(sub_processId);
				list_sub.addAll(list_subprocess);
			}
		}

		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> list_qtyfjghq = new ArrayList<Map<String,String>>();//牵头研发机构会签意见
		List<Map<String,String>> list_phyfjghq = new ArrayList<Map<String,String>>();//牵头配合研发机构会签意见
		List<Map<String,String>> list_xqbmsp = new ArrayList<Map<String,String>>();//需求/配合部门审批意见
		List<Map<String,String>> list_jsbsp = new ArrayList<Map<String,String>>();//技术部意见
		List<Map<String,String>> list_jtgs = new ArrayList<Map<String,String>>();//公司领导意见
		List<Map<String,String>> list_zbyfjkr = new ArrayList<Map<String,String>>();//总部研发接口人意见
		List<Map<String,String>> list_xgbmls = new ArrayList<Map<String,String>>();//相关部门落实意见
		list_parent.addAll(list_sub);
		if(list_parent != null && list_parent.size() > 0){
			for(int i = 0;i < list_parent.size();i++){
				String activityName = list_parent.get(i).getActivityName();//节点名
				String msg = list_parent.get(i).getMsg();//留言信息
				
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
					if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
						String str = msg.split("原因：", 2)[1];
						if(str.equals("无")){
							msg= "";
						}else{
							msg=str;
						}
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_parent.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_parent.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_parent.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_parent.get(i).getDepartmentName();					
					String taskid =  list_parent.get(i).getTaskInstId();
					TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
					String activityDefid = taskInstance.getActivityDefId();
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);
					if(JxActivityConst.list_qtyfjghq.toString().contains(activityDefid)){
						list_qtyfjghq.add(map);
					}else if(JxActivityConst.list_phyfjghq.toString().contains(activityDefid)){						
						list_phyfjghq.add(map);
					}else if(JxActivityConst.list_zbyfjkr.toString().contains(activityDefid)){
						list_zbyfjkr.add(map);
					}else if(JxActivityConst.list_xqbmsp.toString().contains(activityDefid)){
						list_xqbmsp.add(map);
					}else if(JxActivityConst.list_jsbsp.toString().contains(activityDefid)){
						list_jsbsp.add(map);
					}else if(JxActivityConst.list_jtgs.toString().contains(activityDefid)){
						list_jtgs.add(map);
					}else if(JxActivityConst.list_xgbmls.toString().contains(activityDefid)){
						list_xgbmls.add(map);
					}
				}

			}
		}
		if(list_qtyfjghq != null && list_qtyfjghq.size() > 0){
			html.append(OptionUtil.optionMosaic(list_qtyfjghq, JxActivityConst.qtyfjghq_name));
		}
		if(list_phyfjghq != null && list_phyfjghq.size() > 0){
			html.append(OptionUtil.optionMosaic(list_phyfjghq, JxActivityConst.phyfjghq_name));
		}
		if(list_zbyfjkr != null && list_zbyfjkr.size() > 0){
			html.append(OptionUtil.optionMosaic(list_zbyfjkr, JxActivityConst.zbyfjkr_name));
		}
		if(list_xqbmsp != null && list_xqbmsp.size() > 0){
			html.append(OptionUtil.optionMosaic(list_xqbmsp, JxActivityConst.xqbmsp_name));
		}
		if(list_jsbsp != null && list_jsbsp.size() > 0){
			html.append(OptionUtil.optionMosaic(list_jsbsp, JxActivityConst.jsbsp_name));
		}
		if(list_jtgs != null && list_jtgs.size() > 0){
			html.append(OptionUtil.optionMosaic(list_jtgs, JxActivityConst.jtgs_name));
		}
		if(list_xgbmls != null && list_xgbmls.size() > 0){
			html.append(OptionUtil.optionMosaic(list_xgbmls, JxActivityConst.xgbmls_name));
		}
		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}
	/**
	 * 查询结题评分审批意见
	 * @param processid
	 * @return
	 * @throws ParseException 
	 * zhaoxs 2017-06-22
	 */
	public String queryOperatelog_TitleScore(String processid) {
		List<TaskCommentModel> list_now = new ArrayList<TaskCommentModel>();//当前流程实例ID审批意见
		//当前流程审批意见
		list_now = SDK.getProcessAPI().getCommentsById(processid);
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> list_xqbmyj = new ArrayList<Map<String,String>>();//需求部门意见 （结题评分）
		List<Map<String,String>> list_jsbyj = new ArrayList<Map<String,String>>();//技术部意见 （结题评分）
		if(list_now != null && list_now.size() > 0){
			for(int i = 0;i < list_now.size();i++){
				String activityName = list_now.get(i).getActivityName();//节点名
				String msg = list_now.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_now.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				String taskid = list_now.get(i).getTaskInstId();
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
				String activityDefid = taskInstance.getActivityDefId();
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_now.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_now.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_now.get(i).getDepartmentName();					
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);

					if(TitleScoreConst.list_jsbyjNoteid.contains(activityDefid)){
						list_jsbyj.add(map);
					}else if(TitleScoreConst.list_xqbmyjNoteid.contains(activityDefid)){
						list_xqbmyj.add(map);
					}
				}
			}

		}

		if(list_xqbmyj !=null && list_xqbmyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_xqbmyj, TitleScoreConst.xqbmyjName));
		}
		if(list_jsbyj !=null && list_jsbyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_jsbyj, TitleScoreConst.jsbyjName));
		}


		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}

	/**
	 * 一般委托项目启动反馈审批意见
	 * @param processid
	 * @return
	 * @throws ParseException 
	 * zhaoxs 2017-06-22
	 */
	public String queryOperatelog_ProjectFeedback(String processid) {
		List<TaskCommentModel> list_now = new ArrayList<TaskCommentModel>();//当前流程实例ID审批意见
		//当前流程审批意见
		list_now = SDK.getProcessAPI().getCommentsById(processid);
		//拼接html
		StringBuffer html = new StringBuffer();

		List<Map<String,String>> list_xqbmqdyj = new ArrayList<Map<String,String>>();//需求部门启动意见
		List<Map<String,String>> list_yfjgqryj = new ArrayList<Map<String,String>>();//研发机构确认意见
		List<Map<String,String>> list_jsbyj = new ArrayList<Map<String,String>>();//技术部意见
		List<Map<String,String>> list_yfjgblyj = new ArrayList<Map<String,String>>();//研发机构办理意见
		List<Map<String,String>> list_xqbmfkyj = new ArrayList<Map<String,String>>();//需求部门反馈意见

		if(list_now != null && list_now.size() > 0){
			for(int i = 0;i < list_now.size();i++){
				String activityName = list_now.get(i).getActivityName();//节点名
				String msg = list_now.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_now.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				String taskid = list_now.get(i).getTaskInstId();
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
				String activityDefid = taskInstance.getActivityDefId();
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_now.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_now.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_now.get(i).getDepartmentName();					
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);

					if(ProjectFeedbackConst.list_xqbmqdyjNoteid.contains(activityDefid)){
						list_xqbmqdyj.add(map);
					}else if(ProjectFeedbackConst.list_yfjgqryjNoteid.contains(activityDefid)){
						list_yfjgqryj.add(map);
					}else if(ProjectFeedbackConst.list_jsbyjNoteid.contains(activityDefid))
					{
						list_jsbyj.add(map);
					}else if(ProjectFeedbackConst.list_yfjgblyjNoteid.contains(activityDefid))
					{
						list_yfjgblyj.add(map);	
					}else if(ProjectFeedbackConst.list_xqbmfkyjNoteid.contains(activityDefid))
					{
						list_xqbmfkyj.add(map);
					}
				}
			}

		}		

		if(list_xqbmqdyj !=null && list_xqbmqdyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_xqbmqdyj, ProjectFeedbackConst.xqbmqdyjName));
		}
		if(list_yfjgqryj !=null && list_yfjgqryj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_yfjgqryj, ProjectFeedbackConst.yfjgqryjName));
		}

		if(list_jsbyj !=null && list_jsbyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_jsbyj, ProjectFeedbackConst.jsbyjName));
		}
		if(list_yfjgblyj !=null && list_yfjgblyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_yfjgblyj, ProjectFeedbackConst.yfjgblyjName));
		}
		if(list_xqbmfkyj !=null && list_xqbmfkyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_xqbmfkyj, ProjectFeedbackConst.xqbmfkyjName));
		}

		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}
	/**
	 * 一般委托项目启动反馈审批意见
	 * @author wuxx
	 * @date   20190619
	 * @param processId
	 * @param processDefid
	 * @return
	 */
	public String getFSOpinionnew(String processId,String processDefid){
		

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<TaskCommentModel> list_parent = new ArrayList<TaskCommentModel>();//父流程实例ID审批意见
		List<TaskCommentModel> list_sub = new ArrayList<TaskCommentModel>();//子流程实例ID审批意见
		String processId_parent = "";//父流程实例ID
		List<RowMap> list_subprocessId = new ArrayList<RowMap>();//子流程实例ID集合
		/*System.err.println("processId++++---"+processId);
		System.err.println("processDefid"+processDefid);*/
		if(ProjectFeedbackConst.isMainProcess(processDefid)){//判断是否是主流程
			processId_parent = processId;
			//System.err.println("processId_parent1++++"+processId_parent);
			list_subprocessId = DBSql.getMaps("SELECT ID FROM WFC_PROCESS WHERE PARENTPROCESSINSTID= ? ORDER BY STARTTIME ASC", new Object[]{processId});
		}else{
			processId_parent = DBSql.getString("SELECT PARENTPROCESSINSTID FROM WFC_PROCESS WHERE ID=?", new Object[]{processId});
			//System.err.println("processId_parent2++++"+processId_parent);
			list_subprocessId = DBSql.getMaps("SELECT ID FROM WFC_PROCESS WHERE PARENTPROCESSINSTID= ? ORDER BY STARTTIME ASC", new Object[]{processId_parent});
		}
		//System.err.println("processId_parent++++"+processId_parent);

		//当前流程审批意见
		list_parent = SDK.getProcessAPI().getCommentsById(processId_parent);
		//System.err.println("list_parent"+list_parent);
		if(list_subprocessId != null && list_subprocessId.size() > 0){
			for(int i = 0;i < list_subprocessId.size();i++){
				String sub_processId = list_subprocessId.get(i).getString("ID");
				List<TaskCommentModel> list_subprocess = SDK.getProcessAPI().getCommentsById(sub_processId);
				list_sub.addAll(list_subprocess);
			}
		}
		
		System.err.println("=====获取审批意见1111：=========");
		
		//拼接html
		StringBuffer html = new StringBuffer();
		/*List<Map<String, String>> list_jsbyj = new ArrayList();
	    List<Map<String, String>> list_yfjgyj = new ArrayList();
	    List<Map<String, String>> list_xqjgyj = new ArrayList();
	    List<Map<String, String>> list_qbglyyj = new ArrayList();*/
		List<Map<String,String>> list_xqbmqdyj = new ArrayList<Map<String,String>>();//需求部门启动意见
		List<Map<String,String>> list_yfjgqryj = new ArrayList<Map<String,String>>();//研发机构确认意见
		List<Map<String,String>> list_jsbyj = new ArrayList<Map<String,String>>();//技术部意见
		List<Map<String,String>> list_yfjgblyj = new ArrayList<Map<String,String>>();//研发机构办理意见
		List<Map<String,String>> list_xqbmfkyj = new ArrayList<Map<String,String>>();//需求部门反馈意见
		list_parent.addAll(list_sub);
		if(list_parent != null && list_parent.size() > 0){
			for(int i = 0;i < list_parent.size();i++){
				String activityName = list_parent.get(i).getActivityName();//节点名
				String msg = list_parent.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
					if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
						String str = msg.split("原因：", 2)[1];
						if(str.equals("无")){
							msg= "";
						}else{
							msg=str;
						}
					}	
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_parent.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_parent.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_parent.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String taskid =  list_parent.get(i).getTaskInstId();
					TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
					String activityDefid = taskInstance.getActivityDefId();
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);
					if(ProjectFeedbackConst.list_xqbmqdyjNoteid.contains(activityDefid)){
						list_xqbmqdyj.add(map);
					}else if(ProjectFeedbackConst.list_yfjgqryjNoteid.contains(activityDefid)){
						list_yfjgqryj.add(map);
					}else if(ProjectFeedbackConst.list_jsbyjNoteid.contains(activityDefid))
					{
						list_jsbyj.add(map);
					}else if(ProjectFeedbackConst.list_yfjgblyjNoteid.contains(activityDefid))
					{
						list_yfjgblyj.add(map);	
					}else if(ProjectFeedbackConst.list_xqbmfkyjNoteid.contains(activityDefid))
					{
						list_xqbmfkyj.add(map);
					}
					
					
				}

			}
		}
		/*if ((list_yfjgyj != null) && (list_yfjgyj.size() > 0)) {
		      html.append(OptionUtil.optionMosaic(list_yfjgyj, CmccCommonNew.yfdwName));//承担单位意见
	    }
		if ((list_xqjgyj != null) && (list_xqjgyj.size() > 0)) {
	        html.append(OptionUtil.optionMosaic(list_xqjgyj, CmccCommonNew.xqdwName));//技术部和需求单位意见
	      }
		if ((list_qbglyyj != null) && (list_qbglyyj.size() > 0)) {
	        html.append(OptionUtil.optionMosaic(list_qbglyyj, CmccCommonNew.qbglyName));//企标管理员意见
	      }*/
		if(list_xqbmqdyj !=null && list_xqbmqdyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_xqbmqdyj, ProjectFeedbackConst.xqbmqdyjName));
		}
		if(list_yfjgqryj !=null && list_yfjgqryj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_yfjgqryj, ProjectFeedbackConst.yfjgqryjName));
		}

		if(list_jsbyj !=null && list_jsbyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_jsbyj, ProjectFeedbackConst.jsbyjName));
		}
		if(list_yfjgblyj !=null && list_yfjgblyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_yfjgblyj, ProjectFeedbackConst.yfjgblyjName));
		}
		if(list_xqbmfkyj !=null && list_xqbmfkyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_xqbmfkyj, ProjectFeedbackConst.xqbmfkyjName));
		}
	    
	    
		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}

	/**
	 * 查询一般委托流程的研发机构和任务名称
	 * @param processid
	 * @return
	 * @throws ParseException 
	 */	
	public String queryOperatelog_QueryYFJG(String processId) {
		String yfjgid="";
		String taskname="";
		String taskUser="";
		String xqbmUser="";
		String createuser="";
		List<BO> list =SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", processId).list();
		if(list!=null&&list.size()>0)
		{
			for(BO bo : list){
				yfjgid = bo.getString("QTYFJGBMID");//研发机构id
				taskname = bo.getString("PHYFJGBM");//任务名称
				taskUser = bo.getString("PROJECTTYPE");//任务负责人
				xqbmUser = bo.getString("QTXQBM");
				createuser = bo.getString("CREATEUSERID");//任务发起人
			}
		}
		String yfjgname = DBSql.getString("SELECT DEPARTMENTNAME FROM ORGDEPARTMENT WHERE OUTERID=?",new Object[]{yfjgid});
		ResponseObject res = ResponseObject.newOkResponse();

		String taskUserName = DBSql.getString("SELECT USERNAME FROM ORGUSER WHERE USERID=?",new Object[]{taskUser});
		StringBuffer sbftaskuser = new StringBuffer();
		sbftaskuser.append(taskUser).append("<").append(taskUserName).append(">");
        System.err.println("lalala"+taskUser);
		String xqbmuserName = DBSql.getString("SELECT USERNAME FROM ORGUSER WHERE USERID=?",new Object[]{xqbmUser});
		StringBuffer sbfxqbmuser = new StringBuffer();
		sbfxqbmuser.append(xqbmUser).append("<").append(xqbmuserName).append(">");
		//wuxx
		String createuserName = DBSql.getString("SELECT USERNAME FROM ORGUSER WHERE USERID=?",new Object[]{createuser});
		StringBuffer createUser = new StringBuffer();
		createUser.append(createuser).append("<").append(createuserName).append(">");
        //查询任务责任人是否已离职
		String closed = DBSql.getString("SELECT CLOSED FROM ORGUSER WHERE userid = ?",
				new Object[] { taskUser });
		System.err.println("111333"+closed);
		res.put("yfjgname", yfjgname);
		res.put("taskname", taskname);
		if(!"1".equals(closed)){
			res.put("taskuser", sbftaskuser.toString().trim());
		}else{
			res.put("taskuser", "");
		}
		
		res.put("xqbmuser", sbfxqbmuser.toString().trim());
		return res.toString();

	}
	/**
	 * 查询一般委托流程的研发机构和任务名称
	 * @param processid
	 * @return
	 * @throws ParseException 
	 */	
	public String queryOperatelog_QueryYFJGQCR(String processId) {

		String yfjgid="";
		String taskname="";
		String taskUser="";
		String xqbmUser="";
		String createuser="";
		List<BO> list =SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", processId).list();
		if(list!=null&&list.size()>0)
		{
			for(BO bo : list){
				yfjgid = bo.getString("QTYFJGBMID");//研发机构id
				taskname = bo.getString("PHYFJGBM");//任务名称
				taskUser = bo.getString("PROJECTTYPE");//任务负责人
				xqbmUser = bo.getString("QTXQBM");
				createuser = bo.getString("CREATEUSERID");//任务发起人
			}
		}
		String yfjgname = DBSql.getString("SELECT DEPARTMENTNAME FROM ORGDEPARTMENT WHERE OUTERID=?",new Object[]{yfjgid});
		ResponseObject res = ResponseObject.newOkResponse();

		String taskUserName = DBSql.getString("SELECT USERNAME FROM ORGUSER WHERE USERID=?",new Object[]{taskUser});
		StringBuffer sbftaskuser = new StringBuffer();
		sbftaskuser.append(taskUser).append("<").append(taskUserName).append(">");
        System.err.println("lalala"+taskUser);
		String xqbmuserName = DBSql.getString("SELECT USERNAME FROM ORGUSER WHERE USERID=?",new Object[]{xqbmUser});
		StringBuffer sbfxqbmuser = new StringBuffer();
		sbfxqbmuser.append(xqbmUser).append("<").append(xqbmuserName).append(">");
		String createuserName = DBSql.getString("SELECT USERNAME FROM ORGUSER WHERE USERID=?",new Object[]{createuser});
		StringBuffer createUser = new StringBuffer();
		createUser.append(createuser).append("<").append(createuserName).append(">");
        //查询任务责任人是否已离职
		String closed = DBSql.getString("SELECT CLOSED FROM ORGUSER WHERE userid = ?",
				new Object[] { taskUser });
		res.put("yfjgname", yfjgname);
		res.put("taskname", taskname);
		if(!"1".equals(closed)){
			res.put("taskuser", sbftaskuser.toString().trim());
		}else{
			res.put("taskuser", "");
		}
		
		res.put("xqbmuser", createUser.toString().trim());
		return res.toString();

	}

	/**
	 * 查询一般项目发布信息
	 * @param processid
	 * @return
	 * @throws ParseException 
	 */	
	public String queryOperatelog_QueryProjectInfor(String processId) {
		String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "ChangeState");
		url = url + "?process_id=" + processId+"&complete=1";// 项目信息修改状态
		String str = UtilURL.get(url);
		JSONObject resultJson = JSONObject.fromObject(str);
		String data = resultJson.getString("data");
		JSONObject datajson = JSONObject.fromObject(data);
		int code = (Integer) datajson.get("code");
		String msg = datajson.getString("msg");
		ResponseObject res = ResponseObject.newOkResponse();
		res.put("code", code);
		res.put("msg", msg);
		return res.toString();	

	}
	/**
	 * 重大重点项目立项在办理下一步弹出窗口选项目管理人员,通过接受项目管理人员返回维护表对应的信息
	 * @param projectManagerList
	 * @return
	 * @throws ParseException 
	 * sunk 2017-05-26
	 */
	public String queryProjectManagerMaintenanceInfo(String projectManagerList) {
		Connection conn=null;
		StringBuffer projectManagersb=new StringBuffer();
		JSONArray ja = new JSONArray();
		try{
			conn=DBSql.open();
			Vector<Hashtable<String,Object>> projectManagerV = new Vector<Hashtable<String,Object>>();
			if(null==projectManagerList||projectManagerList.equals("")){
				projectManagerV = CommontUtil.query(conn, 
						"select * from BO_ACT_PM_MAINTENANCEINFO_S "
						+ "order by px asc");
			}else{
				String[] projectManagers=projectManagerList.split("\\|");
				if(projectManagers.length>0){
					for(int i=0;i<projectManagers.length;i++){
						String projectManager=projectManagers[i];
						if(projectManager!=null&&!projectManager.equals("")){
							projectManagersb.append("'"+projectManager+"',");
						}
					}
					projectManagerV = CommontUtil.query(conn, 
							"select * from BO_ACT_PM_MAINTENANCEINFO_S "
							+ "where glr in ("+projectManagersb.substring(0,projectManagersb.length()-1)+") "
							+ "order by px asc");
					
				}
			}
			for(int i=0;i<projectManagerV.size();i++){
				Hashtable<String,Object> ht=projectManagerV.get(i);
				String bs=ht.get("BS").toString();//负责部所
				String glr=ht.get("GLR").toString();//项目管理人
				String glrxm=ht.get("GLRXM").toString();//项目管理人姓名
				JSONObject jso=new JSONObject();
				jso.put("BS", bs);
				jso.put("GLR", glr);
				jso.put("GLRXM", glrxm);
				ja.put(jso);

			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBSql.close(conn);
		}
		ResponseObject rsobj = ResponseObject.newOkResponse();
		rsobj.put("ja", ja.toString());
		return rsobj.toString();
	}



	/**
	 * 在流程流转的时候给相关人发送通知
	 * @param processInstId 流程bindid
	 * @param taskInstId 任务ID
	 * @param reader 阅读人
	 * @return
	 * sunk 2017-06-01
	 */
	public String sendNotification(UserContext me,String processInstId,String taskInstId,String reader) {
		if(null==reader||reader.equals("")){
			return "";
		}
		TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskInstId);
		String taskTitle = taskInstance.getTitle();//任务标题
		SDK.getTaskAPI().createUserNotifyTaskInstance(processInstId, taskInstId, me.getUID(), reader, taskTitle);
		ResponseObject res = ResponseObject.newOkResponse();
		res.put("code", 0);
		res.put("msg", "通知发送成功");
		return res.toString();
	}
	
	
	/**
	 * 调用接口完成任务
	 * @param taskInstId
	 * @param ActionName
	 * @param Msg
	 * @return
	 * @throws ParseException 
	 */	
	public String queryOperatelog_CompleteTask(UserContext me,String taskInstId,String ActionName,String Msg) {
		//创建审批记录
		Boolean bol = SDK.getTaskAPI().commitComment(taskInstId, me.getUID(),ActionName, Msg, true);
		ResponseObject res = ResponseObject.newOkResponse();
		ResponseObject err = ResponseObject.newErrResponse();
		if(bol){
			//完成任务
			ProcessExecuteQuery peq = SDK.getTaskAPI().completeTask(taskInstId, me.getUID(), true);
			if(peq != null){
				return res.toString();
			}else{
				String processInstId = SDK.getTaskAPI().getInstanceById(taskInstId).getProcessInstId();
				//办理完成失败，删除审批记录
				List<TaskCommentModel> listComments = SDK.getProcessAPI().getCommentsById(processInstId);
				if(listComments != null && listComments.size()> 0){
					for(int i = listComments.size() - 1;i>=0;i--){
						String commentTaskId = listComments.get(i).getTaskInstId();//审批记录中任务ID
						if(commentTaskId.equals(commentTaskId)){
							String commentId = listComments.get(i).getId();//审批记录ID
							DBSql.update("DELETE FROM WFC_COMMENT WHERE ID = ?", new Object[]{commentId});
							break;
						}
					}
				}
				err.put("err", "流程办理失败");
				return err.toString();
			}
		}else {
			err.put("err", "流程办理失败");
			return err.toString();
		}
		
	}
	
	
	/**
	 * 根据任务实例ID，查询当前任务实例id是否有子任务
	 * @param taskInstId
	 * @return
	 * zhaoxs 2017-06-22
	 */
	public String querySubTask(String taskInstId){
		List<HistoryTaskInstance> htlist = SDK.getHistoryTaskQueryAPI().parentTaskInstId(taskInstId).list();
		ResponseObject rsobj = ResponseObject.newOkResponse();
		if(htlist!=null&&htlist.size()>0){
			rsobj.put("msg","有");
		}else{
			rsobj.put("msg","没有");	
		}
		return rsobj.toString();
	}
	
	/**
	 * 查询自立、国拨计划外项目审批意见
	 * @param processid
	 * @return
	 * @throws ParseException 
	 * zhaoxs 2017-07-18
	 */
	public String queryOperatelog_Unplaned(String processid) {
		List<TaskCommentModel> list_now = new ArrayList<TaskCommentModel>();//当前流程实例ID审批意见
		//当前流程审批意见
		list_now = SDK.getProcessAPI().getCommentsById(processid);
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> list_yfjgyj = new ArrayList<Map<String,String>>();//研发机构意见 （计划外项目）

		if(list_now != null && list_now.size() > 0){
			for(int i = 0;i < list_now.size();i++){
				String activityName = list_now.get(i).getActivityName();//节点名
				String msg = list_now.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_now.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				String taskid = list_now.get(i).getTaskInstId();
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
				String activityDefid = taskInstance.getActivityDefId();
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_now.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_now.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_now.get(i).getDepartmentName();					
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);

					if(UnplanedProjectConst.yfjg_noteid.contains(activityDefid)){
						list_yfjgyj.add(map);
					}
				}
			}

		}

		if(list_yfjgyj !=null && list_yfjgyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_yfjgyj, UnplanedProjectConst.yfjg_name));
		}
		
		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}

	/**
	 * 查询取消终止流程审批意见
	 * @param processid
	 * @return
	 * @throws ParseException 
	 * zhaoxs 2017-07-18
	 */
	public String queryOperatelog_Cancel(String processid) {
		List<TaskCommentModel> list_now = new ArrayList<TaskCommentModel>();//当前流程实例ID审批意见
		//当前流程审批意见
		list_now = SDK.getProcessAPI().getCommentsById(processid);
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> list_yfjgyj = new ArrayList<Map<String,String>>();//研发机构意见 （取消终止流程）

		if(list_now != null && list_now.size() > 0){
			for(int i = 0;i < list_now.size();i++){
				String activityName = list_now.get(i).getActivityName();//节点名
				String msg = list_now.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_now.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				String taskid = list_now.get(i).getTaskInstId();
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
				String activityDefid = taskInstance.getActivityDefId();
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_now.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_now.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_now.get(i).getDepartmentName();					
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);

					if(CancelConst.yfjg_noteid.contains(activityDefid)){
						list_yfjgyj.add(map);
					}
				}
			}

		}

		if(list_yfjgyj !=null && list_yfjgyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_yfjgyj, CancelConst.yfjg_name));
		}
		
		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}
	
	/**
	 * 查询项目变更流程审批意见
	 * @param processid
	 * @return
	 * @throws ParseException 
	 * zhaoxs 2017-07-25
	 */
	public String queryOperatelog_Change(String processid) {
		List<TaskCommentModel> list_now = new ArrayList<TaskCommentModel>();//当前流程实例ID审批意见
		//当前流程审批意见
		list_now = SDK.getProcessAPI().getCommentsById(processid);
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> list_yfjgyj = new ArrayList<Map<String,String>>();//研发机构意见 （取消终止流程）

		if(list_now != null && list_now.size() > 0){
			for(int i = 0;i < list_now.size();i++){
				String activityName = list_now.get(i).getActivityName();//节点名
				String msg = list_now.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_now.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				String taskid = list_now.get(i).getTaskInstId();
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
				String activityDefid = taskInstance.getActivityDefId();
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_now.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_now.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_now.get(i).getDepartmentName();
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);

					if(ItemChangeConst.list_noteid.contains(activityDefid)){
						list_yfjgyj.add(map);
					}
				}
			}

		}

		if(list_yfjgyj !=null && list_yfjgyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_yfjgyj, ItemChangeConst.list_yfjgName));
		}
		
		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}
	
	/**
	 * 查询合同计提流程审批意见
	 * @param processid
	 * @return
	 * @throws ParseException 
	 * zhaoxs 2017-07-25
	 */
	public String queryOperatelog_contract(String processid) {
		List<TaskCommentModel> list_now = new ArrayList<TaskCommentModel>();//当前流程实例ID审批意见
		//当前流程审批意见
		list_now = SDK.getProcessAPI().getCommentsById(processid);
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> list_yfjgyj = new ArrayList<Map<String,String>>();//研发机构意见 （取消终止流程）

		if(list_now != null && list_now.size() > 0){
			for(int i = 0;i < list_now.size();i++){
				String activityName = list_now.get(i).getActivityName();//节点名
				String msg = list_now.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_now.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				String taskid = list_now.get(i).getTaskInstId();
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
				String activityDefid = taskInstance.getActivityDefId();
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_now.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_now.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_now.get(i).getDepartmentName();
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);

					if(CongruenceConst.bmyj_noteid.contains(activityDefid)){
						list_yfjgyj.add(map);
					}
				}
			}

		}

		if(list_yfjgyj !=null && list_yfjgyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_yfjgyj, CongruenceConst.bmyj_name));
		}
		
		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}
	
	

	/**
	 * 查询预算调整流程审批意见
	 * @param processid
	 * @return
	 * @throws ParseException 
	 * zhaoxs 2017-08-24
	 */
	public String queryOperatelog_budget(String processid) {
		List<TaskCommentModel> list_now = new ArrayList<TaskCommentModel>();//当前流程实例ID审批意见
		//当前流程审批意见
		list_now = SDK.getProcessAPI().getCommentsById(processid);
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> list_ysbmyj = new ArrayList<Map<String,String>>();//预算部门意见 （预算调整流程）
		List<Map<String,String>> list_gkbmyj = new ArrayList<Map<String,String>>();//归口部门意见 （预算调整流程）
		List<Map<String,String>> list_cwbmyj = new ArrayList<Map<String,String>>();//财务部门意见 （预算调整流程）

		if(list_now != null && list_now.size() > 0){
			for(int i = 0;i < list_now.size();i++){
				String activityName = list_now.get(i).getActivityName();//节点名
				String msg = list_now.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_now.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				String taskid = list_now.get(i).getTaskInstId();
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
				String activityDefid = taskInstance.getActivityDefId();
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_now.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_now.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_now.get(i).getDepartmentName();
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);

					if(BudgetConst.ysbmyj_list_noteid.contains(activityDefid)){
						list_ysbmyj.add(map);
					}
					if(BudgetConst.gkbmyj_list_noteid.contains(activityDefid)){
						list_gkbmyj.add(map);
					}
					if(BudgetConst.cwbmyj_list_noteid.contains(activityDefid)){
						list_cwbmyj.add(map);
					}
				}
			}

		}

		if(list_ysbmyj !=null && list_ysbmyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_ysbmyj, BudgetConst.ysbmyj_name));
		}
		if(list_gkbmyj !=null && list_gkbmyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_gkbmyj, BudgetConst.gkbmyj_name));
		}
		if(list_cwbmyj !=null && list_cwbmyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_cwbmyj, BudgetConst.cwbmyj_name));
		}
		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}
	
	
	/**
	 * 查询虚拟收入确认流程审批意见
	 * @param processid
	 * @return
	 * @throws ParseException 
	 * zhaoxs 2017-09-07
	 */
	public String queryOperatelog_virtual(String processid) {
		List<TaskCommentModel> list_now = new ArrayList<TaskCommentModel>();//当前流程实例ID审批意见
		//当前流程审批意见
		list_now = SDK.getProcessAPI().getCommentsById(processid);
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> list_jsbyj = new ArrayList<Map<String,String>>();//技术部意见
		List<Map<String,String>> list_xqbmyj = new ArrayList<Map<String,String>>();//需求部门意见 
		List<Map<String,String>> list_yfjgyj = new ArrayList<Map<String,String>>();//研发机构意见 

		if(list_now != null && list_now.size() > 0){
			for(int i = 0;i < list_now.size();i++){
				String activityName = list_now.get(i).getActivityName();//节点名
				String msg = list_now.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_now.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				String taskid = list_now.get(i).getTaskInstId();
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
				String activityDefid = taskInstance.getActivityDefId();
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_now.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_now.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_now.get(i).getDepartmentName();
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);

					if(VirtualIncomeConst.list_yfjgNoteid.contains(activityDefid)){
						list_yfjgyj.add(map);
					}
					if(VirtualIncomeConst.list_xqbmyjNoteid.contains(activityDefid)){
						list_xqbmyj.add(map);
					}
					if(VirtualIncomeConst.list_jsbyjNoteid.contains(activityDefid)){
						list_jsbyj.add(map);
					}
					
					
				}
			}

		}

		if(list_yfjgyj !=null && list_yfjgyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_yfjgyj, VirtualIncomeConst.yfjgyjName));
		}
		if(list_xqbmyj !=null && list_xqbmyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_xqbmyj, VirtualIncomeConst.xqbmyjName));
		}
		if(list_jsbyj !=null && list_jsbyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_jsbyj, VirtualIncomeConst.jsbyjName));
		}
		
		
		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}
	/**
	 * 研究院立项审批意见
	 * @author niech
	 * @date 20170920
	 */
	public String queryOperatelog_yjylx(String processId) {
		List<TaskCommentModel> list_parent = new ArrayList<TaskCommentModel>();//父流程实例ID审批意见
		//当前流程审批意见
		list_parent = SDK.getProcessAPI().getCommentsById(processId);
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> list_msg = new ArrayList<Map<String,String>>();//审批意见
		if(list_parent != null && list_parent.size() > 0){
			for(int i = 0;i < list_parent.size();i++){
				String activityName = list_parent.get(i).getActivityName();//节点名
				String msg = list_parent.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_parent.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_parent.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_parent.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_parent.get(i).getDepartmentName();
					String taskid =  list_parent.get(i).getTaskInstId();
					TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);
					list_msg.add(map);
				}

			}
		}
		if(list_msg != null && list_msg.size() > 0){
			html.append(OptionUtil.optionMosaic(list_msg, "研究院审批意见"));
		}
		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}
	
	/**
	 * 查询合同url
	 * @param processId
	 * @return
	 * zhaoxs 20170922
	 */
	public String queryurl(String processId){
		String exec_url = DBSql.getString("SELECT EXEC_URL FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?", new Object[]{processId});
		if(UtilString.isEmpty(exec_url)){
			ResponseObject rsobj = ResponseObject.newErrResponse();
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse();
			rsobj.put("exec_url", exec_url);
			return rsobj.toString();
		}
	}
	
	
	
	/**
	 * 查询研究院内部结项流程审批意见
	 * @param processid
	 * @return
	 * @throws ParseException 
	 * pcj 2017-09-22
	 */
	public String queryOperatelog_yjyjx(String processId) {
		List<TaskCommentModel> list_now = new ArrayList<TaskCommentModel>();//当前流程实例ID审批意见
		//当前流程审批意见
		list_now = SDK.getProcessAPI().getCommentsById(processId);
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> list_yfjgyj = new ArrayList<Map<String,String>>();//研发机构意见 

		if(list_now != null && list_now.size() > 0){
			for(int i = 0;i < list_now.size();i++){
				String activityName = list_now.get(i).getActivityName();//节点名
				String msg = list_now.get(i).getMsg();//留言信息
				if(!UtilString.isEmpty(msg) && msg.contains("<br>")){
					msg = msg.split("<br>")[0];
				}else if(!UtilString.isEmpty(msg) && msg.contains("原因：")){
					String str = msg.split("原因：", 2)[1];
					if(str.equals("无")){
						msg= "";
					}else{
						msg=str;
					}
				}else if(!UtilString.isEmpty(msg) && msg.contains("color")&&!msg.contains("<br>")){
					msg = "";
				}
				String taskId = list_now.get(i).getTaskInstId();//任务ID
				String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();//任务控制状态
				String taskid = list_now.get(i).getTaskInstId();
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
				String activityDefid = taskInstance.getActivityDefId();
				if(!"cancel".equals(controlState)){
					Timestamp createTimestamp = list_now.get(i).getCreateDate();//留言时间
					String createData = sdf.format(createTimestamp);
					String createUserid = list_now.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					//String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();//留言人部门名称
					String deptname = list_now.get(i).getDepartmentName();
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);

					if(YjyJX_Const.yfjg_noteid.contains(activityDefid)){
						list_yfjgyj.add(map);
					}
				}
			}

		}

		if(list_yfjgyj !=null && list_yfjgyj.size()>0)
		{
			html.append(OptionUtil.optionMosaic(list_yfjgyj, YjyJX_Const.yfjg_name));
		}
		
		if(UtilString.isEmpty(html.toString())){
			ResponseObject rsobj = ResponseObject.newOkResponse("");
			return rsobj.toString();
		}else{
			ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
			return rsobj.toString();
		}
	}
	
	/**
	 * 查询维护专业公司
	 * @param processId
	 * @return
	 * zhaoxs 20171221
	 */
	public String query_Querypro(String processId){
		String comid = DBSql.getString("SELECT SUBRESULTXQBM FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?", new Object[]{processId});
		List<RowMap> list =	DBSql.getMaps("SELECT * FROM BO_ACT_PROC");
	   List<String> idList = new  ArrayList<String>();
	   if(list!=null&&list.size()>0){
		   for(int i=0;i<list.size();i++){
			   idList.add(list.get(i).getString("COMPANYID")); 
		   }
	   }
	   ResponseObject rsobj = ResponseObject.newOkResponse();
	   if(!UtilString.isEmpty(comid)&&!UtilString.isEmpty(idList)&&idList.contains(comid)){ 
			rsobj.put("isPro","是");	
	   }else {
		   rsobj.put("isPro","不是");
	   }
	   return rsobj.toString();
	}
	/**
	 * 获取当前流程所流程的所有节点的节点ID
	 * @param me
	 * @param processId
	 * @return
	 */
	public String getProcessAllStepId(UserContext me,String processId){
		
		System.err.println("=================");
		System.err.println("========processId:"+processId+"=========");
		//获取当前流程所有的状态为1的已办理任务ID
		String sql = "select id from WFH_TASK where PROCESSINSTID = ? and taskstate = 1";
		//任务ID集合
		List<RowMap> list = DBSql.getMaps(sql, new Object[]{processId});
		//节点ID
		String stepid = "";
		//遍历
		for(RowMap map : list){
			//任务ID
			String taskid = map.getString("id");
			stepid += SDK.getTaskAPI().getInstanceById(taskid).getActivityDefId() + ",";
			
		}
		System.err.println("=======stepid:"+stepid+"==========");
		ResponseObject rsobj = ResponseObject.newOkResponse();
		rsobj.put("msg", stepid);
		return rsobj.toString();
	}
	/**
	 * 立项、结项等流程，第一个节点判断是否为设计院/研究院、其他公司
	 * 路径分组维护表：BO_ACT_CMCC_PATHGROUP
	 * @author 	chenxf
	 * @date   	2018年4月12日 下午2:50:19
	 * @param 	processId
	 * @return
	 */
	public String queryPathGroup(UserContext me,
					String process_type,String processId){
		String deptId = "";
		ResponseObject rsobj = ResponseObject.newOkResponse();
		//只有里程碑调用接口获取承担部门ID
		if("hq-milestone".equals(process_type)){
			// 参数定义列表  
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("processType", process_type);
			params.put("processID", processId);
			System.err.println("=====流程类型："+process_type+"===========");
			System.err.println("=====流程实例ID："+processId+"===========");
			// 调用App 
			String sourceAppId = CmccConst.sourceAppId;
			// aslp服务地址 
			String aslp = CmccConst.getUndertakeDeptId;
			
			ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, params);
			//获取承担部门ID
			String undertakeDeptId = ro.getErrorCode();
			System.err.println("======承担部门ID："+undertakeDeptId+"=========");
			//承担部门ID
			deptId = undertakeDeptId;
		}else{
			//当前人部门ID
			deptId = me.getDepartmentModel().getId();
		}
		//返回值
		String isYjy = "否"; 
		if(deptId != null 
				&& !"".equals(deptId) 
				&& !deptId.equals("2")){
			//承担部门全路径
			String currentPathDeptId = DepartmentCache.getModel(deptId).getPathIdOfCache();
			System.err.println("======承担部门全路径ID："+currentPathDeptId+"=========");
			//查询出所有为研究院、设计院的公司ID--->A：研究院/设计院，B：其他公司
			String sql = "select GROUP_CONCAT(COMPANYID) pathDeptid "
							+ "from BO_ACT_CMCC_PATHGROUP where GROUPTYPE = 'A'";
			//所有公司ID集合的字符串
			String pathDeptid = DBSql.getString(sql);
			if(!UtilString.isEmpty(pathDeptid)) {
				//拆分成数组
				String[] deptids = pathDeptid.split(",");
				for(int i = 0;i<deptids.length;i++){
					String deptid = deptids[i];
					//判断当前人是否为设计院、研究院
					if(!UtilString.isEmpty(deptid) 
							&& currentPathDeptId.contains(deptid)){
						isYjy = "是";
						break;
					}
				}
			}
		}
		rsobj.put("isYjy", isYjy);
		return rsobj.toString();
	}
	/**
	 * 企标流程，第一个节点判断是否为研究院
	 * 路径分组维护表：BO_ACT_CMCC_PATHGROUP
	 * @author 	wuxx
	 * @date   	20190417
	 * @param 	processId
	 * @return
	 */
	public String queryYjyPath(UserContext me,
					String process_type,String processId){
		String deptId = "";
		ResponseObject rsobj = ResponseObject.newOkResponse();
		//只有里程碑调用接口获取承担部门ID
		if("hq-milestone".equals(process_type)){
			// 参数定义列表  
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("processType", process_type);
			params.put("processID", processId);
			System.err.println("=====流程类型："+process_type+"===========");
			System.err.println("=====流程实例ID："+processId+"===========");
			// 调用App 
			String sourceAppId = CmccConst.sourceAppId;
			// aslp服务地址 
			String aslp = CmccConst.getUndertakeDeptId;
			
			ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, params);
			//获取承担部门ID
			String undertakeDeptId = ro.getErrorCode();
			System.err.println("======承担部门ID："+undertakeDeptId+"=========");
			//承担部门ID
			deptId = undertakeDeptId;
		}else{
			//当前人部门ID
			deptId = me.getDepartmentModel().getId();
		}
		//返回值
		String isYjy = "否"; 
		if(deptId != null 
				&& !"".equals(deptId) 
				&& !deptId.equals("2")){
			//承担部门全路径
			String currentPathDeptId = DepartmentCache.getModel(deptId).getPathIdOfCache();
			System.err.println("======承担部门全路径ID："+currentPathDeptId+"=========");
			//查询出所有为研究院、设计院的公司ID--->A：研究院/设计院，B：其他公司
			String sql = "select GROUP_CONCAT(COMPANYID) pathDeptid "
							+ "from BO_ACT_CMCC_PATHGROUP where GROUPTYPE = 'A'";
			//所有公司ID集合的字符串
			String pathDeptid = DBSql.getString(sql);
			
			if(!UtilString.isEmpty(pathDeptid)) {
				//拆分成数组
				String[] deptids = pathDeptid.split(",");
				for(int i = 0;i<deptids.length;i++){
					String deptid = deptids[i];
					
					//判断当前人是否是研究院
					/*if(!UtilString.isEmpty(deptid) 
							&& deptid.contains("5b93a3f7-3ae2-4831-9fcc-d8809ffc462c")){
						isYjy = "是";
						break;
					}*/
					if(!UtilString.isEmpty(deptid) 
							&& currentPathDeptId.contains("5b93a3f7-3ae2-4831-9fcc-d8809ffc462c")){
						isYjy = "是";
						break;
					}
				}
			}
		}
		rsobj.put("isYjy", isYjy);
		return rsobj.toString();
	}
	/**
	 * 同一部门接口人不能选两个或两个以上的人员
	 * @author chenxf
	 * @date   2018年6月7日 下午4:55:19
	 * @param me
	 * @param participants
	 * @return
	 */
	public String demandDepartmentSign(UserContext me, 
							String participants, String params){
		System.err.println("===成功调用===");
		System.err.println("===participants："+participants+"===");
		ResponseObject rsobj = ResponseObject.newOkResponse();
		//拆分办理人
		String[] peoples = participants.split(" ");
		//人员的数量
		int length = peoples.length;
		/*
		 * 只有选择两个以上人员才判断
		 */
		if(length >= 2){
			if(UtilEntryEventClass.getMapperDeptIdByUserid(peoples,length,params)){
				rsobj.put("isxqbm", "true");
			}else{
				rsobj.put("isxqbm", "false");
			}
		}else{
			rsobj.put("isxqbm", "true");
		}
		return rsobj.toString();
	}
}
	
	
	

