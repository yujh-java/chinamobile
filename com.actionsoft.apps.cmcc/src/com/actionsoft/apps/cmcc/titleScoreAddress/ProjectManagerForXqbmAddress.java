package com.actionsoft.apps.cmcc.titleScoreAddress;

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
 * 结题评分项目经理地址簿过滤
 * @author zhaoxs
 * @date  2017-10-30
 * 
 */
public class ProjectManagerForXqbmAddress implements AddressUIFilterInterface {
	public String deptid = "";
	
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model, AdvancedAddressModel Admodel) {
		String userPathCache = model.getPathIdOfCache();//过滤部门全路径
		String processid = Admodel.getInstanceId();//流程实例ID
		if (UtilString.isEmpty(deptid)) {
			deptid = DBSql.getString("SELECT SUBRESULTXQBM FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
					new Object[] { processid });
		}
		String deptPathCache = DepartmentCache.getModel(deptid).getPathIdOfCache();
		if(userPathCache.contains(deptPathCache)||deptPathCache.contains(userPathCache)){
		return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel Admodel) {
		String userid = model.getUID();
		int layer = DepartmentCache.getLayer(model.getDepartmentId());
		if (layer == 1) {
			return false;
		} else if (userid.contains("@hq.cmcc")) {
			if (layer == 2) {
				return false;
			}
		}
		return true;
	}

}
