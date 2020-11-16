package com.actionsoft.apps.cmcc.enterprise.address;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.enterprise.CmccConst;
import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserMapModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

/**
 * 企标主流程企标归口人员审核节点地址簿事件
 * 承担单位的企标归口人
 * @author wxx
 *
 */
public class GKDeptImpUserAddress implements AddressUIFilterInterface{
	private List<BO> list = null;//记录牵头需求部门、配合需求部门集合
	private String bmjkrId = "";//企标管理员角色ID
	private List<RowMap> listRowMap = new ArrayList<RowMap>();//角色下所有人员
	private List<String> listDeptPathId = new ArrayList<String>();//角色下所有人员部门全路径

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String userid = SDK.getProcessAPI().getInstanceById(advancedAddressModel.getInstanceId()).getCreateUser();// 获取发起人id
		String deptid_current = UserCache.getModel(userid).getDepartmentId();// 根据发起人id得到部门路径
		
        //String deptPathId_model1 = DepartmentCache.getModel("5b93a3f7-3ae2-4831-9fcc-d8809ffc462c").getPathIdOfCache();
        String deptPathId_model1 = DepartmentCache.getModel(CmccCommon.deptid_sjy).getPathIdOfCache();
        String roleId =DBSql.getString("SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{CmccConst.qbdeptRolename});
        List<UserMapModel> userMapsByDept = SDK.getORGAPI().getUserMapsByDept(model.getId());
        boolean flag=false;
        for (UserMapModel userMapModel : userMapsByDept) {
        	if(userMapModel.getRoleId().equals(roleId)){
        		flag=true;
        		break;
        	}
		}
        if(flag){
        	return true;
        }
		
		
		
		/*if(deptPathId_model.equals(deptPathId_model1)){//登录人的大部门id
			return true;
		}*/
		/*if(userpathIdofCache.equals(deptid_current)){//登录人的大部门id
			return true;
		}*/
		if(userpathIdofCache.contains(deptid_current)){
			return true;
		}

		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {

		String userid = model.getUID();
		//企标归口人角色
		if(UtilString.isEmpty(bmjkrId)){
			Connection conn = null;
			try{
				conn = DBSql.open();
				bmjkrId =DBSql.getString(conn,"SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{CmccConst.qbdeptRolename});
				
				listRowMap = DBSql.getMaps(conn,"SELECT USERID FROM ORGUSERMAP WHERE ROLEID=?", new Object[]{bmjkrId});
			}catch(Exception e){
				e.printStackTrace(System.err);
			}finally{
				DBSql.close(conn);
			}
		}
		if(listRowMap != null && listRowMap.size() > 0){
			for(int i = 0;i<listRowMap.size();i++){
				String USERID = (String) listRowMap.get(i).get("USERID");
				if(userid.equals(USERID)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
