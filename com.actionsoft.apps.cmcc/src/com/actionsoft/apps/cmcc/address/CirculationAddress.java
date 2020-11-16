package com.actionsoft.apps.cmcc.address;
/**
 * 传阅地址薄过滤事件,本部门及以下，部长、处长传阅.立项、结项相关部门落实节点
 * @author nch
 * @date 2017-6-22
 */
import java.util.List;

import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.RoleCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserMapModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class CirculationAddress implements AddressUIFilterInterface {
	private String isRoleimp = "";
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		String deptPathId = model.getPathIdOfCache();
		String userid = uc.getUID();
		String userDeptPathId = uc.getDepartmentModel().getPathIdOfCache(); // 当前登陆账户部门全路径ID
		if(userid.contains("hq.cmcc")){
			//判断是否是研发机构接口人
			if(UtilString.isEmpty(isRoleimp)){
				isRoleimp = "false";
				List<UserMapModel> listMapModels = SDK.getORGAPI().getUserMaps(userid);
				if(listMapModels != null && listMapModels.size() > 0){
					for(int i = 0;i < listMapModels.size();i++){
						String roleId = listMapModels.get(i).getRoleId();
						String roleName = RoleCache.getModel(roleId).getName();
						if("研发机构项目接口人".equals(roleName) 
								|| "需求部门接口人".equals(roleName)){
							isRoleimp = "true";
							break;
						}
					}
				}
			}
			//研发机构接口人、需求部门接口人，显示所有部门，所有人员
			if("true".equals(isRoleimp)){
				String[] arrDeptid = userDeptPathId.split("/");
				int modellayer = model.getLayer();
				if(arrDeptid.length>=2){
					String firstDeptid = arrDeptid[0];
					String secondDeptid = arrDeptid[1];
					if(deptPathId.contains(secondDeptid) && deptPathId.contains(firstDeptid)){
						return true;
					}else if(modellayer==1 && deptPathId.contains(firstDeptid)){
						return true;
					}
				}else if(deptPathId.contains(userDeptPathId)){
					return true;
				}
			}else{
				//过滤部门，显示本部门及子部门
				if(userDeptPathId.contains(deptPathId) || deptPathId.contains(userDeptPathId)){
					return true;
				}
			}
		}else {
			//查询登录人根部门
			String userPathDeptId = uc.getDepartmentModel().getPathIdOfCache();
			String zoneDeptid = userPathDeptId.split("/")[0];
			if(deptPathId.contains(zoneDeptid)){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		//过滤角色，不同角色显示不同人员
		/*String userid = uc.getUID();
		String zwmc_user = uc.getUserModel().getExt1();//当前登录人职位
		if(model.getUID().equals(uc.getUID())){
			return false;
		}
		//hq.cmcc普通员工职位：4，5，6或空；非hq.cmcc普通员工为：5，6或空
		//普通员工没有传阅权限
		if(UtilString.isEmpty(zwmc_user)
				|| zwmc_user.equals(CmccConst.user_leave5) || zwmc_user.equals(CmccConst.user_leave6)){
			return false;
		}else if(userid.contains("@hq.cmcc") && zwmc_user.equals(CmccConst.user_leave4)){
			return false;
		}else{
			return true;
		}*/
		String userid = uc.getUID();
		if(userid.contains("hq.cmcc")){
			//当前用户为研发机构接口人，显示所有人员
			if(UtilString.isEmpty(isRoleimp)){
				isRoleimp = "false";
				List<UserMapModel> listMapModels = SDK.getORGAPI().getUserMaps(userid);
				if(listMapModels != null && listMapModels.size() > 0){
					for(int i = 0;i < listMapModels.size();i++){
						String roleId = listMapModels.get(i).getRoleId();
						String roleName = RoleCache.getModel(roleId).getName();
						if("研发机构项目接口人".equals(roleName) 
								|| "需求部门接口人".equals(roleName)){
							isRoleimp = "true";
							break;
						}
					}
				}
			}
			if("true".equals(isRoleimp)){
				return true;
			}else{
				//显示部门及以下人员，部门层级不小于登录人所在部门层级
				int userLayer = uc.getDepartmentModel().getLayer();
				String deptid_model = model.getDepartmentId();
				int layer_model = DepartmentCache.getModel(deptid_model).getLayer();
				if(layer_model >= userLayer){
					return true;
				}else {
					return false;
				}
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
