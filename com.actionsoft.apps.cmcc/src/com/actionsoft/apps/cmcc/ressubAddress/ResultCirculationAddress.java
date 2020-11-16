package com.actionsoft.apps.cmcc.ressubAddress;
/**
 * 成果提交
 * 成果提交技术部逐级审批
 * @author nch
 * @date 20170622
 */
import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;

public class ResultCirculationAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel arg1, AdvancedAddressModel arg2) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advanceModel) {
		String userDeptPathId = uc.getDepartmentModel().getPathIdOfCache(); // 当前登陆账户部门全路径ID
		String deptPathId = model.getPathIdOfCache();
		//过滤部门，显示本部门及子部门
		if(userDeptPathId.contains(deptPathId) || deptPathId.contains(userDeptPathId)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advanceModel) {
		/*String zwmc_model = model.getExt1();
		String zwmc_user = uc.getUserModel().getExt1();//当前登录人职位
		String userid = uc.getUID();
		//过滤角色，不同角色显示不同人员
		if(UtilString.isEmpty(zwmc_user)){
			return false;
		}else{
			if(UtilString.isEmpty(zwmc_model)){
				if(!userid.contains("@hq.cmcc") && CmccConst.user_leave4.equals(zwmc_user)){
					return true;
				}else if(CmccConst.user_leave3.equals(zwmc_user)){
					return true;
				}
			}else{
				int zwmc_user_integer = Integer.parseInt(zwmc_user);
				int zwmc_model_integer = Integer.parseInt(zwmc_model);
				if(zwmc_model_integer == zwmc_user_integer+1){
					return true;
				}
			}
		}
		return false;*/
		String userDeptPathId = uc.getDepartmentModel().getPathIdOfCache(); // 当前登陆账户部门全路径ID
		String deptid_model = model.getDepartmentId();
		String deptPathids_model = DepartmentCache.getModel(deptid_model).getPathIdOfCache();
		if(deptPathids_model.contains(userDeptPathId)){
			return true;
		}
		return false;
	}

}
