package com.actionsoft.apps.cmcc.address;
/**
 * 参与者地址薄过滤事件，根据当前登录人找处长、副处长。所有流程
 * @author nch
 * @date 20170622
 */
import java.util.Iterator;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;

public class LeaderUsersAddress implements AddressUIFilterInterface {

	private String leaderDeptId = "";//处长所在部门ID

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		/*String userid = uc.getUID();
		//登录人所在路径逐级向上找处室领导，hq.cmcc:3;非hq.cmcc:3或4
		if(UtilString.isEmpty(leaderDeptId)){
			String userDeptId = uc.getDepartmentModel().getId();//登录人部门ID
			//先找处长,处长所在部门
			boolean bol = true;
			while(bol){
				Iterator<UserModel> subUserModels = UserCache.getUserListOfDepartment(userDeptId);
				while(subUserModels.hasNext()){
					UserModel userModel = subUserModels.next();
					String zwmc = userModel.getExt1();
					if(zwmc.equals(CmccConst.user_leave3) || (!userid.contains("@hq.cmcc") && CmccConst.user_leave4.equals(zwmc)) ){
						leaderDeptId = userDeptId;
					}
					//查询到处长，停止循环 ；否则继续向上找
					if(UtilString.isEmpty(leaderDeptId)){
						userDeptId = DepartmentCache.getModel(userDeptId).getParentDepartmentId();
						if(UtilString.isEmpty(userDeptId) || userDeptId.equals("0")){
							bol = false;
						}
					}else{//查询到处长
						bol = false;
					}
				}
			}
		}
		if(!UtilString.isEmpty(leaderDeptId)){
			//处长部门全路径
			String leaderPathDeptId = DepartmentCache.getModel(leaderDeptId).getPathIdOfCache();
			String deptpathIdofCache = model.getPathIdOfCache();//过滤部门ID全路径
			if(leaderPathDeptId.contains(deptpathIdofCache)){
				return true;
			}
		}
		return false;*/
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		if(userpathIdofCache.contains(deptPathId_model)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		/*	String userid = uc.getUID();
		String zwmc = model.getExt1();
		if(CmccConst.user_leave3.equals(zwmc) || (!userid.contains("@hq.cmcc") && CmccConst.user_leave4.equals(zwmc))){
			return true;
		}
		return false;*/
		String userid = uc.getUID();//用户账号
		String deptid_model = model.getDepartmentId();//部门ID
		int layer = DepartmentCache.getModel(deptid_model).getLayer();//部门层级
		if(layer==1){
			return false;
		}else if(userid.contains("hq.cmcc")){
			if(layer==2){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
