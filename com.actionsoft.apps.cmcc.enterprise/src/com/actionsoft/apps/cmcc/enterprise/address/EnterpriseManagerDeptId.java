package com.actionsoft.apps.cmcc.enterprise.address;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
/**
 * 企标管理--非重点送审流程
 * 获取集团企标管理员过滤事件
 * @author chenxf
 *
 */
public class EnterpriseManagerDeptId implements AddressUIFilterInterface{
	
	//获取集团企标管理员部门ID数组
	public String[] bmids = CmccCommon.bmid_ep_manager.split(",");
	//获取集团企标管理员所有人员部门ID全路径的集合
	public String pathDeptId_manager;
	
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, 
			CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		//判断是否有值
		if(pathDeptId_manager == null || "".equalsIgnoreCase(pathDeptId_manager)){
			if(bmids != null && bmids.length > 0){
				//遍历赋值
				for(int i = 0; i < bmids.length; i++){
					pathDeptId_manager += DepartmentCache.getModel(bmids[i]).getPathIdOfCache() + ",";
				}
			}
		}
		//遍历部门ID的全路径
		String pathDeptId = model.getPathIdOfCache();
		//包含于中取出
		if(pathDeptId_manager != null && !"".equals(pathDeptId_manager)){
			if(pathDeptId_manager.contains(pathDeptId)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
			UserModel model, AdvancedAddressModel advancedAddressModel) {
		//遍历的账号
		String userid = model.getUID();
		//判断是否有兼职人员（集团企标管理员）
		if(CmccCommon.userid_ep_manager != null && !"".equals(CmccCommon.userid_ep_manager)){
			//包含于其中取出
			if(CmccCommon.userid_ep_manager.contains(userid)){
				return true;
			}
		}
		return false;
	}
}
