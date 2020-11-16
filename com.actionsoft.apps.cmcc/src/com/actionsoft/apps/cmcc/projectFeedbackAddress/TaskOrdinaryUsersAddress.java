package com.actionsoft.apps.cmcc.projectFeedbackAddress;
/**
 * 一般委托流程
 * 参与者地址薄过滤事件 查询研发机构项目经理，任务负责人节点
 * @author zhaoxs
 * @date 2017-06-21
 */
import java.sql.Connection;
import java.util.List;

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

public class TaskOrdinaryUsersAddress implements AddressUIFilterInterface {
	private String taskid = "";
	private String processid = "";
	private String deptid = "";
	private String noteid = "";
	private String id = "";
	List<HistoryTaskInstance> list = null;

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		Connection conn = null;
		try {
			conn = DBSql.open();
			String processid = advancedAddressModel.getInstanceId();
			if (UtilString.isEmpty(deptid)) {
				deptid = DBSql.getString(conn, "SELECT QTYFJGBMID FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
						new Object[] { processid });
			}
			String userpathIdofCache = "";
			String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径

			if (UtilString.isEmpty(noteid)) {
				String taskid = advancedAddressModel.getTaskId();// 获取任务实例id
				noteid = SDK.getTaskAPI().getInstanceById(taskid).getActivityDefId();// 获取节点id
			}
			String target = null;
			if (noteid.equals(ProjectFeedbackConst.yfjgbssh_stepid)
					|| noteid.equals(ProjectFeedbackConst.yfjgzgld_noteid) 
					|| noteid.equals(ProjectFeedbackConst.yfjgjkr_actid))// 退回节点
			{
				// 获取处领导审核历史参与者
				if (UtilString.isEmpty(list)) {

					list = SDK.getHistoryTaskQueryAPI().activityDefId(ProjectFeedbackConst.yfjgrwfzr_noteid)
							.userTaskOfWorking().addQuery("TASKSTATE = ", 1).addQuery("PROCESSINSTID = ", processid)
							.orderByBeginTime().asc().connection(conn).list();

				}
				if ((list != null) && (list.size() > 0)) {
					target = ((HistoryTaskInstance) list.get(list.size() - 1)).getTarget();
				}

				if (UtilString.isEmpty(id)) {
					id = DBSql.getString(conn, "select departmentid from ORGUSER where userid = ?",
							new Object[] { target });// 查询历史参与者部门id
				}
				
				String modelpath = DepartmentCache.getModel(id).getPathIdOfCache();
				if (modelpath.contains(deptpathIdofCache)) {
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
				 * (userpathIdofCache.contains(deptpathIdofCache) ||
				 * deptpathIdofCache.contains(userpathIdofCache))) { return
				 * true; }
				 */
				if (!UtilString.isEmpty(userpathIdofCache) && (userpathIdofCache.contains(deptpathIdofCache)
						|| deptpathIdofCache.contains(userpathIdofCache))) {
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
			processid = advancedAddressModel.getInstanceId();// 流程实例id
		}
		if (UtilString.isEmpty(taskid)) {
			taskid = advancedAddressModel.getTaskId();// 任务ID
		}
		TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
		if (UtilString.isEmpty(noteid)) {
			noteid = taskInstance.getActivityDefId();// 节点ID
		}
		if (noteid.equals(ProjectFeedbackConst.yfjgbssh_stepid)
				|| noteid.equals(ProjectFeedbackConst.yfjgzgld_noteid)||noteid.equals(ProjectFeedbackConst.yfjgjkr_actid)) {
			// 查找研发机构任务负责人历史办理者
			if (UtilString.isEmpty(list)) {
				list = SDK.getHistoryTaskQueryAPI().activityDefId(ProjectFeedbackConst.yfjgrwfzr_noteid)
						.userTaskOfWorking().userTaskOfWorking().addQuery("TASKSTATE = ", 1)
						.addQuery("PROCESSINSTID = ", processid).orderByBeginTime().asc().list();

			}
		}

		String target = "";// 历史办理人账号
	
		
		if (list != null && list.size() > 0) {
			TaskInstance taskInstance_last = list.get(list.size() - 1);
			target = taskInstance_last.getTarget();

		}

		if (UtilString.isEmpty(target)) {
			int layer = DepartmentCache.getLayer(model.getDepartmentId());
			if (layer == 1) {
				return false;
			} else if (userid.contains("@hq.cmcc")) {
				if (layer == 2) {
					return false;
				}
			}
			return true;
			/*
			 * String zwmc = model.getExt1(); if (UtilString.isEmpty(zwmc) ||
			 * (CmccConst.user_leave4).equals(zwmc) ||
			 * (CmccConst.user_leave5).equals(zwmc) ||
			 * (CmccConst.user_leave6).equals(zwmc)) { return true; }
			 */
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
