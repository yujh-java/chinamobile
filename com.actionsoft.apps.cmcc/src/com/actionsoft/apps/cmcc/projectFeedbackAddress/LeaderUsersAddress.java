package com.actionsoft.apps.cmcc.projectFeedbackAddress;
/**
 * 一般委托流程
 * 参与者地址薄过滤事件，根据当前登录人找父部门下面所有人
 * @author zhaoxs
 * @date 20170914
 */

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.sdk.local.SDK;

public class LeaderUsersAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		String itDept_1 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_1).getPathIdOfCache();
		String itDept_2 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_2).getPathIdOfCache();
		String userDept = uc.getDepartmentModel().getPathIdOfCache();
		if((userDept.contains(itDept_1) || userDept.contains(itDept_2)) && (model.getPathIdOfCache().contains(itDept_1) || model.getPathIdOfCache().contains(itDept_2))){
			return true;
		}
		//String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String userParentdepartId = uc.getDepartmentModel().getParentDepartmentId();//登录人部门父ID
		String userpathIdofCache = DepartmentCache.getModel(userParentdepartId).getPathIdOfCache();//父部门ID全路径
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		if(userpathIdofCache.contains(deptPathId_model)||deptPathId_model.contains(userpathIdofCache)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		String userid = uc.getUID();//用户账号
		String deptid_model = model.getDepartmentId();//部门ID
		int layer = DepartmentCache.getModel(deptid_model).getLayer();//部门层级
		if(layer==1){
			return false;
		}else if(userid.contains("hq.cmcc")){
			if(layer==2){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
