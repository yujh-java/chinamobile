package com.actionsoft.apps.budget.address;
/**
 * 参与者地址薄过滤事件,根据当前登录人，获取部门其他人员
 * @author zhaoxs
 * @date 20170815
 */

import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;

public class OtherDeptUsersAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		String userDepartmentId =  uc.getDepartmentModel().getId();
		String departmentID =  model.getId();
		
		if(userDepartmentId.equals(departmentID)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		String userid = uc.getUID();
		String uid = model.getUID();
		if(uid.equals(userid)){
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
