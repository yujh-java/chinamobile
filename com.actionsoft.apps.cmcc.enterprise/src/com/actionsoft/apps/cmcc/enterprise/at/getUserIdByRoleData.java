package com.actionsoft.apps.cmcc.enterprise.at;

import java.util.List;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSExpressionException;
import com.actionsoft.sdk.local.SDK;
/**
 * at公式--在角色维护表中获取指定角色名、部门ID的人员账号
 * ID：@getUserIdByRoleData(roleName,depdId)
 * 例如：
 * @getUserIdByRoleData(
 * 		@processId(),
 * 		@taskId(),
 * 		activityId,
 * 		parentActivityId,
 * 		企标管理员,
 * 		@sqlValue(select departmentid from ORGUSER where userid = '@processCreateUser')
 * )
 * @author chenxf
 *
 */
public class getUserIdByRoleData extends AbstExpression{

	public UserContext uc;
	public getUserIdByRoleData(ExpressionContext atContext, String expressionValue) {
		super(atContext, expressionValue);
		this.uc = atContext.getUserContext();
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		//流程实例ID
		String processId = getParameter(expression, 1);
		//父任务ID
		String taskId = getParameter(expression, 2);
		//当前节点ID
		String activityId = getParameter(expression, 3);
		//父节点ID
		String parentActivityId = getParameter(expression, 4);
		//角色名
		String rolename = getParameter(expression, 5);
		//部门ID
		String deptId = getParameter(expression, 6);
		System.err.println("=====获取企标管理员对应部门ID："+deptId+"=======");
		
		//获取父任务ID的节点ID是否为集团企标管理员，即是否退回
		String parentActivityId_new = DBSql.getString("select ACTIVITYDEFID from WFC_TASK "
													+ "where id = ?", new Object[]{ taskId });
		//判断获取的父任务节点ID是否等于指定的父任务节点ID--如果不相等，则查询历史记录人
		if(!parentActivityId.equals(parentActivityId_new)){
			
			/**
			 * 获取承担单位企标管理员的历史审批人
			 */
			List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI().
												activityDefId(activityId).
												processInstId(processId).
												userTaskOfWorking().addQuery("TASKSTATE = ", 1).
												orderByEndTime().asc().list();
			
			if(list != null && list.size() > 0){
				//获取历史参与者
				HistoryTaskInstance histask = list.get(list.size() - 1);
				return histask.getTarget();
			}
		}
		CmccCommon common = new CmccCommon(); 
		//返回人员账号
		return common.getEnterpriseManager(rolename,deptId);
	}
}
