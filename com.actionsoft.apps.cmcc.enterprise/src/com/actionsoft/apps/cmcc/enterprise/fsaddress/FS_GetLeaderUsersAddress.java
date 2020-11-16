package com.actionsoft.apps.cmcc.enterprise.fsaddress;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;
/**
 * 企标复审流程
 * 获取部门所有处级领导---处长、副处长
 * @author chenxf
 *
 */
public class FS_GetLeaderUsersAddress
  implements AddressUIFilterInterface{
  
  	public String currentParentDeptid;
	public String pathId_currentParent;
  

  public boolean addressUIFlexCompanyFilter(UserContext arg0, 
		  CompanyModel arg1, AdvancedAddressModel arg2)
  {
    return true;
  }
  
  public boolean addressUIFlexDepartmentFilter(UserContext uc, 
		  DepartmentModel model, AdvancedAddressModel advancedAddressModel)
  {
		  if(UtilString.isEmpty(currentParentDeptid)){
			  //当前人部门ID
			  currentParentDeptid = uc.getDepartmentModel().getParentDepartmentId();
			  //当前人父部门ID全路径
			  pathId_currentParent = DepartmentCache.getModel(currentParentDeptid).getPathIdOfCache();
		  }
		//遍历部门ID全路径
		String pathId_model = model.getPathIdOfCache();
		//当前人账号
		String userid = uc.getUID();
		//集团研究院只列出处室领导
		if(userid.indexOf("@hq.cmcc") != -1){
			if (pathId_currentParent.contains(pathId_model) 
					|| pathId_model.contains(pathId_currentParent)) {
			  return true;
			}
		}else{
			//一级部门ID
			String firstDeptid = uc.getDepartmentModel().getPathIdOfCache().split("/")[0];
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
	  //人员层级
	  String ext1 = model.getExt1();
	  //当前人账号
	  String userid = uc.getUID();
	  //判断是否为研究院人员
	  if(userid.contains("@hq.cmcc")){
		  //只查出三级部所领导
		  if(ext1.equals(CmccCommon.user_leave3)){
			  return true;
		  }
		 return false;
	  }
	  return true;
  }
}
