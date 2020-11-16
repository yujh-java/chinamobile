package com.actionsoft.apps.cmcc.budget.util;

import org.json.JSONException;

import com.actionsoft.apps.cmcc.budget.web.CmccBudgetWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
/**
 * 预算流程cmd控制器
 * @author chenxf
 *
 */
@Controller
public class CmccBudgetControllor {

	/**
	 * 获取审批意见事件
	 * @param uc
	 * @param processid
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc_budgetVirtual")
	public String getOpinion(UserContext uc, String processid,String processDefid){
		CmccBudgetWeb web = new CmccBudgetWeb(uc);
		return web.getOpinion(processid,processDefid);
	}
	/**
	 * 获取审批意见事件
	 * @param uc
	 * @param processid
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc_budget_logoff")
	public String logOff(UserContext uc, String processInstId,String processDefid){
		System.err.println("=======注销。。。。=============");
		CmccBudgetWeb web = new CmccBudgetWeb(uc);
		return web.logOff(processInstId);
	}
	/**
	 * 同一部门接口人不能选两个或两个以上的人员
	 * @author wuxx
	 * @date   2018年10月23日 
	 * @param participants
	 * @return
	 * @throws JSONException
	 */
	@Mapping("com.actionsoft.apps.cmcc.budget_DemandDepartmentSign")
	public String demandDepartmentSign(UserContext me,String participants,String params) throws JSONException{
		CmccBudgetWeb web = new CmccBudgetWeb();
		return web.demandDepartmentSign(me,participants,params);
	}

}
