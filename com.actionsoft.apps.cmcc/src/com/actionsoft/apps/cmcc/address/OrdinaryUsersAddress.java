package com.actionsoft.apps.cmcc.address;
/**
 * 参与者地址薄过滤事件,根据当前登录人，获取项目经理(普通员工)。所有流程
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

public class OrdinaryUsersAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String deptpathIdofCache = model.getPathIdOfCache();//过滤部门ID全路径
		if(userpathIdofCache.contains(deptpathIdofCache)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		/*String zwmc = model.getExt1();
		String userid = uc.getUID();
		if(UtilString.isEmpty(zwmc) || zwmc.equals(CmccConst.user_leave5) || zwmc.equals(CmccConst.user_leave6)
				|| (userid.contains("@hq.cmcc") && zwmc.equals(CmccConst.user_leave4))){
			return true;
		}
		return false;*/
		String userid = uc.getUID();//用户账号
		String deptid = uc.getDepartmentModel().getId();//当前人部门ID
		String deptid_model = model.getDepartmentId();//部门ID
		int layer_model = DepartmentCache.getModel(deptid_model).getLayer();//部门层级
		int layer = DepartmentCache.getModel(deptid).getLayer();//部门层级
		if(layer==1){
			return true;
		}else{
			if(layer_model==1){
				return false;
			}else if(userid.contains("hq.cmcc")){
				if(layer_model==2){
					return false;
				}
			}
			return true;
		}
//		if(layer==1){
//			return false;
//		}else if(userid.contains("hq.cmcc")){
//			if(layer==2){
//				return false;
//			}
//		}
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
