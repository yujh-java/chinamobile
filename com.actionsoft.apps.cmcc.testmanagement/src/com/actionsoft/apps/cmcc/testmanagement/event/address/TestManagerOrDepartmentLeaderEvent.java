package com.actionsoft.apps.cmcc.testmanagement.event.address;
import java.util.List;
import java.util.Map;
import com.actionsoft.apps.cmcc.testmanagement.constant.TestManagementConst;
import com.actionsoft.apps.cmcc.testmanagement.util.InterfaceUtil;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserMapModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.constant.AddressUIConst;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

import net.sf.json.JSONObject;

/** 
* @author yujh 
* @version 创建时间：2018年12月20日 上午11:44:34 
* 测试管理经理以及部所领导地址簿专用事件，解决转办阅知并存问题
*/
public class TestManagerOrDepartmentLeaderEvent implements AddressUIFilterInterface{
	private String roleId = "";//测试管理经理ID
	private List<RowMap> usersMap =null;//角色集合
	private String deptId ="";//牵头部所ID
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		Map extendParam = advancedAddressModel.getExtendParam();
		boolean isSearch =false;
		if(null != extendParam && extendParam.size()>0){
			isSearch = extendParam.containsKey("search");
		}
		if(AddressUIConst.ADDRESS_TASK_TRANSFER.equals(advancedAddressModel.getTransType()) && isSearch){//转办
			return true;
		}else if(AddressUIConst.ADDRESS_TASK_TRANSFER.equals(advancedAddressModel.getTransType())){//转办
			String deptPathId_model = model.getPathIdOfCache();//部门全路径
			String deptPathId_model1 = DepartmentCache.getModel(TestManagementConst.DEPT_ALL).getPathIdOfCache();
			if(UtilString.isEmpty(roleId)){
				roleId =DBSql.getString("SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{TestManagementConst.TEST_MANAGER_NAME});
			}
			List<UserMapModel> userMapsByDept = SDK.getORGAPI().getUserMapsByDept(model.getId());
	        for (UserMapModel userMapModel : userMapsByDept) {
	        	if(userMapModel.getRoleId().equals(roleId)){
	        		return true;
	        	}
			}
	        if(deptPathId_model.equals(deptPathId_model1)){
				return true;
			}
			return false;
		}else {// 传阅
			/*InterfaceUtil interfaceUtil = new InterfaceUtil();
			//获取项目牵头部门ID
			if(deptId.equals("")){
				String projectInfo = interfaceUtil.getProjectInfo(advancedAddressModel.getInstanceId());
				JSONObject projectJson = JSONObject.fromObject(projectInfo);
				deptId =projectJson.getString("qtxqbmId");
			}
			String userpathIdofCache = DepartmentCache.getModel(deptId).getPathIdOfCache();// 根据发起人部门路径得到部门全路径
			String deptPathId_model = model.getPathIdOfCache();// 部门全路径
			if (userpathIdofCache.contains(deptPathId_model)) {
				return true;
			}
			return false;*/
			String yjyDeptId = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "YJY_DEPTID");
			String userpathIdofCache = DepartmentCache.getModel(yjyDeptId).getPathIdOfCache();//研究院部门全路径
			String deptPathId_model = model.getPathIdOfCache();// 部门全路径
			if (deptPathId_model.contains(userpathIdofCache) || userpathIdofCache.contains(deptPathId_model) ) {
				return true;
				
			}
			return false;
		}		
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		if(AddressUIConst.ADDRESS_TASK_TRANSFER.equals(advancedAddressModel.getTransType())){//转办
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
		}else{//传阅
			advancedAddressModel.setDisplayMap(false);
			int layer = SDK.getORGAPI().getDepartmentById(model.getDepartmentId()).getLayer();
			String deptId = SDK.getORGAPI().getDepartmentByUser(model.getUID()).getId();
			//if(layer == 3 && model.getDepartmentId().equals(deptId)){
			if(model.getDepartmentId().equals(deptId)){ //范围到全院
				return true;
			}else {
				return false;
			}
		}
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}
}
