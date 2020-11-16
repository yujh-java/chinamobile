package com.actionsoft.apps.cmcc.testmanagement.job;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ORGAPI;
import com.actionsoft.sdk.local.api.TaskQueryAPI;

/** 
* @author 作者  yujh: 
* @version 创建时间：2019年3月12日 下午4:09:34 
* 类说明  预算系统定时发送邮件
*/
public class BudgetSendEmailJob implements IJob{

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		ORGAPI orgapi = SDK.getORGAPI();
		TaskQueryAPI taskQueryAPI = SDK.getTaskQueryAPI();
		String processDefId="obj_0568104fac974bbc82c08fa5afc4280b";
		String userSQL= "SELECT TARGET FROM WFC_TASK WHERE PROCESSDEFID=? AND TARGET !='' AND TARGET !=? GROUP BY TARGET ";
		List<RowMap> userMaps = DBSql.getMaps(userSQL, new Object[]{processDefId,"admin"});
		if(!userMaps.isEmpty() && userMaps.size()>0){
			//遍历人员
			for (RowMap rowMap : userMaps) {
				String userId= rowMap.getString("TARGET");
				if(orgapi.validateUsers(userId).isEmpty()){//检查账户是否合法
					List<TaskInstance> taskList = taskQueryAPI.processDefId("obj_0568104fac974bbc82c08fa5afc4280b").orderByBeginTime().desc().list();
					if(!taskList.isEmpty() && taskList.size()>0){
						for (TaskInstance taskInstance : taskList) {
							
						}
					}
				}
			}
		}
	}

}
