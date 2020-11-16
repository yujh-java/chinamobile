package com.actionsoft.apps.cmcc.standardization.event.address;

import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;

/** 
* @author yujh
* @version 创建时间：2019年4月23日 下午4:30:13 
* 部所内部所有人过滤
*/
public class WithInDepartmentUserEvent implements AddressUIFilterInterface{
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		//获得流程创建人的全路径
		String userid = uc.getUID();
		String deptid_current = UserCache.getModel(userid).getDepartmentId();// 根据发起人id得到部门路径
		String userpathIdofCache = DepartmentCache.getModel(deptid_current).getPathIdOfCache();// 根据发起人部门路径得到部门全路径
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		if(userpathIdofCache.contains(deptPathId_model)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		if(uc.getDepartmentModel().getId().equals(model.getDepartmentId())){
			return true;
		}
		
		return false;
	}
}
