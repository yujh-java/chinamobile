package com.actionsoft.apps.cmcc.titleScoreAddress;

/**
 * 结题评分参与者地址薄过滤事件
 * 根据需求部门，查询领导(处长)
 * @author zhaoxs
 * @date  2017-06-22
 */
import java.sql.Connection;
import java.util.List;
import com.actionsoft.apps.cmcc.titleScore.TitleScoreConst;
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

public class LeaderForXqbmAddress implements AddressUIFilterInterface {
	public String deptid = "";
	public String taskid = "";
	public String processid = "";
	public String activityDefid = "";
	public List<HistoryTaskInstance> list = null;

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		/*
		 * String processid = advancedAddressModel.getInstanceId(); if
		 * (UtilString.isEmpty(deptid)) { deptid = DBSql.getString(
		 * "SELECT SUBRESULTXQBM FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?"
		 * , new Object[] { processid }); } String xqbmPathCache =
		 * DepartmentCache.getModel(deptid).getPathIdOfCache();// 需求部门ID全路径
		 * String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径 if
		 * (deptpathIdofCache.contains(xqbmPathCache) ||
		 * xqbmPathCache.contains(deptpathIdofCache)) { return true; } return
		 * false;
		 */
		Connection conn = null;
		try {
			conn = DBSql.open();
			if (UtilString.isEmpty(taskid)) {
				taskid = advancedAddressModel.getTaskId();// 任务ID
			}
			if (UtilString.isEmpty(processid)) {
				processid = advancedAddressModel.getInstanceId();
			}
			TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
			if (UtilString.isEmpty(activityDefid)) {
				activityDefid = taskInstance.getActivityDefId();// 节点ID
			}
			if (activityDefid.equals(TitleScoreConst.xmjl_noteid)) {
				// 查找处室领导历史办理人
				list = SDK.getHistoryTaskQueryAPI().activityDefId(TitleScoreConst.nbcs_noteid)
						.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
						.orderByEndTime().asc().connection(conn).list();
			}
			String target = "";// 历史办理人账号

			if (list != null && list.size() > 0) {
				TaskInstance taskInstance_last = list.get(list.size() - 1);
				target = taskInstance_last.getTarget();
			}
			if (UtilString.isEmpty(deptid)) {
				deptid = DBSql.getString(conn, "SELECT SUBRESULTXQBM FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
						new Object[] { processid });
			}
			String xqbmPathCache = DepartmentCache.getModel(deptid).getPathIdOfCache();// 需求部门ID全路径
			String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径
			if (UtilString.isEmpty(target)) {
				if (!UtilString.isEmpty(xqbmPathCache)
						&& (deptpathIdofCache.contains(xqbmPathCache) || xqbmPathCache.contains(deptpathIdofCache))) {
					return true;
				}
			} else {
				String id = DBSql.getString(conn, "SELECT DEPARTMENTID FROM ORGUSER WHERE USERID = ?",
						new Object[] { target });
				String idpathCache = DepartmentCache.getModel(id).getPathIdOfCache();
				if (idpathCache.contains(deptpathIdofCache)) {
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
		if (UtilString.isEmpty(taskid)) {
			taskid = advancedAddressModel.getTaskId();// 任务ID
		}
		TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
		if (UtilString.isEmpty(activityDefid)) {
			activityDefid = taskInstance.getActivityDefId();// 节点ID
		}
		if (activityDefid.equals(TitleScoreConst.xmjl_noteid)) {
			// 查找处室领导历史办理人
			if (UtilString.isEmpty(list)) {
				list = SDK.getHistoryTaskQueryAPI().activityDefId(TitleScoreConst.nbcs_noteid)
						.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
						.orderByEndTime().asc().list();
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
