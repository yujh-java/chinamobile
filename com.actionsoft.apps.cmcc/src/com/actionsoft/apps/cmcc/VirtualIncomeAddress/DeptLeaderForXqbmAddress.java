package com.actionsoft.apps.cmcc.VirtualIncomeAddress;

/**
 * 虚拟收入
 * 参与者地址薄过滤事件
 * 根据需求部门，查询领导(部长)
 * @author zhaoxs
 * @date  2017-09-13
 */

import java.util.ArrayList;
import java.util.List;
import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;

public class DeptLeaderForXqbmAddress implements AddressUIFilterInterface {
	public List<HistoryTaskInstance> list = new ArrayList<HistoryTaskInstance>();
	public String processid = "";
	public String activityDefid = "";

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		if (UtilString.isEmpty(processid)) {
			processid = advancedAddressModel.getInstanceId();
		}
		String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径
		String deptid = DBSql.getString("SELECT QTXQBMID FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
				new Object[] { processid });
		if (!UtilString.isEmpty(deptid)) {
			String idpathCache = DepartmentCache.getModel(deptid).getPathIdOfCache();
			if (deptpathIdofCache.contains(idpathCache) || idpathCache.contains(deptpathIdofCache)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		String zwmc = model.getExt1();
		String userid = model.getUID();
		if (userid.contains("@hq.cmcc")) { 
			if(zwmc.equals(CmccConst.user_leave2)) {
				return true; 
				} 
			} else
			{ if(zwmc.equals(CmccConst.user_leave1) ||
			  zwmc.equals(CmccConst.user_leave2)) { 
				return true; 
			  } 
			}
		return false;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
