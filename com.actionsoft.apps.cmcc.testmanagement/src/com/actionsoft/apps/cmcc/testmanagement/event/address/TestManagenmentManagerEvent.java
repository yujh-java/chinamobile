package com.actionsoft.apps.cmcc.testmanagement.event.address;

import java.util.List;
import java.util.Map;

import com.actionsoft.apps.cmcc.testmanagement.constant.TestManagementConst;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
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
* @author 作者 : yujh
* @version 创建时间：2019年4月8日 下午5:24:46 
* 类说明  获取测试管理经理
*/
public class TestManagenmentManagerEvent implements AddressUIFilterInterface{
	private String roleId = "";//测试管理经理ID
	private List<RowMap> usersMap =null;//角色集合
	
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		Map extendParam = advancedAddressModel.getExtendParam();
		boolean isSearch =false;
		if(null != extendParam && extendParam.size()>0){
			isSearch = extendParam.containsKey("search");
		}
		if(isSearch){
			return true;
		}
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		String deptPathId_model1 = DepartmentCache.getModel(TestManagementConst.DEPT_ALL).getPathIdOfCache();
		if(UtilString.isEmpty(roleId)){
			roleId =DBSql.getString("SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{TestManagementConst.TEST_MANAGER_NAME});
		}
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
        if(deptPathId_model.equals(deptPathId_model1)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel arg2) {
		String uid =model.getUID();
		if(uid.equals(uc.getUID())){//过滤当前登录人
			return false;
		}
		if(UtilString.isEmpty(roleId)){
			roleId =DBSql.getString("SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{TestManagementConst.TEST_MANAGER_NAME});
		}
		if(null == usersMap){
			usersMap=DBSql.getMaps("SELECT USERID FROM ORGUSERMAP WHERE ROLEID=?", new Object[]{roleId});
		}
		if(usersMap != null && usersMap.size() > 0){
			for(int i = 0;i<usersMap.size();i++){
				String userId = (String) usersMap.get(i).get("USERID");
				if(uid.trim().equals(userId)){
					return true;
				}
			}
		}
		return false;
	}

}
