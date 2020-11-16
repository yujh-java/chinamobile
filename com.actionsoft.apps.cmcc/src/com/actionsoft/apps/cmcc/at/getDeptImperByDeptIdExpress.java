package com.actionsoft.apps.cmcc.at;
/**
 * 查询流程指定节点、部门的参与者,过滤历史参与者（子流程回退）.立项、结项：牵头研发机构接口人、总部研发机构接口人节点
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;
import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.exception.AWSExpressionException;
import com.actionsoft.sdk.local.SDK;

public class getDeptImperByDeptIdExpress extends AbstExpression {

	public getDeptImperByDeptIdExpress(ExpressionContext atContext,
			String expressionValue) {
		super(atContext, expressionValue);
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		String processId = getParameter(expression, 1);//流程实例ID
		String activityId = getParameter(expression, 2);//流程节点ID
		String roleId = getParameter(expression, 3);//角色ID
		String deptID = getParameter(expression, 4);//部门ID

		if(!UtilString.isEmpty(activityId)){
			//查询子流程结束记录节点参与者
			List<BO> list_bo = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSHANDLE").addQuery("PROCESSID = ", processId)
					.addQuery("ACTIVITYID = ", activityId).list();
			if(list_bo != null && list_bo.size() > 0){
				String handlers = list_bo.get(0).getString("HANDLERS");
				return handlers;
			}
		}
		StringBuffer sb = new StringBuffer();
		//按部门逐级查询对应角色用户
		if(!UtilString.isEmpty(deptID)){
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
		}else{
			List<RowMap> list_row = DBSql.getMaps("SELECT USERID FROM ORGUSERMAP WHERE ROLEID=?", new Object[]{roleId});
			if(list_row != null && list_row.size() > 0){
				for(int i = 0;i<list_row.size();i++){
					String userid = list_row.get(i).getString("USERID");
					sb.append(userid+" ");
				}
			}
		}
		return sb.toString().trim();
	}
}
