package com.actionsoft.apps.cmcc.enterprise.address;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
/**
 * 企标管理
 * 获取需求部门领导过滤事件
 * @author chenxf
 *
 */
public class EnterpriseDemandLeaderAddress implements AddressUIFilterInterface{

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, 
			CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		
		//当前人部门ID全路径即为需求部门ID全路径
		String pathdeptId = uc.getDepartmentModel().getPathIdOfCache();
		//遍历部门ID的全路径
		String deptid_query = DepartmentCache.getModel(model.getId()).getPathIdOfCache();
		 //部门层级
	    int layer = model.getLayer();
		//当前人账号
		String userid = uc.getUID();
		//如果是集团研究院的，则只列出需求部门领导
		if(userid.indexOf("@hq.cmcc") != -1){
			
			if((layer == 1 || layer == 2) 
						&& pathdeptId.contains(deptid_query)){
				return true;
			}
		}else{
			//一级部门ID
			String firstDeptid = pathdeptId.split("/")[0];
			//遍历的一级部门ID
			String firstDeptid_model = deptid_query.split("/")[0];
			if(firstDeptid.equals(firstDeptid_model)){
				return true;
			}
			
//			if(pathdeptId.contains(deptid_query)){
//				return true;
//			}else{
//				//当前人父部门ID
//		    	String parentDeptId_current = uc.getDepartmentModel().getParentDepartmentId();
//		    	//遍历部门父部门ID
//		    	String parentDeptId_model = model.getParentDepartmentId();
//		    	//如果当前人父部门ID==遍历部门父部门ID，则显示出来
//		    	if(parentDeptId_current.equals(parentDeptId_model)){
//		    		return true;
//		    	}else{
//		    		//当前人部ID
//		    		String currentDeptId = uc.getDepartmentModel().getId();
//		    		//如果是需求部门领导发给内部办理时
//		    		if(currentDeptId.equals(parentDeptId_model)){
//		    			return true;
//		    		}
//		    	}
//			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
			UserModel model, AdvancedAddressModel advancedAddressModel) {
		//职位等级
		String zwmc = model.getExt1();
		//账号
		String userid = model.getUID();
		//只要部门领导级别的人员
		if(userid.contains("hq.cmcc") && !CmccCommon.user_leave2.equals(zwmc)){
			return false;
		}
		return true;
	}
}
