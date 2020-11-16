package com.actionsoft.apps.cmcc.lxAddress;

/**
 * 立项流程
 * 参与者地址薄过滤事件，领导
 * 处长、副处长(研究院),立项、结项牵头部所领导审核
 * @author nch
 * @date 20170622
 */
import java.util.List;

import com.actionsoft.apps.cmcc.jx.JxActivityConst;
import com.actionsoft.apps.cmcc.labslx.LabsLxActivityIdConst;
import com.actionsoft.apps.cmcc.lx.LxActivityIdConst;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class LX_YjyLeaderUsersAddress implements AddressUIFilterInterface {
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
		if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(LxActivityIdConst.zgylg)
				|| activityDefid.equals(LxActivityIdConst.yzsp))) {
			// 立项-牵头研发机构；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(LxActivityIdConst.xmqtbsld).addQuery("PROCESSINSTID = ", processid)
						.userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc().list();
				TaskInstance taskInstance_last = list.get(list.size() - 1);
				//String hisTargetDeptId = taskInstance_last.getTargetDepartmentId();
				
				//chenxf modify 20181101
				String hisTargetDeptId = UserContext.fromUID(taskInstance_last.getTarget()).getDepartmentModel().getId();
				hisTargetPathDeptID = SDK.getORGAPI().getDepartmentById(hisTargetDeptId).getPathIdOfCache();
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			} else {
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			}
		} else if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(LxActivityIdConst.sub_zgylg)
				|| activityDefid.equals(LxActivityIdConst.sub_yzsp))) {
			// 立项-配合研发机构；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(LxActivityIdConst.sub_qtbsldsh).addQuery("PROCESSINSTID = ", processid)
						.userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc().list();
				TaskInstance taskInstance_last = list.get(list.size() - 1);
//				String hisTargetDeptId = taskInstance_last.getTargetDepartmentId();
				
				//chenxf modify 20181101
				String hisTargetDeptId = UserContext.fromUID(taskInstance_last.getTarget()).getDepartmentModel().getId();
				hisTargetPathDeptID = SDK.getORGAPI().getDepartmentById(hisTargetDeptId).getPathIdOfCache();
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			} else {
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			}
		} else if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(JxActivityConst.zgylg)
				|| activityDefid.equals(JxActivityConst.yzsp))) {
			// 结项-牵头研发机构；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(JxActivityConst.xmqtbsld).addQuery("PROCESSINSTID = ", processid)
						.userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc().list();
				TaskInstance taskInstance_last = list.get(list.size() - 1);
//				String hisTargetDeptId = taskInstance_last.getTargetDepartmentId();
				
				//chenxf modify 20181101
				String hisTargetDeptId = UserContext.fromUID(taskInstance_last.getTarget()).getDepartmentModel().getId();
				hisTargetPathDeptID = SDK.getORGAPI().getDepartmentById(hisTargetDeptId).getPathIdOfCache();
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			} else {
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			}
		} else if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(JxActivityConst.sub_zgylg)
				|| activityDefid.equals(JxActivityConst.sub_yzsp))) {
			// 结项-配合研发机构；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(JxActivityConst.sub_qtbsldsh).addQuery("PROCESSINSTID = ", processid)
						.userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc().list();
				TaskInstance taskInstance_last = list.get(list.size() - 1);
//				String hisTargetDeptId = taskInstance_last.getTargetDepartmentId();

				//chenxf modify 20181101
				String hisTargetDeptId = UserContext.fromUID(taskInstance_last.getTarget()).getDepartmentModel().getId();
				hisTargetPathDeptID = SDK.getORGAPI().getDepartmentById(hisTargetDeptId).getPathIdOfCache();
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			} else {
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			}
		}else if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(LabsLxActivityIdConst.zgyld_activityid)
				|| activityDefid.equals(LabsLxActivityIdConst.yld_activityid))) {
			// 研究院项目立项；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(LabsLxActivityIdConst.qtbsld_activityid).addQuery("PROCESSINSTID = ", processid)
						.userTaskOfWorking().addQuery("TASKSTATE =", 1).orderByEndTime().asc().list();
				TaskInstance taskInstance_last = list.get(list.size() - 1);
//				String hisTargetDeptId = taskInstance_last.getTargetDepartmentId();
				
				//chenxf modify 20181101
				String hisTargetDeptId = UserContext.fromUID(taskInstance_last.getTarget()).getDepartmentModel().getId();
				hisTargetPathDeptID = SDK.getORGAPI().getDepartmentById(hisTargetDeptId).getPathIdOfCache();
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			} else {
				if (hisTargetPathDeptID.contains(deptpathIdofCache)) {
					return true;
				}
			}
		} else {
			String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();// 登录人部门ID全路径
			if (userpathIdofCache.contains(deptpathIdofCache)) {
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
		if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(LxActivityIdConst.zgylg)
				|| activityDefid.equals(LxActivityIdConst.yzsp))) {
			// 立项-牵头研发机构；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTarget)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(LxActivityIdConst.xmqtbsld).addQuery("PROCESSINSTID = ", processid)
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
		} else if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(LxActivityIdConst.sub_zgylg)
				|| activityDefid.equals(LxActivityIdConst.sub_yzsp))) {
			// 立项-配合研发机构；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(LxActivityIdConst.sub_qtbsldsh).addQuery("PROCESSINSTID = ", processid)
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
		} else if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(JxActivityConst.zgylg)
				|| activityDefid.equals(JxActivityConst.yzsp))) {
			// 结项-牵头研发机构；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(JxActivityConst.xmqtbsld).addQuery("PROCESSINSTID = ", processid)
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
		} else if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(JxActivityConst.sub_zgylg)
				|| activityDefid.equals(JxActivityConst.sub_yzsp))) {
			// 结项-配合研发机构；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(JxActivityConst.sub_qtbsldsh).addQuery("PROCESSINSTID = ", processid)
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
		}else if (!UtilString.isEmpty(activityDefid) && (activityDefid.equals(LabsLxActivityIdConst.zgyld_activityid)
				|| activityDefid.equals(LabsLxActivityIdConst.yld_activityid))) {
			// 结项-配合研发机构；主管院领导审批节点退回
			if (UtilString.isEmpty(hisTargetPathDeptID)) {
				String processid = advancedAddressModel.getInstanceId();
				List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI()
						.activityDefId(LabsLxActivityIdConst.qtbsld_activityid).addQuery("PROCESSINSTID = ", processid)
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
			/*String zwmc = model.getExt1();
			if (zwmc.equals(CmccConst.user_leave3)) {
				return true;
			}*/
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
