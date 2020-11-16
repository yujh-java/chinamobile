package com.actionsoft.apps.cmcc.budget.at;

import java.sql.Connection;
import java.util.List;

import com.actionsoft.apps.cmcc.budget.ys.YsCommentCont;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.exception.AWSExpressionException;
import com.actionsoft.sdk.local.SDK;

/**
 * 财务部审核节点查询该节点参与者
 * @author wxx
 * @date 20180709
 */
public class getDeptImperByDeptIdExpress extends AbstExpression{

	public getDeptImperByDeptIdExpress(ExpressionContext atContext,
			String expressionValue) {
		super(atContext, expressionValue);
	}

	@Override
	public String execute(String expression) {
		String processId = getParameter(expression, 1);//流程实例ID
		String activityId = getParameter(expression, 2);//流程节点ID
		String roleId = getParameter(expression, 3);//角色ID
		String taskid = getParameter(expression, 4);//任务实例ID
		String deptID = getParameter(expression, 5);//部门ID
		
		//返回参与者
		String target = null;
		if(!UtilString.isEmpty(activityId)){
			
			TaskInstance taskinst = SDK.getTaskAPI().getTaskInstance(taskid);//得到任务实例id
			System.err.println("===="+taskid);
			String parentTaskId = taskinst.getParentTaskInstId();//获取父任务id
			//BO bo = SDK.getBOAPI().get("WFH_TASK", parentTaskId);
			System.err.println("furenwuid"+parentTaskId);
			//String  parentActivityid= DBSql.getString("SELECT ACTIVITYDEFID FROM WFH_TASK WHERE id=? ", new Object[]{parentTaskId});
			String  parentActivityid= DBSql.getString("SELECT ACTIVITYDEFID FROM WFC_TASK WHERE id=? ", new Object[]{taskid});
			System.err.println("123344"+parentActivityid);

			/*
			 * 
			 */
			StringBuffer sb = new StringBuffer();
			if(!parentActivityid.contains(YsCommentCont.lx_xz_noteids)){
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(activityId)//YsCommentCont.qtbsld_noteid.get(activityId)
						.addQuery("PROCESSINSTID = ", processId)
						.userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc().list();
				System.err.println("当前节点id"+activityId);

				if (list != null && list.size() > 0) {
					target = list.get(list.size() - 1).getTarget();
					System.err.println("=======返回的target："+target+"==========");
					return target;
				}
				//按部门逐级查询对应角色用户
				if(!UtilString.isEmpty(deptID)){
					System.err.println("正常走流程"+deptID);
					String[] deptidArr = deptID.split(",");
					Connection conn = null;
					try{
						conn = DBSql.open();
						for(int j = 0;j < deptidArr.length;j++){
							String deptmentid = deptidArr[j];
							if(!UtilString.isEmpty(deptmentid)){
								DepartmentModel deptModel = DepartmentCache.getModel(deptmentid);
								boolean bol = true;
								while(bol){
									String departmentid = deptModel.getId();//部门ID
									//查询部门、角色对应的人员
									List<RowMap> list_row = DBSql.getMaps(conn,"SELECT USERID FROM ORGUSERMAP WHERE ROLEID=? AND DEPARTMENTID= ?", new Object[]{roleId,departmentid});
									//list_row不为空，收集userID，跳出循环
									if(list_row != null && list_row.size() > 0){
										for(int i = 0;i<list_row.size();i++){
											String userid = list_row.get(i).getString("USERID");
											sb.append(userid+" ");
										}
										bol = false;
									}else{//list_row为空，向上寻找部门循环
										String parentDeptId = deptModel.getParentDepartmentId();
										//上级部门
										if(!UtilString.isEmpty(parentDeptId) && !"0".equals(parentDeptId)){
											deptModel = DepartmentCache.getModel(parentDeptId);
										}else{
											bol = false;
										}
									}
								}
							}
						}
					}catch(Exception e){
						e.printStackTrace(System.err);
					}finally{
						DBSql.close(conn);
					}
				}
			}else{
				//List<RowMap> list_row = DBSql.getMaps("SELECT USERID FROM ORGUSERMAP WHERE ROLEID=?", new Object[]{roleId});
				System.err.println("进来了");
				List<RowMap> list_row = DBSql.getMaps("SELECT USERID FROM BO_ACT_ENTERPRISE_ROLEDATA WHERE ROLEID=?", new Object[]{roleId});
				if(list_row != null && list_row.size() > 0){
					for(int i = 0;i<list_row.size();i++){
						String userid = list_row.get(i).getString("USERID");
						sb.append(userid+" ");
					}
				}
			}
			return sb.toString().trim();
		}
		return target;
	}
}
