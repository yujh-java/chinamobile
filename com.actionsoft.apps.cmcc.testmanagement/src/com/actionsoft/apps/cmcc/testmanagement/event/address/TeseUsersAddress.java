package com.actionsoft.apps.cmcc.testmanagement.event.address;

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
 * 测试启动会流程
 * 测试管理经理审批节点，显示所有部所人员
 * @author yujh
 *
 */
public class TeseUsersAddress implements AddressUIFilterInterface{
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		//String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径、也就是上个节点的登录人的全路径
		//获得流程创建人的全路径
		String userid = SDK.getProcessAPI().getInstanceById(advancedAddressModel.getInstanceId()).getCreateUser();//获取发起人id
		String deptid_current = UserCache.getModel(userid).getDepartmentId();//根据发起人id得到部门路径
		String userpathIdofCache = DepartmentCache.getModel(deptid_current).getPathIdOfCache();//根据发起人部门路径得到部门全路径
		
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
        
		System.err.println("==++"+deptPathId_model);
	
		if(userpathIdofCache.contains(deptPathId_model)){
			return true;
		}

		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		return true;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
