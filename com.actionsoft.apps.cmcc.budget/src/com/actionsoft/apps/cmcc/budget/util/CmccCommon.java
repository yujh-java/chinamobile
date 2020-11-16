package com.actionsoft.apps.cmcc.budget.util;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.cache.util.UserTaskDefUtil;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class CmccCommon{
	
	  public static String user_leave1 = "1";
	  public static String user_leave2 = "2";
	  public static String user_leave3 = "3";
	  public static String user_leave4 = "4";
	  public static String user_leave5 = "5";
	  public static String user_leave6 = "6";
	  //集团下研究院部门全路径ID
	  public static String pathDeptId_yjy = "f1c01f98-9184-4e8d-8908-ae5614328586/5b93a3f7-3ae2-4831-9fcc-d8809ffc462c";
	  public static String deptid_yjy = "5b93a3f7-3ae2-4831-9fcc-d8809ffc462c";//研究院路径
	  //技术部
	  public static String deptid_jsb = "1f5c54ba-1214-478f-b911-8e7018d3b74b";
	  public static final int ipm_connectTime = 10000;
	  //需求部门角色
	  public static String xqbm_role = "需求部门接口人";
	 //预算接口人
	  public static String qbgly_role = "预算接口人";//wuxx
	 //项目总监
	  public static String xmsz_role = "项目总监";//wuxx
	  /*
	   * 预算部门意见，不同节点ID（预算调整）
	   */
	  public static String ngr_ysbm_bp = "obj_c7db7cbf00d0000125b7183011981f37";
	  public static String bsqtry_ysbm_bp = "obj_c7db7ce00060000162dd4e75508b1e01";
	  public static String bsld_ysbm_bp = "obj_c7db7cf2c7e00001ebe912e01b201c71";
	  public static String bsqtld_ysbm_bp = "obj_c7db7d26ffe000011b58aed013de13c8";
	  public static String xgbs_ysbm_bp = "obj_c7db7d3e8bd000013a3624a417e0b040";
	  public static String tjcw_ysbm_bp = "obj_c7db7e0224900001ff8d18d0ca901f60";
	  
	  //研发单位list
	  public static List<String> list_ysbmNoteid;
	  public static String ysbmName = "预算部门意见";
	  static{
		  list_ysbmNoteid = new ArrayList();
		  list_ysbmNoteid.add(ngr_ysbm_bp);
		  list_ysbmNoteid.add(bsqtry_ysbm_bp);
		  list_ysbmNoteid.add(bsld_ysbm_bp);
		  list_ysbmNoteid.add(bsqtld_ysbm_bp);
		  list_ysbmNoteid.add(xgbs_ysbm_bp);
		  list_ysbmNoteid.add(tjcw_ysbm_bp);
	  }
	  /*
	   * 归口单位不同节点ID（预算调整）
	   */
	  public static String gkhq_gkglbm_gl = "obj_c808ed4f4fe000013a25157019908c10";//预算归口管理部门会签
	  public static String gkhq_gkglbm_nb = "obj_c80d760a3c900001fd721e50e540abe0";//部所内部会签
	  public static String gkhq_gkglbm_xz = "obj_c808f8fbdee0000183ab11901be04010";//选择相关部所审核
	  public static String gkhq_gkglbm_cl = "obj_c8302458dc600001fbb91672c3901f3f";//预算归口管理部门内部处理
	  
	  //归口单位list
	  public static List<String> list_gkbmNoteid;
	  public static String gkbmName = "归口部门意见";
	  static{
		  list_gkbmNoteid = new ArrayList();
		  list_gkbmNoteid.add(gkhq_gkglbm_gl);
		  list_gkbmNoteid.add(gkhq_gkglbm_nb);
		  list_gkbmNoteid.add(gkhq_gkglbm_xz);
		  list_gkbmNoteid.add(gkhq_gkglbm_cl);
	  }
	  
	  /*
	   * 财务部门、主管领导节点ID（预算调整）
	   */
	  public static String cwld_cwbm_fzdss = "obj_c7db7e0b018000016f37147040d3e560";
	  public static String zgyld_cwbm_fzdss = "obj_c7db7e2ed53000019f481e901420b460";
	  
	  //财务部list
	  public static List<String> list_cwbmNoteid;
	  public static String cwbmName = "财务部门意见";
	  static{
		  list_cwbmNoteid = new ArrayList();
		  list_cwbmNoteid.add(cwld_cwbm_fzdss);
		  //list_cwbmNoteid.add(zgyld_cwbm_fzdss);
		    
	  }
	  //主管领导list
	  public static List<String> list_zgldNoteid;
	  public static String zgldName = "主管领导意见";
	  static{
		  list_zgldNoteid = new ArrayList();
		  list_zgldNoteid.add(zgyld_cwbm_fzdss);
		    
	  }
	  
	  
	  //获取兼职角色“集团企标管理员”的人员所在部门ID集合
	  public static String bmid_ep_manager_sql = "SELECT GROUP_CONCAT(u.DEPARTMENTID) deptid "
							  		+ "FROM ORGUSERMAP m, ORGROLE r, ORGUSER u "
							  		+ "WHERE m.ROLEID = r.ID "
							  		+ "AND m.userid = u.userid "
							  		+ "AND r.ROLENAME = '企标管理员'";
	  //集团企标管理员部门ID集合
	  public static String bmid_ep_manager;
	  static{
		  bmid_ep_manager = DBSql.getString(bmid_ep_manager_sql);
	  }
	  
	  //获取兼职角色“集团企标管理员”的人员账号集合
	  public static String userid_ep_manager_sql = "select GROUP_CONCAT(m.USERID) userid "
							+ "from ORGUSERMAP m,ORGROLE r "
							+ "where m.ROLEID = r.ID "
							+ "and r.ROLENAME = '集团企标管理员'";
	  //集团企标管理员账号集合
	  public static String userid_ep_manager;
	  static{
		  userid_ep_manager = DBSql.getString(userid_ep_manager_sql);
	  }
	  /**
	   * 通知指定人员
	   * @param processInst
	   * @param parentTaskInst
	   * @param uc
	   * @param userid 指定人员，多个用空格隔开
	   * @param title
	   */
	  public static void circulatePeople(String processInst,
			  String parentTaskInst,
			  UserContext uc,
			  String userid,
			  String title){
		  //发送传阅任务
//		  SDK.getTaskAPI().
//		  		createUserCCTaskInstance(
//		  				processInst, 
//		  				parentTaskInst, 
//		  				uc.getUID(), 
//		  				userid, 
//		  				title);
		  //发送通知任务
		  SDK.getTaskAPI().
		  		createUserNotifyTaskInstance(
		  				processInst, 
		  				parentTaskInst, 
		  				uc.getUID(),
		  				userid, 
		  				title);
	  }
  /**
   * 在角色维护表中：BO_ACT_ENTERPRISE_ROLEDATA
   * 获取指定角色名、部门ID的人员账号
   * @param rolename 指定角色
   * @param deptId 指定部门ID
   * @return
   */
  public static String getEnterpriseManager(String rolename,String deptId){
	  //在维护表中获取集团企标管理员角色的账号
	  String sql = "SELECT GROUP_CONCAT(userid) userid "
				  		+ "FROM BO_ACT_ENTERPRISE_ROLEDATA "
				  		+ "WHERE ROLENAME = '"+rolename+"' ";
	  /*
	   * 如果指定部门ID不为空
	   */
	  if(deptId != null && !"".equals(deptId)){
		  //当前人部门全路径
		  String pathCurrentDeptId = DepartmentCache.getModel(deptId).getPathIdOfCache();
		  sql += "AND locate(deptid, '"+pathCurrentDeptId+"' ) > 0";
	  }
	  System.err.println("===获取维护表中角色的sql："+sql+"======");
	  
	  String people = DBSql.getString(sql);
	  //把字符串中逗号全部替换成空格
	  if(people != null && !"".equals(people)){
		  people = people.replaceAll(",", " ");
	  }
	  return people;
  }
  
  /**
   * 获取当前节点名称
   * @param taskState
   * @param parentTaskID
   * @param task_title
   * @param activityId
   * @param processDefid
   * @return
   */
  public static String getNoteName(int taskState,String parentTaskID,String task_title,String activityId,String processDefid){
		String activityName = "";
		UserTaskModel taskModel = UserTaskDefUtil.getModel(processDefid, activityId);
		if(taskModel != null ){
			activityName = taskModel.name;
		}//节点名称

		if(taskState == 11){
			//任务为加签或阅办类任务
			//父任务状态
			TaskInstance parent_taskinstance = SDK.getTaskAPI().getInstanceById(parentTaskID);
			if(parent_taskinstance != null){
				List<BO> list_processStep = SDK.getBOAPI().query("BO_ACT_EXBUTTON_MEMO_DATA").addQuery("PROCESSACTIVITYID = ", activityId).list();
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
		return activityName;
	}
	 /**
	 * 根据流程定义ID，判断是否是主流程
	 * 
	 * @author nch
	 * @date 2017-10-20
	 * @param processdefid
	 * @return
	 */
	public static boolean isMainProcess(String processdefid) {
	
		List<BO> list = SDK.getBOAPI().query("BO_ACT_PROCESS_DATA").addQuery("PROCESSDEFNID = ", processdefid)
				.addQuery("ISMAIN = ", "否").list();
		boolean bol = true;
		if (list != null && list.size() > 0) {
			bol = false;
		}
		return bol;
	}
}
