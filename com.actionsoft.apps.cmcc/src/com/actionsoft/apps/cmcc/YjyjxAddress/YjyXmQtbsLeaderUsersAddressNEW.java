package com.actionsoft.apps.cmcc.YjyjxAddress;
import java.util.ArrayList;

import java.util.List;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.apps.cmcc.yjyjx.YjyCommentCont;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
/**
 * 研究院流程最新版本适用
 * 参与者地址薄过滤事件
 * 项目总监或所主管审核节点---查出所有部所领导及项目总监
 * @author 	chenxf
 * @date 	2018-11-06
 */
public class YjyXmQtbsLeaderUsersAddressNEW implements AddressUIFilterInterface {
	private String activityDefid = "";// 当前任务节点ID
	private String hisTarget = "";// 历史参与者
	private String hisTargetPathDeptID = "";// 历史参与者部门全路径ID
	/*
	 * 获取项目总监的sql
	 */
	public String sql = "select USERID from BO_ACT_ENTERPRISE_ROLEDATA where ROLENAME = '项目总监'";
	public List<String> list_userid = new ArrayList<String>();
	
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径
		if (UtilString.isEmpty(activityDefid)) {
			String taskid = advancedAddressModel.getTaskId();// 任务ID
			TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
			activityDefid = taskInstance.getActivityDefId();// 节点ID
		}
		if (!UtilString.isEmpty(activityDefid) && 
				YjyCommentCont.yfjg_noteid.toString().contains(activityDefid)) {
			// 主管院领导、院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
								.activityDefId(YjyCommentCont.qtbsld_noteid.get(activityDefid))
								.addQuery("PROCESSINSTID = ", processid)
								.userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc().list();
				
				TaskInstance taskInstance_last = list.get(list.size() - 1);
//				String hisTargetDeptId = taskInstance_last.getTargetDepartmentId();
				String target = taskInstance_last.getTarget();
				String hisTargetDeptId = UserContext.fromUID(target).getDepartmentModel().getId();
				
				hisTargetPathDeptID = DepartmentCache.getModel(hisTargetDeptId).getPathIdOfCache();
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			} else {
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			}
		} else{
			// 登录人部门ID全路径
			String deptid = uc.getDepartmentModel().getParentDepartmentId();
			String userpathIdofCache = DepartmentCache.getModel(deptid).getPathIdOfCache();
			if (userpathIdofCache.contains(deptpathIdofCache) 
					|| deptpathIdofCache.contains(userpathIdofCache)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		String userid = model.getUID();
		if (UtilString.isEmpty(activityDefid)) {
			String taskid = advancedAddressModel.getTaskId();// 任务ID
			TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
			activityDefid = taskInstance.getActivityDefId();// 节点ID
		}
		if (!UtilString.isEmpty(activityDefid) && 
				YjyCommentCont.yfjg_noteid.toString().contains(activityDefid)) {
			// 主管院领导审批节点退回
			if (UtilString.isEmpty(hisTarget)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(YjyCommentCont.qtbsld_noteid.get(activityDefid))
						.addQuery("PROCESSINSTID = ", processid)
						.userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc().list();
				TaskInstance taskInstance_last = list.get(list.size() - 1);
				String hisTarget = taskInstance_last.getTarget();
				if (userid.equals(hisTarget)) {
					return true;
				}
			} else {
				if (userid.equals(hisTarget)) {
					return true;
				}
			}
		}  else {
			/*
			 * 获取项目总监角色维护人员集合
			 */
			if(list_userid == null || list_userid.size() == 0){
				List<RowMap> list = DBSql.getMaps(sql);
				for(RowMap map : list){
					String userid_xmzj = map.get("USERID").toString();
					list_userid.add(userid_xmzj);
				}
			}
			String zwmc = model.getExt1(); 
			if(zwmc.equals(CmccConst.user_leave3)) { //所有部所领导
				return true; 
			}else if(list_userid.toString().contains(userid)){//包括项目总监
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
