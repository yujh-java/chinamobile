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
/**
 * 立项流程
 * 院内立项、结项、取消、变更等牵头部所领导审核节点加签事件,
 * 获取所在部门的其他同级部门、人员审批，
 * 查询所有--部所领导
 * @author nch
 * @date 20170622
 */
public class LX_QtbsLeaderSignAddress implements AddressUIFilterInterface {

	
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel arg1, AdvancedAddressModel arg2) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel arg2) {
		//过滤本部门，显示其他同级部门及子部门
//		String deptId = uc.getDepartmentModel().getId();//当前部门ID
		
		String parentDeptID= uc.getDepartmentModel().getParentDepartmentId();//上级部门ID
		String parentDeptPathId = DepartmentCache.getModel(parentDeptID).getPathIdOfCache();// 当前登陆账户上级部门全路径ID
		String deptPathId_model = model.getPathIdOfCache();
		if(parentDeptPathId.contains(deptPathId_model) 
				|| deptPathId_model.contains(parentDeptPathId)){
			//过滤本部门
//			if(model.getId().equals(deptId)){
//				return false;
//			}
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel arg2) {
		/*
		 * chenxf add 2018-05-30
		 */
		//model职位名称
		String zwmc_model = model.getExt1();
		//所有部所领导
		if(CmccConst.user_leave3.equals(zwmc_model)){
			return true;
		}
		return false;
	}
}
