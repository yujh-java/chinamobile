package com.actionsoft.apps.cmcc.testmanagement.event.address;

import com.actionsoft.apps.cmcc.testmanagement.constant.TestManagementConst;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.sdk.local.SDK;

/**
 * @author yujh
 * @version 创建时间：2019年6月19日 上午10:04:33 获取创建人单位所有人
 */
public class CompanyUserEevent implements AddressUIFilterInterface {
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		String userid = SDK.getProcessAPI().getInstanceById(advancedAddressModel.getInstanceId()).getCreateUser();// 获取发起人id
		String deptid_current = SDK.getORGAPI().getDepartmentByUser(userid).getParentDepartmentId();// 根据发起人id得到研究院路径
		String userpathIdofCache = DepartmentCache.getModel(deptid_current).getPathIdOfCache();// 根据发起人部门路径得到部门全路径
		String deptPathId_model = model.getPathIdOfCache();// 部门全路径
		if (userpathIdofCache.contains(deptPathId_model) || deptPathId_model.contains(userpathIdofCache)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		if(model.getExt1().equals(TestManagementConst.user_leave2)){//部门负责人过滤掉
			return false;
		}
		return true;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}
}
