package com.actionsoft.apps.cmcc.projectFeedbackAddress;
/**
 * 一般委托流程
 * 参与者地址薄过滤事件
 * 根据需求部门，查询项目经理
 * @author zhaoxs
 * @date 2017-06-21
 */
import java.sql.Connection;
import java.util.List;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.apps.cmcc.projectFeedback.ProjectFeedbackConst;
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
import com.actionsoft.sdk.local.api.HistoryTaskQueryAPI;

public class OrdinaryUsersForXqbmAddress implements AddressUIFilterInterface {
	private String deptid = "";
	private String noteid = "";
	private String processid = "";
	private List<HistoryTaskInstance> list = null;
	private String id = "";

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		String itDept_1 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_1).getPathIdOfCache();
		String itDept_2 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_2).getPathIdOfCache();
		String userDept = uc.getDepartmentModel().getPathIdOfCache();
		if((userDept.contains(itDept_1) || userDept.contains(itDept_2)) && (model.getPathIdOfCache().contains(itDept_1) || model.getPathIdOfCache().contains(itDept_2))){
			return true;
		}
		Connection conn = null;
		try {
			conn = DBSql.open();
			if (UtilString.isEmpty(processid)) {
				processid = advancedAddressModel.getInstanceId();
			}
			if (UtilString.isEmpty(deptid)) {
				deptid = DBSql.getString(conn, "SELECT SUBRESULTXQBM FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
						new Object[] { processid });
			}
			String userpathIdofCache = "";
			String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径

			if (UtilString.isEmpty(noteid)) {
				String taskid = advancedAddressModel.getTaskId();// 获取任务实例id
				noteid = SDK.getTaskAPI().getInstanceById(taskid).getActivityDefId();// 获取节点id
			}

			userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();// 获取用户id全路径
			String target = null;
			if (noteid.equals(ProjectFeedbackConst.xqbmcld_stepid)
					|| noteid.equals(ProjectFeedbackConst.xqbmbld_stepid))// 退回
			{
				// 获取处领导审核历史参与者
				if (UtilString.isEmpty(list)) {

					list = ((HistoryTaskQueryAPI) SDK.getHistoryTaskQueryAPI()
							.activityDefId(ProjectFeedbackConst.xqbmxmjl_noteid).userTaskOfWorking()
							.addQuery("TASKSTATE =", 1).addQuery("PROCESSINSTID=", processid)).connection(conn).list();
				}
				if ((list != null) && (list.size() > 0)) {
					target = ((HistoryTaskInstance) list.get(list.size() - 1)).getTarget();
				}
				if (UtilString.isEmpty(id)) {
					id = DBSql.getString(conn, "select departmentid from ORGUSER where userid = ?",
							new Object[] { target });// 查询历史参与者部门id
				}
				String modelpath = DepartmentCache.getModel(id).getPathIdOfCache();
				if (deptpathIdofCache.contains(modelpath) || modelpath.contains(deptpathIdofCache)) {
					return true;
				}
				return false;
			} else {
				if (!UtilString.isEmpty(deptid)) {
					DepartmentModel deptModel = DepartmentCache.getModel(deptid);
					userpathIdofCache = deptModel.getPathIdOfCache();
				}
				/*
				 * if (!UtilString.isEmpty(userpathIdofCache) &&
				 * (deptpathIdofCache.contains(userpathIdofCache) ||
				 * userpathIdofCache.contains(deptpathIdofCache))) { return
				 * true; } return false;
				 */
				if (!UtilString.isEmpty(userpathIdofCache) && (deptpathIdofCache.contains(userpathIdofCache)
						|| userpathIdofCache.contains(deptpathIdofCache))) {
					return true;
				}

			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(conn);
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		String userid = model.getUID();
		if (UtilString.isEmpty(processid)) {
			processid = advancedAddressModel.getInstanceId();
		}
		String taskid = advancedAddressModel.getTaskId();// 任务ID
		TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
		if (UtilString.isEmpty(noteid)) {
			noteid = taskInstance.getActivityDefId();// 节点ID
		}
		if (noteid.equals(ProjectFeedbackConst.xqbmcld_stepid) || noteid.equals(ProjectFeedbackConst.xqbmbld_stepid)) {
			// 查找需求部门项目经理历史办理人
			if (UtilString.isEmpty(list)) {
				list = SDK.getHistoryTaskQueryAPI().activityDefId(ProjectFeedbackConst.xqbmxmjl_noteid)
						.userTaskOfWorking().addQuery("TASKSTATE =", 1).addQuery("PROCESSINSTID = ", processid)
						.orderByEndTime().asc().list();
			}
		}
		String target = "";// 历史办理人账号
		if (list != null && list.size() > 0) {
			TaskInstance taskInstance_last = list.get(list.size() - 1);
			target = taskInstance_last.getTarget();
		}
		if (UtilString.isEmpty(target)) {

			/*
			 * String zwmc = model.getExt1(); if (UtilString.isEmpty(zwmc) ||
			 * "普通员工".equals(zwmc) || zwmc.equals(CmccConst.user_leave4) ||
			 * zwmc.equals(CmccConst.user_leave5) ||
			 * zwmc.equals(CmccConst.user_leave6)) { return true; }
			 */
			int layer = DepartmentCache.getLayer(model.getDepartmentId());
			if (layer == 1) {
				return false;
			} else if (userid.contains("@hq.cmcc")) {
				if (layer == 2) {
					return false;
				}
			}
			return true;
		} else {

			if (target.equals(userid)) {
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
