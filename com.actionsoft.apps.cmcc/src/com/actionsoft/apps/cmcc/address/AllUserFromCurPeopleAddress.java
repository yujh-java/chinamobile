package com.actionsoft.apps.cmcc.address;

import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
/**
 * 项目模块流程
 * 获取当前人所在部门所有人--适用所有流程
 * @author chenxf
 *
 */
public class AllUserFromCurPeopleAddress implements AddressUIFilterInterface{

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			DepartmentModel model, AdvancedAddressModel arg2) {
		//当前人部门全路径
		String currentPathDeptId = uc.getDepartmentModel().getPathIdOfCache();
		//遍历部门全路径
		String pathDeptId = model.getPathIdOfCache();
		
		if(uc.getUID().contains("@hq.cmcc")){
			//当前人部门层级
			int layer = uc.getDepartmentModel().getLayer();
			if(layer == 1){
				if(currentPathDeptId.contains(pathDeptId)){
					return true;
				}
			}else if(layer == 2){
				if(currentPathDeptId.contains(pathDeptId) 
						|| pathDeptId.contains(currentPathDeptId)){
					return true;
				}
			}else if(layer == 3){
				/*
				 * 获取当前人父部门ID、父部门全路径
				 */
				String parentDeptid = uc.getDepartmentModel().getParentDepartmentId();
				String pathParentDeptid = DepartmentCache.getModel(parentDeptid).getPathIdOfCache();
				if(pathParentDeptid.contains(pathDeptId) 
						|| pathDeptId.contains(pathParentDeptid)){
					return true;
				}
			}
		}else{
			//跟部门ID
			String rootDeptid = currentPathDeptId.split("/")[0];
			if(pathDeptId.contains(rootDeptid)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext arg0, UserModel arg1, AdvancedAddressModel arg2) {
		return true;
	}
}
