package com.actionsoft.apps.cmcc.lxAddress;

/**
 * 立项流程
 * 立项、结项配合研发机构项目经理地址薄事件
 * @author nch
 * @date 20170622
 */
import java.util.List;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.apps.cmcc.jx.JxActivityConst;
import com.actionsoft.apps.cmcc.lx.LxActivityIdConst;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
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

public class PhjgOrdinaryUsersAddress implements AddressUIFilterInterface {
	private String yfjgDeptId = "";

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径
		if (UtilString.isEmpty(yfjgDeptId)) {
			String processid = advancedAddressModel.getInstanceId();
			String taskid = advancedAddressModel.getTaskId();// 任务ID
			TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
			String activityDefid = taskInstance.getActivityDefId();// 节点ID
			List<HistoryTaskInstance> list = null;

			if (activityDefid.equals(LxActivityIdConst.sub_phyfjgcsld) // 处室领导退回项目经理
					|| activityDefid.equals(LxActivityIdConst.sub_phyfjgld)// 主管领导退回项目经理
			) {// 立项配合非研发机构
				// 查找处室领导历史办理人
				list = SDK.getHistoryTaskQueryAPI().activityDefId(LxActivityIdConst.sub_phyfjgxmjl)
						.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
						.orderByEndTime().asc().list();
			} else if (activityDefid.equals(LxActivityIdConst.sub_xmglry)
					|| activityDefid.equals(LxActivityIdConst.sub_qtbsldsh)
					|| activityDefid.equals(LxActivityIdConst.sub_xmglbmld)
					|| activityDefid.equals(LxActivityIdConst.sub_jsjl)
					|| activityDefid.equals(LxActivityIdConst.sub_zgylg)) {
				// 立项配合研发机构
				list = SDK.getHistoryTaskQueryAPI().activityDefId(LxActivityIdConst.sub_xmjl)
						.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
						.orderByEndTime().asc().list();
			} else if (activityDefid.equals(JxActivityConst.sub_phyfjgcsld)
					|| activityDefid.equals(JxActivityConst.sub_phyfjgld)) {
				// 结项配合非研发机构
				list = SDK.getHistoryTaskQueryAPI().activityDefId(JxActivityConst.sub_phyfjgxmjl)
						.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
						.orderByEndTime().asc().list();
			} else if (activityDefid.equals(JxActivityConst.sub_cwjs)
					|| activityDefid.equals(JxActivityConst.sub_xmjlqr)
					|| activityDefid.equals(JxActivityConst.sub_qtbsldsh)
					|| activityDefid.equals(JxActivityConst.sub_xmglry)
					|| activityDefid.equals(JxActivityConst.sub_xmglbmld)
					|| activityDefid.equals(JxActivityConst.sub_jsjl)) {
				// 结项配合研发机构
				list = SDK.getHistoryTaskQueryAPI().activityDefId(JxActivityConst.sub_xmjl)
						.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
						.orderByEndTime().asc().list();
			}
			String targetDeptId = "";// 历史办理人账号部门ID
			if (list != null && list.size() > 0) {
				TaskInstance taskInstance_last = list.get(list.size() - 1);
//				targetDeptId = taskInstance_last.getTargetDepartmentId();
				
				//chenxf modify 20181101
				//修改通过账号获取部门ID的方法
				String target = taskInstance_last.getTarget();
//				targetDeptId = DBSql.getString("SELECT DEPARTMENTID FROM ORGUSER WHERE USERID = ?",
//						new Object[] { target });
				
				targetDeptId = UserContext.fromUID(target).getDepartmentModel().getId();
				
			}

			if (UtilString.isEmpty(targetDeptId)) {
				yfjgDeptId = DBSql.getString("SELECT PHYFJGBMID FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
						new Object[] { processid });

			} else {
				yfjgDeptId = targetDeptId;
			}
		}
		DepartmentModel deptModel = DepartmentCache.getModel(yfjgDeptId);
		String userpathIdofCache = "";// 牵头研发机构部门全路径
		if (deptModel != null) {
			userpathIdofCache = deptModel.getPathIdOfCache();
		}
		if (deptpathIdofCache.contains(userpathIdofCache) || userpathIdofCache.contains(deptpathIdofCache)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		String userid = model.getUID();
		String processid = advancedAddressModel.getInstanceId();
		String taskid = advancedAddressModel.getTaskId();// 任务ID
		TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
		String activityDefid = taskInstance.getActivityDefId();// 节点ID
		List<HistoryTaskInstance> list = null;

		if (activityDefid.equals(LxActivityIdConst.sub_phyfjgcsld) // 处室领导退回部门领导
				|| activityDefid.equals(LxActivityIdConst.sub_phyfjgld)// 主管领导退回部门领导
		) {// 立项非研发机构
			// 查找处室领导历史办理人
			list = SDK.getHistoryTaskQueryAPI().activityDefId(LxActivityIdConst.sub_phyfjgxmjl)
					.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
					.orderByEndTime().asc().list();
		} else if (activityDefid.equals(LxActivityIdConst.sub_xmglry)
				|| activityDefid.equals(LxActivityIdConst.sub_qtbsldsh)
				|| activityDefid.equals(LxActivityIdConst.sub_xmglbmld)
				|| activityDefid.equals(LxActivityIdConst.sub_jsjl)
				|| activityDefid.equals(LxActivityIdConst.sub_zgylg)) {
			// 立项研发机构
			list = SDK.getHistoryTaskQueryAPI().activityDefId(LxActivityIdConst.sub_xmjl)
					.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
					.orderByEndTime().asc().list();
		} else if (activityDefid.equals(JxActivityConst.sub_phyfjgcsld)
				|| activityDefid.equals(JxActivityConst.sub_phyfjgld)) {
			// 结项非研发机构
			list = SDK.getHistoryTaskQueryAPI().activityDefId(JxActivityConst.sub_phyfjgxmjl)
					.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
					.orderByEndTime().asc().list();
		} else if (activityDefid.equals(JxActivityConst.sub_cwjs)
				|| activityDefid.equals(JxActivityConst.sub_xmjlqr)
				|| activityDefid.equals(JxActivityConst.sub_qtbsldsh)
				|| activityDefid.equals(JxActivityConst.sub_xmglry)
				|| activityDefid.equals(JxActivityConst.sub_xmglbmld)
				|| activityDefid.equals(JxActivityConst.sub_jsjl)) {
			// 结项研发机构
			list = SDK.getHistoryTaskQueryAPI().activityDefId(JxActivityConst.sub_xmjl)
					.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
					.orderByEndTime().asc().list();
		}
		String target = "";// 历史办理人账号
		if (list != null && list.size() > 0) {
			TaskInstance taskInstance_last = list.get(list.size() - 1);
			target = taskInstance_last.getTarget();
		}
		/*if (UtilString.isEmpty(target)) {
			String zwmc = model.getExt1();
			if (UtilString.isEmpty(zwmc) || zwmc.equals(CmccConst.user_leave5) || zwmc.equals(CmccConst.user_leave6)
					|| (zwmc.equals(CmccConst.user_leave4) && userid.contains("@hq.cmcc"))) {
				return true;
			}
		} else {
			if (target.equals(userid)) {
				return true;
			}
		}*/
		if (UtilString.isEmpty(target)) {
			String deptid_model = model.getDepartmentId();//部门ID
			int layer = DepartmentCache.getModel(deptid_model).getLayer();//部门层级
			if(layer==1){
				return false;
			}else if(userid.contains("hq.cmcc")){
				if(layer==2){
					return false;
				}
			}
		} else {
			if (target.equals(userid)) {
				return true;
			}else{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}
}