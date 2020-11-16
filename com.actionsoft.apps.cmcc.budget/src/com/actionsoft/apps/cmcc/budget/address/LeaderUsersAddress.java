package com.actionsoft.apps.cmcc.budget.address;

import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.sdk.local.SDK;
/**
 * 获取处级领导---处长、副处长
 * @author chenxf
 *
 */
public class LeaderUsersAddress
  implements AddressUIFilterInterface
{
  public boolean addressUIFlexCompanyFilter(UserContext arg0, 
		  CompanyModel arg1, AdvancedAddressModel arg2)
  {
    return true;
  }
  
  public boolean addressUIFlexDepartmentFilter(UserContext uc, 
		  DepartmentModel model, AdvancedAddressModel advancedAddressModel)
  {
	  
		//流程实例ID
		String bindid = advancedAddressModel.getInstanceId();
		//当前流程创建人部门ID
		String createDeptId = SDK.getProcessAPI().getInstanceById(bindid).getCreateUserDeptId();
		String pathId_current = DepartmentCache.getModel(createDeptId).getPathIdOfCache();
		//遍历部门ID全路径
		String pathId_model = model.getPathIdOfCache();
		//当前人账号
		String userid = uc.getUID();
		//集团研究院只列出处室领导
		if(userid.indexOf("@hq.cmcc") != -1){
			if (pathId_current.contains(pathId_model)) {
			  return true;
			}
		}else{
			//一级部门ID
			String firstDeptid = pathId_current.split("/")[0];
			//遍历的一级部门ID
			String firstDeptid_model = pathId_model.split("/")[0];
			if(firstDeptid.equals(firstDeptid_model)){
				return true;
			}
		}
		return false;
  }
  
  public boolean addressUIFlexUserFilter(UserContext uc, 
		  UserModel model, AdvancedAddressModel advancedAddressModel)
  {
    String userid = uc.getUID();
    
    String deptId = model.getDepartmentId();
    
    int layer = DepartmentCache.getModel(deptId).getLayer();
    if (layer == 1) {
      return false;
    }
    if ((userid.contains("hq.cmcc")) && 
      (layer == 2)) {
      return false;
    }
    return true;
  }
}
