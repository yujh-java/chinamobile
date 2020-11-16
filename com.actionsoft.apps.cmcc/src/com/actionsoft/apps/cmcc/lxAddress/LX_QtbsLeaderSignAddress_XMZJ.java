package com.actionsoft.apps.cmcc.lxAddress;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;


/** 
* @author yujh
* @version 创建时间：2020年5月21日 上午9:33:11 
* 研究院立项-项目总监-送相关部所领导会签环节
*/
public class LX_QtbsLeaderSignAddress_XMZJ implements AddressUIFilterInterface {
	/*
	 * 获取项目总监的sql
	 */
	public String sql = "select USERID from BO_ACT_ENTERPRISE_ROLEDATA where ROLENAME = '项目总监'";
	public List<String> list_userid = new ArrayList<String>();
	
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
		
		/*
		 * 获取项目总监角色维护人员集合
		 */
		if(list_userid == null || list_userid.size() == 0){
			List<RowMap> list = DBSql.getMaps(sql);
			for(RowMap map : list){
				String userid_xmzj = map.get("USERID").toString();
				list_userid.add(userid_xmzj);
			}
		}
		String userid = model.getUID();
		if(list_userid.toString().contains(userid)){
			return true;
		}
		
		return false;
	}
}
