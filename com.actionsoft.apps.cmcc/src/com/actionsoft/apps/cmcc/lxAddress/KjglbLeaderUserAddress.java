package com.actionsoft.apps.cmcc.lxAddress;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
/**
 * 立项流程
 * 参与者地址薄过滤事件
 * 研究院科技管理部处室领导
 * 立项、结项牵头研发机构、配合研发机构项目管理领导节点
 * @author nch
 * @date 20170622
 */
public class KjglbLeaderUserAddress implements AddressUIFilterInterface {
	private String kjglbDeptId = "";// 记录科技管理部部门ID

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {

		if (UtilString.isEmpty(kjglbDeptId)) {
			kjglbDeptId = DBSql.getString("SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?",
					new Object[] { "00030087000800000000" });
		}
		String userpathIdofCache = "";// 科技管理部全路径
		DepartmentModel deptModel = DepartmentCache.getModel(kjglbDeptId);
		if (deptModel != null) {
			userpathIdofCache = deptModel.getPathIdOfCache();
		}
		String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径
		if (deptpathIdofCache.contains(userpathIdofCache)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		
		/*
		 * chenxf add 2018-05-30
		 */
		//model职位名称
		String zwmc_model = model.getExt1();
		//只查找部所领导
		if(CmccConst.user_leave3.equals(zwmc_model)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
