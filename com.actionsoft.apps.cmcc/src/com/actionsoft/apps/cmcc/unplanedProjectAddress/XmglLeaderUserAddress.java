package com.actionsoft.apps.cmcc.unplanedProjectAddress;
/**
 * 自立国拨计划外项目地址簿过滤事件（处长、副处长）
 * @author zhaoxs
 * @date 2017-06-29
 * 
 * */

import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;

public class XmglLeaderUserAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model, AdvancedAddressModel advancedModel) {
		String modelPathCache =  model.getPathIdOfCache();//过滤部门全路径
		String userPathCache =  uc.getDepartmentModel().getPathIdOfCache();//用户部门全路径
		if(modelPathCache.contains(userPathCache)){
		return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedModel) {
		/*
		String ext1 =  model.getExt1();
		if(ext1.equals(CmccConst.user_leave3)){
		return true;
		}
		return false;
		*/
		return true;
	}

}
