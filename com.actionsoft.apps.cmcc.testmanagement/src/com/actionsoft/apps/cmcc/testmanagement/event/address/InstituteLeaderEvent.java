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
* @author yujh: 
* @version 创建时间：2018年12月20日 下午3:00:51 
* 院领导审批人员过滤
*/
public class InstituteLeaderEvent  implements AddressUIFilterInterface{
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		//获得流程创建人的全路径
		String userid = SDK.getProcessAPI().getInstanceById(advancedAddressModel.getInstanceId()).getCreateUser();//获取发起人id
		String deptid_current = UserCache.getModel(userid).getDepartmentId();//根据发起人id得到部门路径
		String userpathIdofCache = DepartmentCache.getModel(deptid_current	).getPathIdOfCache();//根据发起人部门路径得到部门全路径
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		int layer = model.getLayer();//部门层级
		if(layer == 1 || layer == 2){
			if(userpathIdofCache.contains(deptPathId_model)){
				return true;
			}
		}
		return false; 
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		String kzzd=model.getExt1();
		if(!TestManagementConst.user_leave2.equals("2") || !"2".equals(kzzd)){
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
