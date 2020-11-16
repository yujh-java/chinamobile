package com.actionsoft.apps.report.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.apps.report.ReportConst;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.model.def.ActivityModel;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 定时器任务，向中间表中保存节点信息
 * 
 * @author zhaoxs
 * @date 2017-09-15
 * 
 */
public class SaveNoteInfor implements Job {
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		int s = DBSql.update("DELETE FROM BO_ACT_NOTEID_DATA");
		List<RowMap> list = DBSql.getMaps("SELECT * FROM BO_ACT_PROCESS_DATA");
		for (int i = 0; i < list.size(); i++) {
			String str = SDK.getRepositoryAPI().getActivityExtendAttribute(list.get(i).getString("PROCESSDEFNID"));
			JSONArray json = JSONArray.fromObject(str); // 首先把字符串转成 JSONArray 对象
			String processname = list.get(i).getString("PROCESSNAME");
			String ismain = list.get(i).getString("ISMAIN");
			String processdefinid = list.get(i).getString("PROCESSDEFNID");
			String processtype = list.get(i).getString("PROCESSTYPE");
			if (json.size() > 0) {
				for (int j = 0; j < json.size(); j++) {
					JSONObject job = json.getJSONObject(j); // 遍历
															// jsonarray数组，把每一个对象转成
															// json对象
					String noteid = job.getString("id"); // 得到 节点ID
					String notename = job.getString("name");// 获取节点名称
					String createActivityid = "";
					if (ReportConst.subProcessDefid_activityid.containsKey(processdefinid)) {
						createActivityid = ReportConst.subProcessDefid_activityid.get(processdefinid);
					}
					ActivityModel amodel = SDK.getRepositoryAPI().getActivityModel(processdefinid, noteid);
					int noteno = 0 ; 
					if(amodel.getNo()==0){
						if(noteid.equals(ReportConst.lxphSub_noteid)){
							noteno = 12;
						}else if(noteid.equals(ReportConst.lxxqSub_noetid)){
							noteno = 14;
						}else if(noteid.equals(ReportConst.jxphSub_noteid)){
							noteno = 16;
						}else if(noteid.equals(ReportConst.jxxqSub_noteid)){
							noteno = 17;
						}
					}else{
						noteno=amodel.getNo();
					}
					BO bo = new BO();
					bo.set("PROCESSDEFINID", processdefinid);
					bo.set("NOTEID", noteid);
					bo.set("NOTENAME", notename);
					bo.set("PROCESSNAME", processname);
					bo.set("ISMAIN", ismain);
					bo.set("CREATEACTIVITYID", createActivityid);
					bo.set("NOTENO",noteno);
					bo.set("PROCESSTYPE", processtype);
					SDK.getBOAPI().createDataBO("BO_ACT_NOTEID_DATA", bo, UserContext.fromUID("admin"));
				}
			}

		}

	}

}
