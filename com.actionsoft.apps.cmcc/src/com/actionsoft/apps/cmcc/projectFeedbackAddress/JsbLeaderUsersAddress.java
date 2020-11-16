package com.actionsoft.apps.cmcc.projectFeedbackAddress;

/**一般委托流程
 * 参与者地址薄过滤事件，领导
 * 总部处长
 * @author zhaoxs
 * @date 2017-06-21
 */
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;


public class JsbLeaderUsersAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		String userParentID = uc.getDepartmentModel().getParentDepartmentId();
		String userParentPathCache = DepartmentCache.getModel(userParentID).getPathIdOfCache();
		String deptPathCache = model.getPathIdOfCache();// 过滤部门全路径
		if (deptPathCache.contains(userParentPathCache)||userParentPathCache.contains(deptPathCache)) {
			return true;
		}
		return false;

	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		String userid = uc.getUID();
		String deptID = model.getDepartmentId();
		int layer = DepartmentCache.getModel(deptID).getLayer();
		if (layer == 1) {
			return false;
		} else if (userid.contains("@hq.cmcc")) {
			if (layer == 2) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext uc, CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
