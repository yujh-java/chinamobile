package com.actionsoft.apps.cmcc.standardization.web;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.apps.cmcc.standardization.constant.StandardizationConstant;
import com.actionsoft.apps.cmcc.standardization.util.OptionUtil;
import com.actionsoft.bpms.bpmn.engine.model.run.TaskCommentModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.bpms.util.UtilURL;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONArray;

import net.sf.json.JSONObject;

/**
 * @author yujh
 * @version 创建时间：2019年4月11日 下午3:08:14 类说明
 */
public class StandardizationWeb {
	/**
	 * 根据流程实例id删除流程实例
	 * 
	 * @param me
	 *            用户上下文
	 * @param processInstId
	 *            流程实例id
	 * @param processDefid
	 *            流程定义id
	 * @return
	 */
	public String DeleteProcessInst(UserContext me, String processInstId, String processDefid) {
		ProcessInstance instance = SDK.getProcessAPI().getInstanceById(processInstId);// 根据流程实例id获取流程实例
		String target = DBSql.getString("SELECT TARGET FROM WFC_TASK WHERE processinstid = ? AND TASKSTATE=?",
				new Object[] { processInstId, 1 });
		if (!me.getUID().equals(target)) {
			ResponseObject ro = ResponseObject.newErrResponse();
			ro.put("msg", "当前任务已提交,注销失败!");// 返回状态
			return ro.toString();
		}
		String type = DBSql.getString("SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?",
				new Object[] { processDefid });// 查询流程类型
		// 获取url接口地址
		String url = "";
		// 获取注销接口地址
		url = SDK.getAppAPI().getProperty(StandardizationConstant.APPID, "LOGOFF_BUTTON");
		StringBuffer sbf = new StringBuffer();
		sbf.append(url).append("?type=").append(type).append("&process_id=").append(instance.getId());// 拼接接口地址
		String str = UtilURL.get(sbf.toString().trim());
		JSONObject resultJson = JSONObject.fromObject(str); // 从url接口获取数据
		String data = resultJson.getString("data");
		JSONObject datajson = JSONObject.fromObject(data);
		String msg = datajson.getString("msg");// 获取接口返回的状态信息
		if (msg.equals("1") || msg.equals("2")){// 如果等于1或者等于2，删除流程实例
			ResponseObject ro = ResponseObject.newOkResponse();// 获取返回数据
			String sql = "DELETE FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID='" + instance.getId() + "'";
			DBSql.update(sql);
			SDK.getProcessAPI().delete(instance, me);// 删除流程实例
			ro.put("msg", "注销成功!");// 返回状态
			return ro.toString();
		} else {
			ResponseObject ro = ResponseObject.newErrResponse();
			ro.put("msg", "注销失败!");// 返回状态
			return ro.toString();
		}
	}
	
