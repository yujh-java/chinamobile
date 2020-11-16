package com.actionsoft.apps.cmcc.budget.address;
/**
 * 预算归口管理部门会签节点地址簿事件
 * 显示研究院下面的所有部门，人员扩展字段只等于3的人员
 * @author wxx
 *
 */
import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.budget.CmccConst;
import com.actionsoft.apps.cmcc.budget.util.CmccCommon;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;

public class YsLeaderUsersAddress implements AddressUIFilterInterface{
	private String leaderDeptId = "";//处长所在部门ID
	private List<String> list_userPathids  = new ArrayList<String>();//部长所在部门全路径 
	private List<String> list_userids  = new ArrayList<String>();//部长角色所有人员账号
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		//查询职位为1.2.3的部门负责人以及未注销的人员信息
		//List<RowMap> listRowMap = DBSql.getMaps("SELECT * FROM ORGUSER WHERE EXT1 = ? AND CLOSED = ?", new Object[]{CmccConst.user_leave3,0});
		/*if(listRowMap != null && listRowMap.size() > 0){
			for(int i =0;i<listRowMap.size();i++){
				String userid = listRowMap.get(i).getString("USERID");
				String ext1 = listRowMap.get(i).getString("EXT1");//职位
				String deptid = listRowMap.get(i).getString("DEPARTMENTID");
					//部门全路径
					String deptPathid = DepartmentCache.getModel(deptid).getPathIdOfCache();
					list_userPathids.add(deptPathid);
					list_userids.add(userid);
				
				String deptPathidModel = model.getPathIdOfCache();//过滤部门全路径
				if(list_userPathids.toString().contains(deptPathidModel)){
					return true;
				}
			}
		}*/
			
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		//String deptPathId_model1 = DepartmentCache.getModel("3a20fc8a-7157-448d-ad3c-8f82feba842e").getPathIdOfCache();
        String deptPathId_model1 = DepartmentCache.getModel(CmccCommon.deptid_yjy).getPathIdOfCache();
		
		if(deptPathId_model1.contains(deptPathId_model) || deptPathId_model.contains(deptPathId_model1)){
			return true;
		}

		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		/*	String userid = uc.getUID();
		String zwmc = model.getExt1();
		if(CmccConst.user_leave3.equals(zwmc) || (!userid.contains("@hq.cmcc") && CmccConst.user_leave4.equals(zwmc))){
			return true;
		}
		return false;*/
		String userid = uc.getUID();//用户账号
		String kzzd=model.getExt1();
		
		if(!CmccConst.user_leave3.equals("3") || !"3".equals(kzzd)){
			return false;
		}
		/*String deptid_model = model.getDepartmentId();//部门ID
		int layer = DepartmentCache.getModel(deptid_model).getLayer();//部门层级
		if(layer==1){
			return false;
		}else if(userid.contains("hq.cmcc")){
			if(layer==2){
				return false;
			}
		}*/
		return true;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}



}
