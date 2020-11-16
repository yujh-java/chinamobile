package com.actionsoft.apps.cmcc.enterprise.address;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.sdk.local.SDK;
/**
 * 企标管理--承担单位企标管理员处理--相关部所会签--部所所有人
 * @author chenxf
 *
 */
public class AllUsersAddressCounterSign implements AddressUIFilterInterface{

	public String taskid;
	public String parentTaskid;
	public int state;
	
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, 
			CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			  DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		if(taskid == null || "".equals(taskid)){
			//当前任务实例ID
			taskid = advancedAddressModel.getTaskId();
			//当前任务的状态
			state = SDK.getTaskAPI().getTaskInstance(taskid).getState();
		}
		
		//当前人部门ID全路径
		String pathId_current = uc.getDepartmentModel().getPathIdOfCache();
	    //遍历部门ID全路径
	    String pathId_model = model.getPathIdOfCache();
	    if(state == 11 && uc.getUID().contains("@hq.cmcc")){
	    	//只查询本部门的
	    	if (pathId_current.contains(pathId_model)) {
	    		return true;
	    	}
	    }else{
	    	if (pathId_current.contains(pathId_model)) {
	    		if(state == 11){
	    			return true;
	    		}else{
	    			//过滤本部门下处室领导
	    			if(uc.getDepartmentModel().getId().equals(model.getId())){
	    				return false;
	    			}
	    		}
	    		return true;
	    	}else{
	    		//当前人父部门ID
	    		String parentDeptId_current = uc.getDepartmentModel().getParentDepartmentId();
	    		//遍历部门父部门ID
	    		String parentDeptId_model = model.getParentDepartmentId();
	    		//如果当前人父部门ID==遍历部门父部门ID，则显示出来
	    		if(parentDeptId_current.equals(parentDeptId_model)){
	    			return true;
	    		}
	    	}
	    }
	    return false;
	}
	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
			  UserModel model, AdvancedAddressModel advancedAddressModel) {
		//人员层级
		String ext1 = model.getExt1();
		//人员账号
		String userid = model.getUID();
		//判断是否为研究院人员
		if(userid.contains("@hq.cmcc")){
			//只查出三级部所领导
			if(!ext1.equals(CmccCommon.user_leave2)){
				return true;
			}
			return false;
		 }
		 return true;
	}
}
