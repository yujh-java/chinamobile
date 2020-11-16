package com.actionsoft.apps.cmcc;

/**
 * cmd处理类
 * @author nch
 * @date 20170622
 */
import org.json.JSONException;
import com.actionsoft.apps.cmcc.web.CmccWeb;
import com.actionsoft.apps.cmcc.web.LogOffWeb;
import com.actionsoft.apps.cmcc.web.SubProcessOptionWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;


@Controller
public class CmccController {
	@SuppressWarnings("unused")
	private UserContext me;
	
	/**
	 * 判断登录人所在部门是否是无领导部门
	 * @author nch
	 * @date 2017-7-11
	 * @param mecmcc_querypro
	 * @return
	 * @throws JSONException
	 */
	@Mapping("com.actionsoft.apps.cmcc_checkisNoleader")
	public String checkisNoleader(UserContext me) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.checkisNoleader(me);
	}
	/**
	 * 根据任务ID，获取任务owner
	 * @param me
	 * @param taskId
	 * @return
	 * @throws JSONException
	 */
	@Mapping("com.actionsoft.apps.cmcc_getTaskOwner")
	public String getTaskOwner(UserContext me,String taskId) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.getTaskOwner(taskId);
	}
	/**
	 * 获取父任务状态
	 * @author nch
	 * @date 20170622
	 * @param me
	 * @param parentTaskId
	 * @return
	 * @throws JSONException
	 */
	@Mapping("com.actionsoft.apps.cmcc_getParentTaskState")
	public String getParentTaskState(UserContext me,String parentTaskId) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.getParentTaskState(parentTaskId);
	}
	/**
	 * 查询父任务节点ID
	 * @param me
	 * @param parentTaskId
	 * @return
	 * @throws JSONException
	 * nch 20170622
	 */
	@Mapping("com.actionsoft.apps.cmcc_queryParentActivityId")
	public String queryParentActivityId(UserContext me,String parentTaskId,String processId) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.queryParentActivityId(parentTaskId,processId);
	}
	/**
	 * 根据流程实例ID，获取立项项目信息ID
	 * @param me
	 * @param processInstId
	 * @return
	 * @throws JSONException
	 * nch 20170622
	 */
	@Mapping("com.actionsoft.apps.cmcc_queryXmid")
	public String queryXmid(UserContext me,String processInstId) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.queryXmid(processInstId);
	}
	/**
	 * 根据流程实例ID，获取立项项目配合研发机构
	 * @param me
	 * @param processId
	 * @return
	 * @throws JSONException
	 * nch 20170622
	 */
	@Mapping("com.actionsoft.apps.cmcc_queryProjectPhyfjg")
	public String queryProjectPhyfjg(UserContext me,String processId) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.queryProjectPhyfjg(processId);
	}
	/**
	 * 根据流程实例ID，获取立项项目类型
	 * @param me
	 * @param processId
	 * @return
	 * @throws JSONException
	 * nch 20170622
	 */
	@Mapping("com.actionsoft.apps.cmcc_queryProjectType")
	public String queryProjectType(UserContext me,String processId) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.queryProjectType(processId);
	}

	/**
	 * 根据父任务id，获得上一节点ID
	 * @param me
	 * @param parentTaskId
	 * @return
	 * @throws JSONException
	 * nch 20170622
	 */
	@Mapping("com.actionsoft.apps.cmcc_getParentActivetyid")
	public String getParentActivetyid(UserContext me,String parentTaskId) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.getParentActivetyid(parentTaskId);
	}
	/**
	 * 任务单步收回
	 * @param me
	 * @param parentTaskId
	 * @return
	 * @throws JSONException
	 * nch 20170622
	 */
	@Mapping("com.actionsoft.apps.cmcc_undoTask")
	public String undoTask(UserContext me,String parentTaskId) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.undoTask(me.getUID(),parentTaskId);
	}
	/**
	 * 判断是否允许撤销
	 * @param me
	 * @param processInstId
	 * @return
	 * @throws JSONException
	 */
	@Mapping("com.actionsoft.apps.cmcc_checkIsundoTask")
	public String checkIsundoTask(UserContext me,String processInstId) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.checkIsundoTask(processInstId,me);
	}
	/**
	 * 查询审批意见(成果提交)
	 * @param me
	 * @param orderno
	 * @param massorderno
	 * @return
	 * @throws JSONException
	 * nch 20170622
	 */
	@Mapping("com.actionsoft.apps.cmcc_queryOperatelog")
	public String queryOperatelog(UserContext me,String processid) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog(processid);
	}
	/**
	 * 根据流程实例ID，定义ID，获取审批记录
	 * 流程跟踪(未使用)
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * nch 20170622
	 */
	@Mapping("com.actionsoft.apps.cmcc_queryOption")
	public String queryOption(UserContext me,String processId,String processDefid) throws JSONException {

		//		if(processDefid.equals("obj_8b5cc3f8cac84dcc98ce2dbaf57061e8") ||
		//				processDefid.equals("obj_2569912f23c24f48b32ce4bef75e97eb") ||
		//					processDefid.equals("obj_fe69beabb5c541dda3c6cfd01a967692") ||
		//					  processDefid.equals("obj_f3b1c21c0d4640548bb6ad7c1578e2d5") ||
		//						processDefid.equals("obj_b1e878e45d0e4172afb9dc158152da66") ||
		//					      processDefid.equals("obj_7732d3a4633b41d3bfb781b87710f44a")){
		//			SubProcessOptionWeb web = new SubProcessOptionWeb();
		//			return web.getOptionHtmlForLx(processId,processDefid);
		//		}else{
		//			CmccWeb web = new CmccWeb();
		//			return web.getOptionHtml(processId,processDefid);
		//		}
		SubProcessOptionWeb web = new SubProcessOptionWeb();
		return web.getOptionHtmlForLx(processId,processDefid);
	}

	/**
	 * 根据用户，任务实例id判断是否为当前任务实例待办人
	 * @param me
	 * @param currentOpUser  用户
	 * @param taskInstId   任务实例id
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc_islogoff")
	public  String isPresentUser(UserContext me,String currentOpUser,String taskInstId){
		LogOffWeb  logOffWeb = new LogOffWeb();
		return logOffWeb.isPresentUser(me, currentOpUser, taskInstId);
	}
	/**
	 * 根据用户上下文，流程定义id，流程实例id，注销当前流程实例
	 * @param me
	 * @param processInstId  流程实例id
	 * @param processDefid   流程定义id
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc_logoff")
	public String DeleteProcessInst (UserContext me, String processInstId,String processDefid){
		
		LogOffWeb logOffWeb = new LogOffWeb();
		return logOffWeb.DeleteProcessInst(me, processInstId, processDefid);

	}
	/**
	 * 结项任务审批记录查询
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * nch 20170622
	 */
	@Mapping("com.actionsoft.apps.cmcc_queryOperatelog_jx")
	public String queryOperatelog_jx(UserContext me,String processId,String processDefid) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_jx(processId,processDefid);
	}

	/**
	 * 集团立项任务审批记录查询
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * nch 20170622
	 */
	@Mapping("com.actionsoft.apps.cmcc_queryOperatelog_lx")
	public String queryOperatelog_lx(UserContext me,String processId,String processDefid) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_lx(processId,processDefid);
	}
	/**
	 * 研究院立项任务审批记录查询
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * nch 20170920
	 */
	@Mapping("com.actionsoft.apps.cmcc_queryOperatelog_yjylx")
	public String queryOperatelog_yjylx(UserContext me,String processId) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_yjylx(processId);
	}
	
	/**
	 * 根据流程实例ID，定义ID，获取结题评分审批记录
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * zhaoxs 2017-06-22
	 */
	@Mapping("com.actionsoft.apps.cmcc_TitleScore")
	public String queryOperatelog_TitleScore(UserContext me,String processid) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_TitleScore(processid);
	}

	/**
	 * 根据流程实例ID，定义ID，获取一般委托项目启动反馈审批记录
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * @author zhaoxs
	 * @date  2017-06-22
	 */
	@Mapping("com.actionsoft.apps.cmcc_ProjectFeedback")
	public String queryOperatelog_ProjectFeedback(UserContext me,String processid) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_ProjectFeedback(processid);
	}
	/**
	 * 获取获取一般委托项目启动反馈审批记录
	 * @param uc
	 * @param processid
	 * @author wuxx
	 * @date  2019-06-19
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc_FSVirtualNew")
	public String getFSOpinionnew(UserContext uc, String processid,String processDefid){
		CmccWeb web = new CmccWeb(uc);
		return web.getFSOpinionnew(processid, processDefid);
	}

	/**
	 * 根据流程实例ID,查询研发机构和任务名称
	 * @param me
	 * @param processId
	 * @return
	 * @throws JSONException
	 * nch 20170622
	 */
	@Mapping("com.actionsoft.apps.cmcc_QueryYFJG")
	public String queryOperatelog_QueryYFJG(UserContext me,String processId) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_QueryYFJG(processId);
	}
	/**
	 * 根据流程实例ID,查询研发机构和任务名称
	 * @param me
	 * @param processId
	 * @return
	 * @throws JSONException
	 * wuxx 20190625
	 */
	@Mapping("com.actionsoft.apps.cmcc_QueryYFJGQCR")
	public String queryOperatelog_QueryYFJGQCR(UserContext me,String processId) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_QueryYFJGQCR(processId);
	}


	/**
	 * 根据流程实例ID,查询项目信息发布状态
	 * @param me
	 * @param processId
	 * @return
	 * @throws JSONException
	 * nch 20170622
	 */
	@Mapping("com.actionsoft.apps.cmcc_QueryProjectInfor")
	public String queryOperatelog_QueryProjectInfor(UserContext me,String processId) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_QueryProjectInfor(processId);
	}
	/**
	 * 重大重点项目立项在办理下一步弹出窗口选项目管理人员,通过接受项目管理人员返回维护表对应的信息
	 * @param me
	 * @param projectManagerList 项目管理人员
	 * @return
	 * @throws JSONException
	 * sunk 2017-05-26
	 */
	@Mapping("com.actionsoft.apps.cmcc_queryProjectManagerMaintenanceInfo")
	public String queryProjectManagerMaintenanceInfo(UserContext me,String projectManagerList) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryProjectManagerMaintenanceInfo(projectManagerList);
	}
	
	/**
	 * 根据任务实例id,调用完成方法
	 * @param me
	 * @param taskInstId
	 * @param ActionName
	 * @param Msg
	 * @return
	 * @throws JSONException
	 * zhaoxs 2017-06-22
	 */
	@Mapping("com.actionsoft.apps.cmcc_completeTask")
	public String queryOperatelog_CompleteTask(UserContext me,String taskInstId,String ActionName,String Msg) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_CompleteTask(me,taskInstId,ActionName,Msg);
	}
	

	/**
	 * 在流程流转的时候给相关人发送通知
	 * @param processInstId 流程bindid
	 * @param taskInstId 任务ID
	 * @param reader 阅读人
	 * @return
	 * sunk 2017-06-01
	 */
	@Mapping("com.actionsoft.apps.cmcc_sendNotification")
	public String sendNotification(UserContext me,String processInstId,String taskInstId,String reader) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.sendNotification(me,processInstId,taskInstId,reader);
	}
	
	/**
	 * 根据任务实例id，判断是否有子任务
	 * @param me
	 * @param taskInstid
	 * @return
	 * @throws JSONException
	 *  zhaoxs 2017-06-22
	 */
	@Mapping("com.actionsoft.apps.cmcc_querySubTask")
	public String querySubTask(UserContext me,String taskInstId) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.querySubTask(taskInstId);
	}
	
	/**
	 * 根据流程实例ID，定义ID，获取自立、国拨计划外项目审批记录
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * zhaoxs 2017-07-18
	 */
	@Mapping("com.actionsoft.apps.cmcc_Unplaned")
	public String queryOperatelog_Unplaned(UserContext me,String processid) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_Unplaned(processid);
	}
	
	/**
	 * 根据流程实例ID，定义ID，获取取消终止流程审批记录
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * zhaoxs 2017-07-18
	 */
	@Mapping("com.actionsoft.apps.cmcc_Cancel")
	public String queryOperatelog_Cancel(UserContext me,String processid) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_Cancel(processid);
	}
	
	/**
	 * 根据流程实例ID，定义ID，获取项目变更流程审批记录
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * zhaoxs 2017-07-25
	 */
	@Mapping("com.actionsoft.apps.cmcc_Change")
	public String queryOperatelog_(UserContext me,String processid) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_Change(processid);
	}
	
	/**
	 * 根据流程实例ID，定义ID，获取合同计提流程审批记录
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * zhaoxs 2017-07-25
	 */
	@Mapping("com.actionsoft.apps.cmcc_Contract")
	public String queryOperatelog_Contract(UserContext me,String processid) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_contract(processid);
	}
	
	/**
	 * 根据流程实例ID，定义ID，获取预算调整流程审批记录
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * zhaoxs 2017-08-24
	 */
	@Mapping("com.actionsoft.apps.cmcc_Budget")
	public String queryOperatelog_Budget(UserContext me,String processid) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_budget(processid);
	}
	
	/**
	 * 根据流程实例ID，定义ID，获取虚拟收入确认审批记录
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * zhaoxs 2017-09-07
	 */
	@Mapping("com.actionsoft.apps.cmcc_Virtual")
	public String queryOperatelog_Virtual(UserContext me,String processid) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_virtual(processid);
	}
	
	/**
	 * 根据流程实例ID，获取合同url
	 * @param me
	 * @param processInstId
	 * @return
	 * @throws JSONException
	 * zhaoxs 20170922
	 */
	@Mapping("com.actionsoft.apps.cmcc_queryURL")
	public String queryUrl(UserContext me,String processInstId) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.queryurl(processInstId);
	}
	
	
	/**
	 * 根据流程实例ID，定义ID，获取研究院内结项审批记录
	 * @param me
	 * @param processId
	 * @param processDefid
	 * @return
	 * @throws JSONException
	 * pcj 2017-09-22
	 */
	@Mapping("com.actionsoft.apps.cmcc_YjyJx")
	public String queryOperatelog_YjyJx(UserContext me,String processId) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.queryOperatelog_yjyjx(processId);
	}
	
	/**
	 * 根据流程实例ID,查询是否为专业公司
	 * @param me
	 * @param processId
	 * @return
	 * @throws JSONException
	 * zhaoxs 2017-12-21
	 */
	@Mapping("com.actionsoft.apps.cmcc_QueryPro")
	public String query_QueryPro(UserContext me,String processId) throws JSONException {
		CmccWeb web = new CmccWeb();
		return web.query_Querypro(processId);
	}
	/**
	 * 获取当前流程所流程的所有节点的节点ID
	 * @author chenxf
	 * @date   2018年4月12日 下午2:48:04
	 * @param me
	 * @param processId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.cmcc.getProcessAllStepId")
	public String getProcessAllStepId(UserContext me,String processId){
		CmccWeb web = new CmccWeb();
		return web.getProcessAllStepId(me,processId);
	}
	/**
	 * 
	 * @author chenxf
	 * @date   2018年4月12日 下午2:47:22
	 * @param me
	 * @param processId
	 * @return
	 * @throws JSONException
	 */
	@Mapping("com.actionsoft.apps.cmcc_pathGroup")
	public String queryPathGroup(UserContext me,
					String processType,String processId) throws JSONException{
		CmccWeb web = new CmccWeb(me);
		return web.queryPathGroup(me,processType,processId);
	}
	/**
	 * 
	 * @author wuxx
	 * @date   20190417
	 * @param me
	 * @param processId
	 * @return
	 * @throws JSONException
	 */
	@Mapping("com.actionsoft.apps.cmcc_pathYjyGroup")
	public String queryYjyPath(UserContext me,
					String processType,String processId) throws JSONException{
		CmccWeb web = new CmccWeb(me);
		return web.queryYjyPath(me,processType,processId);
	}
	/**
	 * 同一部门接口人不能选两个或两个以上的人员
	 * @author chenxf
	 * @date   2018年6月7日 下午4:53:53
	 * @param me
	 * @param participants
	 * @return
	 * @throws JSONException
	 */
	@Mapping("com.actionsoft.apps.cmcc_DemandDepartmentSign")
	public String demandDepartmentSign(UserContext me,String participants,String params) throws JSONException{
		CmccWeb web = new CmccWeb();
		return web.demandDepartmentSign(me,participants,params);
	}
}
