package com.actionsoft.apps.cmcc.testmanagement;

import com.actionsoft.apps.cmcc.testmanagement.web.TestmanagementWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

/** 
* @author yujh 
* @version 创建时间：2019年1月8日 上午10:36:54 
* 测试管理CMD
*/

@Controller
public class TestmanagementController {
	@Mapping("com.actionsoft.apps.cmcc.testmanagement.DeleteProcessInst")
	public String DeleteProcessInst (UserContext me, String processInstId,String processDefid){
		TestmanagementWeb web=new TestmanagementWeb();
		return web.DeleteProcessInst(me, processInstId, processDefid);
	}
	
	/**
	 * 添加已办表单为静态页面 
	 * @author yujh
	 * @date   
	 * @param me
	 * @param bodyHTML
	 * @param headHTML
	 * @param logsHTML
	 * @param processInstId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc.testmanagement.addTaskComplete_StaticForm")
	public String addTaskCompleteStaticForm(UserContext me,String bodyHTML, String headHTML,String logsHTML,
		String processInstId,String type,String taskId){
		TestmanagementWeb web=new TestmanagementWeb();
		return web.addTaskCompleteStaticForm(me, bodyHTML, headHTML, logsHTML, processInstId, type, taskId);
	}
	
	/**
	 * 打开快照表单
	 * @author yujh
	 * @date  
	 * @param me
	 * @param processInstId
	 * @param taskInstId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc.testmanagement.openFormSnapshot")
	public String processId(UserContext me,String processInstId,String taskInstId){
		TestmanagementWeb web=new TestmanagementWeb();
		return web.openFormSnapshot(me,  processInstId, taskInstId);
	}
	
	/**
	 * 获取项目相关信息
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc.testmanagement.getProjectInfo")
	public String getProjectInfo(String processId){
		TestmanagementWeb web=new TestmanagementWeb();
		return web.getProjectInfo(processId);
	}

	/**
	 * 获取人员项目信息
	 * @param userId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc.testmanagement.getUserInfo")
	public String getUserInfo(UserContext me,String userId){
		TestmanagementWeb web=new TestmanagementWeb();
		return web.getUserInfo(userId);
	}


	
}
