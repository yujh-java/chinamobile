package com.actionsoft.apps.cmcc.standardization.event.address;

import com.actionsoft.apps.cmcc.standardization.constant.StandardizationConstant;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

/** 
* @author yujh 
* @version 创建时间：2018年12月20日 上午11:44:34 
* 部所领导节点人员过滤
*/
public class DepartmentLeaderEvent implements AddressUIFilterInterface{
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		//获得流程创建人的全路径
		String userid = SDK.getProcessAPI().getInstanceById(advancedAddressModel.getInstanceId()).getCreateUser();//获取发起人id
		String deptid_current = UserCache.getModel(userid).getDepartmentId();//根据发起人id得到部门路径
		String userpathIdofCache = DepartmentCache.getModel(deptid_current).getPathIdOfCache();//根据发起人部门路径得到部门全路径
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		if(!uc.getUID().contains("hq.cmcc")){
			String userPathId = uc.getDepartmentModel().getPathIdOfCache();
			String fistDeptid = "";//一级部门ID
			String[] userDeptPathIdArr = null;
			if(!UtilString.isEmpty(userPathId)){
				userDeptPathIdArr = userPathId.split("/");
				fistDeptid = userDeptPathIdArr[0];
			}
			if(deptPathId_model.contains(fistDeptid)){
				return true;
			}
			return false;
			
		}else{
			if(userpathIdofCache.contains(deptPathId_model)){
				return true;
			}
			return false;
		}
		
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		DepartmentModel department = SDK.getORGAPI().getDepartmentById(model.getDepartmentId());
		if(uc.getUID().contains("hq.cmcc") && department.getLayer() >= 3){
			String kzzd=model.getExt1();
			if(!StandardizationConstant.user_leave3.equals("3") || !"3".equals(kzzd)){
				return false;
			}
			return true;
		}else if(!uc.getUID().contains("hq.cmcc")){
			return true;
		}else{
			return false;
		}
		
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}
}
