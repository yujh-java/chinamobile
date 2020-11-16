package com.actionsoft.apps.cmcc.unplanedProjectAddress;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;

/**
 * 获取当前人所在部所领导
 * @author wuxx
 *
 */
public class TJRLeaderUsersAddress implements AddressUIFilterInterface{
	public String currentDeptid;
	public String pathId_current;
	
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}
	
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		String pathDeptid = model.getPathIdOfCache();
		//当前人父部门ID
		String parentDeptId_current = uc.getDepartmentModel().getParentDepartmentId();
		//父部门Id全路径
		String pathParentDeptid = DepartmentCache.getModel(parentDeptId_current).getPathIdOfCache();
		if (UtilString.isEmpty(currentDeptid)) {
			// 当前人部门ID
			currentDeptid = uc.getDepartmentModel().getId();
			// 当前人部门ID全路径
			pathId_current = DepartmentCache.getModel(currentDeptid).getPathIdOfCache();
			
		}
		// 遍历部门ID全路径
		String pathId_model = model.getPathIdOfCache();
		// 当前人账号
		String userid = uc.getUID();
		// 集团研究院只列出处室领导
	       if(userid.contains("@hq.cmcc")){
	    	   if (pathId_current.contains(pathId_model)) {
					return true;
				}
	       }else{
	    	   if (pathDeptid.contains(pathParentDeptid) 
		    			|| pathParentDeptid.contains(pathDeptid)) {
		    		return true;
		    	}
	       }
	       return false;
			/*if (pathId_current.contains(pathId_model)) {
				return true;
			}
			return false;*/
	
	}
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		// 人员层级
		String ext1 = model.getExt1();
		// 当前人账号
		String userid = uc.getUID();
		// 判断是否为研究院人员
		
			//
		if(userid.contains("@hq.cmcc")){
			
			if (ext1.equals(CmccConst.user_leave3)){
				return true;
		    }
		}else{
			return true;
		}
			return false;
		
		
	}

	
	

}
