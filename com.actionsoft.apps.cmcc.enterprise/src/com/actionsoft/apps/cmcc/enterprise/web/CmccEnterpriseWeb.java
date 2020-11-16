package com.actionsoft.apps.cmcc.enterprise.web;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.apps.cmcc.enterprise.util.CmccCommonNew;
import com.actionsoft.apps.cmcc.enterprise.util.EnterpriseUtilEntryEventClass;
import com.actionsoft.apps.cmcc.enterprise.util.OptionUtil;
import com.actionsoft.apps.cmcc.enterprise.util.UtilEntryEventClass;
import com.actionsoft.bpms.bpmn.engine.model.run.TaskCommentModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.commons.mvc.view.ActionWeb;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.bpms.util.UtilURL;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.AppAPI;
import com.actionsoft.sdk.local.api.ORGAPI;
import com.actionsoft.sdk.local.api.ProcessAPI;
import com.actionsoft.sdk.local.api.TaskAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
/**
 * 企标管理web公共类
 * @author chenxf
 *
 */
public class CmccEnterpriseWeb
  extends ActionWeb
{
  public UserContext uc;
  
  public CmccEnterpriseWeb(UserContext uc)
  {
    super(uc);
    this.uc = uc;
  }
  /**
   * 注销按钮事件
   * @param processInstId
   * @param processDefid
   * @return
   */
  public String logOff(String processInstId, String processDefid)
  {
    ProcessInstance instance = SDK.getProcessAPI().getInstanceById(processInstId);
    
    //是否为代办任务
    String target = DBSql.getString("SELECT TARGET FROM WFC_TASK WHERE processinstid = ? AND TASKSTATE=?", new Object[] { processInstId, Integer.valueOf(1) });
    if (!this.uc.getUID().equals(target))
    {
      ResponseObject ro = ResponseObject.newErrResponse();
      ro.put("msg", "当前任务已提交,注销失败!");
      return ro.toString();
    }
    //获取注销接口
    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.enterprise", "LOGOFF_BUTTON");
    
    if ((url != null) && (!"".equals(url)))
    {
      StringBuffer sbf = new StringBuffer();
      //拼接URL
      sbf.append(url).append(instance.getId());
      //调用接口URL，返回json字符串
      String str = UtilURL.get(sbf.toString().trim());
      System.err.println("======返回的json：" + str + "================");
      /**
       * 解析json返回数据
       */
      JSONObject resultJson = JSONObject.fromObject(str);
      //删除结果
      String checkResult = resultJson.getString("checkResult");
      //如果为success时，删除成功！
      if ((checkResult != null) && (checkResult.equals("success")))
      {
        ResponseObject ro = ResponseObject.newOkResponse();
        boolean b = SDK.getProcessAPI().delete(instance, this.uc);
        //删除流程创建表数据，通过流程实例ID
        int sum = DBSql.update("delete from BO_ACT_CMCC_PROCESSDATA "
        					+ "where PROCESSID = '"+processInstId+"'");
        if(b && sum > 0){
        	 ro.put("msg", "注销成功!");
             return ro.toString();
        }
      }
      ResponseObject ro = ResponseObject.newErrResponse();
      ro.put("msg", "注销失败!");
      return ro.toString();
    }
    ResponseObject ro = ResponseObject.newErrResponse();
    ro.put("msg", "注销失败!");
    return ro.toString();
  }
  /**
   * 获取审批意见事件
   * @param processid
   * @return
   */
  public String getOpinion(String processid)
  {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    List<TaskCommentModel> list_now = new ArrayList();
    
    list_now = SDK.getProcessAPI().getCommentsById(processid);
    
    StringBuffer html = new StringBuffer();
    List<Map<String, String>> list_jsbyj = new ArrayList();
    List<Map<String, String>> list_yfjgyj = new ArrayList();
    List<Map<String, String>> list_xqjgyj = new ArrayList();
    if ((list_now != null) && (list_now.size() > 0)) {
      for (int i = 0; i < list_now.size(); i++)
      {
        String activityName = ((TaskCommentModel)list_now.get(i)).getActivityName();
        String msg = ((TaskCommentModel)list_now.get(i)).getMsg();
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
        String taskId = ((TaskCommentModel)list_now.get(i)).getTaskInstId();
        String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();
        String taskid = ((TaskCommentModel)list_now.get(i)).getTaskInstId();
        TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
        String activityDefid = taskInstance.getActivityDefId();
        if (!"cancel".equals(controlState))
        {
          Timestamp createTimestamp = ((TaskCommentModel)list_now.get(i)).getCreateDate();
          String createData = sdf.format(createTimestamp);
          String createUserid = ((TaskCommentModel)list_now.get(i)).getCreateUser();
          String userName = "";
          String zwdj = "";
          UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
          if (usermodel != null)
          {
            userName = usermodel.getUserName();
            zwdj = usermodel.getExt1();
          }
          String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();
          Map<String, String> map = new HashMap();
          map.put("activityName", activityName);
          map.put("msg", msg);
          map.put("userName", userName);
          map.put("zwdj", zwdj);
          map.put("deptname", deptname);
          map.put("createData", createData);
          if (CmccCommon.list_yfdwNoteid.contains(activityDefid)) {
            list_yfjgyj.add(map);
          }
          if (CmccCommon.list_jsdwNoteid.contains(activityDefid)) {
            list_jsbyj.add(map);
          }
          if (CmccCommon.list_xqdwNoteid.contains(activityDefid)) {
        	  list_xqjgyj.add(map);
          }
        }
      }
    }
    if ((list_yfjgyj != null) && (list_yfjgyj.size() > 0)) {
      html.append(OptionUtil.optionMosaic(list_yfjgyj, CmccCommon.yfdwName));
    }
    if ((list_jsbyj != null) && (list_jsbyj.size() > 0)) {
      html.append(OptionUtil.optionMosaic(list_jsbyj, CmccCommon.jsdwName));
    }
    if ((list_xqjgyj != null) && (list_xqjgyj.size() > 0)) {
        html.append(OptionUtil.optionMosaic(list_xqjgyj, CmccCommon.xqdwName));
      }
    if (UtilString.isEmpty(html.toString()))
    {
      ResponseObject rsobj = ResponseObject.newOkResponse("");
      return rsobj.toString();
    }
    ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
    return rsobj.toString();
  }
  
  /**
   * 获取审批意见事件
   * wuxx
   * @param processid
   * @return
   */
  public String getOpinionnew(String processid)
  {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    List<TaskCommentModel> list_now = new ArrayList();
    
    list_now = SDK.getProcessAPI().getCommentsById(processid);
    
    StringBuffer html = new StringBuffer();
    List<Map<String, String>> list_jsbyj = new ArrayList();
    List<Map<String, String>> list_yfjgyj = new ArrayList();
    List<Map<String, String>> list_xqjgyjnew = new ArrayList();
    if ((list_now != null) && (list_now.size() > 0)) {
      for (int i = 0; i < list_now.size(); i++)
      {
        String activityName = ((TaskCommentModel)list_now.get(i)).getActivityName();
        String msg = ((TaskCommentModel)list_now.get(i)).getMsg();
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
        String taskId = ((TaskCommentModel)list_now.get(i)).getTaskInstId();
        String controlState = SDK.getTaskAPI().getTaskInstance(taskId).getControlState();
        String taskid = ((TaskCommentModel)list_now.get(i)).getTaskInstId();
        TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
        String activityDefid = taskInstance.getActivityDefId();
        if (!"cancel".equals(controlState))
        {
          Timestamp createTimestamp = ((TaskCommentModel)list_now.get(i)).getCreateDate();
          String createData = sdf.format(createTimestamp);
          String createUserid = ((TaskCommentModel)list_now.get(i)).getCreateUser();
          String userName = "";
          String zwdj = "";
          UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
          if (usermodel != null)
          {
            userName = usermodel.getUserName();
            zwdj = usermodel.getExt1();
          }
          String deptname = SDK.getORGAPI().getDepartmentByUser(createUserid).getName();
          Map<String, String> map = new HashMap();
          map.put("activityName", activityName);
          map.put("msg", msg);
          map.put("userName", userName);
          map.put("zwdj", zwdj);
          map.put("deptname", deptname);
          map.put("createData", createData);
          if (CmccCommonNew.list_yfdwNoteid.contains(activityDefid)) {//承担单位意见
            list_yfjgyj.add(map);
          }
          if (CmccCommonNew.list_jsdwNoteid.contains(activityDefid)) {//技术部意见
            list_jsbyj.add(map);
          }
          if (CmccCommonNew.list_xqdwNoteid.contains(activityDefid)) {//需求单位意见
        	  list_xqjgyjnew.add(map);
          }
        }
      }
    }
    if ((list_yfjgyj != null) && (list_yfjgyj.size() > 0)) {
      html.append(OptionUtil.optionMosaic(list_yfjgyj, CmccCommonNew.yfdwName));//承担单位意见
    }
    if ((list_jsbyj != null) && (list_jsbyj.size() > 0)) {
      html.append(OptionUtil.optionMosaic(list_jsbyj, CmccCommonNew.jsdwName));//技术部意见
    }
    if ((list_xqjgyjnew != null) && (list_xqjgyjnew.size() > 0)) {
        html.append(OptionUtil.optionMosaic(list_xqjgyjnew, CmccCommonNew.xqdwName));//需求单位意见
      }
    if (UtilString.isEmpty(html.toString()))
    {
      ResponseObject rsobj = ResponseObject.newOkResponse("");
      return rsobj.toString();
    }
    ResponseObject rsobj = ResponseObject.newOkResponse(html.toString());
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
	 * 企标管理
	 * 同一部所下只能选取一个部所领导
	 * @author chenxf
	 * @date   2018年7月25日 下午3:27:21
	 * @param me
	 * @param participants
	 * @param rolename
	 * @return
	 */
	public String demandDepartmentSign(UserContext me,String participants,String rolename){
		System.err.println("===成功调用===");
		System.err.println("===participants："+participants+"===");
		System.err.println("===rolename："+rolename+"===");
		ResponseObject rsobj = ResponseObject.newOkResponse();
		//拆分办理人
		String[] peoples = participants.split(" ");
		//人员的数量
		int length = peoples.length;
		/*
		 * 只有选择两个以上人员才判断
		 */
		if(length >= 2){
			if(UtilEntryEventClass.getMapperDeptIdByUserid(peoples,length,rolename)){
				rsobj.put("isxqbm", "true");
			}else{
				rsobj.put("isxqbm", "false");
			}
		}else{
			rsobj.put("isxqbm", "true");
		}
		return rsobj.toString();
	}
	/**
	 * 同一部门接口人不能选两个或两个以上的人员
	 * @author wuxx
	 * @date   2019年05月10日 
	 * @param me
	 * @param participants
	 * @return
	 */
	public String qbdemandDepartmentSign(UserContext me, 
							String participants, String params){
		System.err.println("===成功调用11111===");
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
			if(EnterpriseUtilEntryEventClass.getMapperDeptIdByUserid(peoples,length,params)){
				rsobj.put("isxqbm", "true");
			}else{
				rsobj.put("isxqbm", "false");
			}
		}else{
			rsobj.put("isxqbm", "true");
		}
		return rsobj.toString();
	}
	
	/**
	 * 添加已办表单为静态页面 
	 * @author chenxf
	 * @date   2018年9月29日 下午5:39:23
	 * @param me
	 * @param bodyHTML
	 * @param headHTML
	 * @param processInstId
	 * @param type
	 * @param taskid
	 * @return
	 */
	public String addTaskCompleteStaticForm(UserContext me,String bodyHTML,String headHTML,
			String processInstId,String type, String taskid){
		
		ResponseObject rsobj = ResponseObject.newOkResponse();
		//拼接后的HTML静态表单
		String formHtml = new CmccCommon().splicingStaticHtml(bodyHTML, headHTML);
		//转成字节数组
		byte[] bates = formHtml.getBytes();
		//创建文件目录路径
		String filePath = "";
		/**
		 * 此处区分是不是项目模块的流程
		 * 如果是项目的话：通过流程实例ID、流程任务ID获取流程定义ID、当前流程任务的节点ID
		 * 并且判断是否在维护表：BO_ACT_COMMON_FORMSNAPSHOT中，如果在，则表单快照保存的名称用任务ID（taskid），
		 * 反之，则用流程实例ID
		 */
		if(!UtilString.isEmpty(type)){
			/*
			 * 判断是否项目模块流程
			 */
			if(type.indexOf("hq-") != -1 || type.indexOf("cmri-") != -1){
				//获取当前任务所在的节点ID
				String processStepId = SDK.getTaskAPI().getInstanceById(taskid).getActivityDefId();
				//当前流程实例的流程定义ID
				String processdefid = SDK.getProcessAPI().getInstanceById(processInstId).getProcessDefId();
				/*
				 * 在维护表BO_ACT_COMMON_FORMSNAPSHOT中查找，如果有，则用任务实例ID保存
				 */
				String sql = "select count(*) c from BO_ACT_COMMON_FORMSNAPSHOT where PROCESSDEFNID = ? and PROCESSSTEPID = ?";
				String count = DBSql.getString(sql, new Object[]{ processdefid, processStepId });
				if(Integer.parseInt(count) > 0){
					filePath = CmccCommon.path + type + "/" + processInstId + "/" + taskid + ".html";
				}else{
					filePath = CmccCommon.path + type + "/" + processInstId + "/" + processInstId + ".html";
				}
				
			}else{
				filePath = CmccCommon.path + type + "/" + processInstId + ".html";
			}
		}else{
			rsobj.put("success", "false");
			return rsobj.toString();
		}
		System.err.println("=======快照保存路径:"+filePath+"==========");
        File file = new File(filePath);
        //创建所有文件夹
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
       
        //创建文件
        try {
			file.createNewFile();
			 //通过IO流写入
	        FileOutputStream fos = new FileOutputStream(file);
	        fos.write(bates);
	        //关闭IO流
	        fos.close();
			
		} catch (IOException e) {
			e.printStackTrace(System.err);
			rsobj.put("success", "false");
			return rsobj.toString();
		}
        rsobj.put("success", "true");
		return rsobj.toString();
	}
	/**
	 * 打开快照表单
	 * @author chenxf
	 * @date   2018年9月29日 下午6:03:16
	 * @param me
	 * @param processInstId
	 * @param processDefid
	 * @param taskInstId
	 * @return
	 */
	public String openFormSnapshot(UserContext me,String processInstId, String taskInstId){
		/**
		 * 获取流程定义ID、父任务ID
		 */
		String processDefid = SDK.getProcessAPI().getInstanceById(processInstId).getProcessDefId();
		String parentTaskId = SDK.getTaskAPI().getInstanceById(taskInstId).getParentTaskInstId();
		/*
		 * 获取流程类型
		 */
		String sql = "select concat_ws(',', PROCESSTYPE, PROCESSNAME) ws from BO_ACT_PROCESS_DATA "
						+ "where PROCESSDEFNID = '"+processDefid+"'";
		String ws = DBSql.getString(sql);
		System.err.println("----流程类型及标题："+ws+"--------");
		String[] datas = ws.split(",");
		String type = datas[0];
		String title = datas[1];
		/*
		 * 获取第三方表单快照
		 */
		String url = "";
		if(type.indexOf("hq-") != -1 || type.indexOf("cmri-") != -1){
			//获取当前任务所在的节点ID
			String processStepId = SDK.getTaskAPI().getInstanceById(taskInstId).getActivityDefId();
			//当前流程实例的流程定义ID
			String processdefid = SDK.getProcessAPI().getInstanceById(processInstId).getProcessDefId();
			/*
			 * 在维护表BO_ACT_COMMON_FORMSNAPSHOT中查找，如果有，则用任务实例ID保存
			 */
			String sql_count = "select count(*) c from BO_ACT_COMMON_FORMSNAPSHOT where PROCESSDEFNID = ? and PROCESSSTEPID = ?";
			String count = DBSql.getString(sql_count, new Object[]{ processdefid, processStepId });
			if(Integer.parseInt(count) > 0){
				url = "../formSnapShot/" + type + "/" + processInstId + "/" + taskInstId +".html";
			}else{
				url = "../formSnapShot/" + type + "/" + processInstId + "/" + processInstId +".html";
			}
			
		}else{
			url = "../formSnapShot/" + type + "/" + processInstId +".html?time="+System.currentTimeMillis();
		}
		System.err.println("-------第三方表单快照："+url+"-----------");
		
		Map<String, Object> map = new HashMap();
		map.put("title", title);
		map.put("url", url);
		map.put("processInstId", processInstId);
		map.put("processDefid", processDefid);
		map.put("sessionid", me.getSessionId());
		map.put("taskInstId", taskInstId);
		map.put("parentTaskId", parentTaskId);
		map.put("processType", type);
		if(type.contains("hq-") || type.contains("cmri-") || type.contains("test_") || type.contains("org_")){
			map.put("tableHeight", "100%");
			map.put("opitionsBackground", "background:#f2f2f2 !important");
			
		}else{
			map.put("tableHeight", "80%");
			map.put("opitionsBackground", "");
		}
		map.put("closeText", "关闭");
		map.put("processTrack", "流程跟踪");
		map.put("processPage", "流程图");
		map.put("logOption", "审批意见");
		
		
		return HtmlPageTemplate.merge("com.actionsoft.apps.common.formsnapshot", 
										"com.actionsoft.apps.budget.formsnapshot.htm", 
										map);
	}
	/**
	 * 获取企标复审流程审批意见
	 * @author chenxf
	 * @date   2018年10月16日 上午11:15:27
	 * @param processId
	 * @param processDefid
	 * @return
	 */
	public String getFSOpinion(String processId,String processDefid){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<TaskCommentModel> list_parent = new ArrayList<TaskCommentModel>();//父流程实例ID审批意见
		List<TaskCommentModel> list_sub = new ArrayList<TaskCommentModel>();//子流程实例ID审批意见
		String processId_parent = "";//父流程实例ID
		List<RowMap> list_subprocessId = new ArrayList<RowMap>();//子流程实例ID集合
		if(CmccCommon.isMainProcess(processDefid)){
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
		
		System.err.println("=====获取审批意见：=========");
		
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String, String>> list_jsbyj = new ArrayList();
	    List<Map<String, String>> list_yfjgyj = new ArrayList();
	    List<Map<String, String>> list_xqjgyj = new ArrayList();
	    List<Map<String, String>> list_qbglyyj = new ArrayList();
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
					if(CmccCommon.list_jsdwNoteid.toString().contains(activityDefid)){
						list_jsbyj.add(map);
					}else if(CmccCommon.list_yfdwNoteid.toString().contains(activityDefid)){
						list_yfjgyj.add(map);
					}else if(CmccCommon.list_xqdwNoteid.toString().contains(activityDefid)){
						list_xqjgyj.add(map);
					}else if(CmccCommon.list_qbglyNoteid.toString().contains(activityDefid)){
						list_qbglyyj.add(map);
					}
				}

			}
		}
		if ((list_yfjgyj != null) && (list_yfjgyj.size() > 0)) {
		      html.append(OptionUtil.optionMosaic(list_yfjgyj, CmccCommon.yfdwName));
	    }
		if ((list_xqjgyj != null) && (list_xqjgyj.size() > 0)) {
	        html.append(OptionUtil.optionMosaic(list_xqjgyj, CmccCommon.xqdwName));
	      }
		if ((list_qbglyyj != null) && (list_qbglyyj.size() > 0)) {
	        html.append(OptionUtil.optionMosaic(list_qbglyyj, CmccCommon.qbglyName));
	      }
	    if ((list_jsbyj != null) && (list_jsbyj.size() > 0)) {
	      html.append(OptionUtil.optionMosaic(list_jsbyj, CmccCommon.jsdwName));
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
	 * 获取企标报批流程审批意见
	 * @author wuxx
	 * @date   20190403
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
		if(CmccCommon.isMainProcess(processDefid)){
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
		
		System.err.println("=====获取审批意见1111：=========");
		
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String, String>> list_jsbyj = new ArrayList();
	    List<Map<String, String>> list_yfjgyj = new ArrayList();
	    List<Map<String, String>> list_xqjgyj = new ArrayList();
	    List<Map<String, String>> list_qbglyyj = new ArrayList();
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
					if(CmccCommonNew.list_jsdwNoteid.toString().contains(activityDefid)){//技术部意见
						list_jsbyj.add(map);
					}else if(CmccCommonNew.list_yfdwNoteid.toString().contains(activityDefid)){//承担单位意见
						list_yfjgyj.add(map);
					}else if(CmccCommonNew.list_xqdwNoteid.toString().contains(activityDefid)){//需求单位意见
						list_xqjgyj.add(map);
					}else if(CmccCommonNew.list_qbglyNoteid.toString().contains(activityDefid)){//企标管理员意见
						list_qbglyyj.add(map);
					}
					System.err.println("00000000"+map);
				}

			}
		}
		if ((list_yfjgyj != null) && (list_yfjgyj.size() > 0)) {
		      html.append(OptionUtil.optionMosaic(list_yfjgyj, CmccCommonNew.yfdwName));//承担单位意见
	    }
		if ((list_xqjgyj != null) && (list_xqjgyj.size() > 0)) {
	        html.append(OptionUtil.optionMosaic(list_xqjgyj, CmccCommonNew.xqdwName));//技术部和需求单位意见
	      }
		if ((list_qbglyyj != null) && (list_qbglyyj.size() > 0)) {
	        html.append(OptionUtil.optionMosaic(list_qbglyyj, CmccCommonNew.qbglyName));//企标管理员意见
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
	 * 获取企标流程审批意见
	 * @author wuxx
	 * @date   20190408
	 * @param processId
	 * @param processDefid
	 * @return
	 */
	public String getQblc(String processId,String processDefid){
		ResponseObject rsobj = ResponseObject.newOkResponse();
		String isYjy = "";
		if(CmccCommon.isMainProcess(processDefid)){
			String spjl = DBSql.getString("SELECT id FROM WFC_COMMENT WHERE processinstid = ? AND actionname=?", new Object[] { processId, "送技术部和需求部门会签" });
			if("".equals(spjl) || spjl==null){
				 isYjy = "否";
			}else{
				 isYjy = "是";
			}
		}
		rsobj.put("isYjy", isYjy);
		return rsobj.toString();
	
	}
}
