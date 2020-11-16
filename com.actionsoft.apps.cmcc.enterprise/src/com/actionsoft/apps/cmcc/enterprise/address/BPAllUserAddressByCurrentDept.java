package com.actionsoft.apps.cmcc.enterprise.address;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;

/**
 * 企标报批--查询当前人所在部所下所有人
 * @author wuxx
 *
 */
public class BPAllUserAddressByCurrentDept implements AddressUIFilterInterface{
	public String currentDeptid;//当前人部门ID
	public String pathCurrentDeptid;//当前人部门ID全路径
	public String userid;//当前人账号

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}
	/**
	 * 过滤部所
	 */
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			DepartmentModel model, AdvancedAddressModel arg2) {
		if(UtilString.isEmpty(currentDeptid)){
			currentDeptid = uc.getDepartmentModel().getId();
		}
		if(UtilString.isEmpty(pathCurrentDeptid)){
			pathCurrentDeptid = DepartmentCache.getModel(currentDeptid).getPathIdOfCache();
		}
		//遍历部门ID全路径
		String pathDeptid = model.getPathIdOfCache();
		//当前人账号
		if(UtilString.isEmpty(userid)){
			userid = uc.getUID();
		}
		/*
		 * 如果是研究院的，则查询本部所所有人，
		 * 否则查找本单位所有人
		 */
		if(userid.contains("@hq.cmcc")){
			if(pathCurrentDeptid.contains(pathDeptid)){
				return true;
			}
		}else{
			//当前人的一级部门ID
			String firstDeptid = pathCurrentDeptid.split("/")[0];
			//遍历的一级部门ID
			String firstDeptid_model = pathDeptid.split("/")[0];
			if(firstDeptid.equals(firstDeptid_model)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 过滤人员
	 */
	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
			UserModel model, AdvancedAddressModel arg2) {
		//遍历的人员层级
		String layer = model.getExt1();
		//当前人账号
		String userid = uc.getUID();
		/*
		 * 判断是否为研究院
		 */
		if(userid.contains("@hq.cmcc")){
			return true;
			//过滤单位领导，即二级领导
			/*if(layer.equals(CmccCommon.user_leave2) || layer.equals("")){
				return false;
			}*/
		}
		return true;
	}
}
