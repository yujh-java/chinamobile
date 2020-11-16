package com.actionsoft.apps.cmcc.enterprise.fs;


import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.enterprise.CmccConst;
import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
/**
 * 企标复审流程
 * 获取需求部门企标管理员
 * @author chenxf
 *
 */
public class FS_Sub_GetRemandManagerAddress implements AddressUIFilterInterface{
	//获取复审需求部门ID
	public String sql_remandDeptId = "select DEMAND from BO_ACT_CMCC_PROCESSDATA where PROCESSID = ?";
	//流程实例ID
	public String processId = "";
	//需求部门ID
	public String remandDeptId = "";
	//需求部门管理员
	public String remandPeople = "";
	public String[] peoples;
	//所有需求部门企标管理员所在部门全路径的集合
	public List<String> list = new ArrayList<String>();
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}
	
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		
		if(UtilString.isEmpty(processId)){
			processId = advancedAddressModel.getInstanceId();
			remandDeptId = DBSql.getString(sql_remandDeptId, new Object[]{ processId });
			System.err.println("=====需求部门ID："+remandDeptId+"=======");
			
		}
		//遍历的部门全路径ID
//		String pathDeptId = model.getPathIdOfCache();
		//遍历部门ID
		String deptid = model.getId();
		
		//获取需求部门企标管理员所有人
		if(UtilString.isEmpty(remandPeople) && !UtilString.isEmpty(remandDeptId)){
			CmccCommon common = new CmccCommon();
			//remandPeople = common.getEnterpriseManager(CmccCommon.qbgly_role, remandDeptId);
			//remandPeople = common.getEnterpriseManager(CmccConst.deptqbRolename, remandDeptId);
			remandPeople = common.getEnterpriseManager("企标管理员", remandDeptId);
			System.err.println("=====需求部门企标管理员："+remandPeople+"=======");
		}
		if(!UtilString.isEmpty(remandPeople)){
			if(peoples == null){
				//拆分
				peoples = remandPeople.split(" ");
				//遍历
				for(int i = 0;i < peoples.length;i++){
					/*
					 * 此人所在部门ID
					 */
					String people = peoples[i];
					String deptId = UserCache.getModel(people).getDepartmentId();
					//需求部门企标管理员所在部门全路径
					String pathDeptId_remand = DepartmentCache.getModel(deptId).getPathIdOfCache();
					String[] deptid_remands = pathDeptId_remand.split("/");
					for(int j = 0;j < deptid_remands.length;j++){
						String deptid_remand = deptid_remands[j];
						list.add(deptid_remand);
					}
				}
				System.err.println("=====需求部门企标管理员部门全路径集合："+list+"=======");
			}
		}
		if(list != null && list.size() > 0 && list.contains(deptid)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
						UserModel model, AdvancedAddressModel advancedAddressModel) {
		
		if(UtilString.isEmpty(processId)){
			processId = advancedAddressModel.getInstanceId();
			remandDeptId = DBSql.getString(sql_remandDeptId, new Object[]{ processId });
		}
		//遍历用户
		String userid = model.getUID();
		//获取需求部门企标管理员所有人
		if(UtilString.isEmpty(remandPeople) && !UtilString.isEmpty(remandDeptId)){
			CmccCommon common = new CmccCommon();
			//remandPeople = common.getEnterpriseManager(CmccCommon.qbgly_role, remandDeptId);
			remandPeople = common.getEnterpriseManager(CmccConst.deptqbRolename, remandDeptId);
		}
		if(remandPeople.contains(userid)){
			return true;
		}
		return false;
	}
}
