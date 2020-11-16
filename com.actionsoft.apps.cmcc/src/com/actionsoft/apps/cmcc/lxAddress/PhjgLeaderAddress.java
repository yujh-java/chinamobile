package com.actionsoft.apps.cmcc.lxAddress;
/**
 * 立项流程
 * 参与者地址薄过滤事件
 * 立项、结项根据需求部门，查询领导(配合研发机构处长)
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

public class PhjgLeaderAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
/*
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String deptpathIdofCache = model.getPathIdOfCache();//过滤部门ID全路径
		if(deptpathIdofCache.contains(userpathIdofCache)
				|| userpathIdofCache.contains(deptpathIdofCache)){
			return true;
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

		/*String zwmc = model.getExt1();
		String userid = model.getUID();
		if(CmccConst.user_leave3.equals(zwmc) || 
				(CmccConst.user_leave4.equals(zwmc) && !userid.contains("@hq.cmcc"))){
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