	/**
	 * 标准化组织加入审批意见
	 * @author yujh
	 * @date 
	 */
	public String queryOperatelog_orgjoin(String processId) {
		List<TaskCommentModel> list_parent = new ArrayList<TaskCommentModel>();//父流程实例ID审批意见
		List<TaskCommentModel> list_sub = new ArrayList<TaskCommentModel>();//子流程实例ID审批意见
		List<RowMap> list_subprocessId = new ArrayList<RowMap>();//子流程实例ID集合
		list_subprocessId = DBSql.getMaps("SELECT ID FROM WFC_PROCESS WHERE PARENTPROCESSINSTID= ? ORDER BY STARTTIME ASC", new Object[]{processId});
		list_parent = SDK.getProcessAPI().getCommentsById(processId);//当前流程审批意见
		if(list_subprocessId != null && list_subprocessId.size() > 0){
			for(int i = 0;i < list_subprocessId.size();i++){
				String sub_processId = list_subprocessId.get(i).getString("ID");
				List<TaskCommentModel> list_subprocess = SDK.getProcessAPI().getCommentsById(sub_processId);
				list_sub.addAll(list_subprocess);
			}
		}
		list_parent.addAll(list_sub);
		Collections.sort(list_parent,new Comparator<TaskCommentModel>() {
			@Override
			public int compare(TaskCommentModel model1, TaskCommentModel model2) {
				if(model1.getCreateDate().before( model2.getCreateDate())){
					return -1;
				}else{
					return 1;
				}
			}
		});
		//拼接html
		StringBuffer html = new StringBuffer();
		List<Map<String,String>> sqbs_comment = new ArrayList<Map<String,String>>();//申请部所审批意见
		List<Map<String,String>> phbs_comment = new ArrayList<Map<String,String>>();//配合部所审批意见
		List<Map<String,String>> bzhgljkr_comment = new ArrayList<Map<String,String>>();//标准化管理家口人审批意见
		List<Map<String,String>> xgbshq_comment = new ArrayList<Map<String,String>>();//相关部所审批意见
		List<Map<String,String>> zbjsb_comment = new ArrayList<Map<String,String>>();//总部技术部审批意见
		List<Map<String,String>> hqdw_comment = new ArrayList<Map<String,String>>();//会签单位审批意见
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
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String createData = sdf.format(createTimestamp);
					String createUserid = list_parent.get(i).getCreateUser();//留言人账号
					String userName = "";
					String zwdj = "";//职位等级
					UserModel usermodel = SDK.getORGAPI().getUser(createUserid);
					if(usermodel != null){
						userName = usermodel.getUserName();
						zwdj = usermodel.getExt1();
					}
					TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskId);
					String activityDefid = taskInstance.getActivityDefId();
					String deptname = list_parent.get(i).getDepartmentName();
					Map<String,String> map = new HashMap<String,String>();
					map.put("activityName", activityName);
					map.put("msg", msg);
					map.put("userName", userName);
					map.put("zwdj", zwdj);
					map.put("deptname", deptname);
					map.put("createData", createData);
					if(StandardizationConstant.SQBS_STEP_LIST.toString().contains(activityDefid)){
						sqbs_comment.add(map);
					}else if(StandardizationConstant.PHBS_STEP_LIST.toString().contains(activityDefid)){
						phbs_comment.add(map);
					}else if(StandardizationConstant.BZHGLJKR_STEP_LIST.toString().contains(activityDefid)){
						bzhgljkr_comment.add(map);
					}else if(StandardizationConstant.XGBS_STEP_LIST.toString().contains(activityDefid)){
						xgbshq_comment.add(map);
					}else if(StandardizationConstant.ZBJSB_STEP_LIST.toString().contains(activityDefid)){
						zbjsb_comment.add(map);
					}else if(StandardizationConstant.HQDW_STEP_LIST.toString().contains(activityDefid)){
						hqdw_comment.add(map);
					}
				}

			}
		}
		if(sqbs_comment != null && sqbs_comment.size() > 0){
			html.append(OptionUtil.optionMosaic(sqbs_comment, "申请单位意见"));
		}
		if(phbs_comment != null && phbs_comment.size() > 0){
			html.append(OptionUtil.optionMosaic(phbs_comment, "配合部所意见"));
		}
		if(bzhgljkr_comment != null && bzhgljkr_comment.size() > 0){
			html.append(OptionUtil.optionMosaic(bzhgljkr_comment, "标准化管理接口人意见"));
		}
		if(xgbshq_comment != null && xgbshq_comment.size() > 0){
			html.append(OptionUtil.optionMosaic(xgbshq_comment, "相关部所意见"));
		}
		if(zbjsb_comment != null && zbjsb_comment.size() > 0){
			html.append(OptionUtil.optionMosaic(zbjsb_comment, "总部技术部意见"));
		}
		if(hqdw_comment != null && hqdw_comment.size() > 0){
			html.append(OptionUtil.optionMosaic(hqdw_comment, "会签单位意见"));
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
	 * 查询该流程实例的审核记录
	 * @param processId
	 * @return
	 */
	public String queryCommentByProcessInst(UserContext me ,String processId) {
		JSONArray array =new JSONArray();
		String sql ="SELECT ACTIONNAME FROM WFC_COMMENT  WHERE PROCESSINSTID =? AND CREATEUSER = ?";
		List<RowMap> maps = DBSql.getMaps(sql, new Object[]{processId,me.getUID()});
		if(null != maps &&maps.size()>0){
			for (RowMap rowMap : maps) {
				array.add(rowMap.getString("ACTIONNAME"));
			}
		}
		ResponseObject rsobj = ResponseObject.newOkResponse(array.toString());
		return rsobj.toString();
	}
}
