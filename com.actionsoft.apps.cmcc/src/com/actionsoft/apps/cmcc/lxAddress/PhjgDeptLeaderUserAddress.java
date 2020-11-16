package com.actionsoft.apps.cmcc.lxAddress;
/**
 * 立项流程
 * 参与者地址薄过滤事件
 * 立项、结项配合机构部门领导
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

public class PhjgDeptLeaderUserAddress implements AddressUIFilterInterface {
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {

		/*String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String deptpathIdofCache = model.getPathIdOfCache();//过滤部门ID全路径
		if(deptpathIdofCache.contains(userpathIdofCache)
				|| userpathIdofCache.contains(deptpathIdofCache)){
			return true;
		}
		return false;*/
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String rootDeptId = userpathIdofCache.split("/")[0];//登录人所在部门根路径
		
		String userid = uc.getUID();//登录人账号
		int layer = model.getLayer();//部门层级
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		if(userid.contains("hq.cmcc")){
			if(layer == 1 || layer == 2){
				if(userpathIdofCache.contains(deptPathId_model)){
					return true;
				}
			}
		}else if(deptPathId_model.contains(rootDeptId)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		/*String zwmc = model.getExt1();
		String userid = model.getUID();
		if(CmccConst.user_leave2.equals(zwmc) || 
				(!userid.contains("@hq.cmcc") && CmccConst.user_leave1.equals(zwmc))){
			return true;
		}
		return false;*/
		String zwmc = model.getExt1();
		String userid = model.getUID();
		if(userid.contains("@hq.cmcc") && !CmccConst.user_leave2.equals(zwmc)){
			return false;
		}
		return true;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
