package com.actionsoft.apps.cmcc.lxAddress;
/**
 * 立项流程
 * 参与者地址薄过滤事件
 * 立项、结项研发机构处室领导
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
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;

public class YfjgLeaderUserAddress implements AddressUIFilterInterface {
	private String yfjgDeptId = "";
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {

		String processid = advancedAddressModel.getInstanceId();
		if(UtilString.isEmpty(yfjgDeptId)){
			yfjgDeptId = DBSql.getString("SELECT QTYFJGBMID FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?", new Object[]{processid});
		}
		String userpathIdofCache = "";//牵头研发机构部门全路径
		DepartmentModel deptModel = DepartmentCache.getModel(yfjgDeptId);
		if(deptModel != null){
			userpathIdofCache = deptModel.getPathIdOfCache();
		}
		String deptpathIdofCache = model.getPathIdOfCache();//过滤部门ID全路径
		if(deptpathIdofCache.contains(userpathIdofCache)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {

		/*String zwmc = model.getExt1();
		if(zwmc.equals(CmccConst.user_leave3)){
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
			CompanyModel model, AdvancedAddressModel advancedAddressModel) {
		return true;
	}

}
