package com.actionsoft.apps.cmcc.enterprise.fs;

import java.sql.Connection;
import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/**
 * 企标报批流程
 * 技术部企标管理员节点任务创建后事件，记录技术部企标管理员参与者
 * @author wuxx
 * @date 20190320
 */
public class BP_DeptContact_TaskAfterCreate extends ExecuteListener implements
ExecuteListenerInterface{
	public String getDescription() {
		return "技术部企标管理员节点任务创建后事件，记录技术部企标管理员参与者";
	}

	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		System.err.println(">>>送会签方法进来了");
		String taskId = param.getTaskInstance().getId();//任务ID
		String bindid = param.getProcessInstance().getId();//流程实例ID
		TaskInstance taskInstance = SDK.getTaskAPI().getTaskInstance(taskId);
		String acticityId = taskInstance.getActivityDefId();//当前节点ID
		String userid = taskInstance.getTarget();
		try{
			//查询 (BO_ACT_CMCC_PROCESSHANDLE) 是否存在记录
			System.err.println(">>开始查询");
			List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSHANDLE").addQuery("PROCESSID = ", bindid).addQuery("ACTIVITYID = ", acticityId).list();
			System.err.println(">>>数据长度："+list.size());
			if(list != null && list.size() > 0){//记录表中已存在
				BO bo = list.get(0);
				String HANDLERS = bo.getString("HANDLERS");//参与者
				HANDLERS = HANDLERS + " " + userid;
				bo.set("HANDLERS", HANDLERS.trim());
				SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSHANDLE", bo);//更新记录
			}else{//记录表中不存在此记录
				BO bo = new BO();
				bo.set("PROCESSID", bindid);
				bo.set("ACTIVITYID", acticityId);
				bo.set("HANDLERS", userid.trim());
				SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSHANDLE", bo, param.getUserContext());//记录表中创建新记录
			}
			System.err.println(">>>>>taskId:"+taskId+";userid:"+userid);
			SDK.getTaskAPI().completeTask(taskId, userid,true);//自动完成任务
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			
		}
		
	}

}
