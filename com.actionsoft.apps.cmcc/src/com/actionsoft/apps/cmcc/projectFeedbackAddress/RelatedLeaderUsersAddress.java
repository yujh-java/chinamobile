package com.actionsoft.apps.cmcc.projectFeedbackAddress;
/**
 * 用于一般委托项目启动反馈流程里面的相关处领导审核节点地址簿过滤事件
 * @author zhaoxs
 * @date  2017-06-21
 */

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class RelatedLeaderUsersAddress implements AddressUIFilterInterface {
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model, AdvancedAddressModel arg2) {
		/*
		 * String deptId = uc.getDepartmentModel().getId(); String parentDeptID
		 * = uc.getDepartmentModel().getParentDepartmentId(); String
		 * parentDeptPathId =
		 * DepartmentCache.getModel(parentDeptID).getPathIdOfCache(); String
		 * deptPathId = model.getPathIdOfCache(); if
		 * (model.getId().equals(deptId)) { return false; } if
		 * ((parentDeptPathId.contains(deptPathId)) ||
		 * (deptPathId.contains(parentDeptPathId))) { return true; } return
		 * false;
		 */
		String itDept_1 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_1).getPathIdOfCache();
		String itDept_2 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_2).getPathIdOfCache();
		String userDept = uc.getDepartmentModel().getPathIdOfCache();
		if((userDept.contains(itDept_1) || userDept.contains(itDept_2)) && (model.getPathIdOfCache().contains(itDept_1) || model.getPathIdOfCache().contains(itDept_2))){
			return true;
		}
		
		String deptid = uc.getDepartmentModel().getId();
		String parentDeptID = uc.getDepartmentModel().getParentDepartmentId();
		String parentDeptPathId = DepartmentCache.getModel(parentDeptID).getPathIdOfCache();
		String deptPathId = model.getPathIdOfCache();//过滤部门全路径
		if(model.getId().equals(deptid)){
			return false;
		}
		if (!UtilString.isEmpty(parentDeptPathId)&&(deptPathId.contains(parentDeptPathId)||parentDeptPathId.contains(deptPathId))) {
			return true;
		}
		return false;
	}

	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel AddressModel) {
		/*
		 * String ext = model.getExt1(); String userid = model.getUID(); if
		 * (userid.contains("@hq.cmcc")) { if
		 * (ext.equals(CmccConst.user_leave3)) { return true; } } else { if
		 * (ext.equals(CmccConst.user_leave4) ||
		 * ext.equals(CmccConst.user_leave3)) { return true; } } return false;
		 */
		String userid = model.getUID();
		int layer = DepartmentCache.getLayer(model.getDepartmentId());
		if(layer==1){
			return false;
		}else if(userid.contains("@hq.cmcc")){
			if(layer==2){
				return false;
			}
		}
		return true;
	}
}
