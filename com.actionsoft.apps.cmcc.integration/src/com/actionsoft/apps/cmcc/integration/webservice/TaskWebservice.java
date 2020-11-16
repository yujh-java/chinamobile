package com.actionsoft.apps.cmcc.integration.webservice;
import java.sql.Connection;
/**
 * 新建任务、任务查询、流程查询接口
 * @author nch
 * @date 20170622
 */
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jws.WebParam;
import javax.jws.WebService;
import com.actionsoft.apps.cmcc.integration.util.PubUtil;
import com.actionsoft.apps.cmcc.integration.util.PubUtilSystem;
import com.actionsoft.apps.cmcc.integration.util.SnapshotForm;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.model.def.ActivityModel;
import com.actionsoft.bpms.bpmn.engine.model.def.ProcessDefinition;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.HandlerType;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.actionsoft.bpms.server.bind.annotation.Param;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONArray;

import net.sf.json.JSONObject;

@Controller(type = HandlerType.NORMAL, apiName = "TaskWebservice Api", desc = "流程任务扩展接口")
@WebService(serviceName = "TaskWebserviceApi")
public class TaskWebservice {
	/**
	 * 创建流程任务
	 * @param processtype
	 * @param submiter
	 * @param processTitle
	 * @return
	 */
	@Mapping(value = "TaskWebserviceApi.startProcess")
	public String  startProcess(@Param(value = "processType", desc = "流程类型", required = true) @WebParam(name = "processType") String processType,
			@Param(value = "submiter", desc = "流程创建者", required = true) @WebParam(name = "submiter") String submiter,
			@Param(value = "taskTitle", desc = "流程标题", required = true) @WebParam(name = "taskTitle") String taskTitle,
			@Param(value = "project_id", desc = "立项项目ID", required = false) @WebParam(name = "project_id") String project_id,
	        @Param(value = "exec_url", desc = "合同URL", required = false) @WebParam(name = "exec_url") String exec_url){

		long bTime=System.currentTimeMillis();
		if(submiter.indexOf("@")==-1){
			submiter=submiter+"@hq.cmcc";
		}
		JSONObject json = new JSONObject();
		if(!UtilString.isEmpty(submiter) && UserCache.getModel(submiter)!=null
				&& UtilString.isEmpty(SDK.getORGAPI().validateUsers(submiter))){
			Connection conn=null;
			try{
				conn=DBSql.open();
				String sql = "SELECT PROCESSDEFNID FROM BO_ACT_PROCESS_DATA WHERE PROCESSTYPE = ? AND ISMAIN = ?";
//				String processDefid  = DBSql.getString(conn, sql, new Object[]{processType,"是"});
//				if(UtilString.isEmpty(processDefid)){
				List<RowMap> list = DBSql.getMaps(conn, sql, new Object[]{processType,"是"});
				if(list.size() <= 0){
					json.put("isSuccess", false);
					json.put("msg", "流程类型不存在");
					json.put("data", null);
				}else{
					/*
					 * chenxf modify
					 */
					//------------------------
					//最大版本号
					int versionNo_z = 0;
					//最大版本号流程定义ID
					String processDefid_z = list.get(0).getString("PROCESSDEFNID");
					//遍历流程定义ID
					for(RowMap map : list){
						String processDefid = (String) map.get("PROCESSDEFNID");
						int versionNo = SDK.getRepositoryAPI().getProcessDefinition(processDefid).getVersionNo();
						//判断当前遍历版本号是否大于上一个版本号，如果大于的话，则取最大版本号流程定义ID
						if(versionNo_z < versionNo){
							processDefid_z = processDefid;
							versionNo_z = versionNo;
						}
					}
					//------------------------------------
					ProcessInstance process = SDK.getProcessAPI().createProcessInstance(processDefid_z, submiter,taskTitle);
					SDK.getProcessAPI().start(process);
					String processid = process.getId();
					Timestamp createtime = process.getCreateTime();
					String createtimeStr = PubUtil.sdf.format(createtime);
					String taskid = DBSql.getString(conn,"select ID from WFC_TASK where PROCESSINSTID = ? ", new Object[]{processid});

					//保存流程信息
					BO bo = new BO();
					bo.set("PROCESSTYPE", processType);
					bo.set("TITLE", taskTitle);
					bo.set("PROCESSID", processid);
					bo.set("CREATEUSERID", submiter);
					if("cmri-contract_exec".equals(processType)){
						bo.set("EXEC_URL", exec_url);
					}
					if("hq-open".equals(processType) || "hq-end".equals(processType)
							||"cmri-stop".equals(processType)||"cmri-out2in".equals(processType)
							||"cmri-change".equals(processType) || "cmri-open".equals(processType)
							|| "cmri-end".equals(processType)){//流程类型：立项;结项,取消终止，计划外，项目变更
						bo.set("PROJECTID", project_id);
					}
					SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSDATA", bo, UserContext.fromUID(submiter),conn);

					JSONObject jsonValue = new JSONObject();
					jsonValue.put("process_id", processid);
					jsonValue.put("submittime", createtimeStr);
					jsonValue.put("task_id", taskid);
					jsonValue.put("providertype", "AWS");

					json.put("isSuccess", true);
					json.put("msg", "ok");
					json.put("data", jsonValue);
				}
			}finally{
				DBSql.close(conn);
			}
		}else{
			json.put("isSuccess", false);
			json.put("msg", "用户不存在或已注销!");
			json.put("data", null);
		}
		long eTime=System.currentTimeMillis();
		long excTime=eTime-bTime;
		System.err.println(">>>>>>方法名:startProcess|开始时间："+UtilDate.timeFormat(new Date(bTime))+"|结束时间："+UtilDate.timeFormat(new Date(eTime))+"|执行时间："+excTime+"ms|");
		return json.toString();
	}	
	/**
	 * 任务查询
	 * @param userid
	 * @param openState
	 * @return
	 */
	@Mapping(value = "TaskWebserviceApi.queryTask")
	public String queryTask(@Param(value = "userid", desc = "用户账号", required = false) @WebParam(name = "userid") String userid,
			@Param(value = "startTime", desc = "开始时间", required = false) @WebParam(name = "startTime") String startTime,
			@Param(value = "endTime", desc = "结束时间", required = false) @WebParam(name = "endTime") String endTime,
			@Param(value = "taskStates", desc = "任务状态", required = true) @WebParam(name = "taskStates") int taskStates,
			@Param(value = "processType", desc = "流程类型", required = false) @WebParam(name = "processType") String processType,
			@Param(value = "taskTitle", desc = "任务标题", required = false) @WebParam(name = "taskTitle") String taskTitle,
			@Param(value = "curPage", desc = "当前页码", required = false) @WebParam(name = "curPage") int curPage,
			@Param(value = "pageSize", desc = "分页大小", required = false) @WebParam(name = "pageSize") int pageSize,
			@Param(value = "processApp", desc = "应用分类", required = false) @WebParam(name = "processApp") String processApp,
			@Param(value = "IOSID", desc = "系统ID", required = false) @WebParam(name = "IOSID") String IOSID,
			@Param(value = "system", desc = "系统", required = false) @WebParam(name = "system") String system,
			@Param(value = "processStates", desc = "流程进行状态", required = false) @WebParam(name = "processStates") String processStates){
		long bTime=System.currentTimeMillis();
		if(!UtilString.isEmpty(userid) && userid.indexOf("@")==-1){
			userid=userid+"@hq.cmcc";
		}
		if(!UtilString.isEmpty(userid) && 
				(UserCache.getModel(userid)==null || !UtilString.isEmpty(SDK.getORGAPI().validateUsers(userid)))
				){
			JSONObject json = new JSONObject();
			json.put("isSuccess", false);
			json.put("msg", "用户不存在或已注销!");
			json.put("size", 0);
			json.put("data", "");
			return json.toString();
		}else{
			Connection conn=null;
			String result = "";
			try{
				conn=DBSql.open();
				int start_sizte = -1;//开始条数
				int end_size = -1;//结束条数
				if(!UtilString.isEmpty(curPage) && !UtilString.isEmpty(pageSize)){
					start_sizte = (curPage - 1)*pageSize;
					end_size = pageSize*curPage - 1;
				}
				if(UtilString.isEmpty(system)){
					if(taskStates==1){//查询待办任务
						result = PubUtil.getTaskOfWorking(conn,userid,startTime,endTime,processType,taskTitle,start_sizte,end_size,processApp,IOSID);
					}else if(taskStates==2){//已办任务查询
						result = PubUtil.getTaskComplete(conn,userid,startTime,endTime,processType,taskTitle,start_sizte,end_size,processApp,IOSID,"2");
					}else if(taskStates == 9){//通知类待阅任务查询
						result = PubUtil.getTaskOfNotification(conn,userid,startTime,endTime,processType,taskTitle,start_sizte,end_size,processApp,IOSID);
					}else if(taskStates == 10){//通知类已阅任务查询
						result = PubUtil.getTaskNotification(conn,userid,startTime,endTime,processType,taskTitle,start_sizte,end_size,processApp,IOSID,"10");
					}
				}else{
					if(taskStates==1){//查询待办任务
						result = PubUtilSystem.getTaskOfWorking(conn,userid,startTime,endTime,processType,taskTitle,start_sizte,end_size,processApp,IOSID,system);
					}else if(taskStates==2){//已办任务查询
						if(!UtilString.isEmpty(processStates)){
							result = PubUtilSystem.getTaskComplete(conn,userid,startTime,endTime,processType,taskTitle,start_sizte,end_size,processApp,IOSID,system,processStates,"2");
						}
					}else if(taskStates == 9){//通知类待阅任务查询
						result = PubUtilSystem.getTaskOfNotification(conn,userid,startTime,endTime,processType,taskTitle,start_sizte,end_size,processApp,IOSID,system);
					}else if(taskStates == 10){//通知类已阅任务查询
						result = PubUtilSystem.getTaskNotification(conn,userid,startTime,endTime,processType,taskTitle,start_sizte,end_size,processApp,IOSID,system,processStates,"10");
					}
				}
				
			}finally{
				DBSql.close(conn);
			}
			long eTime=System.currentTimeMillis();
			long excTime=eTime-bTime;
			String taskType = "待办";
			if(taskStates==1){
				taskType = "待办";
			}else if(taskStates==2){
				taskType = "已办";
			}else if(taskStates==9){
				taskType = "未阅通知";
			}else if(taskStates==10){
				taskType = "已阅通知";
			}
			System.err.println(">>>>>>方法名:queryTask_"+taskType+"|执行人:"+userid+"|开始时间："+UtilDate.timeFormat(new Date(bTime))+"|结束时间："+UtilDate.timeFormat(new Date(eTime))+"|执行时间："+excTime+"ms|系统ID:"+IOSID);
			return result;
		}
	}
	/**
	 *	流程实例查询
	 * @param userid
	 * @param processId
	 * @return
	 */
	@Mapping(value = "TaskWebserviceApi.queryProcess")
	public String queryProcess(@Param(value = "userid", desc = "用户账号", required = true) @WebParam(name = "userid") String userid,
			@Param(value = "process_id", desc = "流程实例ID", required = true) @WebParam(name = "process_id") String process_id,
			@Param(value = "task_id", desc = "任务ID", required = false) @WebParam(name = "task_id") String task_id){
		long bTime=System.currentTimeMillis();
		if(userid.indexOf("@")==-1){
			userid=userid+"@hq.cmcc";
		}
		int status = 0;//主流程结束状态：1：结束，0：未结束
		JSONObject json = new JSONObject();
		if(!UtilString.isEmpty(userid) && UserCache.getModel(userid)!=null
				&& UtilString.isEmpty(SDK.getORGAPI().validateUsers(userid))){
			Connection conn=null;
			String result = "";
			try{
				conn=DBSql.open();    
				if(!UtilString.isEmpty(task_id)){
					//判断当前任务是否是待办任务
					List<TaskInstance> is_list_task = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn).target(userid).addQuery("ID = ", task_id).list();
					if(is_list_task != null && is_list_task.size() > 0){
						List<TaskInstance> list2 = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn).parentTaskInstId(task_id).list();
						if(list2 == null || list2.size() == 0){
							json.put("isSuccess", true);
							json.put("msg", "ok");
							json.put("task_id", task_id);
							json.put("process_id", process_id);
							json.put("version_id", "");
							json.put("status", status);
						}
					}else{
						json.put("isSuccess", true);
						json.put("msg", "error");
						json.put("task_id", task_id);
						json.put("process_id", process_id);
						json.put("version_id", "");
						json.put("status", status);
					}
				}else{
					/**
					 * 查询任务ID前，判断流程是否结束。
					 * 流程结束：使用表单快照
					 * 			判断userid是否属于研究院：
					 * 				属于研究院：判断流程类型是否属于DiffFormSnapshot
					 * 						属于DiffFormSnapshot：查询研究院人生成的表单快照版本ID
					 * 						不属于DiffFormSnapshot：返回表单快照版本ID
					 * 				不属于研究院：返回表单快照版本ID
					 * 流程未结束：继续查询任务ID
					 * version_id
					 */
					ProcessInstance processInstance = SDK.getProcessAPI().getInstanceById(process_id);
					//判断流程是否结束
					boolean is_end = SnapshotForm.processIsEnd(process_id);
					if(is_end){//结束
						status = 1;
						String version_id = "";
						//判断userid是否属于研究院
						/*boolean is_yjy = SnapshotForm.userIsYjy(userid);
						if(is_yjy){
							//判断流程类型是否属于DiffFormSnapshot
							String processDefid = processInstance.getProcessDefId();
							if(CMCCConst.DiffFormSnapshot.toString().contains(processDefid)){
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
						json.put("task_id", "");
						json.put("isSuccess", true);
						json.put("msg", "error");
						json.put("version_id", version_id);
						json.put("process_id", process_id);
						json.put("status", status);
					}else{
						List<String> listPorcessId = new ArrayList<String>();
						listPorcessId.add(process_id);
						//获取流程子流程
						Map<String,Object> mapSubProcess = new HashMap<String,Object>();
						mapSubProcess.put("PARENTPROCESSINSTID", process_id);
						List<RowMap> list_subprocessId = DBSql.getMaps(conn,"SELECT ID FROM WFC_PROCESS WHERE PARENTPROCESSINSTID= ?", new Object[]{process_id});
						if(list_subprocessId != null && list_subprocessId.size() > 0){
							//存在子流程
							for(int i = 0;i < list_subprocessId.size(); i++){
								String subProcessId = list_subprocessId.get(i).getString("ID");
								listPorcessId.add(subProcessId);
							}
						}
						String taskId = "";
						//当前人待办任务
						for(int j = 0;j<listPorcessId.size();j++){
							String PROCESSINSTID = listPorcessId.get(j);
							List<TaskInstance> list_task = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn).processInstId(PROCESSINSTID).target(userid).list();
							if(list_task != null && list_task.size() > 0){
								for(int i = 0;i < list_task.size();i++){
									taskId = list_task.get(i).getId();
									List<TaskInstance> list2 = SDK.getTaskQueryAPI().userTaskOfWorking().connection(conn).parentTaskInstId(taskId).list();
									if(list2 == null || list2.size() == 0){
										json.put("isSuccess", true);
										json.put("msg", "ok");
										json.put("task_id", taskId);
										json.put("process_id", PROCESSINSTID);
										json.put("version_id", "");
										json.put("status", status);
										break;
									}
								}
							}
						}
						if(json.size() == 0){
							List<TaskInstance> list_task = SDK.getTaskQueryAPI().processInstId(process_id).connection(conn).list();
							if(list_task != null && list_task.size() > 0){
								taskId = list_task.get(0).getParentTaskInstId();
								if(!UtilString.isEmpty(taskId) && !"00000000-0000-0000-0000-000000000000".equals(taskId)){//不为流程第一个节点
									String createProcessTaskCallActivity =  SDK.getTaskAPI().getInstanceById(taskId).getActivityType();
									if("callActivity".equals(createProcessTaskCallActivity)){//启动子流程节点
										taskId = SDK.getTaskAPI().getInstanceById(taskId).getParentTaskInstId();
									}
								}else{
									taskId = list_task.get(0).getId();
								}
							}else{
								List<HistoryTaskInstance> list_hisTask = SDK.getHistoryTaskQueryAPI().connection(conn).processInstId(process_id).list();
								if(list_hisTask != null && list_hisTask.size() > 0){
									taskId = list_hisTask.get(0).getId();
								}
							}
							json.put("task_id", taskId);
							json.put("isSuccess", true);
							json.put("msg", "error");
							json.put("version_id", "");
							json.put("process_id", process_id);
							json.put("status", status);
						}
					}
				}
			}finally{
				DBSql.close(conn);
			}
			result = json.toString();
			long eTime=System.currentTimeMillis();
			long excTime=eTime-bTime;
			System.err.println(">>>>>>方法名:queryProcess|执行人:"+userid+"|开始时间："+UtilDate.timeFormat(new Date(bTime))+"|结束时间："+UtilDate.timeFormat(new Date(eTime))+"|执行时间："+excTime+"ms|");
			return result;
		}else{
			json.put("isSuccess", false);
			json.put("msg", "用户不存在或已注销!");
			json.put("task_id", "");
			json.put("process_id", "");
			json.put("version_id", "");
			json.put("status", status);
			return json.toString();
		}
	}
	/**
	 * 流程查询
	 * @author chenxf
	 * @date   2018年4月25日 下午2:29:57
	 * @param processApp
	 * @param processType
	 * @param processState
	 * @param beginDateFrom
	 * @param beginDateTo
	 * @param endDateFrom
	 * @param endDateTo
	 * @param createUser
	 * @param target
	 * @param owner
	 * @param createUserDeptId
	 * @param curPage
	 * @param pageSize
	 * @return
	 */
	@Mapping(value = "TaskWebserviceApi.queryProcessFormReport")
	public String queryProcessFormReport(
			@Param(value = "processApp", desc = "应用类型", required = false) @WebParam(name = "processApp") String processApp,
			@Param(value = "processType", desc = "流程类型", required = false) @WebParam(name = "processType") String processType,
			@Param(value = "processState", desc = "流程状态", required = false) @WebParam(name = "processState") String processState,
			@Param(value = "beginDateFrom", desc = "发起日期从", required = false) @WebParam(name = "beginDateFrom") String beginDateFrom,
			@Param(value = "beginDateTo", desc = "发起日期到", required = false) @WebParam(name = "beginDateTo") String beginDateTo,
			@Param(value = "endDateFrom", desc = "结束日期从", required = false) @WebParam(name = "endDateFrom") String endDateFrom,
			@Param(value = "endDateTo", desc = "结束日期到", required = false) @WebParam(name = "endDateTo") String endDateTo,
			@Param(value = "createUser", desc = "流程发起人", required = false) @WebParam(name = "createUser") String createUser,
			@Param(value = "target", desc = "流程代办人", required = false) @WebParam(name = "target") String target,
			@Param(value = "owner", desc = "流程经办人", required = false) @WebParam(name = "owner") String owner,
			@Param(value = "createUserDeptId", desc = "流程发起人部所", required = false) @WebParam(name = "createUserDeptId") String createUserDeptId,
			@Param(value = "curPage", desc = "当前页码", required = false) @WebParam(name = "curPage") String curPage,
			@Param(value = "pageSize", desc = "分页大小", required = false) @WebParam(name = "pageSize") String pageSize,
			@Param(value = "system", desc = "流程类型模糊查询", required = false) @WebParam(name = "system") String system){
		
		//最终结果
		long bTime=System.currentTimeMillis();
		JSONObject jsonResult = new JSONObject();
		try {
			//过滤后的总数量
			StringBuffer sb_size = new StringBuffer();
			sb_size.append("select count(*) c from VIEW_ACT_PROCESSVIEW PROCESS ");
			sb_size.append("where 1=1 ");
			
			
			StringBuffer sb = new StringBuffer();
			sb.append("select PROCESS.* from ");
			//先排序
			sb.append("(select * from VIEW_ACT_PROCESSVIEW ORDER BY CREATETIME desc) PROCESS ");
			sb.append("where 1=1 ");
			//应用类型
			if(!UtilString.isEmpty(processApp)){
				System.err.println("=======processApp："+processApp+"==========");
				
				sb.append("and PROCESS.PROCESSAPP = '"+processApp+"' ");
				sb_size.append("and PROCESS.PROCESSAPP = '"+processApp+"' ");
			}
			//流程类型
			if(!UtilString.isEmpty(processType)){
				String[] processTypeList = processType.trim().split(" ");
				if(processTypeList.length>=1){
					sb.append(" AND (");
					sb_size.append(" AND (");
					for(int i=0;i<processTypeList.length;i++){
						if(i==0){
							sb.append(" PROCESS.PROCESSTYPE = '"+processTypeList[i]+"' ");
							sb_size.append(" PROCESS.PROCESSTYPE = '"+processTypeList[i]+"' ");
						}else{
							sb.append("OR PROCESS.PROCESSTYPE = '"+processTypeList[i]+"' ");
							sb_size.append("OR PROCESS.PROCESSTYPE = '"+processTypeList[i]+"' ");
						}
					}
					sb.append(" )");
					sb_size.append(" )");
				}else{
					sb.append("and PROCESS.PROCESSTYPE = '"+processType+"' ");
					sb_size.append("and PROCESS.PROCESSTYPE = '"+processType+"' ");
				}
			}
			//流程状态
			if(!UtilString.isEmpty(processState)){
				sb.append("and PROCESS.STATE = '"+processState+"' ");
				sb_size.append("and PROCESS.STATE = '"+processState+"' ");
			}
			//流程创建人账号
			if(!UtilString.isEmpty(createUser)){
				if(createUser.indexOf("@") == -1){
					createUser += "@hq.cmcc";
				}
				sb.append("and PROCESS.CREATEUSER = '"+createUser+"' ");
				sb_size.append("and PROCESS.CREATEUSER = '"+createUser+"' ");
			}
			//流程代办人
			if(!UtilString.isEmpty(target)){
				if(target.indexOf("@") == -1){
					target += "@hq.cmcc";
				}
				sb.append("and PROCESS.TARGET like '%"+target+"%' ");
				sb_size.append("and PROCESS.TARGET like '%"+target+"%' ");
			}
			//当前办理人部所ID
			if(!UtilString.isEmpty(createUserDeptId)){
				String sql = "select id from ORGDEPARTMENT where outerid = '"+createUserDeptId+"'";
				//获取对应部门ID
				String deptid = DBSql.getString(sql);
				sb.append("and PROCESS.TARGETDEPTID = '"+deptid+"' ");
				sb_size.append("and PROCESS.TARGETDEPTID = '"+deptid+"' ");
			}
			/*
			 * 创建日期、结束日期过滤
			 */
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//发起日期从
			if(!UtilString.isEmpty(beginDateFrom)){
//				Date date1 = sdf.parse(beginDateFrom);
				sb.append("and PROCESS.CREATETIME >= '"+beginDateFrom+"' ");
				sb_size.append("and PROCESS.CREATETIME >= '"+beginDateFrom+"' ");
			}
			//发起日期从
			if(!UtilString.isEmpty(beginDateTo)){
//				Date date2 = sdf.parse(beginDateTo);
				sb.append("and PROCESS.CREATETIME <= '"+beginDateTo+"' ");
				sb_size.append("and PROCESS.CREATETIME <= '"+beginDateTo+"' ");
			}
			//发起日期从
			if(!UtilString.isEmpty(endDateFrom)){
//				Date date3 = sdf.parse(endDateFrom);
				sb.append("and PROCESS.ENDTIME >= '"+endDateFrom+"' ");
				sb_size.append("and PROCESS.ENDTIME >= '"+endDateFrom+"' ");
			}
			//发起日期从
			if(!UtilString.isEmpty(endDateTo)){
//				Date date4 = sdf.parse(endDateTo);
				sb.append("and PROCESS.ENDTIME <= '"+endDateTo+"' ");
				sb_size.append("and PROCESS.ENDTIME <= '"+endDateTo+"' ");
			}
			/*
			 * 通过system过滤条件模糊查询流程类型
			 */
			if(!UtilString.isEmpty(system)){
				sb.append("and PROCESS.PROCESSTYPE like '%"+system+"%' ");
				sb_size.append("and PROCESS.PROCESSTYPE like '%"+system+"%' ");
			}
			/*
			 * 过滤经办人
			 */
			if(!UtilString.isEmpty(owner)){
				sb.append("and PROCESS.PENSON like '%"+owner+"%' ");
				sb_size.append("and PROCESS.PENSON like '%"+owner+"%' ");
			}
			
			
			/*
			 * 分页查询
			 */
			int start_size = -1;//开始条数
//			int end_size = -1;//结束条数
			int curPage_bak  = -1;//当前页码
			int pageSize_bak = -1;//数量
			if(!UtilString.isEmpty(curPage) && !UtilString.isEmpty(pageSize)){
				curPage_bak = Integer.parseInt(curPage);
				pageSize_bak = Integer.parseInt(pageSize);
			}
			if(curPage_bak > 0 && pageSize_bak > 0){
				start_size = (curPage_bak - 1) * pageSize_bak;
//				end_size = pageSize_bak * curPage_bak - 1;
			}
			if(start_size >= 0){
				sb.append("LIMIT "+start_size+","+pageSize_bak+" ");
			}
			System.err.println("====流程查询sql:"+sb.toString()+"=======");
			System.err.println("====流程查询总数sql:"+sb_size.toString()+"=======");
			
			//返回的流程数据
			JSONArray ja = new JSONArray();
			//查询出过滤后的所有流程数据
			List<RowMap> list = DBSql.getMaps(sb.toString());
			
			//过滤后的总数量
			String size = DBSql.getString(sb_size.toString());
			//格式化日期
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			System.err.println("======流程查询数据量："+list.size()+"=======");
			
			if(list != null && list.size() > 0){
				//遍历流程数据
				for(int i = 0;i < list.size();i++){
					JSONObject jo = new JSONObject();
					RowMap map = list.get(i);
					//chenxf add 20181120
					//任务ID
//					if(map.get("TASKID") != null){
//						String taskid = map.get("TASKID").toString();
//						if(!UtilString.isEmpty(taskid)){
//							//获取任务的集合
//							List<TaskInstance> list_task = SDK.getTaskQueryAPI().addQuery("ID = ", taskid).list();
//							if(list_task != null && list_task.size() > 0){
//								//获取任务状态
//								int taskState = list_task.get(0).getState();
//								if(taskState == 11){
//									String commentID = SDK.getTaskAPI().getCommentOfTemp(taskid).getId();
//									if(commentID != null){
//										continue;
//									}
//								}
//							}
//							
//						}
//					}
//					
					String processTitle = map.get("PROCESSTITLE").toString();
					//chenxf add 2018-06-05
					//过滤《重新激活》的标题字
					if(processTitle != null 
							&& !"".equals(processTitle) 
							&& processTitle.indexOf("(重新激活)") != -1){
						
						processTitle = processTitle.replaceAll("重新激活", "");
						processTitle = processTitle.replaceAll("\\(|\\)", "");
					}
					//流程实例ID
					jo.put("processInstId", map.get("PROCESSINSTID"));
					//流程类型
					jo.put("processtype", map.get("PROCESSNAME"));
					//流程名称
					jo.put("processname", processTitle);
					//流程定义ID
					String processDefid = (String) map.get("PROCESSDEFID");
					//流程节点ID
					String activityId = (String) map.get("ACTIVITYDEFID");
					//环节名称
					RowMap taskInfo = DBSql.getMap("SELECT ACTIVITYDEFID,TASKSTATE FROM WFC_TASK WHERE PROCESSINSTID=?", new Object[]{map.get("PROCESSINSTID").toString()});
					if(map.get("PROCESSINSTID").toString().equals("0407c4f9-9c08-499c-9bd6-18555aa502d3")){
						System.err.println(">>>>taskInfo:"+taskInfo.size());
						System.err.println(">>>>TASKSTATE:"+taskInfo.getInt("TASKSTATE"));
						System.err.println(">>>>ACTIVITYDEFID:"+taskInfo.getString("ACTIVITYDEFID"));
					}
					if(null ==taskInfo || taskInfo.size() ==0 ){
						jo.put("activityname", PubUtil.getNoteIdName(processDefid, activityId));
					}else{
						if(taskInfo.getInt("TASKSTATE")==0){//任务为父流程调用任务
							ProcessDefinition processDefinition = SDK.getRepositoryAPI().getProcessDefinition(processDefid);
							ActivityModel activityModel = processDefinition.getTasks().get(taskInfo.getString("ACTIVITYDEFID"));
							jo.put("activityname", activityModel.getName());
						}else{
							jo.put("activityname", PubUtil.getNoteIdName(processDefid, activityId));
						}
						
					}
					//jo.put("activityname", PubUtil.getNoteIdName(processDefid, activityId));
					//发起人
					jo.put("processuser", map.get("PROCESSUSER"));
					//代办人
					jo.put("target", map.get("TARGET"));
					//创建日期
					jo.put("createtime", map.get("CREATETIME") == null ? "" :sdf2.format(map.get("CREATETIME")));
					//结束日期
					jo.put("endtime", map.get("ENDTIME") == null ? "" : sdf2.format(map.get("ENDTIME")));
					//放入json数组中
					ja.add(jo);
				}
				jsonResult.put("isSuccess", true);
				jsonResult.put("msg", "ok");
				jsonResult.put("size", size);
				jsonResult.put("data", ja.toString());
			}else{
				jsonResult.put("isSuccess", false);
				jsonResult.put("msg", "ok");
				jsonResult.put("size", 0);
				jsonResult.put("data", null);
			}
			
		} catch (Exception e) {
			jsonResult.put("isSuccess", false);
			jsonResult.put("msg", "error");
			jsonResult.put("size", 0);
			jsonResult.put("data", null);
			e.printStackTrace(System.err);
		}
		long eTime=System.currentTimeMillis();
		long excTime=eTime-bTime;
		System.err.println(">>>>>>方法名:queryProcessFormReport|开始时间："+UtilDate.timeFormat(new Date(bTime))+"|结束时间："+UtilDate.timeFormat(new Date(eTime))+"|执行时间："+excTime+"ms|");
		return jsonResult.toString();
	}
}
