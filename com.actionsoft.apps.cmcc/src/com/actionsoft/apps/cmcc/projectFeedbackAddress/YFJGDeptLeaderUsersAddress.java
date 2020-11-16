package com.actionsoft.apps.cmcc.projectFeedbackAddress;
/**
 * 一般委托流程
 * 参与者地址薄过滤事件承担部所处长
 * @author zhaoxs
 * @date  2017-06-21
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

public class YFJGDeptLeaderUsersAddress implements AddressUIFilterInterface {
	String yfjgDeptId = "";
	String yfjgId = "";
	String id = "";

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		/*
		String processid = advancedAddressModel.getInstanceId();
		if (UtilString.isEmpty(yfjgDeptId)) {
			yfjgDeptId = DBSql.getString("SELECT QTXQBMID FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
					new Object[] { processid });
		}
		String yfjgPathCache = DepartmentCache.getModel(yfjgDeptId).getPathIdOfCache();
		String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径
		if (yfjgPathCache.contains(deptpathIdofCache) || deptpathIdofCache.contains(yfjgPathCache)) {
			return true;
		}
		return false;
		*/
		String processid = advancedAddressModel.getInstanceId();
		if (UtilString.isEmpty(yfjgDeptId)) {
			yfjgDeptId = DBSql.getString("SELECT QTXQBMID FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
					new Object[] { processid });
		}
		if (UtilString.isEmpty(yfjgId)) {
			yfjgId = DBSql.getString("SELECT QTYFJGBM FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
					new Object[] { processid });
		}
		if (UtilString.isEmpty(id)) {
			id = DBSql.getString("SELECT QTYFJGBMID FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
					new Object[] { processid });
		}
		String yfjgPathCache;
		if(CmccConst.deptNo_yjy.equals(yfjgId)){
			yfjgPathCache = DepartmentCache.getModel(yfjgDeptId).getPathIdOfCache();
		}else{
			yfjgPathCache = DepartmentCache.getModel(id).getPathIdOfCache();
		}
		String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径
		if(!UtilString.isEmpty(yfjgPathCache)&&deptpathIdofCache.contains(yfjgPathCache)||yfjgPathCache.contains(deptpathIdofCache))
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		/*
		String zwmc = model.getExt1();
		String userid = model.getUID();
		if (userid.contains("@hq.cmcc")) {
			if (zwmc.equals(CmccConst.user_leave3)) {
				return true;
			}
		} else {
			if ((zwmc.equals(CmccConst.user_leave3)) || (zwmc.equals(CmccConst.user_leave4))) {
				return true;
			}
		}
		return false;
		*/
		
		
		String userid = model.getUID();
		int layer = DepartmentCache.getLayer(model.getDepartmentId());
		if(layer==1){
			return false;
		}else if(userid.contains("@hq.cmcc")){
			if(layer==2){
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
