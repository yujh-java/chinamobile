package com.actionsoft.apps.cmcc.standardization;

import org.json.JSONException;

import com.actionsoft.apps.cmcc.standardization.web.StandardizationWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
/** 
* @author yujh 
* @version 创建时间：2019年4月11日 下午3:03:56 
* 类说明  标准化控制类
*/

@Controller
public class StandardizationController {
	/**
	 * 注销流程
	 * @param me
	 * @param processInstId
	 * @param processDefid
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc.standardization.DeleteProcessInst")
	public String deleteProcessInst (UserContext me, String processInstId,String processDefid){
		StandardizationWeb web=new StandardizationWeb();
		return web.DeleteProcessInst(me, processInstId, processDefid);
	}
	
	/**
	 * 标准化组织加入审批记录查询
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * nch 20170920
	 */
	@Mapping("com.actionsoft.apps.cmcc.standardization.queryOperatelog_orgjoin")
	public String queryOperatelog_orgjoin(UserContext me,String processId) throws JSONException {
		StandardizationWeb web=new StandardizationWeb();
		return web.queryOperatelog_orgjoin(processId);
	}
	
	/**
	 * 查询该流程实例的审核记录
	 * @param me
	 * @param processId
	 * @return
	 * @throws JSONException
	 */
	@Mapping("com.actionsoft.apps.cmcc.standardization.queryCommentByProcessInst")
	public String queryCommentByProcessInst(UserContext me,String processId) throws JSONException {
		StandardizationWeb web=new StandardizationWeb();
		return web.queryCommentByProcessInst(me ,processId);
	}
}
