package com.actionsoft.apps.cmcc.testmanagement.web;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.org.model.UserMapModel;
import org.jfree.io.FileUtilities;

import com.actionsoft.apps.cmcc.testmanagement.constant.TestManagementConst;
import com.actionsoft.apps.cmcc.testmanagement.util.FormUtil;
import com.actionsoft.apps.cmcc.testmanagement.util.InterfaceUtil;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.commons.mvc.view.ActionWeb;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilFile;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.bpms.util.UtilURL;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONArray;

import net.sf.json.JSONObject;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2019年1月8日 上午10:39:51 
* 类说明 
*/
public class TestmanagementWeb extends ActionWeb{
	/**
	 * 根据流程实例id删除流程实例
	 * @param me              用户上下文
	 * @param processInstId   流程实例id
	 * @param processDefid    流程定义id
	 * @return
	 */
	public String DeleteProcessInst(UserContext me, String processInstId,String processDefid){
		ProcessInstance instance = SDK.getProcessAPI().getInstanceById(processInstId);//根据流程实例id获取流程实例
		String target=DBSql.getString("SELECT TARGET FROM WFC_TASK WHERE processinstid = ? AND TASKSTATE=?",new Object[]{processInstId,1});
		if(!me.getUID().equals(target)){
			ResponseObject ro = ResponseObject.newErrResponse();
			ro.put("msg", "当前任务已提交,注销失败!");//返回状态
			return ro.toString();
		}
//		String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "LOGOFF_BUTTON");//获取url接口地址
		String type = DBSql.getString("SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?", new Object[]{processDefid});//查询流程类型
		//获取url接口地址
		String url = "";
		//获取注销接口地址
		url = SDK.getAppAPI().getProperty(TestManagementConst.APPID, "LOGOFF_BUTTON");
		StringBuffer sbf = new StringBuffer();
		sbf.append(url).append("?type=").append(type).append("&process_id=").append(instance.getId());//拼接接口地址

		String str = UtilURL.get(sbf.toString().trim());
		JSONObject resultJson = JSONObject.fromObject(str); //从url接口获取数据
		String data = resultJson.getString("data");
		JSONObject datajson = JSONObject.fromObject(data);
		String msg = datajson.getString("msg");//获取接口返回的状态信息
		
		//if(msg.equals("ok"))
		if(msg.equals("1") || msg.equals("2"))//如果等于1或者等于2，删除流程实例
		{
			ResponseObject ro = ResponseObject.newOkResponse();//获取返回数据
			String sql = "DELETE FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID='"+instance.getId()+"'";
			DBSql.update(sql);
			SDK.getProcessAPI().delete(instance, me);//删除流程实例
			ro.put("msg", "注销成功!");//返回状态
			return ro.toString();
		}else
		{
			ResponseObject ro = ResponseObject.newErrResponse();
			ro.put("msg", "注销失败!");//返回状态
			return ro.toString();
		}
	}
	
