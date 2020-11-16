package com.actionsoft.apps.cmcc.address;
/**
 * 参与者地址薄过滤事件，根据当前登录人找部长、副部长。所有流程
 * @author nch
 * @date 2017-06-22
 */

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.sdk.local.SDK;

public class DeptLeaderUsersAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		String itDept_1 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_1).getPathIdOfCache();
		String itDept_2 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_2).getPathIdOfCache();
		String userDept = uc.getDepartmentModel().getPathIdOfCache();
		if((userDept.contains(itDept_1) || userDept.contains(itDept_2)) && (model.getPathIdOfCache().contains(itDept_1) || model.getPathIdOfCache().contains(itDept_2))){
			return true;
		}
		
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String rootDeptId = userpathIdofCache.split("/")[0];//登录人所在部门根路径
		
		String userid = uc.getUID();//登录人账号
		int layer = model.getLayer();//部门层级
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		if(userid.contains("@hq.cmcc")){
			if(layer == 1 || layer == 2){
				if(userpathIdofCache.contains(deptPathId_model)){
					return true;
				}
			}
		}else if(deptPathId_model.contains(rootDeptId)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		String zwmc = model.getExt1();
		String userid = model.getUID();
		if(userid.contains("@hq.cmcc") 
				&& !CmccConst.user_leave2.equals(zwmc)){
			return false;
		}
		return true;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
