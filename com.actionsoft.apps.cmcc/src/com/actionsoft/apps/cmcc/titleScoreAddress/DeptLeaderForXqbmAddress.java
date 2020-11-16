package com.actionsoft.apps.cmcc.titleScoreAddress;

/**
 * 参与者地址薄过滤事件
 * 根据需求部门，查询领导(部长)
 * @author zhaoxs
 * @date  2017-06-22
 */

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import com.actionsoft.apps.cmcc.CmccConst;
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

public class DeptLeaderForXqbmAddress implements AddressUIFilterInterface {
	private String deptid = "";
	public List<HistoryTaskInstance> list = new ArrayList<HistoryTaskInstance>();
	public String processid = "";
	public String activityDefid = "";
	public String userpathIdofCache = "";//需求部门ID全路径
	public String target = "";// 历史办理人账号
	public String id = "";//历史办理人账号ID
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {

//		Connection conn = null;
		try {
//			conn = DBSql.open();
			String userid = uc.getUID();
			if (UtilString.isEmpty(processid)) {
				processid = advancedAddressModel.getInstanceId();
			}
			String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径
			String taskid = advancedAddressModel.getTaskId();// 任务ID
			TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
			if (UtilString.isEmpty(activityDefid)) {
				activityDefid = taskInstance.getActivityDefId();// 节点ID
			}

			if (UtilString.isEmpty(deptid)) {
				deptid = DBSql.getString(/*conn, */"SELECT SUBRESULTXQBM FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
						new Object[] { processid });
			}
			if (!UtilString.isEmpty(deptid) && UtilString.isEmpty(userpathIdofCache)) {
				DepartmentModel deptModel = DepartmentCache.getModel(deptid);
				userpathIdofCache = deptModel.getPathIdOfCache();
			}
			if (activityDefid.equals(TitleScoreConst.nbcs_noteid)) { // 查找部门领导历史办理人
				if (list == null || list.size() <= 0) {
					list = SDK.getHistoryTaskQueryAPI().activityDefId(TitleScoreConst.bmld_noteid)
							.addQuery("PROCESSINSTID = ", processid).userTaskOfWorking().addQuery("TASKSTATE =", 1)
							.orderByEndTime()/*.connection(conn)*/.asc().list();
				}
			}
			if (list != null && list.size() > 0 && UtilString.isEmpty(target)) {
				TaskInstance taskInstance_last = list.get(list.size() - 1);
				target = taskInstance_last.getTarget();
			}
			if (UtilString.isEmpty(target)) {
				int layer = model.getLayer();
				if (userid.contains("hq.cmcc")) {
					if (layer == 1 || layer == 2) {
						if (userpathIdofCache.contains(deptpathIdofCache)) {
							return true;
						}
					}
				} else if (deptpathIdofCache.contains(deptid)) {
					return true;
				}
			} else {
				 
				if(UtilString.isEmpty(id)){
					id = DBSql.getString(/*conn,*/ "SELECT DEPARTMENTID FROM ORGUSER WHERE USERID=?",
							new Object[] { target });
				}
				String idpathCache = DepartmentCache.getModel(id).getPathIdOfCache();
				System.err.println("idpathCache="+idpathCache);
				System.err.println("deptpathIdofCache="+deptpathIdofCache);
				if (idpathCache.contains(deptpathIdofCache)) {
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace(System.err);
		} /*finally {
			DBSql.close(conn);
		}*/
		return false;

		/*
		 * if (UtilString.isEmpty(processid)) { processid =
		 * advancedAddressModel.getInstanceId(); } String deptpathIdofCache =
		 * model.getPathIdOfCache();// 过滤部门ID全路径 String deptid =
		 * DBSql.getString(
		 * "SELECT SUBRESULTXQBM FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?"
		 * , new Object[] { processid }); if (!UtilString.isEmpty(deptid)) {
		 * String idpathCache =
		 * DepartmentCache.getModel(deptid).getPathIdOfCache(); if
		 * (deptpathIdofCache.contains(idpathCache) ||
		 * idpathCache.contains(deptpathIdofCache)) { return true;
		 * 
		 * } }
		 * 
		 * return false;
		 */
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {

		String userid = model.getUID();
		if (UtilString.isEmpty(processid)) {
			processid = advancedAddressModel.getInstanceId();
		}
		String taskid = advancedAddressModel.getTaskId();// 任务ID
		TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
		if (UtilString.isEmpty(activityDefid)) {
			activityDefid = taskInstance.getActivityDefId();// 节点ID
		}

		if (activityDefid.equals(TitleScoreConst.nbcs_noteid)) { // 查找部门领导历史办理人
			if (UtilString.isEmpty(list)) {
				list = SDK.getHistoryTaskQueryAPI().activityDefId(TitleScoreConst.bmld_noteid)
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
			String zwmc = model.getExt1();
			if (userid.contains("@hq.cmcc")) {
				if (!zwmc.equals(CmccConst.user_leave2)) {
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