	/**
	 * 添加已办表单为静态页面 
	 * @param me
	 * @param bodyHTML
	 * @param headHTML
	 * @param processInstId
	 * @param type
	 * @param taskid
	 * @return
	 */
	public String addTaskCompleteStaticForm(UserContext me,String bodyHTML,String headHTML,String logsHTML,
			String processInstId,String type, String taskid){
		ResponseObject rsobj = ResponseObject.newOkResponse();
		//拼接后的HTML静态表单
		String formHtml = new FormUtil().splicingStaticHtml(bodyHTML, headHTML);
		//表单内容转成字节数组
		byte[] bates = formHtml.getBytes();
		//审批意见内容转成字节数组
		byte[] bates_logs = logsHTML.getBytes();
		//创建表单文件目录路径
		String filePath = "";
		//创建审批意见目录路径
		String filePath_logs = "";
		if(!UtilString.isEmpty(type)){
			filePath = TestManagementConst.path  + type + "/" + processInstId + ".html";
			filePath_logs = TestManagementConst.path  + type + "/" + processInstId + ".txt";
		}else{
			rsobj.put("success", "false");
			return rsobj.toString();
		}
        File file = new File(filePath);
        File file_logs = new File(filePath_logs);
        //创建所有文件夹
        File parent = file.getParentFile();
        File parent_logs = file_logs.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (parent_logs != null && !parent_logs.exists()) {
        	parent_logs.mkdirs();
        }
        //创建文件
        try {
			file.createNewFile();
			 //通过IO流写入
	        FileOutputStream fos = new FileOutputStream(file);
	        fos.write(bates);
	        //关闭IO流
	        fos.close();
	        
	        /** 创建审批意见文件 **/
	        file_logs.createNewFile();
			 //通过IO流写入
	        FileOutputStream fos_logs = new FileOutputStream(file_logs);
	        fos_logs.write(bates_logs);
	        //关闭IO流
	        fos_logs.close();
			
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
	 * @param me
	 * @param processInstId
	 * @param taskInstId
	 * @return
	 */
	public String openFormSnapshot(UserContext me, String processInstId, String taskInstId) {
		/**
		 * 获取流程定义ID、父任务ID
		 */
		String processDefid = SDK.getProcessAPI().getInstanceById(processInstId).getProcessDefId();
		String parentTaskId = SDK.getTaskAPI().getInstanceById(taskInstId).getParentTaskInstId();
		/*
		 * 获取流程类型
		 */
		String sql = "select concat_ws(',', PROCESSTYPE, PROCESSNAME) ws from BO_ACT_PROCESS_DATA "
				+ "where PROCESSDEFNID = '" + processDefid + "'";
		String ws = DBSql.getString(sql);
		System.err.println("----流程类型及标题：" + ws + "--------");
		String[] datas = ws.split(",");
		String type = datas[0];
		String title = datas[1];
		/*
		 * 获取第三方表单快照
		 */
		String url = "../formSnapShot/" + type + "/" + processInstId + ".html";
		String logs_path = TestManagementConst.path  + type + "/" + processInstId + ".txt";
		UtilFile utilFile=new UtilFile(logs_path);
		String logsString = utilFile.readStrUTF8();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", title);
		map.put("url", url);
		map.put("logsString", logsString);
		map.put("processInstId", processInstId);
		map.put("processDefid", processDefid);
		map.put("sessionid", me.getSessionId());
		map.put("taskInstId", taskInstId);
		map.put("parentTaskId", parentTaskId);
		map.put("processType", type);
		map.put("tableHeight", "100%");
		map.put("opitionsBackground", "background:#f2f2f2 !important");
		map.put("closeText", "关闭");
		map.put("processTrack", "流程跟踪");
		map.put("processPage", "流程图");
		map.put("logOption", "审批意见");

		return HtmlPageTemplate.merge(TestManagementConst.APPID,
				"com.actionsoft.apps.cmcc.testmanagement.formsnapshot.htm", map);
	}
	
	/**
	 * 获取项目信息
	 * @param processId
	 * @return
	 */
	/*public String getProjectInfo(String processId){
		JSONObject json =new JSONObject();
		List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", processId).orderBy("CREATEDATE").desc().list();
		if (list != null && list.size() > 0) {
			String projectManager = list.get(0).getString("PROJECTMANAGER");
			json.put("projectManager", projectManager);
			json.put("msg", "true");
		}else{
			json.put("projectManager", "");
			json.put("msg", "false");
		}
		return json.toString();
	}*/
	
	/**
	 * 获取项目信息
	 * @param processId
	 * @return
	 */
	public String getProjectInfo(String processId){
		InterfaceUtil interfaceUtil = new InterfaceUtil();
		String json =interfaceUtil.getProjectInfo(processId);
		return json;
	}

	/**
	 * 获取人员项目信息
	 * @param userId
	 * @return
	 */
	public String getUserInfo(String userId){
		ResponseObject ro = ResponseObject.newOkResponse();//获取返回数据
		JSONObject result =new JSONObject();
		TestmanagementWeb web=new TestmanagementWeb();
		if(UtilString.isEmpty(SDK.getORGAPI().validateUsers(userId))){//校验用户是否合法
			String roleId =DBSql.getString("SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{TestManagementConst.TEST_MANAGER_NAME});
			List<UserMapModel> userMapsByDept = SDK.getORGAPI().getUserMaps(userId);
			for (UserMapModel userMapModel : userMapsByDept) {
				if(userMapModel.getRoleId().equals(roleId)){
					result.put("show",1);
					ro.setData(result);
					return ro.toString();
				}
			}
		}
		result.put("show",0);
		ro.setData(result);
		return ro.toString();
	}
	
}
