package com.actionsoft.apps.cmcc.address;
/**
 * 转办地址薄事件，同级同角色。所有流程转办功能
 * @author nch
 * @date 20170622
 */
import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;

public class TurntodoAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		//过滤部门，显示本部门
		String userDeptPathId = uc.getDepartmentModel().getPathIdOfCache(); // 当前登陆账户部门全路径ID
		String deptPathId = model.getPathIdOfCache();
		if(userDeptPathId.contains(deptPathId)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		/*//过滤角色，不同角色显示不同人员
		String zwmc = uc.getUserModel().getExt1();//当前登录人职位
		String zwmc_model = model.getExt1();
		String userid = uc.getUID();
		if(UtilString.isEmpty(zwmc)){
			if(UtilString.isEmpty(zwmc_model)){
				return true;
			}else if(zwmc_model.equals(CmccConst.user_leave5) || zwmc_model.equals(CmccConst.user_leave6) ||
					(zwmc_model.equals(CmccConst.user_leave4) && userid.contains("hq.cmcc"))){
				return true;
			}
		}else{
			if(!UtilString.isEmpty(zwmc_model) && zwmc.equals(zwmc_model)){
				if(uc.getUID().equals(model.getUID())){
					return false;
				}else{
					return true;
				}
			}
		}
		return false;*/
		String userDeptid = uc.getDepartmentModel().getId();
		String deptid_model = model.getDepartmentId();
		if(userDeptid.equals(deptid_model)){
			return true;
		}
		return false;
	}

}
