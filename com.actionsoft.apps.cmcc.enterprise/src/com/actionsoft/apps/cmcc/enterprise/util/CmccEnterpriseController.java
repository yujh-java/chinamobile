package com.actionsoft.apps.cmcc.enterprise.util;

import org.json.JSONException;

import com.actionsoft.apps.cmcc.enterprise.web.CmccEnterpriseWeb;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
/**
 * 企标管理cmd公共类
 * @author chenxf
 *
 */
@Controller
public class CmccEnterpriseController{

	/**
	 * 注销cmd
	 * @param uc
	 * @param processInstId
	 * @param processDefid
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc_enterprise_logoff")
	public String logoff(UserContext uc, String processInstId, String processDefid){
		CmccEnterpriseWeb web = new CmccEnterpriseWeb(uc);
		return web.logOff(processInstId, processDefid);
	}
	/**
	 * 获取审批意见事件
	 * @param uc
	 * @param processid
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc_enterpriseVirtual")
	public String getOpinion(UserContext uc, String processid){
		CmccEnterpriseWeb web = new CmccEnterpriseWeb(uc);
		return web.getOpinion(processid);
	}
	/**
	 * 获取审批意见事件
	 * @param uc
	 * @param processid
	 * wuxx
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc_enterpriseVirtualnew")
	public String getOpinionnew(UserContext uc, String processid){
		CmccEnterpriseWeb web = new CmccEnterpriseWeb(uc);
		return web.getOpinionnew(processid);
	}
	/**
	 * 判断是否为研究院
	 * @param uc
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc_enterprise_isResearchInstitute")
	public String isResearchInstitute(UserContext uc){
		//返回
		ResponseObject rsobj = ResponseObject.newOkResponse();
		//当前人部门ID
		String pathDeptid = uc.getDepartmentModel().getPathIdOfCache();
		System.err.println("-----当前人部门ID:"+pathDeptid+"---------");
		System.err.println("-----研究院部门ID："+CmccCommon.list_pathDeptId_yjy.toString()+"----------");
		
		//判断是否是集团研究院，如果是，则返回true
		if(CmccCommon.list_pathDeptId_yjy.contains(pathDeptid)){
			System.err.println("------研究院。。-------");
			rsobj.put("msg", "true");
		}else{
			System.err.println("------非研究院。。-------");
			rsobj.put("msg", "false");
		}
	    return rsobj.toString();
	}
	/**
	 * 获取获取节点父任务的状态
	 * @param me
	 * @param parentTaskId
	 * @param processId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc_enterprise_queryParentActivityId")
	public String queryParentActivityId(UserContext me,String parentTaskId,String processId){
		CmccEnterpriseWeb web = new CmccEnterpriseWeb(me);
		return web.getParentTaskState(parentTaskId);
	}
	/**
	 * 同一部所下只能选取一个部所领导
	 * @author chenxf
	 * @date   2018年7月25日 下午3:26:51
	 * @param me
	 * @param participants
	 * @return
	 */
	@Mapping("com.actionsoft.apps.enterprise_DemandDepartmentSign")
	public String demandDepartmentSign(UserContext me,String participants,String rolename){
		CmccEnterpriseWeb web = new CmccEnterpriseWeb(me);
		return web.demandDepartmentSign(me,participants,rolename);
	}
	/**
	 * 添加已办表单为静态页面 
	 * @author chenxf
	 * @date   2018年9月19日 下午5:58:46
	 * @param me
	 * @param bodyHTML
	 * @param headHTML
	 * @param processInstId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.enterprise.addTaskComplete_StaticForm")
	public String addTaskCompleteStaticForm(UserContext me,String bodyHTML, String headHTML,
			String processInstId,String type,String taskId){
		CmccEnterpriseWeb web = new CmccEnterpriseWeb(me);
		return web.addTaskCompleteStaticForm(me, bodyHTML, headHTML, processInstId, type, taskId);
	}
	/**
	 * 打开快照表单
	 * @author chenxf
	 * @date   2018年9月30日 上午9:49:01
	 * @param uid
	 * @param processInstId
	 * @param taskInstId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.common.openFormSnapshot")
	public String openFormSnapshot(UserContext me,String processInstId,String taskInstId){
		CmccEnterpriseWeb web = new CmccEnterpriseWeb(me);
		return web.openFormSnapshot(me,  processInstId, taskInstId);
	}
	/**
	 * 获取复审流程审批意见事件
	 * @param uc
	 * @param processid
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc.enterprise.FSVirtual")
	public String getFSOpinion(UserContext uc, String processid,String processDefid){
		CmccEnterpriseWeb web = new CmccEnterpriseWeb(uc);
		return web.getFSOpinion(processid, processDefid);
	}
	/**
	 * 获取企标报批流程审批意见事件
	 * @param uc
	 * @param processid
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc.enterprise.FSVirtualNew")
	public String getFSOpinionnew(UserContext uc, String processid,String processDefid){
		CmccEnterpriseWeb web = new CmccEnterpriseWeb(uc);
		return web.getFSOpinionnew(processid, processDefid);
	}
	/**
	 * 获取企标流程的审批意见事件
	 * 查询意见表中是否有技术部和需求部门会签这个节点，如果有，返回的时候时候就显示提交结束这个路径，否则隐藏提交结束路径
	 * @param uc
	 * @param processid
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc.enterprise.SsVirtual")
	public String getQblc(UserContext uc, String processid,String processDefid){
		CmccEnterpriseWeb web = new CmccEnterpriseWeb(uc);
		return web.getQblc(processid, processDefid);
	}
	/**
	 * 同一部门接口人不能选两个或两个以上的人员
	 * @author wuxx
	 * @date   2019年05月10日 
	 * @param participants
	 * @return
	 * @throws JSONException
	 */
	@Mapping("com.actionsoft.apps.cmcc.enterprise_QbDemandDepartmentSign")
	public String qbdemandDepartmentSign(UserContext me,String participants,String params) throws JSONException{
		CmccEnterpriseWeb web = new CmccEnterpriseWeb(me);
		
		return web.qbdemandDepartmentSign(me,participants,params);
	}
	
}
