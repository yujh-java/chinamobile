package com.actionsoft.apps.cmcc.enterprise.address;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
/**
 * 企标管理--获取部门领导----部长、副部长
 * @author chenxf
 *
 */
public class DeptLeaderUsersAddress
  implements AddressUIFilterInterface
{
  public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2)
  {
    return true;
  }
  
  public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model, AdvancedAddressModel advancedAddressModel)
  {
	  //当前人所在部门ID全路径
    String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();
    //当前人账号
    String userid = uc.getUID();
    //部门层级
    int layer = model.getLayer();
    //遍历部门ID全路径
    String deptPathId_model = model.getPathIdOfCache();
    if (userid.contains("@hq.cmcc")) {
    	if((layer == 1 || layer == 2) 
    			&& userpathIdofCache.contains(deptPathId_model)){
    		return true;
    	}
    }else{
    	//一级部门ID
		String firstDeptid = userpathIdofCache.split("/")[0];
		//遍历的一级部门ID
		String firstDeptid_model = deptPathId_model.split("/")[0];
		if(firstDeptid.equals(firstDeptid_model)){
			return true;
		}
    }
    return false;
  }
  
  public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel)
  {
	 //职位等级
    String zwmc = model.getExt1();
    //当前人员
    String userid = uc.getUID();
    if ((userid.contains("@hq.cmcc")) 
    		&& (!CmccCommon.user_leave2.equals(zwmc))) {
      return false;
    }
    return true;
  }
}
