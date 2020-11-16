package com.actionsoft.apps.cmcc.budget.web;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.apps.cmcc.budget.util.BudgetUtilEntryEventClass;
import com.actionsoft.apps.cmcc.budget.util.CmccCommon;
import com.actionsoft.apps.cmcc.budget.util.CmccUrlUtil;
import com.actionsoft.apps.cmcc.budget.util.OptionUtil;
import com.actionsoft.bpms.bpmn.engine.model.run.TaskCommentModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.mvc.view.ActionWeb;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.bpms.util.UtilURL;
import com.actionsoft.sdk.local.SDK;

import net.sf.json.JSONObject;
/**
 * 预算调整
 * web类
 * @author chenxf
 *
 */
public class CmccBudgetWeb extends ActionWeb{
	public CmccBudgetWeb() {
		super();
	}

	 public UserContext uc;
	  
	public CmccBudgetWeb(UserContext uc){
		super(uc);
		this.uc = uc;
	}
	/**
	   * 获取审批意见事件
	   * @param processid
	   * @return
	   */
	  public String getOpinion(String processid, String processDefid)
	  {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    List<TaskCommentModel> list_now = new ArrayList<TaskCommentModel>();//父流程实例ID审批意见
		List<TaskCommentModel> list_sub = new ArrayList<TaskCommentModel>();//子流程实例ID审批意见
		String processId_parent = "";//父流程实例ID
		List<RowMap> list_subprocessId = new ArrayList<RowMap>();//子流程实例ID集合
		if(CmccCommon.isMainProcess(processDefid)){
			processId_parent = processid;
			list_subprocessId = DBSql.getMaps("SELECT ID FROM WFC_PROCESS WHERE PARENTPROCESSINSTID= ? ORDER BY STARTTIME ASC", new Object[]{processid});
		}else{
			processId_parent = DBSql.getString("SELECT PARENTPROCESSINSTID FROM WFC_PROCESS WHERE ID=?", new Object[]{processid});
			list_subprocessId = DBSql.getMaps("SELECT ID FROM WFC_PROCESS WHERE PARENTPROCESSINSTID= ? ORDER BY STARTTIME ASC", new Object[]{processId_parent});
		}

		//当前流程审批意见
		list_now = SDK.getProcessAPI().getCommentsById(processId_parent);
		if(list_subprocessId != null && list_subprocessId.size() > 0){
			for(int i = 0;i < list_subprocessId.size();i++){
				String sub_processId = list_subprocessId.get(i).getString("ID");
				List<TaskCommentModel> list_subprocess = SDK.getProcessAPI().getCommentsById(sub_processId);
				list_sub.addAll(list_subprocess);
			}
		}
		list_now.addAll(list_sub);
	    
	    StringBuffer html = new StringBuffer();
	    /*
	     * 预算调整流程意见
	     */
	    List<Map<String, String>> list_jsbyj = new ArrayList();
	    List<Map<String, String>> list_yfjgyj = new ArrayList();
	    List<Map<String, String>> list_xqjgyj = new ArrayList();
	    List<Map<String, String>> list_zgldyj = new ArrayList();//wxx
	    
	    if ((list_now != null) && (list_now.size() > 0)) {
	      for (int i = 0; i < list_now.size(); i++)
	      {
	        String activityName = ((TaskCommentModel)list_now.get(i)).getActivityName();
	        String msg = ((TaskCommentModel)list_now.get(i)).getMsg();
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
	          /*
	           * 预算调整的意见
	           */
	          if (CmccCommon.list_ysbmNoteid.contains(activityDefid)) {
	            list_yfjgyj.add(map);
	          }
	          if (CmccCommon.list_gkbmNoteid.contains(activityDefid)) {
	            list_jsbyj.add(map);
	          }
	          if (CmccCommon.list_cwbmNoteid.contains(activityDefid)) {
	        	  list_xqjgyj.add(map);
	          }
	          if (CmccCommon.list_zgldNoteid.contains(activityDefid)) {//wxx
	        	  list_zgldyj.add(map);
	          }
	        }
	      }
	    }
	    /*
	     * 预算调整流程意见拼接
	     */
	    if ((list_yfjgyj != null) && (list_yfjgyj.size() > 0)) {
	      html.append(OptionUtil.optionMosaic(list_yfjgyj, CmccCommon.ysbmName));
	    }
	    if ((list_jsbyj != null) && (list_jsbyj.size() > 0)) {
	      html.append(OptionUtil.optionMosaic(list_jsbyj, CmccCommon.gkbmName));
	    }
	    if ((list_xqjgyj != null) && (list_xqjgyj.size() > 0)) {
	        html.append(OptionUtil.optionMosaic(list_xqjgyj, CmccCommon.cwbmName));
	      }
	    if ((list_zgldyj != null) && (list_zgldyj.size() > 0)) {//wxx
	        html.append(OptionUtil.optionMosaic(list_zgldyj, CmccCommon.zgldName));
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
	   * 注销按钮事件
	   * @param processInstId
	   * @param processDefid
	   * @return
	   */
	  public String logOff(String processInstId)
	  {
	    ProcessInstance instance = SDK.getProcessAPI().getInstanceById(processInstId);
	    //是否为代办任务
	    String target = DBSql.getString("SELECT TARGET FROM WFC_TASK WHERE processinstid = ? AND TASKSTATE=?", new Object[] { processInstId, Integer.valueOf(1) });
	    String id = DBSql.getString("SELECT id FROM WFC_TASK WHERE processinstid = ? AND TASKSTATE=?", new Object[] { processInstId, Integer.valueOf(1) });
	    if (!this.uc.getUID().equals(target))
	    {
	      ResponseObject ro = ResponseObject.newErrResponse();
	      ro.put("msg", "当前任务已提交,注销失败!");
	      return ro.toString();
	    }
	    //获取注销接口
	    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.budget", "BACKDATAINTERFACE");
	    if ((url != null) && (!"".equals(url)))
	    {
	      StringBuffer sbf = new StringBuffer();
	      //拼接URL
	      sbf.append(url).append("state=1&processId="+processInstId);
	      sbf.append("&parentTaskId=");
	      sbf.append(id);
	      System.err.println("=====注销的接口URL："+sbf.toString()+"=========");
	      //调用接口URL，返回success
	      String str = CmccUrlUtil.get(sbf.toString().trim());
	      System.err.println("======返回的json：" + str + "================");
//	      /**
//	       * 解析json返回数据
//	       */
//	      JSONObject resultJson = JSONObject.fromObject(str);
//	      //删除结果
//	      String checkResult = resultJson.getString("checkResult");
	      //如果为success时，删除成功！
	      if (str != null && "success".equals(str))
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
		 * 同一部门接口人不能选两个或两个以上的人员
		 * @author wuxx
		 * @date   2018年10月23日 
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
				if(BudgetUtilEntryEventClass.getMapperDeptIdByUserid(peoples,length,params)){
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
