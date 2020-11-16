package com.actionsoft.apps.cmcc.unplanedProjectAddress;
/**
 * 计划外项目
 * 参与者地址簿过滤事件
 * 主管院领导参与者的过滤
 * @author Zhaoxs
 * @date 2017-06-29
 * */
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;

public class ZgyldLeaderUsersAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {

		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext arg0, DepartmentModel arg1, AdvancedAddressModel arg2) {

		return true;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedModel) {
		String ext1 = model.getExt1();
		if (ext1.equals("2")) {
			return true;
		}
		return false;
	}

}
