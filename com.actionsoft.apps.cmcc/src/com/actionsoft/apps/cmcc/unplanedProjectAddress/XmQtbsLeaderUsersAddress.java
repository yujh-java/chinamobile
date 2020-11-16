package com.actionsoft.apps.cmcc.unplanedProjectAddress;
/**
 * （自立、国拨计划外项目流程和取消终止流程通用）
 * 参与者地址薄过滤事件，领导
 * 处长、副处长,项目牵头管理部所审核节点
 * @author zhaoxs
 * @date 2017-06-29
 */
import java.util.List;
import com.actionsoft.apps.cmcc.cancel.CancelConst;
import com.actionsoft.apps.cmcc.itemChange.ItemChangeConst;
import com.actionsoft.apps.cmcc.unplanedProject.UnplanedProjectConst;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class XmQtbsLeaderUsersAddress implements AddressUIFilterInterface {
	private String activityDefid = "";// 当前任务节点ID
	private String hisTarget = "";// 历史参与者
	private String hisTargetPathDeptID = "";// 历史参与者部门全路径ID

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径
		if (UtilString.isEmpty(activityDefid)) {
			String taskid = advancedAddressModel.getTaskId();// 任务ID
			TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
			activityDefid = taskInstance.getActivityDefId();// 节点ID
		}
		if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(UnplanedProjectConst.yld_noeteid)
				|| activityDefid.equals(UnplanedProjectConst.zgyld_noteid))) {
			// ；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(UnplanedProjectConst.bsld_noteid).addQuery("PROCESSINSTID = ", processid)
						.userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc().list();
				TaskInstance taskInstance_last = list.get(list.size() - 1);
//				String hisTargetDeptId = taskInstance_last.getTargetDepartmentId();
				
				//chenxf modify 20181101
				String hisTargetDeptId = UserContext.fromUID(taskInstance_last.getTarget()).getDepartmentModel().getId();
				hisTargetPathDeptID = DepartmentCache.getModel(hisTargetDeptId).getPathIdOfCache();
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			} else {
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			}
		} else if (!UtilString.isEmpty(activityDefid)
				&& (activityDefid.equals(CancelConst.zgyld_noteid) || activityDefid.equals(CancelConst.yz_noteid))) {
			// ；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI().activityDefId(CancelConst.qtbsld_noteid)
						.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
						.orderByEndTime().asc().list();
				TaskInstance taskInstance_last = list.get(list.size() - 1);
//				String hisTargetDeptId = taskInstance_last.getTargetDepartmentId();
				
				//chenxf modify 20181101
				String hisTargetDeptId = UserContext.fromUID(taskInstance_last.getTarget()).getDepartmentModel().getId();
				hisTargetPathDeptID = DepartmentCache.getModel(hisTargetDeptId).getPathIdOfCache();
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			} else {
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			}
		} else if (!UtilString.isEmpty(activityDefid)
				&& (activityDefid.equals(ItemChangeConst.zgyld_noteid) || activityDefid.equals(ItemChangeConst.yld_noteid))) {
			// ；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI().activityDefId(ItemChangeConst.xmqtbsld_noteid)
						.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
						.orderByEndTime().asc().list();
				TaskInstance taskInstance_last = list.get(list.size() - 1);
				//String hisTargetDeptId = taskInstance_last.getTargetDepartmentId();
				
				//chenxf modify 20181101
				String hisTargetDeptId = UserContext.fromUID(taskInstance_last.getTarget()).getDepartmentModel().getId();
				hisTargetPathDeptID = DepartmentCache.getModel(hisTargetDeptId).getPathIdOfCache();
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			} else {
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			}
		}
		else{
			String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();// 登录人部门ID全路径
			if (userpathIdofCache.contains(deptpathIdofCache)) {
				return true;
			}
			/*
			 * String userpathIdofCache =
			 * uc.getDepartmentModel().getPathIdOfCache();// 登录人部门ID全路径 if
			 * (userpathIdofCache.contains(deptpathIdofCache)) { return true;
			 * 
			 * }
			 */
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
		if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(UnplanedProjectConst.yld_noeteid)
				|| activityDefid.equals(UnplanedProjectConst.zgyld_noteid))) {
			// 主管院领导审批节点退回
			if (UtilString.isEmpty(hisTarget)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(UnplanedProjectConst.bsld_noteid).addQuery("PROCESSINSTID = ", processid)
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
		} else if (!UtilString.isEmpty(activityDefid)
				&& (activityDefid.equals(CancelConst.zgyld_noteid) || activityDefid.equals(CancelConst.yz_noteid))) {
			// 主管院领导审批节点退回
			if (UtilString.isEmpty(hisTarget)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI().activityDefId(CancelConst.qtbsld_noteid)
						.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
						.orderByEndTime().asc().list();
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
		} else if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(ItemChangeConst.zgyld_noteid)
				|| activityDefid.equals(ItemChangeConst.yld_noteid))) {
			// 主管院领导审批节点退回
			if (UtilString.isEmpty(hisTarget)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(ItemChangeConst.xmqtbsld_noteid).addQuery("PROCESSINSTID = ", processid)
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
		} else {
			return true;
			/*
			 * String zwmc = model.getExt1(); if
			 * (zwmc.equals(CmccConst.user_leave3)) { return true; }
			 */
		}

		return false;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
