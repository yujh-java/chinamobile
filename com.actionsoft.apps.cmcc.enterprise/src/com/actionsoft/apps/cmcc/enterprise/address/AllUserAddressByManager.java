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
 * 查询承担单位下所有人员
 * 根据主管领导的节点来过滤人员
 * @author chenxf
 *
 */
public class AllUserAddressByManager implements AddressUIFilterInterface{
	
	//任务实例ID
	public static String taskid;
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, 
			CompanyModel arg1, AdvancedAddressModel arg2) {
		return false;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		//当前人部门全路径ID
		String currentPathDeptid = uc.getDepartmentModel().getPathIdOfCache();
		//循环遍历的部门全路径ID
		String pathDeptid = model.getPathIdOfCache();
		
		if(uc.getUID().contains("@hq.cmcc")){
			//人员层级
			String layer = uc.getUserModel().getExt1();
			//如果是领导层级
			if(layer.equals(CmccCommon.user_leave2)){
				if(currentPathDeptid.contains(pathDeptid) 
						|| pathDeptid.contains(currentPathDeptid)){
					return true;
				}
			}else{
				//当前人父部门ID
	    		String parentDeptId_current = uc.getDepartmentModel().getParentDepartmentId();
	    		String parentPathDeptId = DepartmentCache.getModel(parentDeptId_current).getPathIdOfCache();
	    		//如果当前人父部门ID==遍历部门父部门ID，则显示出来
	    		if(parentPathDeptId.contains(pathDeptid) 
	    				|| pathDeptid.contains(parentPathDeptId)){
	    			return true;
	    		}
			}
		}else{
			String roolDeptId = currentPathDeptid.split("/")[0];
			if(pathDeptid.contains(roolDeptId)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
			UserModel model, AdvancedAddressModel advancedAddressModel) {
		if(UtilString.isEmpty(taskid)){
			taskid = advancedAddressModel.getTaskId();
		}
		//父任务实例ID
		String parentTaskId = SDK.getTaskAPI().getTaskInstance(taskid).getParentTaskInstId();
		//父任务节点ID
		String parentActivityId = SDK.getTaskAPI().getTaskInstance(parentTaskId).getActivityDefId();
		if(!UtilString.isEmpty(parentActivityId)){
			//判断是否为主管领导节点发过来
			if(!parentActivityId.equals(CmccCommon.cddwzgld_stepid)){
				//遍历人员层级
				String ext1 = model.getExt1();
				//过滤单位领导
				if(!ext1.equals(CmccCommon.user_leave2)){
					return true;
				}
			}else{
				return true;
			}
		}
		return false;
	}
}
