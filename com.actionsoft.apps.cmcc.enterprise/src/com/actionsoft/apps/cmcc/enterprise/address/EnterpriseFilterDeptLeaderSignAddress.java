package com.actionsoft.apps.cmcc.enterprise.address;
/**
 * 企标流程
 * 送技术部内部会签环节，部门领导的过滤
 * @author nch
 * @date 20170622
 */
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;

public class EnterpriseFilterDeptLeaderSignAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advanceModel) {
		String userDeptPathId = uc.getDepartmentModel().getPathIdOfCache(); // 当前登陆账户部门全路径ID
		String rootDeptId = userDeptPathId.split("/")[0];//登录人所在部门根路径
		String deptPathId = model.getPathIdOfCache();
		
		String userid = uc.getUID();
		if(userid.contains("hq.cmcc")){
			//研究院下的去第二个部门ID
			String parentDeptId = userDeptPathId.split("/")[1];
			//获取部门全路径
			String pathParentDeptid = DepartmentCache.getModel(parentDeptId).getPathIdOfCache();
			//显示本部门及以下
			if(pathParentDeptid.contains(deptPathId) || deptPathId.contains(pathParentDeptid)){
				return true;
			}
		}else if(deptPathId.contains(rootDeptId)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advanceModel) {
		String deptid_model = model.getDepartmentId();//过滤部门ID
		int layer = DepartmentCache.getModel(deptid_model).getLayer();//部门层级
		String userid = uc.getUID();//当前登录人账号
		if(userid.contains("hq.cmcc")){
			if(layer==2){
				return false;
			}
		}
		return true;
	}
}
