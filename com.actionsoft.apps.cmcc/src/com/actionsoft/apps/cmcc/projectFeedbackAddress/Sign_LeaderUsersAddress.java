package com.actionsoft.apps.cmcc.projectFeedbackAddress;
/**
 * 用来过滤一般委托项目 启动反馈流程  会签地址簿过滤事件
 * @author zhaoxs
 * @date  2017-06-21
 */

import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;

public class Sign_LeaderUsersAddress implements AddressUIFilterInterface {
	private String taskid = "";
	private String processid = "";

	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model, AdvancedAddressModel admodel) {
		String deptId = uc.getDepartmentModel().getId();// 获取部门id
		String parentDeptID = uc.getDepartmentModel().getParentDepartmentId();// 获取父部门id
		String parentDeptPathId = DepartmentCache.getModel(parentDeptID).getPathIdOfCache();
		String deptPathId = model.getPathIdOfCache();// 过滤部门id全路径
		String userPathId = uc.getDepartmentModel().getPathIdOfCache();
		// 获取任务状态
		if (UtilString.isEmpty(taskid)) {
			taskid = admodel.getTaskId();// 任务实例
		}
		if (UtilString.isEmpty(processid)) {
			processid = admodel.getInstanceId();// 流程实例id
		}
		int state = DBSql.getInt("SELECT TASKSTATE FROM WFC_TASK WHERE ID=? AND PROCESSINSTID = ?",
				new Object[] { taskid, processid });
		// 普通待办
		if (state == 1){
			if (model.getId().equals(deptId)) {
				return false;
			}
			if (!UtilString.isEmpty(parentDeptPathId)
					&& (deptPathId.contains(parentDeptPathId) || (parentDeptPathId.contains(deptPathId)))) {
				return true;
			}
			return false;
		}
		// 加签
		if (state == 11){
			if (!UtilString.isEmpty(userPathId) && (userPathId.contains(deptPathId)||deptPathId.contains(userPathId))) {
				return true;
			}
			
			return false;
		}
		return false;
	}

	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel admodel) {

		/*
		 * String zwmc_model = model.getExt1(); String userid = model.getUID();
		 * if (UtilString.isEmpty(taskid)) { taskid = admodel.getTaskId();//
		 * 任务实例 } if (UtilString.isEmpty(processid)) { processid =
		 * admodel.getInstanceId();// 流程实例id } // 获取任务状态 int state =
		 * DBSql.getInt(
		 * "SELECT TASKSTATE FROM WFC_TASK WHERE ID=? AND PROCESSINSTID = ?",
		 * new Object[] { taskid, processid }); if (state == 1) {// 普通待办 if
		 * (userid.contains("@hq.cmcc")) { if
		 * (zwmc_user.equals(CmccConst.user_leave3)) {
		 * 
		 * if (zwmc_model.equals(CmccConst.user_leave3)) { return true; } return
		 * false; } } else { if (zwmc_user.equals(CmccConst.user_leave3) ||
		 * zwmc_user.equals(CmccConst.user_leave4)) {
		 * 
		 * if (zwmc_model.equals(CmccConst.user_leave3) ||
		 * zwmc_model.equals(CmccConst.user_leave4)) { return true; } return
		 * false; } }
		 * 
		 * } if (state == 11)// 加签 {
		 * 
		 * if ((zwmc_model.equals("")) ||
		 * zwmc_model.equals(CmccConst.user_leave4) ||
		 * zwmc_model.equals(CmccConst.user_leave5) ||
		 * zwmc_model.equals(CmccConst.user_leave6)) { return true; } } return
		 * false;
		 */

		String zwmc_model = model.getExt1();

		String userid = model.getUID();
		if (UtilString.isEmpty(taskid)) {
			taskid = admodel.getTaskId();// 任务实例
		}
		if (UtilString.isEmpty(processid)) {
			processid = admodel.getInstanceId();// 流程实例id
		}
		int state = DBSql.getInt("SELECT TASKSTATE FROM WFC_TASK WHERE ID=? AND PROCESSINSTID = ?", new Object[] { taskid, processid });
		/*
		 * // 获取任务状态 int state = DBSql.getInt(
		 * "SELECT TASKSTATE FROM WFC_TASK WHERE ID=? AND PROCESSINSTID = ?",
		 * new Object[] { taskid, processid }); if (state == 1) {// 普通待办 int
		 * layer = DepartmentCache.getLayer(model.getDepartmentId()); if (layer
		 * == 1) { return false; } else if (userid.contains("@hq.cmcc")) { if
		 * (layer == 2) { return false; } } return true;
		 * 
		 * } else { int layer =
		 * DepartmentCache.getLayer(model.getDepartmentId()); if (layer == 1) {
		 * return false; } else if (userid.contains("@hq.cmcc")) { if (layer ==
		 * 2) { return false; } } return true; }
		 */
		
		int userLayer = uc.getDepartmentModel().getLayer();
		int layer = DepartmentCache.getLayer(model.getDepartmentId());
		if(state==1){
			if(layer<userLayer){
				return false;
			}
		if (layer == 1) {
			return false;
		} else if (userid.contains("@hq.cmcc")) {
			if (layer == 2) {
				return false;
			}
		}}
		
		if(state==11){
			if(layer<userLayer){
				return false;
			}
			if (userid.contains("@hq.cmcc")){
			if(layer==1||layer==2){
				return false;
			}
			}else{
				if(layer==1){
					return false;
				}
			}
			
		}
		return true;

	}

}
