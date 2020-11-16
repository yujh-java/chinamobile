package com.actionsoft.apps.cmcc.integration.aslp;

/*
 * 调用Aslp接口保存接口信息
 * @author Zhaoxs
 * @date  2017-06-27
 * 
 * **/
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.actionsoft.apps.cmcc.integration.util.CmccUrlUtil;
import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.apps.resource.interop.aslp.Meta;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

import net.sf.json.JSONObject;

public class SPMSProcessDataAslp implements ASLP {
	@Override
	@Meta(parameter = { "name:'mapMsg',desc:'保存接口信息'" })
	public ResponseObject call(Map<String, Object> param) {
		String deptId = "";
		String researchId = "";
		String taskName = "";
		String planCompleteTime = "";
		String manager = "";
		String title = "";
		String research = "";
		String research_dept = "";
		String research_deptid = "";
		ResponseObject res = ResponseObject.newOkResponse();
//		String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "PROJECT_DATA_WRITEBACK");
		String process_type = (String) param.get("processtype");
		/*
		 * chenxf modify
		 */
		String url = "";
		//判断流程类型是否为研究院，从而调用不同的接口参数
		if(process_type.indexOf("cmri") != -1){
			url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "PROJECT_DATA_WRITEBACK_CMRI");
		}else{
			url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "PROJECT_DATA_WRITEBACK");
		}
		String process_id = (String) param.get("processid");
		String taskid = (String) param.get("taskid");
		String userid = (String) param.get("userid");
		url = url + "?type=" + process_type + "&process_id=" + process_id;// 需求部门接口地址
		String str = CmccUrlUtil.get(url);
		SDK.getLogAPI().getLogger(this.getClass()).error("str="+str);
		Connection conn = null;
		try{
			conn = DBSql.open();
			JSONObject resultJson = JSONObject.fromObject(str);
			JSONObject datajson = resultJson.getJSONObject("data");
			int code = (Integer) datajson.get("code");
			String alldata = datajson.getString("data");
			
			if (code == 1) {
				JSONObject xqbmdata = JSONObject.fromObject(alldata);
				if (process_type.equals("hq-general")) {
					String deptNo = xqbmdata.getString("req_ou");
					deptId = DBSql.getString(conn,"SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?", new Object[] { deptNo });
					research = xqbmdata.getString("research_ou");
					researchId = DBSql.getString(conn,"SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?",
							new Object[] { research });
					manager = xqbmdata.getString("research_assignee");
					taskName = xqbmdata.getString("task_name");
					planCompleteTime = xqbmdata.getString("plan_complete_time");
					title = xqbmdata.getString("workflow_name");
					research_dept = xqbmdata.getString("research_dept");
					research_deptid = DBSql.getString(conn,"SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?",
							new Object[] { research_dept });
				} else {
					String deptNo = xqbmdata.getString("req_ou");
					deptId = DBSql.getString(conn,"SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?", new Object[] { deptNo });
					title = xqbmdata.getString("title"); // 获取接口中的流程标题
				}
				// 修改任务、流程标题
				SDK.getTaskAPI().setTitle(taskid, title);
				SDK.getProcessAPI().setTitle(process_id, title);
			} else {
				//获取项目信息
				String msg = datajson.getString("msg");
				SDK.getLogAPI().getLogger(this.getClass()).error("调用spms需求部门信息接口失败,返回信息格式错误。流程实例ID:"+process_id);
				res.put("msg", msg);
			}
			BO bo = new BO();
			List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", process_id).connection(conn).list();
			if (list != null && list.size() > 0) {
				bo = list.get(0);
			}
			bo.set("PROCESSID", process_id);
			bo.set("CREATEUSERID", userid);
			bo.set("TITLE", title);
			bo.set("SUBRESULTXQBM", deptId);// 需求部门id
			bo.set("QTYFJGBM", research);// 研发机构
			bo.set("QTYFJGBMID", researchId);// 研发机构id
			bo.set("PROJECTTYPE", manager);// 任务负责人
			bo.set("PHYFJGBM", taskName);// 任务名称
			bo.set("PHYFJGBMID", planCompleteTime);// 成果提交计划完成时间
			bo.set("QTXQBM", userid);// 发起人
			bo.set("PROCESSTYPE", process_type);
			bo.set("QTXQBMID", research_deptid);
			if (list != null && list.size() > 0) {
				SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSDATA", bo,conn);
			} else {
				SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSDATA", bo, UserContext.fromUID("admin"),conn);
			}
			res.errorCode(code + "");
		}catch(Exception e){
			res.errorCode("0");
			res.msg("获取信息失败,返回信息格式错误");
			SDK.getLogAPI().getLogger(this.getClass()).error("调用spms需求部门信息接口失败,返回信息格式错误。流程实例ID:"+process_id);

		}finally{
			DBSql.close(conn);
		}
		return res;
	}
}
