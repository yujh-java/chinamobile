package com.actionsoft.apps.cmcc.enterprise.address;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
/**
 * 企标管理--配合部所会签
 * @author chenxf
 *
 */
public class LeaderUsersAddressCounterSign 
			implements AddressUIFilterInterface{

	public String currentDeptid;//当前人部门ID
	public String pathCurrentDeptid;//当前人部门ID全路径
	public String userid;//当前人账号
	public String taskid;//任务ID
	public int taskState;//任务状态
	
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, 
			CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			  DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		if(UtilString.isEmpty(currentDeptid)){
			currentDeptid = uc.getDepartmentModel().getId();
		}
		if(UtilString.isEmpty(pathCurrentDeptid)){
			pathCurrentDeptid = DepartmentCache.getModel(currentDeptid).getPathIdOfCache();
		}
		//当前人账号
		if(UtilString.isEmpty(userid)){
			userid = uc.getUID();
		}
		//获取当前任务状态
		if(UtilString.isEmpty(taskid)){
			taskid = advancedAddressModel.getTaskId();
			taskState = SDK.getTaskAPI().getTaskInstance(taskid).getState();
		}
	    //遍历部门ID全路径
	    String pathId_model = model.getPathIdOfCache();
	    if(userid.contains("@hq.cmcc")){
	    	if(taskState == 1){
	    		//当前人父部门ID
	    		String parentDeptId_current = uc.getDepartmentModel().getParentDepartmentId();
	    		//父部门Id全路径
	    		String pathParentDeptid = DepartmentCache.getModel(parentDeptId_current).getPathIdOfCache();
		    	//查询本单位所有部所
		    	if (pathId_model.contains(pathParentDeptid) 
		    			|| pathParentDeptid.contains(pathId_model)) {
		    		return true;
		    	}
	    	}else{
	    		//当前人部门全路径
	    		String currentPathDeptId = uc.getDepartmentModel().getPathIdOfCache();
	    		//只查询本部所下所有人
	    		if(currentPathDeptId.contains(pathId_model)){
	    			return true;
	    		}
	    	}
	    	
	    		
	    	
	    	
	    }else{
	    	//当前人的一级部门ID
			String firstDeptid = pathCurrentDeptid.split("/")[0];
			//遍历的一级部门ID
			String firstDeptid_model = pathId_model.split("/")[0];
			if(firstDeptid.equals(firstDeptid_model)){
				return true;
			}
	    }
	    return false;
	}
	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
			  UserModel model, AdvancedAddressModel advancedAddressModel) {
		//获取当前任务状态
		if(UtilString.isEmpty(taskid)){
			taskid = advancedAddressModel.getTaskId();
			taskState = SDK.getTaskAPI().getTaskInstance(taskid).getState();
		}
		//人员层级
		String ext1 = model.getExt1();
		//当前人员账号
		String userid = uc.getUID();
		//判断是否为研究院人员
		if(userid.contains("@hq.cmcc")){
			if(taskState == 11){
				//过滤2级部门领导
				if(ext1.equals(CmccCommon.user_leave2) || "".equals(ext1)){
					return false;
				}
				return true;
			}else{
				//只查出三级部所领导
				if(ext1.equals(CmccCommon.user_leave3) || "".equals(ext1)){
					return true;
				}
				return false;
				
			}
		 }
		 return true;
	}
}
