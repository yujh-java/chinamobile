package com.actionsoft.apps.cmcc.budget.address;

import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
/**
 * 预算管理--获取本部门及其子部门下所有人
 * @author chenxf
 *
 */
public class GetDeptNameAllPeople implements AddressUIFilterInterface{

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}
	/**
	 * 过滤本部门及其子部门下所有人
	 */
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			DepartmentModel model, AdvancedAddressModel advancedAddress) {
		//当前人部门全路径
		String currentPathDept = uc.getDepartmentModel().getPathIdOfCache();
		//遍历的部门全路径
		String pathDept = model.getPathIdOfCache();
		
		if(currentPathDept.contains(pathDept)){
			return true;
		}else{
			if(pathDept.contains(currentPathDept)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 过滤不是本部门及其子部门层级下的人员
	 */
	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
			UserModel model, AdvancedAddressModel arg2) {
		//当前人部门层级
		int currentLayer = uc.getDepartmentModel().getLayer();
		//遍历人员的部门层级
		int layer = DepartmentCache.getModel(model.getDepartmentId()).getLayer();
		if(currentLayer > layer){
			return false;
		}
		return true;
	}
}
