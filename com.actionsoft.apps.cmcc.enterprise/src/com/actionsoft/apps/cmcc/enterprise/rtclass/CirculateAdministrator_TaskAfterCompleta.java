package com.actionsoft.apps.cmcc.enterprise.rtclass;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.sdk.local.SDK;

/**
 * 企标管理
 * 研发部领导任务办理完毕后，传阅集团企标管理员
 * @author chenxf
 *
 */
public class CirculateAdministrator_TaskAfterCompleta 
	extends ExecuteListener
	implements ExecuteListenerInterface{
	
	@Override
	public String getDescription() {
		return "任务办理完毕后,传阅集团企标管理员";
	}

	@Override
	public void execute(ProcessExecutionContext params) throws Exception {
		/*
		 * 获取流程实例ID、任务实例ID
		 */
		String processId = params.getProcessInstance().getId();
		String taskId = params.getTaskInstance().getId();
		CmccCommon common = new CmccCommon(); 
		//指定传阅人---兼职集团企标管理员的所有人
		String userid = common.getEnterpriseManager("企标管理员","");
		System.err.println("===传阅企标管理员："+userid+"========");
		//审核菜单选择
		boolean menu = SDK.getTaskAPI().
							isChoiceActionMenu(taskId, "送需求单位接口人处理");
		if(userid == null || "".equals(userid)){
			System.err.println("-------企标管理员不存在！！------");
		}else{
			if(menu){
				//任务标题
				String title = params.getTaskInstance().getTitle();
				//调用传阅公共方法
				CmccCommon.
					circulatePeople(processId, taskId, 
									params.getUserContext(), 
									userid, title);
			}
		}
	}
}
