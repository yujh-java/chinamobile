package com.actionsoft.apps.cmcc.enterprise.address;

import java.util.List;

import com.actionsoft.apps.cmcc.enterprise.CmccConst;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserMapModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class QBGKDeptImpUserAddress implements AddressUIFilterInterface{
	private String roleId = "";//申请单位接口人ID
	
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/*@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		// 获得流程创建人的全路径
		String userid = SDK.getProcessAPI().getInstanceById(advancedAddressModel.getInstanceId()).getCreateUser();// 获取发起人id
		String deptid_current = UserCache.getModel(userid).getDepartmentId();// 根据发起人id得到部门路径
		String userpathIdofCache = DepartmentCache.getModel(deptid_current).getPathIdOfCache();// 根据发起人部门路径得到部门全路径
		String secondDeptid = "";
		if(!UtilString.isEmpty(userpathIdofCache)){
			String[] userDeptPathIdArr = userpathIdofCache.split("/");
			if(userDeptPathIdArr.length >0){
				secondDeptid = userDeptPathIdArr[1];
			}
		}
		String userpathSecond = DepartmentCache.getModel(secondDeptid).getPathIdOfCache();//二级部门全路径
		String deptPathId_model = model.getPathIdOfCache();// 部门全路径
		
		if (userpathSecond.contains(deptPathId_model)) {
			return true;
		}
		return false;

	}*/
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		// 获得流程创建人的全路径
		String userid = SDK.getProcessAPI().getInstanceById(advancedAddressModel.getInstanceId()).getCreateUser();// 获取发起人id
		String deptid_current = UserCache.getModel(userid).getDepartmentId();// 根据发起人id得到部门路径
		String userpathIdofCache = DepartmentCache.getModel(deptid_current).getPathIdOfCache();// 根据发起人部门路径得到部门全路径
		String secondDeptid = "";
		String fistDeptid = "";
		if(!UtilString.isEmpty(userpathIdofCache)){
			String[] userDeptPathIdArr = userpathIdofCache.split("/");
			if(userDeptPathIdArr.length >0){
				secondDeptid = userDeptPathIdArr[1];
				fistDeptid = userDeptPathIdArr[0];
			}
		}
		String userpathFist = DepartmentCache.getModel(fistDeptid).getPathIdOfCache();//一级部门全路径
		System.err.println("一级"+userpathFist);
		String userpathSecond = DepartmentCache.getModel(secondDeptid).getPathIdOfCache();//二级部门全路径
		System.err.println("二级"+userpathSecond);
		String deptPathId_model = model.getPathIdOfCache();// 部门全路径
		System.err.println("全"+deptPathId_model);
		if(uc.getUID().contains("hq.cmcc")){
			if (userpathSecond.contains(deptPathId_model)) {
				return true;
			}
		}else{
			if (userpathFist.contains(deptPathId_model)) {
				return true;
			}
		}
		
			
		
		    return false;

	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel arg2) {
		String uid = model.getUID();
		if(UtilString.isEmpty(roleId)){
			roleId =DBSql.getString("SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{CmccConst.deptqbRolename});
			
		}
		List<UserMapModel> userMaps = SDK.getORGAPI().getUserMaps(uid);
		/*if(uid.equals("shenjun@cmss.cmcc")){
			System.err.println(userMaps.size());
		}*/
		if(null == userMaps || userMaps.size()<1){
			return false;
		}else{
			for (UserMapModel userMapModel : userMaps) {
				/*if(uid.equals("shenjun@cmss.cmcc")){
					System.err.println("roleId："+roleId);
					System.err.println("userMapModel.getRoleId()："+userMapModel.getRoleId());
				}*/
				String usermapmodel=userMapModel.getDepartmentId();
					if(userMapModel.getRoleId().equals(roleId)){//含有接口人角色显示
						return true;
						
					}
				
				
			}
		}
		return false;
	}

}
